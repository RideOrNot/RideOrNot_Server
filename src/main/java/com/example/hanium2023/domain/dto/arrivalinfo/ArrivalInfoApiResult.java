package com.example.hanium2023.domain.dto.arrivalinfo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ArrivalInfoApiResult {
    @JsonProperty("barvlDt")
    private long arrivalTime;

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
    private int arrivalCode;
    @JsonProperty("arvlMsg2")
    private String arrivalMessage2;

    @JsonProperty("arvlMsg3")
    private String arrivalMessage3;

    @Override
    public String toString() {
        return "ArrivalInfoApiResult{" +
                "arrivalTime=" + arrivalTime +
                ", direction='" + direction + '\'' +
                ", lineId='" + lineId + '\'' +
                ", destination='" + destination + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", stationName='" + stationName + '\'' +
                ", arrivalCode=" + arrivalCode +
                ", arrivalMessage2='" + arrivalMessage2 + '\'' +
                ", arrivalMessage3='" + arrivalMessage3 + '\'' +
                '}';
    }
}
