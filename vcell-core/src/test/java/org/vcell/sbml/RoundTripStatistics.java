package org.vcell.sbml;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RoundTripStatistics {
    private int numBioModelsProcessed;
    private int numBioModelsSucceeded;
    private Map<SEDMLExporterNightlyRoundTrip.SEDML_FAULT, Integer> failureTypeToNumMap;

    public RoundTripStatistics(){
        this.numBioModelsProcessed = 0;
        this.numBioModelsSucceeded = 0;
        this.failureTypeToNumMap = new HashMap<>();
    }

    public void recordFailureResult(RoundTripException e){
        this.numBioModelsProcessed += 1;
        if (!this.failureTypeToNumMap.containsKey(e.faultType))
            this.failureTypeToNumMap.put(e.faultType, 0);
        this.failureTypeToNumMap.put(e.faultType, this.failureTypeToNumMap.get(e.faultType) + 1);
    }

    public void recordSuccessResult(){
        this.numBioModelsProcessed += 1;
        this.numBioModelsSucceeded += 1;
    }

    public void exportResultsToWriter(BufferedWriter writer) throws IOException {
        writer.write(this.getResults());
        writer.flush();
    }

    public String getResults(){
        String str = "";
        str += String.format("Results of Round Trip:\n");
        str += String.format("  BioModels Processed:\t%d\n", this.numBioModelsProcessed);
        str += String.format("  BioModels Succeeded:\t%d\n", this.numBioModelsSucceeded);
        for (SEDMLExporterNightlyRoundTrip.SEDML_FAULT fault : this.failureTypeToNumMap.keySet()){
            str += String.format("  - BioModels failed with: '%s' fault: %d", fault.name(),
                    this.failureTypeToNumMap.get(fault));
        }
        return str;
    }
}
