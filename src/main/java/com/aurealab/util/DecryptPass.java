package com.aurealab.util;

import java.util.Base64;

public class DecryptPass {
    public static String decrypt(String encryptedValue) {
        byte[] decodedBytes = Base64.getDecoder().decode(encryptedValue);
        return new String(decodedBytes);
    }
}
