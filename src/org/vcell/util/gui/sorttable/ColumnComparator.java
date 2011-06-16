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

import java.util.Comparator;

import org.vcell.util.ComparableObject;

public class ColumnComparator implements Comparator<ComparableObject> {
	protected int index;
	protected boolean ascending;

  public ColumnComparator(int index, boolean ascending)
  {
    this.index = index;
    this.ascending = ascending;
  }

  public int compare(ComparableObject one, ComparableObject two) {
		Object[] vOne = one.toObjects();
		Object[] vTwo = two.toObjects();
		Object oOne = vOne[index];
		Object oTwo = vTwo[index];
		if (oOne == null) {
			if (ascending) {
				return -1;
			} else {
				return 1;
			}
		} else if (oTwo == null) {
			if (ascending) {
				return 1;
			} else {
				return -1;
			}
		} else if (oOne instanceof Comparable && oTwo instanceof Comparable) {
			@SuppressWarnings("unchecked")
			Comparable<Object> cOne = (Comparable<Object>) oOne;
			@SuppressWarnings("unchecked")
			Comparable<Object> cTwo = (Comparable<Object>) oTwo;
			if (ascending) {
				return cOne.compareTo(cTwo);
			} else {
				return cTwo.compareTo(cOne);
			}
		}
		
		return 0;
	}
}
