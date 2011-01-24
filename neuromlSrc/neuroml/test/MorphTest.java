package neuroml.test;

import java.io.*;
import java.math.*;
import java.util.*;

import javax.xml.bind.*;
import javax.xml.namespace.*;

import neuroml.meta.*;
import neuroml.morph.*;
import neuroml.morph.Cell.*;
import neuroml.util.*;

import org.junit.*;
import static org.junit.Assert.*;

public class MorphTest
{
	@Test public void testSimpleMorphMarshalling() throws Exception
	{
		neuroml.morph.ObjectFactory ofac = new neuroml.morph.ObjectFactory();
		
		Morphology morph = ofac.createMorphology();
				
		Cell c = ofac.createCell();
		c.setName("JAXB-Generated Simple Cell");		
		
		Segment seg = ofac.createSegment();
		seg.setId(new BigInteger("1"));
		seg.setParent(new BigInteger("0"));
		
		List<Segment> seglist = c.getSegmentList();
		seglist.add(seg);
		
		List<Cell> clist = morph.getCellList();
		clist.add(c);

		String tempdir = AllTests.properties.get(AllTests.PROP_TEMPDIR);
		
		NeuroMLConverter conv = new NeuroMLConverter();
		conv.morphologyToXml(morph, tempdir + File.separator + "simple-cell.xml");
	}
	
	@Test public void testSimpleMorphUnmarshalling() throws Exception
	{
		NeuroMLConverter conv = new NeuroMLConverter();
						
		String fpath = "Simple.morph.xml";
		
		Morphology morph = conv.xmlToMorphology(fpath);
		
		String notes = morph.getNotes();
		assertTrue(
				"A single simple cell, using the MorphML namespace for validation.".equals(notes.trim()));
		
		List<Cell> clist = morph.getCellList();		
		Cell cell = clist.get(0);
		assertTrue("Simple cell".equals(cell.getName()));
		
		List<Segment> segList = cell.getSegmentList();
		assertTrue(segList.size() == 2);
		
		Segment seg1 = segList.get(0);
		assertTrue(seg1.getId().equals(new BigInteger("0")));
		assertTrue("Soma".equals(seg1.getName()));
				
		Point p1 = seg1.getProximal();
		assertTrue((p1.getX() == 0.0) && (p1.getY() == 0.0) && (p1.getZ() == 0.0));
		assertTrue(p1.getDiameter() == 10.0);
		Point d1 = seg1.getDistal();
		assertTrue((d1.getX() == 10.0) && (d1.getY() == 0.0) && (d1.getZ() == 0.0));
		assertTrue(d1.getDiameter() == 10.0);
		
		Segment seg2 = segList.get(1);
		assertTrue(seg2.getId().equals(new BigInteger("1")));
		assertTrue("Dendrite".equals(seg2.getName()));
		
		Point p2 = seg2.getProximal();
		assertTrue((p2.getX() == 10.0) && (p2.getY() == 0.0) && (p2.getZ() == 0.0));
		assertTrue(p2.getDiameter() == 3.0);
		Point d2 = seg2.getDistal();
		assertTrue((d2.getX() == 20.0) && (d2.getY() == 0.0) && (d2.getZ() == 0.0));
		assertTrue(d2.getDiameter() == 3.0);
		
		List<Cable> cblList = cell.getCableList();
		assertTrue(cblList.size() == 2);
		
		Cable cbl1 = cblList.get(0);
		assertTrue("SomaCable".equals(cbl1.getName()));
		assertTrue(cbl1.getId().equals(new BigInteger("0")));
		List<String> groups = cbl1.getGroup();
		assertTrue(groups.size() == 1);
		assertTrue("soma_group".equals(groups.get(0)));
		
		Cable cbl2 = cblList.get(1);
		assertTrue("DendriteCable".equals(cbl2.getName()));
		assertTrue(cbl2.getId().equals(new BigInteger("1")));
		groups = cbl2.getGroup();
		assertTrue(groups.size() == 1);
		assertTrue("dendrite_group".equals(groups.get(0)));		
	}
	
	//incomplete
	@Test public void testMossyMorphUnmarshalling() throws Exception
	{
		NeuroMLConverter conv = new NeuroMLConverter();
		
		String confdir = AllTests.properties.get(AllTests.PROP_CONFDIR);
		String fpath = confdir + File.separator + "test"
				          + File.separator +"MossyCell.xml";
		
		Morphology morph = conv.xmlToMorphology(fpath);		
		assertNotNull(morph);
		
		List<Cell> clist = morph.getCellList();
		Cell mcell = clist.get(0);
		assertNotNull(mcell);
		
		String notes = mcell.getNotes();
		assertTrue("An abstracted Mossy Cell morphology from Santhakumar et al 2005".equals(notes));
	}

}
