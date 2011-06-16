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

/*   Individual  --- by Oliver Ruebenacker, UCHC --- July 2008
 *   Assigns each cell into its own group
 */

import java.util.Collection;
import java.util.Iterator;

import org.vcell.sybil.models.bpimport.table.Cell;
import org.vcell.sybil.models.bpimport.table.options.CellOption;
import org.vcell.sybil.util.collections.BiHashMap;
import org.vcell.sybil.util.collections.BiMap;
import org.vcell.sybil.util.iterators.IterOfOne;

public class GroupIndividual extends GroupManager {

	public static class SingleCellGroup extends CellGroup {

		protected Cell cell;
		
		public SingleCellGroup(Cell cellNew, Collection<? extends CellOption> defaultOptions) {
			super(cellNew.table(), defaultOptions);
			cell = cellNew;
		}

		@Override
		public Iterator<Cell> cellIter() { return new IterOfOne<Cell>(cell); }
		
	}
	
	protected BiMap<Cell, SingleCellGroup> cellToGroup = new BiHashMap<Cell, SingleCellGroup>();
	
	public GroupIndividual(Collection<? extends CellOption> defaultOptions) { super(defaultOptions); }

	@Override
	public SingleCellGroup determineGroup(Cell cell) { return defaultGroup(cell); }

	@Override
	public SingleCellGroup defaultGroup(Cell cell) {
		SingleCellGroup group = cellToGroup.get(cell);
		if(group == null) { 
			group = new SingleCellGroup(cell, defaultOptions);
			cellToGroup.put(cell, group);
		}
		return group;
	}

}
