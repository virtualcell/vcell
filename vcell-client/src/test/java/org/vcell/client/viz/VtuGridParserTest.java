package org.vcell.client.viz;

import java.io.InputStream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import org.vcell.vis.vtk.VtuGridParser.VtuGrid;
import org.vcell.vis.vtk.VtuGridParser;

/**
 * Parses a reference {@code .vtu} generated with the exact settings of the server's Python VTK
 * service ({@code writevtk()}: LittleEndian, binary data mode, compressor NONE, UInt32 headers):
 * two VTK_POLYGON cells (a 5-vertex cut cell and a quad) over 7 points, mimicking a
 * MovingBoundary mesh fragment. See {@code pythonVtk/python_vtk/vtkService/vtkService.py}.
 */
@Tag("Fast")
public class VtuGridParserTest {

	@Test
	public void parsesBinaryUncompressedPolygons() throws Exception {
		byte[] bytes;
		try (InputStream in = VtuGridParserTest.class.getResourceAsStream("binary-uncompressed.vtu")) {
			Assertions.assertNotNull(in, "test resource binary-uncompressed.vtu missing");
			bytes = in.readAllBytes();
		}
		VtuGrid grid = VtuGridParser.parse(bytes);

		Assertions.assertEquals(7, grid.numPoints());
		Assertions.assertEquals(2, grid.cells.length);

		// the 5-vertex cut cell, then the quad — both written as VTK_POLYGON (7)
		Assertions.assertArrayEquals(new int[] { 0, 1, 2, 3, 4 }, grid.cells[0]);
		Assertions.assertArrayEquals(new int[] { 1, 5, 6, 2 }, grid.cells[1]);
		Assertions.assertArrayEquals(new int[] { 7, 7 }, grid.cellTypes);

		// spot-check the one non-integral coordinate (point 2 = 1.6, 0.9, 0), Float32 precision
		Assertions.assertEquals(1.6, grid.points[6], 1e-6);
		Assertions.assertEquals(0.9, grid.points[7], 1e-6);
		Assertions.assertEquals(0.0, grid.points[8], 1e-6);
	}

	// cellMeasures / locateCell power the moving-boundary lab-frame time series and the
	// measure-weighted statistics; exercised on hand-built grids with known geometry

	@Test
	public void measuresTwoDimensionalCells() {
		// unit right triangle, unit square (as VTK_QUAD), and an L-shaped hexagon of area 3
		VtuGrid grid = new VtuGrid(
				new double[] {
						0, 0, 0,  1, 0, 0,  0, 1, 0, // triangle
						2, 0, 0,  3, 0, 0,  3, 1, 0,  2, 1, 0, // square
						4, 0, 0,  6, 0, 0,  6, 1, 0,  5, 1, 0,  5, 2, 0,  4, 2, 0, // L
				},
				new int[][] { { 0, 1, 2 }, { 3, 4, 5, 6 }, { 7, 8, 9, 10, 11, 12 } },
				new int[] { 5, 9, 7 });
		double[] measures = VtuGridParser.cellMeasures(grid);
		Assertions.assertEquals(0.5, measures[0], 1e-12);
		Assertions.assertEquals(1.0, measures[1], 1e-12);
		Assertions.assertEquals(3.0, measures[2], 1e-12);

		Assertions.assertEquals(0, VtuGridParser.locateCell(grid, 0.2, 0.2, 0));
		Assertions.assertEquals(1, VtuGridParser.locateCell(grid, 2.5, 0.5, 0));
		Assertions.assertEquals(2, VtuGridParser.locateCell(grid, 4.5, 1.5, 0));
		// inside the L's bounding box but outside the L itself
		Assertions.assertEquals(-1, VtuGridParser.locateCell(grid, 5.9, 1.5, 0));
		Assertions.assertEquals(-1, VtuGridParser.locateCell(grid, 1.5, 0.5, 0));
	}

	@Test
	public void measuresThreeDimensionalCells() {
		// unit cube in VTK_VOXEL point order, and the corner tetrahedron of volume 1/6
		VtuGrid grid = new VtuGrid(
				new double[] {
						0, 0, 0,  1, 0, 0,  0, 1, 0,  1, 1, 0,
						0, 0, 1,  1, 0, 1,  0, 1, 1,  1, 1, 1, // voxel
						2, 0, 0,  3, 0, 0,  2, 1, 0,  2, 0, 1, // tetra
				},
				new int[][] { { 0, 1, 2, 3, 4, 5, 6, 7 }, { 8, 9, 10, 11 } },
				new int[] { 11, 10 });
		double[] measures = VtuGridParser.cellMeasures(grid);
		Assertions.assertEquals(1.0, measures[0], 1e-12);
		Assertions.assertEquals(1.0 / 6, measures[1], 1e-12);

		Assertions.assertEquals(0, VtuGridParser.locateCell(grid, 0.5, 0.5, 0.5));
		Assertions.assertEquals(1, VtuGridParser.locateCell(grid, 2.1, 0.1, 0.1));
		// inside the tetra's bounding box but beyond its slanted face
		Assertions.assertEquals(-1, VtuGridParser.locateCell(grid, 2.9, 0.9, 0.9));
		Assertions.assertEquals(-1, VtuGridParser.locateCell(grid, 0.5, 0.5, 1.5));
	}

	/**
	 * Chombo writes its cut cells as VTK_POLYHEDRON so that a face shared with a neighbour stays
	 * shared (#1895). The fixture is a voxel with a polyhedral cell stacked on it, written by the
	 * Python service itself — VTK 9.4 and later put the faces in the
	 * {@code polyhedron_offsets}/{@code polyhedron_to_faces}/{@code face_offsets}/
	 * {@code face_connectivity} arrays rather than the older {@code faces}/{@code faceoffsets}.
	 */
	@Test
	public void parsesPolyhedralCells() throws Exception {
		byte[] bytes;
		try (InputStream in = VtuGridParserTest.class.getResourceAsStream("polyhedron-cells.vtu")) {
			Assertions.assertNotNull(in, "test resource polyhedron-cells.vtu missing");
			bytes = in.readAllBytes();
		}
		VtuGrid grid = VtuGridParser.parse(bytes);

		Assertions.assertEquals(12, grid.numPoints());
		Assertions.assertArrayEquals(new int[] { 11, VtuGridParser.VTK_POLYHEDRON }, grid.cellTypes);
		Assertions.assertNull(grid.facesOf(0), "a voxel carries no faces");

		int[][] faces = grid.facesOf(1);
		Assertions.assertEquals(6, faces.length);
		Assertions.assertArrayEquals(new int[] { 7, 5, 4, 6 }, faces[4], "the face shared with the voxel");

		double[] measures = VtuGridParser.cellMeasures(grid);
		Assertions.assertEquals(1.0, measures[0], 1e-12);
		Assertions.assertEquals(1.0, measures[1], 1e-12, "the polyhedron is the unit cube above it");

		Assertions.assertEquals(0, VtuGridParser.locateCell(grid, 0.5, 0.5, 0.5));
		Assertions.assertEquals(1, VtuGridParser.locateCell(grid, 0.5, 0.5, 1.5));
		Assertions.assertEquals(-1, VtuGridParser.locateCell(grid, 0.5, 0.5, 2.5));
	}

	// FEniCSx results bundles (vcell-fenics ADR 010) carry their meshes in the same restricted VTU form

	private static VtuGrid parseResource(String name) throws Exception {
		try (InputStream in = VtuGridParserTest.class.getResourceAsStream(name)) {
			Assertions.assertNotNull(in, "test resource " + name + " missing");
			return VtuGridParser.parse(in.readAllBytes());
		}
	}

	private static double sum(double[] a) {
		double s = 0;
		for (double v : a) s += v;
		return s;
	}

	@Test
	public void parsesFenicsMeshesAndMeasuresTheirDomains() throws Exception {
		// a 2D disk (triangles, z = 0): the summed cell areas must equal the domain measure the solver
		// integrated, total / mean from the bundle's statistics
		VtuGrid disk = parseResource("fenics-2d-triangles.vtu");
		Assertions.assertEquals(403, disk.numPoints());
		Assertions.assertEquals(711, disk.cells.length);
		double area = 0.7830285791419164 / 0.9993355966701419;
		Assertions.assertEquals(area, sum(VtuGridParser.cellMeasures(disk)), 1e-12 * area);

		// a 3D box minus a ball (tetrahedra)
		VtuGrid shell = parseResource("fenics-3d-tetra.vtu");
		Assertions.assertEquals(56, shell.numPoints());
		Assertions.assertEquals(172, shell.cells.length);
		double volume = 0.1701327273303082 / 0.022234804552240577;
		Assertions.assertEquals(volume, sum(VtuGridParser.cellMeasures(shell)), 1e-12 * volume);
	}

	@Test
	public void measuresAndLocatesLineCells() {
		// a 2D membrane is a polyline: a 3-4-5 segment and a unit segment
		VtuGrid curve = new VtuGrid(
				new double[] { 0, 0, 0,  3, 4, 0,  3, 5, 0 },
				new int[][] { { 0, 1 }, { 1, 2 } },
				new int[] { VtuGridParser.VTK_LINE, VtuGridParser.VTK_LINE });
		double[] measures = VtuGridParser.cellMeasures(curve);
		Assertions.assertEquals(5.0, measures[0], 1e-12);
		Assertions.assertEquals(1.0, measures[1], 1e-12);
		Assertions.assertEquals(0, VtuGridParser.locateCell(curve, 1.5, 2.0, 0));
		Assertions.assertEquals(1, VtuGridParser.locateCell(curve, 3.0, 4.5, 0));
		Assertions.assertEquals(-1, VtuGridParser.locateCell(curve, 1.5, 2.1, 0), "off the curve");
		Assertions.assertEquals(-1, VtuGridParser.locateCell(curve, 6.0, 8.0, 0), "beyond the segment's end");
	}

	@Test
	public void measuresAndLocatesSurfaceTrianglesIn3D() {
		// a 3D membrane is a triangulated surface: one triangle in the x = 1 plane (area 2) and one
		// tilted through the unit axis points (area sqrt(3)/2)
		VtuGrid surface = new VtuGrid(
				new double[] { 1, 0, 0,  1, 2, 0,  1, 0, 2,   1, 0, 0,  0, 1, 0,  0, 0, 1 },
				new int[][] { { 0, 1, 2 }, { 3, 4, 5 } },
				new int[] { 5, 5 });
		double[] measures = VtuGridParser.cellMeasures(surface);
		Assertions.assertEquals(2.0, measures[0], 1e-12);
		Assertions.assertEquals(Math.sqrt(3) / 2, measures[1], 1e-12);
		Assertions.assertEquals(0, VtuGridParser.locateCell(surface, 1, 0.5, 0.5));
		Assertions.assertEquals(-1, VtuGridParser.locateCell(surface, 1.1, 0.5, 0.5), "off the plane of the triangle");
		Assertions.assertEquals(1, VtuGridParser.locateCell(surface, 1.0 / 3, 1.0 / 3, 1.0 / 3));
		Assertions.assertEquals(-1, VtuGridParser.locateCell(surface, 0.2, 0.2, 0.2), "inside the tetrahedron, not on its face");
	}

	@Test
	public void rejectsCompressedVtu() {
		byte[] compressed = ("<?xml version=\"1.0\"?><VTKFile type=\"UnstructuredGrid\" version=\"0.1\" "
				+ "byte_order=\"LittleEndian\" header_type=\"UInt32\" compressor=\"vtkZLibDataCompressor\">"
				+ "<UnstructuredGrid><Piece NumberOfPoints=\"0\" NumberOfCells=\"0\"/></UnstructuredGrid></VTKFile>")
				.getBytes(java.nio.charset.StandardCharsets.UTF_8);
		IllegalArgumentException e = Assertions.assertThrows(IllegalArgumentException.class, () -> VtuGridParser.parse(compressed));
		Assertions.assertTrue(e.getMessage().contains("compressed"), e.getMessage());
	}

	@Test
	public void interpolatesPointDataWithVertexWeights() {
		VtuGrid grid = new VtuGrid(
				new double[] { 0, 0, 0,  2, 0, 0,  0, 2, 0,  0, 0, 2 },
				new int[][] { { 0, 1, 2 }, { 0, 1, 2, 3 }, { 1, 3 } },
				new int[] { 5, 10, VtuGridParser.VTK_LINE });
		// triangle: barycentric; at (0.5, 0.5) the weights are 1/2, 1/4, 1/4
		Assertions.assertArrayEquals(new double[] { 0.5, 0.25, 0.25 }, VtuGridParser.vertexWeights(grid, 0, 0.5, 0.5, 0), 1e-12);
		// tetrahedron: barycentric, and it reproduces a vertex exactly
		Assertions.assertArrayEquals(new double[] { 0, 0, 0, 1 }, VtuGridParser.vertexWeights(grid, 1, 0, 0, 2), 1e-12);
		Assertions.assertArrayEquals(new double[] { 0.25, 0.25, 0.25, 0.25 }, VtuGridParser.vertexWeights(grid, 1, 0.5, 0.5, 0.5), 1e-12);
		// line: linear along the segment
		Assertions.assertArrayEquals(new double[] { 0.75, 0.25 }, VtuGridParser.vertexWeights(grid, 2, 1.5, 0, 0.5), 1e-12);
	}

	// the CellLocator (docs/plan-plotting.md P5): the same answers as the linear scan, found faster

	/** Random points over the grid's padded box, plus every vertex, cell centroid and edge midpoint (on shared faces). */
	private static double[][] probePoints(VtuGrid grid, long seed, int random) {
		double[] lo = { Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE };
		double[] hi = { -Double.MAX_VALUE, -Double.MAX_VALUE, -Double.MAX_VALUE };
		for (int i = 0; i < grid.points.length; i++) {
			lo[i % 3] = Math.min(lo[i % 3], grid.points[i]);
			hi[i % 3] = Math.max(hi[i % 3], grid.points[i]);
		}
		java.util.List<double[]> out = new java.util.ArrayList<>();
		java.util.Random rnd = new java.util.Random(seed);
		for (int k = 0; k < random; k++) {
			double[] q = new double[3];
			for (int a = 0; a < 3; a++) {
				double pad = 0.1 * (hi[a] - lo[a]);
				q[a] = lo[a] - pad + rnd.nextDouble() * (hi[a] - lo[a] + 2 * pad);
			}
			out.add(q);
		}
		for (int i = 0; i < grid.numPoints(); i++) {
			out.add(new double[] { grid.points[3 * i], grid.points[3 * i + 1], grid.points[3 * i + 2] });
		}
		for (int[] cell : grid.cells) {
			double[] c = new double[3];
			for (int v : cell) {
				for (int a = 0; a < 3; a++) {
					c[a] += grid.points[3 * v + a] / cell.length;
				}
			}
			out.add(c);
			for (int v = 0; v < cell.length; v++) {
				int w = cell[(v + 1) % cell.length];
				out.add(new double[] { (grid.points[3 * cell[v]] + grid.points[3 * w]) / 2,
						(grid.points[3 * cell[v] + 1] + grid.points[3 * w + 1]) / 2,
						(grid.points[3 * cell[v] + 2] + grid.points[3 * w + 2]) / 2 });
			}
		}
		return out.toArray(new double[0][]);
	}

	private static void assertLocatorAgrees(String what, VtuGrid grid) {
		assertLocatorAgrees(what, grid, 100);
	}

	private static void assertLocatorAgrees(String what, VtuGrid grid, int minHits) {
		int hits = 0;
		for (double[] q : probePoints(grid, 20260929L, 4000)) {
			int expected = VtuGridParser.locateCell(grid, q[0], q[1], q[2]);
			Assertions.assertEquals(expected, VtuGridParser.locate(grid, q[0], q[1], q[2]),
					what + " at " + java.util.Arrays.toString(q));
			hits += expected >= 0 ? 1 : 0;
		}
		Assertions.assertTrue(hits >= minHits, what + ": the probe points reach into the mesh (" + hits + ")");
	}

	@Test
	public void theLocatorAgreesWithTheLinearScan() throws Exception {
		assertLocatorAgrees("3D tetrahedra", parseResource("fenics-3d-tetra.vtu"));
		assertLocatorAgrees("2D triangles", parseResource("fenics-2d-triangles.vtu"));
		assertLocatorAgrees("a voxel and a polyhedron", parseResource("polyhedron-cells.vtu"));
		// Chombo-like: quads and cut pentagons in 2D, voxels and polyhedra in 3D (in Chombo's index frame)
		assertLocatorAgrees("Chombo 2D", VtuGridParser.parse(chomboVtu(FakeChomboRun.SIM_2D)));
		assertLocatorAgrees("Chombo 3D", VtuGridParser.parse(chomboVtu(FakeChomboRun.SIM_3D)));
		// MovingBoundary-like polygons, at two of the times
		assertLocatorAgrees("MovingBoundary t=0", VtuGridParser.parse(FakeMovingBoundaryRun.vtu(0)));
		assertLocatorAgrees("MovingBoundary t=1", VtuGridParser.parse(FakeMovingBoundaryRun.vtu(1)));
		// a 2D membrane: line cells, which test z themselves
		assertLocatorAgrees("line cells", new VtuGrid(new double[] { 0, 0, 0, 3, 4, 0, 3, 5, 0, 1, 7, 0 },
				new int[][] { { 0, 1 }, { 1, 2 }, { 2, 3 } }, new int[] { 3, 3, 3 }), 10); // random points miss a curve
	}

	private static byte[] chomboVtu(String sim) throws Exception {
		java.lang.reflect.Constructor<FakeChomboRun> make = FakeChomboRun.class.getDeclaredConstructor(int.class);
		make.setAccessible(true);
		return make.newInstance(sim.equals(FakeChomboRun.SIM_2D) ? 2 : 3).vtu();
	}

	@Test
	public void aHorizontalTriangleOfA3DSurfaceHoldsOnlyPointsInItsPlane() {
		// a surface mesh with depth: a triangle in z = 1 and one in the x = 0 plane
		VtuGrid surface = new VtuGrid(new double[] { 0, 0, 1, 1, 0, 1, 0, 1, 1, 0, 0, 0, 0, 1, 0, 0, 0, 1 },
				new int[][] { { 0, 1, 2 }, { 3, 4, 5 } }, new int[] { 5, 5 });
		Assertions.assertEquals(0, VtuGridParser.locateCell(surface, 0.2, 0.2, 1));
		Assertions.assertEquals(-1, VtuGridParser.locateCell(surface, 0.2, 0.2, 0.5), "below the triangle, not on it");
		Assertions.assertEquals(0, VtuGridParser.locate(surface, 0.2, 0.2, 1));
		Assertions.assertEquals(-1, VtuGridParser.locate(surface, 0.2, 0.2, 0.5));
		// in a flat (2D) mesh z is ignored, as before
		VtuGrid flat = new VtuGrid(new double[] { 0, 0, 0, 1, 0, 0, 0, 1, 0 }, new int[][] { { 0, 1, 2 } }, new int[] { 5 });
		Assertions.assertEquals(0, VtuGridParser.locateCell(flat, 0.2, 0.2, 7));
		Assertions.assertEquals(0, VtuGridParser.locate(flat, 0.2, 0.2, 7));
	}

	/**
	 * A 40 × 40 × 40 lattice of cubes, each split into six tetrahedra (384,000 cells): the locator and the linear
	 * scan agree, and the timings of both are printed (the scan on a sample of the points, as it is slow).
	 */
	@Test
	public void theLocatorIsFastOnALargeMesh() {
		int m = 40;
		double[] points = new double[3 * (m + 1) * (m + 1) * (m + 1)];
		for (int k = 0, i = 0; k <= m; k++) {
			for (int j = 0; j <= m; j++) {
				for (int l = 0; l <= m; l++, i++) {
					points[3 * i] = l;
					points[3 * i + 1] = j;
					points[3 * i + 2] = k;
				}
			}
		}
		int[][] tets = { { 0, 1, 3, 7 }, { 0, 1, 5, 7 }, { 0, 2, 3, 7 }, { 0, 2, 6, 7 }, { 0, 4, 5, 7 }, { 0, 4, 6, 7 } };
		int[][] cells = new int[6 * m * m * m][];
		for (int k = 0, c = 0; k < m; k++) {
			for (int j = 0; j < m; j++) {
				for (int l = 0; l < m; l++) {
					int[] corner = new int[8];
					for (int b = 0; b < 8; b++) {
						corner[b] = ((k + (b >> 2 & 1)) * (m + 1) + j + (b >> 1 & 1)) * (m + 1) + l + (b & 1);
					}
					for (int[] t : tets) {
						cells[c++] = new int[] { corner[t[0]], corner[t[1]], corner[t[2]], corner[t[3]] };
					}
				}
			}
		}
		int[] types = new int[cells.length];
		java.util.Arrays.fill(types, 10);
		VtuGrid grid = new VtuGrid(points, cells, types);
		java.util.Random rnd = new java.util.Random(7);
		double[][] q = new double[20000][];
		for (int i = 0; i < q.length; i++) {
			q[i] = new double[] { rnd.nextDouble() * m, rnd.nextDouble() * m, rnd.nextDouble() * m };
		}
		long t0 = System.nanoTime();
		grid.locator();
		long built = System.nanoTime();
		int[] found = new int[q.length];
		for (int i = 0; i < q.length; i++) {
			found[i] = VtuGridParser.locate(grid, q[i][0], q[i][1], q[i][2]);
		}
		long located = System.nanoTime();
		int scanned = 200;
		for (int i = 0; i < scanned; i++) {
			Assertions.assertEquals(VtuGridParser.locateCell(grid, q[i][0], q[i][1], q[i][2]), found[i]);
			Assertions.assertTrue(found[i] >= 0);
		}
		long scanEnd = System.nanoTime();
		double perLocate = (located - built) / 1e3 / q.length;
		double perScan = (scanEnd - located) / 1e3 / scanned;
		System.out.printf(java.util.Locale.ROOT, "CellLocator on %d tetrahedra: built in %.1f ms; %.2f µs per point"
				+ " (%d points) vs the linear scan's %.0f µs per point (%d points): %.0f× faster%n",
				cells.length, (built - t0) / 1e6, perLocate, q.length, perScan, scanned, perScan / perLocate);
	}
}
