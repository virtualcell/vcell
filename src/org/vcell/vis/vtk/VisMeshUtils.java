package org.vcell.vis.vtk;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;

import javax.xml.bind.DatatypeConverter;

import org.apache.commons.io.FileUtils;
import org.apache.thrift.TDeserializer;
import org.apache.thrift.TException;
import org.apache.thrift.TSerializer;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.vcell.vis.vismesh.thrift.ChomboIndexData;
import org.vcell.vis.vismesh.thrift.FiniteVolumeIndexData;
import org.vcell.vis.vismesh.thrift.MovingBoundaryIndexData;
import org.vcell.vis.vismesh.thrift.VisMesh;

import cbit.util.xml.XmlUtil;

public class VisMeshUtils {

	public static ChomboIndexData readChomboIndexData(File chomboIndexDataFile) throws IOException {
		TDeserializer deserializer = new TDeserializer(new TBinaryProtocol.Factory());
		byte[] blob = FileUtils.readFileToByteArray(chomboIndexDataFile);
		ChomboIndexData chomboIndexData = new ChomboIndexData();
		try {
			deserializer.deserialize(chomboIndexData, blob);
		} catch (TException e) {
			e.printStackTrace();
			throw new IOException("error reading ChomboIndexData from file "+chomboIndexDataFile.getPath()+": "+e.getMessage(),e);
		}
		return chomboIndexData;
	}

	public static MovingBoundaryIndexData readMovingBoundaryIndexData(File movingBoundaryIndexDataFile) throws IOException {
		TDeserializer deserializer = new TDeserializer(new TBinaryProtocol.Factory());
		byte[] blob = FileUtils.readFileToByteArray(movingBoundaryIndexDataFile);
		MovingBoundaryIndexData movingBoundaryIndexData = new MovingBoundaryIndexData();
		try {
			deserializer.deserialize(movingBoundaryIndexData, blob);
		} catch (TException e) {
			e.printStackTrace();
			throw new IOException("error reading MovingBoundaryIndexData from file "+movingBoundaryIndexDataFile.getPath()+": "+e.getMessage(),e);
		}
		return movingBoundaryIndexData;
	}

	public static FiniteVolumeIndexData readFiniteVolumeIndexData(File finiteVolumeIndexFile) throws IOException {
		TDeserializer deserializer = new TDeserializer(new TBinaryProtocol.Factory());
		byte[] blob = FileUtils.readFileToByteArray(finiteVolumeIndexFile);
		FiniteVolumeIndexData finiteVolumeIndexData = new FiniteVolumeIndexData();
		try {
			deserializer.deserialize(finiteVolumeIndexData, blob);
		} catch (TException e) {
			e.printStackTrace();
			throw new IOException("error reading FiniteVolumeIndexData from file "+finiteVolumeIndexFile.getPath()+": "+e.getMessage(),e);
		}
		return finiteVolumeIndexData;
	}

	static void writeChomboIndexData(File chomboIndexFile, ChomboIndexData chomboIndexData) throws IOException {
		TSerializer serializer = new TSerializer(new TBinaryProtocol.Factory());
		try {
			byte[] blob = serializer.serialize(chomboIndexData);
			FileUtils.writeByteArrayToFile(chomboIndexFile, blob);
		} catch (TException e) {
			e.printStackTrace();
			throw new IOException("error writing ChomboIndexData to file "+chomboIndexFile.getPath()+": "+e.getMessage(),e);
		}
	}

	static void writeVisMesh(File visMeshFile, VisMesh visMesh) throws IOException {
		TSerializer serializer = new TSerializer(new TBinaryProtocol.Factory());
		try {
			byte[] blob = serializer.serialize(visMesh);
			FileUtils.writeByteArrayToFile(visMeshFile, blob);
		} catch (TException e) {
			e.printStackTrace();
			throw new IOException("error writing VisMesh to file "+visMeshFile.getPath()+": "+e.getMessage(),e);
		}
	}

	static void writeFiniteVolumeIndexData(File finiteVolumeIndexFile, FiniteVolumeIndexData finiteVolumeIndexData) throws IOException {
		TSerializer serializer = new TSerializer(new TBinaryProtocol.Factory());
		try {
			byte[] blob = serializer.serialize(finiteVolumeIndexData);
			FileUtils.writeByteArrayToFile(finiteVolumeIndexFile, blob);
		} catch (TException e) {
			e.printStackTrace();
			throw new IOException("error writing FiniteVolumeIndexData to file "+finiteVolumeIndexFile.getPath()+": "+e.getMessage(),e);
		}
	}
	
	public static void writeCellDataToVtu(File inputMeshFile, String dataName, double[] data, File outputMeshFile) throws IOException{
		Document meshDocument = XmlUtil.readXML(inputMeshFile);
		addCellDataToVtuXml(meshDocument, dataName, data);
		FileWriter fw = null;
		try {
			fw = new FileWriter(outputMeshFile);
			XMLOutputter xmlOut = new XMLOutputter();
			Format f = Format.getRawFormat();
			f.setLineSeparator("\n");
			f.setOmitEncoding(true);
			f.setExpandEmptyElements(true);
			xmlOut.setFormat(f);
			xmlOut.output(meshDocument, fw);
		}finally{
			if (fw!=null){
				fw.close();
			}
		}
	}

	public static void writePointDataToVtu(File inputMeshFile, String dataName, double[] data, File outputMeshFile) throws IOException{
		Document meshDocument = XmlUtil.readXML(inputMeshFile);
		addPointDataToVtuXml(meshDocument, dataName, data);
		FileWriter fw = null;
		try {
			fw = new FileWriter(outputMeshFile);
			XMLOutputter xmlOut = new XMLOutputter();
			Format f = Format.getRawFormat();
			f.setLineSeparator("\n");
			f.setOmitEncoding(true);
			f.setExpandEmptyElements(true);
			xmlOut.setFormat(f);
			xmlOut.output(meshDocument, fw);
		}finally{
			if (fw!=null){
				fw.close();
			}
		}
	}

	public static double[] readPointDataFromVtu(File inputMeshFile, String dataName) throws IOException{
		Document meshDocument = XmlUtil.readXML(inputMeshFile);
		double[] data = readPointDataFromVtuXml(meshDocument, dataName);
		return data;
	}

	private static void addCellDataToVtuXml(Document meshFileXmlDocument, String dataName, double[] data) {
		System.out.println("\n\n\n\n\n\n");
		double rangeMin = Double.POSITIVE_INFINITY;
		double rangeMax = Double.NEGATIVE_INFINITY;
		for (int i=0;i<data.length;i++){
			rangeMin = Math.min(rangeMin, data[i]);
			rangeMax = Math.max(rangeMax, data[i]);
		}
		Element vtkFileElement = meshFileXmlDocument.getRootElement();
		ByteOrder byteOrder;
		if (vtkFileElement.getAttributeValue("byte_order").equals("LittleEndian")){
			byteOrder = ByteOrder.LITTLE_ENDIAN;
		}else{
			byteOrder = ByteOrder.BIG_ENDIAN;
		}
		//vtkFileElement.setAttribute("header_type", "UInt32");
		Element unstructuredGridElement = vtkFileElement.getChild("UnstructuredGrid");
		@SuppressWarnings("unchecked")
		List<Element> pieceElements = unstructuredGridElement.getChildren("Piece");
		if (pieceElements.size()!=1){
			throw new RuntimeException("Expecting exactly one mesh piece, found "+pieceElements.size());
		}
		int numberOfCells = Integer.parseInt(pieceElements.get(0).getAttributeValue("NumberOfCells"));
//		int numberOfPoints = Integer.parseInt(pieceElements.get(0).getAttributeValue("NumberOfPoints"));
		if (numberOfCells != data.length){
			throw new RuntimeException("the data size "+data.length+" doesn't match the number of cells "+numberOfCells);
		}
		Element cellData = pieceElements.get(0).getChild("CellData");
		
		Element dataArray = new Element("DataArray");
		dataArray.setAttribute("type","Float64");
		dataArray.setAttribute("Name",dataName);
		dataArray.setAttribute("format","binary");
		if (rangeMin == (int)rangeMin){
			dataArray.setAttribute("RangeMin", ""+((int)rangeMin));
		}else{
			dataArray.setAttribute("RangeMin", ""+rangeMin);
		}
		if (rangeMax == (int)rangeMax){
			dataArray.setAttribute("RangeMax", ""+((int)rangeMax));
		}else{
			dataArray.setAttribute("RangeMax", ""+rangeMax);
		}
		
		
		byte[] bytes = new byte[numberOfCells*8+4];
		ByteBuffer b = ByteBuffer.wrap(bytes);
		b.order(byteOrder);
		b.putInt(numberOfCells*8);
		for (double d : data){
			b.putDouble(d);
		}
		String base64 = DatatypeConverter.printBase64Binary(bytes);
		dataArray.addContent("\n          "+base64+"\n        ");
		cellData.addContent("  ");
		cellData.addContent(dataArray);
		cellData.addContent("\n      ");
	}
	
	private static void addPointDataToVtuXml(Document meshFileXmlDocument, String dataName, double[] data) {
		System.out.println("\n\n\n\n\n\n");
		double rangeMin = Double.POSITIVE_INFINITY;
		double rangeMax = Double.NEGATIVE_INFINITY;
		for (int i=0;i<data.length;i++){
			rangeMin = Math.min(rangeMin, data[i]);
			rangeMax = Math.max(rangeMax, data[i]);
		}
		Element vtkFileElement = meshFileXmlDocument.getRootElement();
		ByteOrder byteOrder;
		if (vtkFileElement.getAttributeValue("byte_order").equals("LittleEndian")){
			byteOrder = ByteOrder.LITTLE_ENDIAN;
		}else{
			byteOrder = ByteOrder.BIG_ENDIAN;
		}
		//vtkFileElement.setAttribute("header_type", "UInt32");
		Element unstructuredGridElement = vtkFileElement.getChild("UnstructuredGrid");
		@SuppressWarnings("unchecked")
		List<Element> pieceElements = unstructuredGridElement.getChildren("Piece");
		if (pieceElements.size()!=1){
			throw new RuntimeException("Expecting exactly one mesh piece, found "+pieceElements.size());
		}
//		int numberOfCells = Integer.parseInt(pieceElements.get(0).getAttributeValue("NumberOfCells"));
		int numberOfPoints = Integer.parseInt(pieceElements.get(0).getAttributeValue("NumberOfPoints"));
		if (numberOfPoints != data.length){
			throw new RuntimeException("the data size "+data.length+" doesn't match the number of points "+numberOfPoints);
		}
		Element cellData = pieceElements.get(0).getChild("PointData");
		
		Element dataArray = new Element("DataArray");
		dataArray.setAttribute("type","Float64");
		dataArray.setAttribute("Name",dataName);
		dataArray.setAttribute("format","binary");
		if (rangeMin == (int)rangeMin){
			dataArray.setAttribute("RangeMin", ""+((int)rangeMin));
		}else{
			dataArray.setAttribute("RangeMin", ""+rangeMin);
		}
		if (rangeMax == (int)rangeMax){
			dataArray.setAttribute("RangeMax", ""+((int)rangeMax));
		}else{
			dataArray.setAttribute("RangeMax", ""+rangeMax);
		}
		
		
		byte[] bytes = new byte[numberOfPoints*8+4];
		ByteBuffer b = ByteBuffer.wrap(bytes);
		b.order(byteOrder);
		b.putInt(numberOfPoints*8);
		for (double d : data){
			b.putDouble(d);
		}
		String base64 = DatatypeConverter.printBase64Binary(bytes);
		dataArray.addContent("\n          "+base64+"\n        ");
		cellData.addContent("  ");
		cellData.addContent(dataArray);
		cellData.addContent("\n      ");
	}
	
	private static double[] readPointDataFromVtuXml(Document meshFileXmlDocument, String dataName) {
		Element vtkFileElement = meshFileXmlDocument.getRootElement();
		ByteOrder byteOrder;
		if (vtkFileElement.getAttributeValue("byte_order").equals("LittleEndian")){
			byteOrder = ByteOrder.LITTLE_ENDIAN;
		}else{
			byteOrder = ByteOrder.BIG_ENDIAN;
		}
		//vtkFileElement.setAttribute("header_type", "UInt32");
		Element unstructuredGridElement = vtkFileElement.getChild("UnstructuredGrid");
		@SuppressWarnings("unchecked")
		List<Element> pieceElements = unstructuredGridElement.getChildren("Piece");
		if (pieceElements.size()!=1){
			throw new RuntimeException("Expecting exactly one mesh piece, found "+pieceElements.size());
		}
//		int numberOfCells = Integer.parseInt(pieceElements.get(0).getAttributeValue("NumberOfCells"));
		int numberOfPoints = Integer.parseInt(pieceElements.get(0).getAttributeValue("NumberOfPoints"));
		Element pointData = pieceElements.get(0).getChild("PointData");
		List<Element> dataArrayElements = pointData.getChildren("DataArray");
		for (Element dataArrayElement : dataArrayElements){
			if (dataArrayElement.getAttribute("Name").equals(dataName)){
				if (!dataArrayElement.getAttribute("type").equals("Float64")){
					throw new RuntimeException("expecting type Float64");
				}
				if (!dataArrayElement.getAttribute("format").equals("binary")){
					throw new RuntimeException("expecting format binary");
				}
			}
			String base64 = dataArrayElement.getText().trim();
			byte[] bytes = new byte[numberOfPoints*8+4];
			ByteBuffer b = ByteBuffer.wrap(DatatypeConverter.parseBase64Binary(base64));
			b.order(byteOrder);
			double[] data = new double[numberOfPoints];
			int numPointsEncoded = b.getInt()/8;
			for (int i=0;i<numPointsEncoded;i++){
				data[i] = b.getDouble();
			}
			return data;
		}
		throw new RuntimeException("point data "+dataName+" not found");
	}

}
