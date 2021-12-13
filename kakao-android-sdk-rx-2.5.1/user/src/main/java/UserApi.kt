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
package com.kakao.sdk.user

import com.kakao.sdk.common.json.IntDate
import com.kakao.sdk.user.model.*
import retrofit2.Call
import retrofit2.http.*
import java.util.*

/**
 * @suppress
 */
interface UserApi {
    @GET(Constants.V2_ME_PATH)
    fun me(
        @Query(Constants.SECURE_RESOURCE) secureResource: Boolean = true,
        @Query(Constants.PROPERTY_KEYS) properties: String? = null
    ): Call<User>

    @GET(Constants.V1_ACCESS_TOKEN_INFO_PATH)
    fun accessTokenInfo(): Call<AccessTokenInfo>

    @POST(Constants.V1_UPDATE_PROFILE_PATH)
    @FormUrlEncoded
    fun updateProfile(
        @Field(Constants.PROPERTIES) properties: Map<String, String>
    ): Call<Unit>

    @POST(Constants.V1_LOGOUT_PATH)
    fun logout(): Call<Unit>

    @POST(Constants.V1_UNLINK_PATH)
    fun unlink(): Call<Unit>

    @GET(Constants.V1_SHIPPING_ADDRESSES_PATH)
    fun shippingAddresses(
        @Query(Constants.ADDRESS_ID) addressId: Long? = null,
        @IntDate @Query(Constants.FROM_UPDATED_AT) fromUpdateAt: Date? = null,
        @Query(Constants.PAGE_SIZE) pageSize: Int? = null
    ): Call<UserShippingAddresses>

    @GET(Constants.V1_SERVICE_TERMS_PATH)
    fun serviceTerms(@Query(Constants.EXTRA) extra: String? = null): Call<UserServiceTerms>

    @POST(Constants.V1_SIGNUP_PATH)
    @FormUrlEncoded
    fun signup(
        @Field(Constants.PROPERTIES) properties: Map<String, String>? = null
    ): Call<Unit>

    @GET(Constants.V2_SCOPES)
    fun scopes(@Query(Constants.SCOPES) scopes: String? = null): Call<ScopeInfo>

    @POST(Constants.V2_REVOKE_SCOPE)
    @FormUrlEncoded
    fun revokeScopes(@Field(Constants.SCOPES) scopes: String): Call<ScopeInfo>
}