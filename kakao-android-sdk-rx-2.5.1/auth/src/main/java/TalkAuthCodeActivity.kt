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
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.ResultReceiver
import java.lang.IllegalArgumentException
import com.kakao.sdk.common.model.*
import com.kakao.sdk.common.util.KakaoJson
import com.kakao.sdk.common.util.SdkLog
import com.kakao.sdk.v2.auth.R
import java.net.HttpURLConnection

/**
 * @suppress
 */
class TalkAuthCodeActivity : AppCompatActivity() {

    private lateinit var resultReceiver: ResultReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_talk_auth_code)

        try {
            val extras = intent.extras ?: throw IllegalArgumentException("no extras.")
            resultReceiver = extras.getParcelable<ResultReceiver>(Constants.KEY_RESULT_RECEIVER) as ResultReceiver
            val requestCode = extras.getInt(Constants.KEY_REQUEST_CODE)
            SdkLog.i("requestCode: $requestCode")
            val loginIntent = extras.getParcelable<Intent>(Constants.KEY_LOGIN_INTENT)
            SdkLog.i("loginIntent:")
            loginIntent?.extras?.run {
                SdkLog.i("\t${Constants.EXTRA_APPLICATION_KEY} : ${getString(Constants.EXTRA_APPLICATION_KEY)}")
                SdkLog.i("\t${Constants.EXTRA_REDIRECT_URI} : ${getString(Constants.EXTRA_REDIRECT_URI)}")
                SdkLog.i("\t${Constants.EXTRA_KA_HEADER} : ${getString(Constants.EXTRA_KA_HEADER)}")
                getBundle(Constants.EXTRA_EXTRAPARAMS)?.run {
                    SdkLog.i("\t${Constants.EXTRA_EXTRAPARAMS}")
                    keySet().map { "\t\t$it : ${get(it)}" }.forEach { SdkLog.i(it) }
                }
            }
            startActivityForResult(loginIntent, requestCode)
        } catch (e: Throwable) {
            SdkLog.e(e)
            sendError(ClientError(ClientErrorCause.Unknown).apply { initCause(e) })
        }
    }

    private fun sendError(exception: KakaoSdkError) {
        if (this::resultReceiver.isInitialized) {
            resultReceiver.send(
                Activity.RESULT_CANCELED,
                Bundle().apply { putSerializable(Constants.KEY_EXCEPTION, exception) }
            )
        }
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val bundle = Bundle()
        if (data == null || resultCode == Activity.RESULT_CANCELED) {
            sendError(ClientError(ClientErrorCause.Cancelled))
            return
        }
        if (resultCode == Activity.RESULT_OK) {
            val extras = data.extras
            if (extras == null) {
                // no result returned from kakaotalk
                sendError(
                    ClientError(
                        ClientErrorCause.Unknown,
                        "No result from KakaoTalk."
                    )
                )
                return
            }
            val error = extras.getString(EXTRA_ERROR_TYPE)
            val errorDescription = extras.getString(EXTRA_ERROR_DESCRIPTION)
            if (error == "access_denied") {
                sendError(ClientError(ClientErrorCause.Cancelled))
                return
            }
            if (error != null) {
                val cause = KakaoJson.fromJson(error, AuthErrorCause::class.java)
                    ?: AuthErrorCause.Unknown
                sendError(
                    AuthError(
                        HttpURLConnection.HTTP_MOVED_TEMP,
                        cause,
                        AuthErrorResponse(
                            error,
                            errorDescription ?: "no error description"
                        )
                    )
                )
                return
            }
            bundle.putParcelable(
                Constants.KEY_URL,
                Uri.parse(extras[Constants.EXTRA_REDIRECT_URL] as String)
            )
            resultReceiver.send(Activity.RESULT_OK, bundle)
            finish()
            overridePendingTransition(0, 0)
            return
        }
        throw IllegalArgumentException("")
    }

    val EXTRA_ERROR_TYPE = "com.kakao.sdk.talk.error.type"
    val EXTRA_ERROR_DESCRIPTION = "com.kakao.sdk.talk.error.description"

    val NOT_SUPPORT_ERROR = "NotSupportError" // KakaoTalk installed but not signed up
    val UNKNOWN_ERROR = "UnknownError" // No redirect url
    val PROTOCOL_ERROR = "ProtocolError" // Wrong parameters provided
    val APPLICATION_ERROR = "ApplicationError" // Empty redirect url
    val AUTH_CODE_ERROR = "AuthCodeError"
    val CLIENT_INFO_ERROR = "ClientInfoError" // Could not fetch app info

}
