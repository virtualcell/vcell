package cbit.render.objects;
import cbit.vcell.units.VCUnitDefinition;
/**
 * Insert the type's description here.
 * Creation date: (5/27/2004 11:37:23 AM)
 * @author: Jim Schaff
 */
public class VolumeRegion extends Region {
	private int fieldRegionID = -1;
	private Object fieldModelObject = null;

/**
 * VolumeGeometricRegion constructor comment.
 * @param argName java.lang.String
 */
public VolumeRegion(String argName, double argSize, VCUnitDefinition argSizeUnit, Object argModelObject, int argRegionID) {
	super(argName,argSize,argSizeUnit);
	this.fieldModelObject = argModelObject;
	this.fieldRegionID = argRegionID;
}


/**
 * Checks for internal representation of objects, not keys from database
 * @return boolean
 * @param obj java.lang.Object
 */
public boolean compareEqual(org.vcell.util.Matchable obj) {
	if (obj instanceof VolumeRegion){
		VolumeRegion volumeRegion = (VolumeRegion)obj;
		if (!super.compareEqual0(volumeRegion)){
			return false;
		}
		if (fieldRegionID != volumeRegion.fieldRegionID){
			return false;
		}
		if (fieldModelObject!=null && volumeRegion.fieldModelObject==null){
			return false;
		}
		if (fieldModelObject==null && volumeRegion.fieldModelObject!=null){
			return false;
		}
		if (fieldModelObject!=null && volumeRegion.fieldModelObject!=null){
			if (!fieldModelObject.equals(volumeRegion.fieldModelObject)){
				return false;
			}
		}
		return true;
	}else{
		return false;
	}
}


/**
 * Insert the method's description here.
 * Creation date: (5/27/2004 11:40:19 AM)
 * @return int
 */
public int getRegionID() {
	return fieldRegionID;
}


/**
 * Insert the method's description here.
 * Creation date: (5/27/2004 11:40:19 AM)
 * @return SubVolume
 */
public Object getModelObject() {
	return fieldModelObject;
}


/**
 * Insert the method's description here.
 * Creation date: (6/3/2004 10:25:57 AM)
 * @param newFieldSubVolume cbit.vcell.geometry.SubVolume
 */
void setModelObject(Object modelObject) {
	fieldModelObject = modelObject;
}


/**
 * Insert the method's description here.
 * Creation date: (5/27/2004 1:02:26 PM)
 * @return java.lang.String
 */
public String toString() {
	return "VolumeRegion@("+Integer.toHexString(hashCode())+") '"+getName()+"', size="+getSize()+" ["+getSizeUnit().getSymbol()+"], regionID="+getRegionID()+", subVolume=("+getModelObject()+")";
}
}