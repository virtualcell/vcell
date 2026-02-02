package cbit.vcell.simdata;


import cbit.vcell.solver.ode.ODESimData;

import java.io.Serializable;

public class LangevinBatchResultSet implements Serializable {

    private ODEDataInfo odeDataInfo = null;
    private ODESimData odeSimDataAvg = null;
    private ODESimData odeSimDataMax = null;
    private ODESimData odeSimDataMin = null;
    private ODESimData odeSimDataStd = null;
    private ODESimData odeSimDataClusterCounts = null;
    private ODESimData odeSimDataClusterMean = null;
    private ODESimData odeSimDataClusterOverall = null;

    public enum LangevinFileType {

        Avg("_Avg", ".ida"),
        Max("_Max", ".ida"),
        Min("_Min", ".ida"),
        Std("_Std", ".ida"),
        ClusterCounts("_clusters_counts", ".csv"),
        ClusterMean("_clusters_mean", ".csv"),
        ClusterOverall("_clusters_overall", ".csv");

        private final String suffix;
        private final String extension;

        LangevinFileType(String suffix, String extension) {
            this.suffix = suffix;
            this.extension = extension;
        }
        public String suffix() {
            return suffix;
        }
        public String extension() {
            return extension;
        }
        public String buildFilename(String baseName) {
            return baseName + suffix + extension;
        }
    }

    public LangevinBatchResultSet(ODEDataInfo odeDataInfo) {

        this.odeDataInfo = odeDataInfo;
    }

    public ODEDataInfo getOdeDataInfo() {
        return odeDataInfo;
    }

    public ODESimData getOdeSimDataAvg() {
        return odeSimDataAvg;
    }
    public void setOdeSimDataAvg(ODESimData odeSimDataAvg) {
        this.odeSimDataAvg = odeSimDataAvg;
    }

    public ODESimData getOdeSimDataMax() {
        return odeSimDataMax;
    }
    public void setOdeSimDataMax(ODESimData odeSimDataMax) {
        this.odeSimDataMax = odeSimDataMax;
    }

    public ODESimData getOdeSimDataMin() {
        return odeSimDataMin;
    }
    public void setOdeSimDataMin(ODESimData odeSimDataMin) {
        this.odeSimDataMin = odeSimDataMin;
    }

    public ODESimData getOdeSimDataStd() {
        return odeSimDataStd;
    }
    public void setOdeSimDataStd(ODESimData odeSimDataStd) {
        this.odeSimDataStd = odeSimDataStd;
    }

    public ODESimData getOdeSimDataClusterCounts() {  return odeSimDataClusterCounts; }
    public void setOdeSimDataClusterCounts(ODESimData odeSimDataClusterCounts) {
        this.odeSimDataClusterCounts = odeSimDataClusterCounts;
    }

    public ODESimData getOdeSimDataClusterMean() {  return odeSimDataClusterMean; }
    public void setOdeSimDataClusterMean(ODESimData odeSimDataClusterMean) {
        this.odeSimDataClusterMean = odeSimDataClusterMean;
    }

    public ODESimData getOdeSimDataClusterOverall() {  return odeSimDataClusterOverall; }
    public void setOdeSimDataClusterOverall(ODESimData odeSimDataClusterOverall) {
        this.odeSimDataClusterOverall = odeSimDataClusterOverall;
    }
}

