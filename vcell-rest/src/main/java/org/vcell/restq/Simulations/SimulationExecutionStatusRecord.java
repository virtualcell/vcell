package org.vcell.restq.Simulations;

import cbit.vcell.server.HtcJobID;
import cbit.vcell.server.SimulationExecutionStatus;

import java.time.Instant;

public record SimulationExecutionStatusRecord(
        Instant fieldStartDate,
        Instant fieldLatestUpdateDate,
        Instant fieldEndDate,
        String fieldComputeHost,
        boolean fieldHasData,
        HtcJobID fieldHtcJobID
) {

    public static SimulationExecutionStatusRecord fromSimulationExecutionStatus(SimulationExecutionStatus status) {
        if (status == null) {return null;}
        return new SimulationExecutionStatusRecord(
                status.getStartDate() != null ? status.getStartDate().toInstant(): null,
                status.getLatestUpdateDate() != null ? status.getLatestUpdateDate().toInstant() : null,
                status.getEndDate() != null ? status.getEndDate().toInstant(): null,
                status.getComputeHost(),
                status.hasData(),
                status.getHtcJobID()
        );
    }
}
