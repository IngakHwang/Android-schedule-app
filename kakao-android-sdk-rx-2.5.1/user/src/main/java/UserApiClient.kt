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
package com.kakao.sdk.user

import android.content.Context
import com.kakao.sdk.auth.AuthApiClient
import com.kakao.sdk.auth.AuthCodeClient
import com.kakao.sdk.auth.TokenManagerProvider
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.auth.model.Prompt
import com.kakao.sdk.auth.network.kapiWithOAuth
import com.kakao.sdk.common.util.KakaoJson
import com.kakao.sdk.network.ApiCallback
import com.kakao.sdk.network.ApiFactory
import com.kakao.sdk.user.model.*
import java.util.*

/**
 * 사용자관리 API 호출을 담당하는 클라이언트.
 */
class UserApiClient(
    private val userApi: UserApi = ApiFactory.kapiWithOAuth.create(UserApi::class.java),
    private val tokenManagerProvider: TokenManagerProvider = TokenManagerProvider.instance
) {

    /**
     * 카카오톡으로 로그인 가능(설치) 여부 검사.
     */
    fun isKakaoTalkLoginAvailable(context: Context): Boolean =
        AuthCodeClient.instance.isKakaoTalkLoginAvailable(context)

    /**
     * 카카오톡으로 로그인. 카카오톡에 연결된 카카오계정으로 사용자를 인증하고 [OAuthToken] 발급.
     *
     * 발급된 토큰은 [TokenManagerProvider]에 지정된 토큰 저장소에 자동으로 저장됨.
     *
     * @param context 카카오톡 로그인 Activity를 실행하기 위한 현재 Activity context
     * @param callback 발급 받은 [OAuthToken] 반환.
     */
    @JvmOverloads
    fun loginWithKakaoTalk(
        context: Context,
        requestCode: Int = AuthCodeClient.DEFAULT_REQUEST_CODE,
        channelPublicIds: List<String>? = null,
        serviceTerms: List<String>? = null,
        callback: (token: OAuthToken?, error: Throwable?) -> Unit
    ) {
        val codeVerifier = AuthCodeClient.codeVerifier()
        AuthCodeClient.instance.authorizeWithKakaoTalk(
            context,
            requestCode,
            channelPublicIds = channelPublicIds,
            serviceTerms = serviceTerms,
            codeVerifier = codeVerifier
        ) { code, codeError ->
            if (codeError != null) {
                callback(null, codeError)
            } else {
                AuthApiClient.instance.issueAccessToken(code!!, codeVerifier) { token, tokenError ->
                    callback(token, tokenError)
                }
            }
        }
    }

    /**
     * 카카오계정으로 로그인. 기본 웹 브라우저(CustomTabs)에 있는 카카오계정 cookie 로 사용자를 인증하고 [OAuthToken] 발급.
     *
     * 발급된 토큰은 [TokenManagerProvider]에 지정된 토큰 저장소에 자동으로 저장됨.
     *
     * @param context CustomTabs를 실행하기 위한 현재 Activity context
     * @param prompts 동의 화면 요청 시 추가 상호작용을 요청하고자 할 때 전달. [Prompt]
     * @param callback 발급 받은 [OAuthToken] 반환.
     */
    @JvmOverloads
    fun loginWithKakaoAccount(
        context: Context,
        prompts: List<Prompt>? = null,
        channelPublicIds: List<String>? = null,
        serviceTerms: List<String>? = null,
        callback: (token: OAuthToken?, error: Throwable?) -> Unit
    ) {
        val codeVerifier = AuthCodeClient.codeVerifier()
        AuthCodeClient.instance.authorizeWithKakaoAccount(
            context,
            prompts = prompts,
            channelPublicIds = channelPublicIds,
            serviceTerms = serviceTerms,
            codeVerifier = codeVerifier
        ) { code, codeError ->
            if (codeError != null) {
                callback(null, codeError)
            } else {
                AuthApiClient.instance.issueAccessToken(code!!, codeVerifier) { token, tokenError ->
                    callback(token, tokenError)
                }
            }
        }
    }

    /**
     * 사용자가 아직 동의하지 않은 개인정보 및 접근권한 동의 항목에 대하여 동의를 요청하는 동의 화면을 출력하고, 사용자 동의 시 동의항목이 업데이트 된 [OAuthToken] 발급.
     *
     * 발급된 토큰은 [TokenManagerProvider]에 지정된 토큰 저장소에 자동으로 저장됨.
     *
     * @param context CustomTabs를 실행하기 위한 현재 Activity context
     * @param scopes 추가로 동의 받고자 하는 동의 항목 ID 목록. 카카오 디벨로퍼스 동의 항목 설정 화면에서 확인 가능.
     * @param callback 발급 받은 [OAuthToken] 반환.
     */
    fun loginWithNewScopes(
        context: Context,
        scopes: List<String>,
        callback: (token: OAuthToken?, error: Throwable?) -> Unit
    ) {
        AuthApiClient.instance.agt { agt, agtError ->
            if (agtError != null) {
                callback(null, agtError)
            } else {
                val codeVerifier = AuthCodeClient.codeVerifier()
                AuthCodeClient.instance.authorizeWithKakaoAccount(
                    context,
                    scopes = scopes,
                    agt = agt,
                    codeVerifier = codeVerifier
                ) { code, codeError ->
                    if (codeError != null) {
                        callback(null, codeError)
                    } else {
                        AuthApiClient.instance.issueAccessToken(
                            code!!,
                            codeVerifier
                        ) { token, tokenError ->
                            callback(token, tokenError)
                        }
                    }
                }
            }
        }
    }

    /**
     * 사용자 정보 요청.
     */
    @JvmOverloads
    fun me(secureReSource: Boolean = true, callback: (user: User?, error: Throwable?) -> Unit) {
        userApi.me(secureReSource)
            .enqueue(object : ApiCallback<User>() {
                override fun onComplete(model: User?, error: Throwable?) {
                    callback(model, error)
                }
            })
    }

    /**
     * 현재 로그인한 사용자의 엑세스 토큰 정보 보기.
     *
     * [me] 에서 제공되는 다양한 사용자 정보 없이 가볍게 토큰의 유효성을 체크하는 용도로 추천.
     * 액세스 토큰이 만료된 경우 자동으로 갱신된 새로운 액세스 토큰 정보 반환.
     */
    fun accessTokenInfo(callback: (tokenInfo: AccessTokenInfo?, error: Throwable?) -> Unit) {
        userApi.accessTokenInfo()
            .enqueue(object : ApiCallback<AccessTokenInfo>() {
                override fun onComplete(model: AccessTokenInfo?, error: Throwable?) {
                    callback(model, error)
                }
            })
    }

    /**
     * User 클래스에서 제공되고 있는 사용자의 부가정보를 신규저장 및 수정.
     *
     * 저장 가능한 키 이름은 카카오 디벨로퍼스 > 카카오 로그인 > 사용자 프로퍼티 메뉴에서 확인.
     * 앱 연결 시 기본 저장되는 nickname, profile_image, thumbnail_image 값도 덮어쓰기 가능하며 새로운 컬럼을 추가하면 해당 키 이름으로 정보 저장 가능.
     */
    fun updateProfile(properties: Map<String, String>, callback: (error: Throwable?) -> Unit) {
        userApi.updateProfile(properties)
            .enqueue(object : ApiCallback<Unit>() {
                override fun onComplete(model: Unit?, error: Throwable?) {
                    callback(error)
                }
            })
    }

    /**
     * 현재 토큰을 강제로 만료시키고 로그아웃.
     *
     * API 호출 결과와 관계 없이 [TokenManagerProvider]에 지정된 저장소에서 토큰을 자동으로 삭제함.
     */
    fun logout(callback: (error: Throwable?) -> Unit) {
        userApi.logout()
            .enqueue(object : ApiCallback<Unit>() {
                override fun onComplete(model: Unit?, error: Throwable?) {
                    tokenManagerProvider.manager.clear()
                    callback(error)
                }
            })
    }

    /**
     * 연결 끊기. 카카오 로그인을 통한 사용자와 서비스 간의 연결 관계를 해제하고 사용자의 정보 제공 및 카카오 플랫폼 사용을 중단.
     *
     * API 호출에 성공하면 [TokenManagerProvider]에 지정된 저장소에서 토큰을 자동으로 삭제함.
     */
    fun unlink(callback: (error: Throwable?) -> Unit) {
        userApi.unlink()
            .enqueue(object : ApiCallback<Unit>() {
                override fun onComplete(model: Unit?, error: Throwable?) {
                    if (error == null) {
                        tokenManagerProvider.manager.clear()
                    }
                    callback(error)
                }
            })
    }

    /**
     * 사용자의 배송지 정보 획득.
     *
     * @param fromUpdateAt
     * @param pageSize
     */
    @JvmOverloads
    fun shippingAddresses(
        fromUpdateAt: Date? = null,
        pageSize: Int? = null,
        callback: (userShippingAddresses: UserShippingAddresses?, error: Throwable?) -> Unit
    ) {
        userApi.shippingAddresses(fromUpdateAt = fromUpdateAt, pageSize = pageSize)
            .enqueue(object : ApiCallback<UserShippingAddresses>() {
                override fun onComplete(model: UserShippingAddresses?, error: Throwable?) {
                    callback(model, error)
                }
            })
    }

    /**
     * 사용자의 배송지 정보 획득.
     *
     * @param addressId 가져올 배송지 id
     */
    fun shippingAddresses(
        addressId: Long,
        callback: (userShippingAddresses: UserShippingAddresses?, error: Throwable?) -> Unit
    ) {
        userApi.shippingAddresses(addressId)
            .enqueue(object : ApiCallback<UserShippingAddresses>() {
                override fun onComplete(model: UserShippingAddresses?, error: Throwable?) {
                    callback(model, error)
                }
            })
    }

    /**
     * 사용자가 카카오 간편가입을 통해 동의한 서비스 약관 내역 반환.
     */
    fun serviceTerms(
        extra: String? = null,
        callback: (userServiceTerms: UserServiceTerms?, error: Throwable?) -> Unit
    ) {
        userApi.serviceTerms(extra)
            .enqueue(object : ApiCallback<UserServiceTerms>() {
                override fun onComplete(model: UserServiceTerms?, error: Throwable?) {
                    callback(model, error)
                }
            })
    }

    /**
     * 앱 연결 상태가 **PREREGISTER** 상태의 사용자에 대하여 앱 연결 요청. **자동연결** 설정을 비활성화한 앱에서 사용.
     */
    fun signup(
        properties: Map<String, String>? = null,
        callback: (error: Throwable?) -> Unit
    ) {
        userApi.signup(properties)
            .enqueue(object : ApiCallback<Unit>() {
                override fun onComplete(model: Unit?, error: Throwable?) {
                    callback(error)
                }
            })
    }

    /**
     * 사용자 동의 항목의 상세 정보 목록 반환.
     */
    fun scopes(
        scopes: List<String>? = null,
        callback: (scopeInfo: ScopeInfo?, error: Throwable?) -> Unit
    ) {
        userApi.scopes(if (scopes == null) null else KakaoJson.toJson(scopes))
            .enqueue(object : ApiCallback<ScopeInfo>() {
                override fun onComplete(model: ScopeInfo?, error: Throwable?) {
                    callback(model, error)
                }
            })
    }

    /**
     * 사용자의 특정 동의 항목에 대한 동의를 철회하고, 남은 사용자 동의 항목의 상세 정보 목록 반환.
     *
     * @param scopes 동의를 철회할 동의 항목 ID 목록
     */
    fun revokeScopes(
        scopes: List<String>,
        callback: (scopeInfo: ScopeInfo?, error: Throwable?) -> Unit
    ) {
        userApi.revokeScopes(KakaoJson.toJson(scopes)).enqueue(object : ApiCallback<ScopeInfo>() {
            override fun onComplete(model: ScopeInfo?, error: Throwable?) {
                callback(model, error)
            }
        })
    }

    companion object {
        /**
         * 간편한 API 호출을 위해 기본 제공되는 singleton 객체
         */
        @JvmStatic
        val instance by lazy { UserApiClient() }
    }
}
