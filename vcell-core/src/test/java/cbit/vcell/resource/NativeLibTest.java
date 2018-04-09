package cbit.vcell.resource;

import org.junit.Before;
import org.junit.Test;

public class NativeLibTest {
	@Before
	public void init( ) {
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
