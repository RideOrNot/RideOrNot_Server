package com.example.hanium2023.controller;

import com.example.hanium2023.domain.dto.ressponse.Response;
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
    public Response<String> addExit() throws IOException, InterruptedException {
        //접근 제한을 위한 주석처리
        //stationExitService.addExit();
        return Response.success("add");
    }
    @GetMapping("/add-station")
    public Response<String> addStation() throws IOException, InterruptedException {
        //접근 제한을 위한 주석처리
        //stationService.addStation();
        return Response.success("add");
    }
}
