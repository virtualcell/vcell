package cbit.vcell.resource;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.prefs.BackingStoreException;

import org.junit.After;
import org.junit.Test;

import cbit.vcell.resource.ResourceUtil;

public class ResourceUtilTest {
	
	public static String TEST_DIR  = "d:/test";
	public static String TEST_EXE  = "MovingBoundary";
	
	@Test
	public void findTest() throws FileNotFoundException {
		 File f = ResourceUtil.getExecutable(TEST_EXE, false,null);
		 System.out.println(f.getAbsolutePath());
	}
	
	@After
	public void clearStored( ) throws BackingStoreException {
		ResourceUtil.ExeCache.forgetExecutableLocations( );
	}
	

}
