package com.example.hanium2023.exception;

import com.example.hanium2023.domain.dto.ressponse.ErrorResponse;
import com.example.hanium2023.domain.dto.ressponse.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;

@RestControllerAdvice
public class ExceptionManager {
    @ExceptionHandler(AppException.class)
    public ResponseEntity<?> finalProjectAppExceptionHandler(AppException e){
        return ResponseEntity.status(e.getErrorCode().getStatus())
                .body(Response.error(new ErrorResponse(e.getErrorCode(), e.getErrorCode().getMessage())));
    }

    @ExceptionHandler(SQLException.class)
    public ResponseEntity<?> sqlExceptionHandler(SQLException e){
        ErrorResponse errorResponse = new ErrorResponse(ErrorCode.DATABASE_ERROR, e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Response.error(errorResponse));
    }


    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> runtimeExceptionHandler(RuntimeException e){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Response.error(e));
    }


}