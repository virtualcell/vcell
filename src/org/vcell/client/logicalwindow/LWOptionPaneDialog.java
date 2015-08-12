package org.vcell.client.logicalwindow;

import java.awt.BorderLayout;
import java.awt.ComponentOrientation;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Objects;

import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * Replacement for {@link JOptionPane#createDialog(java.awt.Component, String)}
 */
@SuppressWarnings("serial")
public abstract class LWOptionPaneDialog extends LWDialog {

	/**
	 * @param parent preferably not null but works ...
	 * @param title null is okay
	 * @param optionPane not null
	 */
	public LWOptionPaneDialog(LWContainerHandle parent, String title, JOptionPane optionPane) {
		super(parent, title);
		Objects.requireNonNull(optionPane);
		JPanel contentPane = new JPanel(new BorderLayout());
		JMenuBar mb = new JMenuBar();
		mb.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		mb.add( LWTopFrame.createWindowMenu(false) );
		contentPane.add(mb, BorderLayout.NORTH);
		contentPane.add(optionPane,BorderLayout.CENTER);
		setContentPane(contentPane);
		pack();
		new OptionPaneHandler(optionPane);
	}

	/**
	 * add the event handlers that {@link JOptionPane#createDialog(String)} does
	 * code pretty much copied from JOptionPane.java source
	 */
	class OptionPaneHandler implements PropertyChangeListener, WindowListener, WindowFocusListener, ComponentListener {
		final JOptionPane jOptionPane;
		private boolean gotFocus = false;

		OptionPaneHandler(JOptionPane jOptionPane) {
			super();
			this.jOptionPane = jOptionPane;
			jOptionPane.addPropertyChangeListener(JOptionPane.VALUE_PROPERTY, this);
			LWOptionPaneDialog.this.addWindowListener(this);
			LWOptionPaneDialog.this.addWindowFocusListener(this);
			LWOptionPaneDialog.this.addComponentListener(this);
		}


		//PropertyChangeListener
		@Override
		public void propertyChange(PropertyChangeEvent event) {
			Object nvalue = event.getNewValue();
			if (nvalue != null && nvalue != JOptionPane.UNINITIALIZED_VALUE) {
				setVisible(false);
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
}
