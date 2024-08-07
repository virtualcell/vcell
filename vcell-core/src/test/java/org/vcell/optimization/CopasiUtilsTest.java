package org.vcell.optimization;

import cbit.vcell.xml.XmlParseException;
import com.google.common.io.Files;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.vcell.optimization.jtd.OptProgressItem;
import org.vcell.optimization.jtd.OptProgressReport;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("Fast")
public class CopasiUtilsTest {

    @Test
    public void test_parse_report_csv() throws IOException, XmlParseException {
        // header is ["Kf", "Kr", "s0_init_uM"]
        String csv_text = getTextContentFromResource("report.txt");
        File tempFile = File.createTempFile("report","csv");
        Files.write(csv_text,tempFile,StandardCharsets.UTF_8);
        OptProgressReport progressReport = CopasiUtils.readProgressReportFromCSV(tempFile);
        tempFile.delete();

        // compare first row
        OptProgressItem firstItem = progressReport.getProgressItems().get(0);
        assertEquals(20, firstItem.getNumFunctionEvaluations().intValue());
        assertEquals(0.559754,firstItem.getObjFuncValue().doubleValue(), 0);

        // compare last row
        OptProgressItem lastItem = progressReport.getProgressItems().get(progressReport.getProgressItems().size()-1);
        assertEquals(2880, lastItem.getNumFunctionEvaluations().intValue());
        assertEquals(2.86559e-14,lastItem.getObjFuncValue().doubleValue(), 0);
        assertEquals(0.8125, progressReport.getBestParamValues().get("Kf").doubleValue(), 0);
        assertEquals(0.6875, progressReport.getBestParamValues().get("Kr").doubleValue(), 0);
        assertEquals(8.95578e-07, progressReport.getBestParamValues().get("s0_init_uM").doubleValue(), 0);
    }

    private static String getTextContentFromResource(String fileName) throws IOException, XmlParseException {
        try (InputStream inputStream = CopasiUtils.class.getResourceAsStream(fileName)) {
            if (inputStream == null) {
                throw new FileNotFoundException("file not found! " + fileName);
            } else {
                String string_content = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
                return string_content;
            }
        }
    }

}
