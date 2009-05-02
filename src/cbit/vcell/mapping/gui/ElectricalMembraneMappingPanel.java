package cbit.vcell.mapping.gui;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import cbit.vcell.model.Model;
import cbit.vcell.model.Structure;
import cbit.vcell.model.Feature;
import cbit.vcell.mapping.*;
/**
 * This type was created in VisualAge.
 */
public class ElectricalMembraneMappingPanel extends javax.swing.JPanel {
	private cbit.vcell.geometry.Geometry ivjGeometry = null;
	private GeometryContext ivjgeometryContext1 = null;
	private javax.swing.JScrollPane ivjJScrollPane1 = null;
	private Component ivjComponent1 = null;
	private javax.swing.DefaultCellEditor ivjDefaultCellEditor1 = null;
	private org.vcell.util.gui.JTableFixed ivjScrollPaneTable1 = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private ElectricalMembraneMappingTableModel ivjElectricalMembraneMappingTableModel1 = null;
	private ElectricalStimulusPanel ivjElectricalStimuliPanel = null;
	private SimulationContext ivjsimulationContext1 = null;
	private javax.swing.JLabel ivjJLabel1 = null;
	private javax.swing.JLabel ivjJLabel2 = null;
	private cbit.vcell.mapping.SimulationContext fieldSimulationContext = null;
	private boolean ivjConnPtoP2Aligning = false;

class IvjEventHandler implements java.awt.event.FocusListener, java.beans.PropertyChangeListener {
		public void focusGained(java.awt.event.FocusEvent e) {};
		public void focusLost(java.awt.event.FocusEvent e) {
			if (e.getSource() == ElectricalMembraneMappingPanel.this.getComponent1()) 
				connEtoC2(e);
		};
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == ElectricalMembraneMappingPanel.this.getgeometryContext1() && (evt.getPropertyName().equals("geometry"))) 
				connEtoM3(evt);
			if (evt.getSource() == ElectricalMembraneMappingPanel.this.getScrollPaneTable1() && (evt.getPropertyName().equals("cellEditor"))) 
				connEtoM7(evt);
			if (evt.getSource() == ElectricalMembraneMappingPanel.this && (evt.getPropertyName().equals("simulationContext"))) 
				connPtoP2SetTarget();
		};
	};

/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public ElectricalMembraneMappingPanel() {
	super();
	initialize();
}


/**
 * Comment
 */
private void component1_FocusLost(java.awt.event.FocusEvent focusEvent) {
	if(getDefaultCellEditor1() != null){
		getDefaultCellEditor1().stopCellEditing();
	}
}


/**
 * connEtoC1:  (ElectricalMembraneMappingPanel.initialize() --> ElectricalMembraneMappingPanel.electricalMembraneMappingPanel_Initialize()V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1() {
	try {
		// user code begin {1}
		// user code end
		this.electricalMembraneMappingPanel_Initialize();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC2:  (Component1.focus.focusLost(java.awt.event.FocusEvent) --> ElectricalMembraneMappingPanel.component1_FocusLost(Ljava.awt.event.FocusEvent;)V)
 * @param arg1 java.awt.event.FocusEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.FocusEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.component1_FocusLost(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM1:  (simulationContext1.this --> geometryContext1.this)
 * @param value cbit.vcell.mapping.SimulationContext
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM1(cbit.vcell.mapping.SimulationContext value) {
	try {
		// user code begin {1}
		// user code end
		if ((getsimulationContext1() != null)) {
			setgeometryContext1(getsimulationContext1().getGeometryContext());
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
 * connEtoM2:  (geometryContext1.this --> Geometry.this)
 * @param value cbit.vcell.mapping.GeometryContext
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM2(cbit.vcell.mapping.GeometryContext value) {
	try {
		// user code begin {1}
		// user code end
		if ((getgeometryContext1() != null)) {
			setGeometry(getgeometryContext1().getGeometry());
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
 * connEtoM3:  (geometryContext1.geometry --> Geometry.this)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM3(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		if ((getgeometryContext1() != null)) {
			setGeometry(getgeometryContext1().getGeometry());
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
 * connEtoM4:  (geometryContext1.this --> StructureMappingTableModel1.geometryContext)
 * @param value cbit.vcell.mapping.GeometryContext
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM4(cbit.vcell.mapping.GeometryContext value) {
	try {
		// user code begin {1}
		// user code end
		getElectricalMembraneMappingTableModel1().setGeometryContext(getgeometryContext1());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoM5:  (simulationContext1.geometryContext --> geometryContext1.this)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM5() {
	try {
		// user code begin {1}
		// user code end
		setgeometryContext1(getsimulationContext1().getGeometryContext());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoM6:  (simulationContext1.this --> ElectricalStimuliPanel.simulationContext)
 * @param value cbit.vcell.mapping.SimulationContext
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM6(cbit.vcell.mapping.SimulationContext value) {
	try {
		// user code begin {1}
		// user code end
		getElectricalStimuliPanel().setSimulationContext(getsimulationContext1());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM7:  (ScrollPaneTable1.cellEditor --> DefaultCellEditor1.this)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM7(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		setDefaultCellEditor1((javax.swing.DefaultCellEditor)getScrollPaneTable1().getCellEditor());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM8:  (DefaultCellEditor1.this --> Component1.this)
 * @param value javax.swing.DefaultCellEditor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM8(javax.swing.DefaultCellEditor value) {
	try {
		// user code begin {1}
		// user code end
		if ((getDefaultCellEditor1() != null)) {
			setComponent1(getDefaultCellEditor1().getComponent());
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
 * connPtoP1SetTarget:  (ScrollPaneTable.model <--> StructureMappingTableModel.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP1SetTarget() {
	/* Set the target from the source */
	try {
		getScrollPaneTable1().setModel(getElectricalMembraneMappingTableModel1());
		getScrollPaneTable1().createDefaultColumnsFromModel();
		// user code begin {1}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connPtoP2SetSource:  (ElectricalMembraneMappingPanel.simulationContext <--> simulationContext1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP2SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP2Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP2Aligning = true;
			if ((getsimulationContext1() != null)) {
				this.setSimulationContext(getsimulationContext1());
			}
			// user code begin {2}
			// user code end
			ivjConnPtoP2Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP2Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connPtoP2SetTarget:  (ElectricalMembraneMappingPanel.simulationContext <--> simulationContext1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP2SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP2Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP2Aligning = true;
			setsimulationContext1(this.getSimulationContext());
			// user code begin {2}
			// user code end
			ivjConnPtoP2Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP2Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * Comment
 */
private void electricalMembraneMappingPanel_Initialize() {
	
	getScrollPaneTable1().setDefaultRenderer(cbit.vcell.parser.ScopedExpression.class,new cbit.vcell.model.gui.ScopedExpressionTableCellRenderer());

	getElectricalMembraneMappingTableModel1().addTableModelListener(
		new javax.swing.event.TableModelListener(){
			public void tableChanged(javax.swing.event.TableModelEvent e){
				//System.out.println((
					//e.getType() == javax.swing.event.TableModelEvent.INSERT?"INSERT":"")+
					//(e.getType() == javax.swing.event.TableModelEvent.UPDATE?"UPDATE":"")+
					//(e.getType() == javax.swing.event.TableModelEvent.DELETE?"DELETE":""));
				//if(e.getType() == javax.swing.event.TableModelEvent.UPDATE){
					cbit.vcell.model.gui.ScopedExpressionTableCellRenderer.formatTableCellSizes(getScrollPaneTable1(),null,null);
				//}
			}
		}
	);
}


/**
 * Return the Component1 property value.
 * @return java.awt.Component
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.Component getComponent1() {
	// user code begin {1}
	// user code end
	return ivjComponent1;
}


/**
 * Return the DefaultCellEditor1 property value.
 * @return javax.swing.DefaultCellEditor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.DefaultCellEditor getDefaultCellEditor1() {
	// user code begin {1}
	// user code end
	return ivjDefaultCellEditor1;
}


/**
 * Return the StructureMappingTableModel1 property value.
 * @return cbit.vcell.mapping.gui.ElectricalMembraneMappingTableModel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private ElectricalMembraneMappingTableModel getElectricalMembraneMappingTableModel1() {
	if (ivjElectricalMembraneMappingTableModel1 == null) {
		try {
			ivjElectricalMembraneMappingTableModel1 = new cbit.vcell.mapping.gui.ElectricalMembraneMappingTableModel();
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjElectricalMembraneMappingTableModel1;
}

/**
 * Return the ElectricalStimuliPanel property value.
 * @return cbit.vcell.mapping.gui.ElectricalStimulusPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private ElectricalStimulusPanel getElectricalStimuliPanel() {
	if (ivjElectricalStimuliPanel == null) {
		try {
			ivjElectricalStimuliPanel = new cbit.vcell.mapping.gui.ElectricalStimulusPanel();
			ivjElectricalStimuliPanel.setName("ElectricalStimuliPanel");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjElectricalStimuliPanel;
}


/**
 * Return the Geometry property value.
 * @return java.lang.Object
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.geometry.Geometry getGeometry() {
	// user code begin {1}
	// user code end
	return ivjGeometry;
}

/**
 * Return the GeometryContext property value.
 * @return cbit.vcell.mapping.GeometryContext
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.mapping.GeometryContext getgeometryContext1() {
	// user code begin {1}
	// user code end
	return ivjgeometryContext1;
}

/**
 * Return the JLabel1 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel1() {
	if (ivjJLabel1 == null) {
		try {
			ivjJLabel1 = new javax.swing.JLabel();
			ivjJLabel1.setName("JLabel1");
			ivjJLabel1.setText("Membrane Potential Options");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel1;
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
			ivjJLabel2.setText("Electrical Stimulus");
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
 * Return the JScrollPane1 property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getJScrollPane1() {
	if (ivjJScrollPane1 == null) {
		try {
			ivjJScrollPane1 = new javax.swing.JScrollPane();
			ivjJScrollPane1.setName("JScrollPane1");
			ivjJScrollPane1.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			ivjJScrollPane1.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			ivjJScrollPane1.setMinimumSize(new java.awt.Dimension(22, 40));
			getJScrollPane1().setViewportView(getScrollPaneTable1());
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
 * Return the ScrollPaneTable1 property value.
 * @return cbit.gui.JTableFixed
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private org.vcell.util.gui.JTableFixed getScrollPaneTable1() {
	if (ivjScrollPaneTable1 == null) {
		try {
			ivjScrollPaneTable1 = new org.vcell.util.gui.JTableFixed();
			ivjScrollPaneTable1.setName("ScrollPaneTable1");
			getJScrollPane1().setColumnHeaderView(ivjScrollPaneTable1.getTableHeader());
			getJScrollPane1().getViewport().setBackingStoreEnabled(true);
			ivjScrollPaneTable1.setBounds(0, 0, 450, 400);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjScrollPaneTable1;
}


/**
 * Gets the simulationContext property (cbit.vcell.mapping.SimulationContext) value.
 * @return The simulationContext property value.
 * @see #setSimulationContext
 */
public cbit.vcell.mapping.SimulationContext getSimulationContext() {
	return fieldSimulationContext;
}


/**
 * Return the simulationContext1 property value.
 * @return cbit.vcell.mapping.SimulationContext
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.mapping.SimulationContext getsimulationContext1() {
	// user code begin {1}
	// user code end
	return ivjsimulationContext1;
}


/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	System.out.println("--------- UNCAUGHT EXCEPTION --------- in cbit.vcell.mapping.StructureMappingPanel");
	exception.printStackTrace(System.out);
}


/**
 * Initializes connections
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	getScrollPaneTable1().addPropertyChangeListener(ivjEventHandler);
	this.addPropertyChangeListener(ivjEventHandler);
	connPtoP1SetTarget();
	connPtoP2SetTarget();
}

/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("ElectricalMembraneMappingPanel");
		setPreferredSize(new java.awt.Dimension(677, 700));
		setLayout(new java.awt.GridBagLayout());
		setSize(528, 532);
		setMinimumSize(new java.awt.Dimension(419, 700));

		java.awt.GridBagConstraints constraintsJScrollPane1 = new java.awt.GridBagConstraints();
		constraintsJScrollPane1.gridx = 0; constraintsJScrollPane1.gridy = 1;
		constraintsJScrollPane1.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJScrollPane1.weightx = 1.0;
		constraintsJScrollPane1.weighty = 0.2;
		constraintsJScrollPane1.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJScrollPane1(), constraintsJScrollPane1);

		java.awt.GridBagConstraints constraintsElectricalStimuliPanel = new java.awt.GridBagConstraints();
		constraintsElectricalStimuliPanel.gridx = 0; constraintsElectricalStimuliPanel.gridy = 3;
		constraintsElectricalStimuliPanel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsElectricalStimuliPanel.weightx = 1.0;
		constraintsElectricalStimuliPanel.weighty = 1.0;
		constraintsElectricalStimuliPanel.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getElectricalStimuliPanel(), constraintsElectricalStimuliPanel);

		java.awt.GridBagConstraints constraintsJLabel1 = new java.awt.GridBagConstraints();
		constraintsJLabel1.gridx = 0; constraintsJLabel1.gridy = 0;
		constraintsJLabel1.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJLabel1(), constraintsJLabel1);

		java.awt.GridBagConstraints constraintsJLabel2 = new java.awt.GridBagConstraints();
		constraintsJLabel2.gridx = 0; constraintsJLabel2.gridy = 2;
		constraintsJLabel2.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJLabel2(), constraintsJLabel2);
		initConnections();
		connEtoC1();
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
		StructureMappingPanel aStructureMappingPanel;
		aStructureMappingPanel = new StructureMappingPanel();
		frame.setContentPane(aStructureMappingPanel);
		frame.setSize(aStructureMappingPanel.getSize());
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
 * Set the Component1 to a new value.
 * @param newValue java.awt.Component
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setComponent1(java.awt.Component newValue) {
	if (ivjComponent1 != newValue) {
		try {
			/* Stop listening for events from the current object */
			if (ivjComponent1 != null) {
				ivjComponent1.removeFocusListener(ivjEventHandler);
			}
			ivjComponent1 = newValue;

			/* Listen for events from the new object */
			if (ivjComponent1 != null) {
				ivjComponent1.addFocusListener(ivjEventHandler);
			}
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
 * Set the DefaultCellEditor1 to a new value.
 * @param newValue javax.swing.DefaultCellEditor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setDefaultCellEditor1(javax.swing.DefaultCellEditor newValue) {
	if (ivjDefaultCellEditor1 != newValue) {
		try {
			ivjDefaultCellEditor1 = newValue;
			connEtoM8(ivjDefaultCellEditor1);
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
 * Set the Geometry to a new value.
 * @param newValue java.lang.Object
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setGeometry(cbit.vcell.geometry.Geometry newValue) {
	if (ivjGeometry != newValue) {
		try {
			ivjGeometry = newValue;
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
 * Set the GeometryContext to a new value.
 * @param newValue cbit.vcell.mapping.GeometryContext
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setgeometryContext1(cbit.vcell.mapping.GeometryContext newValue) {
	if (ivjgeometryContext1 != newValue) {
		try {
			/* Stop listening for events from the current object */
			if (ivjgeometryContext1 != null) {
				ivjgeometryContext1.removePropertyChangeListener(ivjEventHandler);
			}
			ivjgeometryContext1 = newValue;

			/* Listen for events from the new object */
			if (ivjgeometryContext1 != null) {
				ivjgeometryContext1.addPropertyChangeListener(ivjEventHandler);
			}
			connEtoM2(ivjgeometryContext1);
			connEtoM4(ivjgeometryContext1);
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
public void setSimulationContext(cbit.vcell.mapping.SimulationContext simulationContext) {
	SimulationContext oldValue = fieldSimulationContext;
	fieldSimulationContext = simulationContext;
	firePropertyChange("simulationContext", oldValue, simulationContext);

	cbit.vcell.model.gui.ScopedExpressionTableCellRenderer.formatTableCellSizes(getScrollPaneTable1(),null,null);
}


/**
 * Set the simulationContext1 to a new value.
 * @param newValue cbit.vcell.mapping.SimulationContext
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setsimulationContext1(cbit.vcell.mapping.SimulationContext newValue) {
	if (ivjsimulationContext1 != newValue) {
		try {
			cbit.vcell.mapping.SimulationContext oldValue = getsimulationContext1();
			ivjsimulationContext1 = newValue;
			connPtoP2SetSource();
			connEtoM1(ivjsimulationContext1);
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
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GE2FBB0B6GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135BCFFD8D4571526556AFEA52D5D0F26A6B52D1B1806EDC857EE68565D52FEF657BF5256EDADA1BFF60B5F1A2FB6E1931325DB1B4F5D257D9E83C6A6C1A5D19B13B5892636819D098CA88803721BC187C198E1C01099E69E4C407CF26671C3855DF36E0F775E4C3CE114EF43779D5F5CF34F39771CF34FBD775C7B6ED3B870611ADDF93A83021037CBE0FF55F902E0BBAF883BFF537DFB0E2B79B8C3AF28
	7E0EG3CA5FCBFD8GCFB3542D8FE6680B05F75FA8047290141382997AF76077FE617C50D1B37C0853CFEE3BA09CDD16F26F427D3CF90177331B145F29A80267DEGBB6070197AB1767FC945A106DF4A709C72F40210DC0AB467AB8EB3DC8E5094822E9B40D5CA696760C985D8D6DB4368063E14A26C78F1C837875BD13549A10B718AE574D9A27CF7DECA997695A6F2A2CF8D1403G28FC3EB0B5DB87CFD3776478D96728C7AA1FF60ADEEF394F918CFA7C636563D31E5A5AD62F6814C29E27435BA97AC6C38E3F
	58C92B3B6127D7B8FA8C5B7FDEC5A1E9CF9036C35B6B882E278C7BF4436FA5G2B0EF17C3F99903F83FE979D4F509FBD4E752C7F584474305D7C2E10E974956C0B232769B8554FF5DC51B3EBF20B6EDDB2A697ACE87BB5D0379160B900DC0043068C3D8960FF09AF74DCB8864F46D129B96073857C0D41E01357956A8C38C4AF7C2E2D059A066B911D01108BFC1034FD3D4220A14FC2C079165FB59F35C9CAA59F7B541EEF89D9CF5C275B2DE1132C141BBA2B015A42EBD0ECC27B5E90376FA4C324EFC6765DAF28
	7B1E7D1B2773F7E9741D3C7A697CB52C1DB4D55F39869C6BE7088FE442EF644371247897883E61040227FEEAA3F8AC3B21EE5B896E9B363DDC96CB41C3C2F145D7B46D306A9F13F476F2AA43ECF934ACBFBB01ED2FAD27FD9E11E549A6789C951E4A72AB6227D8B6FD1221B71F4C50D37F32DE44F9EB2D00F9843082E08D408600AC001C8A6EE3DF3D70499D7AD837A3A4B5CDF93CAEB1A4D0DE2B658F7094E7251023439F169CFE27586788F99C23DEB1ACB01AE18B5AE0B402EF14783E937AE8977DE248A1F982
	FEE85D6193A5B19426B413163710A6A412DA2B47A7C2873E20405B8DDB76239FB9425265204BA1098A852D3FE941B119F674C10B50G7C26F639278B6DD5EC4C5017825498399DDEA36DDD90C390AEEAEBAD014064D4500A1A88AB0CE867E7C96C3040EF17115BF83E9BF11B203CCDEE67C4675B2AF6DABCCE945F913A4E67D8D6990E4BCE20BFDA1921EFG3082AC85D88B10DE49FD69D59B772516BD0F8B06273F2C69CB058FBE1D1FD5C9FDE8C7E5741C5ACE7AC2F98FD7C1DC8198821883D88D10DE4557072B
	E5E857252CFFEC63E825B26756325F56CA6DF908BA20BEF793B71418B3CF7AFE134DA7446FD2516CD14D75F8ED645FE53FFBE5DFF879927AC29658E1A74021D3FC8C5D1DA5F4CEB4B942A21F85DDA1F0E051250CA8157F69938DC88B0073288260C3GDC2F4AG0E8334828CG607AFE0E750D7D3C8FD04966AEED6BD55256C91043863084A085E0B340CE001253B03F8102G2BGB6GE48394D7039F835881D6D473B1FD59004EF2B7B6F9A7CA368F19ECEA9A2E53DE153E15D1B4F56CF90E59E5AF33411D48B0
	300831CC6CE365A76E56968723EC215730857A265AE7F7B31B29E95ED459167B69DBF7A8437E7FE7DBD05F3EFA91E59627C53F54A286C53F8BFE04D91DF89175E471FCACA073CC92FC9062FFB3845C300C3FF691F5F1C810F80DCEC9E22FA8C910F1A97557C9DF6129512032F4703E16DCC41B85E6A07E4B744BAEE26CF4867CFE11CCB2190555AFA731DB740ABE903AB9B06517A87E5EAE4CF7A7456BEC6D78D2576B8246FE6F942EC3B407DC6740318806BCBE183A3814C11209F17DD5B987C6D8877387F361CC
	F8E6D773D8BC73C79E0B0F5E78EBE143C8D02715E32CFA640BBAF38D0D03C3B5CAAC26B2FD26DBDB7695E48DE178F0BE417D47798F6019A02595A8EDD5CDB1BC4745GAC1E216B4D813F8463D7C16BB29D58F6BB2B233857060EAE54B6D972486A4F5A30DC41B1E991479CD3DE2999726ED617C78A04AAF87F9F9CFC831E10BC8483FE50C0467F3C8479429E5F14172C60DCB29EFB0F7622BD6640CEAB81D2EAF86CAD380475FC7DE4C137B96075D217003A22B3B8EE270E9DE0F65FG3C4F5550F15BAB57BFA657
	DB216CBD2354E7137AA347F9BD426AB3517C1DF27D49DA083B3551757FA4574FC25D4A187AB386DE1F86F51BD475D44FDFDF42F18B0B5726C4C81B506A97E47F5DF9696D283ABE664B34DE68C1DF762AF21402BA6C333C1C6F5532EA691A3F23162E7B78C47CCAC27783F9CF578DE5DCEF5CF514FE8E1E6500276D4D986576D475316D55D7F23A9DF5F45D52EE6F57B2DD1189729FG330966A3F693ED0F527D300A5385D5752BD5F4345F6527B85D8A93DDA331DF24DFE3227DEEB3A1DD63E9CE375EC4F3A0BB6B
	FFB34087F23FC5551C6EE4BD155FDCCF69ED7534DF5A5E9619CE2A27631AD4CF732954FAF5FB0F56F03AF4D57DE176D474701D61F41975F4DDC7BD10FEFB3D5AFE25351C2E702CD22FEE0F523DAA53151CD57A2D015FC3E7199EE711AE3D0E534D1D25FAA433E7EA845D34C9364BB9BA8E66F314EE49B9CAC7E36CEF4AB01612FD7A04E35AB1DB9E1E0110D2DBFB1EC4AC3A873799B1A6E1DC5DF00E6F35FFF193E341CEA897B7C0DE5E40636E4F77703DF672E40AE0DD17199F6FCC2121016D359B1438DB1AB413
	CFE30D5F0AFD2E053A34869EE7EE7628737D46F0B8607410C045665831DEF5DC2638CFFAB1560DF9C2E12983964ED9BE9FCF75223C3897E1E836B79BBAD6A450F878A474ABB472730CCED7299077BC233636BDB218D3DBB8AB39ADD65EF8C430FF79815D1BF1ECD153C8EDE0EBD4EC31D3FFAA1F4A14A14B34926A1F14E57AE641C79AB271F5C5910A0FF5135490DF0784E7B24AA2C3F51BEFD30FF37A3E79383A89CDCC27A6C5371A245718EE52F1FEFEE4077AD5CDDC3763CDFF54744B18D50D0D5BCBF2BB1B21
	0D42E65E4E0BF3917EDD6E1811EAEB65250F725A8D5C3E8D40B78B302E99718F9AB0C741B8C1697A4BB95D8E28B734E468693A39348269A437A7CCED7378878DCC77CD5FFAC8A8F4AC53E91DB7A57D7B384E55C2ED125422324DD3DBF496G5A670B953C4FCC20A9EA65FA9DB80BCF12AF89FC7D3613F94A471F4AB2B740E5D932A9CDB8347FDECDD990DADA59B9CF2BA2CB4173DBF4071E67322CB1F2D95201E6C7AB374BD8A5479B5AE02F5D46716FD59531B8339AF019GB4FF7BFA33BAFFABEA4F50D78158G
	3C6D5C2677D4613CF5AA434CF4FC2C0A5B1A0DFB956FFBA9702637733E571D12478AF086B3EF376CB45A45499DC01E779327B93D8BE8D74B743FBD0D727B507185FEFE751F0D6A71DD3FBBC953266EC7D769B24D541639E6452669E04F4D32CDCFD5F39BD9BAA0BEF770FED7D7F3BDE9F9E375BEA1A6E7E4323FDEA30F8B706F105BF8420039FECCBEA77B4B17EAA2ED39D9EE47F09E4671BC3765FDE7B8FEB560B24F73767755461769D2AD572B68C2063E6582EF6B1DBA0E1F83DC5A856E939D6DEA1F58FE011E
	5998AC997A868B6F6F213AF87A30F3B539ED9B702C1479ACA644FB7C9E496370FAEE0825E5F48E4DDC4273CBF1411CFD24C34556C7477F217D6A71B75D3E2AB97EAB7639F5CFDA683867D814715F1C31C537AD030F7F57CDDCE6D3E706DE6A64B23FDE0F6748E38167D4381C7CEB890425D29E424A49E6303C8DABDA71E7990F710F7598557EDDB152A6B3DB62F257157571FD7730111F6926F6D219B7F4AA67C41970BBDBD5A63ECD4EBA3B66D00F5CCEFA56291771C384FF68220227FABFCF7287ACD7C15D2A0B
	7CFC6EAF7A518FF221FC288B72DCGB3009BE0C5974F833E5F0B67978B1EFFF600E7047C8EEFEF44C6161FC3DA51463077E465992BBA16D2BB0C9EC3BB2C6D3A73B3B24A97A4FC69F741C775FE5506FB0F6D40D7581D21AF81288130F5F31DF32D06C4BA73F3DC26AC156739C56A7142A275308E229E33A0F7B2C0AA40C600ED329EED17930EDD6B2C6658D139EE9CDF5CFB12950B644BD9A45F6F9749B7E6D85CF8C9063B9DAF9A4B4A86556FG8B5EBA2F433921159F94DC22ED9F3894FDE6BDDC1EC8D76B25C8
	39A85FE4C23E59A8BE6AE32FDD413D4DCA28CB8558883095202087668DC0CB8F7735644BF589FD8D431FB55068E46BC964A996137315046F5834757B1F04FC2E9EAD7D2607D13FB928DB85308EE093C08EC0D1AF4444DE2E5F8B171B9269A73F22819D7919DF2C1ACC5EEF989727E7DAC2BEF32F961F37F2F17D5DC848E7576C2F24AA1AEFB60ACE3B3F72983E8F224A49D16D303DF578BDC13D5FA2BB2C73E4A755661995DD82BB4F3CEFCE1D7B2476525C67B9F8EE8720FB70E71B79FB7082AB5F035F5706EB28
	894AD6G2F153FC7480911774E6C7A7B04FCCB2DDAFCFFF7EAF17DEDCC4837DA333F60694575F7ABA1DF1AD5EBBE3EFC956763A6284B8690AEE7688F83B4G8C81AC394C67E3EDDF4279A8CF42BB190FF5550B73570E983EE8FFDDF5D90B2F28E6F1F6FDAFA15FBA4D7E1ED9E4FF5B9272BD29595F6AB30B6B6F1B897936EA7657DF3B387193E3782247EF27E6FF6F57AD2E3F63B1FC517D9577E94537AF76D789F7FC1E54AB613EEDAA2CBAD73C271D473452BE9E5374E4BFE40772AC404ABE795D68A2F57B2F98
	3E7228F2CA1F561C375BF14EEF003AAC009DG457D997AAA00EEG291F4F79FB7B6A05BB4BB11A81E78E84A6B526BB1375915362F4FCA206AFE6FD6A0F642377A3A4F2DE148AF5CF82D05C72F944E5C1B9C74639EEA36E008D7663B60EDBA6A02E9B4AEE9B9F234BF378FEE48E4A49G6B81B649F523248E4FF970CCFBAB600581DE7762CDDB027995210B0D6A325E2668047D4C59A2F50C349108F597FD6CD271F21CDE08FFEF017A42DF7647FFCFFBFF3F76FB5A2F77EB3F27FD247FAD0A07B65437F8F87D637D
	8B3F477DCE3F56FB5C77098E4F68F00E05267CB0A5C552AD1D4F626C916A4BBDD3FE6A4E58F3B8FAFFA977711AAD3E3E3F33E96B1BBF202D2FFEE0E1FD5F9AD8D85F21417832B88735E5718CEA4B9298DCD8161941F832505831460167EAA583BCC66C3DE294E2E384BD576446350A0ED05758181C947486C52740466BC5896FB6443CE33F88193D404F72124B750C26D58AF49AE5D9052178F659BD24ED17BDC3784EA69C188AB945E85D8F8C9D126B14C856A109BE9407523CCF78A5C7E8DC0C315DD142CF6B62
	711F980AE7DF2A6BD1631BCC576E31D04057ED64779306636B3AFCD8DB57FB07636B1ABC1CD8578707636B3AF6B8312E0F8F23AE239E17A7445E16D39D37985563D9A96B7EF0E5246E154CC7FE7407FD44E743E1111E6935B8A48763692FD237D5AD7075FB3C95F9B55E7D75BAC3812F97D7EA2827EB75CF6778BBC053GDF2BDFEA42B8A2C1B989E02D9C4F1BDA974A175287E263B4BDABFC239D5B5BBCC04F8A73E47CFE024FD2616999626AB67E4EA79B6AAC037C6C7463D3EAFDEBE45B3DF2AA52F6558C7F68
	E9CE5FAD853AAB64717B6D55787E14FBD55B1FF4A3717DC9BF12581F5E9E096FCF6F0EA476270FC6347DA9D42D36C915EC13669A356EC6993F670C1A3EC246BFF5A65206958C7F05DAB57DA9195EDA27461F9678FA7C397605FCE45DE0BC9F19A779614641E89F799C41EF9E0C7611FDA66EA3D9D057F0057B48C7A615DC4D4A18CF58630F79B53B76184F58630F79ADFB62B1DFEE0FBF66DF30A79E732F58970E17D9913AAAE37CF9A3769B8C09E3E2A8A43AFABDB7F03EE7DDC13B2DA53961DA783DCE36595766
	710E1657F1BDB0A589C25290191B377859C58914EBG6CG5290BD4320F1665BB368B3E3A1078F44A28B1BFB98F9EF5E663C19C01F8DD08878D2G73307C9E0C5C4783C658BD780804D43E09BC14EF9D50254B3443B7F02CD5510B5D072A2F42BB4F51DB91CF441D2CC70F20DF1E37B8C6C5AF776909F685E7E4F71052CD6ABBC85943D44F222BA0BF291FB3EA5517DC653940DA479E75D831B51C4E092766781C2882FA735548B9B08465E0940E7A7E55E654FB69D56A7B79B25ECD7049AABC1D93B9CD7CFD5EDA
	28AB9C61B97DC0AB4F733DA3BC1697378B32FC6F2AFEBF54F6B779745D826D63FB1FEA9FD467875B394EABE51DBFEBD274CCD27DBEE87A74F5AE7FD47B58BA025F9EADF78B82777B397992F87A451992796DE08B6C8A7CF8813F74F4F53521247A6449B27A2EE04D0C7ADDC14D9B1F513CD309F054CE7D3406BDF38FBE403E972A3B4563F1906AD26CBCC6D4107DF654BD0D761047556498371008C373104F5EAAE47CDB6DBC8F79B6E97310A3C3FF92406C60F711E67F4C6F2824FE63A1411C2AFD97E92BF5DEE7
	F7D0B917BA14F79BB913DBF4344F3F3A4D7B5CG75BB9D641B0F5B6AF90E36ACAB20E78B34BECFD057E3FC46B5C6292F0C28A7F715A3784508FA34D1BD63273A7D64CFDC375C1F2C933C5FD99A775B2D06D1F64F0ABDD7EE3BBFEF5BAFEE64D3DFF8F4C271059F5CDC40972AAA4ED0DFE0E75BE3F16E0D11E8C96D769BD3A113FD75A8BF47FE4248F11923BC6E3CC4F24BA2A70C1F13631A49B9C48B143DB26EEB73FC2EAE11F13F9BC25CE6A8E7BB797CF5B5637EABD7AE7BC9996F8E5272EBCDB6D67F694D63FD
	4DB6E1F15FB95C895075B3584A574FC32E8C3D8140826085C8825861C21A347696BE87DC317A66894A7798889B00A69360A0ABE73B14FC0A7A5D4F2E28E3C07183DE9D2F0FEE5BA4527E4C22B227687E787EC99C33B9402F92793A38490D5FA874B9A6E2F747006C8E8502E2C83ACE3164E6AD33F3632D453DE76DDAA45F0A3B6623EB711FEEE30CDAAFC6672D65843FD10C4EDB3FAB47494DD0D7B5466FC77C580BE33E8A4AE9G1BG3681940CC30EB14EEDF950F3D2C8F0A6E98D84AD1064FAD5479B6C9D606D
	45FDCF78435DBC9E257D43B705751FEB09FB9F30EA1CFD1B441E253F3CBF8F1FD43FE1BF6A37946A56GEC8448824881A8F4F37D06BD6CAE843AC323A4B91CEEF263D3BE287208B38A9A93CE2A5B4E18F3D20443EEAA47C9F76C7C20FC6BB55FEF36B0FA1B869F0D27F973788D49B4B8AE49BAC9DA5A6488F5873C9E67F516479EA6B18D3F51713AF99EBBB80F36F087C21E9B813F944B49F374E9420B6BC22A1B4F776F4C73791E23A1974F83B994F8B27407BC4AFCB7F894BF24329C98564E3F8D4358F78D5037
	F8385CFF3E0EE73CCA7ACD5EF4D65038FFDFAB0F7BC18FDFC3FF5A9A3D069E49D72F01C2DB749A0AF2A975463668B5F4D9843F2BADFA8D657CD426DF35F319D2C0268F098E82B95726FFB96734F0F45DB463A7F04C771FAF623EFF7831070516E7DBF4FF0C637BDBBD4C57A61418186CAFE1F71ECD5730B56A63AFCF7CC908938F41434F43125BB5BA819B9A2BE3DC66795AA436A96FADE81864674913E88B798E9C39BF578B047EF1768E6836A07BFB156A7777EFFD7AB96FB31FEA9FD47F273C7177757F6A555E
	57EF75EA1F8F3F60DD78AC7AD5EF7CEF28FE6BD5FFC3C5473DA304E363861FD88A1082108E30F902473B4F7A701EB7FDFB9C9D6BDADD63F82E00DF72CA3C7FB1FFFCDDFD7E445F7FCE7B716E6DE8C01282BEEC1F63677CB8DED2A0C81094F70F9FED94720C3BA5CA4A4FDB7DB8E73C6298C153DCEDDFBD623603DE3993BCFF136AF9CED7B849F1BFB3F2BA1B0CBBD94969DC13BC5ED44E63B916AA7DACD76D7D4B7BDAFB2C9D4D0D16115E661EAE0B252931E72431F764E2EBEBCBEB8B653FF77E74827C662E1E0E
	412E0BD66D96F88CF9E11E4710344944B164017B716FC7AC06E4214B30BD599BD3781B4A754FCD787EAECE9EEEB49ACA2ACE16713973550833566DA00360DDB82F25FCAFC51C3F9DG1E5284FC54D7F7CE617AE282DA9B409CC0B2C08A408600CC00EC00DC0083BE48C9819AG2CGEE00D900A400B5BE797DE990573EF8F99F5BG92878FB9BC643EB6653B940A6F6336D0E29FF7044837A2601B566BC1F60EB69642732D4058D8D8146752E428D846F5043B83E18FC6CE365E55137B5350919D434C999CC33A8E
	FF3E1E2F43E9BE6DF5D83D96BF89B4888799BE4B272C43346D226BCA5B6D153C6D6CB8ED6FDA206DDCD55B6C5D7A3C5A978A7D997A437E05FD014E493FA5EB7AC92045F517623ECBF0E6409D11F1F9E6CE97A86038574D1CEEFFBE37F551F47CB15DBF1DF8CC8FCFEB472D0F26E3635647535AF16B4434BAEED11CF19A6DB98E3EE813692AE55C000CB311B691576FF1C9EE995FC464C23CD9448EB95ECC641AA1B89B43F52AF0838C57B30DFE89AA3B71BB880A6B1FC65F9B73F83D34FC0550F87C602632BD6CC4
	C6CF502112DB14F1A8B70DE16975BC06397CDAB10C7BE05A8DC166192D14777640C36BDF39A9483E213B4547BCCF466D3E4547DC71D1471FD5F7730959A87E706C9E854F4D467C750775EAFF4D0476329378AB7BFF8BA26E8D6682CFF160CE7C7C397AD8DD36542BF52138644AD83A7BAB55F4747F8B2A8AFC880554D704DD611070F6EC0CEF7C423FBC1BBA52F2C0A768BA04575DC4798EE1578CDAE79672CEDDBA40A361EB42AD34B716729E06167649ADF590B758BADDD91B7F4B0773F5C281B4C327C507D000
	3E8D2D05E82B79B0AD30CD21C08D246DFCCDAC7E6127F0FF100477788793A55D818966AA0CD4C9D79063E3D89276AB676DA57C6E4F824D46E0797D645B6A73061D4B1CF14F9CF303F4AC0B2FC51FB724DDA37F8F88605D5764EF162F1D906267573DBE07578BBB2F49C63F8BF36D266B122864F7FFFF8D5B8FF39AD6C96BB6DCC339A7D4BC7F87D0CB87889976A9691598GG00CAGGD0CB818294G94G88G88GE2FBB0B69976A9691598GG00CAGG8CGGGGGGGGGGGGGGGGGE2F5E9
	ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG4F98GGGG
**end of data**/
}
}