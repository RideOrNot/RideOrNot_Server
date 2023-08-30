package com.example.hanium2023.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

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
    private Integer beforeStation1Time;
    private Integer nextStationId1;
    private String nextStation1;
    private Integer nextStation1Time;
    private Integer beforeStationId2;
    private String beforeStation2;
    private Integer beforeStation2Time;
    private Integer nextStationId2;
    private String nextStation2;
    private Integer nextStation2Time;
    private String SKStationCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "line_id", nullable = false)
    private Line line;

    @OneToMany(mappedBy = "station", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<StationExit> stationExits  = new ArrayList<>();

    public void updateSKStationCode(String code){
        this.SKStationCode = code;
    }

    @Transactional
    public void updateNextStation1Time(Integer time){
        this.nextStation1Time = time;
    }

    @Transactional
    public void updateBeforeStation1Time(Integer time){
        this.beforeStation1Time = time;
    }
}
