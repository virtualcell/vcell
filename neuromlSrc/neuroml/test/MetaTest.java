package neuroml.test;

import java.io.*;
import java.util.*;
import javax.xml.bind.*;

import neuroml.meta.*;
import org.junit.*;

public class MetaTest
{
	@Test public void testPoints() throws Exception
	{		
		neuroml.meta.ObjectFactory ofac = new neuroml.meta.ObjectFactory();
		Points pnts = ofac.createPoints();
		List<Point> pntList = pnts.getPoint();
		
		Assert.assertTrue(pntList != null);
		Assert.assertTrue(pntList.size() == 0);		
		
		Point p1 = ofac.createPoint();
		Assert.assertTrue(p1 != null);
		p1.setDiameter(5.5);
		p1.setX(1.1);
		p1.setY(2.2);
		p1.setZ(3.3);
		
		pntList.add(p1);
	}
}
