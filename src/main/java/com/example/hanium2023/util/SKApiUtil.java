package com.example.hanium2023.util;

import org.json.simple.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class SKApiUtil {

    public HttpRequest makeAvailableStationUri(String key){
        //api 호출
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://apis.openapi.sk.com/puzzle/subway/meta/stations?type=exit"
                        //api 호출
                ))
                .header("accept", "application/json")
                .header("Content-Type", "application/json")
                .header("appKey", key)
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        return request;
    }

    public HttpRequest makePassengerPerDayUri(String key, String stationCode, String day){
        //api 호출
        StringBuilder uri = new StringBuilder();
        uri.append("https://apis.openapi.sk.com/puzzle/subway/exit/stat/dow/stations/")
                .append(stationCode)
                .append("?gender=all&ageGrp=all&dow=")
                .append(day);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri.toString()))
                .header("accept", "application/json")
                .header("Content-Type", "application/json")
                .header("appKey", key)
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        return request;
    }

    public HttpRequest makePassengerByTimeUri(String key, String stationCode, String datetime){
        //api 호출
        StringBuilder uri = new StringBuilder();
        uri.append("https://apis.openapi.sk.com/puzzle/subway/exit/raw/hourly/stations/")
                .append(stationCode)
                .append("?gender=all&ageGrp=all&date=")
                .append(datetime);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri.toString()))
                .header("accept", "application/json")
                .header("Content-Type", "application/json")
                .header("appKey", key)
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        return request;
    }
    public static JSONObject getAvailableStationApiResult(String key) throws IOException, InterruptedException {
        HttpResponse<String> response = HttpClient.newHttpClient().send(
                new SKApiUtil().makeAvailableStationUri(key),
                HttpResponse.BodyHandlers.ofString());
        return new JsonUtil().parseJsonObject(response.body().toString());
    }

    public static JSONObject getPassengerPerDayApiResult(String key, String stationCode, String day) throws IOException, InterruptedException {
        HttpResponse<String> response = HttpClient.newHttpClient().send(new SKApiUtil().makePassengerPerDayUri(key, stationCode,day),
                HttpResponse.BodyHandlers.ofString());
        return new JsonUtil().parseJsonObject(response.body().toString());
    }

    public static JSONObject getPassengerByTimeApiResult(String key, String stationCode, String datetime) throws IOException, InterruptedException {
        HttpResponse<String> response = HttpClient.newHttpClient().send(new SKApiUtil().makePassengerByTimeUri(key, stationCode, datetime),
                HttpResponse.BodyHandlers.ofString());
        return new JsonUtil().parseJsonObject(response.body().toString());
    }
}
