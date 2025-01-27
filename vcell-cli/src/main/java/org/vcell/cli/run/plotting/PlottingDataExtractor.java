package org.vcell.cli.run.plotting;

import cbit.vcell.resource.OperatingSystemInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jlibsedml.*;
import org.vcell.cli.run.results.NonSpatialValueHolder;
import org.vcell.cli.run.results.NonSpatialResultsConverter;
import org.vcell.cli.run.results.SpatialResultsConverter;

import java.util.*;

public class PlottingDataExtractor {
    private final SedML sedml;

    private final static Logger logger = LogManager.getLogger(PlottingDataExtractor.class);

    /**
     * Constructor to initialize the factory for a given set of simulation and model data.
     *
     * @param sedml the sedml object to get outputs, datasets, and data generators from.
     */
    public PlottingDataExtractor(SedML sedml){
        this.sedml = sedml;
    }

    /**
     *
     * @param organizedNonSpatialResults the non-spatial results set of a sedml execution
     * @return a wrapper for hdf5 relevant data
     * @see NonSpatialResultsConverter ::convertNonspatialResultsToSedmlFormat
     * @see SpatialResultsConverter ::collectSpatialDatasets
     */
    public Map<Results2DLinePlot, String> extractPlotRelevantData(Map<DataGenerator, NonSpatialValueHolder> organizedNonSpatialResults) {
        Map<Results2DLinePlot, String> plots = new LinkedHashMap<>();
        Set<String> xAxisNames = new LinkedHashSet<>();
        if (organizedNonSpatialResults.isEmpty()) return plots;

        for (Plot2D requestedPlot : this.sedml.getOutputs().stream().filter(Plot2D.class::isInstance).map(Plot2D.class::cast).toList()){
            Results2DLinePlot plot = new Results2DLinePlot();
            plot.setTitle(requestedPlot.getName());

            for (Curve curve : requestedPlot.getListOfCurves()){
                NonSpatialValueHolder xResults, yResults;
                DataGenerator requestedXGenerator = this.sedml.getDataGeneratorWithId(curve.getXDataReference());
                DataGenerator requestedYGenerator = this.sedml.getDataGeneratorWithId(curve.getYDataReference());
                if (requestedXGenerator == null || requestedYGenerator == null) throw new RuntimeException("Unexpected null returns");
                if (null == (xResults = organizedNonSpatialResults.get(requestedXGenerator))) throw new RuntimeException("Unexpected lack of x-axis results!");
                if (null == (yResults = organizedNonSpatialResults.get(requestedYGenerator))) throw new RuntimeException("Unexpected lack of y-axis results!");

                // There's two cases: 1 x-axis, n y-axes; or n x-axes, n y-axes.
                final boolean hasSingleXSeries = xResults.listOfResultSets.size() == 1;
                final boolean hasSingleYSeries = yResults.listOfResultSets.size() == 1;
                boolean hasPairsOfSeries = xResults.listOfResultSets.size() == yResults.listOfResultSets.size();
                if (!hasSingleXSeries && !hasPairsOfSeries){
                    throw new RuntimeException("Unexpected mismatch between number of x data sets, and y data sets!");
                }

                boolean hasBadXName = requestedXGenerator.getName() == null || "".equals(requestedXGenerator.getName());
                boolean hasBadYName = requestedYGenerator.getName() == null || "".equals(requestedYGenerator.getName());
                String xLabel = hasBadXName ? requestedXGenerator.getId() : requestedXGenerator.getName();
                String yLabel = hasBadYName ? requestedYGenerator.getId() : requestedYGenerator.getName();
                xAxisNames.add(xLabel);

                for (int i = 0; i < yResults.listOfResultSets.size(); i++){
                    double[] xDataArray = xResults.listOfResultSets.get(hasSingleXSeries ? 0 : i);
                    double[] yDataArray = yResults.listOfResultSets.get(i);
                    List<Double> xData = Arrays.stream(xDataArray).boxed().toList();
                    List<Double> yData = Arrays.stream(yDataArray).boxed().toList();

                    String xSeriesLabel = xLabel + (hasSingleXSeries ? "" : " (" + i + ")");
                    String ySeriesLabel = yLabel + (hasSingleYSeries ? "" : " (" + i + ")");
                    SingleAxisSeries xSeries = new SingleAxisSeries(xSeriesLabel, xData);
                    SingleAxisSeries ySeries = new SingleAxisSeries(ySeriesLabel, yData);
                    plot.addXYData(xSeries, ySeries);
                }
            }

            plot.setXAxisTitle(String.join("/", xAxisNames));
            String plotFileName = getValidPlotName(requestedPlot);
            plots.put(plot, plotFileName);
        }

        return plots;
    }

    private static String getValidPlotName(Plot2D requestedPlot) {
        String illegalChars = OperatingSystemInfo.getInstance().isWindows() ? "<>:\"/\\|?*" : "/";
        String illegalRegex = ".*[" + illegalChars + "].*"; // Need to make sure the file can be made in the OS
        String requestedPlotName = requestedPlot.getName();
        boolean hasBadPlotName = requestedPlotName == null || requestedPlotName.isBlank() || requestedPlotName.matches(illegalRegex);
        return hasBadPlotName ? requestedPlot.getId() : requestedPlot.getName();
    }
}
