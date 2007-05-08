package cbit.vcell.manage.windows;
/**
 * Insert the type's description here.
 * Creation date: (6/20/2006 12:23:35 PM)
 * @author: Ion Moraru
 */
public class DaemonServiceManager extends AbstractVCellDaemonService {
/**
 * DaemonService constructor comment.
 */
public DaemonServiceManager() {
	// must be present for Class.newInstance() to succeed...
	super();
}


/**
 * Insert the method's description here.
 * Creation date: (6/23/2006 7:35:02 PM)
 */
public java.lang.String getDaemonType() {
	return DAEMON_TYPE_MANAGER;
}


/**
 * Insert the method's description here.
 * Creation date: (6/23/2006 7:33:02 PM)
 */
void startWork() throws java.lang.Throwable {
	startMonitorThread();
	Class managerDaemonClass = Class.forName("cbit.vcell.messaging.admin.ServerManagerDaemon");
	try {
		daemon = managerDaemonClass.getConstructor(new Class[] {String.class}).newInstance(new String[] {getName()});
		java.lang.reflect.Method startMethod = daemon.getClass().getMethod("start", null);
		startMethod.invoke(daemon, null);
	} catch (java.lang.reflect.InvocationTargetException exc) {
		// unwrap it
		throw exc.getTargetException();
	}
}


/**
 * Insert the method's description here.
 * Creation date: (6/23/2006 7:33:02 PM)
 */
void stopWork(int stopType) throws java.lang.Throwable {
	switch (stopType) {
		case STOP_SERVICE: {			
			// do something specific?
		}
		case STOP_SHUTDOWN: {			
			// do something specific?
		}
	}
	// try to stop all services nicely...
	if (initSuccess) {
		// since all services are typically on remote hosts, find out how many we have and give use some time
		java.lang.reflect.Field vcServerInfoField = daemon.getClass().getSuperclass().getDeclaredField("vcServerInfo");  // it's on the superclass, and is protected...
		vcServerInfoField.setAccessible(true);
		java.lang.reflect.Method getServiceListMethod = vcServerInfoField.get(daemon).getClass().getMethod("getServiceList", null);
		java.util.ArrayList arrayList = (java.util.ArrayList)getServiceListMethod.invoke(vcServerInfoField.get(daemon), null);
		setStopTimeout(arrayList.size() * 30000); // 30 seconds per service (ignored if called by shutdown...)
		// now try stopping them
		stopDependentServices();
	}
}
}