package com.example.hanium2023.domain.dto.user;

import com.example.hanium2023.domain.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    Long userId;
    double walkingSpeed;
    double runningSpeed;
    double InitialWalkingSpeed;
    double InitialRunningSpeed;

    public UserDto(User user) {
        this.InitialRunningSpeed = user.getInitialRunningSpeed();
        this.InitialWalkingSpeed = user.getInitialWalkingSpeed();
        this.userId = user.getUserId();
        this.runningSpeed = user.getRunningSpeed();
        this.walkingSpeed = user.getWalkingSpeed();
    }
}
