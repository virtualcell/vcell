package org.vcell.sybil.util.category;

/*   SimpleCat  --- by Oliver Ruebenacker, UCHC --- October 2008
 *   A simple implementation for compareTo() using rank().
 */

public abstract class SimpleCategory<C extends Category<C>> implements Category<C> {

	public abstract Class<?> c();
	public abstract int rank();
	public int compareTo(C cat) { return rank() - cat.rank(); }

}
