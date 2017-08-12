package org.vcell.client.logicalwindow;

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JOptionPane;

/**
 * add the event handlers that {@link JOptionPane#createDialog(String)} does
 * code pretty much copied from JOptionPane.java source
 */
class OptionPaneHandler implements PropertyChangeListener, WindowListener, WindowFocusListener, ComponentListener {
	final JOptionPane jOptionPane;
	private boolean gotFocus = false;
	private final LWFrameOrDialog container;
	

	public OptionPaneHandler(LWFrameOrDialog container, JOptionPane jOptionPane) {
		this.jOptionPane = jOptionPane;
		this.container = container;
		jOptionPane.addPropertyChangeListener(JOptionPane.VALUE_PROPERTY, this);
		container.addWindowListener(this);
		container.addWindowFocusListener(this);
		container.addComponentListener(this);
	}


	//PropertyChangeListener
	@Override
	public void propertyChange(PropertyChangeEvent event) {
		Object nvalue = event.getNewValue();
		if (nvalue != null && nvalue != JOptionPane.UNINITIALIZED_VALUE) {
			container.setVisible(false);
		}
	}

	//WindowListener
	@Override
	public void windowClosing(WindowEvent we) {
		jOptionPane.setValue(null);
	}

	@Override
	public void windowClosed(WindowEvent e) {
		jOptionPane.removePropertyChangeListener(this);
		jOptionPane.removeAll();
	}
	

	//WindowFocusListener
	@Override
	public void windowGainedFocus(WindowEvent e) {
		if (!gotFocus) {
			jOptionPane.selectInitialValue();
			gotFocus = true;
		}
	}

	//ComponentListener
	@Override
	public void componentShown(ComponentEvent ce) {
		// reset value to ensure closing works properly
		jOptionPane.setValue(JOptionPane.UNINITIALIZED_VALUE);
	}
	
	//events we don't care about
	public void windowLostFocus(WindowEvent e) { }
	public void windowOpened(WindowEvent e) { }
	public void windowIconified(WindowEvent e) { }
	public void windowDeiconified(WindowEvent e) { }
	public void windowActivated(WindowEvent e) { }
	public void windowDeactivated(WindowEvent e) { }
	public void componentResized(ComponentEvent e) { }
	public void componentMoved(ComponentEvent e) { }
	public void componentHidden(ComponentEvent e) { }
}