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

/*   CellNoneOption  --- by Oliver Ruebenacker, UCHC --- July 2008 to March 2009
 *   An option to choose from in a cell to indicate no node is selected
 */

public class CellNoneOption extends CellSelectableOption {

	public static final String LABEL = "[none]";
	
	public String label() { return LABEL; }
	public String toString() { return LABEL; }
	public boolean equals(Object o) { return o instanceof CellNoneOption; }
	public int hashCode() { return LABEL.hashCode(); }

}
