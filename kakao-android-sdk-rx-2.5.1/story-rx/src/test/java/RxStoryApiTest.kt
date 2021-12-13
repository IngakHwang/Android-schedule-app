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
package com.kakao.sdk.story

import com.google.gson.annotations.SerializedName
import com.kakao.sdk.common.util.Utility
import com.kakao.sdk.network.ApiFactory
import com.kakao.sdk.network.withClient
import com.kakao.sdk.story.model.*
import io.reactivex.observers.TestObserver
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.junit.jupiter.params.provider.ValueSource
import java.util.stream.Stream

class RxStoryApiTest {
    private lateinit var server: MockWebServer
    private lateinit var api: RxStoryApi

    @BeforeEach
    fun setup() {
        server = MockWebServer()
        api = ApiFactory.withClient(server.url("/").toString(), OkHttpClient.Builder())
            .create(RxStoryApi::class.java)
        server.enqueue(MockResponse().setResponseCode(200))
    }

    @AfterEach
    fun cleanup() {
        server.shutdown()
    }

    // path, statusCode, type,
    @Test
    fun isStoryUser() {
        val observer = TestObserver<StoryUserResult>()
        api.isStoryUser().subscribe(observer)
        val request = server.takeRequest()

        assertEquals("GET", request.method)
        assertEquals(Constants.IS_STORY_USER_PATH, request.requestUrl?.encodedPath())
    }

    @MethodSource("booleanProvider")
    @ParameterizedTest
    fun profile(secureResource: Boolean?) {
        val observer = TestObserver<StoryProfile>()
        api.profile(secureResource).subscribe(observer)
        val request = server.takeRequest()

        val params = Utility.parseQuery(request.requestUrl?.query())

        assertEquals("GET", request.method)
        assertEquals(Constants.STORY_PROFILE_PATH, request.requestUrl?.encodedPath())
        if (secureResource == null) {
            assertFalse(params.containsKey(Constants.SECURE_RESOURCE))
            return
        }
        assertEquals(secureResource.toString(), params[Constants.SECURE_RESOURCE])
    }

    @ValueSource(strings = ["", "last_id"])
    @ParameterizedTest
    fun myStory(id: String) {
        api.story(id).subscribe(TestObserver<Story>())
        val request = server.takeRequest()
        val params = Utility.parseQuery(request.requestUrl?.query())

        assertEquals("GET", request.method)
        assertEquals(Constants.GET_STORY_PATH, request.requestUrl?.encodedPath())
        assertEquals(id, params[Constants.ID])
    }

    @MethodSource("stringProvider")
    @ParameterizedTest
    fun myStories(id: String?) {
        api.stories(id).subscribe(TestObserver<List<Story>>())
    }

    @MethodSource("postNoteProvider")
    @ParameterizedTest
    fun postNote(
        content: String,
        permission: Story.Permission,
        enableShare: Boolean,
        params1: Map<String, String>?,
        params2: Map<String, String>?,
        params3: Map<String, String>?,
        params4: Map<String, String>?
    ) {
        api.postNote(content, permission, enableShare, params1, params2, params3, params4)
            .subscribe(TestObserver<StoryPostResult>())
        val request = server.takeRequest()
        val params = Utility.parseQuery(request.body.readUtf8())

        assertEquals("POST", request.method)
        assertEquals(Constants.POST_NOTE_PATH, request.requestUrl?.encodedPath())
        assertEquals(content, params[Constants.CONTENT])
        println(params)
        assertEquals(
            Story.Permission::class.java.getField(permission.name).getAnnotation(
                SerializedName::class.java
            ).value, params[Constants.PERMISSION]
        )
        assertEquals(enableShare.toString(), params[Constants.ENABLE_SHARE])

        assertEquals(Utility.buildQuery(params1), params[Constants.ANDROID_EXEC_PARAM] ?: "")
        assertEquals(Utility.buildQuery(params2), params[Constants.IOS_EXEC_PARAM] ?: "")
        assertEquals(Utility.buildQuery(params3), params[Constants.ANDROID_MARKET_PARAM] ?: "")
        assertEquals(Utility.buildQuery(params4), params[Constants.IOS_MARKET_PARAM] ?: "")
    }

//    @MethodSource("postPhotoProvider")
//    @ParameterizedTest fun postPhoto(images: String,
//                                     content: String,
//                                     permission: Story.Permission,
//                                     enableShare: Boolean,
//                                     androidExecutionParams: String?,
//                                     iosExecutionParams: String?,
//                                     androidMarketParams: String?,
//                                     iosMarketParams: String?) {
//        api.postPhoto(images, content, permission, enableShare, androidExecutionParams, iosExecutionParams, androidMarketParams, iosMarketParams)
//                .subscribe(TestObserver<StoryPostResult>())
//        val request = server.takeRequest()
//        val params = Utility.parseQuery(request.body.readUtf8())
//
//        assertEquals("POST", request.method)
//        assertEquals(Constants.POST_PHOTO_PATH, request.requestUrl?.encodedPath())
//        assertEquals(images, params[Constants.IMAGE_URL_LIST])
//        assertEquals(content, params[Constants.CONTENT])
//        assertEquals(permission.value, params[Constants.PERMISSION])
//        assertEquals(enableShare.toString(), params[Constants.ENABLE_SHARE])
//        assertEquals(androidExecutionParams, params[Constants.ANDROID_EXEC_PARAM])
//        assertEquals(iosExecutionParams, params[Constants.IOS_EXEC_PARAM])
//    }

    @Disabled
    @ParameterizedTest
    fun postLink() {
    }


    @ValueSource(strings = ["", "last_id"])
    @ParameterizedTest
    fun delete(id: String) {
        api.delete(id).subscribe(TestObserver<Void>())
        val request = server.takeRequest()
        val params = Utility.parseQuery(request.requestUrl?.query())

        assertEquals("DELETE", request.method)
        assertEquals(Constants.DELETE_STORY_PATH, request.requestUrl?.encodedPath())
        assertEquals(id, params[Constants.ID])
    }

    @ValueSource(strings = ["", "scrap_url"])
    @ParameterizedTest
    fun scrapLink(url: String) {
        api.linkInfo(url).subscribe(TestObserver<LinkInfo>())
        val request = server.takeRequest()
        val params = Utility.parseQuery(request.requestUrl?.query())

        assertEquals("GET", request.method)
        assertEquals(Constants.SCRAP_LINK_PATH, request.requestUrl?.encodedPath())
        assertEquals(url, params[Constants.URL])
    }

    // TODO
    @Disabled
    @ParameterizedTest
    fun scrapImages() {
    }

    companion object {
        @Suppress("unused")
        @JvmStatic
        fun booleanProvider(): Stream<Arguments> {
            return Stream.of(true, false, null).map { Arguments.of(it) }
        }

        @Suppress("unused")
        @JvmStatic
        fun stringProvider(): Stream<Arguments> {
            return Stream.of(null, "", "last_id").map { Arguments.of(it) }
        }

        @Suppress("unused")
        @JvmStatic
        fun postNoteProvider(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(
                    "content",
                    Story.Permission.PUBLIC,
                    true,
                    null, null, null, null
                ),
                Arguments.of(
                    "content",
                    Story.Permission.FRIEND,
                    false,
                    mapOf("key1" to "value1"),
                    mapOf("key1" to "value1"),
                    mapOf("key1" to "value1"),
                    mapOf("key1" to "value1")
                ),
                Arguments.of(
                    "escaped \"content\"",
                    Story.Permission.ONLY_ME,
                    true,
                    mapOf("key1" to "value1"), null, mapOf("key1" to "value1"), null
                )
            )
//            return Stream.of(Triple("content", Story.Permission.PUBLIC, true))
//                    .map { Arguments.of(it.first, it.second, it.third) }
        }
//
//        @Suppress("unused")
//        @JvmStatic fun postLinkProvider(): Stream<Arguments> {
//
//        }
//
//        @JvmStatic fun postPhotoProvider(): Stream<Arguments> {
//
//        }
    }
}