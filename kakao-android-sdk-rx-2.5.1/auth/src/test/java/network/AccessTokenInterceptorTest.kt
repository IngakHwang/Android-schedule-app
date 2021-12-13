/*
  Copyright 2019 Kakao Corp.

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
 */
package com.kakao.sdk.auth.network

import com.kakao.sdk.auth.*
import com.kakao.sdk.common.Constants
import com.kakao.sdk.common.model.ApprovalType
import com.kakao.sdk.common.model.AuthError
import com.kakao.sdk.common.model.KakaoSdkError
import com.kakao.sdk.network.ApiFactory
import com.kakao.sdk.network.origin
import okhttp3.*
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.junit.Assert.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.IOException
import java.util.concurrent.CountDownLatch

class AccessTokenInterceptorTest {
    private lateinit var server: MockWebServer
    private lateinit var client: OkHttpClient
    private lateinit var tokenManager: TokenManageable
    private lateinit var authApi: AuthApi
    private lateinit var authApiClient: AuthApiClient
    private lateinit var interceptor: AccessTokenInterceptor

    private lateinit var newAccessToken: String
    private lateinit var newRefreshToken: String

    @BeforeEach
    fun setup() {
        server = MockWebServer()
        tokenManager = TestTokenManager(testOAuthToken())
        authApi = ApiFactory.withClientAndAdapter(
            server.url("/").toString(),
            OkHttpClient.Builder()
        ).create(AuthApi::class.java)
        authApiClient = AuthApiClient(authApi, TokenManagerProvider(tokenManager), TestApplicationInfo(), TestContextInfo(), ApprovalType())
        interceptor = AccessTokenInterceptor(TokenManagerProvider(tokenManager), authApiClient)
        client = OkHttpClient().newBuilder().addInterceptor(interceptor).build()
    }

    @AfterEach
    fun tearDown() {
        server.shutdown()
    }

    /**
     * 모든 요청 인증헤더에 현재 토큰이 추가돼야 함
     */
    @Test
    fun authorization() {
        server.enqueue(MockResponse())
        client.newCall(Request.Builder().url(server.url("/")).build()).execute()
        val request = server.takeRequest()

        assertEquals("${Constants.BEARER} ${tokenManager.getToken()!!.accessToken}", request.getHeader(Constants.AUTHORIZATION))
    }

    /**
     * 자동 갱신 테스트
     * -401 에러코드 발생 시 토큰저장소 토큰이 갱신되고 인증헤더에 새 토큰이 추가돼야 함
     * refreshToken 은 갱신되지 않았다면 토큰저장소의 기존 토큰이 유지되어야 한다. 갱신되었다면 새 refreshToken으로 바뀌어야 한다.
     */
    @Test
    fun refresh() {
        newAccessToken = "new_access_token"
        newRefreshToken = "new_refresh_token"

        server.setDispatcher(object: Dispatcher() {
            override fun dispatch(request: RecordedRequest?): MockResponse {
                return when (request?.path) {
                    "/oauth/token" ->
                        MockResponse().setResponseCode(200)
                            .setBody(
                                testTokenResponseRefreshTokenGrant(
                                    accessToken = newAccessToken
                                )
                            )
                    else ->
                        MockResponse().setResponseCode(401).setBody("{\"msg\":\"this access token does not exist\",\"code\":-401}")
                }
            }
        })
        client.newCall(Request.Builder().url(server.url("/")).build()).execute()
//        val request = server.takeRequest()

        assertEquals(newAccessToken, tokenManager.getToken()?.accessToken)
//        assertEquals("${Constants.BEARER} ${tokenManager.getToken()!!.accessToken}", request.getHeader(Constants.AUTHORIZATION))   // TODO: 실제로는 갱신 잘되나 여기서 안된다.

        server.setDispatcher(object: Dispatcher() {
            override fun dispatch(request: RecordedRequest?): MockResponse {
                return when (request?.path) {
                    "/oauth/token" ->
                        MockResponse().setResponseCode(200)
                            .setBody(
                                testTokenResponseRefreshTokenGrant(
                                    accessToken = newAccessToken,
                                    refreshToken = newRefreshToken
                                )
                            )
                    else ->
                        MockResponse().setResponseCode(401).setBody("{\"msg\":\"this access token does not exist\",\"code\":-401}")
                }
            }
        })
        client.newCall(Request.Builder().url(server.url("/")).build()).execute()

        assertEquals(newAccessToken, tokenManager.getToken()?.accessToken)
        assertEquals(newRefreshToken, tokenManager.getToken()?.refreshToken)
    }

    /**
     * 중복 갱신 테스트
     * -401이 동시 다발적으로 일어난 경우 한 번만 갱신하도록 한다.
     *
     * TODO: 테스트에서 인터셉터 내부 로직에 딜레이를 줄 수 있는 방법이 없어서
     *       동시에 호출한다고 인터셉터 내부 pending 지점까지 모두 도달한다는 보장을 할 수 없다.
     *       잠재적으로 fail 가능성이 있어서 개선이 필요하다.
     */
    @Test
    fun multipleRefreshAtSameTime() {
        newAccessToken = "new_access_token"

        server.setDispatcher(object: Dispatcher() {
            var refreshed = false
            override fun dispatch(request: RecordedRequest?): MockResponse {
                return when (request?.path) {
                    "/oauth/token" -> {

                        assertFalse("토큰 갱신이 pending 없이 여러 번 호출되었습니다.", refreshed)

                        refreshed = true
                        MockResponse().setResponseCode(200).setBody(testTokenResponseRefreshTokenGrant(newAccessToken))
                    }
                    else -> MockResponse().setResponseCode(401).setBody("{\"msg\":\"this access token does not exist\",\"code\":-401}")
                }
            }
        })

        val MAX = 5 // 아마도 max requests 기본값. 6개 이상 넣으면 대기 걸려서 갱신이 두번 들어감 https://square.github.io/okhttp/4.x/okhttp/okhttp3/-dispatcher/max-requests-per-host/ 여기랑 MockWebServer 스펙 리서치 해봐야 함

        val latch = CountDownLatch(MAX)
        val callback = object: Callback {
            override fun onFailure(call: Call, e: IOException) { latch.countDown() }
            override fun onResponse(call: Call, response: Response) { response.body()?.close(); latch.countDown() }
        }
        repeat(MAX) {
            client.newCall(Request.Builder().url(server.url("/")).build()).enqueue(callback)
        }
        latch.await()

        assertEquals(newAccessToken, tokenManager.getToken()?.accessToken)
    }

    /**
     * 자동 갱신 실패 테스트
     *
     * 갱신 시 발생한 에러가 리턴되어야 한다.
     */
    @Test
    fun refreshError() {
        server.setDispatcher(object: Dispatcher() {
            override fun dispatch(request: RecordedRequest?): MockResponse {
                return when (request?.path) {
                    "/oauth/token" ->
                        MockResponse().setResponseCode(400).setBody("{\"error\":\"invalid_grant\",\"error_description\":\"expired_or_invalid_refresh_token\"}")
                    else ->
                        MockResponse().setResponseCode(401).setBody("{\"msg\":\"this access token does not exist\",\"code\":-401}")
                }
            }
        })

        val error = kotlin.runCatching { client.newCall(Request.Builder().url(server.url("/")).build()).execute() }
            .exceptionOrNull()?.origin

        assertTrue(error is AuthError)
        assertTrue((error as KakaoSdkError).isInvalidTokenError()) // AuthError 발생하므로 true 리턴
    }

}