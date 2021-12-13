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

import com.kakao.sdk.v2.common.BuildConfig
import com.kakao.sdk.common.Constants
import com.kakao.sdk.common.util.KakaoJson
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.common.util.SdkLog
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory

/**
 * @suppress
 */
object ApiFactory {
    fun withClientAndAdapter(
        url: String,
        clientBuilder: OkHttpClient.Builder,
        factory: CallAdapter.Factory? = null
    ): Retrofit {
        val builder = Retrofit.Builder().baseUrl(url)
            .addConverterFactory(KakaoRetrofitConverterFactory())
            .addConverterFactory(GsonConverterFactory.create(KakaoJson.base))
            .client(clientBuilder.build())
        factory?.let {
            builder.addCallAdapterFactory(it)
        }
        return builder.build()

    }

    val loggingInterceptor by lazy {
        val interceptor = HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger {
            override fun log(message: String) {
                SdkLog.i(message)
            }
        })

        interceptor.level =
            if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.HEADERS
        return@lazy interceptor
    }

    val kapi by lazy {
        withClientAndAdapter(
            "${Constants.SCHEME}://${KakaoSdk.hosts.kapi}",
            OkHttpClient.Builder()
                .addInterceptor(KakaoAgentInterceptor())
                .addInterceptor(AppKeyInterceptor())
                .addInterceptor(loggingInterceptor)
        )
    }
}
