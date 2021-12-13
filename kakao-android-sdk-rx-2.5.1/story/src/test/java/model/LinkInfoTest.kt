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
package com.kakao.sdk.story.model

import com.google.gson.JsonObject
import com.kakao.sdk.common.util.KakaoJson
import com.kakao.sdk.common.util.Utility
import com.kakao.sdk.story.Constants
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class LinkInfoTest {
    @ValueSource(strings = ["linkinfo1", "linkinfo2"])
    @ParameterizedTest
    fun parse(path: String) {
        val body = Utility.getJson("json/linkinfo/$path.json")
        val expected: JsonObject = KakaoJson.fromJson(body, JsonObject::class.java)
        val response: LinkInfo = KakaoJson.fromJson(body, LinkInfo::class.java)

        assertEquals(expected[Constants.URL].asString, response.url)
        assertEquals(expected[Constants.REQUESTED_URL].asString, response.requestedUrl)
        assertEquals(expected[Constants.HOST].asString, response.host)
        assertEquals(expected[Constants.TITLE].asString, response.title)
        assertEquals(expected[Constants.DESCRIPTION].asString, response.description)
        assertEquals(expected[Constants.SECTION].asString, response.section)
        assertEquals(expected[Constants.TYPE].asString, response.type)

        assertEquals(expected[Constants.IMAGE].asJsonArray.size(), response.images?.size)

        val array = expected[Constants.IMAGE].asJsonArray
        array.forEachIndexed { i, json ->
            val expectedImage = json.asString
            val image = response.images!![i]
            assertEquals(expectedImage, image)
        }
    }
}