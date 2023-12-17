package org.vcell.util;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

	@Test
	public void badConvert( ) {
		assertThrows(RuntimeException.class, () -> {
			ArrayList<Object> al = new ArrayList<Object>();
			for (int i = 0; i < 5; i++) {
				al.add(Integer.valueOf(i));
			}
			List<String> asIntArray = GenericUtils.convert(al, String.class);
			System.out.println(asIntArray);
		});
	}
}
