package com.example.hanium2023.service;

import com.example.hanium2023.domain.entity.Line;
import com.example.hanium2023.domain.entity.Station;
import com.example.hanium2023.repository.LineRepository;
import com.example.hanium2023.repository.StationRepository;
import com.example.hanium2023.util.CsvParsing;
import com.example.hanium2023.util.KatecToLatLong;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StationService {
    private final LineRepository lineRepository;
    private final StationRepository stationRepository;
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
