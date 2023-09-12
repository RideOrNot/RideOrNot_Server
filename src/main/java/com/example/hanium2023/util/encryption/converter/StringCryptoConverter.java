package com.example.hanium2023.util.encryption.converter;

import com.example.hanium2023.util.EncryptionUtil;
import org.springframework.beans.factory.annotation.Value;

import javax.persistence.AttributeConverter;
import javax.persistence.Convert;

@Convert
public class StringCryptoConverter implements AttributeConverter<String, String> {
    @Override
    public String convertToDatabaseColumn(String attribute) {
        return EncryptionUtil.encryptString(attribute);
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        return EncryptionUtil.decryptString(dbData);
    }

}
