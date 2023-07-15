package com.example.hanium2023.domain.dto.arrivalinfo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@AllArgsConstructor
public class ArrivalInfoStationInfoPageResponse {
    long arrivalTime;
    String direction;
    String lineId;
    String destination;
    String currentTime;

    public ArrivalInfoStationInfoPageResponse(ArrivalInfoApiResult apiResult) {
        this.arrivalTime = apiResult.getArrivalTime();
        this.direction = apiResult.getDirection();
        this.lineId = apiResult.getLineId();
        this.destination = apiResult.getDestination();
        this.currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd a HH:mm:ss"));
    }
}

