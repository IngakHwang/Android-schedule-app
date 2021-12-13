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
package com.kakao.sdk.common.model

/**
 * @suppress
 */
open class ServerHosts {
    open val kauth: String = "kauth.kakao.com"
    open val kapi: String = "kapi.kakao.com"
    open val account: String = "accounts.kakao.com"
    open val mobileAccount: String = "auth.kakao.com"
    open val sharer: String = "sharer.kakao.com"
    open val navi: String = "kakaonavi-wguide.kakao.com"
    open val channel: String = "pf.kakao.com"

    companion object
}