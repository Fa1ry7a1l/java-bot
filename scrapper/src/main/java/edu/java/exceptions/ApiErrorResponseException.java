package edu.java.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApiErrorResponseException extends RuntimeException {
    private final String description;
}
