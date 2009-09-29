package cbit.vcell.solver.ode.gui;

import cbit.vcell.solver.ErrorTolerance;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
/**
 * Insert the type's description here.
 * Creation date: (11/2/2000 4:46:30 PM)
 * @author: 
 */
public class ErrorTolerancePanel extends javax.swing.JPanel implements java.awt.event.FocusListener, java.beans.PropertyChangeListener {
	private ErrorTolerance fieldErrorTolerance = null;
	private javax.swing.JLabel ivjAbsoluteErrorToleranceLabel = null;
	private javax.swing.JTextField ivjAbsoluteErrorToleranceTextField = null;
	private boolean ivjConnPtoP1Aligning = false;
	private javax.swing.JLabel ivjErrorTolerancesLabel = null;
	private javax.swing.JLabel ivjRelativeErrorToleranceLabel = null;
	private javax.swing.JTextField ivjRelativeErrorToleranceTextField = null;
	private ErrorTolerance ivjTornOffErrorTolerance = null;
	private ErrorTolerance ivjErrorToleranceFactory = null;
/**
 * ErrorTolerancePanel constructor comment.
 */
public ErrorTolerancePanel() {
	super();
	initialize();
}
/**
 * ErrorTolerancePanel constructor comment.
 * @param layout java.awt.LayoutManager
 */
public ErrorTolerancePanel(java.awt.LayoutManager layout) {
	super(layout);
}
/**
 * ErrorTolerancePanel constructor comment.
 * @param layout java.awt.LayoutManager
 * @param isDoubleBuffered boolean
 */
public ErrorTolerancePanel(java.awt.LayoutManager layout, boolean isDoubleBuffered) {
	super(layout, isDoubleBuffered);
}
/**
 * ErrorTolerancePanel constructor comment.
 * @param isDoubleBuffered boolean
 */
public ErrorTolerancePanel(boolean isDoubleBuffered) {
	super(isDoubleBuffered);
}
/**
 * connEtoM1:  (TornOffErrorTolerance.this --> AbsoluteErrorToleranceTextField.text)
 * @param value cbit.vcell.solver.ErrorTolerance
 */
private void connEtoM1(ErrorTolerance value) {
	try {
		if ((getTornOffErrorTolerance() != null)) {
			getAbsoluteErrorToleranceTextField().setText(String.valueOf(getTornOffErrorTolerance().getAbsoluteErrorTolerance()));
		}
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}
/**
 * connEtoM2:  (TornOffErrorTolerance.this --> RelativeErrorToleranceTextField.text)
 * @param value cbit.vcell.solver.ErrorTolerance
 */
private void connEtoM2(ErrorTolerance value) {
	try {
		if ((getTornOffErrorTolerance() != null)) {
			getRelativeErrorToleranceTextField().setText(String.valueOf(getTornOffErrorTolerance().getRelativeErrorTolerance()));
		}
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}
/**
 * connEtoM3:  (AbsoluteErrorToleranceTextField.focus.focusLost(java.awt.event.FocusEvent) --> TornOffErrorTolerance.this)
 * @param arg1 java.awt.event.FocusEvent
 */
private void connEtoM3(java.awt.event.FocusEvent arg1) {
	cbit.vcell.solver.ErrorTolerance localValue = null;
	try {
		setTornOffErrorTolerance(localValue = readErrorTolerance());
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	setErrorToleranceFactory(localValue);
}
/**
 * connEtoM4:  (RelativeErrorToleranceTextField.focus.focusLost(java.awt.event.FocusEvent) --> TornOffErrorTolerance.this)
 * @param arg1 java.awt.event.FocusEvent
 */
private void connEtoM4(java.awt.event.FocusEvent arg1) {
	cbit.vcell.solver.ErrorTolerance localValue = null;
	try {
		setTornOffErrorTolerance(localValue = readErrorTolerance());
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	setErrorToleranceFactory(localValue);
}

private ErrorTolerance readErrorTolerance() {
	double absError = getAbsoluteErrorToleranceTextField().isEnabled() ? new Double(getAbsoluteErrorToleranceTextField().getText()).doubleValue() : 1e-9;
	double relError = getRelativeErrorToleranceTextField().isEnabled() ? new Double(getRelativeErrorToleranceTextField().getText()).doubleValue() : 1e-9;
	return new ErrorTolerance(absError, relError);
}

/**
 * connPtoP1SetSource:  (ErrorTolerancePanel.errorTolerance <--> TornOffErrorTolerance.this)
 */
private void connPtoP1SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP1Aligning == false) {
			ivjConnPtoP1Aligning = true;
			if ((getTornOffErrorTolerance() != null)) {
				this.setErrorTolerance(getTornOffErrorTolerance());
			}
			ivjConnPtoP1Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP1Aligning = false;
		handleException(ivjExc);
	}
}
/**
 * connPtoP1SetTarget:  (ErrorTolerancePanel.errorTolerance <--> TornOffErrorTolerance.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP1SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP1Aligning == false) {
			ivjConnPtoP1Aligning = true;
			setTornOffErrorTolerance(this.getErrorTolerance());
			ivjConnPtoP1Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP1Aligning = false;
		handleException(ivjExc);
	}
}
/**
 * Method to handle events for the FocusListener interface.
 * @param e java.awt.event.FocusEvent
 */
public void focusGained(java.awt.event.FocusEvent e) {
}
/**
 * Method to handle events for the FocusListener interface.
 * @param e java.awt.event.FocusEvent
 */
public void focusLost(java.awt.event.FocusEvent e) {
	if (e.getSource() == getAbsoluteErrorToleranceTextField()) 
		connEtoM3(e);
	if (e.getSource() == getRelativeErrorToleranceTextField()) 
		connEtoM4(e);
}
/**
 * Return the AbsoluteErrorToleranceLabel property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getAbsoluteErrorToleranceLabel() {
	if (ivjAbsoluteErrorToleranceLabel == null) {
		try {
			ivjAbsoluteErrorToleranceLabel = new javax.swing.JLabel();
			ivjAbsoluteErrorToleranceLabel.setName("AbsoluteErrorToleranceLabel");
			ivjAbsoluteErrorToleranceLabel.setText("Absolute");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjAbsoluteErrorToleranceLabel;
}
/**
 * Return the AbsoluteErrorToleranceTextField property value.
 * @return javax.swing.JTextField
 */
private javax.swing.JTextField getAbsoluteErrorToleranceTextField() {
	if (ivjAbsoluteErrorToleranceTextField == null) {
		try {
			ivjAbsoluteErrorToleranceTextField = new javax.swing.JTextField();
			ivjAbsoluteErrorToleranceTextField.setName("AbsoluteErrorToleranceTextField");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjAbsoluteErrorToleranceTextField;
}
/**
 * Gets the errorTolerance property (cbit.vcell.solver.ErrorTolerance) value.
 * @return The errorTolerance property value.
 * @see #setErrorTolerance
 */
public cbit.vcell.solver.ErrorTolerance getErrorTolerance() {
	return fieldErrorTolerance;
}
/**
 * Return the ErrorToleranceFactory property value.
 * @return cbit.vcell.solver.ErrorTolerance
 */
private cbit.vcell.solver.ErrorTolerance getErrorToleranceFactory() {
	return ivjErrorToleranceFactory;
}
/**
 * Return the ErrorTolerancesLabel property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getErrorTolerancesLabel() {
	if (ivjErrorTolerancesLabel == null) {
		try {
			ivjErrorTolerancesLabel = new javax.swing.JLabel();
			ivjErrorTolerancesLabel.setName("ErrorTolerancesLabel");
			ivjErrorTolerancesLabel.setText("Error Tolerances");
			ivjErrorTolerancesLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjErrorTolerancesLabel;
}
/**
 * Return the RelativeErrorToleranceLabel property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getRelativeErrorToleranceLabel() {
	if (ivjRelativeErrorToleranceLabel == null) {
		try {
			ivjRelativeErrorToleranceLabel = new javax.swing.JLabel();
			ivjRelativeErrorToleranceLabel.setName("RelativeErrorToleranceLabel");
			ivjRelativeErrorToleranceLabel.setText("Relative");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjRelativeErrorToleranceLabel;
}
/**
 * Return the RelativeErrorToleranceTextField property value.
 * @return javax.swing.JTextField
 */
private javax.swing.JTextField getRelativeErrorToleranceTextField() {
	if (ivjRelativeErrorToleranceTextField == null) {
		try {
			ivjRelativeErrorToleranceTextField = new javax.swing.JTextField();
			ivjRelativeErrorToleranceTextField.setName("RelativeErrorToleranceTextField");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjRelativeErrorToleranceTextField;
}
/**
 * Return the TornOffErrorTolerance property value.
 * @return cbit.vcell.solver.ErrorTolerance
 */
private cbit.vcell.solver.ErrorTolerance getTornOffErrorTolerance() {
	return ivjTornOffErrorTolerance;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	System.out.println("--------- UNCAUGHT EXCEPTION ---------");
	exception.printStackTrace(System.out);
	org.vcell.util.gui.DialogUtils.showWarningDialog(this, "Error in Tolerance value : " + exception.getMessage(), new String[] {"Ok"}, "Ok");
}
/**
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
private void initConnections() throws java.lang.Exception {
	this.addPropertyChangeListener(this);
	getAbsoluteErrorToleranceTextField().addFocusListener(this);
	getRelativeErrorToleranceTextField().addFocusListener(this);
	connPtoP1SetTarget();
}
/**
 * Initialize the class.
 */
private void initialize() {
	try {
		setName("ErrorTolerancePanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(160, 120);

		java.awt.GridBagConstraints constraintsErrorTolerancesLabel = new java.awt.GridBagConstraints();
		constraintsErrorTolerancesLabel.gridx = 0; constraintsErrorTolerancesLabel.gridy = 0;
		constraintsErrorTolerancesLabel.gridwidth = 2;
		constraintsErrorTolerancesLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsErrorTolerancesLabel.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getErrorTolerancesLabel(), constraintsErrorTolerancesLabel);

		java.awt.GridBagConstraints constraintsAbsoluteErrorToleranceLabel = new java.awt.GridBagConstraints();
		constraintsAbsoluteErrorToleranceLabel.gridx = 0; constraintsAbsoluteErrorToleranceLabel.gridy = 1;
		constraintsAbsoluteErrorToleranceLabel.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getAbsoluteErrorToleranceLabel(), constraintsAbsoluteErrorToleranceLabel);

		java.awt.GridBagConstraints constraintsRelativeErrorToleranceLabel = new java.awt.GridBagConstraints();
		constraintsRelativeErrorToleranceLabel.gridx = 0; constraintsRelativeErrorToleranceLabel.gridy = 2;
		constraintsRelativeErrorToleranceLabel.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getRelativeErrorToleranceLabel(), constraintsRelativeErrorToleranceLabel);

		java.awt.GridBagConstraints constraintsAbsoluteErrorToleranceTextField = new java.awt.GridBagConstraints();
		constraintsAbsoluteErrorToleranceTextField.gridx = 1; constraintsAbsoluteErrorToleranceTextField.gridy = 1;
		constraintsAbsoluteErrorToleranceTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsAbsoluteErrorToleranceTextField.weightx = 1.0;
		constraintsAbsoluteErrorToleranceTextField.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getAbsoluteErrorToleranceTextField(), constraintsAbsoluteErrorToleranceTextField);

		java.awt.GridBagConstraints constraintsRelativeErrorToleranceTextField = new java.awt.GridBagConstraints();
		constraintsRelativeErrorToleranceTextField.gridx = 1; constraintsRelativeErrorToleranceTextField.gridy = 2;
		constraintsRelativeErrorToleranceTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsRelativeErrorToleranceTextField.weightx = 1.0;
		constraintsRelativeErrorToleranceTextField.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getRelativeErrorToleranceTextField(), constraintsRelativeErrorToleranceTextField);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		ErrorTolerancePanel aErrorTolerancePanel;
		aErrorTolerancePanel = new ErrorTolerancePanel();
		frame.setContentPane(aErrorTolerancePanel);
		frame.setSize(aErrorTolerancePanel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
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
public void propertyChange(java.beans.PropertyChangeEvent evt) {
	if (evt.getSource() == this && (evt.getPropertyName().equals("errorTolerance"))) 
		connPtoP1SetTarget();
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
	getErrorTolerancesLabel().setEnabled(b);
	getAbsoluteErrorToleranceLabel().setEnabled(b);
	getAbsoluteErrorToleranceTextField().setEnabled(b);
	getRelativeErrorToleranceLabel().setEnabled(b);
	getRelativeErrorToleranceTextField().setEnabled(b);
}
/**
 * Sets the errorTolerance property (cbit.vcell.solver.ErrorTolerance) value.
 * @param errorTolerance The new value for the property.
 * @exception java.beans.PropertyVetoException The exception description.
 * @see #getErrorTolerance
 */
public void setErrorTolerance(cbit.vcell.solver.ErrorTolerance errorTolerance) throws java.beans.PropertyVetoException {
	cbit.vcell.solver.ErrorTolerance oldValue = fieldErrorTolerance;
	fireVetoableChange("errorTolerance", oldValue, errorTolerance);
	fieldErrorTolerance = errorTolerance;
	firePropertyChange("errorTolerance", oldValue, errorTolerance);
}
/**
 * Set the ErrorToleranceFactory to a new value.
 * @param newValue cbit.vcell.solver.ErrorTolerance
 */
private void setErrorToleranceFactory(cbit.vcell.solver.ErrorTolerance newValue) {
	if (ivjErrorToleranceFactory != newValue) {
		try {
			cbit.vcell.solver.ErrorTolerance oldValue = getErrorToleranceFactory();
			ivjErrorToleranceFactory = newValue;
			firePropertyChange("errorTolerance", oldValue, newValue);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	};
}
/**
 * Set the TornOffErrorTolerance to a new value.
 * @param newValue cbit.vcell.solver.ErrorTolerance
 */
private void setTornOffErrorTolerance(cbit.vcell.solver.ErrorTolerance newValue) {
	if (ivjTornOffErrorTolerance != newValue) {
		try {
			cbit.vcell.solver.ErrorTolerance oldValue = getTornOffErrorTolerance();
			ivjTornOffErrorTolerance = newValue;
			connPtoP1SetSource();
			connEtoM1(ivjTornOffErrorTolerance);
			connEtoM2(ivjTornOffErrorTolerance);
			firePropertyChange("errorTolerance", oldValue, newValue);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	};
}

public void setupForSemiImplicitSolver() {
	getAbsoluteErrorToleranceLabel().setEnabled(false);
	getAbsoluteErrorToleranceTextField().setEnabled(false);
	getAbsoluteErrorToleranceTextField().setText(null);
	getErrorTolerancesLabel().setText("Linear Solver Tolerance");
}
}
