package com.example.hanium2023.domain.dto.arrivalinfo;


import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
public class ArrivalInfoPushAlarmResponse {
    long arrivalTime;
    long movingTime;
    String direction;
    String lineId;
    String destination;
    String message;
    int movingSpeedStep;
    String currentTime;
    String stationName;

    public ArrivalInfoPushAlarmResponse(ArrivalInfoApiResult apiResult, String stationName) {
        this.arrivalTime = apiResult.getArrivalTime();
        this.direction = apiResult.getDirection();
        this.lineId = apiResult.getLineId();
        this.destination = apiResult.getDestination();
        this.currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd a HH:mm:ss"));
        this.stationName = stationName;
    }
}
