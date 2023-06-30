package com.example.hanium2023.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@IdClass(StationExitTmp.class)
public class StationExitTmp implements Serializable {
    @Id
    private String exitName;
    @Column(precision =8, scale = 6)
    private BigDecimal exitLatitude;
    @Column(precision =9, scale = 6)
    private BigDecimal exitLongitude;

    @Id
    private Integer stationId;
}
