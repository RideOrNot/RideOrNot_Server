package com.example.hanium2023.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CallHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer callHistoryId;
    private int arrivalTime;
    private long movingTime;
    private String direction;
    private String lineId;
    private String destination;
    private String message;
    private int movingSpeedStep;
    private double movingSpeed;
    private String stationName;
    private String trainStatus;
    private String createdAt;
    private int trainNumber;
    private String loggedAt;
}
