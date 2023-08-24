package com.example.hanium2023.util.converter;

import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import javax.crypto.Cipher;
import javax.persistence.AttributeConverter;
import javax.persistence.Convert;

@Convert
public class StringCryptoConverter implements AttributeConverter<String, String> {
    @Value("${spring.database.column.encrypt.key}")
    private String encryptKey;
    private Cipher encryptCipher;
    private Cipher decryptCipher;

    @PostConstruct
    public void init() throws Exception{
        encryptCipher = Cipher.getInstance("AES");
        encryptCipher.init(Cipher.ENCRYPT_MODE, generateMySQLAESKey(key, "UTF-8"));
        decryptCipher = Cipher.getInstance("AES");
        decryptCipher.init(Cipher.DECRYPT_MODE, generateMySQLAESKey(key, "UTF-8"));
    }
    @Override
    public String convertToDatabaseColumn(String attribute) {
        return null;
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        return null;
    }
}
