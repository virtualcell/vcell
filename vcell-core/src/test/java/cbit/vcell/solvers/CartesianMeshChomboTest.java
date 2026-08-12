package cbit.vcell.solvers;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import org.vcell.util.Coordinate;

/**
 * Reads a real Chombo mesh file — the 8x8x8 embedded sphere of BioModel 276459600 "chombo 3d",
 * written by {@code VCellChombo3D} — through the pure-java {@code io.jhdf}. No native library is
 * loaded, which is the point: the legacy {@code ncsa.hdf} binding has no arm64 build, so this read
 * path used to be unavailable on an Apple-silicon machine.
 */
@Tag("Fast")
public class CartesianMeshChomboTest {

	private static File meshFile() throws Exception {
		try (InputStream in = CartesianMeshChomboTest.class.getResourceAsStream("chombo/chombo3d-sphere.mesh.hdf5")) {
			Assertions.assertNotNull(in, "test resource chombo3d-sphere.mesh.hdf5 missing");
			Path temp = Files.createTempFile("chombo3d-sphere", ".mesh.hdf5");
			Files.copy(in, temp, StandardCopyOption.REPLACE_EXISTING);
			temp.toFile().deleteOnExit();
			return temp.toFile();
		}
	}

	@Test
	public void readsMeshGeometryFromAttributes() throws Exception {
		CartesianMeshChombo mesh = CartesianMeshChombo.readMeshFile(meshFile());

		// matching the .fvinput this run came from: MESH_SIZE 8 8 8, DOMAIN_SIZE 10, ORIGIN 0.
		// Nx names its members i,j,k where extent and origin use x,y,z — both are compounds, and
		// the legacy reader saw them only as the string "{x,y,z}"
		Assertions.assertEquals(3, mesh.getDimension());
		Assertions.assertEquals(8, mesh.getSizeX());
		Assertions.assertEquals(8, mesh.getSizeY());
		Assertions.assertEquals(8, mesh.getSizeZ());
		Assertions.assertEquals(10.0, mesh.getExtent().getX(), 1e-12);
		Assertions.assertEquals(10.0, mesh.getExtent().getZ(), 1e-12);
		Assertions.assertEquals(0.0, mesh.getOrigin().getX(), 1e-12);
		Assertions.assertEquals(0.0, mesh.getOrigin().getZ(), 1e-12);
	}

	@Test
	public void readsCompoundDatasetsInFileOrder() throws Exception {
		CartesianMeshChombo mesh = CartesianMeshChombo.readMeshFile(meshFile());

		// one entry per subvolume, from the "featurephasevols" compound
		Assertions.assertEquals(2, mesh.getFeaturePhaseVols().length);

		// The columns of a compound dataset are read positionally, so they have to arrive in file
		// order: every membrane centroid must land on the sphere of radius 4 that this geometry
		// defines, which it cannot if x, y and z are transposed with the index columns.
		int membraneCount = 0;
		for (int i = 0; ; i++) {
			Coordinate centroid;
			try {
				centroid = mesh.getCoordinateFromMembraneIndex(i);
			} catch (RuntimeException stop) {
				break;
			}
			if (centroid == null) {
				break;
			}
			double radius = Math.sqrt(Math.pow(centroid.getX() - 5, 2) + Math.pow(centroid.getY() - 5, 2)
					+ Math.pow(centroid.getZ() - 5, 2));
			Assertions.assertEquals(4.0, radius, 0.2,
					"membrane element " + i + " should sit on the sphere at " + centroid);
			membraneCount++;
		}
		Assertions.assertEquals(224, membraneCount, "one membrane element per cut cell");
	}
}
