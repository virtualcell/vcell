package cbit.vcell.exports;

import cbit.vcell.export.server.ExportFormat;
import cbit.vcell.export.server.ExportSpecs;
import cbit.vcell.export.server.HumanReadableExportData;
import cbit.vcell.solver.VCSimulationDataIdentifier;
import org.vcell.util.document.KeyValue;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

public record ExportHistoryDBRep(
        int jobID,
        KeyValue simulationRef,
        ExportFormat exportFormat,
        Timestamp exportDate,
        String uri,
        String dataIdValue,
        String simName,
        String appName,
        String bioName,
        String[] variables,
        List<HumanReadableExportData.DifferentParameterValues> parameterValues,
        double startTimeValue,
        double endTimeValue,
        String savedFileNameValue,
        String applicationTypeValue,
        boolean nonSpatialValue,
        int zSlicesValue,
        int tSlicesValue,
        int numVariablesValue
) {
    public boolean equals(ExportHistoryDBRep other){
        return this.jobID == other.jobID &&
                this.simulationRef.equals(other.simulationRef) &&
                this.exportFormat.equals(other.exportFormat) &&
                this.exportDate.equals(other.exportDate) &&
                this.uri.equals(other.uri) &&
                this.dataIdValue.equals(other.dataIdValue) &&
                this.simName.equals(other.simName) &&
                this.appName.equals(other.appName) &&
                this.bioName.equals(other.bioName) &&
                List.of(this.variables).equals(List.of(other.variables)) &&
                this.parameterValues.equals(other.parameterValues) &&
                Double.compare(this.startTimeValue, other.startTimeValue) == 0 &&
                Double.compare(this.endTimeValue, other.endTimeValue) == 0 &&
                this.savedFileNameValue.equals(other.savedFileNameValue) &&
                this.applicationTypeValue.equals(other.applicationTypeValue) &&
                this.nonSpatialValue == other.nonSpatialValue &&
                this.zSlicesValue == other.zSlicesValue &&
                this.tSlicesValue == other.tSlicesValue &&
                this.numVariablesValue == other.numVariablesValue;
    }

    public static ExportHistoryDBRep fromExportSpec(ExportSpecs exportSpecs, String uri, int jobID, VCSimulationDataIdentifier vcSimulationIdentifier){
        double[] times = exportSpecs.getTimeSpecs().getAllTimes();
        Timestamp timestamp = new Timestamp(Instant.now().toEpochMilli());
        return new ExportHistoryDBRep(
                jobID,
                vcSimulationIdentifier.getSimulationKey(),
                exportSpecs.getFormat(),
                timestamp,
                uri,
                vcSimulationIdentifier.getDataKey().toString(),
                exportSpecs.getSimulationName(),
                exportSpecs.getHumanReadableExportData().applicationName,
                exportSpecs.getHumanReadableExportData().biomodelName,
                exportSpecs.getVariableSpecs().getVariableNames(),
                exportSpecs.getHumanReadableExportData().differentParameterValues,
                times[exportSpecs.getTimeSpecs().getBeginTimeIndex()],
                times[exportSpecs.getTimeSpecs().getEndTimeIndex()],
                exportSpecs.getHumanReadableExportData().serverSavedFileName,
                exportSpecs.getHumanReadableExportData().applicationType,
                exportSpecs.getHumanReadableExportData().nonSpatial,
                exportSpecs.getHumanReadableExportData().zSlices,
                exportSpecs.getHumanReadableExportData().tSlices,
                exportSpecs.getHumanReadableExportData().numChannels
        );
    }
}
