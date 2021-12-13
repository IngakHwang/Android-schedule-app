/*
  Copyright 2021 Kakao Corp.

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

import com.kakao.sdk.common.Constants
import com.kakao.sdk.common.model.ApiError
import com.kakao.sdk.common.model.ApiErrorCause
import com.kakao.sdk.common.model.ApiErrorResponse
import com.kakao.sdk.common.util.KakaoJson
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody

/**
 * @suppress
 */
inline fun Interceptor.Chain.proceedBodyString(
    request: Request,
    bodyHandler: (response: Response, bodyString: String?) -> Response
): Response {
    val originalResponse = proceed(request)
    val body = originalResponse.body()
    val bodyString = body?.string()

    // 에러 응답 파싱을 위해 body를 한번 소비했기 때문에 새로 만들어주지 않으면 다른 인터셉터에서 body 접근 시 크래시 난다.
    val newResponse =
        originalResponse.newBuilder()
            .body(ResponseBody.create(body?.contentType(), bodyString))
            .build()
    return bodyHandler(newResponse, bodyString)
}

/**
 * @suppress
 */
inline fun Interceptor.Chain.proceedApiError(
    request: Request,
    errorHandler: (response: Response, error: ApiError) -> Response
): Response =
    proceedBodyString(request) { response, bodyString ->
        if (!response.isSuccessful) {
            val apiErrorResponse = bodyString?.let {
                KakaoJson.fromJson<ApiErrorResponse>(it, ApiErrorResponse::class.java)
            }
            val apiErrorCause = apiErrorResponse?.let {
                KakaoJson.fromJson<ApiErrorCause>(it.code.toString(), ApiErrorCause::class.java)
            }

            if (apiErrorCause != null && apiErrorResponse != null) {
                return errorHandler(response, ApiError(response.code(), apiErrorCause, apiErrorResponse))
            }
        }
        return response
    }
