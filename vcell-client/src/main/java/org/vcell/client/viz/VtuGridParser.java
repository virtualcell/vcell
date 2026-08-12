package org.vcell.client.viz;

import java.io.ByteArrayInputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Base64;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Parses a VTK XML unstructured-grid file ({@code .vtu}) into flat arrays, for re-serving as the
 * field viewer's JSON grid contract.
 * <p>
 * This is deliberately NOT a general VTU reader. It exists for exactly one producer: the server's
 * Python VTK service ({@code pythonVtk/.../vtkService.py, writevtk()}), which writes
 * single-piece, LittleEndian, <b>binary-uncompressed</b> ({@code SetCompressorTypeToNone()} +
 * {@code SetDataModeToBinary()}) files — inline base64 blocks, each prefixed by a byte-count
 * header whose width is the {@code VTKFile header_type} (UInt32 here, UInt64 tolerated). ASCII
 * data arrays are also accepted for robustness. Anything else (appended data, compression,
 * multiple pieces) is rejected loudly rather than half-read.
 */
final class VtuGridParser {

	/** One parsed unstructured grid: points as x,y,z triples, cells as point-index lists. */
	static final class VtuGrid {
		final double[] points; // 3 per point
		final int[][] cells;
		final int[] cellTypes; // VTK cell type per cell
		/**
		 * Faces of each {@link #VTK_POLYHEDRON} cell — Chombo writes its cut cells that way, since a
		 * polyhedron keeps the faces it shares with its neighbours instead of re-triangulating them.
		 * {@code null} when the grid has no polyhedra, and {@code null} at every non-polyhedral cell.
		 */
		final int[][][] cellFaces;

		VtuGrid(double[] points, int[][] cells, int[] cellTypes) {
			this(points, cells, cellTypes, null);
		}

		VtuGrid(double[] points, int[][] cells, int[] cellTypes, int[][][] cellFaces) {
			this.points = points;
			this.cells = cells;
			this.cellTypes = cellTypes;
			this.cellFaces = cellFaces;
		}

		int[][] facesOf(int cell) {
			return cellFaces == null ? null : cellFaces[cell];
		}

		int numPoints() {
			return points.length / 3;
		}
	}

	private static final int VTK_TRIANGLE = 5;
	private static final int VTK_POLYGON = 7;
	private static final int VTK_QUAD = 9;
	private static final int VTK_TETRA = 10;
	private static final int VTK_VOXEL = 11;
	static final int VTK_POLYHEDRON = 42;

	/**
	 * Per-cell measure — area for in-plane 2D cells, volume for 3D cells — for volume-weighted
	 * statistics over body-fitted meshes.
	 */
	static double[] cellMeasures(VtuGrid grid) {
		double[] measures = new double[grid.cells.length];
		for (int c = 0; c < grid.cells.length; c++) {
			int[] cell = grid.cells[c];
			double[] p = grid.points;
			switch (grid.cellTypes[c]) {
				case VTK_TRIANGLE, VTK_POLYGON, VTK_QUAD -> {
					// shoelace over the in-plane polygon (z ignored)
					double twiceArea = 0;
					for (int v = 0; v < cell.length; v++) {
						int a = cell[v];
						int b = cell[(v + 1) % cell.length];
						twiceArea += p[3 * a] * p[3 * b + 1] - p[3 * b] * p[3 * a + 1];
					}
					measures[c] = Math.abs(twiceArea) / 2;
				}
				case VTK_TETRA -> measures[c] = Math.abs(tripleProduct(p, cell[0], cell[1], cell[2], cell[3])) / 6;
				case VTK_POLYHEDRON -> measures[c] = polyhedronVolume(p, grid.facesOf(c), cell);
				case VTK_VOXEL -> {
					// axis-aligned by definition; corners 0 and 7 span the box
					double dx = Math.abs(p[3 * cell[7]] - p[3 * cell[0]]);
					double dy = Math.abs(p[3 * cell[7] + 1] - p[3 * cell[0] + 1]);
					double dz = Math.abs(p[3 * cell[7] + 2] - p[3 * cell[0] + 2]);
					measures[c] = dx * dy * dz;
				}
				default -> throw new IllegalArgumentException(
						"no measure for VTK cell type " + grid.cellTypes[c]);
			}
		}
		return measures;
	}

	/**
	 * Volume of a closed polyhedron, as the sum of the tetrahedra spanned by its faces and its own
	 * vertex centroid. Absolute values are summed so the faces' winding does not have to be
	 * consistent, which holds for the convex cells Chombo cuts.
	 */
	private static double polyhedronVolume(double[] p, int[][] faces, int[] cell) {
		if (faces == null) {
			throw new IllegalArgumentException("polyhedron cell without faces");
		}
		double[] apex = centroid(p, cell);
		double volume = 0;
		for (int[] face : faces) {
			for (int v = 1; v + 1 < face.length; v++) {
				volume += Math.abs(tetVolume(p, apex, face[0], face[v], face[v + 1]));
			}
		}
		return volume;
	}

	private static double[] centroid(double[] p, int[] cell) {
		double[] c = new double[3];
		for (int i : cell) {
			c[0] += p[3 * i];
			c[1] += p[3 * i + 1];
			c[2] += p[3 * i + 2];
		}
		c[0] /= cell.length;
		c[1] /= cell.length;
		c[2] /= cell.length;
		return c;
	}

	/** signed volume of the tetrahedron (apex, i0, i1, i2) */
	private static double tetVolume(double[] p, double[] apex, int i0, int i1, int i2) {
		double ax = p[3 * i0] - apex[0], ay = p[3 * i0 + 1] - apex[1], az = p[3 * i0 + 2] - apex[2];
		double bx = p[3 * i1] - apex[0], by = p[3 * i1 + 1] - apex[1], bz = p[3 * i1 + 2] - apex[2];
		double cx = p[3 * i2] - apex[0], cy = p[3 * i2 + 1] - apex[1], cz = p[3 * i2 + 2] - apex[2];
		return (ax * (by * cz - bz * cy) - ay * (bx * cz - bz * cx) + az * (bx * cy - by * cx)) / 6;
	}

	private static double tripleProduct(double[] p, int i0, int i1, int i2, int i3) {
		double ax = p[3 * i1] - p[3 * i0], ay = p[3 * i1 + 1] - p[3 * i0 + 1], az = p[3 * i1 + 2] - p[3 * i0 + 2];
		double bx = p[3 * i2] - p[3 * i0], by = p[3 * i2 + 1] - p[3 * i0 + 1], bz = p[3 * i2 + 2] - p[3 * i0 + 2];
		double cx = p[3 * i3] - p[3 * i0], cy = p[3 * i3 + 1] - p[3 * i0 + 1], cz = p[3 * i3 + 2] - p[3 * i0 + 2];
		return ax * (by * cz - bz * cy) - ay * (bx * cz - bz * cx) + az * (bx * cy - by * cx);
	}

	/**
	 * The cell containing a lab-frame point, or -1 when the point lies outside the mesh — which
	 * for a moving-boundary run is the physically meaningful "the domain has moved past this
	 * point". In-plane cells test by point-in-polygon (z ignored); voxels by bounds; tets by
	 * barycentric signs.
	 */
	static int locateCell(VtuGrid grid, double x, double y, double z) {
		double[] p = grid.points;
		for (int c = 0; c < grid.cells.length; c++) {
			int[] cell = grid.cells[c];
			boolean hit = switch (grid.cellTypes[c]) {
				case VTK_TRIANGLE, VTK_POLYGON, VTK_QUAD -> pointInPolygon(p, cell, x, y);
				case VTK_VOXEL -> x >= Math.min(p[3 * cell[0]], p[3 * cell[7]])
						&& x <= Math.max(p[3 * cell[0]], p[3 * cell[7]])
						&& y >= Math.min(p[3 * cell[0] + 1], p[3 * cell[7] + 1])
						&& y <= Math.max(p[3 * cell[0] + 1], p[3 * cell[7] + 1])
						&& z >= Math.min(p[3 * cell[0] + 2], p[3 * cell[7] + 2])
						&& z <= Math.max(p[3 * cell[0] + 2], p[3 * cell[7] + 2]);
				case VTK_TETRA -> pointInTetra(p, cell, x, y, z);
				case VTK_POLYHEDRON -> pointInPolyhedron(p, grid.facesOf(c), cell, x, y, z);
				default -> false;
			};
			if (hit) {
				return c;
			}
		}
		return -1;
	}

	private static boolean pointInPolygon(double[] p, int[] cell, double x, double y) {
		boolean inside = false;
		for (int v = 0, w = cell.length - 1; v < cell.length; w = v++) {
			double xv = p[3 * cell[v]], yv = p[3 * cell[v] + 1];
			double xw = p[3 * cell[w]], yw = p[3 * cell[w] + 1];
			if ((yv > y) != (yw > y) && x < (xw - xv) * (y - yv) / (yw - yv) + xv) {
				inside = !inside;
			}
		}
		return inside;
	}

	/** inside the polyhedron == inside one of the tetrahedra it is fanned into from its centroid */
	private static boolean pointInPolyhedron(double[] p, int[][] faces, int[] cell,
			double x, double y, double z) {
		if (faces == null) {
			return false;
		}
		double[] apex = centroid(p, cell);
		for (int[] face : faces) {
			for (int v = 1; v + 1 < face.length; v++) {
				if (pointInFanTet(p, apex, face[0], face[v], face[v + 1], x, y, z)) {
					return true;
				}
			}
		}
		return false;
	}

	private static boolean pointInFanTet(double[] p, double[] apex, int i0, int i1, int i2,
			double x, double y, double z) {
		double total = tetVolume(p, apex, i0, i1, i2);
		if (Math.abs(total) < 1e-300) {
			return false;
		}
		double[] q = { x, y, z };
		// the four sub-tetrahedra formed by replacing each corner with the query point must all
		// keep the sign of the whole
		double[] corners = { p[3 * i0], p[3 * i0 + 1], p[3 * i0 + 2],
				p[3 * i1], p[3 * i1 + 1], p[3 * i1 + 2],
				p[3 * i2], p[3 * i2 + 1], p[3 * i2 + 2] };
		double[][] tet = { apex, { corners[0], corners[1], corners[2] },
				{ corners[3], corners[4], corners[5] }, { corners[6], corners[7], corners[8] } };
		for (int r = 0; r < 4; r++) {
			double[] saved = tet[r];
			tet[r] = q;
			double sub = orient(tet[0], tet[1], tet[2], tet[3]);
			tet[r] = saved;
			if (sub * total < -1e-12 * Math.abs(total)) {
				return false;
			}
		}
		return true;
	}

	private static double orient(double[] o, double[] a, double[] b, double[] c) {
		double ax = a[0] - o[0], ay = a[1] - o[1], az = a[2] - o[2];
		double bx = b[0] - o[0], by = b[1] - o[1], bz = b[2] - o[2];
		double cx = c[0] - o[0], cy = c[1] - o[1], cz = c[2] - o[2];
		return (ax * (by * cz - bz * cy) - ay * (bx * cz - bz * cx) + az * (bx * cy - by * cx)) / 6;
	}

	private static boolean pointInTetra(double[] p, int[] cell, double x, double y, double z) {
		// same-side test against each face, tolerant of degenerate (near-flat) tets
		double total = tripleProduct(p, cell[0], cell[1], cell[2], cell[3]);
		if (Math.abs(total) < 1e-300) {
			return false;
		}
		for (int f = 0; f < 4; f++) {
			double[] q = p.clone();
			int replaced = cell[f];
			q[3 * replaced] = x;
			q[3 * replaced + 1] = y;
			q[3 * replaced + 2] = z;
			double sub = tripleProduct(q, cell[0], cell[1], cell[2], cell[3]);
			if (sub * total < -1e-12 * Math.abs(total)) {
				return false;
			}
		}
		return true;
	}

	private VtuGridParser() {
	}

	static VtuGrid parse(byte[] vtuFileBytes) throws Exception {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(false);
		dbf.setExpandEntityReferences(false);
		dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
		Document doc = dbf.newDocumentBuilder().parse(new ByteArrayInputStream(vtuFileBytes));

		Element vtkFile = doc.getDocumentElement();
		if (!"VTKFile".equals(vtkFile.getTagName())
				|| !"UnstructuredGrid".equals(vtkFile.getAttribute("type"))) {
			throw new IllegalArgumentException("not a VTKFile/UnstructuredGrid document");
		}
		String byteOrder = attrOr(vtkFile, "byte_order", "LittleEndian");
		if (!"LittleEndian".equals(byteOrder)) {
			throw new IllegalArgumentException("unsupported byte order " + byteOrder);
		}
		int headerBytes = "UInt64".equals(attrOr(vtkFile, "header_type", "UInt32")) ? 8 : 4;

		NodeList pieces = doc.getElementsByTagName("Piece");
		if (pieces.getLength() != 1) {
			throw new IllegalArgumentException("expected exactly one Piece, found " + pieces.getLength());
		}
		Element piece = (Element) pieces.item(0);

		Element pointsEl = onlyChild(piece, "Points");
		double[] points = readDataArray(firstDataArray(pointsEl), headerBytes);

		Element cellsEl = onlyChild(piece, "Cells");
		double[] connectivity = null;
		double[] offsets = null;
		double[] types = null;
		double[] faceStream = null;
		double[] faceOffsets = null;
		double[] polyhedronOffsets = null;
		double[] polyhedronToFaces = null;
		double[] newFaceOffsets = null;
		double[] faceConnectivity = null;
		NodeList arrays = cellsEl.getElementsByTagName("DataArray");
		for (int i = 0; i < arrays.getLength(); i++) {
			Element da = (Element) arrays.item(i);
			double[] values = readDataArray(da, headerBytes);
			switch (da.getAttribute("Name")) {
				case "connectivity": connectivity = values; break;
				case "offsets": offsets = values; break;
				case "types": types = values; break;
				case "faces": faceStream = values; break;
				case "faceoffsets": faceOffsets = values; break;
				case "polyhedron_offsets": polyhedronOffsets = values; break;
				case "polyhedron_to_faces": polyhedronToFaces = values; break;
				case "face_offsets": newFaceOffsets = values; break;
				case "face_connectivity": faceConnectivity = values; break;
				default: // ignore
			}
		}
		if (connectivity == null || offsets == null || types == null) {
			throw new IllegalArgumentException("Cells is missing connectivity, offsets or types");
		}

		int[][] cells = new int[offsets.length][];
		int[] cellTypes = new int[offsets.length];
		int start = 0;
		for (int c = 0; c < offsets.length; c++) {
			int end = (int) offsets[c];
			int[] cell = new int[end - start];
			for (int v = 0; v < cell.length; v++) {
				cell[v] = (int) connectivity[start + v];
			}
			cells[c] = cell;
			cellTypes[c] = (int) types[c];
			start = end;
		}
		int[][][] cellFaces = polyhedronToFaces != null
				? readFacesCellArray(polyhedronOffsets, polyhedronToFaces, newFaceOffsets, faceConnectivity, cellTypes)
				: readFacesStream(faceStream, faceOffsets, cellTypes);
		return new VtuGrid(points, cells, cellTypes, cellFaces);
	}

	/**
	 * VTK 9.4 and later store polyhedron faces as two chained cell arrays: cell {@code c} owns the
	 * face ids {@code polyhedron_to_faces[polyhedron_offsets[c-1] … polyhedron_offsets[c])} — an
	 * empty range where the cell is not a polyhedron — and face {@code f} owns the point ids
	 * {@code face_connectivity[face_offsets[f-1] … face_offsets[f])}. As elsewhere in the VTU, an
	 * offsets array holds one END offset per entry, with the first range starting at zero.
	 */
	private static int[][][] readFacesCellArray(double[] polyhedronOffsets, double[] polyhedronToFaces,
			double[] faceOffsets, double[] faceConnectivity, int[] cellTypes) {
		if (polyhedronOffsets == null || faceOffsets == null || faceConnectivity == null) {
			throw new IllegalArgumentException(
					"polyhedron_to_faces without polyhedron_offsets, face_offsets or face_connectivity");
		}
		int[][][] cellFaces = new int[cellTypes.length][][];
		int faceStart = 0;
		for (int c = 0; c < cellTypes.length && c < polyhedronOffsets.length; c++) {
			int faceEnd = (int) polyhedronOffsets[c];
			if (faceEnd > faceStart) {
				int[][] faces = new int[faceEnd - faceStart][];
				for (int f = faceStart; f < faceEnd; f++) {
					int faceId = (int) polyhedronToFaces[f];
					int pointStart = faceId == 0 ? 0 : (int) faceOffsets[faceId - 1];
					int pointEnd = (int) faceOffsets[faceId];
					int[] face = new int[pointEnd - pointStart];
					for (int v = 0; v < face.length; v++) {
						face[v] = (int) faceConnectivity[pointStart + v];
					}
					faces[f - faceStart] = face;
				}
				cellFaces[c] = faces;
			}
			faceStart = faceEnd;
		}
		return cellFaces;
	}

	/**
	 * The pre-9.4 layout: {@code faceoffsets[c]} is the end of cell {@code c}'s slice of
	 * {@code faces} (and -1 where the cell is not a polyhedron), and each slice reads
	 * {@code [numFaces, numPoints, ids…, numPoints, ids…]}.
	 */
	private static int[][][] readFacesStream(double[] faceStream, double[] faceOffsets, int[] cellTypes) {
		if (faceStream == null || faceOffsets == null) {
			for (int type : cellTypes) {
				if (type == VTK_POLYHEDRON) {
					throw new IllegalArgumentException("polyhedral cells without faces/faceoffsets");
				}
			}
			return null;
		}
		int[][][] cellFaces = new int[cellTypes.length][][];
		int start = 0;
		for (int c = 0; c < cellTypes.length && c < faceOffsets.length; c++) {
			int end = (int) faceOffsets[c];
			if (end < 0) { // not a polyhedron; the offset array marks it with -1
				continue;
			}
			int cursor = start;
			int numFaces = (int) faceStream[cursor++];
			int[][] faces = new int[numFaces][];
			for (int f = 0; f < numFaces; f++) {
				int numPoints = (int) faceStream[cursor++];
				int[] face = new int[numPoints];
				for (int v = 0; v < numPoints; v++) {
					face[v] = (int) faceStream[cursor++];
				}
				faces[f] = face;
			}
			if (cursor != end) {
				throw new IllegalArgumentException(
						"face stream for cell " + c + " ended at " + cursor + ", expected " + end);
			}
			cellFaces[c] = faces;
			start = end;
		}
		return cellFaces;
	}

	private static String attrOr(Element el, String name, String fallback) {
		String v = el.getAttribute(name);
		return v == null || v.isEmpty() ? fallback : v;
	}

	private static Element onlyChild(Element parent, String tag) {
		NodeList list = parent.getElementsByTagName(tag);
		if (list.getLength() != 1) {
			throw new IllegalArgumentException("expected exactly one <" + tag + ">, found " + list.getLength());
		}
		return (Element) list.item(0);
	}

	private static Element firstDataArray(Element parent) {
		NodeList list = parent.getElementsByTagName("DataArray");
		if (list.getLength() < 1) {
			throw new IllegalArgumentException("no DataArray under <" + parent.getTagName() + ">");
		}
		return (Element) list.item(0);
	}

	/** Reads one DataArray's values as doubles, whatever its declared numeric type. */
	private static double[] readDataArray(Element dataArray, int headerBytes) {
		String format = attrOr(dataArray, "format", "ascii");
		String type = dataArray.getAttribute("type");
		String text = dataArray.getTextContent();
		if ("ascii".equals(format)) {
			String[] tokens = text.trim().split("\\s+");
			double[] values = new double[tokens.length];
			for (int i = 0; i < tokens.length; i++) {
				values[i] = Double.parseDouble(tokens[i]);
			}
			return values;
		}
		if (!"binary".equals(format)) {
			throw new IllegalArgumentException("unsupported DataArray format '" + format
					+ "' (appended/compressed VTU is not produced by the VCell Python VTK service)");
		}
		// InformationKey children contribute text too; the base64 block is the first token
		String base64 = text.trim().split("\\s+")[0];
		byte[] raw = Base64.getDecoder().decode(base64);
		ByteBuffer buf = ByteBuffer.wrap(raw).order(ByteOrder.LITTLE_ENDIAN);
		long byteCount = headerBytes == 8 ? buf.getLong() : Integer.toUnsignedLong(buf.getInt());
		int width = widthOf(type);
		int n = (int) (byteCount / width);
		double[] values = new double[n];
		for (int i = 0; i < n; i++) {
			values[i] = switch (type) {
				case "Float32" -> buf.getFloat();
				case "Float64" -> buf.getDouble();
				case "Int8" -> buf.get();
				case "UInt8" -> buf.get() & 0xFF;
				case "Int16" -> buf.getShort();
				case "UInt16" -> buf.getShort() & 0xFFFF;
				case "Int32" -> buf.getInt();
				case "UInt32" -> Integer.toUnsignedLong(buf.getInt());
				case "Int64", "UInt64" -> buf.getLong();
				default -> throw new IllegalArgumentException("unsupported DataArray type " + type);
			};
		}
		return values;
	}

	private static int widthOf(String type) {
		return switch (type) {
			case "Int8", "UInt8" -> 1;
			case "Int16", "UInt16" -> 2;
			case "Float32", "Int32", "UInt32" -> 4;
			case "Float64", "Int64", "UInt64" -> 8;
			default -> throw new IllegalArgumentException("unsupported DataArray type " + type);
		};
	}
}
