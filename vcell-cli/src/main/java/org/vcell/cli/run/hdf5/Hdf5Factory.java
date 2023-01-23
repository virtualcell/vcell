package org.vcell.cli.run.hdf5;

import cbit.vcell.solver.Simulation;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.solver.ode.ODESolverResultSet;
import ncsa.hdf.hdf5lib.exceptions.HDF5Exception;
//import org.jlibsedml.*;
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
//import org.jlibsedml.Simulation;
import org.jlibsedml.execution.IXPathToVariableIDResolver;
import org.jlibsedml.modelsupport.SBMLSupport;
import org.vcell.cli.run.TaskJob;
import org.vcell.util.BeanUtils;
import org.vcell.util.DataAccessException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.*;


public class Hdf5Factory {
    private SedML sedml;
    private Map<AbstractTask, Simulation> taskToSimulationMap;
    private String sedmlLocation, nameOfSedmlOutDir;

    private final static Logger logger = LogManager.getLogger(Hdf5Factory.class);

    public Hdf5Factory(String nameOfSedmlOutDir, SedML sedml, Map<AbstractTask, Simulation> taskToSimulationMap, String sedmlLocation){
        this.nameOfSedmlOutDir = nameOfSedmlOutDir;
        this.sedml = sedml;
        this.taskToSimulationMap = taskToSimulationMap;
        this.sedmlLocation = sedmlLocation;
    }

    public Hdf5FileWrapper generateHdf5File(Map<TaskJob, ODESolverResultSet> nonSpatialResults, Map<TaskJob, File> spatialResults) throws HDF5Exception, ExpressionException, DataAccessException, IOException {
        Hdf5FileWrapper hdf5FileWrapper = new Hdf5FileWrapper();

        hdf5FileWrapper.combineArchiveLocation = nameOfSedmlOutDir;
        hdf5FileWrapper.uri = nameOfSedmlOutDir;

        hdf5FileWrapper.datasetWrappers.addAll(this.collectNonspatialDatasets(this.sedml, nonSpatialResults, this.taskToSimulationMap, this.sedmlLocation));
        hdf5FileWrapper.datasetWrappers.addAll(this.collectSpatialDatasets(this.sedml, spatialResults, this.taskToSimulationMap, this.sedmlLocation));

        return hdf5FileWrapper;
    }

    public List<Hdf5DatasetWrapper> collectNonspatialDatasets(SedML sedml, Map<TaskJob, ODESolverResultSet> nonspatialResultsHash, Map<AbstractTask, Simulation> taskToSimulationMap, String sedmlLocation) throws DataAccessException, IOException, HDF5Exception, ExpressionException {
        List<Hdf5DatasetWrapper> datasetWrappers = new ArrayList<>();

        for (Report report : this.getReports(sedml.getOutputs())){
            Map<DataSet, Map<Variable, NonspatialValueHolder>> dataSetValues = new LinkedHashMap<>();

            logger.info("Generating report `" + report.getId() + "`.");

            // go through each entry (dataset)
            for (DataSet dataset : report.getListOfDataSets()) { 
                
                List<String> varIDs = new ArrayList<>();
                Map<Variable, NonspatialValueHolder> values = new HashMap<>();
                int maxLengthOfAllData = 0; // We have to pad up to this value

                // use the data reference to obtain the data generator
                DataGenerator datagen = sedml.getDataGeneratorWithId(dataset.getDataReference()); assert datagen != null;
                
                // get the list of variables associated with the data reference
                for (Variable var : datagen.getListOfVariables()) {
                    // for each variable we recover the task
                    AbstractTask initialTask = sedml.getTaskWithId(var.getReference());
                    AbstractTask referredTask = this.getOriginalTask(initialTask);
                    
                    // from the task we get the sbml model
                    org.jlibsedml.Simulation sedmlSim = sedml.getSimulation(((Task)referredTask).getSimulationReference());

                    
                    // must get variable ID from SBML model
                    String sbmlVarId = getSbmlVarId(var);

                    boolean bFoundTaskInNonspatial = nonspatialResultsHash.keySet().stream().anyMatch(taskJob -> taskJob.getTaskId().equals(initialTask.getId()));
                    if (!bFoundTaskInNonspatial){
                        break;
                    }

                    // ==================================================================================
                    
                    ArrayList<TaskJob> taskJobs = new ArrayList<>();

                    for (Map.Entry<TaskJob, ODESolverResultSet> entry : nonspatialResultsHash.entrySet()) {
                        TaskJob taskJob = entry.getKey();
                        if (entry.getValue() != null && taskJob.getTaskId().equals(initialTask.getId())) {
                            taskJobs.add(taskJob);
                            if (!(initialTask instanceof RepeatedTask)) 
                                break; // No need to keep looking if its not a repeated task
                        }
                    }

                    if (taskJobs.isEmpty()) continue;

                    varIDs.add(var.getId());

                    if (!(sedmlSim instanceof UniformTimeCourse)){
                        logger.error("only uniform time course simulations are supported");
                        continue;
                    }

                    // we want to keep the last outputNumberOfPoints only
                    int outputNumberOfPoints = ((UniformTimeCourse) sedmlSim).getNumberOfPoints();
                    double outputStartTime = ((UniformTimeCourse) sedmlSim).getOutputStartTime();
                    NonspatialValueHolder variablesList;

                    for (TaskJob taskJob : taskJobs) {
                        ODESolverResultSet results = nonspatialResultsHash.get(taskJob);
                        int column = results.findColumn(sbmlVarId);
                        double[] data = results.extractColumn(column);
                        
                        if (outputStartTime > 0){
                            double[] correctiveData = new double[outputNumberOfPoints + 1];
                            for (int i = data.length - outputNumberOfPoints - 1, j = 0; i < data.length; i++, j++) {
                                correctiveData[j] = data[i];
                            }
                            data = correctiveData;
                        }

                        maxLengthOfAllData = Integer.max(maxLengthOfAllData, data.length);
                        if (initialTask instanceof RepeatedTask && values.containsKey(var)) { // double[] exists
                            variablesList = values.get(var);
                        } else { // this is the first double[]
                            variablesList = new NonspatialValueHolder(taskToSimulationMap.get(initialTask));
                        }
                        variablesList.values.add(data);
                        values.put(var, variablesList);
                    }
                }
                dataSetValues.put(dataset, values);

            } // end of dataset

            Hdf5DatasetWrapper hdf5DatasetWrapper = new Hdf5DatasetWrapper();
            hdf5DatasetWrapper.datasetMetadata._type = report.getKind();
            hdf5DatasetWrapper.datasetMetadata.sedmlId = report.getId();
            hdf5DatasetWrapper.datasetMetadata.sedmlName = report.getName();
            hdf5DatasetWrapper.datasetMetadata.uri = sedmlLocation;

            Map<Variable, NonspatialValueHolder> firstValues = dataSetValues.entrySet().iterator().next().getValue();
            if (firstValues.size()==0){
                continue;
            }
            NonspatialValueHolder valuesForFirstVar = firstValues.entrySet().iterator().next().getValue();
            int numJobs = valuesForFirstVar.getNumJobs();

            Hdf5DataSourceNonspatial dataSourceNonspatial = new Hdf5DataSourceNonspatial();
            hdf5DatasetWrapper.dataSource = dataSourceNonspatial;
            for (int i=0; i<numJobs; i++) {
                dataSourceNonspatial.jobData.add(new Hdf5DataSourceNonspatial.Hdf5JobData());
            }

            for (Map.Entry<DataSet, Map<Variable, NonspatialValueHolder>> dataSetEntry : dataSetValues.entrySet()){
                DataSet dataSet = dataSetEntry.getKey();
                Map<Variable, NonspatialValueHolder> values = dataSetEntry.getValue();

                for (Map.Entry<Variable, NonspatialValueHolder> varEntry : values.entrySet()){
                    Variable var = varEntry.getKey();
                    NonspatialValueHolder dataArrays = varEntry.getValue();
                    dataSourceNonspatial.scanBounds = dataArrays.vcSimulation.getMathOverrides().getScanBounds();
                    dataSourceNonspatial.scanParameterNames = dataArrays.vcSimulation.getMathOverrides().getScannedConstantNames();
                    for (int jobIndex=0; jobIndex<numJobs; jobIndex++){
                        double[] data = dataArrays.values.get(jobIndex);
                        Hdf5DataSourceNonspatial.Hdf5JobData hdf5JobData = dataSourceNonspatial.jobData.get(jobIndex);
                        hdf5JobData.varData.put(var,data);
                    }
                }
                hdf5DatasetWrapper.datasetMetadata.sedmlDataSetDataTypes.add("float64");
                hdf5DatasetWrapper.datasetMetadata.sedmlDataSetIds.add(dataSet.getId());
                hdf5DatasetWrapper.datasetMetadata.sedmlDataSetLabels.add(dataSet.getLabel());
                hdf5DatasetWrapper.datasetMetadata.sedmlDataSetNames.add(dataSet.getName());
            }
            hdf5DatasetWrapper.datasetMetadata.sedmlDataSetShapes = null;
            datasetWrappers.add(hdf5DatasetWrapper);
        } // outputs/reports
        return datasetWrappers;
    }


    public List<Hdf5DatasetWrapper> collectSpatialDatasets(SedML sedml, Map<TaskJob, File> spatialResultsHash, Map<AbstractTask, Simulation> taskToSimulationMap, String sedmlLocation) throws DataAccessException, IOException, HDF5Exception, ExpressionException {
        List<Hdf5DatasetWrapper> datasetWrappers = new ArrayList<>();
        
        for (Report report : this.getReports(sedml.getOutputs())){
            boolean bNotSpatial = false;
            Hdf5DataSourceSpatial hdf5DataSourceSpatial = new Hdf5DataSourceSpatial();

            logger.info("Generating report `" + report.getId() + "`.");

            // go through each entry (dataset)
            for (DataSet dataset : report.getListOfDataSets()) { 
                // use the data reference to obtain the data generator
                DataGenerator datagen = sedml.getDataGeneratorWithId(dataset.getDataReference()); assert datagen != null;
                
                // get the list of variables associated with the data reference
                for (Variable var : datagen.getListOfVariables()) {
                    // for each variable we recover the task
                    AbstractTask initialTask = sedml.getTaskWithId(var.getReference());
                    AbstractTask referredTask = this.getOriginalTask(initialTask);
                    // from the task we get the sbml model
                    org.jlibsedml.Simulation sedmlSim = sedml.getSimulation(((Task)referredTask).getSimulationReference());

                    boolean bFoundTaskInSpatial = spatialResultsHash.keySet().stream().anyMatch(taskJob -> taskJob.getTaskId().equals(initialTask.getId()));
                    if (!bFoundTaskInSpatial){
                        bNotSpatial = true;
                        break;
                    }

                    // ==================================================================================
                    if (!(sedmlSim instanceof UniformTimeCourse)){
                        logger.error("only uniform time course simulations are supported");
                        continue;
                    }

                    ArrayList<TaskJob> taskJobs = new ArrayList<>();
                    int[] scanBounds;
                    String[] scanParamNames;

                    if (initialTask instanceof RepeatedTask) {
                        for (Map.Entry<TaskJob, File> entry : spatialResultsHash.entrySet()) {
                            TaskJob taskJob = entry.getKey();
                            if (entry.getValue() != null && taskJob.getTaskId().equals(initialTask.getId())) {
                                taskJobs.add(taskJob);
                            }
                        }
                        scanBounds = taskToSimulationMap.get(initialTask).getMathOverrides().getScanBounds();
                        scanParamNames = taskToSimulationMap.get(initialTask).getMathOverrides().getScannedConstantNames();
                    } else { // Repeated Tasks
                        taskJobs.add(new TaskJob(referredTask.getId(), 0));
                        scanBounds = new int[0];
                        scanParamNames = new String[0];
                    }

                    int outputNumberOfPoints = ((UniformTimeCourse) sedmlSim).getNumberOfPoints();
                    double outputStartTime = ((UniformTimeCourse) sedmlSim).getOutputStartTime();
                    int jobIndex=0;
                    for (TaskJob taskJob : taskJobs) {
                        File spatialH5File = spatialResultsHash.get(taskJob);
                        if (spatialH5File!=null) {
                            Hdf5DataSourceSpatialVarDataItem job = new Hdf5DataSourceSpatialVarDataItem(
                                    report, dataset, var, jobIndex, spatialH5File, outputStartTime, outputNumberOfPoints);
                            hdf5DataSourceSpatial.varDataItems.add(job);
                            hdf5DataSourceSpatial.scanBounds = scanBounds;
                            hdf5DataSourceSpatial.scanParameterNames = scanParamNames;
                        }
                        jobIndex++;
                    }
                }
            } // end of dataset

            if (bNotSpatial || hdf5DataSourceSpatial.varDataItems.size()==0){
                continue;
            }

            Hdf5DatasetWrapper hdf5DatasetWrapper = new Hdf5DatasetWrapper();
            hdf5DatasetWrapper.dataSource = hdf5DataSourceSpatial;
            hdf5DatasetWrapper.datasetMetadata._type = report.getKind();
            hdf5DatasetWrapper.datasetMetadata.sedmlId = report.getId();
            hdf5DatasetWrapper.datasetMetadata.sedmlName = report.getName();
            hdf5DatasetWrapper.datasetMetadata.uri = sedmlLocation;

            hdf5DatasetWrapper.dataSource = hdf5DataSourceSpatial;
            for (Hdf5DataSourceSpatialVarDataItem job : hdf5DataSourceSpatial.varDataItems){
                DataSet dataSet = job.sedmlDataset;
                hdf5DatasetWrapper.datasetMetadata.sedmlDataSetDataTypes.add("float64");
                hdf5DatasetWrapper.datasetMetadata.sedmlDataSetIds.add(dataSet.getId());
                hdf5DatasetWrapper.datasetMetadata.sedmlDataSetLabels.add(dataSet.getLabel());
                hdf5DatasetWrapper.datasetMetadata.sedmlDataSetNames.add(dataSet.getName());
            }
            hdf5DatasetWrapper.datasetMetadata.sedmlDataSetShapes = null;
            datasetWrappers.add(hdf5DatasetWrapper);
        } // outputs/reports
        return datasetWrappers;
    }

    private List<Report> getReports(List<Output> outputs){
        List<Report> reports = new LinkedList<>();
        for (Output out : sedml.getOutputs()) {
            if (out instanceof Report){
                reports.add((Report)out);
            } else {
                logger.info("Ignoring unsupported output `" + out.getId() + "` while CSV generation.");
            }
        } 
        return reports;
    }

    private AbstractTask getOriginalTask(AbstractTask task){
        while (task instanceof RepeatedTask) { // We need to find the original task burried beneath.
            // We assume that we can never have a sequential repeated task at this point, we check for that in SEDMLImporter
            SubTask st = ((RepeatedTask)task).getSubTasks().entrySet().iterator().next().getValue(); // single subtask
            task = sedml.getTaskWithId(st.getTaskId());
        }
        return task;
    }

    private String getSbmlVarId(Variable var){
         // must get variable ID from SBML model
         String sbmlVarId = "";
         if (var.getSymbol() != null) { // it is a predefined symbol
             // search the sbml model to find the vcell variable name associated with the run
             switch(var.getSymbol().name()){
                 case "TIME": { // TIME is t, etc
                     sbmlVarId = "t"; // this is VCell reserved symbold for time
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

    private static class NonspatialValueHolder {
        List<double[]> values = new ArrayList<>();
        final Simulation vcSimulation;

        public NonspatialValueHolder(Simulation simulation) {
            this.vcSimulation = simulation;
        }

        public int getNumJobs() {
            return values.size();
        }

        public int[] getJobCoordinate(int index){
            String[] names = vcSimulation.getMathOverrides().getScannedConstantNames();
            java.util.Arrays.sort(names); // must do things in a consistent way
            int[] bounds = new int[names.length]; // bounds of scanning matrix
            for (int i = 0; i < names.length; i++){
                bounds[i] = vcSimulation.getMathOverrides().getConstantArraySpec(names[i]).getNumValues() - 1;
            }
            int[] coordinates = BeanUtils.indexToCoordinate(index, bounds);
            return coordinates;
        }
    }
}
