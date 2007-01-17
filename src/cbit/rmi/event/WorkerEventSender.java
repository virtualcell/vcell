package cbit.rmi.event;
/**
 * Insert the type's description here.
 * Creation date: (2/10/2004 2:00:22 PM)
 * @author: Fei Gao
 */
public interface WorkerEventSender {
public abstract void addWorkerEventListener(WorkerEventListener listener);


public abstract void removeWorkerEventListener(WorkerEventListener listener);
}