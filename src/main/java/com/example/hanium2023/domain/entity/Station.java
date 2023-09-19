package com.example.hanium2023.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Station {
    @Id
    private Integer stationId;
    private String statnName;
    @Column(precision =8, scale = 6)
    private BigDecimal statnLatitude;
    @Column(precision =9, scale = 6)
    private BigDecimal statnLongitude;

    private Integer beforeStationId1;
    private String beforeStation1;
    private Integer beforeStationTime1;
    private Integer nextStationId1;
    private String nextStation1;
    private Integer nextStationTime1;
    private Integer beforeStationId2;
    private String beforeStation2;
    private Integer beforeStationTime2;
    private Integer nextStationId2;
    private String nextStation2;
    private Integer nextStationTime2;
    private String SKStationCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "line_id", nullable = false)
    private Line line;

    @OneToMany(mappedBy = "station", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<StationExit> stationExits  = new ArrayList<>();

    @OneToMany(mappedBy = "station", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<ExitUserCountPerDay> exitUserCountPerDays  = new ArrayList<>();

    @OneToMany(mappedBy = "station", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<ExitUserCountPerTime> exitUserCountPerTimes  = new ArrayList<>();

    public void updateSKStationCode(String code){
        this.SKStationCode = code;
    }

    public void updateNextStationTime1(Integer time){
        System.out.println("++++++++++++++++엔티티 함수 실행 전+++++++++++++++");
        this.nextStationTime1 = time;
        System.out.println("++++++++++++++++엔티티 함수 실행 후+++++++++++++++");
    }

    public void updateBeforeStationTime1(Integer time){
        this.beforeStationTime1 = time;
    }
}
