/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */
package org.vcell.inversepde.microscopy;

import org.vcell.util.Matchable;

public class OriginalGlobalScaleInfo implements Matchable {
	private final int originalGlobalScaledMin;
	private final int originalGlobalScaledMax;
	private final double originalScaleFactor;
	private final double originalOffsetFactor;
	public OriginalGlobalScaleInfo(
		int originalGlobalScaledMin,int originalGlobalScaledMax,
		double originalScaleFactor,double originalOffsetFactor){
		this.originalGlobalScaledMin = originalGlobalScaledMin;
		this.originalGlobalScaledMax = originalGlobalScaledMax;
		this.originalScaleFactor = originalScaleFactor;
		this.originalOffsetFactor = originalOffsetFactor;
	}
	public int getOriginalGlobalScaledMin() {
		return originalGlobalScaledMin;
	}
	public int getOriginalGlobalScaledMax() {
		return originalGlobalScaledMax;
	}
	public double getOriginalScaleFactor() {
		return originalScaleFactor;
	}
	public double getOriginalOffsetFactor() {
		return originalOffsetFactor;
	}
	public boolean compareEqual(Matchable obj) {
		if (obj instanceof OriginalGlobalScaleInfo){
			OriginalGlobalScaleInfo info = (OriginalGlobalScaleInfo)obj;
			if (originalGlobalScaledMin != info.originalGlobalScaledMin){
				return false;
			}
			if (originalGlobalScaledMax != info.originalGlobalScaledMax){
				return false;
			}
			if (originalOffsetFactor != info.originalOffsetFactor){
				return false;
			}
			if (originalScaleFactor != info.originalScaleFactor){
				return false;
			}
			return true;
		}
		return false;
	}
	public void setOriginalGlobalScaleInfo(OriginalGlobalScaleInfo originalGlobalScaleInfo) {
	}
	public OriginalGlobalScaleInfo getOriginalGlobalScaleInfo() {
		return null;
	}
}