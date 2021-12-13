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
package com.kakao.sdk.link.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * 이미지 업로드,스크랩 요청 결과
 *
 * @property infos 업로드된 이미지 정보
 */
@Parcelize
data class ImageUploadResult(
    val infos: ImageInfos
) : Parcelable

/**
 * 업로드된 이미지 정보
 *
 * @property original 원본 이미지
 */
@Parcelize
data class ImageInfos(val original: ImageInfo) : Parcelable

/**
 * @property url 업로드 된 이미지의 URL
 * @property contentType 업로드 된 이미지의 Content-Type
 * @property length 업로드 된 이미지의 용량 (단위: 바이트)
 * @property width 업로드 된 이미지의 너비 (단위: 픽셀)
 * @property height 업로드 된 이미지의 높이 (단위: 픽셀)
 */
@Parcelize
data class ImageInfo(
    val url: String,
    val contentType: String,
    val length: Int,
    val width: Int,
    val height: Int
) : Parcelable