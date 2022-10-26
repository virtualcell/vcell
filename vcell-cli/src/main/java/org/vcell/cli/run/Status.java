package org.vcell.cli.run;

public enum Status {
    RUNNING("Simulation still running"),
    SKIPPED("Simulation skipped"),
    SUCCEEDED("Simulation succeeded"),
    FAILED("Simulation failed"),
    ABORTED("Simulation aborted"),
    QUEUED("Simulation is waiting to be run.");

    private final String description;

    Status(String desc) {
        this.description = desc;
    }


}
