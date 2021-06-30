package com.example.foruma;

import android.util.Base64;
import android.util.Log;

import java.security.MessageDigest;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class Encryption {

    public static String encrypt(String message) {
        try {
            final MessageDigest msgDig = MessageDigest.getInstance("md5");
            final byte[] digestOfPassword = msgDig.digest("BliBli.com".getBytes("utf-8"));
            final byte[] keyBytes = Arrays.copyOf(digestOfPassword, 24);
            for (int j = 0, k = 16; j < 8;) {
                keyBytes[k++] = keyBytes[j++];
            }
            final SecretKey secKey = new SecretKeySpec(keyBytes, "AES");
            final Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secKey);
            final byte[] plainTextBytes = message.getBytes("utf-8");
            final byte[] cipherText = cipher.doFinal(plainTextBytes);
            return Base64.encodeToString(cipherText, Base64.NO_WRAP);
        } catch (Exception e) {
            Log.e("Encryption", "Error during encrypting");
        }
        return message;
    }

    public static String decrypt(String message) {
        try {
            final MessageDigest msgDig = MessageDigest.getInstance("md5");
            final byte[] digestOfPassword = msgDig.digest("BliBli.com".getBytes("utf-8"));
            final byte[] keyBytes = Arrays.copyOf(digestOfPassword, 24);
            for (int j = 0, k = 16; j < 8;) {
                keyBytes[k++] = keyBytes[j++];
            }
            final SecretKey secKey = new SecretKeySpec(keyBytes, "AES");
            final Cipher decipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            decipher.init(Cipher.DECRYPT_MODE, secKey);
            final byte[] plainText = decipher.doFinal(Base64.decode(message, Base64.NO_WRAP));
            return new String(plainText, "UTF-8");
        } catch (Exception e) {
            Log.e("Encryption", "Error during decrypting");
        }
        return message;
    }
}
