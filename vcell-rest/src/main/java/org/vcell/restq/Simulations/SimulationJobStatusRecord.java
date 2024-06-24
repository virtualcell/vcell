package org.vcell.restq.Simulations;

import cbit.vcell.server.SimulationExecutionStatus;
import cbit.vcell.server.SimulationJobStatus;
import cbit.vcell.server.SimulationQueueEntryStatus;
import cbit.vcell.solver.VCSimulationIdentifier;
import cbit.vcell.solver.server.SimulationMessage;

import java.time.LocalTime;
import java.time.ZoneId;

public record SimulationJobStatusRecord(
        LocalTime fieldTimeDataStamp,
        VCSimulationIdentifier fieldVCSimID,
        LocalTime fieldSubmitDate,
        SimulationJobStatus.SchedulerStatus fieldSchedulerStatus,
        SimulationMessage fieldSimulationMessage,
        int fieldTaskID,
        String fieldServerID,
        int fieldJobIndex,
        SimulationExecutionStatus fieldSimulationExecutionStatus,
        SimulationQueueEntryStatus fieldSimulationQueueEntryStatus
) {

    public static SimulationJobStatusRecord fromSimulationJobStatus(SimulationJobStatus s) {
        if (s == null) {return null;}
        return new SimulationJobStatusRecord(
                s.getTimeDateStamp() != null ? s.getTimeDateStamp().toInstant().atZone(ZoneId.systemDefault()).toLocalTime(): null,
                s.getVCSimulationIdentifier(),
                s.getSubmitDate() != null ? s.getSubmitDate().toInstant().atZone(ZoneId.systemDefault()).toLocalTime(): null,
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
