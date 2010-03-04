package org.vcell.sybil.util.comparator;

/*   ComparatorReverse  --- by Oliver Ruebenacker, UCHC --- February 2009
 *   A comparator which is the reverse of another given one
 */

import java.util.Comparator;

public class ComparatorReverse<T> implements Comparator<T> {
	protected Comparator<T> comparator;
	public ComparatorReverse(Comparator<T> comperatorNew) { comparator = comperatorNew; }
	public Comparator<T> comparator() { return comparator; }
	public int compare(T t1, T t2) { return comparator.compare(t2, t1); }
}