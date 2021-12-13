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
package com.kakao.sdk.talk

import android.net.Uri
import com.kakao.sdk.auth.network.kapiWithOAuth
import com.kakao.sdk.common.util.KakaoJson
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.common.model.ApplicationInfo
import com.kakao.sdk.common.model.ContextInfo
import com.kakao.sdk.network.ApiCallback
import com.kakao.sdk.network.ApiFactory
import com.kakao.sdk.talk.model.*
import com.kakao.sdk.template.model.DefaultTemplate

/**
 * 카카오톡 API 호출을 담당하는 클라이언트.
 */
class TalkApiClient(
    private val talkApi: TalkApi = ApiFactory.kapiWithOAuth.create(TalkApi::class.java),
    private val applicationInfo: ApplicationInfo = KakaoSdk.applicationContextInfo,
    private val contextInfo: ContextInfo = KakaoSdk.applicationContextInfo
) {

    /**
     * 카카오톡 프로필 가져오기.
     */
    @JvmOverloads
    fun profile(
        callback: (profile: TalkProfile?, error: Throwable?) -> Unit
    ) {
        talkApi.profile()
            .enqueue(object: ApiCallback<TalkProfile>() {
                override fun onComplete(model: TalkProfile?, error: Throwable?) {
                    callback(model, error)
                }
            })
    }

    /**
     * 카카오톡 친구 목록 가져오기.
     */
    @JvmOverloads
    fun friends(
        offset: Int? = null,
        limit: Int? = null,
        order: Order? = null,
        friendOrder: FriendOrder? = null,
        callback: (friends: Friends<Friend>?, error: Throwable?) -> Unit
    ) {
        talkApi.friends(offset, limit, order, friendOrder)
            .enqueue(object: ApiCallback<Friends<Friend>>() {
                override fun onComplete(model: Friends<Friend>?, error: Throwable?) {
                    callback(model, error)
                }
            })
    }

    /**
     * 카카오톡 친구 목록 가져오기.
     */
    @JvmOverloads
    fun friends(
            context: FriendsContext?,
            callback: (friends: Friends<Friend>?, error: Throwable?) -> Unit
    ) {
        talkApi.friends(context?.offset, context?.limit, context?.order, context?.friendOrder)
                .enqueue(object: ApiCallback<Friends<Friend>>() {
                    override fun onComplete(model: Friends<Friend>?, error: Throwable?) {
                        callback(model, error)
                    }
                })
    }

    /**
     * 카카오 디벨로퍼스에서 생성한 서비스만의 커스텀 메시지 템플릿을 사용하여, 카카오톡의 나와의 채팅방으로 메시지 전송.
     *
     * 템플릿을 생성하는 방법은 [메시지 템플릿 가이드](https://developers.kakao.com/docs/latest/ko/message/message-template) 참고.
     */
    @JvmOverloads
    fun sendCustomMemo(
        templateId: Long,
        templateArgs: Map<String, String>? = null,
        callback: (error: Throwable?) -> Unit
    ) {
        talkApi.sendCustomMemo(templateId, templateArgs)
            .enqueue(object: ApiCallback<Unit>() {
                override fun onComplete(model: Unit?, error: Throwable?) {
                    callback(error)
                }
            })
    }

    /**
     * 기본 템플릿을 이용하여, 카카오톡의 나와의 채팅방으로 메시지 전송.
     */
    fun sendDefaultMemo(
        template: DefaultTemplate,
        callback: (error: Throwable?) -> Unit
    ) {
        talkApi.sendDefaultMemo(template)
            .enqueue(object: ApiCallback<Unit>() {
                override fun onComplete(model: Unit?, error: Throwable?) {
                    callback(error)
                }
            })
    }

    /**
     * 지정된 URL 을 스크랩하여, 카카오톡의 나와의 채팅방으로 메시지 전송.
     */
    @JvmOverloads
    fun sendScrapMemo(
        requestUrl: String,
        templateId: Long? = null,
        templateArgs: Map<String, String>? = null,
        callback: (error: Throwable?) -> Unit
    ) {
        talkApi.sendScrapMemo(requestUrl, templateId, templateArgs)
            .enqueue(object: ApiCallback<Unit>() {
                override fun onComplete(model: Unit?, error: Throwable?) {
                    callback(error)
                }
            })
    }

    /**
     * 카카오 디벨로퍼스에서 생성한 서비스만의 커스텀 메시지 템플릿을 사용하여, 조회한 친구를 대상으로 카카오톡으로 메시지 전송.
     *
     * 템플릿을 생성하는 방법은 [메시지 템플릿 가이드](https://developers.kakao.com/docs/latest/ko/message/message-template) 참고.
     */
    @JvmOverloads
    fun sendCustomMessage(
        receiverUuids: List<String>,
        templateId: Long,
        templateArgs: Map<String, String>? = null,
        callback: (result: MessageSendResult?, error: Throwable?) -> Unit
    ) {
        talkApi.sendCustomMessage(KakaoJson.toJson(receiverUuids), templateId, templateArgs)
            .enqueue(object: ApiCallback<MessageSendResult>() {
                override fun onComplete(model: MessageSendResult?, error: Throwable?) {
                    callback(model, error)
                }
            })
    }

    /**
     * 기본 템플릿을 사용하여, 조회한 친구를 대상으로 카카오톡으로 메시지 전송.
     */
    fun sendDefaultMessage(
        receiverUuids: List<String>,
        template: DefaultTemplate,
        callback: (result: MessageSendResult?, error: Throwable?) -> Unit
    ) {
        talkApi.sendDefaultMessage(KakaoJson.toJson(receiverUuids), template)
            .enqueue(object: ApiCallback<MessageSendResult>() {
                override fun onComplete(model: MessageSendResult?, error: Throwable?) {
                    callback(model, error)
                }
            })
    }

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
        templateArgs: Map<String, String>? = null,
        callback: (result: MessageSendResult?, error: Throwable?) -> Unit
    ) {
        talkApi.sendScrapMessage(KakaoJson.toJson(receiverUuids), requestUrl, templateId, templateArgs)
            .enqueue(object: ApiCallback<MessageSendResult>() {
                override fun onComplete(model: MessageSendResult?, error: Throwable?) {
                    callback(model, error)
                }
            })
    }

    /**
     * 사용자가 특정 카카오톡 채널을 추가했는지 확인.
     */
    @JvmOverloads
    fun channels(
        publicIds: List<String>? = null,
        callback: (relations: Channels?, error: Throwable?) -> Unit
    ) {
        talkApi.channels(publicIds?.let { KakaoJson.toJson(it) })
            .enqueue(object: ApiCallback<Channels>() {
                override fun onComplete(model: Channels?, error: Throwable?) {
                    callback(model, error)
                }
            })
    }

    /**
     * 카카오톡 채널을 추가하기 위한 URL 반환. URL 을 브라우저나 웹뷰에서 로드하면 브릿지 웹페이지를 통해 카카오톡 실행.
     *
     * channelPublicId: 카카오톡 채널 홈 URL 에 들어간 {_영문}으로 구성된 고유 아이디.
     * 홈 URL 은 카카오톡 채널 관리자센터 > 관리 > 상세설정 페이지에서 확인.
     *
     * @see com.kakao.sdk.common.util.KakaoCustomTabsClient
     */
    fun addChannelUrl(channelPublicId: String): Uri {
        return baseUri(appKey = applicationInfo.appKey, kaHeader = contextInfo.kaHeader)
            .path("$channelPublicId/${Constants.FRIEND}").build()
    }

    /**
     * 카카오톡 채널 1:1 대화방 실행을 위한 URL 반환. URL 을 브라우저나 웹뷰에서 로드하면 브릿지 웹페이지를 통해 카카오톡 실행.
     *
     * channelPublicId: 카카오톡 채널 홈 URL 에 들어간 {_영문}으로 구성된 고유 아이디.
     * 홈 URL 은 카카오톡 채널 관리자센터 > 관리 > 상세설정 페이지에서 확인.
     *
     * @see com.kakao.sdk.common.util.KakaoCustomTabsClient
     */
    fun channelChatUrl(channelPublicId: String): Uri {
        return baseUri(appKey = applicationInfo.appKey, kaHeader = contextInfo.kaHeader)
            .path("$channelPublicId/${Constants.CHAT}").build()
    }

    private fun baseUri(appKey: String, kaHeader: String): Uri.Builder {
        return Uri.Builder().scheme(com.kakao.sdk.common.Constants.SCHEME)
            .authority(KakaoSdk.hosts.channel)
            .appendQueryParameter(com.kakao.sdk.common.Constants.APP_KEY, appKey)
            .appendQueryParameter(Constants.KAKAO_AGENT, kaHeader)
            .appendQueryParameter(Constants.API_VER, Constants.API_VER_10)
    }

    companion object {
        /**
         * 간편한 API 호출을 위해 기본 제공되는 singleton 객체
         */
        @JvmStatic
        val instance by lazy { TalkApiClient() }
    }
}