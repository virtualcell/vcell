/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.sybil.util.enumerations;

/*   SmartEnumWrapper  --- by Oliver Ruebenacker, UCHC --- November 2007 to November 2009
 *   A view of a resource representing an SBPAX object
 */

import java.util.Enumeration;

public class SmartEnumWrapper<E> implements SmartEnum<E> {

	protected Enumeration<E> enumeration;
	protected int number;
	
	public SmartEnumWrapper(Enumeration<E> newEnumeration) { enumeration = newEnumeration; }

	public int count() { 
		if(enumeration instanceof SmartEnum<?>) {
			return ((SmartEnum<E>) enumeration).count(); 
		} else {
			return number;
		}
	}

	public boolean hasMoreElements() { return enumeration.hasMoreElements(); }
	public E nextElement() { return enumeration.nextElement(); }

	public boolean isAtBoundary() {
		if(enumeration instanceof SmartEnum<?>) {
			((SmartEnum<E>) enumeration).isAtBoundary();
		} 
		return number == 0 || !enumeration.hasMoreElements();
	}

	public boolean isAtInternalBoundary() {
		if(enumeration instanceof SmartEnum<?>) {
			((SmartEnum<E>) enumeration).isAtBoundary();
		} 
		return false;
	}

	public int subCount() {
		if(enumeration instanceof SmartEnum<?>) {
			((SmartEnum<E>) enumeration).subCount();
		}
		return number;
	}

}
