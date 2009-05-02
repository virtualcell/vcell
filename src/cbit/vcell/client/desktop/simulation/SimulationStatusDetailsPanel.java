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
		frame.setVisible(true);
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
	getScrollPaneTable().setDefaultRenderer(java.util.Date.class, new org.vcell.util.gui.DateRenderer());
	getScrollPaneTable().setDefaultRenderer(Object.class, new org.vcell.util.gui.DefaultTableCellRendererEnhanced());
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
	getIDTextField().setText("" + (sim.getKey() != null ? sim.getKey().toString() : "")+
			(sim.getSimulationVersion() != null &&
				sim.getSimulationVersion().getParentSimulationReference() != null
				?" (parentSimRef="+sim.getSimulationVersion().getParentSimulationReference().toString()+")"
				:"")
	);
	getSolverTextField().setText("" + sim.getSolverTaskDescription().getSolverDescription().getDisplayLabel());
	return;
}

}