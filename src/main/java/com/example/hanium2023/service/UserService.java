package com.example.hanium2023.service;

import com.example.hanium2023.domain.dto.user.UserAutoFeedBackResponse;
import com.example.hanium2023.domain.dto.user.UserAutoFeedbackRequest;
import com.example.hanium2023.domain.entity.BoardingHistory;
import com.example.hanium2023.domain.entity.StationExit;
import com.example.hanium2023.domain.entity.User;
import com.example.hanium2023.repository.BoardingHistoryRepository;
import com.example.hanium2023.repository.StationExitRepository;
import com.example.hanium2023.repository.UserRepository;
import com.example.hanium2023.util.RedisUtil;
import lombok.RequiredArgsConstructor;
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
        updateUserMovingSpeed(userAutoFeedbackRequest, user);
        updateDistance(userAutoFeedbackRequest, user);
        Long boardingHistoryId = saveBoardingHistory(userAutoFeedbackRequest, user);
        return new UserAutoFeedBackResponse(boardingHistoryId);
    }

    private void updateDistance(UserAutoFeedbackRequest userAutoFeedbackRequest, User user) {
        Integer stationId = redisUtil.getStationIdByStationNameAndLineId(userAutoFeedbackRequest.getStationName(), userAutoFeedbackRequest.getLineId());
        Double currentDistance = redisUtil.getDistanceByStationIdAndExitName(stationId, userAutoFeedbackRequest.getExitName());
        Double deviation = getDeviationPercentageByUser(user) * 0.05 * Math.abs(currentDistance - getUserMovingDistance(userAutoFeedbackRequest));
        Double newDistance = userAutoFeedbackRequest.isBoarded() ? (currentDistance + deviation) : (currentDistance - deviation);
        redisUtil.putDistance(stationId, userAutoFeedbackRequest.getExitName(), newDistance);
    }

    private void updateUserMovingSpeed(UserAutoFeedbackRequest userAutoFeedbackRequest, User user) {
        boolean isWalking = isWalking(userAutoFeedbackRequest);
        double weight = isWalking ? 0.2 : 0.35;
        double multiplier = userAutoFeedbackRequest.isBoarded() ? 0.8 : 1.2;
        double newMovingSpeed = calculateNewMovingSpeed(user, weight, multiplier, isWalking);

        if(isWalking)
            user.updateWalkingSpeed(newMovingSpeed);
        else
            user.updateRunningSpeed(newMovingSpeed);
    }

    private boolean isWalking(UserAutoFeedbackRequest userAutoFeedbackRequest) {
        return 1 <= userAutoFeedbackRequest.getMovingSpeedStep() && userAutoFeedbackRequest.getMovingSpeedStep() <= 3;
    }

    private Long saveBoardingHistory(UserAutoFeedbackRequest userAutoFeedbackRequest, User user) {
        Integer stationId = redisUtil.getStationIdByStationNameAndLineId(userAutoFeedbackRequest.getStationName(), userAutoFeedbackRequest.getLineId());
        StationExit stationExit = stationExitRepository.findByExitNameAndStation_StationId(userAutoFeedbackRequest.getExitName(), stationId);
        BoardingHistory savedBoardingHistory = boardingHistoryRepository.save(userAutoFeedbackRequest.toEntity(stationExit, user));
        return savedBoardingHistory.getBoardingHistoryId();
    }

    private double calculateNewMovingSpeed(User user, double weight, double multiplier, boolean isWalking) {
        double currentSpeed = isWalking ? user.getWalkingSpeed() : user.getRunningSpeed();
        double initialSpeed = isWalking ? user.getInitialWalkingSpeed() : user.getInitialRunningSpeed();
        return Double.parseDouble(String.format("%.5f", (1 - weight) * currentSpeed + weight * initialSpeed * multiplier));
    }

    private double getDeviationPercentageByUser(User user) {
        long boardingHistoryCount = boardingHistoryRepository.countByUser(user);
        if (boardingHistoryCount >= 100) {
            return 1;
        } else {
            return (double) boardingHistoryCount / 100;
        }
    }

    private double getUserMovingDistance(UserAutoFeedbackRequest userAutoFeedbackRequest) {
        return userAutoFeedbackRequest.getMovingSpeed() * userAutoFeedbackRequest.getMovingTime();
    }
}
