package cbit.vcell.resource;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public class VCellExecutorService {
	private static ScheduledExecutorService service = null;
	private static int counter = 1; 
	private static final String IDENT = "VCellExecutorService";
	private static ThreadGroup tgroup;

	
	public static ScheduledExecutorService get( ) {
		if (service != null) {
			return service;
		}
		tgroup = new ThreadGroup(IDENT);
		service = new ScheduledThreadPoolExecutor(3, (r) -> newThread(r));
		return service;
	}
	
	private VCellExecutorService() { }
	
	/**
	 * implements ThreadFactory 
	 * @param r
	 * @return new Thread
	 */
	private static Thread newThread(Runnable r) {
		Thread t = new Thread(tgroup,r, IDENT + "-thread-"+counter++);
		t.setDaemon(true);
		return t;
	}
	
	
	
	
}
