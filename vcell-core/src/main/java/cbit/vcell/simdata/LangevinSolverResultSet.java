package cbit.vcell.simdata;

import cbit.vcell.math.ODESolverResultSetColumnDescription;
import cbit.vcell.math.RowColumnResultSet;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.solver.DataSymbolMetadata;
import cbit.vcell.solver.SimulationModelInfo;
import cbit.vcell.solver.ode.ODESimData;
import cbit.vcell.solver.ode.ODESolverResultSet;
import cbit.vcell.units.VCUnitDefinition;
import cbit.vcell.util.ColumnDescription;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.model.ssld.SsldUtils;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.util.document.VCDataIdentifier;

import java.io.*;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class LangevinSolverResultSet implements Serializable {

    private static final Logger lg = LogManager.getLogger(LangevinSolverResultSet.class);

    private final LangevinBatchResultSet raw;
    public LangevinSolverResultSet(LangevinBatchResultSet raw) {
        this.raw = raw;
    }
    public final Map<String, SsldUtils.LangevinResult> metadataMap = new LinkedHashMap<>();

    // derived data, based on raw, populated in postProcess()
    private ODESimData clusterMass = null;
    private double maxClusterMassOverall;      // max mass across all sizes & times
    private double maxClusterCountOverall;     // max count across all sizes & times

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
    public ODESimData getClusterMass() {
        return clusterMass;
    }
    public ODESimData getClusterMean() {
        return raw == null ? null : raw.getOdeSimDataClusterMean();
    }
    public ODESimData getClusterOverall() {
        return raw == null ? null : raw.getOdeSimDataClusterOverall();
    }

    public double getMaxClusterMassOverall() {
        return maxClusterMassOverall;
    }
    public double getMaxClusterCountOverall() {
        return maxClusterCountOverall;
    }

    public ColumnDescription getColumnDescriptionByName(String columnName) {
        if(raw == null || raw.getOdeSimDataAvg() == null) {
            return null;
        }
        int index = raw.getOdeSimDataAvg().findColumn(columnName);
        ColumnDescription cd = raw.getOdeSimDataAvg().getColumnDescriptions(index);
        return cd;
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

    public void postProcess() {
        if(isClusterDataAvailable()) {
            ODESimData co = getClusterOverall();
            checkTrivial(co);
            co = getClusterMean();
            checkTrivial(co);
            co = getClusterCounts();
            checkTrivial(co);
            computeClusterMass();
            co = getClusterMass();
            checkTrivial(co);
        }
        if(isAverageDataAvailable()) {
            ODESimData co = getAvg();
            populateMetadata(co);
            checkTrivial(co);
            // if avg is trivial, then min, max and std will be trivial too
//            co = getMin();
//            checkTrivial(co);
//            co = getMax();
//            checkTrivial(co);
//            co = getStd();
//            checkTrivial(co);
        }
    }
    private void populateMetadata(ODESimData co) {
        // from solver-generated observable (ColumnDescription.name) extract molecule, site and state names
        ColumnDescription[] cds = co.getColumnDescriptions();
        for(ColumnDescription cd : cds) {
            String columnName = cd.getName();
            SimulationModelInfo.ModelCategoryType filterCategory = null;	// parse name to find the filter category
            SsldUtils.LangevinResult lr = SsldUtils.LangevinResult.fromString(columnName);
            if(lr.qualifier.equals(SsldUtils.Qualifier.NONE)) {
                lg.warn("Ignoring LangevinResult token: " + columnName + ", qualifier missing");
                continue;
            }
            metadataMap.put(columnName, lr);
        }
    }

    private void computeClusterMass() {
        ODESimData counts = getClusterCounts();
        if (counts == null) {
            clusterMass = null;
            return;
        }
        try {
            // 1. Deep copy counts via serialization
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(bos);
            out.writeObject(counts);
            out.flush();
            ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray()));
            clusterMass = (ODESimData) in.readObject();

            // take advantage of the fact that we iterate here through all the columns and rows,
            // to compute max mass and count across all sizes and times
            maxClusterMassOverall = 0;
            maxClusterCountOverall = 0;

            // 2. Modify numeric values in-place
            ColumnDescription[] cds = clusterMass.getColumnDescriptions();
            int nCols = cds.length;
            for (int c = 0; c < nCols; c++) {
                ColumnDescription cd = cds[c];
                String name = cd.getName();
                if (name.equals("t")) {
                    continue; // time column unchanged
                }
                int clusterSize = Integer.parseInt(name);
                double[] series = clusterMass.extractColumn(c); // reference to internal array
                for (int i = 0; i < series.length; i++) {
                    double count = series[i];
                    double mass = count * clusterSize;
                    clusterMass.setValue(i, c, mass);
                    if (count > maxClusterCountOverall) {   // compute global maxima
                        maxClusterCountOverall = count;
                    }
                    if (mass > maxClusterMassOverall) {
                        maxClusterMassOverall = mass;
                    }
                }
            }
        } catch (Exception e) {
            lg.error("Failed to compute cluster mass", e);
            clusterMass = null;
        }
    }

    private static void checkTrivial(ODESimData co) {
        ColumnDescription[] cds = co.getColumnDescriptions();
        for(ColumnDescription columnDescription : cds) {
            if (columnDescription instanceof ODESolverResultSetColumnDescription cd) {
                double[] data = null;
                int index = co.findColumn(cd.getName());
                try {
                    data = co.extractColumn(index);
                } catch (ExpressionException e) {
                    lg.warn("Failed to extract column: " + e.getMessage());
                    continue;
                }
                if(data == null || data.length == 0) {
                    continue;
                }
                double initial = data[0];
                boolean isTrivial = true;
                for(double d : data) {
                    if(initial != d) {
                        isTrivial = false;
                        break;	// one mismatch is enough to know it's not trivial
                    }
                }
                cd.setIsTrivial(isTrivial);
            }
        }
    }

    public static double[] getSeries(ODESimData data, String columnName) throws ExpressionException {
        int idx = data.findColumn(columnName);
        if (idx < 0) return null;
        return data.extractColumn(idx);
    }

}
