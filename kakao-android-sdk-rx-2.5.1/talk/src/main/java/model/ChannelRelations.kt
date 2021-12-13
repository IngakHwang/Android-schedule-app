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
import com.kakao.sdk.common.json.UnknownValue
import com.kakao.sdk.talk.Constants
import kotlinx.android.parcel.Parcelize
import java.util.*

/**
 * 카카오톡 채널 추가상태 조회 API 응답 클래스
 *
 * @property userId 사용자 아이디
 * @property channels 사용자의 채널 추가상태 목록
 */
@Parcelize
data class Channels(
    val userId: Long?,
    val channels: List<Channel>?
) :
    Parcelable

/**
 * 카카오톡 채널 추가상태 정보.
 *
 * @property uuid 채널의 uuid
 * @property encodedId encoded channel public id (ex. https://pf.kakao.com/${channelId})
 * @property relation 사용자의 채널 추가 상태
 * @property updatedAt 마지막 상태 변경 일시 (현재는 ADDED 상태의 친구 추가시각만 의미)
 */
@Parcelize
data class Channel(
    @SerializedName(Constants.CHANNEL_UUID) val uuid: String,
    @SerializedName(Constants.CHANNEL_PUBLIC_ID) val encodedId: String,
    val relation: Relation,
    val updatedAt: Date?
) : Parcelable {

    /**
     * 카카오톡 채널과의 관계
     */
    enum class Relation {
        /**
         * 추가된 상태
         */
        @SerializedName("ADDED")
        ADDED,

        /**
         * 관계없음
         */
        @SerializedName("NONE")
        NONE,

        /**
         * 차단 상태
         */
        @SerializedName("BLOCKED")
        BLOCKED,

        @UnknownValue
        UNKNOWN;
    }
}

