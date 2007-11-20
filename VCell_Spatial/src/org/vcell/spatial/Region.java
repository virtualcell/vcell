package org.vcell.spatial;
import org.vcell.units.VCUnitDefinition;
/**
 * Insert the type's description here.
 * Creation date: (5/27/2004 11:31:42 AM)
 * @author: Jim Schaff
 */
public abstract class Region implements java.io.Serializable, org.vcell.util.Matchable {
	private String fieldName = null;
	private Region[] fieldAdjacentGeometricRegions = new Region[0];
	private double fieldSize = -1;
	private VCUnitDefinition fieldSizeUnit = null;

/**
 * GeometricRegion constructor comment.
 */
public Region(String argName, double argSize, VCUnitDefinition argSizeUnit) {
	super();
	this.fieldName = argName;
	this.fieldSize = argSize;
	this.fieldSizeUnit = argSizeUnit;
}


/**
 * Insert the method's description here.
 * Creation date: (5/27/2004 12:19:49 PM)
 * @param adjacentRegion cbit.vcell.geometry.surface.GeometricRegion
 */
public void addAdjacentGeometricRegion(Region adjacentRegion) {
	setAdjacentGeometricRegions((Region[])org.vcell.util.BeanUtils.addElement(fieldAdjacentGeometricRegions,adjacentRegion));
}


/**
 * Insert the method's description here.
 * Creation date: (5/27/2004 11:43:14 AM)
 * @return boolean
 * @param obj cbit.util.Matchable
 */
protected boolean compareEqual0(Region geometricRegion) {
	if (!org.vcell.util.Compare.isEqual(getName(),geometricRegion.getName())){
		return false;
	}
	if (fieldAdjacentGeometricRegions!=null || geometricRegion.fieldAdjacentGeometricRegions!=null){
		if (fieldAdjacentGeometricRegions==null || geometricRegion.fieldAdjacentGeometricRegions==null){
			return false;
		}
		if (fieldAdjacentGeometricRegions.length != geometricRegion.fieldAdjacentGeometricRegions.length){
			return false;
		}
		for (int i = 0; i < fieldAdjacentGeometricRegions.length; i++){
			if (!fieldAdjacentGeometricRegions[i].getName().equals(geometricRegion.fieldAdjacentGeometricRegions[i].getName())){
				return false;
			}
		}
	}
	if (fieldSize != geometricRegion.fieldSize){
		return false;
	}
	if (getSizeUnit()==null || geometricRegion.getSizeUnit()==null || !getSizeUnit().compareEqual(geometricRegion.getSizeUnit())){
		return false;
	}
	return true;
}


/**
 * Gets the adjacentGeometricRegions property (cbit.vcell.geometry.surface.GeometricRegion[]) value.
 * @return The adjacentGeometricRegions property value.
 * @see #setAdjacentGeometricRegions
 */
public Region[] getAdjacentGeometricRegions() {
	return fieldAdjacentGeometricRegions;
}


/**
 * Gets the name property (java.lang.String) value.
 * @return The name property value.
 * @see #setName
 */
public java.lang.String getName() {
	return fieldName;
}


/**
 * Gets the size property (java.lang.Double) value.
 * @return The size property value.
 * @see #setSize
 */
public double getSize() {
	return fieldSize;
}


/**
 * Gets the sizeUnit property (cbit.vcell.units.VCUnitDefinition) value.
 * @return The sizeUnit property value.
 * @see #setSizeUnit
 */
public org.vcell.units.VCUnitDefinition getSizeUnit() {
	return fieldSizeUnit;
}


/**
 * Sets the adjacentGeometricRegions property (cbit.vcell.geometry.surface.GeometricRegion[]) value.
 * @param adjacentGeometricRegions The new value for the property.
 * @see #getAdjacentGeometricRegions
 */
public void setAdjacentGeometricRegions(Region[] adjacentGeometricRegions) {
	fieldAdjacentGeometricRegions = adjacentGeometricRegions;
}
}