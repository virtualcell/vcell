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

/*   CellNodeOption  --- by Oliver Ruebenacker, UCHC --- July 2008 to March 2009
 *   An option to choose from in a cell, representing a node
 */

import org.vcell.sybil.models.bpimport.table.Cell;

public class CellSelectableOption implements CellOption.Selection {

	public CellOption eventSelect(Cell cell, CellOption.Selector selector) {
		selector.select(this);
		selector.stop();
		return this;
	}
	
}
