package cbit.rmi.event;

import org.vcell.util.VCDataIdentifier;

import cbit.vcell.simulation.Simulation;
import cbit.vcell.simulation.VCSimulationIdentifier;

/**
 * Insert the type's description here.
 * Creation date: (8/24/2004 10:55:36 AM)
 * @author: Jim Schaff
 */
public class VCSimulationDataIdentifier implements java.io.Serializable, VCDataIdentifier {
	private VCSimulationIdentifier vcSimID = null;
	private int jobIndex = -1;

/**
 * VCSimulationIdentifier constructor comment.
 */
public VCSimulationDataIdentifier(VCSimulationIdentifier vcSimID, int jobIndex) {
	this.vcSimID = vcSimID;
	this.jobIndex = jobIndex;
}


/**
 * Insert the method's description here.
 * Creation date: (8/24/2004 10:56:16 AM)
 * @return boolean
 * @param object java.lang.Object
 */
public boolean equals(Object object) {
	if (object instanceof VCSimulationDataIdentifier){
		VCSimulationDataIdentifier other = (VCSimulationDataIdentifier)object;
		if (other.getID().equals(getID()) && other.getJobIndex() == getJobIndex()){
			return true;
		}
	}
	return false;
}
/**
 * Insert the method's description here.
 * Creation date: (10/14/2005 12:09:29 PM)
 * @return java.lang.String
 */
public static String createSimulationJobID(String simulationID, int jobIndex) {
	return simulationID+"_"+jobIndex+"_";
}




/**
 * Insert the method's description here.
 * Creation date: (8/24/2004 2:07:52 PM)
 * @return java.lang.String
 */
public java.lang.String getID() {
	return createSimulationJobID(Simulation.createSimulationID(getSimulationKey()), jobIndex);
}


/**
 * Insert the method's description here.
 * Creation date: (10/14/2005 1:09:00 PM)
 * @return int
 */
public int getJobIndex() {
	return jobIndex;
}


/**
 * Insert the method's description here.
 * Creation date: (8/24/2004 11:12:39 AM)
 * @return cbit.vcell.server.User
 */
public org.vcell.util.document.User getOwner() {
	return vcSimID.getOwner();
}


/**
 * Insert the method's description here.
 * Creation date: (8/24/2004 10:59:23 AM)
 * @return cbit.sql.KeyValue
 */
public org.vcell.util.document.KeyValue getSimulationKey() {
	return vcSimID.getSimulationKey();
}


/**
 * Insert the method's description here.
 * Creation date: (10/14/2005 12:02:27 PM)
 * @return cbit.vcell.solver.VCSimulationIdentifier
 */
public VCSimulationIdentifier getVcSimID() {
	return vcSimID;
}


/**
 * Insert the method's description here.
 * Creation date: (1/25/01 12:28:06 PM)
 * @return int
 */
public int hashCode() {
	return getID().toString().hashCode() + getJobIndex();
}


/**
 * Insert the method's description here.
 * Creation date: (8/24/2004 1:12:48 PM)
 * @return java.lang.String
 */
public String toString() {
	return "VCSimulationIdentifier["+getSimulationKey()+","+jobIndex+","+getOwner()+"]";
}
}