package cbit.vcell.mapping;
import cbit.vcell.mapping.potential.VoltageClampElectricalDevice;
import cbit.vcell.mapping.potential.CurrentClampElectricalDevice;
import cbit.vcell.mapping.potential.MembraneElectricalDevice;
import cbit.vcell.mapping.potential.PotentialMapping;
import cbit.vcell.mapping.potential.ElectricalDevice;
import cbit.vcell.units.VCUnitException;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.util.ISize;
import cbit.vcell.math.*;
import cbit.vcell.matrix.MatrixException;
import cbit.vcell.model.*;
import cbit.vcell.geometry.*;
import cbit.vcell.parser.*;
import java.util.*;
import cbit.vcell.units.VCUnitDefinition;
/**
 * The MathMapping class performs the Biological to Mathematical transformation once upon calling getMathDescription().
 * This is not a "live" transformation, so that an updated SimulationContext must be given to a new MathMapping object
 * to get an updated MathDescription.
 */
public class MathMapping implements ScopedSymbolTable {
	private SimulationContext simContext = null;
	private MathDescription mathDesc = null;
	private PotentialMapping potentialMapping = null;  // null if don't need it
	private Vector issueList = new Vector();
	private MathSymbolMapping mathSymbolMapping = new MathSymbolMapping();

	private MathMapping.MathMappingParameter[] fieldMathMappingParameters = new MathMappingParameter[0];
	protected transient java.beans.VetoableChangeSupport vetoPropertyChange;
	protected transient java.beans.PropertyChangeSupport propertyChange;
	private NameScope nameScope = new MathMappingNameScope();

	public static final int PARAMETER_ROLE_TOTALMASS = 0;
	public static final int PARAMETER_ROLE_DEPENDENT_VARIABLE = 1;
	public static final int PARAMETER_ROLE_KFLUX = 2;
	public static final int NUM_PARAMETER_ROLES = 3;
		
	private Vector structureAnalyzerList = new Vector();
	
	private Vector speciesContextMappingList = new Vector();

	public class MathMappingNameScope extends BioNameScope {
		private cbit.vcell.parser.NameScope nameScopes[] = null;
		public MathMappingNameScope(){
			super();
		}
		public cbit.vcell.parser.NameScope[] getChildren() {
			//
			// return model namescope
			//
			ElectricalDevice electricalDevices[] = (potentialMapping!=null)?(potentialMapping.getElectricalDevices()):(new ElectricalDevice[0]);
			NameScope simContextChildren[] = simContext.getNameScope().getChildren();
			NameScope modelChildren[] = simContext.getModel().getNameScope().getChildren();
			int childCount = simContextChildren.length + modelChildren.length + electricalDevices.length;
			if (nameScopes==null || nameScopes.length != childCount){
				nameScopes = new NameScope[childCount];
				int index = 0;
				for (int i=0;i<electricalDevices.length;i++){
					nameScopes[index++] = electricalDevices[i].getNameScope();
				}
				for (int i=0;i<simContextChildren.length;i++){
					nameScopes[index++] = simContextChildren[i];
				}
				for (int i=0;i<modelChildren.length;i++){
					nameScopes[index++] = modelChildren[i];
				}
			}
			return nameScopes;
		}
		public String getName() {
			return "MathMapping_for_"+cbit.util.TokenMangler.fixTokenStrict(simContext.getName());
		}
		public cbit.vcell.parser.NameScope getParent() {
			//System.out.println("MathMappingNameScope.getParent() returning null ... no parent");
			return null;
		}
		public cbit.vcell.parser.ScopedSymbolTable getScopedSymbolTable() {
			return MathMapping.this;
		}
		public boolean isPeer(cbit.vcell.parser.NameScope nameScope){
			return (nameScope == MathMapping.this.simContext.getNameScope() || nameScope == MathMapping.this.simContext.getModel().getNameScope());
		}
	}

	public class MathMappingParameter extends Parameter {
		
		private String fieldParameterName = null;
		private Expression fieldExpression = null;
		private VCUnitDefinition fieldVCUnitDefinition = null;
		private int fieldRole = -1;

		protected MathMappingParameter(String argName, Expression argExpression, int argRole, VCUnitDefinition argVCUnitDefinition) {
			if (argName == null){
				throw new IllegalArgumentException("parameter name is null");
			}
			if (argName.length()<1){
				throw new IllegalArgumentException("parameter name is zero length");
			}
			if (argExpression == null){
				throw new IllegalArgumentException("parameter name is null");
			}
			if (argRole<0 || argRole>=NUM_PARAMETER_ROLES){
				throw new IllegalArgumentException("parameter role is invalid");
			}
			this.fieldParameterName = argName;
			this.fieldExpression = argExpression;
			this.fieldRole = argRole;
			this.fieldVCUnitDefinition = argVCUnitDefinition;
		}

		public boolean compareEqual(cbit.util.Matchable obj) {
			if (!(obj instanceof MathMappingParameter)){
				return false;
			}
			MathMappingParameter mmp = (MathMappingParameter)obj;
			if (!super.compareEqual0(mmp)){
				return false;
			}			
			return true;
		}

		public double getConstantValue() throws ExpressionException {
			throw new ExpressionException("no expression defined for MathMappingParameter '"+fieldParameterName+"'");
		}      

		public Expression getExpression() {
			return this.fieldExpression;
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

		public int getIndex() {
			return -1;
		}

		public cbit.vcell.units.VCUnitDefinition getUnitDefinition() {
			return fieldVCUnitDefinition;
		}

		public int getRole() {
			return fieldRole;
		}

		public String getName(){ 
			return this.fieldParameterName; 
		}   

		public NameScope getNameScope() {
			return MathMapping.this.getNameScope();
		}

		public void setExpression(Expression argExpression) throws java.beans.PropertyVetoException {
			Expression oldValue = fieldExpression;
			super.fireVetoableChange("expression", oldValue, argExpression);
			fieldExpression = argExpression;
			super.firePropertyChange("expression", oldValue, argExpression);
		}

		public void setName(java.lang.String name) throws java.beans.PropertyVetoException {
			String oldValue = fieldParameterName;
			super.fireVetoableChange("name", oldValue, name);
			fieldParameterName = name;
			super.firePropertyChange("name", oldValue, name);
		}

	}
	public class KFluxParameter extends MathMappingParameter {
		
		private MembraneMapping fieldMembraneMapping = null;
		private Feature fieldFeature = null;

		protected KFluxParameter(String argName, Expression argExpression, VCUnitDefinition argVCUnitDefinition, MembraneMapping argMembraneMapping, Feature argFeature) {
			super(argName,argExpression,PARAMETER_ROLE_KFLUX,argVCUnitDefinition);
			this.fieldMembraneMapping = argMembraneMapping;
			this.fieldFeature = argFeature;
		}

		public MembraneMapping getMembraneMapping() {
			return fieldMembraneMapping;
		}

		public Feature getFeature() {
			return fieldFeature;
		}

	}
	
	static {
		System.out.println("MathMapping: capacitances must not be overridden and must be constant (used as literals in KVL)");
	};

/**
 * This method was created in VisualAge.
 * @param model cbit.vcell.model.Model
 * @param geometry cbit.vcell.geometry.Geometry
 */
public MathMapping(SimulationContext simContext) {
	this.simContext = simContext;
}


/**
 * Insert the method's description here.
 * Creation date: (3/29/2004 12:44:00 AM)
 * @return cbit.vcell.mapping.MathMapping.MathMappingParameter
 * @param name java.lang.String
 * @param expression cbit.vcell.parser.Expression
 * @param role int
 */
MathMapping.MathMappingParameter addMathMappingParameter(String name, Expression expression, int role, VCUnitDefinition unitDefinition) throws java.beans.PropertyVetoException, ExpressionBindingException {

	MathMapping.MathMappingParameter newParameter = new MathMapping.MathMappingParameter(name,expression,role,unitDefinition);
	MathMapping.MathMappingParameter previousParameter = getMathMappingParameter(name);
	if(previousParameter != null){
		System.out.println("MathMapping.MathMappingParameter addMathMappingParameter found duplicate parameter for name "+name);
		if(!previousParameter.compareEqual(newParameter)){
			throw new RuntimeException("MathMapping.MathMappingParameter addMathMappingParameter found duplicate parameter for name "+name);
		}
		return previousParameter;
	}
	expression.bindExpression(this);
	MathMapping.MathMappingParameter newParameters[] = (MathMapping.MathMappingParameter[])cbit.util.BeanUtils.addElement(fieldMathMappingParameters,newParameter);
	setMathMapppingParameters(newParameters);
	return newParameter;
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
 * getEntry method comment.
 */
public cbit.vcell.parser.SymbolTableEntry getEntry(java.lang.String identifierString) throws cbit.vcell.parser.ExpressionBindingException {
	cbit.vcell.parser.SymbolTableEntry ste = getLocalEntry(identifierString);
	if (ste != null){
		return ste;
	}
	ste = getNameScope().getExternalEntry(identifierString);
	if (ste==null){
		System.out.println("MathMapping is unable to bind identifier '"+identifierString+"'");
	}
	return ste;
}


/**
 * This method was created in VisualAge.
 * @return Expression
 */
public MathMapping.KFluxParameter getFluxCorrectionParameter(MembraneMapping membraneMapping, Feature feature) throws MappingException, ExpressionException {
	for (int i = 0; i < fieldMathMappingParameters.length; i++){
		if (fieldMathMappingParameters[i] instanceof KFluxParameter){
			MathMapping.KFluxParameter kfluxParameter = (MathMapping.KFluxParameter)fieldMathMappingParameters[i];
			if (kfluxParameter.getMembraneMapping() == membraneMapping &&
				kfluxParameter.getFeature() == feature){
				return kfluxParameter;
			}
		}
		
	}
	throw new MappingException("KFluxParameter for membrane "+membraneMapping.getMembrane().getName()+" and feature " + feature.getName() + " not found");
}


/**
 * Substitutes appropriate variables for speciesContext bindings
 *
 * @return cbit.vcell.parser.Expression
 * @param origExp cbit.vcell.parser.Expression
 * @param structureMapping cbit.vcell.mapping.StructureMapping
 */
private Expression getIdentifierSubstitutions(Expression origExp, VCUnitDefinition desiredExpUnitDef, StructureMapping structureMapping) throws ExpressionException, MappingException {
	String symbols[] = origExp.getSymbols(null);
	if (symbols == null){
		return origExp;
	}
	VCUnitDefinition expUnitDef = null;
	try {
		expUnitDef = VCUnitEvaluator.getUnitDefinition(origExp);
		if (desiredExpUnitDef == null){
			System.out.println("...........exp='"+origExp.infix()+"', desiredUnits are null");
			issueList.add(new cbit.util.Issue(origExp, "Units","expected=[null], observed=["+expUnitDef.getSymbol()+"]",cbit.util.Issue.SEVERITY_WARNING));
		}else if (expUnitDef == null){
			System.out.println("...........exp='"+origExp.infix()+"', evaluated Units are null");
			issueList.add(new cbit.util.Issue(origExp, "Units","expected=["+desiredExpUnitDef.getSymbol()+"], observed=[null]",cbit.util.Issue.SEVERITY_WARNING));
		}else if (desiredExpUnitDef.isTBD()){
			System.out.println("...........exp='"+origExp.infix()+"', desiredUnits are ["+desiredExpUnitDef.getSymbol()+"] and expression units are ["+expUnitDef.getSymbol()+"]");
			issueList.add(new cbit.util.Issue(origExp, "Units","expected=["+desiredExpUnitDef.getSymbol()+"], observed=["+expUnitDef.getSymbol()+"] for exp = "+origExp.infix(),cbit.util.Issue.SEVERITY_WARNING));
		}else if (!desiredExpUnitDef.compareEqual(expUnitDef) && !expUnitDef.isTBD()){
			System.out.println("...........exp='"+origExp.infix()+"', desiredUnits are ["+desiredExpUnitDef.getSymbol()+"] and expression units are ["+expUnitDef.getSymbol()+"]");
			issueList.add(new cbit.util.Issue(origExp, "Units","expected=["+desiredExpUnitDef.getSymbol()+"], observed=["+expUnitDef.getSymbol()+"] for exp = "+origExp.infix(),cbit.util.Issue.SEVERITY_WARNING));
		}
	}catch (VCUnitException e){
		System.out.println(".........exp='"+origExp.infix()+"' exception='"+e.getMessage()+"'");
		issueList.add(new cbit.util.Issue(origExp, "Units","expected=["+((desiredExpUnitDef!=null)?(desiredExpUnitDef.getSymbol()):("null"))+"], exception="+e.getMessage(),cbit.util.Issue.SEVERITY_WARNING));
	}catch (ExpressionException e){
		System.out.println(".........exp='"+origExp.infix()+"' exception='"+e.getMessage()+"'");
		issueList.add(new cbit.util.Issue(origExp, "Units","expected=["+((desiredExpUnitDef!=null)?(desiredExpUnitDef.getSymbol()):("null"))+"], exception="+e.getMessage(),cbit.util.Issue.SEVERITY_WARNING));
	}catch (Exception e){
		e.printStackTrace(System.out);
		issueList.add(new cbit.util.Issue(origExp, "Units","expected=["+((desiredExpUnitDef!=null)?(desiredExpUnitDef.getSymbol()):("null"))+"], exception="+e.getMessage(),cbit.util.Issue.SEVERITY_WARNING));
	}
	Expression newExp = new Expression(origExp);
	for (int i=0;i<symbols.length;i++){
		SymbolTableEntry ste = origExp.getSymbolBinding(symbols[i]);
		
		if (ste == null){
			throw new ExpressionBindingException("symbol '"+symbols[i]+"' not bound");
			//ste = simContext.getGeometryContext().getModel().getSpeciesContext(symbols[i]);
		}
		
		if (ste != null){
			String newName = getMathSymbol(ste,structureMapping);
			if (!newName.equals(symbols[i])){
				newExp.substituteInPlace(new Expression(symbols[i]),new Expression(newName));
			}
		}
	}
	return newExp;
}


/**
 * This method was created in VisualAge.
 * @return cbit.vcell.math.Function
 */
private static Expression getInsideFluxCorrectionExpression(SimulationContext simulationContext, MembraneMapping membraneMapping) throws ExpressionException {
	//
	//
	// use surfaceToVolumeRatio to get from membrane to total inside volume
	// then divide by 1-Sum(child volume fractions)
	//
	//
	FeatureMapping insideFeatureMapping = (FeatureMapping) simulationContext.getGeometryContext().getStructureMapping(membraneMapping.getMembrane().getInsideFeature());
	Expression insideResidualVolFraction = insideFeatureMapping.getResidualVolumeFraction(simulationContext);
	Expression exp = new Expression(simulationContext.getNameScope().getSymbolName(membraneMapping.getSurfaceToVolumeParameter()));
	exp = Expression.mult(exp,Expression.invert(insideResidualVolFraction));
	exp = exp.flatten();
	exp.bindExpression(simulationContext);
	return exp;
}


/**
 * Insert the method's description here.
 * Creation date: (11/8/2004 3:36:31 PM)
 * @return cbit.util.Issue
 */
public cbit.util.Issue[] getIssues() {
	return (cbit.util.Issue[])cbit.util.BeanUtils.getArray(issueList,cbit.util.Issue.class);
}


/**
 * Insert the method's description here.
 * Creation date: (4/4/2004 1:01:22 AM)
 * @return cbit.vcell.parser.SymbolTableEntry
 * @param identifier java.lang.String
 */
public cbit.vcell.parser.SymbolTableEntry getLocalEntry(java.lang.String identifier) throws cbit.vcell.parser.ExpressionBindingException {

	//
	// the MathMapping "nameScope" is the union of the Model and SimContext namescopes (with the addition of any locally defined parameters)
	//

	
	//
	// try "truely" local first
	//
	SymbolTableEntry localSTE = null;	
	localSTE = ReservedSymbol.fromString(identifier);
	if (localSTE!=null){
		return localSTE;
	}else{
		localSTE = getMathMappingParameter(identifier);
	}

	//
	// try "model" next
	//
	SymbolTableEntry modelSTE = simContext.getModel().getLocalEntry(identifier);
	
	//
	// try "simContext" next
	//
	SymbolTableEntry simContextSTE = simContext.getLocalEntry(identifier);

	int resolutionCount = 0;
	SymbolTableEntry ste = null;
	if (localSTE!=null){ 
		resolutionCount++;
		ste = localSTE;
	}
	if (modelSTE!=null){
		resolutionCount++;
		ste = modelSTE;
	}
	if (simContextSTE!=null){
		resolutionCount++;
		ste = simContextSTE;
	}

	if (resolutionCount==0 || resolutionCount==1){
		return ste;
	}else if (resolutionCount == 2){
		if (localSTE!=null){
			if (modelSTE!=null){
				// local and model
				throw new ExpressionBindingException("identifier '"+identifier+"' ambiguous, resolved by MathMapping ("+localSTE+") and Model ("+modelSTE+")");
			}else{
				// local and simContext
				throw new ExpressionBindingException("identifier '"+identifier+"' ambiguous, resolved by MathMapping ("+localSTE+") and Application ("+simContextSTE+")");
			}
		}else{
			// model and simContext
			throw new ExpressionBindingException("identifier '"+identifier+"' ambiguous, resolved by Model ("+modelSTE+") and Application ("+simContextSTE+")");
		}
	}else if (resolutionCount == 3){
		// local and model and simContext
		throw new ExpressionBindingException("identifier '"+identifier+"' ambiguous, resolved by MathMapping ("+localSTE+") and Model ("+modelSTE+") and Application ("+simContextSTE+")");
	}	

	return null;
}


/**
 * This method was created in VisualAge.
 * @return cbit.vcell.math.MathDescription
 */
public MathDescription getMathDescription() throws MappingException, MathException, ExpressionException, ModelException {
	if (mathDesc==null){
		refresh();
	}
	return mathDesc;
}


/**
 * Gets the mathMappingParameters index property (cbit.vcell.mapping.MathMappingParameter) value.
 * @return The mathMappingParameters property value.
 * @param index The index value into the property array.
 */
public MathMappingParameter getMathMappingParameter(String argName) {
	for (int i = 0; i < fieldMathMappingParameters.length; i++){
		if (fieldMathMappingParameters[i].getName().equals(argName)){
			return fieldMathMappingParameters[i];
		}
	}
	return null;
}


/**
 * Gets the mathMappingParameters property (MathMappingParameter[]) value.
 * @return The mathMappingParameters property value.
 */
public MathMappingParameter[] getMathMappingParameters() {
	return fieldMathMappingParameters;
}


/**
 * Gets the mathMappingParameters index property (cbit.vcell.mapping.MathMappingParameter) value.
 * @return The mathMappingParameters property value.
 * @param index The index value into the property array.
 */
public MathMappingParameter getMathMappingParameters(int index) {
	return getMathMappingParameters()[index];
}


/**
 * Substitutes appropriate variables for speciesContext bindings
 *
 * @return cbit.vcell.parser.Expression
 * @param origExp cbit.vcell.parser.Expression
 * @param structureMapping cbit.vcell.mapping.StructureMapping
 */
public String getMathSymbol(SymbolTableEntry ste, StructureMapping structureMapping) throws MappingException {

	String mathSymbol = getMathSymbol0(ste,structureMapping);
	
	mathSymbolMapping.put(ste,mathSymbol);

	return mathSymbol;
}


/**
 * Substitutes appropriate variables for speciesContext bindings
 *
 * @return cbit.vcell.parser.Expression
 * @param origExp cbit.vcell.parser.Expression
 * @param structureMapping cbit.vcell.mapping.StructureMapping
 */
private String getMathSymbol0(SymbolTableEntry ste, StructureMapping structureMapping) throws MappingException {
	if (ste instanceof Kinetics.KineticsParameter){
		int count = 0;
		ReactionStep reactionSteps[] = simContext.getModel().getReactionSteps();
		for (int j = 0; j < reactionSteps.length; j++){
			if (reactionSteps[j].getKinetics().getKineticsParameter(ste.getName())!=null){
				count++;
			}
		}
		if (count>1 || ste.getName().equals("J")){
			return ste.getName()+"_"+ste.getNameScope().getName();
			//return getNameScope().getSymbolName(ste);
		}else{
			return ste.getName();
		}
	}
	if (ste instanceof MembraneVoltage){
		return ste.getName();
	}
	if (ste instanceof SpeciesContextSpec.SpeciesContextSpecParameter){
		SpeciesContextSpec.SpeciesContextSpecParameter scsParm = (SpeciesContextSpec.SpeciesContextSpecParameter)ste;
		if (scsParm.getRole()==SpeciesContextSpec.ROLE_InitialConcentration){
			return ((SpeciesContextSpec)(scsParm.getNameScope().getScopedSymbolTable())).getSpeciesContext().getName()+"_init";
		}
		if (scsParm.getRole()==SpeciesContextSpec.ROLE_DiffusionRate){
			return ((SpeciesContextSpec)(scsParm.getNameScope().getScopedSymbolTable())).getSpeciesContext().getName()+"_diffusionRate";
		}
	}
	if (ste instanceof ElectricalDevice.ElectricalDeviceParameter){
		ElectricalDevice.ElectricalDeviceParameter edParm = (ElectricalDevice.ElectricalDeviceParameter)ste;
		ElectricalDevice electricalDevice = (ElectricalDevice)edParm.getNameScope().getScopedSymbolTable();
		if (electricalDevice instanceof MembraneElectricalDevice){
			if (edParm.getRole()==ElectricalDevice.ROLE_TotalCurrentDensity){
				return "I_"+((MembraneElectricalDevice)electricalDevice).getMembraneMapping().getMembrane().getNameScope().getName();
			}
			if (edParm.getRole()==ElectricalDevice.ROLE_TransmembraneCurrentDensity){
				return "F_"+((MembraneElectricalDevice)electricalDevice).getMembraneMapping().getMembrane().getNameScope().getName();
			}
		//}else if (electricalDevice instanceof CurrentClampElectricalDevice) {
			//if (edParm.getRole()==ElectricalDevice.ROLE_TotalCurrentDensity){
				//return "I_"+((CurrentClampElectricalDevice)electricalDevice).getCurrentClampStimulus().getNameScope().getName();
			//}
			//if (edParm.getRole()==ElectricalDevice.ROLE_TransmembraneCurrentDensity){
				//return "F_"+((CurrentClampElectricalDevice)electricalDevice).getCurrentClampStimulus().getNameScope().getName();
			//}
		//}else if (electricalDevice instanceof VoltageClampElectricalDevice) {
			//if (edParm.getRole()==ElectricalDevice.ROLE_TotalCurrentDensity){
				//return "I_"+((VoltageClampElectricalDevice)electricalDevice).getVoltageClampStimulus().getNameScope().getName();
			//}
			//if (edParm.getRole()==ElectricalDevice.ROLE_TransmembraneCurrentDensity){
				//return "F_"+((VoltageClampElectricalDevice)electricalDevice).getVoltageClampStimulus().getNameScope().getName();
			//}
		}
	}
	if (ste instanceof ElectricalStimulus.ElectricalStimulusParameter){
		ElectricalStimulus.ElectricalStimulusParameter esParm = (ElectricalStimulus.ElectricalStimulusParameter)ste;
		ElectricalStimulus electricalStimulus = (ElectricalStimulus)esParm.getNameScope().getScopedSymbolTable();
		if (esParm.getRole()==ElectricalStimulus.ROLE_Current){
			return "I_"+electricalStimulus.getNameScope().getName();
		}
		if (esParm.getRole()==ElectricalDevice.ROLE_TransmembraneCurrentDensity){
			return "V_"+electricalStimulus.getNameScope().getName();
		}
	}
	if (ste instanceof StructureMapping.StructureMappingParameter){
		StructureMapping.StructureMappingParameter smParm = (StructureMapping.StructureMappingParameter)ste;
		Structure structure = ((StructureMapping)(smParm.getNameScope().getScopedSymbolTable())).getStructure();
		if (smParm.getRole()==StructureMapping.ROLE_VolumeFraction){
			return "VolFract_"+((Membrane)structure).getInsideFeature().getNameScope().getName();
		}
		if (smParm.getRole()==StructureMapping.ROLE_SurfaceToVolumeRatio){
			return "SurfToVol_"+structure.getNameScope().getName();
		}
		if (smParm.getRole()==StructureMapping.ROLE_InitialVoltage){
			return smParm.getName();
		}
		if (smParm.getRole()==StructureMapping.ROLE_SpecificCapacitance){
			return "C_"+structure.getNameScope().getName();
		}
	}
	//
	// substitute Variable or Function if a SpeciesContext
	//
	if (ste instanceof SpeciesContext){
		SpeciesContext sc = (SpeciesContext)ste;
		SpeciesContextMapping scm = getSpeciesContextMapping(sc);
		//
		// for reactions within a feature
		//
		if (structureMapping instanceof FeatureMapping){
			//
			// for any SpeciesContext, replace Symbol name with Variable name
			//				
			if (scm.getVariable()!=null && !scm.getVariable().getName().equals(ste.getName())){
				return scm.getVariable().getName();
			}
		//
		// for reactions within a spatially resolved membrane, may need "_INSIDE" or "_OUTSIDE" for jump condition
		//
		// if the membrane is distributed, then always use the plain variable.
		//
		}else if (structureMapping instanceof MembraneMapping){
			Membrane membrane = ((MembraneMapping)structureMapping).getMembrane();
			//
			// if the speciesContext is also within the membrane, replace SpeciesContext name with Variable name
			//
			if (sc.getStructure() instanceof Membrane || ((MembraneMapping)structureMapping).getResolved(simContext)==false){
				if (scm.getVariable()!=null && !(scm.getVariable().getName().equals(ste.getName()))){
					return scm.getVariable().getName();
				}
			//
			// if the speciesContext is outside the membrane
			//
			} else {
				SpeciesContextSpec scs = simContext.getReactionContext().getSpeciesContextSpec(sc);
				if (sc.getStructure()==membrane.getInsideFeature()){
					if (((MembraneMapping)structureMapping).getResolved(simContext) && !scs.isConstant()){
						if (!scs.isDiffusing()){
							throw new MappingException("species '"+sc.getName()+"' ('"+sc.getSpecies().getCommonName()+"' in structure '"+sc.getStructure().getName()+"') interacts"+
														"\n  with the spatially resolved membrane '"+membrane.getName()+"'"+
														"\n  which results in a flux, so it must diffuse."+
														"\n"+
														"\nenable diffusion and set a non-zero diffusion rate for species '"+sc.getName()+"'"+
														"\n or disable those reaction(s)");
						}
						return scm.getVariable().getName()+"_INSIDE";
					}else{
						return scm.getSpeciesContext().getName();
					}
				}else if (sc.getStructure()==membrane.getOutsideFeature()){
					if (((MembraneMapping)structureMapping).getResolved(simContext) && !scs.isConstant()){
						if (!scs.isDiffusing()){
							throw new MappingException("species '"+sc.getName()+"' ('"+sc.getSpecies().getCommonName()+"' in structure '"+sc.getStructure().getName()+"') interacts"+
														"\n  with the spatially resolved membrane '"+membrane.getName()+"'"+
														"\n  which results in a flux, so it must diffuse."+
														"\n"+
														"\nenable diffusion and set a non-zero diffusion rate for species '"+sc.getName()+"'"+
														"\n or disable those reaction(s)");
						}
						return scm.getVariable().getName()+"_OUTSIDE";
					}else{
						return scm.getSpeciesContext().getName();
					}
				}else{
					throw new MappingException(sc.getName()+" shouldn't be involved with structure "+structureMapping.getStructure().getName());
				}
			}
		}
	}
	return getNameScope().getSymbolName(ste);
}


/**
 * Insert the method's description here.
 * Creation date: (5/3/2006 4:28:57 PM)
 * @return cbit.vcell.mapping.MathSymbolMapping
 */
public MathSymbolMapping getMathSymbolMapping()  throws MappingException, MathException, ExpressionException, ModelException {
	
	mathSymbolMapping.reconcileVarNames(getMathDescription());
	
	return mathSymbolMapping;
}


/**
 * This method was created in VisualAge.
 * @return cbit.vcell.mapping.MembraneStructureAnalyzer
 * @param membrane cbit.vcell.model.Membrane
 */
private MembraneStructureAnalyzer getMembraneStructureAnalyzer(Membrane membrane) {
	Enumeration enum1 = getStructureAnalyzers();
	while (enum1.hasMoreElements()){
		StructureAnalyzer sa = (StructureAnalyzer)enum1.nextElement();
		if (sa instanceof MembraneStructureAnalyzer){
			MembraneStructureAnalyzer msa = (MembraneStructureAnalyzer)sa;
			if (msa.getMembrane()==membrane){
				return msa;
			}
		}
	}
	return null;
}


/**
 * Insert the method's description here.
 * Creation date: (4/4/2004 1:01:22 AM)
 * @return cbit.vcell.parser.NameScope
 */
public cbit.vcell.parser.NameScope getNameScope() {
	return nameScope;
}


/**
 * This method was created in VisualAge.
 * @return cbit.vcell.math.Function
 */
private static Expression getOutsideFluxCorrectionExpression(SimulationContext simulationContext, MembraneMapping membraneMapping) throws ExpressionException {
	//
	// ?????? only works for 1 distributed organelle
	//
	FeatureMapping outsideFeatureMapping = (FeatureMapping) simulationContext.getGeometryContext().getStructureMapping(membraneMapping.getMembrane().getOutsideFeature());
	Expression outsideVolFraction = outsideFeatureMapping.getResidualVolumeFraction(simulationContext);
	Expression exp = new Expression(simulationContext.getNameScope().getSymbolName(membraneMapping.getSurfaceToVolumeParameter()) + "*" + 
									simulationContext.getNameScope().getSymbolName(membraneMapping.getVolumeFractionParameter()) + "/" + 
									"(" + outsideVolFraction.infix() + ");");
	return exp;
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
 * Find the defined variable (VolVariable) for the resolved compartments.
 * This method is used to assure only one variable is created.
 * @return cbit.vcell.math.VolVariable
 * @param species cbit.vcell.model.Species
 */
private VolVariable getResolvedVolVariable(Species species) {
	Enumeration enum1 = getSpeciesContextMappings();
	while (enum1.hasMoreElements()){
		SpeciesContextMapping scm = (SpeciesContextMapping)enum1.nextElement();
		if (scm.getSpeciesContext().getSpecies()==species){
			Variable var = scm.getVariable();
			if (var instanceof VolVariable){
				Structure structure = scm.getSpeciesContext().getStructure();
				StructureMapping sm = simContext.getGeometryContext().getStructureMapping(structure);
				if (structure instanceof Feature && ((FeatureMapping)sm).getResolved()){
					return (VolVariable)var;
				}
			}
		}
	}
	return new VolVariable(species.getCommonName());
}


/**
 * Insert the method's description here.
 * Creation date: (4/4/01 12:19:27 PM)
 * @return cbit.vcell.mapping.SimulationContext
 */
public SimulationContext getSimulationContext() {
	return simContext;
}


/**
 * This method was created in VisualAge.
 * @return cbit.vcell.mapping.SpeciesContextMapping
 * @param speciesContext SpeciesContext
 */
public SpeciesContextMapping getSpeciesContextMapping(SpeciesContext speciesContext) {
	Enumeration enum1 = getSpeciesContextMappings();
	while (enum1.hasMoreElements()){
		SpeciesContextMapping scm = (SpeciesContextMapping)enum1.nextElement();
		if (scm.getSpeciesContext()==speciesContext){
			return scm;
		}
	}
	return null;
}


/**
 * This method was created in VisualAge.
 * @return java.util.Enumeration
 */
private Enumeration getSpeciesContextMappings() {
	return speciesContextMappingList.elements();
}


/**
 * This method was created by a SmartGuide.
 * @return java.util.Enumeration
 */
public java.util.Enumeration getStructureAnalyzers() {
	return structureAnalyzerList.elements();
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
 * This method was created in VisualAge.
 * @return cbit.vcell.mapping.StructureAnalyzer
 * @param subVolume cbit.vcell.geometry.SubVolume
 */
private VolumeStructureAnalyzer getVolumeStructureAnalyzer(SubVolume subVolume) {
	Enumeration enum1 = getStructureAnalyzers();
	while (enum1.hasMoreElements()){
		StructureAnalyzer sa = (StructureAnalyzer)enum1.nextElement();
		if (sa instanceof VolumeStructureAnalyzer){
			VolumeStructureAnalyzer vsa = (VolumeStructureAnalyzer)sa;
			if (vsa.getSubVolume()==subVolume){
				return vsa;
			}
		}
	}
	return null;
}


/**
 * The hasListeners method was generated to support the propertyChange field.
 */
public synchronized boolean hasListeners(java.lang.String propertyName) {
	return getPropertyChange().hasListeners(propertyName);
}


/**
 * Insert the method's description here.
 * Creation date: (8/8/01 3:46:23 PM)
 * @return boolean
 * @param speciesContext cbit.vcell.model.SpeciesContext
 */
private boolean isDiffusionRequired(SpeciesContext speciesContext) {
	//
	// compartmental models never need diffusion
	//
	if (simContext.getGeometryContext().getGeometry().getDimension() == 0){
		return false;
	}

	//
	// if speciesContext is from a structure which is not spatially resolved, then it won't diffuse (PDE not required).
	//
	StructureMapping sm = simContext.getGeometryContext().getStructureMapping(speciesContext.getStructure());
	if (sm instanceof FeatureMapping){
		if (!((FeatureMapping)sm).getResolved()){
			return false;
		}
	} else if (sm instanceof MembraneMapping){
		if (!((MembraneMapping)sm).getResolved(simContext)){
			return false;
		}
	} else {
		return false;	// not Feature and not Membrane ... can't diffuse now.
	}

	//
	// check if any resolved speciesContext from the same species needs diffusion
	//
	boolean bDiffusionNeeded = false;
	SpeciesContext speciesContexts[] = simContext.getGeometryContext().getModel().getSpeciesContexts();
	for (int i = 0; i < speciesContexts.length; i++){
		if (speciesContexts[i].getSpecies().compareEqual(speciesContext.getSpecies())){
			StructureMapping otherSM = simContext.getGeometryContext().getStructureMapping(speciesContexts[i].getStructure());
			SpeciesContextSpec otherSCS = simContext.getReactionContext().getSpeciesContextSpec(speciesContexts[i]);
			//
			// another speciesContext needs diffusion if it is from a spatially resolved structure and has non-zero diffusion
			//
			if (otherSM instanceof FeatureMapping && ((FeatureMapping)otherSM).getResolved() && otherSCS.isDiffusing()){
				bDiffusionNeeded = true;
			}
			if (otherSM instanceof MembraneMapping && ((MembraneMapping)otherSM).getResolved(simContext) && otherSCS.isDiffusing()){
				bDiffusionNeeded = true;
			}
		}
	}
	
	//
	// if this speciesContextSpec specifically disables diffusion, but is required elsewhere to make "global" PDE work,
	// then tell it that diffusion is required and give it a zero diffusion rate.
	//
	SpeciesContextSpec scs = simContext.getReactionContext().getSpeciesContextSpec(speciesContext);
	if (bDiffusionNeeded){
		System.out.println("WARNING: MathMapping.isDiffusionRequired("+speciesContext+"), diffusion is disabled for "+speciesContext+" but needed for resolved species "+speciesContext.getSpecies());
	}

	return bDiffusionNeeded;
}


/**
 * Insert the method's description here.
 * Creation date: (11/2/2005 4:42:01 PM)
 * @return cbit.vcell.math.Variable
 * @param name java.lang.String
 * @param exp cbit.vcell.parser.Expression
 */
private Variable newFunctionOrConstant(String name, Expression exp) {
	if (exp.isNumeric()){
		return new Constant(name,exp);
	}else{
		return new Function(name,exp);
	}
}


/**
 * This method was created in VisualAge.
 * @param obs java.util.Observable
 * @param obj java.lang.Object
 */
private void refresh() throws MappingException, ExpressionException, MathException, ModelException {
//System.out.println("MathMapping.refresh()");
	issueList.clear();
	refreshKFluxParameters();
	refreshSpeciesContextMappings();
	refreshStructureAnalyzers();
	refreshVariables();
	refreshMathDescription();
}


/**
 * Insert the method's description here.
 * Creation date: (6/13/2004 8:10:34 AM)
 */
private void refreshKFluxParameters() throws ExpressionException {
	
	MathMapping.MathMappingParameter[] newMathMappingParameters = (MathMappingParameter[])fieldMathMappingParameters.clone();
	//
	// Remove existing KFlux Parameters
	//
	for (int i = 0; i < newMathMappingParameters.length; i++){
		if (newMathMappingParameters[i].getRole() == PARAMETER_ROLE_KFLUX){
			newMathMappingParameters = (MathMappingParameter[])cbit.util.BeanUtils.removeElement(newMathMappingParameters,newMathMappingParameters[i]);
		}		
	}
	
	
	//
	// Add new KFlux Parameters
	//
	StructureMapping structureMappings[] = simContext.getGeometryContext().getStructureMappings();
	for (int i = 0; i < structureMappings.length; i++){
		if (structureMappings[i] instanceof MembraneMapping && !((MembraneMapping)structureMappings[i]).getResolved(simContext)){
			MembraneMapping membraneMapping = (MembraneMapping)structureMappings[i];
			//
			// add "inside" flux correction
			//
			Expression insideCorrectionExp = getInsideFluxCorrectionExpression(simContext,membraneMapping);
			insideCorrectionExp.bindExpression(this);
			Feature insideFeature = membraneMapping.getMembrane().getInsideFeature();
			String insideName = "KFlux_"+membraneMapping.getNameScope().getName()+"_"+insideFeature.getNameScope().getName();
			KFluxParameter insideKFluxParameter = new KFluxParameter(insideName,insideCorrectionExp,VCUnitDefinition.UNIT_per_um,membraneMapping,insideFeature);
			newMathMappingParameters = (MathMappingParameter[])cbit.util.BeanUtils.addElement(newMathMappingParameters,insideKFluxParameter);

			//
			// add "outside" flux correction
			//
			Expression outsideCorrectionExp = getOutsideFluxCorrectionExpression(simContext,membraneMapping);
			outsideCorrectionExp.bindExpression(this);
			Feature outsideFeature = membraneMapping.getMembrane().getOutsideFeature();
			String outsideName = "KFlux_"+membraneMapping.getNameScope().getName()+"_"+outsideFeature.getNameScope().getName();
			KFluxParameter outsideKFluxParameter = new KFluxParameter(outsideName,outsideCorrectionExp,VCUnitDefinition.UNIT_per_um,membraneMapping,outsideFeature);
			newMathMappingParameters = (MathMappingParameter[])cbit.util.BeanUtils.addElement(newMathMappingParameters,outsideKFluxParameter);
		}
	}
	try {
		setMathMapppingParameters(newMathMappingParameters);
	}catch (java.beans.PropertyVetoException e){
		e.printStackTrace(System.out);
		throw new RuntimeException(e.getMessage());
	}
}


/**
 * This method was created in VisualAge.
 */
private void refreshMathDescription() throws MappingException, MathException, ExpressionException, ModelException {

//System.out.println("MathMapping.refreshMathDescription()");
	//
	// verify that all structures are mapped to subvolumes and all subvolumes are mapped to a structure
	//
	Structure structures[] = simContext.getGeometryContext().getModel().getStructures();
	for (int i = 0; i < structures.length; i++){
		StructureMapping sm = simContext.getGeometryContext().getStructureMapping(structures[i]);
		if (sm==null || (sm instanceof FeatureMapping && ((FeatureMapping)sm).getSubVolume() == null)){
			throw new MappingException("model structure '"+structures[i].getName()+"' not mapped to a geometry subVolume");
		}
		if (sm!=null && (sm instanceof MembraneMapping) && ((MembraneMapping)sm).getVolumeFractionParameter()!=null){
			Expression volFractExp = ((MembraneMapping)sm).getVolumeFractionParameter().getExpression();
			try {
				double volFract = volFractExp.evaluateConstant();
				if (volFract>=1.0){
					throw new MappingException("model structure '"+((MembraneMapping)sm).getMembrane().getInsideFeature().getName()+"' has volume fraction >= 1.0");
				}
			}catch (ExpressionException e){
			}
		}
	}
	SubVolume subVolumes[] = simContext.getGeometryContext().getGeometry().getGeometrySpec().getSubVolumes();
	for (int i = 0; i < subVolumes.length; i++){
		if (simContext.getGeometryContext().getStructures(subVolumes[i])==null || simContext.getGeometryContext().getStructures(subVolumes[i]).length==0){
			throw new MappingException("geometry subVolume '"+subVolumes[i].getName()+"' not mapped from a model structure");
		}
	}
	//
	// gather only those reactionSteps that are not "excluded"
	//
	ReactionSpec reactionSpecs[] = simContext.getReactionContext().getReactionSpecs();
	Vector rsList = new Vector();
	for (int i = 0; i < reactionSpecs.length; i++){
		if (reactionSpecs[i].isExcluded()==false){
			rsList.add(reactionSpecs[i].getReactionStep());
		}
	}
	ReactionStep reactionSteps[] = new ReactionStep[rsList.size()];
	rsList.copyInto(reactionSteps);

	//
	// fail if any unresolved parameters
	//
	for (int i = 0; i < reactionSteps.length; i++){
		Kinetics.UnresolvedParameter unresolvedParameters[] = reactionSteps[i].getKinetics().getUnresolvedParameters();
		if (unresolvedParameters!=null && unresolvedParameters.length>0){
			StringBuffer buffer = new StringBuffer();
			for (int j = 0; j < unresolvedParameters.length; j++){
				if (j>0){
					buffer.append(", ");
				}
				buffer.append(unresolvedParameters[j].getName());
			}
			throw new MappingException(reactionSteps[i].getTerm()+" '"+reactionSteps[i].getName()+"' contains unresolved identifier(s): "+buffer);
		}
	}
	
	//
	// create new MathDescription (based on simContext's previous MathDescription if possible)
	//
	MathDescription oldMathDesc = simContext.getMathDescription();
	mathDesc = null;
	if (oldMathDesc != null){
		if (oldMathDesc.getVersion() != null){
			mathDesc = new MathDescription(oldMathDesc.getVersion());
		}else{
			mathDesc = new MathDescription(oldMathDesc.getName());
		}
	}else{
		mathDesc = new MathDescription(simContext.getName()+"_generated");
	}

	//
	// temporarily place all variables in a hashtable (before binding) and discarding duplicates (check for equality)
	//
	VariableHash varHash = new VariableHash();
	
	//
	// volume variables
	//
	Enumeration enum1 = getSpeciesContextMappings();
	while (enum1.hasMoreElements()){
		SpeciesContextMapping scm = (SpeciesContextMapping)enum1.nextElement();
		if (scm.getVariable() instanceof VolVariable){
			if (!(mathDesc.getVariable(scm.getVariable().getName()) instanceof VolVariable)){
				varHash.addVariable(scm.getVariable());
			}
		}
	}
	//
	// membrane variables
	//
	enum1 = getSpeciesContextMappings();
	while (enum1.hasMoreElements()){
		SpeciesContextMapping scm = (SpeciesContextMapping)enum1.nextElement();
		if (scm.getVariable() instanceof MemVariable){
			varHash.addVariable(scm.getVariable());
		}
	}

	varHash.addVariable(new Constant(getMathSymbol(ReservedSymbol.FARADAY_CONSTANT,null),getIdentifierSubstitutions(ReservedSymbol.FARADAY_CONSTANT.getExpression(),ReservedSymbol.FARADAY_CONSTANT.getUnitDefinition(),null)));
	varHash.addVariable(new Constant(getMathSymbol(ReservedSymbol.FARADAY_CONSTANT_NMOLE,null),getIdentifierSubstitutions(ReservedSymbol.FARADAY_CONSTANT_NMOLE.getExpression(),ReservedSymbol.FARADAY_CONSTANT_NMOLE.getUnitDefinition(),null)));
	varHash.addVariable(new Constant(getMathSymbol(ReservedSymbol.GAS_CONSTANT,null),getIdentifierSubstitutions(ReservedSymbol.GAS_CONSTANT.getExpression(),ReservedSymbol.GAS_CONSTANT.getUnitDefinition(),null)));
	varHash.addVariable(new Constant(getMathSymbol(ReservedSymbol.TEMPERATURE,null),getIdentifierSubstitutions(new Expression(simContext.getTemperatureKelvin()),cbit.vcell.units.VCUnitDefinition.UNIT_K,null)));

	//
	// only calculate potential if at least one MembraneMapping has CalculateVoltage == true
	//
	boolean bCalculatePotential = false;
	StructureMapping structureMappings[] = simContext.getGeometryContext().getStructureMappings();
	for (int i = 0; i < structureMappings.length; i++){
		if (structureMappings[i] instanceof MembraneMapping){
			if (((MembraneMapping)structureMappings[i]).getCalculateVoltage()){
				bCalculatePotential = true;
			}
		}
		
	}
	//(simContext.getGeometry().getDimension() == 0);
	potentialMapping = new PotentialMapping(simContext,this);
	try {
		potentialMapping.computeMath();
	} catch (MatrixException e1) {
		e1.printStackTrace();
		throw new MappingException(e1.getMessage());
	}
	
	if (bCalculatePotential){
		//
		// copy functions for currents and constants for capacitances
		//
		ElectricalDevice devices[] = potentialMapping.getElectricalDevices();
		for (int j = 0; j < devices.length; j++){

			
			if (devices[j] instanceof MembraneElectricalDevice){
				MembraneElectricalDevice membraneElectricalDevice = (MembraneElectricalDevice)devices[j];
				MembraneMapping memMapping = membraneElectricalDevice.getMembraneMapping();
				Parameter specificCapacitanceParm = memMapping.getParameterFromRole(MembraneMapping.ROLE_SpecificCapacitance);
				varHash.addVariable(new Constant(getMathSymbol(specificCapacitanceParm,memMapping),getIdentifierSubstitutions(specificCapacitanceParm.getExpression(),specificCapacitanceParm.getUnitDefinition(),memMapping)));

				ElectricalDevice.ElectricalDeviceParameter transmembraneCurrentDensityParm = membraneElectricalDevice.getParameterFromRole(ElectricalDevice.ROLE_TransmembraneCurrentDensity);
				ElectricalDevice.ElectricalDeviceParameter totalCurrentDensityParm = membraneElectricalDevice.getParameterFromRole(ElectricalDevice.ROLE_TotalCurrentDensity);

				if (totalCurrentDensityParm!=null && /* totalCurrentDensityParm.getExpression()!=null && */ memMapping.getCalculateVoltage()){
					Expression totalCurrentDensityExp = (totalCurrentDensityParm.getExpression()!=null)?(totalCurrentDensityParm.getExpression()):(new Expression(0.0));
					varHash.addVariable(newFunctionOrConstant(getMathSymbol(totalCurrentDensityParm,membraneElectricalDevice.getMembraneMapping()),
													getIdentifierSubstitutions(totalCurrentDensityExp,totalCurrentDensityParm.getUnitDefinition(),membraneElectricalDevice.getMembraneMapping())));
				}
				if (transmembraneCurrentDensityParm!=null && transmembraneCurrentDensityParm.getExpression()!=null && memMapping.getCalculateVoltage()){
					varHash.addVariable(newFunctionOrConstant(getMathSymbol(transmembraneCurrentDensityParm,membraneElectricalDevice.getMembraneMapping()),
													getIdentifierSubstitutions(transmembraneCurrentDensityParm.getExpression(),transmembraneCurrentDensityParm.getUnitDefinition(),membraneElectricalDevice.getMembraneMapping())));
				}
				//		
				//
				// membrane ode
				//
				if (membraneElectricalDevice.getDependentVoltageExpression()==null){  // is Voltage Independent?
					StructureMapping.StructureMappingParameter initialVoltageParm = memMapping.getInitialVoltageParameter();
					varHash.addVariable(newFunctionOrConstant(getMathSymbol(initialVoltageParm,memMapping),
													getIdentifierSubstitutions(initialVoltageParm.getExpression(),initialVoltageParm.getUnitDefinition(),memMapping)));
				}
				//
				// membrane forced potential
				//
				else {
					varHash.addVariable(newFunctionOrConstant(getMathSymbol(memMapping.getMembrane().getMembraneVoltage(),memMapping),
													getIdentifierSubstitutions(membraneElectricalDevice.getDependentVoltageExpression(),memMapping.getMembrane().getMembraneVoltage().getUnitDefinition(),memMapping)));
				}
			}else if (devices[j] instanceof CurrentClampElectricalDevice){
				CurrentClampElectricalDevice currentClampDevice = (CurrentClampElectricalDevice)devices[j];
				// total current = current source (no capacitance)
				Parameter totalCurrentDensityParm = currentClampDevice.getParameterFromRole(currentClampDevice.ROLE_TotalCurrentDensity);
				Parameter currentDensityParm = currentClampDevice.getParameterFromRole(currentClampDevice.ROLE_TransmembraneCurrentDensity);
				//Parameter dependentVoltage = currentClampDevice.getCurrentClampStimulus().getVoltageParameter();
				
				varHash.addVariable(newFunctionOrConstant(getMathSymbol(totalCurrentDensityParm,null),getIdentifierSubstitutions(totalCurrentDensityParm.getExpression(),totalCurrentDensityParm.getUnitDefinition(),null)));
				varHash.addVariable(newFunctionOrConstant(getMathSymbol(currentDensityParm,null),getIdentifierSubstitutions(currentDensityParm.getExpression(),currentDensityParm.getUnitDefinition(),null)));
				//varHash.addVariable(newFunctionOrConstant(getMathSymbol(dependentVoltage,null),getIdentifierSubstitutions(currentClampDevice.getDependentVoltageExpression(),dependentVoltage.getUnitDefinition(),null)));

				//
				// add user-defined parameters
				//
				ElectricalDevice.ElectricalDeviceParameter[] parameters = currentClampDevice.getParameters();
				for (int k = 0; k < parameters.length; k++){
					varHash.addVariable(newFunctionOrConstant(getMathSymbol(parameters[k],null),getIdentifierSubstitutions(parameters[k].getExpression(),parameters[k].getUnitDefinition(),null)));
				}
			}else if (devices[j] instanceof VoltageClampElectricalDevice){
				VoltageClampElectricalDevice voltageClampDevice = (VoltageClampElectricalDevice)devices[j];
				// total current = current source (no capacitance)
				Parameter totalCurrent = voltageClampDevice.getParameterFromRole(voltageClampDevice.ROLE_TotalCurrentDensity);
				Parameter totalCurrentDensityParm = voltageClampDevice.getParameterFromRole(voltageClampDevice.ROLE_TotalCurrentDensity);
				Parameter voltageParm = voltageClampDevice.getParameterFromRole(voltageClampDevice.ROLE_Voltage);
				
				varHash.addVariable(newFunctionOrConstant(getMathSymbol(totalCurrent,null),getIdentifierSubstitutions(totalCurrent.getExpression(),totalCurrent.getUnitDefinition(),null)));
				varHash.addVariable(newFunctionOrConstant(getMathSymbol(totalCurrentDensityParm,null),getIdentifierSubstitutions(totalCurrentDensityParm.getExpression(),totalCurrentDensityParm.getUnitDefinition(),null)));
				varHash.addVariable(newFunctionOrConstant(getMathSymbol(voltageParm,null),getIdentifierSubstitutions(voltageParm.getExpression(),voltageParm.getUnitDefinition(),null)));

				//
				// add user-defined parameters
				//
				ElectricalDevice.ElectricalDeviceParameter[] parameters = voltageClampDevice.getParameters();
				for (int k = 0; k < parameters.length; k++){
					if (parameters[k].getRole() == ElectricalDevice.ROLE_UserDefined){
						varHash.addVariable(newFunctionOrConstant(getMathSymbol(parameters[k],null),getIdentifierSubstitutions(parameters[k].getExpression(),parameters[k].getUnitDefinition(),null)));
					}
				}
			}
		}
	} else {    // (bCalculatePotential == false) ... don't solve for any potentials, but still need them
		//
		// copy functions for currents and constants for capacitances
		//
		for (int j = 0; j < structureMappings.length; j++){
			if (structureMappings[j] instanceof MembraneMapping){
				MembraneMapping memMapping = (MembraneMapping)structureMappings[j];
				varHash.addVariable(newFunctionOrConstant(getMathSymbol(memMapping.getMembrane().getMembraneVoltage(),memMapping),getIdentifierSubstitutions(memMapping.getInitialVoltageParameter().getExpression(),memMapping.getInitialVoltageParameter().getUnitDefinition(),memMapping)));
			}
		}
	}
	//
	// add rate term for all reactions
	// add current source terms for each reaction step in a membrane
	//
	for (int i = 0; i < reactionSteps.length; i++){
		//
		// if "excluded" (or disabled) then go to next reaction
		//
		ReactionSpec rs = simContext.getReactionContext().getReactionSpec(reactionSteps[i]);
		if (rs.isExcluded()){
			continue;
		}
		boolean bAllReactionParticipantsFixed = true;
		ReactionParticipant rp_Array[] = reactionSteps[i].getReactionParticipants();
		for (int j = 0; j < rp_Array.length; j++) {
			SpeciesContextSpec scs = simContext.getReactionContext().getSpeciesContextSpec(rp_Array[j].getSpeciesContext());
			if (!(rp_Array[j] instanceof Catalyst) && !scs.isConstant()){
				bAllReactionParticipantsFixed = false;  // found at least one reactionParticipant that is not fixed and needs this rate
			}
		}
		StructureMapping sm = simContext.getGeometryContext().getStructureMapping(reactionSteps[i].getStructure());
		////
		//// add rate function (e.g.  J_Reaction1 = Kon*F - Koff*B)
		//// ...unless all reaction participants are "fixed"
		////
		//if (!bAllReactionParticipantsFixed){
			//Expression rateExp = getIdentifierSubstitutions(reactionSteps[i].getKinetics().getRateExpression(),sm);
			////
			//// if of the form ABC = ABC, then don't add this function
			//// 
			//// it must be a GeneralKinetics where the single "requiredParameter"
			//// is both the KineticRateName as well as the topLevel expression
			////
			//if (!(new Expression(reactionSteps[i].getKineticRateName()).equals(rateExp))){
				//varHash.addVariable(new cbit.vcell.math.Function(reactionSteps[i].getKineticRateName(),rateExp));
			//}
		//}
		////
		//// add current function (e.g.  I_Reaction1 = 1e-9*_F_*z*(Kon*F - Koff*B)  ) 
		//// ...unless equal to 0.0 (see that PotentialMapping.getTotalMembraneCurrent() doesn't include 0.0 terms)
		////
		//if (reactionSteps[i].getStructure() instanceof Membrane){
			//Membrane membrane = (Membrane)reactionSteps[i].getStructure();
			//Expression currentExp = getIdentifierSubstitutions(reactionSteps[i].getKinetics().getCurrentExpression(),sm);
			////
			//// if of the form ABC = ABC, then don't add this function
			//// 
			//// it must be a GeneralCurrentKinetics where the single "requiredParameter"
			//// is both the CurrentSourceName as well as the topLevel expression
			////
			//if (!(new Expression(reactionSteps[i].getCurrentSourceName()).equals(currentExp))){
				//if (!currentExp.compareEqual(new Expression(0.0))){
					//varHash.addVariable(new cbit.vcell.math.Function(reactionSteps[i].getCurrentSourceName(),currentExp));
				//}
			//}
		//}
	}
		
	//
	// add constants for R,F,T and Initial Voltages
	//
	for (int j=0;j<structureMappings.length;j++){
		if (structureMappings[j] instanceof MembraneMapping){
			MembraneMapping membraneMapping = (MembraneMapping)structureMappings[j];
			MembraneVoltage membraneVoltage = membraneMapping.getMembrane().getMembraneVoltage();
			ElectricalDevice membraneDevices[] = potentialMapping.getElectricalDevices(membraneMapping.getMembrane());
			ElectricalDevice membraneDevice = null;
			for (int i = 0; i < membraneDevices.length; i++){
				if (membraneDevices[i].hasCapacitance() && membraneDevices[i].getDependentVoltageExpression()==null){
					if (membraneMapping.getCalculateVoltage() && bCalculatePotential){
						if (membraneMapping.getResolved(simContext)){
							//
							// spatially resolved membrane, and must solve for potential .... 
							//   make single MembraneRegionVariable for all resolved potentials
							//
							if (mathDesc.getVariable(MembraneVoltage.MEMBRANE_VOLTAGE_REGION_NAME)==null){
								//varHash.addVariable(new MembraneRegionVariable(MembraneVoltage.MEMBRANE_VOLTAGE_REGION_NAME));
								varHash.addVariable(new MembraneRegionVariable(getMathSymbol(membraneVoltage,membraneMapping)));
							}
						}else{
							//
							// spatially unresolved membrane, and must solve for potential ... make VolVariable for this compartment
							//
							varHash.addVariable(new VolVariable(getMathSymbol(membraneVoltage,membraneMapping)));
						}
						Parameter initialVoltageParm = membraneMapping.getInitialVoltageParameter();
						Variable initVoltageFunction = newFunctionOrConstant(getMathSymbol(initialVoltageParm,membraneMapping),getIdentifierSubstitutions(initialVoltageParm.getExpression(),initialVoltageParm.getUnitDefinition(),membraneMapping));
						varHash.addVariable(initVoltageFunction);
					}else{
						//
						// don't calculate voltage, still may need it though
						//
						Parameter initialVoltageParm = membraneMapping.getInitialVoltageParameter();
						Variable voltageFunction = newFunctionOrConstant(getMathSymbol(membraneMapping.getMembrane().getMembraneVoltage(),membraneMapping),getIdentifierSubstitutions(initialVoltageParm.getExpression(),initialVoltageParm.getUnitDefinition(),membraneMapping));
						varHash.addVariable(voltageFunction);
					}
				}
			}
		}
	}
	
	
	//
	// kinetic constants that evaluate to constants
	//
	for (int j=0;j<reactionSteps.length;j++){
		ReactionStep rs = reactionSteps[j];
		Kinetics.KineticsParameter parameters[] = rs.getKinetics().getKineticsParameters();
		if (parameters != null){
			for (int i=0;i<parameters.length;i++){
				if (parameters[i].getRole() == Kinetics.ROLE_Current && (parameters[i].getExpression()==null || parameters[i].getExpression().isZero())){
					continue;
				}
				try {
					double value = parameters[i].getExpression().evaluateConstant();
					Constant constant = new Constant(getMathSymbol(parameters[i],null),new Expression(value));
					varHash.addVariable(constant);
				}catch (ExpressionException e){
				}
			}
		}
	}
	//
	// charge valences as constants
	//
	//for (int j=0;j<reactionSteps.length;j++){
		//ReactionStep rs = reactionSteps[j];
		//if (rs.getStructure() instanceof Membrane && rs.getChargeCarrierValence()!=null){
			////Kinetics.KineticsParameter currentParm = rs.getKinetics().getKineticsParameterFromRole(Kinetics.ROLE_Current);
			////if (currentParm!=null && currentParm.getExpression()!=null && !currentParm.getExpression().isZero()){
				//try {
					//double value = rs.getChargeCarrierValence().getExpression().evaluateConstant();
					//Constant constant = new Constant(getMathSymbol(rs.getChargeCarrierValence(),null),new Expression(value));
					//varHash.addVariable(constant);
				//}catch (ExpressionException e){
				//}
			////}
		//}
	//}
	//
	// kinetic constants that are functions of other constants and maybe variables (really need proper dependency analysis ... but quick and dirty)
	//
	for (int j=0;j<reactionSteps.length;j++){
		ReactionStep rs = reactionSteps[j];
		if (simContext.getReactionContext().getReactionSpec(rs).isExcluded()){
			continue;
		}
		Kinetics.KineticsParameter parameters[] = rs.getKinetics().getKineticsParameters();
		if (parameters != null){
			for (int i=0;i<parameters.length;i++){
				if (parameters[i].getRole() == Kinetics.ROLE_Current && (parameters[i].getExpression()==null || parameters[i].getExpression().isZero())){
					continue;
				}
				if (parameters[i].getRole() == Kinetics.ROLE_Rate && reactionSteps[j].getPhysicsOptions()==ReactionStep.PHYSICS_ELECTRICAL_ONLY){
					continue;
				}
				try {
					parameters[i].getExpression().evaluateConstant();
				}catch (ExpressionException e){
					StructureMapping sm = simContext.getGeometryContext().getStructureMapping(rs.getStructure());
					Expression exp = getIdentifierSubstitutions(parameters[i].getExpression(),parameters[i].getUnitDefinition(),sm);
					Function function = new Function(getMathSymbol(parameters[i],null),exp);
					varHash.addVariable(function);
				}
			}
		}
	}
	//
	// initial constants (either function or constant)
	//
	SpeciesContextSpec speciesContextSpecs[] = simContext.getReactionContext().getSpeciesContextSpecs();
	for (int i = 0; i < speciesContextSpecs.length; i++){
		SpeciesContextSpec.SpeciesContextSpecParameter initParm = speciesContextSpecs[i].getParameterFromRole(SpeciesContextSpec.ROLE_InitialConcentration);
		if (initParm!=null){
			try {
				initParm.getExpression().evaluateConstant();
				varHash.addVariable(new Constant(getMathSymbol(initParm,null),getIdentifierSubstitutions(initParm.getExpression(),initParm.getUnitDefinition(),null)));
			}catch (ExpressionException e){
				varHash.addVariable(new Function(getMathSymbol(initParm,null),getIdentifierSubstitutions(initParm.getExpression(),initParm.getUnitDefinition(),null)));
			}
		}
	}
	
	//
	// diffusion constants (either function or constant)
	//
	for (int i = 0; i < speciesContextSpecs.length; i++){
		SpeciesContextMapping scm = getSpeciesContextMapping(speciesContextSpecs[i].getSpeciesContext());
		SpeciesContextSpec.SpeciesContextSpecParameter diffParm = speciesContextSpecs[i].getParameterFromRole(SpeciesContextSpec.ROLE_DiffusionRate);
		if (diffParm!=null && scm.isDiffusing()){
			try {
				diffParm.getExpression().evaluateConstant();
				varHash.addVariable(new Constant(getMathSymbol(diffParm,null),getIdentifierSubstitutions(diffParm.getExpression(),diffParm.getUnitDefinition(),null)));
			}catch (ExpressionException e){
				varHash.addVariable(new Function(getMathSymbol(diffParm,null),getIdentifierSubstitutions(diffParm.getExpression(),diffParm.getUnitDefinition(),null)));
			}
		}
	}
	
	//
	// constant species (either function or constant)
	//
	enum1 = getSpeciesContextMappings();
	while (enum1.hasMoreElements()){
		SpeciesContextMapping scm = (SpeciesContextMapping)enum1.nextElement();
		if (scm.getVariable() instanceof Constant){
			varHash.addVariable(scm.getVariable());
		}
	}
	//
	// conversion factors
	//
	varHash.addVariable(new Constant(ReservedSymbol.KMOLE.getName(),getIdentifierSubstitutions(ReservedSymbol.KMOLE.getExpression(),ReservedSymbol.KMOLE.getUnitDefinition(),null)));
	varHash.addVariable(new Constant(ReservedSymbol.N_PMOLE.getName(),getIdentifierSubstitutions(ReservedSymbol.N_PMOLE.getExpression(),ReservedSymbol.N_PMOLE.getUnitDefinition(),null)));
	varHash.addVariable(new Constant(ReservedSymbol.KMILLIVOLTS.getName(),getIdentifierSubstitutions(ReservedSymbol.KMILLIVOLTS.getExpression(),ReservedSymbol.KMILLIVOLTS.getUnitDefinition(),null)));
	varHash.addVariable(new Constant(ReservedSymbol.K_GHK.getName(),getIdentifierSubstitutions(ReservedSymbol.K_GHK.getExpression(),ReservedSymbol.K_GHK.getUnitDefinition(),null)));
	//
	// geometric functions
	//
	for (int i=0;i<structureMappings.length;i++){
		StructureMapping sm = structureMappings[i];
		if (sm instanceof MembraneMapping && !(((MembraneMapping)sm).getResolved(simContext))){
			Parameter parm = ((MembraneMapping)sm).getVolumeFractionParameter();
			try {
				double value = parm.getExpression().evaluateConstant();
				varHash.addVariable(new Constant(getMathSymbol(parm,sm),new Expression(value)));
			}catch (ExpressionException e){
				varHash.addVariable(new Function(getMathSymbol(parm,sm),getIdentifierSubstitutions(parm.getExpression(),parm.getUnitDefinition(),sm)));
			}	
			parm = ((MembraneMapping)sm).getSurfaceToVolumeParameter();
			try {
				double value = parm.getExpression().evaluateConstant();
				varHash.addVariable(new Constant(getMathSymbol(parm,sm),new Expression(value)));
			}catch (ExpressionException e){
				varHash.addVariable(new Function(getMathSymbol(parm,sm),getIdentifierSubstitutions(parm.getExpression(),parm.getUnitDefinition(),sm)));
			}	
		}
	}
	//for (int i=0;i<structureMappings.length;i++){
		//StructureMapping sm = structureMappings[i];
		//if (sm instanceof MembraneMapping && !(((MembraneMapping)sm).getResolved(simContext))){
			//Parameter outsideFluxCorrectionParameter = ((MembraneMapping)sm).getOutsideFluxCorrectionParameter();
			//Parameter insideFluxCorrectionParameter = ((MembraneMapping)sm).getInsideFluxCorrectionParameter();
			//varHash.addVariable(new Function(getMathSymbol(outsideFluxCorrectionParameter,null),getIdentifierSubstitutions(outsideFluxCorrectionParameter.getExpression(),null)));
			//varHash.addVariable(new Function(getMathSymbol(insideFluxCorrectionParameter,null),getIdentifierSubstitutions(insideFluxCorrectionParameter.getExpression(),null)));
		//}
	//}
	////
	//// conserved constants  (e.g. K = A + B + C) (these are treated as functions now)
	////
	for (int i = 0; i < fieldMathMappingParameters.length; i++){
		varHash.addVariable(new Function(getMathSymbol(fieldMathMappingParameters[i],null),getIdentifierSubstitutions(fieldMathMappingParameters[i].getExpression(),fieldMathMappingParameters[i].getUnitDefinition(),null)));
	}

	//
	// functions
	//
	enum1 = getSpeciesContextMappings();
	while (enum1.hasMoreElements()){
		SpeciesContextMapping scm = (SpeciesContextMapping)enum1.nextElement();
		if (scm.getVariable()==null && scm.getDependencyExpression()!=null){
			StructureMapping sm = simContext.getGeometryContext().getStructureMapping(scm.getSpeciesContext().getStructure());
			Expression exp = getIdentifierSubstitutions(scm.getDependencyExpression(),scm.getSpeciesContext().getUnitDefinition(),sm);
			try {
				double value = exp.evaluateConstant();
				varHash.addVariable(new Constant(getMathSymbol(scm.getSpeciesContext(),sm),new Expression(value)));
			}catch (ExpressionException e){
				varHash.addVariable(new Function(getMathSymbol(scm.getSpeciesContext(),sm),exp));
			}
		}
	}

	//
	// set Variables to MathDescription all at once with the order resolved by "VariableHash"
	//
	mathDesc.setAllVariables(varHash.getReorderedVariables());
	
	//
	// geometry
	//
	if (simContext.getGeometryContext().getGeometry() != null){
		try {
			mathDesc.setGeometry(simContext.getGeometryContext().getGeometry());
		}catch (java.beans.PropertyVetoException e){
			e.printStackTrace(System.out);
			throw new MappingException("failure setting geometry "+e.getMessage());
		}
	}else{
		throw new MappingException("geometry must be defined");
	}

	//
	// volume subdomains
	//
	subVolumes = simContext.getGeometryContext().getGeometry().getGeometrySpec().getSubVolumes();
	for (int j=0;j<subVolumes.length;j++){
		SubVolume subVolume = (SubVolume)subVolumes[j];
		//
		// get priority of subDomain
		//
		int priority;
		Feature spatialFeature = simContext.getGeometryContext().getResolvedFeature(subVolume);
		if (spatialFeature==null){
			if (simContext.getGeometryContext().getGeometry().getDimension()>0){
				throw new MappingException("no compartment (in Physiology) is mapped to subdomain '"+subVolume.getName()+"' (in Geometry)");
			}else{
				priority = CompartmentSubDomain.NON_SPATIAL_PRIORITY;
			}
		}else{
			priority = spatialFeature.getPriority() * 100 + j; // now does not have to match spatial feature, *BUT* needs to be unique
		}
		//
		// create subDomain
		//
		CompartmentSubDomain subDomain = new CompartmentSubDomain(subVolume.getName(),priority);
		mathDesc.addSubDomain(subDomain);

		//
		// assign boundary condition types
		//
		if (spatialFeature != null){
			FeatureMapping fm = (FeatureMapping)simContext.getGeometryContext().getStructureMapping(spatialFeature);
			subDomain.setBoundaryConditionXm(fm.getBoundaryConditionTypeXm());
			subDomain.setBoundaryConditionXp(fm.getBoundaryConditionTypeXp());
			if (simContext.getGeometry().getDimension()>1){
				subDomain.setBoundaryConditionYm(fm.getBoundaryConditionTypeYm());
				subDomain.setBoundaryConditionYp(fm.getBoundaryConditionTypeYp());
			}
			if (simContext.getGeometry().getDimension()>2){
				subDomain.setBoundaryConditionZm(fm.getBoundaryConditionTypeZm());
				subDomain.setBoundaryConditionZp(fm.getBoundaryConditionTypeZp());
			}
		}

		//
		// create equations
		//
		VolumeStructureAnalyzer structureAnalyzer = getVolumeStructureAnalyzer(subVolume);
		Enumeration enumSCM = getSpeciesContextMappings();
		while (enumSCM.hasMoreElements()){
			SpeciesContextMapping scm = (SpeciesContextMapping)enumSCM.nextElement();
			//
			// if an independent volume variable, then create equation for it
			// ...even if the speciesContext is for another subDomain (e.g. CaCyt in Extracellular)
			//
			if (scm.getVariable() instanceof VolVariable && scm.getDependencyExpression()==null){
				SpeciesContext        sc  = scm.getSpeciesContext();
				StructureMapping      sm  = simContext.getGeometryContext().getStructureMapping(sc.getStructure());
				SpeciesContextSpec    scs = simContext.getReactionContext().getSpeciesContextSpec(sc);
				VolVariable variable = (VolVariable)scm.getVariable();
				Equation equation = null;
				if (scm.isDiffusing() && sm instanceof FeatureMapping){
					//
					// PDE
					//
					if (((FeatureMapping)sm).getSubVolume() == subVolume){
						//
						// species context belongs to this subDomain
						//
						Expression initial = new Expression(getMathSymbol(scs.getParameterFromRole(SpeciesContextSpec.ROLE_InitialConcentration),sm));
						Expression rate = getIdentifierSubstitutions(scm.getRate(),scm.getSpeciesContext().getUnitDefinition().divideBy(VCUnitDefinition.UNIT_s),simContext.getGeometryContext().getStructureMapping(sc.getStructure()));
						Expression diffusion = new Expression(getMathSymbol(scs.getDiffusionParameter(),sm));
						equation = new PdeEquation(variable,initial,rate,diffusion);
						((PdeEquation)equation).setBoundaryXm((scs.getBoundaryXmParameter().getExpression()==null)?(null):getIdentifierSubstitutions(scs.getBoundaryXmParameter().getExpression(),scs.getBoundaryXmParameter().getUnitDefinition(),sm));
						((PdeEquation)equation).setBoundaryXp((scs.getBoundaryXpParameter().getExpression()==null)?(null):getIdentifierSubstitutions(scs.getBoundaryXpParameter().getExpression(),scs.getBoundaryXpParameter().getUnitDefinition(),sm));
						((PdeEquation)equation).setBoundaryYm((scs.getBoundaryYmParameter().getExpression()==null)?(null):getIdentifierSubstitutions(scs.getBoundaryYmParameter().getExpression(),scs.getBoundaryYmParameter().getUnitDefinition(),sm));
						((PdeEquation)equation).setBoundaryYp((scs.getBoundaryYpParameter().getExpression()==null)?(null):getIdentifierSubstitutions(scs.getBoundaryYpParameter().getExpression(),scs.getBoundaryYpParameter().getUnitDefinition(),sm));
						((PdeEquation)equation).setBoundaryZm((scs.getBoundaryZmParameter().getExpression()==null)?(null):getIdentifierSubstitutions(scs.getBoundaryZmParameter().getExpression(),scs.getBoundaryZmParameter().getUnitDefinition(),sm));
						((PdeEquation)equation).setBoundaryZp((scs.getBoundaryZpParameter().getExpression()==null)?(null):getIdentifierSubstitutions(scs.getBoundaryZpParameter().getExpression(),scs.getBoundaryZpParameter().getUnitDefinition(),sm));
						subDomain.replaceEquation(equation);
					}else{
						Expression initial = new Expression(0.0);
						Expression rate = new Expression(0.0);
						Expression diffusion = new Expression(getMathSymbol(scs.getDiffusionParameter(),sm));
						equation = new PdeEquation(variable,initial,rate,diffusion);
						if (subDomain.getEquation(variable)==null){
							subDomain.addEquation(equation);
						}
					}
				}else{
					//
					// ODE
					//
					SubVolume mappedSubVolume = null;
					if (sm instanceof FeatureMapping){
						mappedSubVolume = ((FeatureMapping)sm).getSubVolume();
					}else if (sm instanceof MembraneMapping){
						// membrane is mapped to that of the inside feature
						FeatureMapping featureMapping = (FeatureMapping)simContext.getGeometryContext().getStructureMapping(((Membrane)sm.getStructure()).getInsideFeature());
						mappedSubVolume = featureMapping.getSubVolume();
					}
					if (mappedSubVolume == subVolume){
						//
						// species context belongs to this subDomain
						//
						Expression initial = new Expression(getMathSymbol(scs.getParameterFromRole(SpeciesContextSpec.ROLE_InitialConcentration),null));
						Expression rate = (scm.getRate()==null) ? new Expression(0.0) : getIdentifierSubstitutions(scm.getRate(),scm.getSpeciesContext().getUnitDefinition().divideBy(VCUnitDefinition.UNIT_s),simContext.getGeometryContext().getStructureMapping(sc.getStructure()));
						equation = new OdeEquation(variable,initial,rate);
						subDomain.replaceEquation(equation);
					}else{
						Expression initial = new Expression(0.0);
						Expression rate = new Expression(0.0);
						equation = new OdeEquation(variable,initial,rate);
						if (subDomain.getEquation(variable)==null){
							subDomain.addEquation(equation);
						}
					}
				}
			}
		}
		//
		// create fast system (if neccessary)
		//
		SpeciesContextMapping fastSpeciesContextMappings[] = structureAnalyzer.getFastSpeciesContextMappings();
		VCUnitDefinition subDomainUnit = VCUnitDefinition.UNIT_uM;
		if (fastSpeciesContextMappings!=null){
			FastSystem fastSystem = new FastSystemImplicit(mathDesc);
			for (int i=0;i<fastSpeciesContextMappings.length;i++){
				SpeciesContextMapping scm = fastSpeciesContextMappings[i];
				if (scm.getFastInvariant()==null){
					//
					// independant-fast variable, create a fastRate object
					//
					Expression rate = getIdentifierSubstitutions(scm.getFastRate(),scm.getSpeciesContext().getUnitDefinition().divideBy(VCUnitDefinition.UNIT_s),simContext.getGeometryContext().getStructureMapping(simContext.getGeometryContext().getResolvedFeature(subVolume)));
					FastRate fastRate = new FastRate(rate);
					fastSystem.addFastRate(fastRate);
				}else{
					//
					// dependant-fast variable, create a fastInvariant object
					//
					Expression rate = getIdentifierSubstitutions(scm.getFastInvariant(),subDomainUnit,simContext.getGeometryContext().getStructureMapping(simContext.getGeometryContext().getResolvedFeature(subVolume)));
					FastInvariant fastInvariant = new FastInvariant(rate);
					fastSystem.addFastInvariant(fastInvariant);
				}
			}
			subDomain.setFastSystem(fastSystem);
			fastSystem.checkValidity();
		}
		//
		// create ode's for voltages to be calculated on unresolved membranes mapped to this subVolume
		//
		Structure localStructures[] = simContext.getGeometryContext().getStructures(subVolume);
		for (int sIndex = 0; sIndex < localStructures.length; sIndex++){
			if (localStructures[sIndex] instanceof Membrane){
				Membrane membrane = (Membrane)localStructures[sIndex];
				MembraneMapping membraneMapping = (MembraneMapping)simContext.getGeometryContext().getStructureMapping(membrane);
				if (!membraneMapping.getResolved(simContext) && membraneMapping.getCalculateVoltage()){
					MembraneElectricalDevice capacitiveDevice = potentialMapping.getCapacitiveDevice(membrane);
					if (capacitiveDevice.getDependentVoltageExpression()==null){
						VolVariable vVar = (VolVariable)mathDesc.getVariable(capacitiveDevice.getVName());
						Expression initExp = new Expression(getMathSymbol(capacitiveDevice.getMembraneMapping().getInitialVoltageParameter(),membraneMapping));
						subDomain.addEquation(new OdeEquation(vVar,initExp,getIdentifierSubstitutions(potentialMapping.getOdeRHS(capacitiveDevice,this),VCUnitDefinition.UNIT_mV_per_s,membraneMapping)));
					}else{
						//
						//
						//
					}
				}
			}
		}
	}

	//
	// membrane subdomains
	//
	for (int k=0;k<subVolumes.length;k++){
		SubVolume subVolume = (SubVolume)subVolumes[k];
		//
		// if there is a spatially resolved membrane surrounding this subVolume, then create a membraneSubDomain
		//
		structures = simContext.getGeometryContext().getStructures(subVolume);
		Membrane membrane = null;
		if (structures!=null){
			for (int j=0;j<structures.length;j++){
				if (structures[j] instanceof Membrane && ((MembraneMapping)simContext.getGeometryContext().getStructureMapping(structures[j])).getResolved(simContext)){
					membrane = (Membrane)structures[j];
				}
			}
		}
		if (membrane==null){
			continue;
		}
		SubVolume outerSubVolume = ((FeatureMapping)simContext.getGeometryContext().getStructureMapping(membrane.getOutsideFeature())).getSubVolume();
		SubVolume innerSubVolume = ((FeatureMapping)simContext.getGeometryContext().getStructureMapping(membrane.getInsideFeature())).getSubVolume();
		if (innerSubVolume!=subVolume){
			throw new MappingException("membrane "+membrane.getName()+" improperly mapped to inner subVolume "+innerSubVolume.getName());
		}

		//
		// get priority of subDomain
		//
		Feature spatialFeature = simContext.getGeometryContext().getResolvedFeature(subVolume);
		int priority = spatialFeature.getPriority();
		//
		// create subDomain
		//
		CompartmentSubDomain outerCompartment = mathDesc.getCompartmentSubDomain(outerSubVolume.getName());
		CompartmentSubDomain innerCompartment = mathDesc.getCompartmentSubDomain(innerSubVolume.getName());

		MembraneSubDomain memSubDomain = new MembraneSubDomain(innerCompartment,outerCompartment);
		mathDesc.addSubDomain(memSubDomain);

		//
		// create equations for membrane-bound molecular species
		//
		MembraneStructureAnalyzer membraneStructureAnalyzer = getMembraneStructureAnalyzer(membrane);
		Enumeration enumSCM = getSpeciesContextMappings();
		while (enumSCM.hasMoreElements()){
			SpeciesContextMapping scm = (SpeciesContextMapping)enumSCM.nextElement();
			SpeciesContext        sc  = scm.getSpeciesContext();
			SpeciesContextSpec    scs = simContext.getReactionContext().getSpeciesContextSpec(sc);
			//
			// if an independent membrane variable, then create equation for it
			// ...even if the speciesContext is for another membraneSubDomain (e.g. BradykininReceptor in NuclearMembrane)
			//
			if ((scm.getVariable() instanceof MemVariable) && scm.getDependencyExpression()==null){
				//
				// independant variable, create an equation object
				//
				Equation equation = null;
				MemVariable variable = (MemVariable)scm.getVariable();
				MembraneMapping mm = (MembraneMapping)simContext.getGeometryContext().getStructureMapping(sc.getStructure());
				if (scm.isDiffusing()){
					//
					// PDE
					//
					if (mm.getMembrane() == membrane){
						//
						// species context belongs to this subDomain
						//
						Expression initial = new Expression(getMathSymbol(scs.getParameterFromRole(SpeciesContextSpec.ROLE_InitialConcentration),mm));
						Expression rate = getIdentifierSubstitutions(scm.getRate(),scm.getSpeciesContext().getUnitDefinition().divideBy(VCUnitDefinition.UNIT_s),simContext.getGeometryContext().getStructureMapping(sc.getStructure()));
						Expression diffusion = new Expression(getMathSymbol(scs.getDiffusionParameter(),mm));
						equation = new PdeEquation(variable,initial,rate,diffusion);
						((PdeEquation)equation).setBoundaryXm((scs.getBoundaryXmParameter().getExpression()==null)?(null):getIdentifierSubstitutions(scs.getBoundaryXmParameter().getExpression(),scs.getBoundaryXmParameter().getUnitDefinition(),mm));
						((PdeEquation)equation).setBoundaryXp((scs.getBoundaryXpParameter().getExpression()==null)?(null):getIdentifierSubstitutions(scs.getBoundaryXpParameter().getExpression(),scs.getBoundaryXpParameter().getUnitDefinition(),mm));
						((PdeEquation)equation).setBoundaryYm((scs.getBoundaryYmParameter().getExpression()==null)?(null):getIdentifierSubstitutions(scs.getBoundaryYmParameter().getExpression(),scs.getBoundaryYmParameter().getUnitDefinition(),mm));
						((PdeEquation)equation).setBoundaryYp((scs.getBoundaryYpParameter().getExpression()==null)?(null):getIdentifierSubstitutions(scs.getBoundaryYpParameter().getExpression(),scs.getBoundaryYpParameter().getUnitDefinition(),mm));
						((PdeEquation)equation).setBoundaryZm((scs.getBoundaryZmParameter().getExpression()==null)?(null):getIdentifierSubstitutions(scs.getBoundaryZmParameter().getExpression(),scs.getBoundaryZmParameter().getUnitDefinition(),mm));
						((PdeEquation)equation).setBoundaryZp((scs.getBoundaryZpParameter().getExpression()==null)?(null):getIdentifierSubstitutions(scs.getBoundaryZpParameter().getExpression(),scs.getBoundaryZpParameter().getUnitDefinition(),mm));
						memSubDomain.replaceEquation(equation);
					}else{
						Expression initial = new Expression(0.0);
						Expression rate = new Expression(0.0);
						Expression diffusion = new Expression(getMathSymbol(scs.getDiffusionParameter(),mm));
						equation = new PdeEquation(variable,initial,rate,diffusion);
						if (memSubDomain.getEquation(variable)==null){
							memSubDomain.addEquation(equation);
						}

					}
				} else {
					//
					// ODE					
					//
					if (mm.getMembrane() == membrane){
						//
						// species context belongs to this subDomain
						//
						Expression initial = new Expression(getMathSymbol(scs.getParameterFromRole(SpeciesContextSpec.ROLE_InitialConcentration),null));
						Expression rate = getIdentifierSubstitutions(scm.getRate(),scm.getSpeciesContext().getUnitDefinition().divideBy(VCUnitDefinition.UNIT_s),simContext.getGeometryContext().getStructureMapping(sc.getStructure()));
						equation = new OdeEquation(variable,initial,rate);
						memSubDomain.replaceEquation(equation);
					}else{
						Expression initial = new Expression(0.0);
						Expression rate = new Expression(0.0);
						equation = new OdeEquation(variable,initial,rate);
						if (memSubDomain.getEquation(variable)==null){
							memSubDomain.addEquation(equation);
						}
					}
				}
			}
		}
		//
		// create dummy jump conditions for all volume variables that diffuse
		//
		Enumeration enum_scm = getSpeciesContextMappings();
		while (enum_scm.hasMoreElements()){
			SpeciesContextMapping scm = (SpeciesContextMapping)enum_scm.nextElement();
			if (scm.isDiffusing()){
				Species species = scm.getSpeciesContext().getSpecies();
				Variable var = scm.getVariable();
				if (var instanceof VolVariable && scm.isDiffusing()){
					JumpCondition jc = memSubDomain.getJumpCondition((VolVariable)var);
					if (jc==null){
//System.out.println("MathMapping.refreshMathDescription(), adding jump condition for diffusing variable "+var.getName()+" on membrane "+membraneStructureAnalyzer.getMembrane().getName());
						jc = new JumpCondition((VolVariable)var);
						memSubDomain.addJumpCondition(jc);
					}
				}
			}
		}
		//
		// create jump conditions for any volume variables that bind to membrane or have explicitly defined fluxes
		//
		ResolvedFlux resolvedFluxes[] = membraneStructureAnalyzer.getResolvedFluxes();
		if (resolvedFluxes!=null){
			for (int i=0;i<resolvedFluxes.length;i++){
				Species species = resolvedFluxes[i].getSpecies();
				SpeciesContext sc = simContext.getReactionContext().getModel().getSpeciesContext(species,membraneStructureAnalyzer.getMembrane().getInsideFeature());
				if (sc==null){
					sc = simContext.getReactionContext().getModel().getSpeciesContext(species,membraneStructureAnalyzer.getMembrane().getOutsideFeature());
				}
				SpeciesContextMapping scm = getSpeciesContextMapping(sc);
				//
				// introduce Bug Compatability mode for NoFluxIfFixed
				//
				// if (scm.getVariable() instanceof VolVariable && scm.isDiffusing()){
				if (scm.getVariable() instanceof VolVariable && (MembraneStructureAnalyzer.bNoFluxIfFixed || scm.isDiffusing())){
					if (MembraneStructureAnalyzer.bNoFluxIfFixed && !scm.isDiffusing()){
						MembraneStructureAnalyzer.bNoFluxIfFixedExercised = true;
					}
					JumpCondition jc = memSubDomain.getJumpCondition((VolVariable)scm.getVariable());
					if (jc==null){
						jc = new JumpCondition((VolVariable)scm.getVariable());
						memSubDomain.addJumpCondition(jc);
					}
					Expression inFlux = getIdentifierSubstitutions(resolvedFluxes[i].inFlux,VCUnitDefinition.UNIT_uM_um_per_s,simContext.getGeometryContext().getStructureMapping(membraneStructureAnalyzer.getMembrane()));
					jc.setInFlux(inFlux);
					Expression outFlux = getIdentifierSubstitutions(resolvedFluxes[i].outFlux,VCUnitDefinition.UNIT_uM_um_per_s,simContext.getGeometryContext().getStructureMapping(membraneStructureAnalyzer.getMembrane()));
					jc.setOutFlux(outFlux);
				}else{
					throw new MappingException(scm.getSpeciesContext().getName()+" has spatially resolved flux at membrane "+membrane.getName()+", but doesn't diffuse in compartment "+scm.getSpeciesContext().getStructure().getName());
				}
			}
		}

		//
		// create fast system (if neccessary)
		//
		SpeciesContextMapping fastSpeciesContextMappings[] = membraneStructureAnalyzer.getFastSpeciesContextMappings();
		if (fastSpeciesContextMappings!=null){
			FastSystem fastSystem = new FastSystemImplicit(mathDesc);
			for (int i=0;i<fastSpeciesContextMappings.length;i++){
				SpeciesContextMapping scm = fastSpeciesContextMappings[i];
				if (scm.getFastInvariant()==null){
					//
					// independant-fast variable, create a fastRate object
					//
					VCUnitDefinition rateUnit = scm.getSpeciesContext().getUnitDefinition().divideBy(ReservedSymbol.TIME.getUnitDefinition());
					MembraneMapping membraneMapping = (MembraneMapping)simContext.getGeometryContext().getStructureMapping(membraneStructureAnalyzer.getMembrane());
					FastRate fastRate = new FastRate(getIdentifierSubstitutions(scm.getFastRate(),rateUnit,membraneMapping));
					fastSystem.addFastRate(fastRate);
				}else{
					//
					// dependant-fast variable, create a fastInvariant object
					//
					VCUnitDefinition invariantUnit = scm.getSpeciesContext().getUnitDefinition();
					MembraneMapping membraneMapping = (MembraneMapping)simContext.getGeometryContext().getStructureMapping(membraneStructureAnalyzer.getMembrane());
					FastInvariant fastInvariant = new FastInvariant(getIdentifierSubstitutions(scm.getFastInvariant(),invariantUnit,membraneMapping));
					fastSystem.addFastInvariant(fastInvariant);
				}
			}
			memSubDomain.setFastSystem(fastSystem);
			fastSystem.checkValidity();
		}
		//
		// create Membrane-region equations for potential of this resolved membrane
		//
		MembraneMapping membraneMapping = (MembraneMapping)simContext.getGeometryContext().getStructureMapping(membrane);
		if (membraneMapping.getCalculateVoltage()){
			ElectricalDevice membraneDevices[] = potentialMapping.getElectricalDevices(membrane);
			int numCapacitiveDevices = 0;
			MembraneElectricalDevice capacitiveDevice = null;
			for (int i = 0; i < membraneDevices.length; i++){
				if (membraneDevices[i] instanceof MembraneElectricalDevice){
					numCapacitiveDevices++;
					capacitiveDevice = (MembraneElectricalDevice)membraneDevices[i];
				}
			}
			if (numCapacitiveDevices!=1){
				throw new MappingException("expecting 1 capacitive electrical device on graph edge for membrane "+membrane.getName()+", found '"+numCapacitiveDevices+"'");
			}
			if (mathDesc.getVariable(capacitiveDevice.getVName()) instanceof MembraneRegionVariable){
				MembraneRegionVariable vVar = (MembraneRegionVariable)mathDesc.getVariable(capacitiveDevice.getVName());
				Parameter initialVoltageParm = capacitiveDevice.getMembraneMapping().getInitialVoltageParameter();
				Expression initExp = getIdentifierSubstitutions(initialVoltageParm.getExpression(),initialVoltageParm.getUnitDefinition(),capacitiveDevice.getMembraneMapping());
				MembraneRegionEquation vEquation = new MembraneRegionEquation(vVar,initExp);
				vEquation.setMembraneRateExpression(getIdentifierSubstitutions(potentialMapping.getOdeRHS(capacitiveDevice,this),VCUnitDefinition.UNIT_mV_per_s,capacitiveDevice.getMembraneMapping()));
				memSubDomain.addEquation(vEquation);
			}
		}
	}

	if (!mathDesc.isValid()){
		throw new MappingException("generated an invalid mathDescription: "+mathDesc.getWarning());
	}

//System.out.println("]]]]]]]]]]]]]]]]]]]]]] VCML string begin ]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]");
//System.out.println(mathDesc.getVCML());
//System.out.println("]]]]]]]]]]]]]]]]]]]]]] VCML string end ]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]");
}


/**
 * This method was created in VisualAge.
 */
private void refreshSpeciesContextMappings() throws ExpressionException, MappingException, MathException {
	
	//
	// create a SpeciesContextMapping for each speciesContextSpec.
	//
	// set initialExpression from SpeciesContextSpec.
	// set diffusing
	// set variable (only if "Constant" or "Function", else leave it as null)
	//
	speciesContextMappingList.removeAllElements();
	
	SpeciesContextSpec speciesContextSpecs[] = simContext.getReactionContext().getSpeciesContextSpecs();
	for (int i=0;i<speciesContextSpecs.length;i++){
		SpeciesContextSpec scs = speciesContextSpecs[i];

		SpeciesContextMapping scm = new SpeciesContextMapping(scs.getSpeciesContext());

		scm.setDiffusing(isDiffusionRequired(scs.getSpeciesContext()));
		if (scs.isConstant()){
			Expression initCond = scs.getInitialConditionParameter().getExpression();
			scm.setDependencyExpression(initCond);
			////
			//// determine if a Function is necessary
			////
			//boolean bNeedFunction = false;
			//if (initCond.getSymbols()!=null){
				//bNeedFunction = true;
			//}
			//if (bNeedFunction){
				//scm.setVariable(new Function(scm.getSpeciesContext().getName(),initCond));
			//}else{
				//scm.setVariable(new Constant(scm.getSpeciesContext().getName(),initCond));
			//}
		}
		//
		// test if participant in fast reaction step, request elimination if possible
		//
		scm.setFastParticipant(false);
		ReactionSpec reactionSpecs[] = simContext.getReactionContext().getReactionSpecs();
		for (int j=0;j<reactionSpecs.length;j++){
			ReactionSpec reactionSpec = reactionSpecs[j];
			if (reactionSpec.isExcluded()){
				continue;
			}
			ReactionStep rs = reactionSpec.getReactionStep();
			if (rs instanceof SimpleReaction && rs.getReactionParticipant(scs.getSpeciesContext())!=null){
				if (reactionSpec.isFast()){
					scm.setFastParticipant(true);
				}
			}
		}
		speciesContextMappingList.addElement(scm);
	}
}


/**
 * This method was created by a SmartGuide.
 */
private void refreshStructureAnalyzers() {

	structureAnalyzerList.removeAllElements();
	
	//
	// update structureAnalyzer list if any subVolumes were added
	//
	SubVolume subVolumes[] = simContext.getGeometryContext().getGeometry().getGeometrySpec().getSubVolumes();
	for (int j=0;j<subVolumes.length;j++){
		SubVolume subVolume = (SubVolume)subVolumes[j];
		if (getVolumeStructureAnalyzer(subVolume)==null){
			structureAnalyzerList.addElement(new VolumeStructureAnalyzer(this,subVolume));
		}
		//
		// Add a MembraneStructureAnalyzer if necessary
		//
		// go through list of MembraneMappings and determine if inner and outer compartment
		// are both mapped to subVolumes, then add
		//
		Structure structures[] = simContext.getGeometryContext().getStructures(subVolume);
		if (structures!=null){
			for (int i=0;i<structures.length;i++){
				if (structures[i] instanceof Membrane){
					Membrane membrane = (Membrane)structures[i];
					MembraneMapping mm = (MembraneMapping)simContext.getGeometryContext().getStructureMapping(membrane);
					if (mm != null){
						if (mm.getResolved(simContext) && getMembraneStructureAnalyzer(membrane)==null){
							SubVolume outerSubVolume = ((FeatureMapping)simContext.getGeometryContext().getStructureMapping(membrane.getOutsideFeature())).getSubVolume();
							structureAnalyzerList.addElement(new MembraneStructureAnalyzer(this,membrane,subVolume,outerSubVolume));
						}
					}
				}
			}		
		}
	}


	//
	// invoke all structuralAnalyzers
	//
	Enumeration enum1 = getStructureAnalyzers();
	while (enum1.hasMoreElements()) {
		StructureAnalyzer sa = (StructureAnalyzer) enum1.nextElement();
		sa.refresh();
	}
}


/**
 * This method was created in VisualAge.
 */
private void refreshVariables() throws MappingException {

//System.out.println("MathMapping.refreshVariables()");

	//
	// non-constant dependant variables require a function
	//
	Enumeration enum1 = getSpeciesContextMappings();
	while (enum1.hasMoreElements()){
		SpeciesContextMapping scm = (SpeciesContextMapping)enum1.nextElement();
		SpeciesContextSpec scs = simContext.getReactionContext().getSpeciesContextSpec(scm.getSpeciesContext());
		if (scm.getDependencyExpression() != null && !scs.isConstant()){
			//scm.setVariable(new Function(scm.getSpeciesContext().getName(),scm.getDependencyExpression()));
			scm.setVariable(null);
		}
	}

	//
	// non-constant independant variables require either a membrane or volume variable
	//
	enum1 = getSpeciesContextMappings();
	while (enum1.hasMoreElements()){
		SpeciesContextMapping scm = (SpeciesContextMapping)enum1.nextElement();
		SpeciesContextSpec scs = simContext.getReactionContext().getSpeciesContextSpec(scm.getSpeciesContext());
		if (scm.getDependencyExpression() == null && !scs.isConstant()){
			StructureMapping sm = simContext.getGeometryContext().getStructureMapping(scm.getSpeciesContext().getStructure());
			Structure struct = scm.getSpeciesContext().getStructure();
			if (struct instanceof Feature){
				if (((FeatureMapping)sm).getResolved()){
					scm.setVariable(getResolvedVolVariable(scm.getSpeciesContext().getSpecies()));
				}else{
					scm.setVariable(new VolVariable(scm.getSpeciesContext().getName()));
				}
			}else if (struct instanceof Membrane){
				if (((MembraneMapping)sm).getResolved(simContext)){
					scm.setVariable(new MemVariable(scm.getSpeciesContext().getName()));
				}else{
					scm.setVariable(new VolVariable(scm.getSpeciesContext().getName()));
				}
			}else{
				throw new MappingException("class "+scm.getSpeciesContext().getStructure().getClass()+" not supported");
			}
		}
	}

}


/**
 * Insert the method's description here.
 * Creation date: (3/29/2004 12:59:56 AM)
 * @param mathMappingParameter cbit.vcell.mapping.MathMapping.MathMappingParameter
 */
void removeMathMappingParameter(MathMapping.MathMappingParameter mathMappingParameter) throws java.beans.PropertyVetoException {
	MathMappingParameter newMathMappingParameters[] = (MathMappingParameter[])cbit.util.BeanUtils.removeElement(fieldMathMappingParameters,mathMappingParameter);
	setMathMapppingParameters(newMathMappingParameters);
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
 * Sets the mathMappingParameters property (MathMapping.MathMappingParameter[]) value.
 * @param kineticsParameters The new value for the property.
 * @exception java.beans.PropertyVetoException The exception description.
 * @see #getMathMappingParameters
 */
void setMathMapppingParameters(MathMappingParameter[] mathMappingParameters) throws java.beans.PropertyVetoException {
	MathMapping.MathMappingParameter[] oldValue = fieldMathMappingParameters;
	fireVetoableChange("mathMappingParameters", oldValue, mathMappingParameters);
	fieldMathMappingParameters = mathMappingParameters;
	firePropertyChange("mathMappingParameters", oldValue, mathMappingParameters);
}
}