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
	private cbit.vcell.simulation.TimeStep fieldTimeStep = null;
	private boolean ivjConnPtoP1Aligning = false;
	private javax.swing.JLabel ivjDefaultTimeStepLabel = null;
	private javax.swing.JTextField ivjDefaultTimeStepTextField = null;
	private javax.swing.JLabel ivjMaximumTimeStepLabel = null;
	private javax.swing.JTextField ivjMaximumTimeStepTextField = null;
	private javax.swing.JLabel ivjMinimumTimeStepLabel = null;
	private javax.swing.JTextField ivjMinimumTimeStepTextField = null;
	private javax.swing.JLabel ivjTimeStepLabel = null;
	private cbit.vcell.simulation.TimeStep ivjTornOffTimeStep = null;
	private cbit.vcell.simulation.TimeStep ivjTimeStepFactory = null;
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
	cbit.vcell.simulation.TimeStep localValue = null;
	try {
		// user code begin {1}
		// user code end
		setTornOffTimeStep(localValue = new cbit.vcell.simulation.TimeStep(new Double(getMinimumTimeStepTextField().getText()).doubleValue(), new Double(getDefaultTimeStepTextField().getText()).doubleValue(), new Double(getMaximumTimeStepTextField().getText()).doubleValue()));
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
	cbit.vcell.simulation.TimeStep localValue = null;
	try {
		// user code begin {1}
		// user code end
		setTornOffTimeStep(localValue = new cbit.vcell.simulation.TimeStep(new Double(getMinimumTimeStepTextField().getText()).doubleValue(), new Double(getDefaultTimeStepTextField().getText()).doubleValue(), new Double(getMaximumTimeStepTextField().getText()).doubleValue()));
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
	cbit.vcell.simulation.TimeStep localValue = null;
	try {
		// user code begin {1}
		// user code end
		setTornOffTimeStep(localValue = new cbit.vcell.simulation.TimeStep(new Double(getMinimumTimeStepTextField().getText()).doubleValue(), new Double(getDefaultTimeStepTextField().getText()).doubleValue(), new Double(getMaximumTimeStepTextField().getText()).doubleValue()));
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
private void connEtoM4(cbit.vcell.simulation.TimeStep value) {
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
private void connEtoM5(cbit.vcell.simulation.TimeStep value) {
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
private void connEtoM6(cbit.vcell.simulation.TimeStep value) {
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
public void disableDefaultTimeStep() 
{
	getDefaultTimeStepLabel().setEnabled(false);
	getDefaultTimeStepTextField().setText("");
	getDefaultTimeStepTextField().setEnabled(false);
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
public cbit.vcell.simulation.TimeStep getTimeStep() {
	return fieldTimeStep;
}
/**
 * Return the TimeStepFactory property value.
 * @return cbit.vcell.solver.TimeStep
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.simulation.TimeStep getTimeStepFactory() {
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
private cbit.vcell.simulation.TimeStep getTornOffTimeStep() {
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
public void setTimeStep(cbit.vcell.simulation.TimeStep timeStep) throws java.beans.PropertyVetoException {
	cbit.vcell.simulation.TimeStep oldValue = fieldTimeStep;
	fireVetoableChange("timeStep", oldValue, timeStep);
	fieldTimeStep = timeStep;
	firePropertyChange("timeStep", oldValue, timeStep);
}
/**
 * Set the TimeStepFactory to a new value.
 * @param newValue cbit.vcell.solver.TimeStep
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setTimeStepFactory(cbit.vcell.simulation.TimeStep newValue) {
	if (ivjTimeStepFactory != newValue) {
		try {
			cbit.vcell.simulation.TimeStep oldValue = getTimeStepFactory();
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
private void setTornOffTimeStep(cbit.vcell.simulation.TimeStep newValue) {
	if (ivjTornOffTimeStep != newValue) {
		try {
			cbit.vcell.simulation.TimeStep oldValue = getTornOffTimeStep();
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
	D0CB838494G88G88G6E0171B4GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E15DBCEBF494E5168183860C9AB5C200A0110786088A9299E6579F6EACF30633434CB09A9FBBE6F7589DBC9395C7DD59BD9C97F7AA8F1E9D84A433B2CE2BF1898EE04870081A09998D90A00188A1B4CD831124BADD7DC82A3BBA5D55A11088FB3F472D2E6ED427A0E7BDE74E661CCBF55D6F3E3F477DDE05D076E1566A4222B2C1A8DCAD703F4F8B852123D1903AEB66DCC2DCDA40EE91F4FF9F81EC915E6E
	AA01E79614ED1BEF37EC961E975641FBAE3C17BEE6373C8B3F378A9F591B0F420F84FAAA20ECDE5A6C29C36BF973886AD9CB5FF77BCB6179B640824011E709C2749F72EF6378FC0EC7A8AC92042514669B3F15639A613D83A093E00E42689741F3BD55BC2A360653355D15AD2C7807D051C6A2C7A793E1E71071B673E73970E6610AA051B51A5AC9F812617DF1G62FC3170670ABD702CEB6976FC59697429D53D1D12ACD7059539D78AD5A9AE294A937155565AFCFE690CAA85DB4400A483FD3007487C29FF9D15
	A188B5703E0A624E3793BD3970FBB140B28D3FCFA4787A105D62G880650373F7C310E5B6E687F1E304C7343751B92781692E2BE4D89C5FDAB5F6DAC7EBD2D075AE3A45E4B21ECF358EE29GE884708224G7C01567F41233B61F950299ED67CFEA5F0B098BCA43BC2DF0317B27C2E2D05920EEB15BA1510CB90D89D277AAB8D6C998A985F441B66E3B149BE00751D537A3010BFEFDC51DA031864E77797E505D9AC960423B1E13A1FCA287B39F0ECFB0C55FDCE506B96DE5C3FF63501EE6BB37B57AE67F284B52A
	3BC6A5F57D935A864A61F7A10F85437FA3458F68702CED36D3BCF9CF073215AA368D475BE8CBF349D4E1733E7106F14878C1EAD1DB045920C662ED71B9096C2C885339C733A50562CBFA23F8E64B67341D12F7AF1465DD36DBD87BBA5CC47AEA7D953B258DC085C8854881D88410FF855B585823FF3A41B656A20654C3911F6C12C28263BDFC6C3F61A97529A131A990D645C027E497C3BE51A9CBE10153B49FA3B1F046701D26ED3F93F49C1382D2C8D4FDCAG240BFEC915C2E1C6F3615896C29312E8A9694F20
	409F94D02E7358D6F84AE2D8BD9BF4092A9425E065DD47C81D740AF610888260B70BCB70B80957AA703F6CAA743B2B980797283C93D288860C5A5AE6C5690E84ED4493C13DCA627C929DBBEA60F74BD50C31FC026096407B92CD0E35719D1D1C86DFA7B1DF8CDD41BE7694EDBB4B00DE6837DB2C7D58F6FE634036533071A1217269FB8D5BCE9A34631AFE56E6DA7A637B50AF29ECE2DF9F14E5746318AF35BBF4B2086E1B6963E86750E34735989D45B7A11F40EAAEA7D1B90B77AF1A09FF8B4016BC00957D9873
	F30D652C8D9F924392365AD3A1E8F012ABDA834CCE456960F1AD5669A5F5C012A749D31BA89D11AB883F83F8176BA73A5E8778D0E039F5BB408E00BF826C8278C40B459DD4C769005DD28D5086108C10831087308A2062DB3B2599C0854884D888309C206C1A5DD2876082C88548854887A839EE37D482582EE31D7EC56CA87C6E7C38193A7B7F8CAC5699B4565FC51C777C95787857822CAFFF5DC462A975CA8135C18ACA81977C8873B2DB9309BB4FB7EEC56339C071C148CF07A1A104B5FCC7932997D1D5C3BE
	E7C4154EC82A6A8BF82265DE2AAB9CF18623298DF5A9CD242E154B101FB4FA35090C6D1DCAA0A051C1C5E361653DB437C8326487238FAB11004A708347499C3CDB3A42F3DB5271B58249CDF20464C9B62F1DA31276948C797CB0D411D4ABAC3ECE72CEE1E01BAEC68300A37373ACF866DE475CF1F9BB660E0A2BB305A75A03C5C6F3200C99F79615A531F1FCE7D2B4F7B01BFACE985BFE1966B80E8FB3DBDF3211F69FBF47AE26747FCEE3EAD3C201D3EEB7127032FF6E5F2463BBAAF62A8AA4C6B636EFBDC974B7
	02DDEDGEA920E6DEF1CA06518CF7920FED811E5D685D0161BC462D451D1467DCC057769C9ACCE643DFE84A97F11D6CEE095AF2F9C01FA16B6EFA6EDC16A09C8B00DA1097F04D6DF3FEEFEA72E4C4E6B0E153F5ACC6ACE564D9996D21DF9CEDCAF250C6075B6026566DCF8FEG783411046E3ECE245B8C6FBB819AC7B23A24114C076ADB88DD2F8B6972C7325C32E2A44B736BEE6173EACA37CDC23A24DBD87E493805494D06E715C61763C53A12E4C6E705E7E5F2BCDD2F8F691AB91D8B1E2A0E0E79D15F05F469
	DC0E319FEB3A11AEA71965C3E3BF32B53A0549ACE79A7B91103538F0BAE3BF2A35756B8ACEE76C471B9ADD69A8D6EE6C479381242B9845F238319FCE85696AC631BCEF6C474EA05259B81D319F3F5168E4CEE76CC7EE8F52A50F0A5A0FFA2376DD8DA1DD66A8B66FD8417D4D9D251777E9986916686421FDD1F97F2951950CE2739714A70C564B1B2FA21DF5F4D41EBAC85EC084692AC733F9904AEB667258387446AEB2D6523D01AE31D76C2B8ADF06713E3676B89D22589EC0F7E09D27770E46357E789E3A6602
	7785GCBC663387A730D3856CF6F4E9672DF5C37B651BEC641E8E6EF692D51F1B5775503EB5858764DA92233994A5AEE4571266BEB7DFA63E0B82CF47A68D2070FA93FBF299FF7996E03139B6069760542EA93A446BE9CFFAACE92FB49DA0823795A70C4C7A99F170320B79BE0692DF4AD59C17704C6201F1D87504F342BB3040EFBA796D99278D911427CEBC8097AD9F9ED65EA96FF6B6E7F4178F7AA7EAA1F53DF956975D709CE35CAF279545ADA9C7279FE4CFFF560FE4CC08A5A726DD1FD4C9F5F74EDC2DBF2
	398DF9BADBAA7C36354C77371C68FB5998E8A7GAC875DC7F52ABC87B1DCCAA709076A7505198D7FF6301E6BD87C70D4E1415A54A223BD9E6F0399C549E3186E1CB1D19B2A6FF796B782B0BBFE5409F62C821A6A31F68B4BC73FBD234FC772D8362ECC9F0BF6D639485E0FDB690C042B683F4DCAD85DC91AF515F89962C9A6C8D5C7C9419172F397665F8ED72CAF8FFD2077E51DB84A50175497BDC559E3198FCB46C6FD19F33733F8615D684B94977A326DB63077B65C13AB7B12BC69FCC66B0B13DC65C2B4576F
	F4A991480B1565314FDD4CC63586ED4CF972B14151F507210DB40F5E467754EE0B5AD8B06E2522D5G4C46031296EF2029CE45F8E6CBA4AEE248330F3D0F7352B841E41259B0F09EEDC8DA1CABEC5BFAFBC29B64D426BBBDB5EA43C01633B8AD8B63743497EDD886B4565B5106BA9F37E1BF1F1308C6ED408674BC5EBE146385DC16A6E7CD9717F30049F9C6B212731CA6475705F216832E628E447F299B71ED00CB5770BF57700B81D7F2A762474B082F831C2A613F71A3BE87F04BB47C76G622DE960D79A62DF
	54702900DB2861EFD3343A83DC75DD08BF9DC43C8C38699A7E5D9E4467832E6CEE447F1306EF86DC32061F96C27C6384F7B746539AA6F56D7785FC7E08DF1B33B21ECBE164293C87726CBDA82BC2C5FCA660726EC1D913D5A24BA53945082C46497A2546D31AEE37B424232C9991448FGAEB79DE5FD50CB6D927BE26CE2EB52E0A0FE3FF4F9FAF40FC638F770FE8D5BC77C830DC1593DEC9FF113067F04626BF4F8E65BE49ACB6A9314AD3D97773E7ADAC92EED9B87F998A08DA09BE091C041B84CF1B9A78F88A6
	FB0BCD8195D220A81F09D904619E5FA99287D8B771F777A97DB84362F03A034421E47C0D6FE9B13EF64AE73D89BEB6CE6C3A20BF5BA859F23C28B841B8514CE5370D0F0E93ACF63BEC243FE682FE814092008200F599983717CFD51A458D77D9F94018CF45E222BD741D9946FEB2FB6EBCCB6C29839A87C090A085A0DB3367539653FABC52E7D80F5C2ECE33F305C5F176B13EAD26FC4B637818BFAF5849FC31E4025DD28ED083E0836883C887D8B0817D9A55F2484C2F0338F9E0D3341F0763B7G505E1F8C534F
	67CD7916CC0865E3B9F647CE622741FC5786E96D28CF12DDEC1E75198D67D98593307FDEF0905E7209F6CB9D008DA088108EB087A0EFA246685916AFCC6B1ECCC0A0BEB4A4030343FC1C619ADEEC6619722D18E85486FEF11E78D7BA8956BBG75GEDGC9G19GCFCCC27FF61E7E4A4CBF6DE8424C47E35270FCBCEB4A37F412110F97AE929FD7C2D9D9A62C0BG9A81E40094001CCC74317F34E9BB577BE85A4E1F778E4F4F97CD7996E6467271F502086DB7BF93773FE77806591ECC794AA69B5930510DB6D4
	CEC69B142E6159702DA91FEB32915F275D4353F75814EF40D05F3343547702A9DF3A213E497270744DB6654BB15457E9FA5EED2C2F4714EF21213EDD0366ECB7266FF3D33EFCC3FD2F8ED35FDB26FCAB8C755D258CCF5F94D33E527B0C784E850727CFB2652BB05477FE4F7074FDE24AD7E7286FD74354773AA91F4DD05F4C507074FD4F14CFB654678A8FCFDF4814AF59D05FEEF5F87AEACC79B28D753DB0CCFDEF1872659A6A1B9D991E3E6F1B72AD0E53476EC45CCA77D217C159CAG06F385894EBA85566FD3
	B0CFFEC477179D702E822483648264812C8430E6A15DBECAD78F6F9DGC9GD9D9EC3F3A126FEFB33AE9BDC13378CC095A1B277B2DCE0975C38F8EDE06B6694B52B5DFFEE02AFB3D6E37C577FB7590BCF8DF824F13636B084D63C634A6BE9BCDE9B5BE9B3D235578ECB43DF58B43038C7D4D9EAC4FEC9D7A6CF4FA2B5159697B5417E70A481EC1A892A05B2CD4ADDB2FC8FD649C9CE61B2D11G1B0A924D6178B52D00735B0CB6324F75C4964EE3EB4F6C97864FE30F38FC2A92C2A1B6C98C1DF23B35C96C192054
	09B66F33A50EDF0D4DB8FEDF5A0C6357E09BBAFE4D36C471E37D64BDBA274D875FDE537C9BFFB631FD771DB536EF7AD9E37BE61D9D5A3EC74FA6320F55612FD5F217E150D977C9D8A5883857DC5B391E533428CA4BBECD762B7644FE7C075D580FD5F6527643CAA454A9455BDAFCEE1BD696DD14B429121F1843E8CA4F91FED58CF924C13EEE257C2CAC913F759C692BCE1F4B9762A74B4C4792977AF8C4D53E5EA760187905ED28B153FA7F60F10669F1B99247265BE19C1B9EC7625874B94CE3B34211B8B6A98E
	73585C65C854D6D80C72DD96EC876E106207A8B13EE567937B7A72F9E3DFDFBF1F585715674DFDAD3C105857F5974CFD5DF2E1E8DF2FC9B13E36202F2E0BC3546B458475FAF108FA3DF88375FAF108FA3DF88375FA51384D3F630B75F1BF6E63B907EA73FB8D5ABCEF9FF1728EF0F96F3807125710D05E83DD7ABE39DF434B5D316DF0BF473FAD476A2F66783CB87C279CBF519F0B7F8C4F5682FA3D87B43D9FAB31FA8FF07C22D8FC4B679CFFC7B096DF4371361ED87C1FB97E3D10DEEF3526F7A95D335735453D
	8CBFA92C277FD4239755B8FAFE96E60D6869BF53687FB592C73F1F1F2186487DA5D93C22C4D4D86B5ECF7259E35ABDA5873C8781B2GF26EE7E7D1AC7F4F3DCC7CF307C4BFF4A91A183326925E7EA0721642FBB9C0BDC087C052D4BC8BC9DB2F3FF354ACBAF5F70EE62EA77B57DF471E5FD092FE6F0816B7441E49684A37EE207C3167AC3A723FDF3FED30FCED0F15512C24B4F1BA62E876EC58B6D80FC6434E723CA17DD9DE41D4963F52E9F8EEBC46E3D4DE2E1517380C4A2B35727D06650D53F08E125936D1BF
	7E71FC4F6AAF4DCB7AED2F58E53B9214B86E49AF4A05C45FE3C43F43E0F31ED69B2E8D5A2661FA61BDA762F2B45CAEB762D6CD534E748278BDC873F43BA5F9BA3629FD9DB1FB6064F03C3676D84867BAA4FA1AE98BE57BF7BB14F51C7F7169F86F638CBDEFDC816F25B36C160A99F887216FE33C03107340D4E1498B4677BE165A3EAD2A1B4146280E993A737F5D4EE226737EA06A4C04723C9984773320BE7E24DD3C8ED085404A8BFB7465A4976D0AA9BF90D3CE6CEDAF5417CBB1652BF57C4C379F6FC05FD67D
	F80EA0BF161C705B9C6B837C7E9CFF167FF4C261660C16B57C2E0DBB41DD9B5AEF187FD3C244FF52E67AC08643BD61417BB7F3B45C97AE4495E8B81F06ABFF9056018FD21C2D814718870DF3039EE420C982789D17137EA0BB5BA440646DF3223C028472D66B642D0A1337B9BBDEDE3D9B65D9330D652D55495B1B9DAB2FC1A70F6F7F186E4345835F7F99A65FEE53FD38F8E0E747BB6879FC0B4EFE062F2278B69D1E7565738A76E5AF14AD4E46B3295317C81B5ABB53EEE986F0818CGE482AC180963D345B7FB
	8513338C1B92EC0609182CDB76B07F9EB05D770B8756E77EEEAD76195C3F19A5AC985D1070CE5262192C5E1773670245938A491379372C1D78E71DE53754G348128GA9GD93350BF799BFE76CB1635300A95BB3D74861836C876C91723E812C419EF538D7760F2E771EF7378B3FFD634DDB23E9FFA0C78DE63F4A5338737E346D7EFF8AE370D53D7A6644BB3BC6B2A6774B683BEB69ECC34E977760066C3015D37DFA7605FEBDF8D46C13F1CCD3E0934FA842DAE7F25946B52BAFB1A5070ECC3517684F5390C5B
	D21D939D5B135B2A57B3FB1CCE34C74EB13607699E6D95B49FFA5C48B3FDC81E19BEC10B57A741686FEA5D6FF6C57F3D4D7FB5B09DFD5F298EFEF77FD262F513F849F85D6439E43C67613FB4741ED564D2627B7E831774777DD9FF3D52C5F2E9BE5455CAG6BC3F64BDE000607302FEEE8A7779D59AD0178FEFA4465A16BB672D51C0A7ADFE9CF6C6B0AF673EF695EEAA777BF1D0A2AAAFEA2DF5B9F69A07525AAC10A64EB5D8E9223104F63D599A9473FCD6FBF4A121B2259FC6BEFE91E6EG3F028F619C6C19B0
	4E4186B45CF61A3B97437BF28D17CFF14243707BE1449D74A22FD5431D2379DC0677818D77388B69D2B45C4B9241BD876F859A2EC51B8F2EF89877E127446C3FB4BCE237B89E997AAE8F6B7FBF3B283F3F33F922DC0439A91EDE66727A9E093F3FE315C91E4F817CA20065GA5F36C16EDGFB819AG9CGB2C0B2C0A6409C0005G4F8194G2C8258BC975B1168A1F3B43B58B5F8C79310FCB2CB9BD3C8741166447B27A7E6BDDE8172EA668E9D8346579B336EEE861E8E93BE3E5F50B5447EF317F97B3D2B0B5E
	E906F6C796908CB72E0B2CA59537BBACE97DE4D257E68D5794EED142BE32B94156D0CF06705EA69BAB0F87C82D327957ABE11CFF29867EE0BD631CAEA9EEBE17B2B7BACF617B1B9EB2BEFB15106F2A92D0C579206C7384C8AF97964EA57AEC9E1C5F274F45F56A9B1ED87B7CCA54BE07976DDB772831FD7A792B5EBED2E6FDB45E3E00ABD6D7DAB02A6BA9976A5A19C0576AA1F4558D52D5640E55B52FA72A4B234DFB9B9368DABB042EB61DAE565F6753DC6F65F8BEC6D1DC924731FE58C106EA5EE676F46FC854
	07E0B4BE8ECB14D34EAE29D335099E0DE72F1C38BDFFAE1B3767FA59F8BCBEAA8F9E0FCF48466371E9D9BF9EB31CDDA6F56301FE6F5068AEEA38739A2E034AA438F3BE176A5570EEEA9741B7CAC4A162E5EA57E50AF3609E248EF79E3FE313C91F84172169A3EF3FCC6A5F6D13E576FE1D52788250C535F8A4D39BFDC1D1E777988DF7DE2B4BEB4E4175FB0BD3DF3F8CF73BFBB05D5DEEBD9D1BD7FD20623CAA93702CFDBECD52BEEF3B5D9EF26E24DB26D61DA17FE2FA8ED79DBEF252F624D5409C11F5D91FA3EA
	B60CB47C1E0EC09EEF6F857C197A5B89054C26B1DEED7DBA8FED7AAAA2E8B617C8D86E504A7BFA2365995ADCB1CDAB1FF4D9507A2D78F1A90DBF8C0D22CC2B1FFF035137B10A476A67637E7618F365C5A0B7FF5E5063BE6BE70DFD4416570036B497F356FB6AA7097A9BA0DBC2CAD08A29D79816FEAD075FC6475C812B84F97539B7123356472C9BEC40633DA13E49B139EEGF852CC78D8BB2BD587373D2FD4FD5B635F2B76919EED4E49DC456FD57B4A22F5613BCABF6FB98304818F13B743BBD847AC6F60FED0
	AE8D265BA18D264BB568AF5F0F69AF6C7F15395DCB9AF34EAB426AF0C85824FDD352C4BB73525E8335453F1AD6DCA414B489EB586056A41410912AC9D8CD8730A621988637BE12EDCA74906E912E518EF483D8D5D8CFB6874B696E203016EF2E42AF3E05CAF0EC2374E6245E8C165F673E2E6F6BABC7F5A66C6BED73D93B894E0F1F8F2E6EA6B14E817C227958BF4EF66DC954BFA09F1D710B328CB30A6E03819749CD072E28D2F41D32211B488FA38DAF6467D35D446EAE9D4F7F82D0CB878831A11393F296GG
	08C8GGD0CB818294G94G88G88G6E0171B431A11393F296GG08C8GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG2C96GGGG
**end of data**/
}
}
