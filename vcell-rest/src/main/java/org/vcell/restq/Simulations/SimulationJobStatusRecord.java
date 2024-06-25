package org.vcell.restq.Simulations;

import cbit.vcell.server.SimulationExecutionStatus;
import cbit.vcell.server.SimulationJobStatus;
import cbit.vcell.server.SimulationQueueEntryStatus;
import cbit.vcell.solver.VCSimulationIdentifier;
import cbit.vcell.solver.server.SimulationMessage;

import java.time.Instant;
import java.time.ZoneId;

public record SimulationJobStatusRecord(
        Instant fieldTimeDataStamp,
        VCSimulationIdentifier fieldVCSimID,
        Instant fieldSubmitDate,
        SimulationJobStatus.SchedulerStatus fieldSchedulerStatus,
        SimulationMessage fieldSimulationMessage,
        int fieldTaskID,
        String fieldServerID,
        int fieldJobIndex,
        SimulationExecutionStatusRecord fieldSimulationExecutionStatus,
        SimulationQueueEntryStatusRecord fieldSimulationQueueEntryStatus
) {

    public static SimulationJobStatusRecord fromSimulationJobStatus(SimulationJobStatus s) {
        if (s == null) {return null;}
        return new SimulationJobStatusRecord(
                s.getTimeDateStamp() != null ? s.getTimeDateStamp().toInstant(): null,
                s.getVCSimulationIdentifier(),
                s.getSubmitDate() != null ? s.getSubmitDate().toInstant(): null,
                s.getSchedulerStatus(),
                s.getSimulationMessage(),
                s.getTaskID(),
                s.getServerID().toString(),
                s.getJobIndex(),
                SimulationExecutionStatusRecord.fromSimulationExecutionStatus(s.getSimulationExecutionStatus()),
                SimulationQueueEntryStatusRecord.fromStatusRecord(s.getSimulationQueueEntryStatus())
        );
    }

}
