package cbit.vcell.exp;
/**
 * Insert the type's description here.
 * Creation date: (12/31/2004 6:08:52 AM)
 * @author: Jim Schaff
 */
public class Experiment implements cbit.util.Matchable, java.io.Serializable, cbit.vcell.parser.ScopedSymbolTable {
// name, description
	private java.lang.String fieldName = new String();
	protected transient java.beans.VetoableChangeSupport vetoPropertyChange;
	protected transient java.beans.PropertyChangeSupport propertyChange;
	private java.lang.String fieldDescription = new String();
	private Experiment.ExperimentNameScope nameScope = new Experiment.ExperimentNameScope();
	private Experiment.ExperimentParameter[] fieldExperimentParameters = new Experiment.ExperimentParameter[0];

	
	public class ExperimentNameScope extends cbit.vcell.model.BioNameScope {
		private final cbit.vcell.parser.NameScope children[] = new cbit.vcell.parser.NameScope[0]; // always empty
		public ExperimentNameScope(){
			super();
		}
		public cbit.vcell.parser.NameScope[] getChildren() {
			return children;
		}
		public String getName() {
			return cbit.util.TokenMangler.fixTokenStrict(Experiment.this.getName());
		}
		public cbit.vcell.parser.NameScope getParent() {
			//System.out.println("ExperimentNameScope.getParent() returning null ... no parent");
			return null;
		}
		public cbit.vcell.parser.ScopedSymbolTable getScopedSymbolTable() {
			return Experiment.this;
		}
		public boolean isPeer(cbit.vcell.parser.NameScope nameScope){
			return ((nameScope instanceof cbit.vcell.mapping.MathMapping.MathMappingNameScope) && nameScope.isPeer(this));
		}

	}

	public static final int ROLE_UserDefined	= 0;
	public static final int ROLE_pH				= 1;
	public static final int ROLE_TemperatureK	= 2;
	public static final int ROLE_Concentration	= 3;
	public static final int ROLE_Voltage		= 4;
	public static final int ROLE_Affinity		= 5;
	public static final int ROLE_RateConstant	= 6;
	public static final int ROLE_Permeability	= 7;
	public static final int ROLE_Conductivity	= 8;
	public static final int ROLE_Volume			= 9;
//	public static final int ROLE_Measurement    = 10;

	public static final int NUM_ROLES			= 10;

	
	private static final String RoleDescs[] = {
		"user defined",
		"pH",
		"Temperature (deg K)",
		"Concentration",
		"Voltage",
		"Affinity (Kd)",
		"Rate Constant",
		"Permeability",
		"Conductivity",
		"Volume",
	};


	private static final String DefaultNames[] = {
		null,
		"pH",
		"T",
		null,
		"V",
		"Kd",
		"k",
		"P",
		"C",
		"Volume"
	};


	
	public class ExperimentParameter extends cbit.vcell.model.Parameter {
		
		private String fieldParameterName = null;
		private cbit.vcell.parser.Expression fieldParameterExpression = null;
		private int fieldParameterRole = -1;
		private cbit.vcell.units.VCUnitDefinition fieldUnitDefinition = null;
		
		protected ExperimentParameter(String argName, cbit.vcell.parser.Expression expression, int argRole, cbit.vcell.units.VCUnitDefinition argUnitDefinition) {
			if (argName == null){
				throw new IllegalArgumentException("parameter name is null");
			}
			if (argName.length()<1){
				throw new IllegalArgumentException("parameter name is zero length");
			}
			this.fieldParameterName = argName;
			this.fieldParameterExpression = expression;
			this.fieldUnitDefinition = argUnitDefinition;
			if (argRole >= 0 && argRole < NUM_ROLES){
				this.fieldParameterRole = argRole;
			}else{
				throw new IllegalArgumentException("parameter 'role' = "+argRole+" is out of range");
			}
		}


		public boolean compareEqual(cbit.util.Matchable obj) {
			if (!(obj instanceof ExperimentParameter)){
				return false;
			}
			ExperimentParameter mp = (ExperimentParameter)obj;
			if (!super.compareEqual0(mp)){
				return false;
			}
			if (fieldParameterRole != mp.fieldParameterRole){
				return false;
			}
			
			return true;
		}


		public boolean isExpressionEditable(){
			return true;
		}

		public boolean isUnitEditable(){
			return true;
		}

		public boolean isNameEditable(){
			return true;
		}

		public double getConstantValue() throws cbit.vcell.parser.ExpressionException {
			return this.fieldParameterExpression.evaluateConstant();
		}      


		public cbit.vcell.parser.Expression getExpression() {
			return this.fieldParameterExpression;
		}


		public int getIndex() {
			return -1;
		}


		public String getName(){ 
			return this.fieldParameterName; 
		}   


		public cbit.vcell.parser.NameScope getNameScope() {
			return Experiment.this.nameScope;
		}

		public int getRole() {
			return this.fieldParameterRole;
		}

		public cbit.vcell.units.VCUnitDefinition getUnitDefinition() {
			return fieldUnitDefinition;
		}

		public void setUnitDefinition(cbit.vcell.units.VCUnitDefinition unitDefinition) throws java.beans.PropertyVetoException {
			cbit.vcell.units.VCUnitDefinition oldValue = fieldUnitDefinition;
			super.fireVetoableChange("unitDefinition", oldValue, unitDefinition);
			fieldUnitDefinition = unitDefinition;
			super.firePropertyChange("unitDefinition", oldValue, unitDefinition);
		}
		public void setExpression(cbit.vcell.parser.Expression expression) throws java.beans.PropertyVetoException {
			cbit.vcell.parser.Expression oldValue = fieldParameterExpression;
			super.fireVetoableChange("expression", oldValue, expression);
			fieldParameterExpression = expression;
			super.firePropertyChange("expression", oldValue, expression);
		}
		public void setName(java.lang.String name) throws java.beans.PropertyVetoException {
			String oldValue = fieldParameterName;
			super.fireVetoableChange("name", oldValue, name);
			fieldParameterName = name;
			super.firePropertyChange("name", oldValue, name);
		}

	}
	private cbit.vcell.exp.Experiment.ExperimentParameter[] fieldParameters = null;
	private cbit.vcell.constraints.AbstractConstraint[] fieldConditions = null;

/**
 * Experiment constructor comment.
 */
public Experiment() {
	super();
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
	return false;
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
 * Gets the conditions property (cbit.vcell.constraints.AbstractConstraint[]) value.
 * @return The conditions property value.
 * @see #setConditions
 */
public cbit.vcell.constraints.AbstractConstraint[] getConditions() {
	return fieldConditions;
}


/**
 * Gets the conditions index property (cbit.vcell.constraints.AbstractConstraint) value.
 * @return The conditions property value.
 * @param index The index value into the property array.
 * @see #setConditions
 */
public cbit.vcell.constraints.AbstractConstraint getConditions(int index) {
	return getConditions()[index];
}


/**
 * Gets the description property (java.lang.String) value.
 * @return The description property value.
 * @see #setDescription
 */
public java.lang.String getDescription() {
	return fieldDescription;
}


/**
 * getEntry method comment.
 */
public cbit.vcell.parser.SymbolTableEntry getEntry(java.lang.String identifierString) throws cbit.vcell.parser.ExpressionBindingException {
	return null;
}


/**
 * Insert the method's description here.
 * Creation date: (12/31/2004 7:04:37 AM)
 * @return cbit.vcell.parser.SymbolTableEntry
 * @param identifier java.lang.String
 */
public cbit.vcell.parser.SymbolTableEntry getLocalEntry(java.lang.String identifier) throws cbit.vcell.parser.ExpressionBindingException {
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
 * Creation date: (12/31/2004 7:04:37 AM)
 * @return cbit.vcell.parser.NameScope
 */
public cbit.vcell.parser.NameScope getNameScope() {
	return nameScope;
}


/**
 * Gets the parameters property (cbit.vcell.exp.Experiment.ExperimentParameter[]) value.
 * @return The parameters property value.
 * @see #setParameters
 */
public cbit.vcell.exp.Experiment.ExperimentParameter[] getParameters() {
	return fieldParameters;
}


/**
 * Gets the parameters index property (cbit.vcell.exp.Experiment.ExperimentParameter) value.
 * @return The parameters property value.
 * @param index The index value into the property array.
 * @see #setParameters
 */
public cbit.vcell.exp.Experiment.ExperimentParameter getParameters(int index) {
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
public synchronized boolean hasListeners(java.lang.String propertyName) {
	return getPropertyChange().hasListeners(propertyName);
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
 * Sets the conditions property (cbit.vcell.constraints.AbstractConstraint[]) value.
 * @param conditions The new value for the property.
 * @exception java.beans.PropertyVetoException The exception description.
 * @see #getConditions
 */
public void setConditions(cbit.vcell.constraints.AbstractConstraint[] conditions) throws java.beans.PropertyVetoException {
	cbit.vcell.constraints.AbstractConstraint[] oldValue = fieldConditions;
	fireVetoableChange("conditions", oldValue, conditions);
	fieldConditions = conditions;
	firePropertyChange("conditions", oldValue, conditions);
}


/**
 * Sets the conditions index property (cbit.vcell.constraints.AbstractConstraint[]) value.
 * @param index The index value into the property array.
 * @param conditions The new value for the property.
 * @see #getConditions
 */
public void setConditions(int index, cbit.vcell.constraints.AbstractConstraint conditions) {
	cbit.vcell.constraints.AbstractConstraint oldValue = fieldConditions[index];
	fieldConditions[index] = conditions;
	if (oldValue != null && !oldValue.equals(conditions)) {
		firePropertyChange("conditions", null, fieldConditions);
	};
}


/**
 * Sets the description property (java.lang.String) value.
 * @param description The new value for the property.
 * @see #getDescription
 */
public void setDescription(java.lang.String description) {
	String oldValue = fieldDescription;
	fieldDescription = description;
	firePropertyChange("description", oldValue, description);
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
 * Sets the parameters property (cbit.vcell.exp.Experiment.ExperimentParameter[]) value.
 * @param parameters The new value for the property.
 * @exception java.beans.PropertyVetoException The exception description.
 * @see #getParameters
 */
public void setParameters(cbit.vcell.exp.Experiment.ExperimentParameter[] parameters) throws java.beans.PropertyVetoException {
	cbit.vcell.exp.Experiment.ExperimentParameter[] oldValue = fieldParameters;
	fireVetoableChange("parameters", oldValue, parameters);
	fieldParameters = parameters;
	firePropertyChange("parameters", oldValue, parameters);
}


/**
 * Sets the parameters index property (cbit.vcell.exp.Experiment.ExperimentParameter[]) value.
 * @param index The index value into the property array.
 * @param parameters The new value for the property.
 * @see #getParameters
 */
public void setParameters(int index, cbit.vcell.exp.Experiment.ExperimentParameter parameters) {
	cbit.vcell.exp.Experiment.ExperimentParameter oldValue = fieldParameters[index];
	fieldParameters[index] = parameters;
	if (oldValue != null && !oldValue.equals(parameters)) {
		firePropertyChange("parameters", null, fieldParameters);
	};
}
}