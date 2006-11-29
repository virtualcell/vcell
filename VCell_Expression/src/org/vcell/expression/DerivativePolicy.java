package org.vcell.expression;

public interface DerivativePolicy {
	String newSymbolForDerivative(String identifier, SymbolTableEntry ste, String variable);
}
