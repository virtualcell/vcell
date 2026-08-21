package cbit.vcell.resource;

import com.google.gson.Gson;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

    @Test
    @Tag("Fast")
    public void emailTextCarriesEveryFieldUnderAHeading() {
        ErrorUtils.ErrorReport r = new ErrorUtils.ErrorReport(
                "bob", "it crashed", "boom", "java.lang.NullPointerException\n\tat Foo.bar(Foo.java:1)",
                "Rel_Version_8.0.0_build_03", "Java 17 on Windows");
        String text = r.toEmailText();

        assertTrue(text.startsWith("VCell error report"), text);
        assertTrue(text.contains("User:      bob"), text);
        assertTrue(text.contains("Version:   Rel_Version_8.0.0_build_03"), text);
        assertTrue(text.contains("Platform:  Java 17 on Windows"), text);
        assertTrue(text.contains("--- User message"), text);
        assertTrue(text.contains("it crashed"), text);
        assertTrue(text.contains("--- Exception chain and stack trace"), text);
        assertTrue(text.contains("\tat Foo.bar(Foo.java:1)"), text);
        assertTrue(text.contains("boom"), text);
    }

    @Test
    @Tag("Fast")
    public void emailTextIsNotOneLongLine() {
        // the whole point: JSON put the report on a single line with escaped newlines
        ErrorUtils.ErrorReport r = new ErrorUtils.ErrorReport(
                "bob", "msg", "line one\nline two", "at A\nat B", "8.0", "Java");
        String text = r.toEmailText();
        assertTrue(text.lines().count() > 10, "expected a multi-line body, got: " + text);
        assertFalse(text.contains("\\n"), "body still contains an escaped newline: " + text);
    }

    @Test
    @Tag("Fast")
    public void windowsNewlinesAreNormalised() {
        ErrorUtils.ErrorReport r = new ErrorUtils.ErrorReport(
                "bob", null, "first\r\nsecond", null, "8.0", "Java");
        assertFalse(r.toEmailText().contains("\r"), "CR survived normalisation");
    }

    @Test
    @Tag("Fast")
    public void missingFieldsAreLabelledRatherThanBlank() {
        ErrorUtils.ErrorReport r = new ErrorUtils.ErrorReport(null, null, null, null, null, null);
        String text = r.toEmailText();
        assertTrue(text.contains("User:      (not reported)"), text);
        assertTrue(text.contains("(none)"), text);
    }

    @Test
    @Tag("Fast")
    public void aMultiLinePlatformStaysOnItsHeaderLine() {
        ErrorUtils.ErrorReport r = new ErrorUtils.ErrorReport(
                "bob", null, null, null, "8.0", "Java 17\non Windows");
        String header = r.toEmailText().lines().filter(l -> l.startsWith("Platform:")).findFirst().orElse("");
        assertEquals("Platform:  Java 17 on Windows", header);
    }

    @Test
    @Tag("Fast")
    public void theClientLogComesLastSoAPreviewShowsTheDiagnosis() {
        // mail clients preview the first couple of thousand characters; the log can be
        // hundreds of kilobytes, so it must not be what a reader sees first.
        String hugeLog = "Log file content:\n" + "noise noise noise\n".repeat(500);
        ErrorUtils.ErrorReport r = new ErrorUtils.ErrorReport(
                "bob", "it crashed", hugeLog, "java.lang.IllegalStateException\n\tat Foo.bar(Foo.java:1)",
                "8.0", "Java");
        String text = r.toEmailText();
        assertTrue(text.indexOf("IllegalStateException") < text.indexOf("noise"),
                "the stack trace must precede the client log");
        assertTrue(text.indexOf("IllegalStateException") < 2000,
                "the stack trace must fall inside a typical preview window");
    }

    @Test
    @Tag("Fast")
    public void theClientLogIsNotCarriedTwice() {
        // Reports are raised as new RuntimeException(log), so the log arrives both as
        // exceptionMessage and as the message of the exception opening the stack trace.
        // On a real support email that duplicate was 94% of the body.
        String log = "Log file content:\nUNIQUE-LOG-SENTINEL\n" + "routine chatter\n".repeat(60);
        String trace = "java.lang.RuntimeException: \n" + log
                + "\n\tat cbit.vcell.Foo.bar(Foo.java:1)\n\tat cbit.vcell.Baz.qux(Baz.java:2)";
        ErrorUtils.ErrorReport r = new ErrorUtils.ErrorReport(null, null, log, trace, "8.0", "Java");
        String text = r.toEmailText();

        assertEquals(1, countOccurrences(text, "UNIQUE-LOG-SENTINEL"),
                "the client log should appear exactly once");
        assertTrue(text.contains("(client log -- reproduced in full below)"), text);
        assertTrue(text.contains("\tat cbit.vcell.Foo.bar(Foo.java:1)"), "frames must survive");
        assertTrue(text.indexOf("at cbit.vcell.Foo.bar") < text.indexOf("UNIQUE-LOG-SENTINEL"),
                "frames must come before the log");
    }

    @Test
    @Tag("Fast")
    public void aShortExceptionMessageIsLeftAloneInTheTrace() {
        // Only a body long enough to be the log is worth moving; a short message that
        // happens to appear in its own stack trace must not be rewritten.
        ErrorUtils.ErrorReport r = new ErrorUtils.ErrorReport(
                null, null, "boom", "java.lang.RuntimeException: boom\n\tat Foo.bar(Foo.java:1)",
                "8.0", "Java");
        String text = r.toEmailText();
        assertTrue(text.contains("java.lang.RuntimeException: boom"), text);
        assertFalse(text.contains("reproduced in full below"), text);
    }

    private static int countOccurrences(String haystack, String needle) {
        int n = 0, i = haystack.indexOf(needle);
        while (i >= 0) { n++; i = haystack.indexOf(needle, i + needle.length()); }
        return n;
    }
}
