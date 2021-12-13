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
package com.kakao.sdk.navi

import android.content.Context
import android.content.Intent
import android.content.pm.*
import android.os.Build
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.gson.JsonObject
import com.kakao.sdk.common.model.ServerHosts
import com.kakao.sdk.navi.model.*
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.runner.RunWith
import org.robolectric.Shadows
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowPackageManager

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.P])
class NaviClientTest {
    lateinit var context: Context
    lateinit var packageManager: PackageManager
    lateinit var shadow: ShadowPackageManager

    lateinit var client: NaviClient

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext<Context>()
        packageManager = context.packageManager
        shadow = Shadows.shadowOf(packageManager)
        client = NaviClient(
            TestApplicationInfo("client_id"),
            TestContextInfo("ka_header", "key_hash", JsonObject(), appVer = "1.0.0"),
            ServerHosts()
        )
    }

    @Test
    fun isKakaoNaviInstalled() {
        val intent = Intent(Intent.ACTION_MAIN).setPackage(Constants.NAVI_PACKAGE)
            .addCategory(Intent.CATEGORY_LAUNCHER)

        val activityInfo = ActivityInfo()
        activityInfo.packageName = Constants.NAVI_PACKAGE
        activityInfo.name = "SplashActivity" // 임의의 이름
        val resolveInfo = ResolveInfo()
        resolveInfo.activityInfo = activityInfo

        assertFalse(client.isKakaoNaviInstalled(context))
        shadow.addResolveInfoForIntent(intent, resolveInfo)
        assertTrue(client.isKakaoNaviInstalled(context))
    }

    @Test
    fun shareDestinationUri() {
        val info = PackageInfo()
        info.packageName = context.packageName
        info.versionName = "1.0.0"
        info.signatures = arrayOf(Signature("00000000"))
        shadow.installPackage(info)

        val uri = client.navigateWebUrl(
            Location("name", "30", "30"),
            NaviOption(
                CoordType.WGS84,
                vehicleType = VehicleType.SECOND,
                rpOption = RpOption.FREE
            )
        )
        println(uri.getQueryParameter(com.kakao.sdk.common.Constants.EXTRAS))
        println(uri.getQueryParameter(Constants.PARAM))
    }
}