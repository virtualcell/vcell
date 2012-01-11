/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */
package org.vcell.inversepde;

import org.vcell.util.Compare;
import org.vcell.util.Matchable;


public class VolumeBasis extends SpatialBasis {

	// consider using ROIImageComponent and and SubVolume instances rather than handle and name.....
	//
	private int basisImageValue; // e.g. color in bases image. ... lookup into ROIImage to get the corresponding ROIImageComponent
	private String subvolumeName; // which subvolume does this belong to.
	
	public int getBasisImageValue() {
		return basisImageValue;
	}
	public void setBasisImageValue(int basisImageValue) {
		this.basisImageValue = basisImageValue;
	}
	public String getSubvolumeName() {
		return subvolumeName;
	}
	public void setSubvolumeName(String subvolumeName) {
		this.subvolumeName = subvolumeName;
	}

	public boolean compareEqual(Matchable obj) {
		if (obj instanceof VolumeBasis){
			VolumeBasis vb = (VolumeBasis)obj;
			if (!super.compareEqual0(vb)){
				return false;
			}
			if (basisImageValue!=vb.basisImageValue){
				return false;
			}
			if (!Compare.isEqualOrNull(subvolumeName, vb.subvolumeName)){
				return false;
			}
			return true;
		}
		return false;
	}

	public String getReport(){
		StringBuffer buffer = new StringBuffer();
		buffer.append(super.getReport());
		buffer.append(", subvolumeName="+subvolumeName+", basisImageValue="+basisImageValue);
		return buffer.toString();
	}
}
