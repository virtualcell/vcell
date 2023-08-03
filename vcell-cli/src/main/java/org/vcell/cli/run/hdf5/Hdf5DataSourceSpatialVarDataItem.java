package org.vcell.cli.run.hdf5;

import io.jhdf.api.Dataset;
import io.jhdf.api.Group;
import io.jhdf.api.Node;
import org.jlibsedml.DataSet;
import org.jlibsedml.Report;
import org.jlibsedml.Variable;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * 
 */
public class Hdf5DataSourceSpatialVarDataItem {

    private final static Logger lg = LogManager.getLogger(Hdf5DataSourceSpatialVarDataItem.class);
    public final Report sedmlReport;
    public final DataSet sedmlDataset;
    public final Variable sedmlVariable;
    public final int jobIndex;

    public final File hdf5File;
    public final double outputStartTime;
    public final int outputNumberOfPoints;

    public int[] timebounds;
    public double[] times;
    public int[] spaceTimeDimensions;
    public Map<String, String> varToDatasetPathMap;

    /**
     * Standard constructor; needs more documentation
     * 
     * @param sedmlReport the SED-ML report the var data item is connected to.
     * @param sedmlDataset the SED-ML dataset the var data item is fulfilling.
     * @param sedmlVariable
     * @param jobIndex the job number
     * @param hdf5File the hdf5 file containing the VCell format spatial data.
     * @param outputStartTime the start time of the data
     * @param outputNumberOfPoints the total number of data points.
     */
    public Hdf5DataSourceSpatialVarDataItem(
            Report sedmlReport, DataSet sedmlDataset, Variable sedmlVariable,
            int jobIndex, File hdf5File, double outputStartTime, int outputNumberOfPoints, String vcellVarId) {
        this.sedmlReport = sedmlReport;
        this.sedmlDataset = sedmlDataset;
        this.sedmlVariable = sedmlVariable;
        this.jobIndex = jobIndex;
        this.hdf5File = hdf5File;
        this.outputStartTime = outputStartTime;
        this.outputNumberOfPoints = outputNumberOfPoints;
        this.parseMetadata();
    }

    /**
     * Getter for spatial data
     * 
     * @return the data as a double array
     */
    public double[] getSpatialData() {
        lg.info("Fetching experiment data");
        try (io.jhdf.HdfFile jhdfFile = new io.jhdf.HdfFile(Paths.get(this.hdf5File.toURI()))) {
            Dataset dataset = jhdfFile.getDatasetByPath(this.varToDatasetPathMap.get(this.sedmlVariable.getName()));
            if (dataset == null){
                throw new RuntimeException("could not find data for variable " + this.sedmlVariable.getName());
            }
            return (double[])dataset.getDataFlat();
        }
    }

    private void parseMetadata() {
        lg.info("Fetching metadata");
        try (io.jhdf.HdfFile jhdfFile = new io.jhdf.HdfFile(Paths.get(this.hdf5File.toURI()))) {
            Map<String, Node> children = jhdfFile.getChildren();
            Map.Entry<String, Node> topLevelTaskEntry = children.entrySet().stream().findFirst().get();
            System.out.println(topLevelTaskEntry); //TODO log as trace
            if (!(topLevelTaskEntry.getValue() instanceof Group)) {
                throw new RuntimeException("expecting top level child in spatial data hdf5 file to be a group");
            }
            this.varToDatasetPathMap = new LinkedHashMap<>();
            Group topLevelGroup = (Group) topLevelTaskEntry.getValue();
            for (Map.Entry<String, Node> groupEntry : topLevelGroup.getChildren().entrySet()) {
                if (groupEntry.getValue() instanceof Dataset) {
                    Dataset jhdfDataset = (Dataset) groupEntry.getValue();
                    if (jhdfDataset.getName().equals("TIMEBOUNDS")) {
                        this.timebounds = (int[]) jhdfDataset.getDataFlat();
                        System.out.println(Arrays.toString(this.timebounds));
                    }
                    if (jhdfDataset.getName().equals("TIMES")) {
                        this.times = (double[]) jhdfDataset.getDataFlat();
                        System.out.println(Arrays.toString(this.times));
                    }
                } else if (groupEntry.getValue() instanceof Group) {
                    Group varGroup = (Group) groupEntry.getValue();
                    Map.Entry<String, Node> varDatasetEntry = varGroup.getChildren().entrySet().stream().findFirst().get();
                    this.varToDatasetPathMap.put(varGroup.getName(), varGroup.getPath() + varDatasetEntry.getKey());
                    if (groupEntry.getKey().equals(this.sedmlVariable.getName())){
                        Dataset dataset = jhdfFile.getDatasetByPath(this.varToDatasetPathMap.get(this.sedmlVariable.getName()));
                        this.spaceTimeDimensions = dataset.getDimensions();
                    }
                }
            }
        }
    }
}
