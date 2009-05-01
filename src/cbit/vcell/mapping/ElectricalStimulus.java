package cbit.vcell.mapping;
import cbit.vcell.parser.SymbolTableEntry;
import cbit.vcell.model.ModelException;
import java.util.Vector;
import java.beans.PropertyVetoException;

import org.vcell.util.BeanUtils;

import cbit.vcell.parser.ScopedSymbolTable;
import cbit.vcell.parser.AbstractNameScope;
import cbit.vcell.model.BioNameScope;
import cbit.vcell.parser.NameScope;
import cbit.vcell.model.Parameter;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.units.VCUnitDefinition;
import cbit.vcell.parser.ExpressionBindingException;
import cbit.vcell.model.VCMODL;
/**
 * Insert the type's description here.
 * Creation date: (4/8/2002 11:14:58 AM)
 * @author: Anuradha Lakshminarayana
 */
public abstract class ElectricalStimulus implements org.vcell.util.Matchable, cbit.vcell.parser.ScopedSymbolTable, java.io.Serializable, java.beans.PropertyChangeListener, java.beans.VetoableChangeListener {

	private static final String GENERAL_PROTOCOL = "General_Protocol";
	
	public class ElectricalStimulusNameScope extends BioNameScope {
		private cbit.vcell.parser.NameScope[] children = new cbit.vcell.parser.NameScope[0];
		public ElectricalStimulusNameScope(){
			super();
		}
		public cbit.vcell.parser.NameScope[] getChildren() {
			return children;
		}
		public String getName() {
			return org.vcell.util.TokenMangler.fixTokenStrict(ElectricalStimulus.this.getName());
		}
		public cbit.vcell.parser.NameScope getParent() {
			return ElectricalStimulus.this.simulationContext.getNameScope();
		}
		public cbit.vcell.parser.ScopedSymbolTable getScopedSymbolTable() {
			return ElectricalStimulus.this;
		}
	}

	public class ElectricalStimulusParameter extends Parameter {
		
		private String fieldParameterName = null;
		private Expression fieldExpression = null;
		private VCUnitDefinition fieldVCUnitDefinition = null;
		private int fieldParameterRole = ROLE_UserDefined;

		protected ElectricalStimulusParameter(String argName, Expression argExpression, int argRole, VCUnitDefinition argVCUnitDefinition) {
			if (argName == null){
				throw new IllegalArgumentException("parameter name is null");
			}
			if (argName.length()<1){
				throw new IllegalArgumentException("parameter name is zero length");
			}

			if (argRole>=0 || argRole<NUM_PARAMETER_ROLES){
				this.fieldParameterRole = argRole;
				setDescription(ElectricalStimulus.RoleDescs[argRole]);
			}else{
				throw new IllegalArgumentException("parameter 'role' = "+argRole+" is out of range");
			}
			this.fieldParameterName = argName;
			this.fieldExpression = argExpression;
			this.fieldVCUnitDefinition = argVCUnitDefinition;
		}

		public boolean compareEqual(org.vcell.util.Matchable obj) {
			if (!(obj instanceof ElectricalStimulusParameter)){
				return false;
			}
			ElectricalStimulusParameter esp = (ElectricalStimulusParameter)obj;
			if (!super.compareEqual0(esp)){
				return false;
			}
			if (fieldParameterRole != esp.fieldParameterRole){
				return false;
			}
				
			return true;
		}

		public double getConstantValue() throws ExpressionException {
			return this.fieldExpression.evaluateConstant();
		}      

		public Expression getExpression() {
			return this.fieldExpression;
		}

		public boolean isExpressionEditable(){
			if (ElectricalStimulus.this instanceof cbit.vcell.mapping.CurrentClampStimulus && getRole()==ElectricalStimulus.ROLE_Voltage){
				return false;
			}
			if (ElectricalStimulus.this instanceof cbit.vcell.mapping.VoltageClampStimulus && getRole()==ElectricalStimulus.ROLE_Current){
				return false;
			}
			return true;
		}

		public boolean isUnitEditable(){
			if (getRole() == ROLE_UserDefined){
				return true;
			}else{
				return false;
			}
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
			return this.fieldParameterRole;
		}

		public String getName(){ 
			return this.fieldParameterName; 
		}   

		public NameScope getNameScope() {
			return ElectricalStimulus.this.getNameScope();
		}

		public void setUnitDefinition(cbit.vcell.units.VCUnitDefinition unitDefinition) {
			cbit.vcell.units.VCUnitDefinition oldValue = fieldVCUnitDefinition;
			fieldVCUnitDefinition = unitDefinition;
			if (oldValue==unitDefinition){
				return;
			}
			if (unitDefinition!=null && unitDefinition.compareEqual(oldValue)){
				return;
			}
			super.firePropertyChange("unitDefinition", oldValue, unitDefinition);
		}

		public void setExpression(Expression argExpression) throws java.beans.PropertyVetoException {
			//if (argExpression!=null){
				//argExpression = new Expression(argExpression);
				//argExpression.bindExpression(ElectricalStimulus.this);
			//}
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

		public boolean isDescriptionEditable() {
			return false;
		}

	}

	public class UnresolvedParameter extends Parameter {
		
		private String fieldParameterName = null;

		protected UnresolvedParameter(String argName) {
			if (argName == null){
				throw new IllegalArgumentException("parameter name is null");
			}
			if (argName.length()<1){
				throw new IllegalArgumentException("parameter name is zero length");
			}
			this.fieldParameterName = argName;
			setDescription("unresolved");
		}

		public boolean compareEqual(org.vcell.util.Matchable obj) {
			if (!(obj instanceof UnresolvedParameter)){
				return false;
			}
			UnresolvedParameter up = (UnresolvedParameter)obj;
			if (!super.compareEqual0(up)){
				return false;
			}			
			return true;
		}

		public double getConstantValue() throws ExpressionException {
			throw new ExpressionException("no expression defined for UnresolvedParameter '"+fieldParameterName+"'");
		}      

		public Expression getExpression() {
			return null;
		}

		public boolean isExpressionEditable(){
			return false;
		}

		public boolean isNameEditable(){
			return false;
		}

		public boolean isUnitEditable(){
			return false;
		}

		public void setExpression(Expression expression) throws PropertyVetoException, ExpressionBindingException {
			throw new RuntimeException("expression is not editable");
		}

		public void setUnitDefinition(VCUnitDefinition unit) throws PropertyVetoException {
			throw new RuntimeException("unit is not editable");
		}
		
		public int getIndex() {
			return -1;
		}

		public String getName(){ 
			return this.fieldParameterName; 
		}

		public cbit.vcell.units.VCUnitDefinition getUnitDefinition() {
			return cbit.vcell.units.VCUnitDefinition.UNIT_TBD;
		}

		public NameScope getNameScope() {
			if (ElectricalStimulus.this != null){
				return ElectricalStimulus.this.getNameScope();
			}else{
				return null;
			}
		}

		public void setName(java.lang.String name) throws java.beans.PropertyVetoException {
			String oldValue = fieldParameterName;
			super.fireVetoableChange("name", oldValue, name);
			fieldParameterName = name;
			super.firePropertyChange("name", oldValue, name);
		}

		public boolean isDescriptionEditable() {
			return false;
		}
	}
		
	protected transient java.beans.VetoableChangeSupport vetoPropertyChange;
	protected transient java.beans.PropertyChangeSupport propertyChange;
	private Electrode fieldElectrode = null;
	private java.lang.String fieldName = null;
	private java.lang.String fieldAnnotation = null;
	private ElectricalStimulus.ElectricalStimulusNameScope fieldNameScope = new ElectricalStimulus.ElectricalStimulusNameScope();
	private ElectricalStimulus.ElectricalStimulusParameter[] fieldElectricalStimulusParameters = new ElectricalStimulusParameter[0];
	private ElectricalStimulus.UnresolvedParameter[] fieldUnresolvedParameters = new UnresolvedParameter[0];
	private SimulationContext simulationContext = null;
	private transient boolean bReading = false;
	private transient boolean bResolvingUnits = false;
	//
	// for Voltage Clamp:  CurrentParameter.exp == null and VoltageParameter.exp != null
	// for Current Clamp:  CurrentParameter.exp != null and VoltageParameter.exp == null
	//
	public static final int NUM_PARAMETER_ROLES = 3;
	public static final int ROLE_UserDefined = 0;
	public static final int ROLE_Current = 1;
	public static final int ROLE_Voltage = 2;

	private static final String RoleTags[] = {
		VCMODL.ES_Role_UserDefined,
		VCMODL.ES_Role_Current,
		VCMODL.ES_Role_Voltage
	};
	public static final String DefaultNames[] = {
		null,
		"I",
		"V",
	};
	public static final String RoleDescs[] = {
		"user defined",
		"total current",
		"potential difference",
	};
	
/**
 * ElectricalStimulus constructor comment.
 */
protected ElectricalStimulus(Electrode argElectrode, String argName, SimulationContext argSimulationContext) {
	if (argElectrode==null){ throw new IllegalArgumentException("electrode must not be null"); }
	if (argName==null){ throw new IllegalArgumentException("name must not be null"); }
	if (argSimulationContext==null){ throw new IllegalArgumentException("simulation context must not be null"); }
	fieldElectrode = argElectrode;
	fieldName = argName;
	simulationContext = argSimulationContext;
	fieldElectricalStimulusParameters = new ElectricalStimulusParameter[2];
	fieldElectricalStimulusParameters[0] = new ElectricalStimulusParameter(
														DefaultNames[ROLE_Current],null,  // initially no expression
														ROLE_Current,VCUnitDefinition.UNIT_pA_per_um2);

	fieldElectricalStimulusParameters[1] = new ElectricalStimulusParameter(
														DefaultNames[ROLE_Voltage],null,  // initially no expression
														ROLE_Voltage,VCUnitDefinition.UNIT_mV);
	addPropertyChangeListener(this);
	addVetoableChangeListener(this);
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
 * Insert the method's description here.
 * Creation date: (9/22/2003 9:51:49 AM)
 * @param parameterName java.lang.String
 */
public void addUnresolvedParameter(String parameterName) {
	if (getParameter(parameterName)!=null){
		throw new RuntimeException("parameter '"+parameterName+"' already exists");
	}
	UnresolvedParameter newUnresolvedParameters[] = (UnresolvedParameter[])BeanUtils.addElement(fieldUnresolvedParameters,new UnresolvedParameter(parameterName));
	setUnresolvedParameters(newUnresolvedParameters);
}


/**
 * Insert the method's description here.
 * Creation date: (9/22/2003 9:51:49 AM)
 * @param parameterName java.lang.String
 */
public ElectricalStimulusParameter addUserDefinedKineticsParameter(String parameterName, Expression expression, cbit.vcell.units.VCUnitDefinition unit) throws PropertyVetoException {
	if (getParameter(parameterName)!=null){
		throw new RuntimeException("parameter '"+parameterName+"' already exists");
	}
	ElectricalStimulusParameter newParameter = new ElectricalStimulusParameter(parameterName,expression,ROLE_UserDefined, unit);
	ElectricalStimulusParameter newParameters[] = (ElectricalStimulusParameter[])BeanUtils.addElement(fieldElectricalStimulusParameters,newParameter);
	setElectricalStimulusParameters(newParameters);
	return newParameter;
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
 */
private final void cleanupParameters() throws ExpressionException, PropertyVetoException {
	if (bReading){
		return;
	}
	//
	// for each parameter, see if it is used, if not delete it
	//
	if (fieldElectricalStimulusParameters != null){
		for (int i=0;i<fieldElectricalStimulusParameters.length;i++){
			if (fieldElectricalStimulusParameters[i].getRole()==ROLE_UserDefined && !isReferenced(fieldElectricalStimulusParameters[i],0)){
				removeElectricalStimulusParameter(fieldElectricalStimulusParameters[i]);
				i--;
			}
		}
	}
	//if (fieldUnresolvedParameters != null){
		//for (int i=0;i<fieldUnresolvedParameters.length;i++){
			//if (!isReferenced(fieldUnresolvedParameters[i],0)){
				//removeUnresolvedParameter(fieldUnresolvedParameters[i]);
				//i--;
			//}
		//}
	//}

	for (int i = 0;fieldElectricalStimulusParameters!=null && i < fieldElectricalStimulusParameters.length; i++){
		Expression exp = fieldElectricalStimulusParameters[i].getExpression();
		if (exp!=null){
			try {
				exp.bindExpression(this);
			}catch (ExpressionBindingException e){
				System.out.println("error binding expression '"+exp.infix()+"': "+e.getMessage());
			}
		}
	}
	resolveUndefinedUnits();
}


/**
 * Checks for internal representation of objects, not keys from database
 * @return boolean
 * @param obj java.lang.Object
 */
protected final boolean compareEqual0(org.vcell.util.Matchable obj) {
	if (obj instanceof ElectricalStimulus){
		ElectricalStimulus es = (ElectricalStimulus)obj;
		
		if (!org.vcell.util.Compare.isEqual(getName(),es.getName())){
			return false;
		}
		if (!org.vcell.util.Compare.isEqualOrNull(getAnnotation(),es.getAnnotation())){
			return false;
		}
		if (!org.vcell.util.Compare.isEqual(getElectrode(),es.getElectrode())){
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
 * Gets the annotation property (java.lang.String) value.
 * @return The annotation property value.
 * @see #setAnnotation
 */
public java.lang.String getAnnotation() {
	return fieldAnnotation;
}


/**
 * Gets the currentExpression property (cbit.vcell.parser.Expression) value.
 * @return The currentExpression property value.
 * @see #setCurrentExpression
 */
public ElectricalStimulus.ElectricalStimulusParameter getCurrentParameter() {
	return getElectricalStimulusParameterFromRole(ROLE_Current);
}


/**
 * Insert the method's description here.
 * Creation date: (10/16/2003 7:22:36 AM)
 * @return java.lang.String
 * @param role int
 */
public String getDefaultParameterDesc(int role) {
	if (role < 0 || role >= NUM_PARAMETER_ROLES){
		throw new IllegalArgumentException("role out of range, "+role);
	}
//	return DefaultNames[role]+"_"+TokenMangler.fixTokenStrict(getReactionStep().getName());
	return RoleDescs[role];
}


public ElectricalStimulus.ElectricalStimulusParameter getElectricalStimulusParameter(String pName){
	if (fieldElectricalStimulusParameters == null){
		return null;
	}
	for (int i=0;i<fieldElectricalStimulusParameters.length;i++){
		ElectricalStimulus.ElectricalStimulusParameter parm = fieldElectricalStimulusParameters[i];
		if (pName.equals(parm.getName())){
			return parm;
		}
	}
	return null;
}   


/**
 * Gets the kineticsParameters index property (cbit.vcell.model.KineticsParameter) value.
 * @return The kineticsParameters property value.
 * @param index The index value into the property array.
 * @see #setKineticsParameters
 */
public ElectricalStimulus.ElectricalStimulusParameter getElectricalStimulusParameterFromRole(int role) {
	for (int i = 0; i < fieldElectricalStimulusParameters.length; i++){
		if (fieldElectricalStimulusParameters[i].getRole() == role){
			return fieldElectricalStimulusParameters[i];
		}
	}
	return null;
}


/**
 * Gets the kineticsParameters property (cbit.vcell.model.KineticsParameter[]) value.
 * @return The kineticsParameters property value.
 * @see #setKineticsParameters
 */
public ElectricalStimulus.ElectricalStimulusParameter[] getElectricalStimulusParameters() {
	return fieldElectricalStimulusParameters;
}


/**
 * Gets the kineticsParameters index property (cbit.vcell.model.KineticsParameter) value.
 * @return The kineticsParameters property value.
 * @param index The index value into the property array.
 * @see #setKineticsParameters
 */
public ElectricalStimulus.ElectricalStimulusParameter getElectricalStimulusParameters(int index) {
	return getElectricalStimulusParameters()[index];
}


/**
 * Gets the electrode property (cbit.vcell.mapping.Electrode) value.
 * @return The electrode property value.
 * @see #setElectrode
 */
public Electrode getElectrode() {
	return fieldElectrode;
}


/**
 * getEntry method comment.
 */
public cbit.vcell.parser.SymbolTableEntry getEntry(java.lang.String identifierString) throws cbit.vcell.parser.ExpressionBindingException {
	cbit.vcell.parser.SymbolTableEntry ste = getLocalEntry(identifierString);
	if (ste != null){
		if (ste instanceof ElectricalStimulusParameter){
			ElectricalStimulusParameter esParm = (ElectricalStimulusParameter)ste;
			if (esParm.getRole()==ROLE_Voltage || esParm.getRole()==ROLE_Current){
				throw new ExpressionBindingException("electrophysiological protocols can't be a function of Voltage or Current");
			}
		}
		return ste;
	}
	return getNameScope().getExternalEntry(identifierString,this);
}


/**
 * Insert the method's description here.
 * Creation date: (4/6/2004 2:10:26 PM)
 * @return cbit.vcell.parser.SymbolTableEntry
 * @param identifier java.lang.String
 */
public cbit.vcell.parser.SymbolTableEntry getLocalEntry(java.lang.String identifier) throws cbit.vcell.parser.ExpressionBindingException {
	cbit.vcell.parser.SymbolTableEntry ste = null;

	ste = cbit.vcell.model.ReservedSymbol.fromString(identifier);
	if (ste!=null){
		return ste;
	}

	ste = getElectricalStimulusParameter(identifier);
	if (ste!=null){
		return ste;
	}

	return null;
}


/**
 * Gets the name property (java.lang.String) value.
 * @return The name property value.
 * @see #setName
 */
public java.lang.String getName() {
	return fieldName;
}


/**
 * Insert the method's description here.
 * Creation date: (4/6/2004 2:10:26 PM)
 * @return cbit.vcell.parser.NameScope
 */
public cbit.vcell.parser.NameScope getNameScope() {
	return this.fieldNameScope;
}


public Parameter getParameter(String pName){
	ElectricalStimulusParameter esParameter = getElectricalStimulusParameter(pName);
	UnresolvedParameter unresolvedParameter = getUnresolvedParameter(pName);
	if (esParameter==null && unresolvedParameter==null){
		return null;
	}else if (esParameter!=null && unresolvedParameter!=null){
		throw new RuntimeException("parameter '"+pName+"' exists both as electrical stimulus parameter and unresolvedParameter");
	}else if (esParameter!=null){
		return esParameter;
	}else{
		return unresolvedParameter;
	}
}   


	public static int getParamRoleFromDesc(String paramDesc) {

		int paramRole = -1;
		
		if (paramDesc == null || paramDesc.length() == 0) {
			throw new IllegalArgumentException("Invalid value for parameter description: " + paramDesc); 
		}
		for (int i = 0; i < RoleDescs.length; i++) {
			if (RoleDescs[i].equals(paramDesc)) {
				paramRole = i;
			}	
		}

		if (paramRole == -1) {
			throw new IllegalArgumentException("Parameter description: " + paramDesc + " is not valid.");
		}

		return paramRole;
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


public UnresolvedParameter getUnresolvedParameter(String pName){
	if (fieldUnresolvedParameters == null){
		return null;
	}
	for (int i=0;i<fieldUnresolvedParameters.length;i++){
		UnresolvedParameter parm = fieldUnresolvedParameters[i];
		if (pName.equals(parm.getName())){
			return parm;
		}
	}
	return null;
}   


/**
 * Gets the unresolvedParameters property (cbit.vcell.model.UnresolvedParameter[]) value.
 * @return The unresolvedParameters property value.
 * @see #setUnresolvedParameters
 */
public UnresolvedParameter[] getUnresolvedParameters() {
	return fieldUnresolvedParameters;
}


/**
 * Gets the unresolvedParameters index property (cbit.vcell.model.UnresolvedParameter) value.
 * @return The unresolvedParameters property value.
 * @param index The index value into the property array.
 * @see #setUnresolvedParameters
 */
public UnresolvedParameter getUnresolvedParameters(int index) {
	return getUnresolvedParameters()[index];
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
 * Gets the currentExpression property (cbit.vcell.parser.Expression) value.
 * @return The currentExpression property value.
 * @see #setCurrentExpression
 */
public ElectricalStimulus.ElectricalStimulusParameter getVoltageParameter() {
	return getElectricalStimulusParameterFromRole(ROLE_Voltage);
}


/**
 * The hasListeners method was generated to support the propertyChange field.
 */
public synchronized boolean hasListeners(java.lang.String propertyName) {
	return getPropertyChange().hasListeners(propertyName);
}


/**
 * This method was created in VisualAge.
 * @return boolean
 * @param parm cbit.vcell.model.Parameter
 */
private boolean isReferenced(Parameter parm, int level) throws ExpressionException {
	//
	// check for unbounded recursion (level > 10)
	//
	if (level >= 10){
		throw new RuntimeException("there is a loop in the parameter definitions");
	}
	
	////
	//// if this unresolved parameter is same as another parameter, then it should be removed
	//// note: that external identifiers have precedence over UndefinedParameters in ReactionStep.getEntry()
	////       so getEntry() will only return a UnresolvedParameter if there is no alternative.
	////
	//if ((parm instanceof Kinetics.UnresolvedParameter) && parm != reactionStep.getEntry(parm.getName())){
		//return false;
	//}

	////
	//// if parameter is referenced in rate expression, then it is OK
	////
	if (parm instanceof ElectricalStimulusParameter && ((ElectricalStimulusParameter)parm).getRole() != ROLE_UserDefined){
		return true;
	}

	//
	// else, if parameter is referenced in another parameter's expression, continue with that expression
	//
	boolean bReferenced = false;
	if (fieldElectricalStimulusParameters != null){
		for (int i=0;i<fieldElectricalStimulusParameters.length;i++){
			Parameter parentParm = fieldElectricalStimulusParameters[i];
			Expression exp = parentParm.getExpression();
			if (parentParm.getExpression()!=null){
				String[] symbols = exp.getSymbols();
				if (symbols!=null){
					for (int j=0;j<symbols.length;j++){
						if (cbit.vcell.parser.AbstractNameScope.getStrippedIdentifier(symbols[j]).equals(parm.getName())){
							bReferenced = true;
							if (isReferenced(parentParm,level+1)){
								return true;
							}
						}
					}
				}
			}
		}
	}
	return false;
}


/**
 * This method was created by a SmartGuide.
 * @param tokens java.util.StringTokenizer
 * @exception java.lang.Exception The exception description.
 */
public final void parameterVCMLSet(org.vcell.util.CommentStringTokenizer tokens) throws ExpressionException,PropertyVetoException{

	if(tokens == null){
		return;
	}
	
	Vector esParametersV = new Vector();
	
	if (!tokens.nextToken().equalsIgnoreCase(VCMODL.ElectricalStimulus) ||
		!tokens.nextToken().equalsIgnoreCase(GENERAL_PROTOCOL) ||
		!tokens.nextToken().equalsIgnoreCase(VCMODL.BeginBlock)
		){
		throw new RuntimeException(ElectricalStimulus.class.getName()+".parameterVCMLRead, unexpected token ");
	}
	String token = null;
	while (tokens.hasMoreTokens()){
		token = tokens.nextToken();
		if (token.equalsIgnoreCase(VCMODL.EndBlock)){
			break;
		}
		if (token.equalsIgnoreCase(VCMODL.Parameter)){
			String roleName = tokens.nextToken();
			int roleInt = -1;
			for(int i =0;i<RoleTags.length;i+= 1){
				if(roleName.equals(RoleTags[i])){
					roleInt = i;
					break;
				}
			}
			if(roleInt == -1){
				throw new RuntimeException(ElectricalStimulus.class.getName()+".parameterVCMLRead, unexpected token for roleName "+roleName);
			}
			String parameterName = tokens.nextToken();
			Expression exp = new Expression(tokens);
			
			String unitsString = tokens.nextToken();
			cbit.vcell.units.VCUnitDefinition unitDef = cbit.vcell.units.VCUnitDefinition.UNIT_TBD;
			if (unitsString.startsWith("[")){
				while (!unitsString.endsWith("]")){
					String tempToken = tokens.nextToken();
					unitsString = unitsString + " " + tempToken;
				}
				//
				// now string starts with '[' and ends with ']'
				//
				unitDef = cbit.vcell.units.VCUnitDefinition.getInstance(unitsString.substring(1,unitsString.length()-1));
			}else{
				tokens.pushToken(unitsString);
			}
			ElectricalStimulusParameter esp = new ElectricalStimulusParameter(parameterName,exp,roleInt,unitDef);
			esParametersV.add(esp);
		}else{
			throw new RuntimeException(ElectricalStimulus.class.getName()+".parameterVCMLRead, unexpected token for paramter tag "+token);
		}
	}

	if(esParametersV.size() > 0){
		ElectricalStimulusParameter[] espArr = new ElectricalStimulusParameter[esParametersV.size()];
		esParametersV.copyInto(espArr);
		setElectricalStimulusParameters(espArr);
	}else{
		setElectricalStimulusParameters(null);
	}
}


/**
 * This method was created by a SmartGuide.
 * @param ps java.io.PrintStream
 * @exception java.lang.Exception The exception description.
 */
public final void parameterVCMLWrite(java.io.PrintWriter pw) {
	
	//Example:
	//
	//	ElectricalStimulus General_Protocol {
	//      Parameter UserDefined a 10;
	//      Parameter Voltage b 3;
	//      Parameter Current c d/2; [pAmps]
	//  }
	//
	//

	pw.println("\t\t"+VCMODL.ElectricalStimulus+" "+GENERAL_PROTOCOL+" "+VCMODL.BeginBlock+" ");

	ElectricalStimulusParameter parameters[] = getElectricalStimulusParameters();
	if (parameters!=null){
		for (int i=0;i<parameters.length;i++){
			ElectricalStimulusParameter parm = parameters[i];
			String roleName = RoleTags[parm.getRole()];
			cbit.vcell.units.VCUnitDefinition unit = parm.getUnitDefinition();
			pw.println("\t\t\t"+
				VCMODL.Parameter+" "+
				roleName + " " +
				parm.getName()+" "+
				parm.getExpression().infix() + ";" +
				(unit != null?" ["+unit.getSymbol()+"]":""));
		}
	}
	
	pw.println("\t\t"+VCMODL.EndBlock+" ");
}


/**
 * Insert the method's description here.
 * Creation date: (12/31/2004 11:34:56 AM)
 * @param evt java.beans.PropertyChangeEvent
 */
public void propertyChange(java.beans.PropertyChangeEvent event) {
	try {
		if (event.getSource() == this && event.getPropertyName().equals("electricalStimulusParameters")){
			ElectricalStimulusParameter oldValues[] = (ElectricalStimulusParameter[])event.getOldValue();
			for (int i = 0; oldValues!=null && i < oldValues.length; i++){
				oldValues[i].removePropertyChangeListener(this);
			}
			ElectricalStimulusParameter newValues[] = (ElectricalStimulusParameter[])event.getNewValue();
			for (int i = 0; newValues != null && i < newValues.length; i++){
				newValues[i].addPropertyChangeListener(this);
			}
			cleanupParameters();
			resolveUndefinedUnits();
		}
		if (event.getSource() instanceof ElectricalStimulusParameter){
			cleanupParameters();
			resolveUndefinedUnits();
		}
	}catch (Throwable e){
		e.printStackTrace(System.out);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (5/11/2004 6:19:00 PM)
 * @param bReading boolean
 */
public void reading(boolean argReading) {
	if (argReading == bReading){
		throw new RuntimeException("flag conflict");
	}
	this.bReading = argReading;
	if (!bReading){
		resolveUndefinedUnits();
	}
}


/**
 * Insert the method's description here.
 * Creation date: (12/31/2004 11:30:54 AM)
 */
public void refreshDependencies() {
	removePropertyChangeListener(this);
	removeVetoableChangeListener(this);
	addPropertyChangeListener(this);
	addVetoableChangeListener(this);

	for (int i = 0; i < fieldElectricalStimulusParameters.length; i++){
		fieldElectricalStimulusParameters[i].removePropertyChangeListener(this);
		fieldElectricalStimulusParameters[i].addPropertyChangeListener(this);
	}
	
	//refreshUnits();
	resolveUndefinedUnits();
}


/**
 * Insert the method's description here.
 * Creation date: (9/22/2003 9:51:49 AM)
 * @param parameterName java.lang.String
 */
void removeAllUnresolvedParameters() {
	setUnresolvedParameters(new UnresolvedParameter[0]);
}


/**
 * This method was created by a SmartGuide.
 * @param name java.lang.String
 * @exception java.lang.Exception The exception description.
 */
protected void removeElectricalStimulusParameter(ElectricalStimulusParameter parameter) throws PropertyVetoException {
	ElectricalStimulusParameter newElectricalStimulusParameters[] = (ElectricalStimulusParameter[])BeanUtils.removeElement(fieldElectricalStimulusParameters,parameter);
	setElectricalStimulusParameters(newElectricalStimulusParameters);
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
 * Insert the method's description here.
 * Creation date: (9/22/2003 9:51:49 AM)
 * @param parameterName java.lang.String
 */
protected void removeUnresolvedParameter(UnresolvedParameter parameter) {
	for (int i = 0; i < fieldUnresolvedParameters.length; i++){
		if (fieldUnresolvedParameters[i] == parameter){
			UnresolvedParameter newUnresolvedParameters[] = (UnresolvedParameter[])BeanUtils.removeElement(fieldUnresolvedParameters,parameter);
			setUnresolvedParameters(newUnresolvedParameters);
			return;
		}
	}
	throw new RuntimeException("UnresolvedParameter '"+parameter.getName()+"' not found");
}


/**
 * Insert the method's description here.
 * Creation date: (9/22/2003 9:51:49 AM)
 * @param parameterName java.lang.String
 */
void removeUnresolvedParameters(cbit.vcell.parser.SymbolTable symbolTable) {
	ElectricalStimulus.UnresolvedParameter unresolvedParms[] = (ElectricalStimulus.UnresolvedParameter[])fieldUnresolvedParameters.clone();
	for (int i = 0; i < unresolvedParms.length; i++){
		try {
			SymbolTableEntry ste = symbolTable.getEntry(unresolvedParms[i].getName());
			if (ste != unresolvedParms[i]){
				unresolvedParms = (ElectricalStimulus.UnresolvedParameter[])BeanUtils.removeElement(unresolvedParms,unresolvedParms[i]);
				i--;
			}
		}catch (ExpressionBindingException e){
			e.printStackTrace(System.out);
			throw new RuntimeException("unexpected exception while removing Unresolved Parameters: "+e.getMessage());
		}
	}
	setUnresolvedParameters(unresolvedParms);
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
 * Insert the method's description here.
 * Creation date: (5/24/01 4:05:36 PM)
 */
public void renameParameter(String oldName, String newName) throws ExpressionException, java.beans.PropertyVetoException{
	if (oldName==null || newName==null){
		throw new RuntimeException("renameParameter from '"+oldName+"' to '"+newName+"', nulls are not allowed");
	}
	NameScope nameScope = getNameScope();
	String prefix = AbstractNameScope.getPrefix(newName);
	String strippedName = AbstractNameScope.getStrippedIdentifier(newName);
	if (prefix!=null){
		NameScope prefixNameScope = nameScope.getNameScopeFromPrefix(prefix);
		if (prefixNameScope != nameScope){ // from different namescope, then strip any prefix.
			throw new ExpressionException("reaction parameter cannot be renamed to '"+newName+"', name is scoped to '"+prefixNameScope.getName()+"'");
		}
	}
	newName = strippedName;
	if (oldName.equals(newName)){
		throw new RuntimeException("renameParameter from '"+oldName+"' to '"+newName+"', same name not allowed");
	}
	ElectricalStimulusParameter parameter = getElectricalStimulusParameter(oldName);
	if (parameter!=null){
		//
		// must change name in ElectricalStimulusParameter directly
		// then change all references to this name in the other parameter's expressions.
		//
		ElectricalStimulusParameter newParameters[] = (ElectricalStimulusParameter[])getElectricalStimulusParameters().clone();
		//
		// replaces parameter with name 'oldName' with new parameter with name 'newName' and original expression.
		//
		for (int i = 0; i < newParameters.length; i++){
			if (newParameters[i] == parameter){
				newParameters[i] = new ElectricalStimulusParameter(newName,parameter.getExpression(),parameter.getRole(),parameter.getUnitDefinition());
			}
		}
		//
		// go through all parameters' expressions and replace references to 'oldName' with 'newName'
		//
		for (int i = 0; i < newParameters.length; i++){ 
			Expression newExp = new Expression(getElectricalStimulusParameters()[i].getExpression());
			newExp.substituteInPlace(new Expression(oldName),new Expression(newName));
			//
			// if expression changed, create a new ElectricalStimulusParameter
			//
//			if (!getElectricalStimulusParameters()[i].getExpression().compareEqual(newExp)){
				newParameters[i] = new ElectricalStimulusParameter(newParameters[i].getName(),newExp,newParameters[i].getRole(),newParameters[i].getUnitDefinition());
//			}
		}
		setElectricalStimulusParameters(newParameters);

		// 
		// rebind all expressions
		//
		for (int i = 0; i < newParameters.length; i++){
			newParameters[i].getExpression().bindExpression(this);
		}
		////
		//// then, if this parameter was a "requiredIdentifier", must also alter that indexed property (identifier array)
		////
		//String requiredIdentifiers[] = getRequiredIdentifiers();
		//String newRequiredIdentifiers[] = null;
		//for (int i = 0; i < requiredIdentifiers.length; i++){
			//if (parameter.getName().equals(requiredIdentifiers[i])){
				//newRequiredIdentifiers = (String[])requiredIdentifiers.clone();
				//newRequiredIdentifiers[i] = newName;
				//break;
			//}
		//}
		//if (newRequiredIdentifiers!=null){
			//setRequiredIdentifiers(newRequiredIdentifiers);
		//}

		//
		// clean up dangling parameters (those not reachable from the 'required' parameters).
		//
		try {
			cleanupParameters();
		}catch (Exception e){
			e.printStackTrace(System.out);
			throw new RuntimeException(e.getMessage());
		}
	}
}


/**
 * Insert the method's description here.
 * Creation date: (4/13/2004 3:09:21 PM)
 */
public void resolveUndefinedUnits() {
	//
	// try to fix units for UserDefined parameters
	//
	if (!bResolvingUnits){
		try {
			bResolvingUnits = true;
			boolean bAnyTBDUnits = false;
			for (int i=0;i<fieldElectricalStimulusParameters.length;i++){
				if (fieldElectricalStimulusParameters[i].getUnitDefinition()==null){
					return; // not ready to resolve units yet
				}else if (fieldElectricalStimulusParameters[i].getUnitDefinition().compareEqual(cbit.vcell.units.VCUnitDefinition.UNIT_TBD)){
					bAnyTBDUnits = true;
				}
			}
			//
			// try to resolve TBD units (will fail if units are inconsistent) ... but these errors are collected in Kinetics.getIssues().
			//
			if (bAnyTBDUnits){
				cbit.vcell.units.VCUnitDefinition vcUnitDefinitions[] = cbit.vcell.parser.VCUnitEvaluator.suggestUnitDefinitions(fieldElectricalStimulusParameters);
				for (int i = 0; i < fieldElectricalStimulusParameters.length; i++){
					if (!fieldElectricalStimulusParameters[i].getUnitDefinition().compareEqual(vcUnitDefinitions[i])){
						fieldElectricalStimulusParameters[i].setUnitDefinition(vcUnitDefinitions[i]);
					}
				}
				//System.out.println("successfully completed Kinetics.resolveUndefinedUnits() for ReactionStep '"+getReactionStep()+"'");
			}
		}catch (ExpressionBindingException e){
			System.out.println("Kinetics.resolveUndefinedUnits(): EXCEPTION: "+e.getMessage());
		}catch (Exception e){
			System.out.println("Kinetics.resolveUndefinedUnits(): EXCEPTION: "+e.getMessage());
		}finally{
			bResolvingUnits = false;
		}
	}
}


/**
 * Sets the annotation property (java.lang.String) value.
 * @param annotation The new value for the property.
 * @see #getAnnotation
 */
public void setAnnotation(java.lang.String annotation) {
	String oldValue = fieldAnnotation;
	fieldAnnotation = annotation;
	firePropertyChange("annotation", oldValue, annotation);
}


/**
 * Sets the electricalStimulusParameters property (cbit.vcell.mapping.ElectricalStimulus.ElectricalStimulusParameter[]) value.
 * @param electricalStimulusParameters The new value for the property.
 * @exception java.beans.PropertyVetoException The exception description.
 * @see #getElectricalStimulusParameters
 */
private void setElectricalStimulusParameters(ElectricalStimulus.ElectricalStimulusParameter[] electricalStimulusParameters) throws java.beans.PropertyVetoException {
	ElectricalStimulus.ElectricalStimulusParameter[] oldValue = fieldElectricalStimulusParameters;
	fireVetoableChange("electricalStimulusParameters", oldValue, electricalStimulusParameters);
	fieldElectricalStimulusParameters = electricalStimulusParameters;
	firePropertyChange("electricalStimulusParameters", oldValue, electricalStimulusParameters);
}


/**
 * Sets the electrode property (cbit.vcell.mapping.Electrode) value.
 * @param electrode The new value for the property.
 * @exception java.beans.PropertyVetoException The exception description.
 * @see #getElectrode
 */
public void setElectrode(Electrode electrode) throws java.beans.PropertyVetoException {
	Electrode oldValue = fieldElectrode;
	fireVetoableChange("electrode", oldValue, electrode);
	fieldElectrode = electrode;
	firePropertyChange("electrode", oldValue, electrode);
}


/**
 * Sets the name property (java.lang.String) value.
 * @param name The new value for the property.
 * @exception java.beans.PropertyVetoException The exception description.
 * @see #getName
 */
public void setName(java.lang.String name) throws java.beans.PropertyVetoException {
	String oldValue = fieldName;
	fireVetoableChange("name", oldValue, name);
	fieldName = name;
	firePropertyChange("name", oldValue, name);
}


/**
 * This method was created by a SmartGuide.
 * @param expressionString java.lang.String
 * @exception java.lang.Exception The exception description.
 */
public void setParameterValue(ElectricalStimulusParameter parm, Expression exp) throws ExpressionException, PropertyVetoException {
	Parameter p = getElectricalStimulusParameter(parm.getName());
	if (p != parm){
		throw new RuntimeException("parameter "+parm.getName()+" not found");
	}
	Expression oldExpression = parm.getExpression();
	boolean bBound = false;
	try {
		ElectricalStimulusParameter newElectricalStimulusParameters[] = (ElectricalStimulusParameter[])fieldElectricalStimulusParameters.clone();
		String symbols[] = exp.getSymbols(getNameScope());
		Vector symbolsToAdd = new Vector();
		for (int i = 0; symbols!=null && i < symbols.length; i++){
			if (getEntry(symbols[i])==null){
				symbolsToAdd.add(symbols[i]);
			}
		}
		for (int i = 0; i < symbolsToAdd.size(); i++){
			newElectricalStimulusParameters = (ElectricalStimulusParameter[])BeanUtils.addElement(newElectricalStimulusParameters,
				new ElectricalStimulusParameter((String)symbolsToAdd.elementAt(i),new Expression(0.0),ROLE_UserDefined,cbit.vcell.units.VCUnitDefinition.UNIT_TBD));
		}
		parm.setExpression(exp);
		setElectricalStimulusParameters(newElectricalStimulusParameters);
		exp.bindExpression(this);
		bBound = true;
	}finally{
		try {
			if (!bBound){
				parm.setExpression(oldExpression);
			}
			cleanupParameters();
		}catch (PropertyVetoException e){
			e.printStackTrace(System.out);
			throw new RuntimeException(e.getMessage());
		}
	}
}


/**
 * Sets the unresolvedParameters property (cbit.vcell.model.UnresolvedParameter[]) value.
 * @param unresolvedParameters The new value for the property.
 * @see #getUnresolvedParameters
 */
private void setUnresolvedParameters(UnresolvedParameter[] unresolvedParameters) {
	UnresolvedParameter[] oldValue = fieldUnresolvedParameters;
	fieldUnresolvedParameters = unresolvedParameters;
	firePropertyChange("unresolvedParameters", oldValue, unresolvedParameters);
}


public void vetoableChange(java.beans.PropertyChangeEvent e) throws PropertyVetoException {
}
}