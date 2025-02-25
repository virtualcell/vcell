package org.vcell.cli.run.plotting;

import cbit.vcell.resource.OperatingSystemInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jlibsedml.*;
import org.vcell.cli.run.results.NonSpatialResultsConverter;
import org.vcell.cli.run.results.ValueHolder;
import org.vcell.sbml.vcell.SBMLDataRecord;
import org.vcell.sbml.vcell.lazy.LazySBMLDataAccessor;
import org.vcell.sbml.vcell.lazy.LazySBMLNonSpatialDataAccessor;
import org.vcell.sedml.log.BiosimulationLog;
import org.vcell.util.Pair;

import java.util.*;

public class PlottingDataExtractor {
    private final SedML sedml;
    private final String sedmlName;

    private final static Logger logger = LogManager.getLogger(PlottingDataExtractor.class);

    /**
     * Constructor to initialize the factory for a given set of simulation and model data.
     *
     * @param sedml the sedml object to get outputs, datasets, and data generators from.
     */
    public PlottingDataExtractor(SedML sedml, String sedmlName){
        this.sedml = sedml;
        this.sedmlName = sedmlName;
    }

    /**
     *
     * @param organizedNonSpatialResults the non-spatial results set of a sedml execution
     * @return a mapping from Results to plot to a pair of strings: a valid name to apply to an exported plot file, and the sedml id of the plot.
     * @see NonSpatialResultsConverter ::convertNonspatialResultsToSedmlFormat
     * @see org.vcell.cli.run.results.SpatialResultsConverter ::collectSpatialDatasets
     */
    public Map<Results2DLinePlot, Pair<String, String>> extractPlotRelevantData(Map<DataGenerator, ValueHolder<LazySBMLNonSpatialDataAccessor>> organizedNonSpatialResults) {
        Map<Results2DLinePlot, Pair<String, String>> plots = new LinkedHashMap<>();
        Set<String> xAxisNames = new LinkedHashSet<>();
        if (organizedNonSpatialResults.isEmpty()) return plots;

        for (Plot2D requestedPlot : this.sedml.getOutputs().stream().filter(Plot2D.class::isInstance).map(Plot2D.class::cast).toList()){
            BiosimulationLog.instance().updatePlotStatusYml(this.sedmlName, requestedPlot.getId(), BiosimulationLog.Status.RUNNING);
            Results2DLinePlot plot = new Results2DLinePlot();
            plot.setTitle(requestedPlot.getName());

            for (Curve curve : requestedPlot.getListOfCurves()){
                ValueHolder<LazySBMLNonSpatialDataAccessor> xResults, yResults;
                BiosimulationLog.instance().updateCurveStatusYml(this.sedmlName, requestedPlot.getId(), curve.getId(), BiosimulationLog.Status.RUNNING);
                DataGenerator requestedXGenerator = this.sedml.getDataGeneratorWithId(curve.getXDataReference());
                DataGenerator requestedYGenerator = this.sedml.getDataGeneratorWithId(curve.getYDataReference());
                if (requestedXGenerator == null || requestedYGenerator == null)
                    throw this.logBeforeThrowing(new RuntimeException("Unexpected null returns"), requestedPlot.getId(), curve.getId());
                if (null == (xResults = PlottingDataExtractor.simplifyRedundantSets(organizedNonSpatialResults.get(requestedXGenerator))))
                    throw this.logBeforeThrowing(new RuntimeException("Unexpected lack of x-axis results!"), requestedPlot.getId(), curve.getId());
                if (null == (yResults = organizedNonSpatialResults.get(requestedYGenerator)))
                    throw this.logBeforeThrowing(new RuntimeException("Unexpected lack of y-axis results!"), requestedPlot.getId(), curve.getId());

                // There's two cases: 1 x-axis, n y-axes; or n x-axes, n y-axes.
                final boolean hasSingleXSeries = xResults.listOfResultSets.size() == 1;
                final boolean hasSingleYSeries = yResults.listOfResultSets.size() == 1;
                boolean hasPairsOfSeries = xResults.listOfResultSets.size() == yResults.listOfResultSets.size();
                if (!hasSingleXSeries && !hasPairsOfSeries){
                    RuntimeException exception = new RuntimeException("Unexpected mismatch between number of x data sets, and y data sets!");
                    throw this.logBeforeThrowing(exception, requestedPlot.getId(), curve.getId());
                }

                boolean hasBadXName = requestedXGenerator.getName() == null || "".equals(requestedXGenerator.getName());
                boolean hasBadYName = requestedYGenerator.getName() == null || "".equals(requestedYGenerator.getName());
                String xLabel = hasBadXName ? requestedXGenerator.getId() : requestedXGenerator.getName();
                String yLabel = hasBadYName ? requestedYGenerator.getId() : requestedYGenerator.getName();
                xAxisNames.add(xLabel);

                for (int i = 0; i < yResults.listOfResultSets.size(); i++){
                    SBMLDataRecord xLazyResults, yLazyResults;
                    LazySBMLDataAccessor xAccessor = xResults.listOfResultSets.get(hasSingleXSeries ? 0 : i);
                    LazySBMLDataAccessor yAccessor = yResults.listOfResultSets.get(i);
                    try {
                        xLazyResults = xAccessor.getData();
                        yLazyResults = yAccessor.getData();
                    } catch (Exception e) {
                        throw new ChartCouldNotBeProducedException("Fetching lazy curve data failed.", e);
                    }
                    double[] xDataArray = xLazyResults.data();
                    double[] yDataArray = yLazyResults.data();
                    List<Double> xData = Arrays.stream(xDataArray).boxed().toList();
                    List<Double> yData = Arrays.stream(yDataArray).boxed().toList();

                    String xSeriesLabel = xLabel + (hasSingleXSeries ? "" : " (" + i + ")");
                    String ySeriesLabel = yLabel + (hasSingleYSeries ? "" : " (" + i + ")");
                    SingleAxisSeries xSeries = new SingleAxisSeries(xSeriesLabel, xData);
                    SingleAxisSeries ySeries = new SingleAxisSeries(ySeriesLabel, yData);
                    plot.addXYData(xSeries, ySeries);
                }
                BiosimulationLog.instance().updateCurveStatusYml(this.sedmlName, requestedPlot.getId(), curve.getId(), BiosimulationLog.Status.SUCCEEDED);
            }

            plot.setXAxisTitle(String.join("/", xAxisNames));
            String plotFileName = getValidPlotName(requestedPlot);
            plots.put(plot, new Pair<>(plotFileName, requestedPlot.getId()));
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

    private RuntimeException logBeforeThrowing(RuntimeException e, String plotId, String curveId) {
        BiosimulationLog.instance().updateCurveStatusYml(this.sedmlName, plotId, curveId, BiosimulationLog.Status.FAILED);
        return e;
    }

    /**
     * Basically, some people make track a species that remains constant over all iterations. We want to reduce that to 1 entry
     * @return a ValueHolder with the simplified sets
     */
    private static ValueHolder<LazySBMLNonSpatialDataAccessor> simplifyRedundantSets(ValueHolder<LazySBMLNonSpatialDataAccessor> startingValues){
        if (startingValues == null) return null;
        Set<LazySBMLNonSpatialDataAccessor> setOfData = new LinkedHashSet<>(); // Need to preserve order!
        for (LazySBMLNonSpatialDataAccessor dataSet : startingValues.listOfResultSets){
            if (setOfData.contains(dataSet)) continue;
            setOfData.add(dataSet);
        }
        ValueHolder<LazySBMLNonSpatialDataAccessor> adjustedSets = startingValues.createEmptySetWithSameVCsim();
        adjustedSets.listOfResultSets.addAll(setOfData);
        return adjustedSets;
    }
}
