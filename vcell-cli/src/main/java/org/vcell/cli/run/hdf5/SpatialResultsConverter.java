package org.vcell.cli.run.hdf5;


import cbit.vcell.parser.ExpressionException;
import cbit.vcell.solver.TempSimulation;
import ncsa.hdf.hdf5lib.exceptions.HDF5Exception;

import org.jlibsedml.*;
import org.jlibsedml.execution.IXPathToVariableIDResolver;
import org.jlibsedml.modelsupport.SBMLSupport;
import org.vcell.cli.PythonStreamException;
import org.vcell.cli.run.PythonCalls;
import org.vcell.cli.run.Status;
import org.vcell.cli.run.TaskJob;
import org.vcell.util.DataAccessException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.file.Paths;
import java.util.*;
public class SpatialResultsConverter {
    private final static Logger logger = LogManager.getLogger(SpatialResultsConverter.class);

    public static Map<Report, List<Hdf5SedmlResults>> convertSpatialResultsToSedmlFormat(SedML sedml, Map<TaskJob, File> spatialResultsHash, Map<AbstractTask, TempSimulation> taskToSimulationMap, String sedmlLocation, String outDir) throws DataAccessException, IOException, HDF5Exception, ExpressionException, PythonStreamException {
        Map<Report, List<Hdf5SedmlResults>> results = new LinkedHashMap<>();
        List<Report> allReports = SpatialResultsConverter.getReports(sedml.getOutputs());

        for (Report report : allReports){
            boolean bNotSpatial = false, hasNotGivenWarning = true;
            Hdf5SedmlResultsSpatial hdf5DataSourceSpatial = new Hdf5SedmlResultsSpatial();

            // go through each entry (dataset)
            for (DataSet dataset : report.getListOfDataSets()) { 
                // use the data reference to obtain the data generator
                DataGenerator datagen = sedml.getDataGeneratorWithId(dataset.getDataReference()); assert datagen != null;
                //TODO: Allow multiple variables in DataGenerators for Spatial, AND fill out TDDO below.
                if (datagen.getListOfVariables().size() != 1 && hasNotGivenWarning){
                    logger.warn("VCell does not support multi-variable data generators in spatial sims.");
                    hasNotGivenWarning = false;
                }
                Map<Variable, Hdf5DataSourceSpatialVarDataItem> variableToDataMap = new HashMap<>();

                // get the list of variables associated with the data reference
                for (Variable var : datagen.getListOfVariables()) {
                    // for each variable we recover the task
                    AbstractTask topLevelTask = sedml.getTaskWithId(var.getReference());
                    AbstractTask baseTask = SpatialResultsConverter.getBaseTask(topLevelTask, sedml);
                    // from the task we get the sbml model
                    org.jlibsedml.Simulation sedmlSim = sedml.getSimulation(baseTask.getSimulationReference());

                    if (!(sedmlSim instanceof UniformTimeCourse)){
                        logger.error("only uniform time course simulations are supported");
                        continue;
                    }

                    String vcellVarId = convertToVCellSymbol(var);

                    boolean bFoundTaskInSpatial = spatialResultsHash.keySet().stream().anyMatch(taskJob -> taskJob.getTaskId().equals(topLevelTask.getId()));
                    if (!bFoundTaskInSpatial){
                        bNotSpatial = true;
                        logger.warn("Was not able to find simulation data for task with ID: " + topLevelTask.getId());
                        break;
                    }

                    // ==================================================================================


                    ArrayList<TaskJob> taskJobs = new ArrayList<>();
                    int[] scanBounds;
                    String[] scanParamNames;

                    if (topLevelTask instanceof RepeatedTask) {
                        for (Map.Entry<TaskJob, File> entry : spatialResultsHash.entrySet()) {
                            TaskJob taskJob = entry.getKey();
                            if (entry.getValue() != null && taskJob.getTaskId().equals(topLevelTask.getId())) {
                                taskJobs.add(taskJob);
                            }
                        }
                        scanBounds = taskToSimulationMap.get(topLevelTask).getMathOverrides().getScanBounds();
                        scanParamNames = taskToSimulationMap.get(topLevelTask).getMathOverrides().getScannedConstantNames();
                    } else { // Not repeated Tasks
                        taskJobs.add(new TaskJob(baseTask.getId(), 0));
                        scanBounds = new int[0];
                        scanParamNames = new String[0];
                    }

                    for (Map.Entry<TaskJob, File> entry : spatialResultsHash.entrySet()) {
                        TaskJob taskJob = entry.getKey();
                        if (entry.getValue() != null && taskJob.getTaskId().equals(topLevelTask.getId())) {
                            taskJobs.add(taskJob);
                            if (topLevelTask instanceof RepeatedTask)
                                break; // No need to keep looking if it's not a repeated task
                        }
                    }

                    if (taskJobs.isEmpty()) continue;

                    int outputNumberOfPoints = ((UniformTimeCourse) sedmlSim).getNumberOfPoints();
                    double outputStartTime = ((UniformTimeCourse) sedmlSim).getOutputStartTime();

                    int jobIndex = 0;
                    for (TaskJob taskJob : taskJobs) {
                        File spatialH5File = spatialResultsHash.get(taskJob);
                        if (spatialH5File != null) {
                            Hdf5DataSourceSpatialVarDataItem job = new Hdf5DataSourceSpatialVarDataItem(
                                    report, dataset, var, jobIndex, spatialH5File,
                                    outputStartTime, outputNumberOfPoints, vcellVarId);
                            if (job.spaceTimeDimensions == null){
                                throw new RuntimeException("Unable to find dimensionality data for " + vcellVarId);
                            }
                            variableToDataMap.put(var, job);
                            hdf5DataSourceSpatial.scanBounds = scanBounds;
                            hdf5DataSourceSpatial.scanParameterNames = scanParamNames;
                        }
                        jobIndex++;
                    }
                }
                // TODO: Data generator logic (to resolve multiple variables) goes here...
                for (Variable var : variableToDataMap.keySet()) { // ...Modify this loop!!
                    hdf5DataSourceSpatial.dataItems.put(report, dataset, variableToDataMap.get(var));
                }
                PythonCalls.updateDatasetStatusYml(sedmlLocation, report.getId(), dataset.getId(), Status.SUCCEEDED, outDir);
            } // end of dataset

            if (bNotSpatial || hdf5DataSourceSpatial.dataItems.isEmpty()){
                logger.warn("We encountered non-compatible (or non-existent) data. " +
                        "This may mean a problem has been encountered.");
                continue;
            }

            Hdf5SedmlResults hdf5DatasetWrapper = new Hdf5SedmlResults();
            hdf5DatasetWrapper.dataSource = hdf5DataSourceSpatial;
            hdf5DatasetWrapper.datasetMetadata._type = SpatialResultsConverter.getKind(report.getId());
            hdf5DatasetWrapper.datasetMetadata.sedmlId = SpatialResultsConverter.removeVCellPrefixes(report.getId(), report.getId());
            hdf5DatasetWrapper.datasetMetadata.sedmlName = report.getName();
            hdf5DatasetWrapper.datasetMetadata.uri = Paths.get(sedmlLocation, report.getId()).toString();

            hdf5DatasetWrapper.dataSource = hdf5DataSourceSpatial;
            Map<DataSet, Hdf5DataSourceSpatialVarDataItem> reportMappings = hdf5DataSourceSpatial.dataItems.getDataSetMappings(report);
            for (DataSet jobDataSet : reportMappings.keySet()){
                Hdf5DataSourceSpatialVarDataItem job = reportMappings.get(jobDataSet);
                String dimensionLabelString = "[" + "XYZ".substring(0, job.spaceTimeDimensions.length - 1) + "T]";
                VariableSymbol symbol = job.sedmlVariable.getSymbol();
                if (symbol != null && "TIME".equals(symbol.name())) continue; // Skip time, no need for n-dimensional duplicated time-centric hdf5 dataset for spatial.
                DataSet dataSet = job.sedmlDataset;
                hdf5DatasetWrapper.datasetMetadata.sedmlDataSetDataTypes.add("float64");
                hdf5DatasetWrapper.datasetMetadata.sedmlDataSetIds.add(
                    SpatialResultsConverter.removeVCellPrefixes(dataSet.getId(), hdf5DatasetWrapper.datasetMetadata.sedmlId));
                hdf5DatasetWrapper.datasetMetadata.sedmlDataSetLabels.add(dataSet.getLabel() + dimensionLabelString);
                hdf5DatasetWrapper.datasetMetadata.sedmlDataSetNames.add(dataSet.getName() + dimensionLabelString);
                List<Integer> shapes = new LinkedList<>();
                if (hdf5DataSourceSpatial.scanBounds.length > 0)
                    shapes.add(hdf5DataSourceSpatial.scanBounds[hdf5DataSourceSpatial.scanBounds.length - 1]);
                for (int size : job.spaceTimeDimensions) shapes.add(size);
                hdf5DatasetWrapper.datasetMetadata.sedmlDataSetShapes.add(shapes.toString());
            }
            if (!results.containsKey(report)) results.put(report, new LinkedList<>());
            results.get(report).add(hdf5DatasetWrapper);
        } // outputs/reports
        return results;
    }

    private static List<Report> getReports(List<Output> outputs){
        List<Report> reports = new LinkedList<>();
        for (Output out : outputs) {
            if (out instanceof Report){
                reports.add((Report)out);
            } else {
                logger.info("Ignoring unsupported output `" + out.getId() + "` while CSV generation.");
            }
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

    private static String convertToVCellSymbol(Variable var){
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
}
