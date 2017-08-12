/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.geometry;

import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;
/**
 * Insert the type's description here.
 * Creation date: (8/19/00 3:27:35 PM)
 * @author: 
 */
public class FilamentGroup implements Serializable, org.vcell.util.Matchable {
	public static String FILAMENT_GROUP_PROPERTY = "filamentGroup";
	//HashSet enforces No Duplicates
	private HashSet curves = new HashSet();
	private HashSet filamentNames = new HashSet();
	private HashSet filamentNamesCurves = new HashSet();

	// Inner Class defining FilamentName-Curve association
	private class FilamentNameCurvePairs implements Serializable, org.vcell.util.Matchable {
		private String filamentName = null;
		private Curve curve = null;
		public FilamentNameCurvePairs(String argFilName, Curve argCurve) {
			filamentName = argFilName;
			curve = argCurve;
		}
		public Curve getCurve() {
			return curve;
		}
		public String getFilamentName() {
			return filamentName;
		}
		public boolean compareEqual(org.vcell.util.Matchable obj) {
			if (obj == null) {
				return false;
			}
			if (!(obj instanceof FilamentNameCurvePairs)) {
				return false;
			}
			FilamentNameCurvePairs fncp = null;
			fncp = (FilamentNameCurvePairs) obj;
			if (!org.vcell.util.Compare.isEqual(filamentName, fncp.filamentName)) {
				return false;
			}
			if (!org.vcell.util.Compare.isEqual(curve, fncp.curve)) {
				return false;
			}
			return true;
		}
	}
	private java.beans.PropertyChangeSupport propertyChange = null;
/**
 * FilamentGroup constructor comment.
 */
public FilamentGroup() {
	super();
	propertyChange = new PropertyChangeSupport(this);
}
/**
 * Insert the method's description here.
 * Creation date: (8/19/00 3:28:36 PM)
 * @param filamentName java.lang.String
 * @param curve cbit.vcell.geometry.Curve
 */
public synchronized void addCurve(String filamentName, Curve curve) {
	if (filamentName == null || filamentName.length() == 0 || curve == null) {
		throw new IllegalArgumentException();
	}
	// Remove curve in case we're moving it to new Filament(Does cleanup)
	if (curves.contains(curve)) {
		removeCurve(curve, false); //Don't fire property change yet
	}
	// Add Curve to Set
	curves.add(curve);
	// Add filamentName to Set
	filamentNames.add(filamentName);
	// Create filamentName-Cuve association;
	FilamentNameCurvePairs fncp = new FilamentNameCurvePairs(filamentName, curve);
	// Add new name-curve associateion to Set
	boolean bFilamentNameCurveAssociationExistedAlready = !filamentNamesCurves.add(fncp);
	if (bFilamentNameCurveAssociationExistedAlready) {
		//This should be impossible at this point
		throw new RuntimeException("FilamentGroup.addCurve FilamentName " + filamentName + " Curve " + curve.getClass().getName() + " Failed");
	}
	propertyChange.firePropertyChange(FILAMENT_GROUP_PROPERTY, null, null);
}
/**
 * Insert the method's description here.
 * Creation date: (8/21/00 2:35:25 PM)
 */
public void addPropertyChangeListener(java.beans.PropertyChangeListener listener){
	propertyChange.addPropertyChangeListener(listener);
}
/**
 * Insert the method's description here.
 * Creation date: (8/21/00 2:35:25 PM)
 */
public void addPropertyChangeListener(String property,java.beans.PropertyChangeListener listener){
	propertyChange.addPropertyChangeListener(property,listener);
}
/**
 * Insert the method's description here.
 * Creation date: (8/19/00 4:40:33 PM)
 * @param filamentName java.lang.String
 */
private synchronized void cleanupFilament(String filamentName) {
	if (filamentName == null || filamentName.length() == 0) {
		throw new IllegalArgumentException();
	}
	if (!filamentNames.contains(filamentName)) {
		throw new RuntimeException("FilamentGroup.cleanupFilament: filamentName " + filamentName + " Not Found");
	}
	boolean curveFound = false;
	//Try and find any curves for filament
	for (Iterator i = filamentNamesCurves.iterator(); i.hasNext();) {
		FilamentNameCurvePairs old = (FilamentNameCurvePairs) i.next();
		if (old.getFilamentName().equals(filamentName)) {
			curveFound = true;
			break;
		}
	}
	//Remove filament if it has no curves
	if (!curveFound) {
		if (!filamentNames.remove(filamentName)) {
			//Didn't find filament, this shouldn't be possible at this point
			throw new RuntimeException("FilamentGroup.cleanupFilament: FilamentGroup Error");
		}
	}
}
/**
 * compareEqual method comment.
 */
public boolean compareEqual(org.vcell.util.Matchable obj) {
	if (obj == null) {
		return false;
	}
	if (!(obj instanceof FilamentGroup)) {
		return false;
	}
	FilamentGroup fg = (FilamentGroup) obj;
	if (curves.size() != fg.curves.size() || filamentNames.size() != fg.filamentNames.size() || filamentNamesCurves.size() != fg.filamentNamesCurves.size()) {
		return false;
	}
	//Curves
	Iterator curvesI = curves.iterator();
	Iterator fgCurvesI = fg.curves.iterator();
	while (curvesI.hasNext()) {
		Curve thisCurve = (Curve) curvesI.next();
		Curve fgCurve = (Curve) fgCurvesI.next();
		if (!org.vcell.util.Compare.isEqual(thisCurve, fgCurve)) {
			return false;
		}
	}
	//FilamentNames
	Iterator filamentNamesI = filamentNames.iterator();
	Iterator fgFilamentNamesI = fg.filamentNames.iterator();
	while (filamentNamesI.hasNext()) {
		String thisFilamentNames = (String) filamentNamesI.next();
		String fgFilamentNames = (String) fgFilamentNamesI.next();
		if (!org.vcell.util.Compare.isEqual(thisFilamentNames, fgFilamentNames)) {
			return false;
		}
	}
	//FilamentNames-Curves Association
	Iterator filamentNamesCurvesI = filamentNamesCurves.iterator();
	Iterator fgFilamentNamesCurvesI = fg.filamentNamesCurves.iterator();
	while (filamentNamesCurvesI.hasNext()) {
		FilamentNameCurvePairs thisFilamentNamesCurves = (FilamentNameCurvePairs) filamentNamesCurvesI.next();
		FilamentNameCurvePairs fgFilamentNamesCurves = (FilamentNameCurvePairs) fgFilamentNamesCurvesI.next();
		if (!org.vcell.util.Compare.isEqual(thisFilamentNamesCurves, fgFilamentNamesCurves)) {
			return false;
		}
	}
	return true;
}
/**
 * Insert the method's description here.
 * Creation date: (8/19/00 4:55:49 PM)
 * @return int
 */
public synchronized int getCurveCount() {
	return curves.size();
}
/**
 * Insert the method's description here.
 * Creation date: (8/19/00 3:30:04 PM)
 * @return java.lang.String[]
 */
public synchronized Curve[] getCurvesArray() {
	if(curves.size() == 0){
		return new Curve[0];
	}
	return (Curve[])curves.toArray(new Curve[curves.size()]);
}
/**
 * Insert the method's description here.
 * Creation date: (8/19/00 3:30:04 PM)
 * @return java.lang.String[]
 */
public synchronized Vector getCurvesVector() {
	if (curves.size() == 0) {
		return new Vector();
	}
	return new Vector(curves);
}
/**
 * Insert the method's description here.
 * Creation date: (8/19/00 3:30:04 PM)
 * @return java.lang.String[]
 */
public synchronized Filament getFilament(Curve curve) {
	if(curve == null){
		return null;
	}
	String filamentName = getFilamentName(curve);
	if (filamentName != null) {
		return getFilament(filamentName);
	}
	return null;
}
/**
 * Insert the method's description here.
 * Creation date: (8/19/00 3:30:04 PM)
 * @return java.lang.String[]
 */
public synchronized Filament getFilament(String filamentName) {
	if (filamentName == null || filamentName.length() == 0) {
		throw new IllegalArgumentException();
	}
	if (!filamentNames.contains(filamentName)) {
		throw new RuntimeException("Couldn't find filamentName " + filamentName + " in filament group");
	}
	HashSet filamentCurves = new HashSet();//Make holder for curves
	for (Iterator i = filamentNamesCurves.iterator(); i.hasNext();) {
		FilamentNameCurvePairs fncp = (FilamentNameCurvePairs) i.next();
		if (fncp.getFilamentName().equals(filamentName)) {
			filamentCurves.add(fncp.getCurve());//Add curves that match filamentName
		}
	}
	//Make new Filament and return
	return new Filament(filamentName, filamentCurves);
}
/**
 * Insert the method's description here.
 * Creation date: (8/19/00 4:57:21 PM)
 * @return int
 */
public synchronized int getFilamentCount() {
	return filamentNames.size();
}
/**
 * Insert the method's description here.
 * Creation date: (8/19/00 3:30:04 PM)
 * @return java.lang.String[]
 */
public synchronized String getFilamentName(Curve curve) {
	if (curve == null) {
		throw new IllegalArgumentException();
	}
	for (Iterator i = filamentNamesCurves.iterator(); i.hasNext();) {
		FilamentNameCurvePairs fncp = (FilamentNameCurvePairs) i.next();
		if (fncp.getCurve() == curve) {
			return fncp.getFilamentName();
		}
	}
	throw new RuntimeException("Curve not found in filamentGroup");
}
/**
 * Insert the method's description here.
 * Creation date: (8/19/00 3:30:04 PM)
 * @return java.lang.String[]
 */
public synchronized String[] getFilamentNames() {
	if(filamentNames.size() == 0){
		return new String[0];
	}
	return (String[])filamentNames.toArray(new String[filamentNames.size()]);
}
/**
 * Insert the method's description here.
 * Creation date: (8/19/00 3:30:04 PM)
 * @return java.lang.String[]
 */
public synchronized Filament[] getFilaments() {
	if(filamentNames.size() == 0){
		return new Filament[0];
	}
	Vector filamentVector = new Vector();
	for (Iterator i = filamentNames.iterator(); i.hasNext();) {
		//Get filament Names
		String filementName = (String) i.next();
		// Get filament for this name
		filamentVector.addElement(getFilament(filementName));
	}
	return (Filament[]) filamentVector.toArray(new Filament[0]);
}
/**
 * Insert the method's description here.
 * Creation date: (8/19/00 3:28:36 PM)
 * @param filamentName java.lang.String
 * @param curve cbit.vcell.geometry.Curve
 */
public synchronized void removeCurve(Curve curve) {
	removeCurve(curve,true);
}
/**
 * Insert the method's description here.
 * Creation date: (8/19/00 3:28:36 PM)
 * @param filamentName java.lang.String
 * @param curve cbit.vcell.geometry.Curve
 */
private synchronized void removeCurve(Curve curve, boolean bFirePropertyChange) {
	if (curve == null) {
		throw new IllegalArgumentException();
	}
	// Remove the curve
	boolean bCurveWasRemoved = curves.remove(curve);
	if (bCurveWasRemoved) {
		//Curve was removed so do cleanup
		// Remove the old filamentName-Cuve association
		for (Iterator i = filamentNamesCurves.iterator(); i.hasNext();) {
			FilamentNameCurvePairs old = (FilamentNameCurvePairs) i.next();
			if (old.getCurve() == curve) {
				i.remove();
				cleanupFilament(old.getFilamentName());
				if (bFirePropertyChange) {
					propertyChange.firePropertyChange(FILAMENT_GROUP_PROPERTY, null, null);
				}
				return;
			}
		}
		throw new RuntimeException("FilamentGroup.removeCurve: filamentName-Cuve association not found for curve");
	} else {
		throw new RuntimeException("FilamentGroup.removeCurve: Curve not found in filamentGroup");
	}
}
/**
 * Insert the method's description here.
 * Creation date: (8/19/00 4:40:33 PM)
 * @param filamentName java.lang.String
 */
public synchronized void removeFilament(String filamentName) {
	if (filamentName == null || filamentName.length() == 0) {
		throw new IllegalArgumentException();
	}
	//Get all the curves for this filamentName
	Filament filament = getFilament(filamentName);
	//Remove curves, this also remove filamentName-curve association
	//and removes filamentName when last curve is gone
	Curve[] filCurves = filament.getCurves();
	for (int i = 0; i < filCurves.length; i += 1) {
		removeCurve(filCurves[i]);
	}
	//Check just to make sure filamentName was removed during curve remove process
	if (filamentNames.contains(filamentName)) {
		//Found filamentName, bad
		throw new RuntimeException("FilamentGroup.removeFilament:  Internal Error");
	}
}
/**
 * Insert the method's description here.
 * Creation date: (8/21/00 2:35:25 PM)
 */
public void removePropertyChangeListener(java.beans.PropertyChangeListener listener){
	propertyChange.removePropertyChangeListener(listener);
}
}
