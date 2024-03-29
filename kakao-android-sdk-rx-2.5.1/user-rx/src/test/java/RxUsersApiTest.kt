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
package com.kakao.sdk.user

import com.google.gson.JsonObject
import com.kakao.sdk.common.util.KakaoJson
import com.kakao.sdk.common.util.Utility
import com.kakao.sdk.network.ApiFactory
import com.kakao.sdk.network.withClient
import com.kakao.sdk.user.model.AccessTokenInfo
import com.kakao.sdk.user.model.ScopeInfo
import com.kakao.sdk.user.model.User
import com.kakao.sdk.user.model.UserShippingAddresses
import io.reactivex.observers.TestObserver
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import retrofit2.Retrofit
import java.io.File
import java.net.URLDecoder
import java.util.*
import java.util.concurrent.TimeUnit

class RxUsersApiTest {
    private lateinit var api: RxUserApi
    private lateinit var retrofit: Retrofit
    private lateinit var server: MockWebServer

    private lateinit var body: String
    private lateinit var expected: JsonObject

    @BeforeEach
    fun setup() {
        server = MockWebServer()
        server.start()
        retrofit = ApiFactory.withClient(server.url("/").toString(), OkHttpClient.Builder())
        api = retrofit.create(RxUserApi::class.java)
    }

    @Test
    fun me() {
        val body = Utility.getJson("json/users/deprecated.json")

//        val expected = KakaoJson.fromJson<JsonObject>(body, JsonObject::class.java)

        val response = MockResponse().setResponseCode(200).setBody(body)
        server.enqueue(response)

        val observer = TestObserver<User>()
        api.me(true).subscribe(observer)

        observer.awaitTerminalEvent(1, TimeUnit.SECONDS)
        observer.assertNoErrors()
        observer.assertValueCount(1)

        val request = server.takeRequest()
        assertEquals("GET", request.method)

//        observer.assertValue { user ->
//            return@assertValue expected["kaccount_email"].asString == user.email
//                    && expected["kaccount_email_verified"].asBoolean == user.emailVerified
//                    && 1376016924426814086 == user.id
//                    && expected["uuid"].asString == user.uuid
//                    && expected["service_user_id"].asLong == user.serviceUserId
//                    && expected["remaining_invite_count"].asInt == user.remainingInviteCount
//                    && expected["remaining_group_msg_count"].asInt == user.remainingGroupMsgCount
//                    && user.properties!!.containsKey("nickname")
//                    && user.properties!!.containsKey("thumbnail_image")
//                    && user.properties!!.containsKey("profile_image")
//        }
    }

    @Nested
    @DisplayName(Constants.V1_ACCESS_TOKEN_INFO_PATH)
    inner class TokenInfo {
        @BeforeEach
        fun setup() {
            val classloader = javaClass.classLoader ?: throw NullPointerException()
            val uri = classloader.getResource("json/token_info/internal.json")
            val file = File(uri.path)
            body = String(file.readBytes())
            expected = KakaoJson.fromJson(body, JsonObject::class.java)
            val response = MockResponse().setResponseCode(200).setBody(body)
            server.enqueue(response)
        }

        @Test
        fun accessTokenInfo() {
            val observer = TestObserver<AccessTokenInfo>()
            api.accessTokenInfo().subscribe(observer)
            observer.awaitTerminalEvent(1, TimeUnit.SECONDS)
            observer.assertNoErrors()
            observer.assertValueCount(1)
            observer.assertValue {
                expected["id"].asLong == it.id
                        && expected["expiresInMillis"].asLong == it.expiresInMillis
            }
        }
    }

    @Nested
    @DisplayName(Constants.V1_UPDATE_PROFILE_PATH)
    inner class UpdateProfile {
        @BeforeEach
        fun setup() {
            val classloader = javaClass.classLoader ?: throw NullPointerException()
            val uri = classloader.getResource("json/user_id.json")
            val file = File(uri.path)
            body = String(file.readBytes())
            expected = KakaoJson.fromJson(body, JsonObject::class.java)
            val response = MockResponse().setResponseCode(200).setBody(body)
            server.enqueue(response)
        }

        @Test
        fun updateProfile() {
            val observer = TestObserver<Void>()
            val properties = mapOf(Pair("key1", "value1"), Pair("key2", "value2"))
            api.updateProfile(properties = properties).subscribe(observer)
            observer.awaitTerminalEvent(1, TimeUnit.SECONDS)
            observer.assertNoErrors()
            observer.assertComplete()

            val request = server.takeRequest()
            val requestBody = Utility.parseQuery(request.body.readUtf8())

            assertEquals("POST", request.method)
            val requestProperties = KakaoJson.fromJson<JsonObject>(
                URLDecoder.decode(
                    requestBody["properties"],
                    "UTF-8"
                ), JsonObject::class.java
            )
            assertEquals("value1", requestProperties["key1"].asString)
            assertEquals("value2", requestProperties["key2"].asString)
        }
    }

    @Nested
    @DisplayName("/v1/user/logout and /v1/user/unlink")
    inner class LogoutAndUnlink {
        @BeforeEach
        fun setup() {
            val classloader = javaClass.classLoader ?: throw NullPointerException()
            val uri = classloader.getResource("json/user_id.json")
            val file = File(uri.path)
            body = String(file.readBytes())
            val response = MockResponse().setResponseCode(200).setBody(body)
            server.enqueue(response)
        }

        @Test
        fun logout() {
            val observer = TestObserver<Void>()
            api.logout().subscribe(observer)
            observer.awaitTerminalEvent(1, TimeUnit.SECONDS)
            observer.assertNoErrors()
            observer.assertComplete()
        }

        @Test
        fun unlink() {
            val observer = TestObserver<Void>()
            api.unlink().subscribe(observer)
            observer.awaitTerminalEvent(1, TimeUnit.SECONDS)
            observer.assertNoErrors()
            observer.assertComplete()
        }
    }

    @Test
    fun shippingAddresses() {
        val observer = TestObserver<UserShippingAddresses>()

        val response = MockResponse().setResponseCode(200)
        server.enqueue(response)
        val date = Date()
        api.shippingAddresses(addressId = 1234, fromUpdateAt = Date(), pageSize = 5)
            .subscribe(observer)
        val request = server.takeRequest()

        val params = Utility.parseQuery(request.requestUrl?.query())
        assertEquals(1234.toString(), params[Constants.ADDRESS_ID])
        assertEquals((date.time / 1000).toString(), params[Constants.FROM_UPDATED_AT])
        assertEquals(5.toString(), params[Constants.PAGE_SIZE])
    }

    @Test
    fun scopes() {
        val observer = TestObserver<ScopeInfo>()
        val body = Utility.getJson("json/scopes/scopes.json")

        val response = MockResponse().setResponseCode(200).setBody(body)
        server.enqueue(response)
        api.scopes().subscribe(observer)
        observer.assertComplete()
        observer.assertNoErrors()
    }

    @AfterEach
    fun cleanup() {
        server.shutdown()
    }
}