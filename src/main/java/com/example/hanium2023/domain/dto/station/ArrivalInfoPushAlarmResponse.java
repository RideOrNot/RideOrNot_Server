package com.example.hanium2023.domain.dto.station;

import com.example.hanium2023.domain.dto.publicapi.arrivalinfo.ArrivalInfoPushAlarm;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Data
public class ArrivalInfoPushAlarmResponse {
    List<ArrivalInfoPushAlarm> arrivalInfo;
    String currentTime;
    String congestion;

    public ArrivalInfoPushAlarmResponse(List<ArrivalInfoPushAlarm> arrivalInfo) {
        this.arrivalInfo = arrivalInfo;
        this.currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd a HH:mm:ss").withLocale(Locale.forLanguageTag("ko")));
    }
}
