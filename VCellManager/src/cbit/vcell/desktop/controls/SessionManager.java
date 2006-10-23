package cbit.vcell.desktop.controls;

/**
 * Insert the type's description here.
 * Creation date: (5/25/2004 12:50:01 PM)
 * @author: Ion Moraru
 */
public interface SessionManager {
/**
 * Insert the method's description here.
 * Creation date: (5/25/2004 12:50:55 PM)
 * @return cbit.vcell.server.User
 */
cbit.util.document.User getUser();
/**
 * Insert the method's description here.
 * Creation date: (5/25/2004 12:51:14 PM)
 * @return cbit.vcell.server.UserMetaDbServer
 */
cbit.vcell.server.UserMetaDbServer getUserMetaDbServer() throws cbit.util.DataAccessException;
}
