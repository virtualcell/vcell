package cbit.vcell.exports;

import cbit.vcell.export.server.ExportEnums;
import cbit.vcell.export.server.ExportFormat;
import org.vcell.util.document.KeyValue;

import java.time.Instant;

public record ExportHistory(
        long exportJobID,
        KeyValue simulationRef,
        KeyValue bioModelRef,
        KeyValue mathModelRef,
        KeyValue mathRef,
        ExportFormat exportFormat,
        Instant exportDate,
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
        int selectedZSlice,
        boolean entireZStack,
        ExportEnums.ExportProgressType eventStatus
) {
}
