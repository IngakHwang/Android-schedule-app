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
import com.kakao.sdk.story.Constants
import kotlinx.android.parcel.Parcelize

/**
 * 카카오스토리 포스팅을 위한 스크랩 API 응답 클래스
 *
 * @property url 스크랩 한 주소의 URL. shorten URL 의 경우 resolution 한 실제 URL
 * @property requestedUrl 요청시의 URL 원본. resolution 을 하기 전의 URL
 * @property host 스크랩한 호스트 도메인
 * @property title 웹 페이지의 제목
 * @property description 웹 페이지의 설명
 * @property section 웹 페이지의 섹션 정보
 * @property type 웹 페이지의 콘텐츠 타입. 예) video, music, book, article, profile, website 등.
 * @property images 웹 페이지의 대표 이미지 주소의 url array. 최대 3개.
 */
@Parcelize
data class LinkInfo(
    val url: String?,
    val requestedUrl: String?,
    val host: String?,
    val title: String?,
    val description: String?,
    val section: String?,
    val type: String?,
    @SerializedName(Constants.IMAGE) val images: List<String>?
) : Parcelable