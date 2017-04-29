/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.client;
import java.awt.Component;

import org.vcell.util.document.User;

import cbit.vcell.client.server.SimStatusListener;
import cbit.vcell.client.server.UserPreferences;
import cbit.vcell.export.server.ExportSpecs;
import cbit.vcell.simdata.DataListener;
import cbit.vcell.simdata.OutputContext;
/**
 * Insert the type's description here.
 * Creation date: (11/18/2004 11:25:04 AM)
 * @author: Anuradha Lakshminarayana
 */
public interface DataViewerManager extends cbit.rmi.event.ExportListener, SimStatusListener ,cbit.rmi.event.DataJobListener{
/**
 * Add a cbit.vcell.desktop.controls.DataListener.
 */
public void addDataListener(DataListener newListener);


/**
 * Insert the method's description here.
 * Creation date: (6/1/2004 9:58:46 PM)
 */
public UserPreferences getUserPreferences();

public User getUser();

public RequestManager getRequestManager();

/**
 * Remove a cbit.vcell.desktop.controls.DataListener.
 */
public void removeDataListener(DataListener newListener);

/**
 * Insert the method's description here.
 * Creation date: (6/1/2004 9:58:46 PM)
 */
public void startExport(Component requester,OutputContext outputContext,ExportSpecs exportSpecs);
}
