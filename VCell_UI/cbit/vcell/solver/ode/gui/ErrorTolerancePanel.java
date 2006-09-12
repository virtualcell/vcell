package cbit.vcell.solver.ode.gui;

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
	private cbit.vcell.solver.ErrorTolerance fieldErrorTolerance = null;
	private javax.swing.JLabel ivjAbsoluteErrorToleranceLabel = null;
	private javax.swing.JTextField ivjAbsoluteErrorToleranceTextField = null;
	private boolean ivjConnPtoP1Aligning = false;
	private javax.swing.JLabel ivjErrorTolerancesLabel = null;
	private javax.swing.JLabel ivjRelativeErrorToleranceLabel = null;
	private javax.swing.JTextField ivjRelativeErrorToleranceTextField = null;
	private cbit.vcell.solver.ErrorTolerance ivjTornOffErrorTolerance = null;
	private cbit.vcell.solver.ErrorTolerance ivjErrorToleranceFactory = null;
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
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM1(cbit.vcell.solver.ErrorTolerance value) {
	try {
		// user code begin {1}
		// user code end
		if ((getTornOffErrorTolerance() != null)) {
			getAbsoluteErrorToleranceTextField().setText(String.valueOf(getTornOffErrorTolerance().getAbsoluteErrorTolerance()));
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
 * connEtoM2:  (TornOffErrorTolerance.this --> RelativeErrorToleranceTextField.text)
 * @param value cbit.vcell.solver.ErrorTolerance
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM2(cbit.vcell.solver.ErrorTolerance value) {
	try {
		// user code begin {1}
		// user code end
		if ((getTornOffErrorTolerance() != null)) {
			getRelativeErrorToleranceTextField().setText(String.valueOf(getTornOffErrorTolerance().getRelativeErrorTolerance()));
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
 * connEtoM3:  (AbsoluteErrorToleranceTextField.focus.focusLost(java.awt.event.FocusEvent) --> TornOffErrorTolerance.this)
 * @param arg1 java.awt.event.FocusEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM3(java.awt.event.FocusEvent arg1) {
	cbit.vcell.solver.ErrorTolerance localValue = null;
	try {
		// user code begin {1}
		// user code end
		setTornOffErrorTolerance(localValue = new cbit.vcell.solver.ErrorTolerance(new Double(getAbsoluteErrorToleranceTextField().getText()).doubleValue(), new Double(getRelativeErrorToleranceTextField().getText()).doubleValue()));
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
	setErrorToleranceFactory(localValue);
}
/**
 * connEtoM4:  (RelativeErrorToleranceTextField.focus.focusLost(java.awt.event.FocusEvent) --> TornOffErrorTolerance.this)
 * @param arg1 java.awt.event.FocusEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM4(java.awt.event.FocusEvent arg1) {
	cbit.vcell.solver.ErrorTolerance localValue = null;
	try {
		// user code begin {1}
		// user code end
		setTornOffErrorTolerance(localValue = new cbit.vcell.solver.ErrorTolerance(new Double(getAbsoluteErrorToleranceTextField().getText()).doubleValue(), new Double(getRelativeErrorToleranceTextField().getText()).doubleValue()));
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
	setErrorToleranceFactory(localValue);
}
/**
 * connPtoP1SetSource:  (ErrorTolerancePanel.errorTolerance <--> TornOffErrorTolerance.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP1SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP1Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP1Aligning = true;
			if ((getTornOffErrorTolerance() != null)) {
				this.setErrorTolerance(getTornOffErrorTolerance());
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
 * connPtoP1SetTarget:  (ErrorTolerancePanel.errorTolerance <--> TornOffErrorTolerance.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP1SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP1Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP1Aligning = true;
			setTornOffErrorTolerance(this.getErrorTolerance());
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
	if (e.getSource() == getAbsoluteErrorToleranceTextField()) 
		connEtoM3(e);
	if (e.getSource() == getRelativeErrorToleranceTextField()) 
		connEtoM4(e);
	// user code begin {2}
	// user code end
}
/**
 * Return the AbsoluteErrorToleranceLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getAbsoluteErrorToleranceLabel() {
	if (ivjAbsoluteErrorToleranceLabel == null) {
		try {
			ivjAbsoluteErrorToleranceLabel = new javax.swing.JLabel();
			ivjAbsoluteErrorToleranceLabel.setName("AbsoluteErrorToleranceLabel");
			ivjAbsoluteErrorToleranceLabel.setText("Absolute");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAbsoluteErrorToleranceLabel;
}
/**
 * Return the AbsoluteErrorToleranceTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getAbsoluteErrorToleranceTextField() {
	if (ivjAbsoluteErrorToleranceTextField == null) {
		try {
			ivjAbsoluteErrorToleranceTextField = new javax.swing.JTextField();
			ivjAbsoluteErrorToleranceTextField.setName("AbsoluteErrorToleranceTextField");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
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
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.solver.ErrorTolerance getErrorToleranceFactory() {
	// user code begin {1}
	// user code end
	return ivjErrorToleranceFactory;
}
/**
 * Return the ErrorTolerancesLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getErrorTolerancesLabel() {
	if (ivjErrorTolerancesLabel == null) {
		try {
			ivjErrorTolerancesLabel = new javax.swing.JLabel();
			ivjErrorTolerancesLabel.setName("ErrorTolerancesLabel");
			ivjErrorTolerancesLabel.setText("Error Tolerances");
			ivjErrorTolerancesLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjErrorTolerancesLabel;
}
/**
 * Return the RelativeErrorToleranceLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getRelativeErrorToleranceLabel() {
	if (ivjRelativeErrorToleranceLabel == null) {
		try {
			ivjRelativeErrorToleranceLabel = new javax.swing.JLabel();
			ivjRelativeErrorToleranceLabel.setName("RelativeErrorToleranceLabel");
			ivjRelativeErrorToleranceLabel.setText("Relative");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRelativeErrorToleranceLabel;
}
/**
 * Return the RelativeErrorToleranceTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getRelativeErrorToleranceTextField() {
	if (ivjRelativeErrorToleranceTextField == null) {
		try {
			ivjRelativeErrorToleranceTextField = new javax.swing.JTextField();
			ivjRelativeErrorToleranceTextField.setName("RelativeErrorToleranceTextField");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRelativeErrorToleranceTextField;
}
/**
 * Return the TornOffErrorTolerance property value.
 * @return cbit.vcell.solver.ErrorTolerance
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.solver.ErrorTolerance getTornOffErrorTolerance() {
	// user code begin {1}
	// user code end
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
	cbit.gui.DialogUtils.showWarningDialog(this, "Error in Tolerance value : " + exception.getMessage(), new String[] {"Ok"}, "Ok");
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
	getAbsoluteErrorToleranceTextField().addFocusListener(this);
	getRelativeErrorToleranceTextField().addFocusListener(this);
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
		ErrorTolerancePanel aErrorTolerancePanel;
		aErrorTolerancePanel = new ErrorTolerancePanel();
		frame.setContentPane(aErrorTolerancePanel);
		frame.setSize(aErrorTolerancePanel.getSize());
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
	if (evt.getSource() == this && (evt.getPropertyName().equals("errorTolerance"))) 
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
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setErrorToleranceFactory(cbit.vcell.solver.ErrorTolerance newValue) {
	if (ivjErrorToleranceFactory != newValue) {
		try {
			cbit.vcell.solver.ErrorTolerance oldValue = getErrorToleranceFactory();
			ivjErrorToleranceFactory = newValue;
			firePropertyChange("errorTolerance", oldValue, newValue);
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
 * Set the TornOffErrorTolerance to a new value.
 * @param newValue cbit.vcell.solver.ErrorTolerance
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setTornOffErrorTolerance(cbit.vcell.solver.ErrorTolerance newValue) {
	if (ivjTornOffErrorTolerance != newValue) {
		try {
			cbit.vcell.solver.ErrorTolerance oldValue = getTornOffErrorTolerance();
			ivjTornOffErrorTolerance = newValue;
			connPtoP1SetSource();
			connEtoM1(ivjTornOffErrorTolerance);
			connEtoM2(ivjTornOffErrorTolerance);
			firePropertyChange("errorTolerance", oldValue, newValue);
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
	D0CB838494G88G88G490171B4GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DBB8BF49457F5828411ED713141A0C0584BDF46F2AC47B8A80E6A62DF6D5A1CDA0EF10C931D164474A03754A6ADBD25BDEAB37A990C04AD472241B53115843B00294CC7F69405A0A42D44CA2C2CC5DA1695A8B1333BA3E9E134331E9D014437773D7B5E6C6CEAC6EB54531EF24EE5F66E3B775D4F3B6F3E7B5EBC89159F5ACA8B0BAA852130D4E07FFED3A88829C784617A66259247CD8BF94A84433FDF
	823CA37C19D082CF9B345917F94AAA04554AD6F8DF8E6F01FBBCE5BF075F6F8AAFAB0B6A6007051C2B5056397DDB0E5165B49F65F236507767CE154373AD00EC4011E709C2642FBF35136173981EC3E111A02C23B4C51EDD8C670077F3GB600658A522F02671BD472443ADAC657F3FB0630699935E8BB694750A7077DE164F531E71570AF051B43C456A71EF24613826FAB8108714542C73FBF8C4FA32E8BFDC77D3E20D6FD51AF49F2F5C411AFCAEA3592102A7B8603F5F54D2A2A28EEC516D4B16417DCE2C812
	856DAB52EF4433157683FD403B2092DC2819484A0677DC00FC9D6FB8CE70F6785D8320295C3E5F7F6791263F774A8AE1DD5F13EF3EEDE1DF2A0AF6ADD72376A56F73953FC747E2E2BD7179C6E82B0CF84A76GB881E400D40077E98C249E5D874F861F56248C8CA82106F038D18E286DE0298C3F6B6A200561BAA53F22868481477920E73F09BE238172555FB49F7AA463B09F734C4EEF8AF98F4EAC5AE262133C0CABC50BA2680B1CC854A7A8FB48D2F6DEA4B6A6E3E51F960C3205D78FEDA9B5113D6B7B0736EC
	E47D14E8D159C7B4B256BF26B1309BFE97B2DFA07EB50ACFB260B1BEB50AA76FE950D6207158703E45F5F116AC90AA8E4EB275C35AE329C56786D1072141F8DDAA9D246FC503A873FDDD17FD943F4E00C7DD3E20F1CA5EF7DF04583D64A943781A52C066EB9E3CEF862898029883F0G84G0606F80C3DF874532F99E3AED1559A8703F2C0D285641DD27F6F701406B4D5EC8DC5B4B25DBC229A94FD329491984D2CFA6283DF8CDFBA0DFDBF48B8AE05E01AEAC1A5843D0B8312A6299124D9DC7F8E21D1A55A6A8E
	8EC8A0E0A0AC70FE6F2BFF971E329851CE058322A6C5A9307D21FAB2A697C58F74889D40EF744BF79B093F52407EAC00153A9F8E9CA37D1D10D4C897F5F5CEC539B098F693CB040AE1626729B4F72C867A8DC35C47AB1A884E8D6D72B06F276F584F8C7D5803FE223E288E73B9160FB18574B600A706F96C3C666531E35FFE1F307F45BBCDE3E79A44F17EB0460CF0B9FE8EED22FD937D76C01B63B24F7B171B3D8966F19253057C368D1B4F3751F3477404B20A99AD79DDFA937D3F250F5DC7543EFE30AB99E051
	E56E7385472AB0069B4508442336C3050013825191C0BD7F43D1437CDA4C74A0F8B286E481A5EB555B140E74AB88BBGFE4E64A37F814A1F8332578114DF711455G74GA483E4822C86A8G58FD95F2BA008610FE150F057BF86E98655F0C3F7E2F81ED3B0D5AF633F63D7F7FC07F51GE3AC2D0168AFDD14C21ADD8ACB21G7C0830363B9B081DAC8F76AABA4FE20A8FC35EEC02C495517177B690BF081A2686FD031A54A5E9DAB05497EDFF104A0A8C7A4251144BE5BD5CC0FC2BDC023C29537FE98349B9FEA5
	9412E8306BAC2C7DD11A73A4D99AG251B1441100678279AC9FDF8C19AE6B9775946B78412B365C1123F31664A924978055560GCCA13284881557C8BEDC6259E9705193905F24F64C07E741D51E53AE7D1A67345D176F91D6FE99AEB2DB1B5396CFAD6A3906B9A7FCAD1A53D0271CA6F35D9F23391761A328EB739FC8EC1955FF4514E76FD992F7EEC58DF5747646123138FDD7A8366F9851C70B6857941242D99E7AE38B51E99A683A88A0679A4FC3C76D241D67FE16001A94D946E10136826A3B5F36D4B25BD7
	437B06EB68BB721EF21D347F255ECE40FE9D5B356BDC4EA74D95A4BE242F86A5500EACD2A774B1BC547C3338B68F9BCFEC2FEBA663A99B56374A9BC4660003577739CCDE7EF5DCC7CA207D8340E7D33A5F34F23AF3B730FD881EA9G99B75086F79221ABED63F4A5C918ABF7A5A17D81F8D66B7DBD26535912227DE5A7457717F202532DC9421C4B7B5B9853DF331353D50EC33D760F0B76CB709963885DFB6D1CAEB08EF3B7690F2C19D7479965FED7275BB48E73FB7978283D55BA5D09131CEE57F8242B999F35
	A32A5F0ECECEE7E76D667A7DD0276B990FEB0939FE573AB81DB69E579BF37D1A5C1CAE1551456B07F3689F7612F9CD77D867450B62D0F5649264263A3AB6BA11F09FD5F1FAAB23DFB41E6F170E1CA1F3A28F5EB78114CF60B960796DFC3FB463C20610776A41ADD67B415D9350AE4704E88E183631618D1CF39947094C94E8CB1B406741C34D461A2DA192D17CC1DAAE32D88F3498F3846202AD5B60599BD4A3DAABA471A1BEAF149622AF29A7991A55576768DCA739A193642E835815CC7073DAC86EA4F3846D74
	9F66F6CE3B3CD8B8F7671C22B28BBB4FA523FDC949D1BB77DF5FDC0A7E5F356FD75C7FFEE520BA689B289E3CB8D0AD7A34EAA990546A6A74E404E33163B85F53E6A7F3DD2E3598FD1E7376B5CBDDB6B29DAAA6C6F5B9263837206DB39D5CF6973427GE00EFC300550CAF189945BFE59CA7C227587A3284B5FB79CE332D6FDF301103DA53548EC3F5C3FB42DE8A553A15F20CB5AFCDFF1A6G9B0BD62ECF6DA4CFD9F8925767A9DDA6664F8FC4E37E4C1804B5FB2ECE7FBA5243020DBA5E7701D1472D62C4D39DD3
	DF6DABDAB789F5AB7FC6D4474A693E62BD53390E55EDDCC7B75024FE031FDDD49EA5CF3A368A5CD7CB5B487EC974C594994A00788589F53B5A4DF5CBDA35DC58796EE4D35D689A4CF45AE050ED2D4DD73C4946F57BDBDD37A3A9301EG203F2EF51879AB935A116F01931CEFA54125F0BF6EF3927DD5C906C9F7F1047E489BF2F25E8337F84A7AEF613CAFBA4959C62F629F0CD4537F1DCAC45BC3D2CE35F889E21D94DA55AD2421197C5C4B7BEBEC677D59202FB537F07F16DCA1EBD42C8EFB824AA02CF07B2B70
	39177BD42B65BE4D7C5EC302777C94CB1FD65C0A3E2C3DB56A5354343FA9328120BE4BDCDC1FE4204939155B774EC992E3225AF7101D991C64F41B00E67FED1C6E071C6E90529D6D64F48120C93F0D0FC1C997472F86DCF9AA47AF5171F640DD5571321B631783EEC3AA5FFF6CB89DFF4E32FBF2F43FD7BBF9645E8F4F9F32E8FEF7CE467307B29D7F88454B86BC9BA70AA76F295036F1B25FB3BF5EC656977794183FGA9GB60015GEB27703C7ECFAD641CEF54B309561086E9DF143BE20AE4FEB6F01C588DF5
	ADFBFF6038F16E219FFADB089F8A26FC7D3DAE72E9142FF26A576743185BFB46F8AEDA72CEDBD131C54C59D95F6E295118C35FE9CEB2CFE7GBE8B60E90035G42B46E374F9D7B93790D1F4FB00731BD71F12B33372AE966F622BEBFE8A77A54G0D8B209FA08940266B1364C8B80E4DC3266308FA25BB921DC72E0C538F7916A564DB974707769C68A0B5D281346D3C9D6AD4GA7C098A095A06BF6EED72E23B111DD8DFCF367D6741FCD719BB4BD96DB47E62716106F695BE37958B97A9EE227C90D6706A531A5
	A849812CADCE093C36D8FBBB1F3F0BDD04F7679DD0BF82B8817A81D281B2GD65D41FD742B565FA59CFB125841BF54A5A31D03B656340D4DB70D8979B65CE196839F513A3BE43A272C8A2096408DB084B083A0FBBA37CFEBCD98837A11665709011F0C514E1FA6644B1D9E4B07E33B3C030FED7EF43EB62CB8B1B69D1ECC48D7B543B4FE1DE313B7BEA1DF2D29BCC77B5864F9927239E6184552C23AB76B0736E400F400EC00BC004DG3B6E643174D4EB7D4D44522873A54BB5B69B9FC948F764CEB33E4BA747A6
	AFA5A11F5BD41E3DF3EC72BA937249267236F50DCD5E4E04FC492672B247A86F6104FC692672D4775864A5A564DB9EA78F3F236C27E7872B20EDADG625E6CA6381219B0A7E672DC746DB32436B8866F67GD2G32G727476A7E87B56D91E32BDG0ED9F866C14EB358DCBFB35A7788521F71BDEFE662FA0E68F10C6901B244D1E5EC09FBAFCB50BF81DC1F31E6445C302A4D7A5C7905B673F367175B4C4F1D7F2A6D9D44C39F462FF93CFDFD5B6867522F37191DCB7F026A7C7DA2D26F2B03A1ADB8A0D131E80B
	B4C46EA2C0866B9C8CE1FAA312A371752840F366ABEE329F6A1F45F36344930704113931B990549415F76216C4352337D7DFE43B42121F6BFC55E96D3F89CEF37F5D62B4775FD467687E1B6534729F4679C1FAD6138C362569767D385DDA3FD75B4D75FB3D5DDC3FFFEC9FDD3FFFED37528F47F03DC64E3EC7FCC77882D69E011FD93C60F8135138B445F5D06F3B2E435A0E068EF3BB0EF710334008B2287A25F8DD3F68582937C5573AD6CD9AA06AA04DE94A2F4166D99AE16B9FA9BF36D97177F510B9698B8602
	AABB21C79B6735F29B1BB5257D2040F35ECA5FE8B9EF552C11B984655C6F32764D37DD663E49F1D97B66F1D7E25F2CF2D97B6685D7E25F64392CE285FDF4212D0C47C12F2A8CC0A4A05F271D5636FE5EE9EE6B11CEEBDB1BBA935B6A6C3436352BB3312DBD1D66F1306704B18E8E69F1701D0E5162E02DC99CE0FF2F1D087559A1063F57E914F3D8273F519EC35F4EFDFCDF17350F17F7197B7861AEEB9FBF5A1558474FF4D97B78792E44BE7EC1576871345E956B1B438C7F4049D8FC8D43DF69B47A2CC66F2721
	AB56E75C472F4462DD1FB17CBD6ED87C678C5FF41AFC8B1545E1E5D00335BF0D646FB32779B74FCDB04665002B81F02461F9A92EF78FDCA27D75C2358E6651056848EC42DB554DF9B300BE87A08F20GE057ECFE9678D9AF29E9E24FB0A3CE51A749585E51CBFC59E0FADECC49186EA9142E5374DC56C8F72177C34B7E7423GC63B08521A779925457364FFBEE3BCCFCE1E0D7E49184D4F1B27784D5AF3747639BE33765C59FC4DDC6F5EEE0CA536BE217F277513B9FAD1BC6F9E8ECB9C977CD8303AB798FFDF10
	2F55AF79F8CD3AEEB62FD3F3BBB82EE68EBFE79DFF1A5FD5CC86DC4E9C9E87874F451C3110C36D3A3A63EAB050A876B9E9D461BE7C99DA8F937ECDF37837344D344F7DF3BDE5F6001E397C3B4E50477C3BCE661285C26EDF1BFFCBDB6B3ED6244D459C14B65770ADE51F2F98E536F4F3192B213DBC1D1ECFF79BFDCE467CA7G55G583EA4261D4C653DB16DEBE25A093E8D054676EDB1B1D1EA60C75B1E7D085BD670EC16A0BF14ECF9EF541D4E6E4E31E755DF4CAE5C1066FA03FD3F6C3578FEC9839F6DBFF016
	58CF62A48B7AC05C13FE7ECDB3DF47FD4F47F1D573BCE536F974CE204F4E74704EB34F73C61001A689E0876BE746BCBCA7A7007D2D6F607D65DB74D7EA682FA02E3F0A3B227D31FD7508737DC400FCB34748775CCD7361F703E73CA446F79974C77C8B943F470047F976EF7ABC2B0136343B78F9E4BA3D07D2G6FBB6F76149DGF0G7483A45F4D7305F26A001060FC452D041D30204A0632996DBB51BD36BB310FEE61713C7C61E5C276A43B65B758343BF15C3379F3556CC272C47BBE75927B72212D4486FBDD
	00E3GEEG59466DDBF10A1D7B13ED916C02C4FFBF7D622DEF32025225A81AACCAE85BE7E74DECCB32219EB358B343960DCB64FB46EF46174368F2EDA3639879DEB22D1F57B17A4DA6FCB88FBF5467E679FCCF5907825EA15BAA70FF9B6BC7628441BE1F5C096F7BD2504764C765FCCCF65D3BD0303FE4AF7A354518244CC7DD564C0F66CFEFEF4D1B284FCE975727E20139BEA87B79F302EEFB2EB77A7BE5436F92EF22FB137F93C0993F78DF15416E3AF92CEB52CC0FF9CD7AAD0F79BEB65BB37AB944CA0F757D
	3827BC467BF1B80766537BG8E982BGC08AC0BAC05682BEFFCE1DA6F7AE702BCD7C5CE98E74111A185CEE56387CDA2F352D755E44F7229DDEF2874527E81AB2C07A57773CDEB2DE1A9226C836FF71929F29413EFE8DC9993E5BCB62CD16FAA99A6B93A7DD57D601DDF98BF84D525743EB16FC9D37074E275A051EB26FC20E7B91454970FED5473584B8EF0A0EFB0D2E09EB60BDDF47F579B95D26057CECECF7CC0E30AF82D90BC6BFE343B97A5CD963F7520AB9F291DF876269E5565F5022786F24F6BFD9D3B381
	7FB4408600124530BEG9CG3083F881E400E400F400AC00A7G56G648394GD4AC61711179A529D5BC62791127CF00E4C59D8D92D58C12B06173AE66CCFDB774D73BE4F49F203CFBC46B784A92934757F768F7BB896202944108FBC4A47B992537B7A269F17C38D82163DAA3AEA592A47BB62C777DE7799D994CE5D574EF1A30E6395E43EB962709BDFC3CF85D1294D7331CDB92DD5BD1D63D1F644FFEC58DDED6C21AA8B748413E901905C27A92A22F5B4F6BD5F9895FABED76476AF7E7EFD43FBF8438FEEBAD
	74B356E8C67DE89DBEC23F52B8D959FDD1D9677DDC163054DCD669A832AA17C6E561DC7A9D35350A6191F78C67AB43E10CCB7D029EAF27FC9FD945A7E430B6A803BBFC67E1D36E967BF49E2F5FBA463E74A70E31FE3FF98E8B79C76630085FBC078D7B0DB98CF1577D44DFFDB0273CBA5D849D572D63D2E81F84F7BA98507AF57C942A9741BBA4A21063E7D03DAED11C1761E69BF05D8CF7171F4C93B099421173AE24635F9B14E5FCDFCAE902A118B63ABFB2290E4130E8507B819D572D0FE50DEB64787E56E59C
	DF2CB16E6F61B5069B7098F7CF8685BDA643FEF22EEE5862D4F7117F79D29529EEEA7E425D5CA9703CEA3BE44C2B355B461B5E45A6104A6258461E29BF1DDD08BACD89F01D5668BAFDFAC150F51E2B57E995993C3DCF9674B9A0FEDCCE7D86E9C614693031BB0FC35B23F8BEECAC07AE6FB166D087745B13B1FA8E4579D1B9C0F48983EDEA864F635B4EFFE2B5CFG69D2153024EA4308253726795F36840CBAE4C2FFAB936800FC3618B5F0B57064A76043F8F836E7E40C647698E3846DF9EF007068F5922A4C5A
	2A86AA23BE8DDE2657E23B00B054077DAD8B0C14F1FF40A883F1ABCC72D1CECCBE423FD7F55DF80178666F045208AA3C2D5F63EB2513E46D4543F5452FACACAE92CADA05B7B0E9348AA5E466378A25B4B1348A4510B406489FF81598A17215F01D86F08CD693AA6831CDD5741CCC93367213AC78498F2A4C59EF864B6FEF5DB04E1F4D937D1673A7E5990EF17A32783A64CF8AE96D8478CD4BF8CCEEAF7CAF2B18045C5DB5A04AB22C08979AC28112479B07B5A9DA87BFC6770991CE439A316D4F8B095E678DBC7F
	8DD0CB8788F4399FFFA794GG34BCGGD0CB818294G94G88G88G490171B4F4399FFFA794GG34BCGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGE194GGGG
**end of data**/
}
}
