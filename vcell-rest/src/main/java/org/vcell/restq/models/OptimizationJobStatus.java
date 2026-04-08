package org.vcell.restq.models;

import org.vcell.optimization.jtd.OptProgressReport;
import org.vcell.optimization.jtd.Vcellopt;

public record OptimizationJobStatus(
    String id,
    OptJobStatus status,
    String statusMessage,
    String htcJobId,
    OptProgressReport progressReport,
    Vcellopt results
) {}
