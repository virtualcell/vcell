package org.vcell.cli.run.results;

import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.solver.MathOverrides;
import cbit.vcell.solver.TempSimulation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jlibsedml.*;
import org.jlibsedml.execution.IXPathToVariableIDResolver;
import org.jlibsedml.modelsupport.SBMLSupport;
import org.vcell.cli.run.TaskJob;
import org.vcell.cli.run.hdf5.Hdf5SedmlResults;
import org.vcell.cli.run.hdf5.Hdf5SedmlResultsNonspatial;
import org.vcell.sbml.vcell.SBMLNonspatialSimResults;
import org.vcell.sedml.log.BiosimulationLog;

import java.nio.file.Paths;
import java.util.*;
public class NonSpatialResultsConverter {
    private final static Logger logger = LogManager.getLogger(NonSpatialResultsConverter.class);

    public static Map<DataGenerator, NonSpatialValueHolder> organizeNonSpatialResultsBySedmlDataGenerator(SedML sedml, Map<TaskJob, SBMLNonspatialSimResults> nonSpatialResultsHash, Map<AbstractTask, TempSimulation> taskToSimulationMap) throws ExpressionException {
        Map<DataGenerator, NonSpatialValueHolder> nonSpatialOrganizedResultsMap = new HashMap<>();
        if (nonSpatialResultsHash.isEmpty()) return nonSpatialOrganizedResultsMap;

        for (Output output : NonSpatialResultsConverter.getValidOutputs(sedml)){
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
                NonSpatialValueHolder valueHolder = NonSpatialResultsConverter.getNonSpatialValueHolderForDataGenerator(sedml, dataGen, nonSpatialResultsHash, taskToSimulationMap);
                if (valueHolder == null) continue;
                nonSpatialOrganizedResultsMap.put(dataGen, valueHolder);
            }
        }

        return nonSpatialOrganizedResultsMap;
    }


    public static Map<Report, List<Hdf5SedmlResults>> prepareNonSpatialDataForHdf5(SedML sedml, Map<DataGenerator, NonSpatialValueHolder> organizedNonSpatialResults, String sedmlLocation) {
        Map<Report, List<Hdf5SedmlResults>> results = new LinkedHashMap<>();

        // Just get the Reports from the outputs, and nothing else
        for (Report report : sedml.getOutputs().stream().filter(Report.class::isInstance).map(Report.class::cast).toList()){
            Map<DataSet, NonSpatialValueHolder> dataSetValues = new LinkedHashMap<>();

            for (DataSet dataset : report.getListOfDataSets()) {
                // use the data reference to obtain the data generator
                DataGenerator dataGen = sedml.getDataGeneratorWithId(dataset.getDataReference());
                if (dataGen == null || !organizedNonSpatialResults.containsKey(dataGen))
                    throw new RuntimeException("No data for Data Generator `" + dataset.getDataReference() + "` can be found!");
                NonSpatialValueHolder value = organizedNonSpatialResults.get(dataGen);
                dataSetValues.put(dataset, value);
            } // end of current dataset processing

            if (dataSetValues.isEmpty()) {
                logger.warn("We did not get any entries in the final data set. This may mean a problem has been encountered.");
                continue;
            }

            List<String> shapes = new LinkedList<>();
            Hdf5SedmlResultsNonspatial dataSourceNonSpatial = new Hdf5SedmlResultsNonspatial();
            Hdf5SedmlResults hdf5DatasetWrapper = new Hdf5SedmlResults();

            if (dataSetValues.entrySet().iterator().next().getValue().isEmpty()) continue; // Check if we have data to work with.

            hdf5DatasetWrapper.datasetMetadata._type = NonSpatialResultsConverter.getKind(report.getId());
            hdf5DatasetWrapper.datasetMetadata.sedmlId = NonSpatialResultsConverter.removeVCellPrefixes(report.getId(), report.getId());
            hdf5DatasetWrapper.datasetMetadata.sedmlName = report.getName();
            hdf5DatasetWrapper.datasetMetadata.uri = Paths.get(sedmlLocation, report.getId()).toString();

            for (DataSet dataSet : dataSetValues.keySet()){
                NonSpatialValueHolder dataSetValuesSource = dataSetValues.get(dataSet);

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

                for (double[] data : dataSetValuesSource.listOfResultSets) {
                    dataSourceNonSpatial.dataItems.get(report, dataSet).add(data);
                    shapes.add(Integer.toString(data.length));
                }
                
                hdf5DatasetWrapper.dataSource = dataSourceNonSpatial; // Using upcasting
                hdf5DatasetWrapper.datasetMetadata.sedmlDataSetDataTypes.add("float64");
                hdf5DatasetWrapper.datasetMetadata.sedmlDataSetIds.add(
                    NonSpatialResultsConverter.removeVCellPrefixes(dataSet.getId(), hdf5DatasetWrapper.datasetMetadata.sedmlId));
                hdf5DatasetWrapper.datasetMetadata.sedmlDataSetLabels.add(dataSet.getLabel());
                hdf5DatasetWrapper.datasetMetadata.sedmlDataSetNames.add(dataSet.getName());
                hdf5DatasetWrapper.datasetMetadata.sedmlDataSetShapes = shapes;
            }
            if (!results.containsKey(report)) results.put(report, new LinkedList<>());
            results.get(report).add(hdf5DatasetWrapper);
        } // outputs/reports
        return results;
    }

    private static NonSpatialValueHolder getNonSpatialValueHolderForDataGenerator(SedML sedml, DataGenerator dataGen, Map<TaskJob, SBMLNonspatialSimResults> nonSpatialResultsHash, Map<AbstractTask, TempSimulation> taskToSimulationMap) throws ExpressionException {
        if (dataGen == null) throw new IllegalArgumentException("Provided Data Generator can not be null!");
        Map<Variable, NonSpatialValueHolder> resultsByVariable = new HashMap<>();

        // get the list of variables associated with the data reference
        for (Variable var : dataGen.getListOfVariables()) {
            // for each variable we recover the task
            AbstractTask topLevelTask = sedml.getTaskWithId(var.getReference());
            AbstractTask baseTask = NonSpatialResultsConverter.getBaseTask(topLevelTask, sedml); // if !RepeatedTask, baseTask == topLevelTask

            // from the task we get the sbml model
            org.jlibsedml.Simulation sedmlSim = sedml.getSimulation(baseTask.getSimulationReference());

            if (!(sedmlSim instanceof UniformTimeCourse utcSim)){
                logger.error("only uniform time course simulations are supported");
                continue;
            }

            // must get variable ID from SBML model
            String vcellVarId = convertToVCellSymbol(var);

            // If the task isn't in our results hash, it's unwanted and skippable.
            boolean bFoundTaskInNonspatial = nonSpatialResultsHash.keySet().stream().anyMatch(taskJob -> taskJob.getTaskId().equals(topLevelTask.getId()));
            if (!bFoundTaskInNonspatial){
                logger.warn("Was not able to find simulation data for task with ID: {}", topLevelTask.getId());
                break;
            }

            // ==================================================================================

            ArrayList<TaskJob> taskJobs = new ArrayList<>();

            // We can have multiple TaskJobs sharing IDs (in the case of repeated tasks), so we need to get them all
            for (TaskJob taskJob : nonSpatialResultsHash.keySet()) {
                SBMLNonspatialSimResults simResults = nonSpatialResultsHash.get(taskJob);
                if (simResults == null || !taskJob.getTaskId().equals(topLevelTask.getId())) continue;
                taskJobs.add(taskJob);
                if (!(topLevelTask instanceof RepeatedTask)) break; // No need to keep looking if it's not a repeated task, one "loop" is good
            }

            if (taskJobs.isEmpty()) continue;

            boolean resultsAlreadyExist = topLevelTask instanceof RepeatedTask && resultsByVariable.containsKey(var);
            NonSpatialValueHolder individualVarResultsHolder = resultsAlreadyExist ? resultsByVariable.get(var) :
                    new NonSpatialValueHolder(taskToSimulationMap.get(topLevelTask));
            for (TaskJob taskJob : taskJobs) {
                // Leaving intermediate variables for debugging access
                SBMLNonspatialSimResults nonSpatialResults = nonSpatialResultsHash.get(taskJob);
                double[] data = nonSpatialResults.getDataForSBMLVar(vcellVarId, utcSim.getOutputStartTime(), utcSim.getNumberOfPoints());
                individualVarResultsHolder.listOfResultSets.add(data);
            }
            resultsByVariable.put(var, individualVarResultsHolder);
        }
        if (resultsByVariable.isEmpty()) return null;

        // We now need to condense the multiple variables into a single resolved value

        String exampleReference = resultsByVariable.keySet().iterator().next().getReference();
        int numJobs = resultsByVariable.values().iterator().next().listOfResultSets.size();
        NonSpatialValueHolder synthesizedResults = new NonSpatialValueHolder(taskToSimulationMap.get(sedml.getTaskWithId(exampleReference)));
        SimpleDataGenCalculator calc = new SimpleDataGenCalculator(dataGen);

        // Get padding value
        int maxLengthOfData = 0;
        for (NonSpatialValueHolder nvh : resultsByVariable.values()){
            for (double[] dataSet : nvh.listOfResultSets){
                if (dataSet.length <= maxLengthOfData) continue;
                maxLengthOfData = dataSet.length;
            }
        }

        // Perform the math!
        for (int jobNum = 0; jobNum < numJobs; jobNum++){
            double[] synthesizedDataset = new double[maxLengthOfData];
            for (int datumIndex = 0; datumIndex < synthesizedDataset.length; datumIndex++){

                for (Variable var : resultsByVariable.keySet()){
                    //if (processedDataSet == null) processedDataSet = new NonspatialValueHolder(sedml.getTaskWithId(var.getReference()));
                    if (jobNum >= resultsByVariable.get(var).listOfResultSets.size()) continue;
                    NonSpatialValueHolder nonspatialValue = resultsByVariable.get(var);
                    double[] specficJobDataSet = nonspatialValue.listOfResultSets.get(jobNum);
                    double datum = datumIndex >= specficJobDataSet.length ? Double.NaN : specficJobDataSet[datumIndex];
                    calc.setArgument(var.getId(), datum);
                    if (!synthesizedResults.vcSimulation.equals(nonspatialValue.vcSimulation)){
                        logger.warn("Simulations differ across variables; need to fix data structures to accomodate?");
                    }
                }
                synthesizedDataset[datumIndex] = calc.evaluateWithCurrentArguments(true);
            }
            synthesizedResults.listOfResultSets.add(synthesizedDataset);
        }
        return synthesizedResults;
    }

    private static List<Output> getValidOutputs(SedML sedml){
        List<Output> nonPlot3DOutputs = new ArrayList<>();
        List<Plot3D> plot3DOutputs = new ArrayList<>();
        for (Output output : sedml.getOutputs()){
            if (output instanceof Plot3D plot3D) plot3DOutputs.add(plot3D);
            else nonPlot3DOutputs.add(output);
        }

        if (!plot3DOutputs.isEmpty()) logger.warn("VCell currently does not support creation of 3D plots, {} plot{} will be skipped.",
                plot3DOutputs.size(), plot3DOutputs.size() == 1 ? "" : "s");
        return nonPlot3DOutputs;

    }

    private static AbstractTask getBaseTask(AbstractTask task, SedML sedml){
        if (task == null) throw new IllegalArgumentException("task arguement is `null`!");
        while (task instanceof RepeatedTask repeatedTask) { // We need to find the original task burried beneath.
            // We assume that we can never have a sequential repeated task at this point, we check for that in SEDMLImporter
            SubTask st = repeatedTask.getSubTasks().entrySet().iterator().next().getValue(); // single subtask
            task = sedml.getTaskWithId(st.getTaskId());
            if (task == null) throw new IllegalArgumentException("Bad SedML formatting; task with id " + st.getTaskId() +" not found");
        }
        return task;
    }

    private static String convertToVCellSymbol(Variable var){
         // must get variable ID from SBML model
         String sbmlVarId = "";
         if (var.getSymbol() != null) { // it is a predefined symbol
             // search the sbml model to find the vcell variable name associated with the run
             switch(var.getSymbol().name()){
                 case "TIME": { // TIME is t, etc
                     sbmlVarId = "t"; // this is VCell reserved symbold for time
                     break;
                 }
                 default:{
                     sbmlVarId = var.getSymbol().name();
                 }
                 // etc, TODO: check spec for other symbols (CSymbols?)
                 // Delay? Avogadro? rateOf?
             }
         } else { // it is an XPATH target in model
             String target = var.getTarget();
             IXPathToVariableIDResolver resolver = new SBMLSupport();
             sbmlVarId = resolver.getIdFromXPathIdentifer(target);
         }
         return sbmlVarId;
    }


    private static String getKind(String prefixedSedmlId){
        String plotPrefix = "__plot__";
        if (prefixedSedmlId.startsWith(plotPrefix))
            return "SedPlot2D";
        return "SedReport";
    }

    /**
     * We need the sedmlId to help remove prefixes, but the sedmlId itself may need to be fixed.
     * 
     * If a sedmlId is being checked, just provide itself twice
     * 
     * The reason for this, is having an overload with just "(String s)" as a requirment is misleading.
     */
    private static String removeVCellPrefixes(String s, String sedmlId){
        String plotPrefix = "__plot__";
        String reservedPrefix = "__vcell_reserved_data_set_prefix__";
        
        String checkedId = sedmlId.startsWith(plotPrefix) ? sedmlId.replace(plotPrefix, "") : sedmlId;
        if (sedmlId.equals(s)) return checkedId;
        
        if (s.startsWith(plotPrefix)){
            s = s.replace(plotPrefix, "");
        } 
        
        if (s.startsWith(reservedPrefix)){
            s = s.replace(reservedPrefix, "");
        }

        if (s.startsWith(checkedId + "_")){
            s = s.replace(checkedId + "_", "");
        }

        if (s.startsWith(checkedId)){
            s = s.replace(checkedId, "");
        }

        return s;
    }
}
