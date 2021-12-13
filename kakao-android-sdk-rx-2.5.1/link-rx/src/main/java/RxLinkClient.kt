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
@file:JvmName("LinkClientKt")

package com.kakao.sdk.link

import android.content.Context
import com.kakao.sdk.link.model.ImageUploadResult
import com.kakao.sdk.link.model.LinkResult
import com.kakao.sdk.network.ApiFactory
import com.kakao.sdk.network.RxOperations
import com.kakao.sdk.network.rxKapi
import com.kakao.sdk.template.model.DefaultTemplate
import io.reactivex.Single
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

/**
 * 카카오링크 호출을 담당하는 클라이언트. (for ReactiveX)
 */
class RxLinkClient(
    private val api: RxLinkApi = ApiFactory.rxKapi.create(RxLinkApi::class.java),
    /** @suppress */ val linkIntentClient: KakaoLinkIntentClient = KakaoLinkIntentClient.instance
) {

    /**
     * 카카오 디벨로퍼스에서 생성한 메시지 템플릿을 카카오톡으로 공유.
     * 템플릿을 생성하는 방법은 [메시지 템플릿 가이드](https://developers.kakao.com/docs/latest/ko/message/message-template) 참고.
     */
    @JvmOverloads
    fun customTemplate(
        context: Context,
        templateId: Long,
        templateArgs: Map<String, String>? = null,
        serverCallbackArgs: Map<String, String>? = null
    ): Single<LinkResult> {
        return api.validateCustom(templateId, templateArgs)
            .compose(RxOperations.handleApiError())
            .map { linkIntentClient.linkResultFromResponse(context, it, serverCallbackArgs) }
    }

    /**
     * 기본 템플릿을 카카오톡으로 공유.
     */
    @JvmOverloads
    fun defaultTemplate(
        context: Context,
        defaultTemplate: DefaultTemplate,
        serverCallbackArgs: Map<String, String>? = null
    ): Single<LinkResult> {
        return api.validateDefault(defaultTemplate)
            .compose(RxOperations.handleApiError())
            .map { linkIntentClient.linkResultFromResponse(context, it, serverCallbackArgs) }
    }

    /**
     * 지정된 URL 을 스크랩하여 만들어진 템플릿을 카카오톡으로 공유.
     */
    @JvmOverloads
    fun scrapTemplate(
        context: Context,
        url: String,
        templateId: Long? = null,
        templateArgs: Map<String, String>? = null,
        serverCallbackArgs: Map<String, String>? = null
    ): Single<LinkResult> {
        return api.validateScrap(url, templateId, templateArgs)
            .compose(RxOperations.handleApiError())
            .map { linkIntentClient.linkResultFromResponse(context, it, serverCallbackArgs) }
    }

    /**
     * 카카오링크 컨텐츠 이미지로 활용하기 위해 로컬 이미지를 카카오 이미지 서버로 업로드.
     */
    @JvmOverloads
    fun uploadImage(image: File, secureResource: Boolean = true): Single<ImageUploadResult> =
        Single.just(image).map { Pair(it.name, RequestBody.create(MediaType.parse("image/*"), it)) }
            .map { MultipartBody.Part.createFormData("file", it.first, it.second) }
            .flatMap { api.uploadImage(image = it, secureResource = secureResource) }
            .compose(RxOperations.handleApiError())

    /**
     * 카카오링크 컨텐츠 이미지로 활용하기 위해 원격 이미지를 카카오 이미지 서버로 업로드.
     */
    @JvmOverloads
    fun scrapImage(imageUrl: String, secureResource: Boolean = true): Single<ImageUploadResult> =
        api.scrapImage(imageUrl, secureResource)
            .compose(RxOperations.handleApiError())

    companion object {
        @JvmStatic
        val instance by lazy { RxLinkClient() }
    }
}

/**
 * ReactiveX 를 위한 [LinkClient] singleton 객체
 */
val LinkClient.Companion.rx by lazy { RxLinkClient() }