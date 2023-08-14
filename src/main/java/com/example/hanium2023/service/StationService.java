package com.example.hanium2023.service;

import com.example.hanium2023.domain.dto.station.ArrivalInfoResponse;
import com.example.hanium2023.domain.dto.station.StationInfoPageResponse;
import com.example.hanium2023.domain.entity.Line;
import com.example.hanium2023.domain.entity.Station;
import com.example.hanium2023.domain.entity.StationExit;
import com.example.hanium2023.repository.LineRepository;
import com.example.hanium2023.repository.StationExitRepository;
import com.example.hanium2023.repository.StationRepository;
import com.example.hanium2023.util.CsvParsing;
import com.example.hanium2023.util.KatecToLatLong;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StationService {
    @Value("${public-api-key.lat-lon-key}")
    private String key;
    private final StringRedisTemplate stringRedisTemplate;
    private final StationExitRepository stationExitRepository;
    private final StationRepository stationRepository;
    private final LineRepository lineRepository;
    private final PublicApiService publicApiService;

    public StationInfoPageResponse getStationInfo(String stationName, String lineId) {
        return new StationInfoPageResponse(publicApiService.getRealTimeInfoForStationInfoPage(stationName, lineId), 0);
    }

    public ArrivalInfoResponse getStationArrivalInfo(String stationName) {
        return new ArrivalInfoResponse(publicApiService.getArrivalInfo(stationName));
    }

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
            Optional<Line> stationLine = lineRepository.findByCsvLine(Integer.valueOf(line[0]) / 100);
            if (!stationLine.isPresent()) continue;
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
    public void insertRelatedStation() throws IOException, InterruptedException {
        CsvParsing festivalCSVParsing = new CsvParsing("file path");
        String[] line = null;
        int lineCount = 0;
        while ((line = festivalCSVParsing.nextRead()) != null) {
            if (lineCount == 0) {
                lineCount++;
                continue;
            }
            Optional<Station> existedStation = stationRepository.findById(Integer.valueOf(line[0]));
            if(existedStation.isEmpty()) continue;
            Station updatedStation = Station.builder()
                    .statnName(existedStation.get().getStatnName())
                    .stationId(existedStation.get().getStationId())
                    .statnLongitude(existedStation.get().getStatnLongitude())
                    .statnLatitude(existedStation.get().getStatnLatitude())
                    .beforeStationId1(Integer.valueOf((line[9])))
                    .beforeStationId2(Integer.valueOf((line[11])))
                    .nextStationId1(Integer.valueOf((line[5])))
                    .nextStationId2(Integer.valueOf((line[7])))
                    .nextStation1(line[6])
                    .nextStation2(line[8])
                    .beforeStation1(line[10])
                    .beforeStation2(line[12])
                    .line(existedStation.get().getLine())
                    .stationExits(existedStation.get().getStationExits())
                    .build();
            stationRepository.save(updatedStation);
        }
    }
}

