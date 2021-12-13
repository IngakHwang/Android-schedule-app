package com.kakao.sdk.talk

import com.google.gson.JsonObject
import com.kakao.sdk.network.withClient
import com.kakao.sdk.common.util.KakaoJson
import com.kakao.sdk.common.util.Utility
import com.kakao.sdk.network.ApiFactory
import com.kakao.sdk.talk.model.Channels
import io.reactivex.observers.TestObserver
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.net.URLDecoder
import java.util.stream.Stream

class RxTalkApiTest {
    private lateinit var server: MockWebServer
    private lateinit var api: RxTalkApi

    @BeforeEach
    fun setup() {
        server = MockWebServer()
        server.start()
        api = ApiFactory.withClient(server.url("/").toString(), OkHttpClient.Builder())
            .create(RxTalkApi::class.java)
        val response = MockResponse().setResponseCode(200)
        server.enqueue(response)
    }

    @MethodSource("sendMemoProvider")
    @ParameterizedTest
    fun sendMemo(templateId: Long, templateArgs: Map<String, String>?) {
        api.sendCustomMemo(templateId, templateArgs).subscribe(TestObserver<Void>())
        val request = server.takeRequest()
        val params = Utility.parseQuery(request.body.readUtf8())

        assertEquals(templateId.toString(), params[Constants.TEMPLATE_ID])
        if (templateArgs == null) {
            assertFalse(params.containsKey(Constants.TEMPLATE_ARGS))
            return
        }

        val decoded = URLDecoder.decode(params[Constants.TEMPLATE_ARGS], "UTF-8")
        val decodedJson: JsonObject = KakaoJson.fromJson(decoded, JsonObject::class.java)
        assertEquals(templateArgs.size, decodedJson.size())

        for ((k, v) in templateArgs) {
            assertEquals(v, decodedJson[k].asString)
        }
    }

    @MethodSource("channelsProvider")
    @ParameterizedTest
    fun channels(publicIds: List<String>?) {
        val observer = TestObserver<Channels>()
        api.channels(if (publicIds == null) null else KakaoJson.toJson(publicIds))
            .subscribe(observer)
        val request = server.takeRequest()
        val params = Utility.parseQuery(request.requestUrl?.query())
        assertEquals("GET", request.method)
    }

    @AfterEach
    fun cleanup() {
        server.shutdown()
    }

    companion object {
        @Suppress("unused")
        @JvmStatic
        fun sendMemoProvider(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(1234L, mapOf(Pair("key1", "value1"))),
                Arguments.of(1234L, null),
                Arguments.of(1234L, mapOf<String, String>())
            )
        }

        @Suppress("unused")
        @JvmStatic
        fun sendMessageProvider(): Stream<Arguments> {
            return Stream.of(
                Arguments.of("user_id", "1234", 1234L, null),
                Arguments.of("uuid", "1234", 1234L, mapOf<String, String>()),
                Arguments.of("chat_id", "1234", 1234L, mapOf(Pair("key1", "value1")))
            )
        }

        @Suppress("unused")
        @JvmStatic
        fun channelsProvider(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(null),
                Arguments.of(listOf("abcd", "1234"))
            )
        }
    }
}