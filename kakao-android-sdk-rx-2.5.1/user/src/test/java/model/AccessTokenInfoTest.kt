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
package com.kakao.sdk.user.model

import com.google.gson.JsonObject
import com.kakao.sdk.common.util.KakaoJson
import com.kakao.sdk.common.util.Utility
import com.kakao.sdk.user.Constants
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class AccessTokenInfoTest {

    @ValueSource(strings = ["internal", "external"])
    @ParameterizedTest
    @Disabled
    fun parse(path: String) {
        val body = Utility.getJson("json/token_info/$path.json")
        val expected = KakaoJson.fromJson<JsonObject>(body, JsonObject::class.java)
        val response = KakaoJson.fromJson<AccessTokenInfo>(body, AccessTokenInfo::class.java)
        assertEquals(expected[Constants.EXPIRESINMILLIS].asLong, response.expiresInMillis)
        assertEquals(expected[Constants.ID].asLong, response.id)
    }
}