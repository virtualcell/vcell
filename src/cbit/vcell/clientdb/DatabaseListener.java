/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.clientdb;

/**
 * The event set listener interface for the database feature.
 */
public interface DatabaseListener extends java.util.EventListener {
/**
 * 
 * @param event cbit.vcell.clientdb.DatabaseEvent
 */
void databaseDelete(cbit.vcell.clientdb.DatabaseEvent event);
/**
 * 
 * @param event cbit.vcell.clientdb.DatabaseEvent
 */
void databaseInsert(cbit.vcell.clientdb.DatabaseEvent event);
/**
 * 
 * @param event cbit.vcell.clientdb.DatabaseEvent
 */
void databaseRefresh(cbit.vcell.clientdb.DatabaseEvent event);
/**
 * 
 * @param event cbit.vcell.clientdb.DatabaseEvent
 */
void databaseUpdate(cbit.vcell.clientdb.DatabaseEvent event);
}
