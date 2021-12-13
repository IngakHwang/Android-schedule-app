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

import android.util.Base64
import com.kakao.sdk.common.model.ContextInfo
import com.kakao.sdk.common.KakaoSdk
import java.security.InvalidKeyException
import java.util.*
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec
import kotlin.experimental.xor

/**
 * @suppress
 */
class AESCipher(
    contextInfo: ContextInfo = KakaoSdk.applicationContextInfo
) : com.kakao.sdk.common.util.Cipher {
    private val keyGenAlgorithm = base64DecodeAndXor("My0oeSI1IzInbyA+LVFaW2wiNSokPAMiMipOLS4=")
    private val cipherAlgorithm = base64DecodeAndXor("Iio+ASgjKE4/ZSIjXDMOCUoCDww=")
    private val algorithm = "AES"
    private val ITER_COUNT = 2
    private val KEY_LENGTH = 256
    private val CHAR_SET = Charsets.UTF_8

    private val encryptor: Cipher
    private val decryptor: Cipher

    private val initVector =
        byteArrayOf(112, 78, 75, 55, -54, -30, -10, 44, 102, -126, -126, 92, -116, -48, -123, -55)
    private val IV_PARAMETER_SPEC = IvParameterSpec(initVector)

    init {
        val keyValue = contextInfo.signingKeyHash
        val factory = SecretKeyFactory.getInstance(keyGenAlgorithm)
        val keySpec = PBEKeySpec(
            keyValue.substring(0, Math.min(keyValue.length, 16)).toCharArray(),
            contextInfo.salt,
            ITER_COUNT,
            KEY_LENGTH
        )
        val tmp = factory.generateSecret(keySpec)
        val secret = SecretKeySpec(tmp.encoded, algorithm)

        encryptor = Cipher.getInstance(cipherAlgorithm)
        decryptor = Cipher.getInstance(cipherAlgorithm)

        try {
            encryptor.init(Cipher.ENCRYPT_MODE, secret, IV_PARAMETER_SPEC)
            decryptor.init(Cipher.DECRYPT_MODE, secret, IV_PARAMETER_SPEC)
        } catch (e: InvalidKeyException) {
            // Due to invalid key size. Using 128 bits instead.
            val shorterSecret =
                SecretKeySpec(Arrays.copyOfRange(tmp.encoded, 0, tmp.encoded.size / 2), algorithm)
            encryptor.init(Cipher.ENCRYPT_MODE, shorterSecret, IV_PARAMETER_SPEC)
            decryptor.init(Cipher.DECRYPT_MODE, shorterSecret, IV_PARAMETER_SPEC)
        }
    }

    override fun encrypt(value: String): String {
        return Base64.encodeToString(encryptor.doFinal(value.toByteArray(CHAR_SET)), Base64.NO_WRAP)
    }

    override fun decrypt(encrypted: String): String {
        return decryptor.doFinal(Base64.decode(encrypted, Base64.NO_WRAP)).toString(CHAR_SET)
    }


    private fun xorMessage(message: String): String? {
        return xorMessage(message, "com.kakao.api")
    }

    private fun xorMessage(message: String?, key: String?): String? {
        try {
            if (message == null || key == null) {
                return null
            }

            val keys = key.toCharArray()
            val msg = message.toCharArray()

            val ml = msg.size
            val kl = keys.size
            val newMsg = CharArray(ml)

            for (i in 0 until ml) {
                newMsg[i] = (msg[i].toByte() xor keys[i % kl].toByte()).toChar()
            }
            return String(newMsg)
        } catch (e: Exception) {
            return null
        }

    }

    private fun base64DecodeAndXor(source: String): String? {
        return xorMessage(String(Base64.decode(source, Base64.DEFAULT)))
    }
}