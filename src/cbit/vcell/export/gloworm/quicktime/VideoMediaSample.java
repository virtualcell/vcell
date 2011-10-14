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


import cbit.vcell.export.gloworm.atoms.SampleDescriptionEntry;
/**
 * This type was created in VisualAge.
 */
public abstract class VideoMediaSample implements MediaSample {
	private short colorDepth;
	private boolean isGrayscale;
	private short width;
	private short height;
	private byte[] dataBytes = null;
	private int duration;
	public final static String mediaType = "vide";
/**
 * This method was created in VisualAge.
 * @return short
 */
public abstract short getColorDepth();
/**
 * This method was created in VisualAge.
 * @return byte[]
 */
public abstract byte[] getDataBytes();
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public abstract String getDataFormat();
/**
 * This method was created in VisualAge.
 * @return int
 */
public abstract int getDuration();
/**
 * This method was created in VisualAge.
 * @return short
 */
public abstract int getHeight();
/**
 * This method was created in VisualAge.
 * @return boolean
 */
public abstract boolean getIsGrayscale();
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public abstract String getMediaType();
/**
 * This method was created in VisualAge.
 * @return SampleDescriptionEntry
 */
public abstract SampleDescriptionEntry getSampleDescriptionEntry();
/**
 * getSize method comment.
 */
public abstract int getSize();
/**
 * This method was created in VisualAge.
 * @return short
 */
public abstract int getWidth();
/**
 * isKeyFrame method comment.
 */
public abstract boolean isKeyFrame();
}
