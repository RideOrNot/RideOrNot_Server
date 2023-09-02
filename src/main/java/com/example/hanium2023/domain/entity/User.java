package com.example.hanium2023.domain.entity;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.security.Timestamp;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private String userId;
    private String nickname;
    private String email;
    private double walkingSpeed;
    private double runningSpeed;
    private double InitialWalkingSpeed;
    private double InitialRunningSpeed;
    private int ageRange;
    private int gender;
    private String password;
    private String username;
    private boolean success;
    //private String role;
    //private String provider;
    //private String providerId;
    public void updateWalkingSpeed(double walkingSpeed){
        this.walkingSpeed = walkingSpeed;
    }

    public void updateRunningSpeed(double runningSpeed){
        this.runningSpeed = runningSpeed;
    }

    @Builder
    public User(String userId, String nickname, String password, String username, String email, String role, String provider, String providerId){
        this.userId = userId;
        this.nickname = nickname;
        this.password = password;
        this.username = username;
        this.email = email;
        //this.role = role;
        //this.provider = provider;
        //this.providerId = providerId;
    }
}
