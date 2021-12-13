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
package com.kakao.sdk.story.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.kakao.sdk.common.json.UnknownValue
import com.kakao.sdk.story.Constants
import kotlinx.android.parcel.Parcelize

/**
 * 카카오스토리 프로필 조회 API 응답 클래스
 *
 * @property nickname 카카오스토리 닉네임
 * @property profileImageUrl 카카오스토리 프로필 이미지 URL
 * @property thumbnailUrl 카카오스토리 프로필 이미지 썸네일 URL
 * @property bgImageUrl 카카오스토리 배경이미지 URL
 * @property permalink 카카오스토리 permanent link. 내 스토리를 방문할 수 있는 웹 page 의 URL
 * @property birthday 생일 (MMDD)
 * @property birthdayType 생일 타입
 */
@Parcelize
data class StoryProfile(
    @SerializedName(Constants.NICKNAME) val nickname: String?,
    @SerializedName(Constants.PROFILE_IMAGE_URL) val profileImageUrl: String?,
    @SerializedName(Constants.THUMBNAIL_URL) val thumbnailUrl: String?,
    @SerializedName(Constants.BG_IMAGE_URL) val bgImageUrl: String?,
    val permalink: String?,
    val birthday: String?,
    @SerializedName(Constants.BIRTHDAY_TYPE) val birthdayType: BirthdayType?
) : Parcelable

/**
 * 스토리 프로필의 생일 타입
 */
enum class BirthdayType {
    /**
     * 양력 생일
     */
    SOLAR,

    /**
     * 음력 생일
     */
    LUNAR,

    @UnknownValue
    UNKNOWN;
}