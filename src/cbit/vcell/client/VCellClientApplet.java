package cbit.vcell.client;
import java.awt.*;
import javax.swing.*;

import org.vcell.util.BeanUtils;

import cbit.vcell.desktop.*;
import cbit.util.*;
import cbit.vcell.client.server.*;
/**
 * Insert the type's description here.
 * Creation date: (6/6/2004 12:13:45 AM)
 * @author: Ion Moraru
 */
public class VCellClientApplet extends JApplet {
	private boolean inited = false;
	private JTextArea textArea = null;
	private JLabel label = null;
	private VCellMainSplashScreen splash = null;
	private JWindow splashWindow = null;
	private VCellClient client = null;

/**
 * Insert the method's description here.
 * Creation date: (6/6/2004 3:08:22 PM)
 */
private void createAppletGui() {
	try {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	} catch (Exception exc) {
		handleException(exc);
	}
	setTextArea(new JTextArea("The Virtual Cell will start in a separate window\n"));
	getTextArea().setEditable(false);
	getTextArea().setFont(getTextArea().getFont().deriveFont(Font.BOLD));
	setLabel(new JLabel("initializing..."));
	JScrollPane scroller = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	scroller.getViewport().add(textArea);
	getContentPane().add(scroller, BorderLayout.CENTER);
	getContentPane().add(getLabel(), BorderLayout.SOUTH);
	setSize(500, 200);
}

/**
 * This method was created in VisualAge.
 */
public void destroy() {
	// ignored
	// we mimick application-like behavior, user should choose exit from application
}


/**
 * Insert the method's description here.
 * Creation date: (6/6/2004 4:55:24 PM)
 * @return cbit.vcell.client.VCellClient
 */
VCellClient getClient() {
	return client;
}


/**
 * Insert the method's description here.
 * Creation date: (6/6/2004 3:42:41 PM)
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getLabel() {
	return label;
}


/**
 * Insert the method's description here.
 * Creation date: (6/6/2004 7:48:48 PM)
 * @return cbit.vcell.desktop.VCellMainSplashScreen
 */
private cbit.vcell.desktop.VCellMainSplashScreen getSplash() {
	return splash;
}


/**
 * Insert the method's description here.
 * Creation date: (6/6/2004 7:52:16 PM)
 * @return javax.swing.JWindow
 */
javax.swing.JWindow getSplashWindow() {
	return splashWindow;
}


/**
 * Insert the method's description here.
 * Creation date: (6/6/2004 3:42:41 PM)
 * @return javax.swing.JTextArea
 */
private javax.swing.JTextArea getTextArea() {
	return textArea;
}


/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(Throwable exception) {

	if (getTextArea() != null) {
		getTextArea().setText(getTextArea().getText()+"\n"+exception);
	}

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	System.out.println("--------- UNCAUGHT EXCEPTION --------- in cbit.vcell.client.VCellClientApplet");
	exception.printStackTrace(System.out);
}


/**
 * Handle the Applet init method.
 */
public void init() {
	// some browsers call this more then once, so
	if (! inited) {
		// start the applet gui
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				public void run() {
					createAppletGui();
				}
			});
		} catch (Throwable exc) {
			exc.printStackTrace(System.out);
		}
		inited = true;
	}
	if (inited) setVisible(true);
}

/**
 * Insert the method's description here.
 * Creation date: (6/6/2004 4:55:24 PM)
 * @param newClient cbit.vcell.client.VCellClient
 */
void setClient(VCellClient newClient) {
	client = newClient;
}


/**
 * Insert the method's description here.
 * Creation date: (6/6/2004 3:42:41 PM)
 * @param newLabel javax.swing.JLabel
 */
private void setLabel(javax.swing.JLabel newLabel) {
	label = newLabel;
}


/**
 * Insert the method's description here.
 * Creation date: (6/6/2004 7:48:48 PM)
 * @param newSplash cbit.vcell.desktop.VCellMainSplashScreen
 */
 private void setSplash(cbit.vcell.desktop.VCellMainSplashScreen newSplash) {
	splash = newSplash;
}


/**
 * Insert the method's description here.
 * Creation date: (6/6/2004 7:52:16 PM)
 * @param newSplashWindow javax.swing.JWindow
 */
private void setSplashWindow(javax.swing.JWindow newSplashWindow) {
	splashWindow = newSplashWindow;
}


/**
 * Insert the method's description here.
 * Creation date: (6/6/2004 3:42:41 PM)
 * @param newTextArea javax.swing.JTextArea
 */
private void setTextArea(javax.swing.JTextArea newTextArea) {
	textArea = newTextArea;
}


/**
 * Insert the method's description here.
 * Creation date: (6/6/2004 5:25:22 PM)
 * @param status java.lang.String
 */
public void showStatus(String status) {
	super.showStatus(status);
	getLabel().setText(status);
}


/**
 * This method was created in VisualAge.
 */
public void start() {
	// only the first time
	// we mimick application-like behavior, launch once and stay until exit while JVM is up
	if (inited && getClient() == null) {
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				public void run() {
					try {
						// show a splash screen
						setSplash(new VCellMainSplashScreen());
						setSplashWindow(new JWindow());
						getSplashWindow().setContentPane(getSplash());
						getSplashWindow().pack();
						BeanUtils.centerOnScreen(getSplashWindow());
						getSplashWindow().show();
						// pass the ball to the real client
						VCellClient.startClientFromApplet(VCellClientApplet.this);
					} catch (Throwable exc) {
						handleException(exc);
					}
				}
			});
		} catch (Throwable exc) {
			handleException(exc);
		}
	}
}


/**
 * This method was created in VisualAge.
 */
public void stop() {
	// ignored
	// we mimick application-like behavior, user should choose exit from application
}
}