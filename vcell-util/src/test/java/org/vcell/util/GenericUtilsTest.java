package org.vcell.util;

import org.junit.jupiter.api.Tag;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.vcell.test.Fast;

@Tag("Fast")
public class GenericUtilsTest {
	
	@Test
	public void goodConvert( ) {
		ArrayList<Object> al = new ArrayList<Object>( );
		for (int i = 0; i < 5; i++) {
			al.add(Integer.valueOf(i));
		}
		List<Integer> asIntArray = GenericUtils.convert(al, Integer.class); 
		for (int i = 0; i < 5; i++) {
			assertTrue(asIntArray.get(i) == i);
		}
	}

	@Test(expected = RuntimeException.class)
	public void badConvert( ) {
		ArrayList<Object> al = new ArrayList<Object>( );
		for (int i = 0; i < 5; i++) {
			al.add(Integer.valueOf(i));
		}
		List<String> asIntArray = GenericUtils.convert(al, String.class); 
		System.out.println(asIntArray);
	}
}
