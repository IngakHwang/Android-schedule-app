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
@file:JvmName("UserApiClientKt")

package com.kakao.sdk.user

import android.content.Context
import com.kakao.sdk.auth.AuthApiClient
import com.kakao.sdk.auth.AuthCodeClient
import com.kakao.sdk.auth.TokenManagerProvider
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.auth.model.Prompt
import com.kakao.sdk.auth.network.RxAuthOperations
import com.kakao.sdk.auth.network.rxKapiWithOAuth
import com.kakao.sdk.auth.rx
import com.kakao.sdk.common.util.KakaoJson
import com.kakao.sdk.network.ApiFactory
import com.kakao.sdk.network.RxOperations
import com.kakao.sdk.user.model.*
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import java.util.*

/**
 * 사용자관리 API 호출을 담당하는 클라이언트. (for ReactiveX)
 */
class RxUserApiClient(
    private val userApi: RxUserApi = ApiFactory.rxKapiWithOAuth.create(RxUserApi::class.java),
    val authOperations: RxAuthOperations = RxAuthOperations.instance,
    private val tokenManagerProvider: TokenManagerProvider = TokenManagerProvider.instance
) {

    /**
     * 카카오톡으로 로그인. 카카오톡에 연결된 카카오계정으로 사용자를 인증하고 [OAuthToken] 발급.
     *
     * 발급된 토큰은 [TokenManagerProvider]에 지정된 토큰 저장소에 자동으로 저장됨.
     *
     * @param context 카카오톡 로그인 Activity를 실행하기 위한 현재 Activity context
     *
     * @return [OAuthToken]을 방출하는 [Single] 반환.
     */
    @JvmOverloads
    fun loginWithKakaoTalk(
        context: Context,
        requestCode: Int = AuthCodeClient.DEFAULT_REQUEST_CODE,
        channelPublicIds: List<String>? = null,
        serviceTerms: List<String>? = null
    ): Single<OAuthToken> {
        val codeVerifier = AuthCodeClient.codeVerifier()
        return AuthCodeClient.rx.authorizeWithKakaoTalk(
            context,
            requestCode = requestCode,
            channelPublicIds = channelPublicIds,
            serviceTerms = serviceTerms,
            codeVerifier = codeVerifier
        )
            .observeOn(Schedulers.io())
            .flatMap { AuthApiClient.rx.issueAccessToken(it, codeVerifier) }
    }

    /**
     * 카카오계정으로 로그인. 기본 웹 브라우저(CustomTabs)에 있는 카카오계정 cookie 로 사용자를 인증하고 [OAuthToken] 발급.
     *
     * 발급된 토큰은 [TokenManagerProvider]에 지정된 토큰 저장소에 자동으로 저장됨.
     *
     * @param context CustomTabs를 실행하기 위한 현재 Activity context
     * @param prompts 동의 화면 요청 시 추가 상호작용을 요청하고자 할 때 전달. [Prompt]
     *
     * @return [OAuthToken]을 방출하는 [Single] 반환.
     */
    @JvmOverloads
    fun loginWithKakaoAccount(
        context: Context,
        prompts: List<Prompt>? = null,
        channelPublicIds: List<String>? = null,
        serviceTerms: List<String>? = null
    ): Single<OAuthToken> {
        val codeVerifier = AuthCodeClient.codeVerifier()
        return AuthCodeClient.rx.authorizeWithKakaoAccount(
            context,
            prompts = prompts,
            channelPublicIds = channelPublicIds,
            serviceTerms = serviceTerms,
            codeVerifier = codeVerifier
        )
            .observeOn(Schedulers.io())
            .flatMap { AuthApiClient.rx.issueAccessToken(it, codeVerifier) }
    }

    /**
     * 사용자가 아직 동의하지 않은 개인정보 및 접근권한 동의 항목에 대하여 동의를 요청하는 동의 화면을 출력하고, 사용자 동의 시 동의항목이 업데이트 된 [OAuthToken] 발급.
     *
     * 발급된 토큰은 [TokenManagerProvider]에 지정된 토큰 저장소에 자동으로 저장됨.
     *
     * @param context CustomTabs를 실행하기 위한 현재 Activity context
     * @param scopes 추가로 동의 받고자 하는 동의 항목 ID 목록. 카카오 디벨로퍼스 동의 항목 설정 화면에서 확인 가능.
     *
     * @return [OAuthToken]을 방출하는 [Single] 반환.
     */
    fun loginWithNewScopes(
        context: Context,
        scopes: List<String>
    ): Single<OAuthToken> {
        val codeVerifier = AuthCodeClient.codeVerifier()
        return AuthApiClient.rx.agt()
            .subscribeOn(Schedulers.io())
            .flatMap {
                AuthCodeClient.rx.authorizeWithKakaoAccount(
                    context,
                    scopes = scopes,
                    agt = it,
                    codeVerifier = codeVerifier
                )
            }
            .observeOn(Schedulers.io())
            .flatMap { AuthApiClient.rx.issueAccessToken(it, codeVerifier) }
    }

    /**
     * 사용자에 대한 다양한 정보 획득.
     */
    @JvmOverloads
    fun me(secureReSource: Boolean = true): Single<User> =
        userApi.me(secureReSource)
            .compose(RxOperations.handleApiError())
            .compose(authOperations.handleApiError())

    /**
     * 앱 연결 상태가 **PREREGISTER** 상태의 사용자에 대하여 앱 연결 요청. **자동연결** 설정을 비활성화한 앱에서 사용.
     */
    @JvmOverloads
    fun signup(properties: Map<String, String>? = null): Completable =
        userApi.signup(properties)
            .compose(RxOperations.handleCompletableError())
            .compose(authOperations.handleCompletableError())

    /**
     * 현재 로그인한 사용자의 엑세스 토큰 정보 보기.
     *
     * [me] 에서 제공되는 다양한 사용자 정보 없이 가볍게 토큰의 유효성을 체크하는 용도로 사용하는 경우 추천합니다.
     * 액세스토큰이 만려되어있는 경우 리프레시토큰으로 갱신된 새로운 액세스토큰의 정보를 반환합니다.
     */
    fun accessTokenInfo(): Single<AccessTokenInfo> =
        userApi.accessTokenInfo()
            .compose(RxOperations.handleApiError())
            .compose(authOperations.handleApiError())

    /**
     * User 클래스에서 제공되고 있는 사용자의 부가정보를 신규저장 및 수정.
     *
     * 저장 가능한 키 이름은 카카오 디벨로퍼스 > 카카오 로그인 > 사용자 프로퍼티 메뉴에서 확인.
     * 앱 연결 시 기본 저장되는 nickname, profile_image, thumbnail_image 값도 덮어쓰기 가능하며 새로운 컬럼을 추가하면 해당 키 이름으로 정보 저장 가능.
     */
    fun updateProfile(properties: Map<String, String>): Completable =
        userApi.updateProfile(properties)
            .compose(RxOperations.handleCompletableError())
            .compose(authOperations.handleCompletableError())

    /**
     * 현재 토큰을 강제로 만료시키고 로그아웃.
     *
     * API 호출 결과와 관계 없이 [TokenManagerProvider]에 지정된 저장소에서 토큰을 자동으로 삭제함.
     */
    fun logout(): Completable =
        userApi.logout()
            .compose(RxOperations.handleCompletableError())
            .compose(authOperations.handleCompletableError())
            .doOnEvent { tokenManagerProvider.manager.clear() }

    /**
     * 연결 끊기. 카카오 로그인을 통한 사용자와 서비스 간의 연결 관계를 해제하고 사용자의 정보 제공 및 카카오 플랫폼 사용을 중단.
     *
     * API 호출에 성공하면 [TokenManagerProvider]에 지정된 저장소에서 토큰을 자동으로 삭제함.
     */
    fun unlink(): Completable =
        userApi.unlink()
            .compose(RxOperations.handleCompletableError())
            .compose(authOperations.handleCompletableError())
            .doOnComplete { tokenManagerProvider.manager.clear() }

    /**
     * 사용자의 배송지 정보 획득.
     *
     * @param fromUpdateAt
     * @param pageSize
     */
    @JvmOverloads
    fun shippingAddresses(
        fromUpdateAt: Date? = null,
        pageSize: Int? = null
    ): Single<UserShippingAddresses> {
        return userApi.shippingAddresses(fromUpdateAt = fromUpdateAt, pageSize = pageSize)
            .compose(RxOperations.handleApiError())
            .compose(authOperations.handleApiError())
    }

    /**
     * 사용자의 배송지 정보 획득.
     *
     * @param addressId 가져올 배송지 id
     */
    fun shippingAddresses(addressId: Long): Single<UserShippingAddresses> {
        return userApi.shippingAddresses(addressId)
            .compose(RxOperations.handleApiError())
            .compose(authOperations.handleApiError())
    }

    /**
     * 사용자가 카카오 간편가입을 통해 동의한 서비스 약관 내역 반환.
     */
    fun serviceTerms(extra: String? = null): Single<UserServiceTerms> {
        return userApi.serviceTerms(extra)
            .compose(RxOperations.handleApiError())
            .compose(authOperations.handleApiError())
    }

    /**
     * 사용자 동의 항목의 상세 정보 목록 반환.
     */
    fun scopes(scopes: List<String>? = null): Single<ScopeInfo> {
        return userApi.scopes(if (scopes == null) null else KakaoJson.toJson(scopes))
            .compose(RxOperations.handleApiError())
            .compose(authOperations.handleApiError())
    }

    /**
     * 사용자의 특정 동의 항목에 대한 동의를 철회하고, 남은 사용자 동의 항목의 상세 정보 목록 반환.
     *
     * @param scopes 동의를 철회할 동의 항목 ID 목록
     */
    fun revokeScopes(scopes: List<String>): Single<ScopeInfo> {
        return userApi.revokeScopes(KakaoJson.toJson(scopes))
            .compose(RxOperations.handleApiError())
            .compose(authOperations.handleApiError())
    }

    companion object {
        /**
         * User API 를 호출하기 위한 rx singleton
         */
        @JvmStatic
        val instance by lazy { UserApiClient.rx }
    }
}

/**
 * ReactiveX 를 위한 [UserApiClient] singleton 객체
 */
val UserApiClient.Companion.rx by lazy { RxUserApiClient() }