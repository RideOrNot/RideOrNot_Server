package com.example.hanium2023.domain.dto.ressponse;

import com.example.hanium2023.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ErrorResponse {
    private ErrorCode errorCode;
    private String message;

}
