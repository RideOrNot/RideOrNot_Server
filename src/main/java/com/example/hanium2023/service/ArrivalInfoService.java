package com.example.hanium2023.service;

import com.example.hanium2023.domain.dto.publicapi.arrivalinfo.ArrivalInfoApiResult;
import com.example.hanium2023.domain.dto.publicapi.arrivalinfo.ArrivalInfoPushAlarm;
import com.example.hanium2023.domain.dto.publicapi.arrivalinfo.ArrivalInfoStationInfoPageResponse;
import com.example.hanium2023.domain.dto.user.MovingSpeedInfo;
import com.example.hanium2023.domain.dto.user.UserDto;
import com.example.hanium2023.enums.TrainStatusCodeEnum;
import com.example.hanium2023.enums.MovingMessageEnum;
import com.example.hanium2023.repository.UserRepository;
import com.example.hanium2023.util.JsonUtil;
import com.example.hanium2023.util.RedisUtil;
import com.example.hanium2023.util.TimeUtil;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ArrivalInfoService {
    private final PublicApiService publicApiService;
    private final UserRepository userRepository;
    private final RedisUtil redisUtil;

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

    public List<ArrivalInfoPushAlarm> getRealTimeInfoForPushAlarm(String stationName, String exitName) {
        Predicate<ArrivalInfoApiResult> removeTooFarArrivalInfoFilter = this::removeTooFarArrivalInfo;
        Predicate<ArrivalInfoApiResult> arrivalInfoFilter = removeTooFarArrivalInfoFilter.and(this::removeInvalidArrivalInfo);
        List<ArrivalInfoApiResult> arrivalInfoApiResultList = getArrivalInfoFromPublicApi(stationName, arrivalInfoFilter);

        UserDto userDto = new UserDto(userRepository.findById(40L).get());

        return arrivalInfoApiResultList
                .stream()
                .map(ArrivalInfoPushAlarm::new)
                .map(apiResult -> calculateMovingTime(apiResult, stationName, exitName, userDto))
                .collect(Collectors.toList());
    }

    private List<ArrivalInfoApiResult> getArrivalInfoFromPublicApi(String stationName, Predicate<ArrivalInfoApiResult> filterPredicate) {
        JSONObject apiResultJsonObject = publicApiService.getApiResult(publicApiService.buildRealTimeApiUrl(stationName));
        Optional<JSONArray> jsonArray = Optional.ofNullable((JSONArray) apiResultJsonObject.get("realtimeArrivalList"));
        List<ArrivalInfoApiResult> arrivalInfoApiResultList = new ArrayList<>();

        if (jsonArray.isPresent()) {
            arrivalInfoApiResultList = JsonUtil.convertJsonArrayToDtoList(jsonArray.get(), ArrivalInfoApiResult.class)
                    .stream()
                    .map(this::correctArrivalTime)
                    .filter(this::removeExpiredArrivalInfo)
                    .filter(filterPredicate)
                    .collect(Collectors.toList());
        }
        return arrivalInfoApiResultList;
    }

    private ArrivalInfoPushAlarm calculateMovingTime(ArrivalInfoPushAlarm arrivalInfoPushAlarm, String stationName, String exitName, UserDto userDto) {
        Integer stationId = redisUtil.getStationIdByStationNameAndLineId(stationName, Integer.valueOf(arrivalInfoPushAlarm.getLineId()));
        double distance = redisUtil.getDistanceByStationIdAndExitName(stationId, exitName);
        double minMovingSpeed = distance / (double) arrivalInfoPushAlarm.getArrivalTime();

        MovingSpeedInfo movingSpeedInfo = getMovingSpeedInfo(userDto, minMovingSpeed);

        long movingTime = (long) (distance / movingSpeedInfo.getMovingSpeed());

        if (movingSpeedInfo.getMovingSpeed() == -1.0)
            arrivalInfoPushAlarm.setMessage(movingSpeedInfo.getMovingMessageEnum().getMessage());
        else
            arrivalInfoPushAlarm.setMessage(movingTime + "초 동안 " + movingSpeedInfo.getMovingMessageEnum().getMessage());

        arrivalInfoPushAlarm.setMovingTime(movingTime > 0 ? movingTime : 0);
        arrivalInfoPushAlarm.setMovingSpeedStep(movingSpeedInfo.getMovingMessageEnum().getMovingSpeedStep());
        arrivalInfoPushAlarm.setMovingSpeed(movingSpeedInfo.getMovingSpeed());

        return arrivalInfoPushAlarm;
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
        LocalDateTime currentTime = TimeUtil.getCurrentTime();
        LocalDateTime targetTime = TimeUtil.getTimeFromString(apiResult.getCreatedAt());
        Duration timeGap = TimeUtil.getDuration(targetTime, currentTime);
        int correctedArrivalTime = (int) (apiResult.getArrivalTime() - timeGap.getSeconds());

        apiResult.setArrivalTime(correctedArrivalTime > 0 ? correctedArrivalTime : 0);
        return apiResult;
    }

    private boolean filterArrivalInfoByLineId(ArrivalInfoApiResult arrivalInfo, String lineId) {
        return arrivalInfo.getLineId().equals(lineId);
    }

    private boolean removeTooFarArrivalInfo(ArrivalInfoApiResult arrivalInfo) {
        return arrivalInfo.getArrivalCode() != TrainStatusCodeEnum.NOT_CLOSE_STATION.getCode();
    }

    private boolean removeInvalidArrivalInfo(ArrivalInfoApiResult arrivalInfo) {
        return arrivalInfo.getArrivalTime() >= 30 && arrivalInfo.getArrivalTime() <= 300;
    }

    private boolean removeExpiredArrivalInfo(ArrivalInfoApiResult arrivalInfo) {
        return (arrivalInfo.getArrivalTime() > 0) || (arrivalInfo.getArrivalCode() == TrainStatusCodeEnum.NOT_CLOSE_STATION.getCode());
    }
}
