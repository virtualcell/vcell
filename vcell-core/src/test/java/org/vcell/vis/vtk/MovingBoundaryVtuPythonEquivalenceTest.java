package org.vcell.vis.vtk;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.vcell.util.PythonUtils;
import org.vcell.vis.mapping.movingboundary.MovingBoundaryMeshMapping;
import org.vcell.vis.vismesh.thrift.MovingBoundaryIndexData;
import org.vcell.vis.vismesh.thrift.VisMesh;

import cbit.vcell.solvers.mb.MovingBoundaryReader;

/**
 * The pure-Java MovingBoundary grid writer ({@link VtkService#writeMovingBoundaryVtkGridAndIndexData}) against
 * the Python VTK service it replaced ({@code pythonVtk}, mesh type {@code movingboundary}), on every saved time
 * of a real mbsolver result: the same cells, the same cell types and connectivity, the same points (Python
 * stores them as Float32, so to single precision), and an identical index record.
 * <p>
 * Needs the {@code pythonVtk} Poetry environment, which CI installs for the Fast tests; skipped where it is
 * missing.
 */
@Tag("Fast")
public class MovingBoundaryVtuPythonEquivalenceTest {

	private static final File PYTHON_VTK_DIR = new File("../pythonVtk");

	private static boolean pythonVtkAvailable() {
		if (!new File(PYTHON_VTK_DIR, "python_vtk/vtkService/vtkService.py").isFile()) {
			return false;
		}
		try {
			List<String> command = PythonUtils.pythonCommandPrefix();
			command.addAll(List.of("-c", "import vtk, thrift"));
			Process p = new ProcessBuilder(command).directory(PYTHON_VTK_DIR).redirectErrorStream(true).start();
			p.getInputStream().transferTo(java.io.OutputStream.nullOutputStream());
			return p.waitFor(120, TimeUnit.SECONDS) && p.exitValue() == 0;
		} catch (Exception e) {
			return false;
		}
	}

	private static double maxDiff(double[] a, double[] b) {
		double d = 0;
		for (int i = 0; i < a.length; i++) {
			d = Math.max(d, Math.abs(a[i] - b[i]));
		}
		return d;
	}

	@Test
	public void javaWriterMatchesPythonService(@TempDir Path dir) throws Exception {
		Assumptions.assumeTrue(pythonVtkAvailable(), "pythonVtk Poetry environment not installed");
		File h5 = MovingBoundaryVtuWriterTest.copyFixture(dir);
		MovingBoundaryReader reader = new MovingBoundaryReader(h5.getAbsolutePath());
		String domain = MovingBoundaryReader.getFakeInsideDomainName();
		int numTimes = reader.getTimeInfo().generationTimes.size();
		VtkService javaService = VtkService.getInstance();

		for (int t = 0; t < numTimes; t++) {
			VisMesh visMesh = new MovingBoundaryMeshMapping().fromReader(reader, MovingBoundaryMeshMapping.DomainType.INSIDE, t);

			File javaVtu = dir.resolve("java_" + t + ".vtu").toFile();
			File javaIndex = dir.resolve("java_" + t + ".movingboundaryindex").toFile();
			javaService.writeMovingBoundaryVtkGridAndIndexData(visMesh, domain, javaVtu, javaIndex);

			File visMeshFile = dir.resolve("python_" + t + ".visMesh").toFile();
			File pythonVtu = dir.resolve("python_" + t + ".vtu").toFile();
			File pythonIndex = dir.resolve("python_" + t + ".movingboundaryindex").toFile();
			VisMeshUtils.writeVisMesh(visMeshFile, visMesh);
			PythonUtils.callPythonModule(PYTHON_VTK_DIR.getAbsoluteFile(), "python_vtk.vtkService.vtkService", new String[] {
					"movingboundary", domain, visMeshFile.getAbsolutePath(), pythonVtu.getAbsolutePath(), pythonIndex.getAbsolutePath() });

			VtuTestReader j = VtuTestReader.read(javaVtu);
			VtuTestReader p = VtuTestReader.read(pythonVtu);
			String at = " at time index " + t;
			Assertions.assertEquals(p.numberOfPoints, j.numberOfPoints, "points" + at);
			Assertions.assertEquals(p.numberOfCells, j.numberOfCells, "cells" + at);
			Assertions.assertArrayEquals(p.types, j.types, "cell types" + at);
			for (int c = 0; c < p.numberOfCells; c++) {
				Assertions.assertArrayEquals(p.cells[c], j.cells[c], "connectivity of cell " + c + at);
			}
			double scale = 0;
			for (double v : j.points) {
				scale = Math.max(scale, Math.abs(v));
			}
			double tolerance = "Float32".equals(p.pointsType) ? 1e-6 * Math.max(scale, 1) : 1e-12 * Math.max(scale, 1);
			for (int i = 0; i < j.points.length; i++) {
				Assertions.assertEquals(p.points[i], j.points[i], tolerance, "coordinate " + i + at);
			}

			MovingBoundaryIndexData javaIndexData = VisMeshUtils.readMovingBoundaryIndexData(javaIndex);
			MovingBoundaryIndexData pythonIndexData = VisMeshUtils.readMovingBoundaryIndexData(pythonIndex);
			Assertions.assertEquals(pythonIndexData, javaIndexData, "index data" + at);
			Assertions.assertArrayEquals(Files.readAllBytes(pythonIndex.toPath()), Files.readAllBytes(javaIndex.toPath()),
					"index file bytes" + at);
			System.out.println("MovingBoundary t" + t + ": java == python: " + j.numberOfPoints + " points, " + j.numberOfCells
					+ " cells, " + javaIndexData.getMovingBoundaryVolumeIndicesSize() + " volume indices (python points "
					+ p.pointsType + ", max |dx| " + maxDiff(p.points, j.points) + ")");
		}
	}
}
