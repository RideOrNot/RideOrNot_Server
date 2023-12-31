package com.example.hanium2023.domain.dto.station;

import com.example.hanium2023.domain.dto.publicapi.arrivalinfo.ArrivalInfoStationInfoPageResponse;
import com.example.hanium2023.util.TimeUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Data
public class StationInfoPageResponse {
    List<ArrivalInfoStationInfoPageResponse> arrivalInfo;
    String congestion;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd a HH:mm:ss", timezone = "Asia/Seoul", locale = "ko")
    LocalDateTime currentTime;
    public StationInfoPageResponse(List<ArrivalInfoStationInfoPageResponse> arrivalInfo, String congestion) {
        this.arrivalInfo = arrivalInfo;
        this.congestion = congestion;
//        this.currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd a HH:mm:ss").withLocale(Locale.forLanguageTag("ko")));
        this.currentTime = TimeUtil.getCurrentTime();
    }
}
