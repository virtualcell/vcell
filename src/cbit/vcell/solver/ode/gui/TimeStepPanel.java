package cbit.vcell.solver.ode.gui;

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
	private cbit.vcell.solver.TimeStep ivjTornOffTimeStep = null;
	private cbit.vcell.solver.TimeStep ivjTimeStepFactory = null;
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
	cbit.vcell.solver.TimeStep localValue = null;
	try {
		// user code begin {1}
		// user code end
		setTornOffTimeStep(localValue = new cbit.vcell.solver.TimeStep(new Double(getMinimumTimeStepTextField().getText()).doubleValue(), new Double(getDefaultTimeStepTextField().getText()).doubleValue(), new Double(getMaximumTimeStepTextField().getText()).doubleValue()));
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
	cbit.vcell.solver.TimeStep localValue = null;
	try {
		// user code begin {1}
		// user code end
		setTornOffTimeStep(localValue = new cbit.vcell.solver.TimeStep(new Double(getMinimumTimeStepTextField().getText()).doubleValue(), new Double(getDefaultTimeStepTextField().getText()).doubleValue(), new Double(getMaximumTimeStepTextField().getText()).doubleValue()));
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
 * connEtoM3:  (MaximumTimeStepTextField.focus.focusLost(java.awt.event.FocusEvent) --> TornOffTimeStep.this)
 * @param arg1 java.awt.event.FocusEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM3(java.awt.event.FocusEvent arg1) {
	cbit.vcell.solver.TimeStep localValue = null;
	try {
		// user code begin {1}
		// user code end
		setTornOffTimeStep(localValue = new cbit.vcell.solver.TimeStep(new Double(getMinimumTimeStepTextField().getText()).doubleValue(), new Double(getDefaultTimeStepTextField().getText()).doubleValue(), new Double(getMaximumTimeStepTextField().getText()).doubleValue()));
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
private void connEtoM4(cbit.vcell.solver.TimeStep value) {
	try {
		// user code begin {1}
		// user code end
		if ((getTornOffTimeStep() != null)) {
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
private void connEtoM5(cbit.vcell.solver.TimeStep value) {
	try {
		// user code begin {1}
		// user code end
		if ((getTornOffTimeStep() != null)) {
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
private void connEtoM6(cbit.vcell.solver.TimeStep value) {
	try {
		// user code begin {1}
		// user code end
		if ((getTornOffTimeStep() != null)) {
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
 * Insert the method's description here.
 * Creation date: (10/4/2006 5:10:02 PM)
 */
public void disableTimeStep() 
{
	getMinimumTimeStepLabel().setEnabled(false);
	getMinimumTimeStepTextField().setText("");
	getMinimumTimeStepTextField().setEnabled(false);
	getDefaultTimeStepLabel().setEnabled(false);
	getDefaultTimeStepTextField().setText("");
	getDefaultTimeStepTextField().setEnabled(false);
	getMaximumTimeStepLabel().setEnabled(false);
	getMaximumTimeStepTextField().setText("");
	getMaximumTimeStepTextField().setEnabled(false);

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
		getMaximumTimeStepLabel().setEnabled(true);
		getMaximumTimeStepTextField().setEnabled(true);
	} else {
		getMinimumTimeStepLabel().setEnabled(false);
		getMinimumTimeStepTextField().setText("");
		getMinimumTimeStepTextField().setEnabled(false);
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
public cbit.vcell.solver.TimeStep getTimeStep() {
	return fieldTimeStep;
}
/**
 * Return the TimeStepFactory property value.
 * @return cbit.vcell.solver.TimeStep
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.solver.TimeStep getTimeStepFactory() {
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
private cbit.vcell.solver.TimeStep getTornOffTimeStep() {
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
	cbit.gui.DialogUtils.showWarningDialog(this, "Possible error in TimeStep value : " + exception.getMessage(), new String[] {"Ok"}, "Ok");
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
public void setTimeStep(cbit.vcell.solver.TimeStep timeStep) throws java.beans.PropertyVetoException {
	cbit.vcell.solver.TimeStep oldValue = fieldTimeStep;
	fireVetoableChange("timeStep", oldValue, timeStep);
	fieldTimeStep = timeStep;
	firePropertyChange("timeStep", oldValue, timeStep);
}
/**
 * Set the TimeStepFactory to a new value.
 * @param newValue cbit.vcell.solver.TimeStep
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setTimeStepFactory(cbit.vcell.solver.TimeStep newValue) {
	if (ivjTimeStepFactory != newValue) {
		try {
			cbit.vcell.solver.TimeStep oldValue = getTimeStepFactory();
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
private void setTornOffTimeStep(cbit.vcell.solver.TimeStep newValue) {
	if (ivjTornOffTimeStep != newValue) {
		try {
			cbit.vcell.solver.TimeStep oldValue = getTornOffTimeStep();
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
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GCA01B8B6GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E15DBC8BD4D4E7FAEE17A42649591A04341EC8930E0956E44D06C65258AC5D1A445335D97704CDC81BF62D58AE27EBD2134D838D1BA56EE598849C94D4D40CA0A8838E8243EBFC91D4C4FC20A8A30CBAAA5DB8B0979819D7E6AE0204B47D7E7B7F5F1DBBB3F7384AE94E59D34E793C77FE7F777E3F7F7D0FDCE1E9ECD606261063B232B876F7B00363EE1C6138972B0EC838318F176BB8595FEE00CDDC4CB1
	ADBC27133243493A82EE62F08EFC4740F76A41E45DF6F85F42BDD75DF98ADEA26851C35931EA7E5949754C5D09FA32456F364EBCF8EE8408859CF92E02E781F72D33086197B3BCC206066316FCC4E8C6BA0B992E9E3E4FGC4814C7C08524741B3D754FCCF73A1C6577B50BCAE75B517E68311A31309D070B165B531E79937BAE35947C45763221D0447895FCF819067B339437ADAF8361807870F7677B984632DFE5E69B47ABD4EDB3C4F683173464191C7F333456162FB845EEB3639F9A750DBD3094C7F6C4C91
	E5009E78CE91F1FFF90568090177F80025927E9FCF933CE175F2DDAB00F5B57AF6E4EF8B335D7A750B1CBE7F095C7C883EF9D7D31FE62E8E78D630E05E3A9DE2BDFCD8CC621D84E5E9GF9EB166B6A814CGDE001DE27D276D5C874F63FD42890F4B65F19F77FA5B1CF65FF97052896F4D4DD042F05DFC3F47E767B8DA47CFF556A858B399D03E676F1A0F46E4DE9356777C6E9FF14B96BC2449D6084932F937B4535750D8442E8944046A7E2F083A972D894E47E05DD7B8396E95DC0CA6CBC1F77AFB33B4C9CC4E
	CA196E32CFC8DDEF32123A4E01778C968B0AAF9171F6991E666613E24E106F89A8DB71896606F5A35A52291D4D95B4BC2C9807192F5C27E9CF23B67426055A1251C1E4CFCF23BAEBA5DB4AC47CAA991E5A723A1827645B7C5B653A04CF176BE8FEFD3A0B34D5C37AF2DDBBC0AF00G908D908730B89DF36CF8F15D9D66185966935AC69CCEBB6F63A86F27455B60490F89BEDB075BAF585C7D7CE51B4FE16BF372FE0E51E41610987485716514105C6F879D27F9B76F33898E0F9B245BDC3C407B7C14E6D349A6C2
	636345D2521EC1014B4B215C9DA5DB6069347905CBDE3BCD60839434FCF7892913DB364BA0918440BB0D4B5EB2922F9470BF9DA06FB30CC30CA86F9C6F038E2339395363999E71DA08A7DC6FE7A44E1A73246FA80377C31FE10C4BF793DCACFCAF14643CD932D9A6275D51CF4C3779EEE39B5BAE6654D220DF8920DD0B39733E95F327FD4333DC4DDBB394F3477B7EACCD59DA1AB30756063621F2D1B6316F8614CDDB0BFD7E53D72CB299C4775D34F134F3723E6315A09D19F7A11FC0961393291C467B21BD443F
	D830A581E059DA0C7946DDE5B4075BECFE9E33364B8789475B83B5C06D4C6E303238E64A7412BAA003A7992772C5BAA217633682ECE77A092E9200D20E0E2D65G95GFB812AG2A25D8D40ABAD2413E625FC37B83F082C483A4G24G64F149BA93C0AFC09440FC00A400F4GFDC6322E93C0G0881D88C308A20D0132CEB8120BA5EBAFDA3633B7363EE6A6E7FB350D8571FA1317EAE62DC7BC760639F8B50362CADA57164EF71EE211D7772EEBB3C78D9192E14441D0DB7839E09E71308774278F48286843F04
	5FDECA6A45A688BEC75F084077700260F08F864ACBC5DD7E11BEEFE0E8C3DD1525242EBD23B0BEC974552524EF6F77385D3C5829C8AC2C3CD69CDBF8A76F8223CFF8C65C8245B71511B978B0FF1B0DED874B56F1E4ECF20E10F1124EEB63ECA41F3CBE078B3AAAB254F2719A32AE58F13EC896A3BB60487CFCBABCE3G6858B1DA0EE307FE7CE9EE5117430AF32019F37ED4138EBC643DA0B3D99ABB28CD065D4A369B45B10E617D5456F7ADA46FC3675819A27D6A8E92D30B47676E9A98C092D6367C6BFC995FA9
	DB3F60010111766D2F15937D75E0D7BBC0AFG557F4CEED20E63A96B54CFF81CCEDA85D09613C962B4E4ADE4FE8A707DE0A60D9379B6E8C97917278BE531C8E16505DA547397FB8AC8AE70DF0D70B00DA1837FB9293E9E5F33B9246CB22BBBDABEF78F29BB27EC4EB0DF54696C407512D74B6A8D1EE4EC0E0167AE400FE5913A83E711AE0D70812EBE0B528DE551FAAADEC768B61EC33A4500A7E34BB2F812F17E83C697AB5245C9F4C370CD461FE96B285C9970B4CAF46DDDC8370A51E933E11C498E255BE8C6BA
	D3B6256B04E72F0C0E7A7136C4B74164A87B7178C5240B861A1408FE34F6A35DFC20A3E326329F6BFA2438B0BAE5BF16CAF44B981D329F8FDB10AE1515AB7BE13D04F4F9B9F48CD77663D309CE1FC347F9E5BF96DDC63A96C6276C473DD7104E4268147D6834A21DB3A7E0BF6A8D583779AA52C56550F947B266EFCC0EDC5E4FA53A05B2F9E8DFC05EA35710AEB1074EDFD05E4AA0F9972EA31DB6B7A02FB7CCDEE1AF52956752F9904AB365D2F934DF7A380A7415625E40CD5BAD5B1851BF8A7DFDF373D9310B22
	FBG5F976771045E8C3CF42D7F1CD9DCF341F7AC4042DC6CD7FF3181577A51437338040CD99177B192F3293D293901FE557971C0A66D5B8AAB08CE53FAE8BB6B313F59DDAEDFEF9C777BBD7D8EF129437A1444F2F93FCBF1EF152F076700436797BAE0E09C437E67176544DE3296E2E836B67CB3F1BDC07ADEAB681D8130E43D588E2C621E10967D6CEFC2BFE70C4F61EEBC7A28C697414FBC9D752FC69770B366070319B47E457B76E07C7BBDAE23234FE59C3965B25A7A84A3EFF7884D4D5865337D18BFBF0D7B
	B1F69D5A724D292018670FC534A5065910A033A56979F96B286F0FF6206F69D0EE4ACB5651B168BF2CC4274046A00AABBBCB62A18CB97C54060F0E37B29DCBFFB41BDB18FD1FC6E90FE7682F9E51B873286E683C008D71717F26C932A664D2BB3EBC0BF624GCD7106E49D9D0F9E48170FC7964013F55B448634739F4E123D1F81CF7F085FA87E5B69718B861256C65BA84413CC100C27C841C972DA0563EFDA08AF4F6E127B12E31B26684B036F74E9E6EC20BEAC5C907045BB3BA2A78A007AF2709C7A42ED84FB
	81E83D979EA5CFF1BEA33545D976B2AEB0569B6C1E919897EB4A021FD554C661905AB87F0D97B86B4D7B95ED9447510D5436199B83B65603FD6D120D7FA2591882B44579984F56CE92971BEF3001FE9B3B44B841E4125AB0F195ED1836B41EAB5AB2BD228D16FC2AFBA2BFE043F4287398GEA032B8BEDD88AB45A8234A1290B5950C8F32043211483ADC04F62EDC6B9E640CD1764BCEEE6F21A281CACCEC94EA2C9CE46C5141384383CCD087F2904EF875C0404F7F4A3BE9EF02BA4FCDD8F62751B211F5E0C7855
	92BE9AF0CBA57C0FAD0857EE01356B9644FF6F926285404D17706517253A83DCF1A1625F11709640BDA8611F3B02784500CB1770A3D23FE75A1A2CF3EEC57C97D7917F9441ED45F846DFA5F56DF2389D2E9117B4E725BCEBA41E42EDB04EEEC3D98957909F853804EDA8EB7FF5A24B4E8F58C61CC2082C516B48138A74B55BD15611DE445B8197339DE57DCD2FE817EDAC48AE3AA61DFEBEF43FB4E9FB3234C733D2760EC077911B44BACF07F2125B79927E040857978570543697449CA45F1DD03684000EE9F586
	B256366F00F198E08CE086C09CC0628E9C637A75CD1C4A5EE207DB00A150666C89DA04619EDF8509832C1B58F7C9053C1F21F1F854CA6230EAC7383F110072BDA972E9BF3FF3BE5ACFD4DD131FEDE8B71D54E4C668A7CCCCF67B6701FE0246EEC595E92FD1000F85D888108870416798375995B5EAF143FDD696B06A532C531176508BF6AA7BC96DB9D5CD6C5183CDAB0095408BB0E3A75A332AC2359ECF0EA956A3332BC36DDCA1AE44BE36EF294A179442C77D0929A17345D5D016D31C2CAB83E8813881B08190
	DB0CFE35D4362979F59CB78FAC9E6975C46886G5A4B1F1D1A1FFED53E054541FCF40C2DB090BF95663B969856CEB9F8271D4E33DE4947F9D6E2B1365FCDC6421BD3122C538334GD881A6GE682A414E00CFED07905EA5D13898844C78CC9F8F0280FC74ECDADB667D4791615A86540C3F5443FD4A8AB5885F3AE00F600A100A800F93B503F644AE3EA7EC9C793EABEA6CF514777D479166CD272F1D38371F185142583941642BA954082608508AEC59F9B772A66395CC755BC77F7CD4D4F6F29724DAF8D662379
	BB4D01793B3894773F0F182718CF2AFC690AB634F0E8C3E1995A10FDF1EAB6ECD5656BACD3627B7994757DD2154F2E284F53BDB5FD5F2872CDA86A5B5FB3B5FDA7D4792295757DEE0A7AF2AD2AF92C286FE5D5BEE5FD2F2B72ADD654F75F252969FBD415EF19223E2A4BD353F7D015AFD5D15FEF26286FB3D53E3C5DCAFCAFDC191A3ED7D579740A7A3E36CECD5F7D2AFCAD0A7A0EDF1D1A3EAED5BE0B223E74A96ADBFFCD0D4F29286F95D5BEE5FD092AFCD10A7A3E39BEB5FD8F287245A86ABB51BBB5FD97D579
	62C37451BB91CF08FB29CB21EC85G45CDF4911CF68F2C5F7760B8F9D85C6FEF056FDE0089009800840095G5AF2246B90698C70FD86E088E0FAB95D2FAEE47B5B146E47E62FDAFC76846CCD103D776E8976C38E2D2C8CED1217CDC83E7C2B2A6EDC593BCE761EB5898F5E1740736450BA227338C30648E723478C4AE723A78D4AE723678C1BA89EE448EF76E0F90FE17233536B86253353925117FF5210BD835F081BEC330AEA69FA059FA36760B05B6C9EF15329A8516C8FDD53F2B83F6DEAA47BDC734AF19E7B
	2121118B1F471E34BB840F8F05D8F81B2FEBE0C01A4476F879FE34F9E5D564787D26CAB9FE2B2B1463773B2A49631739AFD27CE8BBF9DE5C7BDE8C3E3DAD79F721BA32FD172A156D3BDE2DEC1F2DFAF27B9C55116C23F578EB015CE588BB6B3EG2B848E771A5F6848E5B4E641E3EE10E44F2D096C4733B54AFEAC28A1396F770C787A79D0DBDF2AA9124A820B128E01F791F3A84D2BA23FE0738D72E13E3EA6725332C87CEF5610365A6730BBFC6CE4197A98F396FDBCA9F84EB7F058E77EB4FF32BED32B8F6FE7
	281E1D46483159E3D40E0D4198B9B6F5C675589CB2C60E4DB123FAEC4E98A3658A0D11F0CE07F9B0607338A0CA14EFD6DDE4DFE757A97BBA2FAE322F4F55297B3A30AE322FFFDF276E6B123A49FD5D5699642B99FD5D59B0C93DB6C4285706C96A35618E6A35E112FAED3803FAEDD04E79E73A02FDECE4B9FF0FE3321CAFD248F9169F66E0F9CDB85F64A613D793D15E510B72B659A861355D41F9584870F30371E69343BBFB02717B993E51920CBF00E7EB96395EA6C96FCFAE856BEDE2789B1703659CE4784AAB
	4178C38C3FAA84FF18611F374A751AA43DE3625904AC976BA93E691A1CFE3FC47F5BEBA1746CACAC7E3A1C7E00C4BF5E9BC25F484EA54E137BCBCE5BED4F08GEBDD3DF8B7C03A27548A5FD600E9G51FAFA96C5477F3F9EA57E8D78ECAEE8D26240BC3DC23CAF50053C29C01F83B883409900218ABC8B1911AB3FF354E96B135DB9FABA176CDF1F8FBE3F91C9583DA3313CBD78CCC6D63EE53D489FFC4EA2ABFFB537A8DC3E34474AF2C324895191C2D33B3EA8DC0FC4C34F7226DD141F65A5D6507825D6603931
	4629D41ED30965BB4B154A0B25727F9ED3AA2F2F44B9C8CF6386F97F4746FBDAFFB306C83B3DE53BE9396D65996E0DAF4A38C83F47887C8E034EF91667635A203D92578BB18E44C5CBB8AB073814CA690C71BC7E9E4434B7D9675C0BE74F8DB70276404961F8F373E91F435EE69B6C94B3146E5F9D391043781F5A0B77BE06AF90194B60BB95A04F00F79046AA718E427C393339384F9ED33C773142B226519BE89FF54690B86351AF18370E6A7C5BAE549985658986927FDFF74963CF7262C3GA3GAD5FE11617
	133128AA287CDCD0B931774B8CF979B7C165D9B2FE6A5B4FAA5037741F45F14E973E0DF827C5DBC5FDB2306722395199CFE9372FE3F7ED86A25C35915B8D757F450B44FF12B3B7C086459DF6627D1B19922E3B9CF10992AEFA8CF1B97BE09D380F7836BA3F9D7B18FD4AE3039CACC0B3843015491958C74FB689D0F98B9CA8AFB1023CAC193C1490F9E9E172BC9C4A53D6AB4B4B1649AB2A8E16D7D39D104776A7434EF855006DCFDE189ADF5BDD73513363A362FD07C3B27BA97E1408EF1761E9DB7E6A8236E5B3
	1445D7631954F5934929221AE41D892093408E908590DB037D1331291ED3B94B30F83C1DB091F34A16BD543F1F281EBF0482EDB3AFE7E31B09FF6999EE613D47A25EC90A2F21751E441E56B9519964C97DFBFFBF71CFDB1B2CAB83E88768857082CC2FC57FEEB53133DF322C05D52C2DFFC83C81A6AD129D7CE8GCD86D16ADB02629EFFCCAD35A30EBD9757867212729DF5AA713D4D68D6550667B1653BD22E444799A9FD21B1925F6FEFAB719998FD0B829F6D8FAA8B24FBFBC0D34A517B76B99C7EFDF0AC9C87
	6D92E8B92E7288A755653F67E1DD967F70C92E7D1F0FE94AA35465D2E6CBF1DD20EFCFBC38A6075A1362C0FBACF54A76D05DB30EF2128F319C72BCB8A94F82F2A409FBFADD0177CE597B6885796FED7E2F016A18711D6A2079EEB7C5DEB78D1B1457CDDF191477BCC6CD136FD9FDEB0AFC5F7FDE137C3EBFED2F5FEFA5E369E2282B95G5A7AE4DD91C0CDBD3655427D643EA33DA5905ACECF5A87493A0D7CAACEC07D1F6C0F6C6B5A7D6A3F254BBEC06EFF76F9844163A272913F61G29AF41639511943779G09
	114FB1B8A4D0D206AFBAC0724D498F08E8BA5FFAD39C274F00DF56FA1C033D5F0DF3B03B04CB936FCE4643F712043BA40E67AB61BBCD423D64C4DEED83625C6298EF01EF3B04FB339C693C92EE5BB841AD026FC4897775B852ADEB40FD586A82F9BFD253182CEBED1C7CAE8FED7F2FDF175F5FA9F8ECD003E3D3A83D05493B5198FAFF27320D0C7351000F83C882D88540B5C1FEG54G3482D8G1CGD1GB3816683AC82C884C881C8EB42BC9ABEC266E817EDB743F7B48149A633E2B279EC8E12CE2CFD3E9334
	9E4FB34138E21ABC8614EFCE503A5B84BCE7D478281D8F34C64E5F07DB5573F7D62BF82799720EACA0A8AE36152CA5BD8383FEDEEAA7F3DA8BA4DC075F6C71BB486684DDC33DFB916FED523E52C22A024D3F32FAF07E552B608F56B34E6906C266F3DED3E01EC2F5AD98A67D73104767987738851B733853B168A62D1C1BEF92573C43B83F1FB061BA55698C36EF74E4403EDFB8513E8FA258A71F3F4A6DA3E55A7D21765DD39E2C6B31D381DD6F16232E027D4A3A32A651258F53D5BB9E2C6B6FCE87F4BDBC0E3A
	6AA3684A1EC4D73BCC97ED6FED62D8EFE6F80ABBAD6206980E36431BE7B8A9B78F9E2F08540620B7BE8BCB142E3E1BFC3FE0318DCABC4DED117379F81BFABE1FEDD36E0F4DED617D7125B6657E78FA1B3CBF26389BED24EE86215DDBA5BADE42DD15F0B7C51984F745E19706A43CD7340B604F70C4A162EF09F60D0AB8AB43FDAD43DDE538E9ED24CD024B107A48FBCF9B297F810753C93F6F97E99CEEE822D2BCFEA05A68705AE4F6BFA2612ECAF5495D8C2F5F2C1B727A253884AE1C6EE5CECEC767D587FAF0DE
	9585F81A1F499D1C143BA943645CC936CCB57610FFF1F8769BCF1C3CE0B9594D619891BBAA9FA38E2D7FC67177F48492D83EA732E74D5B519954A60DCBDA3F9EC01BBA4EF1124DBB75D85EAA157FC9E7207C55F1AC9F134A671C6724F6EB2B4C93638FDD234DA9D6BF7B8D465806G9E2B1F757BDF845DF70983390B8FCC5E6F53F656AD6E1D3C8D342987F04C521F2D0E545EGE976F93C3CCF38CD31622F65188D59AE398D058713F50603F7B2E6156965FCAD40E33EA33EA563F2BEBB700C297051BC6B6E894F
	3DABBD725C23F138F20170C8F3CE6AAAAB7B430542C0DDB846451F7774G21FB104A5B658A57D16E8AEF87877561F4AD7AF03A6471F03A14F1B99D7DFFE5F69EA549BC7FBDAE4B6F6372255F14F4080DF9452D26664CDFBD192961349D5CBA5A39F5F0DA52C3F5F0D9E2875641E5C267B6C68E4235F270FF457D0F58006EGABF039E4F3300C6C8E8ADCB65BDC05B73605CAF0F42374EE245E8D165D677ED65E56536E79C344365EFE182D698F074E879F9167B551000FBB0C6D63D747EAA3358F980FFADCB62793
	E69443475DF6B2B6355D967840BAE5E6AB116FC79AD6C84B1EE8A5F65F14717CAFD0CB8788DE6B2E711F96GG08C8GGD0CB818294G94G88G88GCA01B8B6DE6B2E711F96GG08C8GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG5996GGGG
**end of data**/
}
}
