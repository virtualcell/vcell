package org.vcell.restq.Simulations.DTO;

import cbit.vcell.solver.VCSimulationIdentifier;
import cbit.vcell.solver.server.SimulationMessage;

import java.time.Instant;

public record SimulationJobStatus(
        Instant fieldTimeDataStamp,
        VCSimulationIdentifier fieldVCSimID,
        Instant fieldSubmitDate,
        cbit.vcell.server.SimulationJobStatus.SchedulerStatus fieldSchedulerStatus,
        SimulationMessage fieldSimulationMessage,
        int fieldTaskID,
        String fieldServerID,
        int fieldJobIndex,
        SimulationExecutionStatus fieldSimulationExecutionStatus,
        SimulationQueueEntryStatus fieldSimulationQueueEntryStatus
) {

    public static SimulationJobStatus fromSimulationJobStatus(cbit.vcell.server.SimulationJobStatus s) {
        if (s == null) {return null;}
        return new SimulationJobStatus(
                s.getTimeDateStamp() != null ? s.getTimeDateStamp().toInstant(): null,
                s.getVCSimulationIdentifier(),
                s.getSubmitDate() != null ? s.getSubmitDate().toInstant(): null,
                s.getSchedulerStatus(),
                s.getSimulationMessage(),
                s.getTaskID(),
                s.getServerID().toString(),
                s.getJobIndex(),
                SimulationExecutionStatus.fromSimulationExecutionStatus(s.getSimulationExecutionStatus()),
                SimulationQueueEntryStatus.fromStatusRecord(s.getSimulationQueueEntryStatus())
        );
    }

}
