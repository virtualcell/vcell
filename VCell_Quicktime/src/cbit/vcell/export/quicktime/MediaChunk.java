package cbit.vcell.export.quicktime;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.vcell.export.quicktime.atoms.*;
/**
 * This type was created in VisualAge.
 */
public interface MediaChunk {
/**
 * This method was created in VisualAge.
 * @return java.lang.Byte[]
 */
byte[] getDataBytes();
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
String getDataFormat();
/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 */
String getDataReference();
/**
 * This method was created in VisualAge.
 * @return int
 */
int getDuration();
/**
 * This method was created in VisualAge.
 * @return cbit.vcell.export.quicktime.MediaSample[]
 */
MediaSample[] getMediaSamples();
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
String getMediaType();
/**
 * This method was created in VisualAge.
 * @return int
 */
int getNumberOfSamples();
/**
 * This method was created in VisualAge.
 * @return int
 */
int getOffset();
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
 * @param dataReference java.lang.String
 */
void setDataReference(String dataReference);
/**
 * This method was created in VisualAge.
 * @param offset int
 */
void setOffset(int offset);
}
