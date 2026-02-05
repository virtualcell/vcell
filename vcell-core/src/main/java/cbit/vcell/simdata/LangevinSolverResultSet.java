package cbit.vcell.simdata;

import cbit.vcell.solver.ode.ODESimData;

import java.io.*;

public class LangevinSolverResultSet implements Serializable {

    private final LangevinBatchResultSet raw;

    public LangevinSolverResultSet(LangevinBatchResultSet raw) {
        this.raw = raw;
    }

//    // safe getter that returns a deep copy, but I don't think we need it
//    public LangevinBatchResultSet getLangevinBatchResultSetSafe() {
//        return deepCopy(raw);
//    }

    // convenience getters
    public ODESimData getAvg() {
        return raw == null ? null : raw.getOdeSimDataAvg();
    }
    public ODESimData getMin() {
        return raw == null ? null : raw.getOdeSimDataMin();
    }
    public ODESimData getMax() {
        return raw == null ? null : raw.getOdeSimDataMax();
    }
    public ODESimData getStd() {
        return raw == null ? null : raw.getOdeSimDataStd();
    }
    public ODESimData getClusterCounts() {
        return raw == null ? null : raw.getOdeSimDataClusterCounts();
    }
    public ODESimData getClusterMean() {
        return raw == null ? null : raw.getOdeSimDataClusterMean();
    }
    public ODESimData getClusterOverall() {
        return raw == null ? null : raw.getOdeSimDataClusterOverall();
    }

    // helper functions
    public boolean isAverageDataAvailable() {
        return getAvg() != null &&
                getMin() != null &&
                getMax() != null &&
                getStd() != null;
    }
    public boolean isClusterDataAvailable() {
        return getClusterCounts() != null &&
                getClusterMean() != null &&
                getClusterOverall() != null;
    }

    private static LangevinBatchResultSet deepCopy(LangevinBatchResultSet original) {
        if (original == null) {
            return null;
        }
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(bos);
            out.writeObject(original);
            out.flush();

            ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray()));
            return (LangevinBatchResultSet) in.readObject();

        } catch (ClassNotFoundException | IOException e) {
            throw new RuntimeException("Deep copy failed", e);
        }
    }

}
