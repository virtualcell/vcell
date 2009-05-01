package cbit.vcell.client.task;
import cbit.vcell.solver.*;
import cbit.vcell.client.server.*;
import java.util.*;

import org.vcell.util.BeanUtils;
import org.vcell.util.UserCancelException;
import org.vcell.util.document.VCDocument;

import cbit.vcell.client.*;
import cbit.vcell.client.desktop.*;
import cbit.vcell.clientdb.*;
import cbit.vcell.geometry.*;
import cbit.vcell.mathmodel.*;
import cbit.vcell.server.*;
import cbit.vcell.mapping.*;
import cbit.vcell.math.*;
import cbit.vcell.biomodel.*;
import cbit.vcell.desktop.controls.*;
import cbit.vcell.document.*;
import cbit.util.*;
/**
 * Insert the type's description here.
 * Creation date: (5/31/2004 6:03:16 PM)
 * @author: Ion Moraru
 */
public class RunSims extends AsynchClientTask {
/**
 * Insert the method's description here.
 * Creation date: (5/31/2004 6:04:14 PM)
 * @return java.lang.String
 */
public java.lang.String getTaskName() {
	return "Sending simulation start requests";
}


/**
 * Insert the method's description here.
 * Creation date: (5/31/2004 6:04:14 PM)
 * @return int
 */
public int getTaskType() {
	return TASKTYPE_NONSWING_BLOCKING;
}


/**
 * Insert the method's description here.
 * Creation date: (5/31/2004 6:04:14 PM)
 * @param hashTable java.util.Hashtable
 * @param clientWorker cbit.vcell.desktop.controls.ClientWorker
 */
public void run(java.util.Hashtable hashTable) throws java.lang.Exception {
	DocumentWindowManager documentWindowManager = (DocumentWindowManager)hashTable.get("documentWindowManager");
	ClientSimManager clientSimManager = (ClientSimManager)hashTable.get("clientSimManager");
	DocumentManager documentManager = (DocumentManager)hashTable.get("documentManager");
	JobManager jobManager = (JobManager)hashTable.get("jobManager");
	Simulation[] simulations = (Simulation[])hashTable.get("simulations");
	Hashtable failures = new Hashtable();
	if (simulations != null && simulations.length > 0) {
		// we need to get the new ones if a save occurred...
		if (hashTable.containsKey("savedDocument")) {
			VCDocument savedDocument = (VCDocument)hashTable.get("savedDocument");
			Simulation[] allSims = null;
			if (savedDocument instanceof BioModel) {
				allSims = ((BioModel)savedDocument).getSimulations();
			} else if (savedDocument instanceof MathModel) {
				allSims = ((MathModel)savedDocument).getSimulations();
			}
			Vector v = new Vector();
			for (int i = 0; i < simulations.length; i++){
				for (int j = 0; j < allSims.length; j++){
					if (simulations[i].getName().equals(allSims[j].getName())) {
						v.add(allSims[j]);
						break;
					}
				}
			}
			simulations = (Simulation[])BeanUtils.getArray(v, Simulation.class);
		}
		for (int i = 0; i < simulations.length; i++){
			try {
				SimulationInfo simInfo = simulations[i].getSimulationInfo();
				if (simInfo != null) {
					//
					// translate to common ancestral simulation (oldest mathematically equivalent simulation)
					//
					jobManager.startSimulation(simInfo.getAuthoritativeVCSimulationIdentifier());
					// updateStatus
					clientSimManager.updateStatusFromStartRequest(simulations[i], false, null);
				} else {
					// this should really not happen...
					throw new RuntimeException(">>>>>>>>>> trying to run an unsaved simulation...");
				}	
			} catch (Throwable exc) {
				exc.printStackTrace(System.out);
				failures.put(simulations[i], exc);
			}
		}
	}
	// we actually have an array of requests and more than one can fail
	// we deal with individual request failures here, passing down only other things (that break the whole thing down) to dispatcher
	if (! failures.isEmpty()) {
		Enumeration en = failures.keys();
		while (en.hasMoreElements()) {
			Simulation sim = (Simulation)en.nextElement();
			Throwable exc = (Throwable)failures.get(sim);
			// updateStatus
			clientSimManager.updateStatusFromStartRequest(sim, true, exc.getMessage());
			// notify user
			PopupGenerator.showErrorDialog(documentWindowManager, "Failed to start simulation'"+sim.getName()+"'\n"+exc.getMessage());
		}
	}
}


/**
 * Insert the method's description here.
 * Creation date: (6/1/2004 8:44:23 PM)
 * @return boolean
 */
public boolean skipIfAbort() {
	return true;
}


/**
 * Insert the method's description here.
 * Creation date: (6/8/2004 4:40:01 PM)
 * @return boolean
 */
public boolean skipIfCancel(UserCancelException exc) {
	if (exc == UserCancelException.CANCEL_DELETE_OLD) {
		return false;
	} else {
		return true;
	}
}
}