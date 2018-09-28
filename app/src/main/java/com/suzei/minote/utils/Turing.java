package com.suzei.minote.utils;

import se.simbio.encryption.Encryption;

public class Turing {

    public static String encrypt(String textToEncrypt) {
        Encryption encryption = Encryption.getDefault(
                "MiNoteEncryptKey",
                "MiNoteSalt",
                new byte[16]);

        return encryption.encryptOrNull(textToEncrypt);
    }

    public static String decrypt(String textToDecrypt) {
        Encryption encryption = Encryption.getDefault(
                "MiNoteEncryptKey",
                "MiNoteSalt",
                new byte[16]);

        return encryption.decryptOrNull(textToDecrypt);
    }

}
