package cbit.vcell.modeldb;

import cbit.vcell.export.server.ExportFormat;
import cbit.vcell.export.server.ExportSpecs;

import java.sql.Timestamp;

public record ExportHistoryRep(
        long jobID,
        long modelRef,
        ExportFormat exportFormat,
        Timestamp exportDate,
        String uri,
        ExportSpecs exportSpecs
) {
}
