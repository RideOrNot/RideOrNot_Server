package com.example.hanium2023.service;

import com.example.hanium2023.domain.dto.user.UserDto;
import com.example.hanium2023.domain.dto.user.UserAutoFeedbackRequest;
import com.example.hanium2023.domain.entity.User;
import com.example.hanium2023.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DecimalFormat;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    @Transactional
    public String doFeedback(UserAutoFeedbackRequest userAutoFeedbackRequest) {
        User user = userRepository.findById(1L).get();
        UserDto userDto = new UserDto(user);

        double alpha = 0.2;
        double beta = 0.35;

        double newMovingSpeed;

        if (1 <= userAutoFeedbackRequest.getMovingSpeedStep() && userAutoFeedbackRequest.getMovingSpeedStep() <= 3) {
            if (userAutoFeedbackRequest.isSubwayBoarded()) {
                newMovingSpeed = calculateNewMovingSpeed(userDto.getWalkingSpeed(), userDto.getInitialWalkingSpeed(), alpha, 0.8);
            } else {
                newMovingSpeed = calculateNewMovingSpeed(userDto.getWalkingSpeed(), userDto.getInitialWalkingSpeed(), beta, 1.2);
            }
            user.updateWalkingSpeed(newMovingSpeed);
        } else {
            if (userAutoFeedbackRequest.isSubwayBoarded()) {
                newMovingSpeed = calculateNewMovingSpeed(userDto.getRunningSpeed(), userDto.getInitialRunningSpeed(), alpha, 0.8);
            } else {
                newMovingSpeed = calculateNewMovingSpeed(userDto.getRunningSpeed(), userDto.getInitialRunningSpeed(), beta, 1.2);
            }
            user.updateRunningSpeed(newMovingSpeed);
        }
        return String.valueOf(newMovingSpeed);
    }


    private double calculateNewMovingSpeed(double currentSpeed, double initialSpeed, double weight, double multiplier) {
        double newMovingSpeed = (1 - weight) * currentSpeed + weight * initialSpeed * multiplier;
        DecimalFormat decimalFormat = new DecimalFormat("#.#####");
        String formattedValue = decimalFormat.format(newMovingSpeed);
        return Double.parseDouble(formattedValue);
    }
}
