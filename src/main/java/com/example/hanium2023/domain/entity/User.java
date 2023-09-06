package com.example.hanium2023.domain.entity;

import com.example.hanium2023.util.converter.StringCryptoConverter;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Convert(converter = StringCryptoConverter.class)
    private String nickname;
    private String email;
    private double walkingSpeed;
    private double runningSpeed;
    private double InitialWalkingSpeed;
    private double InitialRunningSpeed;
    private int ageRange;
    private int gender;
    private String username;
    public void updateWalkingSpeed(double walkingSpeed){
        this.walkingSpeed = walkingSpeed;
    }

    public void updateRunningSpeed(double runningSpeed){
        this.runningSpeed = runningSpeed;
    }
}
