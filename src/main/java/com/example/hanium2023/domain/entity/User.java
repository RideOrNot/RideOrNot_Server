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
    //private String password;
    //private boolean success;
    //private String role;
    //private String provider;
    //private String providerId;
    public void updateWalkingSpeed(double walkingSpeed){
        this.walkingSpeed = walkingSpeed;
    }

    public void updateRunningSpeed(double runningSpeed){
        this.runningSpeed = runningSpeed;
    }

    /*public User(String nickname, String username, String email){
        //this.userId = userId;
        this.nickname = nickname;
        this.username = username;
        //this.password = password;
        this.email = email;
    }*/
}
