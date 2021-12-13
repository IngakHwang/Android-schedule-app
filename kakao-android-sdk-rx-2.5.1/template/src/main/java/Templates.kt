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
package com.kakao.sdk.template.model

import android.os.Parcelable
import com.kakao.sdk.template.Constants
import kotlinx.android.parcel.Parcelize

/**
 * Kakao SDK의 기본 템플릿을 나타내는 인터페이스.
 *
 * 별도 템플릿을 만들지 않고도 소스코드 레벨에서 간단하게 템플릿을 작성할 수 있도록 기본 템플릿 제공.
 * 이 모듈에서 제공되는 모든 템플릿 클래스는 이 인터페이스를 구현하고 있음. 생성된 템플릿으로 카카오링크, 카카오톡 메시지 전송에 활용 가능.
 */
interface DefaultTemplate

/**
 * 기본 템플릿으로 제공되는 피드 템플릿 클래스.
 *
 * @property content 메시지의 내용. 텍스트 및 이미지, 링크 정보 포함.
 * @property social 댓글수, 좋아요수 등, 컨텐츠에 대한 소셜 정보.
 * @property buttons 버튼 목록. 버튼 타이틀과 링크를 변경하고 싶을때, 버튼 두개를 사용하고 싶을때 사용. (최대 2개)
 * @property buttonTitle 기본 버튼 타이틀(자세히 보기)을 변경하고 싶을 때 설정. 이 값을 사용하면 클릭 시 이동할 링크는 content 에 입력된 값이 사용됨.
 */
@Parcelize
data class FeedTemplate @JvmOverloads constructor(
    val content: Content,
    val social: Social? = null,
    val buttons: List<Button>? = null,
    val buttonTitle: String? = null
) : DefaultTemplate, Parcelable {

    /** "feed" 고정 값 */
    val objectType = Constants.TYPE_FEED
}

/**
 * 여러 개의 컨텐츠를 리스트 형태로 보여줄 수 있는 메시지 템플릿 클래스.
 *
 * @property headerTitle 리스트 상단에 노출되는 헤더 타이틀 (최대 200자)
 * @property headerLink 헤더 타이틀 내용에 해당하는 링크 정보
 * @deprecated 더 이상 카카오톡에서 사용되지 않으므로 2.0.3 버전에서 삭제 됨. @property headerImageUrl 리스트 템플릿의 상단에 보이는 이미지 URL
 * @deprecated 더 이상 카카오톡에서 사용되지 않으므로 2.0.3 버전에서 삭제 됨. @property headerImageWidth 리스트 템플릿의 상단에 보이는 이미지 widht, 권장 800 (단위: 픽셀)
 * @deprecated 더 이상 카카오톡에서 사용되지 않으므로 2.0.3 버전에서 삭제 됨. @property headerImageHeight 리스트 템플릿의 상단에 보이는 이미지 height, 권장 190 (단위: 픽셀)
 * @property contents 리스트에 노출되는 컨텐츠 목록 (최소 2개, 최대 3개)
 * @property buttons 버튼 목록. 버튼 타이틀과 링크를 변경하고 싶을때, 버튼 두개를 사용하고 싶을때 사용. (최대 2개)
 * @property buttonTitle 기본 버튼 타이틀(자세히 보기)을 변경하고 싶을 때 설정. 이 값을 사용하면 클릭 시 이동할 링크는 content 에 입력된 값이 사용됨.
 */
@Parcelize
data class ListTemplate @JvmOverloads constructor(
    val headerTitle: String,
    val headerLink: Link,
    val contents: List<Content>,
    val buttons: List<Button>? = null,
    val buttonTitle: String? = null
) : DefaultTemplate, Parcelable {

    /** "list" 고정 값 */
    val objectType = Constants.TYPE_LIST
}

/**
 * 기본 템플릿으로 제공되는 커머스 템플릿 클래스
 *
 * @property content 메시지의 내용. 텍스트 및 이미지, 링크 정보 포함.
 * @property commerce 컨텐츠에 대한 가격 정보
 * @property buttons 버튼 목록. 버튼 타이틀과 링크를 변경하고 싶을때, 버튼 두개를 사용하고 싶을때 사용. (최대 2개)
 * @property buttonTitle 기본 버튼 타이틀(자세히 보기)을 변경하고 싶을 때 설정. 이 값을 사용하면 클릭 시 이동할 링크는 content 에 입력된 값이 사용됨.
 */
@Parcelize
data class CommerceTemplate @JvmOverloads constructor(
    val content: Content,
    val commerce: Commerce,
    val buttons: List<Button>? = null,
    val buttonTitle: String? = null
) : DefaultTemplate, Parcelable {

    /** "commerce" 고정 값 */
    val objectType = Constants.TYPE_COMMERCE
}

/**
 * 주소를 이용하여 특정 위치를 공유할 수 있는 메시지 템플릿.
 *
 * @property address 공유할 위치의 주소. 예) 경기 성남시 분당구 판교역로 235
 * @property addressTitle 카카오톡 내의 지도 뷰에서 사용되는 타이틀. 예) 카카오판교오피스
 * @property content 위치에 대해 설명하는 컨텐츠 정보
 * @property social 댓글수, 좋아요수 등, 컨텐츠에 대한 소셜 정보
 * @property buttons 버튼 목록. 기본 버튼의 타이틀 외에 링크도 변경하고 싶을 때 설정. (최대 1개, 오른쪽 위치 보기 버튼은 고정)
 * @property buttonTitle 기본 버튼 타이틀(자세히 보기)을 변경하고 싶을 때 설정. 이 값을 사용하면 클릭 시 이동할 링크는 content에 입력된 값이 사용됨.
 */
@Parcelize
data class LocationTemplate @JvmOverloads constructor(
    val address: String,
    val content: Content,
    val addressTitle: String? = null,
    val social: Social? = null,
    val buttons: List<Button>? = null,
    val buttonTitle: String? = null
) : DefaultTemplate, Parcelable {

    /** "location" 고정 값 */
    val objectType = Constants.TYPE_LOCATION
}

/**
 * 텍스트형 기본 템플릿 클래스
 *
 * @property text 메시지에 들어갈 텍스트 (최대 200자)
 * @property link 컨텐츠 클릭 시 이동할 링크 정보
 * @property buttons 버튼 목록. 버튼 타이틀과 링크를 변경하고 싶을때, 버튼 두개를 사용하고 싶을때 사용. (최대 2개)
 * @property buttonTitle 기본 버튼 타이틀(자세히 보기)을 변경하고 싶을 때 설정. 이 값을 사용하면 클릭 시 이동할 링크는 content에 입력된 값이 사용됨.
 */
@Parcelize
data class TextTemplate @JvmOverloads constructor(
    val text: String,
    val link: Link,
    val buttons: List<Button>? = null,
    val buttonTitle: String? = null
) : DefaultTemplate, Parcelable {

    /** "text" 고정 값 */
    val objectType = Constants.TYPE_TEXT
}