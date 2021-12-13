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
import com.kakao.sdk.network.withClient
import com.kakao.sdk.common.util.Utility
import com.kakao.sdk.network.ApiFactory
import io.reactivex.observers.TestObserver
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvFileSource

class RxAuthApiRequestTest {
    private lateinit var api: RxAuthApi
    private lateinit var server: MockWebServer

    @BeforeEach
    fun setup() {
        server = MockWebServer()
        server.start()
        api = ApiFactory.withClient(server.url("/").toString(), OkHttpClient.Builder())
            .create(RxAuthApi::class.java)
        val response = MockResponse().setResponseCode(200)
        server.enqueue(response)
    }

    @CsvFileSource(resources = ["/csv/token_requests.csv"], numLinesToSkip = 1)
    @ParameterizedTest
    fun simple(
        clientId: String,
        androidKeyHash: String,
        code: String,
        redirectUri: String
    ) {
        api.issueAccessToken(clientId, androidKeyHash, code, redirectUri)
            .subscribe(TestObserver<AccessTokenResponse>())

        val request = server.takeRequest()
        val params = Utility.parseQuery(request.body.readUtf8())

        assertEquals("POST", request.method)
        print(params)
        assertEquals(clientId, params[Constants.CLIENT_ID])
        assertEquals(redirectUri, params[Constants.REDIRECT_URI])
        assertEquals(androidKeyHash, params[Constants.ANDROID_KEY_HASH])
        assertEquals(code, params[Constants.CODE])
    }

    @AfterEach
    fun cleanup() {
        server.shutdown()
    }
}