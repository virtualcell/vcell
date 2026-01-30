package org.jlibsedml;

import cbit.util.xml.VCLogger;
import cbit.util.xml.VCLoggerException;
import org.jdom2.JDOMException;
import org.jlibsedml.components.SId;
import org.jlibsedml.components.SedBase;
import org.jlibsedml.components.SedML;
import org.jlibsedml.components.output.Axis;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

@Tag("Fast")
public class TestComponents {


    private static SedMLDataContainer sedmlContainer;

    @BeforeAll
    public static void loadSedMLContainer() {
        try (InputStream stream = TestComponents.class.getResourceAsStream("example.sedml")){
            TestComponents.sedmlContainer = SedMLReader.readFile(stream);
        } catch (IOException | XMLException | JDOMException e) {
            Assertions.fail(e);
        }

    }

    @Test
    public void testImport() {
        SedML sedml = TestComponents.sedmlContainer.getSedML();
        SId xAxisId = new SId("x_axis_for_autogen_plot_for_task1");
        SId yAxisId = new SId("y_axis_for_autogen_plot_for_task1");
        SedBase maybeXAxis = sedml.searchInOutputsFor(xAxisId);
        if (!(maybeXAxis instanceof Axis xAxis)){
            Assertions.fail(String.format("id `%s` does not map to the expected xAxis element", xAxisId.string())); return; }
        SedBase maybeYAxis = sedml.searchInOutputsFor(yAxisId);
        Assertions.assertEquals(xAxisId, xAxis.getId());
        Assertions.assertEquals("Time", xAxis.getName());
        Assertions.assertEquals(Axis.Type.LINEAR, xAxis.getType());
        if (!(maybeYAxis instanceof Axis yAxis)){ Assertions.fail(String.format("id `%s` does not map to the expected yAxis element", yAxisId.string())); return; }
        Assertions.assertEquals(yAxisId, yAxis.getId());
        Assertions.assertEquals("Species", yAxis.getName());
        Assertions.assertEquals(Axis.Type.LINEAR, yAxis.getType());
    }

    // TODO: Complete this logger and use it for whole CLI
    private static class MockLogger extends VCLogger {
        @Override
        public void sendMessage(Priority p, ErrorType et, String message) throws VCLoggerException {

        }

        public void sendAllMessages() {}

        public boolean hasMessages() {
            return false;
        }
    }

}
