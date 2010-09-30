package org.vcell.sybil.util.lists;

/*   ListUtil  --- by Oliver Ruebenacker, UCHC --- April to September 2010
 *   Useful methods to build and modify lists
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListUtil {

	public static <E> List<E> newList(E...es) {
		return new ArrayList<E>(Arrays.asList(es));
	}
	
	@SuppressWarnings("unchecked")
	public static <E> List<E> concatedList(List<E> list1, List<?>...lists) {
		List<E> concatedList = new ArrayList<E>();
		concatedList.addAll(list1);
		for(List<?> list : lists) { concatedList.addAll((List<E>)list); }
		return concatedList;
	}
	
	// like a remove and insert, but more efficient for array lists (not for linked lists)
	public static <E> void shiftElement(List<E> list, int i1, int i2) {
		if(i1 < i2) {
			E element = list.get(i1);
			for(int i = i1; i < i2; ++i) {
				list.set(i, list.get(i + 1));
			}
			list.set(i2, element);
		} else if (i2 < i1) {
			E element = list.get(i1);
			for(int i = i1; i > i2; --i) {
				list.set(i, list.get(i - 1));
			}
			list.set(i2, element);			
		}
	}
	
}
