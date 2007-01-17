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
		getMinimumTimeStepTextField().setEnabled(false);
		getMaximumTimeStepLabel().setEnabled(false);
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
	D0CB838494G88G88GF1FBB0B6GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E15DBC8BD4D46719B6A50FEE490636A6EBD2772CCDC8EAADE95233E4176DF1DB135093B7212DA1A4F5DBCC68063321DD533589C95D1666DC07C19945089193F26A8395930CC890B4A26283998CA88842E806B0821719BBB0F019BB5C3943CBEC777B6F7FFFF76E8CF79865EC4F69D94E793CF33F7FFB7F0F6FFFDD39225D0B7256190AB8EEDD9E477E0E2D63383EA60E7BE65D58924455FC18EA61F4FFFBG
	36F2CBA4B3BC1B20EC19B755D2487DBEAF9F5E9D701EB812EAF98FFEEF637E353B659C7C082027944AD23FAE5FB93B1EFBAE201E8D6A7BF3BB8B6079B6C09F6048B361C3227F3FF696B3FC8C43A32CB3F15C9DE504E67D2E9D8C3786E87281E7853017D1FA9FBC2981375656B0BA47D796F359CF4B264DC40ECEA6428A461B4D1EA55C6F56ADBCC0F4FD3C2B007114433B8B00B81F4B9DAFBD844F3A766101D34EFE0FD2B16694C43142AF09E302DCA139040A0100273656669991BA9541574EFB8591680BBFA2B2
	47F7652BB2B8AE8364452BB8CEA0FA9C70DB81087D8871AB5D841F853F390329166203685B498F6B186D76EB7F4C25D50D1837C4702D72A07549FEB068DB4A50AA537BEABD3C50C262BD9F4A12GD2G56G941627DAAA817E28567F2A8B9F41333EDFE910C6C6A4EF3D4FF7D6F4492D6025883FEBEB210461BA8427A43BB80E567151DD6586764C86142F612679E8CC96D7E3FDA7F4FC07CB7F47FBCC9B8CE2123E781A29291C46222FBC98932AFBBA226EF965216DB1D477E71CDE77725167F3738CF4E776BC1F
	BB1F49D92C531DD1CE6ADAD15B40F2783D0E45026267297852C3C1BCED1B6BD5BCF92F0332C50730ED585FC6DBDA4C77F305077F4EB08E8B1E08B519AB288DBBAA42EDA99CA4321BAA284EC31AAD9F2B78849D1E5A72225ACE49FB61472916694AD48BEDDFAFDDA4FDB56BF02A45DC15EA5981F08440866083082942B676C14B47B758465AF9D9B99B7008AEC166A86FCBAD6F42D318D0E43E596BD7F82FD338424B9E3EDF947C9C2349EAA1B1688F61FBD5ED7BCE5071296095E4DE71C8DE104E0F880AA07BA94D
	1BADDB890DAC282524BF0302919F07F25FEA5986CF1177AB17FDAEDE91029434FCFDAB2913B17E8AC88481701B4625201D44AB9E7CDF8A102245618B2DC45E85C10681233636C5120683BE9B71045BD1CD627CB5F56C48G7A35D598637CCB845787659355A8A729759D1D9C2B47C94C6765C96CE3D3EA5B09857A45G495558F6FEED4736E35D7CB0D77E7C5D06ED475E7BFCEEC6B5EDB3EB2B437B50ED2AECE2DF4911D44B19A3B8669788F61D0C58EA637E9489504E59470E7DA1BAF2EFC2BE01BCA6A7D2B90D
	77D887712F8F7C1A86D8F884E37EFAEB89ED43E7F93F002D36CD0686A73802B5C06DFCF7504E621A2B53CB6AE093G49D3DBD4BAA2176336833C477493DDBB81F6F3B4376E83588F70A140810003DAAC0E2ABA92413EF4G7351D4CBB9008DE09AA09EA085A09BE047A710C381C40079GCB81B2818A0EC1FF85F0G44GA4G2481649C43BA4DF4772D7B4B79F1B3F5777F99E82CCF8F10D87FA562FC682F40473F9620FD791797C9BC05B1412BD8851F60F5418FBFABDBF311441D659B3724713C2962FD101F9A
	A0A178B5FC4EC5D2AF3C22481E7E00A2F48A0A62718E844B4DEDC417BF506F8B26B65435290D5435B48E79C923AFECA3E33BD372FA85F5D051D8D879BBEAEE91C4E1840CEE1082DE05620B5B499CFCD818E439EDC77BFA0E64A6B1C072A41D57BE4A13766413BDA3B0D411D44BA9B5A46FAC59D52C0BD1A960487C3C891E0E9A4C9D637BB0F714CEFD0BDB566BB3994D0196BCF817E9E98D9D47D75484F38735E973A5E35B3729B90E617D5456DF59C83B8F1FE3672A74EFB413185AA4595B66F6A389ABFB715A96
	9D5FB95E29C8109869587EC8A7513F866CB29F07F171B80E6DF7DDA26518CF59205EA009A22D82A8F39CA7F1FAC6A8E2FE9E01771663B4CE64BDCBAD773A0BF43108E765E91A1E690EC25296045100G53981278AFE87575450EF7424A2E303A2365DF6EA0F5A76A668CE2AD51F9ED9057CB15352C5EEAE9EEF640F39760EBCE903AC68F52A581FE85409AC6D7F6027A102E526D9CC23A1893B437ACBCC1737C230C2E2F0E507DC023AB2B237967CC9D155B8E4F8A0D2E530BF4890CAE991EE9F561F4BBA5244BE6
	F485A7210ECE8669289F2FE8F4F5A7299CE3BF9E76A11D6DA44D0746FE340DA21DF81266CCE3BF364BDADC981D319FBF576896B2BAE3BF3E61C73AC4D6EE6C470002F4A9A7E98EB7764392C03A4C13B44F9B7B71930DAE0751997BF1779852951D22F446FEF40FA3DD7929207D28B7E85F1E8924331E22738E62075ACECF696525EBF493BAF9E8DFD05E7D13C897F70A4EDFD05E6290F9D72710AED9A78F7D884A5BF78D6952CF51F9904A4BE6726838747A81B2D62AFB83C37C98BFD1619F07713E36763CBAC451
	BD00225D790C3E70B42E754F6CD557DC705E87B0F19A4755E7B763DAFF7E70E2AEDECE4B0D340F91F71A5A1BF8BAB82E16770D6752316DFFECC4E7B614194F60F8333ED33F5E28777BA527C7DD6A30B16531CE7D38CBF15F6B5C88CF37C776AB4D1098A7F07CD956C96CA5EBA106E6EB43575475G99FB0BC1EFBB409DE7543524DB5D932AC5BF1D5568E75C5403DC5F5D7719B6C570B3658C75EF7519201F05AF9E4925715F7151DE0C3FD39A2970740FD48446C6AA78FE25C2F0F9145ADA9C7259FE4C03EE5C0F
	A92DC7DB2E1F5347FC69166B91EDF154D39B266BF536DCFCB3177A9EBF083EAF0572ECG1A039AD51D8A4BC19477310744C3997478298D2F551FC1FF3FF3BF1734A156E43447B3784D85267233D4376DEC5006040CD526E5G548E2F876D08871AF4G1A0F3E5F2D4FC7C58DF4DDD95700F6BE6FA1FBBFEE499970D7287F36C8FE250CB46B8AFE9C62C9A6C89567C8C1A379F9G736F0621D0DF9E5E2577A51F3F55501758DF8E185A9B288F938DC1DF4AD2D7196AD25117F3C3684BB2E2EF836E49951DA2CFF5BE
	2375452F3BCA38E02EAFF3C981480B65A5214F8354C62586EDCCC8FD0C338F7D2D210DEA9E3532BDB5EB50461A7FFEEAC38BG35B1CB33B19EE8522D984F36E19297DE9EB8CC5FCFFA55B841E4125AB05D05B64CCBC9640A375D99510622C62A3B2EB1E8C376CB2BCC79DA1DCFFB5106D820C9EEC49BDE76B29B2AE89B48970D5AC08E5033F8CBA827709C4C9D4E211C07A5A6271A4AF9E358C84E3CF3A8275007F26683AEC5433F20614D1FC25B7B9471932308D7G172061CF49084F845C0EA6441B7D083781AE
	D643BF2D6113811723616394449FE9063C590C782A00D6F700CB5770EBB5FC51F9185B1DC77C7747909F8338259ABEE69C715900AB3F0078739308F7915C850C671393242EC7BCDE4FC8E0C41B33D21E3CC964C9837A029614750C063782EE3A85E5D92708AC176066832292A66B36A964C9847A551A2C16EB08AFED051C5B0A32FE284E17C77809903B681A747D5D617B2573DB03FBB40BDBE76E5750FD44E4B557ADED257B08DBB4FC1A0A4F5461D99BD471643D60A2640F0B3877D5E4A739565C86F998208620
	9D4087905706B92E49D64DC559DBEC76AA1082F931B3E49106FBFCB69287D8B731F753E57DB8C36330D6A0F1C8E83B71BDAD4A375ECD7812EF020F0E9387BE571FED18371EB765C698A73219ECF3FBF01C20317BE197692FD6407781CCG4481BC5A0EF13B7DF2F9343861BEAB8B9875E909BB529E7A0AF6E3BF29BD8755DC1489B45C25D4CBB1C0A5C07BA53467594BD16B31F142309E19DD03514E95FC17C26D23FCC9D179660771D1FF3E60A0734584A8DB8E10811083D05281E39CC0DF877A35674A59E8FE55
	636601CD52FEB604EFG203D0AE7EEFE5E9215EF22A3140F66587DE544CF03792E8D525AB90FA03A68BC6B0DEE1CE745F5E07FFD73AA61DD8E6F19GB9G451D909FGBB40F4A74648FF65C4543AA79390080F9A12194121BE368C4DADB61FC765DB58E9548646FA09FF09D03682A08B40EC0375850095E050067EA5DFB99D4DBF6DE8A21A0F3F1E230FB9D1796E30997978E69F71F19114AD85C883D883D0F4997AAF006DB27A785EE7D15B395E47286D7C966F5C7C0C0B4AA7DE8E65236D7734086DB766B26EFF
	37C8F3ECCFD179169A5AD02E59102659702EEFEEB614C665AB38E24417BEC7FD3F0A4AD7EA286FCF23F353979B152F4ED01FD51E1B3E0E28FCB6C3FD9B7DF353D79C15CFB45437E20E7ADE0C4A97E3286F9EE5EE7A622372ADB454D793181B3EF3D179928D753DB5C7FD85E35178D28C75A5C765B356179A15AF53D05F5F0C4FCD5F0228FCB9067A5AA766262FA7AADF51E7C6FC1BA667266F7D28FC65067A1E1D233E172272D98D7545CE4DCD5FFDD1799C067ABA2E4DCDDFDFD4BEA5CC9F3D93D1296E2546C259
	A2G0A5B3C07601261BD7DB34C13CF6FA573944E8EB4GF5G8E00E900C5G49F6247B19CA17856F79DDB0D783E86A227B55E40F196C8B335C3F5797ADBE76203D533A5FBB6C21FE6801E3E5E8133E2CCE33F1B82AEE0B6E77A65D6F3CD9F8703E841EA70757911D476D34C7BE9BAD359B1F0DFEE4B7BE9B2D34EF25F810213F590365B576594FCE4F580D4ECEF72A3E3CE0A2FB86F240CB36D9D535F43DA2CC10F3F018EDF6843CF4AACAB47B4357349C4EEF0F7413FD2E41AE1C473EE02F62E64EE39BDD9EC512
	D108CD6065B637DB1B44F67A84A75A7C232E4871CB6BB20E5F4F3A0C631751B5FB7C323AA2450F76134755FD689870ED0166DFF5F7E47BEA3B0D6DBB53EDEC5F396E596D3B589D49BEDA072FA864AE430C336E0B30CA60F02F79970396C6532EC86D07B559DFF6C476632B8EE3BF96B8C85B77CB8159A9045B7A8F0EE22DAC38A8E9D60491E28E25D92C72AB3CBCA04C707591151F16C562CFF2103E5A6FF1F9E4F632CCFD7C3687FDECD42456439C0E193F6B1EED4CCC6E1AB94ED0BDDCCF6458EC68B10E4D661E
	4831A96A099E1B1DBD11E3D35A93BDB607FAA235959A23AF8EED42F6601625910892651B579B59575BFB0DFD3D33B7322F73FB237B7A773D11FD0D6F0D6E6B433D337B3AFFB84457F6741563E729D7BEC23D723354ABFF83754A4FD22F7C8D54ABEF5C667F459B6AE3952E4F44595A7CCA03B64F5A0794AA2F1A49BBBC2B3C559165357A74FD32CA43EF9B8DED07D58C7FD8A83E7D8843DF13C371C7993E5E9F0A7F844F56943D5EEACD6F0F14D03D558CAF85C2659CE37823E321789A067FFD987EB843BFBE2E57
	FBC453FB3BFA1E21EB0B1594FFF6D2CFFFD4234F1F8A23E7E7E1CFCE6969BF51683FF4AD0C3E0A62BF5DCD6EAF097C2494D06079B949E7BD3B711E92876F45GE7GEC1F5333A81A7F3FBBCE7CF34B7C88F4A9B5B1B7F5935E52BD481B88744B8132G72212C2C9B4FC262AC7ABBC7ADFC3F6E4E5137ACE47F3AB5747CC6A5E1770E54F2EB68190C2EFC5BC615BF741CC5D77E2425F826FCED0F15522CD5E942F404519C5AD8BCD30FC6C34F72366E531F6545F55378A5F66339F15928D179F22D3C4E30BCDDABF7
	F80C4A57F4639C2426FF33FE7CE3791E56DF5CA06937E37C10ED52A7B0DC6A0992AE527798416FB0681CE72B835786E6872E973AC54459B4DC13060BF7609959ED3B71FB10EC4015DF45B6F538AFE48F1C9C0E5756FEAAFBDCE77901963505527D3B9F2BEB95426F3A0A77BEF2D4198B61BD91A065AA5EC11878G6FA0A4BCF4BF3740EBFC6FA343F65D14F9150ED179BD3AFB7CC32BCCD46779BD28538A6553BD6A596A9EFD7CC93B788DC085GADDF92D2CEF2510110727410F2E2EF6FBAFD795BFB7565F9BAFE
	6A5BB37B51371CE79E654447E2A2FE1B134CFC4AE24F384CFB57E5BDB2456EFA3C6E0EF057C66DB7547F26FD44FF52E6CAFAE1CCD0F13D23F87F462E617C9AAECE43FD4D03386500EB6AD35B3E430AE3CC1FF1EE50C391501481ECE7F26A7A6859A681AACF96D1DEDC84F9F9BAF971E17212E6486B5564A5C71037C1A7EFE5183C55BAF9EC7FE04699EFB4E07B87F364FB6E26796859710FCB546F8EF476D37CCFD53C198F62E9DFB615E0DFAE04B20547B329D71D24CD2D0477EC00027ED4CBA90095202F9F4727
	E57D15DC14338C1B64EB01091828DB76D07F5A764C6D7B2647B7E01FC97C5E92AE69B6EB44BBC9CABF2D77796CD976723D6B48137AB7CFA07EA5C3D986005989639DC0A5C013937D4BF73233DF322C05D5AC6F9CD4EF00E90BE40FB09EC413A4CAFDEB5DE7641B43493E4DE34F98E730DDD2BE1761DE7D82C617601C590EA95F04A15FB2C61F96116FAD43332EACC61FE340C74703AE07F6EF4F15EA594D517B76799C7E2DB9BD93877D52C53E09CCA2D385D6173FA8403A5C716D87B86BCF2D26FD916AB256456E
	FA3AF45F6F0D1464D2FB9E90511EA241589E2AFB2F1753FC0853F8DAE665E9A4CB84962F3412606FD53A5F79A57A6FED7E2F016A58739755416E6EBBA32F1B7E60B4DEB7653A0C77BCAC2E59772C363AA25F77AFF6696F7B537E3A5DC7F2E98C5455A200E40015G2B856C2B8F3948FDC7FACBA03C1FB63A86483A0DFC9527207EAF8911FD0D9322FFCBF72FC06EFF76CB0AA20D90795A7E08C06ACB11FCAA122DF58592A359B3B028D0D206DFA21076A68AEE95CD67DBAEB5CF673BA1773AF18E7667D21C0315EA
	38A7FC6A5DC9F81F2F612E2939FBB13CA7E9380CD164CD56F0F72938228110B70038EC0D2ED243ED9FA638F9709E2761260611EE61G6E4376B8746348EA40F303335F65217D7FA75D7A7BBB057709A64CCD6174C5CCDE49E0307F53F611B4CA723C8D70BE0079G89G4BGD6822C8660BC2916A200F2GAB009DC08418879087908F106441F67487114C512E70C3B3F7B48149A633EAE312F98FE9CE2CFFAE8ED9BF27003C8C4F6CB120FCF70770E583CF7E506CFCEC3F41B74B7E33AFFA7BBD24369F815A9DD9
	C0D0DC150F2CA5A5375BAFE87D64182FD043B57B5BA53F07ECCE30B57BBE3C37C9474A7FA45D1B4D3F6EA94579578E83FF301EF1CED796B67F2A9C8A4ED3282EFF771171F9D012BDD312D76145FA51B360A53D1C9307083E57FDB83F2F9B42F56AD7C7C36D338402767DF6946DFBB402FD7A792B5EBED216BC433E0742F4150F85F5ED12D1570A883A72E65115B9C3D779F0282E3671202E2F8C232EB591F4ED18C517F9B8280B7677001A6B8B991E0D81AA2E0C61E8BF4CD1ED20ED665FFC7BA375A1980D4F4392
	252DFFC8F0AAB6FEC063C9990D5C1E1F9B0D5E1E53C70D47637F981DB99E3FB2EABC9E3FBA2A9F0FA96E35D1D2B783506F6D9A5DEFB5DC17064BD1E5925CE79E17B22861CD72C606EF9208C244672B7360F195E747BDC89D2E8B3FE313C91F84172169A36FFBB229FF37C794697B9FD59A0F973A28960F921958687171BA3B77EB38AE2DAEFFA04E2C5FE4D1DF3F9417E0C0775D90BABA2FFA2A9467D5D6405376B9BA49E9EDF763A8B9F752ADD3ABBA493F181E7D958D0D97ED0D9D9C6608C5637A9CD133719643
	6F6988CC8F3335B1FB66EC3FEF9D3529CC56562F1ACDBF1F62B41B6B34F2CE44722269E0799343D8DE23159F3B4EE97D167F20C00DBF8C0D3C28D6BF7B86E3E2F3900F554F467D14D27D386F8339B1626C63BE6DE7195709AD8B00B6D1441C75CF1387A375B7C0364B12CF1015C90AD53F16E3B69C8A392B138672322258C0796AC378F20027F064C6781E9A56731582CFCD94BE5A4EDEAE1D597632CA75ED0FFD2FFA1D70E8F3CE6AAAFE2FFA3DA8D8971EA975731ECEA0748ED0F9D6F9260EE6F9E6BF309B50DD
	B520FBF1F8A65D4B43FABA7A7F4A9C7012461C70AA17671739AD5AB7A54DEAE74E982B2E4DFD7901DC93E7EE665653412D19B313912A194BD38730E6AE97863789F290EE5603FF147B135A01EEG2BF09632B9D8C2F68795EE835BDC05DFEC8B156068C6694DC83D99AC3B4F7DE7FDDFDFFB2BB3E2DFB7CB345D94CB617341EF2873G9B60FD92760F5FC807A2758F48C71DA33CA8420CE2385E6BA239696C24A28457A98F7908FCBF5230C2F6BE251EFD8C69F87E97D0CB8788A22DE8CEEC96GG08C8GGD0CB
	818294G94G88G88GF1FBB0B6A22DE8CEEC96GG08C8GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG2696GGGG
**end of data**/
}
}
