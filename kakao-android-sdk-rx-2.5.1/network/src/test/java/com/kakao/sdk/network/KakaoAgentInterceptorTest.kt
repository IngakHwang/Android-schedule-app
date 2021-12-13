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

import android.app.Application
import android.content.pm.PackageInfo
import android.content.pm.Signature
import android.os.Build
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.gson.JsonObject
import com.kakao.sdk.v2.common.BuildConfig
import com.kakao.sdk.common.Constants
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.common.model.ContextInfo
import com.kakao.sdk.network.KakaoAgentInterceptor
import com.kakao.sdk.common.util.Utility
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.runner.RunWith
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowLog
import org.robolectric.shadows.ShadowPackageManager
import java.util.*

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.P])
class KakaoAgentInterceptorTest {

    lateinit var interceptor: KakaoAgentInterceptor
    lateinit var shadowPackageManger: ShadowPackageManager
    lateinit var application: Application
    @Suppress("DEPRECATION") // Robolectric 이 API level 28 을 지원하면 ShadowSigningInfo 로 변경
    @Before
    fun setup() {
        ShadowLog.stream = System.out
        application = ApplicationProvider.getApplicationContext()
        val packageManager = application.packageManager
        shadowPackageManger = shadowOf(packageManager)

        val info = PackageInfo()
        info.packageName = application.packageName
        info.versionName = "1.0.0"
        info.signatures = arrayOf(Signature("00000000"))
        shadowPackageManger.addPackage(info)

        interceptor = KakaoAgentInterceptor(object :
            ContextInfo {
            override val appVer: String
                get() = "1.0.0"
            override val salt: ByteArray
                get() = "1234".toByteArray()
            override val kaHeader: String
                get() = Utility.getKAHeader(application, KakaoSdk.Type.KOTLIN)
            override val signingKeyHash: String
                get() = Utility.getKeyHash(application)
            override val extras: JsonObject
                get() = Utility.getExtras(application, KakaoSdk.Type.KOTLIN)

        })
    }

    @Test
    fun interceptor() {
        val client = OkHttpClient().newBuilder().addInterceptor(interceptor).build()
        val server = MockWebServer()
        server.start()

        server.enqueue(MockResponse())
        client.newCall(Request.Builder().url(server.url("/")).build()).execute()
        val request = server.takeRequest()
        server.shutdown()

        val ka = request.getHeader(Constants.KA)
        if (ka == null) {
            fail<Void>("KA header was null")
            return
        }
        val headerMap = parseKAHeader(ka)
        assertEquals(BuildConfig.VERSION_NAME, headerMap[Constants.SDK])
        assertEquals(String.format("android-%s", Build.VERSION.SDK_INT), headerMap[Constants.OS])
        assertEquals(
            String.format(
                "%s-%s",
                Locale.getDefault().language.toLowerCase(),
                Locale.getDefault().country.toUpperCase()
            ), headerMap[Constants.LANG]
        )
        assertTrue(headerMap.containsKey(Constants.ORIGIN))
        assertTrue(headerMap.containsKey(Constants.DEVICE))
        assertEquals(application.packageName, headerMap[Constants.ANDROID_PKG])
        assertEquals("1.0.0", headerMap[Constants.APP_VER])
    }

    fun parseKAHeader(header: String): Map<String, String> {
        val map = hashMapOf<String, String>()
        header.split(" ").map { kv -> kv.split("/") }.forEach { map[it[0]] = it[1] }
        return map
    }
}