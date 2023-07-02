package com.example.hanium2023.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum MovingMessageEnum {
    WALK_SLOWLY(0,"천천히 걸으세요!"),
    WALK(1,"걸으세요!"),
    WALK_FAST(2,"빠르게 걸으세요!"),
    RUN_SLOWLY(3,"가볍게 뛰세요!"),
    RUN(4,"뛰세요!"),
    RUN_FAST(5,"전속력으로 뛰세요!"),
    CANNOT_BOARD(6,"다음 열차에 탑승하세요!");
    private final int movingSpeedStep;
    private final String message;
}
