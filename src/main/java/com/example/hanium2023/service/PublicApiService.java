package com.example.hanium2023.service;

import com.example.hanium2023.domain.dto.arrivalinfo.ArrivalInfoApiResult;
import com.example.hanium2023.domain.dto.arrivalinfo.ArrivalInfoPushAlarmResponse;
import com.example.hanium2023.domain.dto.arrivalinfo.ArrivalInfoStationInfoPageResponse;
import com.example.hanium2023.domain.dto.congestion.AvailableStationInfoApiResult;
import com.example.hanium2023.domain.dto.congestion.CongestionResponse;
import com.example.hanium2023.domain.dto.congestion.PassengerByTimeResult;
import com.example.hanium2023.domain.dto.congestion.PassengerPerDayResult;
import com.example.hanium2023.domain.dto.user.MovingSpeedInfo;
import com.example.hanium2023.domain.dto.user.UserDto;
import com.example.hanium2023.domain.entity.Line;
import com.example.hanium2023.domain.entity.Station;
import com.example.hanium2023.enums.ArrivalCodeEnum;
import com.example.hanium2023.enums.CongestionEnum;
import com.example.hanium2023.enums.MovingMessageEnum;
import com.example.hanium2023.exception.AppException;
import com.example.hanium2023.exception.ErrorCode;
import com.example.hanium2023.repository.LineRepository;
import com.example.hanium2023.repository.StationRepository;
import com.example.hanium2023.repository.UserRepository;
import com.example.hanium2023.util.JsonUtil;
import com.example.hanium2023.util.RedisUtil;
import com.example.hanium2023.util.SKApiUtil;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PublicApiService {
    @Value("${public-api-key.real-time-key}")
    private String realTimeApiKey;
    @Value("${public-api-key.lat-lon-key}")
    private String skKey;
    private final JsonUtil jsonUtil;
    private final UserRepository userRepository;
    private final LineRepository lineRepository;
    private final StationRepository stationRepository;
    private final RedisUtil redisUtil;
    private final int avgPassenger = 23058;

    public List<ArrivalInfoStationInfoPageResponse> getArrivalInfo(String stationName) {
        Predicate<ArrivalInfoApiResult> arrivalInfoFilter = this::removeExpiredArrivalInfo;
        List<ArrivalInfoApiResult> arrivalInfoApiResultList = getArrivalInfoFromPublicApi(stationName, arrivalInfoFilter);

        return arrivalInfoApiResultList
                .stream()
                .sorted(Comparator.comparing(ArrivalInfoApiResult::getLineId))
                .map(ArrivalInfoStationInfoPageResponse::new)
                .collect(Collectors.toList());
    }

    public List<ArrivalInfoStationInfoPageResponse> getRealTimeInfoForStationInfoPage(String stationName, String lineId) {
        Predicate<ArrivalInfoApiResult> removeExpiredArrivalInfoFilter = this::removeExpiredArrivalInfo;
        Predicate<ArrivalInfoApiResult> arrivalInfoFilter = removeExpiredArrivalInfoFilter.and(apiResult -> filterArrivalInfoByLineId(apiResult, lineId));
        List<ArrivalInfoApiResult> arrivalInfoApiResultList = getArrivalInfoFromPublicApi(stationName, arrivalInfoFilter);

        return arrivalInfoApiResultList
                .stream()
                .map(ArrivalInfoStationInfoPageResponse::new)
                .collect(Collectors.toList());
    }

    public List<ArrivalInfoPushAlarmResponse> getRealTimeInfoForPushAlarm(String stationName, String exitName) {
        Predicate<ArrivalInfoApiResult> removeTooFarArrivalInfoFilter = this::removeTooFarArrivalInfo;
        Predicate<ArrivalInfoApiResult> arrivalInfoFilter = removeTooFarArrivalInfoFilter.and(this::removeInvalidArrivalInfo);
        List<ArrivalInfoApiResult> arrivalInfoApiResultList = getArrivalInfoFromPublicApi(stationName, arrivalInfoFilter);

        UserDto userDto = new UserDto(userRepository.findById(1L).get());

        return arrivalInfoApiResultList
                .stream()
                .map(ArrivalInfoPushAlarmResponse::new)
                .map(apiResult -> calculateMovingTime(apiResult, stationName, exitName, userDto))
                .collect(Collectors.toList());
    }

    private List<ArrivalInfoApiResult> getArrivalInfoFromPublicApi(String stationName, Predicate<ArrivalInfoApiResult> filterPredicate) {
        JSONObject apiResultJsonObject = getApiResult(buildRealTimeApiUrl(stationName));
        Optional<JSONArray> jsonArray = Optional.ofNullable((JSONArray) apiResultJsonObject.get("realtimeArrivalList"));
        List<ArrivalInfoApiResult> arrivalInfoApiResultList = new ArrayList<>();

        if (jsonArray.isPresent()) {
            arrivalInfoApiResultList = jsonUtil.convertJsonArrayToDtoList(jsonArray.get(), ArrivalInfoApiResult.class)
                    .stream()
                    .map(this::correctArrivalTime)
                    .filter(this::removeExpiredArrivalInfo)
                    .filter(filterPredicate)
                    .collect(Collectors.toList());
        }
        return arrivalInfoApiResultList;
    }

    private ArrivalInfoPushAlarmResponse calculateMovingTime(ArrivalInfoPushAlarmResponse arrivalInfoPushAlarmResponse, String stationName, String exitName, UserDto userDto) {
        Integer stationId = redisUtil.getStationIdByStationNameAndLineId(stationName, Integer.valueOf(arrivalInfoPushAlarmResponse.getLineId()));
        double distance = redisUtil.getDistanceByStationIdAndExitName(stationId, exitName);
        double minMovingSpeed = distance / (double) arrivalInfoPushAlarmResponse.getArrivalTime();

        MovingSpeedInfo movingSpeedInfo = getMovingSpeedInfo(userDto, minMovingSpeed);

        long movingTime = (long) (distance / movingSpeedInfo.getMovingSpeed());

        if (movingSpeedInfo.getMovingSpeed() == -1.0)
            arrivalInfoPushAlarmResponse.setMessage(movingSpeedInfo.getMovingMessageEnum().getMessage());
        else
            arrivalInfoPushAlarmResponse.setMessage(movingTime + "초 동안 " + movingSpeedInfo.getMovingMessageEnum().getMessage());

        arrivalInfoPushAlarmResponse.setMovingTime(movingTime > 0 ? movingTime : 0);
        arrivalInfoPushAlarmResponse.setMovingSpeedStep(movingSpeedInfo.getMovingMessageEnum().getMovingSpeedStep());
        arrivalInfoPushAlarmResponse.setMovingSpeed(movingSpeedInfo.getMovingSpeed());

        return arrivalInfoPushAlarmResponse;
    }

    private MovingSpeedInfo getMovingSpeedInfo(UserDto userDto, double minMovingSpeed) {
        MovingMessageEnum[] movingMessageEnums = MovingMessageEnum.getMovingMessageEnums();
        double[] speedBoundary = getSpeedBoundary(userDto);

        for (int i = 0; i < speedBoundary.length; i++) {
            if (minMovingSpeed <= speedBoundary[i]) {
                return new MovingSpeedInfo(movingMessageEnums[i], speedBoundary[i]);
            }
        }

        return new MovingSpeedInfo(MovingMessageEnum.CANNOT_BOARD, -1.0);
    }

    private double[] getSpeedBoundary(UserDto userDto) {
        return new double[]{
                userDto.getWalkingSpeed(),
                userDto.getWalkingSpeed() * 1.2,
                userDto.getWalkingSpeed() * 1.5,
                userDto.getRunningSpeed() * 0.5,
                userDto.getRunningSpeed(),
                userDto.getRunningSpeed() * 1.2
        };
    }

    private ArrivalInfoApiResult correctArrivalTime(ArrivalInfoApiResult apiResult) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime targetTime = LocalDateTime.parse(apiResult.getCreatedAt(), formatter);
        Duration timeGap = Duration.between(targetTime, currentTime);
        long correctedArrivalTime = apiResult.getArrivalTime() - timeGap.getSeconds();

        apiResult.setArrivalTime(correctedArrivalTime > 0 ? correctedArrivalTime : 0);
        return apiResult;
    }

    private boolean filterArrivalInfoByLineId(ArrivalInfoApiResult arrivalInfo, String lineId) {
        return arrivalInfo.getLineId().equals(lineId);
    }

    private boolean removeTooFarArrivalInfo(ArrivalInfoApiResult arrivalInfo) {
        return arrivalInfo.getArrivalCode() != ArrivalCodeEnum.NOT_CLOSE_STATION.getCode();
    }

    private boolean removeInvalidArrivalInfo(ArrivalInfoApiResult arrivalInfo) {
        return arrivalInfo.getArrivalTime() >= 30 && arrivalInfo.getArrivalTime() <= 300;
    }

    private boolean removeExpiredArrivalInfo(ArrivalInfoApiResult arrivalInfo) {
        return (arrivalInfo.getArrivalTime() > 0) || (arrivalInfo.getArrivalCode() == ArrivalCodeEnum.NOT_CLOSE_STATION.getCode());
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

    public List<AvailableStationInfoApiResult> addSkStationId() throws IOException, InterruptedException {
        JSONObject apiResultJsonObject = SKApiUtil.getAvailableStationApiResult(skKey);
        Optional<JSONArray> jsonArray = Optional.ofNullable((JSONArray) apiResultJsonObject.get("contents"));
        List<AvailableStationInfoApiResult> result = new ArrayList<>();
        if (jsonArray.isPresent()) {
            result = new ArrayList<>(jsonUtil.convertJsonArrayToDtoList(jsonArray.get(), AvailableStationInfoApiResult.class));
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

    public CongestionResponse getCongestionForPushAlarm(String stationName, String exitName) {
        try {
            String defaultCongestionMessage = stationName + "역 " + exitName + "출구가 ";
            CongestionResponse response = CongestionResponse.builder()
                    .congestionMessage(CongestionEnum.NULL.getMessage())
                    .build();
            Optional<Station> station = stationRepository.findByStatnNameAndSKStationCodeIsNotNull(stationName);
            if (station.isEmpty()) return response;
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
            JSONObject passengerByTime = SKApiUtil.getPassengerByTimeApiResult(
                    skKey,
                    station.get().getSKStationCode(),
                    new SimpleDateFormat("yyyyMMdd").format(currentCalendar.getTime()));
            JSONObject passengerPerDayObject = (JSONObject) passengerPerDay.get("contents");
            JSONObject passengerByTimeObject = (JSONObject) passengerByTime.get("contents");
            Optional<JSONArray> passengerPerDayArray = Optional.ofNullable((JSONArray) passengerPerDayObject.get("stat"));
            Optional<JSONArray> passengerByTimeArray = Optional.ofNullable((JSONArray) passengerByTimeObject.get("raw"));
            List<PassengerPerDayResult> passengerPerDayResult = new ArrayList<>();
            List<PassengerByTimeResult> passengerByTimeResult = new ArrayList<>();
            if (passengerPerDayArray.isPresent()) {
                passengerPerDayResult = new ArrayList<>(jsonUtil.convertJsonArrayToDtoList(passengerPerDayArray.get(), PassengerPerDayResult.class));
            }
            if (passengerByTimeArray.isPresent()) {
                passengerByTimeResult = new ArrayList<>(jsonUtil.convertJsonArrayToDtoList(passengerByTimeArray.get(), PassengerByTimeResult.class));
            }
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
            if (congestion < 20)
                response.setCongestionMessage(defaultCongestionMessage + CongestionEnum.LOW.getMessage());
            else if (congestion < 40)
                response.setCongestionMessage(defaultCongestionMessage + CongestionEnum.NORMAL.getMessage());
            else response.setCongestionMessage(defaultCongestionMessage + CongestionEnum.HIGH.getMessage());
            return response;
        } catch (Exception e) {
            throw new AppException(ErrorCode.PUBLIC_API_ERROR, ErrorCode.PUBLIC_API_ERROR.getMessage());
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
            JSONObject passengerByTime = SKApiUtil.getPassengerByTimeApiResult(
                    skKey,
                    station.get().getSKStationCode(),
                    new SimpleDateFormat("yyyyMMdd").format(currentCalendar.getTime()));
            JSONObject passengerPerDayObject = (JSONObject) passengerPerDay.get("contents");
            JSONObject passengerByTimeObject = (JSONObject) passengerByTime.get("contents");
            Optional<JSONArray> passengerPerDayArray = Optional.ofNullable((JSONArray) passengerPerDayObject.get("stat"));
            Optional<JSONArray> passengerByTimeArray = Optional.ofNullable((JSONArray) passengerByTimeObject.get("raw"));
            List<PassengerPerDayResult> passengerPerDayResult = new ArrayList<>();
            List<PassengerByTimeResult> passengerByTimeResult = new ArrayList<>();
            if (passengerPerDayArray.isPresent()) {
                passengerPerDayResult = new ArrayList<>(jsonUtil.convertJsonArrayToDtoList(passengerPerDayArray.get(), PassengerPerDayResult.class));
            }
            if (passengerByTimeArray.isPresent()) {
                passengerByTimeResult = new ArrayList<>(jsonUtil.convertJsonArrayToDtoList(passengerByTimeArray.get(), PassengerByTimeResult.class));
            }
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
            throw new AppException(ErrorCode.PUBLIC_API_ERROR, ErrorCode.PUBLIC_API_ERROR.getMessage());
        }
    }
}
