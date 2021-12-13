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

import java.io.IOException

/**
 * @suppress
 * 인터셉터 등 네트워킹 내부에서 발생한 IOException 외 모든 예외를 onFailure로 전달 가능하도록 IOException으로 래핑
 */
class ExceptionWrapper(
    val origin: Throwable
): IOException()

/**
 * @suppress
 */
val Throwable.origin
    get() = if (this is ExceptionWrapper) origin else this
