package com.suzei.minote.utils

import se.simbio.encryption.Encryption

object Turing {

    fun encrypt(textToEncrypt: String): String {
        val encryption = Encryption.getDefault(
                "MiNoteEncryptKey",
                "MiNoteSalt",
                ByteArray(16))

        return encryption.encryptOrNull(textToEncrypt)
    }

    fun decrypt(textToDecrypt: String): String {
        val encryption = Encryption.getDefault(
                "MiNoteEncryptKey",
                "MiNoteSalt",
                ByteArray(16))

        return encryption.decryptOrNull(textToDecrypt)
    }

}
