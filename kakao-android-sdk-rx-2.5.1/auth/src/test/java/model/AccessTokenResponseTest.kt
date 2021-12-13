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

import com.google.gson.JsonObject
import com.kakao.sdk.auth.Constants
import com.kakao.sdk.common.util.KakaoJson
import com.kakao.sdk.common.util.Utility
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class AccessTokenResponseTest {
    @ValueSource(strings = ["has_rt", "has_rt_and_scopes", "no_rt"])
    @ParameterizedTest
    fun parse(path: String) {
        val body = Utility.getJson("json/token/$path.json")
        val expected = KakaoJson.fromJson<JsonObject>(body, JsonObject::class.java)
        val response =
            KakaoJson.fromJson<AccessTokenResponse>(body, AccessTokenResponse::class.java)

        if (expected.has(Constants.ACCESS_TOKEN)) {
            assertEquals(expected[Constants.ACCESS_TOKEN].asString, response.accessToken)
        }
        if (expected.has(Constants.EXPIRES_IN)) {
            assertEquals(expected[Constants.EXPIRES_IN].asLong, response.accessTokenExpiresIn)
        }
        if (expected.has(Constants.REFRESH_TOKEN)) {
            assertEquals(expected[Constants.REFRESH_TOKEN].asString, response.refreshToken)
        } else {
            assertNull(response.refreshToken)
        }
        if (expected.has(Constants.REFRESH_TOKEN_EXPIRES_IN)) {
            assertEquals(
                expected[Constants.REFRESH_TOKEN_EXPIRES_IN].asLong,
                response.refreshTokenExpiresIn
            )
        } else {
            assertNull(response.refreshTokenExpiresIn)
        }
        if (expected.has(Constants.TOKEN_TYPE)) {
            assertEquals(expected[Constants.TOKEN_TYPE].asString, response.tokenType)
        }
        if (expected.has(Constants.SCOPE)) {
            assertEquals(expected[Constants.SCOPE].asString, response.scope)
        }
    }
}