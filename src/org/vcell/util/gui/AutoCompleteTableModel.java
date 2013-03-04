/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.util.gui;

import java.util.Set;

import javax.swing.table.TableModel;

import cbit.vcell.parser.AutoCompleteSymbolFilter;
import cbit.vcell.parser.SymbolTable;

public interface AutoCompleteTableModel extends TableModel {
	public abstract String checkInputValue(String inputValue, int row, int column);
	public abstract SymbolTable getSymbolTable(int row, int column);
	public abstract AutoCompleteSymbolFilter getAutoCompleteSymbolFilter(int row, int column);
	public abstract Set<String> getAutoCompletionWords(int row, int column);
}
