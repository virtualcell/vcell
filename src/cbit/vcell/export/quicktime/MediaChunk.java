package cbit.vcell.export.quicktime;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.io.IOException;
import java.io.OutputStream;

import cbit.vcell.export.quicktime.atoms.*;
/**
 * This type was created in VisualAge.
 */
public interface MediaChunk {
void writeBytes(OutputStream out)  throws IOException;
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
