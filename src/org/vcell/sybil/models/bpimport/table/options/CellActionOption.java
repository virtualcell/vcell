/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.sybil.models.bpimport.table.options;

/*   CellActionOption  --- by Oliver Ruebenacker, UCHC --- July 2008
 *   An option to choose from in a cell, representing an action to be taken
 */

import org.vcell.sybil.models.bpimport.table.Cell;

public abstract class CellActionOption implements CellOption {

	public static class ID {};
	
	protected ID id;
	protected String name;
	
	public CellActionOption(ID idNew, String nameNew) { id = idNew; name = nameNew; }
	
	public abstract CellOption eventSelect(Cell cell, CellOption.Selector selector);
	
	public ID id() { return id; }
	public String name() { return name; }
	public String toString() { return "[" + name + "]"; }

	public boolean equals(Object o) { 
		return o instanceof CellActionOption ? id.equals(((CellActionOption) o).id()) : false; 
	}
	
	public int hashCode() { return id.hashCode(); }

}
