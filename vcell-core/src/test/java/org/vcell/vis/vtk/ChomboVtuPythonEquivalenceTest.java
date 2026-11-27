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
import org.vcell.vis.vismesh.thrift.ChomboIndexData;
import org.vcell.vis.vismesh.thrift.VisMesh;

/**
 * The pure-Java Chombo grid writers ({@link VtkService#writeChomboVolumeVtkGridAndIndexData},
 * {@link VtkService#writeChomboMembraneVtkGridAndIndexData}) against the Python VTK service they replaced
 * ({@code pythonVtk}, mesh types {@code chombovolume} and {@code chombomembrane}), on the {@link VisMesh}es
 * {@code ChomboMeshMapping} builds from two real Chombo results ({@link ChomboRunFixture}: 2D, and 3D with its
 * cut-cell polyhedra): the same cells, the same cell types and connectivity, the same polyhedron faces, the same
 * points (Python stores them as Float32, so to single precision), and an identical index record.
 * <p>
 * Needs the {@code pythonVtk} Poetry environment, which CI installs for the Fast tests; skipped where it is
 * missing.
 */
@Tag("Fast")
public class ChomboVtuPythonEquivalenceTest {

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

	@Test
	public void javaWritersMatchPythonService3D(@TempDir Path dir) throws Exception {
		Assumptions.assumeTrue(pythonVtkAvailable(), "pythonVtk Poetry environment not installed");
		compare(ChomboRunFixture.SIM_3D, dir);
	}

	@Test
	public void javaWritersMatchPythonService2D(@TempDir Path dir) throws Exception {
		Assumptions.assumeTrue(pythonVtkAvailable(), "pythonVtk Poetry environment not installed");
		compare(ChomboRunFixture.SIM_2D, dir);
	}

	private static void compare(String sim, Path dir) throws Exception {
		VisMesh visMesh = ChomboRunFixture.visMesh(ChomboRunFixture.chomboFiles(sim, dir.resolve("data")));
		File visMeshFile = dir.resolve("mesh.visMesh").toFile();
		VisMeshUtils.writeVisMesh(visMeshFile, visMesh);
		VtkService javaService = VtkService.getInstance();

		for (boolean membrane : new boolean[] { false, true }) {
			String kind = membrane ? "membrane" : "volume";
			String domain = membrane ? ChomboRunFixture.MEMBRANE : ChomboRunFixture.VOLUME;
			File javaVtu = dir.resolve("java_" + kind + ".vtu").toFile();
			File javaIndex = dir.resolve("java_" + kind + ".chomboindex").toFile();
			if (membrane) {
				javaService.writeChomboMembraneVtkGridAndIndexData(visMesh, domain, javaVtu, javaIndex);
			} else {
				javaService.writeChomboVolumeVtkGridAndIndexData(visMesh, domain, javaVtu, javaIndex);
			}
			File pythonVtu = dir.resolve("python_" + kind + ".vtu").toFile();
			File pythonIndex = dir.resolve("python_" + kind + ".chomboindex").toFile();
			PythonUtils.callPythonModule(PYTHON_VTK_DIR.getAbsoluteFile(), "python_vtk.vtkService.vtkService", new String[] {
					membrane ? "chombomembrane" : "chombovolume", domain, visMeshFile.getAbsolutePath(), pythonVtu.getAbsolutePath(),
					pythonIndex.getAbsolutePath() });

			VtuTestReader j = VtuTestReader.read(javaVtu);
			VtuTestReader p = VtuTestReader.read(pythonVtu);
			String at = " (" + sim + " " + kind + ")";
			Assertions.assertTrue(j.numberOfCells > 0, "cells" + at);
			Assertions.assertEquals(p.numberOfPoints, j.numberOfPoints, "points" + at);
			Assertions.assertEquals(p.numberOfCells, j.numberOfCells, "cells" + at);
			Assertions.assertArrayEquals(p.types, j.types, "cell types" + at);
			int polyhedra = 0;
			for (int c = 0; c < p.numberOfCells; c++) {
				Assertions.assertArrayEquals(p.cells[c], j.cells[c], "connectivity of cell " + c + at);
				int[][] pf = p.faces != null ? p.faces[c] : null;
				int[][] jf = j.faces != null ? j.faces[c] : null;
				Assertions.assertEquals(pf == null, jf == null, "faces of cell " + c + at);
				if (pf != null) {
					polyhedra++;
					Assertions.assertEquals(pf.length, jf.length, "number of faces of cell " + c + at);
					for (int f = 0; f < pf.length; f++) {
						Assertions.assertArrayEquals(pf[f], jf[f], "face " + f + " of cell " + c + at);
					}
				}
			}
			double scale = 1;
			for (double v : j.points) {
				scale = Math.max(scale, Math.abs(v));
			}
			double tolerance = "Float32".equals(p.pointsType) ? 1e-6 * scale : 1e-12 * scale;
			double maxDiff = 0;
			for (int i = 0; i < j.points.length; i++) {
				Assertions.assertEquals(p.points[i], j.points[i], tolerance, "coordinate " + i + at);
				maxDiff = Math.max(maxDiff, Math.abs(p.points[i] - j.points[i]));
			}

			ChomboIndexData javaIndexData = VisMeshUtils.readChomboIndexData(javaIndex);
			ChomboIndexData pythonIndexData = VisMeshUtils.readChomboIndexData(pythonIndex);
			Assertions.assertEquals(pythonIndexData, javaIndexData, "index data" + at);
			Assertions.assertArrayEquals(Files.readAllBytes(pythonIndex.toPath()), Files.readAllBytes(javaIndex.toPath()),
					"index file bytes" + at);
			System.out.println("Chombo " + sim + " " + kind + ": java == python: " + j.numberOfPoints + " points, " + j.numberOfCells
					+ " cells (" + polyhedra + " polyhedra), " + (membrane ? javaIndexData.getChomboSurfaceIndicesSize()
							: javaIndexData.getChomboVolumeIndicesSize())
					+ " indices (python points " + p.pointsType + ", max |dx| " + maxDiff + ")");
		}
	}
}
