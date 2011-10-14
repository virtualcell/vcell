/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.mapping.gui;
import java.awt.Font;

import org.vcell.util.gui.ScrollTable;

import cbit.vcell.geometry.Geometry;
import cbit.vcell.mapping.GeometryContext;
import cbit.vcell.mapping.SimulationContext;
/**
 * This type was created in VisualAge.
 */
public class ElectricalMembraneMappingPanel extends javax.swing.JPanel {
	private Geometry ivjGeometry = null;
	private GeometryContext ivjgeometryContext1 = null;
	private ScrollTable ivjScrollPaneTable1 = null;
	private IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private ElectricalMembraneMappingTableModel ivjElectricalMembraneMappingTableModel1 = null;
	private ElectricalStimulusPanel ivjElectricalStimuliPanel = null;
	private SimulationContext ivjsimulationContext1 = null;
	private javax.swing.JLabel ivjJLabel1 = null;
	private SimulationContext fieldSimulationContext = null;
	private boolean ivjConnPtoP2Aligning = false;

class IvjEventHandler implements java.beans.PropertyChangeListener {
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == ElectricalMembraneMappingPanel.this.getgeometryContext1() && (evt.getPropertyName().equals("geometry"))) 
				connEtoM3(evt);
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
private void connEtoM1(SimulationContext value) {
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
private void connEtoM2(GeometryContext value) {
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
private void connEtoM4(GeometryContext value) {
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
 * connEtoM6:  (simulationContext1.this --> ElectricalStimuliPanel.simulationContext)
 * @param value cbit.vcell.mapping.SimulationContext
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM6(SimulationContext value) {
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
 * connPtoP1SetTarget:  (ScrollPaneTable.model <--> StructureMappingTableModel.this)
 */
private void connPtoP1SetTarget() {
	/* Set the target from the source */
	try {
		getScrollPaneTable1().setModel(getElectricalMembraneMappingTableModel1());
		getScrollPaneTable1().createDefaultColumnsFromModel();
	} catch (java.lang.Throwable ivjExc) {
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
 * Return the StructureMappingTableModel1 property value.
 * @return cbit.vcell.mapping.gui.ElectricalMembraneMappingTableModel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private ElectricalMembraneMappingTableModel getElectricalMembraneMappingTableModel1() {
	if (ivjElectricalMembraneMappingTableModel1 == null) {
		try {
			ivjElectricalMembraneMappingTableModel1 = new ElectricalMembraneMappingTableModel(getScrollPaneTable1());
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
			ivjElectricalStimuliPanel = new ElectricalStimulusPanel();
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
 * Return the GeometryContext property value.
 * @return cbit.vcell.mapping.GeometryContext
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private GeometryContext getgeometryContext1() {
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
			ivjJLabel1.setFont(ivjJLabel1.getFont().deriveFont(Font.BOLD));
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJLabel1;
}

/**
 * Return the ScrollPaneTable1 property value.
 * @return cbit.gui.JTableFixed
 */
private ScrollTable getScrollPaneTable1() {
	if (ivjScrollPaneTable1 == null) {
		try {
			ivjScrollPaneTable1 = new ScrollTable();
			ivjScrollPaneTable1.setName("ScrollPaneTable1");
		} catch (java.lang.Throwable ivjExc) {
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
public SimulationContext getSimulationContext() {
	return fieldSimulationContext;
}


/**
 * Return the simulationContext1 property value.
 * @return cbit.vcell.mapping.SimulationContext
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private SimulationContext getsimulationContext1() {
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
		setLayout(new java.awt.GridBagLayout());

		java.awt.GridBagConstraints constraintsJScrollPane1 = new java.awt.GridBagConstraints();
		constraintsJScrollPane1.gridx = 0; constraintsJScrollPane1.gridy = 1;
		constraintsJScrollPane1.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJScrollPane1.weightx = 1.0;
		constraintsJScrollPane1.weighty = 0.5;
		constraintsJScrollPane1.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getScrollPaneTable1().getEnclosingScrollPane(), constraintsJScrollPane1);

		java.awt.GridBagConstraints constraintsElectricalStimuliPanel = new java.awt.GridBagConstraints();
		constraintsElectricalStimuliPanel.gridx = 0; constraintsElectricalStimuliPanel.gridy = 3;
		constraintsElectricalStimuliPanel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsElectricalStimuliPanel.weightx = 1.0;
		constraintsElectricalStimuliPanel.weighty = 0.5;
		constraintsElectricalStimuliPanel.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getElectricalStimuliPanel(), constraintsElectricalStimuliPanel);

		java.awt.GridBagConstraints constraintsJLabel1 = new java.awt.GridBagConstraints();
		constraintsJLabel1.gridx = 0; constraintsJLabel1.gridy = 0;
		constraintsJLabel1.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJLabel1(), constraintsJLabel1);

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
		ElectricalMembraneMappingPanel aElectricalMembraneMappingPanel;
		aElectricalMembraneMappingPanel = new ElectricalMembraneMappingPanel();
		frame.add(aElectricalMembraneMappingPanel);
		frame.setSize(aElectricalMembraneMappingPanel.getSize());
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
 * Set the Geometry to a new value.
 * @param newValue java.lang.Object
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setGeometry(Geometry newValue) {
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
private void setgeometryContext1(GeometryContext newValue) {
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
public void setSimulationContext(SimulationContext simulationContext) {
	SimulationContext oldValue = fieldSimulationContext;
	fieldSimulationContext = simulationContext;
	firePropertyChange("simulationContext", oldValue, simulationContext);
}


/**
 * Set the simulationContext1 to a new value.
 * @param newValue cbit.vcell.mapping.SimulationContext
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setsimulationContext1(SimulationContext newValue) {
	if (ivjsimulationContext1 != newValue) {
		try {
			SimulationContext oldValue = getsimulationContext1();
			ivjsimulationContext1 = newValue;
			connPtoP2SetSource();
			connEtoM1(ivjsimulationContext1);
			connEtoM6(ivjsimulationContext1);
			firePropertyChange("simulationContext", oldValue, newValue);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	};
}

}
