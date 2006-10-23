package cbit.vcell.messaging.server;
import cbit.util.MessageConstants;
import cbit.util.PropertyLoader;

/**
 * Insert the type's description here.
 * Creation date: (12/17/2003 8:59:18 AM)
 * @author: Fei Gao
 */
public class PropertyLoaderThread extends Thread {
/**
 * PropertyLoaderThread constructor comment.
 */
public PropertyLoaderThread() {
	super();
}


/**
 * PropertyLoaderThread constructor comment.
 * @param target java.lang.Runnable
 */
public PropertyLoaderThread(Runnable target) {
	super(target);
}


/**
 * PropertyLoaderThread constructor comment.
 * @param target java.lang.Runnable
 * @param name java.lang.String
 */
public PropertyLoaderThread(Runnable target, String name) {
	super(target, name);
}


/**
 * PropertyLoaderThread constructor comment.
 * @param name java.lang.String
 */
public PropertyLoaderThread(String name) {
	super(name);
}


/**
 * PropertyLoaderThread constructor comment.
 * @param group java.lang.ThreadGroup
 * @param target java.lang.Runnable
 */
public PropertyLoaderThread(ThreadGroup group, Runnable target) {
	super(group, target);
}


/**
 * PropertyLoaderThread constructor comment.
 * @param group java.lang.ThreadGroup
 * @param target java.lang.Runnable
 * @param name java.lang.String
 */
public PropertyLoaderThread(ThreadGroup group, Runnable target, String name) {
	super(group, target, name);
}


/**
 * PropertyLoaderThread constructor comment.
 * @param group java.lang.ThreadGroup
 * @param name java.lang.String
 */
public PropertyLoaderThread(ThreadGroup group, String name) {
	super(group, name);
}


/**
 * Insert the method's description here.
 * Creation date: (12/17/2003 8:59:45 AM)
 */
public void run() {
	while (true){
		//
		// sleep for a while
		//
		try {
			Thread.sleep(20 * MessageConstants.MINUTE);
		} catch (InterruptedException e){
		}
		
		//
		// re-read the property file
		//
		System.out.println("re-read the property file");
		try {
			PropertyLoader.loadProperties();
		} catch (Throwable e){
			e.printStackTrace(System.out);
		}
	}		
}
}