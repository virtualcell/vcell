package cbit.vcell.export;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.io.*;
/**
 * This type was created in VisualAge.
 */
public class TimeSpecs implements Serializable {
	private int beginTimeIndex;
	private int endTimeIndex;
	private double[] allTimes;
	private int modeID;
/**
 * TimeSpecs constructor comment.
 */
public TimeSpecs(int beginTimeIndex, int endTimeIndex, double[] allTimes, int modeID) {
	this.beginTimeIndex = beginTimeIndex;
	this.endTimeIndex = endTimeIndex;
	this.allTimes = allTimes;
	this.modeID = modeID;
}
/**
 * Insert the method's description here.
 * Creation date: (4/2/2001 12:04:55 AM)
 * @return boolean
 * @param object java.lang.Object
 */
public boolean equals(Object object) {
	if (object instanceof TimeSpecs) {
		TimeSpecs timeSpecs = (TimeSpecs)object;
		if (
			beginTimeIndex == timeSpecs.getBeginTimeIndex() &&
			endTimeIndex == timeSpecs.getEndTimeIndex() &&
			modeID == timeSpecs.getModeID() &&
			allTimes.length == timeSpecs.getAllTimes().length
		) {
			for (int i = 0; i < allTimes.length; i++){
				if (! (allTimes[i] == timeSpecs.getAllTimes()[i])) {
					return false;
				}
			}
			return true;
		}
	}
	return false;
}
/**
 * This method was created in VisualAge.
 * @return double[]
 */
public double[] getAllTimes() {
	return allTimes;
}
/**
 * This method was created in VisualAge.
 * @return double
 */
public int getBeginTimeIndex() {
	return beginTimeIndex;
}
/**
 * This method was created in VisualAge.
 * @return double
 */
public int getEndTimeIndex() {
	return endTimeIndex;
}
/**
 * This method was created in VisualAge.
 * @return int
 */
public int getModeID() {
	return modeID;
}
/**
 * Insert the method's description here.
 * Creation date: (4/2/2001 4:33:23 PM)
 * @return int
 */
public int hashCode() {
	return toString().hashCode();
}
/**
 * Insert the method's description here.
 * Creation date: (4/2/2001 4:23:04 PM)
 * @return java.lang.String
 */
public String toString() {
	StringBuffer buf = new StringBuffer();
	buf.append("TimeSpecs [");
	buf.append("beginTimeIndex: " + beginTimeIndex + ", ");
	buf.append("endTimeIndex: " + endTimeIndex + ", ");
	buf.append("times: ");
	if (allTimes != null) {
		buf.append("{");
		for (int i = 0; i < allTimes.length; i++){
			buf.append(allTimes[i]);
			if (i < allTimes.length - 1) buf.append(",");
		}
		buf.append("}");
	} else {
		buf.append("null");
	}
	buf.append(", modeID: " + modeID + "]");
	return buf.toString();
}
}
