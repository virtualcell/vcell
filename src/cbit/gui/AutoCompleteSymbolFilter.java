package cbit.gui;

import cbit.vcell.parser.SymbolTableEntry;

public interface AutoCompleteSymbolFilter {
	boolean accept(SymbolTableEntry ste);
	boolean acceptFunction(String funcName);
}
