package org.vcell.sybil.util.enumerations;

/*   NestedEnum  --- by Oliver Ruebenacker, UCHC --- October 2007
 *   An enumeration build form a set of enumeration
 */

import java.util.Enumeration;
import java.util.Vector;

public class NestedEnum<E> implements SmartEnum<E> {

	protected Vector<SmartEnum<E>> backup;
	protected SmartEnum<E> current;
	protected int count;

	public NestedEnum() {
		backup = new Vector<SmartEnum<E>>();
		current = new NoElementEnum<E>();
	}
	
	private void tryBackupIfCurrentEmpty() {
		while((!current.hasMoreElements()) && (backup.size() > 0)) {
			current = backup.remove(0);
		}
	}
	
	public void add(Enumeration<E> newEnumeration) {
		backup.add(new SmartEnumWrapper<E>(newEnumeration));
	}
	
	public boolean hasMoreElements() {
		tryBackupIfCurrentEmpty();
		return current.hasMoreElements();
	}

	public E nextElement() {
		tryBackupIfCurrentEmpty();
		++count;
		return current.nextElement();
	}

	public int subCount() { return current.count(); }

	public int count() { return count; }

	public boolean isAtBoundary() { return current.count() == 0 || !current.hasMoreElements(); }

	public boolean isAtInternalBoundary() { 
		return (current.count() == 0 && count() > 0) || (!current.hasMoreElements() && backup.size() > 0); 
	}

}
