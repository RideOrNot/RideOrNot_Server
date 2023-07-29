package com.example.hanium2023.service;

import com.example.hanium2023.domain.dto.arrivalinfo.ArrivalInfoApiResult;
import com.example.hanium2023.domain.dto.arrivalinfo.ArrivalInfoPushAlarmResponse;
import com.example.hanium2023.domain.dto.arrivalinfo.ArrivalInfoStationInfoPageResponse;
import com.example.hanium2023.domain.dto.station.PushAlarmResponse;
import com.example.hanium2023.domain.dto.user.UserDto;
import com.example.hanium2023.enums.ArrivalCodeEnum;
import com.example.hanium2023.enums.MovingMessageEnum;
import com.example.hanium2023.repository.UserRepository;
import com.example.hanium2023.util.JsonUtil;
import com.example.hanium2023.util.RedisUtil;
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
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PublicApiService {
    @Value("${public-api-key.real-time-key}")
    private String realTimeApiKey;
    private final JsonUtil jsonUtil;
    private final UserRepository userRepository;
    private final RedisUtil redisUtil;

    public List<ArrivalInfoStationInfoPageResponse> getArrivalInfo(String stationName) {
        JSONObject apiResultJsonObject = getApiResult(buildRealTimeApiUrl(stationName));
        JSONArray jsonArray = (JSONArray) apiResultJsonObject.get("realtimeArrivalList");
        List<ArrivalInfoApiResult> arrivalInfoApiResultList;

        if (jsonArray != null) {
            arrivalInfoApiResultList = jsonUtil.convertJsonArrayToDtoList(jsonArray, ArrivalInfoApiResult.class)
                    .stream()
                    .map(this::correctArrivalTime)
                    .filter(this::removeExpiredArrivalInfo)
                    .sorted(Comparator.comparing(ArrivalInfoApiResult::getLineId))
                    .collect(Collectors.toList());
        } else {
            arrivalInfoApiResultList = new ArrayList<>();
        }

        return arrivalInfoApiResultList
                .stream()
                .map(ArrivalInfoStationInfoPageResponse::new)
                .collect(Collectors.toList());
    }

    public List<ArrivalInfoStationInfoPageResponse> getRealTimeInfoForStationInfoPage(String stationName, String lineId) {
        JSONObject apiResultJsonObject = getApiResult(buildRealTimeApiUrl(stationName));
        JSONArray jsonArray = (JSONArray) apiResultJsonObject.get("realtimeArrivalList");
        List<ArrivalInfoApiResult> arrivalInfoApiResultList;

        if (jsonArray != null) {
            arrivalInfoApiResultList = jsonUtil.convertJsonArrayToDtoList(jsonArray, ArrivalInfoApiResult.class)
                    .stream()
                    .map(this::correctArrivalTime)
                    .filter(this::removeExpiredArrivalInfo)
                    .filter(apiResult -> filterArrivalInfoByLineId(apiResult, lineId))
                    .collect(Collectors.toList());
        } else {
            arrivalInfoApiResultList = new ArrayList<>();
        }

        return arrivalInfoApiResultList
                .stream()
                .map(ArrivalInfoStationInfoPageResponse::new)
                .collect(Collectors.toList());

    }

    public PushAlarmResponse getRealTimeInfoForPushAlarm(String stationName, String exitName) {
        JSONObject apiResultJsonObject = getApiResult(buildRealTimeApiUrl(stationName));
        JSONArray jsonArray = (JSONArray) apiResultJsonObject.get("realtimeArrivalList");
        List<ArrivalInfoApiResult> arrivalInfoApiResultList;

        if (jsonArray != null){
            arrivalInfoApiResultList = jsonUtil.convertJsonArrayToDtoList(jsonArray, ArrivalInfoApiResult.class)
                    .stream()
                    .filter(this::removeTooFarArrivalInfo)
                    .map(this::correctArrivalTime)
                    .filter(this::removeInvalidArrivalInfo)
                    .collect(Collectors.toList());
        } else {
            arrivalInfoApiResultList = new ArrayList<>();
        }

        UserDto userDto = new UserDto(userRepository.findById(1L).get());

        return new PushAlarmResponse(arrivalInfoApiResultList
                .stream()
                .map(ArrivalInfoPushAlarmResponse::new)
                .map(apiResult -> calculateMovingTime(apiResult, stationName, exitName, userDto))
                .collect(Collectors.toList()));
    }

    private ArrivalInfoPushAlarmResponse calculateMovingTime(ArrivalInfoPushAlarmResponse arrivalInfoPushAlarmResponse, String stationName, String exitName, UserDto userDto) {
        Integer stationId = redisUtil.getStationIdByStationNameAndLineId(stationName, Integer.valueOf(arrivalInfoPushAlarmResponse.getLineId()));
        double distance = redisUtil.getDistanceByStationIdAndExitName(stationId, exitName);

        double userWalkingSpeed = userDto.getWalkingSpeed();
        double userRunningSpeed = userDto.getRunningSpeed();

        // 최대 이동 속도를 구함 ( m/s 단위)
        // 최소 movingSpeed보다 빠르게 이동해야 탈 수 있음
        double minMovingSpeed = distance / (double) arrivalInfoPushAlarmResponse.getArrivalTime();
        Pair<MovingMessageEnum, Double> movingSpeedInfo = getMovingSpeedInfo(userWalkingSpeed, userRunningSpeed, minMovingSpeed);

        long movingTime = (long) (distance / movingSpeedInfo.getSecond());
        arrivalInfoPushAlarmResponse.setMovingTime(movingTime > 0 ? movingTime : 0);

        if (movingSpeedInfo.getSecond() == -1)
            arrivalInfoPushAlarmResponse.setMessage(movingSpeedInfo.getFirst().getMessage());
        else
            arrivalInfoPushAlarmResponse.setMessage(movingTime + "초 동안 " + movingSpeedInfo.getFirst().getMessage());

        arrivalInfoPushAlarmResponse.setMovingSpeedStep(movingSpeedInfo.getFirst().getMovingSpeedStep());
        arrivalInfoPushAlarmResponse.setMovingSpeed(movingSpeedInfo.getSecond());
        return arrivalInfoPushAlarmResponse;
    }

    private Pair<MovingMessageEnum, Double> getMovingSpeedInfo(double walkingSpeed, double runningSpeed, double minMovingSpeed) {
        if (minMovingSpeed <= walkingSpeed) {
            return Pair.of(MovingMessageEnum.WALK_SLOWLY, walkingSpeed);
        } else if (minMovingSpeed <= walkingSpeed * 1.2) {
            return Pair.of(MovingMessageEnum.WALK, walkingSpeed * 1.2);
        } else if (minMovingSpeed <= walkingSpeed * 1.5) {
            return Pair.of(MovingMessageEnum.WALK_FAST, walkingSpeed * 1.5);
        } else if (minMovingSpeed <= runningSpeed * 0.5) {
            return Pair.of(MovingMessageEnum.RUN_SLOWLY, runningSpeed * 0.5);
        } else if (minMovingSpeed <= runningSpeed) {
            return Pair.of(MovingMessageEnum.RUN, runningSpeed);
        } else if (minMovingSpeed <= runningSpeed * 1.2) {
            return Pair.of(MovingMessageEnum.RUN_FAST, runningSpeed * 1.2);
        } else
            return Pair.of(MovingMessageEnum.CANNOT_BOARD, -1.0);
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

}
