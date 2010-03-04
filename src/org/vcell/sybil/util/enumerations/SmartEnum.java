package org.vcell.sybil.util.enumerations;

import java.util.Enumeration;

public interface SmartEnum<E> extends Enumeration<E> {

	public int count();
	public int subCount();
	public boolean isAtBoundary();
	public boolean isAtInternalBoundary();

}
