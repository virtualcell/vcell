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

import java.io.*;
/**
 * This type was created in VisualAge.
 */
public class TimeToSampleEntry extends Atoms {
	protected int sampleCount;
	protected int sampleDuration;
/**
 * This method was created in VisualAge.
 * @param count int
 * @param duration int
 */
public TimeToSampleEntry(int count, int duration) {
	sampleCount = count;
	sampleDuration = duration;
}
/**
 * writeData method comment.
 */
public boolean writeData(DataOutputStream out) {
	try {
		out.writeInt(sampleCount);
		out.writeInt(sampleDuration);
		return true;
	} catch (IOException e) {
		System.out.println("Unable to write: " + e.getMessage());
		e.printStackTrace();
		return false;
	}
}
}
