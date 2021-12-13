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
package com.kakao.sdk.network

import com.kakao.sdk.common.util.KakaoJson
import com.kakao.sdk.common.json.IntDate
import com.kakao.sdk.common.json.MapToQuery
import com.kakao.sdk.common.util.Utility
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.util.*

/**
 * @suppress
 */
class KakaoRetrofitConverterFactory : Converter.Factory() {
    override fun stringConverter(
        type: Type?,
        annotations: Array<out Annotation>?,
        retrofit: Retrofit?
    ): Converter<*, String>? {
        if (type == String::class.java) {
            return null
        }
        if (type is Class<*> && type.isEnum) {
            return Converter { enum: Enum<*> ->
                val encoded = KakaoJson.toJson(enum)
                return@Converter encoded.substring(1, encoded.length - 1)
            }
        }
        if (type == Date::class.java) {
            annotations?.filterIsInstance<IntDate>()?.firstOrNull()?.let {
                return Converter { value: Date -> (value.time / 1000).toString() }
            }
        }
        if (type is ParameterizedType && type.rawType == Map::class.java) {
            annotations?.filterIsInstance<MapToQuery>()?.firstOrNull()?.let {
                return Converter { map: Map<String, String> -> Utility.buildQuery(map) }
            }
        }
        return Converter { value: Any ->
            KakaoJson.toJson(value)
        }
    }
}
