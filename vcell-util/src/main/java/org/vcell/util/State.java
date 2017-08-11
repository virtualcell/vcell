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

import java.io.Serializable;

@SuppressWarnings("serial")
public class State<E> implements Serializable {
	private E oldValue = null;
	private E currentValue = null;
	private boolean bDirty = false;	
	
	public State(E value) {
		super();
		setValue(value);
	}
	
	public final E getCurrentValue() {
		return currentValue;
	}
	
	public final E getOldValue() {
		return oldValue;
	}
	
	public final void setValue(E value) {
		this.oldValue = currentValue;
		this.currentValue = value;
		bDirty = false;
	}
	
	public final boolean isDirty() {
		return bDirty;
	}
	
	public final void setDirty() {
		this.oldValue = currentValue;
		this.currentValue = null;
		this.bDirty = true;
	}	
}
