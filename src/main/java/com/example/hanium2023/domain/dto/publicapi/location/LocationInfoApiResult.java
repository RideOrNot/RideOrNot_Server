package com.example.hanium2023.domain.dto.publicapi.location;

import com.example.hanium2023.domain.entity.CallHistory;
import com.example.hanium2023.enums.DirectionCodeEnum;
import com.example.hanium2023.enums.TrainStatusCodeEnum;
import com.example.hanium2023.util.TimeUtil;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class LocationInfoApiResult {
    //    (0 : 상행/내선, 1 : 하행/외선)
    @JsonProperty("updnLine")
    private Integer directionCode;

    @JsonProperty("subwayNm")
    private String lineName;
    @JsonProperty("subwayId")
    private Integer lineId;

    @JsonProperty("recptnDt")
    private String createdAt;

    @JsonProperty("statnNm")
    private String stationName;

    @JsonProperty("statnId")
    private Integer stationId;

    @JsonProperty("statnTnm")
    private String lastStationName;

    @JsonProperty("statnTid")
    private Integer lastStationId;

    @JsonProperty("trainSttus")
    private Integer trainStatusCode;

    @JsonProperty("directAt")
    private Integer isExpress;

    @JsonProperty("lstcarAt")
    private Integer isLastTrain;

    @JsonProperty("trainNo")
    private String trainNumber;

    private int arrivalTime;

    @Override
    public String toString() {
        return "LocationInfoApiResult{" +
                "directionCode=" + directionCode +
                ", lineName='" + lineName + '\'' +
                ", lineId=" + lineId +
                ", createdAt='" + createdAt + '\'' +
                ", stationName='" + stationName + '\'' +
                ", stationId=" + stationId +
                ", lastStationName='" + lastStationName + '\'' +
                ", lastStationId=" + lastStationId +
                ", trainStatusCode=" + trainStatusCode +
                ", isExpress=" + isExpress +
                ", isLastTrain=" + isLastTrain +
                ", trainNumber='" + trainNumber + '\'' +
                ", arrivalTime=" + arrivalTime +
                '}';
    }

    public CallHistory toCallHistory() {
        String direction;
        if (this.getLineId() == 1002) {
            direction = (this.getDirectionCode() == 0) ?
                    DirectionCodeEnum.INNER_CIRCLE_LINE.getDirection() : DirectionCodeEnum.OUTER_CIRCLE_LINE.getDirection();
        } else {
            direction = (this.getDirectionCode() == 0) ?
                    DirectionCodeEnum.UP_LINE.getDirection() : DirectionCodeEnum.DOWN_LINE.getDirection();
        }
        return CallHistory.builder()
                .arrivalTime(this.getArrivalTime())
                .movingTime(0)
                .direction(direction)
                .lineId(String.valueOf(this.getLineId()))
                .destination(this.getLastStationName())
                .message(null)
                .stationName(this.getStationName())
                .trainStatus(TrainStatusCodeEnum.getEnumByCode(this.getTrainStatusCode()).getStatus())
                .createdAt(this.getCreatedAt())
                .trainNumber(this.getTrainNumber())
                .loggedAt(TimeUtil.getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd a hh:mm:ss", Locale.KOREAN)))
                .build();
    }
}
