package org.vcell.sybil.util.comparator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ComparatorByList<T> extends ComparatorByScore<T> {

	protected final Map<T, Integer> rankMap = new HashMap<T, Integer>();
	protected final int unlistedRank;
	
	public ComparatorByList(List<T> list) {
		unlistedRank = list.size();
		for(int i = 0; i < list.size(); ++i) {
			rankMap.put(list.get(i), new Integer(i));
		}
	}
	
	@Override
	public int score(T object) {
		Integer integer = rankMap.get(object);
		if(integer != null) { return integer.intValue(); }
		return unlistedRank;
	}

}
