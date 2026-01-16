package cbit.vcell.simdata;


import cbit.vcell.solver.ode.ODESimData;

import java.io.Serializable;

public class LangevinBatchResultSet implements Serializable {

    private ODEDataInfo odeDataInfo;
    private ODESimData odeSimDataAvg;
    private ODESimData odeSimDataMax;
    private ODESimData odeSimDataMin;
    private ODESimData odeSimDataStd;

    public enum LangevinFileType {
        Avg("_Avg", ".ida"),
        Max("_Max", ".ida"),
        Min("_Min", ".ida"),
        Std("_Std", ".ida"),
        ClusterCounts("_clusters_counts", ".csv"),
        ClusterMean("_clusters_mean", ".csv"),
        ClusterOverall("_clusters_overall", ".csv");

        private final String type;
        private final String extension;

        LangevinFileType(String type, String extension) {
            this.type = type;
            this.extension = extension;
        }

        public String type() {
            return type;
        }

        public String extension() {
            return extension;
        }

        public String buildFilename(String baseName) {
            return baseName + type + extension;
        }
    }

    public LangevinBatchResultSet(
        ODEDataInfo odeDataInfo,
        ODESimData odeSimDataAvg,
        ODESimData odeSimDataMax,
        ODESimData odeSimDataMin,
        ODESimData odeSimDataStd) {
        this.odeDataInfo = odeDataInfo;
        this.odeSimDataAvg = odeSimDataAvg;
        this.odeSimDataMax = odeSimDataMax;
        this.odeSimDataMin = odeSimDataMin;
        this.odeSimDataStd = odeSimDataStd;
    }

}
