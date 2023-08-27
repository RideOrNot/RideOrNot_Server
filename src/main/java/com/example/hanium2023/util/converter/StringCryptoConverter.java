package com.example.hanium2023.util.converter;

import org.springframework.beans.factory.annotation.Value;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.persistence.AttributeConverter;
import javax.persistence.Convert;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

@Convert
public class StringCryptoConverter implements AttributeConverter<String, String> {
    @Value("${spring.database.encrypt.key}")
    private String dbEncryptKey;

    @Override
    public String convertToDatabaseColumn(String attribute) {
        try {
            byte[] dbEncryptKeyBytes = dbEncryptKey.getBytes(StandardCharsets.UTF_8);
            byte[] ivBytes = generateIV();
            Cipher encryptCipher;

            encryptCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            IvParameterSpec ivParamSpec = new IvParameterSpec(ivBytes);
            encryptCipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(dbEncryptKeyBytes, "AES"), ivParamSpec);

            byte[] encryptedBytes = encryptCipher.doFinal(attribute.getBytes(StandardCharsets.UTF_8));
            byte[] combinedIVAndCiphertext = new byte[ivBytes.length + encryptedBytes.length];
            System.arraycopy(ivBytes, 0, combinedIVAndCiphertext, 0, ivBytes.length);
            System.arraycopy(encryptedBytes, 0, combinedIVAndCiphertext, ivBytes.length, encryptedBytes.length);

            return Base64.getEncoder().encodeToString(combinedIVAndCiphertext);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        try {
//            Cipher decryptCipher;
//            byte[] dbEncryptKeyBytes = dbEncryptKey.getBytes("UTF-8");
//            decryptCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
//            IvParameterSpec ivParamSpec = new IvParameterSpec(dbEncryptKey.substring(0, 16).getBytes("UTF-8"));
//            decryptCipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(dbEncryptKeyBytes, "AES"), ivParamSpec);
//            return new String(decryptCipher.doFinal(Base64.getDecoder().decode(dbData)), "UTF-8");
            byte[] dbEncryptKeyBytes = dbEncryptKey.getBytes(StandardCharsets.UTF_8);
            byte[] combinedIVAndCiphertext = Base64.getDecoder().decode(dbData);

            byte[] ivBytes = new byte[16];
            byte[] encryptedBytes = new byte[combinedIVAndCiphertext.length - ivBytes.length];
            System.arraycopy(combinedIVAndCiphertext, 0, ivBytes, 0, ivBytes.length);
            System.arraycopy(combinedIVAndCiphertext, ivBytes.length, encryptedBytes, 0, encryptedBytes.length);

            Cipher decryptCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            IvParameterSpec ivParamSpec = new IvParameterSpec(ivBytes);
            decryptCipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(dbEncryptKeyBytes, "AES"), ivParamSpec);
            byte[] decryptedBytes = decryptCipher.doFinal(encryptedBytes);

            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private byte[] generateIV() {
        byte[] iv = new byte[16]; // 16 bytes for AES
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);
        return iv;
    }
}
