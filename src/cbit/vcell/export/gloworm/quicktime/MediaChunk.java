package cbit.vcell.export.gloworm.quicktime;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.vcell.export.gloworm.atoms.SampleDescriptionEntry;


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
 * @return java.lang.Object
 */
String getDataReferenceType();
/**
 * This method was created in VisualAge.
 * @return int
 */
int getDuration();
/**
 * This method was created in VisualAge.
 * @return SampleDescriptionEntry
 */
MediaSampleInfo[] getMediaSampleInfos();
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
 * Insert the method's description here.
 * Creation date: (11/26/2005 7:24:24 PM)
 * @return boolean
 * @param file java.io.File
 */
boolean isDataInFile(java.io.File file);
/**
 * This method was created in VisualAge.
 * @param dataReference java.lang.String
 */
void setDataReference(String dataReference);
/**
 * This method was created in VisualAge.
 * @param dataReference java.lang.String
 */
void setDataReferenceType(String dataReferenceType);
/**
 * This method was created in VisualAge.
 * @param offset int
 */
void setOffset(int offset);
}
