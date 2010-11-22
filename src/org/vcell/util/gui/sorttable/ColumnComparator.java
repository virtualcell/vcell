package org.vcell.util.gui.sorttable;

import java.util.Comparator;

import org.vcell.util.ComparableObject;

public class ColumnComparator<T> implements Comparator<T> {
	protected int index;
	protected boolean ascending;

  public ColumnComparator(int index, boolean ascending)
  {
    this.index = index;
    this.ascending = ascending;
  }

  public int compare(T one, T two) {
	  if (one instanceof ComparableObject && two instanceof ComparableObject) {
			Object[] vOne = ((ComparableObject)one).toObjects();
			Object[] vTwo = ((ComparableObject)two).toObjects();
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