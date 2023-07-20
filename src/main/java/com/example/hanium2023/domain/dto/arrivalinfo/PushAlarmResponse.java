package com.example.hanium2023.domain.dto.arrivalinfo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PushAlarmResponse {
    List<ArrivalInfoPushAlarmResponse> arrivalInfo;
}
