package cbit.vcell.solver.ode.gui;

import cbit.vcell.solver.TimeStep;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
/**
 * Insert the type's description here.
 * Creation date: (11/2/2000 4:46:05 PM)
 * @author: 
 */
public class TimeStepPanel extends javax.swing.JPanel implements java.awt.event.FocusListener, java.beans.PropertyChangeListener {
	private cbit.vcell.solver.TimeStep fieldTimeStep = null;
	private boolean ivjConnPtoP1Aligning = false;
	private javax.swing.JLabel ivjDefaultTimeStepLabel = null;
	private javax.swing.JTextField ivjDefaultTimeStepTextField = null;
	private javax.swing.JLabel ivjMaximumTimeStepLabel = null;
	private javax.swing.JTextField ivjMaximumTimeStepTextField = null;
	private javax.swing.JLabel ivjMinimumTimeStepLabel = null;
	private javax.swing.JTextField ivjMinimumTimeStepTextField = null;
	private javax.swing.JLabel ivjTimeStepLabel = null;
	private TimeStep ivjTornOffTimeStep = null;
	private TimeStep ivjTimeStepFactory = null;
/**
 * TimeStepPanel constructor comment.
 */
public TimeStepPanel() {
	super();
	initialize();
}
/**
 * TimeStepPanel constructor comment.
 * @param layout java.awt.LayoutManager
 */
public TimeStepPanel(java.awt.LayoutManager layout) {
	super(layout);
}
/**
 * TimeStepPanel constructor comment.
 * @param layout java.awt.LayoutManager
 * @param isDoubleBuffered boolean
 */
public TimeStepPanel(java.awt.LayoutManager layout, boolean isDoubleBuffered) {
	super(layout, isDoubleBuffered);
}
/**
 * TimeStepPanel constructor comment.
 * @param isDoubleBuffered boolean
 */
public TimeStepPanel(boolean isDoubleBuffered) {
	super(isDoubleBuffered);
}
/**
 * connEtoM1:  (MinimumTimeStepTextField.focus.focusLost(java.awt.event.FocusEvent) --> TornOffTimeStep.this)
 * @param arg1 java.awt.event.FocusEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM1(java.awt.event.FocusEvent arg1) {
	TimeStep localValue = null;
	try {
		// user code begin {1}
		// user code end
		setTornOffTimeStep(localValue = readTimeStep());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
	setTimeStepFactory(localValue);
}
/**
 * connEtoM2:  (DefaultTimeStepTextField.focus.focusLost(java.awt.event.FocusEvent) --> TornOffTimeStep.this)
 * @param arg1 java.awt.event.FocusEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM2(java.awt.event.FocusEvent arg1) {
	TimeStep localValue = null;
	try {
		// user code begin {1}
		// user code end
		setTornOffTimeStep(localValue = readTimeStep());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
	setTimeStepFactory(localValue);
}

private TimeStep readTimeStep() {
	double defaultTimeStep = !getDefaultTimeStepTextField().isEnabled() ? 0 : new Double(getDefaultTimeStepTextField().getText()).doubleValue();
	double minTimeStep = !getMinimumTimeStepTextField().isEnabled() ? defaultTimeStep : new Double(getMinimumTimeStepTextField().getText()).doubleValue();
	double maxTimeStep = !getMaximumTimeStepTextField().isEnabled() ? defaultTimeStep : new Double(getMaximumTimeStepTextField().getText()).doubleValue();
	return new TimeStep(minTimeStep, defaultTimeStep, maxTimeStep);
}

/**
 * connEtoM3:  (MaximumTimeStepTextField.focus.focusLost(java.awt.event.FocusEvent) --> TornOffTimeStep.this)
 * @param arg1 java.awt.event.FocusEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM3(java.awt.event.FocusEvent arg1) {
	TimeStep localValue = null;
	try {
		// user code begin {1}
		// user code end
		setTornOffTimeStep(localValue = readTimeStep());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
	setTimeStepFactory(localValue);
}
/**
 * connEtoM4:  (TornOffTimeStep.this --> MinimumTimeStepTextField.text)
 * @param value cbit.vcell.solver.TimeStep
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM4(TimeStep value) {
	try {
		// user code begin {1}
		// user code end
		if ((getTornOffTimeStep() != null) && getMinimumTimeStepTextField().isEnabled()) {
			getMinimumTimeStepTextField().setText(String.valueOf(getTornOffTimeStep().getMinimumTimeStep()));
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
 * connEtoM5:  (TornOffTimeStep.this --> DefaultTimeStepTextField.text)
 * @param value cbit.vcell.solver.TimeStep
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM5(TimeStep value) {
	try {
		// user code begin {1}
		// user code end
		if ((getTornOffTimeStep() != null) && getDefaultTimeStepTextField().isEnabled()) {
			getDefaultTimeStepTextField().setText(String.valueOf(getTornOffTimeStep().getDefaultTimeStep()));
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
 * connEtoM6:  (TornOffTimeStep.this --> MaximumTimeStepTextField.text)
 * @param value cbit.vcell.solver.TimeStep
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM6(TimeStep value) {
	try {
		// user code begin {1}
		// user code end
		if ((getTornOffTimeStep() != null) && getMaximumTimeStepTextField().isEnabled()) {
			getMaximumTimeStepTextField().setText(String.valueOf(getTornOffTimeStep().getMaximumTimeStep()));
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
 * connPtoP1SetSource:  (TimeStepPanel.timeStep <--> TornOffTimeStep.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP1SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP1Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP1Aligning = true;
			if ((getTornOffTimeStep() != null)) {
				this.setTimeStep(getTornOffTimeStep());
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
 * connPtoP1SetTarget:  (TimeStepPanel.timeStep <--> TornOffTimeStep.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP1SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP1Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP1Aligning = true;
			setTornOffTimeStep(this.getTimeStep());
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
 * Disable minmum, maximum and default time steps.
 * Creation date: (10/4/2006 5:10:02 PM)
 */
public void disableTimeStep() 
{
	getMinimumTimeStepLabel().setEnabled(false);
	//getMinimumTimeStepTextField().setText("");
	getMinimumTimeStepTextField().setEnabled(false);
	getDefaultTimeStepLabel().setEnabled(false);
	//getDefaultTimeStepTextField().setText("");
	getDefaultTimeStepTextField().setEnabled(false);
	getMaximumTimeStepLabel().setEnabled(false);
	//getMaximumTimeStepTextField().setText("");
	getMaximumTimeStepTextField().setEnabled(false);

}

/**
 * Disable minmum, maximum time steps,If only default time step is needed.
 * Creation date: (7/13/2007)
 */
public void disableMinAndMaxTimeStep() 
{
	getMinimumTimeStepLabel().setEnabled(false);
	getMinimumTimeStepTextField().setText("");
	getMinimumTimeStepTextField().setEnabled(false);
	getMaximumTimeStepLabel().setEnabled(false);
	getMaximumTimeStepTextField().setText("");
	getMaximumTimeStepTextField().setEnabled(false);
}

public void disableMinTimeStep() 
{
	getMinimumTimeStepLabel().setEnabled(false);
	getMinimumTimeStepTextField().setText("");
	getMinimumTimeStepTextField().setEnabled(false);
}

/**
 * Insert the method's description here.
 * Creation date: (10/23/2005 11:51:27 AM)
 * @param variableTimeStep boolean
 */
public void enableComponents(boolean variableTimeStep) {
	if (variableTimeStep) {
		getMinimumTimeStepLabel().setEnabled(true);
		getMinimumTimeStepTextField().setEnabled(true);
		getMinimumTimeStepTextField().setText(getTimeStep().getMinimumTimeStep()+"");
		getDefaultTimeStepLabel().setEnabled(false);
		getDefaultTimeStepTextField().setText("");
		getDefaultTimeStepTextField().setEnabled(false);
		getMaximumTimeStepLabel().setEnabled(true);
		getMaximumTimeStepTextField().setEnabled(true);
		getMaximumTimeStepTextField().setText(getTimeStep().getMaximumTimeStep()+"");
	} else {
		getMinimumTimeStepLabel().setEnabled(false);
		getMinimumTimeStepTextField().setText("");
		getMinimumTimeStepTextField().setEnabled(false);
		getDefaultTimeStepLabel().setEnabled(true);
		getDefaultTimeStepTextField().setEnabled(true);
		getDefaultTimeStepTextField().setText(getTimeStep().getDefaultTimeStep()+"");
		getMaximumTimeStepLabel().setEnabled(false);
		getMaximumTimeStepTextField().setText("");
		getMaximumTimeStepTextField().setEnabled(false);
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
	if (e.getSource() == getMinimumTimeStepTextField()) 
		connEtoM1(e);
	if (e.getSource() == getDefaultTimeStepTextField()) 
		connEtoM2(e);
	if (e.getSource() == getMaximumTimeStepTextField()) 
		connEtoM3(e);
	// user code begin {2}
	// user code end
}
/**
 * Return the DefaultTimeStepLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getDefaultTimeStepLabel() {
	if (ivjDefaultTimeStepLabel == null) {
		try {
			ivjDefaultTimeStepLabel = new javax.swing.JLabel();
			ivjDefaultTimeStepLabel.setName("DefaultTimeStepLabel");
			ivjDefaultTimeStepLabel.setText("Default");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDefaultTimeStepLabel;
}
/**
 * Return the DefaultTimeStepTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getDefaultTimeStepTextField() {
	if (ivjDefaultTimeStepTextField == null) {
		try {
			ivjDefaultTimeStepTextField = new javax.swing.JTextField();
			ivjDefaultTimeStepTextField.setName("DefaultTimeStepTextField");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDefaultTimeStepTextField;
}
/**
 * Return the MaximumTimeStepLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getMaximumTimeStepLabel() {
	if (ivjMaximumTimeStepLabel == null) {
		try {
			ivjMaximumTimeStepLabel = new javax.swing.JLabel();
			ivjMaximumTimeStepLabel.setName("MaximumTimeStepLabel");
			ivjMaximumTimeStepLabel.setText("Maximum");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMaximumTimeStepLabel;
}
/**
 * Return the MaximumTimeStepTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getMaximumTimeStepTextField() {
	if (ivjMaximumTimeStepTextField == null) {
		try {
			ivjMaximumTimeStepTextField = new javax.swing.JTextField();
			ivjMaximumTimeStepTextField.setName("MaximumTimeStepTextField");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMaximumTimeStepTextField;
}
/**
 * Return the MinimumTimeStepLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getMinimumTimeStepLabel() {
	if (ivjMinimumTimeStepLabel == null) {
		try {
			ivjMinimumTimeStepLabel = new javax.swing.JLabel();
			ivjMinimumTimeStepLabel.setName("MinimumTimeStepLabel");
			ivjMinimumTimeStepLabel.setText("Minimum");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMinimumTimeStepLabel;
}
/**
 * Return the MinimumTimeStepTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getMinimumTimeStepTextField() {
	if (ivjMinimumTimeStepTextField == null) {
		try {
			ivjMinimumTimeStepTextField = new javax.swing.JTextField();
			ivjMinimumTimeStepTextField.setName("MinimumTimeStepTextField");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMinimumTimeStepTextField;
}
/**
 * Gets the timeStep property (cbit.vcell.solver.TimeStep) value.
 * @return The timeStep property value.
 * @see #setTimeStep
 */
public TimeStep getTimeStep() {
	return fieldTimeStep;
}
/**
 * Return the TimeStepFactory property value.
 * @return cbit.vcell.solver.TimeStep
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private TimeStep getTimeStepFactory() {
	// user code begin {1}
	// user code end
	return ivjTimeStepFactory;
}
/**
 * Return the TimeStepLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getTimeStepLabel() {
	if (ivjTimeStepLabel == null) {
		try {
			ivjTimeStepLabel = new javax.swing.JLabel();
			ivjTimeStepLabel.setName("TimeStepLabel");
			ivjTimeStepLabel.setText("Time Step");
			ivjTimeStepLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjTimeStepLabel;
}
/**
 * Return the TornOffTimeStep property value.
 * @return cbit.vcell.solver.TimeStep
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private TimeStep getTornOffTimeStep() {
	// user code begin {1}
	// user code end
	return ivjTornOffTimeStep;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	 System.out.println("--------- UNCAUGHT EXCEPTION ---------");
	 exception.printStackTrace(System.out);
	org.vcell.util.gui.DialogUtils.showWarningDialog(this, "Possible error in TimeStep value : " + exception.getMessage(), new String[] {"Ok"}, "Ok");
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
	getMinimumTimeStepTextField().addFocusListener(this);
	getDefaultTimeStepTextField().addFocusListener(this);
	getMaximumTimeStepTextField().addFocusListener(this);
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
		setName("TimeStepPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(175, 120);

		java.awt.GridBagConstraints constraintsTimeStepLabel = new java.awt.GridBagConstraints();
		constraintsTimeStepLabel.gridx = 0; constraintsTimeStepLabel.gridy = 0;
		constraintsTimeStepLabel.gridwidth = 2;
		constraintsTimeStepLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsTimeStepLabel.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getTimeStepLabel(), constraintsTimeStepLabel);

		java.awt.GridBagConstraints constraintsMinimumTimeStepLabel = new java.awt.GridBagConstraints();
		constraintsMinimumTimeStepLabel.gridx = 0; constraintsMinimumTimeStepLabel.gridy = 1;
		constraintsMinimumTimeStepLabel.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getMinimumTimeStepLabel(), constraintsMinimumTimeStepLabel);

		java.awt.GridBagConstraints constraintsDefaultTimeStepLabel = new java.awt.GridBagConstraints();
		constraintsDefaultTimeStepLabel.gridx = 0; constraintsDefaultTimeStepLabel.gridy = 2;
		constraintsDefaultTimeStepLabel.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getDefaultTimeStepLabel(), constraintsDefaultTimeStepLabel);

		java.awt.GridBagConstraints constraintsMaximumTimeStepLabel = new java.awt.GridBagConstraints();
		constraintsMaximumTimeStepLabel.gridx = 0; constraintsMaximumTimeStepLabel.gridy = 3;
		constraintsMaximumTimeStepLabel.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getMaximumTimeStepLabel(), constraintsMaximumTimeStepLabel);

		java.awt.GridBagConstraints constraintsMinimumTimeStepTextField = new java.awt.GridBagConstraints();
		constraintsMinimumTimeStepTextField.gridx = 1; constraintsMinimumTimeStepTextField.gridy = 1;
		constraintsMinimumTimeStepTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsMinimumTimeStepTextField.weightx = 1.0;
		constraintsMinimumTimeStepTextField.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getMinimumTimeStepTextField(), constraintsMinimumTimeStepTextField);

		java.awt.GridBagConstraints constraintsDefaultTimeStepTextField = new java.awt.GridBagConstraints();
		constraintsDefaultTimeStepTextField.gridx = 1; constraintsDefaultTimeStepTextField.gridy = 2;
		constraintsDefaultTimeStepTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsDefaultTimeStepTextField.weightx = 1.0;
		constraintsDefaultTimeStepTextField.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getDefaultTimeStepTextField(), constraintsDefaultTimeStepTextField);

		java.awt.GridBagConstraints constraintsMaximumTimeStepTextField = new java.awt.GridBagConstraints();
		constraintsMaximumTimeStepTextField.gridx = 1; constraintsMaximumTimeStepTextField.gridy = 3;
		constraintsMaximumTimeStepTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsMaximumTimeStepTextField.weightx = 1.0;
		constraintsMaximumTimeStepTextField.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getMaximumTimeStepTextField(), constraintsMaximumTimeStepTextField);
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
		TimeStepPanel aTimeStepPanel;
		aTimeStepPanel = new TimeStepPanel();
		frame.setContentPane(aTimeStepPanel);
		frame.setSize(aTimeStepPanel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		frame.setVisible(true);
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
	if (evt.getSource() == this && (evt.getPropertyName().equals("timeStep"))) 
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
	getTimeStepLabel().setEnabled(b);
	getMinimumTimeStepLabel().setEnabled(b);
	getMinimumTimeStepTextField().setEnabled(b);
	getDefaultTimeStepLabel().setEnabled(b);
	getDefaultTimeStepTextField().setEnabled(b);
	getMaximumTimeStepLabel().setEnabled(b);
	getMaximumTimeStepTextField().setEnabled(b);
}
/**
 * Sets the timeStep property (cbit.vcell.solver.TimeStep) value.
 * @param timeStep The new value for the property.
 * @exception java.beans.PropertyVetoException The exception description.
 * @see #getTimeStep
 */
public void setTimeStep(TimeStep timeStep) throws java.beans.PropertyVetoException {
	TimeStep oldValue = fieldTimeStep;
	fireVetoableChange("timeStep", oldValue, timeStep);
	fieldTimeStep = timeStep;
	firePropertyChange("timeStep", oldValue, timeStep);
}
/**
 * Set the TimeStepFactory to a new value.
 * @param newValue cbit.vcell.solver.TimeStep
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setTimeStepFactory(TimeStep newValue) {
	if (ivjTimeStepFactory != newValue) {
		try {
			TimeStep oldValue = getTimeStepFactory();
			ivjTimeStepFactory = newValue;
			firePropertyChange("timeStep", oldValue, newValue);
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
 * Set the TornOffTimeStep to a new value.
 * @param newValue cbit.vcell.solver.TimeStep
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setTornOffTimeStep(TimeStep newValue) {
	if (ivjTornOffTimeStep != newValue) {
		try {
			TimeStep oldValue = getTornOffTimeStep();
			ivjTornOffTimeStep = newValue;
			connPtoP1SetSource();
			connEtoM4(ivjTornOffTimeStep);
			connEtoM5(ivjTornOffTimeStep);
			connEtoM6(ivjTornOffTimeStep);
			firePropertyChange("timeStep", oldValue, newValue);
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
