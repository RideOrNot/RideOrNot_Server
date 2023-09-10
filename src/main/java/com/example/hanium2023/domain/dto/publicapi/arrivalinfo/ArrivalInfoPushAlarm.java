package com.example.hanium2023.domain.dto.publicapi.arrivalinfo;


import lombok.Data;

@Data
public class ArrivalInfoPushAlarm {
    int arrivalTime;
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
    String createdAt;
    public ArrivalInfoPushAlarm(ArrivalInfoApiResult apiResult) {
        this.arrivalTime = apiResult.getArrivalTime();
        this.direction = apiResult.getDirection();
        this.lineId = apiResult.getLineId();
        this.destination = apiResult.getDestination();
        this.stationName = apiResult.getStationName();
        this.arrivalMessage2 = apiResult.getArrivalMessage2();
        this.arrivalMessage3 = apiResult.getCurrentStationName();
        this.createdAt = apiResult.getCreatedAt();
    }


}
