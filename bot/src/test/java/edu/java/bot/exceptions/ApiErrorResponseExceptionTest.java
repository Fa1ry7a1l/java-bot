package edu.java.bot.exceptions;

import edu.java.dtos.ApiErrorResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class ApiErrorResponseExceptionTest {

    @Test
    public void constructorAndGetterTest() {
        String description = "description";

        var exception = new ApiErrorResponseException(description);

        Assertions.assertEquals(description,exception.getDescription());
    }
}
