package cbit.vcell.messaging.admin;
/*
=====================================================================

  ColumnComparator.java
  
  Created by Claude Duguay
  Copyright (c) 2002
  
=====================================================================
*/

import java.util.*;

public class ColumnComparator implements Comparator {
	protected int index;
	protected boolean ascending;

  public ColumnComparator(int index, boolean ascending)
  {
    this.index = index;
    this.ascending = ascending;
  }


public int compare(Object one, Object two) {
	if (one instanceof cbit.vcell.messaging.admin.ComparableObject && two instanceof cbit.vcell.messaging.admin.ComparableObject) {
		Object[] vOne = ((cbit.vcell.messaging.admin.ComparableObject)one).toObjects();
		Object[] vTwo = ((cbit.vcell.messaging.admin.ComparableObject)two).toObjects();
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
			Comparable cOne = (Comparable) oOne;
			Comparable cTwo = (Comparable) oTwo;
			if (ascending) {
				return cOne.compareTo(cTwo);
			} else {
				return cTwo.compareTo(cOne);
			}
		}
	} 
	
	return 0;
}
}