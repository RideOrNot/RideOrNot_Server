package com.example.hanium2023.controller;

import com.example.hanium2023.domain.dto.station.ArrivalInfoResponse;
import com.example.hanium2023.domain.dto.station.PushAlarmResponse;
import com.example.hanium2023.domain.dto.ressponse.Response;
import com.example.hanium2023.domain.dto.station.StationInfoPageResponse;
import com.example.hanium2023.service.StationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/stations")
public class StationController {
    private final StationService stationService;

    @GetMapping("/pushAlarm")
    public Response<PushAlarmResponse> getRealTimeInfoForPushAlarm(@RequestParam String stationName, @RequestParam String exitName) {
        return Response.success(stationService.getPushAlarmFromArrivalInfo(stationName, exitName));
    }

    @GetMapping("/{stationName}/{lineId}")
    public Response<StationInfoPageResponse> getStationInfo(@PathVariable("stationName") String stationName, @PathVariable("lineId") String lineId) {
        return Response.success(stationService.getStationInfo(stationName, lineId));
    }

    @GetMapping("/arrivalInfo/{stationName}")
    public Response<ArrivalInfoResponse> getArrivalInfo(@PathVariable("stationName") String stationName) {
        return Response.success(stationService.getStationArrivalInfo(stationName));
    }

    @GetMapping("/insert/distance")
    public String insertDistances() {
        return stationService.insertDistances();
    }

    @GetMapping("/insert/stationId")
    public String insertStationId() {
        return stationService.insertStationId();
    }

    @GetMapping("/test/{stationName}/{exitName}")
    public Response<PushAlarmResponse> test(@PathVariable(name = "stationName") String stationName, @PathVariable(name = "exitName") String exitName) {
        return Response.success(stationService.getPushAlarmFromLocationInfo(stationName, exitName));
    }

    @GetMapping("/insert/stationTime")
    public String insertStationTime() throws IOException, InterruptedException {
        stationService.insertStationTime();
        return "success";
    }
}
