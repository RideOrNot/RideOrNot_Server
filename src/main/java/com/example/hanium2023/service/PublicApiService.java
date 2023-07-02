package com.example.hanium2023.service;

import com.example.hanium2023.domain.dto.ArrivalInfoApiResult;
import com.example.hanium2023.domain.dto.ArrivalInfoResponse;
import com.example.hanium2023.domain.entity.StationExitTmp;
import com.example.hanium2023.domain.entity.User;
import com.example.hanium2023.repository.StationExitTmpRepository;
import com.example.hanium2023.repository.UserRepository;
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
    private final UserRepository userRepository;

    public List<ArrivalInfoResponse> getRealTimeInfos(String stationName) {
        JSONObject apiResultJsonObject = getApiResult(buildRealTimeApiUrl(stationName));
        JSONArray jsonArray = (JSONArray) apiResultJsonObject.get("realtimeArrivalList");
        User user = userRepository.findById(1L).get();
        List<ArrivalInfoApiResult> arrivalInfoApiResultList = jsonUtil.convertJsonArrayToDtoList(jsonArray, ArrivalInfoApiResult.class)
                .stream()
                .filter(this::removeTooFarArrivalInfo)
                .map(this::correctArrivalTime)
                .filter(this::removeExpiredArrivalInfo)
                .collect(Collectors.toList());
        return arrivalInfoApiResultList
                .stream()
                .map(ArrivalInfoResponse::new)
                .map(apiResult -> calculateMovingTime(apiResult, user))
                .collect(Collectors.toList());
    }

    private ArrivalInfoResponse calculateMovingTime(ArrivalInfoResponse arrivalInfoResponse, User user) {
        // 최대 이동 속도를 구함 ( m/s 단위)
        // 최소 movingSpeed보다 빠르게 이동해야 탈 수 있음
        double movingSpeed = 300 / (double) arrivalInfoResponse.getArrivalTime();

        // km/h 단위로 환산
//        movingSpeed = (movingSpeed / 1000) * 3600;

        // 가중치를 곱함
        // 가중치를 줄이면 이동속도 작아짐 -> 이동 시간은 커짐 -> 더 널널하게 안내
        // 그렇다면 사용자가 탑승하지 못하면 가중치를 줄여야하냐?
        movingSpeed *= user.getSpeedWeight();

        // TODO : 사용자 이동속도와 movingSpeed를 비교해서 뛰어야 되는지 말아야되는지 ?
        arrivalInfoResponse.setMovingTime((long) (300 / movingSpeed));
        return arrivalInfoResponse;
    }

    private ArrivalInfoApiResult correctArrivalTime(ArrivalInfoApiResult apiResult) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime targetTime = LocalDateTime.parse(apiResult.getCreatedAt(), formatter);
        Duration timeGap = Duration.between(targetTime, currentTime);
        long correctedArrivalTime = apiResult.getArrivalTime() - timeGap.getSeconds();

        // 음수면 요청 다시 보내게끔?
        apiResult.setArrivalTime(correctedArrivalTime > 0 ? correctedArrivalTime : 0);
        return apiResult;
    }

    private boolean removeTooFarArrivalInfo(ArrivalInfoApiResult arrivalInfo) {
        if (arrivalInfo.getArrivalTime() == 0)
            return false;
        else
            return true;
    }

    private boolean removeExpiredArrivalInfo(ArrivalInfoApiResult arrivalInfo) {
        long arrivalTime = arrivalInfo.getArrivalTime();
        if (arrivalTime < 30 || arrivalTime > 300)
            return false;
        else
            return true;
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
