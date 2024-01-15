package org.vcell.api.common.events;

public class ExportHumanReadableDataSpec {
    public final String bioModelName;
    public String applicationName;
    public String simulationName;

    public ExportHumanReadableDataSpec(String bioModelName, String applicationName, String simulationName){
        this.bioModelName = bioModelName;
        this.applicationName = applicationName;
        this.simulationName = simulationName;
    }


}
