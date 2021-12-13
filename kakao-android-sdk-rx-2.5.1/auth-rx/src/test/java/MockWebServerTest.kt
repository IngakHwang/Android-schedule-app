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
package com.kakao.sdk.auth

import com.kakao.sdk.auth.model.AccessTokenResponse
import io.reactivex.observers.TestObserver
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class OkHttpBugTest {
    @Test
    fun loopback() {
        val server = MockWebServer()
        server.enqueue(MockResponse().setResponseCode(200))
        val api = Retrofit.Builder().baseUrl(server.url("/"))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create()).build()
            .create(RxAuthApi::class.java)
        api.issueAccessToken(
            code = "auth_code",
            clientId = "some_client_id",
            redirectUri = "https://example.com/oauth",
            androidKeyHash = "sample_key_hash"
        ).subscribe(TestObserver<AccessTokenResponse>())
        val request = server.takeRequest()
        Assertions.assertNotNull(request.requestUrl)
        server.shutdown()
    }
}