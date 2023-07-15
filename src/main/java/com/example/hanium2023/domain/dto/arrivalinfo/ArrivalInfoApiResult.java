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

    @Override
    public String toString() {
        return "ArrivalInfoApiResult{" +
                "arrivalTime=" + arrivalTime +
                ", direction='" + direction + '\'' +
                ", lineName='" + lineId + '\'' +
                ", destination='" + destination + '\'' +
                ", createdAt='" + createdAt + '\'' +
                '}';
    }
}
