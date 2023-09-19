package com.example.hanium2023.domain.dto.congestion;

import com.example.hanium2023.domain.entity.ExitUserCountPerTime;
import com.example.hanium2023.domain.entity.Station;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class PassengerByTimeResult {
    @JsonProperty("exit")
    private String exit;
    @JsonProperty("userCount")
    private Integer userCount;
    @JsonProperty("datetime")
    private String datetime;

    public static PassengerByTimeResult of(ExitUserCountPerTime exitUserCountPerTime) {
        return PassengerByTimeResult.builder()
                .exit(exitUserCountPerTime.getExitName())
                .userCount(exitUserCountPerTime.getUserCount())
                .datetime(exitUserCountPerTime.getDatetime())
                .build();
    }

    public ExitUserCountPerTime toEntity(Station station) {
        return ExitUserCountPerTime.builder()
                .datetime(this.datetime)
                .station(station)
                .userCount(this.userCount == null ? 0 : this.userCount)
                .exitName(this.exit)
                .build();
    }
}
