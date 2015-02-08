package cbit.vcell.resource;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.prefs.BackingStoreException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import cbit.vcell.client.test.VCellClientTest;
import cbit.vcell.resource.ResourceUtil;

@SuppressWarnings("unused")
public class ResourceUtilTest {
	
	public static String TEST_DIR  = "d:/test";
	public static String TEST_EXE  = "MovingBoundary";
	
	//@Test
	public void findTest() throws FileNotFoundException, BackingStoreException {
		 File f = ResourceUtil.getExecutable(TEST_EXE, false,null);
		 System.out.println(f.getAbsolutePath());
	}
	
	//@After //uncomment to remove stored executable locations
	public void clearStored( ) throws BackingStoreException {
		ResourceUtil.ExeCache.forgetExecutableLocations( );
	}
	
	@Test
	public void clearLicense( ) throws BackingStoreException {
		LicenseManager.clearLicense(LibraryLicense.CYGWIN);
	}
	//@Test
	public void readLicense( ) throws BackingStoreException {
		System.out.println(LicenseManager.getLicenseText(LibraryLicense.CYGWIN));
	}
}
