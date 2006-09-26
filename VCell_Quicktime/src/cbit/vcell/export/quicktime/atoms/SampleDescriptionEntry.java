package cbit.vcell.export.quicktime.atoms;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.vcell.export.quicktime.*;
import java.io.*;
/**
 * This type was created in VisualAge.
 */
public abstract class SampleDescriptionEntry extends Atoms {
	protected String dataFormat;
	protected byte[] reserved = new byte[6];
	protected short dataReferenceIndex;
/**
 * This method was created in VisualAge.
 * @param newValue int
 */
public void setDataReferenceIndex(int newValue) {
	this.dataReferenceIndex = (short)newValue;
}
}
