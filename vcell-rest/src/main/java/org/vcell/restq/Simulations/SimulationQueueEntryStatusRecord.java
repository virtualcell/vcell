package org.vcell.restq.Simulations;

import cbit.vcell.server.SimulationJobStatus;

import java.time.Instant;

public record SimulationQueueEntryStatusRecord(
        int fieldQueuePriority,
        Instant fieldQueueDate,
        SimulationJobStatus.SimulationQueueID fieldQueueID
) {
}
