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
package com.kakao.sdk.talk

import com.kakao.sdk.talk.model.*
import com.kakao.sdk.template.model.DefaultTemplate
import retrofit2.Call
import retrofit2.http.*

/**
 * @suppress
 */
interface TalkApi {
    @GET(Constants.PROFILE_PATH)
    fun profile(): Call<TalkProfile>

    @GET(Constants.V1_FRIENDS_PATH)
    fun friends(
        @Query(Constants.OFFSET) offset: Int? = null,
        @Query(Constants.LIMIT) limit: Int? = null,
        @Query(Constants.ORDER) order: Order? = null,
        @Query(Constants.FRIEND_ORDER) friendOrder: FriendOrder? = null
    ): Call<Friends<Friend>>

    @FormUrlEncoded
    @POST(Constants.V2_MEMO_PATH)
    fun sendCustomMemo(
        @Field(Constants.TEMPLATE_ID) templateId: Long,
        @Field(Constants.TEMPLATE_ARGS) templateArgs: Map<String, String>? = null
    ): Call<Unit>

    @FormUrlEncoded
    @POST(Constants.V2_MEMO_DEFAULT_PATH)
    fun sendDefaultMemo(@Field(Constants.TEMPLATE_OBJECT) template: DefaultTemplate): Call<Unit>

    @FormUrlEncoded
    @POST(Constants.V2_MEMO_SCRAP_PATH)
    fun sendScrapMemo(
        @Field(Constants.REQUEST_URL) requestUrl: String,
        @Field(Constants.TEMPLATE_ID) templateId: Long? = null,
        @Field(Constants.TEMPLATE_ARGS) templateArgs: Map<String, String>? = null
    ): Call<Unit>

    @FormUrlEncoded
    @POST(Constants.V1_OPEN_TALK_MESSAGE_CUSTOM_PATH)
    fun sendCustomMessage(
        @Field(Constants.RECEIVER_UUIDS) receiverUuids: String,
        @Field(Constants.TEMPLATE_ID) templateId: Long,
        @Field(Constants.TEMPLATE_ARGS) templateArgs: Map<String, String>? = null
    ): Call<MessageSendResult>

    @FormUrlEncoded
    @POST(Constants.V1_OPEN_TALK_MESSAGE_DEFAULT_PATH)
    fun sendDefaultMessage(
        @Field(Constants.RECEIVER_UUIDS) receiverUuids: String,
        @Field(Constants.TEMPLATE_OBJECT) template: DefaultTemplate
    ): Call<MessageSendResult>

    @FormUrlEncoded
    @POST(Constants.V1_OPEN_TALK_MESSAGE_SCRAP_PATH)
    fun sendScrapMessage(
        @Field(Constants.RECEIVER_UUIDS) receiverUuids: String,
        @Field(Constants.REQUEST_URL) requestUrl: String,
        @Field(Constants.TEMPLATE_ID) templateId: Long?,
        @Field(Constants.TEMPLATE_ARGS) templateArgs: Map<String, String>? = null
    ): Call<MessageSendResult>

    @GET(Constants.V1_CHANNELS_PATH)
    fun channels(@Query(Constants.CHANNEL_PUBLIC_IDS) publicIds: String? = null): Call<Channels>
}