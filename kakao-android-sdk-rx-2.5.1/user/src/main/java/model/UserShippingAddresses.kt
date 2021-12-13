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

import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import com.kakao.sdk.common.json.KakaoIntDateTypeAdapter
import com.kakao.sdk.common.json.UnknownValue
import java.util.*

/**
 * 앱에 가입한 사용자의 배송지 정보 API 응답 클래스
 *
 * 배송지의 정렬 순서는 기본배송지가 무조건 젤 먼저, 그후에는 배송지 수정된 시각을 기준으로 최신순으로 정렬되어 나가고, 페이지 사이즈를 주어서 여러 페이지를 나누어 조회하거나, 특정 배송지 아이디만을 지정하여 해당 배송지 정보만을 조회 가능.
 *
 * @property userId 배송지 정보를 요청한 사용자 아이디
 * @property needsAgreement 배송지 정보 조회를 위하여 유저에게 제3자 정보제공동의를 받아야 하는지 여부
 * @property shippingAddresses 사용자의 배송지 정보 리스트. 최신 수정순 (단, 기본 배송지는 수정시각과 상관없이 첫번째에 위치)
 */
data class UserShippingAddresses(
    val userId: Long?,
    @SerializedName("shipping_addresses_needs_agreement") val needsAgreement: Boolean?,
    val shippingAddresses: List<ShippingAddress>?
)

/**
 * 배송지 정보 클래스
 *
 * @property id 배송지 ID
 * @property name 배송지 이름
 * @property isDefault 기본 배송지 여부
 * @property updatedAt 수정시각의 timestamp
 * @property type 배송지 타입. 구주소(지번,번지 주소) 또는 신주소(도로명 주소). "OLD" or "NEW"
 * @property baseAddress 우편번호 검색시 채워지는 기본 주소
 * @property detailAddress 기본 주소에 추가하는 상세 주소
 * @property receiverName 수령인 이름
 * @property receiverPhoneNumber1 수령인 연락처
 * @property receiverPhoneNumber2 수령인 추가 연락처
 * @property zoneNumber 신주소 우편번호. 신주소인 경우에 반드시 존재함.
 * @property zipCode 구주소 우편번호. 우편번호를 소유하지 않는 구주소도 존재하여, 구주소인 경우도 해당값이 없을 수 있음.
 */
data class ShippingAddress(
    val id: Long,
    val name: String?,
    @SerializedName("default") val isDefault: Boolean,
    @JsonAdapter(KakaoIntDateTypeAdapter::class) val updatedAt: Date?,
    val type: ShippingAddressType?,
    val baseAddress: String?,
    val detailAddress: String?,
    val receiverName: String?,
    val receiverPhoneNumber1: String?,
    val receiverPhoneNumber2: String?,
    val zoneNumber: String?,
    val zipCode: String?
)

enum class ShippingAddressType {
    OLD,
    NEW,

    @UnknownValue
    UNKNOWN
}