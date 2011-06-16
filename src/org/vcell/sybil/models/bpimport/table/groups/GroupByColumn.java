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

/*   ByColumn  --- by Oliver Ruebenacker, UCHC --- July 2008 to July 2009
 *   Assigns cells into groups according to another column, but not including cells from there
 */

import java.util.Collection;
import java.util.Iterator;

import org.vcell.sybil.models.bpimport.table.Cell;
import org.vcell.sybil.models.bpimport.table.Column;
import org.vcell.sybil.models.bpimport.table.options.CellOption;

public class GroupByColumn extends DependentGroupManager {
	public GroupByColumn(Column columnNew, Collection<? extends CellOption> defaultOptions) { 
		super(columnNew, defaultOptions); 
	}

	@Override
	public void updateOptions(DependentCellGroup group) {
		Iterator<Cell> cellIter = group.cellIter();
		while(cellIter.hasNext()) {
			Cell cell = cellIter.next();
			CellOption.Selection selected = cell.selected();
			if(!group.options().contains(selected)) { group.options().add(selected); }
		}
	}
}
