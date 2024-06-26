package org.vcell.restq.Simulations.DTO;

import cbit.vcell.server.HtcJobID;

import java.time.Instant;

public record SimulationExecutionStatus(
        Instant fieldStartDate,
        Instant fieldLatestUpdateDate,
        Instant fieldEndDate,
        String fieldComputeHost,
        boolean fieldHasData,
        HtcJobID fieldHtcJobID
) {

    public static SimulationExecutionStatus fromSimulationExecutionStatus(cbit.vcell.server.SimulationExecutionStatus status) {
        if (status == null) {return null;}
        return new SimulationExecutionStatus(
                status.getStartDate() != null ? status.getStartDate().toInstant(): null,
                status.getLatestUpdateDate() != null ? status.getLatestUpdateDate().toInstant() : null,
                status.getEndDate() != null ? status.getEndDate().toInstant(): null,
                status.getComputeHost(),
                status.hasData(),
                status.getHtcJobID()
        );
    }
}
