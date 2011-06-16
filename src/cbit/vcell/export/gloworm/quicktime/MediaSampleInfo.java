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
