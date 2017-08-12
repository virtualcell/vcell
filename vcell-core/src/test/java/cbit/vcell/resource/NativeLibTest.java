package cbit.vcell.resource;

import org.junit.Before;
import org.junit.Test;
import org.vcell.util.logging.Logging;

public class NativeLibTest {
	@Before
	public void init( ) {
		Logging.init( );
		System.setProperty(PropertyLoader.installationRoot,"..");
		ResourceUtil.init();
		ResourceUtil.setNativeLibraryDirectory();
	}
	
	@Test
	public void loadEm( ) {
		for (NativeLib nl : NativeLib.values()) {
			nl.load();
		}
	}

}
