package org.vcell.vis.vtk;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.parallel.ResourceLock;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.vis.io.VtuFileContainer;
import org.vcell.vis.io.VtuVarInfo;

import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.simdata.Cachetable;
import cbit.vcell.simdata.DataServerImpl;
import cbit.vcell.simdata.DataSetControllerImpl;
import cbit.vcell.simdata.OutputContext;
import cbit.vcell.solver.AnnotatedFunction;
import cbit.vcell.solver.VCSimulationDataIdentifier;
import cbit.vcell.solver.VCSimulationIdentifier;
import cbit.vcell.solvers.mb.MovingBoundaryReader;

/**
 * The MovingBoundary VTU seam as the <b>data server</b> runs it: {@link DataServerImpl} over a
 * {@link DataSetControllerImpl}, a server-run (non-local) {@link VCSimulationDataIdentifier}, the result under
 * {@code vcell.primarySimdatadir.internal/<owner>/}, and — as in the deployed {@code data} container —
 * {@code vcell.vtk.pythonDir} not set. Before the Java writer, {@code getEmptyVtuMeshFiles} failed there with
 * "required System property vcell.vtk.pythonDir not defined" (after decoding the whole result, see
 * {@code MovingBoundaryVH5PathTest}), so server-run MovingBoundary results never reached the field viewer.
 * <p>
 * The run is the {@code moving-boundary-2d.h5} fixture, given the {@code .log} (and an empty {@code .functions})
 * the solver writes beside it.
 */
@Tag("Fast")
@ResourceLock("vcellGlobalConfig")
public class MovingBoundaryServerVtuPathTest {

	@Test
	public void theServerServesMovingBoundaryMeshesAndDataWithoutPython(@TempDir Path root) throws Exception {
		User owner = new User("mbowner", new KeyValue("77"));
		Path userDir = Files.createDirectories(root.resolve(owner.getName()));
		File h5 = MovingBoundaryVtuWriterTest.copyFixture(userDir); // SimID_1234_0_.h5
		List<Double> times = new MovingBoundaryReader(h5.getAbsolutePath()).getTimeInfo().generationTimes;
		StringBuilder log = new StringBuilder("MBSData\n");
		for (int t = 0; t < times.size(); t++) {
			log.append(t).append(' ').append(h5.getName()).append(' ').append(String.format(Locale.ROOT, "%.9g", times.get(t))).append('\n');
		}
		Files.writeString(userDir.resolve("SimID_" + MovingBoundaryVtuWriterTest.SIM_KEY + "_0_.log"), log);
		Files.writeString(userDir.resolve("SimID_" + MovingBoundaryVtuWriterTest.SIM_KEY + "_0_.functions"), "##\n"); // no functions

		String previousRoot = System.getProperty(PropertyLoader.primarySimDataDirInternalProperty);
		String previousPython = System.getProperty(PropertyLoader.vtkPythonDir);
		System.setProperty(PropertyLoader.primarySimDataDirInternalProperty, root.toString());
		System.clearProperty(PropertyLoader.vtkPythonDir);
		try {
			DataSetControllerImpl controller = new DataSetControllerImpl(
					new Cachetable(10 * Cachetable.minute, 100_000_000L), root.toFile(), null);
			DataServerImpl server = new DataServerImpl(controller, null);
			VCSimulationDataIdentifier vcdID = new VCSimulationDataIdentifier(
					new VCSimulationIdentifier(MovingBoundaryVtuWriterTest.SIM_KEY, owner), 0);
			Assertions.assertTrue(server.isMovingBoundary(owner, vcdID));

			OutputContext none = new OutputContext(new AnnotatedFunction[0]);
			VtuVarInfo var = null;
			for (VtuVarInfo v : server.getVtuVarInfos(owner, none, vcdID)) {
				if (v.variableDomain == cbit.vcell.math.VariableType.VariableDomain.VARIABLEDOMAIN_VOLUME) {
					var = v;
					break;
				}
			}
			Assertions.assertNotNull(var, "a volume variable");
			double[] dataSetTimes = server.getDataSetTimes(owner, vcdID);
			Assertions.assertEquals(times.size(), dataSetTimes.length);

			for (int t = 0; t < dataSetTimes.length; t++) {
				long start = System.nanoTime();
				VtuFileContainer meshes = server.getEmptyVtuMeshFiles(owner, vcdID, t);
				double[] values = server.getVtuMeshData(owner, none, vcdID, var, dataSetTimes[t]);
				long ms = (System.nanoTime() - start) / 1_000_000;
				Assertions.assertEquals(1, meshes.getVtuMeshes().size());
				File vtu = Files.createTempFile(root, "served", ".vtu").toFile();
				Files.write(vtu.toPath(), meshes.getVtuMeshes().get(0).vtuMeshContents);
				VtuTestReader grid = VtuTestReader.read(vtu);
				Assertions.assertTrue(grid.numberOfCells > 0);
				Assertions.assertEquals(grid.numberOfCells, values.length, "one value per served cell at t=" + dataSetTimes[t]);
				Assertions.assertTrue(ms < 5_000, "a saved time took " + ms + " ms");
			}
			// the server wrote its meshes where it always has, and handed nothing to Python
			try (Stream<Path> files = Files.walk(root)) {
				List<String> names = files.map(p -> p.getFileName().toString()).toList();
				Assertions.assertTrue(names.stream().anyMatch(n -> n.endsWith(".vtu") && n.startsWith("SimID_")), names.toString());
				Assertions.assertTrue(names.stream().anyMatch(n -> n.endsWith(".movingboundaryindex")), names.toString());
				Assertions.assertTrue(names.stream().noneMatch(n -> n.endsWith(".visMesh")), names.toString());
			}
		} finally {
			restore(PropertyLoader.primarySimDataDirInternalProperty, previousRoot);
			restore(PropertyLoader.vtkPythonDir, previousPython);
		}
	}

	private static void restore(String name, String value) {
		if (value == null) {
			System.clearProperty(name);
		} else {
			System.setProperty(name, value);
		}
	}
}
