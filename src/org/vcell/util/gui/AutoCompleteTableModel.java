package org.vcell.util.gui;

import java.util.Set;

import javax.swing.table.TableModel;

import cbit.gui.AutoCompleteSymbolFilter;
import cbit.vcell.parser.SymbolTable;

public interface AutoCompleteTableModel extends TableModel {
	public abstract String checkInputValue(String inputValue, int row, int column);
	public abstract SymbolTable getSymbolTable(int row, int column);
	public abstract AutoCompleteSymbolFilter getAutoCompleteSymbolFilter(int row, int column);
	public abstract Set<String> getAutoCompletionWords(int row, int column);
}
