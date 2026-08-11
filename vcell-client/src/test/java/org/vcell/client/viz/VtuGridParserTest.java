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
}
