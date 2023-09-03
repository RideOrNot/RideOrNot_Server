package com.example.hanium2023.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum DirectionCodeEnum {
    UP_LINE(0, "상행"),
    DOWN_LINE(1, "하행"),
    INNER_CIRCLE_LINE(0,"내선"),
    OUTER_CIRCLE_LINE(0,"외선");

    private final int code;
    private final String direction;

    public static DirectionCodeEnum getEnumByCode(int code) {
        for (DirectionCodeEnum value : DirectionCodeEnum.values()) {
            if (value.getCode() == code) {
                return value;
            }
        }
        return null;
    }
}
