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
@file:JvmName("AuthApiClientKt")

package com.kakao.sdk.auth

import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.auth.network.rxKauth
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.common.model.*
import com.kakao.sdk.common.util.SdkLog
import com.kakao.sdk.network.ApiFactory

import io.reactivex.Single
import io.reactivex.SingleTransformer

/**
 * @suppress
 */
class RxAuthApiClient(
    private val authApi: RxAuthApi = ApiFactory.rxKauth.create(RxAuthApi::class.java),
    /** @suppress */ val tokenManagerProvider: TokenManagerProvider = TokenManagerProvider.instance,
    /** @suppress */ val applicationInfo: ApplicationInfo = KakaoSdk.applicationContextInfo,
    /** @suppress */ val contextInfo: ContextInfo = KakaoSdk.applicationContextInfo,
    /** @suppress */ val approvalType: ApprovalType = KakaoSdk.approvalType
) {

    fun issueAccessToken(
        code: String,
        codeVerifier: String? = null
    ): Single<OAuthToken> =
        authApi.issueAccessToken(
            clientId = applicationInfo.appKey,
            androidKeyHash = contextInfo.signingKeyHash,
            code = code,
            redirectUri = applicationInfo.redirectUri,
            codeVerifier = codeVerifier,
            approvalType = approvalType.value
        )
            .compose(handleAuthError())
            .map { OAuthToken.fromResponse(it) }
            .doOnSuccess { tokenManagerProvider.manager.setToken(it) }


    @JvmOverloads
    fun refreshAccessToken(
        oldToken: OAuthToken = tokenManagerProvider.manager.getToken() ?: throw ClientError(ClientErrorCause.TokenNotFound, "Refresh token not found. You must login first.")
    ): Single<OAuthToken> =
        authApi.refreshAccessToken(
            clientId = applicationInfo.appKey,
            androidKeyHash = contextInfo.signingKeyHash,
            refreshToken = oldToken.refreshToken,
            approvalType = approvalType.value
        )
            .compose(handleAuthError())
            .map { OAuthToken.fromResponse(it, oldToken) }
            .doOnSuccess { tokenManagerProvider.manager.setToken(it) }


    /**
     * @suppress
     */
    fun agt(): Single<String> =
        Single.just(tokenManagerProvider.manager.getToken()?.accessToken)
            .flatMap { authApi.agt(clientId = KakaoSdk.applicationContextInfo.appKey, accessToken = it) }
            .compose(handleAuthError())
            .map { it.agt }

    companion object {
        @JvmStatic
        val instance by lazy { AuthApiClient.rx }

        fun <T> handleAuthError(): SingleTransformer<T, T> = SingleTransformer {
            it.onErrorResumeNext { Single.error(AuthApiClient.translateError(it)) }
                .doOnError { SdkLog.e(it) }
                .doOnSuccess { SdkLog.i(it!!) }
        }
    }
}

val AuthApiClient.Companion.rx by lazy { RxAuthApiClient() }