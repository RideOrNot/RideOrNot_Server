package com.example.hanium2023.service;

import com.example.hanium2023.domain.entity.Station;
import com.example.hanium2023.domain.entity.StationExit;
import com.example.hanium2023.repository.StationExitRepository;
import com.example.hanium2023.repository.StationRepository;
import com.example.hanium2023.util.CsvParsing;
import com.example.hanium2023.util.KatecToLatLong;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StationExitService {
    @Value("${public-api-key.lat-lon-key}")
    private String key;
    private final StationExitRepository stationExitRepository;
    private final StationRepository stationRepository;
    public void addExit() throws IOException, InterruptedException {
        CsvParsing festivalCSVParsing = new CsvParsing("file path");
        String[] line = null;

        int lineCount = 0;
        while ((line = festivalCSVParsing.nextRead()) != null) {
            if (lineCount == 0) {
                lineCount++;
                continue;
            }
            LinkedHashMap<String, String> latLon = KatecToLatLong.getLatLon(key, line[3], line[2]);
            System.out.println(line[0]);
            System.out.println(line[1]);
            Optional<Station> station = stationRepository.findById(Integer.valueOf(line[0]));
            if(!station.isPresent()) continue;
            StationExit exit = StationExit.builder()
                    .exitLatitude(new BigDecimal(latLon.get("lat")))
                    .exitLongitude(new BigDecimal(latLon.get("lon")))
                    .station(station.get())
                    .exitName(line[1])
                    .build();
            stationExitRepository.save(exit);
        }
    }
}
