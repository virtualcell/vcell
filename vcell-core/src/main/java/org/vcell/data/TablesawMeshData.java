package org.vcell.data;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

import org.tensorflow.ndarray.NdArrays;
import org.tensorflow.ndarray.Shape;
import org.tensorflow.ndarray.ShortNdArray;
import org.tensorflow.ndarray.buffer.DataBuffers;
import tech.tablesaw.api.*;
import tech.tablesaw.columns.Column;

public record TablesawMeshData(
		Map<Coordinate, Long> sizeData,
		Map<Coordinate, Double> extentData,
		Map<Coordinate, Double> originData,
		Table volumeRegionsMapSubvolume,
		Table membraneRegionsMapVolumeRegion,
		ShortNdArray volumeRegionMapData,
		Table membraneElements
) {
	public enum Coordinate {
		X,
		Y,
		Z,
	}

	private record ColumnDescription(Class<? extends Column<?>> columnClass, String columnName){}
	private static final Pattern trailingCommentPattern = Pattern.compile("(\\w)//");

	public static TablesawMeshData loadMeshFromFile(File file) throws IOException, DataFormatException {
		if (!file.exists()) throw new IllegalArgumentException("File does not exist: " + file.getAbsolutePath());
		return TablesawMeshData.loadMesh(new BufferedReader(new InputStreamReader(new FileInputStream(file))));
	}

	public static TablesawMeshData loadMeshFromResource(Path resourceLocation) throws IOException, DataFormatException {
		InputStream resourceStream;
		if (!resourceLocation.toFile().exists())
			throw new IllegalArgumentException("Resource does not exist: " + resourceLocation);
		if (null == (resourceStream = ClassLoader.getSystemResourceAsStream(resourceLocation.toFile().getAbsolutePath())))
			throw new IllegalArgumentException(String.format("Cannot load `%s` as resource", resourceLocation));
		return TablesawMeshData.loadMesh(new BufferedReader(new InputStreamReader(resourceStream)));
	}

	private static TablesawMeshData loadMesh(BufferedReader reader) throws IOException, DataFormatException {
		if (!reader.ready()) throw new IllegalStateException("Unable to prepare buffered reader for mesh file");
		TablesawMeshData.processVersion(reader);
		TablesawMeshData.processCartesianMeshHeader(reader);
		Map<Coordinate, Long> sizeData = TablesawMeshData.processCartesianData(reader, Long.class);
		Map<Coordinate, Double> extentData = TablesawMeshData.processCartesianData(reader, Double.class);
		Map<Coordinate, Double> originData = TablesawMeshData.processCartesianData(reader, Double.class);

		TablesawMeshData.processVolumeRegionsMapSubvolumeHeader(reader);
		Table volumeRegionsMapSubvolume = TablesawMeshData.processVolumeRegions(reader);

		TablesawMeshData.processMembraneRegionsMapVolumeRegionHeader(reader);
		Table membraneRegionsMapVolumeRegion = TablesawMeshData.processMembraneRegionsMapVolumeRegion(reader);

		TablesawMeshData.processVolumeElementsMapVolumeRegionHeader(reader);
		Shape dataShape = Shape.of(sizeData.get(Coordinate.Z), sizeData.get(Coordinate.Y), sizeData.get(Coordinate.X));
		ShortNdArray volumeRegionMapData = TablesawMeshData.processVolumeElementsMapVolumeRegion(reader, dataShape, volumeRegionsMapSubvolume);

		TablesawMeshData.processMembraneElementsHeader(reader);
		Table membraneElements = TablesawMeshData.processMembraneElements(reader);

		return new TablesawMeshData(sizeData, extentData, originData, volumeRegionsMapSubvolume, membraneRegionsMapVolumeRegion, volumeRegionMapData, membraneElements);
	}

	private static void processVersion(BufferedReader reader) throws IOException {
		String versionHeader = TablesawMeshData.getLine(reader);
		String[] headerPieces = versionHeader.split("\\s");
		if (headerPieces.length != 2) throw new IllegalStateException(String.format("Mesh version header should be \"Version X.X\", not\"%s\"", versionHeader));
		if (!"Version".equals(headerPieces[0])) throw new IllegalStateException(String.format("Expected string \"Version\", got \"%s\"", headerPieces[0]));
		if (!"1.2".equals(headerPieces[1])) throw new IllegalArgumentException(String.format("This algorithm requires version \"1.2\", got \"%s\"", headerPieces[1]));
	}

	private static void processCartesianMeshHeader(BufferedReader reader) throws IOException {
		String header = TablesawMeshData.getLine(reader);
		if (!"CartesianMesh {".equals(header)) throw new  IllegalStateException(String.format("Expected \"CartesianMesh {\", got \"%s\"", header));
	}

	private static <T> EnumMap<Coordinate, T> processCartesianData(BufferedReader reader, Class<T> type) throws IOException {
		String sizeLine = TablesawMeshData.getLine(reader);
		List<T> sizes = TablesawMeshData.getPartsOfLine(sizeLine, 3, type);
		EnumMap<Coordinate, T> cartesianSize = new EnumMap<>(Coordinate.class);
		cartesianSize.put(Coordinate.X, sizes.get(0));
		cartesianSize.put(Coordinate.Y, sizes.get(1));
		cartesianSize.put(Coordinate.Z, sizes.get(2));
		return cartesianSize;
	}

	private static void processVolumeRegionsMapSubvolumeHeader(BufferedReader reader) throws IOException {
		String header = TablesawMeshData.getLine(reader);
		if (!"VolumeRegionsMapSubvolume {".equals(header)) throw new  IllegalStateException(String.format("Expected \"VolumeRegionsMapSubvolume {\", got \"%s\"", header));
	}

	private static void processMembraneRegionsMapVolumeRegionHeader(BufferedReader reader) throws IOException {
		String header = TablesawMeshData.getLine(reader);
		if (!"MembraneRegionsMapVolumeRegion {".equals(header)) throw new  IllegalStateException(String.format("Expected \"MembraneRegionsMapVolumeRegion {\", got \"%s\"", header));
	}

	private static void processVolumeElementsMapVolumeRegionHeader(BufferedReader reader) throws IOException {
		String header = TablesawMeshData.getLine(reader);
		if (!"VolumeElementsMapVolumeRegion {".equals(header)) throw new  IllegalStateException(String.format("Expected \"VolumeElementsMapVolumeRegion {\", got \"%s\"", header));
	}

	private static void processMembraneElementsHeader(BufferedReader reader) throws IOException {
		String header = TablesawMeshData.getLine(reader);
		if (!"MembraneElements {".equals(header)) throw new  IllegalStateException(String.format("Expected \"MembraneElements {\", got \"%s\"", header));
	}

	private static Table processVolumeRegions(BufferedReader reader) throws IOException {
		return TablesawMeshData.createTable(reader, "VolumeRegionsMapSubvolume",
				new ColumnDescription(IntColumn.class, "VolRegID"),
				new ColumnDescription(IntColumn.class, "SubvolID"),
				new ColumnDescription(DoubleColumn.class, "Volume"),
				new ColumnDescription(StringColumn.class, "DomainName")
		);
	}

	private static Table processMembraneRegionsMapVolumeRegion(BufferedReader reader) throws IOException {
		return TablesawMeshData.createTable(reader, "MembraneRegionsMapVolumeRegion",
				new ColumnDescription(IntColumn.class, "MemRegID"),
				new ColumnDescription(IntColumn.class, "VolReg1"),
				new ColumnDescription(IntColumn.class, "VolReg2"),
				new ColumnDescription(DoubleColumn.class, "Surface")
		);
	}

	private static ShortNdArray processVolumeElementsMapVolumeRegion(BufferedReader reader, Shape dataShape, Table volumeRegionsMapSubvolume) throws IOException, DataFormatException {
		ShortNdArray volumeRegionMapData = NdArrays.ofShorts(Shape.of(dataShape.size())) // Start flat
				.copyFrom(DataBuffers.of(TablesawMeshData.decodeCompressedSection(reader))) // copy flat data over
				.withShape(dataShape); // reshape
		// Verify that each listed region value maps back to the available values.
		volumeRegionMapData.streamOfObjects().forEach( region -> {
			if (!(volumeRegionsMapSubvolume.column(0) instanceof IntColumn intColumn)) throw new IllegalStateException("Expected but didn't find an IntColumn!");
			if (!intColumn.contains(region.intValue())) throw new IllegalStateException("Source file is corrupted; Expected volume region map to have the same unique values as volume regions");
		});
		return volumeRegionMapData;
	}

	private static Table processMembraneElements(BufferedReader reader) throws IOException {
		return TablesawMeshData.createTable(reader, "MembraneElements",
				new ColumnDescription(IntColumn.class, "Indx"),
				new ColumnDescription(IntColumn.class, "Vol1"),
				new ColumnDescription(IntColumn.class, "Vol2"),
				new ColumnDescription(IntColumn.class, "Conn0"),
				new ColumnDescription(IntColumn.class, "Conn1"),
				new ColumnDescription(IntColumn.class, "Conn2"),
				new ColumnDescription(IntColumn.class, "Conn3"),
				new ColumnDescription(IntColumn.class, "MemRegID")
		);
	}

	private static Table createTable(BufferedReader reader, String tableName, ColumnDescription... columnTypes) throws IOException {
		int numberOfRows;
		String numberRowsStr = TablesawMeshData.getLine(reader);
		try {
			numberOfRows = Integer.parseInt(numberRowsStr);
		} catch (NumberFormatException e) {
			throw new IllegalStateException(String.format("Non-integer number of rows found: \"%s\"", numberRowsStr));
		}
		List<? extends Column<?>> columnList = Arrays.stream(columnTypes).filter(Objects::nonNull).map(e->createColumnFromType(e, numberOfRows)).toList();
		if (columnList.size() != columnTypes.length) throw new IllegalArgumentException("Null columns are not allowed!");
		for (int rowNum = 0; rowNum < numberOfRows; rowNum++) {
			String nextLine = TablesawMeshData.getLine(reader);
			if ("}".equals(nextLine)) throw new IllegalStateException(String.format("Unexpected end of section while creating table \"%s\", row %d/%d", nextLine, rowNum, numberOfRows));
			List<String> rowParts = TablesawMeshData.getPartsOfLine(nextLine, columnList.size(), String.class);
			try {
				for (int colIndex = 0; colIndex < columnTypes.length; colIndex++) {
					Column<?> column = columnList.get(colIndex);
					if (column instanceof IntColumn intColumn) intColumn.set(rowNum, Integer.parseInt(rowParts.get(colIndex)));
					else if (column instanceof DoubleColumn doubleColumn) doubleColumn.set(rowNum, Double.parseDouble(rowParts.get(colIndex)));
					else if (column instanceof BooleanColumn booleanColumn) booleanColumn.set(rowNum, Boolean.parseBoolean(rowParts.get(colIndex)));
					else if (column instanceof StringColumn stringColumn) stringColumn.set(rowNum, TablesawMeshData.deCommentString(rowParts.get(colIndex)));
					else throw new IllegalStateException("No logic to handle type of column: " + column.getClass().getName());
				}
			} catch (NumberFormatException e) {
				throw new IllegalStateException("Unexpected data-type detected while parsing VolumeRegionsMapSubvolume table:", e);
			}
		}
		String sectionEnd = TablesawMeshData.getLine(reader);
		if (!"}".equals(sectionEnd)) throw new IllegalStateException("Expected end of section not detected, received \"" + sectionEnd + "\" instead");

		return Table.create(tableName).addColumns(columnList.toArray(Column[]::new));
	}

	private static Column<?> createColumnFromType(ColumnDescription columnDescription, int size) {
		if (IntColumn.class.equals(columnDescription.columnClass)) return IntColumn.create(columnDescription.columnName, size);
		if (DoubleColumn.class.equals(columnDescription.columnClass)) return DoubleColumn.create(columnDescription.columnName, size);
		if (BooleanColumn.class.equals(columnDescription.columnClass)) return BooleanColumn.create(columnDescription.columnName, size);
		if (StringColumn.class.equals(columnDescription.columnClass)) return StringColumn.create(columnDescription.columnName, size);
		throw new IllegalArgumentException("No logic to handle type of column: " + columnDescription.columnClass.getName());
	}

	private static short[] decodeCompressedSection(BufferedReader reader) throws IOException, DataFormatException {
		String sectionHeader = TablesawMeshData.getLine(reader);
		String[] sectionHeaderParts = sectionHeader.split("\\s");
		if (sectionHeaderParts.length != 2 || !"Compressed".equals(sectionHeaderParts[1].strip())) throw new IOException("Invalid section header");
		int claimedMapSize;
		try {
			claimedMapSize = Integer.parseInt(sectionHeaderParts[0]);
		} catch (NumberFormatException e) {
			throw new IllegalStateException(String.format("Non-integer number of size found: \"%s\"", sectionHeaderParts[0]));
		}

		HexFormat formatter = HexFormat.of();
		StringBuilder hexCompressedString = new StringBuilder();

		String nextLine;
		while(!"}".equals(nextLine = TablesawMeshData.getLine(reader))) hexCompressedString.append(nextLine);
		if (hexCompressedString.length() % 2 != 0) throw new IllegalArgumentException("Encountered odd number of hex characters in line: " + nextLine);

		byte[] inputBytes = formatter.parseHex(hexCompressedString.toString());
		ByteBuffer outputBuffer = ByteBuffer.allocate(2 * claimedMapSize + 1); // two bytes per short/"integer", and 1 extra to detect a badly encoded document.
		Inflater inflater = new Inflater();
		inflater.setInput(inputBytes);
		int bytesWritten = inflater.inflate(outputBuffer);
		if (bytesWritten != 2 * claimedMapSize) throw new IllegalStateException(String.format("Stated number of entries (%d, %d bytes) does not match size of decompressed data (%d bytes)!", claimedMapSize, 2*claimedMapSize, bytesWritten));
		ShortBuffer dataBuffer = outputBuffer.flip().order(ByteOrder.LITTLE_ENDIAN).asShortBuffer();
		short[] data = new short[claimedMapSize];
		dataBuffer.get(data, 0, claimedMapSize);
		return data;
	}

	private static String getLine(BufferedReader reader) throws IOException {
		String value = TablesawMeshData.getEOFOrNonSectionEndNorCommentLine(reader);
		if (value == null) throw new IllegalStateException("Unexpected end of mesh file was encountered!"); // Reject EOF (a.k.a.: null) response
		Matcher matcher = TablesawMeshData.trailingCommentPattern.matcher(value);
		return matcher.replaceAll("$1 //");
	}

	private static String getEOFOrNonSectionEndNorCommentLine(BufferedReader reader) throws IOException {
		String value = "";
		while (value.isEmpty() || value.startsWith("//")){
			value = reader.readLine();
			if (null == value) return null;
			value = value.strip();
		}
		return value;
	}

	private static <T> List<T> getPartsOfLine(String line, int expectedNumberOfParts, Class<T> type) {
		List<String> parts = Arrays.stream(line.split("\\s")).filter(s->!s.isBlank()).toList();
		if (parts.size() != expectedNumberOfParts) throw new IllegalStateException(String.format("Expected %d parts, got %d", expectedNumberOfParts, parts.size()));
		List<T> sizes;
		try {
			if (type == Long.class) sizes = parts.stream().map(Long::parseLong).map(type::cast).toList();
			else if (type == Double.class) sizes = parts.stream().map(Double::parseDouble).map(type::cast).toList();
			else if (type == String.class) sizes = parts.stream().map(type::cast).toList();
			else throw new ClassCastException(String.format("Unexpected type \"%s\" requested", type.getSimpleName()));
		} catch (NumberFormatException e) {
			throw new IllegalStateException(String.format("Non-%s contained in line \"%s\"", type.getSimpleName(), line));
		}
		return sizes;
	}

	private static String deCommentString(String line){
		String trimmedLine = line.trim();
		if (!trimmedLine.startsWith("//")) return trimmedLine;
		return trimmedLine.substring(2).trim();
	}

}
