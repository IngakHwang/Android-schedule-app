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

import android.net.Uri
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.kakao.sdk.common.util.KakaoJson
import com.kakao.sdk.talk.Constants.FRIEND_ORDER
import com.kakao.sdk.talk.Constants.LIMIT
import com.kakao.sdk.talk.Constants.OFFSET
import com.kakao.sdk.talk.Constants.ORDER
import kotlinx.android.parcel.Parcelize

/**
 * 친구 목록 조회 API 응답 클래스
 */
@Parcelize
data class Friends<T : Parcelable>(
    val totalCount: Int,
    val elements: List<T>?,

    /// 조회된 친구 중 즐겨찾기에 등록된 친구 수
    val favoriteCount: Int,

    val beforeUrl: String?,
    val afterUrl: String?

) : Parcelable {

    companion object {
        fun <T : Parcelable> fromJson(string: String, clazz: Class<T>): Friends<T> =
            KakaoJson.parameterizedFromJson(string, Friends::class.java, clazz)
    }
}


/**
 * 친구 목록 조회 Context
 */
@Parcelize
data class FriendsContext(
    var offset: Int? = null,
    var limit: Int? = null,
    var order: Order? = null,
    var friendOrder: FriendOrder? = null,
    val url: String = ""
) : Parcelable {

    @Throws(IllegalArgumentException::class)
    constructor(url: String) : this(offset = null, limit = null, order = null, friendOrder = null) {

        val uri = Uri.parse(url) ?: throw IllegalArgumentException()

        offset = uri.getQueryParameter(OFFSET)?.toInt()
        limit = uri.getQueryParameter(LIMIT)?.toInt()
        order = uri.getQueryParameter(ORDER)?.let {
            try {
                Order.values().find { order ->
                    order.javaClass.getField(order.name)
                        .getAnnotation(SerializedName::class.java)?.value == it
                }
            } catch (e: IllegalArgumentException) {
                null
            }
        }
        friendOrder = uri.getQueryParameter(FRIEND_ORDER)?.let {
            try {
                FriendOrder.values().find { friendOrder ->
                    friendOrder.javaClass.getField(friendOrder.name)
                        .getAnnotation(SerializedName::class.java)?.value == it
                }
            } catch (e: IllegalArgumentException) {
                null
            }
        }
    }
}


/**
 * 카카오톡 친구
 * @property id 사용자 아이디
 * @property uuid 메시지를 전송하기 위한 고유 아이디. 사용자의 계정 상태에 따라 이 정보는 바뀔 수 있으므로 앱내의 사용자 식별자로는 권장하지 않음.
 * @property profileNickname 친구의 닉네임
 * @property profileThumbnailImage 썸네일 이미지 URL
 * @property favorite 즐겨찾기 추가 여부
 */
@Parcelize
data class Friend(
    val id: Long?,
    val uuid: String,
    val profileNickname: String?,
    val profileThumbnailImage: String?,
    val favorite: Boolean?
) : Parcelable

enum class Order {
    @SerializedName("asc")
    ASC,

    @SerializedName("desc")
    DESC
}

/**
 * 친구 목록 정렬 기준.
 */
enum class FriendOrder {
    /**
     * 이름 순 정렬
     */
    @SerializedName("nickname")
    NICKNAME,

    /**
     * 나이 순 정렬
     */
    @SerializedName("age")
    AGE,

    /**
     * 즐겨찾기 순 정렬
     */
    @SerializedName("favorite")
    FAVORITE;
}