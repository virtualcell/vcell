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
import java.util.TreeSet;

import org.vcell.vis.vismesh.thrift.PolyhedronFace;
import org.vcell.vis.vismesh.thrift.VisIrregularPolyhedron;
import org.vcell.vis.vismesh.thrift.VisLine;
import org.vcell.vis.vismesh.thrift.VisMesh;
import org.vcell.vis.vismesh.thrift.VisPoint;
import org.vcell.vis.vismesh.thrift.VisPolygon;
import org.vcell.vis.vismesh.thrift.VisSurfaceTriangle;
import org.vcell.vis.vismesh.thrift.VisTetrahedron;
import org.vcell.vis.vismesh.thrift.VisVoxel;

/**
 * Writes a {@link VisMesh} as a VTK XML unstructured grid ({@code .vtu}) in pure Java — no VTK library, no
 * Python.
 * <p>
 * The volume grid ({@link #writeVolumeGrid}) is the one the Python VTK service builds in
 * {@code getVolumeVtkGrid()} ({@code pythonVtk/python_vtk/vtkService/vtkService.py}): the mesh's points in order,
 * then one cell per polygon (a 4-gon as {@code VTK_QUAD}, a 3-gon as {@code VTK_TRIANGLE}, anything else as
 * {@code VTK_POLYGON}), then one {@code VTK_VOXEL} per voxel, then one {@code VTK_TETRA} per tetrahedron, then one
 * {@code VTK_POLYHEDRON} per irregular polyhedron (Chombo's cut cells, which keep the faces they share with their
 * neighbours). The membrane grid ({@link #writeSurfaceGrid}) is the one that service builds in
 * {@code getMembraneVtkGrid()}: the mesh's surface points, then one {@code VTK_LINE} per line in 2D or one
 * {@code VTK_TRIANGLE} per surface triangle in 3D. Cell order is what ties a cell to its solution value through
 * the index data, so it must not change.
 * <p>
 * The file has the same restricted form that service writes with {@code SetDataModeToBinary()} and
 * {@code SetCompressorTypeToNone()}: one piece, {@code LittleEndian}, {@code header_type="UInt32"}, every data
 * array an inline base64 block prefixed by its byte count — which is what {@link VisMeshUtils} (adding cell
 * data) and the field viewer's {@code VtuGridParser} read. Points are written as {@code Float64} (VTK's default
 * {@code vtkPoints} would have rounded them to {@code Float32}); empty {@code PointData} and {@code CellData}
 * elements are present because {@link VisMeshUtils#writeCellDataToVtu} appends to them.
 * <p>
 * A polyhedron is written as VTK 9.4 writes it (file version 2.3, the version the service's VTK writes): its
 * connectivity is its distinct point ids in ascending order, and its faces are two chained cell arrays —
 * {@code polyhedron_to_faces}/{@code polyhedron_offsets} (each cell's face ids; an empty range for every other
 * cell) and {@code face_connectivity}/{@code face_offsets} (each face's point ids, as the mesh lists them). The
 * finite-volume service's surface smoothing is not supported; that path stays with the Python service.
 */
public final class VtuWriter {

	static final int VTK_LINE = 3;
	static final int VTK_TRIANGLE = 5;
	static final int VTK_POLYGON = 7;
	static final int VTK_QUAD = 9;
	static final int VTK_TETRA = 10;
	static final int VTK_VOXEL = 11;
	static final int VTK_POLYHEDRON = 42;

	private VtuWriter() {
	}

	/** Writes the volume cells of {@code visMesh} (polygons, voxels, tetrahedra, irregular polyhedra) to {@code vtuFile}. */
	public static void writeVolumeGrid(VisMesh visMesh, File vtuFile) throws IOException {
		List<VisPoint> points = visMesh.getPoints() != null ? visMesh.getPoints() : List.of();
		List<List<Integer>> cells = new ArrayList<>();
		List<Integer> types = new ArrayList<>();
		List<List<List<Integer>>> faces = new ArrayList<>(); // per cell: a polyhedron's faces, else null
		if (visMesh.getPolygons() != null) {
			for (VisPolygon polygon : visMesh.getPolygons()) {
				List<Integer> p = polygon.getPointIndices();
				cells.add(p);
				types.add(p.size() == 4 ? VTK_QUAD : p.size() == 3 ? VTK_TRIANGLE : VTK_POLYGON);
				faces.add(null);
			}
		}
		if (visMesh.getVisVoxels() != null) {
			for (VisVoxel voxel : visMesh.getVisVoxels()) {
				cells.add(voxel.getPointIndices());
				types.add(VTK_VOXEL);
				faces.add(null);
			}
		}
		if (visMesh.getTetrahedra() != null) {
			for (VisTetrahedron tet : visMesh.getTetrahedra()) {
				cells.add(tet.getPointIndices());
				types.add(VTK_TETRA);
				faces.add(null);
			}
		}
		if (visMesh.getIrregularPolyhedra() != null) {
			for (VisIrregularPolyhedron polyhedron : visMesh.getIrregularPolyhedra()) {
				List<List<Integer>> polyhedronFaces = new ArrayList<>();
				TreeSet<Integer> pointIds = new TreeSet<>();
				for (PolyhedronFace face : polyhedron.getPolyhedronFaces()) {
					polyhedronFaces.add(face.getVertices());
					pointIds.addAll(face.getVertices());
				}
				cells.add(new ArrayList<>(pointIds)); // VTK lists a polyhedron's points once each, ascending
				types.add(VTK_POLYHEDRON);
				faces.add(polyhedronFaces);
			}
		}
		write(points, cells, types, faces, vtuFile);
	}

	/**
	 * Writes the membrane of {@code visMesh} to {@code vtuFile}: its surface points, and one line per
	 * {@link VisLine} in 2D or one triangle per {@link VisSurfaceTriangle} in 3D.
	 */
	public static void writeSurfaceGrid(VisMesh visMesh, File vtuFile) throws IOException {
		List<VisPoint> points = visMesh.getSurfacePoints() != null ? visMesh.getSurfacePoints() : List.of();
		List<List<Integer>> cells = new ArrayList<>();
		List<Integer> types = new ArrayList<>();
		List<List<List<Integer>>> faces = new ArrayList<>();
		if (visMesh.getDimension() == 2) {
			if (visMesh.getVisLines() != null) {
				for (VisLine line : visMesh.getVisLines()) {
					cells.add(List.of(line.getP1(), line.getP2()));
					types.add(VTK_LINE);
					faces.add(null);
				}
			}
		} else if (visMesh.getSurfaceTriangles() != null) {
			for (VisSurfaceTriangle triangle : visMesh.getSurfaceTriangles()) {
				cells.add(triangle.getPointIndices());
				types.add(VTK_TRIANGLE);
				faces.add(null);
			}
		}
		write(points, cells, types, faces, vtuFile);
	}

	private static void write(List<VisPoint> points, List<List<Integer>> cells, List<Integer> types,
			List<List<List<Integer>>> faces, File vtuFile) throws IOException {
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
				checkPoint(c, pointIndex, points.size());
				connectivity[k++] = pointIndex;
			}
			offsets[c] = k; // VTK offsets are END offsets
			cellTypes[c] = (byte) (int) types.get(c);
		}

		// a polyhedron's faces: face ids per cell, and point ids per face, each as END offsets
		boolean polyhedra = faces.stream().anyMatch(f -> f != null);
		List<Long> faceConnectivity = new ArrayList<>();
		List<Long> faceOffsets = new ArrayList<>();
		long[] polyhedronOffsets = new long[cells.size()];
		if (polyhedra) {
			for (int c = 0; c < cells.size(); c++) {
				if (faces.get(c) != null) {
					for (List<Integer> face : faces.get(c)) {
						for (int pointIndex : face) {
							checkPoint(c, pointIndex, points.size());
							faceConnectivity.add((long) pointIndex);
						}
						faceOffsets.add((long) faceConnectivity.size());
					}
				}
				polyhedronOffsets[c] = faceOffsets.size();
			}
		}

		Files.createDirectories(vtuFile.getAbsoluteFile().getParentFile().toPath());
		try (OutputStream out = Files.newOutputStream(vtuFile.toPath());
				Writer w = new OutputStreamWriter(out, StandardCharsets.UTF_8)) {
			w.write("<?xml version=\"1.0\"?>\n");
			w.write("<VTKFile type=\"UnstructuredGrid\" version=\"" + (polyhedra ? "2.3" : "0.1")
					+ "\" byte_order=\"LittleEndian\" header_type=\"UInt32\">\n");
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
			if (polyhedra) {
				long[] polyhedronToFaces = new long[faceOffsets.size()];
				for (int f = 0; f < polyhedronToFaces.length; f++) {
					polyhedronToFaces[f] = f; // each polyhedron owns its faces; none is shared in the file
				}
				dataArray(w, "Int64", "face_connectivity", 1, int64(toArray(faceConnectivity)));
				dataArray(w, "Int64", "face_offsets", 1, int64(toArray(faceOffsets)));
				dataArray(w, "Int64", "polyhedron_to_faces", 1, int64(polyhedronToFaces));
				dataArray(w, "Int64", "polyhedron_offsets", 1, int64(polyhedronOffsets));
			}
			w.write("      </Cells>\n");
			w.write("    </Piece>\n");
			w.write("  </UnstructuredGrid>\n");
			w.write("</VTKFile>\n");
		}
	}

	private static void checkPoint(int cell, int pointIndex, int numPoints) {
		if (pointIndex < 0 || pointIndex >= numPoints) {
			throw new IllegalArgumentException("cell " + cell + " references point " + pointIndex + " of " + numPoints);
		}
	}

	private static long[] toArray(List<Long> values) {
		long[] a = new long[values.size()];
		for (int i = 0; i < a.length; i++) {
			a[i] = values.get(i);
		}
		return a;
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
