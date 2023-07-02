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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "line_id", nullable = false)
    private Line line;

    @OneToMany(mappedBy = "station", cascade = CascadeType.REMOVE, orphanRemoval = true)
    List<StationExit> stationExits  = new ArrayList<>();


}
