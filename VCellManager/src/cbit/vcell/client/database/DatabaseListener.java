package cbit.vcell.client.database;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
/**
 * The event set listener interface for the database feature.
 */
public interface DatabaseListener extends java.util.EventListener {
/**
 * 
 * @param event cbit.vcell.clientdb.DatabaseEvent
 */
void databaseDelete(cbit.vcell.client.database.DatabaseEvent event);
/**
 * 
 * @param event cbit.vcell.clientdb.DatabaseEvent
 */
void databaseInsert(cbit.vcell.client.database.DatabaseEvent event);
/**
 * 
 * @param event cbit.vcell.clientdb.DatabaseEvent
 */
void databaseRefresh(cbit.vcell.client.database.DatabaseEvent event);
/**
 * 
 * @param event cbit.vcell.clientdb.DatabaseEvent
 */
void databaseUpdate(cbit.vcell.client.database.DatabaseEvent event);
}
