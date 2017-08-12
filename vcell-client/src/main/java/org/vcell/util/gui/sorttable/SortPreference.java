/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.util.gui.sorttable;
/**
 * Insert the type's description here.
 * Creation date: (5/12/2006 11:49:14 AM)
 * @author: Fei Gao
 */
public class SortPreference {
	private boolean fieldSortedColumnAscending = false;
	private int fieldSortedColumnIndex = 0;	

/**
 * SortPreference constructor comment.
 */
public SortPreference(boolean ascending, int column) {
	super();
	fieldSortedColumnAscending = ascending;
	fieldSortedColumnIndex = column;
}


/**
 * Insert the method's description here.
 * Creation date: (5/12/2006 11:50:20 AM)
 * @return int
 */
public int getSortedColumnIndex() {
	return fieldSortedColumnIndex;
}


/**
 * Insert the method's description here.
 * Creation date: (5/12/2006 11:53:31 AM)
 * @return boolean
 */
public boolean isSortedColumnAscending() {
	return fieldSortedColumnAscending;
}


@Override
public int hashCode() {
	return new Integer(fieldSortedColumnIndex).hashCode() + new Boolean(fieldSortedColumnAscending).hashCode();
}


@Override
public boolean equals(Object obj) {
	if (!(obj instanceof SortPreference)) {
		return false;
	}
	SortPreference sortPreference = (SortPreference) obj;
	if (fieldSortedColumnIndex != sortPreference.fieldSortedColumnIndex) {
		return false;
	}
	if (fieldSortedColumnAscending != sortPreference.fieldSortedColumnAscending) {
		return false;
	}
	return true;
}
}
