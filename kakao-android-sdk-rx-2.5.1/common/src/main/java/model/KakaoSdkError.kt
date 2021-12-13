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

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.kakao.sdk.common.json.UnknownValue
import kotlinx.android.parcel.Parcelize

/**
 * 카카오 SDK 를 사용하면서 발생하는 에러 정보.
 */
@Suppress("Unused")
sealed class KakaoSdkError(open val msg: String) : RuntimeException(msg){
    /**
     * 유효하지 않은 토큰 에러인지 체크
     */
    fun isInvalidTokenError(): Boolean {
        when (this) {
            is AuthError -> {
                if (reason == AuthErrorCause.InvalidGrant) {
                    return true
                }
            }
            is ApiError -> {
                if (reason == ApiErrorCause.InvalidToken) {
                    return true
                }
            }
            else -> return false
        }
        return false
    }
}

/**
 * API 에러
 */
@Parcelize
data class ApiError(
    val statusCode: Int,
    val reason: ApiErrorCause,
    val response: ApiErrorResponse
) : KakaoSdkError(response.msg), Parcelable {
    companion object {
        fun fromScopes(scopes: List<String>): ApiError {
            return ApiError(
                403,
                ApiErrorCause.InsufficientScope,
                ApiErrorResponse(
                    ApiErrorCause.InsufficientScope.errorCode,
                    msg = "",
                    requiredScopes = scopes
                )
            )
        }
    }
}

/**
 * 로그인 에러
 */
@Parcelize
data class AuthError(
    val statusCode: Int,
    val reason: AuthErrorCause,
    val response: AuthErrorResponse
) : KakaoSdkError(response.errorDescription ?: response.error), Parcelable

/**
 * SDK 내에서 발생하는 클라이언트 에러
 */
@Parcelize
data class ClientError(
    val reason: ClientErrorCause,
    override val msg: String = "Client-side error"
) :
    KakaoSdkError(msg), Parcelable

/**
 * [AuthError]의 발생 원인
 */
enum class AuthErrorCause {
    /** 요청 파라미터 오류 */
    @SerializedName("invalid_request")
    InvalidRequest,

    /** 유효하지 않은 앱 */
    @SerializedName("invalid_client")
    InvalidClient,

    /** 유효하지 않은 scope ID */
    @SerializedName("invalid_scope")
    InvalidScope,

    /** 인증 수단이 유효하지 않아 인증할 수 없는 상태 */
    @SerializedName("invalid_grant")
    InvalidGrant,

    /** 설정이 올바르지 않음 (android key hash) */
    @SerializedName("misconfigured")
    Misconfigured,

    /** 앱이 요청 권한이 없음 */
    @SerializedName("unauthorized")
    Unauthorized,

    /** 접근이 거부 됨 (동의 취소) */
    @SerializedName("access_denied")
    AccessDenied,

    /** 서버 내부 에러 */
    @SerializedName("server_error")
    ServerError,

    /** 기타 에러 */
    @UnknownValue
    Unknown,
}

/**
 * [ApiError]의 발생 원인
 */
enum class ApiErrorCause(val errorCode: Int) {

    /** 기타 서버 에러 */
    @SerializedName("-1")
    InternalError(-1),

    /** 잘못된 파라미터 */
    @SerializedName("-2")
    IllegalParams(-2),

    /** 지원되지 않는 API */
    @SerializedName("-3")
    UnsupportedApi(-3),

    /** API 호출이 금지됨 */
    @SerializedName("-4")
    BlockedAction(-4),

    /** 호출 권한이 없음 */
    @SerializedName("-5")
    PermissionDenied(-5),

    /** 더이상 지원하지 않는 API */
    @SerializedName("-9")
    DeprecatedApi(-9),

    /** 쿼터 초과 */
    @SerializedName("-10")
    ApiLimitExceeded(-10),

    /** 연결되지 않은 사용자 */
    @SerializedName("-101")
    NotRegisteredUser(-101),

    /** 이미 연결된 사용자에 대해 signup 시도 */
    @SerializedName("-102")
    AlreadyRegisteredUser(-102),

    /** 존재하지 않는 카카오계정 */
    @SerializedName("-103")
    AccountDoesNotExist(-103),

    /** 등록되지 않은 user property key */
    @SerializedName("-201")
    PropertyKeyDoesNotExist(-201),

    /** 등록되지 않은 앱키의 요청 또는 존재하지 않는 앱으로의 요청. (앱키가 인증에 사용되는 경우는 -401 참조) */
    @SerializedName("-301")
    AppDoesNotExist(-301),

    /** 앱키 또는 토큰이 잘못된 경우. ex) 토큰 만료 */
    @SerializedName("-401")
    InvalidToken(-401),

    /** 해당 API에서 접근하는 리소스에 대해 사용자의 동의를 받지 않음 */
    @SerializedName("-402")
    InsufficientScope(-402),

    /** 앱의 연령제한에 대해 사용자 연령 인증 받지 않음 */
    @SerializedName("-405")
    RequiredAgeVerification(-405),

    /** 앱의 연령제한보다 사용자의 연령이 낮음 */
    @SerializedName("-406")
    UnderAgeLimit(-406),

    /** 카카오톡 사용자가 아님 */
    @SerializedName("-501")
    NotTalkUser(-501),

    /** 메시지 보낼 대상이 사용자의 친구가 아님 */
    @SerializedName("-502")
    NotFriend(-502),

    /** 지원되지 않는 기기로 메시지 보내는 경우 */
    @SerializedName("-504")
    UserDeviceUnsupported(-504),

    /** 메시지 수신자가 수신을 거부한 경우 */
    @SerializedName("-530")
    TalkMessageDisabled(-530),

    /** 월간 메시지 전송 허용 횟수 초과 */
    @SerializedName("-531")
    TalkSendMessageMonthlyLimitExceed(-531),

    /** 일간 메시지 전송 허용 횟수 초과 */
    @SerializedName("-532")
    TalkSendMessageDailyLimitExceed(-532),

    /** 카카오스토리 사용자가 아님 */
    @SerializedName("-601")
    NotStoryUser(-601),

    /** 카카오스토리 이미지 업로드 사이즈 제한 초과 */
    @SerializedName("-602")
    StoryImageUploadSizeExceeded(-602),

    /** 업로드,스크랩 등 오래 걸리는 API의 타임아웃 */
    @SerializedName("-603")
    TimeOut(-603),

    /** 잘못된 URL로 스크랩 요청한 경우 */
    @SerializedName("-604")
    StoryInvalidScrapUrl(-604),

    /** 유효하지 않은 스토리 아이디로 요청한 경우 */
    @SerializedName("-605")
    StoryInvalidPostId(-605),

    /** 이미지 업로드 시 허용된 업로드 파일 개수 초과 */
    @SerializedName("-606")
    StoryMaxUploadCountExceed(-606),

    /** 존재하지 않는 개발자가 생성한 앱에서 요청한 경우 */
    @SerializedName("-903")
    DeveloperDoesNotExist(-903),

    /** 서버 점검 중 */
    @SerializedName("-9798")
    UnderMaintenance(-9798),

    /** 기타 에러 */
    @UnknownValue
    Unknown(Int.MAX_VALUE);
}

/**
 * [ClientError]의 발생 원인
 */
enum class ClientErrorCause {

    /** 기타 에러 */
    Unknown,

    /** 요청 취소 */
    Cancelled,

    /** API 요청에 사용할 토큰이 없음 */
    TokenNotFound,

    /** 지원되지 않는 기능 */
    NotSupported,

    /** 잘못된 파라미터 */
    BadParameter,

    /** 정상적으로 실행할 수 없는 상태 */
    IllegalState
}


