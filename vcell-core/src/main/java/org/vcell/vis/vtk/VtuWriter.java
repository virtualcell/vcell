package org.vcell.vis.vtk;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.vcell.vis.vismesh.thrift.VisMesh;
import org.vcell.vis.vismesh.thrift.VisPoint;
import org.vcell.vis.vismesh.thrift.VisPolygon;
import org.vcell.vis.vismesh.thrift.VisTetrahedron;
import org.vcell.vis.vismesh.thrift.VisVoxel;

/**
 * Writes a {@link VisMesh} volume grid as a VTK XML unstructured grid ({@code .vtu}) in pure Java — no VTK
 * library, no Python.
 * <p>
 * The grid is the one the Python VTK service builds in {@code getVolumeVtkGrid()}
 * ({@code pythonVtk/python_vtk/vtkService/vtkService.py}): the mesh's points in order, then one cell per
 * polygon (a 4-gon as {@code VTK_QUAD}, a 3-gon as {@code VTK_TRIANGLE}, anything else as
 * {@code VTK_POLYGON}), then one {@code VTK_VOXEL} per voxel, then one {@code VTK_TETRA} per tetrahedron. Cell
 * order is what ties a cell to its solution value through the index data, so it must not change.
 * <p>
 * The file has the same restricted form that service writes with {@code SetDataModeToBinary()} and
 * {@code SetCompressorTypeToNone()}: one piece, {@code LittleEndian}, {@code header_type="UInt32"}, every data
 * array an inline base64 block prefixed by its byte count — which is what {@link VisMeshUtils} (adding cell
 * data) and the field viewer's {@code VtuGridParser} read. Points are written as {@code Float64} (VTK's default
 * {@code vtkPoints} would have rounded them to {@code Float32}); empty {@code PointData} and {@code CellData}
 * elements are present because {@link VisMeshUtils#writeCellDataToVtu} appends to them.
 * <p>
 * Irregular polyhedra (Chombo's cut cells) are not supported; neither is the finite-volume service's surface
 * smoothing. Those paths stay with the Python service.
 */
public final class VtuWriter {

	static final int VTK_TRIANGLE = 5;
	static final int VTK_POLYGON = 7;
	static final int VTK_QUAD = 9;
	static final int VTK_TETRA = 10;
	static final int VTK_VOXEL = 11;

	private VtuWriter() {
	}

	/** Writes the volume cells of {@code visMesh} (polygons, voxels, tetrahedra) to {@code vtuFile}. */
	public static void writeVolumeGrid(VisMesh visMesh, File vtuFile) throws IOException {
		if (visMesh.getIrregularPolyhedraSize() > 0) {
			throw new UnsupportedOperationException("VtuWriter does not write irregular polyhedra");
		}
		List<VisPoint> points = visMesh.getPoints() != null ? visMesh.getPoints() : List.of();
		List<List<Integer>> cells = new ArrayList<>();
		List<Integer> types = new ArrayList<>();
		if (visMesh.getPolygons() != null) {
			for (VisPolygon polygon : visMesh.getPolygons()) {
				List<Integer> p = polygon.getPointIndices();
				cells.add(p);
				types.add(p.size() == 4 ? VTK_QUAD : p.size() == 3 ? VTK_TRIANGLE : VTK_POLYGON);
			}
		}
		if (visMesh.getVisVoxels() != null) {
			for (VisVoxel voxel : visMesh.getVisVoxels()) {
				cells.add(voxel.getPointIndices());
				types.add(VTK_VOXEL);
			}
		}
		if (visMesh.getTetrahedra() != null) {
			for (VisTetrahedron tet : visMesh.getTetrahedra()) {
				cells.add(tet.getPointIndices());
				types.add(VTK_TETRA);
			}
		}

		double[] xyz = new double[3 * points.size()];
		for (int i = 0; i < points.size(); i++) {
			VisPoint p = points.get(i);
			xyz[3 * i] = p.x;
			xyz[3 * i + 1] = p.y;
			xyz[3 * i + 2] = p.z;
		}
		int connectivitySize = 0;
		for (List<Integer> cell : cells) {
			connectivitySize += cell.size();
		}
		long[] connectivity = new long[connectivitySize];
		long[] offsets = new long[cells.size()];
		byte[] cellTypes = new byte[cells.size()];
		int k = 0;
		for (int c = 0; c < cells.size(); c++) {
			for (int pointIndex : cells.get(c)) {
				if (pointIndex < 0 || pointIndex >= points.size()) {
					throw new IllegalArgumentException("cell " + c + " references point " + pointIndex + " of " + points.size());
				}
				connectivity[k++] = pointIndex;
			}
			offsets[c] = k; // VTK offsets are END offsets
			cellTypes[c] = (byte) (int) types.get(c);
		}

		Files.createDirectories(vtuFile.getAbsoluteFile().getParentFile().toPath());
		try (OutputStream out = Files.newOutputStream(vtuFile.toPath());
				Writer w = new OutputStreamWriter(out, StandardCharsets.UTF_8)) {
			w.write("<?xml version=\"1.0\"?>\n");
			w.write("<VTKFile type=\"UnstructuredGrid\" version=\"0.1\" byte_order=\"LittleEndian\" header_type=\"UInt32\">\n");
			w.write("  <UnstructuredGrid>\n");
			w.write("    <Piece NumberOfPoints=\"" + points.size() + "\" NumberOfCells=\"" + cells.size() + "\">\n");
			w.write("      <PointData>\n      </PointData>\n");
			w.write("      <CellData>\n      </CellData>\n");
			w.write("      <Points>\n");
			dataArray(w, "Float64", "Points", 3, float64(xyz));
			w.write("      </Points>\n");
			w.write("      <Cells>\n");
			dataArray(w, "Int64", "connectivity", 1, int64(connectivity));
			dataArray(w, "Int64", "offsets", 1, int64(offsets));
			dataArray(w, "UInt8", "types", 1, cellTypes);
			w.write("      </Cells>\n");
			w.write("    </Piece>\n");
			w.write("  </UnstructuredGrid>\n");
			w.write("</VTKFile>\n");
		}
	}

	private static void dataArray(Writer w, String type, String name, int components, byte[] payload) throws IOException {
		w.write("        <DataArray type=\"" + type + "\" Name=\"" + name + "\"");
		if (components != 1) {
			w.write(" NumberOfComponents=\"" + components + "\"");
		}
		w.write(" format=\"binary\">\n          ");
		w.write(binaryBlock(payload));
		w.write("\n        </DataArray>\n");
	}

	/** VTK's inline binary encoding: a UInt32 LittleEndian byte count, then the bytes, base64 as one block. */
	static String binaryBlock(byte[] payload) {
		ByteBuffer b = ByteBuffer.allocate(4 + payload.length).order(ByteOrder.LITTLE_ENDIAN);
		b.putInt(payload.length);
		b.put(payload);
		return Base64.getEncoder().encodeToString(b.array());
	}

	private static byte[] float64(double[] values) {
		ByteBuffer b = ByteBuffer.allocate(8 * values.length).order(ByteOrder.LITTLE_ENDIAN);
		for (double v : values) {
			b.putDouble(v);
		}
		return b.array();
	}

	private static byte[] int64(long[] values) {
		ByteBuffer b = ByteBuffer.allocate(8 * values.length).order(ByteOrder.LITTLE_ENDIAN);
		for (long v : values) {
			b.putLong(v);
		}
		return b.array();
	}
}
