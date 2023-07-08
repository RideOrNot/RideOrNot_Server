package com.example.hanium2023.controller;

import com.example.hanium2023.service.StationExitService;
import com.example.hanium2023.service.StationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class DataInsertController {
    private final StationExitService stationExitService;
    private final StationService stationService;
    @GetMapping("/add-exit")
    public String addExit() throws IOException, InterruptedException {
        stationExitService.addExit();
        return "add";
    }
    @GetMapping("/add-station")
    public String addStation() throws IOException, InterruptedException {
        stationService.addStation();
        return "add";
    }
}
