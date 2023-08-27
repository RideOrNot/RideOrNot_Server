package com.example.hanium2023.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

@Component
public class EncryptionUtil {
    public static String dbEncryptKey;

    @Value("${spring.database.encrypt.key}")
    public void setDbEncryptKey(String value) {
        dbEncryptKey = value;
    }

    public static String encryptString(String input) {
        try {
            byte[] keyBytes = dbEncryptKey.getBytes(StandardCharsets.UTF_8);
            byte[] ivBytes = generateIV();

            Cipher encryptCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            IvParameterSpec ivParamSpec = new IvParameterSpec(ivBytes);
            encryptCipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(keyBytes, "AES"), ivParamSpec);

            byte[] encryptedBytes = encryptCipher.doFinal(input.getBytes(StandardCharsets.UTF_8));
            byte[] combinedIVAndCiphertext = new byte[ivBytes.length + encryptedBytes.length];
            System.arraycopy(ivBytes, 0, combinedIVAndCiphertext, 0, ivBytes.length);
            System.arraycopy(encryptedBytes, 0, combinedIVAndCiphertext, ivBytes.length, encryptedBytes.length);

            return Base64.getEncoder().encodeToString(combinedIVAndCiphertext);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String decryptString(String encryptedData) {
        try {
            byte[] keyBytes = dbEncryptKey.getBytes(StandardCharsets.UTF_8);
            byte[] combinedIVAndCiphertext = Base64.getDecoder().decode(encryptedData);

            byte[] ivBytes = new byte[16];
            byte[] encryptedBytes = new byte[combinedIVAndCiphertext.length - ivBytes.length];
            System.arraycopy(combinedIVAndCiphertext, 0, ivBytes, 0, ivBytes.length);
            System.arraycopy(combinedIVAndCiphertext, ivBytes.length, encryptedBytes, 0, encryptedBytes.length);

            Cipher decryptCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            IvParameterSpec ivParamSpec = new IvParameterSpec(ivBytes);
            decryptCipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(keyBytes, "AES"), ivParamSpec);
            byte[] decryptedBytes = decryptCipher.doFinal(encryptedBytes);

            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static byte[] generateIV() {
        byte[] iv = new byte[16]; // 16 bytes for AES
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);
        return iv;
    }
}
