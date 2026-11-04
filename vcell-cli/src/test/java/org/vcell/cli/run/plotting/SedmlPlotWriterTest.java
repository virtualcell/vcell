package org.vcell.cli.run.plotting;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.vcell.data.Dataset;
import org.vcell.data.Dimension;
import org.vcell.data.array.ArrayBackend;
import org.vcell.data.sedml.SedmlOutputs;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Tag("Fast")
public class SedmlPlotWriterTest {

    private static final ArrayBackend BACKEND = ArrayBackend.DEFAULT;

    /** Time against a curve over a 2-value scan, and an unnamed x data generator against a named curve. */
    private static Dataset plot(String xAxisName) {
        Dataset.Builder plot = SedmlOutputs.report("sim.sedml", "plot1", "My plot", SedmlOutputs.OutputType.PLOT_2D)
                .dimension(new Dimension.Numeric("k", new double[]{1, 2}))
                .dimension(new Dimension.Indexed(Dimension.TIME, 2))
                .variable(SedmlOutputs.xData("dg_t", "t", SedmlOutputs.TIME_SYMBOL, List.of(Dimension.TIME)), BACKEND.wrap(new double[]{0, 1}, 2))
                .variable(SedmlOutputs.curve("c1", null, "dg_t", "dg_a", null, List.of("k", Dimension.TIME)), BACKEND.wrap(new double[]{5, 6, 7, 8}, 2, 2))
                .variable(SedmlOutputs.xData("dg_b", null, null, List.of(Dimension.TIME)), BACKEND.wrap(new double[]{3, 4}, 2))
                .variable(SedmlOutputs.curve("c2", "B vs b", "dg_b", "dg_c", null, List.of(Dimension.TIME)), BACKEND.wrap(new double[]{9, 9}, 2));
        if (xAxisName != null) plot.attribute(SedmlOutputs.X_AXIS_NAME, xAxisName);
        return plot.build();
    }

    @Test
    public void seriesPerJobLabelledAsTheCliAlwaysHas() {
        Results2DLinePlot chart = SedmlPlotWriter.toChart(plot(null));
        assertEquals("My plot", chart.getTitle());
        assertEquals(3, chart.getNumYSeries(), "one per job of c1, and c2");
        assertEquals(List.of(7.0, 8.0), chart.getYAxisSeries("c1 (1)").data(), "a curve without a name goes by its id");
        assertNotNull(chart.getXAxisSeries("Time"), "the time symbol is labelled Time");
        assertNotNull(chart.getXAxisSeries("dg_b"), "an unnamed data generator goes by its id");
        assertEquals(List.of(9.0, 9.0), chart.getYAxisSeries("B vs b").data());
        assertEquals("Time/dg_b", chart.getXAxisTitle(), "without an x axis, its curves' x labels");
    }

    @Test
    public void xAxisNameIsTheTitle() {
        assertEquals("Seconds", SedmlPlotWriter.toChart(plot("Seconds")).getXAxisTitle());
    }
}
