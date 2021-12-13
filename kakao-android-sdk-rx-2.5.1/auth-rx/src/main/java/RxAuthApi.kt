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
import io.reactivex.Single
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 * @suppress
 */
interface RxAuthApi {
    @POST(Constants.TOKEN_PATH)
    @FormUrlEncoded
    fun issueAccessToken(
        @Field(Constants.CLIENT_ID) clientId: String,
        @Field(Constants.ANDROID_KEY_HASH) androidKeyHash: String,
        @Field(Constants.CODE) code: String,
        @Field(Constants.REDIRECT_URI) redirectUri: String,
        @Field(Constants.CODE_VERIFIER) codeVerifier: String? = null,
        @Field(Constants.APPROVAL_TYPE) approvalType: String? = null,
        @Field(Constants.GRANT_TYPE) grantType: String = Constants.AUTHORIZATION_CODE   // TODO: 값 fix
    ): Single<AccessTokenResponse>

    @POST(Constants.TOKEN_PATH)
    @FormUrlEncoded
    fun refreshAccessToken(
        @Field(Constants.CLIENT_ID) clientId: String,
        @Field(Constants.ANDROID_KEY_HASH) androidKeyHash: String,
        @Field(Constants.REFRESH_TOKEN) refreshToken: String,
        @Field(Constants.APPROVAL_TYPE) approvalType: String? = null,
        @Field(Constants.GRANT_TYPE) grantType: String = Constants.REFRESH_TOKEN    // TODO: 값 fix
    ): Single<AccessTokenResponse>

    @POST(Constants.AGT_PATH)
    @FormUrlEncoded
    fun agt(
        @Field(Constants.CLIENT_ID) clientId: String,
        @Field(Constants.ACCESS_TOKEN) accessToken: String
    ): Single<AgtResponse>
}