/*
  Copyright 2021 Kakao Corp.

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
import kotlinx.android.parcel.Parcelize

/**
 * @property id 회원번호
 * @property scopes 해당 앱의 동의 항목 목록
 */
@Parcelize
data class ScopeInfo(val id: Long, val scopes: List<Scope>?) : Parcelable

/**
 * 사용자 동의 항목
 *
 * @property id 동의항목 ID
 * @property displayName 사용자 동의 화면에 출력되는 동의 항목 이름 또는 설명
 * @property type 동의 항목 타입 PRIVACY(개인정보 보호 동의 항목) / SERVICE(접근권한 관리 동의 항목) 중 하나
 * @property using 동의 항목의 현재 사용 여부. 사용자가 동의했으나 현재 앱에 설정되어 있지 않은 동의 항목의 경우 false
 * @property delegated 카카오가 관리하지 않는 위임 동의 항목인지 여부. 현재 사용 중인 동의 항목이고, 위임 동의 항목인 경우에만 응답에 포함
 * @property agreed 사용자 동의 여부
 * @property revocable 동의 항목의 동의 철회 가능 여부. 사용자가 동의한 동의 항목인 경우에만 응답에 포함
 */
@Parcelize
data class Scope(
    val id: String,
    val displayName: String,
    val type: ScopeType,
    val using: Boolean,
    val delegated: Boolean?,
    val agreed: Boolean,
    val revocable: Boolean?
) : Parcelable

enum class ScopeType {
    PRIVACY,
    SERVICE
}