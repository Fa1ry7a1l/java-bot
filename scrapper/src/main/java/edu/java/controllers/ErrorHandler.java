package edu.java.controllers;

import edu.java.dtos.ApiErrorResponse;
import edu.java.exceptions.ScrapperApiException;
import java.util.Arrays;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> httpMessageNotReadableException(HttpMessageNotReadableException exception) {
        return ResponseEntity.badRequest()
            .body(new ApiErrorResponse(
                "Некорректное тело запроса",
                "400",
                exception.getClass().getSimpleName(),
                exception.getMessage(),
                Arrays.stream(exception.getStackTrace()).map(StackTraceElement::toString).toList()
            ));
    }

    @ExceptionHandler(ScrapperApiException.class)
    public ResponseEntity<ApiErrorResponse> userNotExistException(ScrapperApiException exception) {
        return ResponseEntity.badRequest()
            .body(new ApiErrorResponse(
                exception.getDescription(),
                exception.getStatusCode().toString(),
                exception.getClass().getSimpleName(),
                exception.getMessage(),
                Arrays.stream(exception.getStackTrace()).map(StackTraceElement::toString).toList()
            ));
    }
}
