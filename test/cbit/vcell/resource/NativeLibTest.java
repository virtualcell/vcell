package cbit.vcell.resource;

import org.junit.Before;
import org.junit.Test;
import org.vcell.util.logging.Logging;

import cbit.vcell.util.NativeLoader;

public class NativeLibTest {
	@Before
	public void init( ) {
		Logging.init( );
		ResourceUtil.init();
		NativeLoader.setNativeLibraryDirectory("D:/workspace/VCellTrunk/nativelibs/win64");
	}
	
	@Test
	public void loadEm( ) {
		for (NativeLib nl : NativeLib.values()) {
			nl.load();
		}
	}

}
