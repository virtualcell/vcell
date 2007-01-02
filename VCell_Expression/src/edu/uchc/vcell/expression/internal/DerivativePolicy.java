package edu.uchc.vcell.expression.internal;

import org.vcell.expression.SymbolTableEntry;

/**
 */
public interface DerivativePolicy {
	/**
	 * Method newSymbolForDerivative.
	 * @param name String
	 * @param symbolTableEntry SymbolTableEntry
	 * @param variable String
	 * @return String
	 */
	public String newSymbolForDerivative(String name, SymbolTableEntry symbolTableEntry, String variable);
}
