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
import cbit.vcell.modelapp.GeometryContext;
import cbit.vcell.modelapp.SimulationContext;
import cbit.vcell.mapping.*;
import cbit.vcell.parser.gui.ScopedExpressionTableCellRenderer;
/**
 * This type was created in VisualAge.
 */
public class ElectricalMembraneMappingPanel extends javax.swing.JPanel {
	private cbit.vcell.geometry.Geometry ivjGeometry = null;
	private GeometryContext ivjgeometryContext1 = null;
	private javax.swing.JScrollPane ivjJScrollPane1 = null;
	private Component ivjComponent1 = null;
	private javax.swing.DefaultCellEditor ivjDefaultCellEditor1 = null;
	private cbit.gui.JTableFixed ivjScrollPaneTable1 = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private ElectricalMembraneMappingTableModel ivjElectricalMembraneMappingTableModel1 = null;
	private ElectricalStimulusPanel ivjElectricalStimuliPanel = null;
	private SimulationContext ivjsimulationContext1 = null;
	private javax.swing.JLabel ivjJLabel1 = null;
	private javax.swing.JLabel ivjJLabel2 = null;
	private cbit.vcell.modelapp.SimulationContext fieldSimulationContext = null;
	private boolean ivjConnPtoP2Aligning = false;

class IvjEventHandler implements java.awt.event.FocusListener, java.beans.PropertyChangeListener {
		public void focusGained(java.awt.event.FocusEvent e) {};
		public void focusLost(java.awt.event.FocusEvent e) {
			if (e.getSource() == ElectricalMembraneMappingPanel.this.getComponent1()) 
				connEtoM9(e);
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
 * connEtoM1:  (simulationContext1.this --> geometryContext1.this)
 * @param value cbit.vcell.mapping.SimulationContext
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM1(cbit.vcell.modelapp.SimulationContext value) {
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
 * connEtoM10:  (ElectricalMembraneMappingPanel.initialize() --> ScrollPaneTable1.setDefaultRenderer(Ljava.lang.Class;Ljavax.swing.table.TableCellRenderer;)V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM10() {
	try {
		// user code begin {1}
		// user code end
		getScrollPaneTable1().setDefaultRenderer(cbit.vcell.parser.Expression.class, new ScopedExpressionTableCellRenderer());
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
private void connEtoM2(cbit.vcell.modelapp.GeometryContext value) {
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
private void connEtoM4(cbit.vcell.modelapp.GeometryContext value) {
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
private void connEtoM6(cbit.vcell.modelapp.SimulationContext value) {
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
 * connEtoM9:  (Component1.focus.focusLost(java.awt.event.FocusEvent) --> DefaultCellEditor1.stopCellEditing()Z)
 * @return boolean
 * @param arg1 java.awt.event.FocusEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private boolean connEtoM9(java.awt.event.FocusEvent arg1) {
	boolean connEtoM9Result = false;
	try {
		// user code begin {1}
		// user code end
		connEtoM9Result = getDefaultCellEditor1().stopCellEditing();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
	return connEtoM9Result;
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
private cbit.vcell.modelapp.GeometryContext getgeometryContext1() {
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
private cbit.gui.JTableFixed getScrollPaneTable1() {
	if (ivjScrollPaneTable1 == null) {
		try {
			ivjScrollPaneTable1 = new cbit.gui.JTableFixed();
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
		connEtoM10();
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
private void setgeometryContext1(cbit.vcell.modelapp.GeometryContext newValue) {
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
	D0CB838494G88G88G630171B4GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E13DBC8BF4D455358809E22592B5A2E050A0C122868C98EA2AF4C97BD297EA1645AE5A723A5216DA3A0ADDD8E8CDDB1E257566432FC1C0091AD644D051024DCB8964950CE9A510C882839912A10410AEF2E7664EA7199FF76E6483815F5E675CFD67E666CE86E6BDDE565A5CB97B6CFD4E591F334FBE671E0BD07EFE7A3A2262F2C1A8DAA728FF870B84E131C990023FF8661B04CB71F514893A3FDDGEF88
	2F141442B39D6A46BF51D136CD7801EFA314F3205C7D550E32F76077F661C5DF5AF17891239F856A1AA786022377E3E926FEB630F2C1F0B3BC37G64828E1F25FE6C3FB0D821628BD4BCC1D131A02CE4B4DB159DAA2E994ABDG19G8B7C1CFE89BCB7311E136B8E29F45DF7E48A054F4A452FE3BB3AB689EA831C37D3FDD68A2F968997312FC34AE615A7856A96812070A5C2E870D3F8561B7B1C7F32D95DCAF53FCD72F82A3DE2A06076B92B1DA1F7DDDD13C732A9325BA6FACECADE2BAC7A24133C5A8CBFBD42
	B0EBFF0C321135A7880DD05EA8A36EA7A27639904ACB81D6EB780004F8937C36838C4BA467BF7FDA2F4A51F979F1E139731B1B364610B3D56672AD1043F2D6FEECAD799333C9EA8B6AFE8D546D88F6146D8330GA8G29G6FB2DF383779E3F8B6D81523FE2F576FEB88840EF86C72C93FDD72406F3ABA28D1F1ED124DAF5B85015B7BDFCA1541F8C6834EE73AEEBE2E134C83E47B2C36B9C241E3130AB7986824A073F2F1D6106BA2AF9856896FBBB9E65F4B03A3FDF3E45FE785FD5F42CB7BB72CB368FB47F777
	EFD8233633D98977EDD15056CEAB5A3A8AFEA39F5A13638798FE328E4F7D74A566B3D8DE88F575A1720D4EADB496D3698342361AFB8C75B0799B930AF3C3FC8C0BC311E369F1E05BEBC33C4FDD5AD806983E39BF0C67E369E7FE0A65D428CB9F68A8637EB56D844E5B2CC118C7G4BG56GD48CF5145582B48F110FADEF7E7BB57A18D91415A3A13747AE4982671D5672B6BC25C1C596DBFCC1C57459248ED1F60BD60F84910A53BC5802BA300E60FB04793E8D7AB8A679A4D9D45CFE9F34AEFAA5C512031CA627
	658D2411A5D6EBF1FBA568409B90285D85AD5B616991034A1900DDD424B0852F4FEBC11B740B9D50A2B4G3F39DE1EB5213EDCA07F980094CD8F1F9D4776CEC8B2040B3ABA135F5F978AD8D092E159906AB90D450E8C783DE008F47C74C9446D3E84BEF0095A69BF7E262E1DC6378D07AF4AC3B4473CD6340B99680781A683E48194G2C85A83FCC3E740BCE72254657E78BD55F395B5017D2402FEBAFF39FB2DD0E1CD3D7D8DFB8DE856AD281F2G16G9482148F537A30CAC43D96287DE39BB8B66AE32D7ABB6B
	1271BCC4994CB16A62471CADD66C3BCC1DCF08DF2723F9DDB757E335D1F21D7DEE51FC213695FD219674E086888C138D679E2F64F362089814E896344A604012BDECD1BEFE0763B0528260BC2A81F89FG572B3DGFF8378EF00A600F3G3846FF26760FFD9DGF8CF5345972C2D71B00EEC0025G42150E32BDGE600C100F400C5G2B81AA2EF6149D86F08124G6482AC3BCAB69D1650D92EC7A76F6846751EAAC7A40DDEA61277C0844D276A73B3D5AFDBD4792FE58C679230E5FC9FFBAAA1DD109EF64570BF
	744D2D2CFD6E339B8CF4D62673B772536D57B806377E0FF541FDFB56899C3354AF7914C6A9A0796C70A328564DB901F22A715C6157F8F298BEG717FA8845C2006FF62844AAEAA10F8D9C30A54AEA98AE4DC617AEF303E02A1EBA03CF4D0DF4F1CC01D7987A07EEB744F1F4058E9737BFC921BE49A0BDA3F1845EE49A3F9E150C77DA11F42715FB5E13E5BA78D29EB4777CD6B850C7D1E902EC3BC074C96519681596D05290BCB19507985467547148A1D0EB6828EF361AAF89E7E02E2714087940BF7DFFAC8D8F8
	A1D0EC14E3CC1EB931B87D8B9E87F33E8847E2BE2667CE9A0F7D8536062878E089432DBA3E931EF1D2DA0153569E5A86CF27640745D39EBA6A77A9E03F9ADE37C444360F29F59C771B6E5D23354D16BC367A2BED184E23CD9AA5079872A8C7A16FEE323B953FDCC37D6FFC6B0F700464A1607701849A7E05CA648B3A3DA18FDB41E9E494FB3DE654479250538AGE18C455E1327301E56CFB5689E75FBBC5CA5204EBE866D763435DC55FB8514770D61F65B2255575B28BE8B4AF93A7A43B7E17DBC2D9E21B00A7F
	07DA7DF820CF3FA9325EE2277A45D037A22A7ED92DFE73580E325DE34375DC4E3AD634DBD03A9812A0EDC22D1F507C3721754D083A8E5517F9FDCBAB7A32C71723F40F45BE1FF250DE2DF6ACDF73CDE3793A0FCF442FE0F42D3DC41789E5DCEF962A74CB60D98DF85E5E46BE2D3DF1617A68761E5768CC63783AE55C5ED88F515901EE98A0FD9C4FC7F2477176B8DD1B176896696A8BF5F43C5F8A9F51AD9B4757C86C9769570C53777BED0DEEDB92EFE7CF92276BC9D2F31BA424CB75935D18A41EAB652A631C8C
	651DDAFB9782C437A6094B39AD1953D7A66B5B5BF711686A13397DAD49BC6FD212756D3D2A51A5696A31DDA5D9AF6FC219685212795A0F72A2FDCE32DE5E4BC122DB1C9C2E573747699A343D7D0AE41E578AB7F3F9AB6099G60F42B7A35715D9C9EDF9A7CDEF8B3EF0F4758D776E2ACE57B74DE31DF9C2C8E8EC0C8292BBB4EA2965F033F97421804F1F5594D34577E925B5BD45C8273982079960A3BAF3CCEFB6D543ECC2160251A8D314E94FAEE6163C83AA59CF7F3D6B52C673166715358E79E54AD390562CC
	DE2BBE5FEF88867DB6B78BD46A9C8B1A75F11963864D986B9CEEB928344042B9C873712A99470BFB91952D6E4DDEE67BC70C07AB205F4A71F41EF1523EC708F91ED1D7F7ECE4B0673A309DA0DD24DC1AA9745CBD3538AC06AEBA47F39D8C0E8F6B226A6A1AF5FCCCB69B0DA99B6AD7E8E3FA3874AF86E322F5A5BCA832759165F0EC99621C49049799AE5B154F75F6DE3075CACC59AAEFD5E3782DE159F6FBAD9B38EC6B6CF4FEB4866AF3EFA5597EF66483C33F0CDA55D43BDD565AD98DED6C799235733361917E
	DDAD8EA8F5F55A5247F9FFE9A77D76G5FE40045DFC2FC3504B98A4EFFCE3742C1F46537C1BC85606B66DB3DC827385CC12E1B59BB8F2B32674FF9C058A8A6979B1DB7CDF849D91CF41B9AFBEE8B6BA67BCEEBC99EG6FF330177AAC841A2A89A4D7793F704972A501566FDF76227F137D79D8064F51D846646788955B3FECB896965FA770B1CC18909E4BF03A35A4A51D46D253C7E3A9G1A72AF13DEFE2B61CD00CB52700FF8B62BF1E681608A81F87ED6E25667EFD537C30C85988418F0BB6974CDAF4EDB1B4E
	4C9CDF6FA5DD2BF677D25F9940DBF0BB755D6053ECB591ECB5115AFD4207FA31E98EC0737EF79AFD9250AE50682DFEBC4FF378ED21E0B57B57648FAAFB48152AD9D2DE7DB9D6B4614F3DB457EE2EE77900628F102B027B6E595BC57DFD4D2F59957A2ACA217E1A83486F4509A650F9592F9A747E143BE102218DDD33A6975BD3D49B2604EDD8BB5DDA52BC1DECB8BEC0B6A9G1A72BB285FD58152ABAF3FF3F12B9015232AE3BFF7D133B770A7E9EDB4CA3837084A9FB57FDCFEF1246DBE10291D8550C6619DE43B
	17B4FC551D9DE576BBB5DB84E30FA9A3C8F225817D12BB292DDE05701B6F6AA8EB3C0B5AFA01615DBE300B68F1DF12766C65F65BEFBA01B2C80A9AE85AF03BA5CB729E933339C704C849F6D41F1AF4F129DA4170D84D02A45A1B38BE5D5B211EE32FE7EBA876B1FC970D2DE2004616833815F7517C185D2E1F9F3BD379BAEF0767E0AA0DFF46C0ACDD737D73DFC2F4B61B124AFDA1A3B5FC5E138B3F73D3C71E7F70B34BBF309837AC151FD9EE5270E5CEF6462943F399D298BD16AB6E06BC64EEBAE79BE3C19916
	C1F9ADC079A470F1G9300E7922D676BCF63B944286718ADA0156C93BD6DA3B624F41EF89AF581FBC8353C7034BEA6F2BD3CE2C5BD0C1FF46DE7DD1C2F4806FC69574147657E69995CC3AC863E15G2577F4146DG283F07E4EEEA2B0AA7B31D472A42727138ED0949B110209CA74E229C5DB06EC1009400CC00C51A9C935B625A2EE95050F6FCDCE576445EF77CA9C13E1B92641B18A05FEF6D0959EB1DF43D7662EB445EAE7D3B3C52B70E97635CB0DA67D75C435BDEF3CF645973AF9C71E4BDB4F96438B85F5A
	38FC5D91FC5C47DE6E44BD0A82F5A9G19GF9GABGCA27C08EB705FC6D87ED1F4675B5DCFCAD7E861B9A8BC71EC629633C5A1B18FC936272B5CFB1124F5B057275C05DB0C0AAC0B640920055G65D3C93E3F379F09A71F762A85E424333BE8B155719E6CCBCC4E2638FCD5D30D78FE1FE0FFE51EF8FC0D067D4D0D626B0E20B36E6F29A83E1D91654108F654BDF2704F02FE5F44F6CA4759E27F39FBD0328B6A39E413D33FEE27CC656B7682F8AE8660F94027E6FA1F3DE2AA6D25DFED43B534725E0E32C3G66
	FB69FD40CD5E4474BAB1AE1F6BDEA33EE33E447ABB9317EF0CE1FFC57E447A5B9A17EF32E1FF7F11E0FFAF44654B3E57E87E3FF89E677FA228DB8E309620A28DF28100CE0061B41A7F63624FFFED52DF4B7C3FBD10587CC80B620B1C9F29E9C6FC97AEA626D7EFDC3EAC437E764A0975F7B0AEDF1EE1FF2B926C6F77F1798A8C7BFBB818187DF22378A26DD7E858DFA06A9E473575F7B50A2FBA223CF91AE15CE7FB135D91F5B1F5BB0D62614E014446D9954597299753B42338BF71EC2DF04D67E56D8A6E45C2C1
	5D3969CF5BA9565B27D12CBF5F0331BE8D4AB9G79G7C2E42A7ECFF389C4A42FD04ABF2A12E9C4AFBB45C8F5D08B3C1592E61DEE367BE63211CFE9F692A54096FAAF2201C8F309A2074FE2AFB035561198B1E87D7G7E10D6B74951F98DB6A1403671397A3E307E301F1C7B0CED4B7BF8743A7AD82763A55CA6A1769D829ED3EF3544FEE71AE2B1FEE7BA49E27C4EB44D728647C39B7A9BB5D41FE1997D1DEA1645681D6AFBCC066F9623BF48A19F380F443A653EA78D62BBEC087CEDA19FDF963067E0649ED16B
	6365B33165ADBCE3AC6F2BE70C6595BAC617F7E3476872361F0DBD162E3346E369B9EBBC964759514762BB9BEBACFC8D2E7CB71EB9F55ECFEB6D3A4E7DC274DA4B4F98C93996C914DB9D8EED21ED8FC8B6C135574F94FC479F753E7B94E467821DF3FD685C2452B4A97E137B35317E36B336DE7E50E92C1752F3787EA4688F49B6A9D27632F395DADDB899E8D1A4AF8E075314B3FEC5141DD2146E5EE57C3CAE967F6EF331744BE51D5FDB264AEAF648FE2FF93F5A76C0D7ECD92FF6994B1A5495DB565B3A624B1A
	5A95DB56FB3B624B1A510532D85DF6372C3E3966B27A7A7476BC2049DE6B99A97B8155C71EF9FBAB7359E0D06227420D22A22ABCABBC7A368E8A3456B81B105760BDDC3BCD76FBBC382AC0BDDFD72EB869FD1CF2BF2DAB27583D28ACA8678194E8717C41532365A00577C747E9FE5E67EB4776B62673733EA28DBF407065BABCBF87945A697DCBA554A52553B9E52ED7AF6FA1CDF7D7FDA3F5F7D0459F726B69BF5168DF5570E645EF2E516C3A72FCECBFFB653C311F7D577958FEA6F4477733CD5D317DECDBF7FC
	BF7BD337311FFDB320173DD613FD52C53D6C7BB5FC0F2C272F51709F85C76A36C6457FB828277F9F0DFE1672C7FDBB7B84757E64DFD8CE3282EC39A61DF20D0B8C37F1BA648D534946E786746D7EC320F57E676D2379DE7D74D83E77AD16A31927C77A5ED2066F1E9E69FBE28F791E8B6A7235F119C3E82700ACB9A4D9166C6D6ECBB807DC8FA00F1B49318FFE57BFC07495AE3C036491077CA1C590AAE6A05D17DD34274F823ABC0015G428C3E3767F3F76EG6A5BA10BDE1832EC31A8E43C3FF693EF8F149500
	D4009C00A5B3284F377B109798A1FB7432917271AD6F437155E7F414B5E7906D3FAF211C3A08205E7779E7AD5E690DCCC55DA36E9C4D5A05B6B9EE922D12077C2177D89837DF3DE333C5545F31D1B2381C1361496B5F110C6A33B2E8FD5D7DEF543936DE286BA2770731CE720705C03FA4E3247DD7C0F9ED840E5B7D9BAC36ED1B496DDE226173993ED207677EE0E92577C77B20AEF5A6796E57CED36E3CE6A645C725DD5046F7C577FBFD7B756428578B3C0F77EFE89F5C461F04B07F01F0D4E3FA105DA3E2659D
	8F12CF0D277A7D5C5F6F8B114F26GCDA6C0A14046D95CAE6A3E44C2FA2C1CC5FA7CCECFD8F7BF547DF6765CF8BD0E3921FD84E66177BA9B6C0226535FB8AB61691386AA3577400D103D7B70527A1EFF9CBCD8D5F9F05F3E3D7CDCBEFDC0FFAEFF680FE38D6FA1A26428BADE24BEA73CB6C57D46E63E0B621B1089FB35CC0ABBB5BDF83E94F13761186C369F911DA69645F83E70FB360FC47EC0A6658BFFF3E31B19D0DE8830B4136E6F8CFEC477BA329EFCC0D87CD3637BBB4BACD70AD7E772F116BF24FB577C31
	35047779EEAF75E9067A718FA12E25D79FBBD017FBCB7919812F3FBC229E73CEDC53427533C75463F8F70D607F5108FA5451BFD5FEAE5BF39F12ECEB1F4B96BC7313E2FE6F34D8152910E4FBFECA51364966755C977E6A887B0257B10AAF5456FC42FDC1BDC7F64438EB45A2B057DB13C86F092B9E26B363D392616C8FD3AC1B42F240B4A8AF54F03FE238A5D0DE2B61CE3AE82EEEFB04F075F64475C0F970911A3F0BDAB18ECC504A5FE6E53CD7464BDD2D1DEA7D0D1B47C156C7A25F86DC8B70B5F949E90AEF19
	A04B8200820035GBB32A8A6CAA13CD725BB72016DE7D0122B1B86A1178822E52BEDFC132AFEDFA66967783E4B26186F5E6A33546FBE32A2677853A1DA330621AEA7CB1B635E43C26CBBDE7C900A6EF935072C9DFECF48ABC55DC360FE34251F66644AAC0A83757D586F6E59108781D8GF2E6333B8916C6F54C1E5951B6AF92425FF1A00C811AD4003754F2666CF00E46F5742D73FA9DED1B6AA9267A4836572A7DED1C930EABFCAF5F4D7C3B9E701DF3A85FB059715B068E31B7FAA78FC8336C8FC832B244316C
	C62E6AEBCF3A92FB2F7BCC02FC456E6B6563B94E7DACCE8F4E094C05B399BEE9CEE4AEDC63563E47043A42B9F49F0367311D0FC29E8AB081A083A08FE05923244B3B6D107A0F7E9E42628F18E03365519D45F079AA633EA30D846E97DF5FC0B1B9670907055C1B9BE35EA3ACFCD4DDEF3279B3B7FFCA91BE55F31EDE142F916AFAG12G52G328196E5EB6F116D6A5D8BF40786C591EDAEF6D3D4BBD4F3CB83E1B4A672DC36632211EC856AB8D6E7C74F8F4E57AE9971154E657495F32379F87C49F66237AC7D60
	38AC1BE7697E91D1B67BBDEE5B103ABFF80E4D857C3627EAAE452DCDAE54054BAF3BAF79FDCAB4A76DFDBEE4EB822E0D1E39B45F6DAE1A6F598663227C1AA08FE8164E8D4F7715F343FE484752EEB75E57743135E78D50174E23F1FFB404675161ED8D8BDFB5BCE63DFA1A563EFD73A80F585F9619C76CAA516781763648BC8247952E1F549619C7A40F60DF549619C790BF773523FD38B63A6611DF75BAF03D0895030E427CE9076D104FC9FE119F0AB7D70667993D4778FEDC3E48779A1CEFFBFFE231A71B2D8D
	1367C5461E2FB2FC463C4858F3241FE2CFB65415BFC679CB0B05ECB8883877857EBD9562785F6A4F0CF0BC577A9DDE03D4E3450FB7D32C5871488C2171FB0D459F884631A27BB12EBFA1A73C06A4F557EE6276DB6545AA757BA9470731EC879161B824E92D56DE58D8DBC42746A3BA31CDED0F4B1795354E65C45F516E95323BF95C7A5C96B35D02E617B93A5F875DB7FE1FE43E21FD286FD0DC314F6326390C4F63E6380C4F7E9FF20D7E1EE12EAB7637EA39AE7D37EA5C6EEBFDE81BC360939600C10089G69B9
	B40FBFF763BC66EF5BA35706A63B934F83710BE9057A5F5B9BDB56FD3D713F337E24974F5D2CFEC571FB31FD42FF560B76D27C8106643846DE54116CF63A94CE2A620F33FD16C7F2B0B44F6FC53607D8GF26567D04E7F34C87B00259A2ED7A23ABD7389771CC4F43573A9BE3F684433C87DF5D75D99D4F5C7D31B2565E80369C27B5136D61369C8C35B0506768B271A1A9A1B9A55BB5B2EFF0C425F5C5A565255FA4AE25C821DF95F6D229812B4BFFE8C19BA857F1ED1E348C47CDCD95D474F3B083F7938EADAB1
	DE46583B6D5FDFD5D93BEFAF4D1D57C61C23AF00B1AC1EBF7ADE085B67D89F1EA98655784F5F8FAC5C0E7BCE374F6376C9C77997755A9C7DCAFFB8A6BC257B5D5CFF636303787F5047A90BDE0F4BC107EB6268114F5DF49973134DDF81DF85B08158818681D2GB2GF281728116832C855876F8C759EE00C3G4DG5DG0147E92E9F76E26E94EB4DD68FD1580417C5B77BCEC0FD9F670FBD674B7C7167FC391FFD2384F355B294207781FE74913F439114345832533FCD4335844D7E209BD7926E3703A23BC78F
	9DF11F9EE61FB170BC6E87A265F1C90F9B67F17ADCEEB250A03C2562339E8F2F693C6D1CD0386D97A5EABBB7C65B6BC6E9BBDF57B6770529A376D2CB21FE25C13BFA60B16A6DB6B66FF3BB4AB0EF6338F7992E82F03BB41CE4A1BAFFA961FAADC4373DC47BD6DA0EED53A76578B64D130D637833F2F49C7F16EC9C473FA36B63387ACD340C7AF402AFF6EAF4BF52F067B45CF256A6624E3A6D0ACB437F1C0D8B714D92F6C87855ECDC838C572962FE2D431D237B81B27AA5086C426FEFB86EB599FD4F6176F8F839
	A408B4EE9F3829260F0D6C8E03BBA06A467D3A0643F12B6791D60A693539C6B11DFC50B9A0E8BC5B9D4463819EDE1F4EBE5F63761D6BA21B97E9382F384866E19F95BF52FD7F41544671BB86DF8F63C9ED14C7DB757E1A816D65660E6E2F1C2FE2447D68FC60D996070F0F7BE3EB34ACFF376AE5D1F10EE83ADA071E0E7FBFD53D456FC3A16BE5E1DDD0965E0CDE739AEE7F4F6FE5DDE8ACAF960ADB0475AEA6FC0B30EEG35B388EBE871820019410B42D56C8ECBC5B320252DDACBAD4C8D16759F28AB79490C12
	E221941A615322C5A8C55F0656E45EEA89CC8BECD3A85583EB3B44900BFF7889665B0A70E77AB631526CD7E02E0225AADB83989F030A30BD7C9E2C126EE40D52EC94166E53FF21BF2FDA13EC0BF9EE1F7F047AEE640948732A27BCE8039DCF429A74A42DBBF525787FF0445AEF34FBC50F87F66EFD8DBEBB6EBD0E8CA9D2B85F4D73E07BC122D1AB557F2B005DAB6855717CAFD0CB878841857BE9E999GG78CCGGD0CB818294G94G88G88G630171B441857BE9E999GG78CCGG8CGGGGGGGG
	GGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG2399GGGG
**end of data**/
}
}
