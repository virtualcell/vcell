package org.vcell.restq.Simulations;

import cbit.vcell.server.SimulationJobStatus;
import cbit.vcell.server.SimulationQueueEntryStatus;

import java.time.Instant;

public record SimulationQueueEntryStatusRecord(
        int fieldQueuePriority,
        Instant fieldQueueDate,
        SimulationJobStatus.SimulationQueueID fieldQueueID
) {

    public static SimulationQueueEntryStatusRecord fromStatusRecord(SimulationQueueEntryStatus simulationQueueEntryStatus) {
        if (simulationQueueEntryStatus == null) {return null;}
        return new SimulationQueueEntryStatusRecord(
                simulationQueueEntryStatus.getQueuePriority(),
                simulationQueueEntryStatus.getQueueDate().toInstant(),
                simulationQueueEntryStatus.getQueueID()
        );
    }
}
