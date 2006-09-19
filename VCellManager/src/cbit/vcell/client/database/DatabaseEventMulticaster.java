package cbit.vcell.client.database;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
/**
 * This is the event multicaster class to support the cbit.vcell.clientdb.DatabaseListenerinterface.
 *
 * WARNING ... BUG IN GENERATED CODE FROM VisualAge for Java !!!!!!!!!
 *
 * This was a generated subclass of AWTEventMulticaster and the remove(EventListener) method was to be 
 * overridden.  Instead, VisualAge for Java generated the method: remove(DatabaseListener) which doesn't
 * override anything ... and causes ClassCastExceptions upon removal of a listener.
 *
 * The fix for this bug is to change the argument type of the generated remove() method to that of java.util.EventListener.
 *
 */
public class DatabaseEventMulticaster extends java.awt.AWTEventMulticaster implements cbit.vcell.client.database.DatabaseListener {
/**
 * Constructor to support multicast events.
 * @param a java.util.EventListener
 * @param b java.util.EventListener
 */
protected DatabaseEventMulticaster(java.util.EventListener a, java.util.EventListener b) {
	super(a, b);
}
/**
 * Add new listener to support multicast events.
 * @return cbit.vcell.clientdb.DatabaseListener
 * @param a cbit.vcell.clientdb.DatabaseListener
 * @param b cbit.vcell.clientdb.DatabaseListener
 */
public static cbit.vcell.client.database.DatabaseListener add(cbit.vcell.client.database.DatabaseListener a, cbit.vcell.client.database.DatabaseListener b) {
	return (cbit.vcell.client.database.DatabaseListener)addInternal(a, b);
}
/**
 * Add new listener to support multicast events.
 * @return java.util.EventListener
 * @param a java.util.EventListener
 * @param b java.util.EventListener
 */
protected static java.util.EventListener addInternal(java.util.EventListener a, java.util.EventListener b) {
	if (a == null)  return b;
	if (b == null)  return a;
	return new DatabaseEventMulticaster(a, b);
}
/**
 * 
 * @param event cbit.vcell.clientdb.DatabaseEvent
 */
public void databaseDelete(cbit.vcell.client.database.DatabaseEvent event) {
	((cbit.vcell.client.database.DatabaseListener)a).databaseDelete(event);
	((cbit.vcell.client.database.DatabaseListener)b).databaseDelete(event);
}
/**
 * 
 * @param event cbit.vcell.clientdb.DatabaseEvent
 */
public void databaseInsert(cbit.vcell.client.database.DatabaseEvent event) {
	((cbit.vcell.client.database.DatabaseListener)a).databaseInsert(event);
	((cbit.vcell.client.database.DatabaseListener)b).databaseInsert(event);
}
/**
 * 
 * @param event cbit.vcell.clientdb.DatabaseEvent
 */
public void databaseRefresh(cbit.vcell.client.database.DatabaseEvent event) {
	((cbit.vcell.client.database.DatabaseListener)a).databaseRefresh(event);
	((cbit.vcell.client.database.DatabaseListener)b).databaseRefresh(event);
}
/**
 * 
 * @param event cbit.vcell.clientdb.DatabaseEvent
 */
public void databaseUpdate(cbit.vcell.client.database.DatabaseEvent event) {
	((cbit.vcell.client.database.DatabaseListener)a).databaseUpdate(event);
	((cbit.vcell.client.database.DatabaseListener)b).databaseUpdate(event);
}
/**
 * Remove listener to support multicast events.
 * @return cbit.vcell.clientdb.DatabaseListener
 * @param l cbit.vcell.clientdb.DatabaseListener
 * @param oldl cbit.vcell.clientdb.DatabaseListener
 */
public static cbit.vcell.client.database.DatabaseListener remove(cbit.vcell.client.database.DatabaseListener l, cbit.vcell.client.database.DatabaseListener oldl) {
	if (l == oldl || l == null)
		return null;
	if(l instanceof DatabaseEventMulticaster)
		return (cbit.vcell.client.database.DatabaseListener)((cbit.vcell.client.database.DatabaseEventMulticaster) l).remove(oldl);
	return l;
}
/**
 * 
 * @return java.util.EventListener
 * @param oldl cbit.vcell.clientdb.DatabaseListener
 */
protected java.util.EventListener remove(java.util.EventListener oldl) {
	if (oldl == a)  return b;
	if (oldl == b)  return a;
	java.util.EventListener a2 = removeInternal(a, oldl);
	java.util.EventListener b2 = removeInternal(b, oldl);
	if (a2 == a && b2 == b)
		return this;
	return addInternal(a2, b2);
}
}
