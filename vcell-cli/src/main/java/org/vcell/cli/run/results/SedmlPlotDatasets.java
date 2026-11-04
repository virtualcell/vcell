package org.vcell.cli.run.results;

import org.jlibsedml.SedMLDataContainer;
import org.jlibsedml.components.dataGenerator.DataGenerator;
import org.jlibsedml.components.output.AbstractCurve;
import org.jlibsedml.components.output.Curve;
import org.jlibsedml.components.output.Plot2D;
import org.jlibsedml.components.output.XAxis;
import org.vcell.data.Dataset;
import org.vcell.data.Dimension;
import org.vcell.data.sedml.SedmlOutputs;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Builds one {@link Dataset} per SED-ML 2D plot (shaped as {@link SedmlOutputs} describes) from data generator datasets
 * (see {@link SedmlDataGenerators}): the curves' x and y values, padded to the plot's longest time course. For the new
 * data system this replaces {@code PlottingDataExtractor}'s data gathering; how the chart looks is the writer's business.
 */
public final class SedmlPlotDatasets {
    private SedmlPlotDatasets() { }

    /**
     * @param sedml          the executed SED-ML document
     * @param dataGenerators the document's data generators with nonspatial values; every curve needs both of its own
     * @param document       the SED-ML document's path within its archive
     */
    public static List<Dataset> fromDataGenerators(SedMLDataContainer sedml, Map<DataGenerator, Dataset> dataGenerators, String document) {
        List<Dataset> plots = new ArrayList<>();
        if (dataGenerators.isEmpty()) return plots;
        for (Plot2D plot : sedml.getSedML().getOutputs().stream().filter(Plot2D.class::isInstance).map(Plot2D.class::cast).toList())
            plots.add(SedmlPlotDatasets.plotDataset(sedml, plot, dataGenerators, document));
        return plots;
    }

    private static Dataset plotDataset(SedMLDataContainer sedml, Plot2D plot, Map<DataGenerator, Dataset> dataGenerators, String document) {
        String id = plot.getIdAsString();
        List<Curve> curves = plot.getCurves().stream().filter(Curve.class::isInstance).map(Curve.class::cast).toList();
        List<DataGenerator[]> curveData = new ArrayList<>();
        int points = 0;
        for (Curve curve : curves) {
            DataGenerator x = sedml.findDataGeneratorById(curve.getXDataReference());
            if (x == null) throw new RuntimeException("Unable to retrieve x data reference!");
            DataGenerator y = sedml.findDataGeneratorById(curve.getYDataReference());
            if (y == null) throw new RuntimeException("Unable to retrieve y data reference!");
            if (!dataGenerators.containsKey(x)) throw new RuntimeException("Unexpected lack of x-axis results! (plot `" + id + "`, curve `" + curve.getIdAsString() + "`)");
            if (!dataGenerators.containsKey(y)) throw new RuntimeException("Unexpected lack of y-axis results! (plot `" + id + "`, curve `" + curve.getIdAsString() + "`)");
            long xJobs = SedmlPlotDatasets.jobs(dataGenerators.get(x)), yJobs = SedmlPlotDatasets.jobs(dataGenerators.get(y));
            if (xJobs != 1 && xJobs != yJobs)
                throw new RuntimeException("Unexpected mismatch between number of x data sets, and y data sets! (plot `" + id + "`, curve `" + curve.getIdAsString() + "`)");
            curveData.add(new DataGenerator[]{x, y});
            points = Math.max(points, dataGenerators.get(x).dimension(Dimension.TIME).size());
            points = Math.max(points, dataGenerators.get(y).dimension(Dimension.TIME).size());
        }

        Dataset.Builder builder = SedmlOutputs.report(document, id, plot.getName(), SedmlOutputs.OutputType.PLOT_2D);
        XAxis xAxis = plot.getXAxis();
        if (xAxis != null && xAxis.getName() != null) builder.attribute(SedmlOutputs.X_AXIS_NAME, xAxis.getName());
        if (xAxis != null && xAxis.getId() != null) builder.attribute(SedmlOutputs.X_AXIS_ID, xAxis.getIdAsString());
        builder.dimension(new Dimension.Indexed(Dimension.TIME, points));

        Set<String> xVariables = new HashSet<>();
        for (int i = 0; i < curves.size(); i++) {
            Curve curve = curves.get(i);
            DataGenerator x = curveData.get(i)[0], y = curveData.get(i)[1];
            Dataset xValues = dataGenerators.get(x), yValues = dataGenerators.get(y);
            if (xVariables.add(x.getIdAsString())) {
                SedmlDataGenerators.jobDimensions(xValues).forEach(builder::dimension);
                builder.variable(SedmlOutputs.xData(x.getIdAsString(), x.getName(), SedmlDataGenerators.symbol(x), SedmlPlotDatasets.dimensionNames(xValues)),
                        SedmlDataGenerators.padded(xValues, points));
            }
            SedmlDataGenerators.jobDimensions(yValues).forEach(builder::dimension);
            builder.variable(SedmlOutputs.curve(curve.getIdAsString(), curve.getName(), x.getIdAsString(), y.getIdAsString(), y.getName(),
                    SedmlPlotDatasets.dimensionNames(yValues)), SedmlDataGenerators.padded(yValues, points));
        }
        return builder.build();
    }

    private static long jobs(Dataset dataGenerator) {
        return SedmlDataGenerators.jobDimensions(dataGenerator).stream().mapToLong(Dimension::size).reduce(1, Math::multiplyExact);
    }

    private static List<String> dimensionNames(Dataset dataGenerator) {
        return dataGenerator.dimensions().stream().map(Dimension::name).toList();
    }
}
