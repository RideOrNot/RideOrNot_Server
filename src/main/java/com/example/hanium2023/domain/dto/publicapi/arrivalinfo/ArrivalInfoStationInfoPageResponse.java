package com.example.hanium2023.domain.dto.publicapi.arrivalinfo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ArrivalInfoStationInfoPageResponse {
    long arrivalTime;
    String direction;
    String lineId;
    String destination;
    String arrivalMessage2;
    String arrivalMessage3;
    public ArrivalInfoStationInfoPageResponse(ArrivalInfoApiResult apiResult) {
        this.arrivalTime = apiResult.getArrivalTime();
        this.direction = apiResult.getDirection();
        this.lineId = apiResult.getLineId();
        this.destination = apiResult.getDestination();
        this.arrivalMessage2 = apiResult.getArrivalMessage2();
        this.arrivalMessage3 = apiResult.getArrivalMessage3();
    }
}

