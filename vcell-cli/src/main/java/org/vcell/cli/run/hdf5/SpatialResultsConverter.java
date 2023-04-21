package org.vcell.cli.run.hdf5;

import cbit.vcell.solver.Simulation;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.solver.TempSimulation;
import ncsa.hdf.hdf5lib.exceptions.HDF5Exception;

import org.jlibsedml.SedML;
import org.jlibsedml.AbstractTask;
import org.jlibsedml.Output;
import org.jlibsedml.Report;
import org.jlibsedml.Variable;
import org.jlibsedml.DataGenerator;
import org.jlibsedml.RepeatedTask;
import org.jlibsedml.Task;
import org.jlibsedml.SubTask;
import org.jlibsedml.UniformTimeCourse;
import org.jlibsedml.DataSet;
import org.vcell.cli.run.TaskJob;
import org.vcell.util.DataAccessException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.file.Paths;
import java.util.*;
public class SpatialResultsConverter {
    private final static Logger logger = LogManager.getLogger(SpatialResultsConverter.class);

    public static List<Hdf5SedmlResults> convertSpatialResultsToSedmlFormat(SedML sedml, Map<TaskJob, File> spatialResultsHash, Map<AbstractTask, TempSimulation> taskToSimulationMap, String sedmlLocation) throws DataAccessException, IOException, HDF5Exception, ExpressionException {
        List<Hdf5SedmlResults> datasetWrappers = new ArrayList<>();

        for (Report report : SpatialResultsConverter.getReports(sedml.getOutputs())){
            boolean bNotSpatial = false;
            Hdf5SedmlResultsSpatial hdf5DataSourceSpatial = new Hdf5SedmlResultsSpatial();

            // go through each entry (dataset)
            for (DataSet dataset : report.getListOfDataSets()) { 
                // use the data reference to obtain the data generator
                DataGenerator datagen = sedml.getDataGeneratorWithId(dataset.getDataReference()); assert datagen != null;
                
                // get the list of variables associated with the data reference
                for (Variable var : datagen.getListOfVariables()) {
                    // for each variable we recover the task
                    AbstractTask topLevelTask = sedml.getTaskWithId(var.getReference());
                    AbstractTask baseTask = SpatialResultsConverter.getBaseTask(topLevelTask, sedml);
                    // from the task we get the sbml model
                    org.jlibsedml.Simulation sedmlSim = sedml.getSimulation(((Task)baseTask).getSimulationReference());

                    if (!(sedmlSim instanceof UniformTimeCourse)){
                        logger.error("only uniform time course simulations are supported");
                        continue;
                    }

                    // Do we need Vcell/sbml name conversion here??

                    boolean bFoundTaskInSpatial = spatialResultsHash.keySet().stream().anyMatch(taskJob -> taskJob.getTaskId().equals(topLevelTask.getId()));
                    if (!bFoundTaskInSpatial){
                        bNotSpatial = true;
                        logger.warn("Was not able to find simulation data for task with ID: " + topLevelTask.getId());
                        break;
                    }

                    // ==================================================================================


                    ArrayList<TaskJob> taskJobs = new ArrayList<>();
                    int[] scanBounds = new int[0];
                    String[] scanParamNames = new String[0];

                    /*
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
                    }*/

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
                                    report, dataset, var, jobIndex, spatialH5File, outputStartTime, outputNumberOfPoints);
                            hdf5DataSourceSpatial.varDataItems.add(job);
                            hdf5DataSourceSpatial.scanBounds = scanBounds;
                            hdf5DataSourceSpatial.scanParameterNames = scanParamNames;
                        }
                        jobIndex++;
                    }
                }
                // TODO: Data generator logic goes here
            } // end of dataset

            if (bNotSpatial || hdf5DataSourceSpatial.varDataItems.isEmpty()){
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
            for (Hdf5DataSourceSpatialVarDataItem job : hdf5DataSourceSpatial.varDataItems){
                DataSet dataSet = job.sedmlDataset;
                hdf5DatasetWrapper.datasetMetadata.sedmlDataSetDataTypes.add("float64");
                hdf5DatasetWrapper.datasetMetadata.sedmlDataSetIds.add(
                    SpatialResultsConverter.removeVCellPrefixes(dataSet.getId(), hdf5DatasetWrapper.datasetMetadata.sedmlId));
                hdf5DatasetWrapper.datasetMetadata.sedmlDataSetLabels.add(dataSet.getLabel());
                hdf5DatasetWrapper.datasetMetadata.sedmlDataSetNames.add(dataSet.getName());
                hdf5DatasetWrapper.datasetMetadata.sedmlDataSetShapes.add(null);
            }
            hdf5DatasetWrapper.datasetMetadata.sedmlDataSetShapes = null;
            datasetWrappers.add(hdf5DatasetWrapper);
        } // outputs/reports
        return datasetWrappers;
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
