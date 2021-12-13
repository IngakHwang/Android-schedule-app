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
package com.kakao.sdk.auth

/**
 * @suppress
 */
object Constants {
    const val CAPRI_LOGGED_IN_ACTIVITY = "com.kakao.talk.intent.action.CAPRI_LOGGED_IN_ACTIVITY"

    const val AUTHORIZE_PATH = "oauth/authorize"
    const val TOKEN_PATH = "oauth/token"
    const val AGT_PATH = "api/agt"

    const val ACCOUNT_LOGIN_PATH = "/sdks/page"
    const val ACCOUNT_LOGIN_PARAM_CONTINUE = "continue"

    const val CLIENT_ID = "client_id"
    const val AGT = "agt"
    const val REDIRECT_URI = "redirect_uri"
    const val ANDROID_KEY_HASH = "android_key_hash"
    const val CODE = "code"
    const val ERROR = "error"
    const val ERROR_DESCRIPTION = "error_description"
    const val REFRESH_TOKEN = "refresh_token"
    const val GRANT_TYPE = "grant_type"
    const val RESPONSE_TYPE = "response_type"
    const val SCOPE = "scope"
    const val PROMPT = "prompt"
    const val KA_HEADER = "ka"

    const val AUTHORIZATION_CODE = "authorization_code"

    const val ACCESS_TOKEN = "access_token"
    const val EXPIRES_IN = "expires_in"
    const val REFRESH_TOKEN_EXPIRES_IN = "refresh_token_expires_in"
    const val TOKEN_TYPE = "token_type"

    const val SECURE_RESOURCE = "secure_resource"

    const val KEY_URL = "key.url"
    const val KEY_LOGIN_INTENT = "key.login.intent"
    const val KEY_REQUEST_CODE = "key.request.code"
    const val KEY_REDIRECT_URI = "key.redirect_uri"
    const val KEY_FULL_URI = "key.full_authorize_uri"

    const val KEY_BUNDLE = "key.bundle"
    const val KEY_HEADERS = "key.extra.headers"
    const val KEY_EXCEPTION = "key.exception"
    const val KEY_RESULT_RECEIVER = "key.result.receiver"

    val EXTRA_APPLICATION_KEY = "com.kakao.sdk.talk.appKey"
    val EXTRA_REDIRECT_URI = "com.kakao.sdk.talk.redirectUri"
    val EXTRA_KA_HEADER = "com.kakao.sdk.talk.kaHeader"
    val EXTRA_EXTRAPARAMS = "com.kakao.sdk.talk.extraparams"

    val EXTRA_REDIRECT_URL = "com.kakao.sdk.talk.redirectUrl"
    val EXTRA_ERROR_DESCRIPTION = "com.kakao.sdk.talk.error.description"
    val EXTRA_ERROR_TYPE = "com.kakao.sdk.talk.error.type"

    const val CHANNEL_PUBLIC_ID = "channel_public_id"
    const val SERVICE_TERMS = "service_terms"
    const val APPROVAL_TYPE = "approval_type"

    const val CODE_CHALLENGE = "code_challenge"
    const val CODE_CHALLENGE_METHOD = "code_challenge_method"
    const val CODE_CHALLENGE_METHOD_VALUE = "S256"
    const val CODE_CHALLENGE_ALGORITHM = "SHA-256"

    const val CODE_VERIFIER = "code_verifier"
    const val CODE_VERIFIER_ALGORITHM = "SHA-512"

    val NOT_SUPPORT_ERROR = "NotSupportError" // KakaoTalk installed but not signed up
    val UNKNOWN_ERROR = "unknown"
}