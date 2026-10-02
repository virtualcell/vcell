package org.vcell.vis.vtk;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.vcell.util.document.KeyValue;
import org.vcell.vis.io.MovingBoundarySimFiles;
import org.vcell.vis.io.VtuFileContainer;
import org.vcell.vis.io.VtuVarInfo;
import org.vcell.vis.mapping.movingboundary.MovingBoundaryVtkFileWriter;
import org.vcell.vis.vismesh.thrift.MovingBoundaryIndexData;

import cbit.vcell.math.VariableType;
import cbit.vcell.simdata.SimDataConstants;
import cbit.vcell.solvers.mb.MovingBoundaryReader;
import cbit.vcell.solvers.mb.MovingBoundaryTypes.Element;
import cbit.vcell.solvers.mb.MovingBoundaryTypes.Plane;
import cbit.vcell.solvers.mb.PointIndex;
import cbit.vcell.solvers.mb.Vect3Didx;

/**
 * The MovingBoundary VTU seam — {@link MovingBoundaryVtkFileWriter#getEmptyVtuMeshFiles} and
 * {@link MovingBoundaryVtkFileWriter#getVtuMeshData} — run end to end on a real mbsolver result, with no
 * Python VTK service (the service's {@code .visMesh} hand-off file is never written).
 * <p>
 * The fixture is {@code cbit/vcell/solvers/mb/moving-boundary-2d.h5} (see {@code MovingBoundaryReaderTest}):
 * a disc moving through an 11x11 grid, six saved generations. Every saved time is checked against the
 * solver's own elements: one cell per inside-or-boundary element, in the reader's j-major order, whose
 * corners are exactly the element's boundary points; an index that maps each cell back to its element; and
 * a solution array that lands on the right cells.
 */
@Tag("Fast")
public class MovingBoundaryVtuWriterTest {

	static final KeyValue SIM_KEY = new KeyValue("1234");

	static File copyFixture(Path dir) throws Exception {
		try (InputStream in = MovingBoundaryVtuWriterTest.class.getResourceAsStream("/cbit/vcell/solvers/mb/moving-boundary-2d.h5")) {
			Assertions.assertNotNull(in, "test resource moving-boundary-2d.h5 missing");
			File h5 = dir.resolve("SimID_" + SIM_KEY + "_0_.h5").toFile();
			Files.copy(in, h5.toPath(), StandardCopyOption.REPLACE_EXISTING);
			return h5;
		}
	}

	/** the inside-or-boundary elements of a plane, in the order the mesh mapping visits them (j outer, i inner) */
	static List<int[]> insideElements(Plane plane) {
		List<int[]> out = new ArrayList<>();
		for (int j = 0; j < plane.getSizeY(); j++) {
			for (int i = 0; i < plane.getSizeX(); i++) {
				Element e = plane.get(i, j);
				if (e.position == Element.Position.INSIDE || e.position == Element.Position.BOUNDARY) {
					out.add(new int[] { i, j });
				}
			}
		}
		return out;
	}

	@Test
	public void writesEveryTimesMeshAndIndexWithoutPython(@TempDir Path dir) throws Exception {
		File h5 = copyFixture(dir);
		MovingBoundarySimFiles simFiles = new MovingBoundarySimFiles(SIM_KEY, 0, h5);
		File vtuDir = dir.resolve("vtu").toFile();
		Assertions.assertTrue(vtuDir.mkdir());

		MovingBoundaryReader reader = new MovingBoundaryReader(h5.getAbsolutePath());
		String domain = MovingBoundaryReader.getFakeInsideDomainName();
		PointIndex pointIndex = reader.getPointIndex();
		List<Double> times = reader.getTimeInfo().generationTimes;
		MovingBoundaryVtkFileWriter writer = new MovingBoundaryVtkFileWriter();
		VtuVarInfo var = new VtuVarInfo("C", "C", domain, VariableType.VariableDomain.VARIABLEDOMAIN_VOLUME, null,
				VtuVarInfo.DataType.CellData, false);

		int previousCells = -1;
		boolean meshChanged = false;
		for (int t = 0; t < times.size(); t++) {
			VtuFileContainer container = writer.getEmptyVtuMeshFiles(simFiles, t, vtuDir);
			Assertions.assertEquals(1, container.getVtuMeshes().size());
			VtuFileContainer.VtuMesh vtuMesh = container.getVtuMeshes().get(0);
			Assertions.assertEquals(domain, vtuMesh.domainName);
			Assertions.assertEquals(times.get(t), vtuMesh.time, 0.0);

			File vtuFile = new File(vtuDir, simFiles.getCannonicalFilePrefix(domain) + String.format("_%06d.vtu", t));
			Assertions.assertArrayEquals(Files.readAllBytes(vtuFile.toPath()), vtuMesh.vtuMeshContents, "the container carries the written file");
			VtuTestReader grid = VtuTestReader.read(vtuFile);

			Plane plane = reader.getPlane(t);
			List<int[]> elements = insideElements(plane);
			Assertions.assertFalse(elements.isEmpty());
			Assertions.assertEquals(elements.size(), grid.numberOfCells, "one cell per inside-or-boundary element at t=" + times.get(t));
			Assertions.assertEquals("Float64", grid.pointsType);
			for (int c = 0; c < elements.size(); c++) {
				int[] boundary = plane.get(elements.get(c)[0], elements.get(c)[1]).boundary();
				int corners = boundary.length - 1; // the boundary loop repeats its first point
				Assertions.assertEquals(corners, grid.cells[c].length, "cell " + c);
				Assertions.assertEquals(corners == 4 ? VtuWriter.VTK_QUAD : corners == 3 ? VtuWriter.VTK_TRIANGLE : VtuWriter.VTK_POLYGON,
						grid.types[c]);
				for (int p = 0; p < corners; p++) {
					Vect3Didx expected = pointIndex.lookup(boundary[p]);
					int q = grid.cells[c][p];
					Assertions.assertEquals(expected.x, grid.points[3 * q], 0.0);
					Assertions.assertEquals(expected.y, grid.points[3 * q + 1], 0.0);
					Assertions.assertEquals(expected.z, grid.points[3 * q + 2], 0.0);
				}
			}
			meshChanged |= previousCells >= 0 && previousCells != grid.numberOfCells;
			previousCells = grid.numberOfCells;

			// the index sits beside the result, one volume index per cell, in cell order
			File indexFile = new File(h5.getParentFile(),
					simFiles.getCannonicalFilePrefix(domain) + String.format("_%06d", t) + SimDataConstants.MOVINGBOUNDARYINDEX_FILE_EXTENSION);
			MovingBoundaryIndexData index = VisMeshUtils.readMovingBoundaryIndexData(indexFile);
			Assertions.assertEquals(domain, index.domainName);
			Assertions.assertEquals(elements.size(), index.getMovingBoundaryVolumeIndicesSize());
			for (int c = 0; c < elements.size(); c++) {
				Assertions.assertEquals(c, index.getMovingBoundaryVolumeIndices().get(c).index);
			}

			// a raster whose value is its own raster index comes back, per cell, as that cell's element
			int numX = plane.getSizeX();
			double[] raster = new double[numX * plane.getSizeY()];
			for (int k = 0; k < raster.length; k++) {
				raster[k] = k;
			}
			double[] data = writer.getVtuMeshData(simFiles, raster, vtuDir, var, t);
			Assertions.assertEquals(elements.size(), data.length);
			for (int c = 0; c < data.length; c++) {
				Assertions.assertEquals(elements.get(c)[0] + numX * elements.get(c)[1], data[c], 0.0, "cell " + c);
			}

			// and the existing cell-data appender accepts the file
			File withData = new File(vtuDir, "withData_" + t + ".vtu");
			VisMeshUtils.writeCellDataToVtu(vtuFile, "C", data, withData);
			Assertions.assertTrue(new String(Files.readAllBytes(withData.toPath())).contains("Name=\"C\""));
		}
		Assertions.assertTrue(meshChanged || times.size() == 1, "the fixture's domain moves, so its cell count should change");

		// the Python service's intermediate file is never produced
		try (var files = Files.walk(dir)) {
			Assertions.assertTrue(files.noneMatch(f -> f.toString().endsWith(".visMesh")), "no .visMesh hand-off to Python");
		}
	}
}
