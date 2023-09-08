package com.example.hanium2023.domain.dto.station;

import com.example.hanium2023.domain.dto.publicapi.arrivalinfo.ArrivalInfoStationInfoPageResponse;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Data
public class ArrivalInfoResponse {
    List<ArrivalInfoStationInfoPageResponse> arrivalInfo;
    String currentTime;

    public ArrivalInfoResponse(List<ArrivalInfoStationInfoPageResponse> arrivalInfo) {
        this.arrivalInfo = arrivalInfo;
        this.currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd a HH:mm:ss").withLocale(Locale.forLanguageTag("ko")));
    }
}
