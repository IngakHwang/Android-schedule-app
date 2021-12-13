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
package com.kakao.sdk.link

import com.kakao.sdk.link.model.ImageUploadResult
import com.kakao.sdk.link.model.ValidationResult
import com.kakao.sdk.template.model.DefaultTemplate
import io.reactivex.Single
import okhttp3.MultipartBody
import retrofit2.http.*

/**
 * @suppress
 */
interface RxLinkApi {
    @GET("${Constants.VALIDATE_CUSTOM_PATH}?link_ver=4.0")
    fun validateCustom(
        @Query(Constants.TEMPLATE_ID) templateId: Long,
        @Query(Constants.TEMPLATE_ARGS) templateArgs: Map<String, String>? = null
    ): Single<ValidationResult>

    @GET("${Constants.VALIDATE_DEFAULT_PATH}?link_ver=4.0")
    fun validateDefault(@Query(Constants.TEMPLATE_OBJECT) templateObject: DefaultTemplate): Single<ValidationResult>

    @GET("${Constants.VALIDATE_SCRAP_PATH}?link_ver=4.0")
    fun validateScrap(
        @Query(Constants.REQUEST_URL) url: String,
        @Query(Constants.TEMPLATE_ID) templateId: Long? = null,
        @Query(Constants.TEMPLATE_ARGS) templateArgs: Map<String, String>? = null
    ): Single<ValidationResult>

    @Multipart
    @POST(Constants.UPLOAD_IMAGE_PATH)
    fun uploadImage(
        @Part image: MultipartBody.Part,
        @Part("secure_resource") secureResource: Boolean? = true
    ): Single<ImageUploadResult>


    @POST(Constants.SCRAP_IMAGE_PATH)
    @FormUrlEncoded
    fun scrapImage(
        @Field("image_url") imageUrl: String,
        @Field("secure_resource") secureResource: Boolean? = true
    ): Single<ImageUploadResult>
}