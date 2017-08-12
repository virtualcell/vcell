/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.solver.server;

import java.io.Serializable;

import org.vcell.util.Compare;

//import cbit.vcell.messaging.db.SimulationJobStatusPersistent.SchedulerStatus;
import cbit.vcell.server.HtcJobID;
import cbit.vcell.server.SimulationJobStatusPersistent.SchedulerStatus;

public class SimulationMessagePersistent implements Serializable {
	
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
	};

	public static final SimulationMessagePersistent MESSAGE_DATAMOVEREVENT_MOVED				= new SimulationMessagePersistent(DetailedState.DATAMOVEREVENT_MOVED,			"data moved");
	public static final SimulationMessagePersistent MESSAGE_WORKEREVENT_WORKERALIVE			= new SimulationMessagePersistent(DetailedState.WORKEREVENT_WORKERALIVE,		"running, waiting for progress");
	public static final SimulationMessagePersistent MESSAGE_JOB_WAITING						= new SimulationMessagePersistent(DetailedState.JOB_WAITING,					"waiting to be dispatched");
	public static final SimulationMessagePersistent MESSAGE_JOB_QUEUED						= new SimulationMessagePersistent(DetailedState.JOB_QUEUED,					"queued..."); 
	public static final SimulationMessagePersistent MESSAGE_JOB_QUEUED_RETRY					= new SimulationMessagePersistent(DetailedState.JOB_QUEUED_RETRY,				"Retry automatically upon server failure."); 
	public static final SimulationMessagePersistent MESSAGE_JOB_DISPATCHED					= new SimulationMessagePersistent(DetailedState.JOB_DISPATCHED,				"dispatched..."); 
	public static final SimulationMessagePersistent MESSAGE_JOB_ACCEPTED						= new SimulationMessagePersistent(DetailedState.JOB_ACCEPTED,					"job accepted...");
	public static final SimulationMessagePersistent MESSAGE_JOB_RUNNING_UNKNOWN				= new SimulationMessagePersistent(DetailedState.JOB_RUNNING_UNKNOWN,			"running..."); 
	public static final SimulationMessagePersistent MESSAGE_SOLVER_READY						= new SimulationMessagePersistent(DetailedState.SOLVER_READY,					"Ready"); 
	public static final SimulationMessagePersistent MESSAGE_SOLVER_STARTING_INIT				= new SimulationMessagePersistent(DetailedState.SOLVER_STARTING_INIT,			"solver initializing"); 
	public static final SimulationMessagePersistent MESSAGE_SOLVER_RUNNING_INIT				= new SimulationMessagePersistent(DetailedState.SOLVER_RUNNING_INIT,			"solver initializing"); 
	public static final SimulationMessagePersistent MESSAGE_SOLVER_RUNNING_INPUT_FILE			= new SimulationMessagePersistent(DetailedState.SOLVER_RUNNING_INIT_INPUT_FILE,"solver generating input file..."); 
	public static final SimulationMessagePersistent MESSAGE_SOLVER_RUNNING_CODEGEN			= new SimulationMessagePersistent(DetailedState.SOLVER_RUNNING_INIT_CODEGEN,	"generating code..."); 
	public static final SimulationMessagePersistent MESSAGE_SOLVER_RUNNING_COMPILING			= new SimulationMessagePersistent(DetailedState.SOLVER_RUNNING_INIT_COMPILING,"compiling code"); 
	public static final SimulationMessagePersistent MESSAGE_SOLVER_RUNNING_COMPILE_OK			= new SimulationMessagePersistent(DetailedState.SOLVER_RUNNING_INIT_COMPILE_OK,"compilation successful"); 
	public static final SimulationMessagePersistent MESSAGE_SOLVER_RUNNING_LINKING			= new SimulationMessagePersistent(DetailedState.SOLVER_RUNNING_INIT_LINKING,	"linking code"); 
	public static final SimulationMessagePersistent MESSAGE_SOLVER_RUNNING_LINK_OK			= new SimulationMessagePersistent(DetailedState.SOLVER_RUNNING_INIT_LINK_OK,	"linking successful"); 
	public static final SimulationMessagePersistent MESSAGE_SOLVER_RUNNING_COMPILELINK_OK		= new SimulationMessagePersistent(DetailedState.SOLVER_RUNNING_INIT_COMPILELINK_OK,"compile/link complete"); 
	public static final SimulationMessagePersistent MESSAGE_SOLVEREVENT_STARTING_SUBMITTING	= new SimulationMessagePersistent(DetailedState.SOLVEREVENT_STARTING_SUBMITTING,"submitting to job scheduler..."); 
	public static final SimulationMessagePersistent MESSAGE_SOLVEREVENT_STARTING				= new SimulationMessagePersistent(DetailedState.SOLVEREVENT_STARTING,			"solver starting"); 
	public static final SimulationMessagePersistent MESSAGE_WORKEREVENT_STARTING				= new SimulationMessagePersistent(DetailedState.WORKEREVENT_STARTING,			"worker starting");
	public static final SimulationMessagePersistent MESSAGE_SOLVEREVENT_STARTING_PROC_GEOM	= new SimulationMessagePersistent(DetailedState.SOLVEREVENT_STARTING_PROC_GEOM,	"processing geometry..."); 
	public static final SimulationMessagePersistent MESSAGE_SOLVEREVENT_STARTING_INIT			= new SimulationMessagePersistent(DetailedState.SOLVEREVENT_STARTING_INIT,	"solver initializing"); 
	public static final SimulationMessagePersistent MESSAGE_SOLVEREVENT_STARTING_CODEGEN		= new SimulationMessagePersistent(DetailedState.SOLVEREVENT_STARTING_CODEGEN,	"generating code..."); 
	public static final SimulationMessagePersistent MESSAGE_SOLVEREVENT_STARTING_COMPILELINK	= new SimulationMessagePersistent(DetailedState.SOLVEREVENT_STARTING_COMPILELINK,	"compiling and linking code..."); 
	public static final SimulationMessagePersistent MESSAGE_SOLVEREVENT_STARTING_INPUT_FILE	= new SimulationMessagePersistent(DetailedState.SOLVEREVENT_STARTING_INPUT_FILE,	"solver generating input file..."); 
	public static final SimulationMessagePersistent MESSAGE_SOLVEREVENT_STARTING_RESAMPLE_FD	= new SimulationMessagePersistent(DetailedState.SOLVEREVENT_STARTING_RESAMPLE_FD,	"resampling field data..."); 
	public static final SimulationMessagePersistent MESSAGE_SOLVER_RUNNING_START				= new SimulationMessagePersistent(DetailedState.SOLVER_RUNNING_START,			"solver starting"); 
	public static final SimulationMessagePersistent MESSAGE_SOLVEREVENT_FINISHED				= new SimulationMessagePersistent(DetailedState.SOLVEREVENT_FINISHED,			"completed"); 
	public static final SimulationMessagePersistent MESSAGE_SOLVER_FINISHED					= new SimulationMessagePersistent(DetailedState.SOLVER_FINISHED,				"completed"); 
	public static final SimulationMessagePersistent MESSAGE_JOB_COMPLETED						= new SimulationMessagePersistent(DetailedState.JOB_COMPLETED,				"completed");
	public static final SimulationMessagePersistent MESSAGE_SOLVER_STOPPED_BY_USER			= new SimulationMessagePersistent(DetailedState.SOLVER_STOPPED,				"User aborted simulation"); 
	public static final SimulationMessagePersistent MESSAGE_JOB_STOPPED						= new SimulationMessagePersistent(DetailedState.JOB_STOPPED,					"stopped"); 
	public static final SimulationMessagePersistent MESSAGE_JOB_FAILED_UNKNOWN				= new SimulationMessagePersistent(DetailedState.JOB_FAILED_UNKNOWN,			"failed"); 
	public static final SimulationMessagePersistent MESSAGE_JOB_FAILED_TOOMANYRETRIES			= new SimulationMessagePersistent(DetailedState.JOB_FAILED,					"Too many retries. Please try again later or contact Virtual Cell Support(VCell_Support@uchc.edu)."); 
	public static final SimulationMessagePersistent MESSAGE_JOB_FAILED_NOTAUTHORIZED			= new SimulationMessagePersistent(DetailedState.JOB_FAILED,					"You are not authorized to start this simulation!");
	public static final SimulationMessagePersistent MESSAGE_WORKEREVENT_DATA					= new SimulationMessagePersistent(DetailedState.WORKEREVENT_DATA,				"data");
	public static final SimulationMessagePersistent MESSAGE_WORKEREVENT_PROGRESS				= new SimulationMessagePersistent(DetailedState.WORKEREVENT_PROGRESS,			"progress");
	public static final SimulationMessagePersistent MESSAGE_WORKEREVENT_COMPLETED				= new SimulationMessagePersistent(DetailedState.WORKEREVENT_COMPLETED,		"completed");
	public static final SimulationMessagePersistent MESSAGE_WORKEREVENT_FAILURE				= new SimulationMessagePersistent(DetailedState.WORKEREVENT_FAILURE,			"failed");
	
	private final DetailedState detailedState;
	private final String message;
	private HtcJobID htcJobId;
	private SimulationMessagePersistent(DetailedState detailedState, String message){
		this.detailedState = detailedState;
		this.message = message;
		if (message != null && message.length() > 2048){
			message = message.trim();
			message = message.substring(0, 2048);
			message = message.replace('\r', ' ');
			message = message.replace('\n', ' ');
			message = message.replace('\'', ' ');
		}
	}
	

	public static SimulationMessagePersistent fromSerialized(SchedulerStatus schedulerStatus, String serializedMessage){
		
		SimulationMessagePersistent simulationMessage = fromSerializedMessage(serializedMessage);
		if (simulationMessage != null) {
			return simulationMessage;
		}
		
		if (schedulerStatus == SchedulerStatus.WAITING){
			if (serializedMessage == null) {
				return MESSAGE_JOB_WAITING;
			}
			return new SimulationMessagePersistent(DetailedState.JOB_WAITING, serializedMessage);
		}else if (schedulerStatus == SchedulerStatus.QUEUED){
			if (serializedMessage == null) {
				return MESSAGE_JOB_QUEUED;
			}
			return new SimulationMessagePersistent(DetailedState.JOB_QUEUED, serializedMessage);
		}else if (schedulerStatus == SchedulerStatus.DISPATCHED){
			if (serializedMessage == null) {
				return MESSAGE_JOB_DISPATCHED;
			}
			return new SimulationMessagePersistent(DetailedState.JOB_DISPATCHED, serializedMessage);
		}else if (schedulerStatus == SchedulerStatus.RUNNING){
			if (serializedMessage == null) {
				return MESSAGE_JOB_RUNNING_UNKNOWN;
			}
			return new SimulationMessagePersistent(DetailedState.JOB_RUNNING, serializedMessage);
		}else if (schedulerStatus == SchedulerStatus.COMPLETED){
			if (serializedMessage == null) {
				return MESSAGE_JOB_COMPLETED;
			}
			return new SimulationMessagePersistent(DetailedState.JOB_COMPLETED, serializedMessage);
		}else if (schedulerStatus == SchedulerStatus.STOPPED){
			if (serializedMessage == null) {
				return MESSAGE_JOB_STOPPED;
			}
			return new SimulationMessagePersistent(DetailedState.JOB_STOPPED, serializedMessage);
		}else if (schedulerStatus == SchedulerStatus.FAILED){
			if (serializedMessage == null) {
				return MESSAGE_JOB_FAILED_UNKNOWN;
			}
			return new SimulationMessagePersistent(DetailedState.JOB_FAILED, serializedMessage);
		}else{
			throw new RuntimeException("unexpected scheduler status : " + schedulerStatus);
		}
	}


	public static SimulationMessagePersistent fromSerializedMessage(String serializedMessage) {
		if (serializedMessage != null) {
			try {
				int indexOfDelimiter = serializedMessage.indexOf('|');
				if (indexOfDelimiter>0){
					// delimiter found try to extract detailed status
					String detailedStatusString = serializedMessage.substring(0, indexOfDelimiter);
					DetailedState detailedState = DetailedState.valueOf(detailedStatusString);
					String message = serializedMessage.substring(indexOfDelimiter+1);
					if (detailedState!=null){
						return new SimulationMessagePersistent(detailedState,message);
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace(System.out);
			}
		}
		return null;
	}
	
	public static SimulationMessagePersistent solverAborted(String failureMessage){
		return new SimulationMessagePersistent(DetailedState.SOLVER_ABORTED, failureMessage);
	}
	
	public static SimulationMessagePersistent workerFailure(String failureMessage){
		return new SimulationMessagePersistent(DetailedState.WORKEREVENT_FAILURE, failureMessage);
	}
	
	public static SimulationMessagePersistent solverStopped(String stoppedMessage){
		return new SimulationMessagePersistent(DetailedState.SOLVER_STOPPED, stoppedMessage);
	}
	
	public static SimulationMessagePersistent workerCompleted(String completeMessage){
		return new SimulationMessagePersistent(DetailedState.WORKEREVENT_COMPLETED, completeMessage);
	}
	
	public static SimulationMessagePersistent solverRunning_CompileCommand(String compileCommand){
		return new SimulationMessagePersistent(DetailedState.SOLVER_RUNNING_INIT_COMPILECMD, compileCommand);
	}
	
	public static SimulationMessagePersistent solverRunning_LinkCommand(String linkCommand){
		return new SimulationMessagePersistent(DetailedState.SOLVER_RUNNING_INIT_LINKCMD, linkCommand);
	}
	
	public static SimulationMessagePersistent solverPrinted(double timePoint){
		return new SimulationMessagePersistent(DetailedState.SOLVEREVENT_PRINTED, String.valueOf(timePoint));
	}
	
	public static SimulationMessagePersistent workerData(double timePoint){
		return new SimulationMessagePersistent(DetailedState.WORKEREVENT_DATA, String.valueOf(timePoint));
	}
	
	public static SimulationMessagePersistent solverProgress(double progress){
		return new SimulationMessagePersistent(DetailedState.SOLVEREVENT_PROGRESS, String.valueOf(progress));
	}
	
	public static SimulationMessagePersistent workerProgress(double progress){
		return new SimulationMessagePersistent(DetailedState.WORKEREVENT_PROGRESS, String.valueOf(progress));
	}

	public static SimulationMessagePersistent jobFailed(String failureMessage){
		return new SimulationMessagePersistent(DetailedState.JOB_FAILED,failureMessage);
	}

	public static SimulationMessagePersistent WorkerExited(int solverExitCode){
		if (solverExitCode==0){
			return new SimulationMessagePersistent(DetailedState.WORKEREVENT_WORKEREXIT_NORMAL,"solver exited (code="+solverExitCode+")");
		}else{
			return new SimulationMessagePersistent(DetailedState.WORKEREVENT_WORKEREXIT_ERROR,"solver exited (code="+solverExitCode+")");
		}
	}

	public static SimulationMessagePersistent solverEvent_Starting_Submit(String submitMsg, HtcJobID htcJobId){
		SimulationMessagePersistent simMessage = new SimulationMessagePersistent(DetailedState.SOLVEREVENT_STARTING_SUBMITTED,submitMsg);
		simMessage.htcJobId = htcJobId;
		return simMessage;
	}
	
	public static SimulationMessagePersistent workerStarting(String submitMsg){
		return new SimulationMessagePersistent(DetailedState.WORKEREVENT_STARTING,submitMsg);
	}
	
	public static SimulationMessagePersistent workerAccepted(String submitMsg){
		return new SimulationMessagePersistent(DetailedState.JOB_ACCEPTED,submitMsg);
	}

	public DetailedState getDetailedState() {
		return detailedState;
	}
	
	public HtcJobID getHtcJobId(){
		return htcJobId;
	}
	
	@Override
	public boolean equals(Object obj){
		if (obj instanceof SimulationMessagePersistent){
			SimulationMessagePersistent simMessage = (SimulationMessagePersistent)obj;
			if (!simMessage.detailedState.equals(detailedState)){
				return false;
			}
			if (!Compare.isEqualOrNull(simMessage.message, message)){
				return false;
			}
			return true;
		}
		return false;
	}

	@Override
	public int hashCode(){
		return detailedState.hashCode()+message.hashCode();
	}
	
	@Override
	public String toString(){
		return detailedState.name()+":"+message;
	}

	public String toSerialization(){
		return detailedState.name()+"|"+message;
	}
	
	public String getDisplayMessage() {
		return message;
//		return toSerialization();
	}
}
