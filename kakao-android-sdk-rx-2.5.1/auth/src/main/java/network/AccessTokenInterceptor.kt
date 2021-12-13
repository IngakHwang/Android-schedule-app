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

import com.kakao.sdk.auth.AuthApiClient
import com.kakao.sdk.auth.TokenManagerProvider
import com.kakao.sdk.common.Constants
import com.kakao.sdk.common.model.ApiErrorCause
import com.kakao.sdk.network.ExceptionWrapper
import com.kakao.sdk.network.proceedApiError
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

/**
 * @suppress
 *
 * API 요청에 AccessToken을 추가하는 인터셉터
 * -401 발생시 자동 갱신
 */
class AccessTokenInterceptor(
    private val tokenManagerProvider: TokenManagerProvider = TokenManagerProvider.instance,
    private val authApiClient: AuthApiClient = AuthApiClient.instance
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val usedAccessToken = tokenManagerProvider.manager.getToken()?.accessToken

        val request =
            usedAccessToken?.let {
                chain.request().withAccessToken(it)
            } ?: chain.request()

        return chain.proceedApiError(request) { response, error ->

            // -401 발생시 자동 갱신
            if (error.reason == ApiErrorCause.InvalidToken) {

                // 나중에 들어온 요청들 pending (중복 갱신 방어)
                synchronized(this) {

                    // resume 돼서 들어왔을 때 현재 토큰 보고
                    val currentToken = tokenManagerProvider.manager.getToken()
                    if (currentToken != null ) {

                        val accessToken =
                            if (currentToken.accessToken != usedAccessToken) {
                                // 이전 요청에서 넣었던 토큰과 현재 토큰이 다르면
                                // 이미 앞의 요청에서 갱신됐다고 판단하고, 현재 토큰 사용
                                currentToken.accessToken
                            }
                            else {
                                try {
                                    // 갱신 요청 이후 토큰 사용
                                    authApiClient.refreshAccessToken(currentToken).accessToken
                                } catch (e: Throwable) {
                                    throw ExceptionWrapper(e)
                                }
                            }

                        // 변경된 accessToken으로 API 재시도
                        return chain.proceed(request.withAccessToken(accessToken))
                    }
                }
            }

            return response
        }
    }
}

/**
 * @suppress
 */
inline fun Request.withAccessToken(accessToken: String) =
    newBuilder()
        .removeHeader(Constants.AUTHORIZATION)
        .addHeader(Constants.AUTHORIZATION, "${Constants.BEARER} $accessToken")
        .build()
