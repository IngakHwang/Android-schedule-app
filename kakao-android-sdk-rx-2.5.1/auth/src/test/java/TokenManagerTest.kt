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

import android.app.Application
import android.content.Context
import android.os.Build
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.gson.JsonObject
import com.kakao.sdk.common.util.KakaoJson
import com.kakao.sdk.common.util.Cipher
import com.kakao.sdk.common.util.PersistentKVStore
import com.kakao.sdk.common.util.SharedPrefsWrapper
import com.kakao.sdk.v2.auth.BuildConfig
import org.junit.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.P])
class TokenManagerTest {
    lateinit var tokenManager: TokenManager

    @Test
    fun toCache() {
        tokenManager = TokenManager(
            getEmptyPreferences(),
            IdentityCipher()
        )
        tokenManager.setToken(testOAuthToken())
    }

    @Test
    fun fromEmptyCache() {
        tokenManager = TokenManager(
            getEmptyPreferences(),
            IdentityCipher()
        )
        assertNull(tokenManager.getToken())
    }

    @Test
    fun fromFullCache() {
        tokenManager = TokenManager(
            getFullPreferences(),
            IdentityCipher()
        )
        val token = tokenManager.getToken()
        val scopes = token?.scopes
        assertNotNull(scopes)
        assertEquals(2, scopes!!.size)
        assertEquals("account_email", scopes[0])
        assertEquals("birthday", scopes[1])
    }

    @Test
    fun insecureToV2() {
        val cipher = SimpleCipher()
        val preferences = legacyPreferences()
        tokenManager = TokenManager(preferences, cipher)
        val token = tokenManager.getToken()
        assertEquals("test_access_token", token?.accessToken)
        assertEquals("test_refresh_token", token?.refreshToken)
        assertEquals(BuildConfig.VERSION_NAME, preferences.getString(TokenManager.versionKey))
    }

    @Test
    fun secureToV2() {
        val cipher = SimpleCipher()
        val preferences = legacyPreferences(cipher)
        tokenManager = TokenManager(preferences, cipher)
        val token = tokenManager.getToken()
        assertEquals("test_access_token", token?.accessToken)
        assertEquals("test_refresh_token", token?.refreshToken)
        assertEquals(BuildConfig.VERSION_NAME, preferences.getString(TokenManager.versionKey))
    }

    fun getEmptyPreferences(): PersistentKVStore {
        return SharedPrefsWrapper(
            ApplicationProvider.getApplicationContext<Application>().getSharedPreferences(
                "test_app_key",
                Context.MODE_PRIVATE
            )
        )
    }

    fun getFullPreferences(cipher: Cipher = IdentityCipher()): PersistentKVStore {
        val preferences = ApplicationProvider.getApplicationContext<Application>()
            .getSharedPreferences("test_app_key", Context.MODE_PRIVATE)

        val token = testOAuthToken(scopes = listOf("account_email", "birthday"))
        preferences.edit()
            .putString(TokenManager.tokenKey, cipher.encrypt(KakaoJson.toJson(token)))
            .putString(TokenManager.versionKey, "2.0.0")
            .commit()
        return SharedPrefsWrapper(preferences)
    }

    fun legacyPreferences(cipher: Cipher? = null): PersistentKVStore {
        val preferences = ApplicationProvider.getApplicationContext<Application>()
            .getSharedPreferences("test_app_key", Context.MODE_PRIVATE)

        val legacyAt = JsonObject()
        legacyAt.addProperty("value", cipher?.encrypt("test_access_token") ?: "test_access_token")
        legacyAt.addProperty("valueType", "string")
        val legacyRt = JsonObject()
        legacyRt.addProperty("value", cipher?.encrypt("test_refresh_token") ?: "test_refresh_token")
        legacyRt.addProperty("valueType", "string")
        val legacyAtExpiresAt = JsonObject()
        legacyAtExpiresAt.addProperty("value", 1555099407890L)
        legacyAtExpiresAt.addProperty("valueType", "long")
        val legacyRtExpiresAt = JsonObject()
        legacyRtExpiresAt.addProperty("value", 1557648207890L)
        legacyRtExpiresAt.addProperty("valueType", "long")

        val secureMode = JsonObject()
        secureMode.addProperty("value", cipher != null)
        secureMode.addProperty("valueType", "string")
        preferences.edit()
            .putString(TokenManager.atKey, legacyAt.toString())
            .putString(TokenManager.rtKey, legacyRt.toString())
            .putString(TokenManager.atExpiresAtKey, legacyAtExpiresAt.toString())
            .putString(TokenManager.rtExpiresAtKey, legacyRtExpiresAt.toString())
            .putString(TokenManager.secureModeKey, secureMode.toString())
            .commit()
        return SharedPrefsWrapper(preferences)
    }

    class SimpleCipher : Cipher {
        override fun encrypt(value: String): String = "a${value}"
        override fun decrypt(encrypted: String): String = encrypted.substring(1)
    }

    class IdentityCipher : Cipher {
        override fun encrypt(value: String): String = value
        override fun decrypt(encrypted: String): String = encrypted
    }
}
