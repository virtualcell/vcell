/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.mapping;

import cbit.vcell.geometry.SubVolume;
/**
 * This type was created in VisualAge.
 */
public class VolumeStructureAnalyzer extends StructureAnalyzer {
	private SubVolume subVolume = null;
/**
 * VolumeStructureAnalyzer constructor comment.
 * @param mathMapping cbit.vcell.mapping.MathMapping
 * @param subVolume cbit.vcell.geometry.SubVolume
 */
public VolumeStructureAnalyzer(DiffEquMathMapping mathMapping, cbit.vcell.geometry.SubVolume subVolume) {
	super(mathMapping);
	this.subVolume = subVolume;
	//refresh();
}
/**
 * This method was created in VisualAge.
 * @return cbit.vcell.geometry.SubVolume
 */
public SubVolume getSubVolume() {
	return subVolume;
}
/**
 * Build list of structures that are mapped to this volume subdomain
 */
protected void refreshStructures() {
	//
	// get all structures that are mapped to this subvolume (subdomain)
	//
	structures = mathMapping.getSimulationContext().getGeometryContext().getStructuresFromGeometryClass(subVolume);
}
}
