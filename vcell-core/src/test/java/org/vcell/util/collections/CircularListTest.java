package org.vcell.util.collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

@Tag("Fast")
public class CircularListTest {
	
	
	private CircularList<Integer> list;
	
	@BeforeEach
	public void setup( ) {
		list = new CircularList<>(5);
	}
	
	@Test
	public void tryIt( ) {
		insert(7,10);
		validate(7,8,9,10);
		list.clear();
		insert(1,23);
		validate(19,20,21,22,23);
		list.add(7);
		validate(20,21,22,23,7);
	}
	
	private void validate(Integer ...values) {
        assertEquals(values.length, list.size());
		int index  = 0;
		for (Integer i : list) {
            assertSame(values[index++], i);
		}
	}
	
	private void insert(int low, int high) {
		for (int i = low; i <=high; i++) {
			list.add(i);
		}
	}
	
	/**
	 * display current values on console
	 */
	@SuppressWarnings("unused")
	private void show( ) {
		for (int i : list) {
			System.out.print(i);
			System.out.print(',');
		}
		System.out.println( );
	}
	

}
