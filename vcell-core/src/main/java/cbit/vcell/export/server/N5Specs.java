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
import cbit.vcell.simdata.VCData;
import org.janelia.saalfeldlab.n5.Bzip2Compression;
import org.janelia.saalfeldlab.n5.Compression;
import org.janelia.saalfeldlab.n5.GzipCompression;
import org.janelia.saalfeldlab.n5.N5FSWriter;
import org.vcell.util.Compare;
import org.vcell.util.DataAccessException;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;

/**
 * This type was created in VisualAge.
 */
@SuppressWarnings("serial")
public class N5Specs extends FormatSpecificSpecs implements Serializable {
	private ExportFormat format;
	private ExportConstants.DataType dataType;
	private ExportSpecs.SimNameSimDataID[] simNameSimDataIDs;
	private CompressionLevel compression;

	public static enum CompressionLevel{
		RAW,
		BZIP,
		GZIP
	}

/**
 * TextSpecs constructor comment.
 */
	public N5Specs(ExportSpecs.SimNameSimDataID[] simNameSimDataIDs, ExportConstants.DataType dataType, ExportFormat format, CompressionLevel compressionLevel) {
		this.format = format;
		this.dataType = dataType;
		this.simNameSimDataIDs = simNameSimDataIDs;
		this.compression = compressionLevel;
	}
	public ExportSpecs.SimNameSimDataID[] getSimNameSimDataIDs(){
		return simNameSimDataIDs;
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
				return null;
			case BZIP:
				return new Bzip2Compression();
			case GZIP:
				return new GzipCompression();
			default:
				return null;
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


	public static void imageJMetaData(N5FSWriter n5FSWriter, String datasetPath, VCData vcData, int numChannels, HashMap<String, Object> additionalMetData) throws MathException, DataAccessException {
		HashMap<String, Object> metaData = new HashMap<>();
		metaData.put("name", "TestName");
		metaData.put("fps", 0.0);
		metaData.put("frameInterval", 0.0);
		metaData.put("pixelWidth", 1.0);
		metaData.put("pixelHeight", 1.0);
		metaData.put("pixelDepth", 1.0);
		metaData.put("xOrigin", 0.0);
		metaData.put("yOrigin", 0.0);
		metaData.put("zOrigin", 0.0);
		metaData.put("numChannels", numChannels); //
		metaData.put("numSlices", vcData.getMesh().getSizeZ());
		metaData.put("numFrames", vcData.getDataTimes().length);
		metaData.put("type", 2); //https://imagej.nih.gov/ij/developer/api/ij/ij/ImagePlus.html#getType() Grayscale with float types
		metaData.put("unit", "uM"); //https://imagej.nih.gov/ij/developer/api/ij/ij/measure/Calibration.html#getUnit()
		metaData.put("properties", additionalMetData);

		try {
			n5FSWriter.setAttributes(datasetPath, metaData);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}


}
