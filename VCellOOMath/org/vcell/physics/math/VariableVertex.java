package org.vcell.physics.math;

/**
 * Insert the type's description here.
 * Creation date: (1/16/2006 8:22:13 PM)
 * @author: Jim Schaff
 */
public class VariableVertex extends com.mhhe.clrs2e.Vertex {
	private org.vcell.expression.SymbolTableEntry symbol = null;
/**
 * VariableVertex constructor comment.
 * @param index int
 * @param name java.lang.String
 */
public VariableVertex(int index, org.vcell.expression.SymbolTableEntry argSymbolTableEntry) {
	super(index, argSymbolTableEntry.getName());
	this.symbol = argSymbolTableEntry;
}
/**
 * Insert the method's description here.
 * Creation date: (1/16/2006 8:35:36 PM)
 * @return cbit.vcell.parser.SymbolTableEntry
 */
public org.vcell.expression.SymbolTableEntry getSymbol() {
	return symbol;
}
/**
 * Insert the method's description here.
 * Creation date: (1/16/2006 8:35:36 PM)
 * @param newSymbol cbit.vcell.parser.SymbolTableEntry
 */
public void setSymbol(org.vcell.expression.SymbolTableEntry newSymbol) {
	symbol = newSymbol;
	setName(newSymbol.getName());
}
}
