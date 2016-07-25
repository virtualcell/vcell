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
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sourceforge.interval.ia_math.RealInterval;

import org.vcell.util.Compare;
import org.vcell.util.Issue;
import org.vcell.util.Issue.IssueCategory;
import org.vcell.util.Issue.IssueSource;
import org.vcell.util.IssueContext;
import org.vcell.util.Matchable;
import org.vcell.util.PropertyChangeListenerProxyVCell;
import org.vcell.util.TokenMangler;

import cbit.vcell.Historical;
import cbit.vcell.geometry.CompartmentSubVolume;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.geometry.GeometryClass;
import cbit.vcell.geometry.SubVolume;
import cbit.vcell.geometry.SurfaceClass;
import cbit.vcell.math.BoundaryConditionType;
import cbit.vcell.model.BioNameScope;
import cbit.vcell.model.ExpressionContainer;
import cbit.vcell.model.Feature;
import cbit.vcell.model.Membrane;
import cbit.vcell.model.ModelUnitSystem;
import cbit.vcell.model.Parameter;
import cbit.vcell.model.SimpleBoundsIssue;
import cbit.vcell.model.Structure;
import cbit.vcell.model.VCMODL;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionBindingException;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.NameScope;
import cbit.vcell.parser.ScopedSymbolTable;
import cbit.vcell.parser.SymbolTableEntry;
import cbit.vcell.units.VCUnitDefinition;

@SuppressWarnings("serial")
public abstract class StructureMapping implements Matchable, ScopedSymbolTable, java.io.Serializable, IssueSource {
	
	public class StructureMappingNameScope extends BioNameScope {
		private final NameScope children[] = new NameScope[0]; // always empty
		public StructureMappingNameScope(){
			super();
		}
		public NameScope[] getChildren() {
			//
			// no children to return
			//
			return children;
		}
		public String getName() {
			return TokenMangler.fixTokenStrict(StructureMapping.this.getStructure().getName()+"_mapping");
		}
		public NameScope getParent() {
			if (StructureMapping.this.simulationContext != null){
				return StructureMapping.this.simulationContext.getNameScope();
			}else{
				return null;
			}
		}
		public ScopedSymbolTable getScopedSymbolTable() {
			return StructureMapping.this;
		}
		
		public StructureMapping getStructureMapping(){
			return StructureMapping.this;
		}
		
		@Override
		public String getPathDescription() {
			if (simulationContext != null){
				return "App("+simulationContext.getName()+") / " + getStructure().getTypeName() + "(" + getStructure().getName() + ")";
			}
			return null;
		}
		@Override
		public NamescopeType getNamescopeType() {
			return NamescopeType.structureMappingType;
		}
	}
	
	public class StructureMappingParameter extends Parameter implements ExpressionContainer, IssueSource {

		private int fieldParameterRole = -1;
		private String fieldParameterName = null;
		private Expression fieldParameterExpression = null;
		private VCUnitDefinition fieldVCUnitDefinition = null;


		public StructureMappingParameter(String parmName, Expression argExpression, int argRole, VCUnitDefinition argVCUnitDefinition) {
			super();
			fieldParameterName = parmName;
			fieldParameterExpression = argExpression;
			if (argRole >= 0 && argRole < NUM_ROLES){
				this.fieldParameterRole = argRole;
			}else{
				throw new IllegalArgumentException("parameter 'role' = "+argRole+" is out of range");
			}
			fieldVCUnitDefinition = argVCUnitDefinition;
		}

		public StructureMappingParameter(StructureMapping.StructureMappingParameter structureMappingParameter) {
			this(structureMappingParameter.getName(),structureMappingParameter.getExpression() == null ? null : new Expression(structureMappingParameter.getExpression()),structureMappingParameter.getRole(),structureMappingParameter.getUnitDefinition());			
		}

		public boolean compareEqual(Matchable obj) {
			if (!(obj instanceof StructureMappingParameter)){
				return false;
			}
			StructureMappingParameter smp = (StructureMappingParameter)obj;
			if (!super.compareEqual0(smp)){
				return false;
			}
			if (fieldParameterRole != smp.fieldParameterRole){
				return false;
			}
			
			return true;
		}

//		public static final String Descriptions[] = {
//			"surface/enclosed volume",
//			"enclosed volume/parent volume",
//			"specific capacitance",
//			"initial voltage",
//			"absolute size (volume/area)",
//			"volume/unit volume",
//			"volume/unit area",
//			"area/unit area",
//			"area/unit volume"
//		};
		@Override
		public String getDescription() {
			switch (fieldParameterRole){
			case ROLE_AreaPerUnitArea:{
				if (getStructure() instanceof Membrane && geometryClass instanceof SurfaceClass){
					return "Area Ratio (\""+structure.getName()+"\" : \""+geometryClass.getName()+"\")";
				}
				break;
			}
			case ROLE_AreaPerUnitVolume:{
				if (getStructure() instanceof Membrane && geometryClass instanceof SubVolume){
					return "Area Ratio (\""+structure.getName()+"\" : \""+geometryClass.getName()+"\")";
				}
				break;
			}
			case ROLE_InitialVoltage:{
				return "initial voltage";
			}
			case ROLE_Size:{
				return "size";
			}
			case ROLE_SpecificCapacitance:{
				return "specific capacitance";
			}
			case ROLE_SurfaceToVolumeRatio:{
				return "surface to volume ratio (including children)";
			}
			case ROLE_VolumeFraction:{
				return "volume fraction (including children)";
			}
			case ROLE_VolumePerUnitArea:{
				if (getStructure() instanceof Feature && geometryClass instanceof SurfaceClass){
					return "Volume Ratio (\""+structure.getName()+"\" : \""+geometryClass.getName()+"\")";
				}
				break;
			}
			case ROLE_VolumePerUnitVolume:{
				if (getStructure() instanceof Feature && geometryClass instanceof SubVolume){
					return "Volume Ratio (\""+structure.getName()+"\" : \""+geometryClass.getName()+"\")";
				}
				break;
			}
			default:{
				break;
			}
			}
			return "??";
		}

		public boolean isExpressionEditable(){
			return true;
		}

		public boolean isUnitEditable(){
			return false;
		}

		public boolean isNameEditable(){
			return false;
		}

		public NameScope getNameScope(){
			return StructureMapping.this.getNameScope();
		}
		
		public void setName(java.lang.String name) throws java.beans.PropertyVetoException {
			String oldValue = fieldParameterName;
			super.fireVetoableChange("name", oldValue, name);
			fieldParameterName = name;
			super.firePropertyChange("name", oldValue, name);
		}

		public void setExpression(Expression expression) throws ExpressionBindingException {
			if (expression!=null){
				expression = new Expression(expression);
				expression.bindExpression(StructureMapping.this);
			}
			Expression oldValue = fieldParameterExpression;
			fieldParameterExpression = expression;
			super.firePropertyChange("expression", oldValue, expression);
		}

		public double getConstantValue() throws ExpressionException {
			return fieldParameterExpression.evaluateConstant();
		}

		public Expression getExpression() {
			 return fieldParameterExpression;
		}

		public VCUnitDefinition getUnitDefinition(){
			return fieldVCUnitDefinition;
		}
		

		public void setUnitDefinition(VCUnitDefinition unit) throws PropertyVetoException {
			throw new RuntimeException("unit is not editable");
		}

		public String getName() {
			return fieldParameterName;
		}

		public int getIndex() {
			return -1;
		}

		public int getRole() {
			return fieldParameterRole;
		}
		
		public Structure getStructure() {
			return StructureMapping.this.getStructure();
		}
	}
	
	private Structure structure = null;
	private GeometryClass geometryClass = null;
	private StructureMappingNameScope nameScope = new StructureMappingNameScope();
	protected SimulationContext simulationContext = null; // for determining NameScope parent only
	private BoundaryConditionType boundaryConditionTypes[] = new BoundaryConditionType[6];
	private boolean boundaryConditionValid[] = new boolean[6];
	
	// model unit system for structureMappingParameters
	protected ModelUnitSystem modelUnitSystem = null;

	protected transient java.beans.PropertyChangeSupport propertyChange;
	protected transient java.beans.VetoableChangeSupport vetoPropertyChange;
	public static final int ROLE_SurfaceToVolumeRatio	= 0;
	public static final int ROLE_VolumeFraction			= 1;
	public static final int ROLE_SpecificCapacitance	= 2;
	public static final int ROLE_InitialVoltage			= 3;
	public static final int ROLE_Size					= 4;
	public static final int ROLE_VolumePerUnitVolume	= 5;
	public static final int ROLE_VolumePerUnitArea		= 6;
	public static final int ROLE_AreaPerUnitArea		= 7;
	public static final int ROLE_AreaPerUnitVolume		= 8;
	public static final int NUM_ROLES		= 9;	//surface area for membrane or volume for feature
	public static final String RoleTags[] = {
		VCMODL.SurfaceToVolume,
		VCMODL.VolumeFraction,
		VCMODL.SpecificCapacitance,
		VCMODL.InitialVoltage,
		VCMODL.StructureSize,
		VCMODL.VolumePerUnitVolume,
		VCMODL.VolumePerUnitArea,
		VCMODL.AreaPerUnitArea,
		VCMODL.AreaPerUnitVolume
	};
	public static final String DefaultNames[] = {
		"SurfToVolRatio",
		"VolFraction",
		"SpecCapacitance",
		"InitialVoltage",
		"Size",
		"VolPerUnitVol",
		"VolPerUnitArea",
		"AreaPerUnitArea",
		"AreaPerUnitVol"
	};
	private static final RealInterval[] parameterBounds = {
		new RealInterval(1.0E-3, 1.0E4),	// s/v ratio
		new RealInterval(1.0E-3, 0.999),							// volFract
		new RealInterval(0.0, Double.POSITIVE_INFINITY),	// Capacitance
		new RealInterval(-120, 60),	// init voltage
		new RealInterval(0.0, Double.POSITIVE_INFINITY),		// size
		new RealInterval(0.0, 1.0),								// volume/volume
		new RealInterval(0.0, 100),								// volume/area
		new RealInterval(0.0, 1.0),								// area/area
		new RealInterval(0.0, 100)								// area/volume
	};
	private StructureMapping.StructureMappingParameter[] fieldParameters = null;

protected StructureMapping(StructureMapping structureMapping, SimulationContext argSimulationContext,Geometry newGeometry, ModelUnitSystem argModelUnitSystem) {
	if (argSimulationContext == null) {
		throw new IllegalArgumentException("SimulationContext is null");
	}	
	this.structure = structureMapping.getStructure();
	this.simulationContext = argSimulationContext;
	fieldParameters = new StructureMapping.StructureMappingParameter[structureMapping.getParameters().length];
	for (int i = 0; i < fieldParameters.length; i++){
		fieldParameters[i] = new StructureMappingParameter((StructureMappingParameter)structureMapping.getParameters(i));
	}
	for (int i=0;i<6;i++){
		boundaryConditionTypes[i]=structureMapping.boundaryConditionTypes[i];
		boundaryConditionValid[i]=structureMapping.boundaryConditionValid[i];
	}
	if(structureMapping.getGeometryClass()!= null){
		String geomClassName = structureMapping.getGeometryClass().getName();
		this.geometryClass = newGeometry.getGeometryClass(geomClassName);
	}
	
	// unit system
	modelUnitSystem = argModelUnitSystem;
}      


protected StructureMapping(Structure structure, SimulationContext argSimulationContext, ModelUnitSystem argModelUnitSystem) {
	if (argSimulationContext == null) {
		throw new IllegalArgumentException("SimulationContext is null");
	}
	this.structure = structure;
	this.simulationContext = argSimulationContext;
	for (int i=0;i<6;i++){
		boundaryConditionTypes[i]=BoundaryConditionType.getNEUMANN();
		boundaryConditionValid[i]=false;
	}
	
	// unit system
	modelUnitSystem = argModelUnitSystem;

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
 * @return boolean
 */
public abstract boolean compareEqual(Matchable object);


/**
 * This method was created in VisualAge.
 * @return boolean
 * @param obj java.lang.Object
 */
protected boolean compareEqual0(StructureMapping sm) {

	if (!Compare.isEqual(structure,sm.structure)){
		return false;
	}
	if (!Compare.isEqual(fieldParameters,sm.fieldParameters)){
		return false;
	}
	if (!Compare.isEqual(geometryClass,sm.geometryClass)){
		return false;
	}
	for (int i=0;i<boundaryConditionTypes.length;i++){
		if (!boundaryConditionTypes[i].compareEqual(sm.boundaryConditionTypes[i])){
			return false;
		}
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
public void fireVetoableChange(String propertyName, Object oldValue, Object newValue) throws java.beans.PropertyVetoException {
	getVetoPropertyChange().fireVetoableChange(propertyName, oldValue, newValue);
}

/**
 * Insert the method's description here.
 * Creation date: (11/1/2005 9:57:23 AM)
 * @param issueVector java.util.Vector
 */
public void gatherIssues(IssueContext issueContext, List<Issue> issueVector) {
	// size parameter must be set to non zero value for new ode, and all stoch simulations.
	if (simulationContext != null) {
		Parameter sizeParam = null;
		if (simulationContext.getGeometry().getDimension() == 0) {
			sizeParam = getSizeParameter();
		} else {
			sizeParam = getUnitSizeParameter();
		}
		if (sizeParam != null) {
			if (sizeParam.getExpression() == null) {
				if (!simulationContext.getGeometryContext().isAllVolFracAndSurfVolSpecified()) {			
					issueVector.add(new Issue(this, issueContext, IssueCategory.StructureMappingSizeParameterNotSet, "Size parameter is not set.",Issue.SEVERITY_ERROR));
				}					
			} else {
				try{
					double val = sizeParam.getExpression().evaluateConstant();
					if (val <= 0) {
						issueVector.add(new Issue(this, issueContext, IssueCategory.StructureMappingSizeParameterNotPositive, "Size parameter is not positive.",Issue.SEVERITY_ERROR));
					}
				} catch (ExpressionException e) {
					e.printStackTrace();
					issueVector.add(new Issue(this, issueContext, IssueCategory.StructureMappingSizeParameterNotConstant, "Size parameter is not a constant.",Issue.SEVERITY_ERROR));
				}			
			}
		}
	}
	//
	// add constraints (simpleBounds) for predefined parameters
	//
	for (int i = 0; fieldParameters!=null && i < fieldParameters.length; i++){
		RealInterval simpleBounds = parameterBounds[fieldParameters[i].getRole()];
		if (simpleBounds!=null){
			String parmName = fieldParameters[i].getNameScope().getName()+"."+fieldParameters[i].getName();
			issueVector.add(new SimpleBoundsIssue(fieldParameters[i], issueContext, simpleBounds, "parameter "+parmName+": must be within "+simpleBounds.toString()));
		}
	}
	if (geometryClass == null) {
		issueVector.add(new Issue(this, issueContext, IssueCategory.StructureNotMapped, getStructure().getTypeName() + " " + getStructure().getName() + " is not mapped to a geometry subdomain.", Issue.SEVERITY_WARNING));
	}
	
	if(simulationContext.getGeometryContext().getGeometry().getDimension() > 0) {
		detectMappingConflictIssues(issueContext, issueVector, this);
	}
}

private static void detectMappingConflictIssues(IssueContext issueContext, List<Issue> issueVector, StructureMapping ours) {
	
	final String ourStructureName = ours.getStructure().getName();
	final String ourStructureTypeName = ours.getStructure().getTypeName();
	final String ourSubdomainName = ours.getGeometryClass().getName();
	SimulationContext sc = ours.simulationContext;
	String incompatibilityList = "";		// list of incompatible structures mapped to the same subdomain, correctly built but not used
	boolean incompatibilityFound = false;
	
	StructureMapping[] structureMappings = sc.getGeometryContext().getStructureMappings();
	for(StructureMapping theirs : structureMappings) {
		if(ours == theirs) {
			continue;		// don't compare ours with itself
		}
		
		final String theirSubdomainName = theirs.getGeometryClass().getName();
		if(!ourSubdomainName.equals(theirSubdomainName)) {
			continue;		// we don't care if mapped to another subdomain
		}
		
		String incompatibleStructure = findMappingBoundaryConflict(ours, theirs);
		if(incompatibleStructure != null) {
			if(incompatibilityFound) {
				incompatibilityList += ", ";
			}
			incompatibilityList += incompatibleStructure;
			incompatibilityFound = true;
		}	
	}
	
	if(!incompatibilityList.isEmpty()) {
		// TODO: modify StructureMappingTableRenderer to customize the way issues are being rendered
		String message = "Subdomain '" +  ourSubdomainName + "' boundary conditions (e.g. for X-, X+...) must match.";
		String tooltip = "Can't mix Flux and Value on the same column (e.g. X-, X+...) for multiple Structures mapped to the same Subdomain.";

		issueVector.add(new Issue(ours, issueContext, IssueCategory.SubVolumeVerificationError, message, tooltip, Issue.Severity.ERROR));
	}
}
private static String findMappingBoundaryConflict(StructureMapping ours, StructureMapping theirs) {
	
	SimulationContext sc = ours.simulationContext;
	int dimension = sc.getGeometryContext().getGeometry().getDimension();
	for (int i=0; i<dimension*2; i++){
		if(!ours.boundaryConditionTypes[i].compareEqual(theirs.boundaryConditionTypes[i])) {
			return theirs.getStructure().getName();
		}
	}
	return null;
	
//	SimulationContext sc = ours.simulationContext;
//	int dimension = sc.getGeometryContext().getGeometry().getDimension();
//	for (int i=0; i<dimension*2; i++){
//		if(!ours.boundaryConditionTypes[i].compareEqual(theirs.boundaryConditionTypes[i])) {
//			final String ourStructureName = ours.getStructure().getName();
//			final String theirStructureName = theirs.getStructure().getName();
//			if(ourStructureName.compareTo(theirStructureName) > 0) {
//				return theirStructureName;	// the first is never wrong
//			}
//		}
//	}
//	return null;
}

/**
 * This method was created by a SmartGuide.
 * @return java.lang.String
 */
public cbit.vcell.math.BoundaryConditionType getBoundaryCondition(BoundaryLocation boundaryLocation) {
	return boundaryConditionTypes[boundaryLocation.getNum()];
}


/**
 * This method was created by a SmartGuide.
 * @return java.lang.String
 */
public cbit.vcell.math.BoundaryConditionType getBoundaryConditionTypeXm() {
	return getBoundaryCondition(BoundaryLocation.getXM());
}


/**
 * This method was created by a SmartGuide.
 * @return java.lang.String
 */
public cbit.vcell.math.BoundaryConditionType getBoundaryConditionTypeXp() {
	return getBoundaryCondition(BoundaryLocation.getXP());
}


/**
 * This method was created by a SmartGuide.
 * @return java.lang.String
 */
public cbit.vcell.math.BoundaryConditionType getBoundaryConditionTypeYm() {
	return getBoundaryCondition(BoundaryLocation.getYM());
}


/**
 * This method was created by a SmartGuide.
 * @return java.lang.String
 */
public cbit.vcell.math.BoundaryConditionType getBoundaryConditionTypeYp() {
	return getBoundaryCondition(BoundaryLocation.getYP());
}


/**
 * This method was created by a SmartGuide.
 * @return java.lang.String
 */
public cbit.vcell.math.BoundaryConditionType getBoundaryConditionTypeZm() {
	return getBoundaryCondition(BoundaryLocation.getZM());
}


/**
 * This method was created by a SmartGuide.
 * @return java.lang.String
 */
public cbit.vcell.math.BoundaryConditionType getBoundaryConditionTypeZp() {
	return getBoundaryCondition(BoundaryLocation.getZP());
}

/**
 * getEntry method comment.
 */
public SymbolTableEntry getEntry(java.lang.String identifierString) {
	
	SymbolTableEntry ste = getLocalEntry(identifierString);
	if (ste != null){
		return ste;
	}
			
	return getNameScope().getExternalEntry(identifierString,this);
}


/**
 * Insert the method's description here.
 * Creation date: (12/8/2003 12:47:06 PM)
 * @return SymbolTableEntry
 * @param identifier java.lang.String
 */
public SymbolTableEntry getLocalEntry(java.lang.String identifier) {
	
	Parameter parameter = getParameter(identifier);
	
	return parameter;
}


/**
 * Insert the method's description here.
 * Creation date: (12/8/2003 12:47:06 PM)
 * @return cbit.vcell.parser.NameScope
 */
public NameScope getNameScope() {
	return nameScope;
}


/**
 * Gets the mappingParameters index property (cbit.vcell.mapping.MappingParameter) value.
 * @return The mappingParameters property value.
 * @param index The index value into the property array.
 * @see #setMappingParameters
 */
public StructureMapping.StructureMappingParameter getParameter(String argName) {
	for (int i = 0; i < fieldParameters.length; i++){
		if (fieldParameters[i].getName().equals(argName)){
			return fieldParameters[i];
		}
	}
	return null;
}


/**
 * Gets the structureMappingParameters index property (cbit.vcell.mapping.StructureMappingParameter) value.
 * @return The structureMappingParameters property value.
 * @param index The index value into the property array.
 * @see #setStructureMappingParameters
 */
public StructureMappingParameter getParameterFromRole(int role) {
	for (int i = 0; i < fieldParameters.length; i++){
		if (fieldParameters[i] instanceof StructureMappingParameter){
			StructureMappingParameter structureMappingParameter = (StructureMappingParameter)fieldParameters[i];
			if (structureMappingParameter.getRole() == role){
				return structureMappingParameter;
			}
		}
	}
	return null;
}

public abstract StructureMappingParameter getUnitSizeParameter();

/**
 * Gets the parameters property (cbit.vcell.model.Parameter[]) value.
 * @return The parameters property value.
 * @see #setParameters
 */
public StructureMapping.StructureMappingParameter[] getParameters() {
	return fieldParameters;
}


/**
 * Gets the parameters index property (cbit.vcell.model.Parameter) value.
 * @return The parameters property value.
 * @param index The index value into the property array.
 * @see #setParameters
 */
public StructureMapping.StructureMappingParameter getParameters(int index) {
	return getParameters()[index];
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
 * This method was created by a SmartGuide.
 * @return double
 */
public StructureMappingParameter getSizeParameter() {
	return getParameterFromRole(ROLE_Size);
}

@Historical
public Expression getNullSizeParameterValue() {
	return new Expression(1.0);
}

/**
 * This method was created by a SmartGuide.
 * @return cbit.vcell.model.Feature
 */
public Structure getStructure() {
	return structure;
}


/**
 * This method was created in VisualAge.
 * @return cbit.vcell.parser.Expression
 */
public abstract Expression getNormalizedConcentrationCorrection(SimulationContext simulationContext, UnitFactorProvider unitFactorProvider) throws ExpressionException, MappingException;


/**
 * This method was created in VisualAge.
 * @return cbit.vcell.parser.Expression
 */
public abstract Expression getStructureSizeCorrection(SimulationContext simulationContext, UnitFactorProvider unitFactorProvider) throws ExpressionException, MappingException;


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
 * The hasListeners method was generated to support the propertyChange field.
 */
public synchronized boolean hasListeners(String propertyName) {
	return getPropertyChange().hasListeners(propertyName);
}

/**
 * This method was created by a SmartGuide.
 * @return boolean
 */
public boolean isBoundaryConditionValid(BoundaryLocation boundaryLocation) {
	return boundaryConditionValid[boundaryLocation.getNum()];
}



/**
 * Insert the method's description here.
 * Creation date: (2/19/2002 1:07:58 PM)
 */
public void refreshDependencies(){
	for (int i = 0; i < fieldParameters.length; i++){
		try {
			if (fieldParameters[i].getExpression()!=null){
				fieldParameters[i].getExpression().bindExpression(this);
			}
		}catch (ExpressionException e){
			System.out.println("error binding expression '"+fieldParameters[i].getExpression().infix()+"', "+e.getMessage());
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
 * The removeVetoableChangeListener method was generated to support the vetoPropertyChange field.
 */
public synchronized void removeVetoableChangeListener(String propertyName, java.beans.VetoableChangeListener listener) {
	getVetoPropertyChange().removeVetoableChangeListener(propertyName, listener);
}

/**
 * This method was created by a SmartGuide.
 * @param bct java.lang.String
 * @exception java.lang.Exception The exception description.
 */
public void setBoundaryCondition(BoundaryLocation boundaryLocation, BoundaryConditionType bc) {
	boundaryConditionTypes[boundaryLocation.getNum()] = bc;
}


/**
 * This method was created by a SmartGuide.
 * @param bct java.lang.String
 * @exception java.lang.Exception The exception description.
 */
public void setBoundaryConditionTypeXm(BoundaryConditionType bc) {
	BoundaryConditionType oldBCType = getBoundaryConditionTypeXm();
	setBoundaryCondition(BoundaryLocation.getXM(), bc);
	BoundaryConditionType newBCType = getBoundaryConditionTypeXm();
	firePropertyChange("boundaryConditionTypeXm",oldBCType,newBCType);
}


/**
 * This method was created by a SmartGuide.
 * @param bct java.lang.String
 * @exception java.lang.Exception The exception description.
 */
public void setBoundaryConditionTypeXp(BoundaryConditionType bc) {
	BoundaryConditionType oldBCType = getBoundaryConditionTypeXp();
	setBoundaryCondition(BoundaryLocation.getXP(), bc);
	BoundaryConditionType newBCType = getBoundaryConditionTypeXp();
	firePropertyChange("boundaryConditionTypeXp",oldBCType,newBCType);
}


/**
 * This method was created by a SmartGuide.
 * @param bct java.lang.String
 * @exception java.lang.Exception The exception description.
 */
public void setBoundaryConditionTypeYm(BoundaryConditionType bc) {
	BoundaryConditionType oldBCType = getBoundaryConditionTypeYm();
	setBoundaryCondition(BoundaryLocation.getYM(), bc);
	BoundaryConditionType newBCType = getBoundaryConditionTypeYm();
	firePropertyChange("boundaryConditionTypeYm",oldBCType,newBCType);
}


/**
 * This method was created by a SmartGuide.
 * @param bct java.lang.String
 * @exception java.lang.Exception The exception description.
 */
public void setBoundaryConditionTypeYp(BoundaryConditionType bc) {
	BoundaryConditionType oldBCType = getBoundaryConditionTypeYp();
	setBoundaryCondition(BoundaryLocation.getYP(), bc);
	BoundaryConditionType newBCType = getBoundaryConditionTypeYp();
	firePropertyChange("boundaryConditionTypeYp",oldBCType,newBCType);
}


/**
 * This method was created by a SmartGuide.
 * @param bct java.lang.String
 * @exception java.lang.Exception The exception description.
 */
public void setBoundaryConditionTypeZm(BoundaryConditionType bc) {
	BoundaryConditionType oldBCType = getBoundaryConditionTypeZm();
	setBoundaryCondition(BoundaryLocation.getZM(), bc);
	BoundaryConditionType newBCType = getBoundaryConditionTypeZm();
	firePropertyChange("boundaryConditionTypeZm",oldBCType,newBCType);
}


/**
 * This method was created by a SmartGuide.
 * @param bct java.lang.String
 * @exception java.lang.Exception The exception description.
 */
public void setBoundaryConditionTypeZp(BoundaryConditionType bc) {
	BoundaryConditionType oldBCType = getBoundaryConditionTypeZp();
	setBoundaryCondition(BoundaryLocation.getZP(), bc);
	BoundaryConditionType newBCType = getBoundaryConditionTypeZp();
	firePropertyChange("boundaryConditionTypeZp",oldBCType,newBCType);

}


/**
 * Sets the parameters property (cbit.vcell.model.Parameter[]) value.
 * @param parameters The new value for the property.
 * @exception java.beans.PropertyVetoException The exception description.
 * @see #getParameters
 */
public void setParameters(StructureMapping.StructureMappingParameter[] parameters) throws java.beans.PropertyVetoException {
	StructureMapping.StructureMappingParameter[] oldValue = fieldParameters;
	fireVetoableChange("parameters", oldValue, parameters);
	fieldParameters = parameters;
	firePropertyChange("parameters", oldValue, parameters);
}


/**
 * Insert the method's description here.
 * Creation date: (2/15/2004 9:13:35 AM)
 * @param argSimulationContext cbit.vcell.mapping.SimulationContext
 */
public void setSimulationContext(SimulationContext argSimulationContext) {
	this.simulationContext = argSimulationContext;
	if (this.simulationContext != null && this.simulationContext.getModel() != null) {
		modelUnitSystem = this.simulationContext.getModel().getUnitSystem();
	}
}


/**
 * Insert the method's description here.
 * Creation date: (3/27/01 12:50:12 PM)
 * @param structure cbit.vcell.model.Structure
 */
void setStructure(Structure argStructure) {
	this.structure = argStructure;
}

public void getLocalEntries(Map<String, SymbolTableEntry> entryMap) {	
	for (SymbolTableEntry ste : fieldParameters) {
		entryMap.put(ste.getName(), ste);
	}
}

public void getEntries(Map<String, SymbolTableEntry> entryMap) {
	getNameScope().getExternalEntries(entryMap);		
}

public GeometryClass getGeometryClass() {
	return geometryClass;
}


public void setGeometryClass(GeometryClass argGeometryClass) throws PropertyVetoException {
	GeometryClass oldValue = this.geometryClass;
	fireVetoableChange("geometryClass", oldValue, argGeometryClass);
	this.geometryClass = argGeometryClass;
	firePropertyChange("geometryClass", oldValue, argGeometryClass);
}

public List<StructureMappingParameter> computeApplicableParameterList() {
	List<StructureMappingParameter> structureMappingParameterList = new ArrayList<StructureMapping.StructureMappingParameter>();	
	if (getGeometryClass() instanceof CompartmentSubVolume) { // non spatial
		structureMappingParameterList.add(getSizeParameter());
		if (this instanceof MembraneMapping) {
			structureMappingParameterList.add(((MembraneMapping)this).getSurfaceToVolumeParameter());
			structureMappingParameterList.add(((MembraneMapping)this).getVolumeFractionParameter());
		}
	} else {
		if (getGeometryClass() instanceof SubVolume) {
			structureMappingParameterList.add(getUnitSizeParameter());			
		} else if (getGeometryClass() instanceof SurfaceClass) {
			structureMappingParameterList.add(getUnitSizeParameter());			
		}
	}
	return structureMappingParameterList;
}

}
