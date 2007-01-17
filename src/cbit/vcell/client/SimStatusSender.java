package cbit.vcell.client;

/**
 * Insert the type's description here.
 * Creation date: (2/10/2004 2:00:22 PM)
 * @author: Fei Gao
 */
public interface SimStatusSender {
public abstract void addSimStatusListener(SimStatusListener listener);
public abstract void removeSimStatusListener(SimStatusListener listener);
}
