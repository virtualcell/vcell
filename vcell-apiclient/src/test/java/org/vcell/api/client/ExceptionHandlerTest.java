package org.vcell.api.client;

import cbit.vcell.message.CustomObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.model.VCellHTTPError;
import org.vcell.util.DataAccessException;
import org.vcell.util.ObjectNotFoundException;
import org.vcell.util.PermissionException;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

@Tag("Fast")
public class ExceptionHandlerTest {
    private record TestCase(
           Class serverClass,
           int errorCode,
           String message
    ){ }

    private final ArrayList<Class> classesThatHaveDirectMapping = new ArrayList<>(){{
        add(DataAccessException.class);
        add(ObjectNotFoundException.class);
    }};

    @Test
    public void testApiExceptionErrorReturnsProperExceptions() throws IOException {
        TestCase[] testCases = {new TestCase(DataAccessException.class, 500, "Test Data Access"),
        new TestCase(ObjectNotFoundException.class, 404, "Object not found"),
        new TestCase(PermissionException.class, 403, "Not allowed"),
        new TestCase(SQLException.class, 500, "Unknown error")};

        CustomObjectMapper customObjectMapper = new CustomObjectMapper();

        // Exceptions that are sent as VCellHTTPError
        for (TestCase testCase: testCases){
            VCellHTTPError error = new VCellHTTPError();
            error.setExceptionType(testCase.serverClass.getSimpleName());
            error.setMessage(testCase.message);
            ApiException apiException = new ApiException(testCase.errorCode, testCase.message, null, customObjectMapper.writeValueAsString(error));

            Exception exception = ExceptionHandler.getProperException(apiException);
            if (classesThatHaveDirectMapping.contains(testCase.serverClass)){
                Assertions.assertEquals(exception.getClass(), testCase.serverClass);
            } else {
                Assertions.assertEquals(exception.getClass(), RuntimeException.class);
            }

            Assertions.assertEquals(testCase.message, exception.getMessage());
        }

        // Fringe error case
        String bodyReturned = "Validation error caught by the server.";
        ApiException apiException = new ApiException(402, null, null, bodyReturned);
        Exception exception = ExceptionHandler.getProperException(apiException);
        Assertions.assertEquals(bodyReturned, exception.getMessage());

        // Client Side Error
        String clientError = "Client error";
        apiException = new ApiException(400, clientError, null, null);
        exception = ExceptionHandler.getProperException(apiException);
        Assertions.assertEquals(clientError, exception.getMessage());
    }
}
