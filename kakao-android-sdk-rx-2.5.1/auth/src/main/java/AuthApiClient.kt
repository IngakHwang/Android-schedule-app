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
package com.kakao.sdk.auth

import com.kakao.sdk.auth.model.AccessTokenResponse
import com.kakao.sdk.auth.model.AgtResponse
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.auth.network.kauth
import com.kakao.sdk.network.ApiFactory
import com.kakao.sdk.common.util.KakaoJson
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.common.model.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response


class AuthApiClient(
    private val authApi: AuthApi = ApiFactory.kauth.create(AuthApi::class.java),
    /** @suppress */ val tokenManagerProvider: TokenManagerProvider = TokenManagerProvider.instance,
    /** @suppress */ val applicationInfo: ApplicationInfo = KakaoSdk.applicationContextInfo,
    /** @suppress */ val contextInfo: ContextInfo = KakaoSdk.applicationContextInfo,
    /** @suppress */ val approvalType: ApprovalType = KakaoSdk.approvalType
) {

    /**
     * 사용자가 앞서 로그인을 통해 토큰을 발급 받은 상태인지 확인합니다.
     * 주의: 기존 토큰 존재 여부를 확인하는 기능으로, 사용자가 현재도 로그인 상태임을 보장하지 않습니다.
     */
    fun hasToken() : Boolean {
        return tokenManagerProvider.manager.getToken() != null
    }

    /**
     * 사용자 인증코드를 이용하여 신규 토큰 발급을 요청합니다.
     *
     * @param code 사용자 인증 코드
     * @param codeVerifier 사용자 인증 코드 verifier
     * @param callback 발급 받은 [OAuthToken] 반환.
     */
    fun issueAccessToken(
        code: String,
        codeVerifier: String? = null,
        callback: (token: OAuthToken?, error: Throwable?) -> Unit
    ) {
        authApi.issueAccessToken(
            clientId = applicationInfo.appKey,
            androidKeyHash = contextInfo.signingKeyHash,
            code = code,
            redirectUri = applicationInfo.redirectUri,
            codeVerifier = codeVerifier,
            approvalType = approvalType.value
        ).enqueue(object: Callback<AccessTokenResponse> {
            override fun onFailure(call: Call<AccessTokenResponse>, t: Throwable) {
                callback(null, t)
            }

            override fun onResponse(
                call: Call<AccessTokenResponse>,
                response: Response<AccessTokenResponse>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        val token = OAuthToken.fromResponse(it)
                        tokenManagerProvider.manager.setToken(token)
                        callback(token, null)
                        return
                    }
                    callback(null, ClientError(ClientErrorCause.Unknown, "No body"))
                } else {
                    callback(null, translateError(HttpException(response)))
                }
            }
        })
    }

    /**
     * 기존 토큰을 갱신합니다
     *
     * @param oldToken 기존 토큰
     * @param callback 발급 받은 [OAuthToken] 반환.
     */
    @JvmOverloads
    fun refreshAccessToken(
        oldToken: OAuthToken = tokenManagerProvider.manager.getToken() ?: throw ClientError(ClientErrorCause.TokenNotFound, "Refresh token not found. You must login first."),
        callback: (token: OAuthToken?, error: Throwable?) -> Unit
    ) {
        authApi.refreshAccessToken(
            clientId = applicationInfo.appKey,
            androidKeyHash = contextInfo.signingKeyHash,
            refreshToken = oldToken.refreshToken,
            approvalType = approvalType.value
        ).enqueue(object : Callback<AccessTokenResponse> {
            override fun onFailure(call: Call<AccessTokenResponse>, t: Throwable) {
                callback(null, t)
            }

            override fun onResponse(
                call: Call<AccessTokenResponse>,
                response: Response<AccessTokenResponse>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        val token = OAuthToken.fromResponse(it, oldToken)
                        tokenManagerProvider.manager.setToken(token)
                        callback(token, null)
                        return
                    }
                    callback(null, ClientError(ClientErrorCause.Unknown, "No body"))
                } else {
                    callback(null, translateError(HttpException(response)))
                }
            }

        })
    }

    /**
     * @suppress
     */
    fun agt(
        callback: (agt: String?, error: Throwable?) -> Unit
    ) {
        tokenManagerProvider.manager.getToken()?.accessToken?.let { accessToken ->
            authApi.agt(
                clientId = applicationInfo.appKey,
                accessToken = accessToken
            ).enqueue(object: Callback<AgtResponse> {
                override fun onFailure(call: Call<AgtResponse>, t: Throwable) {
                    callback(null, t)
                }

                override fun onResponse(call: Call<AgtResponse>, response: Response<AgtResponse>) {
                    response.body()?.let { agtResponse ->
                        callback(agtResponse.agt, null)
                        return
                    }
                    callback(null, translateError(HttpException(response)))
                }
            })
        } ?: callback(null, ClientError(ClientErrorCause.TokenNotFound, "Access token not found. You must login first."))
    }

    /**
     * @suppress
     */
    fun refreshAccessToken(
        oldToken: OAuthToken = tokenManagerProvider.manager.getToken() ?: throw ClientError(ClientErrorCause.TokenNotFound, "Refresh token not found. You must login first.")
    ): OAuthToken {
        val response =
            authApi.refreshAccessToken(
                clientId = applicationInfo.appKey,
                androidKeyHash = contextInfo.signingKeyHash,
                refreshToken = oldToken.refreshToken,
                approvalType = approvalType.value
            ).execute()

        val token = response.body()?.let {
            OAuthToken.fromResponse(it, oldToken)
        } ?: throw translateError(HttpException(response))

        tokenManagerProvider.manager.setToken(token)
        return token
    }

    companion object {
        /**
         * @suppress
         */
        fun translateError(t: Throwable): Throwable {
            try {
                if (t is HttpException) {
                    val errorString = t.response()?.errorBody()?.string()
                    val response =
                        KakaoJson.fromJson<AuthErrorResponse>(errorString!!, AuthErrorResponse::class.java)
                    val cause =
                        kotlin.runCatching {
                            KakaoJson.fromJson<AuthErrorCause>(response.error, AuthErrorCause::class.java)
                        }.getOrDefault(
                            AuthErrorCause.Unknown
                        )
                    return AuthError(t.code(), cause, response)
                }
                return t
            } catch (unexpected: Throwable) {
                return unexpected
            }
        }

        @JvmStatic
        val instance by lazy { AuthApiClient() }
    }
}