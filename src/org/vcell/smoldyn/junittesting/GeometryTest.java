package org.vcell.smoldyn.junittesting;
//package org.vcell.smoldyn.matts_project.junittesting;
//
//import static org.junit.Assert.*;
//import org.junit.After;
//import org.junit.AfterClass;
//import org.junit.Before;
//import org.junit.BeforeClass;
//import org.junit.Test;
//import org.vcell.smoldyn.matts_project.model.Boundaries;
//import org.vcell.smoldyn.matts_project.model.Geometry;
//import org.vcell.smoldyn.matts_project.model.Model.Dimensionality;
//import org.vcell.smoldyn.matts_project.model.Surface;
//
//public class GeometryTest {
//
//	private Geometry geometry;
//	
//	@BeforeClass
//	public static void setUpBeforeClass() throws Exception {
//	}
//
//	@AfterClass
//	public static void tearDownAfterClass() throws Exception {
//	}
//
//	@Before
//	public void setUp() throws Exception {
//		this.geometry = new Geometry(new Boundaries [] {new Boundaries(0, 100), 
//				new Boundaries(0, 100), new Boundaries(0, 100)});
//	}
//
//	@After
//	public void tearDown() throws Exception {
//		this.geometry = null;
//	}
//
//	@Test
//	public final void testGeometry() {
//		fail("Not yet implemented"); // TODO
//	}
//
//	@Test
//	public final void testSetBoundaries() {
//		fail("Not yet implemented"); // TODO
//	}
//
//	@Test
//	public final void testGetBoundaries() {
//		fail("Not yet implemented"); // TODO
//	}
//
//	@Test
//	public final void testGetDimensionality() {
//		fail("Not yet implemented"); // TODO
//	}
//
//	@Test
//	public final void testAddCompartment() {
//		fail("Not yet implemented"); // TODO
//	}
//
//	@Test
//	public final void testGetCompartments() {
//		fail("Not yet implemented"); // TODO
//	}
//
//	@Test
//	public final void testGetCompartment() {
//		fail("Not yet implemented"); // TODO
//	}
//
//	@Test
//	public final void testHasCompartment() {
//		fail("Not yet implemented"); // TODO
//	}
//
//	@Test
//	public final void testAddSurface() {
//		String surfacename = "surfacename1";
//		geometry.addSurface(surfacename);
//	}
//
//	@Test
//	public final void testGetSurface() {
//		String surfacename = "whatever";
//		geometry.addSurface(surfacename);
//		Surface s = geometry.getSurface(surfacename);
//		assertEquals("surface name", surfacename, s.getName());
//	}
//
//	@Test
//	public final void testHasSurface() {
//		String surfacename = "surfacenamewhatever";
//		geometry.addSurface(surfacename);
//		assertTrue(geometry.hasSurface(surfacename));
//	}
//
//	@Test
//	public final void testGetSurfaces() {
//		String [] surfacenames = {"s1", "s2", "asdf", "asdfg", "sadwequiydfs", "asdfriuwerysdafh"};
//		for(String s : surfacenames) {
//			geometry.addSurface(s);
//		}
//		assertEquals("number of surfaces", surfacenames.length, geometry.getSurfaces().length);
//		for(String surfacename : surfacenames) {
//			assertEquals("surface name", geometry.getSurface(surfacename).getName(), surfacename);
//		}
//	}
//
//}
