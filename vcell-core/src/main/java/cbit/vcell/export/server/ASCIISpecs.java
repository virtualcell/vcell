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

import java.io.Serializable;

import org.vcell.util.Compare;
/**
 * This type was created in VisualAge.
 */
@SuppressWarnings("serial")
public class ASCIISpecs extends FormatSpecificSpecs implements Serializable {
	private ExportFormat format;
	private ExportConstants.DataType dataType;
	private boolean switchRowsColumns;
	private ExportSpecs.SimNameSimDataID[] simNameSimDataIDs;
	private int[] exportMultipleParamScans;
	public static enum csvRoiLayout {var_time_val,time_sim_var}
	private csvRoiLayout csvLayout;
/**
 * TextSpecs constructor comment.
 */
public ASCIISpecs(ExportFormat format, ExportConstants.DataType dataType2, boolean switchRowsColumns,ExportSpecs.SimNameSimDataID[] simNameSimDataIDs,int[] exportMultipleParamScans,csvRoiLayout csvLayout) {
	this.format = format;
	this.dataType = dataType2;
	this.switchRowsColumns = switchRowsColumns;
	this.simNameSimDataIDs = simNameSimDataIDs;
	this.exportMultipleParamScans = exportMultipleParamScans;
	this.csvLayout = csvLayout;
}
public csvRoiLayout getCSVRoiLayout(){
	return csvLayout;
}
/**
 * Insert the method's description here.
 * Creation date: (4/2/2001 1:08:44 AM)
 * @return boolean
 * @param object java.lang.Object
 */
public boolean equals(java.lang.Object object) {
	if (object instanceof ASCIISpecs) {
		ASCIISpecs asciiSpecs = (ASCIISpecs)object;
		if (
			format == asciiSpecs.getFormat() &&
			dataType == asciiSpecs.getDataType() &&
			switchRowsColumns == asciiSpecs.getSwitchRowsColumns() &&
			Compare.isEqualOrNull(simNameSimDataIDs, asciiSpecs.getSimNameSimDataIDs()) &&
			Compare.isEqualOrNull(exportMultipleParamScans, asciiSpecs.getExportMultipleParamScans())
		) {
			return true;
		}
	}
	return false;
}
public int[] getExportMultipleParamScans(){
	return exportMultipleParamScans;
}
public ExportSpecs.SimNameSimDataID[] getSimNameSimDataIDs(){
	return simNameSimDataIDs;
}
/**
 * This method was created in VisualAge.
 * @return int
 */
public ExportConstants.DataType getDataType() {
	return dataType;
}
/**
 * This method was created in VisualAge.
 * @return int
 */
public ExportFormat getFormat() {
	return format;
}
/**
 * This method was created in VisualAge.
 * @return int
 */
public boolean getSwitchRowsColumns() {
	return switchRowsColumns;
}
/**
 * Insert the method's description here.
 * Creation date: (4/2/2001 5:08:46 PM)
 * @return java.lang.String
 */
public java.lang.String toString() {
	return "ASCIISpecs: [format: " + format + ", dataType: " + dataType + ", switchRowsColumns: " + switchRowsColumns + "]";
}
}
