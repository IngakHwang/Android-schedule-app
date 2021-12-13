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

import android.content.Intent
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * 카카오링크 API 호출 결과.
 *
 * @property intent 요청한 템플릿 정보로 카카오링크를 실행할 수 있는 Intent.
 * @property warningMsg 템플릿 검증 결과
 * @property argumentMsg templateArgs 검증 결과
 */
@Parcelize
data class LinkResult(
    val intent: Intent,
    val warningMsg: Map<String, String>,
    val argumentMsg: Map<String, String>
) : Parcelable
