package cbit.vcell.resource;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.prefs.BackingStoreException;

import org.junit.After;
import org.junit.Test;

import cbit.vcell.resource.ResourceUtil;
import cbit.vcell.solver.SolverDescription.SpecialLicense;

@SuppressWarnings("unused")
public class ResourceUtilTest {
	
	public static String TEST_DIR  = "d:/test";
	public static String TEST_EXE  = "MovingBoundary";
	
	//@Test
	public void findTest() throws FileNotFoundException {
		 File f = ResourceUtil.getExecutable(TEST_EXE, false,null);
		 System.out.println(f.getAbsolutePath());
	}
	
	//@After uncomment to remove stored executable locations
	public void clearStored( ) throws BackingStoreException {
		ResourceUtil.ExeCache.forgetExecutableLocations( );
	}
	
	@Test
	public void clearLicense( ) throws BackingStoreException {
		ResourceUtil.clearLicense(SpecialLicense.CYGWIN);
	}
}
