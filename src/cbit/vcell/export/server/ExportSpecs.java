package cbit.vcell.export.server;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.io.Serializable;

import org.vcell.util.Matchable;
import org.vcell.util.document.VCDataIdentifier;
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
	private String simulatioName;

	public interface SimulationSelector{
		public void selectSimulations();
		public ExportSpecs.SimNameSimDataID[] getSelectedSimDataInfo();
		public int getNumAvailableSimulations();
	}
	public static class SimNameSimDataID implements Matchable{
		private String simulationName;
		private VCDataIdentifier vcDataIdentifier;
		public SimNameSimDataID(String simulationName,VCDataIdentifier vcDataIdentifier) {
			this.simulationName = simulationName;
			this.vcDataIdentifier = vcDataIdentifier;
		}
		public VCDataIdentifier getVCDataIdentifier(){
			return vcDataIdentifier;
		}
		public String getSimulationName(){
			return simulationName;
		}
		public boolean compareEqual(Matchable obj) {
			if (obj instanceof SimNameSimDataID) {
				SimNameSimDataID simNameSimDataID = (SimNameSimDataID)obj;
				if (
					simulationName.equals(simNameSimDataID.getSimulationName()) &&
					vcDataIdentifier.equals(simNameSimDataID.getVCDataIdentifier())){
					
					return true;
				}
			}
			return false;
		}

	}
/**
 * This method was created in VisualAge.
 */
public ExportSpecs(org.vcell.util.document.VCDataIdentifier vcdID, int format,
		VariableSpecs variableSpecs, TimeSpecs timeSpecs, 
		GeometrySpecs geometrySpecs, FormatSpecificSpecs formatSpecificSpecs,
		String simulationName) {
	this.vcDataIdentifier = vcdID;
	this.format = format;
	this.variableSpecs = variableSpecs;
	this.timeSpecs = timeSpecs;
	this.geometrySpecs = geometrySpecs;
	this.formatSpecificSpecs = formatSpecificSpecs;
	this.simulatioName = simulationName;
}

public String getSimulationName(){
	return simulatioName;
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