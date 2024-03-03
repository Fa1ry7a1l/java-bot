package edu.java.exceptions;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.HttpStatus;
import java.net.URI;
import static org.junit.jupiter.api.Assertions.*;

public class ReaddingLinkExceptionTest {
    @Test
    public void test() {

        URI uri = URI.create("https://vk.com");
        Long id = 1L;

        var exception = new ReaddingLinkException(id, uri);

        Assertions.assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        Assertions.assertEquals(
            "Вы повторно пытаетесь добавить ссылку %s".formatted(uri.toString()),
            exception.getDescription()
        );
        Assertions.assertEquals(
            "чат %d пытается повторно добавить ссылку %s".formatted(id, uri.toString()),
            exception.getReason()
        );
    }
}
