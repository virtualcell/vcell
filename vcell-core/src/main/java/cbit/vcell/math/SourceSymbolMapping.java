package cbit.vcell.math;

import cbit.vcell.parser.SymbolTableEntry;

import java.io.Serializable;

public interface SourceSymbolMapping extends Serializable {
    public Variable findVariableByName(String variableName);

    public SymbolTableEntry[] getBiologicalSymbol(Variable var);

    public Variable getVariable(SymbolTableEntry biologicalSymbol);
}
