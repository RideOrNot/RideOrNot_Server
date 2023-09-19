package com.example.hanium2023.domain.dto.publicapi.location;

import com.example.hanium2023.enums.DirectionCodeEnum;
import com.example.hanium2023.enums.TrainStatusCodeEnum;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class LocationInfoPushAlarm {
    int arrivalTime;
    long movingTime;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    int directionCode;
    String direction;
    String lineId;
    String destination;
    String message;
    int movingSpeedStep;
    double movingSpeed;
    String stationName;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    int trainStatusCode;
    String trainStatus;
    String createdAt;
    String trainNumber;

    public LocationInfoPushAlarm(LocationInfoApiResult apiResult) {
        if (apiResult.getLineId() == 1002) {
            this.direction = (apiResult.getDirectionCode() == 0) ?
                    DirectionCodeEnum.INNER_CIRCLE_LINE.getDirection() : DirectionCodeEnum.OUTER_CIRCLE_LINE.getDirection();
        } else {
            this.direction = (apiResult.getDirectionCode() == 0) ?
                    DirectionCodeEnum.UP_LINE.getDirection() : DirectionCodeEnum.DOWN_LINE.getDirection();
        }
        this.directionCode = apiResult.getDirectionCode();
        this.trainStatus = TrainStatusCodeEnum.getEnumByCode(apiResult.getTrainStatusCode()).getStatus();
        this.trainStatusCode = apiResult.getTrainStatusCode();
        this.lineId = String.valueOf(apiResult.getLineId());
        this.stationName = apiResult.getStationName();
        this.destination = apiResult.getLastStationName();
        this.createdAt = apiResult.getCreatedAt();
        this.trainNumber = apiResult.getTrainNumber();
        this.arrivalTime = apiResult.getArrivalTime();
    }

    @Override
    public String toString() {
        return "LocationInfoPushAlarm{" +
                "arrivalTime=" + arrivalTime +
                ", movingTime=" + movingTime +
                ", direction='" + directionCode + '\'' +
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
