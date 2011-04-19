package cbit.vcell.export.server;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.io.*;

import org.vcell.util.Compare;
/**
 * This type was created in VisualAge.
 */
public class ASCIISpecs extends FormatSpecificSpecs implements Serializable {
	private int format;
	private int dataType;
	private boolean switchRowsColumns;
	private ExportSpecs.SimNameSimDataID[] simNameSimDataIDs;
/**
 * TextSpecs constructor comment.
 */
public ASCIISpecs(int format, int dataType, boolean switchRowsColumns,ExportSpecs.SimNameSimDataID[] simNameSimDataIDs) {
	this.format = format;
	this.dataType = dataType;
	this.switchRowsColumns = switchRowsColumns;
	this.simNameSimDataIDs = simNameSimDataIDs;
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
			Compare.isEqualOrNull(simNameSimDataIDs, asciiSpecs.getSimNameSimDataIDs())
		) {
			return true;
		}
	}
	return false;
}
public ExportSpecs.SimNameSimDataID[] getSimNameSimDataIDs(){
	return simNameSimDataIDs;
}
/**
 * This method was created in VisualAge.
 * @return int
 */
public int getDataType() {
	return dataType;
}
/**
 * This method was created in VisualAge.
 * @return int
 */
public int getFormat() {
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
