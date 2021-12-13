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
package com.kakao.sdk.talk.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
 * 메시지 전송 API 호출 결과
 *
 * @property successfulReceiverUuids 메시지 전송에 성공한 대상의 uuid
 * @property failureInfos (복수의 전송 대상을 지정한 경우) 전송 실패한 일부 대상의 오류 정보
 */
@Parcelize
data class MessageSendResult(
    val successfulReceiverUuids: List<String>?,
    @SerializedName("failure_info") val failureInfos: List<MessageFailureInfo>?
) : Parcelable

/**
 * 여러 친구를 대상으로 메시지 전송 API 호출 시 대상 중 일부가 실패한 경우 오류 정보.
 *
 * @property code 오류 코드
 * @property receiverUuids 이 에러로 인해 실패한 대상 목록
 */
@Parcelize
data class MessageFailureInfo(val code: Int, val msg: String, val receiverUuids: List<String>) :
    Parcelable