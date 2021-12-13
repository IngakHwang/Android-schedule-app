/*
  Copyright 2020 Kakao Corp.

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

import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kakao.sdk.common.model.ApprovalType
import com.kakao.sdk.common.model.ServerHosts
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.P])
class AuthCodeClientTest {

    private val CLIENT_ID = "abcdefghijklmnopqrstuvwxyz"
    private val REDIRECT_URI = "kakao$CLIENT_ID://oauth"

    @Test
    fun syncExtension_notDisplay() {
        UriUtility(ServerHosts()).authorize(
            clientId = CLIENT_ID,
            redirectUri = REDIRECT_URI,
            channelPublicIds = listOf(),
            serviceTerms = listOf()
        ).let {
            assert(value = it.getQueryParameter(Constants.CHANNEL_PUBLIC_ID) == "")
            assert(value = it.getQueryParameter(Constants.SERVICE_TERMS) == "")
        }
    }

    @Test
    fun syncExtension_multiple() {
        UriUtility(ServerHosts()).authorize(
            clientId = CLIENT_ID,
            redirectUri = REDIRECT_URI,
            channelPublicIds = listOf("abc","efc"),
            serviceTerms = listOf("123","456")
        ).let {
            assert(value = it.getQueryParameter(Constants.CHANNEL_PUBLIC_ID) == "abc,efc")
            assert(value = it.getQueryParameter(Constants.SERVICE_TERMS) == "123,456")
        }
    }
}