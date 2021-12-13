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

import com.kakao.sdk.common.Constants
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.common.model.ContextInfo
import okhttp3.Interceptor
import okhttp3.Response

/**
 * @suppress
 */
class KakaoAgentInterceptor(val contextInfo: ContextInfo = KakaoSdk.applicationContextInfo) :
    Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()

        val header = contextInfo.kaHeader
        request = request.newBuilder()
            .addHeader(Constants.KA, header)
            .build()
        return chain.proceed(request)
    }
}