package com.example.hanium2023.domain.dto.publicapi.location;

import com.example.hanium2023.enums.TrainStatusCodeEnum;
import lombok.Data;

@Data
public class LocationInfoPushAlarm {
    int arrivalTime;
    long movingTime;
    String direction;
    String lineId;
    String destination;
    String message;
    int movingSpeedStep;
    double movingSpeed;
    String stationName;
    String trainStatus;
    String createdAt;
    int trainNumber;

    public LocationInfoPushAlarm(LocationInfoApiResult apiResult) {
        this.direction = apiResult.getDirection() == 0 ? "상행" : "하행";
        this.trainStatus = TrainStatusCodeEnum.getEnumByCode(apiResult.getTrainStatusCode()).getStatus();
        this.lineId = String.valueOf(apiResult.getLineId());
        this.stationName = apiResult.getStationName();
        this.destination = apiResult.getLastStationName();
        this.createdAt = apiResult.getCreatedAt();
        this.trainNumber = apiResult.getTrainNumber();
    }

    @Override
    public String toString() {
        return "LocationInfoPushAlarm{" +
                "arrivalTime=" + arrivalTime +
                ", movingTime=" + movingTime +
                ", direction='" + direction + '\'' +
                ", lineId='" + lineId + '\'' +
                ", destination='" + destination + '\'' +
                ", message='" + message + '\'' +
                ", movingSpeedStep=" + movingSpeedStep +
                ", movingSpeed=" + movingSpeed +
                ", stationName='" + stationName + '\'' +
                ", trainStatus='" + trainStatus + '\'' +
                ", createdAt='" + createdAt + '\'' +
                '}';
    }
}
