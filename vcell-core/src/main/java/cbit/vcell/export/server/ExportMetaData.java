package cbit.vcell.export.server;

public class ExportMetaData {
    public final String SimulationName;
    public final String BiomodelsName;
    public final String ApplicationName;
    public String timeRange;

    public ExportMetaData(String simulationName, String applicationName, String biomodelsName){
        SimulationName = simulationName;
        ApplicationName = applicationName;
        BiomodelsName = biomodelsName;
    }

    public void setTimeRange(String startTime, String endTime){
        timeRange = "Start Time: " + startTime + ", End Time: " + endTime;
    }
}
