package com.example.hanium2023.domain.dto.user;

import com.example.hanium2023.domain.entity.User;
import com.example.hanium2023.util.encryption.converter.StringCryptoConverter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Convert;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserProfileDto {
    Long id;
    Integer ageRange;
    Integer gender;
    String nickName;
    String email;
    public static UserProfileDto of(User user){
        return UserProfileDto.builder()
                .id(user.getUserId())
                .nickName(user.getNickname())
                .gender(user.getGender())
                .ageRange(user.getAgeRange())
                .email(user.getEmail())
                .build();
    }
}
