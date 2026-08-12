package cbit.vcell.solvers.mb;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import io.jhdf.HdfFile;
import io.jhdf.api.Dataset;

/**
 * Walks a real MovingBoundary result with {@link MovingBoundaryVH5Path} — groups, compound members
 * and attributes — through the pure-java {@code io.jhdf}, with no native library loaded. The
 * fixture is the same 11x11 run of "2D kinematics analytic" used by
 * {@link MovingBoundaryReaderTest}.
 */
@Tag("Fast")
public class MovingBoundaryVH5PathTest {

	private HdfFile testFile;

	@BeforeEach
	public void setup() throws Exception {
		try (InputStream in = MovingBoundaryVH5PathTest.class.getResourceAsStream("moving-boundary-2d.h5")) {
			assertNotNull(in, "test resource moving-boundary-2d.h5 missing");
			Path temp = Files.createTempFile("moving-boundary-2d", ".h5");
			Files.copy(in, temp, StandardCopyOption.REPLACE_EXISTING);
			temp.toFile().deleteOnExit();
			testFile = new HdfFile(temp);
		}
	}

	@AfterEach
	public void close() {
		if (testFile != null) {
			testFile.close();
		}
	}

	@Test
	public void findsDatasetsGroupsAndCompoundMembers() {
		// a top-level dataset comes back as the dataset itself, to be read or sliced by the caller
		Object endTime = new MovingBoundaryVH5Path(testFile, "endTime").getData();
		assertInstanceOf(Dataset.class, endTime);

		// a member of a compound dataset is addressed like a child, and comes back as its column
		Object volume = new MovingBoundaryVH5Path(testFile, "elements", "volume").getData();
		assertInstanceOf(double[][][].class, volume, "one column of the [time][x][y] compound");

		// including the nested compound of boundary points
		Object volumePoints = new MovingBoundaryVH5Path(testFile, "elements", "volumePoints").getData();
		Object cell = ((Object[][][]) volumePoints)[0][0][0];
		assertInstanceOf(Map.class, cell);
		assertTrue(((Map<?, ?>) cell).containsKey("x"), "a boundary point carries x and y");

		// and a group is walked through to its children
		Object mesh = new MovingBoundaryVH5Path(testFile, "Mesh", "size").getData();
		assertInstanceOf(Dataset.class, mesh);
	}

	@Test
	public void findsAttributes() {
		// the last step of a path may name an attribute rather than a child
		Object startX = new MovingBoundaryVH5Path(testFile, "elements", "startX").getData();
		assertNotNull(startX, "elements carries its extents as attributes");
	}

	@Test
	public void typedPathReturnsTheDeclaredType() {
		double[] values = new MovingBoundaryVH5TypedPath<double[]>(testFile, double[].class, "endTime").get();
		assertEquals(1, values.length);
		assertEquals(0.1, values[0], 1e-9, "the run ends where it was asked to");
	}

	@Test
	public void missingPathIsNotFound() {
		assertNull(new MovingBoundaryVH5Path(testFile, "junk", "yard").getData());
		assertThrows(RuntimeException.class,
				() -> new MovingBoundaryVH5TypedPath<double[]>(testFile, double[].class, "junk", "yard"));
	}

	@Test
	public void wrongTypeIsRejected() {
		assertThrows(UnsupportedOperationException.class,
				() -> new MovingBoundaryVH5TypedPath<int[]>(testFile, int[].class, "endTime"));
	}

	@Test
	public void primitiveTypeIsRejected() {
		assertThrows(UnsupportedOperationException.class,
				() -> new MovingBoundaryVH5TypedPath<Integer>(testFile, int.class, "endTime"));
	}
}
