package cbit.vcell.parser;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.util.*;
/**
 * This type was created in VisualAge.
 */
public class VariableSymbolTable implements SymbolTable {
	Vector<SymbolTableEntry> varList = new Vector<SymbolTableEntry>();
/**
 * VariableSymbolTable constructor comment.
 */
public VariableSymbolTable() {
	super();
}
/**
 * This method was created in VisualAge.
 * @param var cbit.vcell.math.Variable
 */
public void addVar(SymbolTableEntry var) {
	if (!varList.contains(var)){
		varList.addElement(var);
	}
}
/**
 * getEntry method comment.
 */
public SymbolTableEntry getEntry(String identifierString) throws ExpressionBindingException {
	for (int i=0;i<varList.size();i++){
		SymbolTableEntry var = varList.elementAt(i);
		if (var.getName().equals(identifierString)){
			return var;
		}
	}
	return null;
}
}
