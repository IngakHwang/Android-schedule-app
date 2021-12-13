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

import com.google.gson.JsonObject
import com.kakao.sdk.common.util.KakaoJson
import com.kakao.sdk.common.util.Utility
import com.kakao.sdk.network.ApiFactory
import com.kakao.sdk.network.withClient
import com.kakao.sdk.story.model.Story
import com.kakao.sdk.story.model.StoryPostResult
import com.kakao.sdk.story.model.StoryProfile
import com.kakao.sdk.story.model.StoryUserResult
import io.reactivex.observers.TestObserver
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class StoryResponseTest {

    private lateinit var server: MockWebServer
    private lateinit var api: RxStoryApi

    @BeforeEach
    fun setup() {
        server = MockWebServer()
        api = ApiFactory.withClient(server.url("/").toString(), OkHttpClient.Builder())
            .create(RxStoryApi::class.java)
    }

    @AfterEach
    fun cleanup() {
        server.shutdown()
    }

    @Test
    fun isStoryUser() {
        val response = Utility.getJson("json/isstoryuser.json")
        server.enqueue(MockResponse().setResponseCode(200).setBody(response))

        val observer = TestObserver<StoryUserResult>()
        api.isStoryUser().subscribe(observer)

        val expected = KakaoJson.fromJson<JsonObject>(response, JsonObject::class.java)

        observer.assertValue {
            expected[Constants.IS_STORY_USER].asBoolean == it.isStoryUser
        }
    }

    @Test
    fun profile() {
        val body = Utility.getJson("json/profile.json")
        server.enqueue(MockResponse().setResponseCode(200).setBody(body))

        val observer = TestObserver<StoryProfile>()
        api.profile().subscribe(observer)

        val expected = KakaoJson.fromJson<JsonObject>(body, JsonObject::class.java)

        observer.assertValue {
            expected[Constants.NICKNAME].asString == it.nickname &&
                    expected[Constants.PROFILE_IMAGE_URL].asString == it.profileImageUrl &&
                    expected[Constants.THUMBNAIL_URL].asString == it.thumbnailUrl &&
                    expected[Constants.BG_IMAGE_URL].asString == it.bgImageUrl &&
                    expected[Constants.PERMALINK].asString == it.permalink &&
                    expected[Constants.BIRTHDAY].asString == it.birthday &&
                    expected[Constants.BIRTHDAY_TYPE].asString == it.birthdayType?.name
        }
    }

    @Test
    fun myStory() {
        val expected = Utility.getJsonObject("json/story.json")
        server.enqueue(MockResponse().setResponseCode(200).setBody(expected.toString()))
        val observer = TestObserver<Story>()
        api.story("last_id").subscribe(observer)

        observer.assertValue {
            expected[Constants.ID].asString == it.id &&
                    expected[Constants.URL].asString == it.url &&
                    expected[Constants.MEDIA_TYPE].asString == it.mediaType &&
                    expected[Constants.CREATED_AT].asString == it.createdAt.toInstant().toString() &&
                    expected[Constants.COMMENT_COUNT].asInt == it.commentCount &&
                    expected[Constants.LIKE_COUNT].asInt == it.likeCount &&
                    expected[Constants.CONTENT].asString == it.content &&
                    expected[Constants.PERMISSION].asString == it.permission
        }
    }

    @Test
    fun myStories() {
        val expected = Utility.getJsonArray("json/stories.json")
        server.enqueue(MockResponse().setResponseCode(200).setBody(expected.toString()))
        val observer = TestObserver<List<Story>>()
        api.stories("1234").subscribe(observer)

        observer.assertValue {
            3 == it.size
        }
    }

    @Test
    fun postNote() {
        val expected = Utility.getJsonObject("json/id.json")
        server.enqueue(MockResponse().setResponseCode(200).setBody(expected.toString()))
        val observer = TestObserver<StoryPostResult>()
        api.postNote(
            "content", Story.Permission.PUBLIC, true,
            null, null, null, null
        )
            .subscribe(observer)

        observer.assertValue {
            expected[Constants.ID].asString == it.id
        }

    }
}