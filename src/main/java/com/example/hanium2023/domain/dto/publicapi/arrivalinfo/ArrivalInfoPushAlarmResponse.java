package com.example.hanium2023.domain.dto.publicapi.arrivalinfo;


import com.example.hanium2023.domain.dto.publicapi.location.LocationInfoApiResult;
import com.example.hanium2023.enums.TrainStatusCodeEnum;
import lombok.Data;

@Data
public class ArrivalInfoPushAlarmResponse {
    long arrivalTime;
    long movingTime;
    String direction;
    String lineId;
    String destination;
    String message;
    int movingSpeedStep;
    double movingSpeed;
    String stationName;
    String arrivalMessage2;
    String arrivalMessage3;
    String trainStatus;


    public ArrivalInfoPushAlarmResponse(ArrivalInfoApiResult apiResult) {
        this.arrivalTime = apiResult.getArrivalTime();
        this.direction = apiResult.getDirection();
        this.lineId = apiResult.getLineId();
        this.destination = apiResult.getDestination();
        this.stationName = apiResult.getStationName();
        this.arrivalMessage2 = apiResult.getArrivalMessage2();
        this.arrivalMessage3 = apiResult.getArrivalMessage3();
    }

    public ArrivalInfoPushAlarmResponse(LocationInfoApiResult apiResult) {
        this.direction = apiResult.getDirection() == 0 ? "상행" : "하행";
        this.trainStatus = TrainStatusCodeEnum.getEnumByCode(apiResult.getTrainStatusCode()).getStatus();
        this.lineId = String.valueOf(apiResult.getLineId());
        this.stationName = apiResult.getStationName();
        this.destination = apiResult.getLastStationName();
    }
}
