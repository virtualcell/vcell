/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.mapping;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.vcell.util.BeanUtils;
import org.vcell.util.Compare;
import org.vcell.util.Issue;
import org.vcell.util.Issue.IssueCategory;
import org.vcell.util.IssueContext;
import org.vcell.util.IssueContext.ContextType;
import org.vcell.util.Matchable;
import org.vcell.util.PropertyChangeListenerProxyVCell;

import cbit.vcell.geometry.CompartmentSubVolume;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.geometry.GeometryClass;
import cbit.vcell.geometry.GeometryOwner;
import cbit.vcell.geometry.GeometrySpec;
import cbit.vcell.geometry.SubVolume;
import cbit.vcell.geometry.SurfaceClass;
import cbit.vcell.mapping.StructureMapping.StructureMappingParameter;
import cbit.vcell.model.Feature;
import cbit.vcell.model.Membrane;
import cbit.vcell.model.Model;
import cbit.vcell.model.Model.StructureTopology;
import cbit.vcell.model.Structure;
import cbit.vcell.model.VCMODL;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionBindingException;
import cbit.vcell.parser.ExpressionException;
/**
 * GeometryContext handles the mapping of the Structures (Feature,Membrane) to the Geometry
 * (subVolumes).  This should be an observer for Geometry and for Model.
 * 
 */
@SuppressWarnings("serial")
public  class GeometryContext implements Serializable, Matchable, PropertyChangeListener {
	public static final String PROPERTY_STRUCTURE_MAPPINGS = "structureMappings";	
	
	protected transient java.beans.PropertyChangeSupport propertyChange;
	private Model fieldModel = null;
	private Geometry fieldGeometry = null;
	protected transient java.beans.VetoableChangeSupport vetoPropertyChange;
	private StructureMapping[] fieldStructureMappings = new StructureMapping[0];
	private SimulationContext fieldSimulationContext = null;
	
	
	public class UnmappedGeometryClass {
		private GeometryClass geometryClass;
		public UnmappedGeometryClass(GeometryClass geoClass){
			this.geometryClass = geoClass;
		}
		public GeometryClass getGeometryClass(){
			return this.geometryClass;
		}
		public SimulationContext getSimulationContext(){
			return GeometryContext.this.fieldSimulationContext;
		}
	}
	
/**
 * This method was created by a SmartGuide.
 * @param model cbit.vcell.model.Model
 * @param geometry cbit.vcell.geometry.Geometry
 */
GeometryContext(GeometryContext geometryContext, SimulationContext newSimulationContext, Geometry newClonedGeometry) {
	this.fieldGeometry = newClonedGeometry;
	this.fieldModel = geometryContext.getModel();
	this.fieldSimulationContext = newSimulationContext;
	//
	// copy the contents of the structure mappings.
	//
	fieldStructureMappings = new StructureMapping[geometryContext.fieldStructureMappings.length];
	for (int i = 0; i < geometryContext.fieldStructureMappings.length; i++){
		//
		// invoke appropriate copy constructor
		//
		if (geometryContext.fieldStructureMappings[i] instanceof FeatureMapping){
			fieldStructureMappings[i] = new FeatureMapping((FeatureMapping)geometryContext.fieldStructureMappings[i],newSimulationContext,this.fieldGeometry, fieldModel.getUnitSystem());
		}else if (geometryContext.fieldStructureMappings[i] instanceof MembraneMapping){
			fieldStructureMappings[i] = new MembraneMapping((MembraneMapping)geometryContext.fieldStructureMappings[i],newSimulationContext,this.fieldGeometry, fieldModel.getUnitSystem());
		}else {
			throw new RuntimeException("unexpected structureMapping = "+geometryContext.fieldStructureMappings[i]);
		}
	}
	try {
		refreshStructureMappings();
	}catch (Exception e){
		e.printStackTrace(System.out);
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
	PropertyChangeListenerProxyVCell.addProxyListener(getPropertyChange(), listener);
}
/**
 * The addVetoableChangeListener method was generated to support the vetoPropertyChange field.
 */
public synchronized void addVetoableChangeListener(java.beans.VetoableChangeListener listener) {
	getVetoPropertyChange().addVetoableChangeListener(listener);
}
/**
 * This method was created in VisualAge.
 * @param feature cbit.vcell.model.Feature
 * @param geometryClass cbit.vcell.geometry.SubVolume
 * @throws MappingException 
 */
public void assignStructure(Structure structure, GeometryClass geometryClass) throws IllegalMappingException, PropertyVetoException, MappingException {
	
	StructureMapping structureMapping = (StructureMapping)getStructureMapping(structure);
	GeometryClass currentlyMappedSubvolume = structureMapping.getGeometryClass();
	//
	// already mapped here, done.
	//
	if (currentlyMappedSubvolume==geometryClass){
		return;
	}

	//
	// check if deleting mapping
	//
	if (geometryClass==null){
		structureMapping.setGeometryClass(null);
	} else {
	
//		//
//		// if any parent is mapped within another subVolume, mapping not allowed, throw an exception
//		//
//		Feature parent = (feature.getMembrane()!=null)?feature.getMembrane().getOutsideFeature():null;
//		while (parent!=null){
//			FeatureMapping parentFM = (FeatureMapping)getStructureMapping(parent);
//			if (parentFM.getGeometryClass()!=null && parentFM.getGeometryClass()!=geometryClass){
//				throw new IllegalMappingException("parent structure ("+parentFM.getFeature().getName()+") is distributed within another subDomain");
//			}
//			parent = (parent.getMembrane()!=null)?parent.getMembrane().getOutsideFeature():null;
//		}
	
		structureMapping.setGeometryClass(geometryClass);
		//
		// if there are encapsulated features and membranes, then map them too
		// Membrane mappings will be fixed within fixMembraneMappings
		//
//		Structure[] structures = getSimulationContext().getModel().getStructures();
//		for (int i = 0; i < structures.length; i++) {
//			if (structures[i] instanceof Feature){
//				Feature otherFeature = (Feature)structures[i];
//				if (getModel().getStructureTopology().getChildStructures(feature).contains(otherFeature)){
//					getStructureMapping(structures[i]).setGeometryClass(geometryClass);
//				}
//			}
//		}
				}
	refreshStructureMappings();
	setDefaultUnitSizes();
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
public void fireVetoableChange(java.lang.String propertyName, java.lang.Object oldValue, java.lang.Object newValue) throws java.beans.PropertyVetoException {
	getVetoPropertyChange().fireVetoableChange(propertyName, oldValue, newValue);
}
/**
 * Insert the method's description here.
 * Creation date: (11/1/2005 9:48:55 AM)
 * @param issueList java.util.Vector
 */
public void gatherIssues(IssueContext issueContext, List<Issue> issueList) {
	issueContext = issueContext.newChildContext(ContextType.Geometry, getGeometry());
	
	GeometrySpec geometrySpec = getGeometry().getGeometrySpec();
	if (geometrySpec != null) {
		geometrySpec.gatherIssues(issueContext, getGeometry(),issueList);
	}
	
	for (int i = 0; fieldStructureMappings!=null && i < fieldStructureMappings.length; i++){
		fieldStructureMappings[i].gatherIssues(issueContext,issueList);
	}
	for (GeometryClass gc : fieldGeometry.getGeometryClasses()) {
		Structure[] structuresFromGeometryClass = getStructuresFromGeometryClass(gc);
		if (structuresFromGeometryClass == null || structuresFromGeometryClass.length == 0) {
			UnmappedGeometryClass unmappedGeometryClass = new UnmappedGeometryClass(gc);
			Issue issue = new Issue(unmappedGeometryClass, issueContext, IssueCategory.GeometryClassNotMapped, "Subdomain '" + gc.getName() + "' is not mapped to any physiological structure.", Issue.SEVERITY_WARNING);
			issueList.add(issue);
		}
	}
	if(getSimulationContext().isStoch() && getGeometry().getDimension() != 0 && getGeometry().getDimension() != 3)
	{
		Issue issue = new Issue(this, issueContext, IssueCategory.Smoldyn_Geometry_3DWarning, "VCell spatial stochastic models only support 3D geometry.", Issue.SEVERITY_ERROR);
		issueList.add(issue);
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
 * Gets the model property (cbit.vcell.model.Model) value.
 * @return The model property value.
 * @see #setModel
 */
public Model getModel() {
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
public StructureMapping[] getStructureMappings(GeometryClass geometryClass){
	if (geometryClass==null){
		return null;
	}
	StructureMapping structureMappings[] = getStructureMappings();
	ArrayList<StructureMapping> structMappings = new ArrayList<StructureMapping>();
	for (int i=0;i<structureMappings.length;i++){
		if (structureMappings[i].getGeometryClass() == geometryClass){
			structMappings.add(structureMappings[i]);
		}
	}
	return structMappings.toArray(new StructureMapping[structMappings.size()]);
}               
/**
 * Gets the structureMappings property (cbit.vcell.mapping.StructureMapping[]) value.
 * @return The structureMappings property value.
 * @see #setStructureMappings
 */
public StructureMapping[] getStructureMappings() {
	return fieldStructureMappings;
}
/**
 * Gets the structureMappings index property (cbit.vcell.mapping.StructureMapping) value.
 * @return The structureMappings property value.
 * @param index The index value into the property array.
 * @see #setStructureMappings
 */
public StructureMapping getStructureMapping(int index) {
	return getStructureMappings()[index];
}

public Structure[] getStructuresFromGeometryClass(GeometryClass geometryClass) {
	Vector<Structure> list = new Vector<Structure>();
	StructureMapping structureMappings[] = getStructureMappings();

	for (int i=0;i<structureMappings.length;i++){
		StructureMapping sm = structureMappings[i];
		if (sm.getGeometryClass()==geometryClass){
			list.addElement(sm.getStructure());
		}
	}
	return list.toArray(new Structure[list.size()]);
}
/**
 * This method was created by a SmartGuide.
 * @return java.lang.String
 */
public String getVCML() throws Exception {
	StringBuffer buffer = new StringBuffer();
	buffer.append(VCMODL.GeometryContext+" {\n");
//
//	//
//	// write FeatureMappings
//	//
//	for (int i=0;i<fieldStructureMappings.length;i++){
//		StructureMapping sm = fieldStructureMappings[i];
//		if (sm instanceof FeatureMapping){
//			buffer.append(sm.getVCML());
//		}
//	}
//	//
//	// write MembraneMappings
//	//
//	for (int i=0;i<fieldStructureMappings.length;i++){
//		StructureMapping sm = fieldStructureMappings[i];
//		if (sm instanceof MembraneMapping){
//			buffer.append(sm.getVCML());
//		}
//	}
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
			catch (ExpressionException e)
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
	for (int i=0;i<structureMappings.length;i++){
		if(structureMappings[i] instanceof MembraneMapping){
			MembraneMapping membraneMapping = (MembraneMapping)structureMappings[i];
			if(membraneMapping.getSurfaceToVolumeParameter().getExpression() == null){
					return false;
			}
			if(membraneMapping.getVolumeFractionParameter().getExpression() == null){
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
	for (int i=0;i<structureMappings.length;i++){
		if(structureMappings[i] instanceof MembraneMapping){
			MembraneMapping membraneMapping = (MembraneMapping)structureMappings[i];
			if(membraneMapping.getSurfaceToVolumeParameter().getExpression() != null){
					return false;
			}
			if(membraneMapping.getVolumeFractionParameter().getExpression() != null){
					return false;
			}
		}
	}
	return true;
}
/**
 * 
 */
public boolean isAllFeatureMapped()
{
	StructureMapping structureMappings[] = getStructureMappings();
	for (int i=0;i<structureMappings.length;i++)
	{
		if(structureMappings[i] instanceof FeatureMapping)
		{
			FeatureMapping featureMapping = (FeatureMapping)structureMappings[i];
			if (featureMapping.getGeometryClass()==null){
				return false;
			}
		}
	}
	return true;
}

public boolean isAllUnitSizeParameterSetForSpatial()
{
	int dimension = getGeometry().getDimension();
	// check unit size parameters only for spatial applications. 
	if (dimension == 0) {
		throw new RuntimeException("UnitSizeParameter is set only for spatial application.");
	}
	StructureMapping structureMappings[] = getStructureMappings();
	boolean bIsAllUnitSizeParamSet = true;
	for (int i=0;i<structureMappings.length;i++)
	{
		if(structureMappings[i].getUnitSizeParameter().getExpression() == null)	{
			bIsAllUnitSizeParamSet = bIsAllUnitSizeParamSet && false;
		}
	}
	return bIsAllUnitSizeParamSet;
}


/**
 * Insert the method's description here.
 * Creation date: (6/5/00 10:23:55 PM)
 * @param event java.beans.PropertyChangeEvent
 */
public void propertyChange(PropertyChangeEvent event) {
	if (event.getSource() == getGeometry()
			|| event.getSource() == getModel() && event.getPropertyName().equals(Model.PROPERTY_NAME_STRUCTURES)
			|| event.getSource() == getGeometry().getGeometrySpec() && event.getPropertyName() != GeometrySpec.PROPERTY_NAME_SAMPLED_IMAGE
				&& event.getPropertyName() != GeometrySpec.PROPERTY_NAME_THUMBNAIL_IMAGE){
		try {
			refreshStructureMappings();
		}catch (MappingException e){
			e.printStackTrace(System.out);
		}catch (PropertyVetoException e){
			e.printStackTrace(System.out);
		}catch (Exception e){
			e.printStackTrace(System.out);
		}
	}
	if (event.getSource() == this && event.getPropertyName().equals(PROPERTY_STRUCTURE_MAPPINGS)){
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
 * @throws ExpressionBindingException 
 */
public void refreshStructureMappings() throws MappingException, PropertyVetoException {
	//
	// step through all structure mappings
	//
	StructureMapping newStructureMappings[] = (StructureMapping[])fieldStructureMappings.clone();
	
	// fill in geometryClass for those MembraneMappings that were not mapped explicitly (and topology is available)
	for (int j=0;j<newStructureMappings.length;j++){
		StructureMapping structureMapping = newStructureMappings[j];
		if (structureMapping instanceof MembraneMapping && structureMapping.getGeometryClass()==null){
			MembraneMapping membraneMapping = (MembraneMapping)structureMapping;
			Feature insideFeature = getModel().getStructureTopology().getInsideFeature(membraneMapping.getMembrane());
			Feature outsideFeature = getModel().getStructureTopology().getOutsideFeature(membraneMapping.getMembrane());
			if (insideFeature!=null && outsideFeature!=null){
				FeatureMapping insideFeatureMapping = (FeatureMapping)getStructureMapping(insideFeature);
				FeatureMapping outsideFeatureMapping = (FeatureMapping)getStructureMapping(outsideFeature);
			if (insideFeatureMapping.getGeometryClass()==outsideFeatureMapping.getGeometryClass()){
				membraneMapping.setGeometryClass(insideFeatureMapping.getGeometryClass());
			}else if (insideFeatureMapping.getGeometryClass() instanceof SubVolume && outsideFeatureMapping.getGeometryClass() instanceof SubVolume){
				SubVolume insideSubVolume = (SubVolume)insideFeatureMapping.getGeometryClass();
				SubVolume outsideSubVolume = (SubVolume)outsideFeatureMapping.getGeometryClass();
				SurfaceClass surfaceClass = getGeometry().getGeometrySurfaceDescription().getSurfaceClass(insideSubVolume, outsideSubVolume);
				if (surfaceClass!=null){
					membraneMapping.setGeometryClass(surfaceClass);
				}
			}
		}
	}
	}
		
	for (int j=0;j<newStructureMappings.length;j++){
		StructureMapping structureMapping = newStructureMappings[j];
		
		Structure mappedStructure = structureMapping.getStructure();
		//SubVolume mappedSubvolume = structureMapping.getSubVolume();
		Structure newStructure = null;
		GeometryClass newGeometryClass = null;
		boolean structureFound = false;
		boolean geometryClassFound = false;
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
		// match up with geometryClasses defined within geometry
		//
		GeometryClass[] geometryClasses = getGeometry().getGeometryClasses();
		for (int i=0;i<geometryClasses.length;i++){
			if (geometryClasses[i].compareEqual(structureMapping.getGeometryClass())){
				geometryClassFound = true;
				newGeometryClass = geometryClasses[i];
				break;
			}
		}
		//
		// delete this feature mapping if not referenced in both the model and the geometry
		//
		if (!(structureFound && geometryClassFound)){
			newStructureMappings = (StructureMapping[])BeanUtils.removeElement(newStructureMappings,structureMapping);
			j--;
//			//
//			// delete accompanied membrane mapping if exists 
//			//
//			for (int i = 0; i < newStructureMappings.length; i++){
//				if (newStructureMappings[i] instanceof MembraneMapping){
//					MembraneMapping membraneMapping = (MembraneMapping)newStructureMappings[i];
//					if (membraneMapping.getMembrane()==null ||
//						membraneMapping.getMembrane().getInsideFeature() == structureMapping.getStructure() ||
//						membraneMapping.getMembrane().getOutsideFeature() == structureMapping.getStructure()){
//						newStructureMappings = (StructureMapping[])BeanUtils.removeElement(newStructureMappings,membraneMapping);
//						break;
//					}
//				}
//			}
		}else{
			// update references to Structure and SubVolume to correspond to those of Model and Geometry
			structureMapping.setGeometryClass(newGeometryClass);
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
				FeatureMapping fm = new FeatureMapping((Feature)structure,fieldSimulationContext, getModel().getUnitSystem());
				fm.setSimulationContext(this.fieldSimulationContext);
				newStructureMappings = (StructureMapping[])BeanUtils.addElement(newStructureMappings,fm);
				if (getGeometry().getDimension()==0){
					fm.setGeometryClass((CompartmentSubVolume)getGeometry().getGeometrySpec().getSubVolumes()[0]);
				}
			}else if (structure instanceof Membrane){
				MembraneMapping mm = new MembraneMapping((Membrane)structure,fieldSimulationContext, getModel().getUnitSystem());
				mm.setSimulationContext(fieldSimulationContext);
				newStructureMappings = (StructureMapping[])BeanUtils.addElement(newStructureMappings,mm);
				if (getGeometry().getDimension()==0){
					mm.setGeometryClass((CompartmentSubVolume)getGeometry().getGeometrySpec().getSubVolumes()[0]);
				}
			}else{
				throw new MappingException("unsupported Structure Mapping for structure "+structure.getClass().toString());
			}
		}
	}
	if (newStructureMappings != fieldStructureMappings){
		try {
			setStructureMappings(newStructureMappings);
		}catch (Exception e){
			e.printStackTrace(System.out);
			throw new MappingException(e.getMessage());
		}
	}
	
	fixMembraneMappings();
}

private void setDefaultUnitSizes() throws PropertyVetoException {
	if (getGeometry().getDimension() > 0) {
		for (StructureMapping sm : fieldStructureMappings){
			StructureMapping[] sms = getStructureMappings(sm.getGeometryClass());
			StructureMappingParameter unitSizeParameter = sm.getUnitSizeParameter();
			if (unitSizeParameter == null) {
				continue;
			}
			Expression exp = unitSizeParameter.getExpression();
			
			if (sm instanceof MembraneMapping) {
				// Membrane mapped to surface or subdomain, default to 1.0
				if (exp == null) {
					try {
						unitSizeParameter.setExpression(new Expression(1.0));
					} catch (ExpressionBindingException e) {
						e.printStackTrace();
					}
				}
			} else if (sm instanceof FeatureMapping) {
				// Feature mapped to subdomain
				if (sm.getGeometryClass() instanceof SubVolume) {
					if (sms != null && sms.length == 1) {
						try {
							unitSizeParameter.setExpression(new Expression(1.0));
						} catch (ExpressionBindingException e) {
							e.printStackTrace();
						}
					}
				} else {
					if (exp == null) {
						try {
							unitSizeParameter.setExpression(new Expression(1.0));
						} catch (ExpressionBindingException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	}
}

private void fixMembraneMappings() throws PropertyVetoException {
	StructureTopology structTopology = getModel().getStructureTopology();
	for (int j=0;j<fieldStructureMappings.length;j++){
		if (fieldStructureMappings[j] instanceof MembraneMapping){
			MembraneMapping membraneMapping = (MembraneMapping)fieldStructureMappings[j];
			Membrane membrane = membraneMapping.getMembrane();
			Feature insideFeature = structTopology.getInsideFeature(membrane);
			Feature outsideFeature = structTopology.getOutsideFeature(membrane);
			//
			// if the topology is specified (inside/outside features defined), then we can "fix" ... otherwise, it there are no constraints/guidance.
			//
			if (insideFeature!=null && outsideFeature!=null){
				FeatureMapping insideFM = (FeatureMapping)getStructureMapping(insideFeature);
				FeatureMapping outsideFM = (FeatureMapping)getStructureMapping(outsideFeature);
				GeometryClass insideGeometryClass = insideFM.getGeometryClass();
				GeometryClass outsideGeometryClass = outsideFM.getGeometryClass();
				
				//
				// try to map membrane to subdomain automatically if both inside/outside features also mapped
				//
				if (insideFM!=null && insideGeometryClass!=null && outsideFM!=null && outsideGeometryClass!=null){
					// inside/outside both mapped to same domain ... membrane must be there too.
					if (insideGeometryClass==outsideGeometryClass){
						membraneMapping.setGeometryClass(insideGeometryClass);

					// inside/outside mapped to different subvolumes (try to map membrane to adjacent surfaceClass)
					}else if (insideGeometryClass instanceof SubVolume && outsideGeometryClass instanceof SubVolume){
						GeometryClass[] geometryClasses = getGeometry().getGeometryClasses();
						boolean bFound = false;
						for (int i = 0; i < geometryClasses.length; i++) {
							if (geometryClasses[i] instanceof SurfaceClass){
								SurfaceClass surfaceClass = (SurfaceClass)geometryClasses[i];
								if (surfaceClass.isAdjacentTo((SubVolume)insideGeometryClass) && surfaceClass.isAdjacentTo((SubVolume)outsideGeometryClass)){
									membraneMapping.setGeometryClass(surfaceClass);
									bFound=true;
								}
							}
						}
						if (!bFound){
							membraneMapping.setGeometryClass(null);
						}
						
					// inside/outside mapped to different membranes (membrane cannot be mapped ... must be cleared).
					}else if (insideGeometryClass instanceof SurfaceClass && outsideGeometryClass instanceof SurfaceClass){
						membraneMapping.setGeometryClass(null);
						
					// inside mapped to surface and outside mapped to subvolume (if adjacent, map membrane to surface ... else clear).
					}else if (insideGeometryClass instanceof SurfaceClass && outsideGeometryClass instanceof SubVolume){
						SurfaceClass surface = (SurfaceClass)insideGeometryClass;
						SubVolume subVolume = (SubVolume)outsideGeometryClass;
						if (surface.isAdjacentTo(subVolume)) {
							membraneMapping.setGeometryClass(surface);
						}else{
							membraneMapping.setGeometryClass(null);
						}
					
					// inside mapped to subvolume and outside mapped to surface (if adjacent, map membrane to surface ... else clear).
					}else if (insideGeometryClass instanceof SubVolume && outsideGeometryClass instanceof SurfaceClass){
						SurfaceClass surface = (SurfaceClass)outsideGeometryClass;
						SubVolume subVolume = (SubVolume)insideGeometryClass;
						if (surface.isAdjacentTo(subVolume)){
							membraneMapping.setGeometryClass(surface);
						}else{
							membraneMapping.setGeometryClass(null);
						}
					}
				}
			}
		}
	}
}

/**
 * The removePropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void removePropertyChangeListener(java.beans.PropertyChangeListener listener) {
	PropertyChangeListenerProxyVCell.removeProxyListener(getPropertyChange(), listener);
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
void setGeometry(Geometry geometry) throws MappingException {
	
	Geometry oldValue = fieldGeometry;
	fieldGeometry = geometry;
	try {
		refreshStructureMappings();
		refreshDependencies();
	}catch (PropertyVetoException e){
		e.printStackTrace(System.out);
		throw new MappingException(e.getMessage());
	}
	firePropertyChange(GeometryOwner.PROPERTY_NAME_GEOMETRY, oldValue, geometry);
}
/**
 * Sets the model property (cbit.vcell.model.Model) value.
 * @param model The new value for the property.
 * @see #getModel
 */
public void setModel(Model model) throws MappingException {
	Model oldValue = fieldModel;
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
public void setStructureMappings(StructureMapping[] structureMappings) throws java.beans.PropertyVetoException {
	StructureMapping[] oldValue = fieldStructureMappings;
	if (oldValue!=null){
		for (int i = 0; i < oldValue.length; i++){
			oldValue[i].removePropertyChangeListener(this);
		}
	}
	fireVetoableChange(PROPERTY_STRUCTURE_MAPPINGS, oldValue, structureMappings);
	fieldStructureMappings = structureMappings;
	if (fieldStructureMappings!=null){
		for (int i = 0; i < fieldStructureMappings.length; i++){
			fieldStructureMappings[i].addPropertyChangeListener(this);
		}
	}
	firePropertyChange(PROPERTY_STRUCTURE_MAPPINGS, oldValue, structureMappings);
}

public void enforceHierarchicalBoundaryConditions(StructureTopology structureTopology) {
	if (structureTopology != null) {
		for (StructureMapping sm : fieldStructureMappings) {
			//
			// look for top level parent structure which is mapped to the same geometric domain, 
			//
			Structure topStructureForDomain = sm.getStructure();
			while (true) {
				Structure parent = structureTopology.getParentStructure(topStructureForDomain);
				if (parent == null || getStructureMapping(parent).getGeometryClass() != sm.getGeometryClass()) {
					break;
				} else {
					topStructureForDomain = parent;
				}
			}
			//
			// if has a top level parent in same domain (not me), apply boundary conditions types 
			// for parent to this structure mapping.
			//
			if (topStructureForDomain != sm.getStructure()) {
				StructureMapping parentSM = getStructureMapping(topStructureForDomain);
				sm.setBoundaryConditionTypeXm(parentSM.getBoundaryConditionTypeXm());
				sm.setBoundaryConditionTypeXp(parentSM.getBoundaryConditionTypeXp());
				sm.setBoundaryConditionTypeYm(parentSM.getBoundaryConditionTypeYm());
				sm.setBoundaryConditionTypeYp(parentSM.getBoundaryConditionTypeYp());
				sm.setBoundaryConditionTypeZm(parentSM.getBoundaryConditionTypeZm());
				sm.setBoundaryConditionTypeZp(parentSM.getBoundaryConditionTypeZp());
			}	
		}
	}
}

}
