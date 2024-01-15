package cbit.vcell.export.server;

import org.vcell.api.common.events.ExportHumanReadableDataSpec;
import org.vcell.api.common.events.ExportTimeSpecs;

import java.io.Serializable;

public class HumanReadableExportData implements Serializable {
    public final String SimulationName;
    public final String BiomodelsName;
    public final String ApplicationName;
    public HumanReadableExportData(String simulationName, String applicationName, String biomodelsName){
        SimulationName = simulationName;
        ApplicationName = applicationName;
        BiomodelsName = biomodelsName;
    }

    public ExportHumanReadableDataSpec toJsonRep() {
        ExportHumanReadableDataSpec rep = new ExportHumanReadableDataSpec(BiomodelsName, ApplicationName, SimulationName);
        return rep;
    }
    public static HumanReadableExportData fromJsonRep(ExportHumanReadableDataSpec rep) {
        HumanReadableExportData humanReadableExportData = new HumanReadableExportData(rep.simulationName, rep.applicationName, rep.bioModelName);
        return humanReadableExportData;
    }
}
