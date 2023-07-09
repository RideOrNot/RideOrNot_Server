<<<<<<< HEAD
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
    private final StationRepository stationRepository;
    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    public String insertDistances() {
        ValueOperations<String, String> stringStringValueOperations = stringRedisTemplate.opsForValue();
        List<StationExit> stationExitList = stationExitRepository.findAll();
        for (StationExit stationExit : stationExitList) {
            Station station = stationExit.getStation();
            double distance = calculateDistance(stationExit.getExitLatitude().doubleValue(),
                    stationExit.getExitLongitude().doubleValue(),
                    station.getStatnLatitude().doubleValue(),
                    station.getStatnLongitude().doubleValue());
            stringStringValueOperations.set(station.getStationId().toString() + "/" + stationExit.getExitName(),
                    String.valueOf(distance));
        }
        return "success";
    }

    public String insertStationId() {
        ValueOperations<String, String> stringStringValueOperations = stringRedisTemplate.opsForValue();
        List<Station> stationList = stationRepository.findAll();
        for (Station station : stationList) {
            stringStringValueOperations.set(station.getStatnName() + "/" + station.getLine().getLineId(),
                    String.valueOf(station.getStationId()));
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

    public void addStation() throws IOException, InterruptedException {
        CsvParsing festivalCSVParsing = new CsvParsing("file path");
        String[] line = null;

        int lineCount = 0;
        while ((line = festivalCSVParsing.nextRead()) != null) {
            if (lineCount == 0) {
                lineCount++;
                continue;
            }
            LinkedHashMap<String, String> latLon = KatecToLatLong.getLatLon(line[4], line[3]);
            System.out.println(line[0]);
            System.out.println(line[1]);
            Optional<Line> stationLine = lineRepository.findByCsvLine(Integer.valueOf(line[0])/100);
            if(!stationLine.isPresent()) continue;
            Station station = Station.builder()
                    .statnName(line[1])
                    .statnLatitude(new BigDecimal(latLon.get("lat")))
                    .statnLongitude(new BigDecimal(latLon.get("lon")))
                    .stationId(Integer.valueOf(line[0]))
                    .line(stationLine.get())
                    .build();
            stationRepository.save(station);
        }
    }
}

