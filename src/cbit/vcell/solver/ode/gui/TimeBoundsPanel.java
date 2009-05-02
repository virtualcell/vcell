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
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GE1FBB0B6GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DBB8BF0D357151EDD32C3F6C9CA26CE6AE6588D13522D1318545D1016F41D8DCDC8CB926F04F51CCFA750F1923621DB3AA1A9ED5D06248F5986ABC00060246E06B634CD4084235866E7048336A579AB0C400AED30937CA4BDCB32E5BDA1BDE15998F64F391F27A77949824F6C4EFA6670744EBD671E4FBD775CF36FFD88959F4CAF5BE02890048DE5827BBB3AC190C21F8AC201654D3F65387A3D85C6C1
	7377A7009D42374ACB615986ED4B5D85466DC2EC2C9C5E8760FD312B4078BB783DD338B266B88EBFD24829023607339F795E74F2CEB5F3B91B487BC26396F83E8DB088B8FC666CC5790F9AAB99FE9643F358E0900459D5C8D3E45C45F0EB01269CF0D6003EAACA9F02A7D56086F3BD239B38A5DBD877F85830957B517449E1B963DD471E3B055FEC18378FE5995F5A42F82A615D8300461788316ECF60596098711EF03B7C0A6912DB8A84CC91B9F0C98A1BE40FE472C67DE63353BFAAB54B5120A7629003D2C028
	7C987BBC73D6B969C3900A203F8584D7A3231C81782DG4C7918630D97913F9AFE8B7B8B0C157B39ED1F6EEDE03A77DD7E0ED0D8B7DA3AAD05EDB57B29CDFD7B63362D98D9E978BD99071BAC686FCCE8DB8C108F3096E0FBF501318660FDB27E194D9F43334925D86451D1B958948AB587BC61D3E0E5GFE1B4D5042F0E7A5379C7688829D63EDEFD56B68B39DD03EBFDCB79F75C976C1BE5EB9E76F95D65CF71BE1130ECFD6E4DFB634D5D3DF8CD647FDC2E57724141DD11D980F0932FB852D6C58B71FAEA95311
	1DB96F6912CC56CF36C6F6D1B50E758292834B607786668B0A4FA5788F8F4471B4B60F90BC3EB7C05B7C83BCB67A5E663A58CB6F943657FEC5578FD98F4FB1141A288E3BCC493ABCCD622E4DC4E53E2F6A72D3021F2D41D3DD3691BC3EAF0336851F9498E9FC5DE643391AD1D3E01C873088A09FE08DC0F9ED01F1D7AD0F31154D1FDCE30CB94430529C7587BCD2D8203C37D95E032794D342E2FBB02208C13754A3067D22ABA0C584C6F30785FD60CA60DBC0E25F8DB2DA25209496953F9C045E45D1C9114291CA
	33503283E94292E945798C82C6C3826F77FE4BCEF886440872D948A3AAD21C0236BFE841B139A476C00F50817C26FEF924857D55G76BB81BC2A9F6A2D58DF279406F4E1B65BE5F9A49AF222A54222DA7473034D18BBE270FBEEAD7771C32D08AB4676BA5E0FECFDC7530F556FC6754570B81FE3F75318827AC60041BA9EBB7F5947E3473AF521D07D742D3A31D37E77CF17E4545118492ACB1EC35FA6FD23FECB202D280E677C86396F9A67F3E35D5439B62803FB1B59904F9D1FDD330CB29D5C269434A56CC97D
	FD209D6D938EC27C82B49C64BE3F5B3A1B46F0339811F8541E8EC340C91E7888D0BD1F3FF8087935046902F89C034DG38CEEDA3F4582FA03C8B70BBA61F723FCA781DA0FB92E081C0BE40BA5469902C07G8100CC00BC00D5GDB8E97988F9D66E3716E4565B314FFBDF9797F9A28EDF6E25B755A757E7F837D27839AE373ED283FF4C98AAAD6A9A485BD70A3425A3EE1C3BBD99E9C12D51E05849F023CE901C495D1717759508F2222047D2E28A2F5CB0A628FFA636D7FCCE4C5222ED0BC65F2D98F5B50377298
	64CD157E87B64CB9EEB99814C8302BAC2C7DB112732400B48ACADB20ACD2A87E49962C8DC724F116F31FEA59A8E04E8CC4B1FF53FAABD744718B057D23B005F08990F28EE3BEACEC2E5478E8GF0D8B7E640B373B04FE9E3FF61B96D43093B04A597C2863D35B96B6BDFB22CBBCCF3C97911F8CE23BAADED55577DF112FB99BEC2F5FD4F0CFECB2E7DCA887D51FE7439D38E87CF8F8D45C9D83C6E947AA3BB92F8DBC437A2E35AE639E728837528827D9AG1CC7F86EE9E943F61E6FD952314801G9D8AE84BBC02
	7EFADD2EE0768660FD56916AAFE26BD1EC3FA3D42171498A563E65A817F330FDBB4604F4B1AA41B20B8BD327BAEE6676F7125AFA5898527666F69C4300E6CD4BA6B2CDE1DE4F7B0E3271BBCA570EACF87E917076FA24ABD669563176AD005F8550D0CFEDD8C1683E9F61F4D975B4BF6656D37A2570B4297DE5A81C2E6AD83C3F46E3497D35C4B95D60B11AE7F9FF31E35A7E3E9F63F4F30F51FE32B57D929F1B116E76F1CED7F40C66EB6C8F574962E3DA398D931CAEE026B9FD16B9EE2FC9255BF81953E5B23A9C
	F35C0E38FE4BD53ABC560F3EFE37CDF23AC2B3DDBF74752B3F42695618699A232F5F1BAADD69F1CA172C9F1DB7BF5F07F3196C290645CBE24C949903FCE4B6F71089C477CEA7AC650CFE57F13EC79AA6F945896F8100D947793C7F372DFC0F14B912ADB8F38BCBD26D7F669D27F6ADB99E1F77D5F346CA681C4B6DC419C28348EA60736041F6ED1D56940948EEBFA991D92C87BA34F90162AEF63C854FA1FFB82234C3620E71F9B156017AE28D49502C267EF3E88B4B8B9F025C8100CC54C1389042FC09F3045A69
	BE486D1CBB71F5E170565B8D1BD35879DC83352F38A1EEE73171F089757F2E0F7F4C7D6F16C7CDFE5728A9FAE954A43A94136471ABE6331A0C68D8BC93627B581ACF39AED7DA34BE4F5BF6A525AE3ECF298E33BF0D6BD2BF6717A554765EB037FD99348BA78A0CB4BFC6C368C3C513B8A97E45B07AC47179A3D40F571A9A191C7CFB6F9496EF1AE3505B9F7B3E11E528BF417602A762FA643D3052309C006A72278857A597E8D62B3A4C0EF019B4F7D6443439F377C9DA23371D647439149E96E82A6342BFEAF5AC
	97EF5055F14ECF3C060113D437D9A7B53EFAEA25417E94577195D547FC20A9ED64679495A770C956D2017B4A27A0AD6CD242385E63DAC37519BC47754948DFA4D46E3CC9D79F0406C62A0727B12ECFA9782BD27559DD8A57E791502C8120BEFAC052735107CD30BFA4FCF214735583AE5444FDF7EF8C479D6A9D26B11FA32F44B8FDB650962974BD63F8E6B1A43B2391937957AEC714AACCABA6F18C62998BA8D38BB6587067BE5E5FA3633C3F4A66822323197BB1DA73DF02F6DD2E7248D1D8412AF75367BEFE8E
	2054F39F66945CAF748D5F1C5207331B296FF21BB5E37A4A8FB6598128AE528457E5BD509C32F05B72A7B00E4430371652FDF219538500E601CAF75BE5C6D7C769FE2D522DG1A8AAB7779C3139CEF875CAC95AFDE6178BC402DD371FB2FF2FC358D762EB63E2730D8124FCE665962FB36DC5B547D9BBDD37891495FCBED74CCE1330ADFC3702BB4F8AABFC47078DE5AD2E0743570FD7093DD38FE64437BEAG213540D889D08F50574A7376EFBB8E8AE94E995A03307D8F0A016E0442176F77BB51EE28D5597B83
	1D5A79C57DD095C2BFA82D7A7BD5BD20FCC7885F1C6B6023B1376F3C761C33F4C70721A4C54C65313E73DB63B1C7FDB76940F939252D40D8855088508790EB63FEBBE12FCE67B7FE66429C46763921D467E9D9ED7AF6D2FDDEBC037A64G4DB200A200E2004AF62E4FECFB5AF13445F447116A75F9B85D99E3FDFB22FE14EFA4AD5FC0929F35273E9BEBA0855A6682E483AC85D88DD05A81BE6F60F6BDE7EFCEE7D7935F30B9E57527A5F95345755D9B19191D075372B5F6A47251BC79D1955A29D343B9E159EB71
	CB818F2D9DBAE33C0E6B6B6073F72193F96742FB8E40D200A20052CED8BBG2C1D5CC79F1FBA1EF66CB12903FF08CB26BA075A780B997A26C4C9476769540B0183BDE85FA434E581648264832C8558E2AFB0565839FD1327524600FACCF9ADB1704D347A6A5B79E0DA3EB6FBA29F9D5B35D2B96BE3504E5706E8F4E6BA7CF5DA3EAC3B9E5FAFE2B39C5B71F4FC393A729ECB4B27AF6F5934FC4B6CFA31F4CFAF46D291342D8728B885718360848881E41E6231746429135793CB534E1711091959B81E16EF51A95D
	9CF4F9E672ACE97972F5657DF6067236CE2663DB25AB6F5134FC7A721ECA4B37DED71E774A4C64C55372D5F469719D38BAB3F9475372D5A74923F7A367495900955A7AGA86E288DF113701E5945F3512A962CAD9643FBA140BA006D27E17E1C666D2F11F6A73CABG192769D9851EEB5076CBAD5ABB86641DCED7BDD8477467BA54E87A20B23E14A043F81D7D73FE70494F41B7EB5AE8EEA86CCAFD167CFC177ED9728BDD7AE749AFF76D20F868C3FBC3475BDF691A7E2C79D7DDFAE74DFFA07ABFE340FABF9C8D
	62361F082535069443EF8BA0031D0D86E9FAC3491164FAD460B97327FD389FDAF41A67461BCE5789D3F3234D63D764B06F44A9096153C3C36AA25B9D125CDC67D90E547E7BBB073E7FEEF1687B6FAB0E697DF707A3157FE81C3FC64EE21603ED2BD47B7E63CCEA7DDEBD232F5F2F4E686B7746196975ABBB1BCABFBA06AFABF857B265EE20CB9C0D1FCBE4060D0C4621480EDA356F666E54F634F76B5BF13A9B4F7AA2F2B46C1612F56D692ED45B62EBDD3BA20D22BA14668261D7E073ACCD31F508705336D47CF2
	B74EB7175F638F3393F8EA639791EE23CD11CF552A735D21DB2FD4B8E8DE28F2CC4DB1D44EF71C29FD73AFCEFD5FACF526764D934E743EA9F426764D734E743ED965CC95AB54C7554AE69E87C3E1F994A201729D6DC9ED6B099EFDDB2DBD29ED6D6CC9EF2B33A7352D7DBD69ED75746847410F95ED9C5429F1506F65F15020B3660EA41C47414FFAF22209BE2B633E9C574AB9287AD818C820BF45FDFCFFEFEA9F67756A7B78615E54BEFE2CB73D0F8BFAD37B78073D69FD7CE26F7471F4E7222D0E038C9F381C08
	BF4470C7A635BEBB247633FEB251E75C477FF0A5311F438C6F391A08BF427097ADF83F9990476528823521837377EC8B3F470C417BDC0045G798EFABECA573BEF0DE1FFC3D02D03F9E4A1DAF286F93BEC1C379A5E9B818681B2G661F6171F1E8E3621D24DDF429F7122F3E037E6B561C8753D6EE771E1D48EBD34FDE353CA7F76ECCE8D337792CFD734EF7137A0ED370BB473D2D5AB361723354666A337C4CF83FDF2F3DD1ED9F736A35BB4F72F570153E2D5A78E0EB8E7569DC9F4E3BCB6230F3BCA4B15CBD0E
	78BDC87237FD5AEF7A785A6BA5E799BE1097BB4B6B4E7C982FBBE729383097F105703EB60E6B67F4255D9C77G798653896F3EEEFE661AEB615FA1CEA2DDB70F095A41047396BC5CB61BDB43FECF336835138823FB72E2D29BA37F1AEEFEEF3647CA3E81F296989BG1CCEFE07935B436FF0F27E71CE21ADC77F5E2C48F94590F052FCB45799BFCFDCBB3252C0E5AAB6AEB39F5ACBBFC35C1DB66DD8E10C3C8AE082206D05896D98177B925AB736E85BD15F268D5A767A0476B28DBF356D090F38ED6B1F48958277
	4FCA79CD287DB376BDA9FB4EDD75558D2B1E1FE0F7E5BF9FCAF1D7C9A687357F0BD6FE8E128DFDD01C4B4F4F3D0AD45C3DDE0E2B6C617375EA0C5F95B9FA70FB5A0303287D0A9E24FFA9E6E5BAE676CCDDA34A18DFB9E483CD9E403B2CEDF98FBDCF675F7E6C94EE74228FC2E1E9C88A07A5CF37FF826747C0AF99AF225B07703B86004A3F311F4B1F5F2BAFDF2B43225EC4794BFA1365AF9FC39F3965C0F4B498E1F2DFA6F3GF98AFB793E6B5BD36E972EE58F34C3D8BAC33EB72E1B0F5EDB9413F935CAE3A745
	EF30A1FE2D86CF67F635154F6D7550B658474FC35F93B1461642FB91C0B14076F3B08EG76F3BC370D1D2B91521C6FB865109D96740026ECE767B23619FDE77B3DCDFC8EAD7A6E5D42623F31263C639DBCC763A303BD2BDE7A6A86FCD27B6EF221FD3950168F3096207CBC6C33818E1D6776BDF40E5DBB60368CF6E1225BC7EE5455CD1EDF9A0B23F1D1243639DB75ECEBBBCF7598E04F507964FD37572F47B70B51E51D1F9A6F146FBE2F9EDF8E23DF2243C7FD79E43F766EE67B6D81830F0DE47AC256C75179F8
	2E257923E21C670F0A7E826387827D360DFC33C07E561E1C0A03DA126415FF47E51869724296BE2E3B6E791AE0FD56EA78CB8ADD6C7DD407F97DF1DDB2033BCBE81F8B0630B7BA373D7D387F8BCAE33B553BD7ABAC20C17C98366AD0756E3A5AFD2A7C79E3DADF543F75D73A5FE991DFB0392B59F37D3B37B3DFFC371F7BE277003EAF280EEF7B62BA3EB9E600E7ED8D9F4BB7B13D315F1BEC715FF556EB7D36F4A6C0E5F47E2F4A20F36A83B1F50D3FC7542F712BC57DF301BAF17AF39D33187A9B42A6D17B8DA1
	4D89BF72617A5D87E327GE48164GAC99D04F33C52C4D68ADD8F2AE30F93C384740AF4095AE7F1FDC29EDFD40157E3B7107DD785D01CBD694F9947B6778E5AE9CAFC58E91A4457D2B8BFD9476FBFD8AA5E57827DC98FF81E90820E90DB7C1EA0395E0571A81DE775D5A46EB41E29577288FF1ED1FC3AD78B947DDA5B544A43C4FD1F1AF79B9EF160AFB1C2C5D2B613DD845FD62D36B4DAF78BA733ADFD0F5F53BBF02E70FB82CFB9A5681F37834EB98B6E1CE512B466C6F936EB01C502F6F0B694FC4295CBDA3B8
	51A601B68BA08F2090E0B500F02140D881D08550G608488GE4GE482E483AC864887A83A205E8B62E7987A3A83129545A4C042229FC30469B1A700BED9877DEC9F64FD7DE4E87FB4FDB942F2C88AAB6394CB3EF2E43EF828DF6B0B9A684FBAB83DAF283C9F7BD247694FFC69637417E4DCA508AF5C48D05C6BBE5CE74AC3C391C91D8F02FF3B0AEB0FB864089F7753B48F5E521AD8F33EB10AF1C1EB3E1CB6DE73756958433FB760F5DFGE890F84D17B118DC732D70E1DE7649E17F049CD444C0D3406F8D62EC
	E6759F358769678C721A78D6FF22FE3B6538FE3F71F37D72D2682705BCCDCD0CB216CF516F85EF222C3ED0DCD60B0F4BDA11C2D659B43256EAE451B9D9C3566F758CCFF107892EC20C7FBF9B4825A329E324E9A4FD0CF40C6867B2474854DC765908FEAE6B9F5166B20A9B9CC1FB3DB03F7AD4BAC9451DD3F14324CF44757ABD0ACF450708DE08EF13D0A047DFA2FA0D91DC9F43DD56604EB1DC468846B9188C6144F9EF9841719B728782747DC6C2638FC2582B7E381968688F099A3D3F2C6262FA5F9240F1D557
	B8BA5DD9DBD62002748B53C88C78A748A7205DC0987452F1FC26FF6A58BE5F2F9DDBDA372CEC63F5CB0D28D73770FA6D72C5C10D512DFE3C7F50ECBFCD5D78AFDF82A3A60B2D4BE9BB2B4A58BF4CE578C0866DEF91AE3D2CFD4C475B672B6D4F62D24E62D95C33A5EEAB99C2765DE8ECEB9C4F07106543975B347970B968F70DB8FDBE24FCB543DABE41856B016BDA78A6FDDA3EAA60E9C843C7476457EDD347694DB66DB8D1DC6B70D4BA7B70D43AAF7B2652E57934F4747F569E583499DEF2FEA614014FA2E1E1
	1BDAA63713D8AD3AF450DC725257CA8CC2E93B30114E5DF62194A7E03BD0C666E73BD082F3B706892AD48B505BBBD30B6E261BFF78EC4E85EB05C1B0C007BE0976D0B60643C97A40BC950CDC942E128C155023A2EC46532F5D7478CB914A79391EA2EC24C7007AFC5703655F347D37F6AFD0FC03BB65DE20424376161E64EF5A320D68EF8F60B3BCFCDDAFAEBFA0242EEF3AC745C0GD62311262087EB1D66F1C50A57328BC97F91CE439AE95BA2F2F6B62C61799FD0CB8788030542AA5B94GG18BDGGD0CB81
	8294G94G88G88GE1FBB0B6030542AA5B94GG18BDGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG9595GGGG
**end of data**/
}
}
