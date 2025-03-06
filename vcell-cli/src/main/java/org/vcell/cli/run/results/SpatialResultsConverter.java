package org.vcell.cli.run.results;

import cbit.vcell.math.MathException;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.solver.MathOverrides;
import cbit.vcell.solver.TempSimulation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jlibsedml.*;
import org.vcell.cli.exceptions.ExecutionException;
import org.vcell.cli.run.TaskJob;
import org.vcell.cli.run.hdf5.Hdf5SedmlResults;
import org.vcell.cli.run.hdf5.Hdf5SedmlResultsSpatial;
import org.vcell.sbml.vcell.SpatialSBMLSimResults;
import org.vcell.sbml.vcell.lazy.LazySBMLDataAccessor;
import org.vcell.sbml.vcell.lazy.LazySBMLSpatialDataAccessor;
import org.vcell.sedml.log.BiosimulationLog;
import org.vcell.util.DataAccessException;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

public class SpatialResultsConverter extends ResultsConverter {
    private final static Logger logger = LogManager.getLogger(SpatialResultsConverter.class);

    public static Map<DataGenerator, ValueHolder<LazySBMLDataAccessor>> organizeSpatialResultsBySedmlDataGenerator(SedML sedml, Map<TaskJob, SpatialSBMLSimResults> spatialResultsHash, Map<AbstractTask, TempSimulation> taskToSimulationMap) throws ExpressionException, MathException, IOException, ExecutionException, DataAccessException {
        Map<DataGenerator, ValueHolder<LazySBMLDataAccessor>> spatialOrganizedResultsMap = new HashMap<>();
        if (spatialResultsHash.isEmpty()) return spatialOrganizedResultsMap;

        for (Output output : ResultsConverter.getValidOutputs(sedml)){
            Set<DataGenerator> dataGeneratorsToProcess;
            if (output instanceof Report report){
                dataGeneratorsToProcess = new LinkedHashSet<>();
                for (DataSet dataSet : report.getListOfDataSets()){
                    // use the data reference to obtain the data generator
                    dataGeneratorsToProcess.add(sedml.getDataGeneratorWithId(dataSet.getDataReference()));
                    BiosimulationLog.instance().updateDatasetStatusYml(Paths.get(sedml.getPathForURI(), sedml.getFileName()).toString(), output.getId(), dataSet.getId(), BiosimulationLog.Status.SUCCEEDED);
                }
            }
            else if (output instanceof Plot2D plot2D){
                Set<DataGenerator> uniqueDataGens = new LinkedHashSet<>();
                for (Curve curve : plot2D.getListOfCurves()){
                    uniqueDataGens.add(sedml.getDataGeneratorWithId(curve.getXDataReference()));
                    uniqueDataGens.add(sedml.getDataGeneratorWithId(curve.getYDataReference()));
                }
                dataGeneratorsToProcess = uniqueDataGens;
            } else {
                if (logger.isDebugEnabled()) logger.warn("Unrecognized output type: `{}` (id={})", output.getClass().getName(), output.getId());
                continue;
            }

            for (DataGenerator dataGen : dataGeneratorsToProcess) {
                ValueHolder<LazySBMLDataAccessor> valueHolder = SpatialResultsConverter.getSpatialValueHolderForDataGenerator(sedml, dataGen, spatialResultsHash, taskToSimulationMap);
                // if (valueHolder == null) continue; // We don't want this, we want nulls to pass through for later processing.
                spatialOrganizedResultsMap.put(dataGen, valueHolder);
            }
        }

        return spatialOrganizedResultsMap;
    }

    public static Map<Report, List<Hdf5SedmlResults>> prepareSpatialDataForHdf5(SedML sedml, Map<DataGenerator, ValueHolder<LazySBMLDataAccessor>> spatialResultsMapping,
                                                                                   Set<DataGenerator> allValidDataGenerators, String sedmlLocation, boolean isBioSimMode) {
        Map<Report, List<Hdf5SedmlResults>> results = new LinkedHashMap<>();
        if (spatialResultsMapping.isEmpty()){
            logger.debug("No spatial data generated; No need to prepare non-existent data!");
            return results;
        }

        List<Report> modifiedList = new ArrayList<>(sedml.getOutputs().stream().filter(Report.class::isInstance).map(Report.class::cast).toList());

        // We can generalize the results now!
        Map<DataGenerator, ValueHolder<LazySBMLDataAccessor>> generalizedResultsMapping = new LinkedHashMap<>();
        for (var set : spatialResultsMapping.entrySet()) generalizedResultsMapping.put(set.getKey(), set.getValue() == null ? null : new ValueHolder<>(set.getValue()));

        // If we're not doing biosimulators mode, we need to record the plot2D as reports as well.
        if (isBioSimMode) ResultsConverter.add2DPlotsAsReports(sedml, generalizedResultsMapping, modifiedList);

        for (Report report : modifiedList){
            Map<DataSet, ValueHolder<LazySBMLDataAccessor>> dataSetValues = new LinkedHashMap<>();

            for (DataSet dataset : report.getListOfDataSets()) {
                // use the data reference to obtain the data generator
                DataGenerator dataGen = sedml.getDataGeneratorWithId(dataset.getDataReference());
                if (dataGen == null)
                    throw new RuntimeException("No data for Data Generator `" + dataset.getDataReference() + "` can be found!");
                if (!generalizedResultsMapping.containsKey(dataGen)){
                    if (allValidDataGenerators.contains(dataGen)) continue;
                    throw new RuntimeException("No data for Data Generator `" + dataset.getDataReference() + "` can be found!");
                }
                ValueHolder<LazySBMLDataAccessor> value = generalizedResultsMapping.get(dataGen);
                dataSetValues.put(dataset, value);
            } // end of current dataset processing

            if (dataSetValues.isEmpty()) {
                logger.warn("We did not get any entries in the final data set. This may mean a problem has been encountered.");
                continue;
            }


            Hdf5SedmlResultsSpatial dataSourceSpatial = new Hdf5SedmlResultsSpatial();
            Hdf5SedmlResults hdf5DatasetWrapper = new Hdf5SedmlResults();

            hdf5DatasetWrapper.datasetMetadata._type = SpatialResultsConverter.getKind(report.getId());
            hdf5DatasetWrapper.datasetMetadata.sedmlId = ResultsConverter.removeVCellPrefixes(report.getId(), report.getId());
            hdf5DatasetWrapper.datasetMetadata.sedmlName = report.getName();
            hdf5DatasetWrapper.datasetMetadata.uri = Paths.get(sedmlLocation, report.getId()).toString();

            Set<DataSet> refinedDataSets = new LinkedHashSet<>();
            for (DataSet dataSet : dataSetValues.keySet()){
                if (null == dataSetValues.get(dataSet)) continue;
                refinedDataSets.add(dataSet);
            }
            if (refinedDataSets.isEmpty()) continue; // Check if we have data to work with.

            for (DataSet dataSet : refinedDataSets){
                ValueHolder<LazySBMLDataAccessor> dataSetValuesSource = dataSetValues.get(dataSet);
                List<String> shapes = new LinkedList<>();
                dataSourceSpatial.dataItems.put(report, dataSet, new LinkedList<>());
                dataSourceSpatial.scanBounds = dataSetValuesSource.vcSimulation.getMathOverrides().getScanBounds();
                dataSourceSpatial.scanParameterNames = dataSetValuesSource.vcSimulation.getMathOverrides().getScannedConstantNames();
                double[][] scanValues = new double[dataSourceSpatial.scanBounds.length][];
                for (int nameIndex = 0; nameIndex < dataSourceSpatial.scanBounds.length; nameIndex++){
                    String nameKey = dataSourceSpatial.scanParameterNames[nameIndex];
                    scanValues[nameIndex] = new double[dataSourceSpatial.scanBounds[nameIndex] + 1];
                    for (int scanIndex = 0; scanIndex < dataSourceSpatial.scanBounds[nameIndex] + 1; scanIndex++){
                        Expression overrideExp = dataSetValuesSource.vcSimulation.getMathOverrides().getActualExpression(nameKey, new MathOverrides.ScanIndex(scanIndex));
                        try { scanValues[nameIndex][scanIndex] = overrideExp.evaluateConstant(); }
                        catch (ExpressionException e){ throw new RuntimeException(e); }
                    }
                }
                dataSourceSpatial.scanParameterValues = scanValues;

                for (LazySBMLDataAccessor data : dataSetValuesSource.listOfResultSets) {
                    if (!(data instanceof LazySBMLSpatialDataAccessor spatialData))
                        throw new IllegalArgumentException("Non-spatial data somehow got into spatial data!");
                    dataSourceSpatial.dataItems.get(report, dataSet).add(spatialData);
                    List<String> subShapes = new LinkedList<>();
                    subShapes.add(Integer.toString(data.getDesiredTimes().size()));
                    subShapes.addAll(data.getSpatialDimensions().stream().map(String::valueOf).filter((x)->!"1".equals(x)).toList());
                    if (1 == subShapes.size()) shapes.add(subShapes.get(0));
                    shapes.add(1 == subShapes.size() ? subShapes.get(0) : "[" + String.join(",", subShapes) + "]");
                }

                hdf5DatasetWrapper.dataSource = dataSourceSpatial; // Using upcasting
                hdf5DatasetWrapper.datasetMetadata.sedmlDataSetDataTypes.add("float64");
                hdf5DatasetWrapper.datasetMetadata.sedmlDataSetIds.add(
                        ResultsConverter.removeVCellPrefixes(dataSet.getId(), hdf5DatasetWrapper.datasetMetadata.sedmlId));
                hdf5DatasetWrapper.datasetMetadata.sedmlDataSetLabels.add(dataSet.getLabel());
                hdf5DatasetWrapper.datasetMetadata.sedmlDataSetNames.add(dataSet.getName());

                String shapesStr = "(" + String.join(",", shapes) + ")";
                hdf5DatasetWrapper.datasetMetadata.sedmlDataSetShapes.add(shapesStr);
            }

            if (!results.containsKey(report)) results.put(report, new LinkedList<>());
            results.get(report).add(hdf5DatasetWrapper);
        } // outputs/reports
        return results;
    }

    private static ValueHolder<LazySBMLDataAccessor> getSpatialValueHolderForDataGenerator(SedML sedml, DataGenerator dataGen,
                                                                                  Map<TaskJob, SpatialSBMLSimResults> spatialResultsHash,
                                                                                  Map<AbstractTask, TempSimulation> taskToSimulationMap) throws ExpressionException, ExecutionException, MathException, IOException, DataAccessException {
        if (dataGen == null) throw new IllegalArgumentException("Provided Data Generator can not be null!");
        Map<Variable, ValueHolder<LazySBMLDataAccessor>> resultsByVariable = new HashMap<>();
        int maxLengthOfData = 0;

        // get the list of variables associated with the data reference
        for (Variable var : dataGen.getListOfVariables()) {
            // for each variable we recover the task
            AbstractTask topLevelTask = sedml.getTaskWithId(var.getReference());
            AbstractTask baseTask = ResultsConverter.getBaseTask(topLevelTask, sedml); // if !RepeatedTask, baseTask == topLevelTask

            // from the task we get the sbml model
            org.jlibsedml.Simulation sedmlSim = sedml.getSimulation(baseTask.getSimulationReference());

            if (!(sedmlSim instanceof UniformTimeCourse utcSim)){
                logger.error("only uniform time course simulations are supported");
                continue;
            }

            // must get variable ID from SBML model
            String vcellVarId = ResultsConverter.convertToVCellSymbol(var);
            // VCell has decided it is inappropriate to include time as a spatial variable; we provide an HDF5 attribute in lieu.
            if ("t".equals(vcellVarId)) return null;

            // If the task isn't in our results hash, it's unwanted and skippable.
            boolean bFoundTaskInSpatial = spatialResultsHash.keySet().stream().anyMatch(taskJob -> taskJob.getTaskId().equals(topLevelTask.getId()));
            if (!bFoundTaskInSpatial){
                if (logger.isDebugEnabled()) logger.warn("Was not able to find simulation data for task with ID: {}", topLevelTask.getId());
                break;
            }

            // ==================================================================================

            ArrayList<TaskJob> taskJobs = new ArrayList<>();

            // We can have multiple TaskJobs sharing IDs (in the case of repeated tasks), so we need to get them all
            for (TaskJob taskJob : spatialResultsHash.keySet()) {
                SpatialSBMLSimResults simResults = spatialResultsHash.get(taskJob);
                if (simResults == null || !taskJob.getTaskId().equals(topLevelTask.getId())) continue;
                taskJobs.add(taskJob);
                if (!(topLevelTask instanceof RepeatedTask)) break; // No need to keep looking if it's not a repeated task, one "loop" is good
            }

            if (taskJobs.isEmpty()) continue;

            boolean resultsAlreadyExist = topLevelTask instanceof RepeatedTask && resultsByVariable.containsKey(var);
            ValueHolder<LazySBMLDataAccessor> individualVarResultsHolder = resultsAlreadyExist ? resultsByVariable.get(var) :
                    new ValueHolder<>(taskToSimulationMap.get(topLevelTask));
            for (TaskJob taskJob : taskJobs) {
                // Leaving intermediate variables for debugging access
                SpatialSBMLSimResults spatialResults = spatialResultsHash.get(taskJob);
                LazySBMLDataAccessor dataAccessor = spatialResults.getSBMLDataAccessor(vcellVarId, utcSim);
                individualVarResultsHolder.listOfResultSets.add(dataAccessor);
                int localMax;
                if ((localMax = spatialResults.getMaxDataFlatLength()) > maxLengthOfData) maxLengthOfData = localMax;
            }
            resultsByVariable.put(var, individualVarResultsHolder);
        }
        if (resultsByVariable.isEmpty()) return null;

        if (resultsByVariable.size() != 1) throw new ExecutionException("Unable to process multi-variable data generators for spatial sims!");
        return resultsByVariable.values().iterator().next();
    }
}
