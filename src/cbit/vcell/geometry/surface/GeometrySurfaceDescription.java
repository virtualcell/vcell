package cbit.vcell.geometry.surface;
import java.beans.PropertyVetoException;
import java.util.ArrayList;

import org.vcell.util.Compare;
import org.vcell.util.ISize;
import org.vcell.util.Matchable;

import cbit.gui.PropertyChangeListenerProxyVCell;
import cbit.image.ImageException;
import cbit.vcell.client.server.VCellThreadChecker;
import cbit.vcell.geometry.AnalyticSubVolume;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.geometry.GeometryClass;
import cbit.vcell.geometry.GeometryException;
import cbit.vcell.geometry.RegionImage;
import cbit.vcell.geometry.SubVolume;
import cbit.vcell.geometry.SurfaceClass;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
/**
 * Insert the type's description here.
 * Creation date: (5/26/2004 9:58:01 AM)
 * @author: Jim Schaff
 */
public class GeometrySurfaceDescription implements Matchable, java.io.Serializable, java.beans.PropertyChangeListener, java.beans.VetoableChangeListener {
	protected transient java.beans.VetoableChangeSupport vetoPropertyChange;
	protected transient java.beans.PropertyChangeSupport propertyChange;
	private ISize fieldVolumeSampleSize = null;
	private java.lang.Double fieldFilterCutoffFrequency = null;
	private transient RegionImage fieldRegionImage = null;
	private transient SurfaceCollection fieldSurfaceCollection = null;
	private Geometry fieldGeometry = null;
	private GeometricRegion[] fieldGeometricRegions = null;
	private SurfaceClass[] fieldSurfaceClasses = null;

/**
 * GeometrySurfaceDescription constructor comment.
 */
public GeometrySurfaceDescription(Geometry geometry) {
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
	ISize sampleSize = geometry.getGeometrySpec().getDefaultSampledImageSize();
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
	getPropertyChange().addPropertyChangeListener(new PropertyChangeListenerProxyVCell(listener));
}

/**
 * The addVetoableChangeListener method was generated to support the vetoPropertyChange field.
 */
public synchronized void addVetoableChangeListener(java.beans.VetoableChangeListener listener) {
	getVetoPropertyChange().addVetoableChangeListener(listener);
}


/**
 * Checks for internal representation of objects, not keys from database
 * @return boolean
 * @param obj java.lang.Object
 */
public boolean compareEqual(Matchable obj) {
	if (obj == this){
		return true;
	}

	if (obj instanceof GeometrySurfaceDescription){
		GeometrySurfaceDescription geometrySurfaceDescription = (GeometrySurfaceDescription)obj;
		if (!Compare.isEqual(getFilterCutoffFrequency(),geometrySurfaceDescription.getFilterCutoffFrequency())){
			return false;
		}
		if (!Compare.isEqual(getVolumeSampleSize(),geometrySurfaceDescription.getVolumeSampleSize())){
			return false;
		}
		if (!Compare.isEqualOrNull(getGeometricRegions(),geometrySurfaceDescription.getGeometricRegions())){
			return false;
		}
		
		return true;
	}else{
		return false;
	}
}

public SurfaceClass[] getAdjacentSurfaceClasses(SubVolume subVolume) {
	SurfaceClass[] surfaceClasses = getSurfaceClasses();
	ArrayList<SurfaceClass> adjacentSurfaceClassList = new ArrayList<SurfaceClass>();
	for (int i = 0; i < surfaceClasses.length; i++) {
		if (surfaceClasses[i].isAdjacentTo(subVolume)){
			adjacentSurfaceClassList.add(surfaceClasses[i]);
		}
	}
	return adjacentSurfaceClassList.toArray(new SurfaceClass[adjacentSurfaceClassList.size()]);
}


/**
 * The firePropertyChange method was generated to support the propertyChange field.
 */
public void firePropertyChange(java.lang.String propertyName, java.lang.Object oldValue, java.lang.Object newValue) {
	getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
}


/**
 * The fireVetoableChange method was generated to support the vetoPropertyChange field.
 */
public void fireVetoableChange(java.lang.String propertyName, java.lang.Object oldValue, java.lang.Object newValue) throws java.beans.PropertyVetoException {
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
public GeometricRegion[] getGeometricRegions() {
	return fieldGeometricRegions;
}

public SurfaceClass[] getSurfaceClasses() {
	if (fieldSurfaceClasses==null && fieldGeometricRegions!=null){
		setSurfaceClasses(computeSurfaceClasses(fieldGeometricRegions));
	}
	return fieldSurfaceClasses;
}

public GeometricRegion[] getGeometricRegions(GeometryClass geometryClass){
	ArrayList<GeometricRegion> regions = new ArrayList<GeometricRegion>();
	if (this.fieldGeometricRegions==null || this.fieldGeometricRegions.length==0){
		throw new RuntimeException("geometric regions have not been computed");
	}
	for (int j = 0; j < fieldGeometricRegions.length; j++) {
		if (getGeometryClass(fieldGeometricRegions[j]).equals(geometryClass)){
			regions.add(fieldGeometricRegions[j]);
		}
	}
	return regions.toArray(new GeometricRegion[regions.size()]);
}

public GeometryClass getGeometryClass(GeometricRegion geometricRegion){
	if (geometricRegion instanceof VolumeGeometricRegion){
		return ((VolumeGeometricRegion)geometricRegion).getSubVolume();
	}else if (geometricRegion instanceof SurfaceGeometricRegion){
		SurfaceGeometricRegion surfaceRegion = (SurfaceGeometricRegion)geometricRegion;
		GeometricRegion[] adjacentRegions = surfaceRegion.getAdjacentGeometricRegions();
		if (adjacentRegions.length!=2 || 
				!(adjacentRegions[0] instanceof VolumeGeometricRegion) || 
				!(adjacentRegions[1] instanceof VolumeGeometricRegion)){
			throw new RuntimeException("expecting two adjacent volume regions for surfaceRegion");
		}
		SubVolume subvolume1 = ((VolumeGeometricRegion)adjacentRegions[0]).getSubVolume();
		SubVolume subvolume2 = ((VolumeGeometricRegion)adjacentRegions[1]).getSubVolume();
		return getSurfaceClass(subvolume1,subvolume2);
	}else{
		throw new RuntimeException("unexpected geometricRegion "+geometricRegion);
	}
}

public SurfaceClass getSurfaceClass(SubVolume subvolume1, SubVolume subvolume2) {
	SurfaceClass[] surfaceClasses = getSurfaceClasses();
	for (int i = 0; i < surfaceClasses.length; i++) {
		if (surfaceClasses[i].isAdjacentTo(subvolume1) && surfaceClasses[i].isAdjacentTo(subvolume2)) {
			return surfaceClasses[i];
		}
	}
	return null;
}

private SurfaceClass[] computeSurfaceClasses(GeometricRegion[] geometricRegions) {
	ArrayList<SurfaceClass> surfaceClasses = new ArrayList<SurfaceClass>();
	for (int i = 0; i < geometricRegions.length; i++) {
		if (geometricRegions[i] instanceof SurfaceGeometricRegion){
			SurfaceGeometricRegion surfaceRegion = (SurfaceGeometricRegion)geometricRegions[i];
			GeometricRegion[] adjacentRegions = surfaceRegion.getAdjacentGeometricRegions();
			if (adjacentRegions.length!=2){
				throw new RuntimeException("found a Surface Region with "+adjacentRegions.length+" adjacent regions, expected 2");
			}
			if (adjacentRegions[0] instanceof VolumeGeometricRegion && adjacentRegions[1] instanceof VolumeGeometricRegion){
				VolumeGeometricRegion volumeRegion0 = (VolumeGeometricRegion)adjacentRegions[0];
				SubVolume subVolume0 = volumeRegion0.getSubVolume();
				VolumeGeometricRegion volumeRegion1 = (VolumeGeometricRegion)adjacentRegions[1];
				SubVolume subVolume1 = volumeRegion1.getSubVolume();
				SurfaceClass surfaceClass = new SurfaceClass(subVolume0,subVolume1);
				
				boolean bFound = false;
				for (int j = 0; j < surfaceClasses.size(); j++) {
					if (surfaceClass.compareEqual(surfaceClasses.get(j))){
						bFound = true;
						break;
					}
				}
				
				if (!bFound){
					surfaceClasses.add(surfaceClass);
				}
			}
		}
	}
	return surfaceClasses.toArray(new SurfaceClass[surfaceClasses.size()]);
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
public Geometry getGeometry() {
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
public RegionImage getRegionImage() {
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
public org.vcell.util.ISize getVolumeSampleSize() {
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
		ISize oldValue = (ISize)evt.getOldValue();
		ISize newValue = (ISize)evt.getNewValue();
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
	if (evt.getSource()==this && evt.getPropertyName().equals("geometricRegions")){
		GeometricRegion[] oldValue = (GeometricRegion[])evt.getOldValue();
		GeometricRegion[] newValue = (GeometricRegion[])evt.getNewValue();
		if (oldValue==null || newValue==null || !Compare.isEqual(oldValue,newValue)){
			if (newValue==null){
				setSurfaceClasses(null);
			}else{
				setSurfaceClasses(computeSurfaceClasses((GeometricRegion[])newValue));
			}
		}
	}
	if (evt.getSource() == getGeometry().getGeometrySpec() && (evt.getPropertyName().equals("extent") || evt.getPropertyName().equals("origin"))){
		org.vcell.util.Matchable oldExtentOrOrigin = (org.vcell.util.Matchable)evt.getOldValue();
		org.vcell.util.Matchable newExtentOrOrigin = (org.vcell.util.Matchable)evt.getNewValue();
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
		Expression oldExpression = (Expression)evt.getOldValue();
		Expression newExpression = (Expression)evt.getNewValue();
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
		if (oldValue==null || newValue==null || !org.vcell.util.Compare.isEqualStrict(oldValue,newValue)){
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
public void setGeometricRegions(GeometricRegion[] geometricRegions) throws java.beans.PropertyVetoException {
	if (fieldGeometricRegions == geometricRegions) {
		return;
	}
	GeometricRegion[] oldValue = fieldGeometricRegions;
	fireVetoableChange("geometricRegions", oldValue, geometricRegions);
	fieldGeometricRegions = geometricRegions;
	firePropertyChange("geometricRegions", oldValue, geometricRegions);
}


/**
 * Sets the surfaceClasses property (cbit.vcell.geometry.SurfaceClass[]) value.
 * @param surfaceClasses The new value for the property.
 * @exception java.beans.PropertyVetoException The exception description.
 * @see #getGeometricRegions
 */
private void setSurfaceClasses(SurfaceClass[] surfaceClasses) {
	if (fieldSurfaceClasses == surfaceClasses) {
		return;
	}
//	cbit.vcell.geometry.surface.GeometricRegion[] oldValue = fieldGeometricRegions;
	SurfaceClass[] oldValue = fieldSurfaceClasses;
	fieldSurfaceClasses = surfaceClasses;
	firePropertyChange("surfaceClasses", oldValue, surfaceClasses);
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
public void setVolumeSampleSize(ISize volumeSampleSize) throws java.beans.PropertyVetoException {
	if (fieldVolumeSampleSize == volumeSampleSize) {
		return;
	}
	
	ISize oldValue = fieldVolumeSampleSize;
	fireVetoableChange("volumeSampleSize", oldValue, volumeSampleSize);
	
	switch (fieldGeometry.getDimension()) {
		case 1:
			fieldVolumeSampleSize = new ISize(volumeSampleSize.getX(), 1, 1);
			break;
		case 2:
			fieldVolumeSampleSize = new ISize(volumeSampleSize.getX(), volumeSampleSize.getY(), 1);
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
public void updateAll() throws GeometryException, ImageException, ExpressionException {
	//
	// updates if necessary: regionImage, surfaceCollection and resolvedLocations[]
	//
	// assumes that volumeSampleSize and filterCutoffFrequency are already specified
	//
	//

	//
	// make new RegionImage if necessary missing or wrong size
	//
	VCellThreadChecker.checkCpuIntensiveInvocation();
	
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
		setSurfaceCollection(updatedRegionImage.getSurfacecollection());
		bChanged = true;
	}

	//
	// parse regionImage into VolumeGeometricRegions and SurfaceCollection into SurfaceGeometricRegions
	//
	if (getGeometricRegions()==null || bChanged){
		try {
			setGeometricRegions(GeometrySurfaceUtils.getUpdatedGeometricRegions(this,getRegionImage(),getSurfaceCollection()));
			bChanged = true;
		}catch (java.beans.PropertyVetoException e){
			e.printStackTrace(System.out);
			throw new GeometryException("unexpected exception while generating regions: "+e.getMessage());
		}
	}
	
	//
	// identify classes of surfaces within this geometry with same adjacent subVolumes.
	//
	if (getSurfaceClasses()==null || bChanged){
		setSurfaceClasses(computeSurfaceClasses(getGeometricRegions()));
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