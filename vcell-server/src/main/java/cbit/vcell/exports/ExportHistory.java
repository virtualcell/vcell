package cbit.vcell.exports;

import cbit.vcell.export.server.ExportEnums;
import cbit.vcell.export.server.ExportFormat;
import org.vcell.util.document.KeyValue;

import java.sql.Timestamp;

public record ExportHistory(
        long exportJobID,
        KeyValue simulationRef,
        ExportFormat exportFormat,
        Timestamp exportDate,
        String uri,
        String simName,
//        String appName,
        String modelName,
        String[] variables,
//        List<MathOverrides.Element> overrides,
        double startTimeValue,
        double endTimeValue,
        String savedFileNameValue,
//        String applicationTypeValue,
        int zSliceStart,
        int zSliceEnd,
        ExportEnums.ExportProgressType eventStatus
) {
}
