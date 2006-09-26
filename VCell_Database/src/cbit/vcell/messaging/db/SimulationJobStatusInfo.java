package cbit.vcell.messaging.db;
import cbit.rmi.event.SimulationJobStatus;
import cbit.util.User;

/**
 * Insert the type's description here.
 * Creation date: (5/9/2006 3:55:48 PM)
 * @author: Jim Schaff
 */
public class SimulationJobStatusInfo implements java.io.Serializable {
	private SimulationJobStatus simJobStatus = null;
	private int dimension = 0;

/**
 * SimulationJobStatusInfo constructor comment.
 */
public SimulationJobStatusInfo(SimulationJobStatus sjs, int dim) {
	super();
	simJobStatus = sjs;
	dimension = dim;
}


/**
 * Insert the method's description here.
 * Creation date: (5/9/2006 3:57:30 PM)
 * @return cbit.vcell.messaging.db.SimulationJobStatus
 */
public SimulationJobStatus getSimJobStatus() {
	return simJobStatus;
}


/**
 * Insert the method's description here.
 * Creation date: (5/9/2006 3:57:30 PM)
 * @return User
 */
public User getUser() {
	return simJobStatus.getVCSimulationIdentifier().getOwner();
}


/**
 * Insert the method's description here.
 * Creation date: (5/9/2006 3:57:30 PM)
 * @return boolean
 */
public boolean isPDE() {
	return dimension > 0;
}
}