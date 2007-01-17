package cbit.util;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
/**
 * Insert the type's description here.
 * Creation date: (2/3/2001 8:51:30 PM)
 * @author: Ion Moraru
 */
public interface HistoryIterator extends java.util.Iterator {
/**
 * Insert the method's description here.
 * Creation date: (2/3/2001 8:57:10 PM)
 * @param newElement java.lang.Object
 */
void add(Object newElement);
/**
 * Insert the method's description here.
 * Creation date: (2/3/2001 8:58:55 PM)
 * @return java.lang.Object[]
 */
Object[] allEntries();
/**
 * Insert the method's description here.
 * Creation date: (2/3/2001 8:57:57 PM)
 * @return java.lang.Object
 */
Object current();
/**
 * Insert the method's description here.
 * Creation date: (2/3/2001 8:58:55 PM)
 * @return java.lang.Object[]
 */
Object[] fullList();
/**
 * Insert the method's description here.
 * Creation date: (2/3/2001 8:53:43 PM)
 * @return boolean
 */
boolean hasPrevious();
/**
 * Insert the method's description here.
 * Creation date: (2/3/2001 8:58:55 PM)
 * @return java.lang.Object[]
 */
Object[] nextList();
/**
 * Insert the method's description here.
 * Creation date: (2/3/2001 8:53:18 PM)
 * @return java.lang.Object
 */
Object previous();
/**
 * Insert the method's description here.
 * Creation date: (2/3/2001 8:58:55 PM)
 * @return java.lang.Object[]
 */
Object[] previousList();
}
