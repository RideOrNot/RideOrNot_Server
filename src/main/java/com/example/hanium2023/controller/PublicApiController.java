package com.example.hanium2023.controller;

import com.example.hanium2023.domain.dto.arrivalinfo.PushAlarmResponse;
import com.example.hanium2023.domain.dto.ressponse.Response;
import com.example.hanium2023.service.PublicApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PublicApiController {
    private final PublicApiService publicApiService;

    @GetMapping("/arrivalInfo")
    public Response<PushAlarmResponse> getRealTimeInfoForPushAlarm(@RequestParam String stationName, @RequestParam String exitName) {
        return Response.success(publicApiService.getRealTimeInfoForPushAlarm(stationName, exitName));
    }
}
