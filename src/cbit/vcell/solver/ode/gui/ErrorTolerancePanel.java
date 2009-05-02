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
	org.vcell.util.gui.DialogUtils.showWarningDialog(this, "Error in Tolerance value : " + exception.getMessage(), new String[] {"Ok"}, "Ok");
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
	D0CB838494G88G88GBCFBB0B6GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DBB8BF0DC55F5A6912D1B2AC184939C478D9E904581C14CC4CCBD582426F593CFB045881B1AA2F784A814D091C46CA69EC604277DD8DAEC3040A2D187B5B8FCE5A396D916EDF9AD5B7A2C2C5FDA1265C5C836FEEFF75FCACF5A9F3BCF327CA16939775C7B762D741E962B53CEBDF37C761DFB4EBD1FFB6E39675EF7A5143E334CDAE8AA9504C22B407E9DA9940454BAC1386F5309FA0E2B7DA84BA6E87E
	7D81608D612E28851E2D501667493215882B44E2F89F0077DD707EBB783DC7D8A326BB6007011CAB507678DF7C68493965A8A6AE27083EB718F64173F500914011E746C7C4FE3F290C61D3981EC32189EC2AA0B40A291C6172012698F02DG8395C82F4093953856D14B68862EDFAEEC7BE954749A69C753A7070D0CF7BBFB6E95DEAADC360F487AEB2AA761290277B1GE23CD9786078C1F856F5CF0C1E70FAE445BE65158281FBAC9C1812227630CF320FCE4A8EC7F3B49A0E3A4381A9AA063CD237981282C279
	FE526F4FCC45349FC14805BE17D35C7EC1A2EB84FECF83246D67F84BB0416741EF4B47D936720F39FD47BF2AE37A77DD7EDBE1E3F550325B403E1A0F512E010F63762D88EEB17DA79D0BCCB37179E2E8DB8D3081E09BC0E9E5162D8660ED9A832BCC7B615960D11A4241E0B8549009B486FC5153E0E9GFEBB9C5042F0BD12B79C7589820E732D26CA9DFD6682643B672A7950A74B8F72B14F6839DB487E6137CDC5BABE49DEFE59642AC4DF78AA63BEC1594F994ACE29CC0C49C4591F8BDA5965AD0F192DBA32EB
	EB9EB3AFE67DE4E8E467D612317E9E0D0175703B10798271F7D0FC45A7F1BC46E795451377FAE8CB7F0447C65F6BDC97176596216440CD3AFED87C772926E2BB5341BED3178DB46EDCF61479362A4BB3941F2A4123AE2FD2BCF9DF8FED4BBF4D32E1FC5DEFA573B525AA4B368CE0A5408600FC00DD8732ECFB8F70983318BF7D0AB156ADC61546C9B960132282725EEF7EADBC25E9A5AA3605E28A19EE3DE2D496BD81A9A6B01A9F1B098FBC89FC8F525877020C96A98453D41143A168DD8CCA0A940DA14DC6739B
	04A6AA51D6379C14C0C0B0A270FE334DFB609990E34AE7911F28C8F18AEC4FB513B11992FB21C768GFE23DF1E5BC17CD58F7677810C29FE9836107EBA24A8248B0743958ECFCCC65C4492E165816267C2B3499DD7607722835C474F9491DC813C17D673FE3EEDF9D3530FD37692754568C5BE4752B126003E95A0D04DE36797FDBCF61C2F5DA5D4EE3AD1B7F6B2A00E532AB1E616D74F1CC3ABE85FC43F8D5016DF4D737E61413EA473380269C2FE37D66B4F3739F3C7D7D299E6C6CBFEDB2F227F575531F35B08
	FDA58733EC15G2E035C670FD876E28CB70AB109C7EDD79482CE7245C7G75FCEC380679554C74A0F8B286BB8148DA351B5211FE8561AD005FB179481FC779FDA0BB95A093A09B40D283BA8138812EG24832C875886D0FEA84B66BC444722E4F843BC65DF0D3F7E2F81EDEB2136DD2DDDEF7FBF50FFAE40987B091568AFCDC9A145A9C524108FFE44D85BBFD9091DAC8F7A43AA4FC60A0FC05EEC02C495D3717FE2A5FE9095A5AAFBA695692C24A8F2E8B45E7E9415951B74C462A9174B7A391578B6FC817226CA
	7F8BAB49B95EF0A8A451E0D7D9D8FBBE4DF9D2C08A0252CD6149100278DF6DA0756104F411655C17F66C90C84E8CCC127C0DB5578A110CDFA4AA87E18A11A5C0D8FD086443B2F31946C78140115AB18D1E6907F8CE3B709E4FE96FDF7A3E30E6B8E252DB1B975FF61D49F298F3CE5961F8CEC31D7E3DC8DF7797E96EE578986A5ABCC8E2CB2F7EB3D31EFD67C95C394351D0175F1FC84662F60FE0F63EB52B0FD322D78913944E7250B79A09CE35202B8B40F7186721E0B1E967391FA52026F0A0004382ED690709
	6F7EE3301459FE855E979DC65F11772AA324FD49D02946BFF92C3D7C8817F38EE8A0BE24AFA6A5500EACD29D6A980A36B7E73475327144F659C646B320D95FD6D3197BC6F8FDBFFD040D6591DCC7B26079FB400F54923A2DAADDF1AD366F05E795C0F7AD5AD0C0699E90B9DDC6AD664A350CFEB3BC6DEAFFFF74F03A5623717EC60E4E6C4F6965F48B0EE24E657DADBE2A6D2F504769D69FC53DB6EA7AA578EE87215B24526DF2E06EA67D11B573FD07D66E850953ADF5E0FE4FF4446D352BF4C5B227DB4B68329D
	F1BB627ABD22526531F6FD7DEE9A67F416E33816686BF7F40253159F4375C6DF3FD7D43A9AC6B7D3BF1CC3DB7711F9CD77D8636214B8ED0FDD005C64F0345309047B282F51DA08503B0E717D525DB44724407BD2004CE3BC87BC729A5FAFAD1AD8AEECEF59E4B65A8F2EBF06F6659F0B6700ABFFF7410CF3EEE39311D9D517E52B2F6373A057262D599AE23130D72665A20B7534C6ED0EC05C0D0D2F4253AFC7E3CA9BA471E9BEAF3E5BC874A575A4C3337A3AFCE8974B8DEE101B8A30360E6E1907C86EA4F3846D
	749E64F626DD3ACD9839F109E927011D4547513E0A63F1BBDD478F19517F657B5F657E770603F6599334CFCE856D22C731CBBED9F1B854E404E37148905F530E9C673AFCF9CA6B73553B3FB454E5B153E125C6176C77FEEDC65BFBC6386D5B213D6AC4168DF3E4F80850CAB392A836ED99A1FED14664986A724B06FABE3EF75FA22CACCAB5696D1747EEDFEC9ABB01BA241D086B12767496D3BAG9B8B116B1383B4E5A739BEDF9739CC4C1FCF9C56664F6E13D833CF2B74F7A2BDAC582863DD3F576AD8ACDE2B2B
	E36A4FC7CD29A7D1374C13F19DF7ED5AE25A3B096B781C2AE38150545473330B529364C957D6017B4A6BA17BA751938B87208C1839A021EED77A39EE8B56E78AE5FB7ECAD7B73A865723CE0B6A633AEDFEEA0BA96FA92E5B6D9E2EDBAE5014B74018D2FF7D7AB3BDFF39219D7902DE4EB7C6F08D5C0F4FF8097ED1A9G13EEEA167E483B5F47F9B783DFC9A3673D5EC74EB67CE16FE44CCE7FF705E3CA85C9B9F6718244BAA9346C27C8C3B379390F77779B353FD668EBC1A3772F65B2D92392F5287005A7E1052B
	5C0B4FFD5C27CAAD77E9C6563DC25F78B78DFD3A3291FD19539877E9EB7ECF0A86GD09F7F9857273289EA01A6EE5FC3E3A44644686881F6E6A0F33A25C033D1253BC9E6F455C87712CA374B897B86A79F03B5639CFF85F019AA5EB34171F900FB3F1963AB829CBFG38C54DFC7F7167661967AC6B1B637B3D1C6659FBBFBCFFF81756E15B1A717CE1270A2F227852D3F1BC4A4F246B81F92F01364527781E7905D3E4FDA90077329608BF00D600B10005ADBC2F3F57C44E7966BC13E88BA9107645405904A21965
	72471B085DD0573277ADCD5A3907FEF8F70878A13D652B6FF5112F0A722D3E8ABE0C39FD6734672216B75ACDE603184BE3FD973444E38EFD372C154C533A56ACDB9FC084E0A14072D66EB70F33B2195F78798CF3985B938F991D3D2DEB553793755951CE7449861A6DGA5EDD9368A0056B62ECF06B369B8B6CF6B0EA36A55B7126CBCF22CADD1BF6473A765CB1D4107768CF4109AA99D5A56GEC865886D0568EF99C202F1D5BD560ECCCE6D7835F5C39436A4F26199BB4B596457959D91514AF521E48474E51AB
	081DBAB51E9B1644D3329470E1ED716691DEDBACEC677377919761DD836F59G5BGCABA40BFG5DGCA8777D1E7733124E3CF92BB78073AE436F350467CF97A66B7C97996F568454019CEE2DF86342D834881A8G586B4A3255810C3838FD4B1A134600FA2479D5E260AE4F7C6CDC1514EF5A154807E3EB6F65E31B6662EBC34CBBBF9D2EC94A374E259B3F3E7949BB1114AFC7D71EC51A1F3CB71372EDF769455243DDA416CACE437E924089B0821082308CE06DE99ECBDB1BCFDECDAC4DB9DF82E3733371D2D2
	3E5C53FAFC07657949F3A665AB5015A70C4FCF5E6BC979CABB7578FEBCCFF99B1372D56A4A9B1B181F3C2924FCCEDDF976407C6455A5659B18A18F3F23FC0F1E9DA8503690GF1172D8417816FEBBBF9AE2AA8A235453370DE5C85F5A2C09FC0CA177ACD03362F00778DG79DDF866C14EB3307D3E9DF3FD0FA07DE95FD33A1257F3C48F22CF1F2A4323F34AA81A713EB3C97F84F0FD461A91F34376D346674E2F1C52BFF73636681FBB6FECF98371500776EB9EEF5F53B27739745BADFA67527FC5F5FE0CFE330F
	CE0694B9A8D131E80BB4CD6EA2C0866B198CE1FAA312E3B36BD101674C5DE748FE28241B6746FBDA2A0559393159A7AB61A86F44AD0951AE3FDFDDE44FC6A4AF57F9F92B317FFE502A6F3FFBDB757D372AF5EE7F7DC32B117FB04E1F27E7B515E0DB3DEA5F6B6D467A3D552E2F5F5B6D7A7A7D21FDEE7D76359B6907E378EF8AB97B1E759D2193D69E011FD9DCB7E2E3B45DCA387B005A373F43580EE0073E9D318EF296988BCFC63D52CCDDAFF51429ED71352ECD1102C49D2479BA65D7E073AC4D32759B149F5B
	0C78EF68A0F352A37B64A8BB21C79B7BC5EEE33392BEFDC060B96F4B5A39F21E52BDBB07201CA7DC463EF94A256F1BE7DD463EF941155CB75BDD463EF945155CB745270DE285FD7411E7A70F83FFB49C04C8C03E731D4636FABA75ED9D6BB436B55C195C5669CEE3DB7F54195C56051D7AF1106B5146C1359A8787FB670A0305BAF1007D5D61CD74D9B5430F7A34F28E2A744724847A535C470FF7997BB827CB5F474FF4997B78792E64BE7ED517310FDF6ECA6E639D5DF347532DE3093EB94870C1B991DF4370C7
	4635BE2BD17BF9F9BC51E75C47B7CFA476F3086125C0A27EB0435FEBA65FC2836245702482EB7F99123F572B5FBC174298E782E48364F763F9A92EF777DCA07D7921DA877368C214CBF93FE5653C5D706E83C881C887D8FB061F859E72131AA6718CB36692BDD2G5B3B7C44178D3A6745140C693E105275681E4BEA69A67C6F9876279E85B05AF4CA2B5FE71C964F13A70A34674915BD681F6E9EFE5E1C6155EB77296DB78F6835CF7770B5F34F195734314456A774FF5A981923D362387BE2C462B879C341685E
	604C7B02FC2DDED44BEB52549EDE273677F2DCF68FBFE75DE666F795AB4F028DE7F99C9C98C9B8E3A1075A8EC7CBD476B50A23AE9AD5388F2F3394B37E25E7793734F18B69F3A33C6781D85C7C3B4E74077C3BCE465F5CA264B569FFCB4BF1FFE9AAF7E38E2AF7474F9057863718D066CAAB17F9855AB35D74FE19D56BF3B266AF825881303DEC07361D4C65FD896D6D896DC45F06C2ED7B1504F62B069FEDFB7083EEDB4103AB04403DA90677C68B38CD1F316FC5395FA92C5DFD19FDBF5B6AB778FEC9839F6D3F
	055E9DA0F15287FDA06EE9AF7F261926629E9B6038F500EB6DA538BBEB1DCC8F21D7BF4FEB2194E8AAG5EE27D547562B9B9816CCF6C657D259974E7557417BE233F151A7EF0EDFAE95679FEB2C03EE24B7C789A2E1A8F3F9B54D9C90C2F55680F78E60A5F204163BC0BD878BC4B06367A4F79F9643A9EB25E69703E86E0B3C0BEC0C9DF162D320F670B6F1E2992121C2F38439197AC2881CD590C765DEF1D5F1D58870AF8BCE75EF70730724F1C065FE06B7BF05CC7583362696F9412A75AF75ECD6CCB03368C00
	8DG4F8294G1476F37B1EBE434E7D493688F6C122F70CFE71D6B7D932F4A10EA60B925AF6FB111EED957D28C79DFBF677476392791E55BD6F73B13A697E59F10CFCCF8C687125B27AE5BAFCB88F33EB79BC4C841AF7843CC3D6AC70FF79A7E76384A12F1F5C093F613C200E49133B7818145FF92B607CE72769BD03B129BA073AACB8974F1F6B23FB4D284F37FA39BEAB4F696B0332DF9E90D45B9B2D715F9D1A5FD316E477A67FA700B22E7BDF15416EE0769857241BFB74EB52FF6D515F4766764CFD8E1157E3
	FCBFEEEB0F76FE9C4E210768FD2BFC982BDD672146GF082741D6773671A33644E85FE3519B9F71AFD2324A6A6371B95AE5F67B636F5421D7CCE3462A6F7D0BCE1C58987C97F6A1E57CD46CB89C7A8126DDF5C44C7D1F9F4CCC1D2063F56CD62ADA079A99A6B139F51F5CD813BD24E731A6571E2DE33242938D3F42D4B01F7E10063DE2138D2F8FFDF4579FC1C37CA457D122E098B60BDCD45FDBE4869168E703331A793764AF900978667BEE343B97A70F96DF75212A58193DF87E65217327E768E4667A80EF7E4
	182C29EE40C7G9681E4G2C81588C1087A08CC17E842884F082748184G96G248124832C9C62713179BC29D5FA45715927CF00E4C59D8D1228A813B0E1736E2604FD62FA68AFE7E8EE9F203CCD03467115BD18BC3EFEC647C30238A0C5B0621E9BA47B19305F9F1354B8FEE130C44535453A43B1196C5B305E5FD4446F48E0AEF309E4F430E629E864B54BB69DFB78F8713A25E2C64DD2BC9CDF5BD1562017644F31F0D43E948EA9E220A1A00F0648AC941C43748E1E17572B2543FC2F74DBDF22FE8FF8627ADD
	76F17D968E6B6B272D51347A51BAFC16FE3641C4D9395E382C50A017355CC016F58ED92BB532F0AEED27EB5DBA06C75C4B941743F09863577B84B5DE2E997A40A8BEA10335C3995C659907CD39DB9CD5F9D2060DE36CAF0713475822E17D9C36E4F8F68E3BF9D8BF075DB62C4DE1083BE31878EB9466D41FCA778395572F62B2E91F8477396CD346D47CFDD4AF02EF1508C00EDFC3753AC0F1FD8C770F9ADCBF437DF4184C93B099421173BEBCCC465FAF8782787EA82511C3B0EDD4FFEC26BA4A91D12377969557
	2F0E655D3D334777073D5A71459A233F11579885004738FB11380E4564B73D64DCDD3345311FA57F7325AAE6EFEA6EF4B77788BC2FAE3B204D2B352FFECD77AEB6011A9176F7A06CD9705612C254A94347F5DAB042F5BACFC20A69FC7320DA97296DB71021E1F3C07CF097759B2499B1C0070D5DF91CFEAD0E674346F268D023B60766C33F96F16E9C0A7323DEA63A1481ED0D487378E17F47C67384105D51F0C40AAA9791CBEFCD733F0BCB785E6C067E461268407F56C74BF785F8521270E1BCCCB64E0E11CB0D
	5A98C1FB1AE5422356C928B2EBF34925F11F4A1768355833C0989A457E5679E64BF84827150138D5033369568CEA69706FD50FFC71A8BCB31E972C312830DB3D4757C6A7C94E54C1077969DB4DA64152A66C402451A6D8484CEF932CB4B13489E6C89A53648F3CACDA08FDA17C1186F082D691CA68314D5E78B919A2947313AC78498F2A74592F864B6FEF7DC9BBFF36DF6BB51CBFD5DE9CE327F7E6DD129E2335936017FAF9CCEE0BFEE29413103B4F8645C0GD6440906100F6471460B0A942F033F9FA37D47B8
	8DEB443695747E6D3806673F81D0CB8788DB815256B394GG34BCGGD0CB818294G94G88G88GBCFBB0B6DB815256B394GG34BCGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGED94GGGG
**end of data**/
}
}
