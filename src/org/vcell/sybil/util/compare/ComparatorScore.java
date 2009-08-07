package org.vcell.sybil.util.compare;

/*   ComparatorScore  --- by Oliver Ruebenacker, UCHC --- February 2009
 *   A comparator based on assigning a score to each node
 */

import java.util.Comparator;

public abstract class ComparatorScore<T> implements Comparator<T> {
	public int compare(T t1, T t2) { return score(t2) - score(t1); }
	public abstract int score(T t);
}