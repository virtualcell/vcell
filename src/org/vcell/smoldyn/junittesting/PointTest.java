package org.vcell.smoldyn.junittesting;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.vcell.smoldyn.model.util.Point;
import org.vcell.smoldyn.model.util.Point.PointFactory;

public class PointTest {

	private Point point;
	private PointFactory pf;
	 
	@Before
	public void setUp() throws Exception {
		pf = new PointFactory(0, 10, 0, 10, 0, 10);
	}

	@After
	public void tearDown() throws Exception {
		point = null;
		pf = null;
	}

	@Test
	public void testPoint() {
		point = pf.getNewPoint(1d,2d,3d);
		assertNotNull(point);
	}

	@Test
	public void testGetX() {
		final double x = 1;
		point = pf.getNewPoint(x,2d,3d);
		assertEquals("problem with x: ", point.getX(), x, .000000001);
	}

	@Test
	public void testGetY() {
		final double y = 2;
		point = pf.getNewPoint(1d,y,3d);
		assertEquals("whatever", point.getY(), y, .000000001);
	}

	@Test
	public void testGetZ() {
		final double z = 3;
		point = pf.getNewPoint(1d,2d,z);
		assertEquals("whatever", point.getZ(), z, .000000001);
	}

}
