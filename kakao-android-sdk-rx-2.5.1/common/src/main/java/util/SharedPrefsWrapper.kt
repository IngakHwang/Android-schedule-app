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

import android.content.SharedPreferences

/**
 * @suppress
 */
class SharedPrefsWrapper(
    val appCache: SharedPreferences
) : PersistentKVStore {
    override fun remove(key: String): PersistentKVStore {
        editor.remove(key)
        return this
    }

    private val editor: SharedPreferences.Editor = appCache.edit()

    override fun getString(key: String, fallbackValue: String?): String? =
        appCache.getString(key, fallbackValue)

    override fun getLong(key: String, fallbackValue: Long): Long =
        appCache.getLong(key, fallbackValue)

    override fun putString(key: String, value: String): PersistentKVStore {
        editor.putString(key, value)
        return this
    }

    override fun putLong(key: String, value: Long): PersistentKVStore {
        editor.putLong(key, value)
        return this
    }

    override fun commit(): PersistentKVStore {
        editor.commit()
        return this
    }

    override fun apply(): PersistentKVStore {
        editor.apply()
        return this
    }
}