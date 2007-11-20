package org.vcell.ncbc.physics.engine;

import org.vcell.expression.ExpressionBindingException;
import org.vcell.expression.IExpression;
import org.vcell.expression.NameScope;
import org.vcell.expression.SymbolTable;
import org.vcell.expression.SymbolTableEntry;

import cbit.vcell.math.Function;
/**
 * Insert the type's description here.
 * Creation date: (2/12/2002 1:01:13 PM)
 * @author: Jim Schaff
 */
public abstract class ElectricalDevice implements SymbolTable {
	private String name = null;
	private String voltageName = null;
	private Function initialVoltageFunction = null;

	private java.util.Hashtable symbolHashtable = new java.util.Hashtable();
		
	class Symbol implements org.vcell.expression.SymbolTableEntry {
		
		int index = -1;
		String name = null;

		Symbol(String argName) {
			this.name = argName;
		}
		
		public boolean isConstant() { 
			return false; 
		}
		public IExpression getExpression() { 
			throw new RuntimeException("not supported"); 
		}
		public String getName() { 
			return name; 
		}
		public int getIndex() { 
			return index; 
		}
		public NameScope getNameScope() { 
			return null; 
		}
		public org.vcell.units.VCUnitDefinition getUnitDefinition(){
			return null;
		}
		public double getConstantValue() throws org.vcell.expression.ExpressionException { 
			throw new org.vcell.expression.ExpressionException("not supported"); 
		}
		public String toString() {
			return "ConstraintSolver$Symbol(\""+name+"\", index="+index+")";
		}
	};

	private Symbol voltageSymbol = null;
		
/**
 * ElectricalDevice constructor comment.
 */
public ElectricalDevice(String argName, String argVoltageName, Function argInitialVoltageFunction) throws ExpressionBindingException {
	this.name = argName;
	this.voltageName = argVoltageName;
	this.initialVoltageFunction = argInitialVoltageFunction;
	this.voltageSymbol = new Symbol(voltageName);
}
/**
 * Insert the method's description here.
 * Creation date: (2/19/2002 11:37:04 AM)
 * @return boolean
 */
public abstract boolean getCalculateVoltage();
/**
 * Insert the method's description here.
 * Creation date: (2/12/2002 1:29:31 PM)
 * @return double
 */
public abstract double getCapacitance_pF();
/**
 * Insert the method's description here.
 * Creation date: (2/12/2002 2:15:22 PM)
 * @return java.lang.String
 */
public final String getCapName() {
	return "C_"+getName();
}
/**
 * Insert the method's description here.
 * Creation date: (2/12/2002 1:31:24 PM)
 * @return cbit.vcell.parser.Expression
 */
public abstract IExpression getCurrentSourceExpression();
/**
 * getEntry method comment.
 */
public org.vcell.expression.SymbolTableEntry getEntry(java.lang.String identifierString) {
	cbit.vcell.model.ReservedSymbol rs = cbit.vcell.model.ReservedSymbol.fromString(identifierString);
	if (rs!=null){
		return rs;
	}
	if (voltageSymbol.getName().equals(identifierString)){
		return voltageSymbol;
	}
	Symbol symbol = (Symbol)symbolHashtable.get(identifierString);
	if (symbol==null){
		symbolHashtable.put(identifierString,new Symbol(identifierString));
		symbol = (Symbol)symbolHashtable.get(identifierString);
	}
	return symbol;
}
/**
 * Insert the method's description here.
 * Creation date: (2/12/2002 2:15:22 PM)
 * @return java.lang.String
 */
public final String getIName() {
	return "I_"+getName();
}
/**
 * Insert the method's description here.
 * Creation date: (2/20/2002 9:21:32 PM)
 * @return cbit.vcell.math.Function
 */
public Function getInitialVoltageFunction() {
	return initialVoltageFunction;
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
 * Creation date: (2/19/2002 11:37:04 AM)
 * @return boolean
 */
public abstract boolean getResolved();
/**
 * Insert the method's description here.
 * Creation date: (2/12/2002 2:18:15 PM)
 * @return java.lang.String
 */
public final String getSourceName() {
	return "F_"+getName();
}
/**
 * Insert the method's description here.
 * Creation date: (2/20/2002 11:33:50 AM)
 * @return cbit.vcell.parser.SymbolTableEntry[]
 */
public SymbolTableEntry[] getUnresolvedSymbols() {
	java.util.Vector symbolList = new java.util.Vector(symbolHashtable.values());
	SymbolTableEntry symbolTableEntries[] = new SymbolTableEntry[symbolList.size()];
	symbolList.copyInto(symbolTableEntries);
	return symbolTableEntries;
}
/**
 * Insert the method's description here.
 * Creation date: (2/12/2002 2:15:22 PM)
 * @return java.lang.String
 */
public final String getVName() {
	return voltageName;
}
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
 * Creation date: (2/12/2002 4:02:31 PM)
 * @return java.lang.String
 */
public String toString() {
	return getName();
}
}
