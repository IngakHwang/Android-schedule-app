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
package com.kakao.sdk.link.model

import com.google.gson.JsonObject
import com.kakao.sdk.common.util.KakaoJson
import com.kakao.sdk.common.util.Utility
import com.kakao.sdk.link.Constants
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

class ValidationResultTest {
    @MethodSource("responseProvider")
    @ParameterizedTest
    fun parse(jsonString: String, jsonObject: JsonObject) {
        val response =
            KakaoJson.fromJson<ValidationResult>(jsonString, ValidationResult::class.java)
        assertEquals(jsonObject[Constants.TEMPLATE_ID].asLong, response.templateId)
    }

    companion object {
        val paths = listOf(
            "default_commerce", "default_feed", "default_list", "default_location", "default_text",
            "validate"
        )

        @Suppress("unused")
        @JvmStatic
        fun responseProvider(): Stream<Arguments> {
            return paths
                .map { "json/$it.json" }
                .map { Utility.getJson(it) }
                .map { it to KakaoJson.fromJson<JsonObject>(it, JsonObject::class.java) }
                .map { Arguments.of(it.first, it.second) }
                .stream()
        }
    }

}