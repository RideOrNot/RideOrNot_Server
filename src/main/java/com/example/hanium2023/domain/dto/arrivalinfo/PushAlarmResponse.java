package com.example.hanium2023.domain.dto.arrivalinfo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Data
public class PushAlarmResponse {
    List<ArrivalInfoPushAlarmResponse> arrivalInfo;
    String currentTime;

    public PushAlarmResponse(List<ArrivalInfoPushAlarmResponse> arrivalInfo) {
        this.arrivalInfo = arrivalInfo;
        this.currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd a HH:mm:ss"));
    }
}
