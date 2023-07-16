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

    //    @Transactional
//    public String doFeedback(UserFeedbackRequest userFeedbackRequest) {
//        User user = userRepository.findById(1L).get();
//        UserDto userDto = new UserDto(user);
//
//        double alpha = 0.2;
//        double beta = 0.35;
//
//        double newMovingSpeed;
//        // 탑승했다면
//        if (userFeedbackRequest.isSubwayBoarded()) {
//            // 걸었다면
//            if (1 <= userFeedbackRequest.getMovingSpeedStep() && userFeedbackRequest.getMovingSpeedStep() <= 3) {
//                newMovingSpeed = (1 - alpha) * userDto.getWalkingSpeed() + alpha * userDto.getInitialWalkingSpeed() * 0.8;
//                DecimalFormat decimalFormat = new DecimalFormat("#.#####");
//                String formattedValue = decimalFormat.format(newMovingSpeed);
//                double cutValue = Double.parseDouble(formattedValue);
//                user.updateWalkingSpeed(cutValue);
//                // 뛰었다면
//            } else {
//                newMovingSpeed = (1 - alpha) * userDto.getRunningSpeed() + alpha * userDto.getInitialRunningSpeed() * 0.8;
//                DecimalFormat decimalFormat = new DecimalFormat("#.#####");
//                String formattedValue = decimalFormat.format(newMovingSpeed);
//                double cutValue = Double.parseDouble(formattedValue);
//                user.updateRunningSpeed(cutValue);
//            }
//        }
//        // 탑승하지 못했다면
//        else {
//            // 걸었다면
//            if (1 <= userFeedbackRequest.getMovingSpeedStep() && userFeedbackRequest.getMovingSpeedStep() <= 3) {
//                newMovingSpeed = (1 - beta) * userDto.getWalkingSpeed() + beta * userDto.getInitialWalkingSpeed() * 1.2;
//                DecimalFormat decimalFormat = new DecimalFormat("#.#####");
//                String formattedValue = decimalFormat.format(newMovingSpeed);
//                double cutValue = Double.parseDouble(formattedValue);
//                user.updateWalkingSpeed(cutValue);
//
//                // 뛰었다면
//            } else {
//                newMovingSpeed = (1 - beta) * userDto.getRunningSpeed() + beta * userDto.getInitialRunningSpeed() * 1.2;
//                DecimalFormat decimalFormat = new DecimalFormat("#.#####");
//                String formattedValue = decimalFormat.format(newMovingSpeed);
//                double cutValue = Double.parseDouble(formattedValue);
//                user.updateRunningSpeed(cutValue);
//            }
//        }
//        return String.valueOf(newMovingSpeed);
//    }
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
