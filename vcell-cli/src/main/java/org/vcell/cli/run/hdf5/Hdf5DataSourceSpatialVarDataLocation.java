package org.vcell.cli.run.hdf5;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

import javax.annotation.Nonnull;

public class Hdf5DataSourceSpatialVarDataLocation implements Comparable<Hdf5DataSourceSpatialVarDataLocation>{
    private final static Logger lg = LogManager.getLogger(Hdf5DataSourceSpatialVarDataLocation.class);

    private final static Hdf5DataSourceFileManager fileManager = Hdf5DataSourceFileManager.getInstance();
    public final String vcellTaskGroupIdentifier;
    public final int scanNum;
    public final String hdf5Path;
    public final String sedmlVariableName;

    /**
     * Builds a location object, specifying where data can be found; depending on type of simulation, may be complete or partial.
     *

     * @param vcellTaskGroupIdentifier the task id related to the hdf5 file with the results to look for the data in
     * @param hdf5Path where in the hdf5 to look to get the data
     */
    public Hdf5DataSourceSpatialVarDataLocation(File pathtoResultsFile, String vcellTaskGroupIdentifier, String hdf5Path, String sedmlVariableName) {
        this(pathtoResultsFile, vcellTaskGroupIdentifier, 0, hdf5Path, sedmlVariableName);
    }

    public Hdf5DataSourceSpatialVarDataLocation(File pathtoResultsFile, String vcellSpecificTaskIdentifier, int scanNum, String hdf5Path, String sedmlVariableName) {
        if (vcellSpecificTaskIdentifier == null) throw new IllegalArgumentException("`vcellResultsFileName` can not be null!");
        if (hdf5Path == null) throw new IllegalArgumentException("`hdf5Path` can not be null!");
        if (sedmlVariableName == null) throw new IllegalArgumentException("`sedmlVariableName` can not be null!");
        this.vcellTaskGroupIdentifier = vcellSpecificTaskIdentifier;
        this.scanNum = scanNum;
        this.hdf5Path = hdf5Path;
        this.sedmlVariableName = sedmlVariableName;
        if (!fileManager.containsFile(vcellSpecificTaskIdentifier))
            fileManager.addNewResultsFile(vcellSpecificTaskIdentifier, pathtoResultsFile, false);
    }

    /**
     * Getter for spatial data
     *
     * @return the data as a double array
     */
    public double[] getSpatialDataFlat() {
        lg.debug("Fetching experiment data");
        return fileManager.getData(this);
    }

    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }

    @Override
    public String toString() {
        return String.format("%s(%d)::%s@[%s]",
                this.vcellTaskGroupIdentifier, this.scanNum, this.sedmlVariableName, this.hdf5Path);
    }

    @Override
    public boolean equals(Object other){
        if (!(other instanceof Hdf5DataSourceSpatialVarDataLocation otherLocation)) return false;
        return this.vcellTaskGroupIdentifier.equals(otherLocation.vcellTaskGroupIdentifier)
                && this.scanNum == otherLocation.scanNum
                && this.hdf5Path.equals(otherLocation.hdf5Path)
                && this.sedmlVariableName.equals(otherLocation.sedmlVariableName);
    }

    /**
     * Compares ***ONLY*** the job numbers of two locations; to reduce overhead. NO CHECK IS INCLUDED.
     * @param o the location object to be compared.
     * @return standard java comparison between the job num integers.
     */
    @Override
    public int compareTo(@Nonnull Hdf5DataSourceSpatialVarDataLocation o) {
        return this.scanNum - o.scanNum;
    }
}
