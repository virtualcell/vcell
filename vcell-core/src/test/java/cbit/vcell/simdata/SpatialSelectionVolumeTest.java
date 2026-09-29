package cbit.vcell.simdata;

import cbit.image.VCImageUncompressed;
import cbit.vcell.geometry.CurveSelectionInfo;
import cbit.vcell.geometry.PolyLine;
import cbit.vcell.geometry.RegionImage;
import cbit.vcell.math.VariableType;
import cbit.vcell.solvers.CartesianMesh;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.vcell.util.Coordinate;
import org.vcell.util.Extent;
import org.vcell.util.ISize;
import org.vcell.util.Origin;

/**
 * The sample points of a volume line selection ({@link SpatialSelection.SSHelper#getSampleCoordinates()}),
 * which the browser field viewer's kymograph reports alongside the desktop's indices and arc lengths.
 */
@Tag("Fast")
public class SpatialSelectionVolumeTest {

	/** 5 × 5, one subvolume, extent 4: element (i, j) sits at (i, j), node-centred. */
	private static CartesianMesh mesh() throws Exception {
		Extent extent = new Extent(4, 4, 1);
		Origin origin = new Origin(0, 0, 0);
		VCImageUncompressed image = new VCImageUncompressed(null, new byte[25], extent, 5, 5, 1);
		RegionImage regionImage = new RegionImage(image, 2, extent, origin, 0.5);
		return CartesianMesh.createSimpleCartesianMesh(origin, extent, new ISize(5, 5, 1), regionImage);
	}

	private static SpatialSelection.SSHelper samples(CartesianMesh mesh, Coordinate... vertices) {
		return new SpatialSelectionVolume(new CurveSelectionInfo(new PolyLine(vertices)), VariableType.VOLUME, mesh)
				.getIndexSamples(0.0, 1.0);
	}

	/** The accumulated lengths are the distances between the sample points, which are one per index. */
	private static void assertLengthsFollowThePoints(SpatialSelection.SSHelper h) {
		Coordinate[] c = h.getSampleCoordinates();
		Assertions.assertEquals(h.getSampledIndexes().length, c.length);
		double accum = 0;
		for (int i = 0; i < c.length; i++) {
			if (i > 0) {
				accum += c[i - 1].distanceTo(c[i]);
			}
			Assertions.assertEquals(accum, h.getWorldCoordinateLengths()[i], 1e-12);
		}
	}

	@Test
	public void anAxisAlignedLineIsSampledAtTheVoxelCentres() throws Exception {
		CartesianMesh mesh = mesh();
		SpatialSelection.SSHelper h = samples(mesh, new Coordinate(0.2, 2.1, 0), new Coordinate(3.9, 2.2, 0));
		Assertions.assertArrayEquals(new int[] { 10, 11, 12, 13, 14 }, h.getSampledIndexes());
		Coordinate[] c = h.getSampleCoordinates();
		for (int i = 0; i < c.length; i++) {
			Assertions.assertEquals(i, c[i].getX(), 1e-12);
			Assertions.assertEquals(2, c[i].getY(), 1e-12);
		}
		assertLengthsFollowThePoints(h);
	}

	@Test
	public void anObliqueLineStartsAndEndsAtItsEndpoints() throws Exception {
		CartesianMesh mesh = mesh();
		Coordinate start = new Coordinate(0.1, 0.3, 0);
		Coordinate end = new Coordinate(3.8, 2.9, 0);
		SpatialSelection.SSHelper h = samples(mesh, start, end);
		Coordinate[] c = h.getSampleCoordinates();
		Assertions.assertTrue(c.length > 2);
		Assertions.assertEquals(0, start.distanceTo(c[0]), 1e-12);
		Assertions.assertEquals(0, end.distanceTo(c[c.length - 1]), 1e-12);
		assertLengthsFollowThePoints(h);
		Assertions.assertNull(h.getMembraneIndexesInOut(), "one subvolume: no membrane to cross");
	}
}
