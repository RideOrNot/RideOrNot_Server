package com.example.hanium2023.service;

import com.example.hanium2023.domain.dto.publicapi.arrivalinfo.ArrivalInfoApiResult;
import com.example.hanium2023.domain.dto.publicapi.location.LocationInfoApiResult;
import com.example.hanium2023.util.JsonUtil;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LocationInfoService {
    private final PublicApiService publicApiService;
    private final JsonUtil jsonUtil;

    public List<LocationInfoApiResult> getLocationInfoFromPublicApi(String lineName) {
        JSONObject apiResultJsonObject = publicApiService.getApiResult(publicApiService.getLocationApiUrl(lineName));
        Optional<JSONArray> jsonArray = Optional.ofNullable((JSONArray) apiResultJsonObject.get("realtimePositionList"));
        List<LocationInfoApiResult> arrivalInfoApiResultList = new ArrayList<>();

        if (jsonArray.isPresent()) {
            arrivalInfoApiResultList = jsonUtil.convertJsonArrayToDtoList(jsonArray.get(), LocationInfoApiResult.class);
        }
        return arrivalInfoApiResultList;
    }
}
