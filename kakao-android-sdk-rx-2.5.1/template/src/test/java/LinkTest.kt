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
package com.kakao.sdk.template.model

import com.kakao.sdk.common.util.KakaoJson
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class LinkTest {

    @Test
    fun empty() {

    }

    @Test
    fun twoKeys() {
        val params = mapOf("key1" to "value1", "key2" to "value2")
        val link = Link("https://kakaocorp.com", "https://kakaocorp.com", params, params)
        val encoded = KakaoJson.pretty.toJson(link)
        val decoded = KakaoJson.pretty.fromJson(encoded, Link::class.java)
        assertEquals(link, decoded)
    }
}