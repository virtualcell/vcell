/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.sybil.models.bpimport.table;

/*   Column  --- by Oliver Ruebenacker, UCHC --- July 2008 to July 2009
 *   An iterator for cells over a column of a process table
 */

import java.util.Iterator;

public class ColumnCellIter implements Iterator<Cell> {

	protected Iterator<Row> rowIter;
	protected Column column;
	
	public ColumnCellIter(ProcessTableModel tableNew, Column columnNew) {
		rowIter = tableNew.rows().iterator();
		column = columnNew;
	}
	
	public boolean hasNext() { return rowIter.hasNext(); }
	public Cell next() { return rowIter.next().cell(column); }
	public void remove() { throw new UnsupportedOperationException(); }
	
}
