package org.vcell.cli.run.hdf5;

import io.jhdf.api.Dataset;
import io.jhdf.api.Group;
import io.jhdf.api.Node;
import org.jlibsedml.DataSet;
import org.jlibsedml.Report;
import org.jlibsedml.Variable;

import java.io.File;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

public class Hdf5DataSourceSpatialVarDataItem {
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

    public double[] getSpatialData() {
        try (io.jhdf.HdfFile jhdfFile = new io.jhdf.HdfFile(Paths.get(hdf5File.toURI()))) {
            Dataset dataset = jhdfFile.getDatasetByPath(varToDatasetPathMap.get(sedmlVariable.getName()));
            if (dataset==null){
                throw new RuntimeException("could not find data for variable "+sedmlVariable.getName());
            }
            return (double[])dataset.getDataFlat();
        }
    }

    public Hdf5DataSourceSpatialVarDataItem(
            Report sedmlReport, DataSet sedmlDataset, Variable sedmlVariable,
            int jobIndex, File hdf5File, double outputStartTime, int outputNumberOfPoints) {
        this.sedmlReport = sedmlReport;
        this.sedmlDataset = sedmlDataset;
        this.sedmlVariable = sedmlVariable;
        this.jobIndex = jobIndex;
        this.hdf5File = hdf5File;
        this.outputStartTime = outputStartTime;
        this.outputNumberOfPoints = outputNumberOfPoints;
        parseMetadata();
    }

    private void parseMetadata() {
        try (io.jhdf.HdfFile jhdfFile = new io.jhdf.HdfFile(Paths.get(hdf5File.toURI()))) {
            Map<String, Node> children = jhdfFile.getChildren();
            Map.Entry<String, Node> topLevelTaskEntry = children.entrySet().stream().findFirst().get();
            System.out.println(topLevelTaskEntry);
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
                        System.out.println(timebounds);
                    }
                    if (jhdfDataset.getName().equals("TIMES")) {
                        this.times = (double[]) jhdfDataset.getDataFlat();
                        System.out.println(timebounds);
                    }
                } else if (groupEntry.getValue() instanceof Group) {
                    Group varGroup = (Group) groupEntry.getValue();
                    Map.Entry<String, Node> varDatasetEntry = varGroup.getChildren().entrySet().stream().findFirst().get();
                    varToDatasetPathMap.put(varGroup.getName(), varGroup.getPath() + varDatasetEntry.getKey());
                    if (groupEntry.getKey().equals(sedmlVariable.getName())){
                        Dataset dataset = jhdfFile.getDatasetByPath(varToDatasetPathMap.get(sedmlVariable.getName()));
                        this.spaceTimeDimensions = dataset.getDimensions();
                    }
                }
            }
        }
    }
}
