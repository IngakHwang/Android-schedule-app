/*
  Copyright 2020 Kakao Corp.

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

import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ApprovalType
import com.kakao.sdk.network.withClient
import com.kakao.sdk.network.ApiFactory
import io.reactivex.observers.TestObserver
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Assert.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.concurrent.TimeUnit

class RxTokenManageableTest {
    private lateinit var server: MockWebServer
    private lateinit var tokenManager: TestTokenManager
    private lateinit var authApi: RxAuthApi
    private lateinit var authApiClient: RxAuthApiClient
    private lateinit var observer: TestObserver<OAuthToken>

    @BeforeEach
    fun setup() {
        server = MockWebServer()
        tokenManager = TestTokenManager()
        authApi = ApiFactory.withClient(server.url("/").toString(), OkHttpClient.Builder()).create(RxAuthApi::class.java)
        authApiClient = RxAuthApiClient(authApi, TokenManagerProvider(tokenManager), TestApplicationInfo(), TestContextInfo(), ApprovalType())
        observer = TestObserver()
    }

    @AfterEach
    fun cleanup() {
        server.shutdown()
    }

    /**
     * 신규 발급 시 토큰 저장소에 발급 받은 토큰을 저장한다.
     */
    @Test
    fun issue() {
        server.enqueue(MockResponse().setResponseCode(200).setBody(
            testTokenResponseAuthorizationCodeGrant()))

        assertNull(tokenManager.getToken())

        authApiClient.issueAccessToken("test_authorization_code")
            .subscribe(observer)

        observer.awaitTerminalEvent(1, TimeUnit.SECONDS)
        observer.assertNoErrors()
        observer.assertValueCount(1)

        assertNotNull(tokenManager.getToken())
    }

    /**
     * access_token 만 갱신되면 토큰 저장소에 새 access_token 이 저장되고 기존 refresh_token 을 새 토큰 객체로 가져온다.
     */
    @Test
    fun refreshOnlyAccessToken() {

        val oldToken = testOAuthToken(accessToken = "old_access_token", accessTokenExpiresAt = java.util.Date())

        server.enqueue(MockResponse().setResponseCode(200).setBody(
            testTokenResponseRefreshTokenGrant()))

        tokenManager.setToken(oldToken)

        authApiClient.refreshAccessToken(tokenManager.getToken()!!)
            .subscribe(observer)

        observer.awaitTerminalEvent(1, TimeUnit.SECONDS)
        observer.assertNoErrors()
        observer.assertValueCount(1)

        val newToken = tokenManager.getToken()!!

        assertNotEquals(oldToken.accessToken, newToken.accessToken)
        assertNotEquals(oldToken.accessTokenExpiresAt, newToken.accessTokenExpiresAt)
        assertEquals(oldToken.refreshToken, newToken.refreshToken)
        assertEquals(oldToken.refreshTokenExpiresAt, newToken.refreshTokenExpiresAt)

        // 겸사겸사 얘도 보너스로 가져와서 유지해준다. 그러나 굳이 유지시켜줄 필요 없어 보인다.
        // https://tools.ietf.org/html/rfc6749#section-5.1 를 참고하면 정확한 해석인지 모르겠으나 요청값과 발급된 scope이 다를 경우 *알려주는* 목적인것 같다.
        assertEquals(oldToken.scopes, newToken.scopes)
    }

    /**
     * refresh_token 도 함께 갱신되면 토큰 저장소에 새 access_token, refresh_token 이 저장된다.
     */
    @Test
    fun refreshAccessTokenAndRefreshToken() {

        val oldToken = testOAuthToken(accessToken = "old_access_token", accessTokenExpiresAt = java.util.Date())

        server.enqueue(MockResponse().setResponseCode(200).setBody(
            testTokenResponseRefreshTokenGrant(refreshToken = "new_refresh_token", refreshTokenExpiresIn = 60 * 60 * 24 * 30 * 2)))

        tokenManager.setToken(oldToken)

        authApiClient.refreshAccessToken(tokenManager.getToken()!!)
            .subscribe(observer)

        observer.awaitTerminalEvent(1, TimeUnit.SECONDS)
        observer.assertNoErrors()
        observer.assertValueCount(1)

        val newToken = tokenManager.getToken()!!

        assertNotEquals(oldToken.accessToken, newToken.accessToken)
        assertNotEquals(oldToken.accessTokenExpiresAt, newToken.accessTokenExpiresAt)
        assertNotEquals(oldToken.refreshToken, newToken.refreshToken)
        assertNotEquals(oldToken.refreshTokenExpiresAt, newToken.refreshTokenExpiresAt) // TODO: 갱신 로직 내부에서 currentTime을 하니 항상 NotEquals.. 정확한 테스트가 안된다.

        // 겸사겸사 얘도 보너스로 가져와서 유지해준다. 그러나 굳이 유지시켜줄 필요 없어 보인다.
        // https://tools.ietf.org/html/rfc6749#section-5.1 를 참고하면 정확한 해석인지 모르겠으나 요청값과 발급된 scope이 다를 경우 *알려주는* 목적인것 같다.
        assertEquals(oldToken.scopes, newToken.scopes)
    }
}
