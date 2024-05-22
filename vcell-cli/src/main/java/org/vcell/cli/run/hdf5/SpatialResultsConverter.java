package org.vcell.cli.run.hdf5;


import cbit.vcell.parser.DivideByZeroException;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.solver.TempSimulation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jlibsedml.*;
import org.jlibsedml.execution.IXPathToVariableIDResolver;
import org.jlibsedml.modelsupport.SBMLSupport;
import org.vcell.cli.PythonStreamException;
import org.vcell.cli.run.PythonCalls;
import org.vcell.cli.run.Status;
import org.vcell.cli.run.TaskJob;

import java.io.*;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SpatialResultsConverter {
    private final static Logger lg = LogManager.getLogger(SpatialResultsConverter.class);

    public static Map<Report, List<Hdf5SedmlResults>> convertSpatialResultsToSedmlFormat(SedML sedml, Map<TaskJob, File> spatialResultsHash, Map<AbstractTask, TempSimulation> taskToSimulationMap, String sedmlLocation, String outDir) throws PythonStreamException {
        Map<Report, List<Hdf5SedmlResults>> results = new LinkedHashMap<>();
        ReorganizedSpatialResults sourceOfTruth = new ReorganizedSpatialResults(spatialResultsHash, taskToSimulationMap);

        for (Report report : SpatialResultsConverter.getReports(sedml.getOutputs())){
            Hdf5SedmlResultsSpatial convertedData = new Hdf5SedmlResultsSpatial();
            Map<DataGenerator, List<DataSet>> dataGenToDataSets = new HashMap<>();

            // go through each entry (dataset)
            for (DataSet dataSet : report.getListOfDataSets()) {
                // use the data reference to obtain the data generator
                DataGenerator dataGen = sedml.getDataGeneratorWithId(dataSet.getDataReference()); assert dataGen != null;
                boolean returnedGoodResult = processDataGenerator(sedml, report, dataGen, taskToSimulationMap, sourceOfTruth, convertedData);
                if (!returnedGoodResult) continue;
                if (!dataGenToDataSets.containsKey(dataGen)) dataGenToDataSets.put(dataGen, new ArrayList<>());
                dataGenToDataSets.get(dataGen).add(dataSet);
                PythonCalls.updateDatasetStatusYml(sedmlLocation, report.getId(), dataSet.getId(), Status.SUCCEEDED, outDir);
            } // end of dataset

            // Fill out DatasetWrapper Values
            Hdf5SedmlResultsSpatial.SpatialComponents reportMappings = convertedData.dataMapping.get(report);
            Hdf5SedmlResults hdf5DatasetWrapper = new Hdf5SedmlResults();
            hdf5DatasetWrapper.datasetMetadata._type = SpatialResultsConverter.getKind(report.getId());
            hdf5DatasetWrapper.datasetMetadata.sedmlId = SpatialResultsConverter.removeVCellPrefixes(report.getId(), report.getId());
            hdf5DatasetWrapper.datasetMetadata.sedmlName = report.getName();
            hdf5DatasetWrapper.datasetMetadata.uri = Paths.get(sedmlLocation, report.getId()).toString();
            hdf5DatasetWrapper.dataSource = convertedData;

            for (DataGenerator jobDataSet : reportMappings.varsToData.getVariableSet()){
                for (DataSet dataSet : dataGenToDataSets.get(jobDataSet)){
                    String dimensionLabelString = "[" + "XYZ".substring(0, reportMappings.metadata.getNumSpaceTimeDimensions() - 1) + "T]";
                    hdf5DatasetWrapper.datasetMetadata.sedmlDataSetDataTypes.add("float64");
                    hdf5DatasetWrapper.datasetMetadata.sedmlDataSetIds.add(
                            SpatialResultsConverter.removeVCellPrefixes(dataSet.getId(), hdf5DatasetWrapper.datasetMetadata.sedmlId));
                    hdf5DatasetWrapper.datasetMetadata.sedmlDataSetLabels.add(dataSet.getLabel() + dimensionLabelString);
                    hdf5DatasetWrapper.datasetMetadata.sedmlDataSetNames.add(dataSet.getName() + dimensionLabelString);
                    List<Integer> shapes = new LinkedList<>();
                    if (reportMappings.varsToData.getMaxNumScans() > 1){
                        shapes.add(reportMappings.varsToData.getMaxNumScans());
                    }
                    for (int size : reportMappings.metadata.getSpaceTimeDimensions()) shapes.add(size);
                    hdf5DatasetWrapper.datasetMetadata.sedmlDataSetShapes.add(shapes.toString());
                }

            }
            if (!results.containsKey(report)) results.put(report, new LinkedList<>());
            results.get(report).add(hdf5DatasetWrapper);
        } // outputs/reports
        return results;
    }

    private static boolean processDataGenerator(SedML sedml, Report report, DataGenerator dataGen, Map<AbstractTask, TempSimulation> completeSedmlTaskToVCellSim,
                                                ReorganizedSpatialResults sourceOfTruth, Hdf5SedmlResultsSpatial convertedData){
        //TODO: Allow multiple variables in DataGenerators for Spatial, AND fill out TDDO below.
        List<Variable> dataGenVarList = dataGen.getListOfVariables();
        if (dataGenVarList.size() != 1){
            lg.error("Multi-variable data generators in spatial sim detected.");
            throw new RuntimeException("VCell is unable to support multi-variable data generators in spatial sims at this time.");
        }

        Map<AbstractTask, List<DataGenerator>> sedmlTaskMap = new HashMap<>();
        // get the list of variables associated with the data reference
        Map<AbstractTask, TempSimulation> sedmlTaskToVCellSim = new HashMap<>();
        boolean allVarsValid = true;
        for (Variable variable : dataGenVarList) { // Since we're only doing single variable, this should run once!
            // Check if it's asking for time (we don't include time with the rest of spatial data).
            if (variable.isSymbol()) if (VariableSymbol.TIME.equals(variable.getSymbol())) continue;
            // for each variable we recover the task
            AbstractTask completeTask = sedml.getTaskWithId(variable.getReference());
            if (completeTask == null) throw new RuntimeException("Null SedML task encountered");
            AbstractTask fundamentalTask = SpatialResultsConverter.getBaseTask(completeTask, sedml);
            // from the task we get the sbml model
            if (!(sedml.getSimulation(fundamentalTask.getSimulationReference()) instanceof UniformTimeCourse utcSim)){
                lg.error("only uniform time course simulations are supported");
                allVarsValid = false;
                break;
            }
            sedmlTaskToVCellSim.put(completeTask, completeSedmlTaskToVCellSim.get(completeTask));
        }
        if (allVarsValid){
            for (AbstractTask completeTask : sedmlTaskToVCellSim.keySet()){
                if (!sedmlTaskMap.containsKey(completeTask)) sedmlTaskMap.put(completeTask, new ArrayList<>());
                sedmlTaskMap.get(completeTask).add(dataGen);
            }
        }


        for (AbstractTask completeTask : sedmlTaskMap.keySet()){
            if (sourceOfTruth.getTaskGroupSet().contains(completeTask.getId()) && sedmlTaskToVCellSim.containsKey(completeTask)) continue;
            String format = "Was not able to find simulation data in VCell for task `%s` in sedml report `%s`";
            lg.warn(String.format(format, completeTask.getId(), report.getId()));
            return false;
        }

        for (AbstractTask completeTask : sedmlTaskMap.keySet()){
            TempSimulation vcellSimulation = sedmlTaskToVCellSim.get(completeTask);
            Hdf5DataSourceSpatialSimMetadata taskMetadata = sourceOfTruth.getMetadataFromTaskGroup(completeTask.getId());
            if (vcellSimulation.getMathOverrides().getScannedConstantNames().length != 0){
                if (taskMetadata.getScanTargets() == null){
                    taskMetadata.validateScanTargets(vcellSimulation.getMathOverrides().getScannedConstantNames());
                    convertedData.scanParameterNames = taskMetadata.getScanTargets();
                }
                if (taskMetadata.getScanValues() == null){
                    int[] eachBound = vcellSimulation.getMathOverrides().getScanBounds();
                    taskMetadata.validateScanBounds(eachBound);
                    convertedData.scanBounds = taskMetadata.getScanBounds();
                    convertedData.scanParameterValues = taskMetadata.getScanValues();
                    double[][] scanValues = new double[eachBound.length][];
                    for (int nameIndex = 0; nameIndex < eachBound.length; nameIndex++){
                        String nameKey = convertedData.scanParameterNames[nameIndex];
                        scanValues[nameIndex] = new double[eachBound[nameIndex] + 1];
                        for (int scanIndex = 0; scanIndex < eachBound[nameIndex] + 1; scanIndex++){
                            Expression overrideExp = vcellSimulation.getMathOverrides().getActualExpression(nameKey, scanIndex);
                            try { scanValues[nameIndex][scanIndex] = overrideExp.evaluateConstant(); }
                            catch (ExpressionException e){ throw new RuntimeException(e); }
                        }
                    }
                    taskMetadata.validateScanValues(scanValues);
                    convertedData.scanParameterValues = taskMetadata.getScanValues();
                }
            }
            Hdf5DataSourceSpatialSimVars taskVars = sourceOfTruth.getVarsFromTaskGroup(completeTask.getId());
            if (!convertedData.dataMapping.containsKey(report)) convertedData.dataMapping.put(report, new Hdf5SedmlResultsSpatial.SpatialComponents());
            Hdf5SedmlResultsSpatial.SpatialComponents comps = convertedData.dataMapping.get(report);
            if (comps.metadata == null) comps.metadata = taskMetadata;
            else if (!taskMetadata.equals(comps.metadata)) lg.warn("Unequal metadata encountered; this could be a sign something has gone wrong.");
            Hdf5DataSourceSpatialSimVars newVars = new Hdf5DataSourceSpatialSimVars();
            for (DataGenerator dataGenerator : sedmlTaskMap.get(completeTask)){
                Hdf5DataSourceSpatialSimVars taskGroupVars = sourceOfTruth.getVarsFromTaskGroup(completeTask.getId());
                for (Hdf5DataSourceSpatialVarDataLocation location : taskGroupVars.getLocations(dataGenerator))
                    newVars.addLocation(dataGenerator, location);
            }
            if (comps.varsToData == null) comps.varsToData = newVars;
            else comps.varsToData.integrateSimilarLocations(taskVars);
        }
        return true;
    }

    private static List<Report> getReports(List<Output> outputs){
        List<Report> reports = new LinkedList<>();
        for (Output out : outputs) {
            if (out instanceof Report report) reports.add(report);
            else lg.info("Ignoring unsupported output `" + out.getId() + "` when generating CSV.");
        } 
        return reports;
    }

    private static AbstractTask getBaseTask(AbstractTask task, SedML sedml){
        while (task instanceof RepeatedTask) { // We need to find the original task burried beneath.
            // We assume that we can never have a sequential repeated task at this point, we check for that in SEDMLImporter
            SubTask st = ((RepeatedTask)task).getSubTasks().entrySet().iterator().next().getValue(); // single subtask
            task = sedml.getTaskWithId(st.getTaskId());
        }
        return task;
    }

    private static String getKind(String prefixedSedmlId){
        String plotPrefix = "__plot__";
        if (prefixedSedmlId.startsWith(plotPrefix))
            return "SedPlot2D";
        return "SedReport";
    }

    private static boolean allTasksFound(Set<TaskJob> setToTest, Set<AbstractTask> setOfRequirements){
        Set<String> vcellSimIDs = setToTest.stream().map(TaskJob::getTaskId).collect(Collectors.toSet());
        Stream<String> reportSimIDs = setOfRequirements.stream().map(AbstractIdentifiableElement::getId);
        return reportSimIDs.filter(vcellSimIDs::contains).count() == setOfRequirements.size();
    }

    private static String convertSymbol(Variable var){
        // must get variable ID from SBML model
        if (var.getSymbol() != null) { // it is a predefined symbol
            // search the sbml model to find the vcell variable name associated with the run
            switch(var.getSymbol().name()){
                case "TIME": { // TIME is t, etc
                    return "t"; // this is VCell reserved symbol for time
                }
                default:{
                    return var.getSymbol().name();
                }
                // etc, TODO: check spec for other symbols (CSymbols?)
                // Delay? Avogadro? rateOf?
            }
        } else { // it is an XPATH target in model
            String target = var.getTarget();
            IXPathToVariableIDResolver resolver = new SBMLSupport();
            return resolver.getIdFromXPathIdentifer(target);
        }
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

//    public static Map<Report, List<Hdf5SedmlResults>> convertSpatialResultsToSedmlFormat(SedML sedml, Map<TaskJob, File> spatialResultsHash, Map<AbstractTask, TempSimulation> taskToSimulationMap, String sedmlLocation, String outDir) throws PythonStreamException {
//        Map<Report, List<Hdf5SedmlResults>> results = new LinkedHashMap<>();
//        List<Report> allReports = SpatialResultsConverter.getReports(sedml.getOutputs());
//
//        for (Report report : allReports){
//            boolean bNotSpatial = false, hasNotGivenWarning = true;
//            Hdf5SedmlResultsSpatial hdf5DataSourceSpatial = new Hdf5SedmlResultsSpatial();
//
//            // go through each entry (dataset)
//            for (DataSet dataset : report.getListOfDataSets()) {
//                // use the data reference to obtain the data generator
//                DataGenerator dataGen = sedml.getDataGeneratorWithId(dataset.getDataReference()); assert dataGen != null;
//                //TODO: Allow multiple variables in DataGenerators for Spatial, AND fill out TDDO below.
//                if (dataGen.getListOfVariables().size() != 1 && hasNotGivenWarning){
//                    logger.warn("VCell does not support multi-variable data generators in spatial sims.");
//                    hasNotGivenWarning = false;
//                }
//                Map<Variable, Hdf5DataSourceSpatialVarDataItem> variableToDataMap = new HashMap<>();
//
//                // get the list of variables associated with the data reference
//                for (Variable variable : dataGen.getListOfVariables()) {
//                    // for each variable we recover the task
//                    AbstractTask topLevelTask = sedml.getTaskWithId(variable.getReference());
//                    if (topLevelTask == null) throw new RuntimeException("Null SedML task encountered");
//                    AbstractTask baseTask = SpatialResultsConverter.getBaseTask(topLevelTask, sedml);
//                    // from the task we get the sbml model
//                    if (!(sedml.getSimulation(baseTask.getSimulationReference()) instanceof UniformTimeCourse utcSim)){
//                        logger.error("only uniform time course simulations are supported");
//                        continue;
//                    }
//
//                    String vcellVarId = convertToVCellSymbol(variable);
//
//                    boolean bFoundTaskInSpatial = spatialResultsHash.keySet().stream().anyMatch(taskJob -> taskJob.getTaskId().equals(topLevelTask.getId()));
//                    if (!bFoundTaskInSpatial){
//                        bNotSpatial = true;
//                        logger.warn("Was not able to find simulation data for task with ID: " + topLevelTask.getId());
//                        break;
//                    }
//
//                    // ==================================================================================
//
//
//                    ArrayList<TaskJob> taskJobs = new ArrayList<>();
//                    int[] scanBounds;
//                    String[] scanParamNames;
//
//                    if (topLevelTask instanceof RepeatedTask) {
//                        for (Map.Entry<TaskJob, File> entry : spatialResultsHash.entrySet()) {
//                            TaskJob taskJob = entry.getKey();
//                            if (entry.getValue() != null && taskJob.getTaskId().equals(topLevelTask.getId())) {
//                                taskJobs.add(taskJob);
//                            }
//                        }
//                        scanBounds = taskToSimulationMap.get(topLevelTask).getMathOverrides().getScanBounds();
//                        scanParamNames = taskToSimulationMap.get(topLevelTask).getMathOverrides().getScannedConstantNames();
//                    } else { // Not repeated Tasks
//                        taskJobs.add(new TaskJob(baseTask.getId(), 0));
//                        scanBounds = new int[0];
//                        scanParamNames = new String[0];
//                    }
//
//                    for (Map.Entry<TaskJob, File> entry : spatialResultsHash.entrySet()) {
//                        TaskJob taskJob = entry.getKey();
//                        if (entry.getValue() != null && taskJob.getTaskId().equals(topLevelTask.getId())) {
//                            taskJobs.add(taskJob);
//                            if (topLevelTask instanceof RepeatedTask)
//                                break; // No need to keep looking if it's not a repeated task
//                        }
//                    }
//
//                    if (taskJobs.isEmpty()) continue;
//
//                    int outputNumberOfPoints = utcSim.getNumberOfPoints();
//                    double outputStartTime = utcSim.getOutputStartTime();
//
//                    int jobIndex = 0;
//                    for (TaskJob taskJob : taskJobs) {
//                        File spatialH5File = spatialResultsHash.get(taskJob);
//                        if (spatialH5File != null) {
//                            Hdf5DataSourceSpatialVarDataItem job;
//                            try {
//                                job = new Hdf5DataSourceSpatialVarDataItem(
//                                        report, dataset, variable, jobIndex, spatialH5File,
//                                        outputStartTime, outputNumberOfPoints, vcellVarId);
//                            } catch (MissingDataException e) {
//                                String warningMessage = "Resulting HDF5 will not be complete as requested: ";
//                                if (logger.isDebugEnabled()) logger.warn(warningMessage, e);
//                                else logger.warn(warningMessage + e.getMessage());
//                                continue;
//                            }
//                            if (job.spaceTimeDimensions == null){
//                                throw new RuntimeException("Unable to find dimensionality data for " + vcellVarId);
//                            }
//                            variableToDataMap.put(variable, job);
//                            hdf5DataSourceSpatial.scanBounds = scanBounds;
//                            hdf5DataSourceSpatial.scanParameterNames = scanParamNames;
//                        }
//                        jobIndex++;
//                    }
//                }
//                // TODO: Data generator logic (to resolve multiple variables) goes here...
//                for (Variable nextVariable : variableToDataMap.keySet()) { // ...Modify this loop!!
//                    hdf5DataSourceSpatial.dataItems.put(report, dataset, variableToDataMap.get(nextVariable));
//                }
//                PythonCalls.updateDatasetStatusYml(sedmlLocation, report.getId(), dataset.getId(), Status.SUCCEEDED, outDir);
//            } // end of dataset
//
//            if (bNotSpatial || hdf5DataSourceSpatial.dataItems.isEmpty()){
//                logger.warn("We encountered non-compatible (or non-existent) data. " +
//                        "This may mean a problem has been encountered.");
//                continue;
//            }
//
//            Hdf5SedmlResults hdf5DatasetWrapper = new Hdf5SedmlResults();
//            hdf5DatasetWrapper.dataSource = hdf5DataSourceSpatial;
//            hdf5DatasetWrapper.datasetMetadata._type = SpatialResultsConverter.getKind(report.getId());
//            hdf5DatasetWrapper.datasetMetadata.sedmlId = SpatialResultsConverter.removeVCellPrefixes(report.getId(), report.getId());
//            hdf5DatasetWrapper.datasetMetadata.sedmlName = report.getName();
//            hdf5DatasetWrapper.datasetMetadata.uri = Paths.get(sedmlLocation, report.getId()).toString();
//
//            hdf5DatasetWrapper.dataSource = hdf5DataSourceSpatial;
//            Map<DataSet, Hdf5DataSourceSpatialVarDataItem> reportMappings = hdf5DataSourceSpatial.dataItems.getDataSetMappings(report);
//            for (DataSet jobDataSet : reportMappings.keySet()){
//                Hdf5DataSourceSpatialVarDataItem job = reportMappings.get(jobDataSet);
//                String dimensionLabelString = "[" + "XYZ".substring(0, job.spaceTimeDimensions.length - 1) + "T]";
//                VariableSymbol symbol = job.sedmlVariable.getSymbol();
//                if (symbol != null && "TIME".equals(symbol.name())) continue; // Skip time, no need for n-dimensional duplicated time-centric hdf5 dataset for spatial.
//                DataSet dataSet = job.sedmlDataset;
//                hdf5DatasetWrapper.datasetMetadata.sedmlDataSetDataTypes.add("float64");
//                hdf5DatasetWrapper.datasetMetadata.sedmlDataSetIds.add(
//                        SpatialResultsConverter.removeVCellPrefixes(dataSet.getId(), hdf5DatasetWrapper.datasetMetadata.sedmlId));
//                hdf5DatasetWrapper.datasetMetadata.sedmlDataSetLabels.add(dataSet.getLabel() + dimensionLabelString);
//                hdf5DatasetWrapper.datasetMetadata.sedmlDataSetNames.add(dataSet.getName() + dimensionLabelString);
//                List<Integer> shapes = new LinkedList<>();
//                if (hdf5DataSourceSpatial.scanBounds.length > 0)
//                    shapes.add(hdf5DataSourceSpatial.scanBounds[hdf5DataSourceSpatial.scanBounds.length - 1]);
//                for (int size : job.spaceTimeDimensions) shapes.add(size);
//                hdf5DatasetWrapper.datasetMetadata.sedmlDataSetShapes.add(shapes.toString());
//            }
//            if (!results.containsKey(report)) results.put(report, new LinkedList<>());
//            results.get(report).add(hdf5DatasetWrapper);
//        } // outputs/reports
//        return results;
//    }
}
