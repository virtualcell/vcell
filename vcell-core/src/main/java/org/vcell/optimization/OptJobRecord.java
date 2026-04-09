package org.vcell.optimization;

import org.vcell.util.document.KeyValue;

import java.time.Instant;

/**
 * Database row record for the vc_optjob table.
 * Shared between vcell-server (OptJobTable) and vcell-rest (OptimizationRestService).
 */
public record OptJobRecord(
        KeyValue id,
        KeyValue ownerKey,
        OptJobStatus status,
        String optProblemFile,
        String optOutputFile,
        String optReportFile,
        String htcJobId,
        String statusMessage,
        Instant insertDate,
        Instant updateDate
) {}
