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
import kotlinx.android.parcel.Parcelize
import java.util.*

/**
 * 스토리 조회 API 응답 클래스
 *
 * @property id 내스토리 정보의 id (포스트 id)
 * @property url 내스토리 정보의 url
 * @property mediaType 미디어 형
 * @property createdAt 작성된 시간
 * @property commentCount 댓글 수
 * @property likeCount 좋아요 수
 * @property content 포스팅 내용
 * @property permission 공개 범위
 * @property media 미디어 목록
 * @property likes 좋아요 정보 목록
 * @property comments 댓글 목록
 */
@Parcelize
data class Story(
    val id: String,
    val url: String,
    val mediaType: String?,
    val createdAt: Date,
    val commentCount: Int,
    val likeCount: Int,
    val content: String?,
    val permission: String?,
    val media: List<StoryImage>?,
    val likes: List<StoryLike>?,
    val comments: List<StoryComment>?
) : Parcelable {

    /**
     * 스토리의 공개 범위
     */
    enum class Permission {
        /**
         * 전체공개
         */
        @SerializedName("A")
        PUBLIC,

        /**
         * 친구공개
         */
        @SerializedName("F")
        FRIEND,

        /**
         * 나만보기
         */
        @SerializedName("M")
        ONLY_ME,

        @UnknownValue
        UNKNOWN;
    }
}

/**
 * 카카오스토리의 내스토리 정보 중 이미지 내용을 담고 있는 클래스
 *
 * @property xlarge 1280 * 1706
 * @property large 720 * 960
 * @property medium 240 * 320
 * @property small 160 * 213
 * @property original 원본 이미지의 url
 */
@Parcelize
data class StoryImage(
    val xlarge: String?,
    val large: String?,
    val medium: String?,
    val small: String?,
    val original: String?
) : Parcelable

/**
 * 카카오스토리의 작성자 정보를 담고 있는 클래스
 */
@Parcelize
data class StoryActor(val displayName: String, val profileThumbnailUrl: String?) : Parcelable

/**
 * 카카오스토리의 댓글 정보를 담고 있는 클래스
 */
@Parcelize
data class StoryComment(val writer: StoryActor, val text: String) : Parcelable

/**
 * 카카오스토리의 좋아요 등 느낌(감정표현)에 대한 정보를 담고 있는 클래스
 */
@Parcelize
data class StoryLike(val actor: StoryActor, val emotion: Emotion) : Parcelable {
    enum class Emotion {
        /**
         * 좋아요
         */
        @SerializedName("LIKE")
        LIKE,

        /**
         * 멋져요
         */
        @SerializedName("COOL")
        COOL,

        /**
         * 기뻐요
         */
        @SerializedName("HAPPY")
        HAPPY,

        /**
         * 슬퍼요
         */
        @SerializedName("SAD")
        SAD,

        /**
         * 힘내요
         */
        @SerializedName("CHEER_UP")
        CHEER_UP,

        /**
         * 정의되지 않은 느낌
         */
        @UnknownValue
        UNKNOWN;
    }
}