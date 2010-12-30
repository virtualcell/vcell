package cbit.vcell.client;

import java.util.Hashtable;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.mapping.SimulationContext;

public class ClientTaskManager {
	public static AsynchClientTask[] newApplication(final BioModel bioModel, final boolean isStoch) {		
		
		AsynchClientTask task0 = new AsynchClientTask("create application", AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
			
			@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {
				String newApplicationName = bioModel.getFreeSimulationContextName();
				SimulationContext newSimulationContext = bioModel.addNewSimulationContext(newApplicationName, isStoch);
				hashTable.put("newSimulationContext", newSimulationContext);
			}
		};
		AsynchClientTask task1 = new AsynchClientTask("process geometry", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
			
			@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {
				SimulationContext newSimulationContext = (SimulationContext)hashTable.get("newSimulationContext");
				newSimulationContext.getGeometry().precomputeAll();
			}
		};
		return new AsynchClientTask[] {task0, task1};
	}
}
