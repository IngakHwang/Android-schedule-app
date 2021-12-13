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
package com.kakao.sdk.common.model

import android.os.Parcelable
import com.kakao.sdk.common.json.IntEnum
import kotlinx.android.parcel.Parcelize

/**
 * 카카오 API 호출 시 에러 응답
 *
 * @property code 에러 코드
 * @property msg 자세한 에러 설명
 * @property requiredScopes API 호출을 위해 추가로 필요한 동의 항목
 */
@Parcelize
data class ApiErrorResponse(
    val code: Int,
    val msg: String,
    val apiType: String? = null,
    val requiredScopes: List<String>?,
    val allowedScopes: List<String>? = null
) : Parcelable