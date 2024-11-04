package cbit.vcell.resource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("Fast")
public class NativeLibTest {
	@BeforeEach
	public void init( ) {
		PropertyLoader.setProperty(PropertyLoader.installationRoot, "..");
		ResourceUtil.init();
	}
	
	@Test
	public void loadEm( ) {
		for (NativeLib nl : NativeLib.values()) {
			// skip if nl is HDF5 and system is a macos arm64
			if (nl == NativeLib.HDF5 && System.getProperty("os.arch").equals("aarch64") && System.getProperty("os.name").equals("Mac OS X")) {
				continue;
			}
			nl.load();
		}
	}

}
