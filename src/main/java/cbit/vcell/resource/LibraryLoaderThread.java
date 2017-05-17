package cbit.vcell.resource;

import javax.swing.JOptionPane;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.vcell.util.BeanUtils;

/**
 * Start and monitor Monitor library loading in thread, show error message when done
 * if exception
 */
public class LibraryLoaderThread extends Thread {
	private final boolean isGui;
	
	private static Logger lg = Logger.getLogger(LibraryLoaderThread.class);
	
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
				if (lg.isDebugEnabled()) {
					lg.debug("loading " + librarySet + " " + librarySet.autoload);
				}
				if (librarySet.autoload) {
					librarySet.load( ); //blocks until done
					if (lg.isDebugEnabled()) {
						lg.debug("completed " + librarySet);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				if (sb == null) {
					sb = new StringBuilder("Unable to load runtime libraries for ");
					sb.append(newline);
				}
				sb.append(librarySet);
				sb.append(":  ");
				sb.append(BeanUtils.getMessageRecursive(e));
				sb.append(newline);
				if (isGui){
					BeanUtils.sendErrorReport(e, sb.toString());
				
				}
			} 
		}
		if (sb != null) {
			if (isGui) {
			if (lg.isEnabledFor(Level.WARN)) {
				lg.warn("scheduling display of " + sb.toString());
			}
			// Suppress modal dialog warning for now
			// SwingUtilities.invokeLater(new Reporter(sb.toString()));
			}
			else {
				if (lg.isDebugEnabled()) {
					lg.debug("printing error " + sb.toString());
				}
				System.err.println(sb.toString( ));
			}
		}
		lg.debug("library loading complete");
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
