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

import org.vcell.util.DataAccessException;

import cbit.vcell.client.server.PDEDataManager;
import cbit.vcell.simdata.*;
/**
 * Insert the type's description here.
 * Creation date: (6/13/2004 3:36:26 PM)
 * @author: Ion Moraru
 */
public class NewClientPDEDataContext extends ClientPDEDataContext {
/**
 * NewClientPDEDataContext constructor comment.
 * @param dataManager cbit.vcell.desktop.controls.DataManager
 */
public NewClientPDEDataContext(PDEDataManager dataManager) {
	super(dataManager);
}


/**
 * This method was created in VisualAge.
 *
 * @param exportSpec cbit.vcell.export.server.ExportSpecs
 */
public void makeRemoteFile(cbit.vcell.export.server.ExportSpecs exportSpecs) throws org.vcell.util.DataAccessException {
	throw new RuntimeException("should not use this method in NewClientPDEDataContext");
}


/**
 * Insert the method's description here.
 * Creation date: (10/3/00 5:03:43 PM)
 */
public void refreshIdentifiers() {
	try {
		DataIdentifier[] newDataIdentifiers = getDataManager().getDataIdentifiers();
		setDataIdentifiers(newDataIdentifiers);
	} catch (DataAccessException exc) {
		exc.printStackTrace(System.out);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (10/3/00 5:03:43 PM)
 */
public void refreshTimes() throws org.vcell.util.DataAccessException {
	setTimePoints(getDataManager().getDataSetTimes());
}
}
