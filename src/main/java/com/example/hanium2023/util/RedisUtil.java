package com.example.hanium2023.util;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class RedisUtil {
    private final StringRedisTemplate stringRedisTemplate;

    public Integer getStationIdByStationNameAndLineId(String stationName, Integer lineId) {
        ValueOperations<String, String> stringValueOperations = stringRedisTemplate.opsForValue();
        return Integer.parseInt(stringValueOperations.get(stationName + "/" + lineId));
    }
}
