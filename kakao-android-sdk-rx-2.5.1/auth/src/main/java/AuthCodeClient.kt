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

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.ResultReceiver
import android.util.Base64
import com.kakao.sdk.auth.model.Prompt
import com.kakao.sdk.common.util.KakaoJson
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.common.util.SdkLog
import com.kakao.sdk.common.model.*
import com.kakao.sdk.common.util.IntentResolveClient
import java.net.HttpURLConnection
import java.security.MessageDigest
import java.util.UUID

/**
 * @suppress
 */
class AuthCodeClient (
    private val intentResolveClient: IntentResolveClient = IntentResolveClient.instance,
    private val applicationInfo: ApplicationInfo = KakaoSdk.applicationContextInfo,
    private val contextInfo: ContextInfo = KakaoSdk.applicationContextInfo,
    private val approvalType: ApprovalType = KakaoSdk.approvalType
) {

    fun isKakaoTalkLoginAvailable(context: Context): Boolean =
        intentResolveClient.resolveTalkIntent(context, AuthCodeIntentFactory.talkBase()) != null

    @JvmOverloads
    fun authorizeWithKakaoTalk(
        context: Context,
        requestCode: Int = DEFAULT_REQUEST_CODE,
        channelPublicIds: List<String>? = null,
        serviceTerms: List<String>? = null,
        codeVerifier: String? = null,
        callback: (code: String?, error: Throwable?) -> Unit
    ) {
        if (!isKakaoTalkLoginAvailable(context)) {
            callback(null, ClientError(ClientErrorCause.NotSupported, "KakaoTalk not installed"))
        }
        else {
            try {
                context.startActivity(
                    AuthCodeIntentFactory.talk(
                        context,
                        requestCode,
                        clientId = applicationInfo.appKey,
                        redirectUri = applicationInfo.redirectUri,
                        kaHeader = contextInfo.kaHeader,
                        extras = Bundle().apply {
                            channelPublicIds?.let { putString(Constants.CHANNEL_PUBLIC_ID, channelPublicIds.joinToString(",")) }
                            serviceTerms?.let { putString(Constants.SERVICE_TERMS, serviceTerms.joinToString(",")) }
                            approvalType.value?.let { putString(Constants.APPROVAL_TYPE, it) }
                            codeVerifier?.let {
                                putString(Constants.CODE_CHALLENGE, codeChallenge(it.toByteArray()))
                                putString(Constants.CODE_CHALLENGE_METHOD, Constants.CODE_CHALLENGE_METHOD_VALUE)
                            }
                        },
                        resultReceiver = resultReceiver(callback)
                    )
                )
            } catch (startActivityError: Throwable) {
                SdkLog.e(startActivityError)
                callback(null, startActivityError)
            }
        }
    }

    @JvmOverloads
    fun authorizeWithKakaoAccount(
        context: Context,
        prompts: List<Prompt>? = null,
        scopes: List<String>? = null,
        agt: String? = null,
        channelPublicIds: List<String>? = null,
        serviceTerms: List<String>? = null,
        forceAccountLogin: Boolean = false,
        accountParameters: Map<String, String>? = null,
        codeVerifier: String? = null,
        callback: (code: String?, error: Throwable?) -> Unit
    ) {
        val uriUtility = UriUtility()
        val uri =
            uriUtility.authorize(
                clientId = applicationInfo.appKey,
                agt = agt,
                redirectUri = applicationInfo.redirectUri,
                scopes = scopes,
                kaHeader = contextInfo.kaHeader,
                channelPublicIds = channelPublicIds,
                serviceTerms = serviceTerms,
                prompts = prompts,
                approvalType = approvalType.value,
                codeChallenge = codeVerifier?.let { codeChallenge(it.toByteArray()) },
                codeChallengeMethod = codeVerifier?.let { Constants.CODE_CHALLENGE_METHOD_VALUE }
            )
                .let { authorizeUri ->
                    if (forceAccountLogin && accountParameters != null)
                        uriUtility.accountLoginAndAuthorize(authorizeUri, accountParameters)
                    else
                        authorizeUri
                }
        SdkLog.i(uri)
        try {
            context.startActivity(
                AuthCodeIntentFactory.account(
                    context, uri, applicationInfo.redirectUri, resultReceiver(callback)
                )
            )
        } catch (startActivityError: Throwable) {
            SdkLog.e(startActivityError)
            callback(null, startActivityError)
        }
    }

    @JvmSynthetic
    internal fun resultReceiver(
        callback: (code: String?, error: Throwable?) -> Unit
    ): ResultReceiver =
        object: ResultReceiver(Handler(Looper.getMainLooper())) {
            override fun onReceiveResult(resultCode: Int, resultData: Bundle?) {
                SdkLog.d("***** AUTH CODE RESULT: $resultData")
                if (resultCode == Activity.RESULT_OK) {
                    val uri = resultData?.getParcelable<Uri>(Constants.KEY_URL)
                    val code = uri?.getQueryParameter(Constants.CODE)
                    if (code != null) {
                        callback(code, null)
                    } else {
                        // oauth spec
                        // error is nonNull, errorDescription is nullable
                        val error = uri?.getQueryParameter(Constants.ERROR)?.let { it } ?: Constants.UNKNOWN_ERROR
                        val errorDescription = uri?.getQueryParameter(Constants.ERROR_DESCRIPTION)

                        callback(null, AuthError(
                            HttpURLConnection.HTTP_MOVED_TEMP,
                            kotlin.runCatching {
                                KakaoJson.fromJson<AuthErrorCause>(error, AuthErrorCause::class.java)
                            }.getOrDefault(
                                AuthErrorCause.Unknown
                            ),
                            AuthErrorResponse(error, errorDescription)
                        ))
                    }

                } else if (resultCode == Activity.RESULT_CANCELED) {
                    var error = resultData?.getSerializable(Constants.KEY_EXCEPTION) as KakaoSdkError
                    callback(null, error)
                } else {
                    var error = IllegalArgumentException("Unknown resultCode in RxAuthCodeClient#onReceivedResult()")
                    callback(null, error)
                }
            }
        }

    companion object {

        @JvmStatic
        val instance by lazy { AuthCodeClient() }

        const val DEFAULT_REQUEST_CODE: Int = 10012

        fun codeVerifier(): String =
            Base64.encodeToString(
                MessageDigest.getInstance(Constants.CODE_VERIFIER_ALGORITHM).digest(
                    UUID.randomUUID().toString().toByteArray()
                ),
                Base64.NO_WRAP or Base64.NO_PADDING
            )

        fun codeChallenge(codeVerifier: ByteArray): String =
            Base64.encodeToString(
                MessageDigest.getInstance(Constants.CODE_CHALLENGE_ALGORITHM).digest(codeVerifier),
                Base64.NO_WRAP or Base64.NO_PADDING or Base64.URL_SAFE
            )
    }
}