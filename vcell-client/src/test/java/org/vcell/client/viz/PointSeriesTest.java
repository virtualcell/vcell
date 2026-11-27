package org.vcell.client.viz;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import org.vcell.client.viz.VtuGridParser.VtuGrid;

/**
 * The shared per-point loop behind multi-point {@code /timeseries} for the body-fitted modes. Chombo and
 * MovingBoundary runs exist only on the server, so their paths are exercised here on hand-built grids: a
 * static mesh (Chombo) and one that moves between rows (MovingBoundary).
 */
@Tag("Fast")
public class PointSeriesTest {

	/** two unit squares side by side, [0,1]×[0,1] and [1,2]×[0,1], shifted by {@code dx} along x */
	private static VtuGrid twoSquares(double dx) {
		return new VtuGrid(new double[] {
				dx, 0, 0, 1 + dx, 0, 0, 1 + dx, 1, 0, dx, 1, 0,
				2 + dx, 0, 0, 2 + dx, 1, 0 },
				new int[][] { { 0, 1, 2, 3 }, { 1, 4, 5, 2 } }, new int[] { 9, 9 });
	}

	@Test
	public void aStaticMeshIsLocatedOnceAndEachRowReadOnce() throws Exception {
		VtuGrid grid = twoSquares(0);
		int[] reads = new int[3];
		PointSeries.Rows rows = new PointSeries.Rows() {
			@Override
			public VtuGrid grid(int row) {
				return grid;
			}

			@Override
			public double[] values(int row) {
				reads[row]++;
				return new double[] { 10 + row, 20 + row }; // one value per cell
			}
		};
		double[][] points = { { 0.5, 0.5, 0 }, { 1.5, 0.5, 0 }, { 5, 5, 0 } };
		PointSeries.Result r = PointSeries.sample(3, points, rows, PointSeries.Location.CELL, false);
		Assertions.assertTrue(r.singleMesh);
		Assertions.assertArrayEquals(new int[] { 0, 1, -1 }, r.cell);
		Assertions.assertArrayEquals(new double[] { 10, 11, 12 }, r.values[0]);
		Assertions.assertArrayEquals(new double[] { 20, 21, 22 }, r.values[1]);
		Assertions.assertTrue(Double.isNaN(r.values[2][0]), "outside the mesh: a gap");
		Assertions.assertArrayEquals(new int[] { 3, 3, 0 }, r.insideCount);
		Assertions.assertArrayEquals(new int[] { 1, 1, 1 }, reads, "one read per row, however many points");

		String json = PointSeries.json("u", "ec", new double[] { 0, 1, 2 }, PointSeries.Location.CELL, PointSeries.series(points, r));
		Assertions.assertEquals("{\"name\":\"u\",\"domain\":\"ec\",\"times\":[0.0,1.0,2.0],\"location\":\"cell\",\"series\":["
				+ "{\"point\":[0.5,0.5,0.0],\"cell\":0,\"inDomain\":true,\"values\":[10.0,11.0,12.0]},"
				+ "{\"point\":[1.5,0.5,0.0],\"cell\":1,\"inDomain\":true,\"values\":[20.0,21.0,22.0]},"
				+ "{\"point\":[5.0,5.0,0.0],\"cell\":-1,\"inDomain\":false,\"values\":[null,null,null]}]}", json);
	}

	@Test
	public void aMovingMeshIsLocatedPerRowWithGapsWhereTheBoundaryHasPassed() throws Exception {
		// the mesh slides right by 0.6 per row: a point at x = 0.3 is in cell 0, then outside
		VtuGrid[] grids = { twoSquares(0), twoSquares(0.6), twoSquares(1.2) };
		int[] reads = new int[3];
		PointSeries.Rows rows = new PointSeries.Rows() {
			@Override
			public VtuGrid grid(int row) {
				return grids[row];
			}

			@Override
			public double[] values(int row) {
				reads[row]++;
				return new double[] { 100 * row, 100 * row + 1 };
			}
		};
		double[][] points = { { 0.3, 0.5, 0 }, { 1.7, 0.5, 0 } };
		PointSeries.Result r = PointSeries.sample(3, points, rows, PointSeries.Location.CELL, false);
		Assertions.assertFalse(r.singleMesh, "a moving mesh has no single cell per point");
		Assertions.assertEquals(0, r.values[0][0]);
		Assertions.assertTrue(Double.isNaN(r.values[0][1]) && Double.isNaN(r.values[0][2]), "the boundary has moved past it");
		// x = 1.7: cell 1, then cell 1 (0.6..2.6 → [1.6,2.6]), then cell 0 ([1.2,2.2])
		Assertions.assertArrayEquals(new double[] { 1, 101, 200 }, r.values[1]);
		Assertions.assertArrayEquals(new int[] { 1, 3 }, r.insideCount);
		Assertions.assertArrayEquals(new int[] { 1, 1, 1 }, reads);
		Assertions.assertFalse(PointSeries.json("u", "d", new double[3], PointSeries.Location.CELL,
				PointSeries.series(points, r)).contains("\"cell\":"));
	}

	@Test
	public void noRowIsReadWhenNoPointIsInside() throws Exception {
		VtuGrid grid = twoSquares(0);
		PointSeries.Rows rows = new PointSeries.Rows() {
			@Override
			public VtuGrid grid(int row) {
				return grid;
			}

			@Override
			public double[] values(int row) {
				throw new AssertionError("read a row no point needs");
			}
		};
		PointSeries.Result r = PointSeries.sample(2, new double[][] { { -1, -1, 0 } }, rows, PointSeries.Location.CELL, false);
		Assertions.assertEquals(0, r.insideCount[0]);
	}

	@Test
	public void aMembraneProbeSnapsToTheNearestPointOnTheCurve() throws Exception {
		// a 2D membrane: two line cells along y = 1, from x = 0 to 2, point data 0, 1, 4 at x = 0, 1, 2
		VtuGrid line = new VtuGrid(new double[] { 0, 1, 0, 1, 1, 0, 2, 1, 0 },
				new int[][] { { 0, 1 }, { 1, 2 } }, new int[] { VtuGridParser.VTK_LINE, VtuGridParser.VTK_LINE });
		PointSeries.Rows rows = new PointSeries.Rows() {
			@Override
			public VtuGrid grid(int row) {
				return line;
			}

			@Override
			public double[] values(int row) {
				return new double[] { 0, 1, 4 };
			}
		};
		double[][] points = { { 1.5, 1.2, 0 }, { 1.5, 5, 0 } };
		PointSeries.Result plain = PointSeries.sample(1, points, rows, PointSeries.Location.POINT, false);
		Assertions.assertEquals(0, plain.insideCount[0], "a click beside the curve misses it without snap");

		PointSeries.Result snapped = PointSeries.sample(1, points, rows, PointSeries.Location.POINT, true);
		Assertions.assertEquals(2.5, snapped.values[0][0], 1e-12, "interpolated at (1.5, 1), the nearest point");
		Assertions.assertArrayEquals(new double[] { 1.5, 1, 0 }, snapped.snapped[0], 1e-12);
		Assertions.assertEquals(1, snapped.cell[0]);
		Assertions.assertTrue(Double.isNaN(snapped.values[1][0]), "farther than a cell diameter: no snap");
		Assertions.assertNull(snapped.snapped[1]);
	}

	@Test
	public void aSurfaceProbeSnapsOntoTheTriangle() throws Exception {
		// a membrane triangle in 3D, tilted out of every coordinate plane; point data 1, 2, 3
		VtuGrid tri = new VtuGrid(new double[] { 0, 0, 0, 1, 0, 1, 0, 1, 1 }, new int[][] { { 0, 1, 2 } }, new int[] { 5 });
		PointSeries.Rows rows = new PointSeries.Rows() {
			@Override
			public VtuGrid grid(int row) {
				return tri;
			}

			@Override
			public double[] values(int row) {
				return new double[] { 1, 2, 3 };
			}
		};
		// the centroid (1/3, 1/3, 2/3), pushed off the plane along its normal (-1, -1, 1)
		double e = 0.05;
		double[][] points = { { 1.0 / 3 - e, 1.0 / 3 - e, 2.0 / 3 + e } };
		PointSeries.Result r = PointSeries.sample(1, points, rows, PointSeries.Location.POINT, true);
		Assertions.assertArrayEquals(new double[] { 1.0 / 3, 1.0 / 3, 2.0 / 3 }, r.snapped[0], 1e-12);
		Assertions.assertEquals(2.0, r.values[0][0], 1e-12, "the centroid's value is the vertex mean");
	}

	@Test
	public void pointsAreParsedAndLimited() {
		double[][] p = PointSeries.parsePoints("1,2,3;4,5", 7.0);
		Assertions.assertArrayEquals(new double[] { 1, 2, 3 }, p[0]);
		Assertions.assertArrayEquals(new double[] { 4, 5, 7 }, p[1], "a 2D point takes the mesh plane's z");
		Assertions.assertEquals(1, PointSeries.parsePoints("1,2,3;", null).length, "a trailing ';' is fine");
		Assertions.assertThrows(IllegalArgumentException.class, () -> PointSeries.parsePoints("1,2", null), "no z in 3D");
		Assertions.assertThrows(IllegalArgumentException.class, () -> PointSeries.parsePoints("1,a,3", null));
		Assertions.assertThrows(IllegalArgumentException.class, () -> PointSeries.parsePoints("1,2,3,4", null));
		Assertions.assertThrows(IllegalArgumentException.class, () -> PointSeries.parsePoints("1,NaN,3", null));
		Assertions.assertThrows(IllegalArgumentException.class, () -> PointSeries.parsePoints(";", null));
		StringBuilder many = new StringBuilder();
		for (int i = 0; i < PointSeries.MAX_POINTS; i++) {
			many.append(i).append(",0,0;");
		}
		Assertions.assertEquals(PointSeries.MAX_POINTS, PointSeries.parsePoints(many.toString(), null).length);
		IllegalArgumentException tooMany = Assertions.assertThrows(IllegalArgumentException.class,
				() -> PointSeries.parsePoints(many + "0,0,0", null));
		Assertions.assertEquals("at most 64 points", tooMany.getMessage());
	}
}
