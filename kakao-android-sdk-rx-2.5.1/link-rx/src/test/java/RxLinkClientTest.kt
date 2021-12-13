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

import android.content.Context
import android.os.Build
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.kakao.sdk.network.withClient
import com.kakao.sdk.common.util.Utility
import com.kakao.sdk.network.ApiFactory
import com.kakao.sdk.template.model.*
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.P])
class RxLinkClientTest {
    private lateinit var server: MockWebServer
    private lateinit var client: RxLinkClient

    @Before
    fun setup() {
        server = MockWebServer()
        val resolveClient = Mockito.spy(TestIntentResolveClient())

        client = RxLinkClient(
            api = ApiFactory.withClient(server.url("/").toString(), OkHttpClient.Builder()).create(
                RxLinkApi::class.java
            ),
            linkIntentClient = KakaoLinkIntentClient(
                contextInfo = TestContextInfo(
                    kaHeader = "ka_header",
                    signingKeyHash = "key_hash",
                    extras = JsonObject(),
                    appVer = "1.0,0"
                ),
                applicationInfo = TestApplicationInfo(
                    appKey = "client_id"
                ),
                intentResolveClient = resolveClient
            )
        )
    }

    @After
    fun cleanup() {
        server.shutdown()
    }

    @Test
    fun defaultTemplate() {
        val json = Utility.getJson("json/default_feed.json")
        val jsonObject = JsonParser().parse(json).asJsonObject
        server.enqueue(MockResponse().setResponseCode(200).setBody(json))
        val linkResult = client.defaultTemplate(
            ApplicationProvider.getApplicationContext<Context>(),
            FeedTemplate(Content("title", "imageUrl", Link()))
        ).blockingGet()
        assertNotNull(linkResult.intent?.data)
        val uri = linkResult.intent.data
        assertEquals(
            jsonObject[Constants.TEMPLATE_ID].asString,
            uri!!.getQueryParameter(Constants.TEMPLATE_ID)
        )
    }
}