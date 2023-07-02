package com.example.hanium2023.util;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;


import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.LinkedHashMap;
import java.util.Map;

public class KatecToLatLong {
    @Value("${public-api-key.lat-lon-key}")
    private String latLonKey;


    public HttpRequest makeUri(String lat, String lon){
        StringBuilder uri = new StringBuilder();
        uri.append("https://apis.openapi.sk.com/tmap/geo/coordconvert?version=1&")
                .append("lat=")
                .append(lat)
                .append("&lon=")
                .append(lon)
                .append("&fromCoord=KATECH&toCoord=WGS84GEO");
        //api 호출
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri.toString()))
                .header("accept", "application/json")
                .header("appKey", this.latLonKey )
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        return request;
    }
    public static LinkedHashMap<String,String> getLatLon(String lat, String lon) throws IOException, InterruptedException {
        //response(json)을 map으로 변경
        HttpResponse<String> response = HttpClient.newHttpClient().send(new KatecToLatLong().makeUri(lat,lon), HttpResponse.BodyHandlers.ofString());
        Map<String,Object> bodyCoordinate = null;
        bodyCoordinate = new ObjectMapper().readValue(response.body(), Map.class) ;

        //coordinate 객체 내의 lat, lon 값 얻기
        LinkedHashMap<String,String> coordinate = (LinkedHashMap<String, String>) bodyCoordinate.get("coordinate");
        return coordinate;
    }

}
