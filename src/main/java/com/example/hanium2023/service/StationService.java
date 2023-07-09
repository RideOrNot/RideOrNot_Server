package com.example.hanium2023.service;

import com.example.hanium2023.domain.entity.Station;
import com.example.hanium2023.domain.entity.StationExit;
import com.example.hanium2023.repository.StationExitRepository;
import com.example.hanium2023.repository.StationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StationService {
    private final StringRedisTemplate stringRedisTemplate;
    private final StationExitRepository stationExitRepository;


    public String insertDistances() {
        ValueOperations<String, String> stringStringValueOperations = stringRedisTemplate.opsForValue();
        List<StationExit> stationExitList = stationExitRepository.findAll();
        for (StationExit stationExit : stationExitList) {
            Station station = stationExit.getStation();
            double distance = calculateDistance(stationExit.getExitLatitude().doubleValue(),
                    stationExit.getExitLongitude().doubleValue(),
                    station.getStatnLatitude().doubleValue(),
                    station.getStatnLongitude().doubleValue());
            stringStringValueOperations.set(station.getStationId().toString() + "/" + station.getStatnName() + "/" + stationExit.getExitName(),
                    String.valueOf(distance));
        }
        return "success";
    }

    private static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));

        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        dist = dist * 1609.344;

        return dist;
    }

    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private static double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }
}
