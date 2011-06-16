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
	public static final short defaultGraphicsMode = (short)0x40;
	public static final short[] defaultOpcolor = {(short)0x8000, (short)0x8000, (short)0x8000};
	public static final short defaultBalance = (short)0;
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
	public static final String mediaHandlerType = "mhlr";
	public static final String dataHandlerType = "dhlr";
	public static final String[] COMPONENT_SUBTYPES = new String[] {"vide", "alis", "qtvr", "obje"};
	public static final String[] COMPONENT_SUBTYPE_HANDLER_NAMES = new String[] {"Apple Video Media Handler", "Apple Alias Data Handler", "QTVR Media Handler", "OBJE Media Handler"};
	public static final int COMPONENT_SUBTYPE_VIDEO = 0;
	public static final int COMPONENT_SUBTYPE_FILE_ALIAS = 1;
	public static final int COMPONENT_SUBTYPE_QTVR = 2;
	public static final int COMPONENT_SUBTYPE_OBJECT = 3;
	public static final String MEDIA_TYPE_VIDEO = "vide";
	public static final String MEDIA_TYPE_SOUND = "soun";
	public static final String MEDIA_TYPE_QTVR = "qtvr";
	public static final String MEDIA_TYPE_OBJECT = "obje";
}
