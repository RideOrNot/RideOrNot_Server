package com.example.hanium2023.service;

import com.example.hanium2023.domain.dto.congestion.AvailableStationInfoApiResult;
import com.example.hanium2023.domain.dto.congestion.CongestionResponse;
import com.example.hanium2023.domain.dto.congestion.PassengerByTimeResult;
import com.example.hanium2023.domain.dto.congestion.PassengerPerDayResult;
import com.example.hanium2023.domain.entity.Line;
import com.example.hanium2023.domain.entity.Station;
import com.example.hanium2023.enums.CongestionEnum;
import com.example.hanium2023.repository.LineRepository;
import com.example.hanium2023.repository.StationRepository;
import com.example.hanium2023.util.JsonUtil;
import com.example.hanium2023.util.SKApiUtil;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PublicApiService {
    @Value("${public-api-key.real-time-key}")
    private String realTimeApiKey;
    @Value("${public-api-key.lat-lon-key}")
    private String skKey;
    private final LineRepository lineRepository;
    private final StationRepository stationRepository;
    private final int avgPassenger = 23058;


    public JSONObject getApiResult(String apiUrl) {
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

        return JsonUtil.parseJsonObject(result.toString());
    }

    public String buildRealTimeApiUrl(String stationName) {
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

    public String getLocationApiUrl(String lineName) {
        StringBuilder urlBuilder = new StringBuilder("http://swopenAPI.seoul.go.kr/api/subway/");
        try {
            urlBuilder.append(URLEncoder.encode(realTimeApiKey, "UTF-8"));
            urlBuilder.append("/json/realtimePosition/0/100/");
            urlBuilder.append(URLEncoder.encode(lineName, "UTF-8"));
            return urlBuilder.toString();
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public List<AvailableStationInfoApiResult> addSkStationId() throws IOException, InterruptedException {
        JSONObject apiResultJsonObject = SKApiUtil.getAvailableStationApiResult(skKey);
        Optional<JSONArray> jsonArray = Optional.ofNullable((JSONArray) apiResultJsonObject.get("contents"));
        List<AvailableStationInfoApiResult> result = new ArrayList<>();
        if (jsonArray.isPresent()) {
            result = new ArrayList<>(JsonUtil.convertJsonArrayToDtoList(jsonArray.get(), AvailableStationInfoApiResult.class));
        }
        for (AvailableStationInfoApiResult availableStationInfoApiResult : result) {
            Optional<Line> line = lineRepository.findByLineNameContains(
                    availableStationInfoApiResult.getSubwayLine().substring(0, 2)
            );
            if (line.isEmpty()) continue;
            List<Station> stations = stationRepository
                    .findAllByStatnNameAndLine(
                            availableStationInfoApiResult
                                    .getStationName()
                                    .substring(0, availableStationInfoApiResult.getStationName().length() - 1), line.get());
            if (stations.isEmpty()) continue;
            for (Station station : stations) {
                station.updateSKStationCode(availableStationInfoApiResult.getStationCode());
                stationRepository.save(station);
            }
        }
        return result;
    }

    //SK 에서 제공하는 역의 경우
    public CongestionResponse getCongestionForPushAlarm(String stationName, String exitName) {

        try {
            String defaultCongestionMessage = stationName + "역 " + exitName + "출구가 ";
            CongestionResponse response = CongestionResponse.builder()
                    .congestionMessage(CongestionEnum.NULL.getMessage())
                    .build();
            //SK API 에서 제공하는 역인지 확인
            Optional<Station> station = stationRepository.findByStatnNameAndSKStationCodeIsNotNull(stationName);

            //제공하지 않는 역의 경우 제공하지 않음을 리턴
            if (station.isEmpty()) return response;
            List<PassengerPerDayResult> passengerPerDayResult = getPassengerPerDayResult(station);
            List<PassengerByTimeResult> passengerByTimeResult = getPassengerByTimeResult(station);
            int totalUser = 0;
            int nowUser = 0;
            for (PassengerPerDayResult result : passengerPerDayResult) {
                if (result == null) continue;
                if (result.getUserCount() == null) continue;
                if (result.getExit().equals(exitName)) nowUser = result.getUserCount();
                totalUser += result.getUserCount();
            }
            double stationWeight = (double) totalUser / 12 / avgPassenger;
            double exitWeight = (double) nowUser / (double) totalUser * 100;
            totalUser = 0;
            nowUser = 0;
            for (PassengerByTimeResult result : passengerByTimeResult) {
                if (result == null) continue;
                if (result.getUserCount() == null) continue;
                if (result.getExit().equals(exitName)) {
                    totalUser += result.getUserCount();
                    if (result.getDatetime().substring(8, 10).equals(LocalDateTime.now().format(DateTimeFormatter.ofPattern("hh")))) {
                        nowUser = result.getUserCount();
                    }
                }

            }
            double timeWeight = (double) nowUser / totalUser * 100;
            double congestion = stationWeight * exitWeight * timeWeight;

            //가중치의 곱 결과를 구간을 나누어 혼잡도 정보로 제공
            if (congestion < 20)
                response.setCongestionMessage(defaultCongestionMessage + CongestionEnum.LOW.getMessage());
            else if (congestion < 40)
                response.setCongestionMessage(defaultCongestionMessage + CongestionEnum.NORMAL.getMessage());
            else response.setCongestionMessage(defaultCongestionMessage + CongestionEnum.HIGH.getMessage());
            return response;
        } catch (Exception e) {
            return CongestionResponse.builder()
                    .congestionMessage(CongestionEnum.NULL.getMessage())
                    .build();
        }

    }

    public CongestionResponse getCongestionForStationInfo(String stationName) {
        try {
            String defaultCongestionMessage = stationName + "역이 ";
            CongestionResponse response = CongestionResponse.builder()
                    .congestionMessage(CongestionEnum.NULL.getMessage())
                    .build();
            Optional<Station> station = stationRepository.findByStatnNameAndSKStationCodeIsNotNull(stationName);
            if (station.isEmpty()) return response;
            List<PassengerPerDayResult> passengerPerDayResult = getPassengerPerDayResult(station);
            List<PassengerByTimeResult> passengerByTimeResult = getPassengerByTimeResult(station);
            if(passengerByTimeResult == null || passengerPerDayResult == null ) return response;
            int totalUser = 0;
            for (PassengerPerDayResult result : passengerPerDayResult) {
                if (result == null) continue;
                if (result.getUserCount() == null) continue;
                totalUser += result.getUserCount();
            }
            double stationWeight = (double) totalUser / 12 / avgPassenger;
            totalUser = 0;
            int nowUser = 0;
            for (PassengerByTimeResult result : passengerByTimeResult) {
                if (result == null) continue;
                if (result.getUserCount() == null) continue;
                totalUser += result.getUserCount();
                if (result.getDatetime().substring(8, 10).equals(LocalDateTime.now().format(DateTimeFormatter.ofPattern("hh")))) {
                    nowUser += result.getUserCount();
                }
            }
            double timeWeight = (double) nowUser / totalUser * 100;
            double congestion = stationWeight * timeWeight;
            if (congestion < 10)
                response.setCongestionMessage(defaultCongestionMessage + CongestionEnum.LOW.getMessage());
            else if (congestion < 15)
                response.setCongestionMessage(defaultCongestionMessage + CongestionEnum.NORMAL.getMessage());
            else response.setCongestionMessage(defaultCongestionMessage + CongestionEnum.HIGH.getMessage());
            return response;
        } catch (Exception e) {
            return CongestionResponse.builder()
                    .congestionMessage(CongestionEnum.NULL.getMessage())
                    .build();
        }
    }

    private List<PassengerPerDayResult> getPassengerPerDayResult(Optional<Station> station){
        try {
            Calendar currentCalendar = Calendar.getInstance();
            currentCalendar.add(currentCalendar.DATE, -7);
            JSONObject passengerPerDay = SKApiUtil.getPassengerPerDayApiResult(
                    skKey,
                    station.get().getSKStationCode(),
                    LocalDateTime.now()
                            .getDayOfWeek()
                            .toString()
                            .toUpperCase()
                            .substring(0, 3));

            JSONObject passengerPerDayObject = (JSONObject) passengerPerDay.get("contents");
            Optional<JSONArray> passengerPerDayArray = Optional.ofNullable((JSONArray) passengerPerDayObject.get("stat"));
            List<PassengerPerDayResult> passengerPerDayResult = new ArrayList<>();
            if (passengerPerDayArray.isPresent()) {
                passengerPerDayResult = new ArrayList<>(JsonUtil.convertJsonArrayToDtoList(passengerPerDayArray.get(), PassengerPerDayResult.class));
            }
            return passengerPerDayResult;

        } catch (Exception e){
            return null;
        }
    }
    private List<PassengerByTimeResult> getPassengerByTimeResult(Optional<Station> station){
        try {
            Calendar currentCalendar = Calendar.getInstance();
            currentCalendar.add(currentCalendar.DATE, -7);
            //현재 시간을 기준으로 일주일 전의 특정 지하철역의 특정 출구의 특정 시간의 통행자수에 대한 정보를 조회
            JSONObject passengerByTime = SKApiUtil.getPassengerByTimeApiResult(
                    skKey,
                    station.get().getSKStationCode(),
                    new SimpleDateFormat("yyyyMMdd").format(currentCalendar.getTime()));
            JSONObject passengerByTimeObject = (JSONObject) passengerByTime.get("contents");
            Optional<JSONArray> passengerByTimeArray = Optional.ofNullable((JSONArray) passengerByTimeObject.get("raw"));
            List<PassengerByTimeResult> passengerByTimeResult = new ArrayList<>();
            if (passengerByTimeArray.isPresent()) {
                passengerByTimeResult = new ArrayList<>(JsonUtil.convertJsonArrayToDtoList(passengerByTimeArray.get(), PassengerByTimeResult.class));
            }
            return passengerByTimeResult;
        } catch (Exception e){
            return null;
        }
    }
}
