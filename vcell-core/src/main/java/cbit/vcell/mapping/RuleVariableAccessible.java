package cbit.vcell.mapping;

import cbit.vcell.parser.Expression;
import cbit.vcell.parser.SymbolTableEntry;

public interface RuleVariableAccessible {
	String getName();
	void setName(String name);
	SymbolTableEntry getRuleVar();
	SimulationContext getSimulationContext();
	Expression getRuleExpression();
}
