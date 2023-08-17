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
public class PassengerByTimeResult {
    @JsonProperty("exit")
    private String exit;
    @JsonProperty("userCount")
    private Integer userCount;
    @JsonProperty("datetime")
    private String datetime;
}
