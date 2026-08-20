package cbit.vcell.export.server;

import cbit.image.VCImageUncompressed;
import cbit.vcell.geometry.RegionImage;
import cbit.vcell.math.VariableType;
import cbit.vcell.solvers.CartesianMesh;
import cbit.vcell.solvers.CartesianMeshTestSupport;
import io.jhdf.HdfFile;
import io.jhdf.WritableHdfFile;
import io.jhdf.api.Dataset;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.vcell.util.Extent;
import org.vcell.util.Origin;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * Characterises what the ASCII/HDF5 export actually writes for spatial data, one timepoint at a time.
 * <p>
 * The export had no tests at all, and its HDF5 writing is being ported off the native hdf.hdf5lib binding to jhdf
 * (jhdf#654). These assertions are the reference the port has to reproduce: the dataset's name, its shape, and
 * which value ends up at which index after the slice extraction reorders the raw simulation data.
 */
@Tag("Fast")
public class ASCIIExporterHdf5SliceTest {

	private static final int SIZE_X = 4;
	private static final int SIZE_Y = 3;
	private static final int SIZE_Z = 2;
	private static final int TIME_COUNT = 3;

	/**
	 * Two subvolumes split along x, so the mesh has real membrane elements between them. A uniform image has none,
	 * and membrane data would then be zero length -- which the type inference still calls MEMBRANE, so the test
	 * would pass while measuring nothing.
	 */
	private static CartesianMesh mesh() throws Exception {
		Extent extent = new Extent(1, 1, 1);
		Origin origin = new Origin(0, 0, 0);
		byte[] pixels = new byte[SIZE_X * SIZE_Y * SIZE_Z];
		for (int z = 0; z < SIZE_Z; z++) {
			for (int y = 0; y < SIZE_Y; y++) {
				for (int x = 0; x < SIZE_X; x++) {
					pixels[z * SIZE_X * SIZE_Y + y * SIZE_X + x] = (byte) (x < SIZE_X / 2 ? 0 : 1);
				}
			}
		}
		VCImageUncompressed image = new VCImageUncompressed(null, pixels, extent, SIZE_X, SIZE_Y, SIZE_Z);
		RegionImage regionImage = new RegionImage(image, 3, extent, origin, 0.5);
		return CartesianMesh.createSimpleCartesianMesh(origin, extent,
			new org.vcell.util.ISize(SIZE_X, SIZE_Y, SIZE_Z), regionImage);
	}

	/** Drives the export's slice writing for every timepoint and returns the file it wrote. */
	private static Path writeTimepoints(CartesianMesh mesh, int sliceNumber, DataForTime dataForTime)
		throws Exception {
		Path file = Files.createTempFile("asciiExport", ".hdf5");
		Files.deleteIfExists(file);

		FileDataContainerManager containers = new FileDataContainerManager();
		try (WritableHdfFile writableHdfFile = HdfFile.write(file)) {
			ASCIIExporter exporter = new ASCIIExporter(null);
			ASCIIExporter.SliceHelper sliceHelper =
				exporter.new SliceHelper(TIME_COUNT, true, "XY", sliceNumber, false, mesh);
			sliceHelper.setHDF5GroupVar(writableHdfFile.putGroup("SimID_1"), "Ran_cyt");
			for (int timeIndex = 0; timeIndex < TIME_COUNT; timeIndex++) {
				sliceHelper.populate(containers, containers.getNewFileDataContainerID(), dataForTime.at(timeIndex));
			}
			sliceHelper.closeHDF5GroupAndValues();
		}
		return file;
	}

	@FunctionalInterface
	private interface DataForTime {
		double[] at(int timeIndex);
	}

	/** Volume data for one timepoint, valued so the index it came from is recoverable. */
	private static double[] volumeData(int timeIndex) {
		double[] data = new double[SIZE_X * SIZE_Y * SIZE_Z];
		for (int i = 0; i < data.length; i++) {
			data[i] = timeIndex * 1000 + i;
		}
		return data;
	}

	@Test
	public void wholeVolumePerTimepoint() throws Exception {
		CartesianMesh mesh = mesh();
		Path file = writeTimepoints(mesh, -1, ASCIIExporterHdf5SliceTest::volumeData);

		try (HdfFile hdfFile = new HdfFile(file)) {
			Dataset dataset = hdfFile.getDatasetByPath("SimID_1/Ran_cyt/DataValues (XYZT)");
			// {outer, inner, slices, time} for an xy slice plane over the whole volume
			assertArrayEquals(new int[]{SIZE_Y, SIZE_X, SIZE_Z, TIME_COUNT}, dataset.getDimensions());

			double[][][][] values = (double[][][][]) dataset.getData();
			for (int timeIndex = 0; timeIndex < TIME_COUNT; timeIndex++) {
				for (int z = 0; z < SIZE_Z; z++) {
					for (int outer = 0; outer < SIZE_Y; outer++) {
						for (int inner = 0; inner < SIZE_X; inner++) {
							double expected = timeIndex * 1000 + z * SIZE_X * SIZE_Y + outer * SIZE_X + inner;
							assertEquals(expected, values[outer][inner][z][timeIndex],
								"value at outer=" + outer + " inner=" + inner + " z=" + z + " t=" + timeIndex);
						}
					}
				}
			}
		}
		Files.deleteIfExists(file);
	}

	/**
	 * One slice of the volume rather than all of it. The dataset drops the Z dimension and its name loses the Z,
	 * so a single-slice export is a different shape and a different name from a whole-volume one.
	 */
	@Test
	public void singleSlicePerTimepoint() throws Exception {
		final int sliceNumber = 1;
		CartesianMesh mesh = mesh();
		Path file = writeTimepoints(mesh, sliceNumber, ASCIIExporterHdf5SliceTest::volumeData);

		try (HdfFile hdfFile = new HdfFile(file)) {
			Dataset dataset = hdfFile.getDatasetByPath("SimID_1/Ran_cyt/DataValues (XYT)");
			assertArrayEquals(new int[]{SIZE_Y, SIZE_X, TIME_COUNT}, dataset.getDimensions());

			double[][][] values = (double[][][]) dataset.getData();
			for (int timeIndex = 0; timeIndex < TIME_COUNT; timeIndex++) {
				for (int outer = 0; outer < SIZE_Y; outer++) {
					for (int inner = 0; inner < SIZE_X; inner++) {
						double expected =
							timeIndex * 1000 + sliceNumber * SIZE_X * SIZE_Y + outer * SIZE_X + inner;
						assertEquals(expected, values[outer][inner][timeIndex],
							"value at outer=" + outer + " inner=" + inner + " t=" + timeIndex);
					}
				}
			}
		}
		Files.deleteIfExists(file);
	}

	/**
	 * Membrane data is not reordered at all: it is written as it arrives, one column per timepoint. The variable
	 * type is inferred from the data's length, so this depends on the mesh actually having membrane elements.
	 */
	@Test
	public void membranePerTimepoint() throws Exception {
		final int membraneLength = 7;
		CartesianMesh mesh = CartesianMeshTestSupport.withMembraneElementCount(mesh(), membraneLength);
		assertEquals(membraneLength, mesh.getDataLength(VariableType.MEMBRANE),
			"the test mesh has no membrane elements, so this would measure nothing");
		assertNotEquals(mesh.getDataLength(VariableType.VOLUME), membraneLength,
			"volume is checked first, so equal lengths would classify membrane data as volume");

		Path file = writeTimepoints(mesh, -1, timeIndex -> {
			double[] data = new double[membraneLength];
			for (int i = 0; i < data.length; i++) {
				data[i] = timeIndex * 1000 + i;
			}
			return data;
		});

		try (HdfFile hdfFile = new HdfFile(file)) {
			Dataset dataset = hdfFile.getDatasetByPath("SimID_1/Ran_cyt/DataValues (MT)");
			assertArrayEquals(new int[]{membraneLength, TIME_COUNT}, dataset.getDimensions());

			double[][] values = (double[][]) dataset.getData();
			for (int timeIndex = 0; timeIndex < TIME_COUNT; timeIndex++) {
				for (int i = 0; i < membraneLength; i++) {
					assertEquals(timeIndex * 1000 + i, values[i][timeIndex],
						"value at element=" + i + " t=" + timeIndex);
				}
			}
		}
		Files.deleteIfExists(file);
	}
}
