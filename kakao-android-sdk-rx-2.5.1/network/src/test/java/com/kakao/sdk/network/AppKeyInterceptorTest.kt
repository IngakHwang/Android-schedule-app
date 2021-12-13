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

import android.app.Application
import android.os.Build
import android.os.Bundle
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kakao.sdk.common.Constants
import com.kakao.sdk.network.AppKeyInterceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.shadows.ShadowLog

import org.junit.jupiter.api.Assertions.*
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.P])
class AppKeyInterceptorTest {
    lateinit var interceptor: AppKeyInterceptor
    @Before
    fun setup() {
        ShadowLog.stream = System.out

        val bundle = Bundle()
        bundle.putString(Constants.META_APP_KEY, "test_app_key")
        val application = ApplicationProvider.getApplicationContext<Application>()
        application.applicationInfo.metaData = bundle
        interceptor = AppKeyInterceptor("test_app_key")
    }

    @Test
    fun interceptor() {
        val client = OkHttpClient().newBuilder().addInterceptor(interceptor).build()
        val server = MockWebServer()
        server.start()

        server.enqueue(MockResponse())
        client.newCall(Request.Builder().url(server.url("/")).build()).execute()
        val request = server.takeRequest()

        assertEquals(
            String.format(
                "%s %s",
                Constants.KAKAO_AK,
                "test_app_key"
            ), request.getHeader(Constants.AUTHORIZATION)
        )
        server.shutdown()
    }
}