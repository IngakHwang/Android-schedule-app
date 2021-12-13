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
package com.kakao.sdk.user.model

import java.util.*

/**
 * 사용자가 동의한 약관 조회 API 응답 클래스
 *
 * @property userId 사용자 아이디
 * @property allowedServiceTerms 사용자가 동의한 3rd의 약관 항목들
 */
data class UserServiceTerms(
    val userId: Long?,
    val allowedServiceTerms: List<ServiceTerms>?,
    val appServiceTerms: List<AppServiceTerms>?
)

/**
 * 3rd party 서비스 약관 정보 클래스
 *
 * @property tag 동의한 약관의 tag. 3rd 에서 설정한 값
 * @property agreedAt 동의한 시간. 약관이 여러번 뜨는 구조라면, 마지막으로 동의한 시간
 */
data class ServiceTerms(val tag: String, val agreedAt: Date)

/**
 * 앱에 사용 설정된 서비스 약관 목록 클래스
 *
 * @property tag 서비스 약관에 설정된 태그(tag)
 * @property createdAt 서비스 약관이 등록된 시간
 * @property updatedAt 서비스 약관이 수정된 시간
 */
data class AppServiceTerms(val tag: String, val createdAt: Date, val updatedAt: Date)