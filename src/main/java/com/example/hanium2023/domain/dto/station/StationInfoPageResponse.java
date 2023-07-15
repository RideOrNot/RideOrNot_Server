package com.example.hanium2023.domain.dto.station;

import com.example.hanium2023.domain.dto.arrivalinfo.ArrivalInfoStationInfoPageResponse;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class StationInfoPageResponse {
    List<ArrivalInfoStationInfoPageResponse> arrivalInfo;
    double congestion;
}
