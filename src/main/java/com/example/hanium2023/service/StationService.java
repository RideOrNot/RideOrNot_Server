package com.example.hanium2023.service;

import com.example.hanium2023.domain.dto.congestion.CongestionResponse;
import com.example.hanium2023.domain.dto.publicapi.location.LocationInfoPushAlarm;
import com.example.hanium2023.domain.dto.station.ArrivalInfoResponse;
import com.example.hanium2023.domain.dto.station.ArrivalInfoPushAlarmResponse;
import com.example.hanium2023.domain.dto.station.LocationInfoPushAlarmResponse;
import com.example.hanium2023.domain.dto.station.StationInfoPageResponse;
import com.example.hanium2023.domain.entity.CallHistory;
import com.example.hanium2023.domain.entity.Line;
import com.example.hanium2023.domain.entity.Station;
import com.example.hanium2023.domain.entity.StationExit;
import com.example.hanium2023.repository.CallHistoryRepository;
import com.example.hanium2023.repository.LineRepository;
import com.example.hanium2023.repository.StationExitRepository;
import com.example.hanium2023.repository.StationRepository;
import com.example.hanium2023.util.CsvParsing;
import com.example.hanium2023.util.KatecToLatLong;
import com.example.hanium2023.util.TimeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class StationService {
    @Value("${public-api-key.lat-lon-key}")
    private String key;
    private final StringRedisTemplate stringRedisTemplate;
    private final StationExitRepository stationExitRepository;
    private final StationRepository stationRepository;
    private final LineRepository lineRepository;
    private final ArrivalInfoService arrivalInfoService;
    private final PublicApiService publicApiService;
    private final LocationInfoService locationInfoService;
    private final CallHistoryRepository callHistoryRepository;


    public ArrivalInfoPushAlarmResponse getPushAlarmFromArrivalInfo(String stationName, String exitName) {
        ArrivalInfoPushAlarmResponse response = new ArrivalInfoPushAlarmResponse(arrivalInfoService.getRealTimeInfoForPushAlarm(stationName, exitName));
        response.setCongestion(publicApiService.getCongestionForPushAlarm(stationName, exitName).getCongestionMessage());
        return response;
    }

    public LocationInfoPushAlarmResponse getPushAlarmFromLocationInfo(String stationName, String exitName) {
        List<LocationInfoPushAlarm> locationInfoForPushAlarm = locationInfoService.getLocationInfoForPushAlarm(stationName, exitName);
        for (LocationInfoPushAlarm l : locationInfoForPushAlarm) {
            CallHistory callHistory = CallHistory.builder()
                    .arrivalTime(l.getArrivalTime())
                    .movingTime(l.getMovingTime())
                    .direction(l.getDirection())
                    .lineId(l.getLineId())
                    .destination(l.getDestination())
                    .message(l.getMessage())
                    .stationName(l.getStationName())
                    .trainStatus(l.getTrainStatus())
                    .createdAt(l.getCreatedAt())
                    .loggedAt(TimeUtil.getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd a hh:mm:ss", Locale.KOREAN)))
                    .build();
            callHistoryRepository.save(callHistory);
        }
        LocationInfoPushAlarmResponse response = new LocationInfoPushAlarmResponse(locationInfoForPushAlarm);
        response.setCongestion(publicApiService.getCongestionForPushAlarm(stationName, exitName).getCongestionMessage());
        return response;
    }

    public StationInfoPageResponse getStationInfo(String stationName, String lineId) {
        CongestionResponse congestionResponse = publicApiService.getCongestionForStationInfo(stationName);
        return new StationInfoPageResponse(arrivalInfoService.getRealTimeInfoForStationInfoPage(stationName, lineId), congestionResponse.getCongestionMessage());
    }

    public ArrivalInfoResponse getStationArrivalInfo(String stationName) {
        return new ArrivalInfoResponse(arrivalInfoService.getArrivalInfo(stationName));
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

    //    @Transactional
    public void insertStationTime() throws IOException, InterruptedException {
        CsvParsing stationTimeCsvParsing = new CsvParsing("station_time.csv", ",");
        ArrayList<String[]> stationTimeList = new ArrayList<>();

        String[] line = null;
        int lineCount = 0;

        // 역간 정보들을 읽어 들임
        while ((line = stationTimeCsvParsing.nextRead()) != null) {
            if (lineCount == 0) {
                lineCount++;
                continue;
            }
            stationTimeList.add(line);
        }

        for (int i = 1; i < stationTimeList.size() - 1; i++) {
            List<Station> stationList = stationRepository.findAllByStatnNameAndLine_LineId(stationTimeList.get(i)[2], Integer.valueOf("100" + stationTimeList.get(i)[1]));
            // 현재 db 데이터에 문제가 있어 해당 조건 임시 추가
            if (stationList.size() == 1) {
                Station station = stationList.get(0);

                // 다음 행의 역이 db에 저장된 nextStation과 같다면 소요 시간을 저장
                if (stationTimeList.get(i + 1)[2].equals(station.getNextStation1())) {
                    Integer seconds = TimeUtil.convertString2Secs(stationTimeList.get(i)[3]);
                    if (seconds == 0)
                        continue;
                    station.updateNextStationTime1(seconds);
                }

                // 이전 행의 역이 db에 저장된 beforeStation과 같다면 소요 시간을 저장
                if (stationTimeList.get(i - 1)[2].equals(station.getBeforeStation1())) {
                    Integer seconds = TimeUtil.convertString2Secs(stationTimeList.get(i - 1)[3]);
                    station.updateBeforeStationTime1(seconds);
                }
                stationRepository.save(station);
            }
        }
        List<Station> stationList = stationRepository.findAll();
        for (Station station : stationList) {
            if (station.getBeforeStationTime1() == null || station.getBeforeStationTime1() == 0) {
                station.updateBeforeStationTime1(90);
            }
            if (station.getNextStationTime1() == null || station.getNextStationTime1() == 0) {
                station.updateNextStationTime1(90);
            }
            stationRepository.save(station);
        }
    }

    public void addStation() throws IOException, InterruptedException {
        CsvParsing festivalCSVParsing = new CsvParsing("file path", "\t");
        String[] line = null;

        int lineCount = 0;
        while ((line = festivalCSVParsing.nextRead()) != null) {
            if (lineCount == 0) {
                lineCount++;
                continue;
            }
            LinkedHashMap<String, String> latLon = KatecToLatLong.getLatLon(key, line[4], line[3]);
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
        CsvParsing festivalCSVParsing = new CsvParsing("file path", "\t");
        String[] line = null;
        int lineCount = 0;
        while ((line = festivalCSVParsing.nextRead()) != null) {
            if (lineCount == 0) {
                lineCount++;
                continue;
            }
            Optional<Station> existedStation = stationRepository.findById(Integer.valueOf(line[0]));
            if (existedStation.isEmpty()) continue;
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

