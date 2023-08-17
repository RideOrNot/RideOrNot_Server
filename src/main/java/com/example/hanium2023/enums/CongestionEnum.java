package com.example.hanium2023.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum CongestionEnum {
    HIGH("평소보다 더 혼잡합니다."),
    NORMAL("평소와 비슷한 정도로 혼잡합니다."),
    LOW("평소보다 여유롭습니다."),
    NULL("혼잡도를 알 수 없습니다.");
    private final String message;
}
