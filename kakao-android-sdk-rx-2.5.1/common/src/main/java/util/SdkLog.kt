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
package com.kakao.sdk.common.util

import com.kakao.sdk.v2.common.BuildConfig
import com.kakao.sdk.common.KakaoSdk
import java.text.SimpleDateFormat
import java.util.*

/**
 * 로그 레벨
 */
enum class SdkLogLevel(val level: Int, val symbol: String) {
    V(0, "[\uD83D\uDCAC]"),
    D(1, "[ℹ️]"),
    I(2, "[\uD83D\uDD2C]"),
    W(3, "[⚠️]"),
    E(4, "[‼️]")
}

/**
 * SDK 내부에서 발생하는 로그
 */
class SdkLog(
    private val enabled: Boolean = KakaoSdk.loggingEnabled
) {
    private val logs: LinkedList<String> by lazy { LinkedList<String>() }
    private val dateFormat by lazy { SimpleDateFormat("MM-dd HH:mm:ss.SSS") }

    companion object {
        const val MAX_SIZE = 100

        /**
         * 현재까지 발생한 SDK 내부 로그 (최대 100건)
         */
        @JvmStatic
        fun log(): String =
            """
                ==== sdk version: ${BuildConfig.VERSION_NAME}
                ==== app version: ${KakaoSdk.applicationContextInfo.appVer}
            """
                .trimIndent()
                .plus(instance.logs.joinToString("\n", "\n"))

        /** @suppress */
        fun v(logged: Any?) = instance.log(logged, SdkLogLevel.V)
        /** @suppress */
        fun d(logged: Any?) = instance.log(logged, SdkLogLevel.D)
        /** @suppress */
        fun i(logged: Any?) = instance.log(logged, SdkLogLevel.I)
        /** @suppress */
        fun w(logged: Any?) = instance.log(logged, SdkLogLevel.W)
        /** @suppress */
        fun e(logged: Any?) = instance.log(logged, SdkLogLevel.E)

        @JvmStatic
        val instance by lazy { SdkLog() }
    }

    private fun log(logged: Any?, logLevel: SdkLogLevel) {
        val loggedObject = "${logLevel.symbol} $logged"
        if (BuildConfig.DEBUG) {
            println(loggedObject)
        }

        if (enabled && logLevel >= SdkLogLevel.I) {
            logs.add("${dateFormat.format(Date())} $loggedObject")
            if (logs.size > MAX_SIZE) {
                logs.poll()
            }
        }
    }
}
