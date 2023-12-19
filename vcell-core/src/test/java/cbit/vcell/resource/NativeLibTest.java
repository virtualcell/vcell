package cbit.vcell.resource;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.vcell.test.Fast;

@Category(Fast.class)
public class NativeLibTest {
	@Before
	public void init( ) {
		PropertyLoader.setProperty(PropertyLoader.installationRoot, "..");
		ResourceUtil.init();
	}
	
	@Test
	public void loadEm( ) {
		for (NativeLib nl : NativeLib.values()) {
			nl.load();
		}
	}

}
