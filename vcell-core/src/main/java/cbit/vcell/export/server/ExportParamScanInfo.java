package cbit.vcell.export.server;

import cbit.vcell.solver.MathOverrides;
import cbit.vcell.solver.Simulation;
import org.vcell.util.Compare;
import org.vcell.util.Matchable;

import java.io.Serializable;
import java.util.Arrays;

public class ExportParamScanInfo implements Matchable, Serializable {
    private int[] paramScanJobIndexes;//these are the param scan job indexes we are possibly interested in
    private int defaultParamScanJobIndex;//this is the "selected" param scan simdata job index at the time this object was created, 0 if no param scan
    private String[] paramScanConstantNames;
    private String[][] paramScanConstantValues;


    public ExportParamScanInfo(int[] paramScanJobIndexes, int defaultParamScanJobIndex, String[] paramScanConstantNames, String[][] paramScanConstantValues) {
        this.paramScanJobIndexes = paramScanJobIndexes;
        this.defaultParamScanJobIndex = defaultParamScanJobIndex;
        this.paramScanConstantNames = paramScanConstantNames;
        this.paramScanConstantValues = paramScanConstantValues;
    }

    public static ExportParamScanInfo getParamScanInfo(Simulation simulation, int selectedParamScanJobIndex){
        int scanCount = simulation.getScanCount();
        if(scanCount == 1){//no parameter scan
            return null;
        }
        String[] scanConstantNames = simulation.getMathOverrides().getScannedConstantNames();
        Arrays.sort(scanConstantNames);
        int[] paramScanJobIndexes = new int[scanCount];
        String[][] scanConstValues = new String[scanCount][scanConstantNames.length];
        for (int i = 0; i < scanCount; i++) {
            paramScanJobIndexes[i] = i;
            for (int j = 0; j < scanConstantNames.length; j++) {
                String paramScanValue = simulation.getMathOverrides().getActualExpression(scanConstantNames[j], new MathOverrides.ScanIndex(i)).infix();
    //			System.out.println("ScanIndex="+i+" ScanConstName='"+scanConstantNames[j]+"' paramScanValue="+paramScanValue);
                scanConstValues[i][j] = paramScanValue;
            }
        }
        return new ExportParamScanInfo(paramScanJobIndexes, selectedParamScanJobIndex, scanConstantNames, scanConstValues);
    }

    public boolean compareEqual(Matchable obj) {
        if (obj instanceof ExportParamScanInfo) {
            ExportParamScanInfo exportParamScanInfo = (ExportParamScanInfo) obj;
            if (defaultParamScanJobIndex == exportParamScanInfo.defaultParamScanJobIndex &&
                    Compare.isEqualOrNull(paramScanJobIndexes, exportParamScanInfo.paramScanJobIndexes)) {

                for (int i = 0; paramScanConstantNames != null && i < paramScanConstantNames.length; i++) {
                    if (!paramScanConstantNames[i].equals(exportParamScanInfo.paramScanConstantNames[i])) {
                        return false;
                    }
                }
                for (int i = 0; paramScanConstantValues != null && i < paramScanConstantValues.length; i++) {
                    if (!paramScanConstantValues[i].equals(exportParamScanInfo.paramScanConstantValues[i])) {
                        return false;
                    }
                }

                return true;
            }
        }
        return false;

    }

    public int[] getParamScanJobIndexes() {
        return paramScanJobIndexes;
    }

    public int getDefaultParamScanJobIndex() {
        return defaultParamScanJobIndex;
    }

    public String[] getParamScanConstantNames() {
        return paramScanConstantNames;
    }

    public String[][] getParamScanConstantValues() {
        return paramScanConstantValues;
    }
}
