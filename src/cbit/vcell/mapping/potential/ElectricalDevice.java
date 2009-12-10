package cbit.vcell.mapping.potential;
import java.beans.PropertyVetoException;
import java.util.Map;

import org.vcell.util.Matchable;
import org.vcell.util.TokenMangler;

import cbit.vcell.parser.*;
import cbit.vcell.mapping.MathMapping;
import cbit.vcell.model.BioNameScope;
import cbit.vcell.model.Parameter;
import cbit.vcell.model.ReservedSymbol;
import cbit.vcell.units.VCUnitDefinition;
/**
 * Insert the type's description here.
 * Creation date: (2/12/2002 1:01:13 PM)
 * @author: Jim Schaff
 */
public abstract class ElectricalDevice implements ScopedSymbolTable {
	private String name = null;
	private ElectricalDeviceNameScope nameScope = new ElectricalDeviceNameScope();
	protected MathMapping mathMapping = null; // for determining NameScope parent only
	private Expression dependentVoltageExpression = null;

	public static final int ROLE_TotalCurrent					= 0;
	public static final int ROLE_TotalCurrentDensity			= 1;
	public static final int ROLE_TransmembraneCurrentDensity	= 2;
	public static final int ROLE_Voltage						= 3;
	public static final int ROLE_UserDefined					= 4;
	public static final int NUM_ROLES		= 5;
	public static final String DefaultNames[] = {
		"LumpedI",
		"I",
		"F",
		"V",
		null
	};
	private ElectricalDevice.ElectricalDeviceParameter[] fieldParameters = null;
	
	public class ElectricalDeviceNameScope extends BioNameScope {
		private final NameScope children[] = new NameScope[0]; // always empty
		public ElectricalDeviceNameScope(){
			super();
		}
		public NameScope[] getChildren() {
			//
			// no children to return
			//
			return children;
		}
		public String getName() {
			return TokenMangler.fixTokenStrict(ElectricalDevice.this.getName());
		}
		public NameScope getParent() {
			if (ElectricalDevice.this.mathMapping != null){
				return ElectricalDevice.this.mathMapping.getNameScope();
			}else{
				return null;
			}
		}
		public ScopedSymbolTable getScopedSymbolTable() {
			return ElectricalDevice.this;
		}
	}

	public class ElectricalDeviceParameter extends Parameter {

		private int fieldParameterRole = -1;
		private String fieldParameterName = null;
		private Expression fieldParameterExpression = null;
		private VCUnitDefinition fieldVCUnitDefinition = null;


		public ElectricalDeviceParameter(String parmName, Expression argExpression, int argRole, VCUnitDefinition argVCUnitDefinition) {
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

		public boolean compareEqual(Matchable obj) {
			if (!(obj instanceof ElectricalDeviceParameter)){
				return false;
			}
			ElectricalDeviceParameter edp = (ElectricalDeviceParameter)obj;
			if (!super.compareEqual0(edp)){
				return false;
			}
			if (fieldParameterRole != edp.fieldParameterRole){
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

		public void setUnitDefinition(VCUnitDefinition unit) {
			throw new RuntimeException("Unit is not editable");
		}
		
		public boolean isNameEditable(){
			return true;
		}

		public NameScope getNameScope(){
			return ElectricalDevice.this.getNameScope();
		}

		public void setName(java.lang.String name) throws java.beans.PropertyVetoException {
			String oldValue = fieldParameterName;
			super.fireVetoableChange("name", oldValue, name);
			fieldParameterName = name;
			super.firePropertyChange("name", oldValue, name);
		}

		public void setExpression(Expression expression) throws java.beans.PropertyVetoException {
			expression = new Expression(expression);
//			try {
//				expression.bindExpression(ElectricalDevice.this);
//			} catch (ExpressionBindingException e) {
//				e.printStackTrace();
//				throw new PropertyVetoException(e.getMessage(),null);
//			}
			Expression oldValue = fieldParameterExpression;
			super.fireVetoableChange("expression", oldValue, expression);
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
		
		public String getName() {
			return fieldParameterName;
		}

		public int getIndex() {
			return -1;
		}

		public int getRole() {
			return fieldParameterRole;
		}

		public boolean isDescriptionEditable() {
			return false;
		}

	}

/**
 * ElectricalDevice constructor comment.
 */
public ElectricalDevice(String argName, MathMapping argMathMapping) {
	this.name = argName;
	this.mathMapping = argMathMapping;
}


/**
 * Insert the method's description here.
 * Creation date: (2/19/2002 11:37:04 AM)
 * @return boolean
 */
public abstract boolean getCalculateVoltage();


/**
 * Insert the method's description here.
 * Creation date: (4/7/2004 12:49:17 PM)
 * @return cbit.vcell.parser.Expression
 */
public Expression getDependentVoltageExpression() {
	return dependentVoltageExpression;
}


/**
 * getEntry method comment.
 */
public SymbolTableEntry getEntry(java.lang.String identifierString) throws ExpressionBindingException {
	
	SymbolTableEntry ste = getLocalEntry(identifierString);
	if (ste != null){
		return ste;
	}
			
	return getNameScope().getExternalEntry(identifierString,this);
}

public void getLocalEntries(Map<String, SymbolTableEntry> entryMap) {
	ReservedSymbol.getAll(entryMap, true, true);
	for (SymbolTableEntry ste : fieldParameters) {
		entryMap.put(ste.getName(), ste);
	}
}

public void getEntries(Map<String, SymbolTableEntry> entryMap) {	
	getNameScope().getExternalEntries(entryMap);	
}

/**
 * Insert the method's description here.
 * Creation date: (2/12/2002 2:15:22 PM)
 * @return java.lang.String
 */
public final SymbolTableEntry getTotalCurrentDensitySymbol() {
	return getParameterFromRole(ROLE_TotalCurrentDensity);
}


/**
 * Insert the method's description here.
 * Creation date: (4/7/2004 10:57:40 AM)
 * @return cbit.vcell.parser.SymbolTableEntry
 * @param identifier java.lang.String
 */
public SymbolTableEntry getLocalEntry(String identifier) throws ExpressionBindingException {
	SymbolTableEntry ste = ReservedSymbol.fromString(identifier);
	if (ste!=null){
		return ste;
	}

	ElectricalDevice.ElectricalDeviceParameter parameter = getParameter(identifier);
	
	return parameter;
}


/**
 * Insert the method's description here.
 * Creation date: (2/12/2002 2:09:38 PM)
 * @return java.lang.String
 */
public final String getName() {
	return name;
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
public ElectricalDevice.ElectricalDeviceParameter getParameter(String argName) {
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
public ElectricalDeviceParameter getParameterFromRole(int role) {
	for (int i = 0; i < fieldParameters.length; i++){
		if (fieldParameters[i] instanceof ElectricalDeviceParameter){
			ElectricalDeviceParameter electricalDeviceParameter = (ElectricalDeviceParameter)fieldParameters[i];
			if (electricalDeviceParameter.getRole() == role){
				return electricalDeviceParameter;
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
public ElectricalDevice.ElectricalDeviceParameter[] getParameters() {
	return fieldParameters;
}


/**
 * Gets the parameters index property (cbit.vcell.model.Parameter) value.
 * @return The parameters property value.
 * @param index The index value into the property array.
 * @see #setParameters
 */
public ElectricalDevice.ElectricalDeviceParameter getParameters(int index) {
	return getParameters()[index];
}


/**
 * Insert the method's description here.
 * Creation date: (4/7/2004 11:31:15 AM)
 * @return boolean
 */
public abstract boolean getResolved();


/**
 * Insert the method's description here.
 * Creation date: (4/7/2004 3:00:30 PM)
 * @return java.lang.String
 */
public final SymbolTableEntry getSourceSymbol() {
	return getParameterFromRole(ROLE_TransmembraneCurrentDensity);
}


/**
 * Insert the method's description here.
 * Creation date: (4/7/2004 2:48:20 PM)
 * @return java.lang.String
 */
public abstract SymbolTableEntry getVoltageSymbol();


/**
 * Insert the method's description here.
 * Creation date: (2/19/2002 11:37:04 AM)
 * @return boolean
 */
public abstract boolean hasCapacitance();


/**
 * Insert the method's description here.
 * Creation date: (4/22/2002 5:39:45 PM)
 * @return boolean
 */
public abstract boolean isVoltageSource();


/**
 * Insert the method's description here.
 * Creation date: (4/7/2004 12:49:17 PM)
 * @param newDependentVoltageExpression cbit.vcell.parser.Expression
 */
public void setDependentVoltageExpression(Expression newDependentVoltageExpression) {
	dependentVoltageExpression = newDependentVoltageExpression;
}


/**
 * Sets the parameters property (cbit.vcell.model.Parameter[]) value.
 * @param parameters The new value for the property.
 * @exception java.beans.PropertyVetoException The exception description.
 * @see #getParameters
 */
void setParameters(ElectricalDevice.ElectricalDeviceParameter[] parameters) {
	fieldParameters = parameters;
	for (int i = 0; i < fieldParameters.length; i++){
		try {
			if (fieldParameters[i].getExpression()!=null){
				fieldParameters[i].getExpression().bindExpression(this);
			}
		}catch (ExpressionBindingException e){
			e.printStackTrace(System.out);
		}
	}
}


/**
 * Insert the method's description here.
 * Creation date: (2/12/2002 4:02:31 PM)
 * @return java.lang.String
 */
public String toString() {
	return getName();
}
}