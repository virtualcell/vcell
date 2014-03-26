package cbit.vcell.util;

import org.junit.Before;
import org.junit.Test;
import org.vcell.util.PropertyLoader;

import cbit.vcell.resource.ResourceUtil;

/**
 * test native loading
 * @author gweatherby
 *
 */
public class NativeLoaderTest {
	@Before
	public void setup( ) {
		if (PropertyLoader.getProperty(PropertyLoader.installationRoot,null) == null) {
			System.setProperty(PropertyLoader.installationRoot,System.getProperty("user.dir"));
		}
	}
	
	//@Test
	public void clean( ) {
		NativeLoader.clean();
	}
	
	@Test
	public void loadEm( ) {
		ResourceUtil.loadNativeLibraries();
	}

}
