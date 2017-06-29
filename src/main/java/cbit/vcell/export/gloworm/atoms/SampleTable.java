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


import java.io.DataOutputStream;
import java.io.IOException;
/**
 * This type was created in VisualAge.
 */
public class SampleTable extends Atoms {

	public static final String type = "stbl";
	protected SampleTableDescription sampleTableDescription;
	protected TimeToSample timeToSample;
	protected SyncSample syncSample;
	protected SampleToChunk sampleToChunk;
	protected SampleSize sampleSize;
	protected ChunkOffset chunkOffset;
	
/**
 * This method was created in VisualAge.
 * @param dReference DataReference
 */
public SampleTable(SampleTableDescription stsd, TimeToSample stts, SyncSample stss, SampleToChunk stsc, SampleSize stsz, ChunkOffset stco) {
	sampleTableDescription = stsd;
	timeToSample = stts;
	syncSample = stss;
	sampleToChunk = stsc;
	sampleSize = stsz;
	chunkOffset = stco;
	size = 8 + stsd.size + stts.size + stsc.size + stsz.size + stco.size;
	if (! stss.allKey) size += stss.size;
}
/**
 * writeData method comment.
 */
public boolean writeData(DataOutputStream out) {
	try {
		out.writeInt(size);
		out.writeBytes(type);
		sampleTableDescription.writeData(out);
		timeToSample.writeData(out);
		if (! syncSample.allKey) syncSample.writeData(out);
		sampleToChunk.writeData(out);
		sampleSize.writeData(out);
		chunkOffset.writeData(out);
		return true;
	} catch (IOException e) {
		System.out.println("Unable to write: " + e.getMessage());
		e.printStackTrace();
		return false;
	}
}
}
