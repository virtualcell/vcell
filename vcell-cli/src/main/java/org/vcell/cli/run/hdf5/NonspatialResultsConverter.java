package org.vcell.cli.run.hdf5;

import cbit.vcell.solver.Simulation;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.solver.TempSimulation;
import cbit.vcell.solver.ode.ODESolverResultSet;
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
import org.jlibsedml.execution.IXPathToVariableIDResolver;
import org.jlibsedml.modelsupport.SBMLSupport;
import org.vcell.cli.run.TaskJob;
import org.vcell.util.DataAccessException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.file.Paths;
import java.util.*;
public class NonspatialResultsConverter {
    private final static Logger logger = LogManager.getLogger(NonspatialResultsConverter.class);


    public static List<Hdf5SedmlResults> convertNonspatialResultsToSedmlFormat(SedML sedml, Map<TaskJob, ODESolverResultSet> nonspatialResultsHash, Map<AbstractTask, TempSimulation> taskToSimulationMap, String sedmlLocation) throws DataAccessException, IOException, HDF5Exception, ExpressionException {
        List<Hdf5SedmlResults> datasetWrappers = new ArrayList<>();

        for (Report report : NonspatialResultsConverter.getReports(sedml.getOutputs())){
            Map<DataSet, NonspatialValueHolder> dataSetValues = new LinkedHashMap<>();

            // go through each entry (dataset)
            for (DataSet dataset : report.getListOfDataSets()) { 
                List<String> varIDs = new ArrayList<>();
                Map<Variable, NonspatialValueHolder> resultsByVariable = new HashMap<>();
                int maxLengthOfAllData = 0; // We have to pad up to this value

                // use the data reference to obtain the data generator
                DataGenerator datagen = sedml.getDataGeneratorWithId(dataset.getDataReference()); assert datagen != null;
                
                // get the list of variables associated with the data reference
                for (Variable var : datagen.getListOfVariables()) {
                    // for each variable we recover the task
                    AbstractTask topLevelTask = sedml.getTaskWithId(var.getReference());
                    AbstractTask baseTask = NonspatialResultsConverter.getBaseTask(topLevelTask, sedml); // if !RepeatedTask, baseTask == topLevelTask
                    
                    // from the task we get the sbml model
                    org.jlibsedml.Simulation sedmlSim = sedml.getSimulation(((Task)baseTask).getSimulationReference());

                    if (!(sedmlSim instanceof UniformTimeCourse)){
                        logger.error("only uniform time course simulations are supported");
                        continue;
                    }
                    
                    // must get variable ID from SBML model
                    String vcellVarId = convertToVCellSymbol(var);

                    // If the task isn't in our results hash, it's unwanted and skippable.
                    boolean bFoundTaskInNonspatial = nonspatialResultsHash.keySet().stream().anyMatch(taskJob -> taskJob.getTaskId().equals(topLevelTask.getId()));
                    if (!bFoundTaskInNonspatial){
                        logger.warn("Was not able to find simulation data for task with ID: " + topLevelTask.getId());
                        break;
                    }

                    // ==================================================================================
                    
                    ArrayList<TaskJob> taskJobs = new ArrayList<>();

                    for (Map.Entry<TaskJob, ODESolverResultSet> entry : nonspatialResultsHash.entrySet()) {
                        TaskJob taskJob = entry.getKey();
                        if (entry.getValue() != null && taskJob.getTaskId().equals(topLevelTask.getId())) {
                            taskJobs.add(taskJob);
                            if (!(topLevelTask instanceof RepeatedTask)) 
                                break; // No need to keep looking if it's not a repeated task
                        }
                    }

                    if (taskJobs.isEmpty()) continue;

                    varIDs.add(var.getId());

                    // we want to keep the last outputNumberOfPoints only
                    int outputNumberOfPoints = ((UniformTimeCourse) sedmlSim).getNumberOfPoints();
                    double outputStartTime = ((UniformTimeCourse) sedmlSim).getOutputStartTime();
                    NonspatialValueHolder resultsHolder;

                    for (TaskJob taskJob : taskJobs) {
                        ODESolverResultSet results = nonspatialResultsHash.get(taskJob);
                        int column = results.findColumn(vcellVarId);
                        double[] data = results.extractColumn(column);
                        
                        if (outputStartTime > 0){
                            double[] correctiveData = new double[outputNumberOfPoints + 1];
                            for (int i = data.length - outputNumberOfPoints - 1, j = 0; i < data.length; i++, j++) {
                                correctiveData[j] = data[i];
                            }
                            data = correctiveData;
                        }

                        maxLengthOfAllData = Integer.max(maxLengthOfAllData, data.length);
                        if (topLevelTask instanceof RepeatedTask && resultsByVariable.containsKey(var)) { // double[] exists
                            resultsHolder = resultsByVariable.get(var);
                        } else { // this is the first double[]
                            resultsHolder = new NonspatialValueHolder(taskToSimulationMap.get(topLevelTask));
                        }
                        resultsHolder.listOfResultSets.add(data);
                        resultsByVariable.put(var, resultsHolder);
                    }
                }

                
                if (resultsByVariable.isEmpty()) continue;
                int numJobs, maxLengthOfData = 0;
                String exampleReference = resultsByVariable.keySet().iterator().next().getReference();
                NonspatialValueHolder synthesizedResults = new NonspatialValueHolder(taskToSimulationMap.get(sedml.getTaskWithId(exampleReference)));
                SimpleDataGenCalculator calc = new SimpleDataGenCalculator(datagen);

                // Get padding value
                for (NonspatialValueHolder nvh : resultsByVariable.values()){
                    for (double[] dataSet : nvh.listOfResultSets){
                        if (dataSet.length > maxLengthOfData){
                            maxLengthOfData = dataSet.length;
                        }
                    }
                }

                // Determine the num of jobs
                numJobs = resultsByVariable.values().iterator().next().getNumSets();

                // Perform the math!
                for (int jobNum = 0; jobNum < numJobs; jobNum++){
                    double[] synthesizedDataset = new double[maxLengthOfData];
                    for (int datumIndex = 0; datumIndex < synthesizedDataset.length; datumIndex++){

                        for (Variable var : resultsByVariable.keySet()){
                            //if (processedDataSet == null) processedDataSet = new NonspatialValueHolder(sedml.getTaskWithId(var.getReference()));
                            if (jobNum >= resultsByVariable.get(var).getNumSets()) continue;
                            NonspatialValueHolder results = resultsByVariable.get(var);
                            double[] specficJobDataSet = results.listOfResultSets.get(jobNum);
                            double datum = datumIndex >= specficJobDataSet.length ? Double.NaN : specficJobDataSet[datumIndex];
                            calc.setArgument(var.getId(), datum);
                            if (!synthesizedResults.vcSimulation.equals(results.vcSimulation)){
                                logger.warn("Simulations differ across variables; need to fix data structures to accomodate?");
                            }
                        }
                        synthesizedDataset[datumIndex] = calc.evaluateWithCurrentArguments(true);
                    }
                    synthesizedResults.listOfResultSets.add(synthesizedDataset);
                    //synthesizedResults.vcSimulation;
                }

                dataSetValues.put(dataset, synthesizedResults);

            } // end of current dataset processing

            if (dataSetValues.isEmpty()) {
                logger.warn("We did not get any entries in the final data set. " +
                        "This may mean a problem has been encountered.");
                continue;
            }
            List<String> shapes = new LinkedList<>();
            Hdf5SedmlResultsNonspatial dataSourceNonspatial = new Hdf5SedmlResultsNonspatial();
            Hdf5SedmlResults hdf5DatasetWrapper = new Hdf5SedmlResults();

            if (dataSetValues.entrySet().iterator().next().getValue().isEmpty()) continue; // Check if we have data to work with.

            hdf5DatasetWrapper.datasetMetadata._type = NonspatialResultsConverter.getKind(report.getId());
            hdf5DatasetWrapper.datasetMetadata.sedmlId = NonspatialResultsConverter.removeVCellPrefixes(report.getId(), report.getId());
            hdf5DatasetWrapper.datasetMetadata.sedmlName = report.getName();
            hdf5DatasetWrapper.datasetMetadata.uri = Paths.get(sedmlLocation, report.getId()).toString();

            for (DataSet dataSet : dataSetValues.keySet()){
                NonspatialValueHolder dataSetValuesSource = dataSetValues.get(dataSet);

                dataSourceNonspatial.allJobResults.put(dataSet, new LinkedList<>());
                dataSourceNonspatial.scanBounds = dataSetValuesSource.vcSimulation.getMathOverrides().getScanBounds();
                dataSourceNonspatial.scanParameterNames = dataSetValuesSource.vcSimulation.getMathOverrides().getScannedConstantNames();
                for (double[] data : dataSetValuesSource.listOfResultSets) {
                    dataSourceNonspatial.allJobResults.get(dataSet).add(data);
                    shapes.add(Integer.toString(data.length));
                }
                
                hdf5DatasetWrapper.dataSource = dataSourceNonspatial; // Using upcasting
                hdf5DatasetWrapper.datasetMetadata.sedmlDataSetDataTypes.add("float64");
                hdf5DatasetWrapper.datasetMetadata.sedmlDataSetIds.add(
                    NonspatialResultsConverter.removeVCellPrefixes(dataSet.getId(), hdf5DatasetWrapper.datasetMetadata.sedmlId));
                hdf5DatasetWrapper.datasetMetadata.sedmlDataSetLabels.add(dataSet.getLabel());
                hdf5DatasetWrapper.datasetMetadata.sedmlDataSetNames.add(dataSet.getName());
                hdf5DatasetWrapper.datasetMetadata.sedmlDataSetShapes = shapes;
            }

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

    private static class NonspatialValueHolder {
        List<double[]> listOfResultSets = new ArrayList<>();
        final Simulation vcSimulation;

        public NonspatialValueHolder(Simulation simulation) {
            this.vcSimulation = simulation;
        }

        public int getNumSets() {
            return listOfResultSets.size();
        }

        public boolean isEmpty(){
            return listOfResultSets.size() == 0 ? true : false;
        }

        /*public int[] getJobCoordinate(int index){
            String[] names = vcSimulation.getMathOverrides().getScannedConstantNames();
            java.util.Arrays.sort(names); // must do things in a consistent way
            int[] bounds = new int[names.length]; // bounds of scanning matrix
            for (int i = 0; i < names.length; i++){
                bounds[i] = vcSimulation.getMathOverrides().getConstantArraySpec(names[i]).getNumValues() - 1;
            }
            int[] coordinates = BeanUtils.indexToCoordinate(index, bounds);
            return coordinates;
        }*/
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
