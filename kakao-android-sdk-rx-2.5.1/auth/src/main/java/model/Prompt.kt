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
package com.kakao.sdk.auth.model

import com.google.gson.annotations.SerializedName

/**
 * 동의 화면 요청 시 추가 상호작용을 요청하고자 할 때 전달하는 파라미터
 */
enum class Prompt {
    /**
     * 기본 웹 브라우저(CustomTabs)에 카카오계정 cookie 가 이미 있더라도 이를 무시하고 무조건 로그인 화면을 보여주도록 함
     */
    @SerializedName("login")
    LOGIN
}
