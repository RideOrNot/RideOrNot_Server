package com.example.hanium2023.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ArrivalCodeEnum {
    ENTER(0, "진입"),
    ARRIVE(1, "도착"),
    DEPART(2, "출발"),
    DEPART_BEFORE_STATION(3, "전역 출발"),
    ENTER_BEFORE_STATION(4, "전역 진입"),
    ARRIVE_BEFORE_STATION(5, "전역 도착"),
    NOT_CLOSE_STATION(99, "멀리 있음");
    private final int code;
    private final String status;

    public static ArrivalCodeEnum getEnumByCode(int code) {
        for (ArrivalCodeEnum value : ArrivalCodeEnum.values()) {
            if (value.getCode() == code) {
                return value;
            }
        }
        return null;
    }
}
