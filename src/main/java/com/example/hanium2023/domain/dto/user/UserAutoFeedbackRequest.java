package com.example.hanium2023.domain.dto.user;

import com.example.hanium2023.domain.entity.BoardingHistory;
import com.example.hanium2023.domain.entity.StationExit;
import com.example.hanium2023.domain.entity.StationExitID;
import com.example.hanium2023.domain.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserAutoFeedbackRequest {
    boolean boarded;
    int movingSpeedStep;
    String stationName;
    String boardedTime;
    Integer lineId;
    String exitName;
    String direction;

    public BoardingHistory toEntity(StationExit stationExit, User user) {
        return BoardingHistory.builder()
                .boarded(this.boarded)
                .movingSpeedStep(this.movingSpeedStep)
                .boardedTime(LocalDateTime.parse(this.boardedTime, DateTimeFormatter.ofPattern("yyyy-MM-dd a HH:mm:ss")))
                .direction(this.direction)
                .stationExit(stationExit)
                .user(user)
                .build();
    }
}
