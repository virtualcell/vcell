package cbit.vcell.client.data;

public class SimlationExportDataRepresentation {
    public String exportDate;
    public String uri;
    public String jobID;
    public String dataID;
    public String simulationName;
    public String applicationName;
    public String biomodelName;
    public String variables;
    public String startAndEndTime;

    public SimlationExportDataRepresentation(String exportDate, String uri, String jobID, String dataID, String simulationName, String applicationName, String biomodelName, String variables, String startAndEndTime){
        this.exportDate = exportDate;
        this.uri = uri;
        this.jobID = jobID;
        this.dataID = dataID;
        this.simulationName = simulationName;
        this.applicationName = applicationName;
        this.biomodelName = biomodelName;
        this.variables = variables;
        this.startAndEndTime = startAndEndTime;
    }
}
