/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.export.gloworm.quicktime;


import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.zip.DataFormatException;

import cbit.vcell.export.gloworm.atoms.AtomConstants;
import cbit.vcell.export.gloworm.atoms.Atoms;
import cbit.vcell.export.gloworm.atoms.BaseMediaHeader;
import cbit.vcell.export.gloworm.atoms.BaseMediaInfo;
import cbit.vcell.export.gloworm.atoms.BaseMediaInformation;
import cbit.vcell.export.gloworm.atoms.ChunkOffset;
import cbit.vcell.export.gloworm.atoms.DataInformation;
import cbit.vcell.export.gloworm.atoms.DataReference;
import cbit.vcell.export.gloworm.atoms.DataReferenceEntry;
import cbit.vcell.export.gloworm.atoms.EditAtom;
import cbit.vcell.export.gloworm.atoms.EditList;
import cbit.vcell.export.gloworm.atoms.HandlerReference;
import cbit.vcell.export.gloworm.atoms.MediaAtom;
import cbit.vcell.export.gloworm.atoms.MediaData;
import cbit.vcell.export.gloworm.atoms.MediaHeader;
import cbit.vcell.export.gloworm.atoms.MediaInformation;
import cbit.vcell.export.gloworm.atoms.MovieAtom;
import cbit.vcell.export.gloworm.atoms.MovieHeader;
import cbit.vcell.export.gloworm.atoms.SampleSize;
import cbit.vcell.export.gloworm.atoms.SampleTable;
import cbit.vcell.export.gloworm.atoms.SampleTableDescription;
import cbit.vcell.export.gloworm.atoms.SampleToChunk;
import cbit.vcell.export.gloworm.atoms.SyncSample;
import cbit.vcell.export.gloworm.atoms.TimeToSample;
import cbit.vcell.export.gloworm.atoms.TrackAtom;
import cbit.vcell.export.gloworm.atoms.TrackHeader;
import cbit.vcell.export.gloworm.atoms.TrackReference;
import cbit.vcell.export.gloworm.atoms.UserData;
import cbit.vcell.export.gloworm.atoms.VideoMediaInformation;
import cbit.vcell.export.gloworm.atoms.VideoMediaInformationHeader;
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
 * @return TrackAtom
 * @param track Track
 */
private final static MediaInformation makeMediaInformation(MediaTrack track) throws DataFormatException {
	SampleTableDescription stsd = new SampleTableDescription(track.getSampleDescriptionEntries());
	TimeToSample stts = new TimeToSample(track.getSampleDurations());
	SyncSample stss = new SyncSample(track.getKeyFrames());
	SampleToChunk stsc = new SampleToChunk(track.getChunkIDs());
	SampleSize stsz = new SampleSize(track.getSampleSizes());
	ChunkOffset stco = new ChunkOffset(track.getChunkOffsets());
	SampleTable stbl = new SampleTable(stsd, stts, stss, stsc, stsz, stco);
	DataReferenceEntry[] entries = new DataReferenceEntry[track.getDataReferences().length];
	for (int i=0;i<track.getDataReferences().length;i++)
		entries[i] = new DataReferenceEntry(track.getDataReferences()[i], track.getDataReferenceTypes()[i]);
	DataReference dref = new DataReference(entries); 
	DataInformation dinf = new DataInformation(dref);
	HandlerReference dhlr = new HandlerReference("dhlr", AtomConstants.COMPONENT_SUBTYPE_FILE_ALIAS);
	if (track.getMediaType().equals(AtomConstants.MEDIA_TYPE_VIDEO)) {
		VideoMediaInformationHeader vmhd = new VideoMediaInformationHeader();
		return new VideoMediaInformation(vmhd, dhlr, dinf, stbl);
	} else if (track.getMediaType().equals(AtomConstants.MEDIA_TYPE_OBJECT) || track.getMediaType().equals(AtomConstants.MEDIA_TYPE_QTVR)) {
		BaseMediaInfo gmin = new BaseMediaInfo();
		BaseMediaHeader gmhd = new BaseMediaHeader(gmin);
		return new BaseMediaInformation(gmhd, dhlr, dinf, stbl);
	} else {
		throw new DataFormatException("Unknown media type");
	}
}
/**
 * This method was created in VisualAge.
 * @return TrackAtom
 * @param movie Movie
 * @param track Track
 */
private final static TrackAtom makeTrackAtom(MediaMovie movie, int trackIndex) throws DataFormatException {
	if ((0 <= trackIndex) && (trackIndex < movie.getTracks().length)) {
		MediaTrack track = movie.getTracks()[trackIndex];
		MediaInformation minf = MediaMethods.makeMediaInformation(track);
		int componentSubtype = 0;
		if (track.getMediaType().equals(AtomConstants.MEDIA_TYPE_VIDEO)) componentSubtype = AtomConstants.COMPONENT_SUBTYPE_VIDEO;
		if (track.getMediaType().equals(AtomConstants.MEDIA_TYPE_OBJECT)) componentSubtype = AtomConstants.COMPONENT_SUBTYPE_OBJECT;
		if (track.getMediaType().equals(AtomConstants.MEDIA_TYPE_QTVR)) componentSubtype = AtomConstants.COMPONENT_SUBTYPE_QTVR;
		HandlerReference mhlr = new HandlerReference("mhlr", componentSubtype);
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
		TrackAtom trak = null;
		if (movie instanceof VRMediaMovie) {
			TrackReference trakRef = ((VRMediaMovie)movie).getTrackReference(trackIndex);
			trak = new TrackAtom(tkhd, null, null, edts, trakRef, null, null, mdia, null);
		} else {
			trak = new TrackAtom(tkhd, edts, mdia);
		}
		return trak;
	} else
		throw new DataFormatException("Track index out of bounds !");
}
/**
 * This method was created in VisualAge.
 * @param out OutputStream
 */
public final static void writeMovie(DataOutputStream dataOutputStream, MediaMovie movie) throws DataFormatException {
	// create and write out data atom
	MediaChunk[] chunksToBeWritten = movie.getSelfreferencedChunks();
	MediaData mdat = new MediaData(chunksToBeWritten);
	mdat.writeData(dataOutputStream);
	// create and write out movie atom
	TrackAtom[] traks = new TrackAtom[movie.getTracks().length];
	for (int i=0;i<movie.getTracks().length;i++) traks[i] = MediaMethods.makeTrackAtom(movie, i);
	MovieHeader mvhd = new MovieHeader(movie.getTimeScale(), movie.getDuration(), movie.getNumberOfTracks());
	UserData userData = new UserData(movie.getUserDataEntries());
	MovieAtom moov = new MovieAtom(mvhd, traks, userData);
	moov.writeData(dataOutputStream);
}
/**
 * This method was created in VisualAge.
 * @param out OutputStream
 */
public final static void writeMovie(File file, MediaMovie movie, boolean isDataFile) throws DataFormatException, IOException {
	// create and write out data atom
	MediaChunk[] chunksToBeWritten = movie.getSelfreferencedChunks();
	MediaData mdat = new MediaData(chunksToBeWritten);
	mdat.writeData(file, isDataFile);
	// create and write out movie atom
	TrackAtom[] traks = new TrackAtom[movie.getTracks().length];
	for (int i=0;i<movie.getTracks().length;i++) traks[i] = MediaMethods.makeTrackAtom(movie, i);
	MovieHeader mvhd = new MovieHeader(movie.getTimeScale(), movie.getDuration(), movie.getNumberOfTracks());
	UserData userData = new UserData(movie.getUserDataEntries());
	MovieAtom moov = new MovieAtom(mvhd, traks, userData);
	ByteArrayOutputStream bout = new ByteArrayOutputStream();
	DataOutputStream dout = new DataOutputStream(bout);
	moov.writeData(dout);
	bout.close();
//	byte[] moovBytes = bout.toByteArray();
	RandomAccessFile fw = new RandomAccessFile(file, "rw");
	fw.seek(file.length());
	fw.write(bout.toByteArray());
	fw.close();
}
}
