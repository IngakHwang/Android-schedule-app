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
package com.kakao.sdk.auth

import com.google.gson.JsonParser
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ApprovalType
import com.kakao.sdk.common.util.Utility
import com.kakao.sdk.common.model.AuthError
import com.kakao.sdk.network.withClient
import com.kakao.sdk.network.ApiFactory
import io.reactivex.Single
import io.reactivex.observers.TestObserver
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.*
import org.mockito.Mockito
import org.mockito.Mockito.*
import java.net.HttpURLConnection
import java.util.concurrent.TimeUnit

class RxAuthApiClientTest {
    private lateinit var authApiClient: RxAuthApiClient
    private lateinit var tokenManager: TokenManageable
    private lateinit var server: MockWebServer
    private lateinit var observer: TestObserver<OAuthToken>

    @BeforeEach
    fun setup() {
        tokenManager = spy(TestTokenManager())
        server = MockWebServer()
        val authApi =
            ApiFactory.withClient(server.url("/").toString(), OkHttpClient.Builder())
                .create(RxAuthApi::class.java)
        authApiClient = RxAuthApiClient(authApi, TokenManagerProvider(tokenManager), TestApplicationInfo(), TestContextInfo(), ApprovalType())
        observer = TestObserver()
    }

    @AfterEach
    fun cleanup() {
        server.shutdown()
    }

    @Nested
    @DisplayName("Issuing access token")
    inner class IssueAccessToken {
        @Test
        fun with200Response() {
            val json = Utility.getJson("json/token/has_rt.json")
            val jsonElement = JsonParser().parse(json).asJsonObject
            server.enqueue(MockResponse().setResponseCode(HttpURLConnection.HTTP_OK).setBody(json))

            authApiClient.issueAccessToken("auth_code")
                .subscribe(observer)

            observer.awaitTerminalEvent(1, TimeUnit.SECONDS)
            observer.assertNoErrors()
            observer.assertValueCount(1)

            val request = server.takeRequest()
            val requestBody = Utility.parseQuery(request.body.readUtf8())

            Assertions.assertEquals(Constants.AUTHORIZATION_CODE, requestBody[Constants.GRANT_TYPE])

            observer.assertValue {
                return@assertValue jsonElement[Constants.ACCESS_TOKEN].asString == it.accessToken &&
                        jsonElement[Constants.REFRESH_TOKEN].asString == it.refreshToken
//                        jsonElement[Constants.EXPIRES_IN].asLong * 1000 == it.accessTokenExpiresAt &&
//                        jsonElement[Constants.REFRESH_TOKEN_EXPIRES_IN].asLong == it.refreshTokenExpiresAt    TODO: 날짜 테스트
            }
            verify(tokenManager).setToken(any())
        }

        @Test
        fun with401Response() {
            val json = Utility.getJson("json/auth_errors/expired_refresh_token.json")
            val jsonElement = JsonParser().parse(json).asJsonObject
            server.enqueue(
                MockResponse().setResponseCode(HttpURLConnection.HTTP_UNAUTHORIZED).setBody(
                    json
                )
            )

            authApiClient.issueAccessToken("auth_code")
                .subscribe(observer)

            observer.awaitTerminalEvent(1, TimeUnit.SECONDS)
            observer.assertError {
                return@assertError it is AuthError && it.statusCode == HttpURLConnection.HTTP_UNAUTHORIZED &&
                        it.response.error == jsonElement[Constants.ERROR].asString &&
                        it.response.errorDescription == jsonElement[Constants.ERROR_DESCRIPTION].asString
//                return@assertError it.javaClass == KakaoSdkError.OAuthError::class.java &&
//                        i
//                        it is AuthResponseException &&
//                        it == HttpURLConnection.HTTP_UNAUTHORIZED &&
//                        it.response.error == jsonElement[Constants.ERROR].asString &&
//                        it.response.errorDescription == jsonElement[Constants.ERROR_DESCRIPTION].asString
            }
            verify(tokenManager, never()).setToken(any())
        }
    }

    @Nested
    @DisplayName("Refreshing access token")
    inner class RefreshAccessToken {
        @Test
        fun with200Response() {
            val json = Single.just("json/token/no_rt.json")
                .map(Utility::getJson)
            val jsonObject =
                json.map { JsonParser().parse(it) }.map { it.asJsonObject }.blockingGet()
            json.map { MockResponse().setResponseCode(HttpURLConnection.HTTP_OK).setBody(it) }
                .doOnSuccess { response -> server.enqueue(response) }
                .flatMap {
                    authApiClient.refreshAccessToken(testOAuthToken())
                }.subscribe(observer)
            observer.awaitTerminalEvent(1, TimeUnit.SECONDS)
            observer.assertNoErrors()
            observer.assertValueCount(1)

//            observer.assertValue {  }

            verify(tokenManager).setToken(any())
        }
    }

    private fun <T> any(): T {
        Mockito.any<T>()
        return uninitialized()
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T> uninitialized(): T = null as T
}