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

import com.google.gson.*
import com.google.gson.reflect.TypeToken
import com.kakao.sdk.common.json.Exclude
import com.kakao.sdk.common.json.KakaoTypeAdapterFactory
import java.lang.reflect.Type

/**
 * @suppress
 */
object KakaoJson {

    private val kakaoExclusionStrategy = object : ExclusionStrategy {
        override fun shouldSkipClass(clazz: Class<*>?): Boolean {
            return false
        }

        override fun shouldSkipField(f: FieldAttributes?): Boolean {
            val exclude = f!!.getAnnotation(Exclude::class.java)
            return exclude != null
        }
    }

    private val internalBuilder = GsonBuilder()
        .registerTypeAdapterFactory(KakaoTypeAdapterFactory())
        .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
        .addSerializationExclusionStrategy(kakaoExclusionStrategy)
        .addDeserializationExclusionStrategy(kakaoExclusionStrategy)

    /**
     * @suppress
     */
    val base: Gson = internalBuilder.create()

    /**
     * @suppress
     */
    val pretty: Gson = internalBuilder.setPrettyPrinting().create()

    fun <T> listFromJson(string: String, type: Class<T>): List<T> =
        base.fromJson(string, TypeToken.getParameterized(List::class.java, type).type)

    fun <T> parameterizedFromJson(string: String, type1: Type, type2: Type): T =
        base.fromJson(string, TypeToken.getParameterized(type1, type2).type)


    fun <T> toJson(model: T): String = base.toJson(model)
    fun <T> fromJson(string: String, type1: Type): T = base.fromJson(string, type1)
}