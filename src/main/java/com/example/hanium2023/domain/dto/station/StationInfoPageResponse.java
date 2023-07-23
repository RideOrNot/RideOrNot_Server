package com.example.hanium2023.domain.dto.station;

import com.example.hanium2023.domain.dto.arrivalinfo.ArrivalInfoStationInfoPageResponse;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Data
public class StationInfoPageResponse {
    List<ArrivalInfoStationInfoPageResponse> arrivalInfo;
    double congestion;
    String currentTime;
    public StationInfoPageResponse(List<ArrivalInfoStationInfoPageResponse> arrivalInfo, double congestion) {
        this.arrivalInfo = arrivalInfo;
        this.congestion = congestion;
        this.currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd a HH:mm:ss").withLocale(Locale.forLanguageTag("ko")));
    }
}
