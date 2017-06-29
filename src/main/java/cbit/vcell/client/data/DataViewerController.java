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

import java.beans.PropertyChangeListener;

import org.vcell.util.DataAccessException;

import cbit.vcell.simdata.DataListener;

/**
 * Insert the type's description here.
 * Creation date: (6/11/2004 1:51:53 PM)
 * @author: Ion Moraru
 */
public interface DataViewerController extends DataListener, PropertyChangeListener {
/**
 * Insert the method's description here.
 * Creation date: (6/11/2004 2:33:04 PM)
 * @return javax.swing.JPanel
 */
DataViewer createViewer() throws DataAccessException;

}
