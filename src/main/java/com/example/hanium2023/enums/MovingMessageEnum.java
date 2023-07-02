package com.example.hanium2023.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum MovingMessageEnum {
    WALK_SLOWLY(1,"천천히 걸으세요!"),
    WALK(2,"걸으세요!"),
    WALK_FAST(3,"빠르게 걸으세요!"),
    RUN_SLOWLY(4,"가볍게 뛰세요!"),
    RUN(5,"뛰세요!"),
    RUN_FAST(6,"전속력으로 뛰세요!"),
    CANNOT_BOARD(0,"다음 열차에 탑승하세요!");
    private final int movingSpeedStep;
    private final String message;
}
