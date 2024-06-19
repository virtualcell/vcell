package org.vcell.restq.Simulations;

import cbit.vcell.server.SimulationJobStatus;

public record StatusMessage(SimulationJobStatus jobStatus0, String userName0, Double progress0, Double timepoint0) {

}
