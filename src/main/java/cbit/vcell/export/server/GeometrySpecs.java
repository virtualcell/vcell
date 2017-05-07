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
import java.io.IOException;
import java.io.Serializable;

import org.vcell.util.BeanUtils;

import cbit.vcell.geometry.SinglePoint;
import cbit.vcell.simdata.SpatialSelection;
import cbit.vcell.simdata.SpatialSelectionMembrane;
/**
 * This type was created in VisualAge.
 */
public class GeometrySpecs implements Serializable {
	private byte[][] serializedSelections = null;
	private transient SpatialSelection[] spatialSelections = null;
	private int axis;
	private String slicePlane;
	private int sliceNumber;
	private int modeID;

/**
 * This method was created in VisualAge.
 * @param selections cbit.vcell.simdata.gui.SpatialSelection[]
 * @param axis int
 * @param sliceNumber int
 * @param modeID int
 */
public GeometrySpecs(SpatialSelection[] selections, int axis, int sliceNumber, int modeID) {
	if (selections != null) {
		try {
			serializedSelections = new byte[selections.length][];
			for (int i = 0; i < selections.length; i++){
				serializedSelections[i] = BeanUtils.toSerialized(selections[i]);
			}
		} catch (IOException exc) {
			exc.printStackTrace(System.out);
		}
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
			if (serializedSelections == null && otherSelections == null) {
				System.err.println("HACK >>> JCS <<<< ");
				return true;
			}
			if (serializedSelections == null || otherSelections == null) {
				System.err.println("HACK >>> JCS <<<< ");
				return false;
			}
			if (serializedSelections.length == otherSelections.length) {
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
		if (! isSinglePoint(getSelections()[i])) {
			count++;
		}
	}
	SpatialSelection[] curves = new SpatialSelection[count];
	count = 0;
	for (int i = 0;getSelections()!=null && i < getSelections().length; i++){
		if (! isSinglePoint(getSelections()[i])) {
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
			int count = getPointCount();
			int[] points = new int[count];
			count = 0;
			for (int i = 0; i < getSelections().length; i++){
				if (isSinglePoint(getSelections()[i])) {
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
public int getPointCount(){
	switch (getModeID()){
		case ExportConstants.GEOMETRY_SLICE: {
			throw new RuntimeException("GeometrySpecs.getPoints() shouldn't be called for modeID = GEOMTRY_SLICE");
		}
		case ExportConstants.GEOMETRY_SELECTIONS: {
			int count = 0;
			for (int i=0;i<getSelections().length;i++) {
				if (isSinglePoint(getSelections()[i])) {
					count++;
				}
			}
			return count;
		}
		default: {
			throw new RuntimeException("GeometrySpecs.getPoints() shouldn't be called for unknown modeID = "+getModeID());	
		}
	}	
}
public SpatialSelection[] getPointSpatialSelections(){
	switch (getModeID()){
	case ExportConstants.GEOMETRY_SLICE: {
		throw new RuntimeException("GeometrySpecs.getPoints() shouldn't be called for modeID = GEOMTRY_SLICE");
	}
	case ExportConstants.GEOMETRY_SELECTIONS: {
		int count = 0;
		for (int i=0;i<getSelections().length;i++) {
			if (isSinglePoint(getSelections()[i])) {
				count++;
			}
		}
		SpatialSelection[] pointSpatialSelections = new SpatialSelection[count];
		count = 0;
		for (int i = 0; i < getSelections().length; i++){
			if (isSinglePoint(getSelections()[i])) {
				pointSpatialSelections[count] = getSelections()[i];
				count++;
			}
		}
		return pointSpatialSelections;
	}
	default: {
		throw new RuntimeException("GeometrySpecs.getPoints() shouldn't be called for unknown modeID = "+getModeID());	
	}
}
	
}
public static boolean isSinglePoint(SpatialSelection spatialSelection){
	return
			spatialSelection.isPoint() ||
			(spatialSelection instanceof SpatialSelectionMembrane && ((SpatialSelectionMembrane)spatialSelection).getSelectionSource() instanceof SinglePoint);
}

/**
 * This method was created in VisualAge.
 * @return cbit.vcell.simdata.gui.SpatialSelection[]
 */
public SpatialSelection[] getSelections() {
	try {
		if (serializedSelections != null && spatialSelections == null) {
			spatialSelections = new SpatialSelection[serializedSelections.length];
			for (int i = 0; i < serializedSelections.length; i++){
				spatialSelections[i] = (SpatialSelection)BeanUtils.fromSerialized(serializedSelections[i]);
			}
		}
		return spatialSelections;
	} catch (Exception exc) {
		exc.printStackTrace(System.out);
		return null;
	}
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
	if (serializedSelections != null) {
		buf.append("{");
		SpatialSelection[] selections = getSelections();
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
