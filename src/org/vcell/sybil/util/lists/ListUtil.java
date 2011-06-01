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
