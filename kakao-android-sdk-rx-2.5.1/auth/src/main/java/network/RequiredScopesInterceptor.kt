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
import com.kakao.sdk.auth.AuthCodeClient
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.common.model.ApiErrorCause
import com.kakao.sdk.common.model.ApplicationContextInfo
import com.kakao.sdk.network.ExceptionWrapper
import com.kakao.sdk.network.proceedApiError
import okhttp3.Interceptor
import okhttp3.Response
import java.util.concurrent.CountDownLatch

/**
 * @suppress
 *
 * -402 에러 시 자동 추가 동의
 */
class RequiredScopesInterceptor(
    private val contextInfo: ApplicationContextInfo = KakaoSdk.applicationContextInfo
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {

        return chain.proceedApiError(chain.request()) { response, error ->
            val requiredScopes = error.response.requiredScopes

            if (error.reason == ApiErrorCause.InsufficientScope && !requiredScopes.isNullOrEmpty()) {
                // TODO: 중복으로 동의 요청이 필요한 상황이 발생하면 어떻게 처리할 것인가? 사용자 인터랙션이 필요하기 때문에 토큰 갱신할 때 처럼 단순하게 Lock을 걸어서 처리하면 안될 것 같다.
                var token: OAuthToken? = null
                var error: Throwable? = null

                val latch = CountDownLatch(1)
                AuthApiClient.instance.agt { agt, agtError ->
                    if (agtError != null) {
                        error = agtError
                        latch.countDown()
                    } else {

                        val codeVerifier = AuthCodeClient.codeVerifier()
                        AuthCodeClient.instance.authorizeWithKakaoAccount(
                            contextInfo.applicationContext,
                            scopes = requiredScopes,
                            agt = agt,
                            codeVerifier = codeVerifier
                        ) { code, codeError ->
                            if (codeError != null) {
                                error = codeError
                                latch.countDown()
                            } else {
                                AuthApiClient.instance.issueAccessToken(
                                    code!!,
                                    codeVerifier
                                ) { t, e ->
                                    token = t
                                    error = e
                                    latch.countDown()
                                }
                            }
                        }
                    }
                }
                latch.await()

                return token?.accessToken?.let {
                    chain.proceed(
                        response.request().withAccessToken(it)
                    )
                } ?: throw ExceptionWrapper(error!!)
            }

            return response
        }
    }
}