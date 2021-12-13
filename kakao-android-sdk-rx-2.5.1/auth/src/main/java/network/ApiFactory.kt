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
package com.kakao.sdk.auth.network

import com.kakao.sdk.network.ApiFactory
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.common.Constants
import com.kakao.sdk.network.KakaoAgentInterceptor
import okhttp3.OkHttpClient


/**
 * 비RX 로그인기반 kapi.kakao.com 클라이언트 생성
 *
 * @suppress
 */
val ApiFactory.kapiWithOAuth by lazy {
    ApiFactory.withClientAndAdapter(
        "${Constants.SCHEME}://${KakaoSdk.hosts.kapi}",
        OkHttpClient.Builder()
            .addInterceptor(KakaoAgentInterceptor())
            .addInterceptor(AccessTokenInterceptor())
            .addInterceptor(RequiredScopesInterceptor())
            .addInterceptor(ApiFactory.loggingInterceptor)
    )
}

/**
 * 비RX kauth.kakao.com 클라이언트 생성
 *
 * @suppress
 */
val ApiFactory.kauth by lazy {
    ApiFactory.withClientAndAdapter(
        "${Constants.SCHEME}://${KakaoSdk.hosts.kauth}",
        OkHttpClient.Builder()
            .addInterceptor(KakaoAgentInterceptor())
            .addInterceptor(ApiFactory.loggingInterceptor)
    )
}
