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

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.kakao.sdk.common.json.UnknownValue
import kotlinx.android.parcel.Parcelize
import java.util.*

/**
 * 사용자 정보 조회 API 응답으로 제공되는 사용자 정보 최상위 클래스.
 *
 * @property id 회원번호
 * @property properties 추가 정보
 * @property kakaoAccount 카카오계정 정보
 * @property groupUserToken 앱이 그룹에 속해 있는 경우 그룹 내 사용자 식별 토큰입니다. 앱의 그룹정보가 변경될 경우 토큰 값도 변경됩니다. 제휴를 통해 권한이 부여된 특정 앱에만 제공됩니다.
 * @property connectedAt 서비스에 연결 완료된 시각, UTC
 * @property synchedAt 카카오싱크 간편가입을 통해 로그인한 시각, UTC
 * @property hasSignedUp signup 여부 정보
 */
@Parcelize
data class User(
    val id: Long,
    val properties: Map<String, String>?,
    val kakaoAccount: Account?,
    val groupUserToken: String?,
    val connectedAt: Date?,
    val synchedAt: Date?,
    val hasSignedUp: Boolean?
) : Parcelable

/**
 * 카카오계정에 등록된 사용자 개인정보.
 *
 * @property profileNeedsAgreement profile 제공에 대한 사용자 동의 필요 여부
 * @property profileNicknameNeedsAgreement profile nickename 제공에 대한 사용자 동의 필요 여부
 * @property profileImageNeedsAgreement profile image 제공에 대한 사용자 동의 필요 여부
 * @property profile 카카오계정 프로필 정보
 * @property emailNeedsAgreement email 제공에 대한 사용자 동의 필요 여부
 * @property isEmailValid 카카오계정에 등록된 이메일의 유효성
 * @property isEmailVerified 카카오계정에 이메일 등록 시 이메일 인증을 받았는지 여부
 * @property email 카카오계정 대표 이메일
 * @property ageRangeNeedsAgreement 연령 제공에 대한 사용자 동의 필요 여부
 * @property ageRange 연령대
 * @property birthyearNeedsAgreement birthyear 제공에 대한 사용자 동의 필요 여부
 * @property birthyear 출생 연도 (YYYY)
 * @property birthdayNeedsAgreement birthday 제공에 대한 사용자 동의 필요 여부
 * @property birthday 생일 (MMDD)
 * @property birthdayType 생일의 양력/음력
 * @property genderNeedsAgreement gender 제공에 대한 사용자의 동의 필요 여부
 * @property gender 성별
 * @property ciNeedsAgreement ci 제공에 대한 사용자의 동의 필요 여부
 * @property ci 암호화된 사용자 확인값
 * @property ciAuthenticatedAt ci 발급시간
 * @property legalNameNeedsAgreement legalName 제공에 대한 사용자 동의 필요 여부
 * @property legalName 실명
 * @property legalGenderNeedsAgreement legalGender 제공에 대한 사용자 동의 필요 여부
 * @property legalGender 법정성별
 * @property legalBirthDateNeedsAgreement legalBirthDate 제공에 대한 사용자 동의 필요 여부
 * @property legalBirthDate 법정생년월일
 * @property phoneNumberNeedsAgreement phoneNumber 제공에 대한 사용자 동의 필요 여부
 * @property phoneNumber 카카오톡에서 인증한 전화번호
 * @property isKoreanNeedsAgreement 한국인 여부 제공에 대한 사용자 동의 필요 여부
 * @property isKorean 한국인 여부
 */
@Parcelize
data class Account(
    val profileNeedsAgreement: Boolean?,
    val profileNicknameNeedsAgreement: Boolean?,
    val profileImageNeedsAgreement: Boolean?,
    val profile: Profile?,

    val emailNeedsAgreement: Boolean?,
    val isEmailValid: Boolean?,
    val isEmailVerified: Boolean?,
    val email: String?,

    val ageRangeNeedsAgreement: Boolean?,
    val ageRange: AgeRange?,

    val birthyearNeedsAgreement: Boolean?,
    val birthyear: String?,

    val birthdayNeedsAgreement: Boolean?,
    val birthday: String?,
    val birthdayType: BirthdayType?,

    val genderNeedsAgreement: Boolean?,
    val gender: Gender?,

    val ciNeedsAgreement: Boolean?,
    val ci: String?,
    val ciAuthenticatedAt: Date?,

    val legalNameNeedsAgreement: Boolean?,
    val legalName: String?,

    val legalBirthDateNeedsAgreement: Boolean?,
    val legalBirthDate: String?,

    val legalGenderNeedsAgreement: Boolean?,
    val legalGender: Gender?,

    val phoneNumberNeedsAgreement: Boolean?,
    val phoneNumber: String?,

    val isKoreanNeedsAgreement: Boolean?,
    val isKorean: Boolean?

) : Parcelable


/**
 * 카카오계정에 등록된 사용자의 프로필 정보를 제공합니다.
 *
 * @property nickname 사용자의 닉네임
 * @property profileImageUrl 카카오계정에 등록된 프로필 이미지 URL
 * @property thumbnailImageUrl 카카오계정에 등록된 프로필 이미지의 썸네일 규격 이미지 URL
 * @property isDefaultImage 카카오계정에 등록된 프로필 이미지가 기본이미지인지 여부
 */
@Parcelize
data class Profile(
    val nickname: String?,
    val profileImageUrl: String?,
    val thumbnailImageUrl: String?,
    val isDefaultImage: Boolean?
) : Parcelable

/**
 * 연령대 (한국 나이)
 */
enum class AgeRange {
    @SerializedName("0~9")
    AGE_0_9,
    @SerializedName("10~14")
    AGE_10_14,
    @SerializedName("15~19")
    AGE_15_19,
    @SerializedName("20~29")
    AGE_20_29,
    @SerializedName("30~39")
    AGE_30_39,
    @SerializedName("40~49")
    AGE_40_49,
    @SerializedName("50~59")
    AGE_50_59,
    @SerializedName("60~69")
    AGE_60_69,
    @SerializedName("70~79")
    AGE_70_79,
    @SerializedName("80~89")
    AGE_80_89,
    @SerializedName("90~")
    AGE_90_ABOVE,
    @UnknownValue
    UNKNOWN;
}

/**
 * 성별
 */
enum class Gender {
    @SerializedName("female")
    FEMALE,
    @SerializedName("male")
    MALE,
    @UnknownValue
    UNKNOWN
}

/**
 * 생일의 양력/음력
 */
enum class BirthdayType {
    @SerializedName("SOLAR")
    SOLAR,
    @SerializedName("LUNAR")
    LUNAR,
    @UnknownValue
    UNKNOWN
}