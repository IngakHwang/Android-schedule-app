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
package com.kakao.sdk.common.json

import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.google.gson.TypeAdapterFactory
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import com.kakao.sdk.common.util.Utility
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

/**
 * @suppress
 */
internal class KakaoTypeAdapterFactory : TypeAdapterFactory {
    @Suppress("UNCHECKED_CAST")
    override fun <T> create(gson: Gson, type: TypeToken<T>): TypeAdapter<T>? {
        val rawType = type.rawType as Class<*>
        if (rawType == Date::class.java) {
            return KakaoDateTypeAdapter() as TypeAdapter<T>
        }
        if (rawType.isEnum) {
            return KakaoEnumTypeAdapter(rawType) as TypeAdapter<T>
        }
        return null
    }
}

/**
 * @suppress
 */
class MapToQueryAdapter : TypeAdapter<Map<String, String>>() {
    override fun write(out: JsonWriter?, value: Map<String, String>?) {
        if (value == null) {
            out?.nullValue()
            return
        }
        val query = Utility.buildQuery(value)
        out?.value(query)
    }

    override fun read(`in`: JsonReader?): Map<String, String>? {
        if (`in`?.peek() == JsonToken.NULL) {
            `in`.nextNull()
            return null
        }
        val input = `in`?.nextString()
        return Utility.parseQuery(input)
    }
}

/**
 * @suppress
 */
class KakaoIntDateTypeAdapter : TypeAdapter<Date>() {
    override fun write(out: JsonWriter?, value: Date?) {
        if (value == null) {
            out?.nullValue()
            return
        }
        out?.value(value.time / 1000)
    }

    override fun read(`in`: JsonReader?): Date? {
        if (`in`?.peek() == JsonToken.NULL) {
            `in`.nextNull()
            return null
        }
        if (`in`?.peek() == JsonToken.NUMBER) {
            val timestamp = `in`.nextLong()
            return Date(timestamp * 1000)
        }
        return null
    }
}

/**
 * @suppress
 */
class KakaoDateTypeAdapter : TypeAdapter<Date>() {
    val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("GMT")
    }

    override fun write(out: JsonWriter?, value: Date?) {
        if (value == null) {
            out?.nullValue()
            return
        }
        out?.value(format.format(value))
    }

    override fun read(`in`: JsonReader?): Date? {
        if (`in`?.peek() == JsonToken.NULL) {
            `in`.nextNull()
            return null
        }
        if (`in`?.peek() == JsonToken.STRING) {
            val dateString = `in`.nextString()
            return format.parse(dateString)
        }
        return null
    }
}

/**
 * @suppress
 */
class KakaoEnumTypeAdapter<T>(private val enumClass: Class<T>) : TypeAdapter<T>() {
    override fun write(out: JsonWriter?, value: T) {
        if (value == null) {
            out?.nullValue()
            return
        }
        val isIntEnum = enumClass.isAnnotationPresent(IntEnum::class.java)
        enumClass.enumConstants?.forEach {
            try {
                val name = (it as Enum<*>).name
                val serializedName =
                    enumClass.getField(name).getAnnotation(SerializedName::class.java)
                if (serializedName != null && it == value) {
                    if (isIntEnum) {
                        out?.value(serializedName.value.toInt())
                    } else {
                        out?.value(serializedName.value)
                    }
                    return
                }
            } catch (e: NoSuchFieldException) {
                throw IOException(e)
            }
        }
        out?.value(value.toString())
    }

    override fun read(`in`: JsonReader?): T? {
        if (`in`?.peek() == JsonToken.NULL) {
            `in`.nextNull()
            return null
        }
        val inputName = when (`in`?.peek()) {
            JsonToken.NUMBER -> Pair(`in`.nextLong(), null)
            JsonToken.STRING -> Pair(null, `in`.nextString())
            else -> Pair(null, null)
        }
        var unknown: T? = null
        enumClass.enumConstants?.forEach {
            try {
                val name = (it as Enum<*>).name
                val field = enumClass.getField(name)
                if (inputName.first != null) {
                    val serializedName = field.getAnnotation(SerializedName::class.java)
                    if (serializedName != null && inputName.first == serializedName.value.toLong()) {
                        return it
                    }
                } else if (inputName.second != null) {
                    if (inputName.second == name) return it
                    val serializedName = field.getAnnotation(SerializedName::class.java)
                    if (serializedName != null && inputName.second == serializedName.value) {
                        return it
                    }
                }

                val annotation = field.getAnnotation(UnknownValue::class.java)
                annotation.let { _ -> unknown = it }
            } catch (e: NoSuchFieldException) {
                throw IOException(e)
            }
        }
        if (unknown != null) return unknown
        throw IOException("No matching enum field")
    }
}