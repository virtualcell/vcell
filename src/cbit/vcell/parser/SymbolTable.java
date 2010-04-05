package cbit.vcell.parser;

import java.util.Map;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
public interface SymbolTable {

public SymbolTableEntry getEntry(String identifierString) throws ExpressionBindingException; 
public void getEntries(Map<String, SymbolTableEntry> entryMap);

}
