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
	private cbit.vcell.simulation.TimeBounds fieldTimeBounds = null;
	private boolean ivjConnPtoP1Aligning = false;
	private cbit.vcell.simulation.TimeBounds ivjTornOffTimeBounds = null;
	private javax.swing.JLabel ivjEndingTimeLabel = null;
	private javax.swing.JTextField ivjEndingTimeTextField = null;
	private javax.swing.JLabel ivjStartingTimeLabel = null;
	private javax.swing.JTextField ivjStartingTimeTextField = null;
	private javax.swing.JLabel ivjTimeBoundsLabel = null;
	private cbit.vcell.simulation.TimeBounds ivjTimeBoundsFactory = null;
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
private void connEtoM1(cbit.vcell.simulation.TimeBounds value) {
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
private void connEtoM2(cbit.vcell.simulation.TimeBounds value) {
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
	cbit.vcell.simulation.TimeBounds localValue = null;
	try {
		// user code begin {1}
		// user code end
		setTornOffTimeBounds(localValue = new cbit.vcell.simulation.TimeBounds(new Double(getStartingTimeTextField().getText()).doubleValue(), new Double(getEndingTimeTextField().getText()).doubleValue()));
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
	cbit.vcell.simulation.TimeBounds localValue = null;
	try {
		// user code begin {1}
		// user code end
		setTornOffTimeBounds(localValue = new cbit.vcell.simulation.TimeBounds(new Double(getStartingTimeTextField().getText()).doubleValue(), new Double(getEndingTimeTextField().getText()).doubleValue()));
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
public cbit.vcell.simulation.TimeBounds getTimeBounds() {
	return fieldTimeBounds;
}
/**
 * Return the TimeBoundsFactory property value.
 * @return cbit.vcell.solver.TimeBounds
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.simulation.TimeBounds getTimeBoundsFactory() {
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
private cbit.vcell.simulation.TimeBounds getTornOffTimeBounds() {
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
public void setTimeBounds(cbit.vcell.simulation.TimeBounds timeBounds) throws java.beans.PropertyVetoException {
	cbit.vcell.simulation.TimeBounds oldValue = fieldTimeBounds;
	fireVetoableChange("timeBounds", oldValue, timeBounds);
	fieldTimeBounds = timeBounds;
	firePropertyChange("timeBounds", oldValue, timeBounds);
}
/**
 * Set the TimeBoundsFactory to a new value.
 * @param newValue cbit.vcell.solver.TimeBounds
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setTimeBoundsFactory(cbit.vcell.simulation.TimeBounds newValue) {
	if (ivjTimeBoundsFactory != newValue) {
		try {
			cbit.vcell.simulation.TimeBounds oldValue = getTimeBoundsFactory();
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
private void setTornOffTimeBounds(cbit.vcell.simulation.TimeBounds newValue) {
	if (ivjTornOffTimeBounds != newValue) {
		try {
			cbit.vcell.simulation.TimeBounds oldValue = getTornOffTimeBounds();
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
	D0CB838494G88G88G630171B4GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DBB8BD4D55715E8D0C9C2A2AAC67CA52898081244B4E442342CD9B6A51DB4F53526E393B2250DDD23AB2625A9ED6CDA2E0EEB667AG45BCCCB01044C61B1014E450D292150EACEAC43E0281F943A3AFA49161FDAE0F0777FDF25FFDFC8C426C7359775D8777711435BAEBDCEBFB5F5DE76F33BFE71FFD76B967A2143E13D8304FD0AA887B8A847E6F4CBEC170FDAC88771DF8EC9D6262BC9623207977AE
	40E16110A19F1E0950D63151E2AC9636798A61BD8D5E0BB7D80CEF426F9261D91F69EF70A30C1C8AE87B74DF071E1AD94EDF4F211C8374FD303E881E2FG24830EBC733DC4FED47D910E4F66F804FD86C158CEE9D6569F6338D6F86F87D88530494B681F0067C1AAF9DEED8D273B3CA8C5587D24ECB8C47A517409D061E33C6D7CD9A67CFE5FEE9F1115C975A4BC51701E81C00C4F93C64D9F42732C692A731C5D66D22A066D22A4D5793D5230A8D7F99DE215B3602A2D6DF6394586EF4063701B2C9ED192FCDF11
	BE0B9A8AE99F02D0836FFBA96E39C1A2A78DFEEF815821625307887E3CECB176G78E4346D637FBA4BF56F797AD1E1077371032F0631ADC6E6B6ED120336151D30657D010EC3200E78BB875A0A7D96E3B9C0AB40A0C08C40D1BA7E6367CE40335E26B4FA5DEE2F275E67EB109CF287D8A9416F5ADAE86138AE516E159D02404678497ACA9DFDE6824617F553FC4CA7A927F13CD33B9E90329F3E4BF0C047A759A9DF9B927D4C97697E20CF186C438D61E4E779C363B1D476E702D6367042298385BA320FBDFD6AC0
	8E6FC7D00232EB94B256E697996BB2783D0F7B02617BA8FEDC03E7313913468CF90F0736BD8A46C64FAB28CBFB7EDA217864B2DDBFA4BC96EB389CE0BAA801293AC4DFA5FDA786184C232AAEABA8FE3B864FF479A79A27643DEC98E297004557841D2BD9A39663EE0022D108B100A600FEGE594E36C71F39F5EE00C192C325290F0C98ED196986FC45D9B7094C795595A6671ABD60FDD34D8E51755A609FE0153AC28A3BE30057045525837030C8B22C7142D0A4B6B015E2DEED191E5BF23D9DAF7185048A2EDA5
	73998438FD82763B3A2E841E12552FFC6AF3D895B1C8415A575711B19936DA20C768GFEB33FECECA07E0A877BD381B2D4BF141DA77DFDA24A10AEEAEB5B3D5E2B81DFB731C4A89AA3FE2E2039E3AB506F9AC59FA7B7921C895A8747301F4F4E3F26692749E5A76ADB65B11CE31F3318827A840074B10C1D97FBB0F61A8E5DAFD47EF829EE6C44C19CE70D3118499D1BBA07C45AB7516F48B50B31669A667CDF8E765C60FCCE981BBE5752F5F02FF09B0239A35FF523B28AF4F08742506671A773776B4D443E9E30
	CB81083F06BEDFFC3E0C45F00355AFE254F64A90F022A3B882CC4F5B2ED6F33F66F1DD881E0C81D9BC49BA75AA25A37D8A426BGEFF2790CBF117227026CEDGFB814A3F36985B819400D5G19GB9G25639663D9G87C06CB80EC5665056D94A3F193C7CFF8D4C36FF27365D2CDDC77F9F68BF9330980B29A77A0B4322C7E992FD224781BF7C3CEDE1BD31136741812F4A331462FD10B79BA1D179D57C4AFA62872B2248AEDBC0914D222238BC4EE07BBAAA4B9F307902A997E5ED28A73E750EC05ED4699F2CA7
	B9476E75F8C49A6CAA8BEFFF0866BCD1925D20F4A314C58A43BF5AC0EA432B62984F395FEA58AF101CA985C87EE67556A6AB99BF1F6CF24394A2CB0010FF1D64433A06A39A9F25C1FC133AB18B1EB9631853C67E04B92D7C5AFD4266BE1FC1EFEDCED83F5050FE1D65127E6B411C46F4CAED54577D9BB477F23C1F697AC6AD715B545AAF0F521F7912783C5BABFBBA878602A4BCDECB04AF7D07C3F8DB2CF645CB52B64FBD17DA089ED120DFBCC06AF54CBDD5CD249D73BDCFBA0DDEC9E2C381EDB954DF9BDC255C
	5ECCF84F3E4E7CC55EC7A7C87B2521D20DCF2AA7D83BE382659CEDAEA6B1A1FE9590E119A58B53A76A383D577C5A14B68B9FC356FE22190C2124D95304C9A27327D7311E5F4C65E5CD3035A3971EEF83BE0952A5CBC857BE495A9D70D4G62A7198D55D104AE60C63A5CC9169F8B23987DB1F8D6297DFD68C13A2828E0FF89D1D37B7B1DCA179E4572AC7637B5243F000CF43B22D8BF421CE03F5447F388DD1B9F69EA6630FCCD7AA36B24E90ED66EAFD53A4CB9AC27E74F895ADB2552BD22A0DD8E274B1F9B34A3
	285FE4G69CA6732FE7475EB9EC63A4A39EC7D50576FF9152EE9AEDBE374753BFF8469AEF33A297A31F97352F1B217691EEA48BAEC9D2D720FC0BE2A2D3DC8A7905BBB5DC76B9FC22F4C45BD520F69FA1C8A6F19G59F3F15EBFF58877C871D7D3046C97CE9E88377F5BBD1759F56416603CCF7BF57DFEB66792AE9019BD50B6F88B4E03754D5ABA2D5E6F775ADD34C464315E5D224D8B8C77F94B4B709CF049FE258D9277A84E0B3E9622AF29A1B91A5754CF51BA0A6405F1101B8610F38B413FB2C472A519A34C
	CE7BE934B36E5AFA21FF698A03B10C1D9FC5B37BCC51C1BBABA77694B07F9FBB719E7A5F6EF5D739EC6E2A40303B4AEAD32AC407CB292DD513919B0BF9C3380F0D09C6DD2E37E8FD1E716A75303AEC66BAEC5768D26E6EBE40EC5FAF216DE573E06F8B4072E37990712122C91C8C3FC8A2BED186DDFE2647EF6B4FF3B9DB9ED8AB249F08B5686D0F8713938CF17378DEF0DED00F6AB5363CD6G264BD6D517427996637979284B99B74AE439734C04B6F70E4EE7B5FA22CA5F4768E101E6BA5E7F36D647C26BBCDD
	9DE3DFF09A5266B35D326787F55C345816173998F53C5B0DBA16AF3098AFAF40F30A52F364C957D281FD7526075042AEC5A66BBDD9EB18BE631F23BED1DB5204A3A5F7686AC347F68153A3E3C1D01F71C4DBDEDCA26A5363C1FD0AE2E03F8740FC74D42F1E0F46211D713D6DC53EB840BD91033E6B154938C33D43B546B9F2370C74422D1023EEC57A82BFB93398705A837EAA7AFF3B572FD41034D2E59D01F8A685D4D58BE9E8A6BF0FE3FF329F7B73C1DFA937229F839F3DA5E857658A07B7GABD8E599FB9E47
	F3G2586FD187A23C7041E21BB437AF07B2D4CF7053785FD386B2EDF987682B0DDCA9554259BE8E2EFC35B3ED6C89CD9E567C9C677B315AE93E8762AF44D81CEF70A512584102E7AF60BD13A9DFD6E99C6FC926032D5FC718862CBE3E1EF910B7827D5FCB460B2E2F1CF71403433135D3141BDDB619D5377EF6CCC210D56B4476EE0E78AC6957F99451F5560197C1F50FCCF5EAFC35B66BBF09F7C70C532FE145F89F3942087408790873069CE4C5B4F371C96A21CB334F9E07B6F31CA66104297777B97085DD02B
	7277B59734730B79619997714316BB75772BFA4078F6D23E9DB74147E26E78975AF34E7C43978DF9E1E22EF4A16B3BFCE1B066186F9C6DE4DEBAG9F8510G3089E06BC2745B872D15117C06E7AE5CE1FC1FBB946EBCADF7213E1DCC1FA7BB08BE79F196E399C08D00894097077A78DAA30EE373286EB8B23D0A24C8E70CF1F1217AB13E37A27225CD61E37614F5129AE88B346D829096C1AD8AF09E60B2C054A234EBF3EBC3A43B6AF143566DD5FFB6CE5DF4213ECF4F524E9D91799296057231BC79FE8531D327
	066B06E52F45A5CA8ED6BBF8A6300E5B3488676FD293615D856F790B41BFGB5G1781C681D6ADC69F3D56763708E3CF12BA78073AE43AF3180DC96E5979669B9179B2966B45406BDD443EED50168BD038847692G2DG8E0018A5E81F23ADE28C284714B79283D7BC3333D30A481738A4140F0DED6115C25EC77A92DC9B4A3D335361D4C43E5CA5FAFCC972AC47B6A2DFE13C9E5F28FFF67266C764AB0B570B25A5E692CBB550568DA08144G2482BC8110930F3174E8DB5D4D44520C7365ADE5F6B6D6C464ABDA
	2A1B03E6A96F3791794AF565BD90181D3C0C08FCE7F565A9433313B7B7A2DF372E3CB7C6E6A72FBCA21F24ABAFFB1672FE9111AFFA0ABCF6B772B8BD9BD885ED1BG98EEED9341ED03771C25180BBADBC8EDD1FC1745D889508EB08890F39736775276D4F85F8210F397BB2BA0679A2C7D674D5ABB8642BB132EFA504EF9D00798CD9FCC466FC3E49C3C497E319F7244F3F023260D65060C0B614F123FF3D17FAC79FB97754F12FFF871B043C39F5A9BBAECFF66624CE74D4FDD54BBEB7EA3557FE9832977650007
	EC7B29D8D6EB0823645B8248E0DD818FCBEFC432FFEABDAAE04E7C17EE329FAADA0639B17049A9E1FAEEECF638942F0C1DF40BD639F3E0C0DDE44DBE510EBACB6D617D67EF57775FD83B3E7FA65BE7765F0276F07EE3F13E061E45D400EDE7D57BFE5491DE3FE7BB7475FB2EC3DF3F1D9DB36B772B0EF07A31B1FCDEA1F7AD536E86AED95D41F309C669A027B1A9DE53C9356F931D616DB85929EF4719CEF256677786E43BB8D557F31DC7543660DA572608EE228E2339C0799558BC0B53EC3DC479D9DBB87E4FBA
	49FC33399CAE191F40B39B8F395146E6455BF1D21D6F7E893D1CA7ADE3F9A1EA59749C43645CE38A6F1BFBCD7A3EC9B505774D4326483E49B005774DF7CC11FD334594AED6180FFE62B6E29C8C48DEB7C48263FB27AB3C2D9FF4695BDA59955E5653DD11EDBD5B955E56462E483636F7694741D20FB68ECE8563208F63A0DEE74CD32660B21661D94F175ED01F1DC2DF4ADAB927D59FBF688F216FC09F2FB60777F112D95F479B4C61FD7C10B9320F3FE98E6F63EF1BA37B78C9734C7174BFCA28EFCEF37C51C0A8
	3E1A63F78634BE2BD67B59B89C6AB374F147C8E8BFFF65781251D07CFFF3FCB63D5F142CE35E008235E182495FAF297718DBE1CCF78194833EBC011D0F32756EA1AAE7G2AF5B00FAEC4C716935EFFECC25EE820CFGC887488258130071D13DBF74CE325DEAD36FA4FF739A711FD9F39E4CDA516E8FCA88EF33FA762A652DABA989E9D337793C5DD8727A143E0394F8673838C5FBA65C3F1C599C3D9C4F0CEF376935A7286D7EBE3D765465388EBE53FDC89B9FFC4DE1BE0D9BA473EE58BA54BD6693B9EE23A9F8
	8FB2755BBE6DB7FD38765EC64FB2B6033C2D4B316E7C7584561D59AA6E36BE022BDC81FB7E9508BBE4C63A4BAAAE7BBA4125427B6695F866FA34913FC35CC668D6E2CC1C6C8FB9EFA1075B353597E41723416AEC2791467664F6FACFCD781BD6623D59A25A67B83C4783242E44BB1C518F708EA7755E3542561D7A77E65B3B2F9BB2D732FC34EB25666E6404AD0F49FC2E89E5162F827BD6D1FB1B35E3C5E264B7GD5G2C3DAE241D44657110762FC25A093E757B346D6BC25A8BB47C4C366F3F0F366D7D7EA6C1
	FAA4BA6CB721C92B7877247CD97603657B0A93CC7B79DD65C0183BCABAB9187D5FEB41F390E1354548F00F59705C2BC645ED68C31CEFB54E57B593F8D714321AFCCFFB74B269267AEEFAB7B65944F54CD9BDFD0DA860FEC59000279460F55ED6F1B7BBCF47EFFFC0D69F7101CF9687C4D9969DE657B5B2BF52282CEFD25D4661F78CG13DFEEC679FB6E56172F5521681ED079E577CC157F52F562A33BD78A38BDFEAE77E5BAFF88CF65BD386FFA37F1F65F13FEB8CB3E411B66E3779676A6FAEF21311361A50AEF
	5560595CDE264E6DEEE8CB3F874FC37F63B20931E30996E38D0089E090A086A0A991F3DB5F279F89914EF73A3D3EF6D850A5CD594E4FD71AE6771D6D378F609CCA7B6686A1FDFED358BB5E74C4969FD97C193EE579BE72E4762D3AC26CABDC836B9CC0ABC0BF0082903B866DCB7E145FBB10ED996C422C76C1FA232EEE72DC62C890CD96C5E65B8FDA74ECCBDC4374C8634FA756CC5DF77F33CD0FAF1B53652E199E6F0C6F41BEBD3E7C357C3B0235537918AFFF7825766E26F805E4405818CADF497B28D99B4C35
	ACFF1C19447CA1C15BBB827B362DD040FF39F553F1D0CB523C1255AF283A7C3C8847755846F5C253B3CD06BF0551A5096B30DB23CB74656A032C4FE5B6529B1B5B86B3597FF94411B2756E358996D08F799836223A326C5449632A7C44912DAFEADE1E2B7B1D967545BAA677BCFF467E67F26E0BCF4D680B51F57A3EE0BA6EF384F5FCF742G4F139F61D83AC8FA633FE54D6F550DB77AED69EC004978073F2B8CB6278E74062F71DF69552F71CBFB754F855E6A1D79DC673D5E705F909E6F55FEC348F242D38E32
	FEEF02315B82108B101FE4B19EC942FC70D8AF294D58AD5854DC506CF012BD867982DCC1796B2F0437B575CA646F469F39C23EBB30F995456BA67DA37EDBD748F8A9DE9FC5723B3BAB44C7324BB928B0D20E7FAE3D8F1144810AE6B55ECEDA9BD403DDCDC9D877892DD88B1AD45C7B34DEC804774DAA6EE794378D5EF72838C6BB72662A386F52357B7CFA68EFBD627E5C2F561B6BF11DF911284AF535743D8FCF0BF5C877B46CA24C61CE5B90EC423A2DCEB5E64FCEEAF3DFAA743BF97D4CE722CC6E9F1CE4F2ED
	835ADC0052FBAD46CA0073GBDG92C094C0BCC0AAC0A6C096C08E00108C36G148354A4E3EC7C51FABC1C6E0064C5B18D90596AA2A14275189CA4BEE907FE86553E32EDFF1E21AF136C750932B2463074ABC76E0BAF4CDADF44C0FF2B12E77685BF0FF3040F536D0E48F17A0203FEEF8671C5B6B28C7722036CB33D8383FED11D8FAFB90AD5DC1B5F64753B48FE1A65413DAD21B5E72E0B44852B795EEC451AEF130EBD783D8156FD19C0C3GEB3E2C6429B55FD99B494B03DE59F54D6BD12CD23D64F2FA48EC66
	751F4506FB079D49D8937F4E962ADF495520FEDF58D13F5294FD7D34D02A2909090C0A14297A7D34AFD456A7D2D0D6C3BF4A2A8EA32BE086D92D9AD9ECCE1A697A5D4D718C57C3F1D2CA706FECCA043FB84347C835B3F20CFC6C544FE50D4E69392C55291F4BAEB935390C614CCEE22F9366D70FCA572362BED7F13D34CF027B4C65D086D53C1D6AC5702DA29108F89755EB0462FAB84E2341FD0EE76DCE9267E0B20493720EBA49788D38A4093D0FD39A178742DE75479C2A234BE75568BDDF4585750E9EA4632A
	2EF1EC3A73365B87CBE93FB00D2C126B9A7D84548C049EA79B472F4C5347F6582C9DDBD6373436E25D921322D737E03D66F18BEA0C3AED647EC3337D2CB2137FF18974D7B5B6DF6AEE6ED2E55CEEC7991BC1866BAF59A3286D7EFEEC5F2336BFC9CAG9E4F568F0A023652A1645F0D0E9E8A62F188F9BE6CE85566430F6E03756E3E1973A1630B33EB79FA00C73AA13E51FEADDF947044C760E3E362E81DBECE2ED66DB8B1DC32FDBADD2AFDBA5D427E69F4717DDABA763735378A64A575D7C281784CAF8B2F2AE5
	F29B0D556D4327EB737EEDDD1EC148EF937633395BA664138958A694507959A664415C9DA5FF13162F85686D3569C5F77D1D4FBE135A57D4EA908C50612048FAA898A143C97BA0F94A637FCA182099AA24C7C5B012532FB2F67C258805F82E27887B599120BE5F4DE0711B36C96DDEE04FBCFB583D0034115F11EC1C7ACDDBFE9E71F7866033B6623A5EED780B903E3EB13B2D12842B5155FA0F0354BA8DE30A982CE58F66117E7DC8439BD9DBF15EC1BE971167FF81D0CB87889D9BDA2A5C94GG18BDGGD0CB
	818294G94G88G88G630171B49D9BDA2A5C94GG18BDGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG9695GGGG
**end of data**/
}
}
