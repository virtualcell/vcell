package cbit.vcell.solver.ode.gui;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
/**
 * Insert the type's description here.
 * Creation date: (11/2/2000 4:45:38 PM)
 * @author: 
 */
public class TimeBoundsPanel extends javax.swing.JPanel implements java.awt.event.FocusListener, java.beans.PropertyChangeListener {
	private cbit.vcell.solver.TimeBounds fieldTimeBounds = null;
	private boolean ivjConnPtoP1Aligning = false;
	private cbit.vcell.solver.TimeBounds ivjTornOffTimeBounds = null;
	private javax.swing.JLabel ivjEndingTimeLabel = null;
	private javax.swing.JTextField ivjEndingTimeTextField = null;
	private javax.swing.JLabel ivjStartingTimeLabel = null;
	private javax.swing.JTextField ivjStartingTimeTextField = null;
	private javax.swing.JLabel ivjTimeBoundsLabel = null;
	private cbit.vcell.solver.TimeBounds ivjTimeBoundsFactory = null;
/**
 * TimeBoundsPanel constructor comment.
 */
public TimeBoundsPanel() {
	super();
	initialize();
}
/**
 * TimeBoundsPanel constructor comment.
 * @param layout java.awt.LayoutManager
 */
public TimeBoundsPanel(java.awt.LayoutManager layout) {
	super(layout);
}
/**
 * TimeBoundsPanel constructor comment.
 * @param layout java.awt.LayoutManager
 * @param isDoubleBuffered boolean
 */
public TimeBoundsPanel(java.awt.LayoutManager layout, boolean isDoubleBuffered) {
	super(layout, isDoubleBuffered);
}
/**
 * TimeBoundsPanel constructor comment.
 * @param isDoubleBuffered boolean
 */
public TimeBoundsPanel(boolean isDoubleBuffered) {
	super(isDoubleBuffered);
}
/**
 * connEtoM1:  (TornOffTimeBounds.this --> StartingTimeTextField.text)
 * @param value cbit.vcell.solver.TimeBounds
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM1(cbit.vcell.solver.TimeBounds value) {
	try {
		// user code begin {1}
		// user code end
		if ((getTornOffTimeBounds() != null)) {
			getStartingTimeTextField().setText(String.valueOf(getTornOffTimeBounds().getStartingTime()));
		}
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoM2:  (TornOffTimeBounds.this --> EndingTimeTextField.text)
 * @param value cbit.vcell.solver.TimeBounds
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM2(cbit.vcell.solver.TimeBounds value) {
	try {
		// user code begin {1}
		// user code end
		if ((getTornOffTimeBounds() != null)) {
			getEndingTimeTextField().setText(String.valueOf(getTornOffTimeBounds().getEndingTime()));
		}
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoM3:  (EndingTimeTextField.focus.focusLost(java.awt.event.FocusEvent) --> TornOffTimeBounds.this)
 * @param arg1 java.awt.event.FocusEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM3(java.awt.event.FocusEvent arg1) {
	cbit.vcell.solver.TimeBounds localValue = null;
	try {
		// user code begin {1}
		// user code end
		setTornOffTimeBounds(localValue = new cbit.vcell.solver.TimeBounds(new Double(getStartingTimeTextField().getText()).doubleValue(), new Double(getEndingTimeTextField().getText()).doubleValue()));
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
	setTimeBoundsFactory(localValue);
}
/**
 * connEtoM4:  (StartingTimeTextField.focus.focusLost(java.awt.event.FocusEvent) --> TornOffTimeBounds.this)
 * @param arg1 java.awt.event.FocusEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM4(java.awt.event.FocusEvent arg1) {
	cbit.vcell.solver.TimeBounds localValue = null;
	try {
		// user code begin {1}
		// user code end
		setTornOffTimeBounds(localValue = new cbit.vcell.solver.TimeBounds(new Double(getStartingTimeTextField().getText()).doubleValue(), new Double(getEndingTimeTextField().getText()).doubleValue()));
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
	setTimeBoundsFactory(localValue);
}
/**
 * connPtoP1SetSource:  (TimeBoundsPanel.timeBounds <--> TornOffTimeBounds.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP1SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP1Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP1Aligning = true;
			if ((getTornOffTimeBounds() != null)) {
				this.setTimeBounds(getTornOffTimeBounds());
			}
			// user code begin {2}
			// user code end
			ivjConnPtoP1Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP1Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connPtoP1SetTarget:  (TimeBoundsPanel.timeBounds <--> TornOffTimeBounds.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP1SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP1Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP1Aligning = true;
			setTornOffTimeBounds(this.getTimeBounds());
			// user code begin {2}
			// user code end
			ivjConnPtoP1Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP1Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * Method to handle events for the FocusListener interface.
 * @param e java.awt.event.FocusEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void focusGained(java.awt.event.FocusEvent e) {
	// user code begin {1}
	// user code end
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the FocusListener interface.
 * @param e java.awt.event.FocusEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void focusLost(java.awt.event.FocusEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getEndingTimeTextField()) 
		connEtoM3(e);
	if (e.getSource() == getStartingTimeTextField()) 
		connEtoM4(e);
	// user code begin {2}
	// user code end
}
/**
 * Return the EndingTimeLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getEndingTimeLabel() {
	if (ivjEndingTimeLabel == null) {
		try {
			ivjEndingTimeLabel = new javax.swing.JLabel();
			ivjEndingTimeLabel.setName("EndingTimeLabel");
			ivjEndingTimeLabel.setText("Ending");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjEndingTimeLabel;
}
/**
 * Return the EndingTimeTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getEndingTimeTextField() {
	if (ivjEndingTimeTextField == null) {
		try {
			ivjEndingTimeTextField = new javax.swing.JTextField();
			ivjEndingTimeTextField.setName("EndingTimeTextField");
			ivjEndingTimeTextField.setPreferredSize(new java.awt.Dimension(4, 21));
			ivjEndingTimeTextField.setColumns(10);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjEndingTimeTextField;
}
/**
 * Return the StartingTimeLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getStartingTimeLabel() {
	if (ivjStartingTimeLabel == null) {
		try {
			ivjStartingTimeLabel = new javax.swing.JLabel();
			ivjStartingTimeLabel.setName("StartingTimeLabel");
			ivjStartingTimeLabel.setText("Starting");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjStartingTimeLabel;
}
/**
 * Return the StartingTimeTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getStartingTimeTextField() {
	if (ivjStartingTimeTextField == null) {
		try {
			ivjStartingTimeTextField = new javax.swing.JTextField();
			ivjStartingTimeTextField.setName("StartingTimeTextField");
			ivjStartingTimeTextField.setText("");
			ivjStartingTimeTextField.setEditable(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjStartingTimeTextField;
}
/**
 * Gets the timeBounds property (cbit.vcell.solver.TimeBounds) value.
 * @return The timeBounds property value.
 * @see #setTimeBounds
 */
public cbit.vcell.solver.TimeBounds getTimeBounds() {
	return fieldTimeBounds;
}
/**
 * Return the TimeBoundsFactory property value.
 * @return cbit.vcell.solver.TimeBounds
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.solver.TimeBounds getTimeBoundsFactory() {
	// user code begin {1}
	// user code end
	return ivjTimeBoundsFactory;
}
/**
 * Return the JLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getTimeBoundsLabel() {
	if (ivjTimeBoundsLabel == null) {
		try {
			ivjTimeBoundsLabel = new javax.swing.JLabel();
			ivjTimeBoundsLabel.setName("TimeBoundsLabel");
			ivjTimeBoundsLabel.setText("Time Bounds");
			ivjTimeBoundsLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjTimeBoundsLabel;
}
/**
 * Return the TornOffTimeBounds property value.
 * @return cbit.vcell.solver.TimeBounds
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.solver.TimeBounds getTornOffTimeBounds() {
	// user code begin {1}
	// user code end
	return ivjTornOffTimeBounds;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	System.out.println("--------- UNCAUGHT EXCEPTION ---------");
	exception.printStackTrace(System.out);
	org.vcell.util.gui.DialogUtils.showWarningDialog(this, "Error in Starting or Ending TimeBounds value : " + exception.getMessage(), new String[] {"Ok"}, "Ok");
}
/**
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	this.addPropertyChangeListener(this);
	getEndingTimeTextField().addFocusListener(this);
	getStartingTimeTextField().addFocusListener(this);
	connPtoP1SetTarget();
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("TimeBoundsPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(160, 120);

		java.awt.GridBagConstraints constraintsTimeBoundsLabel = new java.awt.GridBagConstraints();
		constraintsTimeBoundsLabel.gridx = 1; constraintsTimeBoundsLabel.gridy = 0;
		constraintsTimeBoundsLabel.gridwidth = 2;
		constraintsTimeBoundsLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsTimeBoundsLabel.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getTimeBoundsLabel(), constraintsTimeBoundsLabel);

		java.awt.GridBagConstraints constraintsStartingTimeLabel = new java.awt.GridBagConstraints();
		constraintsStartingTimeLabel.gridx = 1; constraintsStartingTimeLabel.gridy = 1;
		constraintsStartingTimeLabel.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getStartingTimeLabel(), constraintsStartingTimeLabel);

		java.awt.GridBagConstraints constraintsEndingTimeLabel = new java.awt.GridBagConstraints();
		constraintsEndingTimeLabel.gridx = 1; constraintsEndingTimeLabel.gridy = 2;
		constraintsEndingTimeLabel.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getEndingTimeLabel(), constraintsEndingTimeLabel);

		java.awt.GridBagConstraints constraintsEndingTimeTextField = new java.awt.GridBagConstraints();
		constraintsEndingTimeTextField.gridx = 2; constraintsEndingTimeTextField.gridy = 2;
		constraintsEndingTimeTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsEndingTimeTextField.weightx = 1.0;
		constraintsEndingTimeTextField.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getEndingTimeTextField(), constraintsEndingTimeTextField);

		java.awt.GridBagConstraints constraintsStartingTimeTextField = new java.awt.GridBagConstraints();
		constraintsStartingTimeTextField.gridx = 2; constraintsStartingTimeTextField.gridy = 1;
		constraintsStartingTimeTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsStartingTimeTextField.weightx = 1.0;
		constraintsStartingTimeTextField.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getStartingTimeTextField(), constraintsStartingTimeTextField);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		TimeBoundsPanel aTimeBoundsPanel;
		aTimeBoundsPanel = new TimeBoundsPanel();
		frame.setContentPane(aTimeBoundsPanel);
		frame.setSize(aTimeBoundsPanel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		frame.show();
		java.awt.Insets insets = frame.getInsets();
		frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JPanel");
		exception.printStackTrace(System.out);
	}
}
/**
 * Method to handle events for the PropertyChangeListener interface.
 * @param evt java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void propertyChange(java.beans.PropertyChangeEvent evt) {
	// user code begin {1}
	// user code end
	if (evt.getSource() == this && (evt.getPropertyName().equals("timeBounds"))) 
		connPtoP1SetTarget();
	// user code begin {2}
	// user code end
}
/**
 * Enables or disables this component, depending on the value of the
 * parameter <code>b</code>. An enabled component can respond to user
 * input and generate events. Components are enabled initially by default.
 * @param     b   If <code>true</code>, this component and all its children
 *            are enabled; otherwise they are disabled.
 */
public void setEnabled(boolean b) {
	super.setEnabled(b);
	getTimeBoundsLabel().setEnabled(b);
	getStartingTimeLabel().setEnabled(b);
	getStartingTimeTextField().setEnabled(b);
	getEndingTimeLabel().setEnabled(b);
	getEndingTimeTextField().setEnabled(b);
}
/**
 * Sets the timeBounds property (cbit.vcell.solver.TimeBounds) value.
 * @param timeBounds The new value for the property.
 * @exception java.beans.PropertyVetoException The exception description.
 * @see #getTimeBounds
 */
public void setTimeBounds(cbit.vcell.solver.TimeBounds timeBounds) throws java.beans.PropertyVetoException {
	cbit.vcell.solver.TimeBounds oldValue = fieldTimeBounds;
	fireVetoableChange("timeBounds", oldValue, timeBounds);
	fieldTimeBounds = timeBounds;
	firePropertyChange("timeBounds", oldValue, timeBounds);
}
/**
 * Set the TimeBoundsFactory to a new value.
 * @param newValue cbit.vcell.solver.TimeBounds
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setTimeBoundsFactory(cbit.vcell.solver.TimeBounds newValue) {
	if (ivjTimeBoundsFactory != newValue) {
		try {
			cbit.vcell.solver.TimeBounds oldValue = getTimeBoundsFactory();
			ivjTimeBoundsFactory = newValue;
			firePropertyChange("timeBounds", oldValue, newValue);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	};
	// user code begin {3}
	// user code end
}
/**
 * Set the TornOffTimeBounds to a new value.
 * @param newValue cbit.vcell.solver.TimeBounds
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setTornOffTimeBounds(cbit.vcell.solver.TimeBounds newValue) {
	if (ivjTornOffTimeBounds != newValue) {
		try {
			cbit.vcell.solver.TimeBounds oldValue = getTornOffTimeBounds();
			ivjTornOffTimeBounds = newValue;
			connPtoP1SetSource();
			connEtoM1(ivjTornOffTimeBounds);
			connEtoM2(ivjTornOffTimeBounds);
			firePropertyChange("timeBounds", oldValue, newValue);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	};
	// user code begin {3}
	// user code end
}

}
