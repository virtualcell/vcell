package cbit.vcell.resource;

import com.google.gson.Gson;
import org.junit.jupiter.api.Tag;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.vcell.test.Fast;

import java.util.ArrayList;

@Category(Fast.class)
@RunWith(Parameterized.class)
@Tag("Fast")
public class ErrorUtilsTest {

    private ErrorUtils.ErrorReport errorReport;
    private String expectedJson;

    public ErrorUtilsTest(String expectedJson, ErrorUtils.ErrorReport errorReport){
        this.expectedJson = expectedJson;
        this.errorReport = errorReport;
    }

    @Parameterized.Parameters
    public static ArrayList<Object[]> testCases() {
        ArrayList<Object[]> testCases = new ArrayList<>();
        testCases.add(new Object[] {"{\"username\":\"bob\",\"message\":\"msg\",\"exceptionMessage\":\"exceptionMsg\",\"softwareVersion\":\"7.5\",\"platform\":\"no platform\"}",
                new ErrorUtils.ErrorReport("bob","msg","exceptionMsg",null,"7.5","no platform")});
        testCases.add(new Object[] {"{\"message\":\"msg\",\"softwareVersion\":\"7.5\",\"platform\":\"no platform\"}",
                new ErrorUtils.ErrorReport(null,"msg",null,null,"7.5","no platform")});
        testCases.add(new Object[] {"{\"message\":\"msg\"}",
                new ErrorUtils.ErrorReport(null,"msg",null,null,null,null)});
        return testCases;
    }


    @Test
    public void test_error_report_serialization(){
        Gson gson = new Gson();
        String json = gson.toJson(errorReport);
        Assert.assertEquals(expectedJson, json);

        ErrorUtils.ErrorReport errorReport1 = gson.fromJson(json, ErrorUtils.ErrorReport.class);
        Assert.assertEquals(expectedJson, gson.toJson(errorReport1));
    }
}
