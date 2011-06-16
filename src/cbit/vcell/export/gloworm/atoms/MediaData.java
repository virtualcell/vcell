/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.export.gloworm.atoms;



import java.io.*;

import cbit.vcell.export.gloworm.quicktime.MediaChunk;
/**
 * This type was created in VisualAge.
 */
public class MediaData extends Atoms {

	public static final String type = "mdat";
	protected MediaChunk[] mediaChunks;
	
/**
 * This method was created in VisualAge.
 * @param dReference DataReference
 */
public MediaData(MediaChunk[] chunks) {
	mediaChunks = chunks;
	size = 8;
	for (int i=0;i<mediaChunks.length;i++) size += mediaChunks[i].getSize();
}
/**
 * writeData method comment.
 */
public boolean writeData(DataOutputStream out) {
	try {
		int offset = 8;
		out.writeInt(size);
		out.writeBytes(type);
		for (int i=0;i<mediaChunks.length;i++) {
			out.write(mediaChunks[i].getDataBytes());
			mediaChunks[i].setOffset(offset);
			offset += mediaChunks[i].getSize();
		}
		return true;
	} catch (IOException e) {
		System.out.println("Unable to write: " + e.getMessage());
		e.printStackTrace();
		return false;
	}
}
/**
 * writeData method comment.
 */
public void writeData(File file, boolean isDataFile) throws IOException {
	if (!file.exists() || !isDataFile) {
		// new file; just write everything out
		DataOutputStream dout = new DataOutputStream(new FileOutputStream(file));
		writeData(dout);
		dout.close();
	} else {
		// file has mdat atom containing some or all of the chunks
		RandomAccessFile fw = new RandomAccessFile(file, "rw");
		long length = file.length();
		// append chunks that don't have their data bytes already in the file
		fw.seek(length);
		for (int i=0;i<mediaChunks.length;i++) {
			if (!mediaChunks[i].isDataInFile(file)) {
				mediaChunks[i].setOffset((int)length);
				fw.write(mediaChunks[i].getDataBytes());
				length += mediaChunks[i].getSize();
			}
		}
		// update the media data atom header
		size = (int)length;
		fw.seek(0);
		fw.writeInt(size);
		fw.close();
	}
}
}
