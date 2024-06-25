package org.vcell.restq.Simulations;

import cbit.vcell.server.SimulationExecutionStatusPersistent;
import cbit.vcell.server.SimulationJobStatusPersistent;
import cbit.vcell.server.SimulationQueueEntryStatusPersistent;
import cbit.vcell.solver.VCSimulationIdentifier;
import cbit.vcell.solver.server.SimulationMessagePersistent;

import java.time.Instant;
import java.util.Date;

public record SimulationJobStatusPersistentRecord(
        Instant fieldTimeDataStamp,
        VCSimulationIdentifier fieldVCSimID,
        Instant fieldSubmitDate,
        SimulationJobStatusPersistent.SchedulerStatus fieldSchedulerStatus,
        SimulationMessagePersistent fieldSimulationMessage,
        int fieldTaskID,
        String fieldServerID,
        int fieldJobIndex,
        SimulationExecutionStatusPersistent fieldSimulationExecutionStatus,
        SimulationQueueEntryStatusPersistent fieldSimulationQueueEntryStatus
) {

    public static SimulationJobStatusPersistentRecord fromSimulationJobStatusPersistent(SimulationJobStatusPersistent s){
        return new SimulationJobStatusPersistentRecord(
                s.getTimeDateStamp().toInstant(),
                s.getVCSimulationIdentifier(),
                s.getSubmitDate().toInstant(),
                s.getSchedulerStatus(),
                s.getSimulationMessage(),
                s.getTaskID(),
                s.getServerID().toString(),
                s.getJobIndex(),
                s.getSimulationExecutionStatus(),
                s.getSimulationQueueEntryStatus()
        );
    }

}
