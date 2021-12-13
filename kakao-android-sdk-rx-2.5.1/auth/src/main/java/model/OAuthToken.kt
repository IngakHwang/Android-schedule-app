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
package com.kakao.sdk.auth.model

import android.os.Parcelable
import com.kakao.sdk.auth.TokenManageable
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import kotlinx.android.parcel.Parcelize
import java.util.*

/**
 * 카카오 로그인을 통해 발급 받은 토큰. Kakao SDK는 [TokenManageable] 인터페이스를 통해 토큰을 자동으로 관리함.
 *
 * @property accessToken API 인증에 사용하는 엑세스 토큰.
 * @property accessTokenExpiresAt 엑세스 토큰 만료 시각.
 * @property refreshToken 엑세스 토큰을 갱신하는데 사용하는 리프레시 토큰.
 * @property refreshTokenExpiresAt 리프레시 토큰 만료 시각.
 * @property scopes 이 토큰에 부여된 scope 목록.
 */
@Parcelize
data class OAuthToken(
    val accessToken: String,
    val accessTokenExpiresAt: Date,
    val refreshToken: String,
    val refreshTokenExpiresAt: Date,
    val scopes: List<String>? = null
) : Parcelable {

    companion object {
        /**
         * @suppress
         *
         * [AccessTokenResponse] 객체로부터 OAuthToken 객체 생성.
         */
        fun fromResponse(response: AccessTokenResponse, oldToken: OAuthToken? = null): OAuthToken =
            OAuthToken(
                accessToken = response.accessToken,
                accessTokenExpiresAt = Date(Date().time + 1000L * response.accessTokenExpiresIn),
                refreshToken =
                response.refreshToken
                    ?: oldToken?.refreshToken
                    ?: throw ClientError(
                        ClientErrorCause.TokenNotFound,
                        "Refresh token not found in the response."
                    ),
                refreshTokenExpiresAt =
                if (response.refreshToken != null)  // 체크하지 않으면, 새 토큰이 있는데 새 만료 시각이 없거나 못가져 온 경우, 앞에서는 새 토큰을 넣었는데 만료시각은 이전 값을 넣게 되어 싱크가 깨진다.
                    response.refreshTokenExpiresIn?.let { Date(Date().time + 1000L * it) } ?: Date()
                else
                    oldToken?.refreshTokenExpiresAt!!,
                scopes = response.scope?.split(" ") ?: oldToken?.scopes
            )
    }
}