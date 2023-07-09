package com.example.hanium2023.controller;

import com.example.hanium2023.domain.dto.arrivalinfo.ArrivalInfoResponse;
import com.example.hanium2023.service.PublicApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class PublicApiController {
    private final PublicApiService publicApiService;

    @GetMapping("/arrivalInfo")
    public List<ArrivalInfoResponse> test(@RequestParam String stationName, @RequestParam String exitName) {
        return publicApiService.getRealTimeInfos(stationName, exitName);
    }
}
