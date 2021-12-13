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
package com.kakao.sdk.navi.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.kakao.sdk.common.json.IntEnum
import com.kakao.sdk.navi.Constants
import kotlinx.android.parcel.Parcelize

/**
 * @suppress
 */
class KakaoNaviParams(
    val destination: Location,
    val option: NaviOption?,
    val viaList: List<Location>?
)

/**
 * 카카오내비에서 장소를 표현합니다.
 *
 * @property name 장소 이름. 예) 우리집, 회사
 * @property x 경도 좌표
 * @property y 위도 좌표
 * @property rpFlag
 */
@Parcelize
data class Location @JvmOverloads constructor(
    val name: String,
    val x: String,
    val y: String,
    @SerializedName(Constants.RP_FLAG) val rpFlag: String? = null
) : Parcelable

/**
 * 길안내 옵션을 설정합니다.
 *
 * @property coordType 사용할 좌표계
 * @property vehicleType 차종
 * @property rpOption 경로 옵션
 * @property routeInfo 전체 경로정보 보기 사용여부
 * @property startX 시작 위치의 경도 좌표
 * @property startY 시작 위치의 위도 좌표
 * @property startAngle 시작 차량 각도 (0 ~ 359)
 * @property returnUri 길안내 종료(전체 경로보기시 종료) 후 호출 될 URI.
 */
@Parcelize
class NaviOption @JvmOverloads constructor(
    val coordType: CoordType? = null,
    val vehicleType: VehicleType? = null,
    @SerializedName(Constants.RP_OPTION) val rpOption: RpOption? = null,
    val routeInfo: Boolean? = null,
    @SerializedName(Constants.S_X) val startX: String? = null,
    @SerializedName(Constants.S_Y) val startY: String? = null,
    @SerializedName(Constants.S_ANGLE) val startAngle: Int? = null,
    val returnUri: String? = null
) : Parcelable

/**
 * 좌표계 타입을 선택합니다.
 */
enum class CoordType {
    /**
     * World Geodetic System 84 좌표계
     */
    @SerializedName("wgs84")
    WGS84,

    /**
     * Katec 좌표계 (서버 기본값)
     */
    @SerializedName("katec")
    KATEC;
}

/**
 * 안내할 경로를 최적화하기 위한 옵션입니다.
 */
@IntEnum
enum class RpOption {
    /**
     * Fastest route
     */
    @SerializedName("1")
    FAST, // 빠른길
    /**
     * Free route
     */
    @SerializedName("2")
    FREE, // 무료도로
    /**
     * Shortest route
     */
    @SerializedName("3")
    SHORTEST, // 최단거리
    /**
     * Exclude motorway
     */
    @SerializedName("4")
    NO_AUTO, // 자동차전용제외
    /**
     * Wide road first
     */
    @SerializedName("5")
    WIDE, // 큰길우선
    /**
     * Highway first
     */
    @SerializedName("6")
    HIGHWAY, // 고속도로우선
    /**
     * Normal road first
     */
    @SerializedName("8")
    NORMAL, // 일반도로우선
    /**
     * Recommended route (Current default option if not set)
     */
    @SerializedName("100")
    RECOMMENDED; // 추천경로 (기본값)
}

/**
 * 길안내를 사용할 차종(1~7)을 선택합니다.
 */
@IntEnum
enum class VehicleType {
    /**
     * 1종 (승용차/소형승합차/소형화물화)
     */
    @SerializedName("1")
    FIRST,
    /**
     *  2종 (중형승합차/중형화물차)
     */
    @SerializedName("2")
    SECOND,
    /**
     * 3종 (대형승합차/2축 대형화물차)
     */
    @SerializedName("3")
    THIRD,
    /**
     * 4종 (3축 대형화물차)
     */
    @SerializedName("4")
    FOURTH,
    /**
     * 5종 (4축이상 특수화물차)
     */
    @SerializedName("5")
    FIFTH,
    /**
     * 6종 (경차)
     */
    @SerializedName("6")
    SIXTH,
    /**
     * 이륜차
     */
    @SerializedName("7")
    TWO_WHEEL;
}