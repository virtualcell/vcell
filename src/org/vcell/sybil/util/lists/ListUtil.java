/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

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
