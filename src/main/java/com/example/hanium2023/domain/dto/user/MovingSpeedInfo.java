package com.example.hanium2023.domain.dto.user;

import com.example.hanium2023.enums.MovingMessageEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MovingSpeedInfo {
    MovingMessageEnum movingMessageEnum;
    Double movingSpeed;
}
