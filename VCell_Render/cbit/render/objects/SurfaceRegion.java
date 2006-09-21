package cbit.render.objects;
import cbit.vcell.units.VCUnitDefinition;
/**
 * Insert the type's description here.
 * Creation date: (5/27/2004 11:38:09 AM)
 * @author: Jim Schaff
 */
public class SurfaceRegion extends Region {
/**
 * SurfaceGeometricRegion constructor comment.
 * @param argName java.lang.String
 */
public SurfaceRegion(String argName, double size, VCUnitDefinition sizeUnit) {
	super(argName,size,sizeUnit);
}


/**
 * Checks for internal representation of objects, not keys from database
 * @return boolean
 * @param obj java.lang.Object
 */
public boolean compareEqual(cbit.util.Matchable obj) {
	if (obj instanceof SurfaceRegion){
		SurfaceRegion surfaceRegion = (SurfaceRegion)obj;
		if (!super.compareEqual0(surfaceRegion)){
			return false;
		}
		return true;
	}else{
		return false;
	}
}


/**
 * Insert the method's description here.
 * Creation date: (5/27/2004 1:02:26 PM)
 * @return java.lang.String
 */
public String toString() {
	return "SurfaceGeometricRegion@("+Integer.toHexString(hashCode())+") '"+getName()+"', size="+getSize()+" ["+getSizeUnit().getSymbol()+"]";
}
}