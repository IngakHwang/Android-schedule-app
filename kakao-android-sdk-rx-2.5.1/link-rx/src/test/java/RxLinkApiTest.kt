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
package com.kakao.sdk.link

import com.google.gson.JsonObject
import com.kakao.sdk.network.withClient
import com.kakao.sdk.common.util.KakaoJson
import com.kakao.sdk.common.util.Utility
import com.kakao.sdk.link.model.ValidationResult
import com.kakao.sdk.network.ApiFactory
import com.kakao.sdk.template.model.*
import io.reactivex.observers.TestObserver
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.net.URLDecoder
import java.util.stream.Stream

class RxLinkApiTest {
    private lateinit var server: MockWebServer
    private lateinit var api: RxLinkApi
    @BeforeEach
    fun setup() {
        server = MockWebServer()
        server.start()
        api = ApiFactory.withClient(server.url("/").toString(), OkHttpClient.Builder())
            .create(RxLinkApi::class.java)

        val body = Utility.getJson("json/validate.json")
        val response = MockResponse().setResponseCode(200).setBody(body)
        server.enqueue(response)
    }

    @MethodSource("validateProvider")
    @ParameterizedTest
    fun validate(templateId: Long, templateArgs: Map<String, String>?) {
        val observer = TestObserver<ValidationResult>()
        api.validateCustom(templateId, templateArgs).subscribe(observer)
        val request = server.takeRequest()
        val params = Utility.parseQuery(request.requestUrl?.query())

        assertEquals("4.0", params[Constants.LINK_VER])
        assertEquals(templateId.toString(), params[Constants.TEMPLATE_ID])
        if (templateArgs == null) {
            assertFalse(params.containsKey(Constants.TEMPLATE_ARGS))
            return
        }

        val decoded = URLDecoder.decode(params[Constants.TEMPLATE_ARGS], "UTF-8")
        val decodedJson = KakaoJson.fromJson<JsonObject>(decoded, JsonObject::class.java)
        assertEquals(templateArgs.size, decodedJson.size())
        for ((k, v) in templateArgs) {
            assertEquals(v, decodedJson[k].asString)
        }

        observer.assertValue {
            println(it)
            true
        }
    }

    @Test
    fun default() {
        val template = FeedTemplate(
            Content("title", "imageUrl", Link("webUrl"))
        )
        api.validateDefault(template).subscribe(TestObserver<ValidationResult>())
        val request = server.takeRequest()
        val params = Utility.parseQuery(request.requestUrl?.query())

        assertEquals("4.0", params[Constants.LINK_VER])
        assertNotNull(params[Constants.TEMPLATE_OBJECT])
        println(params)
    }

    @MethodSource("scrapProvider")
    @ParameterizedTest
    fun scrap(url: String, templateId: Long, templateArgs: Map<String, String>?) {
        api.validateScrap(url, templateId, templateArgs)
            .subscribe(TestObserver<ValidationResult>())
        val request = server.takeRequest()
        val params = Utility.parseQuery(request.requestUrl?.query())

        assertEquals("4.0", params[Constants.LINK_VER])
        assertEquals(url, params[Constants.REQUEST_URL])
        assertEquals(templateId.toString(), params[Constants.TEMPLATE_ID])

        if (templateArgs == null) {
            assertFalse(params.containsKey(Constants.TEMPLATE_ARGS))
            return
        }

        val decoded = URLDecoder.decode(params[Constants.TEMPLATE_ARGS], "UTF-8")
        val decodedJson = KakaoJson.fromJson<JsonObject>(decoded, JsonObject::class.java)
        assertEquals(templateArgs.size, decodedJson.size())

        for ((k, v) in templateArgs) {
            assertEquals(v, decodedJson[k].asString)
        }
    }

    @AfterEach
    fun cleanup() {
        server.shutdown()
    }

    companion object {
        @Suppress("unused")
        @JvmStatic
        fun validateProvider(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(1234L, null),
                Arguments.of(1234L, mapOf<String, String>()),
                Arguments.of(1234L, mapOf(Pair("key1", "value1"))),
                Arguments.of(1234L, mapOf(Pair("key1", "value1"), Pair("key2", "\"value2\"")))
            )
        }

        @Suppress("unused")
        @JvmStatic
        fun scrapProvider(): Stream<Arguments> {
            return Stream.of(
                Arguments.of("request_url", 1234L, null),
                Arguments.of("request_url", 1234L, mapOf(Pair("key1", "value1"))),
                Arguments.of(
                    "request_url",
                    1234L,
                    mapOf(Pair("key1", "value1"), Pair("key2", "\"value2\""))
                )
            )
        }
    }
}