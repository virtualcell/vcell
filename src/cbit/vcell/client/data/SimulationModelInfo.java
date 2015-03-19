/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.client.data;

import cbit.vcell.client.data.SimulationWorkspaceModelInfo.DataSymbolMetadataResolver;

/**
 * Insert the type's description here.
 * Creation date: (9/19/2005 1:26:40 PM)
 * @author: Ion Moraru
 */
public interface SimulationModelInfo {
/**
 * Insert the method's description here.
 * Creation date: (9/25/2005 11:04:15 AM)
 * @return java.lang.String
 */
String getContextName();


/**
 * Insert the method's description here.
 * Creation date: (9/19/2005 1:28:34 PM)
 * @return java.lang.String
 * @param subVolumeIdIn int
 * @param subVolumeIdOut int
 */
String getMembraneName(int subVolumeIdIn, int subVolumeIdOut, boolean bFromGeometry);


/**
 * Insert the method's description here.
 * Creation date: (9/25/2005 11:18:34 AM)
 * @return java.lang.String
 */
String getSimulationName();


/**
 * Insert the method's description here.
 * Creation date: (9/19/2005 1:29:15 PM)
 * @return java.lang.String
 * @param subVolumeID int
 */
String getVolumeNamePhysiology(int subVolumeID);
String getVolumeNameGeometry(int subVolumeID);

DataSymbolMetadataResolver getDataSymbolMetadataResolver();
}
