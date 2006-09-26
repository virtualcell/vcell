package cbit.vcell.export.quicktime.atoms;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.vcell.export.quicktime.*;
import java.io.*;
/**
 * This type was created in VisualAge.
 */
public class TrackHeader extends LeafAtom {

	public static final int size = 92;
	public static final String type = "tkhd";
	protected int creationTime;
	protected int modificationTime;
	protected int trackID;
	protected byte[] reserved1 = new byte[4];
	protected int trackDuration;
	protected byte[] reserved2 = new byte[8];
	protected short layer;
	protected short alternateGroup;
	protected short volume;
	protected byte[] reserved3 = new byte[2];
	protected int[] matrix;
	protected int trackWidth;
	protected int trackHeight;
/**
 * This method was created in VisualAge.
 * @param ID int
 * @param duration int
 * @param width int
 * @param height int
 */
public TrackHeader(int ID, int duration, int width, int height) {
	this(MediaMethods.getMacintoshTime(), MediaMethods.getMacintoshTime(), ID, duration, width, height);
}
/**
 * This method was created in VisualAge.
 * @param ID int
 * @param duration int
 * @param width int
 * @param height int
 */
public TrackHeader(int cTime, int mTime, int ID, int duration, int width, int height) {
	flags[2] = defaultTrackHeaderFlags;
	creationTime = cTime;
	modificationTime = mTime;
	trackID = ID;
	trackDuration = duration;
	layer = defaultLayer;
	alternateGroup = defaultAlternateGroup;
	volume = defaultVolume;
	matrix = defaultMatrix;
	trackWidth = width;
	trackHeight = height;
}
/**
 * writeData method comment.
 */
public boolean writeData(DataOutputStream out) {
	try {
		out.writeInt(size);
		out.writeBytes(type);
		out.writeByte(version);
		out.write(flags);
		out.writeInt(creationTime);
		out.writeInt(modificationTime);
		out.writeInt(trackID);
		out.write(reserved1);
		out.writeInt(trackDuration);
		out.write(reserved2);
		out.writeShort(layer);
		out.writeShort(alternateGroup);
		out.writeShort(volume);
		out.write(reserved3);
		for (int i=0;i<9;i++) out.writeInt(matrix[i]);
		out.writeInt(trackWidth * 0x10000);
		out.writeInt(trackHeight * 0x10000);
		return true;
	} catch (IOException e) {
		System.out.println("Unable to write: " + e.getMessage());
		e.printStackTrace();
		return false;
	}
}
}
