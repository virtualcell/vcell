package org.vcell.api.utils;

import cbit.rmi.event.DataJobEvent;
import cbit.rmi.event.ExportEvent;
import cbit.rmi.event.SimulationJobStatusEvent;
import cbit.vcell.export.server.HumanReadableExportData;
import cbit.vcell.export.server.TimeSpecs;
import cbit.vcell.export.server.VariableSpecs;
import cbit.vcell.server.*;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.VCSimulationIdentifier;
import cbit.vcell.solver.server.SimulationMessage;
import org.vcell.api.common.SimpleJobStatusRepresentation;
import org.vcell.api.common.events.*;
import org.vcell.util.document.*;

import java.util.Date;

public class DTOOldAPI {
    public static DataJobEventRepresentation dataJobRepToJsonRep(DataJobEvent dataJobEvent) {
        int eventType = dataJobEvent.getEventTypeID();
        Double progress = dataJobEvent.getProgress();
        String username = dataJobEvent.getVcDataJobID().getJobOwner().getName();
        String userkey = dataJobEvent.getVcDataJobID().getJobOwner().getID().toString();
        long jobid = dataJobEvent.getVcDataJobID().getJobID();
        boolean isBackgroundTask = dataJobEvent.getVcDataJobID().isBackgroundTask();
        String dataIdString = dataJobEvent.getDataIdString();
        String dataKey = dataJobEvent.getDataKey().toString();

        DataJobEventRepresentation rep = new DataJobEventRepresentation(
                eventType, progress, username, userkey, jobid, isBackgroundTask, dataIdString, dataKey);
        return rep;
    }

    public static DataJobEvent dataJobEventFromJsonRep(Object eventSource, DataJobEventRepresentation eventRep) {
        Double progress = eventRep.progress;
        org.vcell.util.document.User owner = new User(eventRep.username,new KeyValue(eventRep.userkey));
        VCDataJobID dataJobID = new VCDataJobID(eventRep.jobid, owner, eventRep.isBackgroundTask);
        int eventType = eventRep.eventType;
        KeyValue dataKey = new KeyValue(eventRep.dataKey);
        String dataIdString = eventRep.dataIdString;
        DataJobEvent dataJobEvent = new DataJobEvent(dataJobID, eventType, dataKey, dataIdString, progress);
        return dataJobEvent;
    }

    public static ExportEvent exportEventFromJsonRep(Object eventSource, ExportEventRepresentation rep) {
        User user = new User(rep.username, new KeyValue(rep.userkey));
        TimeSpecs timeSpecs = null;
        if (rep.exportTimeSpecs!=null) {
            timeSpecs = timeSpecsFromJsonRep(rep.exportTimeSpecs);
        }
        VariableSpecs variableSpecs = null;
        if (rep.exportVariableSpecs!=null) {
            variableSpecs = variableSpecsFromJsonRep(rep.exportVariableSpecs);
        }
        HumanReadableExportData humanReadableExportData1 = null;
        if (rep.exportHumanReadableDataSpec!=null){
            humanReadableExportData1 = humanReadableExportDataFromJsonRep(rep.exportHumanReadableDataSpec);
        }
        ExportEvent event = new ExportEvent(
                eventSource, rep.jobid, user,
                rep.dataIdString, new KeyValue(rep.dataKey), rep.eventType,
                rep.format, rep.location, rep.progress,
                timeSpecs, variableSpecs);
        event.setHumanReadableExportData(humanReadableExportData1);
        return event;
    }

    public static ExportEventRepresentation exportEventToJsonRep(ExportEvent event) {
        ExportTimeSpecs exportTimeSpecs = null;
        if (event.getTimeSpecs()!=null) {
            exportTimeSpecs = exportTimeSpecsToJsonRep(event.getTimeSpecs());
        }
        ExportVariableSpecs exportVariableSpecs = null;
        if (event.getVariableSpecs()!=null) {
            exportVariableSpecs = variableSpecsToJsonRep(event.getVariableSpecs());
        }
        ExportHumanReadableDataSpec exportHumanReadableDataSpec = null;
        if (event.getHumanReadableData() != null){
            exportHumanReadableDataSpec = humanReadableExportDataToJsonRep(event.getHumanReadableData());
        }

        return new ExportEventRepresentation(
                event.getEventTypeID(), event.getProgress(), event.getFormat(),
                event.getLocation(), event.getUser().getName(), event.getUser().getID().toString(), event.getJobID(),
                event.getDataIdString(), event.getDataKey().toString(),
                exportTimeSpecs, exportVariableSpecs, exportHumanReadableDataSpec);
    }

    public static ExportTimeSpecs exportTimeSpecsToJsonRep(TimeSpecs timeSpecs) {
        ExportTimeSpecs rep = new ExportTimeSpecs(timeSpecs.getBeginTimeIndex(), timeSpecs.getEndTimeIndex(),
                timeSpecs.getAllTimes(), timeSpecs.getModeID());
        return rep;
    }
    public static TimeSpecs timeSpecsFromJsonRep(ExportTimeSpecs rep) {
        TimeSpecs timeSpecs = new TimeSpecs(rep.beginTimeIndex, rep.endTimeIndex, rep.allTimes, rep.modeID);
        return timeSpecs;
    }


    public static ExportVariableSpecs variableSpecsToJsonRep(VariableSpecs variableSpecs) {
        ExportVariableSpecs rep = new ExportVariableSpecs(variableSpecs.getVariableNames(), variableSpecs.getModeID());
        return rep;
    }

    public static VariableSpecs variableSpecsFromJsonRep(ExportVariableSpecs rep) {
        VariableSpecs variableSpecs = new VariableSpecs(rep.variableNames, rep.modeID);
        return variableSpecs;
    }

    public static ExportHumanReadableDataSpec humanReadableExportDataToJsonRep(HumanReadableExportData hr) {
        return new ExportHumanReadableDataSpec(hr.biomodelName, hr.applicationName, hr.simulationName, hr.differentParameterValues, hr.serverSavedFileName, hr.applicationType,
                hr.nonSpatial, hr.subVolume, hr.zSlices, hr.tSlices, hr.numChannels);
    }

    public static HumanReadableExportData humanReadableExportDataFromJsonRep(ExportHumanReadableDataSpec rep) {
        HumanReadableExportData hre = new HumanReadableExportData(rep.simulationName, rep.applicationName, rep.bioModelName, rep.differentParameterValues,
                rep.serverSavedFileName, rep.applicationType, rep.nonSpatial, rep.subVolume);
        hre.zSlices = rep.zSlices;
        hre.tSlices = rep.tSlices;
        hre.numChannels = rep.numChannels;
        return hre;
    }

    public static SimulationJobStatusEvent simulationJobStatusEventFromJsonRep(Object eventSource, SimulationJobStatusEventRepresentation eventRep) {
        String simid = Simulation.createSimulationID(new KeyValue(eventRep.jobStatus.simulationKey));
        int jobIndex = eventRep.jobStatus.jobIndex;
        int taskID = eventRep.jobStatus.taskID;
        VCellServerID serverID = VCellServerID.getServerID(eventRep.jobStatus.vcellServerID);
        KeyValue simkey = new KeyValue(eventRep.jobStatus.simulationKey);
        User owner = new User(eventRep.jobStatus.owner_userid,new KeyValue(eventRep.jobStatus.onwer_userkey));
        VCSimulationIdentifier vcSimID = new VCSimulationIdentifier(simkey, owner);
        Date submitDate = eventRep.jobStatus.submitDate;

        SimulationJobStatus.SchedulerStatus schedulerStatus = null;
        if (eventRep.jobStatus.schedulerStatus!=null) {
            schedulerStatus = SimulationJobStatus.SchedulerStatus.valueOf(eventRep.jobStatus.schedulerStatus.name());
        }

        HtcJobID htcJobID = null;
        Long htcJobNumber = eventRep.jobStatus.htcJobNumber;
        SimulationJobStatusRepresentation.BatchSystemType htcBatchSystemType = eventRep.jobStatus.htcBatchSystemType;
        if (htcJobNumber!=null) {
            htcJobID = new HtcJobID(htcJobNumber.toString(), HtcJobID.BatchSystemType.valueOf(htcBatchSystemType.name()));
        }

        SimulationMessage simMessage = null;
        SimulationMessage.DetailedState detailedState = SimulationMessage.DetailedState.valueOf(eventRep.jobStatus.detailedState.name());
        String message = eventRep.jobStatus.detailedStateMessage;
        if (detailedState!=null) {
            simMessage = SimulationMessage.create(detailedState, message, htcJobID);
        }

        SimulationQueueEntryStatus simQueueStatus = null;
        Date queueDate = eventRep.jobStatus.queueDate;
        Integer queuePriority = eventRep.jobStatus.queuePriority;
        SimulationJobStatusRepresentation.SimulationQueueID queueId2 = eventRep.jobStatus.queueId;
        if (queueDate!=null && queuePriority!=null) {
            simQueueStatus = new SimulationQueueEntryStatus(queueDate,queuePriority, SimulationJobStatus.SimulationQueueID.valueOf(queueId2.name()));
        }

        SimulationExecutionStatus simExeStatus = null;
        Date startDate = eventRep.jobStatus.simexe_startDate;
        String computeHost = eventRep.jobStatus.computeHost;
        Date latestUpdateDate = eventRep.jobStatus.simexe_latestUpdateDate;
        Date endDate = eventRep.jobStatus.simexe_endDate;
        Boolean hasData = eventRep.jobStatus.hasData;
        if (latestUpdateDate!=null) {
            simExeStatus = new SimulationExecutionStatus(startDate, computeHost, latestUpdateDate, endDate, hasData, htcJobID);
        }

        SimulationJobStatus jobStatus = new SimulationJobStatus(
                serverID,vcSimID,jobIndex,
                submitDate,schedulerStatus,taskID,
                simMessage,simQueueStatus,simExeStatus);

        Double progress = eventRep.progress;
        Double timepoint = eventRep.timePoint;
        String username = eventRep.username;
        SimulationJobStatusEvent event = new SimulationJobStatusEvent(eventSource, simid, jobStatus, progress, timepoint, username);
        return event;
    }

    public static SimulationJobStatusEventRepresentation simulationJobStatusEventToJsonRep(SimulationJobStatusEvent event) {
        String vcellServerID = event.getJobStatus().getServerID().toString();
        Date timeDateStamp = event.getJobStatus().getTimeDateStamp();
        String simulationKey = event.getJobStatus().getVCSimulationIdentifier().getSimulationKey().toString();
        int taskID = event.getJobStatus().getTaskID();
        int jobIndex = event.getJobStatus().getJobIndex();
        Date submitDate = event.getJobStatus().getSubmitDate();
        String owner_userid = event.getUsername();
        String onwer_userkey = event.getJobStatus().getVCSimulationIdentifier().getOwner().getID().toString();
        SimulationJobStatusRepresentation.SchedulerStatus schedulerStatus =
                SimulationJobStatusRepresentation.SchedulerStatus.valueOf(
                        event.getJobStatus().getSchedulerStatus().name());
        SimulationJobStatusRepresentation.DetailedState detailedState =
                SimulationJobStatusRepresentation.DetailedState.valueOf(
                        event.getJobStatus().getSimulationMessage().getDetailedState().name());
        String detailedStateMessage = event.getJobStatus().getSimulationMessage().getDisplayMessage();

        Long htcJobNumber = null;
        String htcComputeServer = null;
        SimulationJobStatusRepresentation.BatchSystemType htcBatchSystemType = null;
        Date simexe_startDate = null;
        Date simexe_latestUpdateDate = null;
        Date simexe_endDate = null;
        String computeHost = null;
        Boolean hasData = null;
        SimulationExecutionStatus simExeStatus = event.getJobStatus().getSimulationExecutionStatus();
        if (simExeStatus!=null) {
            HtcJobID htcJobID = simExeStatus.getHtcJobID();
            if (htcJobID!=null) {
                htcJobNumber = htcJobID.getJobNumber();
                htcComputeServer = htcJobID.getServer();
                htcBatchSystemType = SimulationJobStatusRepresentation.BatchSystemType.valueOf(htcJobID.getBatchSystemType().name());
            }
            simexe_startDate = simExeStatus.getStartDate();
            simexe_latestUpdateDate = simExeStatus.getLatestUpdateDate();
            simexe_endDate = simExeStatus.getEndDate();
            computeHost = simExeStatus.getComputeHost();
            hasData = simExeStatus.hasData();
        }


        Integer queuePriority = null;
        Date queueDate = null;
        SimulationJobStatusRepresentation.SimulationQueueID queueId = null;
        SimulationQueueEntryStatus simQueueStatus = event.getJobStatus().getSimulationQueueEntryStatus();
        if (simQueueStatus!=null) {
            queuePriority = simQueueStatus.getQueuePriority();
            queueDate = simQueueStatus.getQueueDate();
            queueId = SimulationJobStatusRepresentation.SimulationQueueID.valueOf(simQueueStatus.getQueueID().name());
        }

        SimulationJobStatusRepresentation jobStatus = new SimulationJobStatusRepresentation(
                vcellServerID, timeDateStamp, simulationKey,
                taskID, jobIndex, submitDate, owner_userid, onwer_userkey,
                schedulerStatus, detailedState, detailedStateMessage,
                htcJobNumber, htcComputeServer, htcBatchSystemType,
                queuePriority, queueDate, queueId,
                simexe_startDate, simexe_latestUpdateDate, simexe_endDate, computeHost, hasData);
        SimulationJobStatusEventRepresentation eventRep = new SimulationJobStatusEventRepresentation(jobStatus, event.getProgress(),
                event.getTimepoint(), event.getUsername());
        return eventRep;
    }

    public static org.vcell.api.common.UserInfo getApiUserInfo(UserInfo userInfo){
        org.vcell.api.common.UserInfo apiUserInfo = new org.vcell.api.common.UserInfo(
                (userInfo.id!=null) ? userInfo.id.toString() : null,
                userInfo.userid, userInfo.digestedPassword0.getString(), userInfo.email, userInfo.wholeName,
                userInfo.title, userInfo.company, userInfo.country, userInfo.notify, userInfo.insertDate);
        return apiUserInfo;
    }


    public static UserInfo fromApiUserInfo(org.vcell.api.common.UserInfo apiUserInfo) {
        UserInfo userInfo = new UserInfo();
        userInfo.id = new KeyValue(apiUserInfo.id);
        userInfo.userid = apiUserInfo.userid;
        userInfo.digestedPassword0 = UserLoginInfo.DigestedPassword.createAlreadyDigested(apiUserInfo.digestedPassword0);
        userInfo.email = apiUserInfo.email;
        userInfo.wholeName = apiUserInfo.wholeName;
        userInfo.title = apiUserInfo.title;
        userInfo.company = apiUserInfo.company;
        userInfo.country = apiUserInfo.country;
        userInfo.notify = apiUserInfo.notify;
        userInfo.insertDate = apiUserInfo.insertDate;
        return userInfo;
    }

    public static SimpleJobStatusRepresentation fromSimpleJobStatus(SimulationJobStatus status) {
        String vcellServerID = status.getServerID().toString();
        String simulationKey = status.getVCSimulationIdentifier().getSimulationKey().toString();
        int taskID = status.getTaskID();
        int jobIndex = status.getJobIndex();
        long submitDate = status.getSubmitDate().getTime();
        User owner = status.getVCSimulationIdentifier().getOwner();
        SimpleJobStatusRepresentation .SchedulerStatus schedulerStatus = SimpleJobStatusRepresentation.SchedulerStatus.valueOf(status.getSchedulerStatus().name());
        SimpleJobStatusRepresentation.DetailedState detailedState = SimpleJobStatusRepresentation.DetailedState.valueOf(status.getSimulationMessage().getDetailedState().name());
        String detailedStateMessage = status.getSimulationMessage().getDisplayMessage();

        Long htcJobNumber = null;
        String htcComputeServer = null;
        SimpleJobStatusRepresentation.BatchSystemType htcBatchSystemType = null;
        Date simexe_startDate = null;
        Date simexe_latestUpdateDate = null;
        Date simexe_endDate = null;
        String computeHost = null;
        Boolean hasData = null;
        SimulationExecutionStatus simExeStatus = status.getSimulationExecutionStatus();
        if (simExeStatus!=null) {
            HtcJobID htcJobID = simExeStatus.getHtcJobID();
            if (htcJobID!=null) {
                htcJobNumber = htcJobID.getJobNumber();
                htcComputeServer = htcJobID.getServer();
                htcBatchSystemType = SimpleJobStatusRepresentation.BatchSystemType.valueOf(htcJobID.getBatchSystemType().name());
            }
            simexe_startDate = simExeStatus.getStartDate();
            simexe_latestUpdateDate = simExeStatus.getLatestUpdateDate();
            simexe_endDate = simExeStatus.getEndDate();
            computeHost = simExeStatus.getComputeHost();
            hasData = simExeStatus.hasData();
        }

        Integer queuePriority = null;
        Date queueDate = null;
        SimpleJobStatusRepresentation.SimulationQueueID queueId = null;
        SimulationQueueEntryStatus simQueueStatus = status.getSimulationQueueEntryStatus();
        if (simQueueStatus!=null) {
            queuePriority = simQueueStatus.getQueuePriority();
            queueDate = simQueueStatus.getQueueDate();
            queueId = SimpleJobStatusRepresentation.SimulationQueueID.valueOf(simQueueStatus.getQueueID().name());
        }

        SimpleJobStatusRepresentation rep = new SimpleJobStatusRepresentation(
                vcellServerID, simulationKey, taskID, jobIndex, new Date(submitDate), owner.getName(), owner.getID().toString(),
                schedulerStatus, detailedState, detailedStateMessage, htcJobNumber, htcComputeServer, htcBatchSystemType, queuePriority, queueDate, queueId,
                simexe_startDate, simexe_latestUpdateDate, simexe_endDate, computeHost, hasData);

        return rep;
    }

}
