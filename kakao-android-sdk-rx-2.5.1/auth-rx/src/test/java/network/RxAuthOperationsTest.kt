///*
//  Copyright 2019 Kakao Corp.
//
//  Licensed under the Apache License, Version 2.0 (the "License");
//  you may not use this file except in compliance with the License.
//  You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
//  Unless required by applicable law or agreed to in writing, software
//  distributed under the License is distributed on an "AS IS" BASIS,
//  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//  See the License for the specific language governing permissions and
//  limitations under the License.
// */
//package com.kakao.sdk.auth.network
//
//import com.google.gson.JsonObject
//import com.kakao.sdk.auth.model.OAuthToken
//import com.kakao.sdk.auth.*
//import com.kakao.sdk.auth.model.AccessTokenResponse
//import com.kakao.sdk.common.util.Utility
//import com.kakao.sdk.common.model.ApiError
//import com.kakao.sdk.common.model.ApiErrorCause
//import com.kakao.sdk.network.RxOperations
//import io.reactivex.Single
//import io.reactivex.observers.TestObserver
//import okhttp3.MediaType
//import okhttp3.ResponseBody
//import org.junit.jupiter.api.BeforeEach
//import org.junit.jupiter.api.Test
//import org.junit.jupiter.params.ParameterizedTest
//import org.junit.jupiter.params.provider.Arguments
//import org.junit.jupiter.params.provider.MethodSource
//import java.net.HttpURLConnection
//import java.util.stream.Stream
//import org.mockito.Mockito.*
//import retrofit2.HttpException
//import retrofit2.Response
//import java.util.*
//import java.util.concurrent.Callable
//
//class RxAuthOperationsTest {
//
//    private lateinit var authOperations: RxAuthOperations
//    private lateinit var authApiClient: RxAuthApiClient
//    private lateinit var tokenManager: TokenManagable
//
//    @BeforeEach
//    fun setup() {
//        tokenManager = spy(TestTokenManager(testOAuthToken()))
//        authApiClient = mock(RxAuthApiClient::class.java)
//
//        doReturn(
//            Single.just(
//                testTokenResponseRefreshTokenGrant()))
//            .`when`(authApiClient)
//            .refreshAccessToken(tokenManager.getToken()!!, anyString(), anyString())   // TODO: 이 부분 OAuthToken으로 바꾸고나서 stubing 안되는 거 수정해야 함
//        authOperations = RxAuthOperations(
//            authApiClient,
//            tokenManager,
//            TestApplicationInfo(),
//            TestContextInfo()
//        )
//    }
//
//    @MethodSource("httpErrorProvider")
//    @ParameterizedTest
//    fun httpErrors(httpStatus: Int, body: String, errorCode: Int, reason: ApiErrorCause) {
//        val retrofitResponse = Response.error<Void>(
//            httpStatus,
//            ResponseBody.create(MediaType.parse("application/json"), body)
//        )
//        val exception = HttpException(retrofitResponse)
//        val observer = TestObserver<Void>()
//        Single.error<Void>(exception)
//            .compose(RxOperations.handleApiError())
//            .compose(authOperations.handleApiError())
//            .subscribe(observer)
//        observer.assertError {
//            it is ApiError && it.reason == reason
//        }
//    }
//
//    @Test
//    fun clientError() {
//        val observer = TestObserver<Void>()
//        Single.error<Void>(NullPointerException())
//            .compose(RxOperations.handleApiError())
//            .compose(authOperations.handleApiError())
//            .subscribe(observer)
//
//        observer.assertError {
//            it.javaClass == NullPointerException::class.java
//        }
//    }
//
//    @Test
//    fun refreshTokenSucceeds() {
//        val expectedValue = "success"
//        val retrofitResponse = Response.error<Void>(
//            HttpURLConnection.HTTP_UNAUTHORIZED,
//            ResponseBody.create(
//                MediaType.parse("application/json"),
//                Utility.getJson("json/api_errors/invalid_token.json")
//            )
//        )
//        val exception = HttpException(retrofitResponse)
//        val observer = TestObserver<Any>()
//
//        val callable = object : Callable<String> {
//            var subscribedBefore = false
//            override fun call(): String {
//                if (subscribedBefore) {
//                    return expectedValue
//                }
//                subscribedBefore = true
//                throw exception
//            }
//        }
//
//        Single.fromCallable(callable)
//            .compose(RxOperations.handleApiError())
//            .compose(authOperations.handleApiError())
//            .subscribe(observer)
//        observer.assertNoErrors()
//        observer.assertComplete()
//        observer.assertValueCount(1)
//        observer.assertValue(expectedValue)
//    }
//
//    @Test
//    fun refreshTokenFails() {
//
//    }
//
//    companion object {
//        @Suppress("unused")
//        @JvmStatic
//        fun httpErrorProvider(): Stream<Arguments> {
//            return Stream.of(
//                Arguments.of(
//                    HttpURLConnection.HTTP_FORBIDDEN,
//                    Utility.getJson("json/api_errors/invalid_scope.json"),
//                    ApiErrorCause.InsufficientScope.errorCode,
//                    ApiErrorCause.InsufficientScope
//                ),
//                Arguments.of(
//                    HttpURLConnection.HTTP_INTERNAL_ERROR,
//                    Utility.getJson("json/api_errors/internal_error.json"),
//                    ApiErrorCause.InternalError.errorCode,
//                    ApiErrorCause.InternalError
//                ),
//                Arguments.of(
//                    HttpURLConnection.HTTP_UNAUTHORIZED,
//                    Utility.getJson("json/api_errors/need_age_auth.json"),
//                    ApiErrorCause.AgeVerificationRequired.errorCode,
//                    ApiErrorCause.AgeVerificationRequired
//                )
//            )
//        }
//    }
//}