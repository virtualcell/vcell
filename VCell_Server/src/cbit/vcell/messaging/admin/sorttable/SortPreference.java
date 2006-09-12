package cbit.vcell.messaging.admin.sorttable;
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
}