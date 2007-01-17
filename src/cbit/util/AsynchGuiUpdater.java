package cbit.util;
import java.awt.event.*;
import javax.swing.*;
/**
 * Insert the type's description here.
 * Creation date: (5/19/2004 1:51:35 AM)
 * @author: Ion Moraru
 */

/*

Stateless class to perform non-blocking GUI updates from any thread.
Subclasses should implement one or both of the abstract methods as needed.
Execution is guaranteed to come from the EventDispatch thread and can be done recurringly by Timer superclass and/or by direct user calls.
User calls *must* be done to one of the two final methods (dependening on need for parameters), *not* to the implemented abstract methods.

*/
 
public abstract class AsynchGuiUpdater {
	private Timer timer = null;
	private class Listener implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			guiToDo();
		}
	}

/**
 * AsynchGUIUpdater constructor comment.
 * @param delay int
 * @param listener java.awt.event.ActionListener
 */
public AsynchGuiUpdater() {
	timer = new Timer(100, new AsynchGuiUpdater.Listener());
}


/**
 * Insert the method's description here.
 * Creation date: (5/19/2004 3:09:51 AM)
 */
public int getDelay() {
	return timer.getDelay();
}


/**
 * Insert the method's description here.
 * Creation date: (5/19/2004 1:57:54 AM)
 */

/*

 Implement this with any GUI stuff to be done.
 Will be called by timer or by user thread call to updateNow().
 Can be simple stub if only direct calls with supplied parameters are needed - see updateNow(Object).

*/
 
protected abstract void guiToDo();


/**
 * Insert the method's description here.
 * Creation date: (5/19/2004 1:57:54 AM)
 */

/*

 Implement this with any GUI stuff to be done that needs parameter(s) passed on
 Parameters should be wrapped in any Object user thread should call updateNow(Object).
 Can be simple stub if only timer updates or user calls without supplied parameters are needed - see updateNow(Object).

*/

 
protected abstract void guiToDo(Object params);


/**
 * Insert the method's description here.
 * Creation date: (5/19/2004 3:09:51 AM)
 */
public void restart() {
	timer.restart();
}


/**
 * Insert the method's description here.
 * Creation date: (5/19/2004 3:09:51 AM)
 */
public void setDelay(int millis) {
	timer.setDelay(millis);
}


/**
 * Insert the method's description here.
 * Creation date: (5/19/2004 3:09:51 AM)
 */
public void start() {
	timer.start();
}


/**
 * Insert the method's description here.
 * Creation date: (5/19/2004 3:09:51 AM)
 */
public void stop() {
	timer.stop();
}


/**
 * Insert the method's description here.
 * Creation date: (5/19/2004 1:58:22 AM)
 */
public final void updateNow() {
	SwingUtilities.invokeLater(new Runnable() {
		public void run() {
			AsynchGuiUpdater.this.guiToDo();
		}
	});
}


/**
 * Insert the method's description here.
 * Creation date: (5/19/2004 1:58:22 AM)
 */
public final void updateNow(final Object parameter) {
	SwingUtilities.invokeLater(new Runnable() {
		public void run() {
			AsynchGuiUpdater.this.guiToDo(parameter);
		}
	});
}
}