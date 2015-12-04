package org.vcell.vis.vtk;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.thrift.TDeserializer;
import org.apache.thrift.TException;
import org.apache.thrift.TSerializer;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.vcell.vis.vismesh.thrift.ChomboIndexData;
import org.vcell.vis.vismesh.thrift.FiniteVolumeIndexData;

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

}
