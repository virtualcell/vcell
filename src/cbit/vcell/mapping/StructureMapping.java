package cbit.vcell.mapping;
import cbit.vcell.parser.SymbolTableEntry;
import net.sourceforge.interval.ia_math.RealInterval;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.util.*;
import cbit.vcell.parser.Expression;
import cbit.vcell.model.*;
import cbit.vcell.geometry.*;
import cbit.util.*;
import cbit.vcell.parser.NameScope;
import cbit.vcell.parser.ScopedSymbolTable;
import cbit.vcell.parser.ExpressionBindingException;
import cbit.vcell.units.VCUnitDefinition;

public abstract class StructureMapping implements Matchable, cbit.vcell.parser.ScopedSymbolTable, java.io.Serializable {
	static {
		System.out.println("StructureMapping.StructureMappingParameter.setName() not protected for uniqueness, etc");
	}

	public class StructureMappingNameScope extends BioNameScope {
		private final NameScope children[] = new NameScope[0]; // always empty
		public StructureMappingNameScope(){
			super();
		}
		public cbit.vcell.parser.NameScope[] getChildren() {
			//
			// no children to return
			//
			return children;
		}
		public String getName() {
			return TokenMangler.fixTokenStrict(StructureMapping.this.getStructure().getName());
		}
		public cbit.vcell.parser.NameScope getParent() {
			if (StructureMapping.this.simulationContext != null){
				return StructureMapping.this.simulationContext.getNameScope();
			}else{
				return null;
			}
		}
		public cbit.vcell.parser.ScopedSymbolTable getScopedSymbolTable() {
			return StructureMapping.this;
		}
	}

	public class StructureMappingParameter extends cbit.vcell.model.Parameter {

		private int fieldParameterRole = -1;
		private String fieldParameterName = null;
		private Expression fieldParameterExpression = null;
		private VCUnitDefinition fieldVCUnitDefinition = null;


		public StructureMappingParameter(String parmName, cbit.vcell.parser.Expression argExpression, int argRole, VCUnitDefinition argVCUnitDefinition) {
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

		public boolean compareEqual(cbit.util.Matchable obj) {
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

		public boolean isExpressionEditable(){
			return true;
		}

		public boolean isUnitEditable(){
			return false;
		}

		public boolean isNameEditable(){
			return true;
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

		public void setExpression(cbit.vcell.parser.Expression expression) throws java.beans.PropertyVetoException, ExpressionBindingException {
			if (expression!=null){
				expression = new Expression(expression);
				expression.bindExpression(StructureMapping.this);
			}
			Expression oldValue = fieldParameterExpression;
			super.fireVetoableChange("expression", oldValue, expression);
			fieldParameterExpression = expression;
			super.firePropertyChange("expression", oldValue, expression);
		}

		public double getConstantValue() throws cbit.vcell.parser.ExpressionException {
			return fieldParameterExpression.evaluateConstant();
		}

		public cbit.vcell.parser.Expression getExpression() {
			return fieldParameterExpression;
		}

		public VCUnitDefinition getUnitDefinition(){
			return fieldVCUnitDefinition;
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
	}
	private Structure structure = null;
	private StructureMappingNameScope nameScope = new StructureMappingNameScope();
	protected SimulationContext simulationContext = null; // for determining NameScope parent only

	protected transient java.beans.PropertyChangeSupport propertyChange;
	protected transient java.beans.VetoableChangeSupport vetoPropertyChange;
	private transient NameScope parentNameScope = null;
	public static final int ROLE_SurfaceToVolumeRatio	= 0;
	public static final int ROLE_VolumeFraction			= 1;
	public static final int ROLE_SpecificCapacitance	= 2;
	public static final int ROLE_InitialVoltage			= 3;
	public static final int ROLE_Size					= 4;
	public static final int NUM_ROLES		= 5;
	public static final String RoleTags[] = {
		cbit.vcell.model.VCMODL.SurfaceToVolume,
		cbit.vcell.model.VCMODL.VolumeFraction,
		cbit.vcell.model.VCMODL.SpecificCapacitance,
		cbit.vcell.model.VCMODL.InitialVoltage,
		cbit.vcell.model.VCMODL.StructureSize
	};
	public static final String DefaultNames[] = {
		"SurfToVolRatio",
		"VolFract",
		"SpecCapacitance",
		"InitialVoltage",
		"Size"
	};
	private static final RealInterval[] parameterBounds = {
		new RealInterval(1.0E-3, 1.0E4),	// s/v ratio
		new RealInterval(1.0E-3, 0.999),							// volFract
		new RealInterval(0.0, Double.POSITIVE_INFINITY),	// Capacitance
		new RealInterval(-120, 60),	// init voltage
		new RealInterval(0.0, Double.POSITIVE_INFINITY)		// size
	};
	private StructureMapping.StructureMappingParameter[] fieldParameters = null;

protected StructureMapping(StructureMapping structureMapping, SimulationContext argSimulationContext) {
	if (argSimulationContext == null) {
		throw new IllegalArgumentException("SimulationContext is null");
	}	
	this.structure = structureMapping.getStructure();
	this.simulationContext = argSimulationContext;
	fieldParameters = new StructureMapping.StructureMappingParameter[structureMapping.getParameters().length];
	for (int i = 0; i < fieldParameters.length; i++){
		fieldParameters[i] = new StructureMappingParameter((StructureMappingParameter)structureMapping.getParameters(i));
	}
}      


protected StructureMapping(Structure structure, SimulationContext argSimulationContext) {
	if (argSimulationContext == null) {
		throw new IllegalArgumentException("SimulationContext is null");
	}
	this.structure = structure;
	this.simulationContext = argSimulationContext;
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
public synchronized void addPropertyChangeListener(String propertyName, java.beans.PropertyChangeListener listener) {
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
public synchronized void addVetoableChangeListener(String propertyName, java.beans.VetoableChangeListener listener) {
	getVetoPropertyChange().addVetoableChangeListener(propertyName, listener);
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

	return true;
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
public void firePropertyChange(String propertyName, int oldValue, int newValue) {
	getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
}


/**
 * The firePropertyChange method was generated to support the propertyChange field.
 */
public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
	getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
}


/**
 * The firePropertyChange method was generated to support the propertyChange field.
 */
public void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {
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
public void fireVetoableChange(String propertyName, int oldValue, int newValue) throws java.beans.PropertyVetoException {
	getVetoPropertyChange().fireVetoableChange(propertyName, oldValue, newValue);
}


/**
 * The fireVetoableChange method was generated to support the vetoPropertyChange field.
 */
public void fireVetoableChange(String propertyName, Object oldValue, Object newValue) throws java.beans.PropertyVetoException {
	getVetoPropertyChange().fireVetoableChange(propertyName, oldValue, newValue);
}


/**
 * The fireVetoableChange method was generated to support the vetoPropertyChange field.
 */
public void fireVetoableChange(String propertyName, boolean oldValue, boolean newValue) throws java.beans.PropertyVetoException {
	getVetoPropertyChange().fireVetoableChange(propertyName, oldValue, newValue);
}


/**
 * Insert the method's description here.
 * Creation date: (11/1/2005 9:57:23 AM)
 * @param issueVector java.util.Vector
 */
public void gatherIssues(Vector issueVector) {
	//
	// add constraints (simpleBounds) for predefined parameters
	//
	for (int i = 0; fieldParameters!=null && i < fieldParameters.length; i++){
		RealInterval simpleBounds = parameterBounds[fieldParameters[i].getRole()];
		if (simpleBounds!=null){
			String parmName = fieldParameters[i].getNameScope().getName()+"."+fieldParameters[i].getName();
			issueVector.add(new SimpleBoundsIssue(fieldParameters[i], simpleBounds, "parameter "+parmName+": must be within "+simpleBounds.toString()));
		}
	}
}


/**
 * getEntry method comment.
 */
public cbit.vcell.parser.SymbolTableEntry getEntry(java.lang.String identifierString) throws cbit.vcell.parser.ExpressionBindingException {
	
	cbit.vcell.parser.SymbolTableEntry ste = getLocalEntry(identifierString);
	if (ste != null){
		return ste;
	}
			
	return getNameScope().getExternalEntry(identifierString);
}


/**
 * Insert the method's description here.
 * Creation date: (12/8/2003 12:47:06 PM)
 * @return cbit.vcell.parser.SymbolTableEntry
 * @param identifier java.lang.String
 */
public cbit.vcell.parser.SymbolTableEntry getLocalEntry(java.lang.String identifier) throws cbit.vcell.parser.ExpressionBindingException {
	
	SymbolTableEntry ste;
	
	Parameter parameter = getParameter(identifier);
	
	return parameter;
}


/**
 * Insert the method's description here.
 * Creation date: (12/8/2003 12:47:06 PM)
 * @return cbit.vcell.parser.NameScope
 */
public cbit.vcell.parser.NameScope getNameScope() {
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
abstract Expression getTotalVolumeCorrection(SimulationContext simulationContext) throws cbit.vcell.parser.ExpressionException;


/**
 * This method was created by a SmartGuide.
 * @return java.lang.String
 */
public abstract String getVCML() throws Exception;


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
 * Insert the method's description here.
 * Creation date: (2/19/2002 1:07:58 PM)
 */
public void refreshDependencies(){
	for (int i = 0; i < fieldParameters.length; i++){
		try {
			if (fieldParameters[i].getExpression()!=null){
				fieldParameters[i].getExpression().bindExpression(this);
			}
		}catch (cbit.vcell.parser.ExpressionException e){
			System.out.println("error binding expression '"+fieldParameters[i].getExpression().infix()+"', "+e.getMessage());
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
 * The removePropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void removePropertyChangeListener(String propertyName, java.beans.PropertyChangeListener listener) {
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
public synchronized void removeVetoableChangeListener(String propertyName, java.beans.VetoableChangeListener listener) {
	getVetoPropertyChange().removeVetoableChangeListener(propertyName, listener);
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
}


/**
 * Insert the method's description here.
 * Creation date: (3/27/01 12:50:12 PM)
 * @param structure cbit.vcell.model.Structure
 */
void setStructure(Structure argStructure) {
	this.structure = argStructure;
}
}