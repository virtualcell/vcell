/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.parser;

import java.util.Map;

/**
 * Insert the type's description here.
 * Creation date: (8/27/2003 11:43:01 AM)
 * @author: Jim Schaff
 */
public interface ScopedSymbolTable extends SymbolTable {
/**
 * Insert the method's description here.
 * Creation date: (8/27/2003 12:01:04 PM)
 * @return cbit.vcell.parser.SymbolTableEntry
 * @param identifier java.lang.String
 */
SymbolTableEntry getLocalEntry(String identifier);
/**
 * Insert the method's description here.
 * Creation date: (8/27/2003 11:44:13 AM)
 * @return cbit.vcell.parser.NameScope
 */
NameScope getNameScope();
public void getLocalEntries(Map<String, SymbolTableEntry> entryMap);
}
