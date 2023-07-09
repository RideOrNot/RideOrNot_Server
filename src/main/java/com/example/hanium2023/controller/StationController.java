package com.example.hanium2023.controller;

import com.example.hanium2023.service.StationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/station")
public class StationController {
    private final StationService stationService;
    @GetMapping("/insert/distance")
    public String insertDistances(){
        return stationService.insertDistances();
    }
    @GetMapping("/insert/stationId")
    public String insertStationId(){
        return stationService.insertStationId();
    }
}
