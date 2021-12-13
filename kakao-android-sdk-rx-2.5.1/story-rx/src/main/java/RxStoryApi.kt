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
package com.kakao.sdk.story

import com.kakao.sdk.common.json.MapToQuery
import com.kakao.sdk.story.model.*
import io.reactivex.Completable
import io.reactivex.Single
import okhttp3.MultipartBody
import retrofit2.http.*

/**
 * @suppress
 */
interface RxStoryApi {
    @GET(Constants.IS_STORY_USER_PATH)
    fun isStoryUser(): Single<StoryUserResult>

    @GET(Constants.STORY_PROFILE_PATH)
    fun profile(@Query(Constants.SECURE_RESOURCE) secureResource: Boolean? = true): Single<StoryProfile>

    @GET(Constants.GET_STORY_PATH)
    fun story(@Query(Constants.ID) id: String): Single<Story>

    @GET(Constants.GET_STORIES_PATH)
    fun stories(@Query(Constants.LAST_ID) lastId: String? = null): Single<List<Story>>

    @FormUrlEncoded
    @POST(Constants.POST_NOTE_PATH)
    fun postNote(
        @Field(Constants.CONTENT) content: String,
        @Field(Constants.PERMISSION) permission: Story.Permission = Story.Permission.PUBLIC,
        @Field(Constants.ENABLE_SHARE) enableShare: Boolean = true,
        @MapToQuery @Field(Constants.ANDROID_EXEC_PARAM) androidExecutionParams: Map<String, String>? = null,
        @MapToQuery @Field(Constants.IOS_EXEC_PARAM) iosExecutionParams: Map<String, String>? = null,
        @MapToQuery @Field(Constants.ANDROID_MARKET_PARAM) androidMarketParams: Map<String, String>? = null,
        @MapToQuery @Field(Constants.IOS_MARKET_PARAM) iosMarketParams: Map<String, String>? = null
    ): Single<StoryPostResult>

    @FormUrlEncoded
    @POST(Constants.POST_PHOTO_PATH)
    fun postPhoto(
        @Field(Constants.IMAGE_URL_LIST) images: String,
        @Field(Constants.CONTENT) content: String,
        @Field(Constants.PERMISSION) permission: Story.Permission = Story.Permission.PUBLIC,
        @Field(Constants.ENABLE_SHARE) enableShare: Boolean = true,
        @MapToQuery @Field(Constants.ANDROID_EXEC_PARAM) androidExecutionParams: Map<String, String>? = null,
        @MapToQuery @Field(Constants.IOS_EXEC_PARAM) iosExecutionParams: Map<String, String>? = null,
        @MapToQuery @Field(Constants.ANDROID_MARKET_PARAM) androidMarketParams: Map<String, String>? = null,
        @MapToQuery @Field(Constants.IOS_MARKET_PARAM) iosMarketParams: Map<String, String>? = null
    ): Single<StoryPostResult>

    @FormUrlEncoded
    @POST(Constants.POST_LINK_PATH)
    fun postLink(
        @Field(Constants.LINK_INFO) linkInfo: LinkInfo,
        @Field(Constants.CONTENT) content: String,
        @Field(Constants.PERMISSION) permission: Story.Permission = Story.Permission.PUBLIC,
        @Field(Constants.ENABLE_SHARE) enableShare: Boolean = true,
        @MapToQuery @Field(Constants.ANDROID_EXEC_PARAM) androidExecutionParams: Map<String, String>? = null,
        @MapToQuery @Field(Constants.IOS_EXEC_PARAM) iosExecutionParams: Map<String, String>? = null,
        @MapToQuery @Field(Constants.ANDROID_MARKET_PARAM) androidMarketParams: Map<String, String>? = null,
        @MapToQuery @Field(Constants.IOS_MARKET_PARAM) iosMarketParams: Map<String, String>? = null
    ): Single<StoryPostResult>

    @DELETE(Constants.DELETE_STORY_PATH)
    fun delete(@Query(Constants.ID) id: String): Completable

    @GET(Constants.SCRAP_LINK_PATH)
    fun linkInfo(@Query(Constants.URL) url: String): Single<LinkInfo>

    @Multipart
    @POST(Constants.SCRAP_IMAGES_PATH)
    fun upload(@Part images: List<MultipartBody.Part>): Single<List<String>>
}