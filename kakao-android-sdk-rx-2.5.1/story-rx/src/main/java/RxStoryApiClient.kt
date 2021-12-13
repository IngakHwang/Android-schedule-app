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
@file:JvmName("StoryApiClientKt")

package com.kakao.sdk.story

import com.kakao.sdk.auth.network.RxAuthOperations
import com.kakao.sdk.auth.network.rxKapiWithOAuth
import com.kakao.sdk.common.util.KakaoJson
import com.kakao.sdk.network.ApiFactory
import com.kakao.sdk.network.RxOperations
import com.kakao.sdk.story.model.*
import io.reactivex.Completable
import io.reactivex.Single
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

/**
 * 카카오스토리 API 호출을 담당하는 클라이언트. (for ReactiveX)
 */
class RxStoryApiClient(
    private val api: RxStoryApi = ApiFactory.rxKapiWithOAuth.create(RxStoryApi::class.java),
    private val authOperations: RxAuthOperations = RxAuthOperations.instance
) {
    /**
     * 카카오스토리 사용자인지 확인하기.
     */
    fun isStoryUser(): Single<Boolean> {
        return api.isStoryUser()
            .map { it.isStoryUser }
            .compose(RxOperations.handleApiError())
            .compose(authOperations.handleApiError())
    }

    /**
     * 카카오스토리 프로필 가져오기.
     */
    @JvmOverloads
    fun profile(secureResource: Boolean? = true): Single<StoryProfile> {
        return api.profile(secureResource)
            .compose(RxOperations.handleApiError())
            .compose(authOperations.handleApiError())
    }

    /**
     * 카카오스토리의 특정 내 스토리 가져오기. comments, likes 등 각종 상세정보 포함.
     */
    fun story(id: String): Single<Story> {
        return api.story(id)
            .compose(RxOperations.handleApiError())
            .compose(authOperations.handleApiError())
    }

    /**
     * 카카오스토리의 내 스토리 여러 개 가져오기.
     * 단, comments, likes 등의 상세정보는 없으며 이는 내스토리 정보 요청 [story] 통해 획득 가능.
     */
    @JvmOverloads
    fun stories(lastId: String? = null): Single<List<Story>> {
        return api.stories(lastId)
            .compose(RxOperations.handleApiError())
            .compose(authOperations.handleApiError())
    }

    /**
     * 카카오스토리에 글 스토리 쓰기.
     */
    @JvmOverloads
    fun postNote(
        content: String,
        permission: Story.Permission = Story.Permission.PUBLIC,
        enableShare: Boolean = true,
        androidExecutionParams: Map<String, String>? = null,
        iosExecutionParams: Map<String, String>? = null,
        androidMarketParams: Map<String, String>? = null,
        iosMarketParams: Map<String, String>? = null
    ): Single<String> {
        return api.postNote(
            content, permission, enableShare, androidExecutionParams, iosExecutionParams, androidMarketParams,
            iosMarketParams
        ).map { it.id }
            .compose(RxOperations.handleApiError())
            .compose(authOperations.handleApiError())
    }

    /**
     * 카카오스토리에 링크 스토리 쓰기
     *
     * 먼저 포스팅하고자 하는 URL로 [linkInfo]를 호출하고 반환된 링크 정보를 파라미터로 사용.
     */
    @JvmOverloads
    fun postLink(
        linkInfo: LinkInfo,
        content: String,
        permission: Story.Permission = Story.Permission.PUBLIC,
        enableShare: Boolean = true,
        androidExecutionParams: Map<String, String>? = null,
        iosExecutionParams: Map<String, String>? = null,
        androidMarketParams: Map<String, String>? = null,
        iosMarketParams: Map<String, String>? = null
    ): Single<String> {

        return api.postLink(
            linkInfo, content, permission, enableShare, androidExecutionParams, iosExecutionParams,
            androidMarketParams, iosMarketParams
        ).map { it.id }
            .compose(RxOperations.handleApiError())
            .compose(authOperations.handleApiError())
    }

    /**
     * 카카오스토리에 사진 스토리 쓰기.
     *
     * 먼저 올리고자 하는 사진 파일을 [upload]로 카카오 서버에 업로드하고 반환되는 path 목록을 파라미터로 사용.
     */
    @JvmOverloads
    fun postPhoto(
        images: List<String>,
        content: String,
        permission: Story.Permission = Story.Permission.PUBLIC,
        enableShare: Boolean = true,
        androidExecutionParams: Map<String, String>? = null,
        iosExecutionParams: Map<String, String>? = null,
        androidMarketParams: Map<String, String>? = null,
        iosMarketParams: Map<String, String>? = null
    ): Single<String> {
        return api.postPhoto(
            KakaoJson.toJson(images), content, permission, enableShare,
            androidExecutionParams, iosExecutionParams, androidMarketParams, iosMarketParams
        ).map { it.id }
            .compose(RxOperations.handleApiError())
            .compose(authOperations.handleApiError())
    }

    /**
     * 카카오스토리의 특정 내 스토리 삭제.
     */
    fun delete(id: String): Completable {
        return api.delete(id)
            .compose(RxOperations.handleCompletableError())
            .compose(authOperations.handleCompletableError())
    }

    /**
     * 포스팅하고자 하는 URL 을 스크랩하여 링크 정보 생성
     */
    fun linkInfo(url: String): Single<LinkInfo> {
        return api.linkInfo(url)
            .compose(RxOperations.handleApiError())
            .compose(authOperations.handleApiError())
    }

    /**
     * 로컬 이미지 파일 여러장을 카카오스토리에 업로드
     */
    fun upload(images: List<File>): Single<List<String>> {
        return Single.just(
            images.map { Pair(it.name, RequestBody.create(MediaType.parse("image/*"), it)) }
                .mapIndexed { index, pair ->
                    MultipartBody.Part.createFormData(
                        "${Constants.FILE}_$index",
                        pair.first,
                        pair.second
                    )
                })
            .flatMap { api.upload(it) }
            .compose(RxOperations.handleApiError())
            .compose(authOperations.handleApiError())
    }

    companion object {
        @JvmStatic
        val instance by lazy { StoryApiClient.rx }
    }
}

/**
 * ReactiveX 를 위한 [StoryApiClient] singleton 객체
 */
val StoryApiClient.Companion.rx by lazy { RxStoryApiClient() }