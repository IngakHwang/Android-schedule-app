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

import com.google.gson.JsonObject
import com.kakao.sdk.common.model.ContextInfo

class TestContextInfo(override val kaHeader: String, override val signingKeyHash: String, override val extras: JsonObject,
                      override val appVer: String,
                      override val salt: ByteArray = "".toByteArray()
) : ContextInfo