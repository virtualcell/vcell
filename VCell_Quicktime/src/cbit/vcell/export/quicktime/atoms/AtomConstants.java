package cbit.vcell.export.quicktime.atoms;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
/**
 * This type was created in VisualAge.
 */
public interface AtomConstants {
	
	public static final short ditherCopy = (short)0x40;
	public static final short english = (short)0;
	public static final short defaultColorValue = (short)-0x8000;
	public static final short defaultColorTableID = (short)-1;
	public static final short defaultPlayBackQuality = (short)Integer.parseInt("00111000", 2); // 8 bit to 32 bit depth, draft
	public static final short defaultColorDepth = (short)32;
	public static final short defaultLayer = (short)0;
	public static final short defaultAlternateGroup = (short)0;
	public static final short defaultVolume = (short)0x100;
	public static final int lossLessQuality = 1024;
	public static final int defaultFrameResolution = 72 * 0x10000;
	public static final int defaultDataSize = 0;
	public static final int defaultMediaRate = 1 * 0x10000;
	public static final int defaultTimeScale = 1000; // milliseconds;
	public static final int javaToMacSeconds = 2082844800;
	public static final int[] defaultMatrix = {0x10000,0,0,0,0x10000,0,0,0,0x40000000};
	public static final byte defaultTrackHeaderFlags = (byte)Integer.parseInt("1111", 2); // poster, preview, movie, enabled
	public static final byte noLeanAhead = (byte)1;
	public static final byte selfReference = (byte)1;
	public static final String mediaHandler = "mhlr";
	public static final String dataHandler = "dhlr";
	public static final String videoSubtype = "vide";
	public static final String aliasSubtype = "alis";
	
}
