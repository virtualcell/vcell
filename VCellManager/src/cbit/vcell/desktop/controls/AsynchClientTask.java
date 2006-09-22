package cbit.vcell.desktop.controls;
import cbit.util.UserCancelException;

/**
 * Insert the type's description here.
 * Creation date: (5/31/2004 6:09:07 PM)
 * @author: Ion Moraru
 */
public abstract class AsynchClientTask implements cbit.vcell.desktop.controls.ClientTask {
/**
 * Insert the method's description here.
 * Creation date: (5/31/2004 7:18:33 PM)
 * @return boolean
 */
public abstract boolean skipIfAbort();


/**
 * Insert the method's description here.
 * Creation date: (5/31/2004 7:18:33 PM)
 * @return boolean
 */
public abstract boolean skipIfCancel(UserCancelException exc);
}