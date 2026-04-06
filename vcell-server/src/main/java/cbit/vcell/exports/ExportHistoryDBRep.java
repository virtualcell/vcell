package cbit.vcell.exports;

import cbit.vcell.export.server.ExportEnums;
import cbit.vcell.export.server.ExportFormat;
import cbit.vcell.export.server.ExportSpecs;
import cbit.vcell.export.server.HumanReadableExportData;
import cbit.vcell.solver.VCSimulationDataIdentifier;
import org.vcell.util.document.KeyValue;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

public record ExportHistoryDBRep(
        long jobID,
        KeyValue bioModelRef,
        KeyValue mathModelRef,
        KeyValue simulationRef,
        KeyValue mathRef,
        ExportFormat exportFormat,
        Timestamp exportDate,
        String uri,
        String[] variables,
        double startTimeValue,
        double endTimeValue,
        boolean entireZStack,
        int selectedZSlice,
        String savedFileNameValue,
        ExportEnums.ExportProgressType eventStatus
) {
    public boolean equals(ExportHistoryDBRep other){
        return this.jobID == other.jobID &&
                this.bioModelRef == other.bioModelRef() &&
                this.mathModelRef() == other.mathModelRef() &&
                this.simulationRef.equals(other.simulationRef) &&
                this.exportFormat.equals(other.exportFormat) &&
                this.exportDate.equals(other.exportDate) &&
                this.uri.equals(other.uri) &&
                List.of(this.variables).equals(List.of(other.variables)) &&
                Double.compare(this.startTimeValue, other.startTimeValue) == 0 &&
                Double.compare(this.endTimeValue, other.endTimeValue) == 0 &&
                this.savedFileNameValue.equals(other.savedFileNameValue) &&
                this.eventStatus.equals(other.eventStatus) &&
                this.entireZStack == other.entireZStack &&
                this.selectedZSlice == other.selectedZSlice;
    }

    public static ExportHistoryDBRep fromExportSpec(ExportSpecs exportSpecs, String url, long jobID, VCSimulationDataIdentifier simDataId, ExportEnums.ExportProgressType eventStatus) {
        double[] times = exportSpecs.getTimeSpecs().getAllTimes();
        boolean entireZStack = exportSpecs.getGeometrySpecs().getMode() == ExportEnums.GeometryMode.GEOMETRY_FULL;
        return new ExportHistoryDBRep(
                jobID,
                exportSpecs.getBioModelKey(),
                exportSpecs.getMathModelKey(),
                simDataId.getSimulationKey(),
                exportSpecs.getMathDescriptionKey(),
                exportSpecs.getFormat(),
                Timestamp.from(Instant.now()),
                url,
                exportSpecs.getVariableSpecs().getVariableNames(),
                times[exportSpecs.getTimeSpecs().getBeginTimeIndex()],
                times[exportSpecs.getTimeSpecs().getEndTimeIndex()],
                entireZStack,
                entireZStack ? exportSpecs.getGeometrySpecs().getSliceNumber() : -1,
                null,
                eventStatus
        );
    }
}
