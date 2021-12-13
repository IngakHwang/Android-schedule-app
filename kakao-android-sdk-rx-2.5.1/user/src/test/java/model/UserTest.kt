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
package com.kakao.sdk.user.model

import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import com.kakao.sdk.common.util.KakaoJson
import com.kakao.sdk.common.util.Utility
import com.kakao.sdk.user.Constants
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.text.SimpleDateFormat
import java.util.*

class UserTest {
    @ValueSource(strings = ["only_email", "only_phone", "preregi", "full"])
    @ParameterizedTest
    fun parse(path: String) {
        val body = Utility.getJson("json/users/$path.json")
        val expected = KakaoJson.fromJson<JsonObject>(body, JsonObject::class.java)
        val response = KakaoJson.fromJson<User>(body, User::class.java)

        println(response)

        assertEquals(expected[Constants.ID].asLong, response.id)
        if (expected.has(Constants.PROPERTIES)) {
            assertEquals(
                expected[Constants.PROPERTIES].asJsonObject.keySet().size,
                response.properties?.size
            )
        }

        assertEquals(expected[Constants.HAS_SIGNED_UP]?.asBoolean, response?.hasSignedUp)

        if (expected.has(Constants.KAKAO_ACCOUNT)) {
            assertNotNull(response.kakaoAccount)

            val expectedAccount = expected[Constants.KAKAO_ACCOUNT].asJsonObject
            val account = response.kakaoAccount

            assertEquals(
                expectedAccount[Constants.PROFILE_NEEDS_AGREEMENT]?.asBoolean,
                account?.profileNeedsAgreement
            )

            if (expectedAccount.has(Constants.PROFILE)) {
                assertNotNull(account?.profile)

                val expectedProfile = expectedAccount[Constants.PROFILE].asJsonObject
                val profile = account?.profile

                assertEquals(
                    expectedProfile[Constants.NICKNAME]?.asString,
                    profile?.nickname
                )
                assertEquals(
                    expectedProfile[Constants.PROFILE_IMAGE_URL]?.asString,
                    profile?.profileImageUrl
                )
                assertEquals(
                    expectedProfile[Constants.THUMBNAIL_IMAGE_URL]?.asString,
                    profile?.thumbnailImageUrl
                )
            }

            assertEquals(
                expectedAccount[Constants.EMAIL_NEEDS_AGREEMENT]?.asBoolean,
                account?.emailNeedsAgreement
            )
            assertEquals(
                expectedAccount[Constants.IS_EMAIL_VERIFIED]?.asBoolean,
                account?.isEmailVerified
            )
            assertEquals(
                expectedAccount[Constants.IS_EMAIL_VALID]?.asBoolean,
                account?.isEmailValid
            )
            assertEquals(
                expectedAccount[Constants.EMAIL]?.asString,
                account?.email
            )

            assertEquals(
                expectedAccount[Constants.AGE_RANGE_NEEDS_AGREEMENT]?.asBoolean,
                account?.ageRangeNeedsAgreement
            )

            assertEquals(
                expectedAccount[Constants.AGE_RANGE]?.asString,
                account?.ageRange?.javaClass?.getField(account?.ageRange?.name)?.getAnnotation(SerializedName::class.java)?.value
            )

            assertEquals(
                expectedAccount[Constants.BIRTHYEAR_NEEDS_AGREEMENT]?.asBoolean,
                account?.birthyearNeedsAgreement
            )

            assertEquals(
                expectedAccount[Constants.BIRTHYEAR]?.asString,
                account?.birthyear
            )

            assertEquals(
                expectedAccount[Constants.BIRTHDAY_NEEDS_AGREEMENT]?.asBoolean,
                account?.birthdayNeedsAgreement
            )

            assertEquals(
                expectedAccount[Constants.BIRTHDAY]?.asString,
                account?.birthday
            )

            assertEquals(
                expectedAccount[Constants.BIRTHDAY_TYPE]?.asString,
                account?.birthdayType?.javaClass?.getField(account?.birthdayType?.name)?.getAnnotation(SerializedName::class.java)?.value
            )

            assertEquals(
                expectedAccount[Constants.GENDER_NEEDS_AGREEMENT]?.asBoolean,
                account?.genderNeedsAgreement
            )

            assertEquals(
                expectedAccount[Constants.GENDER]?.asString,
                account?.gender?.javaClass?.getField(account?.gender?.name)?.getAnnotation(SerializedName::class.java)?.value
            )

            assertEquals(
                expectedAccount[Constants.CI_NEEDS_AGREEMENT]?.asBoolean,
                account?.ciNeedsAgreement
            )

            assertEquals(
                expectedAccount[Constants.CI]?.asString,
                account?.ci
            )

            assertEquals(
                expectedAccount[Constants.CI_AUTHENTICATION_AT]?.asString,
                account?.ciAuthenticatedAt?.toInstant()?.toString()
            )

            assertEquals(
                expectedAccount[Constants.LEGAL_NAME_NEEDS_AGREEMENT]?.asBoolean,
                account?.legalNameNeedsAgreement
            )

            assertEquals(
                expectedAccount[Constants.LEGAL_NAME]?.asString,
                account?.legalName
            )

            assertEquals(
                expectedAccount[Constants.LEGAL_BIRTH_DATE_NEEDS_AGREEMENT]?.asBoolean,
                account?.legalBirthDateNeedsAgreement
            )

            assertEquals(
                expectedAccount[Constants.LEGAL_BIRTH_DATE]?.asString,
                account?.legalBirthDate
            )

            assertEquals(
                expectedAccount[Constants.LEGAL_GENDER_NEEDS_AGREEMENT]?.asBoolean,
                account?.legalGenderNeedsAgreement
            )

            assertEquals(
                expectedAccount[Constants.LEGAL_GENDER]?.asString,
                account?.legalGender?.javaClass?.getField(account?.legalGender?.name)?.getAnnotation(SerializedName::class.java)?.value
            )

            assertEquals(
                expectedAccount[Constants.PHONE_NUMBER_NEEDS_AGREEMENT]?.asBoolean,
                account?.phoneNumberNeedsAgreement
            )

            assertEquals(
                expectedAccount[Constants.PHONE_NUMBER]?.asString,
                account?.phoneNumber
            )

            assertEquals(
                expectedAccount[Constants.IS_KOREAN_NEEDS_AGREEMENT]?.asBoolean,
                account?.isKoreanNeedsAgreement
            )

            assertEquals(
                expectedAccount[Constants.IS_KOREAN]?.asBoolean,
                account?.isKorean
            )
        }
    }

    @Test
    fun ageRange() {
        val first = KakaoJson.fromJson<AgeRange>("0~9", AgeRange::class.java)
        assertEquals(AgeRange.AGE_0_9, first)
        val last = KakaoJson.fromJson<AgeRange>("90~", AgeRange::class.java)
        assertEquals(AgeRange.AGE_90_ABOVE, last)
        val unknown = KakaoJson.fromJson<AgeRange>("unknown", AgeRange::class.java)
        assertEquals(AgeRange.UNKNOWN, unknown)
    }
}