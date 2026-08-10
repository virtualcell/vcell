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
}
