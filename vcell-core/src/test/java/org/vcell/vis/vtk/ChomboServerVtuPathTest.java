package org.vcell.vis.vtk;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.parallel.ResourceLock;
import org.vcell.vis.io.VtuFileContainer;
import org.vcell.vis.io.VtuVarInfo;

import cbit.vcell.math.VariableType.VariableDomain;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.simdata.Cachetable;
import cbit.vcell.simdata.DataServerImpl;
import cbit.vcell.simdata.DataSetControllerImpl;
import cbit.vcell.simdata.OutputContext;
import cbit.vcell.solver.AnnotatedFunction;
import cbit.vcell.solver.VCSimulationDataIdentifier;

/**
 * The Chombo VTU seam as the <b>data server</b> runs it: {@link DataServerImpl} over a {@link DataSetControllerImpl},
 * a server-run (non-local) {@link VCSimulationDataIdentifier}, the result under
 * {@code vcell.primarySimdatadir.internal/<owner>/}, and — as in the deployed {@code data} container —
 * {@code vcell.vtk.pythonDir} not set. Before the Java writers, {@code getEmptyVtuMeshFiles} failed there with
 * "required System property vcell.vtk.pythonDir not defined", so no stored Chombo result reached the field viewer.
 * <p>
 * The runs are {@link ChomboRunFixture}'s real 2D and 3D results, each with a membrane variable.
 */
@Tag("Fast")
@ResourceLock("vcellGlobalConfig")
public class ChomboServerVtuPathTest {

	@Test
	public void theServerServesChomboVolumeAndMembraneMeshesAndDataWithoutPython(@TempDir Path root) throws Exception {
		Path userDir = Files.createDirectories(root.resolve(ChomboRunFixture.OWNER.getName()));
		ChomboRunFixture.copy(ChomboRunFixture.SIM_2D, userDir);
		ChomboRunFixture.copy(ChomboRunFixture.SIM_3D, userDir);

		String previousRoot = System.getProperty(PropertyLoader.primarySimDataDirInternalProperty);
		String previousPython = System.getProperty(PropertyLoader.vtkPythonDir);
		System.setProperty(PropertyLoader.primarySimDataDirInternalProperty, root.toString());
		System.clearProperty(PropertyLoader.vtkPythonDir);
		try {
			DataSetControllerImpl controller = new DataSetControllerImpl(
					new Cachetable(10 * Cachetable.minute, 100_000_000L), root.toFile(), null);
			DataServerImpl server = new DataServerImpl(controller, null);
			OutputContext none = new OutputContext(new AnnotatedFunction[0]);
			for (String sim : new String[] { ChomboRunFixture.SIM_2D, ChomboRunFixture.SIM_3D }) {
				VCSimulationDataIdentifier vcdID = ChomboRunFixture.vcdID(sim);
				Assertions.assertTrue(server.isChombo(ChomboRunFixture.OWNER, vcdID), sim);

				VtuVarInfo volumeVar = null;
				VtuVarInfo membraneVar = null;
				for (VtuVarInfo v : server.getVtuVarInfos(ChomboRunFixture.OWNER, none, vcdID)) {
					if (v.name.equals(ChomboRunFixture.VOLUME_VAR) && v.variableDomain == VariableDomain.VARIABLEDOMAIN_VOLUME) {
						volumeVar = v;
					}
					if (v.name.equals(ChomboRunFixture.MEMBRANE_VAR) && v.variableDomain == VariableDomain.VARIABLEDOMAIN_MEMBRANE) {
						membraneVar = v;
					}
				}
				Assertions.assertNotNull(volumeVar, "the volume variable of " + sim);
				Assertions.assertNotNull(membraneVar, "the membrane variable of " + sim);
				Assertions.assertEquals(ChomboRunFixture.VOLUME, volumeVar.domainName);
				Assertions.assertEquals(ChomboRunFixture.MEMBRANE, membraneVar.domainName);

				VtuFileContainer meshes = server.getEmptyVtuMeshFiles(ChomboRunFixture.OWNER, vcdID, 0);
				Assertions.assertEquals(List.of(ChomboRunFixture.VOLUME, ChomboRunFixture.MEMBRANE),
						meshes.getVtuMeshes().stream().map(m -> m.domainName).toList(), "a volume and its membrane");
				double[] times = server.getDataSetTimes(ChomboRunFixture.OWNER, vcdID);
				Assertions.assertEquals(3, times.length);
				for (VtuFileContainer.VtuMesh mesh : meshes.getVtuMeshes()) {
					File vtu = Files.createTempFile(root, "served", ".vtu").toFile();
					Files.write(vtu.toPath(), mesh.vtuMeshContents);
					VtuTestReader grid = VtuTestReader.read(vtu);
					Assertions.assertTrue(grid.numberOfCells > 0);
					VtuVarInfo var = mesh.domainName.equals(ChomboRunFixture.MEMBRANE) ? membraneVar : volumeVar;
					for (double time : times) {
						double[] values = server.getVtuMeshData(ChomboRunFixture.OWNER, none, vcdID, var, time);
						Assertions.assertEquals(grid.numberOfCells, values.length,
								"one " + var.name + " value per served " + mesh.domainName + " cell at t=" + time);
					}
				}
			}
			// the server wrote its meshes where it always has, and handed nothing to Python
			try (Stream<Path> files = Files.walk(root)) {
				List<String> names = files.map(p -> p.getFileName().toString()).toList();
				for (String sim : new String[] { ChomboRunFixture.SIM_2D, ChomboRunFixture.SIM_3D }) {
					for (String domain : new String[] { ChomboRunFixture.VOLUME, ChomboRunFixture.MEMBRANE }) {
						String prefix = "SimID_" + sim + "_0_" + domain;
						Assertions.assertTrue(Files.isRegularFile(userDir.resolve(prefix + ".vtu")), prefix + ".vtu in " + names);
						Assertions.assertTrue(Files.isRegularFile(userDir.resolve(prefix + ".chomboindex")), prefix + ".chomboindex in " + names);
					}
				}
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
