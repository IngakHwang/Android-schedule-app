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
package com.kakao.sdk.auth.model

import com.kakao.sdk.auth.testOAuthToken
import com.kakao.sdk.common.util.KakaoJson
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class OAuthTokenTest {
    @Test
    fun serialize() {
        val token = testOAuthToken(scopes = listOf("account_email", "legal_age"))

        val serialized = KakaoJson.toJson(token)
        val deserialized = KakaoJson.fromJson<OAuthToken>(serialized, OAuthToken::class.java)
        assertEquals(token.accessToken, deserialized.accessToken)
        assertEquals(token.refreshToken, deserialized.refreshToken)
        assertEquals(
            token.accessTokenExpiresAt.toString(),
            deserialized.accessTokenExpiresAt.toString()
        )
        assertEquals(
            token.refreshTokenExpiresAt.toString(),
            deserialized.refreshTokenExpiresAt.toString()
        )
        assertEquals(token.scopes, deserialized.scopes)

    }
}