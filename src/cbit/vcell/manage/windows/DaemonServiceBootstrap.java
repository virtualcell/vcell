package cbit.vcell.manage.windows;
/**
 * Insert the type's description here.
 * Creation date: (6/20/2006 12:23:35 PM)
 * @author: Ion Moraru
 */
public class DaemonServiceBootstrap extends AbstractVCellDaemonService {
/**
 * DaemonService constructor comment.
 */
public DaemonServiceBootstrap() {
	// must be present for Class.newInstance() to succeed...
	super();
}


/**
 * Insert the method's description here.
 * Creation date: (6/22/2006 2:12:11 PM)
 * @return java.lang.String
 */
public java.lang.String getDaemonType() {
	return DAEMON_TYPE_BOOTSTRAP;
}


/**
 * Insert the method's description here.
 * Creation date: (6/22/2006 2:12:11 PM)
 */
void startWork() throws Throwable {
	startMonitorThread();
	Class bootstrapDaemonClass = Class.forName("cbit.vcell.messaging.admin.BootstrapDaemon");
	try {
		daemon = bootstrapDaemonClass.getConstructor(new Class[] {String.class}).newInstance(new String[] {getName()});
		java.lang.reflect.Method startMethod = daemon.getClass().getMethod("start", null);
		startMethod.invoke(daemon, null);
	} catch (java.lang.reflect.InvocationTargetException exc) {
		// unwrap it
		throw exc.getTargetException();
	}
}


/**
 * Insert the method's description here.
 * Creation date: (6/22/2006 2:12:11 PM)
 */
void stopWork(int stopType) throws Throwable {
	// TODO
	switch (stopType) {
		case STOP_SERVICE: {			
			// do something specific?
		}
		case STOP_SHUTDOWN: {			
			// do something specific?
		}
	}
	// try to stop all dependent services nicely...
	if (initSuccess) {
		stopDependentServices();
	}
}
	}