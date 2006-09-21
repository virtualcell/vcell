package cbit.vcell.geometry;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.io.*;
import java.util.HashSet;
/**
 * Insert the type's description here.
 * Creation date: (7/14/00 4:06:35 PM)
 * @author: 
 */
public class Filament implements Serializable {
	private java.lang.String fieldName = null;
	private HashSet fieldCurves = null;
/**
 * Filament constructor comment.
 */
public Filament(String filamentName,HashSet curves) {
	super();
	this.fieldName = filamentName;
	this.fieldCurves = curves;
}
/**
 * Gets the curves property (cbit.vcell.geometry.Curve[]) value.
 * @return The curves property value.
 * @see #setCurves
 */
public cbit.vcell.geometry.Curve[] getCurves() {
	return (cbit.vcell.geometry.Curve[])fieldCurves.toArray(new Curve[fieldCurves.size()]);
}
/**
 * Gets the name property (java.lang.String) value.
 * @return The name property value.
 * @see #setName
 */
public java.lang.String getName() {
	return fieldName;
}
}
