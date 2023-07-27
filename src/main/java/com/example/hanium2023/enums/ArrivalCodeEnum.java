package com.example.hanium2023.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ArrivalCodeEnum {
    ENTER(0),
    ARRIVE(1),
    DEPART(2),
    DEPART_BEFORE_STATION(3),
    ENTER_BEFORE_STATION(4),
    ARRIVE_BEFORE_STATION(5),
    NOT_CLOSE_STATION(99);
    private final int code;
}
