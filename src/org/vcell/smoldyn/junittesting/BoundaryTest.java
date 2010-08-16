package org.vcell.smoldyn.junittesting;
//package org.vcell.smoldyn.matts_project.junittesting;
//
//import static org.junit.Assert.*;
//import java.lang.IllegalArgumentException;
//import org.junit.After;
//import org.junit.Before;
//import org.junit.Test;
//import org.vcell.smoldyn.matts_project.model.Boundaries;
//
//public class BoundaryTest {
//
//	@Before
//	public void setUp() throws Exception {
//	}
//
//	@After
//	public void tearDown() throws Exception {
//	}
//
//	@Test
//	public final void testBoundary() {
//		final double low = 13;
//		final double high = 75;
//		Boundaries b = new Boundaries(low, high);
//		assertEquals("low value", low, b.getLow(), .00000001);
//		assertEquals("high value", high, b.getHigh(), .00000001);
//	}
//	
//	@Test(expected=IllegalArgumentException.class)
//	public final void testBoundaryInvariant() {
//		@SuppressWarnings("unused")
//		Boundaries b = new Boundaries(11, 7);
//	}
//
//}
