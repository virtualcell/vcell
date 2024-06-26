package org.vcell.restq.Simulations.DTO;

public record StatusMessage(SimulationJobStatus jobStatus, String userName, Double progress, Double timepoint) {

}
