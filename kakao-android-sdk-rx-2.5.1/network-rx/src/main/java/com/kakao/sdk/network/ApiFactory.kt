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
package com.kakao.sdk.network

import com.kakao.sdk.common.*
import com.kakao.sdk.common.util.KakaoJson
import com.kakao.sdk.common.util.SdkLog
import com.kakao.sdk.network.ApiFactory
import com.kakao.sdk.network.AppKeyInterceptor
import com.kakao.sdk.network.KakaoAgentInterceptor
import com.kakao.sdk.network.KakaoRetrofitConverterFactory
import com.kakao.sdk.v2.common.rx.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

/**
 * @suppress
 */
fun ApiFactory.withClient(url: String, clientBuilder: OkHttpClient.Builder): Retrofit {
    return Retrofit.Builder().baseUrl(url)
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(KakaoRetrofitConverterFactory())
        .addConverterFactory(GsonConverterFactory.create(KakaoJson.base))
        .client(clientBuilder.build())
        .build()
}

/**
 * @suppress
 */
val ApiFactory.rxKapi by lazy {
    ApiFactory.withClientAndAdapter(
        "${Constants.SCHEME}://${KakaoSdk.hosts.kapi}",
        OkHttpClient.Builder()
            .addInterceptor(KakaoAgentInterceptor())
            .addInterceptor(AppKeyInterceptor())
            .addInterceptor(ApiFactory.loggingInterceptor),
        RxJava2CallAdapterFactory.create()
    )
}

/**
 * @suppress
 */
val ApiFactory.loggingInterceptor by lazy {
    val interceptor =
        HttpLoggingInterceptor(
            HttpLoggingInterceptor.Logger { SdkLog.i(it) }
        )

    interceptor.level =
        if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.HEADERS
    return@lazy interceptor
}