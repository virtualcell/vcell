package cbit.vcell.desktop.controls;

/**
 * Insert the type's description here.
 * Creation date: (6/25/2002 8:22:15 AM)
 * @author: Jim Schaff
 */
public interface ClientTask {
	public final static int TASKTYPE_NONSWING_BLOCKING = 1;
	public final static int TASKTYPE_SWING_NONBLOCKING = 2;
	public final static int TASKTYPE_SWING_BLOCKING = 3;
/**
 * Insert the method's description here.
 * Creation date: (6/25/2002 2:08:24 PM)
 * @return java.lang.String
 */
String getTaskName();
/**
 * Insert the method's description here.
 * Creation date: (6/25/2002 8:27:08 AM)
 * @return int
 */
int getTaskType();
/**
 * Insert the method's description here.
 * Creation date: (6/25/2002 8:23:50 AM)
 * @param hashTable java.util.Hashtable
 * @param clientWorker cbit.vcell.desktop.controls.ClientWorker
 */
void run(java.util.Hashtable hashTable) throws Exception;
}
