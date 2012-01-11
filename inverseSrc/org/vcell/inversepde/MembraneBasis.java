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

public class MembraneBasis extends SpatialBasis {

	private VolumeBasis adjacentInsideVolumeBases;
	private String insideSubvolumeName;
	private String outsideSubvolumeName;
	
	public VolumeBasis getAdjacentInsideVolumeBases() {
		return adjacentInsideVolumeBases;
	}
	public void setAdjacentInsideVolumeBases(VolumeBasis adjacentInsideVolumeBases) {
		this.adjacentInsideVolumeBases = adjacentInsideVolumeBases;
	}
	public String getInsideSubvolumeName() {
		return insideSubvolumeName;
	}
	public void setInsideSubvolumeName(String insideSubvolumeName) {
		this.insideSubvolumeName = insideSubvolumeName;
	}
	public String getOutsideSubvolumeName() {
		return outsideSubvolumeName;
	}
	public void setOutsideSubvolumeName(String outsideSubvolumeName) {
		this.outsideSubvolumeName = outsideSubvolumeName;
	}
	
	public boolean compareEqual(Matchable obj) {
		if (obj instanceof VolumeBasis){
			MembraneBasis mb = (MembraneBasis)obj;
			if (!super.compareEqual0(mb)){
				return false;
			}
			if (!Compare.isEqualOrNull(adjacentInsideVolumeBases, mb.adjacentInsideVolumeBases)){
				return false;
			}
			if (!Compare.isEqualOrNull(insideSubvolumeName,mb.insideSubvolumeName)){
				return false;
			}
			if (!Compare.isEqualOrNull(outsideSubvolumeName,mb.outsideSubvolumeName)){
				return false;
			}
			return true;
		}
		return false;
	}

	public String getReport(){
		StringBuffer buffer = new StringBuffer();
		buffer.append(super.getReport());
		buffer.append(", insideVolName="+insideSubvolumeName+", outsideVolName="+outsideSubvolumeName);
		buffer.append(", adjacentInsideVolume="+((adjacentInsideVolumeBases!=null)?(adjacentInsideVolumeBases.getName()):("null")));
		return buffer.toString();
	}
}
