package com.kakao.sdk.auth

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.ResultReceiver
import com.kakao.sdk.common.model.ApprovalType

/**
 * @suppress
 */
object AuthCodeIntentFactory {

    fun talkBase() =
        Intent(Constants.CAPRI_LOGGED_IN_ACTIVITY).addCategory(Intent.CATEGORY_DEFAULT)

    fun talk(
        context: Context,
        requestCode: Int,
        clientId: String,
        redirectUri: String,
        kaHeader: String,
        extras: Bundle,
        resultReceiver: ResultReceiver
    ): Intent =
        Intent(context, TalkAuthCodeActivity::class.java)
            .putExtra(Constants.KEY_LOGIN_INTENT,
                talkBase()
                    .putExtra(Constants.EXTRA_APPLICATION_KEY, clientId)
                    .putExtra(Constants.EXTRA_REDIRECT_URI, redirectUri)
                    .putExtra(Constants.EXTRA_KA_HEADER, kaHeader)
                    .putExtra(Constants.EXTRA_EXTRAPARAMS, extras)
            )
            .putExtra(Constants.KEY_REQUEST_CODE, requestCode)
            .putExtra(Constants.KEY_RESULT_RECEIVER, resultReceiver)

    fun account(
        context: Context,
        fullUri: Uri,
        redirectUri: String,
        resultReceiver: ResultReceiver
    ): Intent =
        Intent(context, AuthCodeHandlerActivity::class.java)
            .putExtra(Constants.KEY_BUNDLE, Bundle().apply {
                putParcelable(Constants.KEY_RESULT_RECEIVER, resultReceiver)
                putParcelable(Constants.KEY_FULL_URI, fullUri)
                putString(Constants.KEY_REDIRECT_URI, redirectUri)
            })
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)    // (동적동의 시 SDK가 갖고 있던) Application Context 로 Activity를 띄우기 위해 설정
}