/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.mapping.vcell_4_8;

import java.util.Vector;

import cbit.vcell.geometry.SubVolume;
import cbit.vcell.mapping.MembraneMapping;
import cbit.vcell.model.Membrane;
import cbit.vcell.model.Structure;
/**
 * This type was created in VisualAge.
 */
class VolumeStructureAnalyzer extends StructureAnalyzer {
	private SubVolume subVolume = null;
/**
 * VolumeStructureAnalyzer constructor comment.
 * @param mathMapping_4_8 cbit.vcell.mapping.MathMapping
 * @param subVolume cbit.vcell.geometry.SubVolume
 */
VolumeStructureAnalyzer(MathMapping_4_8 mathMapping_4_8, cbit.vcell.geometry.SubVolume subVolume) {
	super(mathMapping_4_8);
	this.subVolume = subVolume;
	//refresh();
}
/**
 * This method was created in VisualAge.
 * @return cbit.vcell.geometry.SubVolume
 */
SubVolume getSubVolume() {
	return subVolume;
}
/**
 * Build list of structures that are mapped to this volume subdomain
 */
protected void refreshStructures() {
	//
	// get all structures that are mapped to this subvolume (subdomain)
	//
	Structure structs[] = mathMapping_4_8.getStructures(subVolume);
	Vector<Structure> structList = new Vector<Structure>();
	if (structs!=null){
		for (int i=0;i<structs.length;i++){
			//
			// exclude the spatially resolved membrane that is also mapped to this subVolume
			//
			if (structs[i] instanceof Membrane){
				Membrane membrane = (Membrane)structs[i];
				MembraneMapping membraneMapping = (MembraneMapping)mathMapping_4_8.getSimulationContext().getGeometryContext().getStructureMapping(membrane);
				if (mathMapping_4_8.getResolved(membraneMapping)){
					continue;
				}
			}
			//
			// add all others to the structure list
			//
			structList.addElement(structs[i]);
		}
	}

	//
	// make array
	//
	if (structList.size()>0){
		structures = new Structure[structList.size()];
		structList.copyInto(structures);
	}else{
		structures = null;
	}
}
}
