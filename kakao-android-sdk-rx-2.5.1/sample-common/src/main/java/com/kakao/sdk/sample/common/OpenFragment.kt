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
package com.kakao.sdk.sample.common

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.*
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kakao.sdk.auth.*
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.auth.model.Prompt
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.common.model.*
import com.kakao.sdk.common.util.KakaoCustomTabsClient
import com.kakao.sdk.common.util.KakaoJson
import com.kakao.sdk.link.LinkClient
import com.kakao.sdk.link.WebSharerClient
import com.kakao.sdk.navi.NaviClient
import com.kakao.sdk.navi.model.CoordType
import com.kakao.sdk.navi.model.Location
import com.kakao.sdk.navi.model.NaviOption
import com.kakao.sdk.sample.common.internal.ApiAdapter
import com.kakao.sdk.sample.common.internal.FriendsActivity
import com.kakao.sdk.sample.common.internal.Log
import com.kakao.sdk.sample.common.internal.PickerItem
import com.kakao.sdk.story.StoryApiClient
import com.kakao.sdk.talk.TalkApiClient
import com.kakao.sdk.talk.model.*
import com.kakao.sdk.template.model.*
import com.kakao.sdk.user.UserApiClient
import kotlinx.android.synthetic.main.fragment_open.*
import java.io.File
import java.io.FileOutputStream

class OpenFragment : Fragment() {

    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    val TAG = "KakaoSDKSample"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val context = requireContext()
        Log.view = view

        viewManager = LinearLayoutManager(context)
        viewAdapter = ApiAdapter(
            listOf(
                ApiAdapter.Item.Header("User API"),
                ApiAdapter.Item.ApiItem("isKakaoTalkLoginAvailable()") {

                    // 카카오톡 설치여부 확인
                    if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
                        Log.i(TAG, "카카오톡으로 로그인 가능")
                    } else {
                        Log.i(TAG, "카카오톡 미설치: 카카오계정으로 로그인 사용 권장")
                    }
                },
                ApiAdapter.Item.ApiItem("loginWithKakaoTalk()") {

                    // 카카오톡으로 로그인
                    UserApiClient.instance.loginWithKakaoTalk(context) { token, error ->
                        if (error != null) {
                            Log.e(TAG, "로그인 실패", error)
                        }
                        else if (token != null) {
                            Log.i(TAG, "로그인 성공 ${token.accessToken}")
                        }
                    }
                },
                ApiAdapter.Item.ApiItem("loginWithKakaoAccount()") {

                    // 카카오계정으로 로그인
                    UserApiClient.instance.loginWithKakaoAccount(context) { token, error ->
                        if (error != null) {
                            Log.e(TAG, "로그인 실패", error)
                        }
                        else if (token != null) {
                            Log.i(TAG, "로그인 성공 ${token.accessToken}")
                        }
                    }
                },
                ApiAdapter.Item.ApiItem("loginWithKakaoAccount(prompts:)") {

                    // 카카오계정으로 로그인 - 재인증
                    UserApiClient.instance.loginWithKakaoAccount(context, prompts = listOf(Prompt.LOGIN)) { token, error ->
                        if (error != null) {
                            Log.e(TAG, "로그인 실패", error)
                        }
                        else if (token != null) {
                            Log.i(TAG, "로그인 성공 ${token.accessToken}")
                        }
                    }
                },
                ApiAdapter.Item.ApiItem("Combination Login") {

                    // 로그인 조합 예제

                    // 로그인 공통 callback 구성
                    val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
                        if (error != null) {
                            Log.e(TAG, "로그인 실패", error)
                        }
                        else if (token != null) {
                            Log.i(TAG, "로그인 성공 ${token.accessToken}")
                        }
                    }

                    // 카카오톡이 설치되어 있으면 카카오톡으로 로그인, 아니면 카카오계정으로 로그인
                    if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
                        UserApiClient.instance.loginWithKakaoTalk(context, callback = callback)
                    } else {
                        UserApiClient.instance.loginWithKakaoAccount(context, callback = callback)
                    }
                },
                ApiAdapter.Item.ApiItem("Combination Login (Verbose Callback)") {

                    // 로그인 조합 예제 + 상세한 에러처리 콜백

                    // 로그인 공통 callback 구성
                    val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
                        if (error != null) {
                            // 로그인 실패
                            // 아래는 로그인 요청의 대표적인 에러케이스 처리 예제입니다. 서비스에 최적화된 형태로 간소화하세요.
                            when (error) {
                                is ClientError ->
                                    when (error.reason) {
                                        ClientErrorCause.Cancelled ->
                                            Log.d(TAG, "취소됨 (back button)", error)
                                        ClientErrorCause.NotSupported ->
                                            Log.e(TAG, "지원되지 않음 (카톡 미설치)", error)
                                        else ->
                                            Log.e(TAG, "기타 클라이언트 에러", error)
                                    }
                                is AuthError ->
                                    when (error.reason) {
                                        AuthErrorCause.AccessDenied ->
                                            Log.d(TAG, "취소됨 (동의 취소)", error)
                                        AuthErrorCause.Misconfigured ->
                                            Log.e(TAG, "개발자사이트 앱 설정에 키해시를 등록하세요. 현재 값: ${KakaoSdk.keyHash}", error)
                                        else ->
                                            Log.e(TAG, "기타 인증 에러", error)
                                    }
                                else ->
                                    // 에러처리에 대한 개선사항이 필요하면 데브톡(https://devtalk.kakao.com)으로 문의해주세요.
                                    Log.e(TAG, "기타 에러 (네트워크 장애 등..)", error)
                            }
                        }
                        else if (token != null) {
                            Log.i(TAG, "로그인 성공 ${token.accessToken}")
                        }
                    }

                    // 카카오톡이 설치되어 있으면 카카오톡으로 로그인, 아니면 카카오계정으로 로그인
                    if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
                        UserApiClient.instance.loginWithKakaoTalk(context, callback = callback)
                    } else {
                        UserApiClient.instance.loginWithKakaoAccount(context, callback = callback)
                    }
                },
                ApiAdapter.Item.ApiItem("me()") {

                    // 사용자 정보 요청 (기본)
                    UserApiClient.instance.me { user, error ->
                        if (error != null) {
                            Log.e(TAG, "사용자 정보 요청 실패", error)
                        }
                        else if (user != null) {
                            Log.i(TAG, "사용자 정보 요청 성공" +
                                    "\n회원번호: ${user.id}" +
                                    "\n이메일: ${user.kakaoAccount?.email}" +
                                    "\n닉네임: ${user.kakaoAccount?.profile?.nickname}" +
                                    "\n프로필사진: ${user.kakaoAccount?.profile?.thumbnailImageUrl}")
                        }
                    }
                },
                ApiAdapter.Item.ApiItem("me() - new scopes") {

                    // 사용자 정보 요청 (추가 동의)

                    // 사용자가 로그인 시 제3자 정보제공에 동의하지 않은 개인정보 항목 중 어떤 정보가 반드시 필요한 시나리오에 진입한다면
                    // 다음과 같이 추가 동의를 받고 해당 정보를 획득할 수 있습니다.

                    //  * 주의: 선택 동의항목은 사용자가 거부하더라도 서비스 이용에 지장이 없어야 합니다.

                    // 추가 권한 요청 시나리오 예제
                    UserApiClient.instance.me { user, error ->
                        if (error != null) {
                            Log.e(TAG, "사용자 정보 요청 실패", error)
                        }
                        else if (user != null) {
                            var scopes = mutableListOf<String>()

                            if (user.kakaoAccount?.emailNeedsAgreement == true) { scopes.add("account_email") }
                            if (user.kakaoAccount?.birthdayNeedsAgreement == true) { scopes.add("birthday") }
                            if (user.kakaoAccount?.birthyearNeedsAgreement == true) { scopes.add("birthyear") }
                            if (user.kakaoAccount?.ciNeedsAgreement == true) { scopes.add("account_ci") }
                            if (user.kakaoAccount?.legalNameNeedsAgreement == true) { scopes.add("legal_name") }
                            if (user.kakaoAccount?.legalBirthDateNeedsAgreement == true) { scopes.add("legal_birth_date") }
                            if (user.kakaoAccount?.legalGenderNeedsAgreement == true) { scopes.add("legal_gender") }
                            if (user.kakaoAccount?.phoneNumberNeedsAgreement == true) { scopes.add("phone_number") }
                            if (user.kakaoAccount?.profileNeedsAgreement == true) { scopes.add("profile") }
                            if (user.kakaoAccount?.ageRangeNeedsAgreement == true) { scopes.add("age_range") }

                            if (scopes.count() > 0) {
                                Log.d(TAG, "사용자에게 추가 동의를 받아야 합니다.")

                                UserApiClient.instance.loginWithNewScopes(context, scopes) { token, error ->
                                    if (error != null) {
                                        Log.e(TAG, "사용자 추가 동의 실패", error)
                                    } else {
                                        Log.d(TAG, "allowed scopes: ${token!!.scopes}")

                                        // 사용자 정보 재요청
                                        UserApiClient.instance.me { user, error ->
                                            if (error != null) {
                                                Log.e(TAG, "사용자 정보 요청 실패", error)
                                            }
                                            else if (user != null) {
                                                Log.i(TAG, "사용자 정보 요청 성공")
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                },
                ApiAdapter.Item.ApiItem("signup()"){
                    UserApiClient.instance.signup { error ->
                        if (error != null) {
                            Log.e(TAG, "signup 실패", error)
                        } else {
                            Log.i(TAG, "signup 성공")
                        }
                    }
                },
                ApiAdapter.Item.ApiItem("scopes()") {
                    UserApiClient.instance.scopes { scopeInfo, error->
                        if (error != null) {
                            Log.e(TAG, "동의 정보 확인 실패", error)
                        }else if (scopeInfo != null) {
                            Log.i(TAG, "동의 정보 확인 성공\n 현재 가지고 있는 동의 항목 $scopeInfo")
                        }
                    }
                },
                // 특정 동의 항목 확인하기
                ApiAdapter.Item.ApiItem("scopes() - optional") {
                    val scopes = mutableListOf("account_email", "friends")
                    UserApiClient.instance.scopes(scopes) { scopeInfo, error->
                        if (error != null) {
                            Log.e(TAG, "동의 정보 확인 실패", error)
                        }else if (scopeInfo != null) {
                            Log.i(TAG, "동의 정보 확인 성공\n 현재 가지고 있는 동의 항목 $scopeInfo")
                        }
                    }
                },
                ApiAdapter.Item.ApiItem("revokeScopes()") {
                    val scopes = mutableListOf("account_email", "legal_birth_date", "friends")
                     UserApiClient.instance.revokeScopes(scopes) { scopeInfo, error->
                         if (error != null) {
                             Log.e(TAG, "동의 철회 실패", error)
                         }else if (scopeInfo != null) {
                             Log.i(TAG, "동의 철회 성공\n 현재 가지고 있는 동의 항목 $scopeInfo")
                         }
                     }
                },
                ApiAdapter.Item.ApiItem("accessTokenInfo()") {

                    // 토큰 정보 보기
                    UserApiClient.instance.accessTokenInfo { tokenInfo, error ->
                        if (error != null) {
                            Log.e(TAG, "토큰 정보 보기 실패", error)
                        }
                        else if (tokenInfo != null) {
                            Log.i(TAG, "토큰 정보 보기 성공" +
                                    "\n회원번호: ${tokenInfo.id}" +
                                    "\n만료시간: ${tokenInfo.expiresIn} 초")
                        }
                    }
                },
                ApiAdapter.Item.ApiItem("updateProfile() - nickname") {

                    // 사용자 정보 저장

                    // 변경할 내용
                    val properties = mapOf("nickname" to "${System.currentTimeMillis()}")

                    UserApiClient.instance.updateProfile(properties) { error ->
                        if (error != null) {
                            Log.e(TAG, "사용자 정보 저장 실패", error)
                        }
                        else {
                            Log.i(TAG, "사용자 정보 저장 성공")
                        }
                    }
                },
                ApiAdapter.Item.ApiItem("shippingAddresses()") {

                    // 배송지 조회 (추가 동의)
                    UserApiClient.instance.shippingAddresses { userShippingAddresses, error ->
                        if (error != null) {
                            Log.e(TAG, "배송지 조회 실패", error)
                        }
                        else if (userShippingAddresses != null) {
                            if (userShippingAddresses.shippingAddresses != null) {
                                Log.i(TAG, "배송지 조회 성공" +
                                        "\n회원번호: ${userShippingAddresses.userId}" +
                                        "\n배송지: \n${userShippingAddresses.shippingAddresses?.joinToString("\n")}")
                            }
                            else if (userShippingAddresses.needsAgreement == false) {
                                Log.e(TAG, "사용자 계정에 배송지 없음. 꼭 필요하다면 동의항목 설정에서 수집 기능을 활성화 해보세요.")
                            }
                            else if (userShippingAddresses.needsAgreement == true) {
                                Log.d(TAG, "사용자에게 배송지 제공 동의를 받아야 합니다.")

                                val scopes = listOf("shipping_address")

                                // 사용자에게 배송지 제공 동의 요청
                                UserApiClient.instance.loginWithNewScopes(context, scopes) { token, error ->
                                    if (error != null) {
                                        Log.e(TAG, "배송지 제공 동의 실패", error)
                                    }
                                    else if (token != null) {
                                        Log.d(TAG, "allowed scopes: ${token.scopes}")

                                        // 배송지 조회 재요청
                                        UserApiClient.instance.shippingAddresses { userShippingAddresses, error ->
                                            if (error != null) {
                                                Log.e(TAG, "배송지 조회 실패", error)
                                            }
                                            else if (userShippingAddresses != null) {
                                                Log.i(TAG, "배송지 조회 성공" +
                                                        "\n회원번호: ${userShippingAddresses.userId}" +
                                                        "\n배송지: \n${userShippingAddresses.shippingAddresses?.joinToString("\n")}")
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                },
                ApiAdapter.Item.ApiItem("serviceTerms()") {

                    // 동의한 약관 확인하기
                    UserApiClient.instance.serviceTerms { userServiceTerms, error ->
                        if (error != null) {
                            Log.e(TAG, "동의한 약관 확인하기 실패", error)
                        }
                        else if (userServiceTerms != null) {
                            Log.i(TAG, "동의한 약관 확인하기 성공" +
                                    "\n회원번호: ${userServiceTerms.userId}" +
                                    "\n동의한 약관: \n${userServiceTerms.allowedServiceTerms?.joinToString("\n")}")
                        }
                    }
                },
                ApiAdapter.Item.ApiItem("logout()") {

                    // 로그아웃
                    UserApiClient.instance.logout { error ->
                        if (error != null) {
                            Log.e(TAG, "로그아웃 실패. SDK에서 토큰 삭제 됨", error)
                        }
                        else {
                            Log.i(TAG, "로그아웃 성공. SDK에서 토큰 삭제 됨")
                        }
                    }
                },
                ApiAdapter.Item.ApiItem("unlink()") {

                    // 연결 끊기
                    UserApiClient.instance.unlink { error ->
                        if (error != null) {
                            Log.e(TAG, "연결 끊기 실패", error)
                        }
                        else {
                            Log.i(TAG, "연결 끊기 성공. SDK에서 토큰 삭제 됨")
                        }
                    }
                },
                ApiAdapter.Item.Header("KakaoTalk API"),
                ApiAdapter.Item.ApiItem("profile()") {

                    // 카카오톡 프로필 받기
                    TalkApiClient.instance.profile { profile, error ->
                        if (error != null) {
                            Log.e(TAG, "카카오톡 프로필 받기 실패", error)
                        }
                        else if (profile != null) {
                            Log.i(TAG, "카카오톡 프로필 받기 성공" +
                                    "\n닉네임: ${profile.nickname}" +
                                    "\n프로필사진: ${profile.thumbnailUrl}" +
                                    "\n국가코드: ${profile.countryISO}")
                        }
                    }
                },
                ApiAdapter.Item.ApiItem("sendCustomMemo()") {

                    // 커스텀 템플릿으로 나에게 보내기

                    // 메시지 템플릿 아이디
                    //  * 만들기 가이드: https://developers.kakao.com/docs/latest/ko/message/message-template
                    val templateId = templateIds["customMessage"] as Long

                    TalkApiClient.instance.sendCustomMemo(templateId) { error ->
                        if (error != null) {
                            Log.e(TAG, "나에게 보내기 실패", error)
                        } else {
                            Log.i(TAG, "나에게 보내기 성공")
                        }
                    }
                },
                ApiAdapter.Item.ApiItem("sendDefaultMemo()") {

                    // 디폴트 템플릿으로 나에게 보내기 - Feed
                    TalkApiClient.instance.sendDefaultMemo(defaultFeed) { error ->
                        if (error != null) {
                            Log.e(TAG, "나에게 보내기 실패", error)
                        } else {
                            Log.i(TAG, "나에게 보내기 성공")
                        }
                    }
                },
                ApiAdapter.Item.ApiItem("sendScrapMemo()") {

                    // 스크랩 템플릿으로 나에게 보내기

                    // 공유할 웹페이지 URL
                    //  * 주의: 개발자사이트 Web 플랫폼 설정에 공유할 URL의 도메인이 등록되어 있어야 합니다.
                    val url = "https://developers.kakao.com"

                    TalkApiClient.instance.sendScrapMemo(url) { error ->
                        if (error != null) {
                            Log.e(TAG, "나에게 보내기 실패", error)
                        }
                        else {
                            Log.i(TAG, "나에게 보내기 성공")
                        }
                    }
                },
                ApiAdapter.Item.ApiItem("friends()") {

                    // 카카오톡 친구 목록 받기 (기본)
                    TalkApiClient.instance.friends { friends, error ->
                        if (error != null) {
                            Log.e(TAG, "카카오톡 친구 목록 받기 실패", error)
                        }
                        else if (friends != null) {
                            Log.i(TAG, "카카오톡 친구 목록 받기 성공 \n${friends.elements?.joinToString("\n")}")

                            // 친구의 UUID 로 메시지 보내기 가능
                        }
                    }
                },
                ApiAdapter.Item.ApiItem("friends(order:) - desc") {

                    // 카카오톡 친구 목록 받기 (파라미터)

                    // 내림차순으로 받기
                    TalkApiClient.instance.friends(order = Order.DESC) { friends, error ->
                        if (error != null) {
                            Log.e(TAG, "카카오톡 친구 목록 받기 실패", error)
                        }
                        else if (friends != null) {
                            Log.i(TAG, "카카오톡 친구 목록 받기 성공 \n${friends.elements?.joinToString("\n")}")

                            // 친구의 UUID 로 메시지 보내기 가능
                        }
                    }
                },
                ApiAdapter.Item.ApiItem("friends(context:) - reculsive") {

                    var nextFriendsContext = FriendsContext(offset= 0, limit= 3, order = Order.DESC)

                    recursiveAppFriendsCompletion = recursiveAppFriendsCompletion@{ friends, error  ->
                        if (error == null) {
                            if (friends != null) {
                                try {
                                    friends.afterUrl?.let {
                                        nextFriendsContext = FriendsContext(url = it)
                                    } ?:  return@recursiveAppFriendsCompletion

                                } catch (e: IllegalArgumentException) {
                                    return@recursiveAppFriendsCompletion
                                }
                            }

                            TalkApiClient.instance.friends(context = nextFriendsContext) { friends, error ->
                                if (error != null) {
                                    Log.e(TAG, "카카오톡 친구 목록 받기 실패", error)
                                } else if (friends != null) {
                                    Log.i(TAG, "카카오톡 친구 목록 받기 성공 \n${friends.elements?.joinToString("\n")}")
                                    recursiveAppFriendsCompletion?.let { it(friends, null) }
                                }
                            }
                        }
                    }

                    recursiveAppFriendsCompletion?.let { it(null, null) }
                },
                ApiAdapter.Item.ApiItem("friends(context:) - FriendsContext") {
                    TalkApiClient.instance.friends(context = FriendsContext(offset = 0, limit = 1, order = Order.DESC)) { friends, error ->
                        if (error != null) {
                            Log.e(TAG, "카카오톡 친구 목록 받기 실패", error)
                        } else if (friends != null) {
                            Log.i(TAG, "카카오톡 친구 목록 받기 성공 \n${friends.elements?.joinToString("\n")}")
                        }
                    }

                },
                ApiAdapter.Item.ApiItem("sendCustomMessage()") {

                    // 커스텀 템플릿으로 친구에게 메시지 보내기

                    // 카카오톡 친구 목록 받기
                    TalkApiClient.instance.friends { friends, error ->
                        if (error != null) {
                            Log.e(TAG, "카카오톡 친구 목록 받기 실패", error)
                        }
                        else if (friends != null) {
                            friends.elements?.let {
                                Log.d(TAG, "카카오톡 친구 목록 받기 성공 \n${it.joinToString("\n")}")

                                if (it.isEmpty()) {
                                    Log.e(TAG, "메시지 보낼 친구가 하나도 없어요 ㅠㅠ")
                                } else {
                                    // 서비스에 상황에 맞게 메시지 보낼 친구의 UUID 를 가져오세요.
                                    // 이 샘플에서는 친구 목록을 화면에 보여주고 체크박스로 선택된 친구들의 UUID 를 수집하도록 구현했습니다.
                                    FriendsActivity.startForResult(
                                        context,
                                        it.map { friend -> PickerItem(friend.uuid, friend.profileNickname ?: "", friend.profileThumbnailImage) }
                                    ) { selectedItems ->
                                        if (selectedItems.isEmpty()) return@startForResult
                                        Log.d(TAG, "선택된 친구:\n${selectedItems.joinToString("\n")}")


                                        // 메시지 보낼 친구의 UUID 목록
                                        val receiverUuids = selectedItems

                                        // 메시지 템플릿 아이디
                                        //  * 만들기 가이드: https://developers.kakao.com/docs/latest/ko/message/message-template
                                        val templateId = templateIds["customMessage"] as Long

                                        // 메시지 보내기
                                        TalkApiClient.instance.sendCustomMessage(receiverUuids, templateId) { result, error ->
                                            if (error != null) {
                                                Log.e(TAG, "메시지 보내기 실패", error)
                                            }
                                            else if (result != null){
                                                Log.i(TAG, "메시지 보내기 성공 ${result.successfulReceiverUuids}")

                                                if (result.failureInfos != null) {
                                                    Log.d(TAG, "메시지 보내기에 일부 성공했으나, 일부 대상에게는 실패 \n${result.failureInfos}")
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                },
                ApiAdapter.Item.ApiItem("sendDefaultMessage()") {

                    // 디폴트 템플릿으로 친구에게 메시지 보내기 - Feed

                    // 카카오톡 친구 목록 받기
                    TalkApiClient.instance.friends { friends, error ->
                        if (error != null) {
                            Log.e(TAG, "카카오톡 친구 목록 받기 실패", error)
                        } else if (friends != null) {
                            friends.elements?.let {
                                Log.d(TAG, "카카오톡 친구 목록 받기 성공 \n${it.joinToString("\n")}")

                                if (it.isEmpty()) {
                                    Log.e(TAG, "메시지 보낼 친구가 하나도 없어요 ㅠㅠ")
                                }
                                else {

                                    // 서비스에 상황에 맞게 메시지 보낼 친구의 UUID를 가져오세요.
                                    // 이 샘플에서는 친구 목록을 화면에 보여주고 체크박스로 선택된 친구들의 UUID 를 수집하도록 구현했습니다.
                                    FriendsActivity.startForResult(
                                        context,
                                        it.map { friend -> PickerItem(friend.uuid, friend.profileNickname?: "", friend.profileThumbnailImage) }
                                    ) { selectedItems ->
                                        if (selectedItems.isEmpty()) return@startForResult
                                        Log.d(TAG, "선택된 친구:\n${selectedItems.joinToString("\n")}")


                                        // 메시지 보낼 친구의 UUID 목록
                                        val receiverUuids = selectedItems

                                        // Feed 메시지
                                        val template = defaultFeed

                                        // 메시지 보내기
                                        TalkApiClient.instance.sendDefaultMessage(receiverUuids, template) { result, error ->
                                            if (error != null) {
                                                Log.e(TAG, "메시지 보내기 실패", error)
                                            }
                                            else if (result != null) {
                                                Log.i(TAG, "메시지 보내기 성공 ${result.successfulReceiverUuids}")

                                                if (result.failureInfos != null) {
                                                    Log.d(TAG, "메시지 보내기에 일부 성공했으나, 일부 대상에게는 실패 \n${result.failureInfos}")
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                },
                ApiAdapter.Item.ApiItem("sendScrapMessage()") {

                    // 스크랩 템플릿으로 친구에게 메시지 보내기

                    // 카카오톡 친구 목록 받기
                    TalkApiClient.instance.friends { friends, error ->
                        if (error != null) {
                            Log.e(TAG, "카카오톡 친구 목록 받기 실패", error)
                        } else if (friends != null) {
                            friends.elements?.let {
                                Log.d(TAG, "카카오톡 친구 목록 받기 성공 \n${it.joinToString("\n")}")

                                if (it.isEmpty()) {
                                    Log.e(TAG, "메시지 보낼 친구가 하나도 없어요 ㅠㅠ")
                                } else {

                                    // 서비스에 상황에 맞게 메시지 보낼 친구의 UUID를 가져오세요.
                                    // 이 샘플에서는 친구 목록을 화면에 보여주고 체크박스로 선택된 친구들의 UUID 를 수집하도록 구현했습니다.
                                    FriendsActivity.startForResult(
                                        context,
                                        it.map { friend -> PickerItem(friend.uuid, friend.profileNickname?: "", friend.profileThumbnailImage) }
                                    ) { selectedItems ->
                                        if (selectedItems.isEmpty()) return@startForResult
                                        Log.d(TAG, "선택된 친구:\n${selectedItems.joinToString("\n")}")


                                        // 메시지 보낼 친구의 UUID 목록
                                        val receiverUuids = selectedItems

                                        // 공유할 웹페이지 URL
                                        //  * 주의: 개발자사이트 Web 플랫폼 설정에 공유할 URL의 도메인이 등록되어 있어야 합니다.
                                        val url = "https://developers.kakao.com"

                                        // 메시지 보내기
                                        TalkApiClient.instance.sendScrapMessage(receiverUuids, url) { result, error ->
                                            if (error != null) {
                                                Log.e(TAG, "메시지 보내기 실패", error)
                                            }
                                            else if (result != null) {
                                                Log.i(TAG, "메시지 보내기 성공 ${result.successfulReceiverUuids}")

                                                if (result.failureInfos != null) {
                                                    Log.d(TAG, "메시지 보내기에 일부 성공했으나, 일부 대상에게는 실패 \n${result.failureInfos}")
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                },
                ApiAdapter.Item.ApiItem("channels()") {

                    // 카카오톡 채널 관계 확인하기
                    TalkApiClient.instance.channels { relations, error ->
                        if (error != null) {
                            Log.e(TAG, "채널 관계 확인 실패", error)
                        }
                        else if (relations != null) {
                            Log.i(TAG, "채널 관계 확인 성공 \n${relations.channels}")
                        }
                    }
                },
                ApiAdapter.Item.ApiItem("addChannelUrl()") {

                    // 카카오톡 채널 추가하기 URL
                    val url = TalkApiClient.instance.addChannelUrl("_ZeUTxl")

                    // CustomTabs 로 열기
                    KakaoCustomTabsClient.openWithDefault(context, url)
                },
                ApiAdapter.Item.ApiItem("channelChatUrl()") {

                    // 카카오톡 채널 채팅 URL
                    val url = TalkApiClient.instance.channelChatUrl("_ZeUTxl")

                    // CustomTabs 로 열기
                    KakaoCustomTabsClient.openWithDefault(context, url)
                },
                ApiAdapter.Item.Header("KakaoStory API"),
                ApiAdapter.Item.ApiItem("isStoryUser()") {

                    // 카카오스토리 사용자 확인하기
                    StoryApiClient.instance.isStoryUser { isStoryUser, error ->
                        if (error != null) {
                            Log.e(TAG, "카카오스토리 사용자 확인 실패", error)
                        }
                        else {
                            Log.i(TAG, "카카오스토리 가입 여부: $isStoryUser")
                        }
                    }
                },
                ApiAdapter.Item.ApiItem("profile()") {

                    // 카카오스토리 프로필 받기
                    StoryApiClient.instance.profile { profile, error ->
                        if (error != null) {
                            Log.e(TAG, "카카오스토리 프로필 받기 실패", error)
                        }
                        else if (profile != null) {
                            Log.i(TAG, "카카오스토리 프로필 받기 성공" +
                                    "\n닉네임: ${profile.nickname}" +
                                    "\n프로필사진: ${profile.thumbnailUrl}" +
                                    "\n생일: ${profile.birthday}")
                        }
                    }
                },
                ApiAdapter.Item.ApiItem("stories()") {

                    // 여러 개의 스토리 받기
                    StoryApiClient.instance.stories { stories, error ->
                        if (error != null) {
                            Log.e(TAG, "스토리 받기 실패", error)
                        }
                        else {
                            Log.i(TAG, "스토리 받기 성공 \n$stories")
                        }
                    }
                },
                ApiAdapter.Item.ApiItem("story(id:) - first of stories") {

                    // 지정 스토리 받기

                    // 이 샘플에서는 받고자 하는 스토리 아이디를 얻기 위해 전체 목록을 조회하고 첫번째 스토리 아이디를 사용했습니다.
                    StoryApiClient.instance.stories { stories, error ->
                        if (error != null) {
                            Log.e(TAG, "스토리 받기 실패", error)
                        }
                        else if (stories != null) {
                            if (stories.isEmpty()) {
                                Log.e(TAG, "내 스토리가 하나도 없어요 ㅠㅠ")
                            }
                            else {

                                // 정보를 원하는 스토리 아이디
                                val storyId = stories.first().id

                                // 지정 스토리 받기
                                StoryApiClient.instance.story(storyId) { story, error ->
                                    if (error != null) {
                                        Log.e(TAG, "스토리 받기 실패", error)
                                    }
                                    else if (story != null) {
                                        Log.i(TAG, "스토리 받기 성공" +
                                                "\n아이디: ${story.id}" +
                                                "\n미디어 형식: ${story.mediaType}" +
                                                "\n작성일자: ${story.createdAt}" +
                                                "\n내용: ${story.content}")
                                    }
                                }
                            }
                        }
                    }
                },
                ApiAdapter.Item.ApiItem("delete(id:) - first of stories") {

                    // 내 스토리 삭제하기

                    // 이 샘플에서는 삭제고자 하는 스토리 아이디를 얻기 위해 전체 목록을 조회하고 첫번째 스토리 아이디를 사용했습니다.
                    StoryApiClient.instance.stories { stories, error ->
                        if (error != null) {
                            Log.e(TAG, "스토리 받기 실패", error)
                        }
                        else if (stories != null) {
                            if (stories.isEmpty()) {
                                Log.e(TAG, "내 스토리가 하나도 없어요 ㅠㅠ")
                            }
                            else {

                                // 삭제를 원하는 스토리 아이디
                                val storyId = stories.first().id

                                // 스토리 삭제하기
                                StoryApiClient.instance.delete(storyId) { error ->
                                    if (error != null) {
                                        Log.e(TAG, "스토리 삭제 실패", error)
                                    } else {
                                        Log.i(TAG, "스토리 삭제 성공 [$storyId]")
                                    }
                                }
                            }
                        }
                    }
                },
                ApiAdapter.Item.ApiItem("postNote()") {

                    // 글 스토리 쓰기
                    val content = "Posting note from Kakao SDK Sample."

                    StoryApiClient.instance.postNote(content) { storyPostResult, error ->
                        if (error != null) {
                            Log.e(TAG, "스토리 쓰기 실패", error)
                        }
                        else if (storyPostResult != null) {
                            Log.i(TAG, "스토리 쓰기 성공 [${storyPostResult.id}]")
                        }
                    }
                },
                ApiAdapter.Item.ApiItem("postLink()") {

                    // 링크 스토리 쓰기

                    // 지정된 URL로 링크 만들기
                    StoryApiClient.instance.linkInfo("https://www.kakaocorp.com") { linkInfoResult, error ->
                        if (error != null) {
                            Log.e(TAG, "링크 만들기 실패", error)
                        } else {
                            Log.d(TAG, "링크 만들기 성공 $linkInfoResult")

                            // 링크 스토리 쓰기
                            val linkInfo = linkInfoResult!!
                            val content = "Posting link from Kakao SDK Sample."

                            StoryApiClient.instance.postLink(linkInfo, content) { storyPostResult, error ->
                                if (error != null) {
                                    Log.e(TAG, "스토리 쓰기 실패", error)
                                }
                                else if (storyPostResult != null) {
                                    Log.i(TAG, "스토리 쓰기 성공 [${storyPostResult.id}]")
                                }
                            }
                        }
                    }
                },
                ApiAdapter.Item.ApiItem("postPhoto()") {

                    // 사진 스토리 쓰기

                    // 업로드할 사진 파일
                    // 이 샘플에서는 프로젝트 리소스로 추가한 이미지 파일을 사용했습니다. 갤러리 등 서비스 니즈에 맞는 사진 파일을 준비하세요.
                    val bitmap = BitmapFactory.decodeResource(resources, R.drawable.sample1)
                    val file = File(context.cacheDir, "sample1.png")
                    val stream = FileOutputStream(file)
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                    stream.close()

                    // 사진 업로드
                    StoryApiClient.instance.upload(listOf(file)) { uploadedPaths, error ->
                        if (error != null) {
                            Log.e(TAG, "사진 업로드 실패", error)
                        } else {
                            Log.d(TAG, "사진 업로드 성공 $uploadedPaths")

                            // 사진 스토리 쓰기
                            val images = uploadedPaths!!
                            val content = "Posting photo from Kakao SDK Sample."

                            StoryApiClient.instance.postPhoto(images, content) { storyPostResult, error ->
                                if (error != null) {
                                    Log.e(TAG, "스토리 쓰기 실패", error)
                                }
                                else if (storyPostResult != null) {
                                    Log.i(TAG, "스토리 쓰기 성공 [${storyPostResult.id}]")
                                }
                            }
                        }
                    }
                },
                ApiAdapter.Item.Header("KakaoLink API"),
                ApiAdapter.Item.ApiItem("isKakaoLinkAvailable()") {

                    // 카카오톡 설치여부 확인
                    if (LinkClient.instance.isKakaoLinkAvailable(context)) {
                        Log.i(TAG, "카카오톡으로 카카오링크 공유 가능")
                    } else {
                        Log.i(TAG, "카카오톡 미설치: 웹 공유 사용 권장")
                    }
                },
                ApiAdapter.Item.ApiItem("customTemplate()") {

                    // 커스텀 템플릿으로 카카오링크 보내기
                    //  * 만들기 가이드: https://developers.kakao.com/docs/latest/ko/message/message-template
                    val templateId = templateIds["customMemo"] as Long

                    LinkClient.instance.customTemplate(context, templateId) { linkResult, error ->
                        if (error != null) {
                            Log.e(TAG, "카카오링크 보내기 실패", error)
                        }
                        else if (linkResult != null) {
                            Log.d(TAG, "카카오링크 보내기 성공 ${linkResult.intent}")
                            startActivity(linkResult.intent)

                            // 카카오링크 보내기에 성공했지만 아래 경고 메시지가 존재할 경우 일부 컨텐츠가 정상 동작하지 않을 수 있습니다.
                            Log.w(TAG, "Warning Msg: ${linkResult.warningMsg}")
                            Log.w(TAG, "Argument Msg: ${linkResult.argumentMsg}")
                        }
                    }
                },
                ApiAdapter.Item.ApiItem("scrapTemplate()") {

                    // 스크랩 템플릿으로 카카오링크 보내기

                    // 공유할 웹페이지 URL
                    //  * 주의: 개발자사이트 Web 플랫폼 설정에 공유할 URL의 도메인이 등록되어 있어야 합니다.
                    val url = "https://developers.kakao.com"

                    LinkClient.instance.scrapTemplate(context, url) { linkResult, error ->
                        if (error != null) {
                            Log.e(TAG, "카카오링크 보내기 실패", error)
                        }
                        else if (linkResult != null) {
                            Log.d(TAG, "카카오링크 보내기 성공 ${linkResult.intent}")
                            startActivity(linkResult.intent)

                            // 카카오링크 보내기에 성공했지만 아래 경고 메시지가 존재할 경우 일부 컨텐츠가 정상 동작하지 않을 수 있습니다.
                            Log.w(TAG, "Warning Msg: ${linkResult.warningMsg}")
                            Log.w(TAG, "Argument Msg: ${linkResult.argumentMsg}")
                        }
                    }
                },
                ApiAdapter.Item.ApiItem("defaultTemplate() - feed") {

                    // 디폴트 템플릿으로 카카오링크 보내기 - Feed
                    LinkClient.instance.defaultTemplate(context, defaultFeed) { linkResult, error ->
                        if (error != null) {
                            Log.e(TAG, "카카오링크 보내기 실패", error)
                        }
                        else if (linkResult != null) {
                            Log.d(TAG, "카카오링크 보내기 성공 ${linkResult.intent}")
                            startActivity(linkResult.intent)

                            // 카카오링크 보내기에 성공했지만 아래 경고 메시지가 존재할 경우 일부 컨텐츠가 정상 동작하지 않을 수 있습니다.
                            Log.w(TAG, "Warning Msg: ${linkResult.warningMsg}")
                            Log.w(TAG, "Argument Msg: ${linkResult.argumentMsg}")
                        }
                    }
                },
                ApiAdapter.Item.ApiItem("defaultTemplate() - list") {

                    // 디폴트 템플릿으로 카카오링크 보내기 - List
                    LinkClient.instance.defaultTemplate(context, defaultList) { linkResult, error ->
                        if (error != null) {
                            Log.e(TAG, "카카오링크 보내기 실패", error)
                        }
                        else if (linkResult != null) {
                            Log.d(TAG, "카카오링크 보내기 성공 ${linkResult.intent}")
                            startActivity(linkResult.intent)

                            // 카카오링크 보내기에 성공했지만 아래 경고 메시지가 존재할 경우 일부 컨텐츠가 정상 동작하지 않을 수 있습니다.
                            Log.w(TAG, "Warning Msg: ${linkResult.warningMsg}")
                            Log.w(TAG, "Argument Msg: ${linkResult.argumentMsg}")
                        }
                    }
                },
                ApiAdapter.Item.ApiItem("defaultTemplate() - location") {

                    // 디폴트 템플릿으로 카카오링크 보내기 - Location
                    LinkClient.instance.defaultTemplate(context, defaultLocation) { linkResult, error ->
                        if (error != null) {
                            Log.e(TAG, "카카오링크 보내기 실패", error)
                        }
                        else if (linkResult != null) {
                            Log.d(TAG, "카카오링크 보내기 성공 ${linkResult.intent}")
                            startActivity(linkResult.intent)

                            // 카카오링크 보내기에 성공했지만 아래 경고 메시지가 존재할 경우 일부 컨텐츠가 정상 동작하지 않을 수 있습니다.
                            Log.w(TAG, "Warning Msg: ${linkResult.warningMsg}")
                            Log.w(TAG, "Argument Msg: ${linkResult.argumentMsg}")
                        }
                    }
                },
                ApiAdapter.Item.ApiItem("defaultTemplate() - commerce") {

                    // 디폴트 템플릿으로 카카오링크 보내기 - Commerce
                    LinkClient.instance.defaultTemplate(context, defaultCommerce) { linkResult, error ->
                        if (error != null) {
                            Log.e(TAG, "카카오링크 보내기 실패", error)
                        }
                        else if (linkResult != null) {
                            Log.d(TAG, "카카오링크 보내기 성공 ${linkResult.intent}")
                            startActivity(linkResult.intent)

                            // 카카오링크 보내기에 성공했지만 아래 경고 메시지가 존재할 경우 일부 컨텐츠가 정상 동작하지 않을 수 있습니다.
                            Log.w(TAG, "Warning Msg: ${linkResult.warningMsg}")
                            Log.w(TAG, "Argument Msg: ${linkResult.argumentMsg}")
                        }
                    }
                },
                ApiAdapter.Item.ApiItem("defaultTemplate() - text") {

                    // 디폴트 템플릿으로 카카오링크 보내기 - Text
                    LinkClient.instance.defaultTemplate(context, defaultText) { linkResult, error ->
                        if (error != null) {
                            Log.e(TAG, "카카오링크 보내기 실패", error)
                        }
                        else if (linkResult != null) {
                            Log.d(TAG, "카카오링크 보내기 성공 ${linkResult.intent}")
                            startActivity(linkResult.intent)

                            // 카카오링크 보내기에 성공했지만 아래 경고 메시지가 존재할 경우 일부 컨텐츠가 정상 동작하지 않을 수 있습니다.
                            Log.w(TAG, "Warning Msg: ${linkResult.warningMsg}")
                            Log.w(TAG, "Argument Msg: ${linkResult.argumentMsg}")
                        }
                    }
                },
                ApiAdapter.Item.ApiItem("customTemplateUri() - web sharer") {

                    // 커스텀 템플릿으로 웹에서 카카오링크 보내기

                    // 메시지 템플릿 아이디
                    //  * 만들기 가이드: https://developers.kakao.com/docs/latest/ko/message/message-template
                    val templateId = templateIds["customMemo"] as Long

                    val sharerUrl = WebSharerClient.instance.customTemplateUri(templateId)

                    KakaoCustomTabsClient.openWithDefault(context, sharerUrl)
                },
                ApiAdapter.Item.ApiItem("scrapTemplateUri() - web sharer") {

                    // 스크랩 템플릿으로 웹에서 카카오링크 보내기

                    // 공유할 웹페이지 URL
                    //  * 주의: 개발자사이트 Web 플랫폼 설정에 공유할 URL의 도메인이 등록되어 있어야 합니다.
                    val url = "https://developers.kakao.com"

                    val sharerUrl = WebSharerClient.instance.scrapTemplateUri(url)

                    KakaoCustomTabsClient.openWithDefault(context, sharerUrl)
                },
                ApiAdapter.Item.ApiItem("defaultTemplateUri() - web sharer - feed") {

                    // 디폴트 템플릿으로 웹에서 카카오링크 보내기 - Feed
                    val sharerUrl = WebSharerClient.instance.defaultTemplateUri(defaultFeed)

                    KakaoCustomTabsClient.openWithDefault(context, sharerUrl)
                },
                ApiAdapter.Item.ApiItem("defaultTemplateUri() - web sharer - location") {

                    // 디폴트 템플릿으로 웹에서 카카오링크 보내기 - Location
                    val sharerUrl = WebSharerClient.instance.defaultTemplateUri(defaultLocation)

                    KakaoCustomTabsClient.openWithDefault(context, sharerUrl)
                },
                ApiAdapter.Item.ApiItem("uploadImage()") {

                    // 이미지 업로드

                    // 로컬 이미지 파일
                    // 이 샘플에서는 프로젝트 리소스로 추가한 이미지 파일을 사용했습니다. 갤러리 등 서비스 니즈에 맞는 사진 파일을 준비하세요.
                    val bitmap = BitmapFactory.decodeResource(resources, R.drawable.sample1)
                    val file = File(context.cacheDir, "sample1.png")
                    val stream = FileOutputStream(file)
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                    stream.close()

                    // 카카오 이미지 서버로 업로드
                    LinkClient.instance.uploadImage(file) { imageUploadResult, error ->
                        if (error != null) {
                            Log.e(TAG, "이미지 업로드 실패", error)
                        }
                        else if (imageUploadResult != null) {
                            Log.i(TAG, "이미지 업로드 성공 \n${imageUploadResult.infos.original}")
                        }
                    }
                },
                ApiAdapter.Item.ApiItem("scrapImage()") {

                    // 이미지 스크랩

                    // 원본 원격 이미지 URL
                    val url = "https://t1.kakaocdn.net/kakaocorp/Service/KakaoTalk/pc/slide/talkpc_theme_01.jpg"

                    // 카카오 이미지 서버로 업로드
                    LinkClient.instance.scrapImage(url) { imageUploadResult, error ->
                        if (error != null) {
                            Log.e(TAG, "이미지 스크랩 실패", error)
                        }
                        else if (imageUploadResult != null) {
                            Log.i(TAG, "이미지 스크랩 성공 \n${imageUploadResult.infos.original}")
                        }
                    }
                },
                ApiAdapter.Item.Header("KakaoNavi API"),
                ApiAdapter.Item.ApiItem("isKakaoNaviInstalled()") {

                    // 카카오내비 설치여부 확인
                    if (NaviClient.instance.isKakaoNaviInstalled(context)) {
                        Log.i(TAG, "카카오내비 앱으로 길안내 가능")
                    } else {
                        Log.i(TAG, "카카오내비 미설치: 웹 길안내 사용 권장")
                    }
                },
                ApiAdapter.Item.ApiItem("shareDestinationIntent() - KATEC") {

                    // 카카오내비 앱으로 목적지 공유하기 - KATEC
                    startActivity(
                        NaviClient.instance.shareDestinationIntent(
                            Location("카카오 판교오피스", "321286", "533707")
                        )
                    )
                },
                ApiAdapter.Item.ApiItem("shareDestinationIntent() - WGS84") {

                    // 카카오내비 앱으로 목적지 공유하기 - WGS84
                    startActivity(
                        NaviClient.instance.shareDestinationIntent(
                            Location("카카오 판교오피스", "127.108640", "37.402111"),
                            NaviOption(coordType = CoordType.WGS84)
                        )
                    )
                },
                ApiAdapter.Item.ApiItem("navigateIntent() - KATEC") {

                    // 카카오내비 앱으로 길안내 - KATEC
                    startActivity(
                        NaviClient.instance.navigateIntent(
                            Location("카카오 판교오피스", "321286", "533707")
                        )
                    )
                },
                ApiAdapter.Item.ApiItem("navigateIntent() - WGS84") {

                    // 카카오내비 앱으로 길안내 - WGS84
                    startActivity(
                        NaviClient.instance.navigateIntent(
                            Location("카카오 판교오피스", "127.108640", "37.402111"),
                            NaviOption(coordType = CoordType.WGS84)
                        )
                    )
                },
                ApiAdapter.Item.ApiItem("navigateIntent() - KATEC - viaList") {

                    // 카카오내비 앱으로 길안내 - KATEC - 경유지 추가
                    startActivity(
                        NaviClient.instance.navigateIntent(
                            Location("카카오 판교오피스", "321286", "533707"),
                            viaList = listOf(
                                Location("판교역 1번출구", "321525", "532951")
                            )
                        )
                    )
                },
                ApiAdapter.Item.ApiItem("navigateIntent() - WGS84 - viaList") {

                    // 카카오내비 앱으로 길안내 - WGS84 - 경유지 추가
                    startActivity(
                        NaviClient.instance.navigateIntent(
                            Location("카카오 판교오피스", "127.108640", "37.402111"),
                            NaviOption(coordType = CoordType.WGS84),
                            listOf(
                                Location("판교역 1번출구", "127.111492", "37.395225")
                            )
                        )
                    )
                },
                ApiAdapter.Item.ApiItem("navigateWebUrl() - KATEC") {

                    // 웹 브라우저에서 길안내
                    // 카카오내비가 설치되지 않은 곳에서 활용할 수 있습니다.
                    val url =
                        NaviClient.instance.navigateWebUrl(
                            Location("카카오 판교오피스", "321286", "533707")
                        )

                    // CustomTabs 로 길안내
                    KakaoCustomTabsClient.openWithDefault(context, url)

                    // 또는 외부 브라우저
                    //startActivity(Intent(ACTION_VIEW, url))
                },
                ApiAdapter.Item.ApiItem("navigateWebUrl() - WGS84") {

                    // 웹 브라우저에서 길안내
                    // 카카오내비가 설치되지 않은 곳에서 활용할 수 있습니다.
                    val url =
                        NaviClient.instance.navigateWebUrl(
                            Location("카카오 판교오피스", "127.108640", "37.402111"),
                            NaviOption(coordType = CoordType.WGS84)
                        )

                    // CustomTabs 로 길안내
                    KakaoCustomTabsClient.openWithDefault(context, url)

                    // 또는 외부 브라우저
                    //startActivity(Intent(ACTION_VIEW, url))
                },
                ApiAdapter.Item.Header("Kakao Sync"),
                ApiAdapter.Item.ApiItem("login(serviceTerms:) - select one") {

                    // 약관 선택해 동의 받기

                    // 개발자사이트 간편가입 설정에 등록한 약관 목록 중, 동의 받기를 원하는 약관의 태그 값을 지정합니다.
                    val serviceTerms = listOf("service")

                    // serviceTerms 파라미터와 함께 카카오톡으로 로그인 요청 (카카오계정으로 로그인도 사용법 동일)
                    UserApiClient.instance.loginWithKakaoTalk(
                        context = context,
                        serviceTerms = serviceTerms
                    ) { token, error ->
                        if (error != null) {
                            Log.e(TAG, "로그인 실패", error)
                        }
                        else if (token != null) {
                            Log.i(TAG, "로그인 성공 ${token.accessToken}")
                        }
                    }
                },
                ApiAdapter.Item.ApiItem("login(serviceTerms:) - empty") {

                    // 약관 동의 받지 않기

                    // serviceTerms 파라미터에 empty list 전달해서 카카오톡으로 로그인 요청 (카카오계정으로 로그인도 사용법 동일)
                    UserApiClient.instance.loginWithKakaoTalk(
                        context = context,
                        serviceTerms = listOf()
                    ) { token, error ->
                        if (error != null) {
                            Log.e(TAG, "로그인 실패", error)
                        }
                        else if (token != null) {
                            Log.i(TAG, "로그인 성공 ${token.accessToken}")
                        }
                    }
                },
                ApiAdapter.Item.Header("ETC"),
                ApiAdapter.Item.ApiItem("Get Current Token") {

                    // 현재 토큰 저장소에서 토큰 가져오기
                    Log.i(TAG, "${TokenManagerProvider.instance.manager.getToken()}")
                },
                ApiAdapter.Item.ApiItem("Set Custom TokenManager") {

                    // 커스텀 토큰 저장소 설정
                    TokenManagerProvider.instance.manager = object: TokenManageable {
                        val preferences = context.getSharedPreferences("test_preferences", Context.MODE_PRIVATE)
                        val tokenKey = "test_token_key"

                        override fun getToken(): OAuthToken? =
                            preferences.getString(tokenKey, "")?.let {
                                KakaoJson.fromJson(it, OAuthToken::class.java)
                            }

                        override fun setToken(token: OAuthToken) {
                            Log.d(TAG, "토큰 암호화를 권장합니다.")
                            preferences.edit(true) {
                                putString(tokenKey, KakaoJson.toJson(token))
                            }
                        }

                        override fun clear() {
                            preferences.edit(true) {
                                remove(tokenKey)
                            }
                        }
                    }
                    Log.i(TAG, "커스텀 토큰 저장소 사용")
                },
                ApiAdapter.Item.ApiItem("Set Default TokenManager") {

                    // 기본 저장소 재설정
                    TokenManagerProvider.instance.manager = TokenManager.instance
                    Log.i(TAG, "use default token manager")
                },
                ApiAdapter.Item.ApiItem("hasToken() usage") {
                    if (AuthApiClient.instance.hasToken()) {
                        UserApiClient.instance.accessTokenInfo { _, error ->
                            if (error != null) {
                                if (error is KakaoSdkError && error.isInvalidTokenError() == true) {
                                    Log.e(TAG, "토큰이 만료되었습니다.", error)

                                }
                                else {
                                    Log.e(TAG, "토큰 정보를 가져오는데 실패했습니다.", error)
                                }

                                // 카카오계정으로 로그인
                                UserApiClient.instance.loginWithKakaoAccount(context) { token, error ->
                                    if (error != null) {
                                        Log.e(TAG, "로그인 실패", error)
                                    }
                                    else if (token != null) {
                                        Log.i(TAG, "로그인 성공 ${token.accessToken}")
                                    }
                                }
                            }
                            else {
                                Log.i(TAG, "토큰 유효성 체크 성공")
                            }
                        }
                    }
                    else {
                        Log.i(TAG, "토큰이 없습니다.")

                        // 카카오계정으로 로그인
                        UserApiClient.instance.loginWithKakaoAccount(context) { token, error ->
                            if (error != null) {
                                Log.e(TAG, "로그인 실패", error)
                            }
                            else if (token != null) {
                                Log.i(TAG, "로그인 성공 ${token.accessToken}")
                            }
                        }
                    }
                },
                ApiAdapter.Item.ApiItem("loginWithAccount()") {
                    UserApiClient.instance.loginWithKakaoAccount(context) { token, _ ->
                        Log.i(TAG, "${token?.accessToken}")
                    }
                },
                ApiAdapter.Item.ApiItem("me()") {
                    UserApiClient.instance.me() { user, _ ->
                        Log.i(TAG, "${user?.id} ${user?.kakaoAccount?.email}")
                    }
                },
                ApiAdapter.Item.ApiItem("logout()") {
                    UserApiClient.instance.logout {
                        Log.i(TAG, "logout")
                    }
                }
            )
        )
        recyclerView.apply {
            setHasFixedSize(true)
            addItemDecoration(
                DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
            )
            layoutManager = viewManager
            adapter = viewAdapter
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            templateIds = it
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_open, container, false)
    }

    private lateinit var templateIds: Bundle

    private var recursiveAppFriendsCompletion : ((Friends<Friend>?, Error?) -> Unit)? = null

    companion object {
        fun newInstance(templateIds: Bundle): OpenFragment =
            OpenFragment().apply {
                arguments = templateIds
            }

        val defaultFeed = FeedTemplate(
            content = Content(
                title = "딸기 치즈 케익",
                description = "#케익 #딸기 #삼평동 #카페 #분위기 #소개팅",
                imageUrl = "http://mud-kage.kakao.co.kr/dn/Q2iNx/btqgeRgV54P/VLdBs9cvyn8BJXB3o7N8UK/kakaolink40_original.png",
                link = Link(
                    webUrl = "https://developers.kakao.com",
                    mobileWebUrl = "https://developers.kakao.com"
                )
            ),
            social = Social(
                likeCount = 286,
                commentCount = 45,
                sharedCount = 845
            ),
            buttons = listOf(
                Button(
                    "웹으로 보기",
                    Link(
                        webUrl = "https://developers.kakao.com",
                        mobileWebUrl = "https://developers.kakao.com"
                    )
                ),
                Button(
                    "앱으로 보기",
                    Link(
                        androidExecutionParams = mapOf("key1" to "value1", "key2" to "value2"),
                        iosExecutionParams = mapOf("key1" to "value1", "key2" to "value2")
                    )
                )
            )
        )

        val defaultList = ListTemplate(
            headerTitle = "WEEKLY MAGAZINE",
            headerLink = Link(
                webUrl = "https://developers.kakao.com",
                mobileWebUrl = "https://developers.kakao.com"
            ),
            contents = listOf(
                Content(
                    title = "취미의 특징, 탁구",
                    description = "스포츠",
                    imageUrl = "http://mud-kage.kakao.co.kr/dn/bDPMIb/btqgeoTRQvd/49BuF1gNo6UXkdbKecx600/kakaolink40_original.png",
                    link = Link(
                        webUrl = "https://developers.kakao.com",
                        mobileWebUrl = "https://developers.kakao.com"
                    )
                ),
                Content(
                    title = "크림으로 이해하는 커피이야기",
                    description = "음식",
                    imageUrl = "http://mud-kage.kakao.co.kr/dn/QPeNt/btqgeSfSsCR/0QJIRuWTtkg4cYc57n8H80/kakaolink40_original.png",
                    link = Link(
                        webUrl = "https://developers.kakao.com",
                        mobileWebUrl = "https://developers.kakao.com"
                    )
                ),
                Content(
                    title = "감성이 가득한 분위기",
                    description = "사진",
                    imageUrl = "http://mud-kage.kakao.co.kr/dn/c7MBX4/btqgeRgWhBy/ZMLnndJFAqyUAnqu4sQHS0/kakaolink40_original.png",
                    link = Link(
                        webUrl = "https://developers.kakao.com",
                        mobileWebUrl = "https://developers.kakao.com"
                    )
                )
            ),
            buttons = listOf(
                Button(
                    "웹으로 보기",
                    Link(
                        webUrl = "https://developers.kakao.com",
                        mobileWebUrl = "https://developers.kakao.com"
                    )
                ),
                Button(
                    "앱으로 보기",
                    Link(
                        androidExecutionParams = mapOf("key1" to "value1", "key2" to "value2"),
                        iosExecutionParams = mapOf("key1" to "value1", "key2" to "value2")
                    )
                )
            )
        )

        val defaultLocation = LocationTemplate(
            address = "경기 성남시 분당구 판교역로 235 에이치스퀘어 N동 8층",
            addressTitle = "카카오 판교오피스 카페톡",
            content = Content(
                title = "신메뉴 출시❤️ 체리블라썸라떼",
                description = "이번 주는 체리블라썸라떼 1+1",
                imageUrl = "http://mud-kage.kakao.co.kr/dn/bSbH9w/btqgegaEDfW/vD9KKV0hEintg6bZT4v4WK/kakaolink40_original.png",
                link = Link(
                    webUrl = "https://developers.com",
                    mobileWebUrl = "https://developers.kakao.com"
                )
            ),
            social = Social(
                likeCount = 286,
                commentCount = 45,
                sharedCount = 845
            )
        )

        val defaultCommerce = CommerceTemplate(
            content = Content(
                title = "Ivory long dress (4 Color)",
                imageUrl = "http://mud-kage.kakao.co.kr/dn/RY8ZN/btqgOGzITp3/uCM1x2xu7GNfr7NS9QvEs0/kakaolink40_original.png",
                link = Link(
                    webUrl = "https://developers.kakao.com",
                    mobileWebUrl = "https://developers.kakao.com"
                )
            ),
            commerce = Commerce(
                regularPrice = 208800,
                discountPrice = 146160,
                discountRate = 30,
                productName = "Ivory long dress"
            ),
            buttons = listOf(
                Button(
                    "구매하기",
                    Link(
                        webUrl = "https://developers.kakao.com",
                        mobileWebUrl = "https://developers.kakao.com"
                    )
                ),
                Button(
                    "공유하기",
                    Link(
                        androidExecutionParams = mapOf("key1" to "value1", "key2" to "value2"),
                        iosExecutionParams = mapOf("key1" to "value1", "key2" to "value2")
                    )
                )
            )
        )

        val defaultText = TextTemplate(
            text = """
                카카오링크는 카카오 플랫폼 서비스의 대표 기능으로써 사용자의 모바일 기기에 설치된 카카오 플랫폼과 연동하여 다양한 기능을 실행할 수 있습니다.
                현재 이용할 수 있는 카카오링크는 다음과 같습니다.
                카카오톡링크
                카카오톡을 실행하여 사용자가 선택한 채팅방으로 메시지를 전송합니다.
                카카오스토리링크
                카카오스토리 글쓰기 화면으로 연결합니다.
            """.trimIndent(),
            link = Link(
                webUrl = "https://developers.kakao.com",
                mobileWebUrl = "https://developers.kakao.com"
            )
        )

    }
}
