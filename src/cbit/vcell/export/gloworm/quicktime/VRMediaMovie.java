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

import cbit.vcell.export.gloworm.atoms.TrackReference;
import cbit.vcell.export.gloworm.atoms.TrackReferenceType;
import cbit.vcell.export.gloworm.atoms.UserDataEntry;
import cbit.vcell.export.gloworm.atoms.VRAtom;

/**
 * Insert the type's description here.
 * Creation date: (11/9/2005 4:03:01 AM)
 * @author: Ion Moraru
 */
public class VRMediaMovie extends MediaMovie {
/**
 * VRMediaMovie constructor comment.
 * @param tracks MediaTrack[]
 * @param duration int
 * @param timeScale int
 */
private VRMediaMovie(MediaTrack[] allTracks, int duration, int timeScale) {
	super(allTracks, duration, timeScale);
}


/**
 * Insert the method's description here.
 * Creation date: (11/9/2005 4:13:47 AM)
 * @return VRMediaMovie
 * @param qtvrTrack MediaTrack
 * @param objectTrack MediaTrack
 * @param otherTracks MediaTrack[]
 * @param duration int
 * @param timescale int
 */
public static VRMediaMovie createVRMediaMovie(MediaTrack qtvrTrack, MediaTrack objectTrack, MediaTrack imageTrack, MediaTrack[] otherTracks, int duration, int timescale) {
	if (otherTracks == null) otherTracks = new MediaTrack[0];
	MediaTrack[] allTracks = new MediaTrack[otherTracks.length + 3];
	allTracks[0] = qtvrTrack;
	allTracks[1] = objectTrack;
	allTracks[2] = imageTrack;
	for (int i = 0; i < otherTracks.length; i++){
		allTracks[i+3] = otherTracks[i];
	}
	VRMediaMovie vrMovie = new VRMediaMovie(allTracks, duration, timescale);
	// the f...ing undocumented piece of shit required
	vrMovie.addUserDataEntry(new UserDataEntry("ctyp", "qtvr", false));
	return vrMovie;
}


/**
 * Insert the method's description here.
 * Creation date: (11/9/2005 4:24:15 AM)
 * @return TrackReference
 * @param trackIndex int
 */
public TrackReference getTrackReference(int trackIndex) {
	TrackReference tref = null;
	switch (trackIndex) {
		case 0: {
			tref = new TrackReference(new TrackReferenceType[] {new TrackReferenceType(VRAtom.VR_OBJECT_REFERENCE_TYPE, new int[] {2})});
			break;
		} 
		case 1: {
			tref = new TrackReference(new TrackReferenceType[] {new TrackReferenceType(VRAtom.VR_IMAGE_REFERENCE_TYPE, new int[] {3})});
			break;
		} 
		default: {
			return null;		
		}
	}
	return tref;
}
}
