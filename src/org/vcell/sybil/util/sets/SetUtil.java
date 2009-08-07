package org.vcell.sybil.util.sets;

/*   SetUtil  --- by Oliver Ruebenacker, UCHC --- May to September 2008
 *   A convenient set for adding many members in one line
 */

import java.util.Collection;
import java.util.HashSet;

public class SetUtil {

	public static class Chain<E> extends HashSet<E> {
		
		private static final long serialVersionUID = 7663670503890446660L;
		public Chain<E> plus(E e) { add(e); return this; }
		public Chain<E> plus(Collection<E> coll) { addAll(coll); return this; }
	}
	
	public static <E> Chain<E> chain(E e) { 
		Chain<E> chain = new Chain<E>();
		chain.add(e);
		return chain;
	}
	
	public static <E> Chain<E> merger(Collection<E> coll) {
		Chain<E> chain = new Chain<E>();
		chain.addAll(coll);
		return chain;
	}
	
}
