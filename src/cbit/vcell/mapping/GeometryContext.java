package cbit.vcell.mapping;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.util.*;
import java.beans.*;
import java.io.Serializable;
import cbit.vcell.model.VCMODL;
import cbit.vcell.math.VCML;
import cbit.vcell.model.Model;
import cbit.vcell.model.Feature;
import cbit.vcell.model.Membrane;
import cbit.vcell.model.Structure;
import cbit.vcell.parser.Expression;
import cbit.vcell.geometry.*;
import cbit.util.*;
/**
 * GeometryContext handles the mapping of the Structures (Feature,Membrane) to the Geometry
 * (subVolumes).  This should be an observer for Geometry and for Model.
 * 
 */
public  class GeometryContext implements Serializable, Matchable, PropertyChangeListener {

	protected transient java.beans.PropertyChangeSupport propertyChange;
	private cbit.vcell.model.Model fieldModel = null;
	private cbit.vcell.geometry.Geometry fieldGeometry = null;
	protected transient java.beans.VetoableChangeSupport vetoPropertyChange;
	private cbit.vcell.mapping.StructureMapping[] fieldStructureMappings = new StructureMapping[0];
	private SimulationContext fieldSimulationContext = null;
/**
 * This method was created by a SmartGuide.
 * @param model cbit.vcell.model.Model
 * @param geometry cbit.vcell.geometry.Geometry
 */
GeometryContext(GeometryContext geometryContext, SimulationContext simulationContext) {
	this.fieldGeometry = geometryContext.getGeometry();
	this.fieldModel = geometryContext.getModel();
	this.fieldSimulationContext = simulationContext;
	try {
		refreshStructureMappings();
	}catch (Exception e){
		e.printStackTrace(System.out);
	}
	//
	// copy the contents of the structure mappings.
	//
	fieldStructureMappings = new StructureMapping[geometryContext.fieldStructureMappings.length];
	for (int i = 0; i < geometryContext.fieldStructureMappings.length; i++){
		//
		// invoke appropriate copy constructor
		//
		if (geometryContext.fieldStructureMappings[i] instanceof FeatureMapping){
			fieldStructureMappings[i] = new FeatureMapping((FeatureMapping)geometryContext.fieldStructureMappings[i],simulationContext);
		}else if (geometryContext.fieldStructureMappings[i] instanceof MembraneMapping){
			fieldStructureMappings[i] = new MembraneMapping((MembraneMapping)geometryContext.fieldStructureMappings[i],simulationContext);
		}else {
			throw new RuntimeException("unexpected structureMapping = "+geometryContext.fieldStructureMappings[i]);
		}
	}
	refreshDependencies();
}
/**
 * This method was created by a SmartGuide.
 * @param model cbit.vcell.model.Model
 * @param geometry cbit.vcell.geometry.Geometry
 */
GeometryContext (Model model, Geometry geometry, SimulationContext simulationContext) {
	this.fieldGeometry = geometry;
	this.fieldModel = model;
	this.fieldSimulationContext = simulationContext;
	refreshDependencies();
	try {
		refreshStructureMappings();
	}catch (Exception e){
		e.printStackTrace(System.out);
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
 * The addVetoableChangeListener method was generated to support the vetoPropertyChange field.
 */
public synchronized void addVetoableChangeListener(java.lang.String propertyName, java.beans.VetoableChangeListener listener) {
	getVetoPropertyChange().addVetoableChangeListener(propertyName, listener);
}
/**
 * This method was created in VisualAge.
 * @param feature cbit.vcell.model.Feature
 * @param subVolume cbit.vcell.geometry.SubVolume
 */
public void assignFeature(Feature feature, SubVolume subVolume) throws IllegalMappingException, PropertyVetoException {
	
	FeatureMapping featureMapping = (FeatureMapping)getStructureMapping(feature);
	SubVolume currentlyMappedSubvolume = featureMapping.getSubVolume();

	//
	// check if deleting mapping
	//
	if (subVolume==null){
		featureMapping.setSubVolume(null);
		featureMapping.setResolved(false);
		unmapBadMappings();
		return;
	}
	//
	// already mapped here, done.
	//
	if (currentlyMappedSubvolume==subVolume){
		return;
	}
	
	//
	// if any parent is distributed within another subVolume, mapping not allowed, throw an exception
	//
	Feature parent = (feature.getMembrane()!=null)?feature.getMembrane().getOutsideFeature():null;
	while (parent!=null){
		FeatureMapping parentFM = (FeatureMapping)getStructureMapping(parent);
		if (parentFM.getSubVolume()!=null && parentFM.getSubVolume()!=subVolume && !parentFM.getResolved()){
			throw new IllegalMappingException("parent structure ("+parentFM.getFeature().getName()+") is distributed within another subDomain");
		}
		parent = (parent.getMembrane()!=null)?parent.getMembrane().getOutsideFeature():null;
	}

	Feature currResolvedFeature = getResolvedFeature(subVolume);
	if (currResolvedFeature==null){
		//
		// no current mappings, map as resolved
		//
		featureMapping.setSubVolume(subVolume);
		featureMapping.setResolved(true);
		unmapBadMappings();
		return;
	}else{
		parent = (feature.getMembrane()!=null)?feature.getMembrane().getOutsideFeature():null;
		if (parent!=null){
			//
			// if this feature's parent is mapped to this subvolume, then make distributed
			//
			if (((FeatureMapping)getStructureMapping(parent)).getSubVolume()==subVolume){
				//
				// but first check if any child is mapped spatially
				//
				Enumeration childEnum = getChildFeatureMappings(feature);
				while (childEnum.hasMoreElements()) {
					FeatureMapping childFM = (FeatureMapping) childEnum.nextElement();
					if (childFM.getResolved() && childFM.getSubVolume()!=null) {
						throw new IllegalMappingException("child structure ("+childFM.getFeature().getName()+") is spatially mapped, cannot map '"+feature.getName()+"' as distributed");
					}
				}

				//
				// map as Distributed
				//
				featureMapping.setSubVolume(subVolume);
				featureMapping.setResolved(false);
				unmapBadMappings();
				return;
			}
		}
		//
		// map as Resolved, force out old Resolved FeatureMapping
		//
		//     if its a direct child, make Distributed,
		//     if its not a child, Remove
		//
		if (currResolvedFeature.getMembrane()!=null && currResolvedFeature.getMembrane().getOutsideFeature()==feature){
			((FeatureMapping)getStructureMapping(currResolvedFeature)).setResolved(false);
		}else{
			((FeatureMapping)getStructureMapping(currResolvedFeature)).setSubVolume(null);
			((FeatureMapping)getStructureMapping(currResolvedFeature)).setResolved(false);
		}
		featureMapping.setSubVolume(subVolume);
		featureMapping.setResolved(true);
		unmapBadMappings();
	}
}
/**
 * This method was created in VisualAge.
 * @return boolean
 * @param object java.lang.Object
 */
public boolean compareEqual(Matchable object) {
	GeometryContext geoContext = null;
	if (!(object instanceof GeometryContext)){
		return false;
	}else{
		geoContext = (GeometryContext)object;
	}

	if (!Compare.isEqual(fieldModel,geoContext.fieldModel)){
		return false;
	}

	if (!Compare.isEqual(fieldGeometry,geoContext.fieldGeometry)){
		return false;
	}
	
	if (!Compare.isEqual(fieldStructureMappings, geoContext.fieldStructureMappings)){
		return false;
	}
	
	return true;
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
 * Insert the method's description here.
 * Creation date: (11/1/2005 9:48:55 AM)
 * @param issueVector java.util.Vector
 */
public void gatherIssues(Vector issueVector) {
	for (int i = 0; fieldStructureMappings!=null && i < fieldStructureMappings.length; i++){
		fieldStructureMappings[i].gatherIssues(issueVector);
	}
}
/**
 * This method was created in VisualAge.
 * @return java.util.Enumeration
 * @param feature cbit.vcell.model.Feature
 */
public Enumeration getChildFeatureMappings(Feature feature) {
	//
	// preload list with the featureMapping of the parent
	//
	Vector childList = new Vector();
	FeatureMapping parentFM = (FeatureMapping)getStructureMapping(feature);
	childList.addElement(parentFM);

	//
	// go through list of featureMappings and add child if parent is in list
	// repeat until no more additions
	//
	boolean bFoundMore = true;
	while (bFoundMore){
		bFoundMore = false;
		StructureMapping structureMappings[] = getStructureMappings();
		for (int i=0;i<structureMappings.length;i++){
			StructureMapping sm = structureMappings[i];
			if (sm instanceof FeatureMapping){
				FeatureMapping fm = (FeatureMapping)sm;
				//
				// if parent featureMapping is in the list, add to this featureMapping to the list 
				//
				if (!childList.contains(fm)){
					Feature f = fm.getFeature();
					Feature parent = (f.getMembrane() != null) ? f.getMembrane().getOutsideFeature() : null;
					if (parent != null){
						FeatureMapping pfm = (FeatureMapping)getStructureMapping(parent);
						if (childList.contains(pfm)){
							childList.addElement(fm);
							bFoundMore = true;
						}
					}
				}	
			}
		}
	}

	//
	// remove original featureMapping (only want the children)
	//
	childList.removeElement(parentFM);
	
	
	return childList.elements();
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
 * Gets the model property (cbit.vcell.model.Model) value.
 * @return The model property value.
 * @see #setModel
 */
public cbit.vcell.model.Model getModel() {
	return fieldModel;
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
 * This method was created in VisualAge.
 * @return cbit.vcell.model.Feature
 * @param subVolume cbit.vcell.geometry.SubVolume
 */
public Feature getResolvedFeature(SubVolume subVolume) {
	StructureMapping structureMappings[] = getStructureMappings();
	for (int i=0;i<structureMappings.length;i++){
		StructureMapping sm = structureMappings[i];
		if (sm instanceof FeatureMapping && ((FeatureMapping)sm).getSubVolume() == subVolume){
			if (sm instanceof FeatureMapping && ((FeatureMapping)sm).getResolved()){
				return ((FeatureMapping)sm).getFeature();
			}
		}
	}
	return null;
}
/**
 * Insert the method's description here.
 * Creation date: (12/15/2006 11:30:30 AM)
 * @return cbit.vcell.mapping.SimulationContext
 */
public SimulationContext getSimulationContext() {
	return fieldSimulationContext;
}
public StructureMapping getStructureMapping(Structure structure){
	if (structure==null){
		return null;
	}
	StructureMapping structureMappings[] = getStructureMappings();
	for (int i=0;i<structureMappings.length;i++){
		StructureMapping sm = structureMappings[i];
		if (structure.compareEqual(sm.getStructure())){
			return sm;
		}
	}
	return null;
}               
/**
 * Gets the structureMappings property (cbit.vcell.mapping.StructureMapping[]) value.
 * @return The structureMappings property value.
 * @see #setStructureMappings
 */
public cbit.vcell.mapping.StructureMapping[] getStructureMappings() {
	return fieldStructureMappings;
}
/**
 * Gets the structureMappings index property (cbit.vcell.mapping.StructureMapping) value.
 * @return The structureMappings property value.
 * @param index The index value into the property array.
 * @see #setStructureMappings
 */
public StructureMapping getStructureMappings(int index) {
	return getStructureMappings()[index];
}
/**
 * This method was created in VisualAge.
 * @return cbit.vcell.model.Structure[]
 * @param subDomain cbit.vcell.math.SubDomain
 */
public Structure[] getStructures(SubVolume subVolume) {
	Vector list = new Vector();
	StructureMapping structureMappings[] = getStructureMappings();
	//
	// first pass through, get all Features mapped to this SubVolume
	//
	for (int i=0;i<structureMappings.length;i++){
		StructureMapping sm = structureMappings[i];
		if (sm instanceof FeatureMapping && ((FeatureMapping)sm).getSubVolume() == subVolume){
			list.addElement(sm.getStructure());
		}
	}
	//
	// second pass, get all Membranes enclosing those features
	//
	for (int i=0;i<structureMappings.length;i++){
		StructureMapping sm = structureMappings[i];
		if (sm instanceof MembraneMapping){
			Membrane membrane = ((MembraneMapping)sm).getMembrane();
			if (list.contains(membrane.getInsideFeature())){
				list.add(membrane);
			}
		}
	}

	if (list.size()>0){
		Structure structs[] = new Structure[list.size()];
		list.copyInto(structs);
		return structs;
	}else{
		return null;
	}
}
/**
 * This method was created by a SmartGuide.
 * @return java.lang.String
 */
public String getVCML() throws Exception {
	StringBuffer buffer = new StringBuffer();
	buffer.append(VCMODL.GeometryContext+" {\n");

	//
	// write FeatureMappings
	//
	for (int i=0;i<fieldStructureMappings.length;i++){
		StructureMapping sm = fieldStructureMappings[i];
		if (sm instanceof FeatureMapping){
			buffer.append(sm.getVCML());
		}
	}
	//
	// write MembraneMappings
	//
	for (int i=0;i<fieldStructureMappings.length;i++){
		StructureMapping sm = fieldStructureMappings[i];
		if (sm instanceof MembraneMapping){
			buffer.append(sm.getVCML());
		}
	}
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
 * The hasListeners method was generated to support the vetoPropertyChange field.
 */
public synchronized boolean hasListeners(java.lang.String propertyName) {
	return getVetoPropertyChange().hasListeners(propertyName);
}
/**
 * Check if all the sizes are null.
 * Creation date: (12/18/2006 6:15:29 PM)
 * @return boolean
 */
public boolean isAllSizeSpecifiedNull() 
{
	StructureMapping structureMappings[] = getStructureMappings();
	for (int i=0;i<structureMappings.length;i++)
	{
		if(structureMappings[i].getSizeParameter().getExpression() != null) 
			return false;
	}
	return true;
}
/**
 * Check if all the structures' sizes are specified with non zero(greater than zero) values.
 * Creation date: (12/15/2006 1:52:28 PM)
 * @return boolean
 */
public boolean isAllSizeSpecifiedPositive() 
{
	StructureMapping structureMappings[] = getStructureMappings();
	for (int i=0;i<structureMappings.length;i++)
	{
		if(structureMappings[i].getSizeParameter().getExpression() == null) 
			return false;
		else
		{
			try{
				double size = structureMappings[i].getSizeParameter().getExpression().evaluateConstant();
				if (size <=0)
					return false;
			}
			catch (cbit.vcell.parser.ExpressionException e)
			{
				e.printStackTrace();
				throw new RuntimeException("Size of structure "+structureMappings[i].getStructure().getName()+ "cannot be evaluated to a constant.");
			}
		}
	}
	return true;
}
/**
 * check if all the volFractions and surface to volume ratios are specified.
 * Creation date: (12/18/2006 6:19:12 PM)
 * @return boolean
 */
public boolean isAllVolFracAndSurfVolSpecified() 
{
	StructureMapping structureMappings[] = getStructureMappings();
	for (int i=0;i<structureMappings.length;i++)
	{
		if(structureMappings[i] instanceof FeatureMapping)
		{
			FeatureMapping featureMapping = (FeatureMapping)structureMappings[i];
			if (featureMapping.getResolved() == false && featureMapping.getFeature()!=null && featureMapping.getFeature().getMembrane()!=null)
			{
				Membrane membrane = featureMapping.getFeature().getMembrane();
				MembraneMapping membraneMapping = (MembraneMapping)getStructureMapping(membrane);
				if(membraneMapping.getSurfaceToVolumeParameter().getExpression() == null)
					return false;
				if(membraneMapping.getVolumeFractionParameter().getExpression() == null)
					return false;
			}
		}
	}
	return true;
}
/**
 * check if all the volFractions and surface to volume ratios are specified NULL.
 * Creation date: (12/18/2006 6:19:12 PM)
 * @return boolean
 */
public boolean isAllVolFracAndSurfVolSpecifiedNull() 
{
	StructureMapping structureMappings[] = getStructureMappings();
	for (int i=0;i<structureMappings.length;i++)
	{
		if(structureMappings[i] instanceof FeatureMapping)
		{
			FeatureMapping featureMapping = (FeatureMapping)structureMappings[i];
			if (featureMapping.getResolved() == false && featureMapping.getFeature()!=null && featureMapping.getFeature().getMembrane()!=null)
			{
				Membrane membrane = featureMapping.getFeature().getMembrane();
				MembraneMapping membraneMapping = (MembraneMapping)getStructureMapping(membrane);
				if(membraneMapping.getSurfaceToVolumeParameter().getExpression() != null)
					return false;
				if(membraneMapping.getVolumeFractionParameter().getExpression() != null)
					return false;
			}
		}
	}
	return true;
}
/**
 * 
 */
public boolean isAllFeatureResolved()
{
	StructureMapping structureMappings[] = getStructureMappings();
	for (int i=0;i<structureMappings.length;i++)
	{
		if(structureMappings[i] instanceof FeatureMapping)
		{
			FeatureMapping featureMapping = (FeatureMapping)structureMappings[i];
			if (featureMapping.getResolved() == false)
			{
				return false;
			}
		}
	}
	return true;
}
/**
 * This method was created in VisualAge.
 * @return boolean
 * @param feature cbit.vcell.model.Feature
 * @param subVolume cbit.vcell.geometry.SubVolume
 * @exception java.lang.Exception The exception description.
 */
public boolean isDistributedAllowed(Feature feature, SubVolume subVolume) {
	//
	// for compartmental mappings, all features are distributed
	//
	if (getGeometry().getDimension() == 0){
		return true;
	}

	
	Feature parent = (feature.getMembrane() != null) ? feature.getMembrane().getOutsideFeature() : null;
	if (parent != null) {
		FeatureMapping parentFeatureMapping = (FeatureMapping) getStructureMapping(parent);
		//
		// if parent exists and is mapped to this subvolume and not spatially mapped children, then distributed
		//
		if (parentFeatureMapping.getSubVolume() == subVolume) {
			//
			// if any child is mapped spatially, mapping not allowed, throw an exception
			//
			Enumeration childEnum = getChildFeatureMappings(feature);
			while (childEnum.hasMoreElements()) {
				FeatureMapping childFM = (FeatureMapping) childEnum.nextElement();
				if (childFM.getResolved() && childFM.getSubVolume()!=null) {
					return false;
				}
			}
			return true;
		} else {
			return false;
		}
	}
	return true;
}
/**
 * This method was created in VisualAge.
 * @return boolean
 * @param feature cbit.vcell.model.Feature
 * @param subVolume cbit.vcell.geometry.SubVolume
 * @exception java.lang.Exception The exception description.
 */
public boolean isResolvedAllowed(Feature feature, SubVolume subVolume) {
	if (getGeometry().getDimension() == 0){
		return false;
	}
	if (subVolume==null){
		return false;
	}
	
	Feature parent = (feature.getMembrane() != null) ? feature.getMembrane().getOutsideFeature() : null;
	if (parent != null) {
		FeatureMapping parentFeatureMapping = (FeatureMapping) getStructureMapping(parent);
		//
		// if parent exists and is mapped to this subvolume, then distributed
		//
		if (parentFeatureMapping.getSubVolume() == subVolume) {
			return false;
		} else {
			//
			// if any parent is distributed within another subVolume, mapping not allowed
			//
			parent = (feature.getMembrane() != null) ? feature.getMembrane().getOutsideFeature() : null;
			while (parent != null) {
				FeatureMapping parentFM = (FeatureMapping) getStructureMapping(parent);
				if (parentFM.getSubVolume()!=null && parentFM.getSubVolume() != subVolume && !parentFM.getResolved()) {
					return false;
				}
				parent = (parent.getMembrane() != null) ? parent.getMembrane().getOutsideFeature() : null;
			}
		}
	}
	return true;
}
/**
 * Insert the method's description here.
 * Creation date: (6/5/00 10:23:55 PM)
 * @param event java.beans.PropertyChangeEvent
 */
public void propertyChange(PropertyChangeEvent event) {
	if (event.getSource() == getGeometry() || event.getSource() == getGeometry().getGeometrySpec()){
		try {
			refreshStructureMappings();
		}catch (MappingException e){
			e.printStackTrace(System.out);
		}catch (PropertyVetoException e){
			e.printStackTrace(System.out);
		}
	}
	if (event.getSource() == getModel() && event.getPropertyName().equals("structures")){
		try {
			refreshStructureMappings();
		}catch (MappingException e){
			e.printStackTrace(System.out);
		}catch (PropertyVetoException e){
			e.printStackTrace(System.out);
		}
	}
	if (event.getSource() == this && event.getPropertyName().equals("structureMappings")){
		try {
			fieldSimulationContext.getReactionContext().refreshSpeciesContextSpecBoundaryUnits(getStructureMappings());
		}catch (Exception e){
			e.printStackTrace(System.out);
		}
	}
	if (event.getSource() instanceof StructureMapping){
		if (event.getPropertyName().startsWith("boundaryCondition")){
			try {
				fieldSimulationContext.getReactionContext().refreshSpeciesContextSpecBoundaryUnits(getStructureMappings());
			}catch (Exception e){
				e.printStackTrace(System.out);
			}
		}
	}
}
/**
 * This method was created in VisualAge.
 */
public void refreshDependencies() {
	fieldModel.removePropertyChangeListener(this);
	fieldModel.addPropertyChangeListener(this);
	fieldGeometry.removePropertyChangeListener(this);
	fieldGeometry.addPropertyChangeListener(this);
	fieldGeometry.getGeometrySpec().removePropertyChangeListener(this);
	fieldGeometry.getGeometrySpec().addPropertyChangeListener(this);
	fieldGeometry.getGeometrySpec().refreshDependencies();
	for (int i = 0; i < fieldStructureMappings.length; i++){
		fieldStructureMappings[i].removePropertyChangeListener(this);
		fieldStructureMappings[i].addPropertyChangeListener(this);
		fieldStructureMappings[i].setSimulationContext(fieldSimulationContext);
		fieldStructureMappings[i].refreshDependencies();
	}
//	fieldModel.refreshDependencies();
}
/**
 * This method was created by a SmartGuide.
 */
private void refreshStructureMappings() throws MappingException, PropertyVetoException {
	Enumeration enum_mappings;
	Enumeration enum_structures;
	Enumeration enum_subvolumes;
	//
	// step through all structure mappings
	//
	StructureMapping newStructureMappings[] = (StructureMapping[])fieldStructureMappings.clone();
	for (int j=0;j<newStructureMappings.length;j++){
		StructureMapping structureMapping = newStructureMappings[j];
		
		Structure mappedStructure = structureMapping.getStructure();
		//SubVolume mappedSubvolume = structureMapping.getSubVolume();
		Structure newStructure = null;
		SubVolume newSubvolume = null;
		boolean structureFound = false;
		boolean subvolumeFound = false;
		//
		// match up with structures defined within model
		//
		Structure modelStructures[] = getModel().getStructures();
		for (int i=0;i<modelStructures.length;i++){
			Structure modelStructure = modelStructures[i];
			if (modelStructure.compareEqual(mappedStructure)){
				structureFound = true;
				newStructure = modelStructure;
				break;
			}
		}
		//
		// match up with subvolumes defined within geometry
		//
		if (structureMapping instanceof FeatureMapping){
			FeatureMapping featureMapping = (FeatureMapping)structureMapping;
			SubVolume geometrySubVolumes[] = getGeometry().getGeometrySpec().getSubVolumes();
			for (int i=0;i<geometrySubVolumes.length;i++){
				SubVolume geometrySubVolume = (SubVolume)geometrySubVolumes[i];
				if (geometrySubVolume.compareEqual(featureMapping.getSubVolume())){
					subvolumeFound = true;
					newSubvolume = geometrySubVolume;
					break;
				}
			}
			//
			// delete this feature mapping if not referenced in both the model and the geometry
			//
			if (!(structureFound && subvolumeFound)){
				newStructureMappings = (StructureMapping[])BeanUtils.removeElement(newStructureMappings,structureMapping);
				j--;
				//
				// delete accompanied membrane mapping if exists 
				//
				for (int i = 0; i < newStructureMappings.length; i++){
					if (newStructureMappings[i] instanceof MembraneMapping){
						MembraneMapping membraneMapping = (MembraneMapping)newStructureMappings[i];
						if (membraneMapping.getMembrane()==null ||
							membraneMapping.getMembrane().getInsideFeature() == structureMapping.getStructure() ||
							membraneMapping.getMembrane().getOutsideFeature() == structureMapping.getStructure()){
							newStructureMappings = (StructureMapping[])BeanUtils.removeElement(newStructureMappings,membraneMapping);
							break;
						}
					}
				}
			}else{
				// update references to Structure and SubVolume to correspond to those of Model and Geometry
				((FeatureMapping)structureMapping).setSubVolume(newSubvolume);
			}
		}
		if(structureFound){
			structureMapping.setStructure(newStructure);
		}
	}
	//
	// add default mappings for any new structures
	//
	Structure structures[] = getModel().getStructures();
	for (int i=0;i<structures.length;i++){
		Structure structure = structures[i];
		StructureMapping sm = null;
		for (int j=0;j<newStructureMappings.length;j++){
			if (newStructureMappings[j].getStructure().compareEqual(structure)){
				sm = newStructureMappings[j];
			}
		}
		if (sm == null){
			if (structure instanceof Feature){
				FeatureMapping fm = new FeatureMapping((Feature)structure,fieldSimulationContext);
				fm.setSimulationContext(this.fieldSimulationContext);
				newStructureMappings = (StructureMapping[])BeanUtils.addElement(newStructureMappings,fm);
				if (getGeometry().getDimension()==0){
					fm.setSubVolume((CompartmentSubVolume)getGeometry().getGeometrySpec().getSubVolumes()[0]);
				}
			}else if (structure instanceof Membrane){
				MembraneMapping mm = new MembraneMapping((Membrane)structure,fieldSimulationContext);
				mm.setSimulationContext(fieldSimulationContext);
				newStructureMappings = (StructureMapping[])BeanUtils.addElement(newStructureMappings,mm);
			}else{
				throw new MappingException("unsupported Structure Mapping for structure "+structure.getClass().toString());
			}
		}
	}
	if (newStructureMappings != fieldStructureMappings){
		try {
			setStructureMappings(newStructureMappings);
		}catch (PropertyVetoException e){
			e.printStackTrace(System.out);
			throw new MappingException(e.getMessage());
		}
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
 * Sets the geometry property (cbit.vcell.geometry.Geometry) value.
 * @param geometry The new value for the property.
 * @see #getGeometry
 */
public void setGeometry(cbit.vcell.geometry.Geometry geometry) throws MappingException {
	
	Geometry oldValue = fieldGeometry;
	fieldGeometry = geometry;
	try {
		refreshStructureMappings();
		refreshDependencies();
	}catch (PropertyVetoException e){
		e.printStackTrace(System.out);
		throw new MappingException(e.getMessage());
	}
	firePropertyChange("geometry", oldValue, geometry);
}
/**
 * Sets the model property (cbit.vcell.model.Model) value.
 * @param model The new value for the property.
 * @see #getModel
 */
public void setModel(cbit.vcell.model.Model model) throws MappingException {
	cbit.vcell.model.Model oldValue = fieldModel;
	fieldModel = model;
	try {
		refreshStructureMappings();
		refreshDependencies();
	}catch (PropertyVetoException e){
		e.printStackTrace(System.out);
		throw new MappingException(e.getMessage());
	}
	firePropertyChange("model", oldValue, model);
}
/**
 * Sets the structureMappings property (cbit.vcell.mapping.StructureMapping[]) value.
 * @param structureMappings The new value for the property.
 * @exception java.beans.PropertyVetoException The exception description.
 * @see #getStructureMappings
 */
public void setStructureMappings(cbit.vcell.mapping.StructureMapping[] structureMappings) throws java.beans.PropertyVetoException {
	cbit.vcell.mapping.StructureMapping[] oldValue = fieldStructureMappings;
	if (oldValue!=null){
		for (int i = 0; i < oldValue.length; i++){
			oldValue[i].removePropertyChangeListener(this);
		}
	}
	fireVetoableChange("structureMappings", oldValue, structureMappings);
	fieldStructureMappings = structureMappings;
	if (fieldStructureMappings!=null){
		for (int i = 0; i < fieldStructureMappings.length; i++){
			fieldStructureMappings[i].addPropertyChangeListener(this);
		}
	}
	firePropertyChange("structureMappings", oldValue, structureMappings);
}
/**
 * This method was created in VisualAge.
 */
private void unmapBadMappings() throws PropertyVetoException {
	//
	// kill mapping of any distributed children of feature
	//
	StructureMapping newStructureMappings[] = fieldStructureMappings;
	for (int j=0;j<newStructureMappings.length;j++){
		StructureMapping sm = newStructureMappings[j];
		if (sm instanceof FeatureMapping){
			FeatureMapping fm = (FeatureMapping)sm;
			Feature f = (Feature)fm.getStructure();
			if (!fm.getResolved()){
				SubVolume subVolume = fm.getSubVolume();
				if (subVolume==null){
					continue;
				}
				//
				// make sure parent feature is either distributed in the same 
				//
				Feature fParent = (f.getMembrane()!=null)?f.getMembrane().getOutsideFeature():null;
				if (fParent==null){
					if (getGeometry().getDimension()>0){
						fm.setSubVolume(null);
						fm.setResolved(false);
						unmapBadMappings();
					}
				}else{
					FeatureMapping fmParent = (FeatureMapping)getStructureMapping(fParent);
					if (fmParent.getSubVolume()!=subVolume){
						fm.setSubVolume(null);
						fm.setResolved(false);
						unmapBadMappings();
					}
				}
			}
		}
	}
}
}
