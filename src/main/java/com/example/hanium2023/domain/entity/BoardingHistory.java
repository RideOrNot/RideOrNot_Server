package com.example.hanium2023.domain.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BoardingHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long boardingHistoryId;
    private boolean boarded;
    private int movingSpeedStep;
    private double movingSpeed;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
    private LocalDateTime boardedTime;
    private String direction;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({@JoinColumn(name = "exit_name"), @JoinColumn(name = "statn_id")})
    private StationExit stationExit;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
