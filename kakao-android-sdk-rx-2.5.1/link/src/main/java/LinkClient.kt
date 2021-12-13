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

import android.content.Context
import com.kakao.sdk.link.model.ImageUploadResult
import com.kakao.sdk.link.model.LinkResult
import com.kakao.sdk.link.model.ValidationResult
import com.kakao.sdk.network.ApiCallback
import com.kakao.sdk.network.ApiFactory
import com.kakao.sdk.template.model.DefaultTemplate
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

/**
 * 카카오링크 호출을 담당하는 클라이언트.
 */
class LinkClient(
    private val linkApi: LinkApi = ApiFactory.kapi.create(LinkApi::class.java),
    /** @suppress */ val linkIntentClient: KakaoLinkIntentClient = KakaoLinkIntentClient.instance
) {

    fun isKakaoLinkAvailable(context: Context): Boolean {
        return linkIntentClient.isKakaoLinkAvailable(context)
    }

    /**
     * 카카오 디벨로퍼스에서 생성한 메시지 템플릿을 카카오톡으로 공유.
     * 템플릿을 생성하는 방법은 [메시지 템플릿 가이드](https://developers.kakao.com/docs/latest/ko/message/message-template) 참고.
     */
    @JvmOverloads
    fun customTemplate(
        context: Context,
        templateId: Long,
        templateArgs: Map<String, String>? = null,
        serverCallbackArgs: Map<String, String>? = null,
        callback: (linkResult: LinkResult?, error: Throwable?) -> Unit
    ) {
        linkApi.validateCustom(templateId, templateArgs)
            .enqueue(object: ApiCallback<ValidationResult>() {
                override fun onComplete(model: ValidationResult?, error: Throwable?) {
                    if (model != null) {
                        try {
                            callback(
                                linkIntentClient.linkResultFromResponse(context, model, serverCallbackArgs),
                                null
                            )
                        } catch (e: Throwable) {
                            callback(null, e)
                        }
                    } else {
                        callback(null, error)
                    }
                }
            })
    }

    /**
     * 기본 템플릿을 카카오톡으로 공유.
     */
    @JvmOverloads
    fun defaultTemplate(
        context: Context,
        defaultTemplate: DefaultTemplate,
        serverCallbackArgs: Map<String, String>? = null,
        callback: (linkResult: LinkResult?, error: Throwable?) -> Unit
    ) {
        linkApi.validateDefault(defaultTemplate)
            .enqueue(object: ApiCallback<ValidationResult>() {
                override fun onComplete(model: ValidationResult?, error: Throwable?) {
                    if (model != null) {
                        try {
                            callback(
                                linkIntentClient.linkResultFromResponse(context, model, serverCallbackArgs),
                                null
                            )
                        } catch (e: Throwable) {
                            callback(null, e)
                        }
                    } else {
                        callback(null, error)
                    }
                }
            })
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
        serverCallbackArgs: Map<String, String>? = null,
        callback: (linkResult: LinkResult?, error: Throwable?) -> Unit
    ) {
        linkApi.validateScrap(url, templateId, templateArgs)
            .enqueue(object: ApiCallback<ValidationResult>() {
                override fun onComplete(model: ValidationResult?, error: Throwable?) {
                    if (model != null) {
                        try {
                            callback(
                                linkIntentClient.linkResultFromResponse(context, model, serverCallbackArgs),
                                null
                            )
                        } catch (e: Throwable) {
                            callback(null, e)
                        }
                    } else {
                        callback(null, error)
                    }
                }
            })
    }

    /**
     * 카카오링크 컨텐츠 이미지로 활용하기 위해 로컬 이미지를 카카오 이미지 서버로 업로드.
     */
    @JvmOverloads
    fun uploadImage(
        image: File,
        secureResource: Boolean = true,
        callback: (imageUploadResult: ImageUploadResult?, error: Throwable?) -> Unit
    ) {
        linkApi.uploadImage(
            MultipartBody.Part.createFormData(
                "file",
                image.name,
                RequestBody.create(MediaType.parse("image/*"), image)
            ),
            secureResource
        )
            .enqueue(object: ApiCallback<ImageUploadResult>() {
                override fun onComplete(model: ImageUploadResult?, error: Throwable?) {
                    callback(model, error)
                }
            })
    }

    /**
     * 카카오링크 컨텐츠 이미지로 활용하기 위해 원격 이미지를 카카오 이미지 서버로 업로드.
     */
    @JvmOverloads
    fun scrapImage(
        imageUrl: String,
        secureResource: Boolean = true,
        callback: (imageUploadResult: ImageUploadResult?, error: Throwable?) -> Unit
    ) {
        linkApi.scrapImage(imageUrl, secureResource)
            .enqueue(object: ApiCallback<ImageUploadResult>() {
                override fun onComplete(model: ImageUploadResult?, error: Throwable?) {
                    callback(model, error)
                }
            })
    }

    companion object {
        /**
         * 간편한 API 호출을 위해 기본 제공되는 singleton 객체
         */
        @JvmStatic
        val instance by lazy { LinkClient() }
    }
}