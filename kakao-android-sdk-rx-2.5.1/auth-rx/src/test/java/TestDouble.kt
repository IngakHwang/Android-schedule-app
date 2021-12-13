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

import com.google.gson.JsonObject
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ApplicationInfo
import com.kakao.sdk.common.model.ContextInfo
import com.kakao.sdk.common.util.KakaoJson
import com.kakao.sdk.v2.auth.rx.BuildConfig
import java.util.Date

fun testTokenResponseAuthorizationCodeGrant(
    accessToken: String = "test_access_token",
    accessTokenExpiresIn: Long = 60 * 60 * 12,
    refreshToken: String = "test_refresh_token",
    refreshTokenExpiresIn: Long = 60 * 60 * 24 * 30 * 2,
    scopes: List<String> = listOf("profile")
): String =
    KakaoJson.toJson(
        mapOf(
            Constants.TOKEN_TYPE to "bearer",
            Constants.ACCESS_TOKEN to accessToken,
            Constants.EXPIRES_IN to accessTokenExpiresIn,
            Constants.REFRESH_TOKEN to refreshToken,
            Constants.REFRESH_TOKEN_EXPIRES_IN to refreshTokenExpiresIn,
            Constants.SCOPE to scopes.joinToString(" ")
        )
    )

fun testTokenResponseRefreshTokenGrant(
    accessToken: String = "test_access_token",
    accessTokenExpiresIn: Long = 60 * 60 * 12,
    refreshToken: String? = null,
    refreshTokenExpiresIn: Long? = null,
    scopes: List<String>? = null
): String =
    KakaoJson.toJson(
        mapOf(
            Constants.TOKEN_TYPE to "bearer",
            Constants.ACCESS_TOKEN to accessToken,
            Constants.EXPIRES_IN to accessTokenExpiresIn,
            Constants.REFRESH_TOKEN to refreshToken,
            Constants.REFRESH_TOKEN_EXPIRES_IN to refreshTokenExpiresIn,
            Constants.SCOPE to scopes?.joinToString(" ")
        )
    )

fun testOAuthToken(
    accessToken: String = "test_access_token",
    accessTokenExpiresAt: Date = Date(System.currentTimeMillis() + 1000L * 60 * 60 * 12),
    refreshToken: String = "test_refresh_token",
    refreshTokenExpiresAt: Date = Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 30 * 2),
    scopes: List<String>? = listOf("profile")
): OAuthToken =
    OAuthToken(accessToken, accessTokenExpiresAt, refreshToken, refreshTokenExpiresAt, scopes)

data class TestApplicationInfo(
    override val appKey: String = "appkey",
    override val customScheme: String = "kakao$appKey",
    override val redirectUri: String = "$customScheme://oauth"
): ApplicationInfo

data class TestContextInfo(
    override val kaHeader: String = "sdk/${BuildConfig.VERSION_NAME} os/android origin/androidkeyhash",
    override val signingKeyHash: String = "androidkeyhash",
    override val extras: JsonObject = JsonObject(),
    override val appVer: String = "1.0.0",
    override val salt: ByteArray = ByteArray(0)
): ContextInfo

class TestTokenManager (
    private var token: OAuthToken? = null
): TokenManageable {
    override fun getToken(): OAuthToken? = token
    override fun setToken(token: OAuthToken) { this.token = token }
    override fun clear() { this.token = null }
}
