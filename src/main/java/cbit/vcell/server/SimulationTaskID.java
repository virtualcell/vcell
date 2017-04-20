package cbit.vcell.server;

import org.vcell.util.document.KeyValue;

import cbit.vcell.messaging.server.SimulationTask;
import cbit.vcell.solver.SimulationInfo;
import cbit.vcell.solver.SimulationJob;

public class SimulationTaskID {
	public final KeyValue simKey;
	public final int jobIndex;
	public final int taskID;
	public SimulationTaskID(KeyValue simKey,int jobIndex,int taskID){
		this.simKey = simKey;
		this.jobIndex = jobIndex;
		this.taskID = taskID;
	}
	public SimulationTaskID(SimulationTask simTask){
		this.simKey = simTask.getSimulation().getKey();
		this.jobIndex = simTask.getSimulationJob().getJobIndex();
		this.taskID = simTask.getTaskID();
	}
	public SimulationTaskID(SimulationJob simulationJob ,int taskID){
		this.simKey = simulationJob.getSimulation().getKey();
		this.jobIndex = simulationJob.getJobIndex();
		this.taskID = taskID;
	}
	public SimulationTaskID(SimulationInfo simulationInfo, int jobIndex ,int taskID){
		this.simKey = simulationInfo.getSimulationVersion().getVersionKey();
		this.jobIndex = jobIndex;
		this.taskID = taskID;
	}
	@Override
	public boolean equals(Object obj){
		if (obj instanceof SimulationTaskID){
			return toString().equals(((SimulationTaskID)obj).toString());
		}
		return false;
	}
	@Override
	public int hashCode(){
		return toString().hashCode();
	}
	@Override
	public String toString(){
		return "SimTaskInfo("+simKey.toString()+","+jobIndex+","+taskID+")";
	}
}