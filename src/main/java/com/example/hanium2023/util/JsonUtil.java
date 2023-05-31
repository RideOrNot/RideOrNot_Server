package com.example.hanium2023.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class JsonUtil {
    public <T> List<T> convertJsonArrayToDtoList(JSONArray jsonArray, Class<T> dtoClass) {
        List<T> dtoList = new ArrayList<>();
        for(int i = 0; i<jsonArray.size();i++){
            JSONObject jsonObject = (JSONObject) jsonArray.get(i);
            T dto = convertJsonObjectToDto(jsonObject, dtoClass);
            dtoList.add(dto);
        }
        return dtoList;
    }

    public <T> T convertJsonObjectToDto(JSONObject jsonObject, Class<T> dtoClass) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(jsonObject.toString(), dtoClass);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public JSONObject parseJsonObject(String jsonString) {
        JSONParser jsonParser = new JSONParser();
        try {
            return (JSONObject) jsonParser.parse(jsonString);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
