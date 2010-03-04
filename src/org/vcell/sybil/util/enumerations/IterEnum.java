package org.vcell.sybil.util.enumerations;

/*   IterEnum  --- by Oliver Ruebenacker, UCHC --- October 2008
 *   An enumeration that wraps an iterator
 */

import java.util.Enumeration;
import java.util.Iterator;

public class IterEnum<E> implements Enumeration<E> {

	Iterator<? extends E> iter;
	
	public IterEnum(Iterator<? extends E> iter) { this.iter = iter; }
	public boolean hasMoreElements() { return iter.hasNext(); }
	public E nextElement() { return iter.next(); }

}
