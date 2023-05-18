package com.example.hanium2023.domain.dto;

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
//    byte red;
//
//    @JsonProperty("r")
//    public byte getR() {
//        return red;
//    }
//
//    @JsonProperty("red")
//    public void setRed(byte red) {
//        this.red = red;
//    }
//    @JsonProperty("b")
    @JsonProperty("barvlDt")
    int arrivalTime;

    @JsonProperty("updnLine")
    String direction;

    @JsonProperty("subwayId")
    String lineName;

    @JsonProperty("trainLineNm")
    String destination;

    @JsonProperty("recptnDt")
    String createdAt;

}
