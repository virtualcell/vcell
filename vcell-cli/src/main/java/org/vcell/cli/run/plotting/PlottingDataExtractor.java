package org.vcell.cli.run.plotting;

import cbit.vcell.resource.OperatingSystemInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jlibsedml.*;
import org.jlibsedml.components.SId;
import org.jlibsedml.components.SedBase;
import org.jlibsedml.components.SedML;
import org.jlibsedml.components.Variable;
import org.jlibsedml.components.dataGenerator.DataGenerator;
import org.jlibsedml.components.output.AbstractCurve;
import org.jlibsedml.components.output.Curve;
import org.jlibsedml.components.output.Plot;
import org.jlibsedml.components.output.Plot2D;
import org.vcell.cli.run.results.NonSpatialResultsConverter;
import org.vcell.cli.run.results.ValueHolder;
import org.vcell.sbml.vcell.SBMLDataRecord;
import org.vcell.sbml.vcell.lazy.LazySBMLDataAccessor;
import org.vcell.sbml.vcell.lazy.LazySBMLNonSpatialDataAccessor;
import org.vcell.sedml.log.BiosimulationLog;
import org.vcell.util.Pair;

import java.util.*;

public class PlottingDataExtractor {
    private final SedMLDataContainer sedml;
    private final String sedmlName;

    private final static Logger logger = LogManager.getLogger(PlottingDataExtractor.class);

    /**
     * Constructor to initialize the factory for a given set of simulation and model data.
     *
     * @param sedml the sedml object to get outputs, datasets, and data generators from.
     */
    public PlottingDataExtractor(SedMLDataContainer sedml, String sedmlName){
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
    public Map<Results2DLinePlot, Pair<String, SId>> extractPlotRelevantData(Map<DataGenerator, ValueHolder<LazySBMLNonSpatialDataAccessor>> organizedNonSpatialResults) {
        Map<Results2DLinePlot, Pair<String, SId>> plots = new LinkedHashMap<>();
        Set<String> xLabelNames = new LinkedHashSet<>();
        if (organizedNonSpatialResults.isEmpty()) return plots;

        SedML sedML = this.sedml.getSedML();
        for (Plot2D requestedPlot : sedML.getOutputs().stream().filter(Plot2D.class::isInstance).map(Plot2D.class::cast).toList()){
            BiosimulationLog.instance().updatePlotStatusYml(this.sedmlName, requestedPlot.getIdAsString(), BiosimulationLog.Status.RUNNING);
            Results2DLinePlot plot = new Results2DLinePlot();
            plot.setTitle(requestedPlot.getName());


            for (AbstractCurve abstractCurve: requestedPlot.getCurves()){
                if (!(abstractCurve instanceof Curve curve )) continue;
                ValueHolder<LazySBMLNonSpatialDataAccessor> xResults, yResults;
                BiosimulationLog.instance().updateCurveStatusYml(this.sedmlName, requestedPlot.getIdAsString(), curve.getIdAsString(), BiosimulationLog.Status.RUNNING);
                SedBase maybeXGenerator = sedML.searchInDataGeneratorsFor(curve.getXDataReference());
                if (!(maybeXGenerator instanceof DataGenerator requestedXGenerator)) throw new RuntimeException("Unable to retrieve x data reference!");
                SedBase maybeYGenerator = sedML.searchInDataGeneratorsFor(curve.getYDataReference());
                if (!(maybeYGenerator instanceof DataGenerator requestedYGenerator)) throw new RuntimeException("Unable to retrieve y data reference!");
                if (null == (xResults = PlottingDataExtractor.simplifyRedundantSets(organizedNonSpatialResults.get(requestedXGenerator))))
                    throw this.logBeforeThrowing(new RuntimeException("Unexpected lack of x-axis results!"), requestedPlot.getIdAsString(), curve.getIdAsString());
                if (null == (yResults = organizedNonSpatialResults.get(requestedYGenerator)))
                    throw this.logBeforeThrowing(new RuntimeException("Unexpected lack of y-axis results!"), requestedPlot.getIdAsString(), curve.getIdAsString());

                // There's two cases: 1 x-axis, n y-axes; or n x-axes, n y-axes.
                final boolean hasSingleXSeries = xResults.listOfResultSets.size() == 1;
                final boolean hasSingleYSeries = yResults.listOfResultSets.size() == 1;
                boolean hasPairsOfSeries = xResults.listOfResultSets.size() == yResults.listOfResultSets.size();
                if (!hasSingleXSeries && !hasPairsOfSeries){
                    RuntimeException exception = new RuntimeException("Unexpected mismatch between number of x data sets, and y data sets!");
                    throw this.logBeforeThrowing(exception, requestedPlot.getIdAsString(), curve.getIdAsString());
                }

                Pair<String, String> labelPair = this.getXYAxisLabel(curve);
                xLabelNames.add(labelPair.one);

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

                    String xSeriesLabel = labelPair.one + (hasSingleXSeries ? "" : " (" + i + ")");
                    String ySeriesLabel = labelPair.two + (hasSingleYSeries ? "" : " (" + i + ")");
                    SingleAxisSeries xSeries = new SingleAxisSeries(xSeriesLabel, xData);
                    SingleAxisSeries ySeries = new SingleAxisSeries(ySeriesLabel, yData);
                    plot.addXYData(xSeries, ySeries);
                }
                BiosimulationLog.instance().updateCurveStatusYml(this.sedmlName, requestedPlot.getIdAsString(), curve.getIdAsString(), BiosimulationLog.Status.SUCCEEDED);
            }

            plot.setXAxisTitle(PlottingDataExtractor.getXAxisName(xLabelNames, requestedPlot));
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
        return hasBadPlotName ? requestedPlot.getIdAsString() : requestedPlot.getName();
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

    private static String getXAxisName(Set<String> xLabelNames, Plot plot){
        if (plot.getXAxis() != null){
            if (plot.getXAxis().getName() != null) return plot.getXAxis().getName();
            if (plot.getXAxis().getId() != null) return plot.getXAxis().getId().string();
        }
        String allXNames = String.join("/", xLabelNames);
        return (allXNames.length() > 50) ? allXNames.substring(0, 50) + "..." : allXNames;
    }

    private Pair<String, String> getXYAxisLabel(Curve curve){
        String yLabel;
        SedBase xRef = this.sedml.getSedML().searchInDataGeneratorsFor(curve.getXDataReference());
        String xLabel = xRef instanceof DataGenerator dataGenerator ? PlottingDataExtractor.getBestLabel(dataGenerator) : "";
        if (curve.getName() != null) yLabel = curve.getName();
        else if (curve.getId() != null) yLabel = curve.getId().string();
        else {
            SedBase yRef = this.sedml.getSedML().searchInDataGeneratorsFor(curve.getYDataReference());
            yLabel = yRef instanceof DataGenerator dataGenerator ? PlottingDataExtractor.getBestLabel(dataGenerator) : "";
        }
        return new Pair<>(xLabel, yLabel);
    }

    private static String getBestLabel(DataGenerator dataGenerator){
        // Check if time first
        if (dataGenerator.getVariables().size() == 1){
            Variable targetVar = dataGenerator.getVariables().get(0);
            if (targetVar.isSymbol() && "urn:sedml:symbol:time".equals(targetVar.getSymbol().getUrn())) return "Time";
        }
        if (dataGenerator.getName() != null) return dataGenerator.getName();
        return dataGenerator.getIdAsString();
    }
}
