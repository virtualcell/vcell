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

import org.vcell.util.Compare;
import org.vcell.util.Matchable;


public class MicroscopyData implements Matchable {

	private AnnotatedImageDataset_inv psfImageData = null;
	private AnnotatedImageDataset_inv timeSeriesImageData = null;
	private AnnotatedImageDataset_inv zStackImageData = null;

	public boolean compareEqual(Matchable obj) {
		if (obj instanceof MicroscopyData){
			MicroscopyData ip = (MicroscopyData)obj;
			if (!Compare.isEqualOrNull(getTimeSeriesImageData(), ip.getTimeSeriesImageData())){
				return false;
			}
			if (!Compare.isEqualOrNull(getZStackImageData(), ip.getZStackImageData())){
				return false;
			}
			if (!Compare.isEqualOrNull(getPsfImageData(), ip.getPsfImageData())){
				return false;
			}
			return true;
		}
		return false;
	}

	public AnnotatedImageDataset_inv getPsfImageData() {
		return psfImageData;
	}

	public void setPsfImageData(AnnotatedImageDataset_inv psfImageData) {
		this.psfImageData = psfImageData;
	}

	public AnnotatedImageDataset_inv getTimeSeriesImageData() {
		return timeSeriesImageData;
	}

	public void setTimeSeriesImageData(AnnotatedImageDataset_inv timeSeriesImageData) {
		this.timeSeriesImageData = timeSeriesImageData;
	}

	public AnnotatedImageDataset_inv getZStackImageData() {
		return zStackImageData;
	}

	public void setZStackImageData(AnnotatedImageDataset_inv stackImageData) {
		zStackImageData = stackImageData;
	}


}
