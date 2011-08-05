/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.microscopy;

public class DataVerifyInfo 
{
	private double startingTime;
	private double endingTime;
	private double imageSizeX;
	private double imageSizeY;
	private int startTimeIndex;
	private int endTimeIndex;
	
	public DataVerifyInfo(double sTime, double eTime, double imgSizeX, double imgSizeY, int sIndex, int eIndex)
	{
		startingTime = sTime;
		endingTime = eTime;
		imageSizeX = imgSizeX;
		imageSizeY = imgSizeY;
		startTimeIndex = sIndex;
		endTimeIndex = eIndex; 
	}

	public double getStartingTime() {
		return startingTime;
	}
	public double getEndingTime() {
		return endingTime;
	}
	public double getImageSizeX() {
		return imageSizeX;
	}
	public double getImageSizeY() {
		return imageSizeY;
	}
	public int getStartTimeIndex() {
		return startTimeIndex;
	}
	public int getEndTimeIndex() {
		return endTimeIndex;
	}
}
