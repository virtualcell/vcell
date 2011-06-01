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

/*   CellOption  --- by Oliver Ruebenacker, UCHC --- July 2008
 *   An option to choose from in a cell
 */

import org.vcell.sybil.models.bpimport.table.Cell;

public interface CellOption {
	
	public static interface Selection extends CellOption {}
	
	public static interface Selector {
		public void select(CellOption optionNew);
		public CellOption selected();
		public void returnToLastSelection();
		public void cancel();
		public void stop();
	}

	public CellOption eventSelect(Cell cell, Selector selector);
	
}
