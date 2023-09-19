package com.example.hanium2023.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExitUserCountPerDay {
    @Id
    @Column(name = "exit_user_count_per_day_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer userCount;
    private String dow;
    private String exitName;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "station_id", nullable = false)
    private Station station;

}
