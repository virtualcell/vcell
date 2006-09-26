package cbit.rmi.event;

import java.util.Date;

import cbit.util.KeyValue;
import cbit.util.Matchable;

/**
 * Insert the type's description here.
 * Creation date: (1/31/2003 11:27:24 AM)
 * @author: Jim Schaff
 */
public class SimulationExecutionStatus implements cbit.util.Matchable, java.io.Serializable {
	private Date fieldStartDate = null;
	private Date fieldLatestUpdateDate = null;
	private Date fieldEndDate = null;
	private String fieldComputeHost = null;
	private boolean fieldHasData = false;
/**
 * SimulationExecutionStatus constructor comment.
 */
public SimulationExecutionStatus(Date startDate, String computeHost, Date latestUpdateDate, Date endDate, boolean hasData) {
	fieldStartDate = startDate;
	fieldComputeHost = computeHost;
	fieldLatestUpdateDate = latestUpdateDate;
	fieldEndDate = endDate;
	fieldHasData = hasData;
}
/**
 * Checks for internal representation of objects, not keys from database
 * @return boolean
 * @param obj java.lang.Object
 */
public boolean compareEqual(Matchable obj) {
	if (obj instanceof SimulationExecutionStatus){
		SimulationExecutionStatus exeStatus = (SimulationExecutionStatus)obj;
		if (exeStatus.fieldComputeHost != null && fieldComputeHost != null && !exeStatus.fieldComputeHost.equals(getComputeHost())){
			//System.out.println("fieldComputeHost not = ");
			return false;
		}
		if (exeStatus.fieldEndDate != null && fieldEndDate != null && exeStatus.fieldEndDate.getTime()/1000 != fieldEndDate.getTime()/1000){
			//System.out.println("fieldEndDate not =");
			return false;
		}
		if (exeStatus.fieldHasData != fieldHasData){
			//System.out.println("fieldHasData not = ");
			return false;
		}		
		if (exeStatus.fieldStartDate != null && fieldStartDate != null && exeStatus.fieldStartDate.getTime()/1000 != fieldStartDate.getTime()/1000){
			//System.out.println("fieldStartDate not = ");
			return false;
		}
		if (exeStatus.fieldLatestUpdateDate != null && fieldLatestUpdateDate != null && exeStatus.fieldLatestUpdateDate.getTime()/1000 != fieldLatestUpdateDate.getTime()/1000){
			//System.out.println("fieldLatestUpdateDate not = ");
			return false;
		}
		
		return true;
	}
	return false;
}
/**
 * Insert the method's description here.
 * Creation date: (1/31/2003 11:28:02 AM)
 * @return java.lang.String
 */
public String getComputeHost() {
	return fieldComputeHost;
}
/**
 * Insert the method's description here.
 * Creation date: (1/31/2003 11:28:02 AM)
 * @return java.util.Date
 */
public Date getEndDate() {
	return fieldEndDate;
}
/**
 * Insert the method's description here.
 * Creation date: (1/31/2003 11:28:02 AM)
 * @return java.util.Date
 */
public Date getLatestUpdateDate() {
	return fieldLatestUpdateDate;
}
/**
 * Insert the method's description here.
 * Creation date: (1/31/2003 11:28:02 AM)
 * @return java.util.Date
 */
public Date getStartDate() {
	return fieldStartDate;
}
/**
 * Insert the method's description here.
 * Creation date: (5/28/2003 2:36:02 PM)
 * @return boolean
 */
public boolean hasData() {
	return fieldHasData;
}
/**
 * Insert the method's description here.
 * Creation date: (5/29/2003 7:57:19 AM)
 */
public String toString() {
	return "ExecutionStatus [" + fieldComputeHost + "," + fieldStartDate + "," + fieldEndDate + "," + fieldHasData + "]";
}
}
