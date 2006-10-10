package cbit.vcell.geometry.surface;
import org.vcell.expression.IExpression;

import cbit.image.VCImage;
import cbit.vcell.geometry.RegionImage;
import cbit.render.*;
import cbit.render.objects.SurfaceCollection;
import cbit.vcell.geometry.*;
import cbit.util.Compare;
/**
 * Insert the type's description here.
 * Creation date: (5/26/2004 9:58:01 AM)
 * @author: Jim Schaff
 */
public class GeometrySurfaceDescription implements cbit.util.Matchable, java.io.Serializable, java.beans.PropertyChangeListener, java.beans.VetoableChangeListener {
	protected transient java.beans.VetoableChangeSupport vetoPropertyChange;
	protected transient java.beans.PropertyChangeSupport propertyChange;
	private cbit.util.ISize fieldVolumeSampleSize = null;
	private java.lang.Double fieldFilterCutoffFrequency = null;
	private transient cbit.vcell.geometry.RegionImage fieldRegionImage = null;
	private transient SurfaceCollection fieldSurfaceCollection = null;
	private cbit.vcell.geometry.Geometry fieldGeometry = null;
	private cbit.vcell.geometry.surface.GeometricRegion[] fieldGeometricRegions = null;

/**
 * GeometrySurfaceDescription constructor comment.
 */
public GeometrySurfaceDescription(cbit.vcell.geometry.Geometry geometry) {
	super();
	addPropertyChangeListener(this);
	addVetoableChangeListener(this);
	geometry.getGeometrySpec().addPropertyChangeListener(this);
	SubVolume subVolumes[] = geometry.getGeometrySpec().getSubVolumes();
	for (int i = 0; i < subVolumes.length; i++){
		subVolumes[i].addPropertyChangeListener(this);
	}
	
	this.fieldGeometry = geometry;	
	if (geometry.getDimension()<1){
		throw new IllegalArgumentException("GeometrySurfaceDescriptions only valid for spatial Geometrys");
	}

	//
	// set default volumeSampling
	//
	cbit.util.ISize sampleSize = geometry.getGeometrySpec().getDefaultSampledImageSize();
	//if (geometry.getGeometrySpec().getImage()!=null){
		//VCImage image = geometry.getGeometrySpec().getImage();
		//sampleSize = new cbit.util.ISize(image.getNumX(),image.getNumY(),image.getNumZ());
	//}
	
	// force to be 3D if at all spatial ... due to limitations in surface representation ... not too expensive.
	/*
	if (geometry.getGeometrySpec().getDimension()<3){
		sampleSize = new cbit.util.ISize(Math.max(3,sampleSize.getX()),Math.max(3,sampleSize.getY()),Math.max(3,sampleSize.getZ()));
		System.out.println("GeometrySurfaceDescription(): padding geometry from "+geometry.getGeometrySpec().getDimension()+"D to 3D for surface generation - region determination");
	}
	*/
	fieldVolumeSampleSize = sampleSize;

	//
	// set default cutoff frequency for Taubin filter
	//
	fieldFilterCutoffFrequency = new Double(0.3);
}


/**
 * GeometrySurfaceDescription constructor comment.
 */
public GeometrySurfaceDescription(Geometry geometry, GeometrySurfaceDescription otherGeometrySurfaceDescription) {
	addPropertyChangeListener(this);
	addVetoableChangeListener(this);
	geometry.getGeometrySpec().addPropertyChangeListener(this);
	SubVolume subVolumes[] = geometry.getGeometrySpec().getSubVolumes();
	for (int i = 0; i < subVolumes.length; i++){
		subVolumes[i].addPropertyChangeListener(this);
	}
	
	
	this.fieldGeometry = geometry;
	this.fieldFilterCutoffFrequency = otherGeometrySurfaceDescription.fieldFilterCutoffFrequency;
	this.fieldVolumeSampleSize = otherGeometrySurfaceDescription.fieldVolumeSampleSize;
	this.fieldRegionImage = otherGeometrySurfaceDescription.fieldRegionImage;
	//
	// don't copy surfaces or regions right now
	//
	//if (otherGeometrySurfaceDescription.fieldSurfaceCollection!=null){
		//this.fieldSurfaceCollection = new SurfaceCollection();
		//for (int i = 0; i < otherGeometrySurfaceDescription.fieldSurfaceCollection.getSurfaceCount(); i++){
			//Surface surface = otherGeometrySurfaceDescription.fieldSurfaceCollection.getSurfaces(i);
			//this.fieldSurfaceCollection.addSurface(new FastSurface()
		//}
	//}
}


/**
 * The addPropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void addPropertyChangeListener(java.beans.PropertyChangeListener listener) {
	getPropertyChange().addPropertyChangeListener(listener);
}


/**
 * The addPropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void addPropertyChangeListener(java.lang.String propertyName, java.beans.PropertyChangeListener listener) {
	getPropertyChange().addPropertyChangeListener(propertyName, listener);
}


/**
 * The addVetoableChangeListener method was generated to support the vetoPropertyChange field.
 */
public synchronized void addVetoableChangeListener(java.beans.VetoableChangeListener listener) {
	getVetoPropertyChange().addVetoableChangeListener(listener);
}


/**
 * The addVetoableChangeListener method was generated to support the vetoPropertyChange field.
 */
public synchronized void addVetoableChangeListener(java.lang.String propertyName, java.beans.VetoableChangeListener listener) {
	getVetoPropertyChange().addVetoableChangeListener(propertyName, listener);
}


/**
 * Checks for internal representation of objects, not keys from database
 * @return boolean
 * @param obj java.lang.Object
 */
public boolean compareEqual(cbit.util.Matchable obj) {
	if (obj == this){
		return true;
	}

	if (obj instanceof GeometrySurfaceDescription){
		GeometrySurfaceDescription geometrySurfaceDescription = (GeometrySurfaceDescription)obj;
		if (!cbit.util.Compare.isEqual(getFilterCutoffFrequency(),geometrySurfaceDescription.getFilterCutoffFrequency())){
			return false;
		}
		if (!cbit.util.Compare.isEqual(getVolumeSampleSize(),geometrySurfaceDescription.getVolumeSampleSize())){
			return false;
		}
		if (!cbit.util.Compare.isEqualOrNull(getGeometricRegions(),geometrySurfaceDescription.getGeometricRegions())){
			return false;
		}
		
		return true;
	}else{
		return false;
	}
}


/**
 * The firePropertyChange method was generated to support the propertyChange field.
 */
public void firePropertyChange(java.beans.PropertyChangeEvent evt) {
	getPropertyChange().firePropertyChange(evt);
}


/**
 * The firePropertyChange method was generated to support the propertyChange field.
 */
public void firePropertyChange(java.lang.String propertyName, int oldValue, int newValue) {
	getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
}


/**
 * The firePropertyChange method was generated to support the propertyChange field.
 */
public void firePropertyChange(java.lang.String propertyName, java.lang.Object oldValue, java.lang.Object newValue) {
	getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
}


/**
 * The firePropertyChange method was generated to support the propertyChange field.
 */
public void firePropertyChange(java.lang.String propertyName, boolean oldValue, boolean newValue) {
	getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
}


/**
 * The fireVetoableChange method was generated to support the vetoPropertyChange field.
 */
public void fireVetoableChange(java.beans.PropertyChangeEvent evt) throws java.beans.PropertyVetoException {
	getVetoPropertyChange().fireVetoableChange(evt);
}


/**
 * The fireVetoableChange method was generated to support the vetoPropertyChange field.
 */
public void fireVetoableChange(java.lang.String propertyName, int oldValue, int newValue) throws java.beans.PropertyVetoException {
	getVetoPropertyChange().fireVetoableChange(propertyName, oldValue, newValue);
}


/**
 * The fireVetoableChange method was generated to support the vetoPropertyChange field.
 */
public void fireVetoableChange(java.lang.String propertyName, java.lang.Object oldValue, java.lang.Object newValue) throws java.beans.PropertyVetoException {
	getVetoPropertyChange().fireVetoableChange(propertyName, oldValue, newValue);
}


/**
 * The fireVetoableChange method was generated to support the vetoPropertyChange field.
 */
public void fireVetoableChange(java.lang.String propertyName, boolean oldValue, boolean newValue) throws java.beans.PropertyVetoException {
	getVetoPropertyChange().fireVetoableChange(propertyName, oldValue, newValue);
}


/**
 * Gets the filterCutoffFrequency property (java.lang.Double) value.
 * @return The filterCutoffFrequency property value.
 * @see #setFilterCutoffFrequency
 */
public java.lang.Double getFilterCutoffFrequency() {
	return fieldFilterCutoffFrequency;
}


/**
 * Gets the geometricRegions property (cbit.vcell.geometry.surface.GeometricRegion[]) value.
 * @return The geometricRegions property value.
 * @see #setGeometricRegions
 */
public cbit.vcell.geometry.surface.GeometricRegion[] getGeometricRegions() {
	return fieldGeometricRegions;
}


/**
 * Gets the geometricRegions index property (cbit.vcell.geometry.surface.GeometricRegion) value.
 * @return The geometricRegions property value.
 * @param index The index value into the property array.
 * @see #setGeometricRegions
 */
public GeometricRegion getGeometricRegions(int index) {
	return getGeometricRegions()[index];
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
 * Accessor for the propertyChange field.
 */
protected java.beans.PropertyChangeSupport getPropertyChange() {
	if (propertyChange == null) {
		propertyChange = new java.beans.PropertyChangeSupport(this);
	};
	return propertyChange;
}


/**
 * Gets the regionImage property (cbit.vcell.geometry.RegionImage) value.
 * @return The regionImage property value.
 * @see #setRegionImage
 */
public cbit.vcell.geometry.RegionImage getRegionImage() {
	return fieldRegionImage;
}


/**
 * Gets the surfaceCollection property (cbit.vcell.geometry.surface.SurfaceCollection) value.
 * @return The surfaceCollection property value.
 * @see #setSurfaceCollection
 */
public SurfaceCollection getSurfaceCollection() {
	return fieldSurfaceCollection;
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
 * Gets the volumeSampleSize property (cbit.util.ISize) value.
 * @return The volumeSampleSize property value.
 * @see #setVolumeSampleSize
 */
public cbit.util.ISize getVolumeSampleSize() {
	return fieldVolumeSampleSize;
}


/**
 * The hasListeners method was generated to support the propertyChange field.
 */
public synchronized boolean hasListeners(java.lang.String propertyName) {
	return getPropertyChange().hasListeners(propertyName);
}


	/**
	 * This method gets called when a bound property is changed.
	 * @param evt A PropertyChangeEvent object describing the event source 
	 *   	and the property that has changed.
	 */
public void propertyChange(java.beans.PropertyChangeEvent evt) {
	if (evt.getSource()==this && evt.getPropertyName().equals("volumeSampleSize")){
		cbit.util.ISize oldValue = (cbit.util.ISize)evt.getOldValue();
		cbit.util.ISize newValue = (cbit.util.ISize)evt.getNewValue();
		if (!oldValue.compareEqual(newValue)){
			try {
				fieldRegionImage = null; // nobody listens to this, updateAll() will propagate changes
				setSurfaceCollection(null);
				setGeometricRegions(null);
			}catch (Exception e){
				e.printStackTrace(System.out);
			}
		}
	}
	if (evt.getSource()==this && evt.getPropertyName().equals("filterCutoffFrequency")){
		Double oldValue = (Double)evt.getOldValue();
		Double newValue = (Double)evt.getNewValue();
		if (!oldValue.equals(newValue)){
			try {
				setSurfaceCollection(null);
				setGeometricRegions(null);
			}catch (Exception e){
				e.printStackTrace(System.out);
			}
		}
	}
	if (evt.getSource() == getGeometry().getGeometrySpec() && (evt.getPropertyName().equals("extent") || evt.getPropertyName().equals("origin"))){
		cbit.util.Matchable oldExtentOrOrigin = (cbit.util.Matchable)evt.getOldValue();
		cbit.util.Matchable newExtentOrOrigin = (cbit.util.Matchable)evt.getNewValue();
		if (!Compare.isEqual(oldExtentOrOrigin,newExtentOrOrigin)){
			try {
				fieldRegionImage = null; // nobody listens to this, updateAll() will propagate changes
				setSurfaceCollection(null);
				setGeometricRegions(null);
			}catch (Exception e){
				e.printStackTrace(System.out);
			}
		}
	}
	if (evt.getSource() instanceof AnalyticSubVolume && evt.getPropertyName().equals("expression")) {
		IExpression oldExpression = (IExpression)evt.getOldValue();
		IExpression newExpression = (IExpression)evt.getNewValue();
		if (!Compare.isEqual(oldExpression,newExpression)) {
			try {
				fieldRegionImage = null; // nobody listens to this, updateAll() will propagate changes
				setSurfaceCollection(null);
				setGeometricRegions(null);
			}catch (Exception e){
				e.printStackTrace(System.out);
			}
		}
	}
	if (evt.getSource() instanceof SubVolume && evt.getPropertyName().equals("name")) {
		String oldName = (String)evt.getOldValue();
		String newName = (String)evt.getNewValue();
		if (!Compare.isEqual(oldName,newName)){
			try {
				if (getRegionImage()!=null && getSurfaceCollection()!=null) {
					setGeometricRegions(GeometrySurfaceUtils.getUpdatedGeometricRegions(this,getRegionImage(),getSurfaceCollection()));
				}else{
					setGeometricRegions(null);
				}
			}catch (Exception e){
				e.printStackTrace(System.out);
			}
		}
	}
	if (evt.getSource()==getGeometry().getGeometrySpec() && evt.getPropertyName().equals("subVolumes")){
		SubVolume[] oldValue = (SubVolume[])evt.getOldValue();
		SubVolume[] newValue = (SubVolume[])evt.getNewValue();
		//
		// update listeners
		//
		for (int i = 0; oldValue!=null && i < oldValue.length; i++){
			oldValue[i].removePropertyChangeListener(this);
		}
		for (int i = 0; newValue!=null && i < newValue.length; i++){
			newValue[i].addPropertyChangeListener(this);
		}
		//
		// if instances are different but content is same, then just replace instances
		// otherwise, invalidate surfaces and regions.
		//
		if (oldValue==null || newValue==null || !cbit.util.Compare.isEqualOrdered(oldValue,newValue)){
			try {
				fieldRegionImage = null;
				setSurfaceCollection(null);
				setGeometricRegions(null);
			}catch (Exception e){
				e.printStackTrace(System.out);
			}
		} else if (fieldGeometricRegions!=null && oldValue != newValue) {
			//
			// update instances of subvolume in volumeGeometricRegions.
			//
			for (int i = 0; i < newValue.length; i++){
				SubVolume subVolume = newValue[i];
				for (int j = 0; j < fieldGeometricRegions.length; j++){
					if (fieldGeometricRegions[j] instanceof VolumeGeometricRegion){
						VolumeGeometricRegion volumeRegion = (VolumeGeometricRegion)fieldGeometricRegions[j];
						//
						// replace instance of subVolume.
						//
						if (volumeRegion.getSubVolume().compareEqual(subVolume) && volumeRegion.getSubVolume() != subVolume){
							volumeRegion.setSubVolume(subVolume);
						}
					}
				}
			}
		}
	}
}


/**
 * Insert the method's description here.
 * Creation date: (5/26/2004 11:00:00 AM)
 */
public void refreshDependencies() {
	removePropertyChangeListener(this);
	removeVetoableChangeListener(this);
	addPropertyChangeListener(this);
	addVetoableChangeListener(this);

	fieldGeometry.getGeometrySpec().removePropertyChangeListener(this);
	fieldGeometry.getGeometrySpec().addPropertyChangeListener(this);

	SubVolume subVolumes[] = fieldGeometry.getGeometrySpec().getSubVolumes();
	for (int i = 0;subVolumes!=null && i < subVolumes.length; i++){
		subVolumes[i].removePropertyChangeListener(this);
		subVolumes[i].addPropertyChangeListener(this);
	}
}

/**
 * The removePropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void removePropertyChangeListener(java.beans.PropertyChangeListener listener) {
	getPropertyChange().removePropertyChangeListener(listener);
}


/**
 * The removePropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void removePropertyChangeListener(java.lang.String propertyName, java.beans.PropertyChangeListener listener) {
	getPropertyChange().removePropertyChangeListener(propertyName, listener);
}


/**
 * The removeVetoableChangeListener method was generated to support the vetoPropertyChange field.
 */
public synchronized void removeVetoableChangeListener(java.beans.VetoableChangeListener listener) {
	getVetoPropertyChange().removeVetoableChangeListener(listener);
}


/**
 * The removeVetoableChangeListener method was generated to support the vetoPropertyChange field.
 */
public synchronized void removeVetoableChangeListener(java.lang.String propertyName, java.beans.VetoableChangeListener listener) {
	getVetoPropertyChange().removeVetoableChangeListener(propertyName, listener);
}


/**
 * Sets the filterCutoffFrequency property (java.lang.Double) value.
 * @param filterCutoffFrequency The new value for the property.
 * @exception java.beans.PropertyVetoException The exception description.
 * @see #getFilterCutoffFrequency
 */
public void setFilterCutoffFrequency(java.lang.Double filterCutoffFrequency) throws java.beans.PropertyVetoException {
	if (filterCutoffFrequency == fieldFilterCutoffFrequency) {
		return;
	}
	Double oldValue = fieldFilterCutoffFrequency;
	fireVetoableChange("filterCutoffFrequency", oldValue, filterCutoffFrequency);
	fieldFilterCutoffFrequency = filterCutoffFrequency;
	firePropertyChange("filterCutoffFrequency", oldValue, filterCutoffFrequency);
}


/**
 * Sets the geometricRegions property (cbit.vcell.geometry.surface.GeometricRegion[]) value.
 * @param geometricRegions The new value for the property.
 * @exception java.beans.PropertyVetoException The exception description.
 * @see #getGeometricRegions
 */
public void setGeometricRegions(cbit.vcell.geometry.surface.GeometricRegion[] geometricRegions) throws java.beans.PropertyVetoException {
	if (fieldGeometricRegions == geometricRegions) {
		return;
	}
	cbit.vcell.geometry.surface.GeometricRegion[] oldValue = fieldGeometricRegions;
	fireVetoableChange("geometricRegions", oldValue, geometricRegions);
	fieldGeometricRegions = geometricRegions;
	firePropertyChange("geometricRegions", oldValue, geometricRegions);
}


/**
 * Sets the geometricRegions index property (cbit.vcell.geometry.surface.GeometricRegion[]) value.
 * @param index The index value into the property array.
 * @param geometricRegions The new value for the property.
 * @see #getGeometricRegions
 */
public void setGeometricRegions(int index, GeometricRegion geometricRegions) {
	if (fieldGeometricRegions[index] == geometricRegions) {
		return;
	}
	GeometricRegion oldValue = fieldGeometricRegions[index];
	fieldGeometricRegions[index] = geometricRegions;
	if (oldValue != null && !oldValue.equals(geometricRegions)) {
		firePropertyChange("geometricRegions", null, fieldGeometricRegions);
	};
}


/**
 * Sets the surfaceCollection property (cbit.vcell.geometry.surface.SurfaceCollection) value.
 * @param surfaceCollection The new value for the property.
 * @see #getSurfaceCollection
 */
private void setSurfaceCollection(SurfaceCollection surfaceCollection) {
	if (surfaceCollection == fieldSurfaceCollection) {
		return;
	}
	SurfaceCollection oldValue = fieldSurfaceCollection;
	fieldSurfaceCollection = surfaceCollection;
	firePropertyChange("surfaceCollection", oldValue, surfaceCollection);
}


/**
 * Sets the volumeSampleSize property (cbit.util.ISize) value.
 * @param volumeSampleSize The new value for the property.
 * @exception java.beans.PropertyVetoException The exception description.
 * @see #getVolumeSampleSize
 */
public void setVolumeSampleSize(cbit.util.ISize volumeSampleSize) throws java.beans.PropertyVetoException {
	if (fieldVolumeSampleSize == volumeSampleSize) {
		return;
	}
	
	cbit.util.ISize oldValue = fieldVolumeSampleSize;
	fireVetoableChange("volumeSampleSize", oldValue, volumeSampleSize);
	
	switch (fieldGeometry.getDimension()) {
		case 1:
			fieldVolumeSampleSize = new cbit.util.ISize(volumeSampleSize.getX(), 1, 1);
			break;
		case 2:
			fieldVolumeSampleSize = new cbit.util.ISize(volumeSampleSize.getX(), volumeSampleSize.getY(), 1);
			break;
		case 3:
			fieldVolumeSampleSize = volumeSampleSize;
			break;
	}
	firePropertyChange("volumeSampleSize", oldValue, volumeSampleSize);
}


/**
 * Insert the method's description here.
 * Creation date: (5/26/2004 10:19:59 AM)
 */
public void updateAll() throws GeometryException, cbit.image.ImageException {
	//
	// updates if necessary: regionImage, surfaceCollection and resolvedLocations[]
	//
	// assumes that volumeSampleSize and filterCutoffFrequency are already specified
	//
	//

	//
	// make new RegionImage if necessary missing or wrong size
	//
	GeometrySpec geometrySpec = getGeometry().getGeometrySpec();
	boolean bChanged = false;
	RegionImage updatedRegionImage = GeometrySurfaceUtils.getUpdatedRegionImage(this);
	if (updatedRegionImage != getRegionImage()){  // getUpdatedRegionImage returns same image if not changed
		fieldRegionImage = updatedRegionImage;
		bChanged = true;
	}
	//
	// make the surfaces (if necessary)
	//
	if (getSurfaceCollection()==null || bChanged) {
		setSurfaceCollection(GeometrySurfaceUtils.getUpdatedSurfaceCollection(this,getRegionImage()));
		bChanged = true;
	}

	//
	// parse regionImage into VolumeGeometricRegions and SurfaceCollection into SurfaceGeometricRegions
	//
	if (getGeometricRegions()==null || bChanged){
		try {
			setGeometricRegions(GeometrySurfaceUtils.getUpdatedGeometricRegions(this,getRegionImage(),getSurfaceCollection()));
		}catch (java.beans.PropertyVetoException e){
			e.printStackTrace(System.out);
			throw new GeometryException("unexpected exception while generating regions: "+e.getMessage());
		}
	}
		
}


	/**
	 * This method gets called when a constrained property is changed.
	 *
	 * @param     evt a <code>PropertyChangeEvent</code> object describing the
	 *   	      event source and the property that has changed.
	 * @exception PropertyVetoException if the recipient wishes the property
	 *              change to be rolled back.
	 */
public void vetoableChange(java.beans.PropertyChangeEvent evt) throws java.beans.PropertyVetoException {
	if (evt.getSource()==this && evt.getPropertyName().equals("volumeSampleSize")){
		if (evt.getNewValue()==null){
			throw new java.beans.PropertyVetoException("volumeSampleSize cannot be null",evt);
		}
		/*
		cbit.util.ISize size = (cbit.util.ISize)evt.getNewValue();		
		if (size.getX()<3 || size.getY()<3 || size.getZ()<3){
			throw new java.beans.PropertyVetoException("volumeSampleSize ("+size+") must be at least 3 in each dimension",evt);
		}
		*/
	}
	if (evt.getSource()==this && evt.getPropertyName().equals("filterCutoffFrequency")){
		if (evt.getNewValue()==null){
			throw new java.beans.PropertyVetoException("filterCutoffFrequency cannot be null",evt);
		}
		Double cutoffFrequency = (Double)evt.getNewValue();
		if (cutoffFrequency.doubleValue() <= 0.0 || cutoffFrequency.doubleValue() >= 2.0){
			throw new java.beans.PropertyVetoException("filterCutoffFrequency ("+cutoffFrequency+") must be between [0,2]",evt);
		}
	}
}
}