package cbit.vcell.export.server;

import cbit.vcell.solver.VCSimulationDataIdentifier;
import cbit.vcell.solver.VCSimulationIdentifier;
import org.vcell.util.Compare;
import org.vcell.util.Matchable;
import org.vcell.util.document.VCDataIdentifier;

import java.io.Serializable;

public class SimNameSimDataID implements Matchable, Serializable {
    private String simulationName;
    private VCSimulationIdentifier vcSimulationIdentifier;
    private ExportParamScanInfo exportParamScanInfo;

    public SimNameSimDataID(String simulationName, VCSimulationIdentifier vcSimulationIdentifier, ExportParamScanInfo exportParamScanInfo) {
        this.simulationName = simulationName;
        this.vcSimulationIdentifier = vcSimulationIdentifier;
        this.exportParamScanInfo = exportParamScanInfo;
    }

    public VCDataIdentifier getVCDataIdentifier(int paramScanJobIndex) {
        if (exportParamScanInfo == null && paramScanJobIndex > 0) {
            throw new IllegalArgumentException("Error SimNameSimDataID.getVCDataIdentifier: jobIndex > 0 unexpected with no parameter scan");
        } else if (exportParamScanInfo != null && paramScanJobIndex >= exportParamScanInfo.getParamScanJobIndexes().length) {
            throw new IllegalArgumentException("Error SimNameSimDataID.getVCDataIdentifier: jobIndex > parameter scan count");
        }
        return new VCSimulationDataIdentifier(vcSimulationIdentifier, paramScanJobIndex);
    }

    public int getDefaultJobIndex() {
        return (exportParamScanInfo == null ? 0 : exportParamScanInfo.getDefaultParamScanJobIndex());
    }

    public ExportParamScanInfo getExportParamScanInfo() {
        return exportParamScanInfo;
    }

    public String getSimulationName() {
        return simulationName;
    }

    public boolean compareEqual(Matchable obj) {
        if (obj instanceof SimNameSimDataID) {
            SimNameSimDataID simNameSimDataID = (SimNameSimDataID) obj;
            if (
                    simulationName.equals(simNameSimDataID.getSimulationName()) &&
                            vcSimulationIdentifier.equals(simNameSimDataID.vcSimulationIdentifier) &&
                            Compare.isEqualOrNull(exportParamScanInfo, simNameSimDataID.getExportParamScanInfo())) {
                return true;
            }
        }
        return false;
    }
}
