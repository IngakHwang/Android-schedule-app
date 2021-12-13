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

import com.kakao.sdk.common.util.SdkLog
import io.reactivex.Completable
import io.reactivex.CompletableTransformer
import io.reactivex.Single
import io.reactivex.SingleTransformer

/**
 * @suppress
 */
object RxOperations {

    fun <T> handleApiError(): SingleTransformer<T, T> {
        return SingleTransformer { it ->
            it.onErrorResumeNext { Single.error(ApiCallback.translateError(it)) }
                .doOnError { SdkLog.e(it) }
                .doOnSuccess { SdkLog.i(it!!) }
        }
    }

    fun handleCompletableError(): CompletableTransformer {
        return CompletableTransformer {
            it.onErrorResumeNext { Completable.error(ApiCallback.translateError(it)) }
                .doOnError { SdkLog.e(it) }
                .doOnComplete { }
        }
    }
}