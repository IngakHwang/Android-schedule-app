package com.kakao.sdk.common

import android.app.Application
import android.content.Context
import com.kakao.sdk.common.model.ApprovalType
import com.kakao.sdk.common.model.ServerHosts

/**
 * ReactiveX Kakao SDK 사용에 필요한 설정을 담고 있는 클래스.
 * 이 클래스에서 제공하는 [init] 함수를 사용해 SDK를 사용하기 전에 반드시 초기화 필요.
 *
 *  ```kotlin
 *  class MyApplication : Application {
 *      fun onCreate() {
 *          RxKakaoSdk.init(this, "${NATIVE_APP_KEY}")
 *      }
 *  }
 *  ```
 */
object RxKakaoSdk {

    /**
     * ReactiveX Kakao SDK 초기화. [Application.onCreate] 내에서 호출 권장.
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
        KakaoSdk.init(
            context,
            appKey,
            customScheme ?: "kakao$appKey",
            loggingEnabled ?: false,
            hosts ?: ServerHosts(),
            approvalType ?: ApprovalType(),
            KakaoSdk.Type.KOTLIN
        )
    }
}
