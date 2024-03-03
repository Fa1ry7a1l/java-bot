package edu.java.exceptions;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.HttpStatus;
import static org.junit.jupiter.api.Assertions.*;

public class ScrapperApiExceptionTest {

    @Test
    public void constructorAndGetterTest()
    {
        String descriprion = "descriprion";
        String reason = "reason";
        var exception = new ScrapperApiException(HttpStatus.NOT_FOUND, descriprion, reason);

        Assertions.assertEquals(descriprion,exception.getDescription());
        Assertions.assertEquals(HttpStatus.NOT_FOUND,exception.getStatusCode());
        Assertions.assertEquals(reason,exception.getReason());
    }
}
