package cbit.vcell.solvers.mb;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import cbit.vcell.solvers.CartesianMeshMovingBoundary;
import cbit.vcell.solvers.mb.MovingBoundaryTypes.Element;
import cbit.vcell.solvers.mb.MovingBoundaryTypes.MeshInfo;
import cbit.vcell.solvers.mb.MovingBoundaryTypes.Plane;
import cbit.vcell.solvers.mb.MovingBoundaryTypes.TimeInfo;

/**
 * Reads a real MovingBoundary result through the pure-java {@code io.jhdf}, with no native library
 * loaded — the legacy {@code ncsa.hdf} binding has no arm64 build, so this used to be impossible on
 * an Apple-silicon machine.
 * <p>
 * The fixture is an 11x11 run of "2D kinematics analytic" from
 * {@code vcell-core/src/test/resources/models/Solver_Suite_6_2.vcml}: a disc of radius 3 centred in
 * a 10x10 box, its boundary driven at velocity (sin t, cos t), to t = 0.1.
 */
@Tag("Fast")
public class MovingBoundaryReaderTest {

	private static Path fixture() throws Exception {
		try (InputStream in = MovingBoundaryReaderTest.class.getResourceAsStream("moving-boundary-2d.h5")) {
			Assertions.assertNotNull(in, "test resource moving-boundary-2d.h5 missing");
			Path temp = Files.createTempFile("moving-boundary-2d", ".h5");
			Files.copy(in, temp, StandardCopyOption.REPLACE_EXISTING);
			temp.toFile().deleteOnExit();
			return temp;
		}
	}

	@Test
	public void readsMeshAndTimes() throws Exception {
		MovingBoundaryReader reader = new MovingBoundaryReader(fixture().toString());

		MeshInfo mesh = reader.getMeshInfo();
		Assertions.assertEquals(11, mesh.xinfo.number());
		Assertions.assertEquals(11, mesh.yinfo.number());
		Assertions.assertEquals(0.0, mesh.xinfo.start, 1e-12);
		Assertions.assertEquals(10.0, mesh.xinfo.end, 1e-6); // the solver's last node sits a hair past the box

		TimeInfo times = reader.getTimeInfo();
		Assertions.assertEquals(6, times.generationTimes.size(), "saved generations in this fixture");
		Assertions.assertEquals(6, reader.lastTimeIndex());
		Assertions.assertEquals(0.0, times.generationTimes.get(0), 1e-12);
		Assertions.assertTrue(times.generationTimes.get(times.generationTimes.size() - 1) <= 0.1 + 1e-9,
				"the run stops at the requested end time");
		for (int i = 1; i < times.generationTimes.size(); i++) {
			Assertions.assertTrue(times.generationTimes.get(i) > times.generationTimes.get(i - 1),
					"generation times increase");
		}
	}

	@Test
	public void readsAPlaneOfElements() throws Exception {
		MovingBoundaryReader reader = new MovingBoundaryReader(fixture().toString());
		Plane plane = reader.getPlane(1);

		Assertions.assertEquals(11, plane.getSizeX());
		Assertions.assertEquals(11, plane.getSizeY());

		// the disc covers the middle of the box, so the centre cell is interior and the corner is
		// outside it — this is what breaks first if the [time][x][y] slice is read transposed
		Assertions.assertEquals(Element.Position.INSIDE, plane.get(5, 5).position);
		Assertions.assertEquals(Element.Position.OUTSIDE, plane.get(0, 0).position);
		Assertions.assertTrue(plane.get(5, 5).volume > 0, "an interior cell has volume");
		Assertions.assertEquals(0.0, plane.get(0, 0).volume, 1e-12, "an exterior cell has none");

		int boundaryCells = 0;
		double totalVolume = 0;
		for (int x = 0; x < plane.getSizeX(); x++) {
			for (int y = 0; y < plane.getSizeY(); y++) {
				Element e = plane.get(x, y);
				totalVolume += e.volume;
				if (e.position == Element.Position.BOUNDARY) {
					boundaryCells++;
					Assertions.assertTrue(e.boundary().length > 0,
							"a boundary cell carries its boundary points");
				}
			}
		}
		Assertions.assertTrue(boundaryCells > 0, "the disc has cut cells");
		// area of a disc of radius 3, up to the boundary movement in the first step
		Assertions.assertEquals(Math.PI * 9, totalVolume, 1.5);
	}

	@Test
	public void readsTheMeshFileItself() throws Exception {
		CartesianMeshMovingBoundary mesh = CartesianMeshMovingBoundary.readMeshFile(fixture().toFile());

		Assertions.assertEquals(2, mesh.getDimension());
		Assertions.assertEquals(11, mesh.getSizeX());
		Assertions.assertEquals(11, mesh.getSizeY());
		Assertions.assertEquals(10.0, mesh.getExtent().getX(), 1e-12);
		Assertions.assertEquals(0.0, mesh.getOrigin().getY(), 1e-12);
	}
}
