package com.example.hanium2023.service;

import com.example.hanium2023.domain.dto.arrivalinfo.ArrivalInfoApiResult;
import com.example.hanium2023.domain.dto.arrivalinfo.ArrivalInfoResponse;
import com.example.hanium2023.domain.dto.user.UserDto;
import com.example.hanium2023.domain.entity.StationExitTmp;
import com.example.hanium2023.enums.MovingMessageEnum;
import com.example.hanium2023.repository.StationExitTmpRepository;
import com.example.hanium2023.repository.UserRepository;
import com.example.hanium2023.util.CsvParsing;
import com.example.hanium2023.util.JsonUtil;
import com.example.hanium2023.util.KatecToLatLong;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.util.Pair;
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
    private final StringRedisTemplate stringRedisTemplate;

    public List<ArrivalInfoResponse> getRealTimeInfos(String stationName, String exitName) {
        JSONObject apiResultJsonObject = getApiResult(buildRealTimeApiUrl(stationName));
        JSONArray jsonArray = (JSONArray) apiResultJsonObject.get("realtimeArrivalList");

        UserDto userDto = new UserDto(userRepository.findById(1L).get());

        List<ArrivalInfoApiResult> arrivalInfoApiResultList = jsonUtil.convertJsonArrayToDtoList(jsonArray, ArrivalInfoApiResult.class)
                .stream()
                .filter(this::removeTooFarArrivalInfo)
                .map(this::correctArrivalTime)
                .filter(this::removeExpiredArrivalInfo)
                .collect(Collectors.toList());

        return arrivalInfoApiResultList
                .stream()
                .map(ArrivalInfoResponse::new)
                .map(apiResult -> calculateMovingTime(apiResult, stationName, exitName, userDto))
                .collect(Collectors.toList());
    }

    private ArrivalInfoResponse calculateMovingTime(ArrivalInfoResponse arrivalInfoResponse, String stationName, String exitName, UserDto userDto) {
        ValueOperations<String, String> stringStringValueOperations = stringRedisTemplate.opsForValue();
        String stationId = stringStringValueOperations.get(stationName + "/" + arrivalInfoResponse.getLineName());
        System.out.println("arrivalInfoResponse = " + arrivalInfoResponse.getLineName());
        System.out.println("stationId = " + stationId);
        double distance = Double.parseDouble(stringStringValueOperations.get(stationId + "/" + exitName));
        System.out.println("distance = " + distance);
        double userWalkingSpeed = userDto.getWalkingSpeed();
        double userRunningSpeed = userDto.getRunningSpeed();

        // 최대 이동 속도를 구함 ( m/s 단위)
        // 최소 movingSpeed보다 빠르게 이동해야 탈 수 있음
        double minMovingSpeed = distance / (double) arrivalInfoResponse.getArrivalTime();
        Pair<MovingMessageEnum, Double> movingSpeedInfo = getMovingSpeedInfo(userWalkingSpeed, userRunningSpeed, minMovingSpeed);

        long movingTime = (long) (distance / movingSpeedInfo.getSecond());
        arrivalInfoResponse.setMovingTime(movingTime > 0 ? movingTime : 0);

        if (movingSpeedInfo.getSecond() == -1)
            arrivalInfoResponse.setMessage(movingSpeedInfo.getFirst().getMessage());
        else
            arrivalInfoResponse.setMessage(movingTime + "초 동안 " + movingSpeedInfo.getFirst().getMessage());

        arrivalInfoResponse.setMovingSpeedStep(movingSpeedInfo.getFirst().getMovingSpeedStep());
        return arrivalInfoResponse;
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
