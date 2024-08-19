package org.vcell.restq.Simulations.DTO;

import java.util.ArrayList;

public record StatusMessage(SimulationJobStatus jobStatus, String userName, Double progress, Double timepoint) {


    public static ArrayList<StatusMessage> convertServerStatusMessages(ArrayList<cbit.vcell.message.messages.StatusMessage> statusMessages){
        ArrayList<StatusMessage> dtoStatusMessage = new ArrayList<>();
        for (cbit.vcell.message.messages.StatusMessage statusMessage: statusMessages){
            dtoStatusMessage.add(new StatusMessage(SimulationJobStatus.fromSimulationJobStatus(statusMessage.getJobStatus()), statusMessage.getUserName(),
                    statusMessage.getProgress(), statusMessage.getTimePoint()));
        }
        return dtoStatusMessage;
    }
}
