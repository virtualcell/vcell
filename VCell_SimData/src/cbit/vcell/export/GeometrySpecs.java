package cbit.vcell.export;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.vcell.simdata.SpatialSelection;
import java.io.*;
import cbit.util.*;
/**
 * This type was created in VisualAge.
 */
public class GeometrySpecs implements Serializable {
//	private byte[][] serializedSelections = null;
	private SpatialSelection[] selections = null;
	private int axis;
	private int sliceNumber;
	private int modeID;
/**
 * This method was created in VisualAge.
 * @param selections cbit.vcell.simdata.gui.SpatialSelection[]
 * @param axis int
 * @param sliceNumber int
 * @param modeID int
 */
public GeometrySpecs(SpatialSelection[] argSelections, int axis, int sliceNumber, int modeID) {
	if (argSelections != null) {
//		try {
//			serializedSelections = new byte[argSelections.length][];
//			for (int i = 0; i < argSelections.length; i++){
//				serializedSelections[i] = BeanUtils.toSerialized(argSelections[i]);
//			}
//		} catch (IOException exc) {
//			exc.printStackTrace(System.out);
//		}
		selections = argSelections;
	}
	this.axis = axis;
	this.sliceNumber = sliceNumber;
	this.modeID = modeID;
}
/**
 * Insert the method's description here.
 * Creation date: (4/2/2001 12:04:55 AM)
 * @return boolean
 * @param object java.lang.Object
 */
public boolean equals(Object object) {
	if (object instanceof GeometrySpecs) {
		GeometrySpecs geometrySpecs = (GeometrySpecs)object;
		if (
			axis == geometrySpecs.getAxis() &&
			sliceNumber == geometrySpecs.getSliceNumber() &&
			modeID == geometrySpecs.getModeID()
		) {
			SpatialSelection[] otherSelections = geometrySpecs.getSelections();
//			if (serializedSelections.length == otherSelections.length) {
			if (selections.length == otherSelections.length) {
				SpatialSelection[] selections = getSelections();
				for (int i = 0; i < selections.length; i++){
					if (! selections[i].compareEqual(otherSelections[i])) {
						return false;
					}
				}
				return true;
			}
		}
	}
	return false;
}
/**
 * This method was created in VisualAge.
 * @return int
 */
public int getAxis() {
	return axis;
}
/**
 * This method was created in VisualAge.
 * @return cbit.vcell.simdata.gui.SpatialSelection[]
 */
public SpatialSelection[] getCurves() {
	int count = 0;
	for (int i=0;getSelections()!=null && i<getSelections().length;i++) {
		if (! getSelections()[i].isPoint()) {
			count++;
		}
	}
	SpatialSelection[] curves = new SpatialSelection[count];
	count = 0;
	for (int i = 0;getSelections()!=null && i < getSelections().length; i++){
		if (! getSelections()[i].isPoint()) {
			curves[count] = getSelections()[i];
			count++;
		}
	}
	return curves;
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
 * Creation date: (3/2/2001 9:38:49 PM)
 * @return cbit.vcell.geometry.CoordinateIndex[]
 */
public int[] getPointIndexes() {
	switch (getModeID()){
		case ExportConstants.GEOMETRY_SLICE: {
			throw new RuntimeException("GeometrySpecs.getPoints() shouldn't be called for modeID = GEOMTRY_SLICE");
		}
		case ExportConstants.GEOMETRY_SELECTIONS: {
			int count = 0;
			for (int i=0;i<getSelections().length;i++) {
				if (getSelections()[i].isPoint()) {
					count++;
				}
			}
			int[] points = new int[count];
			count = 0;
			for (int i = 0; i < getSelections().length; i++){
				if (getSelections()[i].isPoint()) {
					points[count] = getSelections()[i].getIndex(0);
					count++;
				}
			}
			return points;
		}
		default: {
			throw new RuntimeException("GeometrySpecs.getPoints() shouldn't be called for unknown modeID = "+getModeID());	
		}
	}
}
/**
 * This method was created in VisualAge.
 * @return cbit.vcell.simdata.gui.SpatialSelection[]
 */
public SpatialSelection[] getSelections() {
//	try {
//		if (serializedSelections != null) {
//			SpatialSelection[] spatialSelections = new SpatialSelection[serializedSelections.length];
//			for (int i = 0; i < serializedSelections.length; i++){
//				spatialSelections[i] = (SpatialSelection)BeanUtils.fromSerialized(serializedSelections[i]);
//			}
//			return spatialSelections;
//		} else {
//			return null;
//		}
//	} catch (Exception exc) {
//		exc.printStackTrace(System.out);
//		return null;
//	}
	return selections;
}
/**
 * This method was created in VisualAge.
 * @return int
 */
public int getSliceNumber() {
	return sliceNumber;
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
	buf.append("GeometrySpecs [");
	buf.append("axis: " + axis + ", ");
	buf.append("sliceNumber: " + sliceNumber + ", ");
	buf.append("spatialSelections: ");
//	if (serializedSelections != null) {
	if (selections != null) {
		buf.append("{");
//		SpatialSelection[] selections = getSelections();
		for (int i = 0; i < selections.length; i++){
			buf.append(selections);
			if (i < selections.length - 1) buf.append(",");
		}
		buf.append("}");
	} else {
		buf.append("null");
	}
	buf.append(", modeID: " + modeID + "]");
	return buf.toString();
}
}
