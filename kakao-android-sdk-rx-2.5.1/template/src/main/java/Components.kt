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
package com.kakao.sdk.template.model

import android.os.Parcelable
import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import com.kakao.sdk.common.json.MapToQueryAdapter
import com.kakao.sdk.template.Constants
import kotlinx.android.parcel.Parcelize

/**
 * 메시지 하단에 추가되는 버튼 오브젝트.
 *
 * @param title 버튼의 타이틀
 * @param link 버튼 클릭 시 이동할 링크 정보
 */
@Parcelize
data class Button(val title: String, val link: Link) : Parcelable

/**
 * 가격 정보를 표현하기 위해 사용되는 오브젝트.
 *
 * @param regularPrice 정상가격
 * @param discountPrice 할인된 가격
 * @param discountRate 할인율
 * @param fixedDiscountPrice 정액 할인 가격
 * @param productName 상품명
 */
@Parcelize
data class Commerce @JvmOverloads constructor(
    val regularPrice: Int,
    val discountPrice: Int? = null,
    val fixedDiscountPrice: Int? = null,
    val discountRate: Int? = null,
    val productName: String? = null
) : Parcelable

/**
 * 콘텐츠의 내용을 담고 있는 오브젝트.
 *
 * @param title 콘텐츠의 타이틀
 * @param imageUrl 콘텐츠의 이미지 URL
 * @param link 콘텐츠 클릭 시 이동할 링크 정보
 * @param imageWidth 콘텐츠의 이미지 너비 (단위: 픽셀)
 * @param imageHeight 콘텐츠의 이미지 높이 (단위: 픽셀)
 */
@Parcelize
data class Content @JvmOverloads constructor(
    val title: String,
    val imageUrl: String,
    val link: Link,
    val description: String? = null,
    val imageWidth: Int? = null,
    val imageHeight: Int? = null
) : Parcelable

/**
 * 메시지에서 콘텐츠 영역이나 버튼 클릭 시에 이동되는 링크 정보 오브젝트.
 *
 * @param webUrl PC 버전 카카오톡에서 사용하는 웹 링크 URL.
 * @param mobileWebUrl 모바일 카카오톡에서 사용하는 웹 링크 URL.
 * @param androidExecutionParams 안드로이드 카카오톡에서 사용하는 앱 링크 URL에 추가할 파라미터.
 * @param iosExecutionParams iOS 카카오톡에서 사용하는 앱 링크 URL에 추가할 파라미터.
 */
@Parcelize
data class Link @JvmOverloads constructor(
    val webUrl: String? = null,
    val mobileWebUrl: String? = null,
    @JsonAdapter(MapToQueryAdapter::class)
    @SerializedName(Constants.ANDROID_EXECUTION_PARAMS) val androidExecutionParams: Map<String, String>? = null,
    @JsonAdapter(MapToQueryAdapter::class)
    @SerializedName(Constants.IOS_EXECUTION_PARAMS) val iosExecutionParams: Map<String, String>? = null
) : Parcelable

/**
 * 좋아요 수, 댓글 수 등의 소셜 정보를 표현하기 위해 사용되는 오브젝트.
 * @param likeCount 콘텐츠의 좋아요 수
 * @param commentCount 콘텐츠의 댓글 수
 * @param sharedCount 콘텐츠의 공유 수
 * @param viewCount 콘텐츠의 조회 수
 * @param subscriberCount 콘텐츠의 구독 수
 */
@Parcelize
data class Social @JvmOverloads constructor(
    val likeCount: Int? = null,
    val commentCount: Int? = null,
    val sharedCount: Int? = null,
    val viewCount: Int? = null,
    val subscriberCount: Int? = null
) : Parcelable