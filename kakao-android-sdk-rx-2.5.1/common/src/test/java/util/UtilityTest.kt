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
package com.kakao.sdk.common.util

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

class UtilityTest {
    @MethodSource("paramsProvider")
    @ParameterizedTest fun buildQuery(params: Map<String, String>) {
        val query = Utility.buildQuery(params)
        assertNotNull(query)
    }

    @Nested
    inner class ParseQuery {
        @Test fun withNull() {
            val params = Utility.parseQuery(null)
            assertEquals(0, params.size)
        }

        @Test fun withEmpty() {
            val params = Utility.parseQuery("")
            assertEquals(0, params.size)
        }

        @Test fun withOneParam() {
            val params = Utility.parseQuery("key1=value1")
            assertEquals(1, params.size)
        }

        @Test fun withTwoParams() {
            val params = Utility.parseQuery("key1=value1&key2=value2")
            assertEquals(2, params.size)
            assertEquals("value1", params["key1"])
            assertEquals("value2", params["key2"])
        }
    }

    companion object {
        @Suppress("unused")
        @JvmStatic fun paramsProvider(): Stream<Arguments> {
            return Stream.of(
                    Arguments.of(mapOf<String, String>()),
                    Arguments.of(mapOf(Pair("key1", "value1"))),
                    Arguments.of(mapOf(Pair("key1", "value1"), Pair("key2", "value2")))
            )
        }
    }
}