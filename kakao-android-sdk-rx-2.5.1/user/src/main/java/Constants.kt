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
package com.kakao.sdk.user

/**
 * @suppress
 */
object Constants {
    const val V2_ME_PATH = "/v2/user/me"
    const val V2_SCOPES = "/v2/user/scopes"
    const val V2_REVOKE_SCOPE = "/v2/user/revoke/scopes"
    const val V1_ACCESS_TOKEN_INFO_PATH = "/v1/user/access_token_info"
    const val V1_UPDATE_PROFILE_PATH = "/v1/user/update_profile"
    const val V1_AGE_AUTH_INFO_PATH = "/v1/user/age_auth"
    const val V1_LOGOUT_PATH = "/v1/user/logout"
    const val V1_UNLINK_PATH = "/v1/user/unlink"
    const val V1_SHIPPING_ADDRESSES_PATH = "/v1/user/shipping_address"
    const val V1_SERVICE_TERMS_PATH = "/v1/user/service/terms"
    const val V1_SIGNUP_PATH = "/v1/user/signup"

    const val SECURE_RESOURCE = "secure_resource"
    const val EXTRA = "extra"
    const val PROPERTIES = "properties"
    const val PROPERTY_KEYS = "property_keys"
    const val AGE_LIMIT = "age_limit"

    const val ID = "id"
    const val HAS_SIGNED_UP = "has_signed_up"
    const val KAKAO_ACCOUNT = "kakao_account"
    const val FOR_PARTNER = "for_partner"


    const val PROFILE_NEEDS_AGREEMENT = "profile_needs_agreement"
    const val PROFILE = "profile"

    const val EMAIL_NEEDS_AGREEMENT = "email_needs_agreement"
    const val IS_EMAIL_VALID = "is_email_valid"
    const val IS_EMAIL_VERIFIED = "is_email_verified"
    const val EMAIL = "email"

    const val AGE_RANGE_NEEDS_AGREEMENT = "age_range_needs_agreement"
    const val AGE_RANGE = "age_range"

    const val BIRTHYEAR_NEEDS_AGREEMENT = "birthyear_needs_agreement"
    const val BIRTHYEAR = "birthyear"

    const val BIRTHDAY_NEEDS_AGREEMENT = "birthday_needs_agreement"
    const val BIRTHDAY = "birthday"
    const val BIRTHDAY_TYPE = "birthday_type"

    const val GENDER_NEEDS_AGREEMENT = "gender_needs_agreement"
    const val GENDER = "gender"

    const val CI_NEEDS_AGREEMENT = "ci_needs_agreement"
    const val CI = "ci"
    const val CI_AUTHENTICATION_AT = "ci_authenticated_at"

    const val LEGAL_NAME_NEEDS_AGREEMENT = "legal_name_needs_agreement"
    const val LEGAL_NAME = "legal_name"

    const val LEGAL_BIRTH_DATE_NEEDS_AGREEMENT = "legal_birth_date_needs_agreement"
    const val LEGAL_BIRTH_DATE = "legal_birth_date"

    const val LEGAL_GENDER_NEEDS_AGREEMENT = "legal_gender_needs_agreement"
    const val LEGAL_GENDER = "legal_gender"

    const val PHONE_NUMBER_NEEDS_AGREEMENT = "phone_number_needs_agreement"
    const val PHONE_NUMBER = "phone_number"

    const val IS_KOREAN_NEEDS_AGREEMENT = "is_korean_needs_agreement"
    const val IS_KOREAN = "is_korean"

    const val IS_KAKAOTALK_USER = "is_kakaotalk_user"
    const val HAS_PHONE_NUMBER = "has_phone_number"

    const val DISPLAY_ID = "display_id"

    const val APPID = "appId"
    const val EXPIRESINMILLIS = "expiresInMillis"
    const val KACCOUNT_ID = "kaccount_id"

    const val ADDRESS_ID = "address_id"
    const val FROM_UPDATED_AT = "from_updated_at"
    const val PAGE_SIZE = "page_size"


    const val NICKNAME = "nickname"
    const val PROFILE_IMAGE_URL = "profile_image_url"
    const val THUMBNAIL_IMAGE_URL = "thumbnail_image_url"

    const val SCOPES = "scopes"
}