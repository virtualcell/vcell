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
