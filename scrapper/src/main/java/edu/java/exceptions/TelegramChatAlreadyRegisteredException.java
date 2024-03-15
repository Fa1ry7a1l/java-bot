package edu.java.exceptions;

import org.springframework.http.HttpStatus;

public class TelegramChatAlreadyRegisteredException extends ScrapperApiException {

    public TelegramChatAlreadyRegisteredException(Long chatId) {
        super(
            HttpStatus.CONFLICT,
            "Вы пытаетесь повторно зарегистрироваться в боте",
            "Повторная попытка зарегистрировать чат %d".formatted(chatId)
        );
    }
}
