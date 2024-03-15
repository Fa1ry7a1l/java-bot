package edu.java.exceptions;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.HttpStatus;
import static org.junit.jupiter.api.Assertions.*;

public class UserNotFoundExceptionTest {
    @Test
    public void test() {

        Long id = 1L;

        var exception = new UserNotFoundException(id);

        Assertions.assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        Assertions.assertEquals(
            "Вы пытаетесь обратиться к пользователю, которого бот не знает",
            exception.getDescription()
        );
        Assertions.assertEquals(
            "Пользователь с id %d не найден".formatted(id),
            exception.getReason()
        );
    }
}
