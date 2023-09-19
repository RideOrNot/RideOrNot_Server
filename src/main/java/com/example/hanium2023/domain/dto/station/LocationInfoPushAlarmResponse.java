package com.example.hanium2023.domain.dto.station;

import com.example.hanium2023.domain.dto.publicapi.arrivalinfo.ArrivalInfoPushAlarm;
import com.example.hanium2023.domain.dto.publicapi.location.LocationInfoPushAlarm;
import com.example.hanium2023.util.TimeUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Data
public class LocationInfoPushAlarmResponse {
    List<LocationInfoPushAlarm> arrivalInfo;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd a HH:mm:ss", timezone = "Asia/Seoul", locale = "ko")
    LocalDateTime currentTime;
    String congestion;

    public LocationInfoPushAlarmResponse(List<LocationInfoPushAlarm> arrivalInfo) {
        this.arrivalInfo = arrivalInfo;
        this.currentTime = TimeUtil.getCurrentTime();

//        this.currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd a HH:mm:ss").withLocale(Locale.forLanguageTag("ko")));
    }
}
