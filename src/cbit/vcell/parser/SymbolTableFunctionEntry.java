package cbit.vcell.parser;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
/**
 * A <code>SymbolTableEntry</code> describes an interface for any object that
 * can represent the value corresponding to a symbol in an expression.  
 * A <code>SymbolTableEntry</code> is returned by a <code>SymbolTable</code>
 * to resolve the reference of a symbol defined in an <code>Expression</code> 
 * or a <code>ReactionParticipant</code>
 * <p>
 */
public interface SymbolTableFunctionEntry extends SymbolTableEntry {

enum FunctionArgType {
	NUMERIC,
	LITERAL
};

public int getNumArguments();
public FunctionArgType[] getArgTypes();
public String[] getArgNames();
public String getFunctionName();

}
