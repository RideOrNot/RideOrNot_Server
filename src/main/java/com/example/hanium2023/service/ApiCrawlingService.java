package com.example.hanium2023.service;

import com.example.hanium2023.domain.dto.publicapi.location.LocationInfoApiResult;
import com.example.hanium2023.domain.entity.CallHistory;
import com.example.hanium2023.repository.CallHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ApiCrawlingService {
    private final LocationInfoService locationInfoService;
    private final CallHistoryRepository callHistoryRepository;
    private static Map<String, LocationInfoApiResult> locationInfoApiResultMap = new HashMap<>();

    @Scheduled(cron = "0/10 * 8-23 * * ?")
    public void crawlLocationInfo() {
        List<LocationInfoApiResult> locationInfoFromPublicApi = locationInfoService.getLocationInfoFromPublicApi("6호선");
        for (LocationInfoApiResult apiResult : locationInfoFromPublicApi) {
            String trainNumber = apiResult.getTrainNumber();
            if (!locationInfoApiResultMap.containsKey(trainNumber) ||
                    !apiResult.getCreatedAt().equals(locationInfoApiResultMap.get(trainNumber).getCreatedAt())) {
                callHistoryRepository.save(apiResult.toCallHistory());
                locationInfoApiResultMap.put(trainNumber, apiResult);
            }
        }
    }
}
