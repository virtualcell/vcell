/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.export.server;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
/**
 * This type was created in VisualAge.
 */
public class TimeSpecs implements Serializable {
	private int beginTimeIndex;
	private int endTimeIndex;
	private double[] allTimes;
	@JsonIgnore
	private ExportSpecss.TimeMode modeID;
/**
 * TimeSpecs constructor comment.
 */
@JsonCreator
public TimeSpecs(@JsonProperty("beginTimeIndex") int beginTimeIndex, @JsonProperty("endTimeIndex") int endTimeIndex,
				 @JsonProperty("allTimes") double[] allTimes, @JsonProperty("mode") ExportSpecss.TimeMode modeID) {
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
			modeID == timeSpecs.getMode() &&
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

public ExportSpecss.TimeMode getMode(){
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
	buf.append("selectedTimes: ");
	if (allTimes != null) {
		buf.append("{");
		buf.append(allTimes[beginTimeIndex] + "..." + allTimes[endTimeIndex]);
		buf.append("}");
	} else {
		buf.append("null");
	}
	buf.append(", modeID: " + modeID + "]");
	return buf.toString();
}

}
