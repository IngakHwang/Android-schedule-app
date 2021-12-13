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
package com.kakao.sdk.talk.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.kakao.sdk.talk.Constants
import kotlinx.android.parcel.Parcelize

/**
 * 카카오톡 프로필 조회 API 응답 클래스
 *
 * @property nickname 카카오톡 닉네임
 * @property profileImageUrl 카카오톡 프로필 이미지 URL
 * @property thumbnailUrl 카카오톡 프로필 이미지 썸네일 URL
 * @property countryISO 카카오톡 국가 코드
 */
@Parcelize
data class TalkProfile(
    @SerializedName(Constants.NICKNAME) val nickname: String?,
    @SerializedName(Constants.PROFILE_IMAGE_URL) val profileImageUrl: String?,
    @SerializedName(Constants.THUMBNAIL_URL) val thumbnailUrl: String?,
    @SerializedName(Constants.COUNTRY_ISO) val countryISO: String?
) : Parcelable