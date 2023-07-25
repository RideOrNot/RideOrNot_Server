package com.example.hanium2023.service;

import com.example.hanium2023.domain.dto.user.UserAutoFeedBackResponse;
import com.example.hanium2023.domain.dto.user.UserDto;
import com.example.hanium2023.domain.dto.user.UserAutoFeedbackRequest;
import com.example.hanium2023.domain.entity.BoardingHistory;
import com.example.hanium2023.domain.entity.StationExit;
import com.example.hanium2023.domain.entity.User;
import com.example.hanium2023.repository.BoardingHistoryRepository;
import com.example.hanium2023.repository.StationExitRepository;
import com.example.hanium2023.repository.UserRepository;
import com.example.hanium2023.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DecimalFormat;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final BoardingHistoryRepository boardingHistoryRepository;
    private final StationExitRepository stationExitRepository;
    private final RedisUtil redisUtil;

    @Transactional
    public UserAutoFeedBackResponse doAutoFeedback(UserAutoFeedbackRequest userAutoFeedbackRequest) {
        User user = userRepository.findById(1L).get();
        UserDto userDto = new UserDto(user);
        updateUserMovingSpeed(userAutoFeedbackRequest, user, userDto);
        updateDistance(userAutoFeedbackRequest);
        Long boardingHistoryId = saveBoardingHistory(userAutoFeedbackRequest, user);
        return new UserAutoFeedBackResponse(boardingHistoryId);
    }

    public void updateDistance(UserAutoFeedbackRequest userAutoFeedbackRequest) {
        Integer stationId = redisUtil.getStationIdByStationNameAndLineId(userAutoFeedbackRequest.getStationName(), userAutoFeedbackRequest.getLineId());
        Double existingDistance = redisUtil.getDistanceByStationIdAndExitName(stationId, userAutoFeedbackRequest.getExitName());
        Double deviation = 0.05 * (existingDistance - userAutoFeedbackRequest.getMovingSpeed() * userAutoFeedbackRequest.getMovingTime());
        Double newDistance = userAutoFeedbackRequest.isBoarded() ? (existingDistance - deviation) : (existingDistance + deviation);
        redisUtil.putDistance(stationId, userAutoFeedbackRequest.getExitName(), newDistance);
    }

    private void updateUserMovingSpeed(UserAutoFeedbackRequest userAutoFeedbackRequest, User user, UserDto userDto) {
        double alpha = 0.2;
        double beta = 0.35;

        double newMovingSpeed;

        if (1 <= userAutoFeedbackRequest.getMovingSpeedStep() && userAutoFeedbackRequest.getMovingSpeedStep() <= 3) {
            if (userAutoFeedbackRequest.isBoarded()) {
                newMovingSpeed = calculateNewMovingSpeed(userDto.getWalkingSpeed(), userDto.getInitialWalkingSpeed(), alpha, 0.8);
            } else {
                newMovingSpeed = calculateNewMovingSpeed(userDto.getWalkingSpeed(), userDto.getInitialWalkingSpeed(), beta, 1.2);
            }
            user.updateWalkingSpeed(newMovingSpeed);
        } else {
            if (userAutoFeedbackRequest.isBoarded()) {
                newMovingSpeed = calculateNewMovingSpeed(userDto.getRunningSpeed(), userDto.getInitialRunningSpeed(), alpha, 0.8);
            } else {
                newMovingSpeed = calculateNewMovingSpeed(userDto.getRunningSpeed(), userDto.getInitialRunningSpeed(), beta, 1.2);
            }
            user.updateRunningSpeed(newMovingSpeed);
        }
    }

    private Long saveBoardingHistory(UserAutoFeedbackRequest userAutoFeedbackRequest, User user) {
        Integer stationId = redisUtil.getStationIdByStationNameAndLineId(userAutoFeedbackRequest.getStationName(), userAutoFeedbackRequest.getLineId());
        StationExit stationExit = stationExitRepository.findByExitNameAndStation_StationId(userAutoFeedbackRequest.getExitName(), stationId);
        BoardingHistory savedBoardingHistory = boardingHistoryRepository.save(userAutoFeedbackRequest.toEntity(stationExit, user));
        return savedBoardingHistory.getBoardingHistoryId();
    }

    private double calculateNewMovingSpeed(double currentSpeed, double initialSpeed, double weight, double multiplier) {
        double newMovingSpeed = (1 - weight) * currentSpeed + weight * initialSpeed * multiplier;
        DecimalFormat decimalFormat = new DecimalFormat("#.#####");
        String formattedValue = decimalFormat.format(newMovingSpeed);
        return Double.parseDouble(formattedValue);
    }
}
