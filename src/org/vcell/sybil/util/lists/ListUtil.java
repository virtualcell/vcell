package org.vcell.sybil.util.lists;

/*   ListUtil  --- by Oliver Ruebenacker, UCHC --- April 2010
 *   Useful methods to build lists
 */

import java.util.Arrays;
import java.util.List;
import java.util.Vector;

public class ListUtil {

	public static <E> List<E> newList(E...es) {
		return new Vector<E>(Arrays.asList(es));
	}
	
	@SuppressWarnings("unchecked")
	public static <E> List<E> concatedList(List<E> list1, List<?>...lists) {
		List<E> concatedList = new Vector<E>();
		concatedList.addAll(list1);
		for(List<?> list : lists) { concatedList.addAll((List<E>)list); }
		return concatedList;
	}
	
}
