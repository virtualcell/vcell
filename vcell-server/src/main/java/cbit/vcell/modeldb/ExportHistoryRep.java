package cbit.vcell.modeldb;

import cbit.vcell.export.server.ExportFormat;
import cbit.vcell.export.server.ExportSpecs;
import cbit.vcell.export.server.HumanReadableExportData;
import org.vcell.util.document.KeyValue;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

public record ExportHistoryRep(
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
}
