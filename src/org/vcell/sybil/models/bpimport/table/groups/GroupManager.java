/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.sybil.models.bpimport.table.groups;

/*   GroupMan  --- by Oliver Ruebenacker, UCHC --- July 2008 to July 2009
 *   Assigns cells into groups
 */

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.vcell.sybil.models.bpimport.table.Cell;
import org.vcell.sybil.models.bpimport.table.Column;
import org.vcell.sybil.models.bpimport.table.options.CellOption;

public abstract class GroupManager {
	
	protected Collection<? extends CellOption> defaultOptions;
	protected Column column;
	// If value changes in this column, notify dependent columns
	protected Set<DependentGroupManager> dependents = new HashSet<DependentGroupManager>();
	
	public GroupManager(Collection<? extends CellOption> defaultOptions) {
		this.defaultOptions = defaultOptions;
	}
	
	public Collection<? extends CellOption> defaultOptions() { return defaultOptions; }
	public void setColumn(Column columnNew) { column = columnNew; }
	public Column column() { return column; }
	
	public final void assignGroup(Cell cell) {
		cell.setGroup(determineGroup(cell));
		notifyDependents(cell);
	}
	
	public abstract CellGroup determineGroup(Cell cell);
	public abstract CellGroup defaultGroup(Cell cell);
	
	protected Set<DependentGroupManager> deps() { return dependents; }
	
	protected void notifyDependents(Cell cell) {
		for(DependentGroupManager dep : dependents) { dep.dependencyGroup(cell); }
	}
	
}
