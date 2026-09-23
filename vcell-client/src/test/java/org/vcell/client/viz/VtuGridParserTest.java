package org.vcell.client.viz;

import java.io.InputStream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import org.vcell.client.viz.VtuGridParser.VtuGrid;

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
}
