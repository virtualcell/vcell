package org.vcell.expression;


/**
 * Insert the type's description here.
 * Creation date: (8/27/2003 11:43:01 AM)
 * @author: Jim Schaff
 */
public interface ScopedSymbolTable extends SymbolTable {
/**
 * Insert the method's description here.
 * Creation date: (8/27/2003 12:01:04 PM)
 * @return cbit.vcell.parser.SymbolTableEntry
 * @param identifier java.lang.String
 */
SymbolTableEntry getLocalEntry(String identifier) throws ExpressionBindingException;
/**
 * Insert the method's description here.
 * Creation date: (8/27/2003 11:44:13 AM)
 * @return cbit.vcell.parser.NameScope
 */
NameScope getNameScope();
}
