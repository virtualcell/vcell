package org.vcell.restq.Simulations;

import cbit.vcell.server.SimulationStatusPersistent;

import java.util.HashMap;

public record SimulationStatusPersistentRecord(
        Status status,
        HashMap<Integer, Double> progressHash,
        String details,
        boolean hasData
) {

    public enum Status{
        UNKNOWN(0,"unknown"),
        NEVER_RAN(1,"never ran"),
        START_REQUESTED(2,"submitted..."),
        DISPATCHED(3,"dispatched..."),
        WAITING(4,"waiting: too many jobs"),
        QUEUED(5,"queued"),
        RUNNING(6,"running..."),
        COMPLETED(7,"completed"),
        FAILED(8,"failed"),
        STOP_REQUESTED(9,"stopping..."),
        STOPPED(10,"stopped"),
        NOT_SAVED(11,"not saved");

        public final String statusDescription;
        private final int index;
        Status(int index, String statusString){
            statusDescription = statusString;
            this.index = index;
        }

        public static Status statusFromIndex(int index){
            for(Status s : Status.values()){
                if(s.index == index){return s;}
            }
            return null;
        }

        public static Status statusFromString(String statusString){
            for (Status s: Status.values()){
                if(s.statusDescription.equals(statusString)){return s;}
            }
            return null;
        }
    }

    public static SimulationStatusPersistentRecord fromSimulationStatusPersistent(SimulationStatusPersistent s) {
        if (s == null) return null;
        return new SimulationStatusPersistentRecord(
                Status.statusFromString(s.getStatusString()),
                s.getProgressHash(),
                s.getDetails(),
                s.getHasData()
        );
    }
}
