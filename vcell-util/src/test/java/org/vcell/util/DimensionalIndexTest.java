package org.vcell.util;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@Tag("Fast")
public class DimensionalIndexTest {
	
	private static final int mX = 3;
	private static final int mY = 7;
	private static final int mZ = 11;
	
	private DimensionalIndex index; 
	
	public DimensionalIndexTest( ) {
		index = new DimensionalIndex(mX, mY, mZ);
	}
	
	
	@Test
	public void validateIndexes( )  {
		boolean punched[] = new boolean[index.getXYZ()];
		for (int x = 0;  x < mX; x++)
			for (int y = 0;  y < mY; y++)
				for (int z = 0;  z < mZ; z++) {
					final int idx = index.rollup(x, y, z);
					assertFalse(punched[idx]);
					punched[idx] = true;
				}
		for (int p = 0; p < punched.length; ++p) {
            assertTrue(punched[p]); //ensure each index was used
		}
	}
	
	@Test
	public void badX( ) {
		assertThrows(RuntimeException.class, () -> {
			index.rollup(mX, 0, 0);
		});
	}
	
	@Test
	public void badY( ) {
		assertThrows(RuntimeException.class, () -> {
			index.rollup(0, mY, 0);
		});
	}
	
	@Test
	public void badZ( ) {
		assertThrows(RuntimeException.class, () -> {
			index.rollup(0, 0, mZ);
		});
	}
	
	@Test
	public void badZmsg( ) {
		try {
			index.rollup(0,0,mZ);
			fail("should have thrown an exception");
		} catch (RuntimeException re) {
		}
	}

}
