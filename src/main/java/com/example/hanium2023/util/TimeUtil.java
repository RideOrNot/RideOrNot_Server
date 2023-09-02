package com.example.hanium2023.util;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TimeUtil {
    public static Integer convertString2Secs(String timeString) {
        String[] time = timeString.split(":");
        Integer minute = Integer.parseInt(time[0]);
        Integer second = Integer.parseInt(time[1]);
        return minute * 60 + second;
    }

    public static LocalDateTime getCurrentTime() {
        return LocalDateTime.now();
    }

    public static LocalDateTime getTimeFromString(String timeString) {
        return LocalDateTime.parse(timeString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public static Duration getDuration(LocalDateTime start, LocalDateTime end) {
        return Duration.between(start, end);
    }
}
