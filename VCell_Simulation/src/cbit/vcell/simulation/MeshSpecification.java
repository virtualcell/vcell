package cbit.vcell.simulation;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.beans.*;
import java.io.Serializable;

import org.vcell.util.*;

import cbit.vcell.geometry.*;
import cbit.vcell.math.VCML;
/**
 * Insert the class' description here.
 * Creation date: (8/20/2000 6:32:34 AM)
 * @author: John Wagner
 */
public class MeshSpecification implements Serializable, Matchable, java.beans.VetoableChangeListener {
	private java.lang.Double fieldMinimumAngleConstraint = null;
	private java.lang.Double fieldMaximumSizeConstraint = null;
	private cbit.vcell.geometry.Geometry fieldGeometry = null;
	private org.vcell.util.ISize fieldSamplingSize = new org.vcell.util.ISize(0, 0, 0);
	protected transient java.beans.PropertyChangeSupport propertyChange;
	protected transient java.beans.VetoableChangeSupport vetoPropertyChange;

/**
 * MeshDescription constructor comment.
 */
public MeshSpecification() {
	this((Geometry)null);
}


/**
 * MeshDescription constructor comment.
 */
public MeshSpecification(Geometry geometry) {
	addVetoableChangeListener(this);
	setGeometry(geometry);
	resetSamplingSize();
}


/**
 * MeshDescription constructor comment.
 */
public MeshSpecification(MeshSpecification meshSpecification) {
	addVetoableChangeListener(this);
	setGeometry(meshSpecification.getGeometry());
	setMaximumSizeConstraint(meshSpecification.getMaximumSizeConstraint());
	setMinimumAngleConstraint(meshSpecification.getMinimumAngleConstraint());
	try {
		setSamplingSize(meshSpecification.getSamplingSize());
	}catch (PropertyVetoException e){
		e.printStackTrace(System.out);
		throw new RuntimeException(e.getMessage());
	}
}


/**
 * The addPropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void addPropertyChangeListener(java.beans.PropertyChangeListener listener) {
	getPropertyChange().addPropertyChangeListener(listener);
}


/**
 * The addVetoableChangeListener method was generated to support the vetoPropertyChange field.
 */
public synchronized void addVetoableChangeListener(java.beans.VetoableChangeListener listener) {
	getVetoPropertyChange().addVetoableChangeListener(listener);
}


/**
 * This method was created in VisualAge.
 * @return boolean
 * @param obj cbit.util.Matchable
 */
public boolean compareEqual(Matchable obj) {
	if (obj instanceof MeshSpecification){
		MeshSpecification ms = (MeshSpecification)obj;
		if (this.fieldSamplingSize.getX() != ms.fieldSamplingSize.getX()){
			return false;
		}
		if (this.fieldSamplingSize.getY() != ms.fieldSamplingSize.getY()){
			return false;
		}
		if (this.fieldSamplingSize.getZ() != ms.fieldSamplingSize.getZ()){
			return false;
		}
		if (!Compare.isEqualOrNull(this.fieldMinimumAngleConstraint,ms.fieldMinimumAngleConstraint)){
			return false;
		}
		if (!Compare.isEqualOrNull(this.fieldMaximumSizeConstraint,ms.fieldMaximumSizeConstraint)){
			return false;
		}
	}else{
		return false;
	}
	return true;
}


/**
 * Sets the geometry property (cbit.vcell.geometry.Geometry) value.
 * @param geometry The new value for the property.
 * @see #getGeometry
 */
public void copyFrom(MeshSpecification meshSpec) {
	setGeometry(meshSpec.getGeometry());
	try {
		setSamplingSize(meshSpec.getSamplingSize());
	}catch (PropertyVetoException e){
		throw new RuntimeException("PropertyVetoException while copying samplingSize ... "+e.getMessage());
	} 
	setMaximumSizeConstraint(meshSpec.getMaximumSizeConstraint());
	setMinimumAngleConstraint(meshSpec.getMinimumAngleConstraint());
}


/**
 * The firePropertyChange method was generated to support the propertyChange field.
 */
public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
	getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
}


/**
 * The fireVetoableChange method was generated to support the vetoPropertyChange field.
 */
public void fireVetoableChange(String propertyName, Object oldValue, Object newValue) throws java.beans.PropertyVetoException {
	getVetoPropertyChange().fireVetoableChange(propertyName, oldValue, newValue);
}


/**
 * Gets the geometry property (cbit.vcell.geometry.Geometry) value.
 * @return The geometry property value.
 * @see #setGeometry
 */
public cbit.vcell.geometry.Geometry getGeometry() {
	return fieldGeometry;
}


/**
 * Gets the maximumSizeConstraint property (java.lang.Double) value.
 * @return The maximumSizeConstraint property value.
 * @see #setMaximumSizeConstraint
 */
public java.lang.Double getMaximumSizeConstraint() {
	return fieldMaximumSizeConstraint;
}


/**
 * Gets the minimumAngleConstraint property (java.lang.Double) value.
 * @return The minimumAngleConstraint property value.
 * @see #setMinimumAngleConstraint
 */
public java.lang.Double getMinimumAngleConstraint() {
	return fieldMinimumAngleConstraint;
}


/**
 * Accessor for the propertyChange field.
 */
protected java.beans.PropertyChangeSupport getPropertyChange() {
	if (propertyChange == null) {
		propertyChange = new java.beans.PropertyChangeSupport(this);
	};
	return propertyChange;
}


/**
 * Gets the samplingSize property (cbit.util.ISize) value.
 * @return The samplingSize property value.
 * @see #setSamplingSize
 */
public org.vcell.util.ISize getSamplingSize() {
	// cbit.util.Assertion.assertNotNull(fieldSamplingSize);
	return fieldSamplingSize;
}


/**
 * This method was created by a SmartGuide.
 * @return java.lang.String
 */
public String getVCML() {
	StringBuffer buffer = new StringBuffer();
	buffer.append(VCML.MeshSpecification+" {\n");
	ISize size = getSamplingSize();
	buffer.append("\t"+VCML.Size+" "+size.getX()+" "+size.getY()+" "+size.getZ()+"\n");
	buffer.append("}\n");
	return buffer.toString();		
}


/**
 * Accessor for the vetoPropertyChange field.
 */
protected java.beans.VetoableChangeSupport getVetoPropertyChange() {
	if (vetoPropertyChange == null) {
		vetoPropertyChange = new java.beans.VetoableChangeSupport(this);
	};
	return vetoPropertyChange;
}


/**
 * Insert the method's description here.
 * Creation date: (6/1/00 2:39:38 PM)
 */
public void refreshDependencies() {
	removeVetoableChangeListener(this);
	addVetoableChangeListener(this);
}

/**
 * The removePropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void removePropertyChangeListener(java.beans.PropertyChangeListener listener) {
	getPropertyChange().removePropertyChangeListener(listener);
}


/**
 * The removeVetoableChangeListener method was generated to support the vetoPropertyChange field.
 */
public synchronized void removeVetoableChangeListener(java.beans.VetoableChangeListener listener) {
	getVetoPropertyChange().removeVetoableChangeListener(listener);
}


/**
 * Sets the geometry property (cbit.vcell.geometry.Geometry) value.
 * @param geometry The new value for the property.
 * @see #getGeometry
 */
public void resetSamplingSize() {
	if (fieldGeometry==null){
		return;
	}
	long numX = 1;
	long numY = 1;
	long numZ = 1;
	
	int dim = getGeometry().getDimension();
	switch (dim){
		case 1:{
			numX = 200;
			numY = 1;
			numZ = 1;
			break;
		}
		case 2:{
			long total = 101*101;
			Extent extent = getGeometry().getExtent();
			double aspectRatio = extent.getX()/extent.getY();
			numY = Math.max(3, Math.round(Math.sqrt(total/aspectRatio)));
			numX = Math.max(3, Math.round(total/numY));
			numZ = 1;
			break;
		}
		case 3:{
			long total = 51*51*51;
			Extent extent = getGeometry().getExtent();
			double aspectRatioZX = extent.getZ()/extent.getX();
			double aspectRatioZY = extent.getZ()/extent.getY();
			numZ = Math.max(3, Math.round(Math.pow(total*aspectRatioZX*aspectRatioZY,0.333333333)));
			numX = Math.max(3, Math.round(numZ/aspectRatioZX));
			numY = Math.max(3, Math.round(numZ/aspectRatioZY));
			break;
		}
	}
	
	ISize samplingSize = new ISize((int)numX,(int)numY,(int)numZ);
	try {
		setSamplingSize(samplingSize);
	}catch (java.beans.PropertyVetoException e){
		throw new RuntimeException("unexpected error while setting mesh sampling : "+e.getMessage());
	}
}


/**
 * Sets the geometry property (cbit.vcell.geometry.Geometry) value.
 * @param geometry The new value for the property.
 * @see #getGeometry
 */
public void setGeometry(cbit.vcell.geometry.Geometry geometry) {
	Geometry oldGeometry = fieldGeometry;
	fieldGeometry = geometry;
	firePropertyChange("geometry",oldGeometry,fieldGeometry);
	
	if (oldGeometry==null || fieldGeometry.getDimension() != oldGeometry.getDimension()){
		resetSamplingSize();
	}
}


/**
 * Sets the maximumSizeConstraint property (java.lang.Double) value.
 * @param maximumSizeConstraint The new value for the property.
 * @see #getMaximumSizeConstraint
 */
public void setMaximumSizeConstraint(java.lang.Double maximumSizeConstraint) {
	Double oldMaximumSizeConstraint = fieldMaximumSizeConstraint;
	fieldMaximumSizeConstraint = maximumSizeConstraint;
	firePropertyChange("maximumSizeConstraint",oldMaximumSizeConstraint,fieldMaximumSizeConstraint);
}


/**
 * Sets the minimumAngleConstraint property (java.lang.Double) value.
 * @param minimumAngleConstraint The new value for the property.
 * @see #getMinimumAngleConstraint
 */
public void setMinimumAngleConstraint(java.lang.Double minimumAngleConstraint) {
	Double oldMinimumAngleConstraint = fieldMinimumAngleConstraint;
	fieldMinimumAngleConstraint = minimumAngleConstraint;
	firePropertyChange("minimumAngleConstraint",oldMinimumAngleConstraint,fieldMinimumAngleConstraint);
}


/**
 * Sets the samplingSize property (cbit.util.ISize) value.
 * @param samplingSize The new value for the property.
 * @see #getSamplingSize
 */
public void setSamplingSize(org.vcell.util.ISize samplingSize) throws java.beans.PropertyVetoException {
	// cbit.util.Assertion.assertNotNull(fieldSamplingSize);
	//
	// check for no-change
	//
	if ((samplingSize.getX() != fieldSamplingSize.getX()) || 
		(samplingSize.getY() != fieldSamplingSize.getY()) ||
		(samplingSize.getZ() != fieldSamplingSize.getZ())){
			
		org.vcell.util.ISize oldSamplingSize = fieldSamplingSize;
		fireVetoableChange("samplingSize",oldSamplingSize,samplingSize);
		fieldSamplingSize = samplingSize;
		firePropertyChange("samplingSize",oldSamplingSize,fieldSamplingSize);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (6/1/00 2:01:28 PM)
 * @param event java.beans.PropertyChangeEvent
 * @exception java.beans.PropertyVetoException The exception description.
 */
public void vetoableChange(java.beans.PropertyChangeEvent event) throws java.beans.PropertyVetoException {
	if (event.getSource() == this && event.getPropertyName().equals("samplingSize")){
		ISize isize = (ISize)event.getNewValue();
		if (getGeometry()!=null){
			int dim = getGeometry().getDimension();
			if (dim == 1){
				if (isize.getX()<3 || isize.getY()!=1 || isize.getZ()!=1){
					throw new java.beans.PropertyVetoException("1D geometry, mesh size must be x>=3, y=1, z=1",event);
				}
			}
			if (dim == 2){
				if (isize.getX()<3 || isize.getY()<3 || isize.getZ()!=1){
					throw new java.beans.PropertyVetoException("2D geometry, mesh size must be x>=3, y>=3, z=1",event);
				}
			}
			if (dim == 3){
				if (isize.getX()<3 || isize.getY()<3 || isize.getZ()<3){
					throw new java.beans.PropertyVetoException("3D geometry, mesh size must be x>=3, y>=3, z>=3",event);
				}
			}
		}
	}
}
}