package cbit.vcell.resource;

import com.google.gson.Gson;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("Fast")
public class ErrorUtilsTest {

    public record TestCase(String expectedJson, ErrorUtils.ErrorReport errorReport){}

    public static ArrayList<TestCase> testCases() {
        ArrayList<TestCase> testCases = new ArrayList<>();
        testCases.add(new TestCase("{\"username\":\"bob\",\"message\":\"msg\",\"exceptionMessage\":\"exceptionMsg\",\"softwareVersion\":\"7.5\",\"platform\":\"no platform\"}",
                new ErrorUtils.ErrorReport("bob","msg","exceptionMsg",null,"7.5","no platform")));
        testCases.add(new TestCase("{\"message\":\"msg\",\"softwareVersion\":\"7.5\",\"platform\":\"no platform\"}",
                new ErrorUtils.ErrorReport(null,"msg",null,null,"7.5","no platform")));
        testCases.add(new TestCase("{\"message\":\"msg\"}",
                new ErrorUtils.ErrorReport(null,"msg",null,null,null,null)));
        return testCases;
    }


    @ParameterizedTest
    @MethodSource("testCases")
    public void test_error_report_serialization(TestCase testCase){
        Gson gson = new Gson();
        String json = gson.toJson(testCase.errorReport);
        assertEquals(testCase.expectedJson, json);

        ErrorUtils.ErrorReport errorReport1 = gson.fromJson(json, ErrorUtils.ErrorReport.class);
        assertEquals(testCase.expectedJson, gson.toJson(errorReport1));
    }
}
