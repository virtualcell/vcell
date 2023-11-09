package cbit.vcell.resource;

import javax.swing.JOptionPane;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.util.StackTraceUtils;

/**
 * Start and monitor Monitor library loading in thread, show error message when done
 * if exception
 */
public class LibraryLoaderThread extends Thread {
	private final static Logger lg = LogManager.getLogger(LibraryLoaderThread.class);
	private final boolean isGui;
	
	private static Logger logger = LogManager.getLogger(LibraryLoaderThread.class);
	
	/**
	 * execute thread
	 * @param hasGui if true, GUI (Swing available)
	 */
	public LibraryLoaderThread(boolean hasGui ) {
		super("LibraryLoader");
		this.isGui = hasGui;
		setDaemon(true);
	}

	@Override
	public void run() {
		char newline = '\n';
		StringBuilder sb = null;
		for (NativeLib librarySet :NativeLib.values( )) {
			try {
				
				if (librarySet.autoLoad) {
					logger.debug("loading " + librarySet + " " + librarySet.autoLoad);
					librarySet.load(); //blocks until done
					logger.debug("completed " + librarySet);
					
				}
			} catch (Exception e) {
				lg.error(e.getMessage(), e);
				if (sb == null) {
					sb = new StringBuilder("Unable to load runtime libraries for ");
					sb.append(newline);
				}
				sb.append(librarySet);
				sb.append(":  ");
				sb.append(StackTraceUtils.getCausalChain(e));
				sb.append(newline);
				if (isGui){
					ErrorUtils.sendErrorReport(e, sb.toString());
				
				}
			} 
		}
		if (sb != null) {
			if (isGui) {
			if (logger.isWarnEnabled()) {
				logger.warn("scheduling display of " + sb.toString());
			}
			// Suppress modal dialog warning for now
			// SwingUtilities.invokeLater(new Reporter(sb.toString()));
			}
			else {
				if (logger.isDebugEnabled()) {
					logger.debug("printing error " + sb.toString());
				}
				System.err.println(sb.toString( ));
			}
		}
		logger.debug("library loading complete");
	}
	
	/**
	 * show exception dialog (on Swing thread)
	 */
	private static class Reporter implements Runnable {
		final String msg;

		public Reporter(String msg) {
			this.msg = msg;
		}

		@Override
		public void run() {
			JOptionPane.showMessageDialog(null,msg,"Error loading libraries",JOptionPane.ERROR_MESSAGE);
		}
	}
}
