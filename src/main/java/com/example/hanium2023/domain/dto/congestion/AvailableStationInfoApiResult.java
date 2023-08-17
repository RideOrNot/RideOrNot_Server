package com.example.hanium2023.domain.dto.congestion;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AvailableStationInfoApiResult {
    @JsonProperty("subwayLine")
    private String subwayLine;

    @JsonProperty("stationName")
    private String stationName;

    @JsonProperty("stationCode")
    private String StationCode;

}
