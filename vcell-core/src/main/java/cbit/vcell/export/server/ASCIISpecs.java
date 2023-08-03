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
	public static enum CsvRoiLayout {var_time_val,time_sim_var}
	private boolean switchRowsColumns;
	private ExportFormat format;
	private ExportConstants.DataType dataType;
	private ExportSpecs.SimNameSimDataID[] simNameSimDataIDs;
	private int[] exportMultipleParamScans;
	private CsvRoiLayout csvLayout;
	private boolean isHDF5;
/**
 * TextSpecs constructor comment.
 */
public ASCIISpecs(ExportSpecs.SimNameSimDataID[] simNameSimDataIDs, ExportConstants.DataType dataType, ExportFormat format,
				   int[] exportMultipleParamScans, CsvRoiLayout csvLayout, boolean isHDF5, boolean switchRowsColumns) {
	this.format = format;
	this.dataType = dataType;
	this.switchRowsColumns = switchRowsColumns;
	this.simNameSimDataIDs = simNameSimDataIDs;
	this.exportMultipleParamScans = exportMultipleParamScans;
	this.csvLayout = csvLayout;
	this.isHDF5 = isHDF5;
}

public ASCIISpecs(ExportSpecs.SimNameSimDataID[] simNameSimDataIDs, ExportConstants.DataType dataType, ExportFormat format,
				  CsvRoiLayout csvLayout, boolean isHDF5, boolean switchRowsColumns){
	this(simNameSimDataIDs, dataType, format, null, csvLayout, isHDF5, switchRowsColumns);
}

public boolean isHDF5() {
	return isHDF5;
}
public CsvRoiLayout getCSVRoiLayout(){
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
			isHDF5 == asciiSpecs.isHDF5() &&
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
