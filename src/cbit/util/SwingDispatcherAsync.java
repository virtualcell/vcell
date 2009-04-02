package cbit.util;

import javax.swing.SwingUtilities;

/**
 * run swing asynchronously by using SwingUtilities.invokeLater
 * <p> since SwingUtilities.invokeLater doesn't throw exception, the caller should call getRunException to 
 * retrieve the exception.
 * @author frm
 *
 */
public abstract class SwingDispatcherAsync {
			
	public abstract void runSwing();
	public abstract void handleException(Throwable ex);
	
	public void dispatch() {
		Runnable runnable = new Runnable(){
			public void run(){
				try {
					runSwing();
				} catch(Throwable e){
					handleException(e);
				}
			}
		};
		SwingUtilities.invokeLater(runnable);
	}
}
