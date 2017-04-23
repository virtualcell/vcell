/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.solver;

import cbit.vcell.client.data.DataSymbolMetadata;
import cbit.vcell.parser.SymbolTableEntry;


/**
 * Insert the type's description here.
 * Creation date: (9/19/2005 1:26:40 PM)
 * @author: Ion Moraru
 */
public interface SimulationModelInfo {
	
	public interface ModelCategoryType {
		// uses == semantics, must be implemented by an enumeration
		String getName();
		boolean isInitialSelect();
		boolean isEnabled();
	}

	public interface DataSymbolMetadataResolver {
		
		DataSymbolMetadata getDataSymbolMetadata(SymbolTableEntry ste);

		DataSymbolMetadata getDataSymbolMetadata(String symbolName);
		
		ModelCategoryType[] getUniqueFilterCategories();
		
		void populateDataSymbolMetadata();
	}	

	String getContextName();

	String getMembraneName(int subVolumeIdIn, int subVolumeIdOut, boolean bFromGeometry);

	String getSimulationName();

	String getVolumeNamePhysiology(int subVolumeID);

	String getVolumeNameGeometry(int subVolumeID);

	DataSymbolMetadataResolver getDataSymbolMetadataResolver();
}
