package edu.java.exceptions;

import edu.java.dtos.ApiErrorResponse;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ApiErrorResponseExceptionTest {

    @Test
    public void constructorAndGetterTest() {
        String description = "description";

        var exception = new ApiErrorResponseException(description);

        Assertions.assertEquals(description, exception.getDescription());
    }
}
