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
package com.kakao.sdk.talk

/**
 * @suppress
 */
object Constants {
    const val PROFILE_PATH = "/v1/api/talk/profile"
    const val V2_MEMO_PATH = "/v2/api/talk/memo/send"
    const val V2_MEMO_DEFAULT_PATH = "/v2/api/talk/memo/default/send"
    const val V2_MEMO_SCRAP_PATH = "/v2/api/talk/memo/scrap/send"
    const val V1_FRIENDS_PATH = "v1/api/talk/friends"
    const val V1_OPEN_TALK_MESSAGE_CUSTOM_PATH = "/v1/api/talk/friends/message/send"
    const val V1_OPEN_TALK_MESSAGE_DEFAULT_PATH = "/v1/api/talk/friends/message/default/send"
    const val V1_OPEN_TALK_MESSAGE_SCRAP_PATH = "/v1/api/talk/friends/message/scrap/send"

    const val V1_CHANNELS_PATH = "/v1/api/talk/channels"

    const val SECURE_RESOURCE = "secure_resource"
    const val NICKNAME = "nickName"
    const val PROFILE_IMAGE_URL = "profileImageURL"
    const val THUMBNAIL_URL = "thumbnailURL"
    const val COUNTRY_ISO = "countryISO"

    const val ELEMENTS = "elements"
    const val TOTAL_COUNT = "total_count"
    const val BEFORE_URL = "before_url"
    const val AFTER_URL = "after_url"

    const val ID = "id"
    const val TITLE = "title"
    const val IMAGE_URL = "image_url"
    const val MEMBER_COUNT = "member_count"
    const val DISPLAY_MEMBER_IMAGES = "display_member_images"
    const val CHAT_TYPE = "chat_type"

    const val TEMPLATE_ID = "template_id"
    const val TEMPLATE_ARGS = "template_args"
    const val TEMPLATE_OBJECT = "template_object"
    const val REQUEST_URL = "request_url"
    const val RECEIVER_UUIDS = "receiver_uuids"

    const val CHANNEL_UUID = "channel_uuid"
    const val CHANNEL_PUBLIC_ID = "channel_public_id"
    const val CHANNEL_PUBLIC_IDS = "channel_public_ids"
    const val RELATION = "relation"
    const val UPDATED_AT = "updated_at"

    const val FRIEND_TYPE = "friend_type"
    const val FRIEND_FILTER = "friend_filter"
    const val FRIEND_ORDER = "friend_order"
    const val OFFSET = "offset"
    const val LIMIT = "limit"
    const val ORDER = "order"
    const val COUNTRY_CODES = "country_codes"
    const val URL = "url"

    const val KAKAO_AGENT = "kakao_agent"
    const val API_VER = "api_ver"
    const val API_VER_10 = "1.0"

    const val FRIEND = "friend"
    const val CHAT = "chat"
}