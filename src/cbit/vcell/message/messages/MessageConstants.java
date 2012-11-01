/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.message.messages;
/**
 * Insert the type's description here.
 * Creation date: (10/23/2001 12:16:47 PM)
 * @author: Jim Schaff
 */
public interface MessageConstants {
	
	public static final String METHOD_NAME_PROPERTY		= "MethodName";	
	public static final String USERNAME_PROPERTY		= "UserName";
	public static final String 		USERNAME_PROPERTY_VALUE_ALL = "All";
	public static final String SIZE_MB_PROPERTY			= "SizeMB";
	public static final String SIMKEY_PROPERTY			= "SimKey";
	public static final String JOBINDEX_PROPERTY		= "JobIndex";
	public static final String TASKID_PROPERTY			= "TaskID";
	public static final String FIELDDATAID_PROPERTY		= "FieldDataID";
	public static final String HTCJOBID_PROPERTY 		= "HtcJobID";
	public static final String SIMULATION_STATUS_PROGRESS_PROPERTY	= "SimulationStatusProgress";
	public static final String SIMULATION_STATUS_TIMEPOINT_PROPERTY = "SimulationStatusTimePoint";
	public static final String WORKEREVENT_STATUS		= "WorkerEvent_Status";
	public static final String WORKEREVENT_PROGRESS		= "WorkerEvent_Progress";
	public static final String WORKEREVENT_TIMEPOINT	= "WorkerEvent_TimePoint";
	public static final String WORKEREVENT_STATUSMSG	= "WorkerEvent_StatusMsg";
	

	public static final String JMSCORRELATIONID_PROPERTY	= "JMSCorrelationID";
	
	public static final String MESSAGE_TYPE_SIMULATION_JOB_VALUE		= "SimulationJob";
	public static final String MESSAGE_TYPE_SIMSTATUS_VALUE				= "SimStatus";
	public static final String MESSAGE_TYPE_RPC_SERVICE_VALUE			= "RPCService";
	public static final String MESSAGE_TYPE_EXPORT_EVENT_VALUE			= "ExportEvent";
	public static final String MESSAGE_TYPE_DATA_EVENT_VALUE			= "DataEvent";
	public static final String MESSAGE_TYPE_STOPSIMULATION_VALUE		= "StopSimulation";	
	public static final String MESSAGE_TYPE_FLUSH_VALUE					= "Flush";
	public static final String MESSAGE_TYPE_ISSERVICEALIVE_VALUE		= "IsServiceAlive";
	public static final String MESSAGE_TYPE_IAMALIVE_VALUE				= "IAmAlive";		
	public static final String MESSAGE_TYPE_ASKPERFORMANCESTATUS_VALUE	= "AskPerformance";
	public static final String MESSAGE_TYPE_REFRESHSERVERMANAGER_VALUE	= "RefreshServerManager";
	public static final String MESSAGE_TYPE_REPLYPERFORMANCESTATUS_VALUE	= "ReplyPerformance";
	public static final String MESSAGE_TYPE_STOPSERVICE_VALUE			= "StopService";
	public static final String MESSAGE_TYPE_BROADCASTMESSAGE_VALUE		= "BroadcastMessage";
	public static final String MESSAGE_TYPE_WORKEREVENT_VALUE			= "WorkerEvent";

	
	public static final String MESSAGE_TYPE_PROPERTY	= "MessageType";

	public static final String COMPUTE_RESOURCE_PROPERTY	= "ComputeResource";	
	
	public static final String HOSTNAME_PROPERTY		= "HostName";
	public static final String SERVICE_TYPE_PROPERTY	= "ServiceType";
	public static final String SERVICE_ID_PROPERTY		= "ServiceID";	
	
	public static final int SECOND_IN_MS = 1000; // in milliseconds
	public static final int MINUTE_IN_S = 60;
	public static final int MINUTE_IN_MS = MINUTE_IN_S * SECOND_IN_MS; // in milliseconds

	public static final long INTERVAL_PING_SERVER_MS = 5 * MINUTE_IN_MS; // in milliseconds
	public static final long INTERVAL_SERVER_FAIL_MS = 10 * MINUTE_IN_MS; // in milliseconds
	public static final long INTERVAL_SIMULATIONJOBSTATUS_TIMEOUT_MS = 10 * MINUTE_IN_MS; // in milliseconds
	public static final long INTERVAL_PROGRESS_MESSAGE_MS = 5 * SECOND_IN_MS;


}
