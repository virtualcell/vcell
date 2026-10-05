package org.vcell.vis.vtk;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import org.junit.jupiter.api.Assertions;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.vis.chombo.ChomboDataset;
import org.vcell.vis.chombo.ChomboDataset.ChomboCombinedVolumeMembraneDomain;
import org.vcell.vis.io.ChomboFileReader;
import org.vcell.vis.io.ChomboFiles;
import org.vcell.vis.mapping.chombo.ChomboMeshMapping;
import org.vcell.vis.vismesh.thrift.VisMesh;

import cbit.vcell.simdata.Cachetable;
import cbit.vcell.simdata.DataSetControllerImpl;
import cbit.vcell.solver.VCSimulationDataIdentifier;
import cbit.vcell.solver.VCSimulationIdentifier;

/**
 * Two real Chombo results, run locally by today's {@code VCellChombo2D_x64}/{@code VCellChombo3D_x64}
 * (the vcell-chombo solver bundle) from {@code Solver_Suite_6_2.vcml} (test resource {@code models/}): the
 * "2D pde analytic" application's "chombo 2D" simulation (a disk of radius 3 at (5, 5) in a 10 × 10 µm box, 8 × 8
 * mesh) and the "3D pde analytic" application's "chombo 3d" (a ball of radius 4 at (5, 5, 5) in a 10 µm cube,
 * 8 × 8 × 8 mesh), each saved at t = 0, 0.5 and 1 ({@code .fvinput} is the solver input). Each has one volume
 * domain, {@link #VOLUME}, and its membrane, {@link #MEMBRANE}, with the membrane variable {@code s2}.
 * <p>
 * Two initial conditions were changed so that a value says where it was read: {@code s2} (rate 0, so constant)
 * and {@code RanC_nuc} at t = 0 are the {@link #code} of the point where the solver evaluated them — a membrane
 * element's centroid, a cut cell's centroid — scaled by {@link #RANC_NUC_SCALE} for {@code RanC_nuc}. Every point of
 * a cell lies in the same voxel of the 1.25 µm grid, so a drawn cell's centroid must give the same code as the
 * value paired with it.
 */
final class ChomboRunFixture {

	static final User OWNER = new User("schaff", new KeyValue("17"));
	static final String SIM_2D = "105373152";
	static final String SIM_3D = "104116603";
	static final String VOLUME = "subdomain1.vol0";
	static final String MEMBRANE = "subdomain1.vol0_Membrane";
	static final String MEMBRANE_VAR = "s2";
	static final String VOLUME_VAR = "RanC_nuc";
	static final double RANC_NUC_SCALE = 1e-8;
	/** the voxel size of both runs: 10 µm over 8 cells */
	static final double H = 1.25;
	static final double[] CENTRE = { 5, 5, 5 };
	static final double RADIUS_2D = 3;
	static final double RADIUS_3D = 4;

	private static final String[] SUFFIXES = { ".functions", ".fvinput", ".hdf5", ".log", ".mesh.hdf5", "00.hdf5.zip",
			"000000_subdomain1_vol0.hdf5", "000050_subdomain1_vol0.hdf5", "000100_subdomain1_vol0.hdf5", "_0.simtask.xml" };

	private ChomboRunFixture() {
	}

	/** the voxel of the 1.25 µm grid holding (x, y, z), as {@code floor(x/h) + 100 floor(y/h) + 10000 floor(z/h)} */
	static double code(double x, double y, double z) {
		return Math.floor(x / H) + 100 * Math.floor(y / H) + 10000 * Math.floor(z / H);
	}

	/** Copies the run {@code sim} into {@code dir}, as {@code SimID_<sim>_0_…}. */
	static void copy(String sim, Path dir) throws Exception {
		for (String suffix : SUFFIXES) {
			String name = "SimID_" + sim + "_0_" + suffix;
			try (InputStream in = ChomboRunFixture.class.getResourceAsStream("/org/vcell/vis/chombo/" + name)) {
				Assertions.assertNotNull(in, "test resource " + name + " missing");
				Files.copy(in, dir.resolve(name), StandardCopyOption.REPLACE_EXISTING);
			}
		}
	}

	static VCSimulationDataIdentifier vcdID(String sim) {
		return new VCSimulationDataIdentifier(new VCSimulationIdentifier(new KeyValue(sim), OWNER), 0);
	}

	/** The run's {@link ChomboFiles}, from a copy of the fixture under {@code root/<owner>/}. */
	static ChomboFiles chomboFiles(String sim, Path root) throws Exception {
		Path userDir = Files.createDirectories(root.resolve(OWNER.getName()));
		copy(sim, userDir);
		DataSetControllerImpl controller = new DataSetControllerImpl(new Cachetable(10 * Cachetable.minute, 100_000_000L),
				root.toFile(), null);
		return controller.getChomboFiles(vcdID(sim));
	}

	/** The volume domain's {@link VisMesh}, as {@code ChomboVtkFileWriter} builds it for the VTU seam. */
	static VisMesh visMesh(ChomboFiles chomboFiles) throws Exception {
		ChomboDataset dataset = ChomboFileReader.readDataset(chomboFiles, chomboFiles.getTimeIndices().get(0));
		Assertions.assertEquals(1, dataset.getCombinedVolumeMembraneDomains().size());
		ChomboCombinedVolumeMembraneDomain domain = dataset.getCombinedVolumeMembraneDomains().get(0);
		Assertions.assertEquals(VOLUME, domain.getVolumeDomainName());
		Assertions.assertEquals(MEMBRANE, domain.getMembraneDomainName());
		return new ChomboMeshMapping().fromMeshData(domain.getChomboMeshData(), domain);
	}

	/**
	 * A {@code ChomboMeshMapping} vertex in microns: the mapping writes {@code v = (p − origin)·N/extent·2 − 1}, so
	 * {@code p = origin + (v + 1)·extent/(2N)}; both runs have origin 0, extent 10 and N = 8 (z stays 0 in 2D).
	 */
	static double physical(double v, int axis, int dimension) {
		return axis < dimension ? (v + 1) * 10.0 / (2 * 8) : v;
	}
}
