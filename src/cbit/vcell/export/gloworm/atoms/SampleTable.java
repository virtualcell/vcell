package cbit.vcell.export.gloworm.atoms;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.vcell.export.quicktime.*;
import java.io.*;
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
 * @param dReference cbit.vcell.export.quicktime.DataReference
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
