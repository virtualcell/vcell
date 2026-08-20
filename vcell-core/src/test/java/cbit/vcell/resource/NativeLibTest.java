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
		// Vacuous while no native libraries are declared, which is the point: it starts working
		// again the moment one is added.
		for (NativeLib nl : NativeLib.values()) {
			nl.load();
		}
	}

}
