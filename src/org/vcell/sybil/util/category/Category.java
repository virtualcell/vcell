package org.vcell.sybil.util.category;

/*   Category  --- by Oliver Ruebenacker, UCHC --- October 2008
 *   An interface for categories. Categories are equal if the class and the parameters are the same
 */

public interface Category<C extends Category<C>> extends Comparable<C> {
	
	public Class<?> c();
	public int rank();

}
