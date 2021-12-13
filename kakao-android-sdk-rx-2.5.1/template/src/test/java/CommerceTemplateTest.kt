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

import com.kakao.sdk.template.Constants
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

class CommerceTemplateTest {
    @Test fun objectType() {
        val template = CommerceTemplate(
                Content("title", "imageUrl", Link()),
                Commerce(10000)
        )

        assertEquals(Constants.TYPE_COMMERCE, template.objectType)
        assertNotNull(template.commerce)
        assertEquals(10000, template.commerce.regularPrice)
    }
}