package com.example.hanium2023.controller;

import com.example.hanium2023.domain.dto.publicapi.location.LocationInfoApiResult;
import com.example.hanium2023.domain.dto.publicapi.location.LocationInfoResponse;
import com.example.hanium2023.domain.dto.station.ArrivalInfoResponse;
import com.example.hanium2023.domain.dto.station.PushAlarmResponse;
import com.example.hanium2023.domain.dto.ressponse.Response;
import com.example.hanium2023.domain.dto.station.StationInfoPageResponse;
import com.example.hanium2023.service.LocationInfoService;
import com.example.hanium2023.service.StationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.naming.Name;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/stations")
public class StationController {
    private final StationService stationService;
    private final LocationInfoService locationInfoService;

    @GetMapping("/pushAlarm")
    public Response<PushAlarmResponse> getRealTimeInfoForPushAlarm(@RequestParam String stationName, @RequestParam String exitName) {
        return Response.success(stationService.getPushAlarm(stationName, exitName));
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

    @GetMapping("/test/{lineName}/{stationName}")
    public Response<List<LocationInfoResponse>> test(@PathVariable(name = "lineName") String lineName, @PathVariable(name = "stationName") String stationName) {
        List<LocationInfoResponse> locationInfoApiResultList = locationInfoService.getLocationInfoFromPublicApi(lineName)
                .stream()
                .filter(l -> {
                    return l.getStationName().equals(stationName);
                })
                .map(LocationInfoResponse::new)
                .sorted(Comparator.comparing(LocationInfoResponse::getDirection))
                .collect(Collectors.toList());
        return Response.success(locationInfoApiResultList);
    }

    @GetMapping("/insert/stationTime")
    public String insertStationTime() throws IOException, InterruptedException {
        stationService.insertStationTime();
        return "success";
    }
}
