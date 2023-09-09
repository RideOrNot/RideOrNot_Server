package com.example.hanium2023.domain.dto.publicapi.arrivalinfo;

import com.example.hanium2023.enums.TrainStatusCodeEnum;
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
    String currentStationName;
    String trainNumber;
    String trainStatus;

    public ArrivalInfoStationInfoPageResponse(ArrivalInfoApiResult apiResult) {
        this.arrivalTime = apiResult.getArrivalTime();
        this.direction = apiResult.getDirection();
        this.lineId = apiResult.getLineId();
        this.destination = apiResult.getDestination();
        this.arrivalMessage2 = apiResult.getArrivalMessage2();
        this.currentStationName = apiResult.getArrivalMessage3();
        this.trainNumber = apiResult.getTrainNumber();
        this.trainStatus = TrainStatusCodeEnum.getEnumByCode(apiResult.getArrivalCode()).getStatus();
    }
}

