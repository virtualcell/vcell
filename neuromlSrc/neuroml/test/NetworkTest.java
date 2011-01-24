package neuroml.test;

import java.io.*;
import java.util.*;

import neuroml.*;
import neuroml.network.*;
import neuroml.util.*;

import org.junit.*;

import static org.junit.Assert.*;

public class NetworkTest
{
	@Test public void testNetworkMarshalling() throws Exception
	{
		NeuroMLConverter conv = new NeuroMLConverter();
		
		String confdir = AllTests.properties.get(AllTests.PROP_CONFDIR);
		String fpath = confdir + File.separator + "test"
				          + File.separator +"SimpleNetworkInstance.xml";
		
		NetworkML netml = conv.xmlToNetwork(fpath);
		
		
		
	}
	
	
	@Test public void testCompleteNetwork() throws Exception
	{
		NeuroMLConverter conv = new NeuroMLConverter();
		
		String confdir = AllTests.properties.get(AllTests.PROP_CONFDIR);
		String fpath = confdir + File.separator + "test"
				          + File.separator +"CompleteNetwork.xml";
		
		NeuroMLLevel3 l3 = conv.readNeuroML(fpath);
		
		List<Level3Cell> l3cells = l3.getCellList();
		assertTrue(l3cells.size() == 1);
	
		List<Population> poplist = l3.getPopulationList();
		assertTrue(poplist.size() == 2);
		
		List<Projection> projlist = l3.getProjectionList();
		assertTrue(projlist.size() == 1);
		
	}
	

}
