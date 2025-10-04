package cbit.vcell.math;

import cbit.vcell.parser.SymbolTableEntry;

import java.io.Serializable;

public interface SourceSymbolMapping extends Serializable {
    Variable findVariableByName(String variableName);

    SymbolTableEntry[] getBiologicalSymbol(Variable var);

    Variable getVariable(SymbolTableEntry biologicalSymbol);
}
