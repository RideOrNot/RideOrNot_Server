package com.example.hanium2023.domain.dto.publicapi.arrivalinfo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ArrivalInfoApiResult {
    @JsonProperty("barvlDt")
    private int arrivalTime;

    @JsonProperty("updnLine")
    private String direction;

    @JsonProperty("subwayId")
    private String lineId;

    @JsonProperty("trainLineNm")
    private String destination;

    @JsonProperty("recptnDt")
    private String createdAt;

    @JsonProperty("statnNm")
    private String stationName;

    @JsonProperty("arvlCd")
    private int trainStatusCode;
    @JsonProperty("arvlMsg2")
    private String arrivalMessage2;

    @JsonProperty("arvlMsg3")
    private String currentStationName;

    @JsonProperty("btrainNo")
    private String trainNumber;

    @Override
    public String toString() {
        return "ArrivalInfoApiResult{" +
                "arrivalTime=" + arrivalTime +
                ", direction='" + direction + '\'' +
                ", lineId='" + lineId + '\'' +
                ", destination='" + destination + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", stationName='" + stationName + '\'' +
                ", trainStatusCode=" + trainStatusCode +
                ", arrivalMessage2='" + arrivalMessage2 + '\'' +
                ", currentStationName='" + currentStationName + '\'' +
                ", trainNumber='" + trainNumber + '\'' +
                '}';
    }
}
