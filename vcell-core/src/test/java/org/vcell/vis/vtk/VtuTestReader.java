package org.vcell.vis.vtk;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Base64;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Reads back the restricted {@code .vtu} form both {@link VtuWriter} and the Python VTK service write: one
 * piece, inline binary (or ASCII) data arrays, a UInt32 or UInt64 byte-count header. Test-only: enough to
 * compare two grids, nothing more.
 */
final class VtuTestReader {

	final int numberOfPoints;
	final int numberOfCells;
	final String pointsType;
	final double[] points; // x,y,z per point
	final int[][] cells;
	final int[] types;

	private VtuTestReader(int numberOfPoints, int numberOfCells, String pointsType, double[] points, int[][] cells, int[] types) {
		this.numberOfPoints = numberOfPoints;
		this.numberOfCells = numberOfCells;
		this.pointsType = pointsType;
		this.points = points;
		this.cells = cells;
		this.types = types;
	}

	static VtuTestReader read(File vtu) throws Exception {
		Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(vtu);
		Element root = doc.getDocumentElement();
		if (!"UnstructuredGrid".equals(root.getAttribute("type"))) {
			throw new IllegalArgumentException("not an unstructured grid");
		}
		ByteOrder order = "BigEndian".equals(root.getAttribute("byte_order")) ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN;
		int headerBytes = "UInt64".equals(root.getAttribute("header_type")) ? 8 : 4;
		Element piece = (Element) root.getElementsByTagName("Piece").item(0);
		int np = Integer.parseInt(piece.getAttribute("NumberOfPoints"));
		int nc = Integer.parseInt(piece.getAttribute("NumberOfCells"));
		Element pointsArray = (Element) ((Element) piece.getElementsByTagName("Points").item(0)).getElementsByTagName("DataArray").item(0);
		double[] points = values(pointsArray, order, headerBytes);
		double[] connectivity = null, offsets = null, types = null;
		NodeList arrays = ((Element) piece.getElementsByTagName("Cells").item(0)).getElementsByTagName("DataArray");
		for (int i = 0; i < arrays.getLength(); i++) {
			Element a = (Element) arrays.item(i);
			switch (a.getAttribute("Name")) {
				case "connectivity" -> connectivity = values(a, order, headerBytes);
				case "offsets" -> offsets = values(a, order, headerBytes);
				case "types" -> types = values(a, order, headerBytes);
				default -> {
				}
			}
		}
		int[][] cells = new int[nc][];
		int[] cellTypes = new int[nc];
		int start = 0;
		for (int c = 0; c < nc; c++) {
			int end = (int) offsets[c];
			cells[c] = new int[end - start];
			for (int v = start; v < end; v++) {
				cells[c][v - start] = (int) connectivity[v];
			}
			cellTypes[c] = (int) types[c];
			start = end;
		}
		return new VtuTestReader(np, nc, pointsArray.getAttribute("type"), points, cells, cellTypes);
	}

	private static double[] values(Element a, ByteOrder order, int headerBytes) {
		String type = a.getAttribute("type");
		String text = a.getTextContent().trim();
		if ("ascii".equals(a.getAttribute("format"))) {
			String[] tokens = text.split("\\s+");
			double[] v = new double[tokens.length];
			for (int i = 0; i < v.length; i++) {
				v[i] = Double.parseDouble(tokens[i]);
			}
			return v;
		}
		if (!"binary".equals(a.getAttribute("format"))) {
			throw new IllegalArgumentException("unsupported format " + a.getAttribute("format"));
		}
		// the first whitespace-free token is the base64 block (VTK may follow it with <InformationKey> children)
		String block = text.split("\\s+")[0];
		ByteBuffer b = ByteBuffer.wrap(Base64.getDecoder().decode(block)).order(order);
		long bytes = headerBytes == 8 ? b.getLong() : Integer.toUnsignedLong(b.getInt());
		int width = switch (type) {
			case "Int8", "UInt8" -> 1;
			case "Float32", "Int32", "UInt32" -> 4;
			case "Float64", "Int64", "UInt64" -> 8;
			default -> throw new IllegalArgumentException("unsupported type " + type);
		};
		double[] v = new double[(int) (bytes / width)];
		for (int i = 0; i < v.length; i++) {
			v[i] = switch (type) {
				case "Int8" -> b.get();
				case "UInt8" -> b.get() & 0xFF;
				case "Float32" -> b.getFloat();
				case "Int32" -> b.getInt();
				case "UInt32" -> Integer.toUnsignedLong(b.getInt());
				case "Float64" -> b.getDouble();
				default -> b.getLong();
			};
		}
		return v;
	}
}
