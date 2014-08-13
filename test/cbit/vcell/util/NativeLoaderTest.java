package cbit.vcell.util;

import static org.junit.Assert.assertTrue;

import java.util.concurrent.Future;
import java.util.prefs.BackingStoreException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.vcell.util.PropertyLoader;
import org.vcell.util.logging.Logging;

import cbit.vcell.util.NativeLoader.OsType;

/**
 * test native loading
 * @author gweatherby
 *
 */
public class NativeLoaderTest {
	@Before
	public void setup( ) throws BackingStoreException {
		if (PropertyLoader.getProperty(PropertyLoader.installationRoot,null) == null) {
			System.setProperty(PropertyLoader.installationRoot,System.getProperty("user.dir"));
		}
		Logging.init();
		Logger lg = Logger.getLogger(NativeGroup.class);
		lg.setLevel(Level.FATAL);
		NativeLoader.clean();
	}
	
	//@Test
	public void clean( ) throws BackingStoreException {
		NativeLoader.clean();
	}
	
	@Test
	public void loadEm( ) {
		NativeLoader.setOsType(OsType.WINDOWS); NativeLoader.setNativeLibraryDirectory("d:/workspace/VCellTrunk/nativelibs/win64");
		long start = System.currentTimeMillis();
		Future<Boolean> r = NativeLoader.load("vtk");
		Future<Boolean> x = NativeLoader.load("vtk");
		Future<Boolean> y = NativeLoader.load("vtk");
		assertTrue(r == x);
		assertTrue(y == x);
		long counter = 0;
		while (!r.isDone()) {
			counter++;
		}
		long finish = System.currentTimeMillis();
		System.out.println("ticks "  + counter + " in " + (finish - start)/1000.0);
		try {
			Boolean result = r.get();
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
