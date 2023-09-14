package com.example.hanium2023.service;

import com.example.hanium2023.domain.dto.publicapi.arrivalinfo.ArrivalInfoApiResult;
import com.example.hanium2023.domain.dto.publicapi.location.LocationInfoApiResult;
import com.example.hanium2023.domain.dto.publicapi.location.LocationInfoPushAlarm;
import com.example.hanium2023.domain.dto.user.MovingSpeedInfo;
import com.example.hanium2023.domain.dto.user.UserDto;
import com.example.hanium2023.domain.entity.Station;
import com.example.hanium2023.enums.DirectionCodeEnum;
import com.example.hanium2023.enums.MovingMessageEnum;
import com.example.hanium2023.enums.TrainStatusCodeEnum;
import com.example.hanium2023.repository.StationRepository;
import com.example.hanium2023.repository.UserRepository;
import com.example.hanium2023.util.JsonUtil;
import com.example.hanium2023.util.RedisUtil;
import com.example.hanium2023.util.TimeUtil;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LocationInfoService {
    private final StationRepository stationRepository;
    private final PublicApiService publicApiService;
    private final UserRepository userRepository;
    private final RedisUtil redisUtil;

    public void correctArrivalInfoByLocationInfo(List<ArrivalInfoApiResult> arrivalInfoApiResultList, String stationName) {
        List<Station> stationList = stationRepository.findAllByStatnName(stationName);

        for (Station station : stationList) {
            List<LocationInfoApiResult> locationInfoApiResultList = getLocationInfoFromPublicApi(station.getLine().getLineName());

            for (ArrivalInfoApiResult arrivalInfoApiResult : arrivalInfoApiResultList) {
                for (LocationInfoApiResult locationInfoApiResult : locationInfoApiResultList) {
                    if (isInArrivalInfoList(locationInfoApiResult, arrivalInfoApiResult)) {
                        if (isAtNearOrCurrentStation(locationInfoApiResult, station)) {
                            calculateNearStationArrivalTime(locationInfoApiResult,station);
                            arrivalInfoApiResult.setArrivalTime(locationInfoApiResult.getArrivalTime());
                        } else {
                            // 일반적인 경우의 이동 시간 계산
                        }
                    }
                }
            }
        }
    }

    private boolean isInArrivalInfoList(LocationInfoApiResult locationInfoApiResult, ArrivalInfoApiResult arrivalInfoApiResult) {
        return locationInfoApiResult.getTrainNumber().equals(arrivalInfoApiResult.getTrainNumber());
    }

    public List<LocationInfoPushAlarm> getLocationInfoForPushAlarm(String stationName, String exitName) {
        List<Station> stationList = stationRepository.findAllByStatnName(stationName);
        List<LocationInfoPushAlarm> locationInfoPushAlarmList = new ArrayList<>();

        UserDto userDto = new UserDto(userRepository.findById(41L).get());

        for (Station station : stationList) {
            locationInfoPushAlarmList.addAll(getLocationInfoFromPublicApi(station.getLine().getLineName())
                    .stream()
                    .filter(result -> filterTerminus(result, station))
                    .filter(result -> isAtNearOrCurrentStation(result, station))
                    .filter(result -> filterTerminus(result, station))
                    .filter(apiResult -> calculateNearStationArrivalTime(apiResult, station))
                    .map(LocationInfoPushAlarm::new)
                    .map(apiResult -> addDestinationInfo(apiResult, station))
                    .map(apiResult -> calculateMovingTime(apiResult, station, exitName, userDto))
                    .collect(Collectors.toList()));
        }
        for (LocationInfoPushAlarm l : locationInfoPushAlarmList) {
            System.out.println("l = " + l);
        }
        return locationInfoPushAlarmList;
    }

//    private int calculateArrivalTime(LocationInfoApiResult locationInfoApiResult, Station station) {
//
//    }

    private boolean isAtNearOrCurrentStation(LocationInfoApiResult apiResult, Station station) {
        if (isAtCurrentStation(apiResult, station) && (apiResult.getTrainStatusCode() == TrainStatusCodeEnum.DEPART_BEFORE_STATION.getCode())) {
            apiResult.setStationName(station.getStatnName());
            return true;
        }
        if (isAtNearStation(apiResult, station) && apiResult.getTrainStatusCode() == TrainStatusCodeEnum.ARRIVE.getCode()) {
            apiResult.setStationName(station.getStatnName());
            apiResult.setTrainStatusCode(TrainStatusCodeEnum.ARRIVE_BEFORE_STATION.getCode());
            return true;
        }
        if (isAtNearStation(apiResult, station) && apiResult.getTrainStatusCode() == TrainStatusCodeEnum.DEPART.getCode()) {
            apiResult.setStationName(station.getStatnName());
            apiResult.setTrainStatusCode(TrainStatusCodeEnum.DEPART_BEFORE_STATION.getCode());
            return true;
        }
        return false;
    }

    private boolean filterTerminus(LocationInfoApiResult apiResult, Station station) {
        return !apiResult.getStationName().equals(apiResult.getLastStationName()) && !station.getStatnName().equals(apiResult.getLastStationName());
    }

    private boolean filterLine6Circular(LocationInfoApiResult apiResult, Station station) {
        if (station.getStatnName().equals("역촌")) {
            if (apiResult.getTrainStatusCode() > 2) {
                return apiResult.getLastStationName().equals("응암순환(상선)");
            }
        }
        return true;
    }

    private boolean isAtCurrentStation(LocationInfoApiResult apiResult, Station station) {
        return validateStationName(station.getStatnName(), apiResult.getStationName());
    }

    private boolean isAtNearStation(LocationInfoApiResult apiResult, Station station) {
        return (validateStationName(station.getNextStation1(), apiResult.getStationName()) && (apiResult.getDirectionCode() == DirectionCodeEnum.UP_LINE.getCode())) ||
                (validateStationName(station.getBeforeStation1(), apiResult.getStationName()) && (apiResult.getDirectionCode() == DirectionCodeEnum.DOWN_LINE.getCode()));
    }

    private boolean validateStationName(String dbStationName, String apiStationName) {
        if (dbStationName.equals(apiStationName)) {
            return true;
        } else if (apiStationName.startsWith(dbStationName + "(") && apiStationName.endsWith(")")) {
            return true;
        } else {
            int indexOpen = apiStationName.indexOf("(");
            int indexClose = apiStationName.indexOf(")");

            if (indexOpen > 0 && indexClose > indexOpen) {
                String prefix = apiStationName.substring(0, indexOpen);
                String suffix = apiStationName.substring(indexClose + 1);
                if (dbStationName.equals(prefix) && suffix.length() > 0) {
                    return true;
                }
            }
        }
        return false;
    }

    private LocationInfoPushAlarm addDestinationInfo(LocationInfoPushAlarm apiResult, Station station) {
        String nextStation = (apiResult.getDirectionCode() == DirectionCodeEnum.UP_LINE.getCode()) ? station.getBeforeStation1() : station.getNextStation1();
        String currentStation = apiResult.getDestination();
        apiResult.setDestination(currentStation + "행 - " + nextStation + "방면");
        return apiResult;
    }

    private boolean calculateNearStationArrivalTime(LocationInfoApiResult apiResult, Station station) {
        Integer adjacentStationTime = (apiResult.getDirectionCode() == DirectionCodeEnum.UP_LINE.getCode()) ? station.getNextStationTime1() : station.getBeforeStationTime1();
        LocalDateTime currentTime = TimeUtil.getCurrentTime();
        int arrivalTime = 0;
        if (apiResult.getTrainStatusCode().equals(TrainStatusCodeEnum.DEPART_BEFORE_STATION.getCode())) {
            // api 딜레이 20초
            LocalDateTime realDepartTime = TimeUtil.getTimeFromString(apiResult.getCreatedAt()).minusSeconds(20);
            arrivalTime = (int) TimeUtil.getDuration(currentTime, realDepartTime.plusSeconds(adjacentStationTime)).getSeconds();
        } else if (apiResult.getTrainStatusCode().equals(TrainStatusCodeEnum.ARRIVE_BEFORE_STATION.getCode())) {
            // api 딜레이 20초
            LocalDateTime realDepartTime = TimeUtil.getTimeFromString(apiResult.getCreatedAt()).plusSeconds(25);
            // 문 개방 시간 20초
            arrivalTime = (int) TimeUtil.getDuration(currentTime, realDepartTime.plusSeconds(20).plusSeconds(adjacentStationTime)).getSeconds();
        }
        apiResult.setArrivalTime(arrivalTime);
//        return apiResult;
        return arrivalTime > 0;
    }

    private LocationInfoPushAlarm calculateMovingTime(LocationInfoPushAlarm locationInfoPushAlarm, Station station, String exitName, UserDto userDto) {
        double distance = redisUtil.getDistanceByStationIdAndExitName(station.getStationId(), exitName);
        double minMovingSpeed = distance / (double) locationInfoPushAlarm.getArrivalTime();

        MovingSpeedInfo movingSpeedInfo = getMovingSpeedInfo(userDto, minMovingSpeed);

        long movingTime = (long) (distance / movingSpeedInfo.getMovingSpeed());

        if (movingSpeedInfo.getMovingSpeed() == -1.0)
            locationInfoPushAlarm.setMessage(movingSpeedInfo.getMovingMessageEnum().getMessage());
        else
            locationInfoPushAlarm.setMessage(movingTime + "초 동안 " + movingSpeedInfo.getMovingMessageEnum().getMessage());

        locationInfoPushAlarm.setMovingTime(movingTime > 0 ? movingTime : 0);
        locationInfoPushAlarm.setMovingSpeedStep(movingSpeedInfo.getMovingMessageEnum().getMovingSpeedStep());
        locationInfoPushAlarm.setMovingSpeed(movingSpeedInfo.getMovingSpeed());

        return locationInfoPushAlarm;
    }

    private List<LocationInfoApiResult> getLocationInfoFromPublicApi(String lineName) {
        JSONObject apiResultJsonObject = publicApiService.getApiResult(publicApiService.getLocationApiUrl(lineName));
        Optional<JSONArray> jsonArray = Optional.ofNullable((JSONArray) apiResultJsonObject.get("realtimePositionList"));
        List<LocationInfoApiResult> locationInfoApiResult = new ArrayList<>();

        if (jsonArray.isPresent()) {
            locationInfoApiResult = JsonUtil.convertJsonArrayToDtoList(jsonArray.get(), LocationInfoApiResult.class);
        }
        return locationInfoApiResult;
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
}
