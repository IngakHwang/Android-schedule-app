/*
  Copyright 2020 Kakao Corp.

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

import com.kakao.sdk.common.util.SdkLog
import com.kakao.sdk.common.model.*
import com.kakao.sdk.common.util.KakaoJson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response

/**
 * @suppress
 */
abstract class ApiCallback<T> : Callback<T> {
    abstract fun onComplete(model: T?, error: Throwable?)

    override fun onFailure(call: Call<T>, t: Throwable) {
        t.origin.also {
            SdkLog.e(it)
            onComplete(null, it)
        }
    }

    override fun onResponse(call: Call<T>, response: Response<T>) {
        val model = response.body()
        if (model != null) {
            SdkLog.i(model)
            onComplete(model, null)
        } else {
            onFailure(call,
                translateError(
                    HttpException(response)
                )
            )
        }
    }

    companion object {
        fun translateError(t: Throwable): Throwable {
            try {
                if (t is HttpException) {
                    val errorString = t.response()?.errorBody()?.string()
                    val response =
                        KakaoJson.fromJson<ApiErrorResponse>(
                            errorString!!,
                            ApiErrorResponse::class.java
                        )
                    val cause =
                        KakaoJson.fromJson(
                            response.code.toString(),
                            ApiErrorCause::class.java
                        )
                            ?: ApiErrorCause.Unknown
                    return ApiError(t.code(), cause, response)
                }
                return t
            } catch (unexpected: Throwable) {
                return unexpected
            }
        }
    }
}
