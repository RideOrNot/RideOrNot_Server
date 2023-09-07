package com.example.hanium2023.domain.dto.congestion;

import com.example.hanium2023.domain.entity.ExitUserCountPerDay;
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
public class PassengerPerDayResult {
    @JsonProperty("exit")
    private String exit;

    @JsonProperty("userCount")
    private Integer userCount;
    public static PassengerPerDayResult of(ExitUserCountPerDay exitUserCountPerDay){
        return PassengerPerDayResult.builder()
                .exit(exitUserCountPerDay.getExitName())
                .userCount(exitUserCountPerDay.getUserCount())
                .build();
    }

    public ExitUserCountPerDay toEntity (Station station, String dow){
        return ExitUserCountPerDay.builder()
                .dow(dow)
                .station(station)
                .userCount(this.userCount == null ? 0 : this.userCount)
                .exitName(this.exit)
                .build();
    }

}
