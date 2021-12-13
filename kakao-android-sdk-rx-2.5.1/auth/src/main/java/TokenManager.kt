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

import android.content.SharedPreferences
import com.google.gson.JsonObject
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.util.KakaoJson
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.common.util.SdkLog
import com.kakao.sdk.common.util.AESCipher
import com.kakao.sdk.common.util.Cipher
import com.kakao.sdk.common.util.PersistentKVStore
import com.kakao.sdk.common.util.SharedPrefsWrapper
import com.kakao.sdk.v2.auth.BuildConfig
import java.util.*
import kotlin.Exception

/**
 * Kakao SDK에서 기본 제공하는 토큰 저장소 구현체.
 *
 * 기기 고유값을 이용해 토큰을 암호화하고 [SharedPreferences]에 저장함.
 *
 *  ```kotlin
 *  // 저장된 토큰 가져오기
 *  val token = TokenManager.instance.getToken()
 *  ```
 *
 * @see TokenManageable
 * @see TokenManagerProvider
 */
class TokenManager(
    /** @suppress */
    val appCache: PersistentKVStore = SharedPrefsWrapper(
        KakaoSdk.applicationContextInfo.sharedPreferences
    ),
    /** @suppress */
    val encryptor: Cipher = AESCipher()
) : TokenManageable {
    private var currentToken: OAuthToken?

    init {
        val version = appCache.getString(versionKey)
        if (version == null) {
            migrateFromOldVersion()
        }
        currentToken = appCache.getString(tokenKey)?.let {
            try {
                KakaoJson.fromJson<OAuthToken>(
                    encryptor.decrypt(it),
                    OAuthToken::class.java
                )
            } catch (e: Throwable) {
                SdkLog.e(e)
                null
            }
        }
    }

    /**
     * [SharedPreferences]에 저장되어 있는 [OAuthToken] 반환.
     */
    override fun getToken(): OAuthToken? {
        return currentToken
    }

    /**
     * 토큰을 [SharedPreferences]에 저장.
     *
     * @param token 저장하고자 하는 [OAuthToken] 객체.
     */
    override fun setToken(token: OAuthToken) {
        val newToken = OAuthToken(
            accessToken = token.accessToken,
            accessTokenExpiresAt = token.accessTokenExpiresAt,
            refreshToken = token.refreshToken,
            refreshTokenExpiresAt = token.refreshTokenExpiresAt,
            scopes = token.scopes
        )
        try {
            appCache.putString(tokenKey, encryptor.encrypt(KakaoJson.toJson(newToken))).commit()
        } catch (e: Throwable) {
            SdkLog.e(e)
        }
        currentToken = newToken
    }

    /**
     * [SharedPreferences]에 저장되어 있는 [OAuthToken] 객체를 삭제.
     */
    override fun clear() {
        currentToken = null
        appCache.remove(tokenKey).commit()
    }

    companion object {

        /**
         * 토큰 저장소 singleton 객체
         */
        @JvmStatic
        val instance by lazy { TokenManager() }

        /** @suppress */
        const val atKey = "com.kakao.token.AccessToken"
        /** @suppress */
        const val rtKey = "com.kakao.token.RefreshToken"
        /** @suppress */
        const val atExpiresAtKey = "com.kakao.token.OAuthToken.ExpiresAt"
        /** @suppress */
        const val rtExpiresAtKey = "com.kakao.token.RefreshToken.ExpiresAt"
        /** @suppress */
        const val secureModeKey = "com.kakao.token.KakaoSecureMode"

        /** @suppress */
        const val tokenKey = "com.kakao.sdk.oauth_token"
        /** @suppress */
        const val versionKey = "com.kakao.sdk.version"
    }

    private fun migrateFromOldVersion() {
        SdkLog.i("=== Migrate from old version token")
        appCache.putString(versionKey, BuildConfig.VERSION_NAME).commit()

        val secureMode = appCache.getString(secureModeKey, null)?.let {
            KakaoJson.fromJson<JsonObject>(it, JsonObject::class.java)["value"].asString
        } ?: "false"
        SdkLog.i("secureMode: $secureMode")
        val at = parseOrNull {
            appCache.getString(atKey, null)?.let {
                val legacyAt =
                    KakaoJson.fromJson<JsonObject>(
                        it,
                        JsonObject::class.java
                    )["value"].asString
                if (legacyAt != null && secureMode == "true") encryptor.decrypt(legacyAt) else legacyAt
            }
        }
        SdkLog.i("accessToken: $at")
        val rt = parseOrNull {
            appCache.getString(rtKey, null)?.let {
                val legacyRt =
                    KakaoJson.fromJson<JsonObject>(
                        it,
                        JsonObject::class.java
                    )["value"].asString
                if (legacyRt != null && secureMode == "true") encryptor.decrypt(legacyRt) else legacyRt
            }
        }
        SdkLog.i("refreshToken: $rt")
        val atExpiresAt = parseOrNull {
            appCache.getString(atExpiresAtKey, null)?.let {
                KakaoJson.fromJson<JsonObject>(it, JsonObject::class.java)["value"].asLong
            }
        } ?: 0L
        val rtExpiresAt = parseOrNull {
            appCache.getString(rtExpiresAtKey, null)?.let {
                KakaoJson.fromJson<JsonObject>(it, JsonObject::class.java)["value"].asLong
            }
        } ?: Long.MAX_VALUE

        if (at != null && rt != null) {     // rt만 있어도 살려서 쓸 수 있어서 rt만 체크하려고 했었다. 그러나 lucas와 상의한 후 구 SDK에서 rt만 저장하던 스펙 없으니 기본적으로 둘다 체크하기로 했다.
            val token = OAuthToken(
                accessToken = at,
                accessTokenExpiresAt = Date(atExpiresAt),
                refreshToken = rt,
                refreshTokenExpiresAt = Date(rtExpiresAt)
            )
            appCache
                .putString(tokenKey, encryptor.encrypt(KakaoJson.toJson(token)))
                .remove(secureModeKey)
                .remove(atKey)
                .remove(rtKey)
                .remove(atExpiresAtKey)
                .remove(rtExpiresAtKey)
                .commit()
        }
    }
}

private inline fun <T> parseOrNull(f: () -> T): T? =
    try {
        f()
    } catch (e: Exception) {
        SdkLog.e(e)
        null
    }