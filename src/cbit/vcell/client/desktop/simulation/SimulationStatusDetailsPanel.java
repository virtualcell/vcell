package cbit.vcell.client.desktop.simulation;
/**
 * Insert the type's description here.
 * Creation date: (8/18/2006 3:38:00 PM)
 * @author: Jim Schaff
 */
public class SimulationStatusDetailsPanel extends javax.swing.JPanel {
	private javax.swing.JPanel ivjJPanel1 = null;
	private javax.swing.JScrollPane ivjJScrollPane1 = null;
	private javax.swing.JTable ivjScrollPaneTable = null;
	private SimulationStatusDetails fieldSimulationStatusDetails = null;
	private SimulationStatusDetailsTableModel ivjSimulationStatusDetailsTableModel1 = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private javax.swing.JLabel ivjJLabel2 = null;
	private javax.swing.JLabel ivjJLabel3 = null;
	private javax.swing.JLabel ivjJLabel4 = null;
	private javax.swing.JTextField ivjIDTextField = null;
	private javax.swing.JTextField ivjNameTextField = null;
	private javax.swing.JTextField ivjSolverTextField = null;

class IvjEventHandler implements java.beans.PropertyChangeListener {
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == SimulationStatusDetailsPanel.this && (evt.getPropertyName().equals("simulationStatusDetails"))) 
				connEtoM1(evt);
			if (evt.getSource() == SimulationStatusDetailsPanel.this && (evt.getPropertyName().equals("simulationStatusDetails"))) 
				connEtoC1(evt);
		};
	};

/**
 * SimulationStatusDetailsPanel constructor comment.
 */
public SimulationStatusDetailsPanel() {
	super();
	initialize();
}

/**
 * SimulationStatusDetailsPanel constructor comment.
 * @param layout java.awt.LayoutManager
 */
public SimulationStatusDetailsPanel(java.awt.LayoutManager layout) {
	super(layout);
}


/**
 * SimulationStatusDetailsPanel constructor comment.
 * @param layout java.awt.LayoutManager
 * @param isDoubleBuffered boolean
 */
public SimulationStatusDetailsPanel(java.awt.LayoutManager layout, boolean isDoubleBuffered) {
	super(layout, isDoubleBuffered);
}


/**
 * SimulationStatusDetailsPanel constructor comment.
 * @param isDoubleBuffered boolean
 */
public SimulationStatusDetailsPanel(boolean isDoubleBuffered) {
	super(isDoubleBuffered);
}


/**
 * connEtoC1:  (SimulationStatusDetailsPanel.simulationStatusDetails --> SimulationStatusDetailsPanel.simulationStatusDetailsPanel_SimulationStatusDetails(Lcbit.vcell.client.desktop.simulation.SimulationStatusDetails;)V)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.simulationStatusDetailsPanel_SimulationStatusDetails(this.getSimulationStatusDetails());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC2:  (SimulationStatusDetailsPanel.initialize() --> SimulationStatusDetailsPanel.simulationStatusDetailsPanel_Initialize()V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2() {
	try {
		// user code begin {1}
		// user code end
		this.simulationStatusDetailsPanel_Initialize();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM1:  (SimulationStatusDetailsPanel.simulationStatusDetails --> SimulationStatusDetailsTableModel1.simulationStatusDetails)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM1(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getSimulationStatusDetailsTableModel1().setSimulationStatusDetails(this.getSimulationStatusDetails());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP1SetTarget:  (SimulationStatusDetailsTableModel1.this <--> ScrollPaneTable.model)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP1SetTarget() {
	/* Set the target from the source */
	try {
		getScrollPaneTable().setModel(getSimulationStatusDetailsTableModel1());
		getScrollPaneTable().createDefaultColumnsFromModel();
		// user code begin {1}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * Return the JTextField2 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getIDTextField() {
	if (ivjIDTextField == null) {
		try {
			ivjIDTextField = new javax.swing.JTextField();
			ivjIDTextField.setName("IDTextField");
			ivjIDTextField.setEditable(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjIDTextField;
}

/**
 * Return the JLabel2 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel2() {
	if (ivjJLabel2 == null) {
		try {
			ivjJLabel2 = new javax.swing.JLabel();
			ivjJLabel2.setName("JLabel2");
			ivjJLabel2.setAlignmentX(java.awt.Component.RIGHT_ALIGNMENT);
			ivjJLabel2.setText("Name");
			ivjJLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel2;
}

/**
 * Return the JLabel3 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel3() {
	if (ivjJLabel3 == null) {
		try {
			ivjJLabel3 = new javax.swing.JLabel();
			ivjJLabel3.setName("JLabel3");
			ivjJLabel3.setAlignmentX(java.awt.Component.RIGHT_ALIGNMENT);
			ivjJLabel3.setText("ID");
			ivjJLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel3;
}

/**
 * Return the JLabel4 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel4() {
	if (ivjJLabel4 == null) {
		try {
			ivjJLabel4 = new javax.swing.JLabel();
			ivjJLabel4.setName("JLabel4");
			ivjJLabel4.setAlignmentX(java.awt.Component.RIGHT_ALIGNMENT);
			ivjJLabel4.setText("Solver");
			ivjJLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel4;
}

/**
 * Return the JPanel1 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel1() {
	if (ivjJPanel1 == null) {
		try {
			ivjJPanel1 = new javax.swing.JPanel();
			ivjJPanel1.setName("JPanel1");
			ivjJPanel1.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJLabel2 = new java.awt.GridBagConstraints();
			constraintsJLabel2.gridx = 0; constraintsJLabel2.gridy = 0;
			constraintsJLabel2.ipadx = 197;
			getJPanel1().add(getJLabel2(), constraintsJLabel2);

			java.awt.GridBagConstraints constraintsJLabel3 = new java.awt.GridBagConstraints();
			constraintsJLabel3.gridx = 0; constraintsJLabel3.gridy = 1;
			constraintsJLabel3.ipadx = 219;
			getJPanel1().add(getJLabel3(), constraintsJLabel3);

			java.awt.GridBagConstraints constraintsJLabel4 = new java.awt.GridBagConstraints();
			constraintsJLabel4.gridx = 0; constraintsJLabel4.gridy = 2;
			constraintsJLabel4.ipadx = 194;
			constraintsJLabel4.insets = new java.awt.Insets(0, 0, 0, 2);
			getJPanel1().add(getJLabel4(), constraintsJLabel4);

			java.awt.GridBagConstraints constraintsNameTextField = new java.awt.GridBagConstraints();
			constraintsNameTextField.gridx = 1; constraintsNameTextField.gridy = 0;
			constraintsNameTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsNameTextField.weightx = 1.0;
			constraintsNameTextField.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getNameTextField(), constraintsNameTextField);

			java.awt.GridBagConstraints constraintsIDTextField = new java.awt.GridBagConstraints();
			constraintsIDTextField.gridx = 1; constraintsIDTextField.gridy = 1;
			constraintsIDTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsIDTextField.weightx = 1.0;
			constraintsIDTextField.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getIDTextField(), constraintsIDTextField);

			java.awt.GridBagConstraints constraintsSolverTextField = new java.awt.GridBagConstraints();
			constraintsSolverTextField.gridx = 1; constraintsSolverTextField.gridy = 2;
			constraintsSolverTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsSolverTextField.weightx = 1.0;
			constraintsSolverTextField.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getSolverTextField(), constraintsSolverTextField);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanel1;
}

/**
 * Return the JScrollPane1 property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getJScrollPane1() {
	if (ivjJScrollPane1 == null) {
		try {
			ivjJScrollPane1 = new javax.swing.JScrollPane();
			ivjJScrollPane1.setName("JScrollPane1");
			ivjJScrollPane1.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			ivjJScrollPane1.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
			getJScrollPane1().setViewportView(getScrollPaneTable());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJScrollPane1;
}


/**
 * Return the JTextField1 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getNameTextField() {
	if (ivjNameTextField == null) {
		try {
			ivjNameTextField = new javax.swing.JTextField();
			ivjNameTextField.setName("NameTextField");
			ivjNameTextField.setEditable(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjNameTextField;
}

/**
 * Return the ScrollPaneTable property value.
 * @return javax.swing.JTable
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTable getScrollPaneTable() {
	if (ivjScrollPaneTable == null) {
		try {
			ivjScrollPaneTable = new javax.swing.JTable();
			ivjScrollPaneTable.setName("ScrollPaneTable");
			getJScrollPane1().setColumnHeaderView(ivjScrollPaneTable.getTableHeader());
			getJScrollPane1().getViewport().setBackingStoreEnabled(true);
			ivjScrollPaneTable.setBounds(0, 0, 200, 200);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjScrollPaneTable;
}

/**
 * Gets the simulationStatusDetails property (cbit.vcell.client.desktop.simulation.SimulationStatusDetails) value.
 * @return The simulationStatusDetails property value.
 * @see #setSimulationStatusDetails
 */
public SimulationStatusDetails getSimulationStatusDetails() {
	return fieldSimulationStatusDetails;
}


/**
 * Return the SimulationStatusDetailsTableModel1 property value.
 * @return cbit.vcell.client.desktop.simulation.SimulationStatusDetailsTableModel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private SimulationStatusDetailsTableModel getSimulationStatusDetailsTableModel1() {
	if (ivjSimulationStatusDetailsTableModel1 == null) {
		try {
			ivjSimulationStatusDetailsTableModel1 = new cbit.vcell.client.desktop.simulation.SimulationStatusDetailsTableModel();
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSimulationStatusDetailsTableModel1;
}


/**
 * Return the JTextField3 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getSolverTextField() {
	if (ivjSolverTextField == null) {
		try {
			ivjSolverTextField = new javax.swing.JTextField();
			ivjSolverTextField.setName("SolverTextField");
			ivjSolverTextField.setEditable(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSolverTextField;
}

/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	// System.out.println("--------- UNCAUGHT EXCEPTION ---------");
	// exception.printStackTrace(System.out);
}


/**
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	this.addPropertyChangeListener(ivjEventHandler);
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
		setName("SimulationStatusDetailsPanel");
		setLayout(new java.awt.BorderLayout());
		setSize(692, 609);
		add(getJScrollPane1(), "Center");
		add(getJPanel1(), "North");
		initConnections();
		connEtoC2();
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
		SimulationStatusDetailsPanel aSimulationStatusDetailsPanel;
		aSimulationStatusDetailsPanel = new SimulationStatusDetailsPanel();
		frame.setContentPane(aSimulationStatusDetailsPanel);
		frame.setSize(aSimulationStatusDetailsPanel.getSize());
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
 * Sets the simulationStatusDetails property (cbit.vcell.client.desktop.simulation.SimulationStatusDetails) value.
 * @param simulationStatusDetails The new value for the property.
 * @see #getSimulationStatusDetails
 */
public void setSimulationStatusDetails(SimulationStatusDetails simulationStatusDetails) {
	SimulationStatusDetails oldValue = fieldSimulationStatusDetails;
	fieldSimulationStatusDetails = simulationStatusDetails;
	firePropertyChange("simulationStatusDetails", oldValue, simulationStatusDetails);
}


/**
 * Comment
 */
public void simulationStatusDetailsPanel_Initialize() {
	getScrollPaneTable().setDefaultRenderer(java.util.Date.class, new cbit.vcell.messaging.admin.DateRenderer());
	getScrollPaneTable().setDefaultRenderer(Object.class, new cbit.gui.DefaultTableCellRendererEnhanced());
	return;
}


/**
 * Comment
 */
public void simulationStatusDetailsPanel_SimulationStatusDetails(cbit.vcell.client.desktop.simulation.SimulationStatusDetails simStatusDetails) {
	if (simStatusDetails == null) {
		return;
	}
	
	cbit.vcell.solver.Simulation sim = simStatusDetails.getSimulation();
	getNameTextField().setText(sim.getName());
	getIDTextField().setText("" + (sim.getKey() != null ? sim.getKey().toString() : ""));
	getSolverTextField().setText("" + sim.getSolverTaskDescription().getSolverDescription().getName());
	return;
}


/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GEBFBB0B6GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135BB8DD4DC5519B91E68E6D7DC71183528B93B9C255D5845CAB5DBD39B57EC1BE32922A6142C3B16EC31269A375693EDEA599E6AF94C70B31310B0B1D133DB0CE90DCA82A2BF09C1C292FE839386B240B89988849E4C83A699E606F90FC0E25C6E775D7B6EFBEFE65E9BB4EA4E79725E7D6E7D6E77F33F7B5D6F3BEF60F63F1DDC18EF5A4DF179051C7C6FC8BE47ADBA46F19B331E30B0DC7D7B19D6CE73
	6FCFGBB39F34585706C043E1C71CC2B0D2B3A0884C3504E9B4B343E896F3B383A0BCB0F420B819FBF74AD39FD19901F0F54467894D33E79A5705C81B08AB8FC263E0F7CAF666F11710BE5BC03FC9347A51763181B724BE45CE698E3815C90C0281C0EDF82CFAA40358D757238211B16F3DB9E891B3663BC1AB999EC12E9CB8E5067BE6E7779AB8EA02F87081CC853866DABG283C197B43058EF8B6BAAFCC9E9F9F73C9D59747853F3FEA5C6F9382D215D790AFC841D015681B1E757312AF98E8E868D75E7BA5DE
	1A955B84097779C5A79F907CDC75C164E5B2D9485C9C178BFCD6915C7D0548DF0277840014038C1FDA04F82EA2533A87203A0269FC6C7DC6D9A74F2777F3D93553852586BAF7D6D0DDC3952A4E9967B7187E072C4F0AF6DC07D468DB8B309140D219E9AD87688478A3710B9F349D04E77318549A1C1E8E861AC321963FB75C9D748AFEF8EFE8009E9957A70C8743DE0E23EB3FBA3FD2C71EF8C0699E7F42F454A64B6B189F2476FD074B3E6F96D3310ECD3217FFEA9A2D2436182FD4EDC2F9971B0CF8A7D5C67AE9
	A46FB31C16F77A4CFAF3218E6F0D67561BD365F9D6EBF8EF2944352EA0EB1D836FC80774947F914157FF2062294FBEC8FC865B8E68CB7B007906E7871345D1F087E72B79062E9D12FE18E05ADDC5E5282C0A16E5DFB14E6D2922BC2D0AAC9F903C2441D3D96EA5B2E2BB917A36GD07FFA308377306743CC2B8410G10823086A087E04B074C478A5ABF7C1CBE666443D24B2C4F6F95429C25FD307D8DF88A73D2183787C4098F0C8BEEBE6C63477C024849E39EEEC79B0CC550BDCAFCFF9CF8F48881A1CCB6AD4C
	4ECF8B12909669182C761DB8A6AC10DE17EFDAG8653A10E4D1B5D3E8B1EFEDE14BE89F9F9C9D0C7507E0D6DE8130B3C9BE60489601D5A653FBA51DE3B2BB32D15G0D554C8EA3A7F13EAEA18C61222141918CDE188D39D093AE219A6D3C17448EA73CCFD5B39BBFEBC75CDAE8EFD266396564EB1AF95AFC63A8BE9F3E4476D8CD91526441787DB54CEFDE74B03FE95BFEB7D7397E6FF47D46B3325E6C2A217E622F095EBF0DE4DE14EDE98DBB83FED46879027BB99A7657C46EBD14BBB2E6E4FFE99E05D1ED3366
	1D5A595505EB3682E4D98B30390659FA5D49FD54F7DBF8D1E05EFABA8C0EA6F8D54BD3B95FAB3649B6B54BBC0B959D6AC81F259662BC0087E0D1AD33614AA20F8E5D1751B7B66E2EA3BC3473DB3FC29E54D6BF6E40F3CA388847E71B909282DEF891653E47BB101F3C0FA7028ACD964107E05F3742C693957CD39D38B7F9C98A7B46E6A521DF10A4DFE0D26D7F8561A54E0E05541041F8BD5F01BA8667E05FAB63DF6C40BDB39E8C848432E88A095C3F196CD941AFCC03502D415900C4712FF4E24EF3C138A4470C
	5FF796F138677D3398FFE89E1146EFC39D423EE9F0858CE1DCEEAD6E674326BD9A9BAD879C66C399704C29E57BF26E9D36AF77DF3E0BDBBD92B2691DADC95F3C5154D4C777052BCE5D17D426DF5975E5FF09448E99AFD2D957CFE14C0E174818896DA16E4F4663C850A2713B066D1F9B9C646CG5916GA457317DB3FD8A7BD92C12B7CEEB506F274BGFD36C3E82BC5C53BB57A2E2C2336D2FDEB308B770FA84C4C8AF084A07FAEE5CD782E57227A5C727A50FEDF972E0FDF93EFD7921E1345AC87759C22B1AD84
	4FC5GA9GFB816FBDCC4EE88B9BD7F0184AD5F6180EEFBAAC67DF9F613847EDECDC1ABCAEDDFE3E90B16E7A1DEC5C2A0FE8FF167CFC61A3BA0E2E55EF8E207F107C74BCFF111F2F9267E08FB4B41CA26BC4734DFFA0E7860E5FFF0465150F125CE1885A92C0429166EB6B36333CF2690565DCCAB84BEC14CB279C21F2249FD1FD2DF2F44ECC577A49EE64E929073C351E2D7346AE6D5956AC0A41F19FF19CF98DEEF6E8FD1162129CDB61B9618B0B129D0245BCDB2F3FF7203CF86E4AE8599EC54D8E6E2B4857D5
	7725F6796C2456AE2BCABFB3EDB330CBE5BD350723DE350B2D6795B34D1392C97E047BA7817A57G503D74BBD21FC9D3BE11460593D6EDDC4803F17BBE4E343681F8BEE6B4EFDBD067E99412E3F9684B4DCDB2CD46F76E60D696A718746A0229FFCCB205BE2672A5FD2C4A191A3341349A004ABAE2E17965A698332F0171FD5802752228EFB80E0E394F96EF8C15756E3DDAD9AD7C353A32A6BCBFE972B4D09997B5283216E7EEB0B5E6B2D9FFE7E3322643986EA813755C4EF8F2D01F795B4B3346E348BE9AB170
	12F24767F132AA836A23875C4A3A5D31CBEB0B463F1C5335451252A9D37DD1EA0351232AADD62F5BE04ADA47EC31E2A733C5AA0C592858620F2FA15E9770C9BE5E6F3BAC149F207A7F740D038B69EF579039D93E1092D3AB15B52AF9D9F5E3EC3DC4EB089324D6E8EB0C2EF3CE913C27B13A4ED92754B9FE68DB5B4872DE3793774821E319D6A7409440A200E4005547D84CF92F3B0EDB202E3087A05D8F707E7E08C40165775DE8BBB85B65F6CEB74AC14EF60E1DEBEBFA71DC4A861E39G3663D0BF83F49EE7B2
	CC76ACA8C37B3C2E8C544E3794AFD48FCF9D0F34353CBE8B52A5C451D1FFB87F294119C202A20DCF5794E963D34AF1DA0F2C0567C6GFA6E4CD959BDC75EF1F66EBC3C9557725009CC2B8340FB022D657F7663F9F7855AC9GE9G99G1B81CA1AE03EA6E64B7BFBDA9632E5B3CB88DDC16535B5BA29E3B692AC51B66AEC0A34093EED45983A3DD1EDEF54BCD44F9BDDC4CF68CB82C88348G588CD0528CFAB6B3BD2D270FAE68B318C4030EC42DD885291CFF30DD1DFE5BF6AE241F33D9CF3FA2B76AB785FD0B81
	16812C8448864885A8EBE17A8D1EBE76B975EB85DCA764E5BACBA84B7AC00C2C1FCF4774983A2A28F6E3CBA49D2D41FFC072A99774CD81D0DCD2816292201D5242EA36F6D28B27C3BBC741B9898E4F72AD8A6E493835EBCECBEC9C5503F403F114476638BC22EBDFA3B09B60653B8B27F1EDB761542F6D7CCE7D5AAE6C5CC971B00776460775DFF6462F7D2EF19A57FE993D46F27E34D7DF4E7F68551773673D7165FC2E57C84E3708BD1F13DE67F4EA4E9EFEDA106D3D0BFB2C58AA0FF1CAC1E78D476274E4BB4E
	2193277B4743D0BD61498D7DB496BFC07C8EFDE1CFAB0B45FFC372BBA73427G9635B2FF3CE20DF7EEA4359A1D6B8D5BF03E54D6FAFE67AB78D602DF294153F3BDE3AB3BF3DF83FD65EDACB66C6FB7DE1F0AFE7D75296947BAC58C4E06470568B5286F5F237429D144AE8953E8E6BA261950CBFCF8D208D943CEC2CF7B0C68FB7B319E9873F9FDE139EA256B7B13B7CA09FF0822A012005646CB3C3C2E4BACECDD5B25E0F70D426FD617317E49AEFD7D3F65B2567FEE57427AFF4FE52C7F032E0575FF58E5646772
	DD41CE0D2E2D2A2EB637312EEF3A75F5FD4BED2C6BFE77423AFE60B6567510FBE1DD0F39636B7AD2042E358ABE530477C2FE7ED2F0D662385CB65C834FAB77BF8950CE8148G48E92379945D675F1DC35F1A8873534026BFA40CF3D96DC8FBCF8123ED02368BC082D88A30221D657C5F88639E53C4049A0A3FD3423DCEEF89EA643B28CB5B226F22D006321310171CE4316121A2969BBA81B7F4B2B29604CE9A4506D7C92CD9FC1246G330237D8913FD403176BE48B2B13D3204F52414E591B2D6C1C75F7B0191A
	50BC72B9E7573CE7EE7D32F7427100727855574A035AE227194549C450B93D03794533D5680FCA7C2F626724060616A05E1FB928135158BFE0364874DCA70B7DFFC5662C073693E02A135D7F4C3F476EB9D23FF5871794503F7F49F1FDE6DA54C957BA25D32DC3F34EEFB0D11E6B8B984F9C685FE3C71C39D0FBFF07EB7B23821AF3517E43917D38478EF05A7E4908FE1457D7286D3FB5223FD0C3CFF5FB74DD26DB5E23E91C7F1F979BFEA79E35D31D964B4F440D3766EF3A6732196A765DED6CBEED151D597359
	9DE84FAD50AEB915E95DFF0A796B3F11F337915A238A6E17450813203DCC411D30B03FCEBB457C3A400AF7C259CAFB87E9635D23DC97DA343E07633EFA7F3EA70207D17EF735C0635837153B2EDC502124AB53DA8960G70F7B1DD5FB4DF5D3764F72F126E5C97262371EC20G7DA021ABBA879AA378242E689C6886E5EF27C01F45416A616D9E34099F5ACBG1281D6827CBB4096874B13F25C555C82B514AB98F240616A572426D43F278A2E6E9B7B3F9633BD346201EFF3AB2FEBB33C8735F4D3FFACE74FE7EE
	4D47A755AFF9G7573C01F84108410863086A03B1B69D7FE26126A576E75E1D54F0FCF111BE1261C5BA74C29E8BC9829EE5F5B962D5B66EEF5EF9474981DD167CAF1AD767450B55A2E6027893ED10327EB77825973589E053E0C9EF6667E3A886F9CAF8AE149B74E7B69515B42071DC13FEF7C92B7FF1A1CFBA486FCGD0FF1A51659523ED2602E15F65E0C00A2564E45ABDA4D6B4832C423693F1EE4B3EB8EB17F71A6ACC79BCF199F3AEB5A5205FBBE8ECDBEBE5F15B63E4F17BD5EBF45C3EA9DF9BF72B37C647
	ED24D77B4736C6476DC87A25DB2363B623276BB902E1CFFE1F523C5FEC7E7A4F77542F15877573B7BC46B970DB9E7D9C789D0FFEBDFA40933FAE2E71985745759E6DB7D17937850338B609608B29GEB81B68264BA599EFD58035FG692DDB747EEC77CEE28E0E3F4810987F3B860CF5CD9BD878F79C5F9F406FC1E3C1C98ACE637C8C7F50G2E17948C91A4452D99C09B05FD13D3929DAA639F99C09F778B9384CD4F5F473631BB1D5D3D6CCC3EFD87BB136BFB59B964AD4538650576BC0E83C886D88510250C91
	49184D503675C16E8C60848881A47631B1AF6F980D72852447672ADEB5BEA57446462A79A8DCF2D45B2BE9D3DE5BCB23F9ED171F5B227CBD1AD7743DD0746F814CD16F3B387545D85BE92EA32AB435C7153B3D4FE5EFEDF60C7437761DF6B8DA1A7BC61A9DE71A877AB9F63EA44FE9FF4BD73F759A5DEF5D88E9FDD44FB572B36F755B72298C9FDA6A624850F93A4FBEF83A4725A7852597ADD827358A78DD0049B5A730F32F6C1F6E644A5EEEB26DB410ABCF16273CDF4D0B5535D89F77EE45B8C63CB8E83CEFFE
	BB38703EF9F510FCDB85FFF7DD8A49F52C79AC6A991C189085E5FFDA4E5A941CDDF486C59F563F34D6FDC5298F07C0B789E099C01A2CA7BD076E5D31AF0EAE697D5AF368A51154208EFECC70574A38593DAA2ECE4665EFD3F107E45C79B274E12C49DD2078C91F6077AA73DA48EFAD6CEDEA8F45D7D711F3AF680723D96D233FAD382BD4FB86E577537BD94E85FD247F5530DE7FEE253FCE546BAFD77A170EE87B9BDD345F2B743FD32757BF2F74DBAB747A93DCACDF3CF18857B7995A2BDCACB65FFE36821EEE7E
	3C1C99E29A958C13688CC8E7B898029465924512DFC348B17F0743380659B0CFAEC059A71956C3G1DG23G92C042A70C4753E74B63702045A8B983423CAFA0513CE9EF894BF91743BCB91F30FA36E6943F9DC455339DE11F37051F2485AD4DD1A6CAD80E32474D6A2F25E4CEA73427G9639D9CE75448E1C3BA7981626B865B79393865F370807D19EB92596B9A6A53959772C43A2432DD5F067951C658C53635D72B27D79952723340F0E30BA32698C1BEF300E616695DCD38543251DE1F6DFBA04FE201C73F4
	7964BDFC7B907A0A721DD77E269B8699E87FE3434631A4EBF861D832E1D87F8CFEE6B8768C5EB42CFF863FB82CBD03A96EB7723CE165821C627FFB9877BA3DB76B66837C24665B4F7E122B2B6FEA2F126E42972623B5C400548A1B4EC45705F3745E60CCF4DDF81B32C7B88F44898F4B9BFED92A651F6E2171B4DBFEEE161F858351B9C00328255BA377D74A4FA679690A218BC650CD497DD764E7227CCC0921DBBB924FCEABE1FC46C0E45EA2677AF57168F200E60BAEDDFBC5BCBA4BA04431C1BDBA89DF65774F
	B46F49A5DFFFED717D2F15874DE173C89C2B865DBB87D9DE5BE3A3FF7B81ED3F023B4F46F25DF985F7EB18FC3F0476A68557AB464EF7D9E47365A9381F09ECBE4BD9064B55196FBA1D796ED16673A834B6094D67D5F083A33173BDB742462DD0467D3896F185C3D04F8EB15C45BAB62ED2415DFB100D3B226006AA5838C485B7DA4846658C31F3E6178DE397045B49G56C09ED62BD7903BEE01F116E1B6D630115FFAC1102D9F463F21F988D397EE59B00EFF445696E7CDD90CB04BF085E890DE17710970FEFD3E
	BA86EA2AB07E66CE3DD3E8E6924A72BD92E66757C62025727CC2D065F9D9DCC89EF39CF9EC67B22D774B7250398BA755395F17961A3BB04E5CE5B07735725CF43ED7C758FC6567E26BA6F3143C0D675479100F731C9A03296D7E3316664CBE890FBFEA2F5F56325CE38846D33EB754B13EA1833EDAD89C45B7C94317727859C146A3C561A1D6B09EAB8DF8E8ED159145A3A70647DF97B29EAFC05F5BC4DF08139C7B37798435C10B8D6BFB160ED5358627C341G7E4C2D4F5E5169023A495E51535D5E63E2F55293
	E735751B6DB63F09710D16199B117F96EBC42D13E265E0FE73AC26EFB20F27CB3435D85B13ED26F78CF8B449F3A7EAF8E4CC6FB3D35D1E8E6F63A26B44FE7C1FDDD908D4C74A5FF9DE15C53C006FB4B77BF01465E669A37AF624B43760CFB4E53FAA294371933CDFD43F4F7F0B1277F1237166A98F28733CFCB0F61E94A5D72C0FBBCFD7D01DA739D0BB8F2D81F2C9BF4D3F6C136FF286B9FE7318F48A6CF7FA6C3CB0AE3978C9057E1D997457904810G1086108E108D3085404243F98CD08D5089B08AA081AC81
	D886108A309AA00BE779EE056F00118C00140B843550605899FBE8D41B97EC02797278785FC9643FD90948C3ECE319567DE31F07AED825252B879A4782F4D43FA3B346B979711905F3720E997D1C3CFBA6B6A76F1B514F49BDB331B979708C7A46A45857230C9BD3F0838AEE124C0938B3BEAF2935645F769339905FA9A0C3069709DCF3846711F1739A5C000C7BEC866FA8C0E5B842145F764C6079B961737BE97BBAB246979085C9314775C4C6DF0857481D2860D0EEFA16F7117DBBBAC663A345B989EEBE8277
	D4DD6C381F57450E3B2EAAF65C75D55AF1B4CE736FE9FE0BCBCCC671E5735BD5BCB399C77FBE7739427570CC7DB5D7A8F185D8A0C91C495EA6F1C574CAC38CF325CA203693EB64DC2CEBB0BFF3275944955839A26A9DF62EG17584E95928F30F3E6700E79909775CF1C617E0F440778D8D38436C08BDF844B3158FE60AF5A73A3775AF1437B3F7DDE3A2F6A3DEAEC2731679372FDC482FC2217450FEF96633796239856BF4D7B7D109DDDE88EF8B11E35DC1284754EFC5002730BEC0C5CA97FDD89798D4FF98D4D
	7F83D0CB87884E908DF87094GG28BDGGD0CB818294G94G88G88GEBFBB0B64E908DF87094GG28BDGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGAA95GGGG
**end of data**/
}
}