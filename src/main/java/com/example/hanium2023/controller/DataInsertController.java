package com.example.hanium2023.controller;

import com.example.hanium2023.domain.dto.ressponse.Response;
import com.example.hanium2023.service.StationExitService;
import com.example.hanium2023.service.StationService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class DataInsertController {
    private final StationExitService stationExitService;
    private final StationService stationService;
    @ApiOperation(value = "초기 데이터 삽입을 위한 api 이므로 호출하지 말것.(현재는 접근 제한됨)")
    @GetMapping("/add-exit")
    public Response<String> addExit() throws IOException, InterruptedException {
        //접근 제한을 위한 주석처리
        //stationExitService.addExit();
        return Response.success("add");
    }
    @ApiOperation(value = "초기 데이터 삽입을 위한 api 이므로 호출하지 말것.(현재는 접근 제한됨)")
    @GetMapping("/add-station")
    public Response<String> addStation() throws IOException, InterruptedException {
        //접근 제한을 위한 주석처리
        //stationService.addStation();
        return Response.success("add");
    }
}
