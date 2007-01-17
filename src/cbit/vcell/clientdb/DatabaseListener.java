package cbit.vcell.clientdb;

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
