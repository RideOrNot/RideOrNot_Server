package com.example.hanium2023.controller;

import com.example.hanium2023.service.PublicApiService;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PublicApiController {
    private final PublicApiService publicApiService;

    @GetMapping("/test")
    public JSONArray test(@RequestParam String stationName) {
        return publicApiService.getRealTimeInfos(stationName);
    }
}
