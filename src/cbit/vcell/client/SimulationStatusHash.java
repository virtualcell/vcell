package cbit.vcell.client;
import java.util.Enumeration;
import java.util.Hashtable;

import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.ode.gui.SimulationStatus;
/**
 * Insert the type's description here.
 * Creation date: (4/20/2005 1:55:06 PM)
 * @author: Jim Schaff
 */
public class SimulationStatusHash {
	private Hashtable<Simulation, SimulationStatus> hash = new Hashtable<Simulation, SimulationStatus>();

/**
 * SimulationStatusHash constructor comment.
 */
public SimulationStatusHash() {
	super();
}


/**
 * Insert the method's description here.
 * Creation date: (6/7/2004 12:55:18 PM)
 * @param simulations cbit.vcell.solver.Simulation[]
 */
public void changeSimulationInstances(Simulation[] newSimulations) {
	if (newSimulations == null) {
		hash.clear();
	} else {
		Hashtable<Simulation, SimulationStatus> newHash = new Hashtable<Simulation, SimulationStatus>();
		for (int i = 0; i < newSimulations.length; i++){
			//
			// look in list for existing status
			//
			if (newSimulations[i].getKey()==null || newSimulations[i].getIsDirty()){
				newHash.put(newSimulations[i],SimulationStatus.newNotSaved(newSimulations[i].getScanCount()));
			// 
			// try to find status for previous instance of this simulation
			// 
			}else{
				SimulationStatus newSimStatus = null;
				Enumeration<Simulation> en = this.hash.keys();
				while (en.hasMoreElements()) {
					Simulation sim = en.nextElement();
					//
					// if simulations have the same "authoritative simulation identifier" then use this status
					//
					cbit.vcell.solver.SimulationInfo oldSimInfo = sim.getSimulationInfo();
					cbit.vcell.solver.SimulationInfo newSimInfo = newSimulations[i].getSimulationInfo();
					if (oldSimInfo!=null && newSimInfo!=null && oldSimInfo.getAuthoritativeVCSimulationIdentifier().equals(newSimInfo.getAuthoritativeVCSimulationIdentifier())){
						//
						// same "job id" ... same status
						//
						if (newSimStatus!=null){
							System.out.println("warning: more than one match for simulation status");
						}
						newSimStatus = hash.get(sim);
					}
				}
				if (newSimStatus!=null){
					newHash.put(newSimulations[i],newSimStatus);
				}else{
					newHash.put(newSimulations[i],SimulationStatus.newUnknown(newSimulations[i].getScanCount())); // unknown status
				}
			}
		}
		hash = newHash;
	}
}
	
/**
 * Insert the method's description here.
 * Creation date: (4/20/2005 1:57:02 PM)
 * @return cbit.vcell.solver.ode.gui.SimulationStatus
 * @param simulation cbit.vcell.solver.Simulation
 */
public SimulationStatus getSimulationStatus(Simulation simulation) {
	return hash.get(simulation);
}


/**
 * Insert the method's description here.
 * Creation date: (4/20/2005 1:57:34 PM)
 * @param simulation cbit.vcell.solver.Simulation
 * @param simStatus cbit.vcell.solver.ode.gui.SimulationStatus
 */
public void setSimulationStatus(Simulation simulation, SimulationStatus simStatus) {
	this.hash.put(simulation,simStatus);
}
}