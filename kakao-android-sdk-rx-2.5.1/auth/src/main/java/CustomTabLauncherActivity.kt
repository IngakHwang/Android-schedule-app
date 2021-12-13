/*
  Copyright 2021 Kakao Corp.

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
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.ServiceConnection
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.ResultReceiver
import androidx.appcompat.app.AppCompatActivity
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.common.model.KakaoSdkError
import com.kakao.sdk.common.util.KakaoCustomTabsClient
import com.kakao.sdk.common.util.SdkLog

/**
 * @suppress
 */

open class CustomTabLauncherActivity : AppCompatActivity() {

    private lateinit var resultReceiver: ResultReceiver
    private lateinit var fullUri: Uri
    private var customTabsOpened = false
    private var customTabsConnection: ServiceConnection? = null
    private var internalHandler : Handler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadData(intent = intent)
    }

    open fun loadData(intent: Intent) {
        try {
            intent.extras?.getBundle(com.kakao.sdk.auth.Constants.KEY_BUNDLE)
                ?.run {
                    resultReceiver = getParcelable<ResultReceiver>(com.kakao.sdk.auth.Constants.KEY_RESULT_RECEIVER) as ResultReceiver
                    fullUri = getParcelable<Uri>(com.kakao.sdk.auth.Constants.KEY_FULL_URI) as Uri
                }

            // Redirection  onResume() -> onNewIntent() 호출 시 처리 위함
            internalHandler = Handler(Looper.getMainLooper()) {
                SdkLog.i("handle delay message")
                sendError(ClientError(ClientErrorCause.Cancelled, "cancelled."))
                true
            }

        } catch (e: Throwable) {
            SdkLog.e(e)
            sendError(ClientError(ClientErrorCause.Unknown).apply { initCause(e) })
        }
    }

    override fun onResume() {
        super.onResume()

        if (!customTabsOpened) {
            customTabsOpened = true

            if (this::fullUri.isInitialized) {
                openChromeCustomTab(fullUri)
            } else {
                sendError(ClientError(ClientErrorCause.IllegalState, "url has been not initialized."))
            }
        } else {
            SdkLog.i("trigger delay message")
            internalHandler?.hasMessages(0).let {
                if (it == false) {
                    internalHandler?.sendEmptyMessageDelayed(0,100)
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        SdkLog.i("onNewIntent")
        setIntent(intent)
        internalHandler?.hasMessages(0).let {
            if (it == true) {
                internalHandler?.removeMessages(0)
            }
        }

        intent?.data?.let {
            sendOK(it)
        }
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        customTabsConnection?.let { unbindService(it) }
    }

    private fun openChromeCustomTab(uri: Uri) {
        SdkLog.i("Authorize Uri: $uri")
        try {
            customTabsConnection = KakaoCustomTabsClient.openWithDefault(this, uri)
        } catch (e: UnsupportedOperationException) {
            SdkLog.w(e)
            try {
                KakaoCustomTabsClient.open(this, uri)
            } catch (e: ActivityNotFoundException) {
                SdkLog.w(e)
                sendError(ClientError(ClientErrorCause.NotSupported, "No browser has been installed on a device."))
            }

        }
    }

    private fun sendError(exception: KakaoSdkError) {
        if (this::resultReceiver.isInitialized) {
            resultReceiver.send(
                Activity.RESULT_CANCELED,
                Bundle().apply { putSerializable(com.kakao.sdk.auth.Constants.KEY_EXCEPTION, exception) }
            )
        }
        finish()
    }

    private fun sendOK(uri: Uri) {
        if (this::resultReceiver.isInitialized) {
            resultReceiver.send(
                Activity.RESULT_OK,
                Bundle().apply { putParcelable(com.kakao.sdk.auth.Constants.KEY_URL, uri) }
            )
        }
        finish()
    }
}