package org.vcell.cli.run.results;

import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.solver.MathOverrides;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.TempSimulation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jlibsedml.*;
import org.vcell.cli.run.TaskJob;
import org.vcell.cli.run.hdf5.Hdf5SedmlResults;
import org.vcell.cli.run.hdf5.Hdf5SedmlResultsNonSpatial;
import org.vcell.sbml.vcell.NonSpatialSBMLSimResults;
import org.vcell.sbml.vcell.SBMLDataRecord;
import org.vcell.sbml.vcell.lazy.LazySBMLDataAccessor;
import org.vcell.sbml.vcell.lazy.LazySBMLNonSpatialDataAccessor;
import org.vcell.sedml.SEDMLImportException;
import org.vcell.sedml.log.BiosimulationLog;

import java.nio.file.Paths;
import java.util.*;

public class NonSpatialResultsConverter extends ResultsConverter {
    private final static Logger logger = LogManager.getLogger(NonSpatialResultsConverter.class);

    public static Map<DataGenerator, ValueHolder<LazySBMLNonSpatialDataAccessor>> organizeNonSpatialResultsBySedmlDataGenerator(SedML sedml, Map<TaskJob, NonSpatialSBMLSimResults> nonSpatialResultsHash, Map<AbstractTask, TempSimulation> taskToSimulationMap) throws ExpressionException, SEDMLImportException {
        Map<DataGenerator, ValueHolder<LazySBMLNonSpatialDataAccessor>> nonSpatialOrganizedResultsMap = new HashMap<>();
        if (nonSpatialResultsHash.isEmpty()) return nonSpatialOrganizedResultsMap;

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
            // We need to determine the "correct" number of data points to pad correctly.
            int maxTimeLength = 0;
            for (DataGenerator dataGenerator : dataGeneratorsToProcess){
                for (Variable variable : dataGenerator.getListOfVariables()){
                    AbstractTask task = sedml.getTaskWithId(variable.getReference());
                    AbstractTask derivedTask = ResultsConverter.getBaseTask(task, sedml);
                    if (!(derivedTask instanceof Task baseTask)) throw new SEDMLImportException("Unable to find base task referred to by var `" + variable.getId() + "`");
                    org.jlibsedml.Simulation sim = sedml.getSimulation(baseTask.getSimulationReference());
                    if (!(sim instanceof UniformTimeCourse utcSim)) throw new SEDMLImportException("Unable to find utc sim referred to by var `" + variable.getId() + "`");
                    maxTimeLength = Math.max(utcSim.getNumberOfSteps() + 1, maxTimeLength);
                }
            }

            for (DataGenerator dataGen : dataGeneratorsToProcess) {
                ValueHolder<LazySBMLNonSpatialDataAccessor> valueHolder = NonSpatialResultsConverter.getNonSpatialValueHolderForDataGenerator(sedml, dataGen, nonSpatialResultsHash, taskToSimulationMap, maxTimeLength);
                if (valueHolder == null) continue;
                nonSpatialOrganizedResultsMap.put(dataGen, valueHolder);
            }
        }

        return nonSpatialOrganizedResultsMap;
    }


    public static Map<Report, List<Hdf5SedmlResults>> prepareNonSpatialDataForHdf5(SedML sedml, Map<DataGenerator, ValueHolder<LazySBMLNonSpatialDataAccessor>> nonSpatialResultsMapping,
                                                                                   Set<DataGenerator> allValidDataGenerators, String sedmlLocation, boolean isBioSimMode) {
        Map<Report, List<Hdf5SedmlResults>> results = new LinkedHashMap<>();
        if (nonSpatialResultsMapping.isEmpty()){
            logger.debug("No non-spatial data generated; No need to prepare non-existent data!");
            return results;
        }
        List<Report> modifiedList = new ArrayList<>(sedml.getOutputs().stream().filter(Report.class::isInstance).map(Report.class::cast).toList());


        Map<DataGenerator, ValueHolder<LazySBMLDataAccessor>> generalizedResultsMapping = new LinkedHashMap<>();
        for (var set : nonSpatialResultsMapping.entrySet()) generalizedResultsMapping.put(set.getKey(), new ValueHolder<>(set.getValue()));
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

            List<String> shapes = new LinkedList<>();
            Hdf5SedmlResultsNonSpatial dataSourceNonSpatial = new Hdf5SedmlResultsNonSpatial();
            Hdf5SedmlResults hdf5DatasetWrapper = new Hdf5SedmlResults();

            if (dataSetValues.entrySet().iterator().next().getValue().isEmpty()) continue; // Check if we have data to work with.

            hdf5DatasetWrapper.datasetMetadata._type = NonSpatialResultsConverter.getKind(report.getId());
            hdf5DatasetWrapper.datasetMetadata.sedmlId = ResultsConverter.removeVCellPrefixes(report.getId(), report.getId());
            hdf5DatasetWrapper.datasetMetadata.sedmlName = report.getName();
            hdf5DatasetWrapper.datasetMetadata.uri = Paths.get(sedmlLocation, report.getId()).toString();

            for (DataSet dataSet : dataSetValues.keySet()){
                ValueHolder<LazySBMLDataAccessor> dataSetValuesSource = dataSetValues.get(dataSet);

                dataSourceNonSpatial.dataItems.put(report, dataSet, new LinkedList<>());
                dataSourceNonSpatial.scanBounds = dataSetValuesSource.vcSimulation.getMathOverrides().getScanBounds();
                dataSourceNonSpatial.scanParameterNames = dataSetValuesSource.vcSimulation.getMathOverrides().getScannedConstantNames();
                double[][] scanValues = new double[dataSourceNonSpatial.scanBounds.length][];
                for (int nameIndex = 0; nameIndex < dataSourceNonSpatial.scanBounds.length; nameIndex++){
                    String nameKey = dataSourceNonSpatial.scanParameterNames[nameIndex];
                    scanValues[nameIndex] = new double[dataSourceNonSpatial.scanBounds[nameIndex] + 1];
                    for (int scanIndex = 0; scanIndex < dataSourceNonSpatial.scanBounds[nameIndex] + 1; scanIndex++){
                        Expression overrideExp = dataSetValuesSource.vcSimulation.getMathOverrides().getActualExpression(nameKey, new MathOverrides.ScanIndex(scanIndex));
                        try { scanValues[nameIndex][scanIndex] = overrideExp.evaluateConstant(); }
                        catch (ExpressionException e){ throw new RuntimeException(e); }
                    }
                }
                dataSourceNonSpatial.scanParameterValues = scanValues;

                for (LazySBMLDataAccessor data : dataSetValuesSource.listOfResultSets) {
                    if (!(data instanceof LazySBMLNonSpatialDataAccessor nonSpatialData))
                        throw new IllegalArgumentException("Spatial data somehow got into non-spatial data!");
                    dataSourceNonSpatial.dataItems.get(report, dataSet).add(nonSpatialData);
                    shapes.add(Integer.toString(data.getFlatSize()));
                }
                
                hdf5DatasetWrapper.dataSource = dataSourceNonSpatial; // Using upcasting
                hdf5DatasetWrapper.datasetMetadata.sedmlDataSetDataTypes.add("float64");
                hdf5DatasetWrapper.datasetMetadata.sedmlDataSetIds.add(
                    ResultsConverter.removeVCellPrefixes(dataSet.getId(), hdf5DatasetWrapper.datasetMetadata.sedmlId));
                hdf5DatasetWrapper.datasetMetadata.sedmlDataSetLabels.add(dataSet.getLabel());
                hdf5DatasetWrapper.datasetMetadata.sedmlDataSetNames.add(dataSet.getName());
                hdf5DatasetWrapper.datasetMetadata.sedmlDataSetShapes = shapes;
            }
            if (!results.containsKey(report)) results.put(report, new LinkedList<>());
            results.get(report).add(hdf5DatasetWrapper);
        } // outputs/reports
        return results;
    }

    private static ValueHolder<LazySBMLNonSpatialDataAccessor> getNonSpatialValueHolderForDataGenerator(SedML sedml, DataGenerator dataGen,
                                                                                  Map<TaskJob, NonSpatialSBMLSimResults> nonSpatialResultsHash,
                                                                                  Map<AbstractTask, TempSimulation> taskToSimulationMap, int padToLength) throws ExpressionException {
        if (dataGen == null) throw new IllegalArgumentException("Provided Data Generator can not be null!");
        Map<Variable, ValueHolder<LazySBMLNonSpatialDataAccessor>> resultsByVariable = new HashMap<>();

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

            // If the task isn't in our results hash, it's unwanted and skippable.
            boolean bFoundTaskInNonspatial = nonSpatialResultsHash.keySet().stream().anyMatch(taskJob -> taskJob.getTaskId().equals(topLevelTask.getId()));
            if (!bFoundTaskInNonspatial){
                if (logger.isDebugEnabled()) logger.warn("Was not able to find simulation data for task with ID: {}", topLevelTask.getId());
                break;
            }

            // ==================================================================================

            ArrayList<TaskJob> taskJobs = new ArrayList<>();

            // We can have multiple TaskJobs sharing IDs (in the case of repeated tasks), so we need to get them all
            for (TaskJob taskJob : nonSpatialResultsHash.keySet()) {
                NonSpatialSBMLSimResults simResults = nonSpatialResultsHash.get(taskJob);
                if (simResults == null || !taskJob.getTaskId().equals(topLevelTask.getId())) continue;
                taskJobs.add(taskJob);
                if (!(topLevelTask instanceof RepeatedTask)) break; // No need to keep looking if it's not a repeated task, one "loop" is good
            }

            if (taskJobs.isEmpty()) continue;

            boolean resultsAlreadyExist = topLevelTask instanceof RepeatedTask && resultsByVariable.containsKey(var);
            ValueHolder<LazySBMLNonSpatialDataAccessor> individualVarResultsHolder = resultsAlreadyExist ? resultsByVariable.get(var) :
                    new ValueHolder<>(taskToSimulationMap.get(topLevelTask));
            for (TaskJob taskJob : taskJobs) {
                // Leaving intermediate variables for debugging access
                NonSpatialSBMLSimResults nonSpatialResults = nonSpatialResultsHash.get(taskJob);
                LazySBMLNonSpatialDataAccessor dataAccessor = nonSpatialResults.getSBMLDataAccessor(vcellVarId, utcSim, padToLength);
                individualVarResultsHolder.listOfResultSets.add(dataAccessor);
            }
            resultsByVariable.put(var, individualVarResultsHolder);
        }
        if (resultsByVariable.isEmpty()) return null;

        // We now need to condense the multiple variables into a single resolved value

        String exampleReference = resultsByVariable.keySet().iterator().next().getReference();
        int numJobs = resultsByVariable.values().iterator().next().listOfResultSets.size();
        ValueHolder<LazySBMLNonSpatialDataAccessor> synthesizedResults = new ValueHolder<>(taskToSimulationMap.get(sedml.getTaskWithId(exampleReference)));
        SimpleDataGenCalculator calc = new SimpleDataGenCalculator(dataGen);

        // Perform the math!
        for (int jobNum = 0; jobNum < numJobs; jobNum++){
            final int finalJobNum = jobNum; // need to finalize to put in lambda.
            LazySBMLNonSpatialDataAccessor synthesizedLazyDataset = new LazySBMLNonSpatialDataAccessor(
                    ()-> NonSpatialResultsConverter.getSynthesizedDataSet(calc, resultsByVariable, synthesizedResults.vcSimulation, padToLength, finalJobNum),
                    padToLength
            );
            synthesizedResults.listOfResultSets.add(synthesizedLazyDataset);
        }
        return synthesizedResults;
    }

    private static SBMLDataRecord getSynthesizedDataSet(SimpleDataGenCalculator calc, Map<Variable, ValueHolder<LazySBMLNonSpatialDataAccessor>> resultsByVariable,
                                                        Simulation vcSimulation, int finalMaxLengthOfData, int jobNum) throws Exception {
        double[] postProcessedData = new double[finalMaxLengthOfData];
        for (int datumIndex = 0; datumIndex < finalMaxLengthOfData; datumIndex++){
            for (Variable var : resultsByVariable.keySet()){
                if (jobNum >= resultsByVariable.get(var).listOfResultSets.size()) continue;
                ValueHolder<LazySBMLNonSpatialDataAccessor> nonSpatialValue = resultsByVariable.get(var);
                LazySBMLNonSpatialDataAccessor specificJobDataSet = nonSpatialValue.listOfResultSets.get(jobNum);
                double[] lazyData = specificJobDataSet.getData().data();
                double datum = datumIndex >= lazyData.length ? Double.NaN : lazyData[datumIndex];
                calc.setArgument(var.getId(), datum);
                if (!vcSimulation.equals(nonSpatialValue.vcSimulation)){
                    logger.warn("Simulations differ across variables; need to fix data structures to accommodate?");
                }
            }
            postProcessedData[datumIndex] = calc.evaluateWithCurrentArguments(true);
        }
        return new SBMLDataRecord(postProcessedData, List.of(postProcessedData.length), null);
    }
}
