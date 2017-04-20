package org.vcell.stochtest;

import java.io.File;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.OpenOption;
import java.nio.file.StandardOpenOption;
import java.rmi.dgc.VMID;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import cbit.vcell.resource.ResourceUtil;

/**
 * test fixture to allow multiple local simulations to run from different processes at same 
 * time; not sufficiently robust for production code
 * 
 * @author GWeatherby
 */
public class ResourceUtilTestFixture extends ResourceUtil {

	private ResourceUtilTestFixture() { }

	/**
	 * set base directory for multiple solver directories; must be existing directory.<br>
	 * must be called before first call to {@link #getSolversDirectory()}; <br>
	 * Note no disk cleanup is provided 
	 * @param baseDirectory
	 * @throws AssertionError if preconditions not met
	 */
	public static void useUniqueSolversDirectoryForTesting(File baseDirectory) {
		try {
			if (ResourceUtil.solversDirectory != null) {
				throw new AssertionError("call useUniqueSolversDirectory before getSolverDirectory( )");
			}
			if (!baseDirectory.isDirectory()) {
				throw new AssertionError(baseDirectory.getAbsolutePath() + " not directory");
			}
			
			Set<OpenOption> opts = new HashSet<OpenOption>(Arrays.asList(StandardOpenOption.WRITE,StandardOpenOption.CREATE));
			FileChannel fc = FileChannel.open(new File(baseDirectory,"lockFile").toPath( ), opts); 
			FileLock lock = fc.lock();
			try {
				File attempt = new File(baseDirectory,ResourceUtilTestFixture.likelyUniqueName());
				while (attempt.exists()) {
					attempt = new File(baseDirectory,ResourceUtilTestFixture.likelyUniqueName());
				}
				attempt.mkdirs();
				ResourceUtil.solversDirectory = attempt;
			}
			finally {
				lock.release();
			}
		}
		catch (Exception e) {
			throw new AssertionError("lock failed",e);
		}
	}

	/**
	 * generate file name likely but not guaranteed to be unique 
	 * @return new String
	 */
	private static String likelyUniqueName( ) {
		//base on unique JVM identifier and timestamp
		VMID vmid = new VMID( );
		int jvm = Math.abs(vmid.hashCode( ));
		return "tempSolvers" + jvm + System.currentTimeMillis();
	}

}
