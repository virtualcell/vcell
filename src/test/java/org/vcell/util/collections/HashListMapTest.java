package org.vcell.util.collections;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;


public class HashListMapTest {

	private HashListMap<String,Integer> testList;
	
	@Before
	public void init( ) {
		testList = new HashListMap<>();
	}
	
	@Test
	public void basic( ) {
		String key = "simple";
		testList.put(key, 1);
		testList.put(key, 2);
		testList.put(key, 3);
		String other = "other";
		testList.put(other, 7);
		int back = 1;
		List<Integer> values = testList.get(key);
		for (Integer i : values) {
			assertTrue(i == back++);
		}
		assertTrue(testList.get(other).get(0) == 7);
	}
	@Test
	public void nullValue( ) {
		String key = "nv";
		testList.put(key, null);
		assertTrue(testList.get(key).get(0) == null);
	}
	@Test
	public void nullKey( ) {
		String other = "other";
		testList.put(other, 7);
		testList.put(null,1);
		testList.put(null,2);
		testList.put(null,3);
		int back = 1;
		List<Integer> values = testList.get(null);
		for (Integer i : values) {
			assertTrue(i == back++);
		}
		assertTrue(testList.get(other).get(0) == 7);
	}

}
