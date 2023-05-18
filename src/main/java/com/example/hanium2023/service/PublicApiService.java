package com.example.hanium2023.service;

import com.example.hanium2023.domain.dto.ArrivalInfoApiResult;
import com.example.hanium2023.util.JsonUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PublicApiService {
    @Value("${public-api-key.real-time-key}")
    private String realTimeApiKey;
    private final JsonUtil jsonUtil;

    public List<ArrivalInfoApiResult> getRealTimeInfos(String stationName) {
        JSONObject apiResultJsonObject = getApiResult(buildRealTimeApiUrl(stationName));
        JSONArray jsonArray = (JSONArray) apiResultJsonObject.get("realtimeArrivalList");

        return jsonUtil.convertJsonArrayToDtoList(jsonArray, ArrivalInfoApiResult.class);
    }

    private JSONObject getApiResult(String apiUrl) {
        StringBuilder result = new StringBuilder();
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(apiUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            BufferedReader bf = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));
            String returnLine;
            while ((returnLine = bf.readLine()) != null) {
                result.append(returnLine + "\n\r");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        urlConnection.disconnect();

        return jsonUtil.parseJsonObject(result.toString());
    }

    private String buildRealTimeApiUrl(String stationName) {
        StringBuilder urlBuilder = new StringBuilder("http://swopenAPI.seoul.go.kr/api/subway/");
        try {
            urlBuilder.append(URLEncoder.encode(realTimeApiKey, "UTF-8"));
            urlBuilder.append("/json/realtimeStationArrival/0/20/");
            urlBuilder.append(URLEncoder.encode(stationName, "UTF-8"));
            return urlBuilder.toString();
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }


}
