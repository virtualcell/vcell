package org.vcell.sbml;

import org.junit.Test;
import org.sbml.libsbml.AnalyticVolume;
import org.vcell.util.logging.Logging;

import cbit.vcell.resource.NativeLib;
import cbit.vcell.resource.ResourceUtil;

public class ListOfAdapterTest {
	private static final String IPROP = "vcell.installDir";

	public ListOfAdapterTest() {
		Logging.init( );
		if (System.getProperty(IPROP) == null) {
			System.setProperty(IPROP, "cwd");
		}
		ResourceUtil.setNativeLibraryDirectory();
		NativeLib.SBML.load();
	}
	
	@Test
	public void testEmpty( ) {
		ListOfAdapter.EmptyList empty = new ListOfAdapter.EmptyList( );
		System.out.println(empty.size());
	}
	
	@Test
	public void testNull( ) {
		ListOfAdapter<AnalyticVolume> empty = new ListOfAdapter<>(null, AnalyticVolume.class); 
		System.out.println(empty.size());
		for (AnalyticVolume av: empty) {
			System.err.println("nope" + av.getDomainType());
		}
	}

}
