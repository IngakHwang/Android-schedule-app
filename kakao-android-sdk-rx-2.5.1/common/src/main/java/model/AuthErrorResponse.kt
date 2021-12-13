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
import kotlinx.android.parcel.Parcelize

/**
 * 카카오 OAuth API 호출 시 에러 응답
 *
 * @property error invalid_grant 등 어떤 에러인지 나타내주는 스트링 값
 * @property errorDescription 자세한 에러 설명
 */
@Parcelize
data class AuthErrorResponse(val error: String, val errorDescription: String?) : Parcelable