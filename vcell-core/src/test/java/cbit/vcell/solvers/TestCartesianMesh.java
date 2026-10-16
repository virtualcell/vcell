package cbit.vcell.solvers;
import cbit.vcell.math.MathException;
import cbit.vcell.simdata.SimDataConstants;
import cbit.vcell.solvers.CartesianMesh;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.vcell.util.Matchable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.file.Files;


public class TestCartesianMesh {
	private static String SIM_ID_PREFIX = "SimID_707203259_0_";
	private static String MESH_FILE_LOCATION = SIM_ID_PREFIX + SimDataConstants.MESHFILE_EXTENSION;
	private static String MESHMETRICS_FILE_LOCATON = SIM_ID_PREFIX + SimDataConstants.MESHMETRICSFILE_EXTENSION;
	private static String SUBDOMAINS_FILE_LOCATION = SIM_ID_PREFIX + SimDataConstants.SUBDOMAINS_FILE_SUFFIX;

	@Test
	public void testCartesianMesh() throws IOException, MathException {
		try (
				InputStream meshStream = TestCartesianMesh.class.getResourceAsStream(MESH_FILE_LOCATION);
				InputStream meshMetricsStream = TestCartesianMesh.class.getResourceAsStream(MESHMETRICS_FILE_LOCATON);
				InputStream subdomainsStream = TestCartesianMesh.class.getResourceAsStream(SUBDOMAINS_FILE_LOCATION);
		){
			CartesianMesh firstRead = CartesianMesh.readFromStreams(meshStream, meshMetricsStream, subdomainsStream);
			File tempFile = Files.createTempFile(TestCartesianMesh.class.getSimpleName(), "").toFile();
			tempFile.deleteOnExit();
			firstRead.write(new PrintStream(tempFile));
			CartesianMesh secondRead = CartesianMesh.readFromFiles(tempFile, null, null);
			Assertions.assertTrue(Matchable.areEqual(firstRead, secondRead));
		}
	}
}
