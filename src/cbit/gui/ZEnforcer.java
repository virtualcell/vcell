package cbit.gui;
import javax.swing.Timer;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
/**
 * Insert the type's description here.
 * Creation date: (5/26/2004 9:04:03 PM)
 * @author: Ion Moraru
 */
public class ZEnforcer {
	private static Vector windowStack = new Vector();
	private static WindowAdapter listener = new WindowAdapter() {
		public void windowDeactivated(WindowEvent evt) {
			if (evt.getWindow() == topWindow()) {
				evt.getWindow().toFront();
			}
		}
		public void windowIconified(WindowEvent evt) {
			if (evt.getWindow() == topWindow()) {
				evt.getWindow().show();
			}
		}
		public void windowClosed(WindowEvent evt) {
			removeFromStack(evt.getWindow());
		}
	};
	private static ActionListener timerListener = new ActionListener() {
		public void actionPerformed(ActionEvent a) {
			checkWindowStack();
		}
	};
	private static Timer timer = new Timer(100, timerListener);

/**
 * Insert the method's description here.
 * Creation date: (5/26/2004 9:18:41 PM)
 */
private static void checkWindowStack() {
	Iterator it = windowStack.iterator();
	while (it.hasNext()) {
		Window window = (Window)it.next();
		if (! window.isShowing()) {
			window.removeWindowListener(listener);
			it.remove();
			window.dispose();
			Window currentTop = topWindow();
			if (currentTop != null) {
				currentTop.setEnabled(true);
				topWindow().show();
			}
		}
	}
	if (windowStack.isEmpty()) {
		timer.stop();
	}
}


/**
 * Insert the method's description here.
 * Creation date: (5/26/2004 9:09:45 PM)
 * @param window java.awt.Window
 */

/*
 Removves this window from our control
 */
 
public static void removeFromStack(Window window) {
	if (window != null && windowStack.contains(window)) {
		window.removeWindowListener(listener);
		windowStack.remove(window);
		window.dispose();
		Window currentTop = topWindow();
		if (currentTop != null) {
			currentTop.setEnabled(true);
			topWindow().show();
		}
	}
	if (windowStack.isEmpty()) {
		timer.stop();
	}
}


/**
 * Insert the method's description here.
 * Creation date: (5/26/2004 11:38:45 PM)
 * @param dialog java.awt.Dialog
 */

/*** SO FAR WE ONLY ACCEPT MODAL DIALOGS ***/

public static void showModalDialogOnTop(Dialog dialog) {
	if (dialog.isModal()) {
		showOnTop(dialog, null);
	} else {
		try {
			throw new RuntimeException("ERROR - dialog is not modal: " + dialog);
		} catch (Exception exc) {
			exc.printStackTrace(System.out);
		}
		dialog.show(); // just show it
	}
}


/**
 * Insert the method's description here.
 * Creation date: (5/26/2004 11:38:45 PM)
 * @param dialog java.awt.Dialog
 */

/*** SO FAR WE ONLY ACCEPT MODAL DIALOGS ***/

public static void showModalDialogOnTop(Dialog dialog, Component toBeCenteredOn) {
	if (dialog.isModal()) {
		showOnTop(dialog, toBeCenteredOn);
	} else {
		try {
			throw new RuntimeException("ERROR - dialog is not modal: " + dialog);
		} catch (Exception exc) {
			exc.printStackTrace(System.out);
		}
		dialog.show(); // just show it
	}
}


/**
 * Insert the method's description here.
 * Creation date: (5/26/2004 9:09:45 PM)
 * @param window java.awt.Window
 */

/*
 Will keep this window on top of all others until it is disposed or specifically removed from control via the remove(Window) method
 Method ignores the request if we have it already in our stack of control windows, since:
 	a. it either is already the window maintained on top, or
 	b. other windows have been requested to be on top after the first request for this one, and have not yet been removed/disposed
 Once window is disposed or removed from control, the previous entry in our stack will stay on top, until the stack is empty
 */
 
private static void showOnTop(Window window, Component toBeCenteredOn) {
	if (window != null && (! windowStack.contains(window))) {
		if (topWindow() != null) {
			topWindow().setEnabled(false);
		}
		windowStack.add(window);
		window.addWindowListener(listener);
		window.show();
		cbit.util.BeanUtils.centerOnComponent(window, toBeCenteredOn);
		window.toFront();
		timer.start(); // no need to check isRunning(), Timer checks it anyway...
	}
}


/**
 * Insert the method's description here.
 * Creation date: (5/26/2004 9:18:41 PM)
 * @return java.awt.Window
 */
private static Window topWindow() {
	if (windowStack.isEmpty()) {
		return null;
	} else {
		return (Window)windowStack.lastElement();
	}
}
}