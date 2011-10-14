package org.vcell.sybil.util.lists;

import java.util.ArrayList;
import java.util.List;

public class ListUtil {
	
	public static <E> List<E> fillGaps(List<E> list, E filler) {
		ArrayList<E> listFilled = new ArrayList<E>();
		for(E element : list) { listFilled.add(element != null ? element : filler); }
		return listFilled;
	}

	public static <E> List<E> fillGaps(List<E> list, E filler, int size) {
		if(list == null) { list = new ArrayList<E>(); }
		List<E> listFilled = fillGaps(list, filler);
		while(listFilled.size() < size) {listFilled.add(filler); }
		return listFilled;
	}

}
