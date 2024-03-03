package edu.java.exceptions;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.HttpStatus;
import java.net.URI;
import static org.junit.jupiter.api.Assertions.*;

public class TelegramChatAlreadyRegisteredExceptionTest {
    @Test
    public void test() {

        Long id = 1L;

        var exception = new TelegramChatAlreadyRegisteredException(id);

        Assertions.assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        Assertions.assertEquals(
            "Вы пытаетесь повторно зарегистрироваться в боте",
            exception.getDescription()
        );
        Assertions.assertEquals(
            "Повторная попытка зарегистрировать чат %d".formatted(id),
            exception.getReason()
        );
    }
}
