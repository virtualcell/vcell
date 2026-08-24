/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.image;

import java.io.IOException;
import java.lang.ref.SoftReference;
import java.security.MessageDigest;
import java.util.zip.DataFormatException;

import cbit.vcell.resource.PropertyLoader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.util.Hex;

/**
 * This type was created in VisualAge.
 */
@SuppressWarnings("serial")
public class VCImageCompressed extends VCImage {
	private final byte compressedPixels[];

	/**
	 * Set false to hold the inflated pixels strongly, as this class did before #2021.
	 * An escape hatch, not a tuning knob.
	 */
	public final static String PROPERTY_SOFT_PIXEL_CACHE = "vcell.image.softPixelCache";

	/**
	 * The inflated pixels, held SOFTLY: the collector may reclaim them when the heap is under
	 * pressure, and {@link #getPixels()} re-inflates from {@link #compressedPixels} on demand.
	 * Real geometry images compress 50-100x, so what is retained between uses is the compressed
	 * form -- roughly 1 MB rather than 62 MB for the image in #2021.
	 *
	 * SOFT, deliberately, not weak. A weak reference is cleared at the next GC whatever the heap
	 * looks like, so a geometry in active use would re-inflate on essentially every collection.
	 * Soft references are cleared only under actual memory pressure, and the JVM ages them by
	 * -XX:SoftRefLRUPolicyMSPerMB, which is the behaviour wanted here: free under duress, free of
	 * charge otherwise.
	 */
	private transient SoftReference<byte[]> softPixels = null;

	/** Used instead of {@link #softPixels} when soft caching is switched off. */
	private transient byte[] strongPixels = null;
	private static Logger lg = LogManager.getLogger(VCImageCompressed.class);
/**
 * This method was created in VisualAge.
 * @param vcimage cbit.image.VCImage
 */
public VCImageCompressed(VCImage vcimage) throws ImageException {
	super(vcimage);
	this.compressedPixels = vcimage.getPixelsCompressed().clone();
}

public VCImageCompressed(org.vcell.util.document.Version aVersion, byte pixels[], org.vcell.util.Extent extent, int aNumX, int aNumY, int aNumZ) throws ImageException {
	super(aVersion,extent,aNumX,aNumY,aNumZ);
	this.compressedPixels = pixels;
	initPixelClasses();
	if (lg.isTraceEnabled()) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			String hashCompressed = Hex.toString(digest.digest(compressedPixels));
			lg.trace("Constructor(byte[]): compressed pixels(" + compressedPixels.length + "): hash=" + hashCompressed.substring(0, 6));
		} catch (Exception e) {
		}
	}
}
public void nullifyUncompressedPixels(){
	softPixels = null;
	strongPixels = null;
}

/**
 * getPixels method comment.
 */
public byte[] getPixels() throws ImageException {
	//
	// Take a STRONG local reference first and hold it for the whole method: reading the
	// SoftReference twice would let the collector clear it between the null check and the
	// return, and hand the caller a null array.
	//
	byte[] pixels = strongPixels;
	if (pixels == null){
		SoftReference<byte[]> ref = softPixels;
		if (ref != null){
			pixels = ref.get();
		}
	}
	if (pixels != null){
		return pixels;
	}
	try {
		pixels = VCImage.inflate(compressedPixels,getNumXYZ());
	} catch (IOException | DataFormatException e){
		throw new ImageException(e.getMessage(), e);
	}
	if (PropertyLoader.getBooleanProperty(PROPERTY_SOFT_PIXEL_CACHE, true)){
		softPixels = new SoftReference<byte[]>(pixels);
	} else {
		strongPixels = pixels;
	}
	return pixels;
}
/**
 * This method was created in VisualAge.
 * @return byte[]
 */
public byte[] getPixelsCompressed() {
	return compressedPixels;
}
}
