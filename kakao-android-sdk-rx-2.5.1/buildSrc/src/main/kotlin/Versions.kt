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
object Versions {
    const val gradle = "4.0.1"  // 'package visibility' min version https://developer.android.com/studio/releases/gradle-plugin#4-0-0
    const val kotlin = "1.3.50"
    const val retrofit = "2.6.2"

    const val okhttp = "3.12.5"

    const val gson = "2.8.6"
}

object SdkVersions {
    const val versionCode = 1020
    const val version = "2.5.1"

    const val minSdkVersion = 19
    const val compileSdkVersion = 30
    const val buildToolsVersion = "30.0.1"
    const val targetSdkVersion = 30
}

object Dependencies {
    const val appCompat = "androidx.appcompat:appcompat:1.1.0"
    const val annotation = "androidx.annotation:annotation:1.1.0"
    const val constraintLayout = "androidx.constraintlayout:constraintlayout:1.1.3"
    const val material = "com.google.android.material:material:1.0.0"
    const val viewPager = "androidx.viewpager:viewpager:1.0.0"

    const val browser = "androidx.browser:browser:1.0.0"

    const val gson = "com.google.code.gson:gson:${Versions.gson}"

    const val rxJava = "io.reactivex.rxjava2:rxjava:2.2.13"
    const val rxAndroid = "io.reactivex.rxjava2:rxandroid:2.1.1"
    const val rxKotlin = "io.reactivex.rxjava2:rxkotlin:2.4.0"

    const val okhttp = "com.squareup.okhttp3:okhttp:${Versions.okhttp}"
    const val retrofit = "com.squareup.retrofit2:retrofit:${Versions.retrofit}"
    const val retrofitRx = "com.squareup.retrofit2:adapter-rxjava2:${Versions.retrofit}"
    const val retrofitGson = "com.squareup.retrofit2:converter-gson:${Versions.retrofit}"

    const val mockwebserver = "com.squareup.okhttp3:mockwebserver:${Versions.okhttp}"
    const val loggingInterceptor = "com.squareup.okhttp3:logging-interceptor:${Versions.okhttp}"

    const val mockitoCore = "org.mockito:mockito-core:3.1.0"
    const val mockitoInline = "org.mockito:mockito-inline:3.1.0"
    const val mockitoAndroid = "org.mockito:mockito-android:3.1.0"
}