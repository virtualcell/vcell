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

import cbit.vcell.math.RowColumnResultSet;
import cbit.vcell.resource.NativeLib;
import cbit.vcell.resource.OperatingSystemInfo;
import cbit.vcell.resource.ResourceUtil;
import cbit.vcell.solvers.NativeCVODESolver;
import cbit.vcell.solvers.NativeIDASolver;

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
		lg.setLevel(Level.DEBUG);
		NativeLoader.clean();
		ResourceUtil.setNativeLibraryDirectory();
	}
	
	//@Test
	public void clean( ) throws BackingStoreException {
		NativeLoader.clean();
	}
	
	//@Test
	public void loadEm( ) {
		long start = System.currentTimeMillis();
		Future<Boolean> r = NativeLoader.load(NativeLib.NATIVE_SOLVERS.name());
		Future<Boolean> x = NativeLoader.load(NativeLib.NATIVE_SOLVERS.name());
		Future<Boolean> y = NativeLoader.load(NativeLib.NATIVE_SOLVERS.name());
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
	
	@Test
	public void nativeIDATest( ) throws Exception {
		NativeLoader.setNativeLibraryDirectory(System.getProperty("user.dir") + "/nativelibs/win64");
		NativeLoader.setOsType(OperatingSystemInfo.OsType.WINDOWS);
		NativeLib.NATIVE_SOLVERS.load();
		NativeIDASolver ns = new NativeIDASolver();
		double x[] = {1.0, 1.0, 1000.0};
		@SuppressWarnings("unused")
		RowColumnResultSet out = ns.solve(NativeIDASolver.testInput(),x);
	}
	
	@Test
	public void nativeCVODETest( )  {
		NativeLoader.setNativeLibraryDirectory(System.getProperty("user.dir") + "/nativelibs/win64");
		NativeLoader.setOsType(OperatingSystemInfo.OsType.WINDOWS);
		NativeLib.NATIVE_SOLVERS.load();
		try {
			final NativeCVODESolver nativesolver = new NativeCVODESolver();
							
			String input = "SOLVER CVODE\n"+ 	
				"STARTING_TIME 0.0\n"+
				"ENDING_TIME 100000.0\n"+
				"RELATIVE_TOLERANCE 1.0E-9\n"+
				"ABSOLUTE_TOLERANCE 1.0E-9\n"+
				"MAX_TIME_STEP 1.0\n"+
				"KEEP_EVERY 1\n"+
				"NUM_EQUATIONS 2\n"+
				"ODE species1_cyt INIT 2.0;\n RATE  - (species1_cyt - species2_cyt);\n"+
				"ODE species2_cyt INIT 3.0;\n RATE ( - ((0.01 * species2_cyt) - (6.0 - species2_cyt - species1_cyt)) + species1_cyt - species2_cyt);";	

			/*
			Thread t = new Thread() {
				public void run() {
					try {
					Thread.sleep(200);
					nativesolver.setStopRequested(true);
					} catch (Exception ex) {
					}
				}
			};

			t.start();
			
			*/
			
			System.out.println("**************Solve 2************************");
			cbit.vcell.math.RowColumnResultSet rcrs = nativesolver.solve(input);
			for (int i = 0; i < rcrs.getRowCount(); i ++) {
				double[] row = rcrs.getRow(i);
				for (int j = 0; j < row.length; j ++) {
					System.out.print(row[j] + " ");
				}
				System.out.println();
			}
		} catch (Exception ex) {
			System.out.println("\nJava caught exception: " + ex.getMessage());
		}
	}

}
