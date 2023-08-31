package com.example.hanium2023.service;

import com.example.hanium2023.domain.dto.publicapi.arrivalinfo.ArrivalInfoPushAlarmResponse;
import com.example.hanium2023.domain.dto.publicapi.location.LocationInfoApiResult;
import com.example.hanium2023.domain.dto.user.MovingSpeedInfo;
import com.example.hanium2023.domain.dto.user.UserDto;
import com.example.hanium2023.domain.entity.Station;
import com.example.hanium2023.enums.TrainStatusCodeEnum;
import com.example.hanium2023.enums.MovingMessageEnum;
import com.example.hanium2023.repository.StationRepository;
import com.example.hanium2023.repository.UserRepository;
import com.example.hanium2023.util.JsonUtil;
import com.example.hanium2023.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LocationInfoService {
    private final StationRepository stationRepository;
    private final PublicApiService publicApiService;
    private final JsonUtil jsonUtil;
    private final UserRepository userRepository;
    private final RedisUtil redisUtil;


    public List<ArrivalInfoPushAlarmResponse> getLocationInfoForPushAlarm(String stationName, String exitName) {
        List<Station> stationList = stationRepository.findAllByStatnName(stationName);
        List<LocationInfoApiResult> locationInfoApiResultList = new ArrayList<>();
        for (Station s : stationList) {
            locationInfoApiResultList.addAll(getLocationInfoFromPublicApi(s.getLine().getLineName()));
            try {
            } catch (Exception e) {
                System.out.println(e);
            }
        }

        UserDto userDto = new UserDto(userRepository.findById(38L).get());

        return locationInfoApiResultList
                .stream()
                .filter(locationInfoApiResult ->
                        locationInfoApiResult.getStationName().equals(stationName))
                .filter(locationInfoApiResult ->
                        locationInfoApiResult.getTrainStatusCode() == TrainStatusCodeEnum.DEPART_BEFORE_STATION.getCode())
                .map(ArrivalInfoPushAlarmResponse::new)
//                .map(apiResult -> calculateMovingTime(apiResult, stationName, exitName, userDto))
                .collect(Collectors.toList());

    }

    public List<LocationInfoApiResult> getLocationInfoFromPublicApi(String lineName) {
        JSONObject apiResultJsonObject = publicApiService.getApiResult(publicApiService.getLocationApiUrl(lineName));
        Optional<JSONArray> jsonArray = Optional.ofNullable((JSONArray) apiResultJsonObject.get("realtimePositionList"));
        List<LocationInfoApiResult> arrivalInfoApiResultList = new ArrayList<>();

        if (jsonArray.isPresent()) {
            arrivalInfoApiResultList = jsonUtil.convertJsonArrayToDtoList(jsonArray.get(), LocationInfoApiResult.class);
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
}
