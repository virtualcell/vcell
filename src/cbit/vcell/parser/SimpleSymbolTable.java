package cbit.vcell.parser;
/**
 * Insert the type's description here.
 * Creation date: (1/8/2003 10:11:18 AM)
 * @author: Jim Schaff
 *
 * 
 * SimpleSymbolTable provides a simple context for expression evaluation (or other operations) that require bound expressions.
 *
 * Usage:
 *
 *   Expression exp = new Expression("a+b/c");
 *
 *   String symbols[] = exp.getSymbols();
 *   SimpleSymbolTable symbolTable = new SimpleSymbolTable(symbols);
 *   exp.bindExpression(symbolTable);
 *
 *   double values[] = new double[symbolTable.getSize()];
 *
 *   .... set values
 *
 *   double result = exp.evaluateVector(values);
 *
 *
 *   NOTE: Expression's hold their symbolTable binding within it's internal state.  So consider
 *         side-effect when binding Expressions that are externally referenced.
 */
public class SimpleSymbolTable implements cbit.vcell.parser.ScopedSymbolTable {
	
	private SimpleSymbolTableEntry steArray[] = null;
	private NameScope nameScope = null;
	
	private class SimpleSymbolTableEntry implements cbit.vcell.parser.SymbolTableEntry {
		private String name = null;
		private int index = -1;
		private NameScope nameScope = null;
		private cbit.vcell.units.VCUnitDefinition vcUnitDefinition = null;
		
		private SimpleSymbolTableEntry(String argName, int argIndex, NameScope argNameScope){
			this.name = argName;
			this.index = argIndex;
			this.nameScope = argNameScope;
		}
		private SimpleSymbolTableEntry(String argName, int argIndex, NameScope argNameScope, cbit.vcell.units.VCUnitDefinition unit){
			this.name = argName;
			this.index = argIndex;
			this.nameScope = argNameScope;
			this.vcUnitDefinition = unit;
		}
		public boolean isConstant(){
			return false;
		}
		public String getName(){
			return name;
		}
		public int getIndex(){
			return index;
		}
		public NameScope getNameScope(){
			return nameScope;
		}
		public cbit.vcell.units.VCUnitDefinition getUnitDefinition() {
			return vcUnitDefinition;
		}
		public Expression getExpression(){
			return null;
		}
		public double getConstantValue() throws cbit.vcell.parser.ExpressionException {
			throw new cbit.vcell.parser.ExpressionException("can't evaluate to constant");
		}
	};
	
public SimpleSymbolTable(String symbols[]){
	this(symbols,null);
}


public SimpleSymbolTable(String symbols[], NameScope argNameScope){
	this.nameScope = argNameScope;
	steArray = new SimpleSymbolTableEntry[symbols.length];
	for (int i=0;i<symbols.length;i++){
		steArray[i] = new SimpleSymbolTableEntry(symbols[i],i,nameScope);
	}
}


public SimpleSymbolTable(String symbols[], NameScope argNameScope, cbit.vcell.units.VCUnitDefinition units[]){
	this.nameScope = argNameScope;
	steArray = new SimpleSymbolTableEntry[symbols.length];
	for (int i=0;i<symbols.length;i++){
		steArray[i] = new SimpleSymbolTableEntry(symbols[i],i,nameScope,units[i]);
	}
}


public cbit.vcell.parser.SymbolTableEntry getEntry(String identifier) throws ExpressionBindingException {
	//
	// check if in the current scope with no scoping
	//
	SymbolTableEntry ste = getLocalEntry(identifier);
	if (ste!=null){
		return ste;
	}
	if (getNameScope()!=null){
		return getNameScope().getExternalEntry(identifier,this);
	}
	return null;
}


public cbit.vcell.parser.SymbolTableEntry getLocalEntry(String identifier) throws ExpressionBindingException {
	//
	// check if in the current scope with no scoping
	//
	for (int i = 0; i < steArray.length; i++){
		if (steArray[i].getName().equals(identifier)){
			return steArray[i];
		}
	}
	return null;
}


/**
 * Insert the method's description here.
 * Creation date: (8/1/2003 10:57:10 AM)
 * @return cbit.vcell.parser.NameScope
 */
public NameScope getNameScope() {
	return nameScope;
}


/**
 * Insert the method's description here.
 * Creation date: (1/8/2003 10:18:35 AM)
 * @return int
 */
public int getSize() {
	return steArray.length;
}


/**
 * Insert the method's description here.
 * Creation date: (8/27/2003 12:54:26 PM)
 * @return java.lang.String
 */
public String toString() {
	return getClass().getName() + "@" + Integer.toHexString(hashCode()) + " ["+((getNameScope()!=null)?(getNameScope().getName()):("null"))+"]";
}
}