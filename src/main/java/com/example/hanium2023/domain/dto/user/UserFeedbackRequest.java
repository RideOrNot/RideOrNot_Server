package com.example.hanium2023.domain.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserFeedbackRequest {
    boolean subwayBoarded;
    int movingSpeedStep;
}
