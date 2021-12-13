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

import android.content.Context
import com.kakao.sdk.auth.*
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ApiError
import com.kakao.sdk.common.model.ApiErrorCause
import io.reactivex.*
import io.reactivex.schedulers.Schedulers
import org.reactivestreams.Publisher

/**
 * @suppress
 */
class RxAuthOperations(
    private val authApiClient: RxAuthApiClient = AuthApiClient.rx,
    private val authCodeClient: RxAuthCodeClient = AuthCodeClient.rx,
    private val tokenManagerProvider: TokenManagerProvider = TokenManagerProvider.instance
) {
    fun <T> handleApiError(): SingleTransformer<T, T> =
        SingleTransformer { it ->
            it.retryWhen { refreshAccessToken(it) }
        }


    fun handleCompletableError(): CompletableTransformer =
        CompletableTransformer {
            it.retryWhen { refreshAccessToken(it) }
        }

    fun incrementalAuthorizationRequired(context: Context) = { flowable: Flowable<Throwable> ->
        flowable.flatMap { apiError ->
            if (apiError is ApiError
                && apiError.reason == ApiErrorCause.InsufficientScope
                && apiError.response.requiredScopes != null
            ) {
                val codeVerifier = AuthCodeClient.codeVerifier()
                authApiClient.agt()
                    .subscribeOn(Schedulers.io())
                    .flatMap { authCodeClient.authorizeWithKakaoAccount(context, scopes = apiError.response.requiredScopes!!, agt = it, codeVerifier = codeVerifier) }
                    .observeOn(Schedulers.io())
                    .flatMap { authApiClient.issueAccessToken(it, codeVerifier) }.toFlowable()

            } else {
                Flowable.error(apiError)
            }
        }
    }

    @JvmSynthetic
    internal fun refreshAccessToken(throwableFlowable: Flowable<Throwable>): Publisher<OAuthToken> =
        throwableFlowable.take(3).flatMap {
            val token = tokenManagerProvider.manager.getToken()
            if (token == null || it !is ApiError || it.reason != ApiErrorCause.InvalidToken) {
                throw it
            }
            return@flatMap authApiClient.refreshAccessToken(token).toFlowable()
        }

    companion object {
        @JvmStatic
        val instance by lazy {
            RxAuthOperations()
        }
    }
}