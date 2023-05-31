package com.example.hanium2023.domain.dto;


import lombok.Builder;
import lombok.Data;

@Data
public class ArrivalInfoResponse {
    long arrivalTime;
    String direction;
    String lineName;
    String destination;

    public ArrivalInfoResponse(ArrivalInfoApiResult apiResult) {
        arrivalTime = apiResult.getArrivalTime();
        direction = apiResult.getDirection();
        lineName = apiResult.getLineName();
        destination = apiResult.getDestination();
    }
}
