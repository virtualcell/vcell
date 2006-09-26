package cbit.vcell.export.quicktime;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.vcell.export.quicktime.atoms.*;
import java.io.*;
import java.util.zip.*;
/**
 * This type was created in VisualAge.
 */
public class MediaMethods {
/**
 * This method was created in VisualAge.
 * @return int
 */
public static final int getMacintoshTime() {
	long time = System.currentTimeMillis();
	int macTime = (int)(time/1000) + Atoms.javaToMacSeconds;
	return macTime;
}
/**
 * This method was created in VisualAge.
 * @return cbit.vcell.export.quicktime.atoms.TrackAtom
 * @param track cbit.vcell.export.quicktime.Track
 */
public final static MediaInformation makeMediaInformation(MediaTrack track) throws DataFormatException {
	SampleTableDescription stsd = new SampleTableDescription(track.getSampleDescriptionEntries());
	TimeToSample stts = new TimeToSample(track.getSampleDurations());
	SyncSample stss = new SyncSample(track.getKeyFrames());
	SampleToChunk stsc = new SampleToChunk(track.getChunkIDs());
	SampleSize stsz = new SampleSize(track.getSampleSizes());
	ChunkOffset stco = new ChunkOffset(track.getChunkOffsets());
	SampleTable stbl = new SampleTable(stsd, stts, stss, stsc, stsz, stco);
	DataReferenceEntry[] entries = new DataReferenceEntry[track.getDataReferences().length];
	for (int i=0;i<track.getDataReferences().length;i++)
		entries[i] = new DataReferenceEntry(track.getDataReferences()[i]);
	DataReference dref = new DataReference(entries); 
	DataInformation dinf = new DataInformation(dref);
	HandlerReference dhlr = new HandlerReference("dhlr", "alis");
	VideoMediaInformationHeader vmhd = new VideoMediaInformationHeader();
	MediaInformation minf = new VideoMediaInformation(vmhd, dhlr, dinf, stbl);
	return minf;
}
/**
 * This method was created in VisualAge.
 * @return cbit.vcell.export.quicktime.atoms.TrackAtom
 * @param movie cbit.vcell.export.quicktime.Movie
 * @param track cbit.vcell.export.quicktime.Track
 */
public final static TrackAtom makeTrackAtom(MediaMovie movie, int trackIndex) throws DataFormatException {
	if ((0 <= trackIndex) && (trackIndex < movie.getTracks().length)) {
		MediaTrack track = movie.getTracks()[trackIndex];
		MediaInformation minf = MediaMethods.makeMediaInformation(track);	
		HandlerReference mhlr = new HandlerReference("mhlr", track.getMediaType());
		MediaHeader mdhd = new MediaHeader(movie.getTimeScale(), track.getDuration());
		MediaAtom mdia = new MediaAtom(mdhd, mhlr, minf);
		TrackHeader tkhd = new TrackHeader(trackIndex + 1, track.getDuration(), track.getWidth(), track.getHeight());
		Edit[] edits;
		if (track.getEdits() == null)
			edits = new Edit[] {new Edit(track.getDuration())};
		else
			edits = track.getEdits();
		EditList elst = new EditList(edits);
		EditAtom edts = new EditAtom(elst);
		TrackAtom trak = new TrackAtom(tkhd, edts, mdia);
		return trak;
	} else
		throw new DataFormatException("Track index out of bounds !");
}
/**
 * This method was created in VisualAge.
 * @param out OutputStream
 */
public final static void writeMovie(DataOutputStream dataOutputStream, MediaMovie movie) throws DataFormatException {
	MediaChunk[] chunksToBeWritten = new MediaChunk[movie.getSelfreferencedChunks().size()];
	movie.getSelfreferencedChunks().copyInto(chunksToBeWritten);
	MediaData mdat = new MediaData(chunksToBeWritten);
	int offset = 8;
	for (int i=0;i<chunksToBeWritten.length;i++) {
		chunksToBeWritten[i].setOffset(offset);
		offset += chunksToBeWritten[i].getSize();
	}
	TrackAtom[] traks = new TrackAtom[movie.getTracks().length];
	for (int i=0;i<movie.getTracks().length;i++) traks[i] = MediaMethods.makeTrackAtom(movie, i);
	MovieHeader mvhd = new MovieHeader(movie.getTimeScale(), movie.getDuration(), movie.getNumberOfTracks());
	UserData userData = new UserData(movie.getUserDataEntries());
	MovieAtom moov = new MovieAtom(mvhd, traks, userData);
	mdat.writeData(dataOutputStream);
	moov.writeData(dataOutputStream);
}
}
