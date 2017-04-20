/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.geometry.gui;

import cbit.vcell.geometry.*;
/**
 * Insert the type's description here.
 * Creation date: (8/3/00 1:02:29 PM)
 * @author: 
 */
public class CurveRendererGeometry extends CurveRenderer implements java.beans.PropertyChangeListener {
	private Geometry fieldGeometry = null;
	public static final String GEOMETRY_PROPERTY = "geometry";
	public static final String CURVES_VALID = "curvesValid";

/**
 * CurveRendererGeometry constructor comment.
 */
public CurveRendererGeometry() {
	super(null);
}
/**
 * This method was created in VisualAge.
 * @param curve cbit.vcell.geometry.Curve
 */
public void addCurve(Curve curve) {
	addCurve("GenericFilament",curve);
	
}
/**
 * This method was created in VisualAge.
 * @param curve cbit.vcell.geometry.Curve
 */
public void addCurve(String filamentName, Curve curve) {
	if (getGeometry() != null) {
		GeometrySpec geometrySpec = getGeometry().getGeometrySpec();
		geometrySpec.getFilamentGroup().addCurve(filamentName, curve);
	}
}
/**
 * Gets the geometry property (cbit.vcell.geometry.Geometry) value.
 * @return The geometry property value.
 * @see #setGeometry
 */
public Geometry getGeometry() {
	return fieldGeometry;
}
/**
 * propertyChange method comment.
 */
public void propertyChange(java.beans.PropertyChangeEvent evt) {
	GeometrySpec geometrySpec = getGeometry().getGeometrySpec();
	if (/*getGeometry() != null && */evt.getSource() == geometrySpec.getFilamentGroup()) {
		if (evt.getPropertyName().equals((String) FilamentGroup.FILAMENT_GROUP_PROPERTY)) {
			updateCurves();
		}
	}
}
/**
 * This method was created in VisualAge.
 * @param curve cbit.vcell.geometry.Curve
 */
public void removeCurve(Curve curve) {
	getGeometry().getGeometrySpec().getFilamentGroup().removeCurve(curve);
}
/**
 * Sets the geometry property (cbit.vcell.geometry.Geometry) value.
 * @param geometry The new value for the property.
 * @see #getGeometry
 */
public void setGeometry(Geometry geometry) {
	if (geometry != null && geometry.getDimension() == 0) {
		geometry = null;
	}
	if (geometry == null){
		setWorldDelta(null);
		setWorldOrigin(null);
	}
	Geometry oldValue = fieldGeometry;
	if (oldValue != null) {
		oldValue.getGeometrySpec().getFilamentGroup().removePropertyChangeListener(this);
	}
	fieldGeometry = geometry;
	firePropertyChange(GEOMETRY_PROPERTY, oldValue, fieldGeometry);
	//
	if (geometry != null) {
		fieldGeometry.getGeometrySpec().getFilamentGroup().addPropertyChangeListener(FilamentGroup.FILAMENT_GROUP_PROPERTY,this);
		firePropertyChange(CURVES_VALID, null,new Boolean(true));
	}else{
		firePropertyChange(CURVES_VALID, null,new Boolean(false));
	}
	//
	updateCurves();
}
/**
 * Insert the method's description here.
 * Creation date: (8/19/00 5:45:57 PM)
 */
protected boolean updateCurveDialog() {
	/*
	if (getCurveDialog() != null && getCurveDialog() instanceof GeometryFilamentCurveDialog) {
		if (getGeometry() != null && getGeometry().getDimension() > 0) {
			//
			GeometryFilamentCurveDialog gfcd = (GeometryFilamentCurveDialog) getCurveDialog();
			GeometryFilamentCurvePanel gfcp = gfcd.getGeometryFilamentCurvePanel();
			if (gfcp.getVcellDrawable() != fieldVcellDrawable) {
				gfcp.setVcellDrawable(fieldVcellDrawable);
			}
			gfcp.setCurve(getCrSelectedCurve());
			if (gfcp.getGeometry() != getGeometry()) {
				gfcp.setGeometry(getGeometry());
			}
			return true;
		} else {
			getCurveDialog().setVisible(false);
		}
	}
	*/
	return false;
}
/**
 * Insert the method's description here.
 * Creation date: (10/30/00 11:09:47 AM)
 */
private void updateCurves() {
	if (getGeometry() != null) {
		java.util.Vector newCurves = getGeometry().getGeometrySpec().getFilamentGroup().getCurvesVector();
		if (newCurves != null && newCurves.size() > 0) {
			//Remove curves that aren't in the new set of curve
			java.util.Enumeration oldCurveEnum = super.curveTable.keys();
			while (oldCurveEnum.hasMoreElements()) {
				Curve oldCurve = (Curve) oldCurveEnum.nextElement();
				if (!newCurves.contains(oldCurve)) {
					super.removeCurve(oldCurve);
				}
			}
			//Add new curves that aren't there already
			for (int i = 0; i < newCurves.size(); i += 1) {
				Curve curve = (Curve) newCurves.elementAt(i);
				if (!curveTable.containsKey(curve)) {
					super.addCurve(curve);
				}
			}
		} else {
			super.removeAllCurves();
		}

	}
}
}
