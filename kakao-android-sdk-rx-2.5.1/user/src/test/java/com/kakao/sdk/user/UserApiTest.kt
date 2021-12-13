/*
  Copyright 2020 Kakao Corp.

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
import com.kakao.sdk.auth.TokenManageable
import com.kakao.sdk.auth.TokenManagerProvider
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.util.KakaoJson
import com.kakao.sdk.common.util.Utility
import com.kakao.sdk.network.ApiFactory
import com.kakao.sdk.user.model.User
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.CompletableFuture

class UserApiTest {
    private lateinit var server: MockWebServer
    private lateinit var api: UserApi
    private lateinit var client: UserApiClient

    @BeforeEach
    fun setup() {
        server = MockWebServer()
        api = ApiFactory.withClientAndAdapter(
            server.url("/").toString(),
            OkHttpClient.Builder()
        ).create(UserApi::class.java)
        client = UserApiClient(api, TokenManagerProvider(object: TokenManageable {
            override fun getToken(): OAuthToken? { return null }
            override fun setToken(token: OAuthToken) {}
            override fun clear() {}
        }))
    }

    @AfterEach
    fun cleanup() {
        server.shutdown()
    }

    @Test
    fun testMe() {
        val body = Utility.getJson("json/users/only_email.json")
        val response = MockResponse().setResponseCode(200).setBody(body)

        server.enqueue(response)

        val future = CompletableFuture<User>()

        client.me { user, error ->
            if (error != null) {
                future.completeExceptionally(error)
            } else {
                future.complete(user)
            }
        }

        Assertions.assertEquals("GET", server.takeRequest().method)

        val expected = KakaoJson.fromJson<JsonObject>(body, JsonObject::class.java)
        val result = future.get()
        println(result)

        val dateFormat =
            SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss'Z'",
                Locale.getDefault()
            ).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }

        Assertions.assertEquals(expected["id"].asLong, result.id)

        Assertions.assertEquals(expected["properties"].toString(), KakaoJson.toJson(result.properties))     // TODO: JsonObject와 Map의 비교 더 좋은 방법 찾자
//        Assertions.assertEquals(dateFormat.parse(expected["connected_at"].asString), result.connectedAt)
//        Assertions.assertEquals(dateFormat.parse(expected["synched_at"].asString), result.synchedAt)
//        Assertions.assertEquals(expected["group_user_token"], result.groupUserToken)

        val expectedKakaoAccount = expected["kakao_account"]
        if (expectedKakaoAccount == null)   return

//        XCTAssertEqual(expectedKakaoAccount["email_needs_agreement"] as? Bool, kakaoAccount.emailNeedsAgreement)
//        XCTAssertEqual(expectedKakaoAccount["email"] as? String, kakaoAccount.email)
//        XCTAssertEqual(expectedKakaoAccount["is_email_valid"] as? Bool, kakaoAccount.isEmailValid)
//        XCTAssertEqual(expectedKakaoAccount["is_email_verified"] as? Bool, kakaoAccount.isEmailVerified)
//        XCTAssertEqual(expectedKakaoAccount["age_range_needs_agreement"] as? Bool, kakaoAccount.ageRangeNeedsAgreement)
//        XCTAssertEqual(AgeRange(rawValue: expectedKakaoAccount["age_range"] as? String ?? ""), kakaoAccount.ageRange)
//        XCTAssertEqual(expectedKakaoAccount["birthyear_needs_agreement"] as? Bool, kakaoAccount.birthyearNeedsAgreement)
//        XCTAssertEqual(expectedKakaoAccount["birthyear"] as? String, kakaoAccount.birthyear)
//        XCTAssertEqual(expectedKakaoAccount["birthday_needs_agreement"] as? Bool, kakaoAccount.birthdayNeedsAgreement)
//        XCTAssertEqual(expectedKakaoAccount["birthday"] as? String, kakaoAccount.birthday)
//        XCTAssertEqual(BirthdayType(rawValue: expectedKakaoAccount["birthday_type"] as? String ?? ""), kakaoAccount.birthdayType)
//        XCTAssertEqual(expectedKakaoAccount["gender_needs_agreement"] as? Bool, kakaoAccount.genderNeedsAgreement)
//        XCTAssertEqual(Gender(rawValue: expectedKakaoAccount["gender"] as? String ?? ""), kakaoAccount.gender)
//        XCTAssertEqual(expectedKakaoAccount["phone_number_needs_agreement"] as? Bool, kakaoAccount.phoneNumberNeedsAgreement)
//        XCTAssertEqual(expectedKakaoAccount["phone_number"] as? String, kakaoAccount.phoneNumber)
//        XCTAssertEqual(expectedKakaoAccount["ci_needs_agreement"] as? Bool, kakaoAccount.ciNeedsAgreement)
//        XCTAssertEqual(expectedKakaoAccount["ci"] as? String, kakaoAccount.ci)
//        XCTAssertEqual(ISO8601DateFormatter().date(from: expectedKakaoAccount["ci_authenticated_at"] as? String ?? ""), kakaoAccount.ciAuthenticatedAt)
//        XCTAssertEqual(expectedKakaoAccount["legal_name_needs_agreement"] as? Bool, kakaoAccount.legalNameNeedsAgreement)
//        XCTAssertEqual(expectedKakaoAccount["legal_name"] as? String, kakaoAccount.legalName)
//        XCTAssertEqual(expectedKakaoAccount["legal_birth_date_needs_agreement"] as? Bool, kakaoAccount.legalBirthDateNeedsAgreement)
//        XCTAssertEqual(expectedKakaoAccount["legal_birth_date"] as? String, kakaoAccount.legalBirthDate)
//        XCTAssertEqual(expectedKakaoAccount["legal_gender_needs_agreement"] as? Bool, kakaoAccount.legalGenderNeedsAgreement)
//        XCTAssertEqual(Gender(rawValue: expectedKakaoAccount["legal_gender"] as? String ?? ""), kakaoAccount.legalGender)
//
//        guard let expectedProfile = expectedKakaoAccount["profile"] as? [String: Any] else { continue }
//        guard let profile = kakaoAccount.profile else {
//            XCTFail("profile 객체 생성 실패")
//            return
//        }
//        XCTAssertEqual(expectedProfile["nickname"] as? String, profile.nickname)
//        XCTAssertEqual(URL(string: expectedProfile["profile_image_url"] as? String ?? ""), profile.profileImageUrl)
//        XCTAssertEqual(URL(string: expectedProfile["thumbnail_image_url"] as? String ?? ""), profile.thumbnailImageUrl)
    }
}