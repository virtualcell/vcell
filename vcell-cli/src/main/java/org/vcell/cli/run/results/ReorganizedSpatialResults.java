package org.vcell.cli.run.results;

import cbit.vcell.solver.TempSimulation;
import io.jhdf.api.Dataset;
import io.jhdf.api.Group;
import io.jhdf.api.Node;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jlibsedml.AbstractTask;
import org.vcell.cli.run.TaskJob;
import org.vcell.cli.run.hdf5.Hdf5DataSourceSpatialSimMetadata;
import org.vcell.cli.run.hdf5.Hdf5DataSourceSpatialSimVars;
import org.vcell.cli.run.hdf5.Hdf5DataSourceSpatialVarDataLocation;

import java.io.File;
import java.nio.file.Paths;
import java.util.*;

public class ReorganizedSpatialResults {
    private final static Logger lg = LogManager.getLogger(Hdf5DataSourceSpatialSimVars.class);

    private final Map<String, Hdf5DataSourceSpatialSimMetadata> taskGroupToMetadata;
    private final Map<String, Hdf5DataSourceSpatialSimVars> taskGroupToMapOfVariableNameToListOfLocations;

    public ReorganizedSpatialResults(Map<TaskJob, File> spatialVcellJobToResultsMap, Map<AbstractTask, TempSimulation> sedmlTaskToVCellSim) {
        this.taskGroupToMetadata = new HashMap<>();
        this.taskGroupToMapOfVariableNameToListOfLocations = new HashMap<>();
        this.populate(spatialVcellJobToResultsMap, sedmlTaskToVCellSim);
    }

    public Set<String> getTaskGroupSet(){
        return this.taskGroupToMetadata.keySet();
    }

    public Hdf5DataSourceSpatialSimMetadata getMetadataFromTaskGroup(String taskGroup){
        if (this.taskGroupToMetadata.containsKey(taskGroup))
            return this.taskGroupToMetadata.get(taskGroup);
        throw new IllegalArgumentException("`" + taskGroup + "` is not a valid task group (key miss)");
    }

    public Hdf5DataSourceSpatialSimVars getVarsFromTaskGroup(String taskGroup){
        if (this.taskGroupToMapOfVariableNameToListOfLocations.containsKey(taskGroup))
            return this.taskGroupToMapOfVariableNameToListOfLocations.get(taskGroup);
        throw new IllegalArgumentException("`" + taskGroup + "` is not a valid task group (key miss)");
    }

    /**
     * Stores results of vcell
     * @param spatialVcellJobToResultsMap
     * @return
     */
    private void populate(Map<TaskJob, File> spatialVcellJobToResultsMap, Map<AbstractTask, TempSimulation> sedmlTaskToVCellSim){
        lg.debug("Parsing Metadata");
        int VC_ID = 0, TASK_NUM = 1, USER_ID = 2;
        for (TaskJob vcellTask : spatialVcellJobToResultsMap.keySet()){
            try (io.jhdf.HdfFile jhdfFile = new io.jhdf.HdfFile(Paths.get(spatialVcellJobToResultsMap.get(vcellTask).toURI()))) {
                Map.Entry<String, Node> topLevelTaskEntry = jhdfFile.getChildren().entrySet().stream().findFirst().orElseThrow();
                String topLevelEntryIdentifier = topLevelTaskEntry.getKey();
                //"VCSimulationIdentifier[`sim_id`,`task_num`,`user_id`]" => [ "`sim_id`" ,"`task_num`", "`user_id`" ]
                String[] VCSimComponents = topLevelEntryIdentifier.substring(23, topLevelEntryIdentifier.length() - 1).split(",");

                lg.trace(topLevelTaskEntry);
                if (!(topLevelTaskEntry.getValue() instanceof Group topLevelGroup)) {
                    throw new RuntimeException("expecting top level child in spatial data hdf5 file to be a group");
                }

                Hdf5DataSourceSpatialSimMetadata simMetadata = new Hdf5DataSourceSpatialSimMetadata();
                Map<String, Node> entrySubsets = topLevelGroup.getChildren();
                Map<String, Hdf5DataSourceSpatialVarDataLocation> localLocationMapping = new HashMap<>();
                for (Map.Entry<String, Node> groupEntry : entrySubsets.entrySet()) {
                    if (groupEntry.getValue() instanceof Dataset jhdfDataset) {
                        if (jhdfDataset.getName().equals("TIMEBOUNDS")) {
                            simMetadata.validateTimeBounds((int[]) jhdfDataset.getDataFlat());
                            lg.trace(Arrays.toString(simMetadata.getTimeBounds()));
                        }
                        if (jhdfDataset.getName().equals("TIMES")) {
                            simMetadata.validateTimes((double[]) jhdfDataset.getDataFlat());
                            lg.trace(Arrays.toString(simMetadata.getTimes()));
                        }
                    } else if (groupEntry.getValue() instanceof Group speciesNode) {
                        String nodeName = speciesNode.getChildren().keySet().stream().findFirst().orElseThrow();
                        // The name of the group does not match name of the actual data set inside the group; thus, the path is different
                        simMetadata.addSpecies(speciesNode.getName());
                        Dataset dataset = jhdfFile.getDatasetByPath(speciesNode.getPath() + nodeName);
                        simMetadata.validateSpaceTimeDimensions(dataset.getDimensions());
                        Hdf5DataSourceSpatialVarDataLocation newLocation =
                                new Hdf5DataSourceSpatialVarDataLocation(spatialVcellJobToResultsMap.get(vcellTask), vcellTask.toString(), Integer.parseInt(VCSimComponents[TASK_NUM]),
                                        speciesNode.getPath() + nodeName, speciesNode.getName());
                        localLocationMapping.put(speciesNode.getName(), newLocation);
                    }
                }

                if (!this.taskGroupToMetadata.containsKey(vcellTask.getTaskId())){
                    this.taskGroupToMetadata.put(vcellTask.getTaskId(), simMetadata);
                } else {
                    Hdf5DataSourceSpatialSimMetadata oldSimMetadata = this.taskGroupToMetadata.get(vcellTask.getTaskId());
                    if (!simMetadata.hasEquivalentShapeTo(oldSimMetadata)){
                        String format = "Shape collision detected. Sim `%s` conflicts with other scan sims.";
                        String errorMessage = String.format(format, vcellTask.getTaskId());
                        throw new RuntimeException(errorMessage);
                    }
                }

                if (!this.taskGroupToMapOfVariableNameToListOfLocations.containsKey(vcellTask.getTaskId())) this.mapSimVars(vcellTask.getTaskId(), new Hdf5DataSourceSpatialSimVars());
                Hdf5DataSourceSpatialSimVars jobGroupVarsToListOfLocations = this.taskGroupToMapOfVariableNameToListOfLocations.get(vcellTask.getTaskId());
                for (String var : localLocationMapping.keySet()){
                    jobGroupVarsToListOfLocations.addLocation(var, localLocationMapping.get(var));
                }

            } // End of File Access
        } // End of Task Loop
        Set<String> taskJobSet1, taskJobSet2;
        taskJobSet1 = new HashSet<>(this.taskGroupToMapOfVariableNameToListOfLocations.keySet());
        taskJobSet2 = new HashSet<>(this.taskGroupToMetadata.keySet());
        // If they're not the same size, we have a mismatch; that's a problem
        if (taskJobSet1.size() != taskJobSet2.size()) throw new RuntimeException("Mismatched keysets detected");
        taskJobSet1.retainAll(taskJobSet2); // If after the intersection the sizes mismatch, we still have a problem.
        if (taskJobSet1.size() != taskJobSet2.size()) throw new RuntimeException("Mismatched keysets detected");
    }

    private void mapSimVars(String taskJobName, Hdf5DataSourceSpatialSimVars vars){
        if (taskJobName == null) throw new IllegalArgumentException("`taskJobName` can not be null!");
        if (vars == null) throw new IllegalArgumentException("`vars` can not be null!");
        this.taskGroupToMapOfVariableNameToListOfLocations.put(taskJobName, vars);
    }
}
