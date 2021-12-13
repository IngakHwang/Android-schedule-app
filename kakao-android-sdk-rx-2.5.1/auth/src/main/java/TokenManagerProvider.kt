/*
  Copyright 2020 Kakao Corp.

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
package com.kakao.sdk.auth

/**
 * Kakao SDK가 사용하게 될 토큰 저장소 제공자.
 *
 * Kakao SDK는 로그인에 성공하면 이 제공자를 통해 현재 지정된 토큰 저장소에 토큰 저장.
 * 저장된 토큰은 로그인 기반 API 호출 시 자동으로 인증 헤더에 추가됨.
 *
 * [TokenManager.instance]를 기본 저장소로 사용하며, 토큰을 직접 관리하고 싶은 경우 [TokenManageable] 인터페이스를 구현하여 나만의 저장소 설정 가능.
 * 앱 서비스 도중 저장소 구현을 변경하는 경우, 앱 업데이트 사용자를 위하여 기존에 저장되어 있던 토큰 마이그레이션 고려해야 함.
 *
 *  ```kotlin
 *  // 사용자 정의 저장소 설정하기
 *  TokenManagerProvider.instance.manager = MyTokenManager()
 *  ```
 *
 * @property manager 현재 지정된 토큰 저장소. 기본 값 [TokenManager.instance]
 */
class TokenManagerProvider(
    var manager: TokenManageable = TokenManager.instance
) {

    companion object {

        /**
         * singleton 객체
         */
        @JvmStatic
        val instance by lazy { TokenManagerProvider() }
    }
}
