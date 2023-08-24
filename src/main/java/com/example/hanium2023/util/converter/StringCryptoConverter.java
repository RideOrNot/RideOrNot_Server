package com.example.hanium2023.util.converter;

import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import javax.persistence.AttributeConverter;
import javax.persistence.Convert;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Convert
public class StringCryptoConverter implements AttributeConverter<String, String> {
    @Value("${spring.database.encrypt.key}")
    private String dbEncryptKey;

    @Override
    public String convertToDatabaseColumn(String attribute) {
        try {
            Cipher encryptCipher;
            byte[] dbEncryptKeyBytes = dbEncryptKey.getBytes(StandardCharsets.UTF_8);
            encryptCipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            encryptCipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(dbEncryptKeyBytes, "AES"));
            return new String(Base64.getEncoder().encode(encryptCipher.doFinal(attribute.getBytes())));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        try {
            Cipher decryptCipher;
            byte[] dbEncryptKeyBytes = dbEncryptKey.getBytes(StandardCharsets.UTF_8);
            decryptCipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            decryptCipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(dbEncryptKeyBytes, "AES"));
            return new String(decryptCipher.doFinal(Base64.getDecoder().decode(dbData)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
