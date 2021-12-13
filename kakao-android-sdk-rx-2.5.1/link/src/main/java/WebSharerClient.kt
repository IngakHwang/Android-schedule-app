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

import android.net.Uri
import com.google.gson.JsonObject
import com.kakao.sdk.common.model.ApplicationInfo
import com.kakao.sdk.common.Constants as CommonConstants
import com.kakao.sdk.common.model.ContextInfo
import com.kakao.sdk.common.util.KakaoJson
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.template.model.DefaultTemplate

class WebSharerClient(
    private val contextInfo: ContextInfo = KakaoSdk.applicationContextInfo,
    private val applicationInfo: ApplicationInfo = KakaoSdk.applicationContextInfo
) {

    @JvmOverloads
    fun customTemplateUri(
        templateId: Long,
        templateArgs: Map<String, String>? = null,
        serverCallbackArgs: Map<String, String>? = null
    ): Uri {
        val validationParams = JsonObject().apply {
            addProperty(Constants.TEMPLATE_ID, templateId)
            templateArgs?.let {
                addProperty(
                    Constants.TEMPLATE_ARGS,
                    KakaoJson.toJson(templateArgs)
                )
            }
            addProperty(Constants.LINK_VER, Constants.LINKVER_40)
        }
        val builder = baseUriBuilder(serverCallbackArgs)
            .appendQueryParameter(Constants.VALIDATION_ACTION, Constants.VALIDATION_CUSTOM)
            .appendQueryParameter(Constants.VALIDATION_PARAMS, validationParams.toString())
        return builder.build()
    }

    @JvmOverloads
    fun scrapTemplateUri(
        requestUrl: String,
        templateId: Long? = null,
        templateArgs: Map<String, String>? = null,
        serverCallbackArgs: Map<String, String>? = null
    ): Uri {
        val validationParams = JsonObject().apply {
            addProperty(Constants.REQUEST_URL, requestUrl)
            templateId?.let { addProperty(Constants.TEMPLATE_ID, it) }
            templateArgs?.let {
                addProperty(
                    Constants.TEMPLATE_ARGS,
                    KakaoJson.toJson(it)
                )
            }
            addProperty(Constants.LINK_VER, Constants.LINKVER_40)
        }
        val builder = baseUriBuilder(serverCallbackArgs)
            .appendQueryParameter(Constants.VALIDATION_ACTION, Constants.VALIDATION_SCRAP)
            .appendQueryParameter(Constants.VALIDATION_PARAMS, validationParams.toString())
        return builder.build()
    }

    @JvmOverloads
    fun defaultTemplateUri(
        template: DefaultTemplate,
        serverCallbackArgs: Map<String, String>? = null
    ): Uri {
        val validationParams = JsonObject().apply {
            add(Constants.TEMPLATE_OBJECT, KakaoJson.base.toJsonTree(template))
            addProperty(Constants.LINK_VER, Constants.LINKVER_40)
        }
        val builder = baseUriBuilder(serverCallbackArgs)
            .appendQueryParameter(Constants.VALIDATION_ACTION, Constants.VALIDATION_DEFAULT)
            .appendQueryParameter(Constants.VALIDATION_PARAMS, validationParams.toString())
        return builder.build()
    }

    private fun baseUriBuilder(serverCallbackArgs: Map<String, String>?): Uri.Builder {
        val builder = Uri.Builder().scheme(CommonConstants.SCHEME)
            .authority(KakaoSdk.hosts.sharer)
            .path(Constants.SHARER_PATH)
            .appendQueryParameter(Constants.SHARER_APP_KEY, applicationInfo.appKey)
            .appendQueryParameter(Constants.SHARER_KA, contextInfo.kaHeader)
        serverCallbackArgs?.let {
            builder.appendQueryParameter(
                Constants.LCBA,
                KakaoJson.toJson(serverCallbackArgs)
            )
        }
        return builder
    }

    companion object {
        @JvmStatic
        val instance by lazy { WebSharerClient() }
    }
}