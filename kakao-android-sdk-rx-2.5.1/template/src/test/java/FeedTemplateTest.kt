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
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

/**
example:

```
{
"object_type": "feed",
"
}
```
 */
class FeedTemplateTest {
    @Test
    fun objectTypeExists() {
        val template = FeedTemplate(Content("title", "imageUrl", Link()))
        assertEquals(Constants.TYPE_FEED, template.objectType)
    }

    @Test
    fun social() {
        val url = "https://developers.kakao.com"
        val templateObject = """
{
  "object_type": "feed",
  "url":"$url"
}
            }
        """.trimIndent()

        val template = FeedTemplate(
            content = Content(
                "title", "imageUrl", Link(
                    webUrl = "", mobileWebUrl = ""
                )
            ),
            social = Social(likeCount = 1)
        )
        assertNotNull(template.social)
        assertEquals(1, template.social?.likeCount)
    }

    @Test
    fun addButtons() {
        val template = FeedTemplate(
            content = Content("title", "imageUrl", Link()),
            social = Social(likeCount = 1),
            buttons = listOf(
                Button("title", Link()),
                Button("title", Link())
            )
        )
        assertEquals(2, template.buttons?.size)
    }
}