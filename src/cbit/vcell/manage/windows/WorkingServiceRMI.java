package cbit.vcell.manage.windows;

/**
 * Insert the type's description here.
 * Creation date: (6/20/2006 12:23:35 PM)
 * @author: Ion Moraru
 */
public class WorkingServiceRMI extends AbstractVCellWorkingService {

/**
 * DaemonService constructor comment.
 */
public WorkingServiceRMI() {
	// must be present for Class.newInstance() to succeed...
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (6/22/2006 1:45:49 PM)
 * @return java.lang.String
 */
public java.lang.String getServiceType() {
	return SERVICE_TYPE_RMI;
}
/**
 * Insert the method's description here.
 * Creation date: (6/23/2006 7:31:30 PM)
 */
void startWork() throws Throwable {

	// TODO

	/* this is how it should really be...

		rmiService = new cbit.vcell.server.RMIServiceMessaging(hostname)
		rmiService.run();

		rmiService should also provide hooks for doing cleanup in stopWork()

	*/

	// for now, the kludge

	String[] args = new String[] {
		hostname,
		"40999", // ignored, overriden by property file
		"messaging",
		"-" // streams already redirected to proper logfile
	};
	cbit.vcell.server.LocalVCellBootstrap.main(args); // this actually returns, since long running thread is spawned off of it
	keepAlive();
	
}
/**
 * Insert the method's description here.
 * Creation date: (6/23/2006 6:29:14 PM)
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
	// could do something maybe...
}
}
