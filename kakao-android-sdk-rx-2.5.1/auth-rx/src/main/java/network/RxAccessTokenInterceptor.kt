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

import com.kakao.sdk.auth.TokenManagerProvider
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import okhttp3.Interceptor
import okhttp3.Response

/**
 * @suppress
 */
class RxAccessTokenInterceptor(
    private val tokenManagerProvider: TokenManagerProvider = TokenManagerProvider.instance
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response =
        tokenManagerProvider.manager.getToken()?.accessToken?.let {
            chain.proceed(
                chain.request().withAccessToken(it)
            )
        } ?: throw ClientError(
            ClientErrorCause.TokenNotFound,
            "Access token not found. You must login first."
        )
}
