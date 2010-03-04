package org.vcell.sybil.util.sets;

/*   SetUtil  --- by Oliver Ruebenacker, UCHC --- May 2008 to March 2010
 *   A convenient set for adding many members in one line
 */

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class SetUtil {

	public static <E> Set<E> newSet(E...es) {
		return new HashSet<E>(Arrays.asList(es));
	}
	
	public static <E> Set<E> unionSet(Set<E>...sets) {
		HashSet<E> union = new HashSet<E>();
		for(Set<E> set : sets) { union.addAll(set); }
		return union;
	}
	
}
