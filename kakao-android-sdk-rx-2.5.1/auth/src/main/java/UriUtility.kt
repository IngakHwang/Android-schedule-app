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
package com.kakao.sdk.auth

import android.net.Uri
import com.google.gson.annotations.SerializedName
import com.kakao.sdk.auth.model.Prompt
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.common.model.ServerHosts

/**
 * @suppress
 */
class UriUtility(
    private val hosts: ServerHosts = KakaoSdk.hosts
) {

    fun authorize(
        clientId: String,
        agt: String? = null,
        redirectUri: String,
        scopes: List<String>? = null,
        kaHeader: String? = null,
        channelPublicIds: List<String>? = null,
        serviceTerms: List<String>? = null,
        prompts: List<Prompt>? = null,
        approvalType: String? = null,
        codeChallenge: String? = null,
        codeChallengeMethod: String? = null
    ): Uri =
        Uri.Builder()
            .scheme(com.kakao.sdk.common.Constants.SCHEME)
            .authority(hosts.kauth).path(Constants.AUTHORIZE_PATH)
            .appendQueryParameter(Constants.CLIENT_ID, clientId)
            .appendQueryParameter(Constants.REDIRECT_URI, redirectUri)
            .appendQueryParameter(Constants.RESPONSE_TYPE, Constants.CODE).apply {
                agt?.let { appendQueryParameter(Constants.AGT, agt) }
                if (!scopes.isNullOrEmpty()) {
                    appendQueryParameter(Constants.SCOPE, scopes.joinToString(","))
                }
                channelPublicIds?.let { appendQueryParameter(Constants.CHANNEL_PUBLIC_ID, channelPublicIds.joinToString(",")) }
                serviceTerms?.let { appendQueryParameter(Constants.SERVICE_TERMS, serviceTerms.joinToString(",")) }
                prompts?.let { prompts ->
                    appendQueryParameter(
                        Constants.PROMPT,
                        prompts.joinToString(",") { prompt ->
                            prompt.javaClass.getField(prompt.name).getAnnotation(SerializedName::class.java).value
                        }
                    )
                }
                approvalType?.let { appendQueryParameter(Constants.APPROVAL_TYPE, it) }
                codeChallenge?.let { appendQueryParameter(Constants.CODE_CHALLENGE, it) }
                codeChallengeMethod?.let { appendQueryParameter(Constants.CODE_CHALLENGE_METHOD, it) }
            }
            .appendQueryParameter(Constants.KA_HEADER, kaHeader)
            .build()

    fun accountLoginAndAuthorize(
        authorizeUri: Uri,
        accountParameters: Map<String, String>? = null
    ): Uri =
        Uri.Builder()
            .scheme(com.kakao.sdk.common.Constants.SCHEME)
            .authority(hosts.mobileAccount)
            .path(Constants.ACCOUNT_LOGIN_PATH)
            .appendQueryParameter(Constants.ACCOUNT_LOGIN_PARAM_CONTINUE, authorizeUri.toString())
            .apply {
                accountParameters?.forEach {
                    appendQueryParameter(it.key, it.value)
                }
            }.build()
}