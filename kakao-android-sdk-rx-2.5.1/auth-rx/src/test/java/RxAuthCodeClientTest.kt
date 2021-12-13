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
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ApprovalType
import com.kakao.sdk.common.model.AuthError
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import io.reactivex.Single
import io.reactivex.observers.TestObserver
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import java.util.*

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.P])
class RxAuthCodeClientTest {
    lateinit var authCodeClient: RxAuthCodeClient
    lateinit var observer: TestObserver<String>
    lateinit var bundle: Bundle

    @Before
    fun setup() {
        val testToken =
            OAuthToken("test_access_token", Date(), "test_refresh_token", Date())
        authCodeClient = RxAuthCodeClient(
            applicationInfo = TestApplicationInfo(),
            contextInfo = TestContextInfo(),
            approvalType = ApprovalType()
        )
        observer = TestObserver()
        bundle = Bundle()
    }

    @Test
    fun onReceivedResult() {
        val bundle = Bundle()
        bundle.putParcelable(
            Constants.KEY_URL,
            TestUriUtility.successfulRedirectUri()
        )
        Single.create<String> {
            authCodeClient.onReceivedResult(Activity.RESULT_OK, bundle, it)
        }.subscribe(observer)

        observer.assertNoErrors()
        observer.assertValue {
            it == "authorization_code"
        }
        observer.assertComplete()
    }

    @Test
    fun onReceivedResultWithFailure() {
        val bundle = Bundle()
        bundle.putParcelable(
            Constants.KEY_URL,
            TestUriUtility.failedRedirectUri()
        )
        Single.create<String> {
            authCodeClient.onReceivedResult(Activity.RESULT_OK, bundle, it)
        }.subscribe(observer)
        observer.assertError {
            it is AuthError && it.response.error == "invalid_grant" && it.response.errorDescription == "error_description"
        }
    }

    @Test
    fun onReceivedResultWithCancel() {
        val bundle = Bundle()
        bundle.putSerializable(
            Constants.KEY_EXCEPTION,
            ClientError(ClientErrorCause.Cancelled)
        )
        Single.create<String> {
            authCodeClient.onReceivedResult(Activity.RESULT_CANCELED, bundle, it)
        }.subscribe(observer)
        observer.assertError {
            it.javaClass == ClientError::class.java && it is ClientError
        }
    }

    object TestUriUtility {
        fun successfulRedirectUri(): Uri {
            return Uri.Builder().scheme("kakao123456").authority("oauth")
                .appendQueryParameter(Constants.CODE, "authorization_code").build()
        }

        fun failedRedirectUri(): Uri {
            return Uri.Builder().scheme("kakao123456").authority("oauth")
                .appendQueryParameter(Constants.ERROR, "invalid_grant")
                .appendQueryParameter(Constants.ERROR_DESCRIPTION, "error_description")
                .build()
        }
    }
}