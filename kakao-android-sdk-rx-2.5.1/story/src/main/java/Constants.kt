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
package com.kakao.sdk.story

/**
 * @suppress
 */
object Constants {
    const val IS_STORY_USER_PATH = "/v1/api/story/isstoryuser"
    const val STORY_PROFILE_PATH = "/v1/api/story/profile"
    const val GET_STORY_PATH = "/v1/api/story/mystory"
    const val GET_STORIES_PATH = "/v1/api/story/mystories"
    const val POST_NOTE_PATH = "/v1/api/story/post/note"
    const val POST_PHOTO_PATH = "/v1/api/story/post/photo"
    const val POST_LINK_PATH = "/v1/api/story/post/link"

    const val DELETE_STORY_PATH = "/v1/api/story/delete/mystory"
    const val SCRAP_LINK_PATH = "/v1/api/story/linkinfo"
    const val SCRAP_IMAGES_PATH = "/v1/api/story/upload/multi"

    const val SECURE_RESOURCE = "secure_resource"
    const val ID = "id"
    const val LAST_ID = "last_id"

    const val URL = "url"

    const val IS_STORY_USER = "isStoryUser"

    // StoryProfile
    const val NICKNAME = "nickName"
    const val PROFILE_IMAGE_URL = "profileImageURL"
    const val THUMBNAIL_URL = "thumbnailURL"
    const val BG_IMAGE_URL = "bgImageURL"
    const val PERMALINK = "permalink"
    const val BIRTHDAY = "birthday"
    const val BIRTHDAY_TYPE = "birthdayType"

    const val MEDIA_TYPE = "media_type"
    const val CREATED_AT = "created_at"
    const val COMMENT_COUNT = "comment_count"
    const val LIKE_COUNT = "like_count"
    const val MEDIA = "media"
    const val LIKES = "likes"
    const val COMMENTS = "comments"

    const val CONTENT = "content"
    const val IMAGE_URL_LIST = "image_url_list"
    const val LINK_INFO = "link_info"
    const val PERMISSION = "permission"
    const val ENABLE_SHARE = "enable_share"
    const val ANDROID_EXEC_PARAM = "android_exec_param"
    const val IOS_EXEC_PARAM = "ios_exec_param"
    const val ANDROID_MARKET_PARAM = "android_market_param"
    const val IOS_MARKET_PARAM = "ios_market_param"

    const val REQUESTED_URL = "requested_url"
    const val HOST = "host"
    const val TITLE = "title"
    const val DESCRIPTION = "description"
    const val SECTION = "section"
    const val TYPE = "type"
    const val IMAGE = "image"
    const val FILE = "file"
}