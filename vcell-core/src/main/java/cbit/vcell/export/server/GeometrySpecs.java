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
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.util.BeanUtils;

import cbit.vcell.geometry.SinglePoint;
import cbit.vcell.simdata.SpatialSelection;
import cbit.vcell.simdata.SpatialSelectionMembrane;
/**
 * This type was created in VisualAge.
 */
public abstract class GeometrySpecs implements Serializable {
	private final static Logger lg = LogManager.getLogger(GeometrySpecs.class);

	protected byte[][] serializedSelections = null;
	protected transient SpatialSelection[] spatialSelections = null;
	protected int axis;
	protected String slicePlane;
	protected int sliceNumber;

	/**
	 * This method was created in VisualAge.
	 * @param selections cbit.vcell.simdata.gui.SpatialSelection[]
	 * @param axis int
	 * @param sliceNumber int
	 */
	public GeometrySpecs(SpatialSelection[] selections, int axis, int sliceNumber) {
		if (selections != null) {
			try {
				serializedSelections = new byte[selections.length][];
				for (int i = 0; i < selections.length; i++){
					serializedSelections[i] = BeanUtils.toSerialized(selections[i]);
				}
			} catch (IOException exc) {
				lg.error(exc);
			}
		}
		this.axis = axis;
		this.sliceNumber = sliceNumber;
	}


	/**
	 * Insert the method's description here.
	 * Creation date: (4/2/2001 12:04:55 AM)
	 * @return boolean
	 * @param object java.lang.Object
	 */
	public boolean equals(Object object) {
		if (!(object instanceof GeometrySpecs)) return false;
		GeometrySpecs geometrySpecs = (GeometrySpecs)object;
		if (!(this.axis == geometrySpecs.getAxis() && this.sliceNumber == geometrySpecs.getSliceNumber())) return false;
		SpatialSelection[] otherSelections = geometrySpecs.getSelections();
		if (this.serializedSelections == null && otherSelections == null) {
			System.err.println("HACK >>> JCS <<<< ");
			return true;
		}
		if (this.serializedSelections == null || otherSelections == null) {
			System.err.println("HACK >>> JCS <<<< ");
			return false;
		}
		if (this.serializedSelections.length != otherSelections.length) return false;
		SpatialSelection[] selections = this.getSelections();
		for (int i = 0; i < selections.length; i++){
			if (selections[i].compareEqual(otherSelections[i])) continue;
			return false;
		}
		return true;
	}


	/**
	 * This method was created in VisualAge.
	 * @return int
	 */
	public int getAxis() {
		return this.axis;
	}


	/**
	 * This method was created in VisualAge.
	 * @return cbit.vcell.simdata.gui.SpatialSelection[]
	 */
	public SpatialSelection[] getCurves() {
		int count = 0;
		List<SpatialSelection> curves = new LinkedList<>();
		SpatialSelection[] selections = this.getSelections();
		// Note: I changed this method to no longer call this.getSelections() constantly, and to just call it once.
		// 		It looks to be a safe change; If this is, in fact, problematic...***revert*** it back!

		for (SpatialSelection selection : selections){
			if (isSinglePoint(selection)) continue;
			curves.add(selection);
		}

		return curves.toArray(new SpatialSelection[0]);
	}


	/**
	 * Insert the method's description here.
	 * Creation date: (3/2/2001 9:38:49 PM)
	 * @return cbit.vcell.geometry.CoordinateIndex[]
	 */
	public abstract int[] getPointIndexes() {
		final String EXEC_MSG = "GeometrySpecs.getPointIndexes() shouldn't be called for %smodeID = %s";
		int modeID = this.getModeID();

		if (modeID == ExportConstants.GEOMETRY_SELECTIONS) {
			List<Integer> points = new ArrayList<>();
			for (SpatialSelection selection : this.getSelections()) {
				if (!isSinglePoint(selection)) continue;
				points.add(selection.getIndex(0));
			}
			return points.stream().mapToInt(Integer::intValue).toArray();
		}
		String isUnknownString = ExportConstants.GEOMETRY_SLICE == modeID ? "" : "unknown ";
		throw new RuntimeException(String.format(EXEC_MSG, isUnknownString , modeID));
	}
	public abstract int getPointCount(){
		final String EXEC_MSG = "GeometrySpecs.getPointCount() shouldn't be called for %smodeID = %s";
		int modeID = this.getModeID();

		if (modeID == ExportConstants.GEOMETRY_SELECTIONS) {
			int count = 0;
			for (SpatialSelection selection : this.getSelections()) {
				if (!isSinglePoint(selection)) continue;
				count++;
			}
			return count;
		}
		String isUnknownString = ExportConstants.GEOMETRY_SLICE == modeID ? "" : "unknown ";
		throw new RuntimeException(String.format(EXEC_MSG, isUnknownString , modeID));
	}
	public abstract SpatialSelection[] getPointSpatialSelections(){
		switch (this.getModeID()){
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
				throw new RuntimeException("GeometrySpecs.getPoints() shouldn't be called for unknown modeID = "+this.getModeID());
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
		List<SpatialSelection> spatialSelections = new LinkedList<>();
		// If we're already good to go, don't repeat
		if (this.serializedSelections == null || this.spatialSelections != null) return this.spatialSelections;
		// Otherwise, let's get deserializing
		for (byte[] selection : this.serializedSelections){
			try {
				spatialSelections.add((SpatialSelection)BeanUtils.fromSerialized(selection));
			} catch (Exception exc) {
				lg.error("Error deserializing selections in Geometry Specs:", exc);
				return null;
			}
		}
		this.spatialSelections = spatialSelections.toArray(new SpatialSelection[0]);
		return this.spatialSelections;
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
		buf.append("axis: ").append(this.axis).append(", ");
		buf.append("sliceNumber: ").append(this.sliceNumber).append(", ");
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
		buf.append("]");
		return buf.toString();
	}
}
