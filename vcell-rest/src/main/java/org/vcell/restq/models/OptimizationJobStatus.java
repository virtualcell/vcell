package org.vcell.restq.models;

import org.vcell.optimization.OptJobStatus;
import org.vcell.optimization.jtd.OptProgressReport;
import org.vcell.optimization.jtd.Vcellopt;
import org.vcell.util.document.KeyValue;

public record OptimizationJobStatus(
    KeyValue id,
    OptJobStatus status,
    String statusMessage,
    String htcJobId,
    OptProgressReport progressReport,
    Vcellopt results
) {}
