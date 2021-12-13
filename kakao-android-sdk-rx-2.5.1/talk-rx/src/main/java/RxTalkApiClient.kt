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
@file:JvmName("TalkApiClientKt")

package com.kakao.sdk.talk

import com.kakao.sdk.auth.network.RxAuthOperations
import com.kakao.sdk.auth.network.rxKapiWithOAuth
import com.kakao.sdk.common.util.KakaoJson
import com.kakao.sdk.network.ApiFactory
import com.kakao.sdk.network.RxOperations
import com.kakao.sdk.template.model.DefaultTemplate
import com.kakao.sdk.talk.model.*
import io.reactivex.Completable
import io.reactivex.Single

/**
 * 카카오톡 API 호출을 담당하는 클라이언트. (for ReactiveX)
 **/
class RxTalkApiClient(
    private val api: RxTalkApi = ApiFactory.rxKapiWithOAuth.create(RxTalkApi::class.java),
    val authOperations: RxAuthOperations = RxAuthOperations.instance
) {

    /**
     * 카카오톡 프로필 가져오기.
     */
    @JvmOverloads
    fun profile(): Single<TalkProfile> =
        api.profile()
            .compose(RxOperations.handleApiError())
            .compose(authOperations.handleApiError())

    /**
     * 카카오톡 친구 목록 가져오기.
     */
    @JvmOverloads
    fun friends(
        offset: Int? = null,
        limit: Int? = null,
        order: Order? = null,
        friendOrder: FriendOrder? = null
    ): Single<Friends<Friend>> = api.friends(offset, limit, order, friendOrder)
        .compose(RxOperations.handleApiError())
        .compose(authOperations.handleApiError())

    /**
     * 카카오 디벨로퍼스에서 생성한 서비스만의 커스텀 메시지 템플릿을 사용하여, 카카오톡의 나와의 채팅방으로 메시지 전송.
     *
     * 템플릿을 생성하는 방법은 [메시지 템플릿 가이드](https://developers.kakao.com/docs/latest/ko/message/message-template) 참고.
     */
    @JvmOverloads
    fun sendCustomMemo(templateId: Long, templateArgs: Map<String, String>? = null): Completable =
        api.sendCustomMemo(templateId, templateArgs)
            .compose(RxOperations.handleCompletableError())
            .compose(authOperations.handleCompletableError())

    /**
     * 기본 템플릿을 이용하여, 카카오톡의 나와의 채팅방으로 메시지 전송.
     */
    fun sendDefaultMemo(template: DefaultTemplate): Completable =
        api.sendDefaultMemo(template)
            .compose(RxOperations.handleCompletableError())
            .compose(authOperations.handleCompletableError())

    /**
     * 지정된 URL 을 스크랩하여, 카카오톡의 나와의 채팅방으로 메시지 전송.
     */
    @JvmOverloads
    fun sendScrapMemo(
        requestUrl: String,
        templateId: Long? = null,
        templateArgs: Map<String, String>? = null
    ): Completable =
        api.sendScrapMemo(requestUrl, templateId, templateArgs)
            .compose(RxOperations.handleCompletableError())
            .compose(authOperations.handleCompletableError())

    /**
     * 카카오 디벨로퍼스에서 생성한 서비스만의 커스텀 메시지 템플릿을 사용하여, 조회한 친구를 대상으로 카카오톡으로 메시지 전송.
     *
     * 템플릿을 생성하는 방법은 [메시지 템플릿 가이드](https://developers.kakao.com/docs/latest/ko/message/message-template) 참고.
     */
    @JvmOverloads
    fun sendCustomMessage(
        receiverUuids: List<String>,
        templateId: Long,
        templateArgs: Map<String, String>? = null
    ): Single<MessageSendResult> =
        api.sendCustomMessage(KakaoJson.toJson(receiverUuids), templateId, templateArgs)
            .compose(RxOperations.handleApiError())
            .compose(authOperations.handleApiError())

    /**
     * 기본 템플릿을 사용하여, 조회한 친구를 대상으로 카카오톡으로 메시지 전송.
     */
    fun sendDefaultMessage(
        receiverUuids: List<String>,
        template: DefaultTemplate
    ): Single<MessageSendResult> =
        api.sendDefaultMessage(KakaoJson.toJson(receiverUuids), template)
            .compose(RxOperations.handleApiError())
            .compose(authOperations.handleApiError())

    /**
     * 지정된 URL을 스크랩하여, 조회한 친구를 대상으로 카카오톡으로 메시지 전송.
     *
     * 스크랩 커스텀 템플릿 가이드를 참고하여 템플릿을 직접 만들고 스크랩 메시지 전송에 이용 가능.
     */
    @JvmOverloads
    fun sendScrapMessage(
        receiverUuids: List<String>,
        requestUrl: String,
        templateId: Long? = null,
        templateArgs: Map<String, String>? = null
    ): Single<MessageSendResult> =
        api.sendScrapMessage(KakaoJson.toJson(receiverUuids), requestUrl, templateId, templateArgs)
            .compose(RxOperations.handleApiError())
            .compose(authOperations.handleApiError())

    /**
     * 사용자가 특정 카카오톡 채널을 추가했는지 확인.
     */
    @JvmOverloads
    fun channels(publicIds: List<String>? = null): Single<Channels> =
        api.channels(publicIds?.let { KakaoJson.toJson(it) })
            .compose(RxOperations.handleApiError())
            .compose(authOperations.handleApiError())

    companion object {
        @JvmStatic
        val instance by lazy { TalkApiClient.rx }
    }
}

/**
 * ReactiveX 를 위한 [TalkApiClient] singleton 객체
 */
val TalkApiClient.Companion.rx by lazy { RxTalkApiClient() }
