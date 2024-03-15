package edu.java.exceptions;

import org.springframework.http.HttpStatus;

public class UserNotFoundException extends ScrapperApiException {
    public UserNotFoundException(Long id) {
        super(
            HttpStatus.NOT_FOUND,
            "Вы пытаетесь обратиться к пользователю, которого бот не знает",
            "Пользователь с id %d не найден".formatted(id)
        );
    }
}
