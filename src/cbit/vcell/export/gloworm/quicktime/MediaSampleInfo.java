package cbit.vcell.export.gloworm.quicktime;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.vcell.export.gloworm.atoms.*;
/**
 * This type was created in VisualAge.
 */
public interface MediaSampleInfo {
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
String getDataFormat();


/**
 * This method was created in VisualAge.
 * @return int
 */
int getDuration();


/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
String getMediaType();


/**
 * This method was created in VisualAge.
 * @return SampleDescriptionEntry
 */
SampleDescriptionEntry getSampleDescriptionEntry();


/**
 * This method was created in VisualAge.
 * @return int
 */
int getSize();


/**
 * This method was created in VisualAge.
 * @return boolean
 */
boolean isKeyFrame();
}