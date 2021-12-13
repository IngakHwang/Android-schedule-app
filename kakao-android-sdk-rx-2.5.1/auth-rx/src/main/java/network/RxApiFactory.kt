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
package com.kakao.sdk.auth.network

import com.kakao.sdk.common.Constants
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.network.ApiFactory
import com.kakao.sdk.network.KakaoAgentInterceptor
import okhttp3.OkHttpClient
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory

/**
 * @suppress
 */
val ApiFactory.rxKapiWithOAuth by lazy {
    ApiFactory.withClientAndAdapter(
        "${Constants.SCHEME}://${KakaoSdk.hosts.kapi}",
        OkHttpClient.Builder()
            .addInterceptor(KakaoAgentInterceptor())
            .addInterceptor(RxAccessTokenInterceptor())
            .addInterceptor(ApiFactory.loggingInterceptor),
        RxJava2CallAdapterFactory.create()
    )
}

/**
 * @suppress
 */
val ApiFactory.rxKauth by lazy {
    ApiFactory.withClientAndAdapter(
        "${Constants.SCHEME}://${KakaoSdk.hosts.kauth}",
        OkHttpClient.Builder()
            .addInterceptor(KakaoAgentInterceptor())
            .addInterceptor(ApiFactory.loggingInterceptor),
        RxJava2CallAdapterFactory.create()
    )
}