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
@file:JvmName("AuthCodeClientKt")

package com.kakao.sdk.auth

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.ResultReceiver
import com.kakao.sdk.auth.AuthCodeClient.Companion.codeChallenge
import com.kakao.sdk.auth.model.Prompt
import com.kakao.sdk.common.util.KakaoJson
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.common.util.SdkLog
import com.kakao.sdk.common.model.*
import com.kakao.sdk.common.util.IntentResolveClient
import io.reactivex.Single
import io.reactivex.SingleEmitter
import io.reactivex.SingleTransformer
import java.net.HttpURLConnection

/**
 * @suppress
 */
class RxAuthCodeClient(
    private val intentResolveClient: IntentResolveClient = IntentResolveClient.instance,
    private val applicationInfo: ApplicationInfo = KakaoSdk.applicationContextInfo,
    private val contextInfo: ContextInfo = KakaoSdk.applicationContextInfo,
    private val approvalType: ApprovalType = KakaoSdk.approvalType
) {

    @JvmOverloads
    fun authorizeWithKakaoTalk(
        context: Context,
        requestCode: Int = AuthCodeClient.DEFAULT_REQUEST_CODE,
        channelPublicIds: List<String>? = null,
        serviceTerms: List<String>? = null,
        codeVerifier: String? = null
    ): Single<String> =
        Single.create<String> { emitter ->
            if (intentResolveClient.resolveTalkIntent(context, AuthCodeIntentFactory.talkBase()) == null) {
                emitter.onError(
                    ClientError(ClientErrorCause.NotSupported, "KakaoTalk not installed")
                )
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
                            resultReceiver = resultReceiver(emitter)
                        )
                    )
                } catch (startActivityError: Throwable) {
                    emitter.onError(startActivityError)
                }
            }
        }.compose(handleAuthCodeError())

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
        codeVerifier: String? = null
    ): Single<String> =
        Single.create<String> { emitter ->
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
                        if (forceAccountLogin)
                            uriUtility.accountLoginAndAuthorize(authorizeUri, accountParameters)
                        else
                            authorizeUri
                    }
            SdkLog.i(uri)
            try {
                context.startActivity(
                    AuthCodeIntentFactory.account(
                        context, uri, applicationInfo.redirectUri, resultReceiver(emitter)
                    )
                )
            } catch (startActivityError: Throwable) {
                emitter.onError(startActivityError)
            }
        }.compose(handleAuthCodeError())

    @JvmSynthetic
    internal fun resultReceiver(
        emitter: SingleEmitter<String>
    ): ResultReceiver =
        object : ResultReceiver(Handler(Looper.getMainLooper())) {
            override fun onReceiveResult(resultCode: Int, resultData: Bundle?) {
                this@RxAuthCodeClient.onReceivedResult(resultCode, resultData, emitter)
            }
        }

    @JvmSynthetic
    internal fun onReceivedResult(
        resultCode: Int,
        resultData: Bundle?,
        emitter: SingleEmitter<String>
    ) {
        when (resultCode) {
            Activity.RESULT_OK -> {
                val uri = resultData?.getParcelable<Uri>(Constants.KEY_URL)
                val code = uri?.getQueryParameter(Constants.CODE)
                if (code != null) {
                    emitter.onSuccess(code)
                    return
                }
                val error = uri?.getQueryParameter(Constants.ERROR) as String
                val errorDescription = uri.getQueryParameter(Constants.ERROR_DESCRIPTION) as String
                emitter.onError(
                    AuthError(
                        HttpURLConnection.HTTP_MOVED_TEMP,
                        kotlin.runCatching {
                            KakaoJson.fromJson<AuthErrorCause>(error, AuthErrorCause::class.java)
                        }.getOrDefault(
                            AuthErrorCause.Unknown
                        ),
                        AuthErrorResponse(error, errorDescription)
                    )
                )
            }
            Activity.RESULT_CANCELED -> {
                val exception =
                    resultData?.getSerializable(Constants.KEY_EXCEPTION)
                            as KakaoSdkError
                emitter.onError(exception)
            }
            else -> throw IllegalArgumentException("Unknown resultCode in RxAuthCodeClient#onReceivedResult()")
        }
    }

    @JvmSynthetic
    internal fun <T> handleAuthCodeError(): SingleTransformer<T, T> = SingleTransformer {
        it.doOnError { SdkLog.e(it) }
            .doOnSuccess { SdkLog.i(it!!) }
    }

    companion object {
        @JvmStatic
        val instance by lazy { AuthCodeClient.rx }
    }
}

val AuthCodeClient.Companion.rx by lazy { RxAuthCodeClient() }