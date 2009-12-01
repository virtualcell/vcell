package cbit.vcell.export.server;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.vcell.solver.*;
import java.io.*;
/**
 * This type was created in VisualAge.
 */
public class ExportSpecs implements Serializable {
	private org.vcell.util.document.VCDataIdentifier vcDataIdentifier = null;
	private int format;
	private TimeSpecs timeSpecs;
	private VariableSpecs variableSpecs;
	private GeometrySpecs geometrySpecs;
	private FormatSpecificSpecs formatSpecificSpecs;

/**
 * This method was created in VisualAge.
 */
public ExportSpecs(org.vcell.util.document.VCDataIdentifier vcdID, int format, VariableSpecs variableSpecs, TimeSpecs timeSpecs, GeometrySpecs geometrySpecs, FormatSpecificSpecs formatSpecificSpecs) {
	this.vcDataIdentifier = vcdID;
	this.format = format;
	this.variableSpecs = variableSpecs;
	this.timeSpecs = timeSpecs;
	this.geometrySpecs = geometrySpecs;
	this.formatSpecificSpecs = formatSpecificSpecs;
}


/**
 * Insert the method's description here.
 * Creation date: (4/2/2001 12:04:55 AM)
 * @return boolean
 * @param object java.lang.Object
 */
public boolean equals(Object object) {
	if (object instanceof ExportSpecs) {
		ExportSpecs exportSpecs = (ExportSpecs)object;
		if (
			(vcDataIdentifier == null && exportSpecs.getVCDataIdentifier() == null) || vcDataIdentifier.equals(exportSpecs.getVCDataIdentifier()) &&
			format == exportSpecs.getFormat() &&
			(variableSpecs == null && exportSpecs.getVariableSpecs() == null) || variableSpecs.equals(exportSpecs.getVariableSpecs()) &&
			(timeSpecs == null && exportSpecs.getTimeSpecs() == null) || timeSpecs.equals(exportSpecs.getTimeSpecs()) &&
			(geometrySpecs == null && exportSpecs.getGeometrySpecs() == null) || geometrySpecs.equals(exportSpecs.getGeometrySpecs()) &&
			(formatSpecificSpecs == null && exportSpecs.getFormatSpecificSpecs() == null) || formatSpecificSpecs.equals(exportSpecs.getFormatSpecificSpecs())
		) {
			return true;
		}
	}
	return false;
}


/**
 * This method was created in VisualAge.
 * @return cbit.vcell.export.server.VariableSpecs
 */
public int getFormat() {
	return format;
}


/**
 * This method was created in VisualAge.
 * @return cbit.vcell.export.server.VariableSpecs
 */
public FormatSpecificSpecs getFormatSpecificSpecs() {
	return formatSpecificSpecs;
}


/**
 * This method was created in VisualAge.
 * @return cbit.vcell.export.server.VariableSpecs
 */
public GeometrySpecs getGeometrySpecs() {
	return geometrySpecs;
}


/**
 * This method was created in VisualAge.
 * @return cbit.vcell.export.server.VariableSpecs
 */
public TimeSpecs getTimeSpecs() {
	return timeSpecs;
}


/**
 * This method was created in VisualAge.
 * @return cbit.vcell.export.server.VariableSpecs
 */
public VariableSpecs getVariableSpecs() {
	return variableSpecs;
}


/**
 * Insert the method's description here.
 * Creation date: (4/1/2001 7:20:40 PM)
 * @return cbit.vcell.solver.SimulationInfo
 */
public org.vcell.util.document.VCDataIdentifier getVCDataIdentifier() {
	return vcDataIdentifier;
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
	if(getTimeSpecs()!= null && getTimeSpecs().getAllTimes().length > 0)
		return "ExportSpecs [" + getVCDataIdentifier() + ", format: " + getFormat() + ", " + getVariableSpecs() + ", " + getTimeSpecs() + ", " + getGeometrySpecs() + ", " + getFormatSpecificSpecs() + "]";
	else
		return "ExportSpecs [" + getVCDataIdentifier() + ", format: " + getFormat() + ", " + getVariableSpecs() + ", " + getGeometrySpecs() + ", " + getFormatSpecificSpecs() + "]";
}
}