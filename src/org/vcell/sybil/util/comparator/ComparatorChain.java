package org.vcell.sybil.util.comparator;

/*   ComparatorChain  --- by Oliver Ruebenacker, UCHC --- February 2009
 *   A comparator which is the chain of two given ones (use second if first is zero)
 */

import java.util.Comparator;

public class ComparatorChain<T> implements Comparator<T> {

	protected Comparator<? super T> c1, c2;
	
	public ComparatorChain(Comparator<? super T> c1New, Comparator<? super T> c2New) {
		c1 = c1New;
		c2 = c2New;
	}
	
	public int compare(T t1, T t2) {
		int comparison1 = c1.compare(t1, t2);
		return comparison1 != 0 ? comparison1 : c2.compare(t1, t2);
	
	}

}
