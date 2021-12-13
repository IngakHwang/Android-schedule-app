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
import com.kakao.sdk.common.util.KakaoJson
import com.kakao.sdk.common.util.Utility
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class UserShippingAddressesTest {
    @Test
    fun simple() {
        val body = Utility.getJson("json/shipping_addresses/normal.json")
        val expected = KakaoJson.fromJson<JsonObject>(body, JsonObject::class.java)
        val response =
            KakaoJson.fromJson<UserShippingAddresses>(body, UserShippingAddresses::class.java)

        assertEquals(expected["user_id"].asLong, response.userId)
        assertEquals(
            expected["shipping_addresses_needs_agreement"].asBoolean,
            response.needsAgreement
        )

        val expectedAddresses = expected["shipping_addresses"].asJsonArray
        val addresses = response.shippingAddresses
        assertEquals(expectedAddresses.size(), addresses?.size)
        addresses?.forEach {

        }
    }
}