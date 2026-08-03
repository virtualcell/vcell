package cbit.vcell.mapping;

import cbit.vcell.parser.Expression;
import cbit.vcell.parser.SymbolTableEntry;
import org.vcell.util.Nameable;

public interface RuleVariableAccessible extends Nameable {
	SymbolTableEntry getRuleVar();
	SimulationContext getSimulationContext();
	Expression getRuleExpression();
}
