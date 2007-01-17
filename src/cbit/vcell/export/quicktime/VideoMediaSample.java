package cbit.vcell.export.quicktime;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.vcell.export.quicktime.atoms.*;
import java.util.zip.*;
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
 * @return cbit.vcell.export.quicktime.atoms.SampleDescriptionEntry
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
