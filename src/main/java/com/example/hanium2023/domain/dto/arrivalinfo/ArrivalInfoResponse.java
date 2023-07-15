package com.example.hanium2023.domain.dto.arrivalinfo;


import com.example.hanium2023.domain.dto.arrivalinfo.ArrivalInfoApiResult;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
public class ArrivalInfoResponse {
    long arrivalTime;
    long movingTime;
    String direction;
    String lineName;
    String destination;
    String message;
    int movingSpeedStep;
    String currentTime;
    String stationName;

    public ArrivalInfoResponse(ArrivalInfoApiResult apiResult,String stationName) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        this.arrivalTime = apiResult.getArrivalTime();
        this.direction = apiResult.getDirection();
        this.lineName = apiResult.getLineName();
        this.destination = apiResult.getDestination();
        this.currentTime = LocalDateTime.now().format(formatter);
        this.stationName = stationName;
    }
}
