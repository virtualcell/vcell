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

		VtuGrid(double[] points, int[][] cells, int[] cellTypes) {
			this.points = points;
			this.cells = cells;
			this.cellTypes = cellTypes;
		}

		int numPoints() {
			return points.length / 3;
		}
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
		NodeList arrays = cellsEl.getElementsByTagName("DataArray");
		for (int i = 0; i < arrays.getLength(); i++) {
			Element da = (Element) arrays.item(i);
			double[] values = readDataArray(da, headerBytes);
			switch (da.getAttribute("Name")) {
				case "connectivity": connectivity = values; break;
				case "offsets": offsets = values; break;
				case "types": types = values; break;
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
		return new VtuGrid(points, cells, cellTypes);
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
