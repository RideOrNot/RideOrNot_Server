package com.example.hanium2023.service;

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

@Service
public class PublicApiService {
    @Value("${public-api-key.real-time-key}")
    private String realTimeApiKey;

    public JSONArray getRealTimeInfos(String stationName){
        String apiResult = getApiResult(buildRealTimeApiUrl(stationName));
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject = null;
        try {
            jsonObject = (JSONObject) jsonParser.parse(apiResult.toString());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return (JSONArray) jsonObject.get("realtimeArrivalList");
    }
    private String getApiResult(String apiUrl){
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
        return result.toString();
    }

    private String buildRealTimeApiUrl(String stationName){
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
