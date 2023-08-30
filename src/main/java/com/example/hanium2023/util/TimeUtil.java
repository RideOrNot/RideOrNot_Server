package com.example.hanium2023.util;

public class TimeUtil {
    public static Integer convertString2Secs(String timeString) {
        String[] time = timeString.split(":");
        Integer minute = Integer.parseInt(time[0]);
        Integer second = Integer.parseInt(time[1]);
        return minute * 60 + second;
    }
}
