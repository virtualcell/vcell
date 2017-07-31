package org.vcell.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Assert;
import org.junit.Test;

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
					final int idx = index.rollup(x,y,z);
					assertFalse(punched[idx]); //ensure each index unique
					punched[idx] = true;
				}
		for (int p = 0; p < punched.length; ++p) {
			assertTrue(punched[p]); //ensure each index was used 
		}
	}
	
	@Test(expected = RuntimeException.class)
	public void badX( ) {
		index.rollup(mX, 0,0); 
	}
	
	@Test(expected = RuntimeException.class)
	public void badY( ) {
		index.rollup(0, mY,0); 
	}
	
	@Test(expected = RuntimeException.class)
	public void badZ( ) {
		index.rollup(0,0,mZ); 
	}
	
	@Test
	public void badZmsg( ) {
		try {
			index.rollup(0,0,mZ);
			Assert.fail("should have thrown an exception");
		} catch (RuntimeException re) {
		}
	}

}
