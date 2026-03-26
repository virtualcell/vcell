package cbit.vcell.simdata;

import cbit.vcell.math.ODESolverResultSetColumnDescription;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.solver.DataSymbolMetadata;
import cbit.vcell.solver.SimulationModelInfo;
import cbit.vcell.solver.ode.ODESimData;
import cbit.vcell.units.VCUnitDefinition;
import cbit.vcell.util.ColumnDescription;
import org.vcell.model.ssld.SsldUtils;

import java.io.*;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class LangevinSolverResultSet implements Serializable {

    private final LangevinBatchResultSet raw;
    public LangevinSolverResultSet(LangevinBatchResultSet raw) {
        this.raw = raw;
    }
    public final Map<String, SsldUtils.LangevinResult> metadataMap = new LinkedHashMap<>();


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
        }
        if(isAverageDataAvailable()) {
            ODESimData co = getAvg();
            populateMetadata(co);
            checkTrivial(co);
            co = getMin();
            checkTrivial(co);
            co = getMax();
            checkTrivial(co);
            co = getStd();
            checkTrivial(co);
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
                System.out.println("Ignoring LangevinResult token: " + columnName + ", qualifier missing");
                continue;
            }
            metadataMap.put(columnName, lr);
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
                    System.out.println("Failed to extract column: " + e.getMessage());
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

}
