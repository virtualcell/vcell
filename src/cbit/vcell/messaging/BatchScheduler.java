package cbit.vcell.messaging;

import java.util.Hashtable;
import cbit.vcell.server.SessionLog;
import cbit.vcell.server.User;
import cbit.vcell.messaging.db.SimulationJobStatusInfo;
import cbit.vcell.messaging.db.VCellServerID;

/**
 * Insert the type's description here.
 * Creation date: (5/11/2006 9:32:17 AM)
 * @author: Jim Schaff
 */
public class BatchScheduler {
/**
 * BatchScheduler constructor comment.
 */
public BatchScheduler() {
	super();
}


/**
 * Insert the method's description here.
 * Creation date: (5/11/2006 9:32:58 AM)
 */
public static SimulationJobStatusInfo schedule(SimulationJobStatusInfo[] allActiveJobs, int globalPdeQuota, int userQuotaOde, int userQuotaPde, VCellServerID systemID, SessionLog log) {
	Hashtable<User, Integer> userPdeRunningJobs = new Hashtable<User, Integer>();
	Hashtable<User, Integer> userOdeRunningJobs = new Hashtable<User, Integer>();

	cbit.vcell.messaging.db.SimulationJobStatus jobStatus = null;
	int runningPDEs = 0;
	for (int i = 0; i < allActiveJobs.length; i++){
		jobStatus = allActiveJobs[i].getSimJobStatus();

		if (!jobStatus.isActive()) {
			continue;
		}
		
		if (jobStatus.isWaiting()) {
			continue;  // we only do statistics on running jobs;
		}
		
		if (allActiveJobs[i].isPDE()) {
			runningPDEs ++; // running PDE jobs
		}
		
		if (jobStatus.getServerID().equals(systemID)) { // the number of running jobs on this site
			User user = allActiveJobs[i].getUser();
			if(allActiveJobs[i].isPDE()) {
				Integer numUserPdeJobs = userPdeRunningJobs.get(user);
				if (numUserPdeJobs == null) {
					userPdeRunningJobs.put(user, 1);
				} else {
					userPdeRunningJobs.put(user, numUserPdeJobs.intValue() + 1);
				}
			} else {
				Integer numUserOdeJobs = userOdeRunningJobs.get(user);
				if (numUserOdeJobs == null) {
					userOdeRunningJobs.put(user, 1);
				} else {
					userOdeRunningJobs.put(user, numUserOdeJobs.intValue() + 1);
				}
			}
		}
	}	
	for (int i = 0; i < allActiveJobs.length; i++){
		jobStatus = allActiveJobs[i].getSimJobStatus();
			
		if (!jobStatus.isWaiting()) {
			continue; // ignore non-waiting job
		}
			
		if (!jobStatus.getServerID().equals(systemID)) {
			continue; // doesn't belong
		}
		
		User user = allActiveJobs[i].getUser();
		if (allActiveJobs[i].isPDE() && runningPDEs >= globalPdeQuota) {
			log.print("Global PDE quota reached [" + jobStatus.getVCSimulationIdentifier() + "]");
			continue; // global LSF quota violated
		}							

		if(allActiveJobs[i].isPDE()) {
			Object numUserPdeJobs = userPdeRunningJobs.get(user);
			if (numUserPdeJobs != null) {
				if (((Integer)numUserPdeJobs).intValue() >= userQuotaPde) {
					//log.print("User PDE quota reached [" + jobStatus.getVCSimulationIdentifier() + "]");
					continue; // user PDE quota reached
				}
			}
		} else {
			Object numUserOdeJobs = userOdeRunningJobs.get(user);
			if (numUserOdeJobs != null) {
				if (((Integer)numUserOdeJobs).intValue() >= userQuotaOde) {
					log.print("User ODE quota reached [" + jobStatus.getVCSimulationIdentifier() + "]");
					continue; // user ODE quota reached
				}
			}
		}	

		return allActiveJobs[i];		
	}

	return null;
}
}