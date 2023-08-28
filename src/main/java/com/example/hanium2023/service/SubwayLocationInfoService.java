package com.example.hanium2023.service;

import com.example.hanium2023.domain.dto.arrivalinfo.ArrivalInfoApiResult;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubwayLocationInfoService {
    private final PublicApiService publicApiService;
    private List<ArrivalInfoApiResult> getArrivalInfoFromPublicApi(String lineName) {
        JSONObject apiResultJsonObject = publicApiService.getApiResult(publicApiService.buildSubwayLocationApiUrl(lineName));
        Optional<JSONArray> jsonArray = Optional.ofNullable((JSONArray) apiResultJsonObject.get("realtimePositionList"));
        List<ArrivalInfoApiResult> arrivalInfoApiResultList = new ArrayList<>();

        if (jsonArray.isPresent()) {
            arrivalInfoApiResultList = jsonUtil.convertJsonArrayToDtoList(jsonArray.get(), ArrivalInfoApiResult.class)
                    .stream()
                    .map(this::correctArrivalTime)
                    .filter(this::removeExpiredArrivalInfo)
                    .filter(filterPredicate)
                    .collect(Collectors.toList());
        }
        return arrivalInfoApiResultList;
    }
}
