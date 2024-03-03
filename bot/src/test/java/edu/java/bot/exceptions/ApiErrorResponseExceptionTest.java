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
        String code = "code";
        String exceptionName = "exceptionName";
        String exceptionMessage = "exceptionMessage";
        List<String> stacktrace = new ArrayList<>();

        var testException = new ApiErrorResponse(description, code, exceptionName, exceptionMessage, stacktrace);

        var exception = new ApiErrorResponseException(testException);

        Assertions.assertNotNull(exception.getApiErrorResponse());
        var responseError = exception.getApiErrorResponse();
        Assertions.assertEquals(testException,responseError);
        Assertions.assertEquals(testException.description(),responseError.description());
        Assertions.assertEquals(testException.code(),responseError.code());
        Assertions.assertEquals(testException.exceptionName(),responseError.exceptionName());
        Assertions.assertEquals(testException.exceptionMessage(),responseError.exceptionMessage());
        Assertions.assertEquals(testException.stacktrace(),responseError.stacktrace());
    }
}
