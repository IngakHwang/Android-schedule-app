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

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import android.util.Base64
import com.google.gson.JsonArray
import com.google.gson.JsonNull
import com.google.gson.JsonObject
import com.kakao.sdk.v2.common.BuildConfig
import com.kakao.sdk.common.Constants
import com.kakao.sdk.common.KakaoSdk
import java.io.File
import java.lang.NullPointerException
import java.net.URLDecoder
import java.security.MessageDigest
import java.util.*
import java.lang.IllegalStateException
import java.security.NoSuchAlgorithmException

/**
 * @suppress
 */
object Utility {
    @TargetApi(Build.VERSION_CODES.P)
    fun getKeyHash(context: Context): String {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//            val packageInfo = context.packageManager.getPackageInfo(context.packageName, PackageManager.GET_SIGNING_CERTIFICATES)
//            val signatures = packageInfo.signingInfo.signingCertificateHistory
//            for (signature in signatures) {
//                val md = MessageDigest.getInstance("SHA")
//                md.update(signature.toByteArray())
//                return Base64.encodeToString(md.digest(), Base64.NO_WRAP)
//            }
//            throw IllegalStateException()
//        }
        return getKeyHashDeprecated(context)
    }

    @Suppress("DEPRECATION")
    @SuppressLint("PackageManagerGetSignatures")
    fun getKeyHashDeprecated(context: Context): String {
        val packageInfo = context.packageManager
            .getPackageInfo(context.packageName, PackageManager.GET_SIGNATURES)
        for (signature in packageInfo.signatures) {
            val md = MessageDigest.getInstance("SHA")
            md.update(signature.toByteArray())
            return Base64.encodeToString(md.digest(), Base64.NO_WRAP)
        }
        throw IllegalStateException()
    }

    /**
     * 카카오 API 에서 클라이언트 검증, 통계, 이슈 해결 등을 위하여 사용하는 KA 헤더를 생성한다.
     *
     * Generate KA header used by Kakao API for client verification, statistics, and customer support.
     */
    fun getKAHeader(context: Context, sdkType: KakaoSdk.Type): String {
        return String.format(
            "%s/%s %s/%s %s/android-%s %s/%s-%s %s/%s %s/%s %s/%s %s/%s",
            Constants.SDK,
            BuildConfig.VERSION_NAME,
            Constants.SDK_TYPE,
            when (sdkType) {
                KakaoSdk.Type.RX_KOTLIN -> Constants.SDK_TYPE_RX_KOTLIN
                else -> Constants.SDK_TYPE_KOTLIN
            },
            Constants.OS,
            Build.VERSION.SDK_INT,
            Constants.LANG,
            Locale.getDefault().language.toLowerCase(),
            Locale.getDefault().country.toUpperCase(),
            Constants.ORIGIN,
            getKeyHash(context),
            Constants.DEVICE,
            Build.MODEL.replace("\\s".toRegex(), "-").toUpperCase(),
            Constants.ANDROID_PKG,
            context.packageName,
            Constants.APP_VER,
            context.packageManager.getPackageInfo(context.packageName, 0).versionName
        )
    }

    fun getExtras(context: Context, sdkType: KakaoSdk.Type): JsonObject {
        val jsonObject = JsonObject()
        jsonObject.addProperty(Constants.APP_PACKAGE, context.packageName)
        jsonObject.addProperty(
            Constants.APP_KEY_HASH,
            getKeyHash(context)
        )
        jsonObject.addProperty(
            Constants.KA,
            getKAHeader(context, sdkType)
        )
        return jsonObject
    }

    fun getMetadata(context: Context, key: String): String? {
        val ai = context.packageManager.getApplicationInfo(
            context.packageName, PackageManager.GET_META_DATA
        )
        return ai.metaData.getString(key)
    }

    @SuppressLint("HardwareIds")
    @Throws(NoSuchAlgorithmException::class)
    fun androidId(context: Context): ByteArray {
        return try {
            val androidId =
                Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
            val stripped = androidId.replace("[0\\s]".toRegex(), "")
            val md = MessageDigest.getInstance("SHA-256")
            md.reset()
            md.update("SDK-$stripped".toByteArray())
            md.digest()
        } catch (e: Exception) {
            ("xxxx" + Build.PRODUCT + "a23456789012345bcdefg").toByteArray()
        }
    }

    /**
     * Below methods are needed for tests.
     */
    fun parseQuery(queries: String?): Map<String, String> {
        if (queries == null) return mapOf()
        val kvList = queries.split("&").map { it.split("=") }.filter { it.size > 1 }
            .map { Pair(it[0], it[1]) }
        val map = mutableMapOf<String, String>()
        kvList.forEach { pair ->
            map[pair.first] = URLDecoder.decode(pair.second, "UTF-8")
        }
        return map
    }

    fun buildQuery(params: Map<String, String>?): String {
        if (params == null || params.isEmpty()) return ""
        return params.map { (k, v) -> "$k=$v" }.reduce { acc, s -> "$acc&$s" }
    }


    fun getJson(path: String): String {
        val classLoader = javaClass.classLoader ?: throw NullPointerException()
        val uri = classLoader.getResource(path)
        val file = File(uri.path)
        return String(file.readBytes())
    }

    fun getJsonObject(path: String): JsonObject {
        return KakaoJson.fromJson(getJson(path), JsonObject::class.java)
    }

    fun getJsonArray(path: String): JsonArray {
        return KakaoJson.fromJson(getJson(path), JsonArray::class.java)
    }

    fun hasAndNotNull(jsonObject: JsonObject, key: String): Boolean {
        return jsonObject.has(key) && jsonObject[key] !is JsonNull
    }
}