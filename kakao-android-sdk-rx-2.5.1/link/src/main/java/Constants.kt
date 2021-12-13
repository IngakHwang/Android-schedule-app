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
package com.kakao.sdk.link

/**
 * @suppress
 */
object Constants {
    const val VALIDATE_CUSTOM_PATH = "/v2/api/kakaolink/talk/template/validate"
    const val VALIDATE_DEFAULT_PATH = "/v2/api/kakaolink/talk/template/default"
    const val VALIDATE_SCRAP_PATH = "/v2/api/kakaolink/talk/template/scrap"
    const val UPLOAD_IMAGE_PATH = "/v2/api/talk/message/image/upload"
    const val SCRAP_IMAGE_PATH = "/v2/api/talk/message/image/scrap"
    const val SHARER_PATH = "talk/friends/picker/easylink"

    const val LINK_VER = "link_ver"

    const val TEMPLATE_ID = "template_id"
    const val TEMPLATE_ARGS = "template_args"
    const val TEMPLATE_OBJECT = "template_object"
    const val REQUEST_URL = "request_url"

    const val TEMPLATE_MSG = "template_msg"
    const val WARNING_MSG = "warning_msg"
    const val ARGUMENT_MSG = "argument_msg"

    const val LINK_SCHEME = "kakaolink"
    const val LINK_AUTHORITY = "send"
    const val LINKVER = "linkver"
    const val TEMPLATE_JSON = "template_json"
    const val APP_KEY = "appkey"
    const val APP_VER = "appver"
    const val LCBA = "lcba" // link callback arguments
    const val EXTRAS = "extras"

    const val SHARER_APP_KEY = "app_key"
    const val SHARER_KA = "ka"
    const val VALIDATION_ACTION = "validation_action"
    const val VALIDATION_PARAMS = "validation_params"

    const val VALIDATION_CUSTOM = "custom"
    const val VALIDATION_SCRAP = "scrap"
    const val VALIDATION_DEFAULT = "default"

    const val LINKVER_40 = "4.0"

    const val LINK_URI_LIMIT = 10 * 1024


}