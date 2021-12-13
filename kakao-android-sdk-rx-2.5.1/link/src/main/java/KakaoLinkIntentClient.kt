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
import android.content.Intent
import android.net.Uri
import com.google.gson.JsonObject
import com.kakao.sdk.common.*
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.common.util.KakaoJson
import com.kakao.sdk.common.model.ApplicationInfo
import com.kakao.sdk.common.model.ContextInfo
import com.kakao.sdk.link.model.ValidationResult
import com.kakao.sdk.common.util.IntentResolveClient
import com.kakao.sdk.common.util.SdkLog
import com.kakao.sdk.link.model.KakaoLinkAttachment
import com.kakao.sdk.link.model.LinkResult

/**
 * @suppress
 */
class KakaoLinkIntentClient(
    val contextInfo: ContextInfo = KakaoSdk.applicationContextInfo,
    val applicationInfo: ApplicationInfo = KakaoSdk.applicationContextInfo,
    val intentResolveClient: IntentResolveClient = IntentResolveClient.instance
) {
    @JvmOverloads
    fun linkResultFromResponse(
        context: Context,
        response: ValidationResult,
        serverCallbackArgs: Map<String, String>?,
        appKey: String = applicationInfo.appKey,
        appVer: String = contextInfo.appVer
    ): LinkResult {
        val attachmentSize = attachmentSize(appKey, response, serverCallbackArgs)
        if (attachmentSize > Constants.LINK_URI_LIMIT) {
            throw ClientError(
                ClientErrorCause.BadParameter,
                "KakaoLink intent size is $attachmentSize bytes. It should be less than ${Constants.LINK_URI_LIMIT} bytes."
            )
        }
        val linkUri = linkUriBuilder()
                .appendQueryParameter(Constants.LINKVER, Constants.LINKVER_40)
                .appendQueryParameter(Constants.APP_KEY, appKey)
                .appendQueryParameter(Constants.APP_VER, appVer)
                .appendQueryParameter(Constants.TEMPLATE_ID, response.templateId.toString())
                .appendQueryParameter(Constants.TEMPLATE_ARGS, response.templateArgs.toString())
                .appendQueryParameter(Constants.TEMPLATE_JSON, response.templateMsg.toString())
                .appendQueryParameter(
                    Constants.EXTRAS,
                    extrasWithServerCallbacks(contextInfo.extras, serverCallbackArgs).toString()
                )
                .build()
        SdkLog.i(linkUri)
        val linkIntent = Intent(Intent.ACTION_SEND, linkUri)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        return LinkResult(
            intentResolveClient.resolveTalkIntent(context, linkIntent) ?: throw ClientError(
                ClientErrorCause.NotSupported,
                "Kakaotalk not installed"
            ),
            // TODO: 데이터에 String타입 아닌 값 등 이상 있을때 어떻게 동작하는지 확인 (minor)
            KakaoJson.fromJson(response.warningMsg.toString(), Map::class.java),
            KakaoJson.fromJson(response.argumentMsg.toString(), Map::class.java)
        )
    }

    fun isKakaoLinkAvailable(context: Context): Boolean =
        intentResolveClient.resolveTalkIntent(context, Intent(Intent.ACTION_VIEW, linkUriBuilder().build())) != null

    private fun linkUriBuilder() =
        Uri.Builder().scheme(Constants.LINK_SCHEME).authority(Constants.LINK_AUTHORITY)

    private fun attachmentSize(appKey: String, response: ValidationResult, serverCallbackArgs: Map<String, String>?): Int {
        val attachment = KakaoLinkAttachment(
            ak = appKey,
            P = response.templateMsg["P"].asJsonObject,
            C = response.templateMsg["C"].asJsonObject,
            ti = response.templateId,
            ta = response.templateArgs,
            extras = extrasWithServerCallbacks(contextInfo.extras, serverCallbackArgs)
        )
        return KakaoJson.toJson(attachment).length
    }

    private fun extrasWithServerCallbacks(
        extras: JsonObject,
        serverCallbackArgs: Map<String, String>?
    ): JsonObject {
        val clone = extras.deepCopy()
        if (serverCallbackArgs == null) return clone
        clone.addProperty(Constants.LCBA, KakaoJson.toJson(serverCallbackArgs))
        return clone
    }

    companion object {
        val instance by lazy { KakaoLinkIntentClient() }
    }
}