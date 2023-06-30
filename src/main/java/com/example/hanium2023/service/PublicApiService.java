package com.example.hanium2023.service;

import com.example.hanium2023.domain.dto.ArrivalInfoApiResult;
import com.example.hanium2023.domain.dto.ArrivalInfoResponse;
import com.example.hanium2023.domain.entity.StationExitTmp;
import com.example.hanium2023.repository.StationExitTmpRepository;
import com.example.hanium2023.util.CsvParsing;
import com.example.hanium2023.util.JsonUtil;
import com.example.hanium2023.util.KatecToLatLong;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.List;

import java.time.LocalDateTime;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PublicApiService {
    @Value("${public-api-key.real-time-key}")
    private String realTimeApiKey;
    private final JsonUtil jsonUtil;
    private final StationExitTmpRepository stationExitTmpRepository;

    public List<ArrivalInfoResponse> getRealTimeInfos(String stationName) {
        JSONObject apiResultJsonObject = getApiResult(buildRealTimeApiUrl(stationName));
        JSONArray jsonArray = (JSONArray) apiResultJsonObject.get("realtimeArrivalList");

        List<ArrivalInfoApiResult> arrivalInfoApiResults = jsonUtil.convertJsonArrayToDtoList(jsonArray, ArrivalInfoApiResult.class);
        arrivalInfoApiResults.forEach(this::correctArrivalTime);
        return arrivalInfoApiResults
                .stream()
                .map(ArrivalInfoResponse::new)
                .collect(Collectors.toList());
    }

    private void correctArrivalTime(ArrivalInfoApiResult apiResult) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime targetTime = LocalDateTime.parse(apiResult.getCreatedAt(), formatter);
        Duration timeGap = Duration.between(targetTime, currentTime);
        long correctedArrivalTime = apiResult.getArrivalTime() - timeGap.getSeconds();

        // 음수면 요청 다시 보내게끔?
        apiResult.setArrivalTime(correctedArrivalTime > 0 ? correctedArrivalTime : 0);
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

    public void addExit() throws IOException, InterruptedException {
        CsvParsing festivalCSVParsing = new CsvParsing("localLink");
        String[] line = null;

        int lineCount = 0;
        while ((line = festivalCSVParsing.nextRead()) != null) {
            if (lineCount == 0) {
                lineCount++;
                continue;
            }
            LinkedHashMap<String, String> latLon = KatecToLatLong.getLatLon(line[3], line[2]);
            System.out.println(line[0]);
            System.out.println(line[1]);
            StationExitTmp exit = StationExitTmp.builder()
                    .exitLatitude(new BigDecimal(latLon.get("lat")))
                    .exitLongitude(new BigDecimal(latLon.get("lon")))
                    .stationId(Integer.valueOf(line[0]))
                    .exitName(line[1])
                    .build();
            stationExitTmpRepository.save(exit);
        }
    }

}
