package org.vcell.restq.Simulations.DTO;

import cbit.vcell.server.SimulationJobStatus;

import java.time.Instant;

public record SimulationQueueEntryStatus(
        int fieldQueuePriority,
        Instant fieldQueueDate,
        SimulationJobStatus.SimulationQueueID fieldQueueID
) {

    public static SimulationQueueEntryStatus fromStatusRecord(cbit.vcell.server.SimulationQueueEntryStatus simulationQueueEntryStatus) {
        if (simulationQueueEntryStatus == null) {return null;}
        return new SimulationQueueEntryStatus(
                simulationQueueEntryStatus.getQueuePriority(),
                simulationQueueEntryStatus.getQueueDate().toInstant(),
                simulationQueueEntryStatus.getQueueID()
        );
    }
}
