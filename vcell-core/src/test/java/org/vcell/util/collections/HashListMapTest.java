package org.vcell.util.collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;


@Tag("Fast")
public class HashListMapTest {

	private HashListMap<String,Integer> testList;
	
	@BeforeEach
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
            assertEquals((int) i, back++);
		}
        assertEquals(7, (int) testList.get(other).get(0));
	}
	@Test
	public void nullValue( ) {
		String key = "nv";
		testList.put(key, null);
        assertNull(testList.get(key).get(0));
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
            assertEquals((int) i, back++);
		}
        assertEquals(7, (int) testList.get(other).get(0));
	}

}
