package cbit.vcell.mapping.gui;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.vcell.model.*;
import cbit.vcell.modelapp.SimulationContext;
import cbit.vcell.mapping.*; 
import java.beans.PropertyChangeListener;

/**
 * This type was created in VisualAge.
 */
public class InitialConditionsPanel extends javax.swing.JPanel {
	private SpeciesContextSpecPanel ivjSpeciesContextSpecPanel = null;
	private static final String ALL_SPECIES = "All Species";
	private static final String ALL_STRUCTURES = "All Structures";
	private cbit.vcell.modelapp.SimulationContext fieldSimulationContext = null;
	private boolean ivjConnPtoP3Aligning = false;
	private SimulationContext ivjsimulationContext1 = null;
	private javax.swing.JScrollPane ivjJScrollPane1 = null;
	private javax.swing.JTable ivjScrollPaneTable = null;
	private SpeciesContextSpecsTableModel ivjSpeciesContextSpecsTableModel = null;
	private boolean ivjConnPtoP5Aligning = false;
	private javax.swing.ListSelectionModel ivjselectionModel1 = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private javax.swing.JDialog ivjJDialog1 = null;
	private javax.swing.JPanel ivjJDialogContentPane = null;
	private javax.swing.JTextArea ivjJTextArea1 = null;
	private javax.swing.JScrollPane ivjJScrollPane2 = null;
	private javax.swing.JButton ivjJButtonCancel = null;
	private javax.swing.JButton ivjJButtonOK = null;
	private javax.swing.JSplitPane ivjJSplitPane1 = null;

class IvjEventHandler implements java.awt.event.ActionListener, java.awt.event.MouseListener, java.beans.PropertyChangeListener, javax.swing.event.ListSelectionListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == InitialConditionsPanel.this.getJButtonCancel()) 
				connEtoM4(e);
			if (e.getSource() == InitialConditionsPanel.this.getJButtonOK()) 
				connEtoM5(e);
		};
		public void mouseClicked(java.awt.event.MouseEvent e) {
			if (e.getSource() == InitialConditionsPanel.this.getScrollPaneTable()) 
				connEtoC1(e);
		};
		public void mouseEntered(java.awt.event.MouseEvent e) {};
		public void mouseExited(java.awt.event.MouseEvent e) {};
		public void mousePressed(java.awt.event.MouseEvent e) {};
		public void mouseReleased(java.awt.event.MouseEvent e) {};
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == InitialConditionsPanel.this && (evt.getPropertyName().equals("simulationContext"))) 
				connPtoP3SetTarget();
			if (evt.getSource() == InitialConditionsPanel.this.getScrollPaneTable() && (evt.getPropertyName().equals("selectionModel"))) 
				connPtoP5SetTarget();
		};
		public void valueChanged(javax.swing.event.ListSelectionEvent e) {
			if (e.getSource() == InitialConditionsPanel.this.getselectionModel1()) 
				connEtoM3(e);
		};
	};

/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public InitialConditionsPanel() {
	super();
	initialize();
}


/**
 * connEtoC1:  (ScrollPaneTable.mouse.mouseClicked(java.awt.event.MouseEvent) --> InitialConditionsPanel.showCustomEditor(Ljava.awt.event.MouseEvent;)V)
 * @param arg1 java.awt.event.MouseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.MouseEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.showCustomEditor(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC2:  ( (JButtonOK,action.actionPerformed(java.awt.event.ActionEvent) --> ScrollPaneTable,setValueAt(Ljava.lang.Object;II)V).exceptionOccurred --> InitialConditionsPanel.connEtoM5_ExceptionOccurred(Ljava.lang.Throwable;)V)
 * @param exception java.lang.Throwable
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.lang.Throwable exception) {
	try {
		// user code begin {1}
		// user code end
		this.connEtoM5_ExceptionOccurred(exception);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM1:  ( (JButtonOK,action.actionPerformed(java.awt.event.ActionEvent) --> ScrollPaneTable,setValueAt(Ljava.lang.Object;II)V).normalResult --> JDialog1.dispose()V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM1() {
	try {
		// user code begin {1}
		// user code end
		getJDialog1().dispose();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM2:  (simulationContext1.this --> SpeciesContextSpecsTableModel.simulationContext)
 * @param value cbit.vcell.mapping.SimulationContext
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM2(cbit.vcell.modelapp.SimulationContext value) {
	try {
		// user code begin {1}
		// user code end
		getSpeciesContextSpecsTableModel().setSimulationContext(getsimulationContext1());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoM3:  (selectionModel1.listSelection.valueChanged(javax.swing.event.ListSelectionEvent) --> SpeciesContextSpecPanel.setSpeciesContextSpec(Lcbit.vcell.mapping.SpeciesContextSpec;)V)
 * @param arg1 javax.swing.event.ListSelectionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM3(javax.swing.event.ListSelectionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getSpeciesContextSpecPanel().setSpeciesContextSpec(this.getSelectedSpeciesContextSpec(getselectionModel1().getMinSelectionIndex()));
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM4:  (JButtonCancel.action.actionPerformed(java.awt.event.ActionEvent) --> JDialog1.dispose()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM4(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getJDialog1().dispose();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM5:  (JButtonOK.action.actionPerformed(java.awt.event.ActionEvent) --> ScrollPaneTable.setValueAt(Ljava.lang.Object;II)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM5(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getScrollPaneTable().setValueAt(getJTextArea1().getText(), getScrollPaneTable().getSelectedRow(), getScrollPaneTable().getSelectedColumn());
		connEtoM1();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		connEtoC2(ivjExc);
	}
}

/**
 * Comment
 */
private void connEtoM5_ExceptionOccurred(java.lang.Throwable arg1) {
	javax.swing.JOptionPane.showMessageDialog(getJDialog1(),arg1.getMessage(),"Error setting new expression",javax.swing.JOptionPane.ERROR_MESSAGE);
}


/**
 * connEtoM6:  (simulationContext1.this --> SpeciesContextSpecPanel.simulationContext)
 * @param value cbit.vcell.mapping.SimulationContext
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM6(cbit.vcell.modelapp.SimulationContext value) {
	try {
		// user code begin {1}
		// user code end
		getSpeciesContextSpecPanel().setSimulationContext(getsimulationContext1());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP3SetSource:  (InitialConditionsPanel.simulationContext <--> simulationContext1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP3SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP3Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP3Aligning = true;
			if ((getsimulationContext1() != null)) {
				this.setSimulationContext(getsimulationContext1());
			}
			// user code begin {2}
			// user code end
			ivjConnPtoP3Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP3Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP3SetTarget:  (InitialConditionsPanel.simulationContext <--> simulationContext1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP3SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP3Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP3Aligning = true;
			setsimulationContext1(this.getSimulationContext());
			// user code begin {2}
			// user code end
			ivjConnPtoP3Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP3Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP4SetTarget:  (ScrollPaneTable.model <--> model2.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP4SetTarget() {
	/* Set the target from the source */
	try {
		getScrollPaneTable().setModel(getSpeciesContextSpecsTableModel());
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
 * connPtoP5SetSource:  (ScrollPaneTable.selectionModel <--> selectionModel1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP5SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP5Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP5Aligning = true;
			if ((getselectionModel1() != null)) {
				getScrollPaneTable().setSelectionModel(getselectionModel1());
			}
			// user code begin {2}
			// user code end
			ivjConnPtoP5Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP5Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP5SetTarget:  (ScrollPaneTable.selectionModel <--> selectionModel1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP5SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP5Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP5Aligning = true;
			setselectionModel1(getScrollPaneTable().getSelectionModel());
			// user code begin {2}
			// user code end
			ivjConnPtoP5Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP5Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * Return the JButtonCancel property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonCancel() {
	if (ivjJButtonCancel == null) {
		try {
			ivjJButtonCancel = new javax.swing.JButton();
			ivjJButtonCancel.setName("JButtonCancel");
			ivjJButtonCancel.setPreferredSize(new java.awt.Dimension(75, 25));
			ivjJButtonCancel.setText("Cancel");
			ivjJButtonCancel.setMaximumSize(new java.awt.Dimension(75, 25));
			ivjJButtonCancel.setMinimumSize(new java.awt.Dimension(75, 25));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonCancel;
}


/**
 * Return the JButtonOK property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonOK() {
	if (ivjJButtonOK == null) {
		try {
			ivjJButtonOK = new javax.swing.JButton();
			ivjJButtonOK.setName("JButtonOK");
			ivjJButtonOK.setPreferredSize(new java.awt.Dimension(75, 25));
			ivjJButtonOK.setText("OK");
			ivjJButtonOK.setMaximumSize(new java.awt.Dimension(75, 25));
			ivjJButtonOK.setMinimumSize(new java.awt.Dimension(75, 25));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonOK;
}


/**
 * Return the JDialog1 property value.
 * @return javax.swing.JDialog
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JDialog getJDialog1() {
	if (ivjJDialog1 == null) {
		try {
			ivjJDialog1 = new javax.swing.JDialog();
			ivjJDialog1.setName("JDialog1");
			ivjJDialog1.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
			ivjJDialog1.setBounds(484, 412, 485, 96);
			ivjJDialog1.setModal(true);
			ivjJDialog1.setTitle("Expression Editor");
			getJDialog1().setContentPane(getJDialogContentPane());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJDialog1;
}

/**
 * Return the JDialogContentPane property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJDialogContentPane() {
	if (ivjJDialogContentPane == null) {
		try {
			ivjJDialogContentPane = new javax.swing.JPanel();
			ivjJDialogContentPane.setName("JDialogContentPane");
			ivjJDialogContentPane.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJScrollPane2 = new java.awt.GridBagConstraints();
			constraintsJScrollPane2.gridx = 0; constraintsJScrollPane2.gridy = 0;
			constraintsJScrollPane2.gridwidth = 2;
			constraintsJScrollPane2.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJScrollPane2.weightx = 1.0;
			constraintsJScrollPane2.weighty = 1.0;
			constraintsJScrollPane2.insets = new java.awt.Insets(4, 4, 4, 4);
			getJDialogContentPane().add(getJScrollPane2(), constraintsJScrollPane2);

			java.awt.GridBagConstraints constraintsJButtonOK = new java.awt.GridBagConstraints();
			constraintsJButtonOK.gridx = 0; constraintsJButtonOK.gridy = 1;
			constraintsJButtonOK.anchor = java.awt.GridBagConstraints.EAST;
			constraintsJButtonOK.weightx = 1.0;
			constraintsJButtonOK.insets = new java.awt.Insets(4, 4, 4, 4);
			getJDialogContentPane().add(getJButtonOK(), constraintsJButtonOK);

			java.awt.GridBagConstraints constraintsJButtonCancel = new java.awt.GridBagConstraints();
			constraintsJButtonCancel.gridx = 1; constraintsJButtonCancel.gridy = 1;
			constraintsJButtonCancel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJButtonCancel.weightx = 1.0;
			constraintsJButtonCancel.insets = new java.awt.Insets(4, 4, 4, 4);
			getJDialogContentPane().add(getJButtonCancel(), constraintsJButtonCancel);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJDialogContentPane;
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
 * Return the JScrollPane2 property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getJScrollPane2() {
	if (ivjJScrollPane2 == null) {
		try {
			ivjJScrollPane2 = new javax.swing.JScrollPane();
			ivjJScrollPane2.setName("JScrollPane2");
			getJScrollPane2().setViewportView(getJTextArea1());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJScrollPane2;
}

/**
 * Return the JSplitPane1 property value.
 * @return javax.swing.JSplitPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JSplitPane getJSplitPane1() {
	if (ivjJSplitPane1 == null) {
		try {
			ivjJSplitPane1 = new javax.swing.JSplitPane(javax.swing.JSplitPane.VERTICAL_SPLIT);
			ivjJSplitPane1.setName("JSplitPane1");
			ivjJSplitPane1.setDividerLocation(300);
			getJSplitPane1().add(getJScrollPane1(), "top");
			getJSplitPane1().add(getSpeciesContextSpecPanel(), "bottom");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJSplitPane1;
}


/**
 * Return the JTextArea1 property value.
 * @return javax.swing.JTextArea
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextArea getJTextArea1() {
	if (ivjJTextArea1 == null) {
		try {
			cbit.gui.BevelBorderBean ivjLocalBorder;
			ivjLocalBorder = new cbit.gui.BevelBorderBean();
			ivjLocalBorder.setColor(new java.awt.Color(160,160,255));
			ivjLocalBorder.setBevelType(1);
			ivjJTextArea1 = new javax.swing.JTextArea();
			ivjJTextArea1.setName("JTextArea1");
			ivjJTextArea1.setBorder(ivjLocalBorder);
			ivjJTextArea1.setBounds(0, 0, 233, 103);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextArea1;
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
			ivjScrollPaneTable.setAutoCreateColumnsFromModel(true);
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
 * Comment
 */
public cbit.vcell.modelapp.SpeciesContextSpec getSelectedSpeciesContextSpec(int index) {
	if (getSimulationContext()!=null && index >= 0){
		return getSimulationContext().getReactionContext().getSpeciesContextSpecs(index);
	}
	return null;
}


/**
 * Return the selectionModel1 property value.
 * @return javax.swing.ListSelectionModel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.ListSelectionModel getselectionModel1() {
	// user code begin {1}
	// user code end
	return ivjselectionModel1;
}


/**
 * Gets the simulationContext property (cbit.vcell.mapping.SimulationContext) value.
 * @return The simulationContext property value.
 * @see #setSimulationContext
 */
public cbit.vcell.modelapp.SimulationContext getSimulationContext() {
	return fieldSimulationContext;
}


/**
 * Return the simulationContext1 property value.
 * @return cbit.vcell.mapping.SimulationContext
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.modelapp.SimulationContext getsimulationContext1() {
	// user code begin {1}
	// user code end
	return ivjsimulationContext1;
}


/**
 * Return the SpeciesContextSpecPanel property value.
 * @return cbit.vcell.mapping.SpeciesContextSpecPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private SpeciesContextSpecPanel getSpeciesContextSpecPanel() {
	if (ivjSpeciesContextSpecPanel == null) {
		try {
			ivjSpeciesContextSpecPanel = new cbit.vcell.mapping.gui.SpeciesContextSpecPanel();
			ivjSpeciesContextSpecPanel.setName("SpeciesContextSpecPanel");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSpeciesContextSpecPanel;
}

/**
 * Return the SpeciesContextSpecsTableModel property value.
 * @return cbit.vcell.mapping.gui.SpeciesContextSpecsTableModel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private SpeciesContextSpecsTableModel getSpeciesContextSpecsTableModel() {
	if (ivjSpeciesContextSpecsTableModel == null) {
		try {
			ivjSpeciesContextSpecsTableModel = new cbit.vcell.mapping.gui.SpeciesContextSpecsTableModel();
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSpeciesContextSpecsTableModel;
}


/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	System.out.println("--------- UNCAUGHT EXCEPTION --------- in cbit.vcell.mapping.InitialConditionPanel");
	exception.printStackTrace(System.out);
}


/**
 * Initializes connections
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	this.addPropertyChangeListener(ivjEventHandler);
	getScrollPaneTable().addPropertyChangeListener(ivjEventHandler);
	getScrollPaneTable().addMouseListener(ivjEventHandler);
	getJButtonCancel().addActionListener(ivjEventHandler);
	getJButtonOK().addActionListener(ivjEventHandler);
	connPtoP3SetTarget();
	connPtoP4SetTarget();
	connPtoP5SetTarget();
}

/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("InitialConditionsPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(456, 539);

		java.awt.GridBagConstraints constraintsJSplitPane1 = new java.awt.GridBagConstraints();
		constraintsJSplitPane1.gridx = 0; constraintsJSplitPane1.gridy = 0;
		constraintsJSplitPane1.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJSplitPane1.weightx = 1.0;
		constraintsJSplitPane1.weighty = 1.0;
		constraintsJSplitPane1.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJSplitPane1(), constraintsJSplitPane1);
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
		InitialConditionsPanel aInitialConditionsPanel;
		aInitialConditionsPanel = new InitialConditionsPanel();
		frame.setContentPane(aInitialConditionsPanel);
		frame.setSize(aInitialConditionsPanel.getSize());
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
 * Set the selectionModel1 to a new value.
 * @param newValue javax.swing.ListSelectionModel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setselectionModel1(javax.swing.ListSelectionModel newValue) {
	if (ivjselectionModel1 != newValue) {
		try {
			/* Stop listening for events from the current object */
			if (ivjselectionModel1 != null) {
				ivjselectionModel1.removeListSelectionListener(ivjEventHandler);
			}
			ivjselectionModel1 = newValue;

			/* Listen for events from the new object */
			if (ivjselectionModel1 != null) {
				ivjselectionModel1.addListSelectionListener(ivjEventHandler);
			}
			connPtoP5SetSource();
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
 * Sets the simulationContext property (cbit.vcell.mapping.SimulationContext) value.
 * @param simulationContext The new value for the property.
 * @see #getSimulationContext
 */
public void setSimulationContext(cbit.vcell.modelapp.SimulationContext simulationContext) {
	SimulationContext oldValue = fieldSimulationContext;
	fieldSimulationContext = simulationContext;
	firePropertyChange("simulationContext", oldValue, simulationContext);
}


/**
 * Set the simulationContext1 to a new value.
 * @param newValue cbit.vcell.mapping.SimulationContext
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setsimulationContext1(cbit.vcell.modelapp.SimulationContext newValue) {
	if (ivjsimulationContext1 != newValue) {
		try {
			cbit.vcell.modelapp.SimulationContext oldValue = getsimulationContext1();
			ivjsimulationContext1 = newValue;
			connPtoP3SetSource();
			connEtoM2(ivjsimulationContext1);
			connEtoM6(ivjsimulationContext1);
			firePropertyChange("simulationContext", oldValue, newValue);
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
 * Comment
 */
private void showCustomEditor(java.awt.event.MouseEvent mouseEvent) {
	if (mouseEvent.getClickCount() == 2) {
		int row = getScrollPaneTable().getSelectedRow();
		int col = getScrollPaneTable().getSelectedColumn();
		switch (getScrollPaneTable().convertColumnIndexToModel(col)) {
			case SpeciesContextSpecsTableModel.COLUMN_INITIAL: {
				getJTextArea1().setText(getScrollPaneTable().getValueAt(row, col).toString());
				cbit.util.BeanUtils.centerOnComponent(getJDialog1(), this);
				getJDialog1().show();
				return;
			}
		}
	}
}


/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G3A0171B4GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E145BC8DF8D35559B8812BD6ADD230E2E565CF2A960658CD442A85D1AAD4E50EED6CDB9FBF36318DB736310DEDEC32F91B965B5282C5F0202256099B0C2E8205D98BCECA0B3410361156D830582649ED13B649CD131BB625257D5E73735E5CA4B78D7411FDBC4F4B6DF94FFB4E7BFB5E73F34F0D30632D040D193A9D021039D1607F5E4F9404E60BA06C7C687B0FA1AE46E94C97D47F5E8658A63C19198D4F
	84285B712CB13FD07836188B65E4A8A7BEE34C5F89FFEF977EC7DC5D8AFF046113CA6A7E547D036179FC22C7BEB9343C2026G1EDBG66810EBC33A5427FFBB53BB8BE036391B2F502300A522C2B594DF155D0EE85C8GC81698FDBABC73A867B165E51C2EF9DC2230EE09DB371974236A932158455A9A7833C878D366FA9761754669825EA68A4A29GC479ACA17EE0A9BC0F993ABA7EE3EE3349A5BDE651EEAFF118DCAE1B33232443EBABAF2FF15AE41B49DEA9B9AD7007647498CCCE51AE485D24DF71F4AE6D
	877A003260A6385F8E92DE73203C8CE035029FF301602B606FE6G590D7AFD780FE3DC7E267E8704DD1E4773360651AF5A4D74CAF67B75336FEF4BFA157AE2DCAD3179DA285B61B1666F85508358812281DE27B190277FA7BCAB5A64CA4961101C95AE57893B45DDABD9C4BB7CDDDE8EB59CF7D6B4CBEE08BD6667F7CE97EB48B39C30F6872F389D33C9E2A97ABC696CECA1637E093A9C8D1BE4A4766B26FB18ADD2BCFE1BB05E1DE1F9E7F882E3B21077E7021A37707C211C0D9A3CF7FF67D04EDA5ECF366C67DD
	A593DFF78C91DF6F013FC9BBB210985ECB71D1AABC0B4FBF5218A165C428AB70E2ECB4EDC1D97459D305420337E95AA1EEE1344E65E5B2C4F903E571FEC17ACE76B21EEFAB325CCC71C20F9F4FE4391146A9A96B212E2957184F62EBD2BD992F961FB1FF94C09C409C00E5GEBG04BE0C319F685F3B4C98B3185C72892F4DEE915D82EBBB29762F7094FD325BD4637448A627D9B41A5CB6D31BDD74881CE6DAAD31C1DBC03BC49A7BE660F1CAF40AEE939928503B49A14A225B43E86E2B5DC6E85CA22DED34B9C4
	E060F089586F03355B61E9B7F964CFDD9613AC7AA9D87D02DA62139E1391FA048E60EFE6174586E22FA2503F8C20DA314331BA525F99518D69223CDCAFC9DDDED7A351C4086BA3F63E06660EE678DB6EC39BBF768941E5C0F91D520F276EE5D5BFD5B6B3915F646E43B1669802E4AE64DEB46697836881FCG3197B106FE5904B1D435F916D07C6D891AB19483711CF81145CE6A45603154CFF990B9D7C3DDEEBF6497006AFE1C87DE9CEC627D64DE8C9CDB3E0B5AE32D1863752A7A58B0340C472EA1641125B1EE
	355AEDD435C7DCFE98DAE67F038D4417F650AB8AA0319FFDB03B2E0845748913C744A82EF7C3G0A963FC7382D3EB8CE1F0270AF9EDBEF8110B1C1727A3BG9FG3C824066E5463B92608DGB29F1DD2F40E25FD25029C2B818A87A046GDA8122G66G2CG90AE01BFG8CGBE008400B40035G3B8651C763AE90278F17FB37F239372B64FB156B70865783E95F8DEA7BAF2E63962E8362BF5060D3491F27061165EA813345720836584C638CFD748A37C58E3705BA6636EA449D46195AE65BB4783C4E1FEF7F
	BF5802457674FAA23F58A3BA65AA51A5BAAD700707575DDBCF74647938DDD25A5CC771AE485F1510B0BD8A7E01FA2223C9165D36B62FACB608328C8BA9FF7D231417475B667227FE6475C4BD311B548B79DB21DFDACFF21FD9F2BAC5BA4814A63C7EA91AFBC53B68G21ABA52FD3E678EF9948BA35CB6C63397F3B061784123B6DDEB20F30355F9C9371114BEDF34050A5D31190B3C872F25C69DDAA9B9D879CD943DA60699B441C5A7B8E6654BD976F96D2DBDCBA2DB5C25C0CDBF4AB86D9CEDBB3684F29CC2634
	CF34E5FF1A4E819C6FE1329E59C96645B0CBD19A275B05A5FB082DBCB60757CEE7B8A01341F387317E8E1144ED03CBB45BC48F2FA4A58F1D14687CACA06D644354CFA084B5BB2DBC486B0EB909FC272BC0922963A04B1B1D0DC41702A1E3FE91C05990664D53E7C9BD4EDDBCE1D6CAF65EAF547906084D975279055847GE56B9033B9A9670EA275D6E1074A2E292AFA56BED629B7G3DF5143F1E797403CC35CFFD3CBE811E4923D056779B8A294E5DDE91169D44A6E7147879CF434BC1F5C69ECB2C7E638692CB
	F6559C3F639A42F779973847C967BCD70DE2F3E8B6543F89F8C6B7398569DA2FE1732B8F1ED1GC91CEEBD25F334A2DD6AB52C9F9F27DFF18D531775F75404F4D5DFE17DB4FF054D593E007EB25B104E476BE3601981A0DC4B7ACB3B165055D911CEFFAD1BFFAC57B2FDE4CE57BC1A503DE4C33A9D23599CD5BC1A75FB8C1EA500AF9BC368D6A8F4092319FC2923D9FF9BC6337E18FCF7F4A25DE8E8978F10B2062DBBB60CD15335F4A9FC47824F310C0E742B9F2B265BEEC73A0131EC4E0C9D9B589F53636B8ADD
	9227DBB4167537FCAC5303510DF1A05D9ACE17FD9D235BF11D1A4E68C43A8357B13AEAF8B681782ED35BE50F04F4D11CAE61BAE617546B54FABC215089D1AC8EF6C5B13F6D0DE2F4ACEF7CF69F09FB3A3F6DB47518FCA51EDE4847656527E966E0FBD85D19DCCEDF9D05FB5512BAB21EC7C1B98EE0CE94663DEFEE463DEAECD72210717C411CF0FB71B4AE471AA8FF5EDB373662A516AF12BFA5BC0B2F0738389E475F028675FA394263114CB61A487898F3B52A73A243F99BB741335D667648B5B0F179F0BC76B7
	92F949DA1E23795E66F93A7FA2F94CGFC23GD22FA7783F8BE43EA0E31369E9AEC5BDE3AE4E90DAA7CC52650751B37B86265F1E9B7CFA968F2E5F486C7FE1F3399061FCA1C2C2E672DC7AD8ED77142D17424A534C6598D04933D2EA4CE17ABFA4207EA9D03F6E8654FFBD551F6488A677596ED7356306E5FD28E331531DC9FCC2E4AC3E91E36709AC424382658100D8G467B4FDFA06FA4402D82607389DD97054CDB824EC5D1ADC4B659EA73B0DB7C2662B857B5FD76D4E1DECE34CE6B3C44BAB3CE37BB1A5920
	AA5AEF0BD1D35B32E28118CC0BDBD026D820C90FC619CADA11A71BBFDCFD6A79A37BA698E7GFA00561B300D3115584E4167D1567F445682BE36FCC0B7C721FD5B94CEE726632CB755BA661A46E86A987DFC07EE75CDCC37421B7DBAEE985F16D5980BBADACC28E39350445C0CB2EC49A47401F3BC4A7D4FB614BB8D6805DB304DCEAB114BEE73488D582E042E2AAA2DA6E707E8592B0A16923A24AC512B09CF9254BEE653AFAE29EDDA86BCFCG71GF3EE41B8DCD9779115D18E9DA5FB759AA3AA146C68BEA357
	29572A68847D8BB12853F3D662EB133B63A063F97FA632AF6990395422C503F74D3E4B644E7D79429B684FBDD5530532F5DD1A7E24F608E1FEEC0D717BB3E9739FB72682B0BD5E3321BF67G4DEAC50F09B65403159F311198F3581C0A75B197B219864E21CC23521305DD5BEF8AABD351B8A6CB75B83FCCE9D35A32B226E00CE5F622ED6300E659B814495A79AFBAA63C9E31047EDFE937193BF81C1418FAE59EA435240E850700F35739A3E4DE74D8255ECA2FC7169CA7E151AB39776A43B6A52E663ACD5D2E9E
	BF47063E5054ADEA2BD517FBAB5369402DFE5DE2E17CA40EC75D96F5223D8700A699002DC947172A5724EBG3FE3BC2CFD47236E7B3B8E91FF30C1429E86515DAE399D217AD728C6876AFF57CEB237D9EC9E17649177925D18BCD1DDA80F9EF80D9E0F727C30DBCB1E1471B816DAB63D4D4692DD3AD6485C12F69838A575ED1D90A2C7EBEA889B4E67189D79ACA3BCE2311F7F50FD0B9374B0594F0A9E2FDDE6744FA974470156970BF6B86960F1F908159D0EFCA56F081633D2AF67B75B2144B534DD2D34FF4549
	5B173272074EAD2A76301970BA1CA87310937BA81AG6B2B895847CF300F034CD69BBA55360A813AF9G998A7D960966FF98452C5FDA8963FBAD506C1D08361816CF461868B30BAE6240FA33596B069D0E9243E3697B8532FFBDA9CB35C7DACE86D32A1D50E8F5CB3DE46A77672CDFA9FCDB01E75CC4E52EF1617C444A032EF07389BB6F48B993FC963EEC227F2CED554450F3B7F6C6BC152E8357CFE4E744D98ABE0962F3EF7363193C076B507E3B20EE60B6BC57FC679C4975AB213C81E0C79C2C518174G76B8
	DC7FE5FDCAECB56C39F18DE865F61A6C8D818788F8FE7BA9618BFBFEDEFE78D3E2B7BABBA9F678559031C3D41C76B9239630F6FF266D922E209D4BC37B4E2B5FDDE5EFBB2D4B5248C3549F3C6F15F17EBC44EC377CBC0955225BE12D8C5088E087083E9D6DF642D89C49EEF8064E8D467AADEFA67DA6C1BFE9GAB8156836C1A047DC69BA37A6324CF539F7C5D20B0B2DBBFA3DC292DD93B1ECC522EEC12F6BBBD473772278CCF964B6559741D4C2460982F227838C941B11E1E05B11E88F585F76079EAD6CB24F7
	DD2EBB82E5E36D36C5EC979B540E79AF66825983A6C1DD9A40CA008DGC57190A7G2D7168478C6309C8FE2C40833BC6C97933B278508D65FDEA04FAFEBFE2BBDFFCE0BB16C3BB7B436CE169B6092DA57F512BDECB46443335E48ABC1781303DCAE29F6EF3D74763DE655E817A6E66CE1837818CF722AF63DB03E5355E99A81B360E770434FBB328BC7ACEADDFEE2467BF71D0B78FE099409A00424910A7818C135117A51F9D0DB8A649A2827CE86673C01093793BD15348FC680B5CEE32D63BEFF40C4C1E0BC25A
	15841523A6EB45CC5B464342956F3B6919880B279F552963A9E1B2FBC717864F0C4998CF93AF613B7FF513B11E0E53773845DF055C82507CD50C27CB561169FFE3C83BE07D652FEA455398FACE988DF509G29G99GC202B1FFB74071840C27D11FD5C40AA765B5EA23D48978EAC96A52C88DDC5EA3361145D5E544F62D891A63061EDF7A20AE96A089A08DE08DC06E9498BBD3D04F651FC54C01AABDA367401C4E1179731510F6417921EA0A161E974DC44FE628738144GA481AC83D883D0B895757CC7530711
	746479A132AF170448FAF93EDC91315D0129DAE3F6EBC318B32793DED9266F8A58586C4F8EFEBFC34EEF2B214F6629B8B617141231C9769972D49C03B7F70DCC1F78086D120274E1B23C6AC69996A9B21C300FCC86C344F69B26EA35DBB5C2FE3F0958EE47B42DF6490E1171DB9831DD31A63FB14E11719B9F31DD55B42D98FD6A9299E7DAEB9118C3AA5C2209476809CD416B913297EF06BEDD53B0C6CB3BF0BD92B58D6323C19AD9DEB105348B4EAB291A767B5D8879E539A271DB91440F5DF9F9133EEBD88BF5
	5953E1BFCBF1AF6889EEAF140FCFC7BBA456117D2DC5A94F25E53226E365723A26A0FE178B244FC8B45C97A36691FCBFC58BD83EAEBE977E5DFB69B96DF76F9F1C53FE777E5139ED8C8FFD28EFD6E17D29F3433F1B2FBF2775EE7E8D2A4FF7F4E4DD6376BAE51BC324EC59F890FD64CC89E61533DEA71BF288E7CF703ED340F9EC3E0578F1E0BA4ED7BFBEF7C8881D2F58C99FF652A81A5C756D6D4A24CC16EAA8F3467970767B51F9ED7BBDFFDE5BFE3FBABF3C7D7EF0FEF87B1DE88EAFCBCD3336AC754D5A32FC
	5ABC3CAC971A4349424654CD743DCC9C58B9C599E72BB2F11CADD3F09B3388EE9D14F3E7A02E0E62F6C379408C9CEF4733095F0EA9655334CCF29AABBF1BB55231928E882F40717873AF1D07FA1C32B87F29CC6E79045CB729031521006F234ADB72B80DC1168C250ACFA6DD886F7FA997347DBF7382F9FF6A113CEE33986C635997F6A9F57ED5DA0DACBA08B87C3E8DEDAF1B5C9DE2C80CBCC25B333AF06D97DFA06B13B61B456666B7B2180E592DEA9D49DE0A0D770E1344CE9A73DF0359AD596DE487A5607A27
	25B7177B2EF9866E0F9E7211BC928B65A400C5CABCED6F9FEEBE4E18911833574E40331B37AE117E326FE2E7B48498FEAF45972A706C6C663E813C873E9B6AC681B03FBF79572DF43CF9BC2207AE102BCC32095B62DB0AAD48996EA1453EDBDB427B7B2FAD5A7E5E5D925E5F6F34C476F7F1CBF8FF1736C476773116F0F90369BA4524767B91857F0B8E358DCA15F83823E4F718F8F059EDB28987968B2F57E3AC2C3A8BE34155C3FC31FB26B17FB000FEA646429D17060B854B4CF0E75549F46F6D1B999C8B8FD3
	7C681941317071A046C28C542D1B09E76B4956FC34C13BDBF2D49E64763D25B53C3FA736EA7BBB3EB53C3F2735C676F7D2EBF8FF5F5F9A595FA92D437B5BEE8B5055F088E33BED1858EE8B935BED4344F65BE544F65BB0315DF699315D261D4B7E53290ADD70A676B95A9CDE479B4D5ABA0EB30757B14E9CD947A96670BAA61AA36BF81FF9F8FFE6F62947A9495BEC7C756560DE23A991475F75F9A47EE3201C88101A08636F19D29CFFAB927DE3ED8D7FDB389B76B2GFB6F7657B15E0F8770BEA2603C7D269B7B
	BB26EAE360FFDB606983088E692F4F2E6E6F2002FF5D919833259C7F73A07CE10ECFF186620FF01BDC5C0467F789F723CDCE51F73DAB203C96A0779E34C9D94099C051BD01B96870BD21B909713EC1D2475EA185DF6CD26B76EF85FF648C3927EAB775C9DED990126FA1B2694F60DE329A4A2DGD1G717730B3C916076F6BA5BA363BCD8E88873AC00E391734FD39965BEEA0FD816EG008140F5AF663BBDE5242DBFE3E33EFB4432D99DEB81EB206919E41DE4915BCDDEBBACDDA50FD86F621FDDG3EB00970FE
	B493E3EE8570E2723C205C43EC849AABC094C0A2C0DA924A33BB0B5F23B159153ECA3350D72B95BA115249B6196EAF56A7116F55EE689294995FEDA3F7B6790DA5FA4C6B145982057954522B4465AC30492CC09FDAE6051BE75E6B23734C2C6077C59FD0FC542C6077C58BFC783EA88E6AB64C42F576BF7BF1BD5EBA9BF1E78688EEB41413E6E36C3DFBC9D046D2096A6F87863E6C75309A980FE57F859EC575448F29333577654B03702BE7635AEF95DDBFEC184D6C3DD171432FA83E70EBFEBC73C3CF8F3ECB5F
	83F5D1DFC31B4F3D04FED873B534797CC13F1D9FD37D2D9F3C7A76907F8BBC86F30257D83B66786DDCBCA7DC6C7F194E994767B05B66A8783CCD845F2442B31B5FC269C95985F54B67204DFFC24FE74B6EB366B75E07385A7D84E70532CF414D5E0F3E992D601E53915CF2A82FD1F0D9D60293669A73F74DC55C91B772A8D6F0371EC29E47955C9FCEA18F3D0233D6A00FF88577132348E34EDC0C1321CD432DD5974D8DE7470758BCB3B7B807BCCE712B67866710DA658E52FA28EB3E9FE52A27E70829D0DE2E60
	E638D01F7577635DCB790C326FC9861BA7633CF130B5E07DCEEEA415171FF25BACA7CC9DFABA8B3139B2051E5B11765149B8D77E0C7639884A99G6B12716E2C6F6FF8D7B5692E2942329FEB5F91DE59F8C9D770F5FE776A6B2A7B197B5B32984F32DA64E9077A69DFA73876DA753D9DE20F773359BBB1D69F97D0CF56CA64BE193FFEC9C0BD11F7DC163AFEC3C07DC6D5FB265B52F7D1378DCB6700DC2343FE0B3C066BD4788D2E5B5337E79AEEB73C4466266F503B8A6A4B121F591C96299726A97255236CA929
	2AE9B054B71CEC292FEB2954439F0CFFC22F7ABEC959262FE8FEF7C2201A73ED664F6897EF4FE4F6FDDB07F60D7D865A35DE97EC578581F63BC697EC578F826CF63FAE582ECB836AFF2C8B366B133C1E5963F4963D4F64760ACC4FE7BED76BD9B8492E433DDB301E69DC3F955F7047909B877D747C216081D873BC00E3E3B945D5C1592E60CAFCB8DEC6BD00637AC89F11A7C9A9FFC44B64DEBFABAF7629F3B4217B72F3744AG9E17F326FCA54056E3957D389ECB839DB2GB6G6C1EE74CBFB68F57D9ED832F41
	53640585AC79F6D6E4B76EBC9F43DA1A5F99E6EB344181DC23B54FC35B3ECF777DF6A8C7A938B2835ABBDE416577905C9AA8E7BF08B67E99456D0272818577BB0AA3F72F49378E8CB75073E54C17040F3AFC0017990F89DF8A0FE09F627729838F929BBCF92908179B9F8C0DA7AB47B1FBCD207B39C500DB71A05AF0C79E5AF503E22FACBAAF974DB76617819866A3FEAB4513BB0F64DB9972DD8D57B3CF2DA7E977E5441A0173E7BCE667DD0D384E9170CC0A442C5ECD72324BAD360B6416E5036DA259A330B85D
	FA1846A448631B0FB1FE77A9653EA75DC77C2FC3A071B81F4E497B2BB80F699A76488A02F9C0138E700E4072554A797EF3A746EB28026C379CA61F4D61F5D04918DCB9F40FF378A1D81BBC04F27D79A82535B903E80B69FA478EF4BEG069369FAE7BA1467GB0BDF412DF0FAD5528C746C321FAE486693176A126476B3CAEBB452F8763F79BDDA39582FEF78A4A704AB1022B06F2E38A466544CDCAFECDD1FEB7241358FB19D2DECA4B64FBA87E6E2D73EA2D017DBC6DD70D875AF03B505EC57673ED121B9E7430
	7D7C25AE4C392BC19661E19803G479FE65F03313C1C62C21AA6405B81228112G529FC679AF065C693D1CB132CD089AE13B85D75C0E2DE9D374A4572CF9B878DC76F10ADF7FF070396CDAE52D1A7D08B15F7A885EA17E31C4EC128E6555G4223305687A883E8FC945FA7FE336B0090617ECB2364520BBD22DD75DA0D6977E16D156A4756AA8BF2F0CD18BC7F9EE15E582A305FE6D99F653F51411F7352EF275F60B07DFC5DC43FF9D0379CE0BDC0E12AB1FF8F4031D4546FD5BB3F6BCBDE11D6483249EC25DF43
	A9AFDCEDE22F9FCD8E02D85FF7505CE000FE6CCADF2D767D70B41ABAC35F3A82526016DC22DB6EE3D87A49G0F033F6B2E54CECC06096418121FAD5DE5FFB79CEF5062B42C0B5997890D268EE5AC5D5ACB3E456F8121ECE6131D3DF0BAE1F29BA43B4D5C47F352AC3A3EA35F1CC427E28E7DF9AF61EF155C360B12D38EED0979AC072E8349FD38F4E84B6664133EA22EC34102501CD944F10C0F5BC14E137DAF4258F740EC4D5D510FEB6E838BF04DBD21BFF84DBDAED33DE6DE5C9F3C66A66D7D75FF6C8F594B84
	345FBF903C664676CC5E89833866E665BA7AED3E4556E303A121174C6CB40F5926032E634E69400EA0BF1BCF6A8DB81F78B4EC231E7388C483CD8240AB3C1C3CA0F8DE7CDB8F99B760A608DCB56F729E34DD6A8274293E1B648855DBC77E4DD37B2BA4C642DC6D643E7875A5BC17DC277437EF1065E9D9169CB82EBB8BF1DC6F3EF79A79CD969D7AA0D8576205CC7F26057EBD08BFAE12F2C24FF49F4B417C9E33101D69A642B3E5A13BA734FAA175DF9E52ACDF986EFC79513CA0BB70EFECEE7E9751015F1C2BBF
	2350AFEB5E6C47D7121F57215FAA5235026495943A7777137BBCCE3137C879D0294A86A9057C704D5E770F94979DBA328F5FAD7CFEBF5A7340E3467C37E8FBC120E7A5745F5A0FC2F148733BBD0200767EC1015A5ED55F2D52619AAAA40FBE467367E3FEFB3B6EBC1C477A2C707975D85FF1B9FA30F6FDFD7EF6568B175FAE7E223F5DF527AE5FEE9FD5235D5606319B6B7F5B7DFEFB2D9F70737A7E514B17B1E1505F2E2774F25A31B1BEEB930E713D0B420FF1DCF79F839A43A27F9837AC8A9E6385ED641BB665
	365CC11C73E7A85F343920CD62A2BC1FDA7C9A1155FF0F1B5E5CBEC1266F93F4CDF5C2B4B9511E3FD06E532DDC04E7D43F215FCF6DFA9C7610G550F635D3B9F3A103675F14C472FF5876763E784F5BE55F78F1F0F873B037371378272711C6EF079185F01548B4A5FCBD5FF7F7E4C55BF8F5E2A3F1ABC581C7FCBA97C7B565FC95A6FDBDF10346FC7E53A063F0B15678A7FBBB305AE756F4C3035090B7E7EC89C44C2B2408A00B5G429338D61A6C7A1BC057DD9AEB2E13168E914F6D107FB55D61F50D6A0E7C9B
	E7633A49B8E17391699F7137F593FF49120BA2799D03EEE2A3372D43AAB3D20E1F5ACD7708E2BBC5B3DC02FB0B5AEEECD907F7115C4486B6769BB3360BF48AEEGC2E7879B3B0BFA49BBD8751925EA5EA9B11EBC5BD8D3D921EFE928BCDB2F571F28B85BD2217F2C62DC03B2765F74158E533E3A7EEC4D797A3AC62D9E5818F578F04C6EFE82676D993EACE68F9E4776E16F91E9816B673001BCCB8F9C603CC69910D7639317BB27E13E4EA2431A777BC94F3BA1BA63FBD615498C7A1AC6E5AC7EBE607ECBB470CF
	F8E278F39896432BB30B60198C344BG568194ACB666978114816881AC0BB11EF749432D7D9591E9D83BCDB688EC1627FF1E03FB33D150573C454ADD66DCDC232DDD0C395714CBE87726415E9620A98DE9BF49C35B3A52701DC1E717FA6EA773F938F37544B4E60B7434403596335D8B7B5536ABF89274FF72F2ECF7D79619678E832D81C0860886C8G1887108E3092E09DC0419258138114816881DA81DCG2381621620FDF7776E9B463E7C850B4A3AA8FFD535DA7EA468AFF5497072B31BBE3789ED3AFC4915
	466B78CF85057F3D913FF5A8D82AF59F7C41086D026F07736F8CB45B6D8D62F1CCD5E6E7876DAE92DB062541E78A9245B7AF8DBED3D8AFE15CDA21AEFDA91E217CB4609E7B01F4E3FED57A7076E67E758D054F6FC3C31173FB54907DAD9B484B0DFDAE1E1BEF9AA27B84293D5DA3AA73482DC3058A2E46E310BC74F73F186CEE1FDA76A6105B9EC1F67E4D04C15DEE9434090D500E1D63ED9EA4B2A7826D07D908FB1962D28177CEB64A7C5B4C703679D3E6E45BE46934673EBCDD685C37C527BD776D50054EFD2F
	52FE5DE287D810DDE6E1783768BBBC36BF2BB5B9CD9D2AEF20FE9D721EBC92705F439CE13B325C111D31FD1B665A0C746071709C452FCE8F9E8F9D39B89E56C1DD53D3B89E0CF9EA7EA900DF765465644F9EA3592B2E82DA61E9480FG47G8CG5627B1A7A66B487C9FAEA71E15A41CF2E4C80922DB159DF92C95E37CE5AAB139339863AFD3717D7C2C707177ECC364787BFE03F67C7D20A1B47EFE52209DFFEB9AC2636F578DA4BFF7C06EEFD268FE2F604EA93897E81F8477194DA2DB95FCA67D2DAB022F9689
	C34467B49239FAA92E09638AD438F39C372D116497D059AA39316DDF9B4999D03B4DEEE76557A80D4D8969C63147DB0DC4C61B4B2412FB0F02BB277862736AD07F34D62B7D43E26CC781F7A686A0BEE21E9EBE46589D6D546D1B29ECF69B09927ACB3F8A1FBCBBFD77817D4CFBDAF96FA091DCBA14D7BD2D1C79131F7B65F2F8D5FF7FC53A7AFB2C1D2E2B4903697C97977A006CBD795D18EE7441B38A5D75BE246BD2F2794DBE243BD341459BC2FDBA452076A95B271B7EDEA0A8FB879A368C3F5B37590F473045
	5F8348755BFF156AEFC76E557745582B4A035949110736DB2A58531D0736A377AB78FA4198BEDF0DB5C64ED7379835735584E3E83E1AE4544ED789C6F53EE2327EE9D3286FDF5C2476BD5B639459092F4D121D2495FEE67BBE9D0F6BE15CE5BF03FB35D9F4AF5FC68EEF4822C721FDDC42FB83871E417716B34F8BCA7FB7CAE46D9AF0DED5C9186DBD786F8345848E9DB9320F53E6C8C4AE32CFE53F5BFBF268028E56513F90B64A423613BE17DB7410E32EA2B6774842CB6C8CD496F475E7BCEE61655093310A1B
	3F775D24162A9DBAC1D7A33CE425A1DCA3EC6CA5B16EA399047E40A9345D2A342D2169F3E5CFE9F9560F26E56904ECE8485CD3A3E4939BC3FBB76BA78B5CC3FB49D60327DB98248A313EA315B6DEF1FB6D9621D83730A5506619B3F6DEDBF8CB29D7A764FEAC3C54EE323B2CA650067E480EDE48EF23EA11399BCCDAC8FF5A68BDE87B9EEDFBD4AF684C9ED9F85D0F27C143EA9BDCA63388A4F996133B0B76C0C4AA7CDC48EBF7F734D18660254FBF7E9C58529229FED9AFEC122DA2BF6817051D7E2E8DE46540AB
	0ED2938BAFA6A43CC8955DCACB2BD629CB704F257A272AF1396686146B6A8654140272DBBFC36ABBCB6B4718435E4DEAFA164DF776E703FF6B67953AEECD847C22E7F15DA4E61277D9615E15B5B8CCF63BEC32F7D5B8AD643D5909BED9741F033D26A37DFB1006D7323A37695A39D355667FGD0CB87885A3FB49B99A1GG04E4GGD0CB818294G94G88G88G3A0171B45A3FB49B99A1GG04E4GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81
	GBAGGGD3A1GGGG
**end of data**/
}
}