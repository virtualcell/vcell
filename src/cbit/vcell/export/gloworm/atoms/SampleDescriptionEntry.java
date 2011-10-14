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
