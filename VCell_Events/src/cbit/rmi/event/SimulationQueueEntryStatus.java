package cbit.rmi.event;

import java.util.Date;
import java.io.Serializable;

/**
 * Insert the type's description here.
 * Creation date: (1/31/2003 11:21:39 AM)
 * @author: Jim Schaff
 */
public class SimulationQueueEntryStatus implements org.vcell.util.Matchable, java.io.Serializable {
	private int fieldQueuePriority = 0;
	private Date fieldQueueDate = null;
	private int fieldQueueID;
/**
 * SimulationQueueEntryStatus constructor comment.
 * @param simKey cbit.sql.KeyValue
 * @param submitDate java.util.Date
 * @param schedulerStatus int
 * @param queuePriority java.lang.Integer
 * @param queueDate java.util.Date
 * @param queueID java.lang.Integer
 */
public SimulationQueueEntryStatus(Date queueDate, int queuePriority, int queueID) {
	this.fieldQueueDate = queueDate;
	this.fieldQueuePriority = queuePriority;	
	this.fieldQueueID = queueID;
}
/**
 * Insert the method's description here.
 * Creation date: (5/27/2003 11:42:49 AM)
 * @return boolean
 * @param obj cbit.util.Matchable
 */
public boolean compareEqual(org.vcell.util.Matchable obj) {
	if (obj instanceof SimulationQueueEntryStatus){
		SimulationQueueEntryStatus entryStatus = (SimulationQueueEntryStatus)obj;
		if (entryStatus.fieldQueueDate != null && fieldQueueDate != null && entryStatus.fieldQueueDate.getTime()/1000 != fieldQueueDate.getTime()/1000) {
			//System.out.println("getQueueDate() not =:" + entryStatus.fieldQueueDate + "," + fieldQueueDate);
			return false;
		}
		
		if (entryStatus.fieldQueueID != fieldQueueID){
			//System.out.println("getQueueID() not =");
			return false;
		}
		if (entryStatus.fieldQueuePriority != fieldQueuePriority){
			//System.out.println("getQueuePriority() not =");
			return false;
		}
		return true;
	}
	return false;
}
/**
 * Insert the method's description here.
 * Creation date: (1/31/2003 11:23:56 AM)
 * @return Date
 */
public Date getQueueDate() {
	return fieldQueueDate;
}
/**
 * Insert the method's description here.
 * Creation date: (1/31/2003 11:23:56 AM)
 * @return int
 */
public int getQueueID() {
	return fieldQueueID;
}
/**
 * Insert the method's description here.
 * Creation date: (1/31/2003 11:23:56 AM)
 * @return int
 */
public int getQueuePriority() {
	return fieldQueuePriority;
}
/**
 * Insert the method's description here.
 * Creation date: (5/29/2003 10:03:41 AM)
 * @param newFieldQueueID int
 */
public void setQueueID(int newFieldQueueID) {
	fieldQueueID = newFieldQueueID;
}
/**
 * Insert the method's description here.
 * Creation date: (5/29/2003 10:02:08 AM)
 * @return java.lang.String
 */
public String toString() {
	return "SimulationQueueEntryStatus[" + fieldQueuePriority + "," + fieldQueueDate + "," + fieldQueueID + "]";
}
}
