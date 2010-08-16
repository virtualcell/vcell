package org.vcell.smoldyn.junittesting;
//package org.vcell.smoldyn.matts_project.junittesting;
//
//import static org.junit.Assert.*;
//import java.util.ArrayList;
//import org.junit.After;
//import org.junit.Before;
//import org.junit.Test;
//import org.vcell.smoldyn.matts_project.model.Boundaries;
//import org.vcell.smoldyn.matts_project.model.Model;
//import org.vcell.smoldyn.matts_project.model.SmoldynMolecule;
//import org.vcell.smoldyn.matts_project.model.util.Point;
//
//public class ModelTest {
//
//	private Model [] models;
//
//	@Before
//	public void setUp() throws Exception {
//		this.models = TestFixturing.getModels();
//	}
//
//	@After
//	public void tearDown() throws Exception {
//		this.models = null;
//	}
//
//	@Test
//	public final void testPointsWithinBoundaries() {
//		for(Model model : this.models) {
//			ArrayList<SmoldynMolecule> molecules = new ArrayList<SmoldynMolecule>();
//			for(SmoldynMolecule s : model.getVolumeMolecules()) {
//				molecules.add(s);
//			}
//			for(SmoldynMolecule s : model.getSurfaceMolecules()) {
//				molecules.add(s);
//			}
//			Boundaries [] boundaries = model.getGeometry().getBoundaries();
//			int count = 0;
//			for(SmoldynMolecule s : molecules) {
//				Point p = s.getPoint();
//				if(p == null) {
//					continue;
//				}
//				assertTrue("x testing", boundaries[0].getLow() <= p.getX() && boundaries[0].getHigh() >= p.getX());
//				assertTrue(boundaries[1].getLow() <= p.getY() && boundaries[1].getHigh() >= p.getY());
//				assertTrue(boundaries[2].getLow() <= p.getZ() && boundaries[2].getHigh() >= p.getZ());
//				++count;
//			}
//			System.out.println("tested " + count + " molecules for location");
//		}
//	}
//	
//	@Test
//	public final void testModel() {
//		fail("Not yet implemented"); // TODO
//	}
//
//	@Test
//	public final void testGetDimensionality() {
//		fail("Not yet implemented"); // TODO
//	}
//
//	@Test
//	public final void testAddSpecies() {
//		fail("Not yet implemented"); // TODO
//	}
//
//	@Test
//	public final void testGetSpecies() {
//		fail("Not yet implemented"); // TODO
//	}
//
//	@Test
//	public final void testGetSpeciesString() {
//		fail("Not yet implemented"); // TODO
//	}
//
//	@Test
//	public final void testAddSpeciesState() {
//		fail("Not yet implemented"); // TODO
//	}
//
//	@Test
//	public final void testGetSpeciesStates() {
//		fail("Not yet implemented"); // TODO
//	}
//
//	@Test
//	public final void testGetSpeciesState() {
//		fail("Not yet implemented"); // TODO
//	}
//
//	@Test
//	public final void testAddVolumeMolecule() {
//		fail("Not yet implemented"); // TODO
//	}
//
//	@Test
//	public final void testGetVolumeMolecules() {
//		fail("Not yet implemented"); // TODO
//	}
//
//	@Test
//	public final void testAddSurfaceMolecule() {
//		fail("Not yet implemented"); // TODO
//	}
//
//	@Test
//	public final void testGetSurfaceMolecules() {
//		fail("Not yet implemented"); // TODO
//	}
//
//	@Test
//	public final void testGetVolumeReactions() {
//		fail("Not yet implemented"); // TODO
//	}
//
//	@Test
//	public final void testAddVolumeReaction() {
//		fail("Not yet implemented"); // TODO
//	}
//
//	@Test
//	public final void testGetVolumeReaction() {
//		fail("Not yet implemented"); // TODO
//	}
//
//	@Test
//	public final void testGetSurfaceReactions() {
//		fail("Not yet implemented"); // TODO
//	}
//
//	@Test
//	public final void testAddSurfaceReaction() {
//		fail("Not yet implemented"); // TODO
//	}
//
//	@Test
//	public final void testGetSurfaceReaction() {
//		fail("Not yet implemented"); // TODO
//	}
//
//	@Test
//	public final void testGetGeometry() {
//		fail("Not yet implemented"); // TODO
//	}
//
//	@Test
//	public final void testAddManipulationEvent() {
//		fail("Not yet implemented"); // TODO
//	}
//
//	@Test
//	public final void testGetManipulationEvents() {
//		fail("Not yet implemented"); // TODO
//	}
//
//	@Test
//	public final void testGetSurfaceActions() {
//		fail("Not yet implemented"); // TODO
//	}
//
//	@Test
//	public final void testAddSurfaceActions() {
//		fail("Not yet implemented"); // TODO
//	}
//
//}
