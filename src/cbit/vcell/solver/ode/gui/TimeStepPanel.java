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
	D0CB838494G88G88G41DAB1B6GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E15DDC8BD4D4671DE76328182022A1910D491244B4E4CB327404ECBCD9FA0E6D322DCDCC5626E4E3A969F193F657765846CDCCEB7750534BG42784404EC499693E2CC2A8932BEC8CA28E9FD209185194008909198E66E4CDC1819BB5E398383026E7F3B5F773FF3E73843D54E661C1E651C3FF76E6F7BBF3F776B4AD53E13D6DAE42E643822D20E7D7D2108637A4FF05C7702E51FA136690BBC8B2779FB97
	E89777013F841E656DF9160445F9168A6EFAD9B93CD7433B1C1AE779AD7CFE037B744B165370A30E1D3D107674EF24EFCCED677533E827CCF95F33F99BBCF7G55GC61E5EF6E23FF1F395431B980ED4E46638B39D04674266EA06E5824FCA2092402BBAA87FC1F8D287E6B6B6B03E1E85695C46A7A573F622C72393A91949E631E78D771FC5BDB6E26B1A62A711D98FE9FB81A34197F31F6EBDG4F2356A1771F9C8302DCB76C603D5E3A20689D6625BA51495739C3C2E323CD70719DB29F305A7D3C977857A8BA
	3F3339DC5141F1E9706E3CC030C4A731D38D3F6B01DAAEA05E6FA2F8BA7CDE813466824676596F0FB25F3B2EBE4E0DBF7671561DF1E25BF00146D4559909AD7B2EFC73DBCAB9341FA379ED0334B0D092D0A650EA208DC03FD34A3F7B6C0770BCBEA01F94FDBE51FFBC90B861F5CA2D902597FEB7B6C28A435AF907A8B9B90E1631E9F32D0EBFD39115DBF853F2B4CF520FE0F9E734BF4265BFFA2739CCA7CF72532F1A4BAF323C38984993EA7B2938360FDD0C2E0F5136AFF2DA5B7563394525BA362DC2EE310D69
	71E8EC27DDA4E5DD285401C478DD4472026265BC418BB4B82D1B7394FE72DE88E97DDDD8B73AF620AFADA577F19507966966C36A37134CB9DD54077C2ED8DFD25DC4F7F9B735F9C07565F18577EAF06A0BCB292764FDB5249D3D14E721756BD2ABE92B69BDF9169C20FC200DC0151773AC87011AAEE39D3B6F6C7F5CE09D335AA579C4C870BAF9092332174E7D97BC7930AC591B7DC1596EF7701DF6C9308FF879A047F81C67C89E8CC44989CA5DF7000D4FF9BFAF59E5C17403F63B0F17F9A9C8F924F33B880F44
	AB2924BD0381DF00C33DE3675EA079E18F4A9782CE3B4CC7B8E87AAD67C8198C5BBBC1A3A8005FB4DF924F137CF2C27C63C0C9EABE7CDA51F70E172043E8ECEC914521D040C6A26172AF13FCFED169BB526077724B184726B602557466D99AFAD1CFFD4B6B9ABDCD020338EF17C6310D1915BA55827C9EA0D3AF561D1FF7E15DE95A7EB0D77B5C9D3AF5A7876AF1DAAF2DB34BFBE35B508EC5B771EFAD24F1FD586727B83BB4BADAFA755BD3BCC2BF276EBB9E0E32D1FC937A8915B2BD7152E9FE3FE2A57155C0DC
	C701FA7AB04F45169ADA07CF5803BC565AB689AA9C6F0C14G75739EF7974B57E20DDDD286DB0148B835D361A3FAB96ECD205FB27B4456EE20F7B8BA363E8774BE506F017683FD246645B7959BB2783794A887E8BDD0E5BF788D6484CA824A824A87AA3167D9EA01ECC063C04B00D681ED842A9E00368A6485CA814A9E40B2753A7A0B3E3AB8EE266C7EBF934D6B6FAAF97DD56473013F00987FD20836E53E1564A7BF4C7B65A6BE407B1D70A348523C2DA45F59F86392D599C94183B0BE1D0481A1286261D6D2AE
	F6D9160401104CF77032AC785D11748445D6B0B490088CEDE8EBF6ABA9EBF18446A715BF2915746D8E516F6715CEC595E169731431057772BEF07A2498724B94CFB9CF6660C37CA89B5BD24FEF664858648D11F1124EEBB36D24BE85A44187DD9599EA39FA8799F7FE254E4B8995107EG665D6510D66D4031E364BD9CBB760EBD442D688B187566C0298F4CB70FBBE8BF1E6C080C9D5427B9ED7A3E4FD746B80687292FBF3511FA9FBB47AED678DFEBA6F9EA93A5FF1B4B05ACAC6D47D7F7EA64CE5B9D328883A3
	6D5BBFEAA776B3412F9C20FC87766DDB2DA49D47D356291F943DDEDA8410D66DA4795441D732B8B7026CB6A74DA7721E2E247FC2CDA764E469A9CE343349DAC16A82FFA54443B4068C7C6754727A0D75751834CED6F6B43D6C8BD2F6DE4D1C2116A7B60BBD38DE5A406C1573F4EC2E06675B00EFD2781ED67942701E8C1449785671B40625AE42770000FCCDAEBA36743868B89FF03172F4933E4F8711EF1D0B0EBF1C1B6A2D00E71D4A77A39F72F95D144F844F94F7AC5F83FE644BE2FC2B60192F6123F108A272
	95B2BD7AF1FC9CC03ECA8F9D8F7563F8DE652B7550B1D3BF0E3B2F287942787463F0C8485743787463589FC4BE59C3537563F8D965CB7250B1DCBF0E85B272AD7350F1DEBF0E0BA1644BE6FC7AF16C9EC63EDC46279F47CBAA5FFACF44FF349B716F219164AB91683C03442154D3C12B4F99C63E86A1228F7D0B687BF094792C821D3F20BECF143EFFD579CC9AFD791374FDFD8C79168AF49E047A3218BE5AAF3D3A1F74154A5E4020FD589E2E8B0EC0FF5F58F8D66922689E00C359E7A17C2B85DC6BEFEFD556DC
	03F9169A2006C16CD73F3F9D577AA9C369DC45448F0A636DE3D887293F72E024DFAD30060AE95F966AA0B633A0ADE7907B1BDB3E502EB70E870322C3D016BA2CCFB9542E6DF7A976C97B96F83A84A9A8B7434098467E67E8BB71172C05984C5606E34AFA00743DEB40EE45D01E654C1032166415BDA19E63F49C41B813479E607A6FD8E25E9AA74E24A19ADF46D0A4CE531CA345B47F2BBF5C03796F90FDF542002FAEB46C2B338F48F53CD3109B9B314BE77BB187DC389FD3B0043ECC1C5666F9764E09383ED4FB
	298FC73D91DF1C652F955358EBBD987BB824E78151B168E91E58145998C43147BDA5CADB97025407D70E9FE3B6D6BDF29F373CAC492C374763F9B0553C1E592E74C5FCD83A6B85F3A6907563972A9FCE60D98AC4472347AFEB4723DC9FDDD7967A504F4B82597BF1090ED030CE7937C58C4A7BC8352E330FC0FE1289D25DE912F00A7C5C0F636FFCA1BA16075F5646D2EE1F299BCB52CF5C668ABF0D21419F09252A7885F3EDB146720C0031A4G4FF2BF6E49D57E09BC15790C5A966FF256F01131FE1FD38C4138
	D8DB937D5CCFFD149B50470CE79F633A8667697A280C234C37AAB162E32DFFD579B1A06AE3E0D04DEF60D9AAE2FEBEBFC87245AE398F5177FF74A9798413C96A43F8B77A1030AA0B2BFAE3EEDC9FF2196DC20D8F8595AF18B7D5E0BE1578510716G74618174E1484FFCB8CC6B402793FAF5A09B78D9FE2BFAD683D6FE8575FCACB2BDC7281E3F1774749C3B02FAD2C554E383ACC945FB830867GD628625C95446BA5D8FBCA08FFC7451781162F62C992621541BCCBCB907176A062A64032D53CCAC6FCA3E035B2
	62CF2838933025AA1E90C2BC9730EDA144CF8FA35E8458380AEF9BC1BC8B3075AA7E2C0A6F9D86EC987373DC181435CF708B3E10CF1D33D21939E114C9817ED543282BE394711291D8570F202ED523C41713F759C3DEB9C6570EB1141101BFE384F5652AF881E055E154552A4C17FD76F014DFF4CD6AA90B5DAF3505A3FBB41E70647D9A3A0F38C0996B4643F49FF1270A5F2B604BB4B8756D8D85A76F2BA06D4CA86EFDA5F71231B6875E5782ED822A984B339C84320E61981753FE04B358DBEC764BB0845A3D9D
	D10BB05C636BA079G6BA676FE2DC35B4F50FCC860C9BEF847EEFCCF0B4A4DD364CCB7A1C77B097DDFEA4FB6CAF61DB5974769A732186E1C31C8BFC1736EF897E92FA5D7E13E8154GE4858ADC45FCAB36559A659B6E33328C23B19DF6455BC3CF3E2A9FA7756741EE624FB260D9813486E883D045B87A53EAB3AC47D3E15DF2E4FEF90C4E958E0EC77BC7650E994A59E264E8BC3F7E124C973D1016881486148D3496289028E682633A5BF642A82E6338F9E093551FA7E3B7G505F42E94639C5B012EB18081623
	E36C7B7BC81CBA73DD9B8CEB27855E6B24732C1FDF46F916F5825B2F54C3E49361FD99D0B6509A20C2202AEB501FDD43BCFA5976C74332A79390489FA5CBA6E78E0DB157B0C67D3CF951D02E671ADE9DF865B209CF0634E420F4209C20F5C0A55773AC7B2EE3FC47AE7C59A8BE75E842A84661416945E8B214BBF3DDAFC6290F44588FE963C0A9C019C039C09B00AA93B0465BAF9856F3ED0C0675FC0BFFFAF1D6994A55A6C44B517A9B18407A5B14007B5F39537461C5C339F1DD9FDE115007945507FB44697910
	E1A837AAC1CF4E99181EBD49D02EC0575E66AB5333D7E9A8D728EBEF55B46D65994AD57E151E5CC2E9FA76528C65EAF56DF58527E74FE5A85724EB6FEDF9FA76EA8D65FAF46D2D18263DE78C65E4DDFB33C2533317E2A81724EB2FE5F8FA763EB414DB26EB6F751169592BB1144B563537E61A767E4DD0AED7575E6D6169593B57D0EE3D2E3D6E516959F39B4A155C225B8F0ECD4F5E9EC339EADDFBF95334374ED02EBE469E3D93C16F2C35C0DABF9045FAAC84B3417B52DBF01CDC245CC5D9C1F8008A012AE740
	9C93289F48B483799E24F7D660FDA550BA20F2935D2FCEE17B5B146F2D5600D17E4C0878FBD473BBBFA68EAD2DE0E96813B62DD00D25415036C573FB2B66F76994B2F8DF824F13E34B084E6396F746BF9BCD6B54BF9B7DDA277E59E8C667AE0A038E6D4D9ECC4F6A1C7A6CB43BD36F6CF437924BBF1B491E01947213EDD645ACDD2F70E1F28E8E334D76101FCEC50965E06C1A164379EDE6BF59675AE742F9EC7B45435C64F96CA927A00B92AA3171F6294D65D2A7319D815E01BEB7F7454FBFEB17FE7EF5F66967
	DFCF575479676C0A17FF341DB4ABFB5FCD90DB179A5F5FF647776F6F3A757D4B6E56776F5B5DD37B77E4F7BC7FE8997E3BCC6EB2CCBA6BBE8F2B848E771A850F0571D8E551FAC855DDF6A9FE9CBBAE6947D1F909547D2098129CFC2C2F3F3BD42526C596A54DB26FA36ED01E3D0A3CEC175C7C24D8BFD264E9DABC797ACB242D8E88CEC1E2A74BB446A38246F8CA96DB8FF158E73EFAF92ABE53E41A5C4FD0BB63BD7173E6C60FFE5E4C69091FB749BD46F93338A7FE5E247598674DC3BD716A8A4D232D03DB319E
	38A4518739C4658AAF470F3538D7BFD6CBEF7CD8ABFA0DE3FD2BB7FE2CB53D46316E6F1DBA560723E335E22C63FDD314EBDF1CF26D1B22DC7BEE20DC7B26A8573E9BA857BE7DBAFF5C9F9D63E1D667C393D35579D41DBA4F6A07982D6F8853774BA957D199F175BDA7EA5B64E1953FAB90DD8F8FB37C33AB51766B99DEACC5639FB37CFBB178A7F83696545ABD225A3DA0C75BBD4270DFC66356BFB07C1B21E83C0161B3062371CF995EBC2235DB2F5A5D291CCDE86A62C10ABF995672FF2C72A70F4670333330F3
	E3DA7ECFD47EDDD7E3788FD37C4EB2F2FF49EB9F95C3B22CF5E712716C61B23C2734824AED8D9087F865CCFA96C5477FEF0C1078DC125D87CDCA99184BE791592085E5E560CF84CA87DA8934EEA61E05A4DB34F70EDA6C831ABBC78FD9487EF5EB74790D42426E9DA969CD51E7B21A74B736A8725167AC1A74FFD07C0B512F6E31D21ECD8ACF0C0D981E83DB2AA65BD1F968D95E0FDB35E7F956D9B47F64D9F8EE7C0C2C171E2826AFF76B25AFD5532BDCFA691933F08E12553FDD5B7F31711E16DF3207345BE17B
	20EDB440B36C59BF56F0713E4708FC07C167BCDBFAF1ED10B38B578BC99308D54EC6ECC790B167ECBCA3FB2E8C3F874982EC7DEC2CD3077A2376404961F8E363671260BCE1F737A8B5146E5F6DAEAFE772FB93715E4739F22233875EE52024C43C03907EG6FA0E4FC6DBE2EE4D47F5E47DA5B04F9D9A26D23D6A6C64EF8D65F15EF26B697DB50E6499CD86F4CA158F7AD5A7CA77562BF006AC88CCA7A2F225249D834BFAA7D23AD5AF462EFDF11B67DF2D4FA29C61E4676547B98DB61D3191C77B1D35CEFF3CCF3
	587DB976CCAED8DC34EDB44C6EFA3C6A0AF357C6E9B7B47EB74F1378C91DD98BBAA8D69A427BB7D537A276329BB12B0A353990CB842C7CB6023D5B5B04FD4C2D7AE30316F201E79D501BCCCF612D74EC139055F77F846A335E262F2FD4234FF9DB343E70ED317A5E8F22BED39CFDE59AFD29B17AB2B47A68787823C9E73CC6C465FEB2CD39BD65B7ABC74F0EF7DB146F8EB47ED37C83854F516034ADFFD10EEDF9B52455A7611914B0C06AD4AA3CE7812D82AAGAA191BE729190B7D53D17BC14E60AC43A686DAE0
	A26655ACFB58FE3DE5FA5FB7FD2B8C5BCC5693FF43AD1F559477CED27DDCDA6EB6765C775262A272247195BAC8FCA6C8CB834A814A875AGD4BE8F63B3595959AFD95642AA566E70A8B74054C532400FC4E0B20852587E7B3CDEEC55735837F96C59B4AFD2AF59D90E6EDEE89763734E1BDC0F295CC6371EDC8263CF09AB77042EDCBA634F5611237DC1FB2FFAEF8FF85E6168FD7BF28E7FB67CF9B2863F6713EFA26B07B835AC7FE59B16E575576F671A1EEFB23F9727ACDB66333B1E73A3FD7B5E221AE26ACF6E
	847A13BBDF5F9FEA7B9EB2CDE1B1F486D1E6DB72D4B25F72F1EAFE3DE7097C2E557C3ED82E7D5E667F1A280D40D7EA035D5D9F083FEE9A9D50DFB7DD9F505F7318B9B0751E555C0178777DD38634777DE9FBDD60D7660DD0D67DC0A620D4200CE4EC2BFB9C643EA33DA5905BCECFB95DE45DC63E0A1351FE29B3FE2C5B1C465F523D69A477BF87C4D996FDC43F3ABF62A465A50B8185E4EBDDA749A3C9F0FBE44A4A708F1C243EF9F91782537956A1E51CDE89F12DC946B9D8CB9B4E418AD44CA3932CFEC11E4536
	G31F715715C836FE1951B95C2D9130AED7590AC975E8BD4AC60C13E8DAA76FDE5AEF0ECE11E453A9031CFD43E1E05388FFB31D75B0FE4G3EE26154F7F9E87B7F27CB5A7BBB95CB3CE69C1BE279F3193E358BE36F6F548748B8DFF9FB1E65A0108D488B14G148A14813482A897E8BDD0C9CA1E258A289668981095488994CE41FAB4BAC466E81D764149BB1AG32492CD219A43BC02A93EB1FBF0CDA0FA7013E34142973004ADD0ABACF4C82191586F2EC3F41BF457E335F387EBE64D76EB4C33DA38B880ABD62
	A7EBC951658A72EABBF954DF21E24DC12B9894486684DDC31DB90F77B6E9DFB9C01AAB1BFFB95AF07E152F938F16B34E6956454C8FB724C466A95456F519744F9ED19246C43FEC779E778AEEBFE965DC6D9D4A7CDE4679FDE18A2ED39BC2517E4D8CC67C3BBB047E856E5077CFBBFF557AC752CCF7447A374793EDABDD0E581A6DC1DB49F1EC15CEE1EB59A4DB6B5C51361E8AC5EC352A36B26358AA1B42D60E4696ED6FFEE52CDF4DF00A8595EC9D43E8BB8C8FF3EA5DBCA53E9F2F8DC1EFFC9616A8ED83033CC3
	36595D2A4C19C07C7A5C96B02E4FDD817D7E38B7B039BFF684747BE3A1206D0FA9668F1032F1C33B6FD27902AA562DE2E1C5A741AE8ACE59236257943F88FE06A78691B7A9FE0DA8D8976EC1EA30EE7C0EADC05AA4048CD59FE5EF8F1072F789DEAFFDDF247088FEE822EAFEACD5FC9482F60D5F772AD837DA169BA6A6176FEB935A722518941C4CB792547251F9D5CA9B4E2BCA6E043934D2BFFFB64229F5B7D3A667CE1AE5EADD8779970767E05D49D367ED275AB99CA352C634E3C44316DBF43F27A3F474CEDA
	5F2D6CD9786612A26A53B36ABAB7D1756962A8277A3C5C0369AB54746431C87AB5B5FD131A7E68D5CEED37768F36A9798FDD235D2B94BF7B86A33CBD02E37133FEFFF11B365FBF38A84F5234E86AFE1F36337EF162CB97704A0BF04CBA9C7EA8DEFB8350AA0981DE12C7A92AFCAD47FC78C154FAA38574259B78403E290E3ADB198DB22BEFC8EEC614DC8148ECB2102375AC33EDF25DFB2CCDDB7758772A63C4C61DF352D071FB55714AC8D988E34A67BD9D4068F7D3FDBF8DCD3671F2E8F2BB4875CC667B21E7B2
	5F929D3E3F0E62237F2F4C83DED219B3FE461586A5EE277ACDC9335218578E9FE9ACFE697EE2B3D7524CED261DDBB3D7C2FA28E62ED469401A39E2685C4264A03CC4CB41AB5CB525815DGAAF39632B9D8C3F687E52E0CED2E42AF3605CAB03AD1FAB3DAEF86E577392FEB5B7A26190E38EDBDE7B12DB7EB96474E87F70BA40FAB1740FCF089368F1F7FC03C768163D1074F6E75420CE268385FC946269323B29FD9276C91097EA07230C4F6BE251CFD8CEAE47E97D0CB878895F4FBA7EB96GG08C8GGD0CB81
	8294G94G88G88G41DAB1B695F4FBA7EB96GG08C8GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG2596GGGG
**end of data**/
}
}
