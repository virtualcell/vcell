package org.vcell.smoldyn.junittesting;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.vcell.smoldyn.model.Surface;
import org.vcell.smoldyn.model.util.Disk;
import org.vcell.smoldyn.model.util.Panel;
import org.vcell.smoldyn.model.util.Rectangle;
import org.vcell.smoldyn.model.util.Triangle;
import org.vcell.smoldyn.model.util.Vector;
import org.vcell.smoldyn.model.util.Point.PointFactory;

public class SurfaceTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test(expected=NullPointerException.class)
	public final void testSurface() {
		@SuppressWarnings("unused")
		Surface s = new Surface(null);
	}

	@Test
	public final void testGetName() {
		final String sname = "surfacename";
		Surface s = new Surface(sname);
		assertEquals("whatever", sname, s.getName());
	}

	@Test
	public final void testAddPanel() {
		Surface s = new Surface("surfacename");
		PointFactory pointfactory = new PointFactory(0, 10, 0, 10, 0, 10);
		s.addPanel(new Triangle(null, pointfactory.getNewPoint(1d,2d,3d), pointfactory.getNewPoint(4d,5d,6d), 
				pointfactory.getNewPoint(7d, 8d, 9d)));
		s.addPanel(new Rectangle(null, 1, pointfactory.getNewPoint(1d,2d,3d), 10, 11));
		s.addPanel(new Disk(null, pointfactory.getNewPoint(1d,2d,3d), 5, new Vector(new float[] {1,2,3})));
	}

	@Test
	public final void testGetPanels() {
		Surface s = new Surface("surfacename");
		PointFactory pointfactory = new PointFactory(0, 10, 0, 10, 0, 10);
		s.addPanel(new Triangle(null, pointfactory.getNewPoint(1d,2d,3d), pointfactory.getNewPoint(4d,5d,6d), 
				pointfactory.getNewPoint(7d, 8d, 9d)));
		s.addPanel(new Rectangle(null, 1, pointfactory.getNewPoint(1d,2d,3d), 10, 11));
		s.addPanel(new Disk(null, pointfactory.getNewPoint(1d,2d,3d), 5, new Vector(new float[] {1,2,3})));
		Panel [] panels = s.getPanels();
		assertEquals("how many panels?", 3, panels.length);
		for(Panel p : panels) {
			assertNotNull(p);
		}
	}

}
