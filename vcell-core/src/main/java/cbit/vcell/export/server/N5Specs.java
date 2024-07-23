/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.export.server;

import cbit.vcell.math.MathException;
import com.google.gson.Gson;
import org.janelia.saalfeldlab.n5.*;
import org.vcell.util.DataAccessException;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

/**
 * This type was created in VisualAge.
 */
@SuppressWarnings("serial")
public class N5Specs extends FormatSpecificSpecs implements Serializable {
	private final ExportFormat format;
	private final ExportConstants.DataType dataType;
	private final CompressionLevel compression;

	public final String dataSetName;

	public static String n5Suffix = "n5";
	public static String maskingMetaDataName = "maskMapping";

	public static enum CompressionLevel{
		RAW,
		BZIP,
		GZIP
	}

/**
 * TextSpecs constructor comment.
 */
	public N5Specs(ExportConstants.DataType dataType, ExportFormat format, CompressionLevel compressionLevel, String dataSetName) {
		this.format = format;
		this.dataType = dataType;
		this.compression = compressionLevel;
		this.dataSetName = dataSetName;
	}
	/**
	 * This method was created in VisualAge.
	 * @return int
	 */
	public ExportConstants.DataType getDataType() {
		return dataType;
	}
	/**
	 * This method was created in VisualAge.
	 * @return int
	 */
	public ExportFormat getFormat() {
		return format;
	}

	public Compression getCompression(){
		switch (compression){
			case RAW:
				return new RawCompression();
			case BZIP:
				return new Bzip2Compression();
			case GZIP:
				return new GzipCompression();
			default:
				return new RawCompression();
		}
	}


	public boolean equals(Object object) {
		return false;
	}

		/**
	 * Insert the method's description here.
	 * Creation date: (4/2/2001 5:08:46 PM)
	 * @return java.lang.String
	 */
	public String toString() {
		return "N5Specs: [compression: " + format + ", chunking: " + dataType + ", switchRowsColumns: " + "]";
	}

	public static void writeImageJMetaData(long jobID,long[] dimensions, int[] blockSize, Compression compression, N5FSWriter n5FSWriter, String datasetName, int numChannels, int zSlices,
										   int timeLength, HashMap<Integer, String> maskMapping, double pixelHeight, double pixelWidth, double pixelDepth, String unit) throws MathException, DataAccessException {
		try {
			HashMap<String, String> compresssionMap = new HashMap<>(){{put("type", compression.getType().toLowerCase());}};
			ImageJMetaData imageJMetaData = ImageJMetaData.generateDefaultRecord(dimensions, blockSize, compresssionMap, datasetName, numChannels, zSlices, timeLength, maskMapping, pixelHeight, pixelWidth, pixelDepth, unit);
			Path path = Path.of(n5FSWriter.getURI().getPath(), String.valueOf(jobID), "attributes.json");
			Gson gson = n5FSWriter.getGson();
			String jsonRepresentation = gson.toJson(imageJMetaData, ImageJMetaData.class);
			FileWriter fileWriter = new FileWriter(path.toFile());
			fileWriter.write(jsonRepresentation);
			fileWriter.close();
		} catch (N5Exception | IOException e) {
			throw new RuntimeException(e);
		}

    }

	record ImageJMetaData(long[] dimensions ,int[] blockSize, HashMap<String, String> compression, String dataType, String name, double fps, double frameInterval, double pixelWidth,
						  double pixelHeight, double pixelDepth, double xOrigin, double yOrigin, double zOrigin, int numChannels, int numSlices, int numFrames,
						  int type, String unit, HashMap<Integer, String> maskMapping){

		// https://github.com/saalfeldlab/n5
		//https://imagej.nih.gov/ij/developer/api/ij/ij/ImagePlus.html#getType() Grayscale with float types
		//https://imagej.nih.gov/ij/developer/api/ij/ij/measure/Calibration.html#getUnit()

		public static ImageJMetaData generateDefaultRecord(long[] dimensions ,int[] blockSize, HashMap<String, String> compression, String dataSetName, int numChannels,
														   int numSlices, int numFrames, HashMap<Integer, String> maskMapping, double pixelHeight, double pixelWidth, double pixelDepth, String unit){
			return  new ImageJMetaData(dimensions, blockSize, compression, DataType.FLOAT64.name().toLowerCase() ,dataSetName, 0.0, 0.0,
					pixelWidth, pixelHeight, pixelDepth, 0.0, 0.0, 0.0, numChannels, numSlices, numFrames, 2, unit, maskMapping);
		}
	}


}
