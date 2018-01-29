package org.vcell.api.common.events;

import java.util.Date;

public class SimulationJobStatusRepresentation {
	
	// enum SimulationJobStatus.SimulationQueueID
	public enum SimulationQueueID {
		QUEUE_ID_WAITING,
		QUEUE_ID_SIMULATIONJOB,
		QUEUE_ID_NULL
	}
	
	// enum HtcJobID.BatchSystemType
	public enum BatchSystemType {
		PBS,
		SGE,
		SLURM
	}
	
	// enum SimulationJobStatus.SchedulerStatus
	public enum SchedulerStatus {
		WAITING,
		QUEUED,
		DISPATCHED,
		RUNNING,
		COMPLETED,
		STOPPED,
		FAILED;
	}
	
	// enum SimulationMessage.DetailedState
	public enum DetailedState {		
		UNKNOWN,
		DATAMOVEREVENT_MOVED,   // used by DataMoverThread, not used by simulation
		WORKEREVENT_WORKERALIVE,
		JOB_WAITING,
		JOB_QUEUED,
		JOB_QUEUED_RETRY,
		JOB_DISPATCHED,
		JOB_ACCEPTED,
		SOLVER_READY,
		SOLVER_STARTING_INIT,
		SOLVEREVENT_STARTING_PROC_GEOM,
		SOLVEREVENT_STARTING_RESAMPLE_FD,
		SOLVER_RUNNING_INIT,
		SOLVER_RUNNING_INIT_INPUT_FILE,
		SOLVER_RUNNING_INIT_CODEGEN,
		SOLVER_RUNNING_INIT_COMPILING,
		SOLVER_RUNNING_INIT_COMPILECMD,
		SOLVER_RUNNING_INIT_COMPILE_OK,
		SOLVER_RUNNING_INIT_LINKING,
		SOLVER_RUNNING_INIT_LINKCMD,
		SOLVER_RUNNING_INIT_LINK_OK,
		SOLVER_RUNNING_INIT_COMPILELINK_OK,
		SOLVEREVENT_STARTING_INIT,
		SOLVEREVENT_STARTING_CODEGEN,
		SOLVEREVENT_STARTING_COMPILELINK,
		SOLVEREVENT_STARTING_INPUT_FILE,
		SOLVEREVENT_STARTING,
		SOLVEREVENT_STARTING_SUBMITTING,
		SOLVEREVENT_STARTING_SUBMITTED,
		WORKEREVENT_STARTING,
		SOLVEREVENT_RUNNING_START,
		SOLVER_RUNNING_START,
		JOB_RUNNING_UNKNOWN,
		SOLVEREVENT_PRINTED,
		WORKEREVENT_DATA,
		JOB_RUNNING,
		SOLVEREVENT_PROGRESS,
		WORKEREVENT_PROGRESS,
		WORKEREVENT_WORKEREXIT_NORMAL,
		WORKEREVENT_WORKEREXIT_ERROR,
		SOLVEREVENT_FINISHED,
		SOLVER_FINISHED,
		WORKEREVENT_COMPLETED,
		JOB_COMPLETED,
		SOLVER_STOPPED,
		JOB_STOPPED,
		JOB_FAILED_UNKNOWN,
		SOLVER_ABORTED,
		WORKEREVENT_FAILURE,
		JOB_FAILED
	}
	public String vcellServerID;

	public Date timeDateStamp = null;
	public String simulationKey = null;
	public int taskID;
	public int jobIndex;
	public Date submitDate = null;
	public String owner_userid = null;
	public String onwer_userkey = null;

	public SchedulerStatus schedulerStatus; 
	
	public DetailedState detailedState;
	public String detailedStateMessage;

	public Long htcJobNumber;  // required (e.g. 1200725)
	public String htcComputeServer;     // optional (e.g. "master.cm.cluster")
	public BatchSystemType htcBatchSystemType;

	
	//public SimulationQueueEntryStatus fieldSimulationQueueEntryStatus = null;	// may be null
	public Integer queuePriority = null;
	public Date queueDate = null;
	public SimulationQueueID queueId = null;
	
	//public SimulationExecutionStatus fieldSimulationExecutionStatus = null;	// may be null
	public Date simexe_startDate = null;
	public Date simexe_latestUpdateDate = null;
	public Date simexe_endDate = null;
	public String computeHost = null;
	public Boolean hasData = false;
	
	private SimulationJobStatusRepresentation() {
		
	}
	
	public SimulationJobStatusRepresentation(String vcellServerID, Date timeDateStamp, String simulationKey, int taskID,
			int jobIndex, Date submitDate, String owner_userid, String onwer_userkey, SchedulerStatus schedulerStatus,
			DetailedState detailedState, String detailedStateMessage, Long htcJobNumber, String htcComputeServer,
			BatchSystemType htcBatchSystemType, Integer queuePriority, Date queueDate, SimulationQueueID queueId,
			Date simexe_startDate, Date simexe_latestUpdateDate, Date simexe_endDate, String computeHost,
			Boolean hasData) {
		super();
		this.vcellServerID = vcellServerID;
		this.timeDateStamp = timeDateStamp;
		this.simulationKey = simulationKey;
		this.taskID = taskID;
		this.jobIndex = jobIndex;
		this.submitDate = submitDate;
		this.owner_userid = owner_userid;
		this.onwer_userkey = onwer_userkey;
		this.schedulerStatus = schedulerStatus;
		this.detailedState = detailedState;
		this.detailedStateMessage = detailedStateMessage;
		this.htcJobNumber = htcJobNumber;
		this.htcComputeServer = htcComputeServer;
		this.htcBatchSystemType = htcBatchSystemType;
		this.queuePriority = queuePriority;
		this.queueDate = queueDate;
		this.queueId = queueId;
		this.simexe_startDate = simexe_startDate;
		this.simexe_latestUpdateDate = simexe_latestUpdateDate;
		this.simexe_endDate = simexe_endDate;
		this.computeHost = computeHost;
		this.hasData = hasData;
	}
	
	
	
}
