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
package com.kakao.sdk.common

import android.app.Application
import android.content.Context
import com.kakao.sdk.common.model.ApplicationContextInfo
import com.kakao.sdk.common.model.ApprovalType
import com.kakao.sdk.common.model.ServerHosts

/**
 * Kakao SDK 사용에 필요한 설정을 담고 있는 클래스.
 * 이 클래스에서 제공하는 [init] 함수를 사용해 SDK를 사용하기 전에 반드시 초기화 필요.
 *
 *  ```kotlin
 *  class MyApplication : Application {
 *      fun onCreate() {
 *          KakaoSdk.init(this, "${NATIVE_APP_KEY}")
 *      }
 *  }
 *  ```
 */
object KakaoSdk {
    lateinit var applicationContextInfo: ApplicationContextInfo
    lateinit var hosts: ServerHosts
    var loggingEnabled: Boolean = false
    lateinit var type: Type
    lateinit var approvalType: ApprovalType

    /**
     * Kakao SDK 초기화. [Application.onCreate] 내에서 호출 권장.
     *
     * @param context Android Context
     * @param appKey Kakao Native App Key
     */
    @JvmStatic
    @JvmOverloads
    fun init(
        context: Context,
        appKey: String,
        customScheme: String? = null,
        loggingEnabled: Boolean? = null,
        hosts: ServerHosts? = null,
        approvalType: ApprovalType? = null
    ) {
        init(
            context,
            appKey,
            customScheme ?: "kakao$appKey",
            loggingEnabled ?: false,
            hosts ?: ServerHosts(),
            approvalType ?: ApprovalType(),
            Type.KOTLIN
        )
    }

    val appKey: String
        get() = applicationContextInfo.appKey

    val redirectUri: String
        get() = applicationContextInfo.redirectUri

    val kaHeader: String
        get() = applicationContextInfo.kaHeader

    val keyHash: String
        get() = applicationContextInfo.signingKeyHash

    fun init(context: Context, appKey: String, customScheme: String, loggingEnabled: Boolean, hosts: ServerHosts, approvalType: ApprovalType, type: Type) {
        this.hosts = hosts
        this.loggingEnabled = loggingEnabled
        this.type = type
        this.approvalType = approvalType
        applicationContextInfo = ApplicationContextInfo(context, appKey, customScheme, type)
    }

    enum class Type {
        KOTLIN,
        RX_KOTLIN
    }
}