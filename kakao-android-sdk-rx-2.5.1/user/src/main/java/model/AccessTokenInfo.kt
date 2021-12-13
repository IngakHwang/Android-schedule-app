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
import kotlinx.android.parcel.Parcelize

/** 토큰 정보 요청 API 응답 클래스
 *
 * @property id 사용자 아이디
 * @property expiresIn 해당 access token의 남은 만료시간 (단위: 초)
 * @property appId 해당 access token이 발급된 앱 ID
 */
@Parcelize
data class AccessTokenInfo(
    val id: Long,
    val expiresIn: Long,
    val appId: Int,

    @Deprecated("'초' 단위를 사용하는 'expiresIn' 속성으로 대체되었습니다.")
    @SerializedName("expiresInMillis")
    val expiresInMillis: Long?
) : Parcelable