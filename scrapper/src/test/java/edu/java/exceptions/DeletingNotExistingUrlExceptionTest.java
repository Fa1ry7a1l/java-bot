package edu.java.exceptions;

import java.net.URI;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.HttpStatus;

public class DeletingNotExistingUrlExceptionTest {


    @Test
    public void test()
    {

        URI uri = URI.create("https://vk.com");
        Long id = 1L;


        var exception = new DeletingNotExistingUrlException(id,uri);

        Assertions.assertEquals(HttpStatus.NOT_FOUND,exception.getStatusCode());
        Assertions.assertEquals("попытка удалить ссылку, которой нет",exception.getDescription());
        Assertions.assertEquals("чат 1 пытается удалить ссылку https://vk.com",exception.getReason());
    }


}
