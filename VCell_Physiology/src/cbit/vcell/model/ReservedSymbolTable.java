package cbit.vcell.model;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import org.vcell.expression.ExpressionBindingException;
import org.vcell.expression.SymbolTable;
import org.vcell.expression.SymbolTableEntry;

import edu.uchc.vcell.expression.internal.*;
/**
 * This type was created in VisualAge.
 */
public class ReservedSymbolTable implements SymbolTable {
	private boolean bIncludeTime = false;
/**
 * ReservedSymbolTable constructor comment.
 */
public ReservedSymbolTable(boolean bIncludeTime) {
	this.bIncludeTime = bIncludeTime;
}
/**
 * getEntry method comment.
 */
public SymbolTableEntry getEntry(String identifierString) throws ExpressionBindingException {
	SymbolTableEntry ste;
	
	ste = ReservedSymbol.fromString(identifierString);
	if (ste != null){
		if (((ReservedSymbol)ste).isTIME() && !bIncludeTime){
			throw new ExpressionBindingException("expression must not be a function of time");
		}else{
			return ste;
		}
	}	
	
	throw new ExpressionBindingException("unresolved symbol <"+identifierString+">");
}
}
