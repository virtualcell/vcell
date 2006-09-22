package cbit.vcell.modelapp;
import java.beans.PropertyVetoException;
import java.io.Serializable;

import net.sourceforge.interval.ia_math.RealInterval;
import cbit.util.Compare;
import cbit.util.Matchable;
import cbit.vcell.model.BioNameScope;
import cbit.vcell.model.Feature;
import cbit.vcell.model.Membrane;
import cbit.vcell.model.ReservedSymbol;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.model.VCMODL;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionBindingException;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.NameScope;
import cbit.vcell.units.VCUnitDefinition;

public class SpeciesContextSpec implements cbit.util.Matchable, cbit.vcell.parser.ScopedSymbolTable, Serializable {

	public class SpeciesContextSpecNameScope extends BioNameScope {
		private final NameScope children[] = new NameScope[0]; // always empty
		public SpeciesContextSpecNameScope(){
			super();
		}
		public cbit.vcell.parser.NameScope[] getChildren() {
			//
			// no children to return
			//
			return children;
		}
		public String getName() {
			return SpeciesContextSpec.this.getSpeciesContext().getName();
		}
		public cbit.vcell.parser.NameScope getParent() {
			if (SpeciesContextSpec.this.simulationContext != null){
				return SpeciesContextSpec.this.simulationContext.getNameScope();
			}else{
				return null;
			}
		}
		public cbit.vcell.parser.ScopedSymbolTable getScopedSymbolTable() {
			return SpeciesContextSpec.this;
		}
	}

	public class SpeciesContextSpecParameter extends cbit.vcell.model.Parameter {
		private Expression fieldParameterExpression = null;
		private String fieldParameterName = null;
 		private int fieldParameterRole = -1;
 		private cbit.vcell.units.VCUnitDefinition fieldUnitDefinition = null;

		public SpeciesContextSpecParameter(String parmName, cbit.vcell.parser.Expression argExpression, int argRole, cbit.vcell.units.VCUnitDefinition argUnitDefinition, String argDescription) {
			super();
			fieldParameterName = parmName;
			fieldParameterExpression = argExpression;
			if (argRole >= 0 && argRole < NUM_ROLES){
				this.fieldParameterRole = argRole;
			}else{
				throw new IllegalArgumentException("parameter 'role' = "+argRole+" is out of range");
			}
			fieldUnitDefinition = argUnitDefinition;
			setDescription(argDescription);
		}

		public boolean compareEqual(cbit.util.Matchable obj) {
			if (!(obj instanceof SpeciesContextSpecParameter)){
				return false;
			}
			SpeciesContextSpecParameter smp = (SpeciesContextSpecParameter)obj;
			if (!super.compareEqual0(smp)){
				return false;
			}
			if (fieldParameterRole != smp.fieldParameterRole){
				return false;
			}
			
			return true;
		}

		public NameScope getNameScope(){
			return SpeciesContextSpec.this.getNameScope();
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

		public cbit.vcell.units.VCUnitDefinition getUnitDefinition() {
			return fieldUnitDefinition;
		}
		
		public void setExpression(Expression expression) throws PropertyVetoException, ExpressionBindingException {
			if (expression!=null){
				expression = new Expression(expression);
				expression.bindExpression(SpeciesContextSpec.this);
			}
			Expression oldValue = fieldParameterExpression;
			super.fireVetoableChange("expression", oldValue, expression);
			fieldParameterExpression = expression;
			super.firePropertyChange("expression", oldValue, expression);
		}
		
		public double getConstantValue() throws cbit.vcell.parser.ExpressionException {
			return fieldParameterExpression.evaluateConstant();
		}
		
		public void setName(java.lang.String name) throws java.beans.PropertyVetoException {
			String oldValue = fieldParameterName;
			super.fireVetoableChange("name", oldValue, name);
			fieldParameterName = name;
			super.firePropertyChange("name", oldValue, name);
		}
		
		public void setUnitDefinition(VCUnitDefinition unitDefinition) throws java.beans.PropertyVetoException {
			VCUnitDefinition oldValue = fieldUnitDefinition;
			super.fireVetoableChange("unitDefinition", oldValue, unitDefinition);
			fieldUnitDefinition = unitDefinition;
			super.firePropertyChange("unitDefinition", oldValue, unitDefinition);
		}
		
		public String getName(){
			return fieldParameterName;
		}
		
		public Expression getExpression(){
			return fieldParameterExpression;
		}
		
		public int getIndex() {
			return -1;
		}

		public int getRole() {
			return fieldParameterRole;
		}
	}
	private SpeciesContext speciesContext = null;
	private static final boolean DEFAULT_CONSTANT = false;
	private static final boolean DEFAULT_ENABLE_DIFFUSING = true;
	private boolean        bConstant = DEFAULT_CONSTANT;
	private boolean        bEnableDiffusing = DEFAULT_ENABLE_DIFFUSING;
	protected transient java.beans.VetoableChangeSupport vetoPropertyChange;
	private SpeciesContextSpecParameter[] fieldParameters = null;
	protected transient java.beans.PropertyChangeSupport propertyChange;
	protected transient SimulationContext simulationContext = null;
	private SpeciesContextSpecNameScope nameScope = new SpeciesContextSpecNameScope();
	public static final int ROLE_InitialConcentration	= 0;
	public static final int ROLE_DiffusionRate			= 1;
	public static final int ROLE_BoundaryValueXm		= 2;
	public static final int ROLE_BoundaryValueXp		= 3;
	public static final int ROLE_BoundaryValueYm		= 4;
	public static final int ROLE_BoundaryValueYp		= 5;
	public static final int ROLE_BoundaryValueZm		= 6;
	public static final int ROLE_BoundaryValueZp		= 7;
	public static final int NUM_ROLES		= 8;
	public static final String RoleTags[] = {
		cbit.vcell.model.VCMODL.InitialCondition,
		cbit.vcell.model.VCMODL.DiffusionRate,
		cbit.vcell.model.VCMODL.BoundaryConditionXm,
		cbit.vcell.model.VCMODL.BoundaryConditionXp,
		cbit.vcell.model.VCMODL.BoundaryConditionYm,
		cbit.vcell.model.VCMODL.BoundaryConditionYp,
		cbit.vcell.model.VCMODL.BoundaryConditionZm,
		cbit.vcell.model.VCMODL.BoundaryConditionZp,
	};
	public static final String RoleNames[] = {
		"init",
		"diff",
		"BC_Xm",
		"BC_Xp",
		"BC_Ym",
		"BC_Yp",
		"BC_Zm",
		"BC_Zp",
	};
	public static final String RoleDescriptions[] = {
		"initial concentration",
		"diffusion constant",
		"Boundary Condition X-",
		"Boundary Condition X+",
		"Boundary Condition Y-",
		"Boundary Condition Y+",
		"Boundary Condition Z-",
		"Boundary Condition Z+",
	};
	private static RealInterval[] parameterBounds = {
		new RealInterval(0.0, Double.POSITIVE_INFINITY), // init cond
		new RealInterval(0.0, Double.POSITIVE_INFINITY), // diff rate
		null,	// BC X-
		null,	// BC X+
		null,	// BC Y-
		null,	// BC Y+
		null,	// BC Z-
		null	// BC Z+
	};

public SpeciesContextSpec(SpeciesContextSpec speciesContextSpec, SimulationContext argSimulationContext) {
	this.speciesContext = speciesContextSpec.speciesContext;
	this.bConstant = speciesContextSpec.bConstant;
	this.bEnableDiffusing = speciesContextSpec.bEnableDiffusing;
	fieldParameters = new SpeciesContextSpecParameter[speciesContextSpec.fieldParameters.length];
	for (int i = 0; i < speciesContextSpec.fieldParameters.length; i++){
		SpeciesContextSpecParameter otherParm = speciesContextSpec.fieldParameters[i];
		Expression otherParmExp = (otherParm.getExpression()==null)?(null):(new Expression(otherParm.getExpression()));
		fieldParameters[i] = new SpeciesContextSpecParameter(otherParm.getName(),otherParmExp,otherParm.getRole(),otherParm.getUnitDefinition(),otherParm.getDescription());
	}
	this.simulationContext = argSimulationContext;
	refreshDependencies();
}            


public SpeciesContextSpec(SpeciesContext speciesContext, SimulationContext argSimulationContext) {
	this.speciesContext = speciesContext;

	VCUnitDefinition fluxUnits = speciesContext.getUnitDefinition().multiplyBy(VCUnitDefinition.UNIT_um).divideBy(VCUnitDefinition.UNIT_s);
	fieldParameters = new SpeciesContextSpecParameter[8];
	fieldParameters[0] = new SpeciesContextSpecParameter(RoleNames[ROLE_InitialConcentration],new Expression(0.0),
														ROLE_InitialConcentration,speciesContext.getUnitDefinition(),
														RoleDescriptions[ROLE_InitialConcentration]);

	fieldParameters[1] = new SpeciesContextSpecParameter(RoleNames[ROLE_DiffusionRate],new Expression(0.0),
														ROLE_DiffusionRate,VCUnitDefinition.UNIT_um2_per_s,
														RoleDescriptions[ROLE_DiffusionRate]);

	fieldParameters[2] = new SpeciesContextSpecParameter(RoleNames[ROLE_BoundaryValueXm],null,
														ROLE_BoundaryValueXm,fluxUnits,
														RoleDescriptions[ROLE_BoundaryValueXm]);
	
	fieldParameters[3] = new SpeciesContextSpecParameter(RoleNames[ROLE_BoundaryValueXp],null,
														ROLE_BoundaryValueXp,fluxUnits,
														RoleDescriptions[ROLE_BoundaryValueXp]);

	fieldParameters[4] = new SpeciesContextSpecParameter(RoleNames[ROLE_BoundaryValueYm],null,
														ROLE_BoundaryValueYm,fluxUnits,
														RoleDescriptions[ROLE_BoundaryValueYm]);
	
	fieldParameters[5] = new SpeciesContextSpecParameter(RoleNames[ROLE_BoundaryValueYp],null,
														ROLE_BoundaryValueYp,fluxUnits,
														RoleDescriptions[ROLE_BoundaryValueYp]);

	fieldParameters[6] = new SpeciesContextSpecParameter(RoleNames[ROLE_BoundaryValueZm],null,
														ROLE_BoundaryValueZm,fluxUnits,
														RoleDescriptions[ROLE_BoundaryValueZm]);
	
	fieldParameters[7] = new SpeciesContextSpecParameter(RoleNames[ROLE_BoundaryValueZp],null,
														ROLE_BoundaryValueZp,fluxUnits,
														RoleDescriptions[ROLE_BoundaryValueZp]);
	this.simulationContext = argSimulationContext;
	resetDefaults();
	refreshDependencies();
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
 * @param object java.lang.Object
 */
public boolean compareEqual(Matchable object) {

	SpeciesContextSpec scs = null;
	if (!(object instanceof SpeciesContextSpec)){
		return false;
	}
	scs = (SpeciesContextSpec)object;

	if (!Compare.isEqual(speciesContext,scs.speciesContext)){
		return false;
	}

	if (bConstant != scs.bConstant){
		return false;
	}
	
	if (bEnableDiffusing != scs.bEnableDiffusing){
		return false;
	}

	if (!Compare.isEqual(fieldParameters,scs.fieldParameters)){
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
 * Creation date: (11/1/2005 10:03:46 AM)
 * @param issueVector java.util.Vector
 */
public void gatherIssues(java.util.Vector issueVector) {
	//
	// add constraints (simpleBounds) for predefined parameters
	//
	for (int i = 0; i < fieldParameters.length; i++){
		RealInterval simpleBounds = parameterBounds[fieldParameters[i].getRole()];
		if (simpleBounds!=null){
			String parmName = fieldParameters[i].getNameScope().getName()+"."+fieldParameters[i].getName();
			issueVector.add(new cbit.vcell.model.SimpleBoundsIssue(fieldParameters[i], simpleBounds, "parameter "+parmName+": must be within "+simpleBounds.toString()));
		}
	}
}


/**
 * This method was created in VisualAge.
 * @return cbit.vcell.parser.Expression
 */
public SpeciesContextSpec.SpeciesContextSpecParameter getBoundaryXmParameter() {
	return getParameterFromRole(ROLE_BoundaryValueXm);		
}


/**
 * This method was created in VisualAge.
 * @return cbit.vcell.parser.Expression
 */
public SpeciesContextSpec.SpeciesContextSpecParameter getBoundaryXpParameter() {
	return getParameterFromRole(ROLE_BoundaryValueXp);		
}


/**
 * This method was created in VisualAge.
 * @return cbit.vcell.parser.Expression
 */
public SpeciesContextSpec.SpeciesContextSpecParameter getBoundaryYmParameter() {
	return getParameterFromRole(ROLE_BoundaryValueYm);		
}


/**
 * This method was created in VisualAge.
 * @return cbit.vcell.parser.Expression
 */
public SpeciesContextSpec.SpeciesContextSpecParameter getBoundaryYpParameter() {
	return getParameterFromRole(ROLE_BoundaryValueYp);		
}


/**
 * This method was created in VisualAge.
 * @return cbit.vcell.parser.Expression
 */
public SpeciesContextSpec.SpeciesContextSpecParameter getBoundaryZmParameter() {
	return getParameterFromRole(ROLE_BoundaryValueZm);		
}


/**
 * This method was created in VisualAge.
 * @return cbit.vcell.parser.Expression
 */
public SpeciesContextSpec.SpeciesContextSpecParameter getBoundaryZpParameter() {
	return getParameterFromRole(ROLE_BoundaryValueZp);		
}


/**
 * This method was created in VisualAge.
 * @return cbit.vcell.parser.Expression
 */
public SpeciesContextSpec.SpeciesContextSpecParameter getDiffusionParameter() {
	return getParameterFromRole(ROLE_DiffusionRate);		
}


/**
 * getEntry method comment.
 */
public cbit.vcell.parser.SymbolTableEntry getEntry(java.lang.String identifierString) throws cbit.vcell.parser.ExpressionBindingException {
	
	cbit.vcell.parser.SymbolTableEntry ste = getLocalEntry(identifierString);
	if (ste != null){
		return ste;
	}
			
	ste = getNameScope().getExternalEntry(identifierString);

	return ste;
}


/**
 * This method was created by a SmartGuide.
 * @return java.lang.String
 */
public SpeciesContextSpec.SpeciesContextSpecParameter getInitialConditionParameter() {
	return getParameterFromRole(ROLE_InitialConcentration);		
}


/**
 * Insert the method's description here.
 * Creation date: (12/8/2003 11:46:37 AM)
 * @return cbit.vcell.parser.SymbolTableEntry
 * @param identifier java.lang.String
 */
public cbit.vcell.parser.SymbolTableEntry getLocalEntry(java.lang.String identifier) throws cbit.vcell.parser.ExpressionBindingException {
	cbit.vcell.parser.SymbolTableEntry ste = null;

	ste = ReservedSymbol.fromString(identifier);
	if (ste!=null){
		return ste;
	}

	ste = getParameterFromName(identifier);
	if (ste!=null){
		return ste;
	}

	return null;
}


/**
 * Insert the method's description here.
 * Creation date: (12/8/2003 11:46:37 AM)
 * @return cbit.vcell.parser.NameScope
 */
public cbit.vcell.parser.NameScope getNameScope() {
	return nameScope;
}


/**
 * Insert the method's description here.
 * Creation date: (9/22/2005 11:56:39 AM)
 */
public int getNumDisplayableParameters() {
	if (simulationContext==null){
		return 1;
	}
	if (isConstant()){
		return 1;
	}

	if (speciesContext.getStructure() instanceof Membrane){
		MembraneMapping membraneMapping = (MembraneMapping)simulationContext.getGeometryContext().getStructureMapping(speciesContext.getStructure());
		boolean bResolved = membraneMapping.getResolved(simulationContext);
		if (simulationContext.getGeometry()!=null && bResolved){
			switch (simulationContext.getGeometry().getDimension()){
				case 0:
				case 1:{
					return 1;
				}
				case 2:{
					return 6;   // IC,Diff,Xm,Xp,Ym,Yp
				}
				case 3:{
					return 8;   // IC,Diff,Xm,Xp,Ym,Yp,Zm,Zp
				}
				default:{
					throw new RuntimeException("unexpected Geometry dimension"); // could never happen
				}
			}
		}else{
			return 1;          // don't have geometry info or not resolved, only Initial Conditions to be displayed.
		}
	}else if (speciesContext.getStructure() instanceof Feature){
		FeatureMapping featureMapping = (FeatureMapping)simulationContext.getGeometryContext().getStructureMapping(speciesContext.getStructure());
		boolean bResolved = featureMapping.getResolved();
		if (simulationContext.getGeometry()!=null && bResolved){
			switch (simulationContext.getGeometry().getDimension()){
				case 0:{
					return 1;  // just IC.
				}
				case 1:{
					return 4;  // IC,Diff,Xm,Xp
				}
				case 2:{
					return 6;  // IC,Diff,Xm,Xp,Ym,Yp
				}
				case 3:{
					return 8;  // IC,Diff,Xm,Xp,Ym,Yp,Zm,Zp
				}
				default:{
					throw new RuntimeException("unexpected Geometry dimension"); // could never happen
				}
			}
		}else{
			return 1;       // don't have geometry info or not resolved, only Initial Conditions to be displayed.
		}
	}else{
		throw new RuntimeException("unsupported Structure type '"+speciesContext.getStructure().getClass().getName()+"'");
	}	
}


/**
 * Insert the method's description here.
 * Creation date: (4/3/2004 10:48:38 AM)
 * @return cbit.vcell.mapping.SpeciesContextSpec.SpeciesContextSpecParameter
 * @param role int
 */
public SpeciesContextSpec.SpeciesContextSpecParameter getParameterFromName(String name) {
	for (int i = 0; i < fieldParameters.length; i++){
		if (fieldParameters[i].getName().equals(name)){
			return fieldParameters[i];
		}
	}
	return null;
}


/**
 * Insert the method's description here.
 * Creation date: (4/3/2004 10:48:38 AM)
 * @return cbit.vcell.mapping.SpeciesContextSpec.SpeciesContextSpecParameter
 * @param role int
 */
public SpeciesContextSpec.SpeciesContextSpecParameter getParameterFromRole(int role) {
	for (int i = 0; i < fieldParameters.length; i++){
		if (fieldParameters[i].getRole() == role){
			return fieldParameters[i];
		}
	}
	return null;
}


/**
 * Gets the parameters property (cbit.vcell.model.Parameter[]) value.
 * @return The parameters property value.
 * @see #setParameters
 */
public cbit.vcell.model.Parameter[] getParameters() {
	return fieldParameters;
}


/**
 * Gets the parameters index property (cbit.vcell.model.Parameter) value.
 * @return The parameters property value.
 * @param index The index value into the property array.
 * @see #setParameters
 */
public cbit.vcell.model.Parameter getParameters(int index) {
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
 * Insert the method's description here.
 * Creation date: (4/3/2004 10:54:49 AM)
 * @return int
 * @param boundaryLocation cbit.vcell.mapping.BoundaryLocation
 */
private int getRole(BoundaryLocation boundaryLocation) {
	int role = -1;
	switch (boundaryLocation.getNum()){
		case BoundaryLocation.BOUNDARY_XM:{
			role = ROLE_BoundaryValueXm;
			break;
		}
		case BoundaryLocation.BOUNDARY_XP:{
			role = ROLE_BoundaryValueXp;
			break;
		}
		case BoundaryLocation.BOUNDARY_YM:{
			role = ROLE_BoundaryValueYm;
			break;
		}
		case BoundaryLocation.BOUNDARY_YP:{
			role = ROLE_BoundaryValueYp;
			break;
		}
		case BoundaryLocation.BOUNDARY_ZM:{
			role = ROLE_BoundaryValueZm;
			break;
		}
		case BoundaryLocation.BOUNDARY_ZP:{
			role = ROLE_BoundaryValueZp;
			break;
		}
	}
	return role;
}


/**
 * This method was created by a SmartGuide.
 * @return cbit.vcell.model.SpeciesContext
 */
public SpeciesContext getSpeciesContext() {
	return speciesContext;
}


/**
 * This method was created by a SmartGuide.
 * @return java.lang.String
 */
public String getVCML() {
	StringBuffer buffer = new StringBuffer();
	buffer.append("\t"+VCMODL.SpeciesContextSpec+" "+getSpeciesContext().getName()+" {\n");
	buffer.append("\t\t"+VCMODL.ForceConstant+" "+isConstant()+"\n");
	buffer.append("\t\t"+VCMODL.EnableDiffusion+" "+isDiffusing()+"\n");
	Expression init = getInitialConditionParameter().getExpression();
	if (init!=null){
		buffer.append("\t\t"+VCMODL.InitialCondition+" "+init.toString()+";\n");
	}
	Expression diffRate = getDiffusionParameter().getExpression();
	if (diffRate!=null){
		buffer.append("\t\t"+VCMODL.DiffusionRate+" "+diffRate.toString()+";\n");
	}
	//
	// write BoundaryConditions
	//
	for (int i=BoundaryLocation.FIRST;i<=BoundaryLocation.LAST;i++){
		BoundaryLocation bl = BoundaryLocation.fromDirection(i);
		Expression boundExp = getParameterFromRole(getRole(bl)).getExpression();
		if (boundExp!=null){
			buffer.append("\t\t"+VCMODL.BoundaryCondition+" "+bl.toString()+" "+boundExp.toString()+"\n");
		}
	}
	buffer.append("\t}\n");
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
 * This method was created by a SmartGuide.
 * @return boolean
 */
public boolean isConstant() {
	return bConstant;
}


/**
 * This method was created in VisualAge.
 * @return boolean
 */
public boolean isDiffusing() {
	if (bConstant){
		return false;
	}
	
	try {
		double constantDiffRate = getDiffusionParameter().getExpression().evaluateConstant();
		return (constantDiffRate>0.0);
	}catch (ExpressionException e){
	}

	//
	// can't evaluate to constant, so must be non-zero
	//
	return true;
}


/**
 * This method was created in VisualAge.
 * @return boolean
 * @deprecated (no longer used)
 */
public final boolean isEnableDiffusing() {
	return bEnableDiffusing;
}


/**
 * This method was created in VisualAge.
 */
public void refreshDependencies() {
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
 * Insert the method's description here.
 * Creation date: (4/3/2004 11:13:04 AM)
 * @param parameter cbit.vcell.mapping.SpeciesContextSpec.SpeciesContextSpecParameter
 */
private void removeParameter(SpeciesContextSpec.SpeciesContextSpecParameter parameter) {}


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
 * This method was created in VisualAge.
 */
private void resetDefaults() {
	bConstant = DEFAULT_CONSTANT;
	if (getSpeciesContext().getStructure() instanceof Feature || getSpeciesContext().getStructure() instanceof Membrane){
		bEnableDiffusing = DEFAULT_ENABLE_DIFFUSING;
	}else{
		bEnableDiffusing = false;
	}
}


/**
 * This method was created in VisualAge.
 * @param isConstant boolean
 */
public void setConstant(boolean isConstant) {
	boolean oldDiffusing = isDiffusing();
	boolean oldConstant = bConstant;
	this.bConstant = isConstant;
	firePropertyChange("constant",new Boolean(oldConstant), new Boolean(isConstant));
	firePropertyChange("diffusing", new Boolean(oldDiffusing), new Boolean(isDiffusing()));
}


/**
 * This method was created in VisualAge.
 * @param isDiffusing boolean
 * @deprecated
 */
public void setEnableDiffusing(boolean isEnableDiffusing) throws MappingException {
	boolean oldEnableDiffusing = bEnableDiffusing;

	this.bEnableDiffusing = isEnableDiffusing;

	firePropertyChange("enableDiffusing",new Boolean(oldEnableDiffusing), new Boolean(isEnableDiffusing));
}


/**
 * Sets the parameters property (cbit.vcell.model.Parameter[]) value.
 * @param parameters The new value for the property.
 * @exception java.beans.PropertyVetoException The exception description.
 * @see #getParameters
 */
private void setParameters(SpeciesContextSpecParameter[] parameters) throws java.beans.PropertyVetoException {
	SpeciesContextSpecParameter[] oldValue = fieldParameters;
	fireVetoableChange("parameters", oldValue, parameters);
	fieldParameters = parameters;
	firePropertyChange("parameters", oldValue, parameters);
}


/**
 * Insert the method's description here.
 * Creation date: (4/23/2004 7:25:49 AM)
 * @param newSimulationContext cbit.vcell.mapping.SimulationContext
 */
public void setSimulationContext(SimulationContext newSimulationContext) {
	simulationContext = newSimulationContext;
}


/**
 * This method was created in VisualAge.
 * @param sc cbit.vcell.model.SpeciesContext
 */
void setSpeciesContextReference(SpeciesContext sc) throws MappingException {
	
	if (this.speciesContext == sc){
		return;
	}
	
	if (sc.compareEqual(this.speciesContext)){
		this.speciesContext = sc;
	}else{
		throw new MappingException("replacing speciesContext that is not the same");
	}
}


/**
 * This method was created by a SmartGuide.
 * @return java.lang.String
 */
public String toString() {
	StringBuffer sb = new StringBuffer();
	
	sb.append(getClass().getName()+"\n");
   if (speciesContext != null) { sb.append("speciesContext = '"+speciesContext+"'\n"); }
 	
	return sb.toString();
}


/**
 * Insert the method's description here.
 * Creation date: (4/3/2004 1:24:41 PM)
 */
protected void updateExpressions() {}
}