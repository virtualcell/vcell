/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.util;

import java.util.*;
/**
 * Insert the type's description here.
 * Creation date: (2/3/2001 9:06:34 PM)
 * @author: Ion Moraru
 */
public class SimpleHistory implements HistoryIterator {
	private Vector allEntries = new Vector();
	private HistoryElement currentHistoryElement = null;
	private class HistoryElement {
		private Object element = null;
		private HistoryElement nextElement = null;
		private HistoryElement previousElement = null;
		public HistoryElement(Object element, HistoryElement previousElement) {
			this.element = element;
			this.previousElement = previousElement;
			if (previousElement != null) {
				previousElement.setNext(this);
			}
			}
		public Object getElement() {
			return element;
			}
		public HistoryElement getNext() {
			return nextElement;
			}
		public HistoryElement getPrevious() {
			return previousElement;
			}
		public boolean hasNext() {
			return nextElement != null;
			}
		public boolean hasPrevious() {
			return previousElement != null;
			}
		public void setNext(HistoryElement nextElement) {
			this.nextElement = nextElement;
			}
	} 
/**
 * Insert the method's description here.
 * Creation date: (2/3/2001 9:06:34 PM)
 * @param newElement java.lang.Object
 */
public synchronized void add(Object newElement) {
	// we only keep track of non-null elements
	if (newElement == null) {
		return;
	}
	// if we just jumped up or down the current list, only reset the current element
	HistoryElement[] list = fullListHE();
	if (list != null) {
		for (int i=0;i<list.length;i++) {
			if (list[i].getElement().equals(newElement)) {
				currentHistoryElement = list[i];
				return;
			}
		}
	}
	// otherwise add to the global history vector if it's really new
	if (! allEntries.contains(newElement)) {
		allEntries.add(newElement);
	}
	// finally, wrap it into a new HistoryElement and reset the current element
	HistoryElement newHistoryElement = new HistoryElement(newElement, currentHistoryElement);
	currentHistoryElement = newHistoryElement;
}
/**
 * Insert the method's description here.
 * Creation date: (2/3/2001 9:06:34 PM)
 * @return java.lang.Object[]
 */
public synchronized java.lang.Object[] allEntries() {
	if (currentHistoryElement != null) {
		return allEntries.toArray();
	} else {
		return null;
	}
}
/**
 * Insert the method's description here.
 * Creation date: (2/3/2001 9:06:34 PM)
 * @return java.lang.Object
 */
public synchronized Object current() {
	if (currentHistoryElement != null) {
		return currentHistoryElement.getElement();
	} else {
		return null;
	}
}
/**
 * Insert the method's description here.
 * Creation date: (2/3/2001 9:06:34 PM)
 * @return java.lang.Object[]
 */
public synchronized java.lang.Object[] fullList() {
	if (currentHistoryElement != null) {
		Object[] previousList = previousList();
		Object[] nextList = nextList();
		Object[] list = new Object[previousList.length + nextList.length + 1];
		for (int i=0;i<previousList.length;i++) {
			list[i] = previousList[i];
		}
		list[previousList.length] = currentHistoryElement.getElement();
		for (int j=0;j<nextList.length;j++) {
			list[j + previousList.length + 1] = nextList[j];
		}
		return list;
	} else {
		return new Object[0];
	}
}
/**
 * Insert the method's description here.
 * Creation date: (2/3/2001 9:06:34 PM)
 * @return java.lang.Object[]
 */
private synchronized HistoryElement[] fullListHE() {
	if (currentHistoryElement != null) {
		HistoryElement[] previousList = previousListHE();
		HistoryElement[] nextList = nextListHE();
		HistoryElement[] list = new HistoryElement[previousList.length + nextList.length + 1];
		for (int i=0;i<previousList.length;i++) {
			list[i] = previousList[i];
		}
		list[previousList.length] = currentHistoryElement;
		for (int j=0;j<nextList.length;j++) {
			list[j + previousList.length + 1] = nextList[j];
		}
		return list;
	} else {
		return new HistoryElement[0];
	}
}
	/**
	 * Returns <tt>true</tt> if the iteration has more elements. (In other
	 * words, returns <tt>true</tt> if <tt>next</tt> would return an element
	 * rather than throwing an exception.)
	 *
	 * @return <tt>true</tt> if the iterator has more elements.
	 */
public synchronized boolean hasNext() {
	if (currentHistoryElement != null) {
		return currentHistoryElement.hasNext();
	} else {
		return false;
	}
}
/**
 * Insert the method's description here.
 * Creation date: (2/3/2001 9:06:34 PM)
 * @return boolean
 */
public synchronized boolean hasPrevious() {
	if (currentHistoryElement != null) {
		return currentHistoryElement.hasPrevious();
	} else {
		return false;
	}
}
	/**
	 * Returns the next element in the interation.
	 *
	 * @returns the next element in the interation.
	 * @exception NoSuchElementException iteration has no more elements.
	 */
public synchronized Object next() {
	if (hasNext()) {
		return currentHistoryElement.getNext().getElement();
	} else {
		return null;
	}
}
/**
 * Insert the method's description here.
 * Creation date: (2/3/2001 9:06:34 PM)
 * @return java.lang.Object[]
 */
public synchronized java.lang.Object[] nextList() {
	if (hasNext()) {
		HistoryElement[] nextListHE = nextListHE();
		Object[] nextList = new Object[nextListHE.length];
		for (int i=0;i<nextListHE.length;i++) {
			nextList[i] = nextListHE[i].getElement();
		}
		return nextList;
	} else {
		return new Object[0];
	}
}
/**
 * Insert the method's description here.
 * Creation date: (2/3/2001 9:06:34 PM)
 * @return java.lang.Object[]
 */
private synchronized HistoryElement[] nextListHE() {
	if (hasNext()) {
		Vector v = new Vector();
		HistoryElement he = currentHistoryElement;
		while (he.hasNext()) {
			he = he.getNext();
			v.add(he);
		}
		return (HistoryElement[])v.toArray(new HistoryElement[v.size()]);
	} else {
		return new HistoryElement[0];
	}
}
/**
 * Insert the method's description here.
 * Creation date: (2/3/2001 9:06:34 PM)
 * @return java.lang.Object
 */
public synchronized Object previous() {
	if (hasPrevious()) {
		return currentHistoryElement.getPrevious().getElement();
	} else {
		return null;
	}
}
/**
 * Insert the method's description here.
 * Creation date: (2/3/2001 9:06:34 PM)
 * @return java.lang.Object[]
 */
public synchronized java.lang.Object[] previousList() {
	if (hasPrevious()) {
		HistoryElement[] previousListHE = previousListHE();
		Object[] previousList = new Object[previousListHE.length];
		for (int i=0;i<previousListHE.length;i++) {
			previousList[i] = previousListHE[i].getElement();
		}
		return previousList;
	} else {
		return new Object[0];
	}
}
/**
 * Insert the method's description here.
 * Creation date: (2/3/2001 9:06:34 PM)
 * @return java.lang.Object[]
 */
private synchronized HistoryElement[] previousListHE() {
	if (hasPrevious()) {
		Vector v = new Vector();
		HistoryElement he = currentHistoryElement;
		while (he.hasPrevious()) {
			he = he.getPrevious();
			v.add(he);
		}
		return (HistoryElement[])v.toArray(new HistoryElement[v.size()]);
	} else {
		return new HistoryElement[0];
	}
}
	/**
	 * 
	 * Removes from the underlying collection the last element returned by the
	 * iterator (optional operation).  This method can be called only once per
	 * call to <tt>next</tt>.  The behavior of an iterator is unspecified if
	 * the underlying collection is modified while the iteration is in
	 * progress in any way other than by calling this method.
	 *
	 * @exception UnsupportedOperationException if the <tt>remove</tt>
	 *		  operation is not supported by this Iterator.
	 
	 * @exception IllegalStateException if the <tt>next</tt> method has not
	 *		  yet been called, or the <tt>remove</tt> method has already
	 *		  been called after the last call to the <tt>next</tt>
	 *		  method.
	 */
public void remove() throws UnsupportedOperationException {
	throw new UnsupportedOperationException();
}
}
