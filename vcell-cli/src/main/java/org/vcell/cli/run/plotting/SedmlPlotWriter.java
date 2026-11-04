package org.vcell.cli.run.plotting;

import org.vcell.data.Dataset;
import org.vcell.data.Dimension;
import org.vcell.data.Variable;
import org.vcell.data.io.DatasetWriter;
import org.vcell.data.sedml.SedmlOutputs;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Draws a SED-ML 2D plot (a dataset shaped as {@link SedmlOutputs} describes) as a PNG or PDF chart, labelled as the
 * VCell CLI has long labelled them:
 * <ul>
 *     <li>the title is the plot's name;</li>
 *     <li>a curve's series is named after the curve (its name, else its id), and its x values after their data
 *     generator ("Time" for the time symbol, else its name, else its id); with several jobs, each job is a series of
 *     its own, suffixed {@code " (<job>)"};</li>
 *     <li>the x axis title is the plot's x axis (its name, else its id), else its curves' x labels joined by "/",
 *     cut to 50 characters.</li>
 * </ul>
 * Drawing itself is {@link Results2DLinePlot}'s.
 */
public final class SedmlPlotWriter implements DatasetWriter {

    public enum Format {PNG, PDF}

    private final Format format;

    public SedmlPlotWriter(Format format) {
        this.format = format;
    }

    @Override
    public String extension() {
        return this.format == Format.PNG ? "png" : "pdf";
    }

    @Override
    public void write(Dataset plot, Path destination) {
        Results2DLinePlot chart = SedmlPlotWriter.toChart(plot);
        String fileName = destination.getFileName().toString();
        if (this.format == Format.PNG) chart.generatePng(fileName, destination.toAbsolutePath().getParent().toFile());
        else chart.generatePdf(fileName, destination.toAbsolutePath().getParent().toFile());
    }

    /** @return the chart for a plot, with every curve's data in it */
    public static Results2DLinePlot toChart(Dataset plot) {
        Results2DLinePlot chart = new Results2DLinePlot();
        chart.setTitle(plot.attributes().get(SedmlOutputs.NAME));
        int points = plot.dimension(Dimension.TIME).size();
        Set<String> xLabels = new LinkedHashSet<>();
        for (Variable curve : plot.variables(Variable.Role.DATA)) {
            Variable x = plot.variable(curve.attributes().get(SedmlOutputs.X_DATA));
            String xLabel = SedmlOutputs.TIME_SYMBOL.equals(x.attributes().get(SedmlOutputs.DATA_GENERATOR_SYMBOL)) ? "Time"
                    : x.attributes().getOrDefault(SedmlOutputs.DATA_GENERATOR_NAME, x.name());
            String yLabel = curve.attributes().getOrDefault(SedmlOutputs.NAME, curve.name());
            xLabels.add(xLabel);

            double[] xValues = plot.values(x.name()).toArray(), yValues = plot.values(curve.name()).toArray();
            int xJobs = xValues.length / points, yJobs = yValues.length / points;
            for (int job = 0; job < yJobs; job++) {
                int xJob = xJobs == 1 ? 0 : job;
                chart.addXYData(
                        new SingleAxisSeries(xLabel + (xJobs == 1 ? "" : " (" + job + ")"), SedmlPlotWriter.series(xValues, xJob, points)),
                        new SingleAxisSeries(yLabel + (yJobs == 1 ? "" : " (" + job + ")"), SedmlPlotWriter.series(yValues, job, points)));
            }
        }
        String xAxisTitle = plot.attributes().get(SedmlOutputs.X_AXIS_NAME);
        if (xAxisTitle == null) xAxisTitle = plot.attributes().get(SedmlOutputs.X_AXIS_ID);
        if (xAxisTitle == null) {
            String joined = String.join("/", xLabels);
            xAxisTitle = joined.length() > 50 ? joined.substring(0, 50) + "..." : joined;
        }
        chart.setXAxisTitle(xAxisTitle);
        return chart;
    }

    private static List<Double> series(double[] values, int job, int points) {
        return Arrays.stream(values, job * points, (job + 1) * points).boxed().toList();
    }
}
