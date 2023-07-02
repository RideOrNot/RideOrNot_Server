package com.example.hanium2023.domain.dto;


import lombok.Data;

@Data
public class ArrivalInfoResponse {
    long arrivalTime;
    long movingTime;
    String direction;
    String lineName;
    String destination;
    String message;
    int movingSpeedStep;

    public ArrivalInfoResponse(ArrivalInfoApiResult apiResult) {
        arrivalTime = apiResult.getArrivalTime();
        direction = apiResult.getDirection();
        lineName = apiResult.getLineName();
        destination = apiResult.getDestination();
    }
}
