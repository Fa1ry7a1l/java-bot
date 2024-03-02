package edu.java.exceptions;

import edu.java.dtos.ApiErrorResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApiErrorResponseException extends RuntimeException {
    private final ApiErrorResponse apiErrorResponse;
}
