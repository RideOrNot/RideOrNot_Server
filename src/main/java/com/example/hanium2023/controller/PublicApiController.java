package com.example.hanium2023.controller;

import com.example.hanium2023.domain.dto.ArrivalInfoApiResult;
import com.example.hanium2023.domain.dto.ArrivalInfoResponse;
import com.example.hanium2023.service.PublicApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PublicApiController {
    private final PublicApiService publicApiService;

    @GetMapping("/test")
    public List<ArrivalInfoResponse> test(@RequestParam String stationName) {
        return publicApiService.getRealTimeInfos(stationName);
    }
}
