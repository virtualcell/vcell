package org.vcell.cli.run.hdf5;

import io.jhdf.api.Dataset;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.nio.file.Paths;
import java.util.*;

import io.jhdf.HdfFile;


/**
 * Manages results hdf5 files for accessing to reduce runtime.
 */
public class Hdf5DataSourceFileManager {
    private final static Logger lg = LogManager.getLogger(Hdf5DataSourceFileManager.class);
    private static Hdf5DataSourceFileManager instance = null;

    private final Map<String, File> managedFrankHdf5Files = new HashMap<>();
    private final Map<File, HdfFile> readerMapping = new HashMap<>();

    private Hdf5DataSourceFileManager(){}

    public static Hdf5DataSourceFileManager getInstance(){
        if (null == instance) instance = new Hdf5DataSourceFileManager();
        return instance;
    }

    public boolean isOpen(String vcTaskId){
        if (!managedFrankHdf5Files.containsKey(vcTaskId))
            throw new IllegalArgumentException("No file found pertaining to vcell task `" + vcTaskId + "`");
        return null != readerMapping.get(managedFrankHdf5Files.get(vcTaskId));
    }

    public void openFile(String vcTaskId){ // Note: `addNewResultsFile` can open while bypassing this function!!
        if (!managedFrankHdf5Files.containsKey(vcTaskId))
            throw new IllegalArgumentException("No file found pertaining to vcell task `" + vcTaskId + "`");
        lg.debug("Opening data file for task `" + vcTaskId + "`");
        File targetFile = managedFrankHdf5Files.get(vcTaskId);
        if (readerMapping.get(targetFile) != null){
            // File is already open
            lg.debug("File `" + vcTaskId + "` is already open");
            return;
        }
        readerMapping.put(targetFile, new HdfFile(Paths.get(targetFile.toURI())));
    }

    public void openAllFiles(){
        for (String vcID : managedFrankHdf5Files.keySet()) openFile(vcID);
    }

    public void closeFile(String vcTaskId){
        if (!managedFrankHdf5Files.containsKey(vcTaskId))
            throw new IllegalArgumentException("No file found pertaining to vcell task `" + vcTaskId + "`");
        lg.debug("Opening data file for task `" + vcTaskId + "`");
        File targetFile = managedFrankHdf5Files.get(vcTaskId);
        if (readerMapping.get(targetFile) == null){
            // File is already closed
            lg.debug("File `" + vcTaskId + "` is already closed");
            return;
        }
        readerMapping.get(targetFile).close();
        readerMapping.put(targetFile, null);
    }

    public void closeAllFiles(){
        for (String vcID : managedFrankHdf5Files.keySet()) closeFile(vcID);
    }

    public void addNewResultsFile(String vcTaskId, File correspondingResultsFile, boolean shouldImmediatelyOpen){
        lg.debug("Adding new results file identified by `" + vcTaskId + "`");
        if (managedFrankHdf5Files.containsKey(vcTaskId)){
            lg.warn("Overwriting file corresponding to VCell Task ID: `" + vcTaskId + "`");
            removeExistingResultsFile(vcTaskId);
        }
        managedFrankHdf5Files.put(vcTaskId, correspondingResultsFile);
        HdfFile initialState = shouldImmediatelyOpen ? new HdfFile(Paths.get(correspondingResultsFile.toURI())) : null;
        readerMapping.put(managedFrankHdf5Files.get(vcTaskId), initialState);
    }

    public void removeExistingResultsFile(String vcTaskId){
        lg.debug("Removing results file for `" + vcTaskId + "`");
        closeFile(vcTaskId); // This should throw an exception if the key is bogus.
        this.readerMapping.remove(this.managedFrankHdf5Files.get(vcTaskId));
        this.managedFrankHdf5Files.remove(vcTaskId);
    }

    public boolean containsFile(String vcTaskId){
        return this.managedFrankHdf5Files.containsKey(vcTaskId);
    }

    public File getFile(String vcTaskId){
        if (!this.managedFrankHdf5Files.containsKey(vcTaskId))
            throw new IllegalArgumentException("No file found pertaining to vcell task `" + vcTaskId + "`");
        return this.managedFrankHdf5Files.get(vcTaskId);
    }

    public double[] getData(Hdf5DataSourceSpatialVarDataLocation accessor){
        return this.isOpen(accessor.vcellTaskGroupIdentifier) ? this.getDataFast(accessor) : this.getDataSafe(accessor);
    }

    private HdfFile getHdfFile(String vcTaskId){
        if (!this.managedFrankHdf5Files.containsKey(vcTaskId))
            throw new IllegalArgumentException("No file found pertaining to vcell task `" + vcTaskId + "`");
        return this.readerMapping.get(this.managedFrankHdf5Files.get(vcTaskId));
    }

    private double[] getDataSafe(Hdf5DataSourceSpatialVarDataLocation accessor){
        this.openFile(accessor.vcellTaskGroupIdentifier);
        Dataset dataSet = this.getHdfFile(accessor.vcellTaskGroupIdentifier).getDatasetByPath(accessor.hdf5Path);
        if (dataSet == null){
            String errorMessage = String.format("could not find data for variable `%s` in hdf5 file `%s`",
                    accessor.sedmlVariableName, this.managedFrankHdf5Files.get(accessor.vcellTaskGroupIdentifier).getName());
            throw new RuntimeException(errorMessage);
        }
        double[] result = (double[])dataSet.getDataFlat();
        this.closeFile(accessor.vcellTaskGroupIdentifier);
        return result;
    }

    private double[] getDataFast(Hdf5DataSourceSpatialVarDataLocation accessor){
            Dataset dataSet = this.getHdfFile(accessor.vcellTaskGroupIdentifier).getDatasetByPath(accessor.hdf5Path);
            if (dataSet == null){
                String errorMessage = String.format("could not find data for variable `%s` in hdf5 file `%s`",
                        accessor.sedmlVariableName, this.managedFrankHdf5Files.get(accessor.vcellTaskGroupIdentifier).getName());
                throw new RuntimeException(errorMessage);
            }
        return (double[])dataSet.getDataFlat();
    }
}
