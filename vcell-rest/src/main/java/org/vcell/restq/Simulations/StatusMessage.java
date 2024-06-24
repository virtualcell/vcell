package org.vcell.restq.Simulations;

import cbit.vcell.server.SimulationJobStatus;

public record StatusMessage(SimulationJobStatusRecord jobStatus, String userName, Double progress, Double timepoint) {

}
