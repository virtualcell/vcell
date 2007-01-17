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
public class StructureMappingPanel extends javax.swing.JPanel {
	private cbit.vcell.geometry.Geometry ivjGeometry = null;
	private FeatureMapping ivjFeatureMapping = null;
	private GeometryContext ivjgeometryContext1 = null;
	private cbit.vcell.mapping.GeometryContext fieldGeometryContext = null;
	private boolean ivjConnPtoP4Aligning = false;
	private javax.swing.JScrollPane ivjJScrollPane1 = null;
	private javax.swing.ListSelectionModel ivjselectionModel1 = null;
	private javax.swing.JButton ivjSetButton = null;
	private Component ivjComponent1 = null;
	private javax.swing.DefaultCellEditor ivjDefaultCellEditor1 = null;
	private cbit.gui.JTableFixed ivjScrollPaneTable1 = null;
	private StructureMappingTableModel ivjStructureMappingTableModel1 = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private boolean ivjConnPtoP3Aligning = false;
	private StructureMappingBoundaryTypeDialog ivjStructureMappingBoundaryTypeDialog1 = null;

class IvjEventHandler implements java.awt.event.ActionListener, java.awt.event.FocusListener, java.beans.PropertyChangeListener, javax.swing.event.ListSelectionListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == StructureMappingPanel.this.getSetButton()) 
				connEtoC6(e);
		};
		public void focusGained(java.awt.event.FocusEvent e) {};
		public void focusLost(java.awt.event.FocusEvent e) {
			if (e.getSource() == StructureMappingPanel.this.getComponent1()) 
				connEtoC2(e);
		};
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == StructureMappingPanel.this && (evt.getPropertyName().equals("geometryContext"))) 
				connPtoP3SetTarget();
			if (evt.getSource() == StructureMappingPanel.this.getScrollPaneTable1() && (evt.getPropertyName().equals("selectionModel"))) 
				connPtoP4SetTarget();
			if (evt.getSource() == StructureMappingPanel.this.getgeometryContext1() && (evt.getPropertyName().equals("geometry"))) 
				connEtoM3(evt);
			if (evt.getSource() == StructureMappingPanel.this.getScrollPaneTable1() && (evt.getPropertyName().equals("cellEditor"))) 
				connEtoM7(evt);
		};
		public void valueChanged(javax.swing.event.ListSelectionEvent e) {
			if (e.getSource() == StructureMappingPanel.this.getselectionModel1()) 
				connEtoM1(e);
		};
	};

/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public StructureMappingPanel() {
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
 * connEtoC1:  (StructureMappingPanel.initialize() --> StructureMappingPanel.structureMappingPanel_Initialize()V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1() {
	try {
		// user code begin {1}
		// user code end
		this.structureMappingPanel_Initialize();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC2:  (Component1.focus.focusLost(java.awt.event.FocusEvent) --> StructureMappingPanel.component1_FocusLost(Ljava.awt.event.FocusEvent;)V)
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
 * connEtoC5:  (FeatureMapping.this --> StructureMappingPanel.refreshEnabled()V)
 * @param value cbit.vcell.mapping.FeatureMapping
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5(cbit.vcell.mapping.FeatureMapping value) {
	try {
		// user code begin {1}
		// user code end
		this.refreshEnabled();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoC6:  (SetButton.action.actionPerformed(java.awt.event.ActionEvent) --> StructureMappingPanel.showBoundaryDialog()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC6(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.showBoundaryDialog();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC7:  (geometryContext1.this --> StructureMappingPanel.refreshAll()V)
 * @param value cbit.vcell.mapping.GeometryContext
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC7(cbit.vcell.mapping.GeometryContext value) {
	try {
		// user code begin {1}
		// user code end
		this.refreshEnabled();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoM1:  (selectionModel1.listSelection.valueChanged(javax.swing.event.ListSelectionEvent) --> FeatureMapping.this)
 * @param arg1 javax.swing.event.ListSelectionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM1(javax.swing.event.ListSelectionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		setFeatureMapping(getStructureMappingTableModel1().getFeatureMapping(getselectionModel1().getMinSelectionIndex()));
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoM10:  (geometryContext1.this --> selectionModel1.clearSelection()V)
 * @param value cbit.vcell.mapping.GeometryContext
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM10(cbit.vcell.mapping.GeometryContext value) {
	try {
		// user code begin {1}
		// user code end
		getselectionModel1().clearSelection();
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
		getStructureMappingTableModel1().setGeometryContext(getgeometryContext1());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM5:  (Geometry.this --> StructureMappingBoundaryTypeDialog1.geometry)
 * @param value java.lang.Object
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM5(cbit.vcell.geometry.Geometry value) {
	try {
		// user code begin {1}
		// user code end
		getStructureMappingBoundaryTypeDialog1().setGeometry(getGeometry());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoM6:  (FeatureMapping.this --> StructureMappingBoundaryTypeDialog1.featureMapping)
 * @param value cbit.vcell.mapping.FeatureMapping
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM6(cbit.vcell.mapping.FeatureMapping value) {
	try {
		// user code begin {1}
		// user code end
		getStructureMappingBoundaryTypeDialog1().setFeatureMapping(getFeatureMapping());
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
		getScrollPaneTable1().setModel(getStructureMappingTableModel1());
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
 * connPtoP3SetSource:  (StructureMappingPanel.geometryContext <--> geometryContext1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP3SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP3Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP3Aligning = true;
			if ((getgeometryContext1() != null)) {
				this.setGeometryContext(getgeometryContext1());
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
 * connPtoP3SetTarget:  (StructureMappingPanel.geometryContext <--> geometryContext1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP3SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP3Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP3Aligning = true;
			setgeometryContext1(this.getGeometryContext());
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
 * connPtoP4SetSource:  (ScrollPaneTable.selectionModel <--> selectionModel1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP4SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP4Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP4Aligning = true;
			if ((getselectionModel1() != null)) {
				getScrollPaneTable1().setSelectionModel(getselectionModel1());
			}
			// user code begin {2}
			// user code end
			ivjConnPtoP4Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP4Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connPtoP4SetTarget:  (ScrollPaneTable.selectionModel <--> selectionModel1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP4SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP4Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP4Aligning = true;
			setselectionModel1(getScrollPaneTable1().getSelectionModel());
			// user code begin {2}
			// user code end
			ivjConnPtoP4Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP4Aligning = false;
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
 * Return the Object1 property value.
 * @return java.lang.Object
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.mapping.FeatureMapping getFeatureMapping() {
	// user code begin {1}
	// user code end
	return ivjFeatureMapping;
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
 * Gets the geometryContext property (cbit.vcell.mapping.GeometryContext) value.
 * @return The geometryContext property value.
 * @see #setGeometryContext
 */
public cbit.vcell.mapping.GeometryContext getGeometryContext() {
	return fieldGeometryContext;
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
 * Return the SetButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getSetButton() {
	if (ivjSetButton == null) {
		try {
			ivjSetButton = new javax.swing.JButton();
			ivjSetButton.setName("SetButton");
			ivjSetButton.setText("Set Boundary Types...");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSetButton;
}


/**
 * Return the StructureMappingBoundaryTypeDialog1 property value.
 * @return cbit.vcell.mapping.gui.StructureMappingBoundaryTypeDialog
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private StructureMappingBoundaryTypeDialog getStructureMappingBoundaryTypeDialog1() {
	// user code begin {1}
	// user code end
	return ivjStructureMappingBoundaryTypeDialog1;
}


/**
 * Return the StructureMappingTableModel1 property value.
 * @return cbit.vcell.mapping.gui.StructureMappingTableModel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private StructureMappingTableModel getStructureMappingTableModel1() {
	if (ivjStructureMappingTableModel1 == null) {
		try {
			ivjStructureMappingTableModel1 = new cbit.vcell.mapping.gui.StructureMappingTableModel();
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjStructureMappingTableModel1;
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
	getSetButton().addActionListener(ivjEventHandler);
	this.addPropertyChangeListener(ivjEventHandler);
	getScrollPaneTable1().addPropertyChangeListener(ivjEventHandler);
	connPtoP3SetTarget();
	connPtoP4SetTarget();
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
		setName("GeometryContextPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(514, 220);

		java.awt.GridBagConstraints constraintsJScrollPane1 = new java.awt.GridBagConstraints();
		constraintsJScrollPane1.gridx = 0; constraintsJScrollPane1.gridy = 0;
		constraintsJScrollPane1.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJScrollPane1.weightx = 1.0;
		constraintsJScrollPane1.weighty = 1.0;
		constraintsJScrollPane1.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJScrollPane1(), constraintsJScrollPane1);

		java.awt.GridBagConstraints constraintsSetButton = new java.awt.GridBagConstraints();
		constraintsSetButton.gridx = 0; constraintsSetButton.gridy = 1;
		constraintsSetButton.anchor = java.awt.GridBagConstraints.WEST;
		constraintsSetButton.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getSetButton(), constraintsSetButton);
		initConnections();
		connEtoC1();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	refreshEnabled();
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
 * This method was created in VisualAge.
 */
private void refreshEnabled() {
	
	boolean bResolved = (getFeatureMapping()!=null)?(getFeatureMapping().getResolved()):false;
	getSetButton().setEnabled(bResolved);
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
 * Set the FeatureMapping to a new value.
 * @param newValue cbit.vcell.mapping.FeatureMapping
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setFeatureMapping(cbit.vcell.mapping.FeatureMapping newValue) {
	if (ivjFeatureMapping != newValue) {
		try {
			ivjFeatureMapping = newValue;
			connEtoC5(ivjFeatureMapping);
			connEtoM6(ivjFeatureMapping);
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
			connEtoM5(ivjGeometry);
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
 * Sets the geometryContext property (cbit.vcell.mapping.GeometryContext) value.
 * @param geometryContext The new value for the property.
 * @see #getGeometryContext
 */
public void setGeometryContext(cbit.vcell.mapping.GeometryContext geometryContext) {
	GeometryContext oldValue = fieldGeometryContext;
	fieldGeometryContext = geometryContext;
	firePropertyChange("geometryContext", oldValue, geometryContext);
}


/**
 * Set the GeometryContext to a new value.
 * @param newValue cbit.vcell.mapping.GeometryContext
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setgeometryContext1(cbit.vcell.mapping.GeometryContext newValue) {
	if (ivjgeometryContext1 != newValue) {
		try {
			cbit.vcell.mapping.GeometryContext oldValue = getgeometryContext1();
			/* Stop listening for events from the current object */
			if (ivjgeometryContext1 != null) {
				ivjgeometryContext1.removePropertyChangeListener(ivjEventHandler);
			}
			ivjgeometryContext1 = newValue;

			/* Listen for events from the new object */
			if (ivjgeometryContext1 != null) {
				ivjgeometryContext1.addPropertyChangeListener(ivjEventHandler);
			}
			connEtoM10(ivjgeometryContext1);
			connEtoC7(ivjgeometryContext1);
			connPtoP3SetSource();
			connEtoM2(ivjgeometryContext1);
			connEtoM4(ivjgeometryContext1);
			firePropertyChange("geometryContext", oldValue, newValue);
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
			connPtoP4SetSource();
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
 * Set the StructureMappingBoundaryTypeDialog1 to a new value.
 * @param newValue cbit.vcell.mapping.gui.StructureMappingBoundaryTypeDialog
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setStructureMappingBoundaryTypeDialog1(StructureMappingBoundaryTypeDialog newValue) {
	if (ivjStructureMappingBoundaryTypeDialog1 != newValue) {
		try {
			ivjStructureMappingBoundaryTypeDialog1 = newValue;
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
private void showBoundaryDialog() {
	cbit.util.BeanUtils.centerOnComponent(getStructureMappingBoundaryTypeDialog1(), this);
	cbit.gui.ZEnforcer.showModalDialogOnTop(getStructureMappingBoundaryTypeDialog1(),this);
	//getStructureMappingBoundaryTypeDialog1().show();
}


/**
 * Comment
 */
private void structureMappingPanel_Initialize() {
	try {
		ivjStructureMappingBoundaryTypeDialog1 = new cbit.vcell.mapping.gui.StructureMappingBoundaryTypeDialog((Frame)null,true);
		ivjStructureMappingBoundaryTypeDialog1.setName("StructureMappingBoundaryTypeDialog1");
		ivjStructureMappingBoundaryTypeDialog1.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		ivjStructureMappingBoundaryTypeDialog1.setResizable(false);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}

	getScrollPaneTable1().setDefaultRenderer(cbit.vcell.parser.ScopedExpression.class, new cbit.vcell.model.gui.ScopedExpressionTableCellRenderer());
	
	getScrollPaneTable1().addPropertyChangeListener(//This listener is to ensure table is formated properly when first initialized
		new java.beans.PropertyChangeListener(){
			public void propertyChange(java.beans.PropertyChangeEvent evt){
				cbit.vcell.model.gui.ScopedExpressionTableCellRenderer.formatTableCellSizes(getScrollPaneTable1(),null,null);
			}
		}
	);
	getStructureMappingTableModel1().addTableModelListener(//This listener formats formats table cells after an edit
		new javax.swing.event.TableModelListener(){
			public void tableChanged(javax.swing.event.TableModelEvent e){
				cbit.vcell.model.gui.ScopedExpressionTableCellRenderer.formatTableCellSizes(getScrollPaneTable1(),null,null);
			}
		}
	);

}


/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GEFFBB0B6GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E13DBC8BF4D4E71958CDDD0E620A088AB5AA27259AECDA51629695F72332C7EAD3DB3334F23630D20D275445152351329A5D1B97A4B4C0AA29A6C0E9DA288DA51DA690A006C7DE931A47A419A48399723E1939B3F3431D3913BBF77220295D6FFF5D39B3F3A783335B651C0FBB77FB7C5F637F7E6F7F6FFF7F9B2E7465D585B939259C17D34051FF67F3B8EEEDB747ADFFBA3303616ADE4FA862F47FDE81F8
	017BFAC1BEDC5B00D6928460EC7EC2389F067BA4B523684F707B90F75DFF348BFE445013E56138ED9BD2BE36301E22C226E7AF3E4F68A8066B8100F14021EBAA32017BD9C79945A7D1BC031CDC0EDBD209F88ABA0FD25CAE60A984DC9B40F0A561D761CA8C38333E0E728DFFAC054B7A1E123B9F3523EB1341E6AA3B1BDEAB385F662CBA09F419BA0B29CCB55CFBG1073F95C3B62E938DE345EF0DFF6CC082AE95AA1C812494B7B7D224FEDF287457A7AFED589BA5420A2F4912C15778992479DFD8335BB5DD908
	5B6138CCE8B385633A46102EF178BD8B30748D06FF5A0170BB61F77E290C22232718FF17DE3FC86D373F7390377934B77FE08C7FEACF913F06CF057CCB1F5C16DB0E7B223D8845FC8550B6GBC8E1085D0DA15D1D48BF084670035708D38B6CD28AD3257AB7B1A7C7EE64929F449CEC1025F7575C02138BE41A1ABCE0EA37DDC5BD9E5E04FC2C0641AEFDB0E44A4658C6B73543E87382D8FFEB2F72FC1CC36263C13EB29A231F0D605E2C2F43F93D3F7D2D5F8CE066B9E60743AB7CDBD11D7E020BBF374093C9534
	1DD41D6E6CAA54574F629CC8075FB9B86E9CC7703F437846B7C3F8121F7D980F6E7DC0ABB6315C309FE036D8726F66CAEABEE59807955FDE121BE2A2B62419A2EDA997F05E19084EB7B5DB6AB05E7AD688CFECF98667A93ADF87340A1A0CA212DF1F6DC063F549E948E700B400ADG3B81CA4FE494D51EE1B93632702DDB4CB1AB2F284DC1D1F28A8AC7E47B0A7E84D7E1D6D578F6DFC065FD8E619A2F087C04A484B84AB304EB4FC4185CB84EFD8768F8DB70898A2F0A328FDA673D02AAA88142A3963D00F89481
	D3ED22D7G85DEBF475A1DAAC271177800FA556F64D5A144C168F338CF2679EB50A2B4G3FC9DC9E4135258D7C9F8770EBF148426DF58A8A540B7AFA0BAC5F887AED4893AE6D8C0A73B2DCBB1260F7729996636FDA90AE3F966AC3ADEB67543EBF6A5AB10B8EE4BE2F4C31B1760B3193F02D86FEAB00843088A099A09DE087C049590C226A33AC277E534EF24A3C7FFE2E6A09CF986614FD6C093C3633A417464FC60E2D1CF12493593D98E8A9G1BGF6G941E03DCGE883701CE373447531253A3611FD716AC0
	350EC7527DCE273F2DBADC75D96376CAA8FEC7043EE4FACFECDB94E6DBD19C3B906469FE6F557D5ECF2F85BA5C2198ED3CC02F87A3704F53AB4907335DA83796C39CD783ECBC47F262AB6D95E40CB57381010D2A9E858604608CE588716FE5418E57FEGB740A5GAF40884D7B17D0BBGE84C56G3C8C508AF01DA3F37EF1008BGA781CE811C8778ABC08340D5AD0617310E2DE0DF7E790C22AAG9B40BC40EA00F400AC0023F599C5E6G89E0B9408600CC00623F429C85B08C1084108A3099A09B60C4BD4B21
	1FBB46F3EEEFAEE1407CFCD9679323B13F8E68E240E84C578BB4EE8DB4D687E80CE2696B2731B9C063F80046BB124F2D7B6DCD402F7783C82C5B1C09469A0D811453FF56451C51D05CDF63C26362C59D6DF55A972FD09BCEF121B1D23E00BEB4965F547533FE8C69C1BFA6EFE50C7FFFG994BA99DA8DE423460D34D02DF70B961C700523E5401EC257321CB56E43E0A71FE18BFDBE0428AE878EFF42018712A2A0893C1D56897D495162CA17AF7302EC0F0429F1AFA192E6FF5209849B3B0FFEA7C0FF5203947A1
	7BFC82AEAA1A88257FG4FFD02A4F8416896B968D3897E071D68B96106B0C7675E27BA77F0E86E1402E89EA7EB6F35BC6AA73FA2FA21D42125G17DC0F6645B51DE53A98D9G071EA17C68DA4F66301963EC8EBBF1F38D17B6664FB5DA232D387723391975741920BEB407911B52AD4636E760B91862834456A1BBBCB0F1EF8BB2ACA81439965927C21F60A701BCACD3DBD782D7F7B83D0650B67305BAD9027B2FFD4816AB309489BDD810DCBE44FD269E0D1348470EE6B0508901324D79853348CB323B0672FF3C
	8977A9980C3B88AF34986D1B4A91033670AAA40C4FB2047A59AC387820243640B3D02BD3D4E5250659F46C45FDF005851DDF76C1D4EA587C646BC1B1AC3BGB59F40FC014DCF5DDD084E56ACF4E2EA11A5EAA350D2AE203E360C17523E9A06FB7582696B839A7D5B9AFD975C97DE0C2457710CDE87B4EB947D810D1E843464A87A0989C65F81B46ED208EE3B04688FEBF484A517A265FF2D51130136AE0AFE43416819C04B5651C91CAEF417603E1B8A8A3054C55D52290D19666EBFC6502E5171C3689D5DE87CC8
	3AF52567B25219A430676A0A4BE4DDD6F71968ED3BCCF4933E0BAE46378E6E5381F6D23E2A864237228171F1EE46E7EDA06BBDCF83D9674DB6685BDB2F71E5D23E6C06F03D0D0D084F6FE1FC450D04FE22B144FFCC633BA4B23EA5F03F9AE0D3A3511B59C87808FD0FCA0CAF0B520B1B085F08AF6447673D0C4F5EC474A1FA0A0EEFED936213FC0C4F5FC456AD0BA9FFB2DCCDDAFB2F490CAFDBC70FEE6FBFB43E52E6327E09EC0F787BB9BF632BEAA6EBE8F3B3715B562C7757B74578BC4D241DC514FFE9B351DB
	5802789A9446175ACC56E0C8AF625B582C376FE90D4F56C256E3D28B0963FC0B5E5F87830CAF390558352E052C734D4D7AB83F27B23EF49D5D2363A3FADB03DA3E3410359F328F71E707697DEF0D2F54CC62DBE5A64F9A974DFA3D5F1F567254CC7A1F71F94C7ABC7D748C63CBB2137CCB2657F5E1FCD7E7B57B4C447EEC4AD75BCA78485C702B13E83E42FBC8137CB4BFEB8A4CC00D2D2F6F40738359A7BA046794442F36327D2086DCBBD361FEA34056D6B6377DE0BF5B8FDAFEA305DB2DEC4E0B355F15C56DA8
	3B921A5B2A46E772C8EDDEEFC5BA6DC073DCE1F579DF3A754F24CD01006C907163B02DA9EA0FFE6E234F3DBD28763BC4A520364362E416551F77FA103D68F91922697EC1E03C18A37346BC68DDF7056D35F5B9AB3918FBED75759113AB0905638C0B45521B77F2631F38AB7779983158C9E3D07CF6A896A57FDE1BC7ECFAD633498C74793719CDDF4CFF5540A6B60F070CE23E0D712C1DF55046CE2D1D4726DEDF483708091E3475844F76A4CF34C17E37E9F16AEC085D16711EE47462006A681CE076AE07765381
	725B91FE7184DA1F21FACAF46EFB797C6D6A8CADA2683E593BD774F93B61603BB17B2A2D1D0E33F6DDDFF57F067655D1870BCB8A503737B39BCFB59FB79CE7D12B966A7BB22D1D328ED89BF430F61E198F9B2FA6FEC62D2F5716B6C476FE3CB706789703DC9AGD987153AD05CD40FC85705DC81DAEFAB02CB91821ED69F8A09337264B50E6DA55EFD28115542876E66AE3EB71AEB34373B7820A7F7F7870DC5E7A8A6E9BF58163B990058A43818CD92702C86A0EB14970E6857A8DBBB49FED23E05591C6EC2FB5C
	BCDEF8194845AAA8AED971022DAC82A67C7CE1EA4264D6747BA42BEFF9A6741C967048B3AC4DC80A91BF09EDA75DAC4F4AC02FD5533D549D9E2F8FCC60BDB8C96015FE36E055476B7EE37AF89572F79A46EB49336E5C4596BA17D8C2712A4C5816FBB103456BE9B70B57EE60296EE2B635F918CD04EF2507596E811ED59A9FA7A25D91EBFAEA67AF1B74F62E5F3B4450CE4F97D664EE69A276F15DA1BBD337EF4BCD5B4E6CF409CC3F95F89683106720C74B744FC1E9005F81D05C13D1D45143EC7C6C8422052F7D
	591A7DA1A95C4F5FC8CCCF9D487B35B6C63C4897C98C28DA17187013DA0B077739E90E5051C6534422E7A659C2F4BC66E5BAD2207D6DG847F891F169FD6488FAB9B0B25175195EB63185D8764C2DD5CC93C672F33F8AFCADF47159D7A08E13C71D832523C3006621D7523ED3905DA3C3BE5E68B578BEB63DEE6E3291F617D00CB51701BB47CF640157531383DB54562CB6E872610AFDE5285F4DCFCEA0A6520956496EB32FF08107D0BE214EB248D0F427427017C6EBE963BB51E73D8C68D1FDDAA5BCFDA8CEAF8
	B853851A47EA9D0BEBEA46D7B97B64BF440CEBF5BF09273DBF94573ADFFCF72F85005839AF406CDC8DBCDB7B192FD38164AB2F386973704AG2A81DE5127E5D0787AE1EB00452C5416D1E43131F6DAD4348EF1044ABC65CFD1195EA4605DE0E37D668BB2FC96602A2E32F61E89A25F9D2C60EBFAAB2719DEA770A6EB7C9F1D8E6F2B07267579C9E41F53E4B703DC71B5A6FB478C0A15AB62B91F48EC1CE1B2E6601F57E4CE4DA23C6893D5A83362CD2172A4696FEF742279AFE074BAF22CDD47FD0D367F37D9567E
	BAE8FB27567ECFE751BBC0176C8886CC78FF0B9CD0A3A702AB0890BE8F5C57F7DA9F371A31AB1A30A5267485D45C491EE219A572FD5C0901503EFC1D6EB783721E6AC13CCE368C1077D4859A7E1FB0FEDC07A7FE3776B03FD520ED99E06FD6DE3361F95F1ED1E4877083AC86C88158E4E76B633D56B3DC1CF7D76DB0B6959FAF7507ED2251B571B72DA846B704B9FA7FAD2B3E3E11B84C0D23B8E45A633F83E1C0647E9E4BE55F069C717BC3D7517A206ABABCDB810C834C822C384EFC3E52DB954FE7768E0EBACB
	5FBBA568476FEF5B8FA2A77009698B700969FB0ACF6C1D7733894A75CEA4665F58446D7AC7EA7F49C17D390A7C97BAF2D16E9A557E7557C95B1B2E07EABF492D0B83A8378A87B30ACEGDC8430812803AC371676469DCF2D3306630978762AA331D8D6A7A837A2C1394FA7A8772090A937FCB03C9F57C65C9339EFC54919A26EB7C748117EDAE6C7FB9ABB01D6B8846BC8000BG63G73G2B06D83F7D2877D25CFEC3655DA637G2ECD16EF34C43EA350EC3D61CA4C4739A839C89F5306426548BCF5309F79EA30
	9F539CD4D53C77CC76E45A0E30BD19ADC36C9D63DB6EF87DE8EC6B2538F2A543C67DD1B4087A2392E8E600E100D900E40075GDB07D9FFD477DD38457EA0CFF0513DC16CFCA64A4EDB6B0BDFC749C576C57630115C67BC0945737E38F225A3C671DCB40462D98534B600F100F900D5G9BG360F30F85E51579CAF1ECD6CA59A44147DB4C8F2E22FA4A666674D38F23BC70CF27CD701431CFE3F866F50F460AD19AB622C6064683B345F74681FA1CBC6495E40C538DA8148B8A8A8E7E758BC23ECEFF274B0DAC32D
	027B75G0F0F327111ADA56667BEEFBC399D23C6F2CF4415B356771338F25C18115C27FD0969DB93D72EEC4CA8DF7FB00C72359AE896GA740A20055GE9G19E3ACDF1F0C1F2F5AF127DB49D71B1C181F93F165328D63FA581F183E4A38F22563C6F25FCFD05F5638F2D5067ABEB315D8DD3DAFCA6ED8443D59D01FCFC94C3F7938F24363C6F9BABF0A72D4855AD20094000DGBBG72790C22CA1E6569497E065B495385672A12C0E2BE9E0BAB57469B497DB8CA6E567AF0D714DCE49F0E9B6AFBC0CD4C3F8DF1
	65E68D75294144745D91D7EE2921BE53F4E2713C98A597994F94C3FD3FCFD05F73B3717425996A7BDE145C2D695B92A5E70A385FE228AFE9B6B1FD4B2264A27D5B95210F1CCFFD8B3F33AC1C00F582G4195F7A3DCA35C5BA7581A60EB781D61BC5CAF57F09BB18E6DD723F385845759E30F11BFC8EE217C5243FC8CDE224399D3C741AD360FA0AF861E54B813AD76F92D33B66373DA97EC4667351AECAF90BC3421BF8D4D686D36054FF3755A0C4EF33D04FDF8B2972D611420CF953D82D6CB560E42ACBAE38715
	38AF68A3E59AE98EC46E75E8BA16DD0D6D6F4A2B467EFE6E2A313F77DEDD585F7B2F4672174CC39B4650DEEC6A841BEF4C57CEF351738D599FE68E5984DE69F13934C5D13FDFF0B0FD6601583EF58E987B56BBE06C5B4040423E0D8D44720D745743FFBA08658381016C1D1AF9152734E7D4748EAE6A9CDEB74C249CFBEF72B3210872342AF24BD9CDEF32BD360F77580DFDDCE3C7E78E82F2D0F18811FE2C3517E934505C5D2E8ADEE48E61799A16D7F945ADC445A18D4B93DAAC79076D31E2C5FC3D5B2577751C
	B67E3B0E984DD51BE84D58EAD0BBC8FBAE773ED0FBDDB5554C56C30331E3F7F850B8F6950331E3777AE07C58D58F460E5D79417831EB9CDCB8F647BC7A581D5770CFEAF82BAA63FDF0525E0706E24760E3C346B17854D06C98FCF6A8FE8C3EB094BB868F8C450F41C3C3686C4104689495FA621078F80F28773116E34F536ED6B44E8C760C7A9D0AACC968A58B47760DCC78DC8D4A37DD936CF9392C8C555ACAC7C6D1A3005D41663CFD658B2D1D7C0ED86FBCB27073F712033C5B485170CFE17CD29D1E3C73F09E
	E6E79D120146B9593B1EB3DEDDDFF755E8FDDD6C5363CFEB788C5F73ACBEAEC576DACF5318970E444E0103A346B97062C86C9CB8BA92BF87DE9B091D83D5A371F3606C08F18EFCD8567BFEC6733D579F6E7B990AAF185267CCA8061B27746D54EA78FB1530F6A06A441E8F0E460E61C7C60DE378715158B13CEBB4FE8C6F990D9D43AF0E460F6103238B5712378361B12B2578F2D59F1BD00D79E9983ECE435F978CEF67BC458B53FA7E339AFF6D0C9EFFCE43674C6AEBDB282F3E91068F657B958BBA17AE71F3F2
	D06538471DE8AC0DD8581E19936E6781D6832CF7123DB3D2AF3EBC036CF54113AC0483AFA8D285A4FB3C1B491641FD950085C082D8A2E867ADEE22312A2BAC743D78B21C837D282368BEB51DAF47766B7B004E79E4BD6E44FB58D950F63E0B2D519FBE425668A5AED6074ED8D99D9A871C6A8A2FBB0BDD316A5092DC5712DD245E64E978CFE2FC2A8ECF6A5031B2764EFD8350AA5C4C26D74A19CDC9EEE653963405C9676D27F53FA78F47DA437FDFG5171F76F2B8E920BBBAD5AB9B670F93B1B65CE4DB85ACB09
	B8FF7636A2BA1BF93785E7A219E79E454FDF787C1A074DB33F43EDDA615E833048434E0C4E7E051D3DCB7D7C5D5C8A1F7119516D36F7F3D7F8C8DFEF70045E65ED1F5C16CBF436F7B31D3B01DEADA25CCC377EFC996A5B2F6510E7D6C2DF93C6C775820D3B907D29B0BA3297FD2C9E226F6F5153D10CAED1F9625BA32FB15F329FD94BC9DFCD0A797D772CC8FCCA26572519ABF3F6FC69E69E99DF6F9C4167B82048EE9651376D475167D71CE59259307D081936D1B699BD96D80EB328829E8447A92DF1B2B49F13
	FC6F40EB046500DFBF49464075DE04CB077BAC8D372E170D0B429BECDCACABC3F65A357BE4FC0F4EDB137B93E5F6CAFF7F4647A52CA356F3707F96C82D2CABE735D285DF968324GEC825821797E6128B3A337E67F4A8465B2EEDB0E5443C73B5038AC14A257EBCFE2FC299439DE4B69E2352182E8C992BB23324703E232836E733D99C5C781EA81ECGFEAFFB66ED9B0747130577D8ED325F828B8DC97798CA7C6B6E3EDD7F48987C56DEB68657FD7D0B5C7A8F1AE31E6DCD72123CDCCD2F15BFD919032E443F15
	A272EFB350F681147AE09C81B48258FD5A1901F1FA9E85BD4643D3BB6F706053685A8605A84C0450E872A5B6BEB624BF4FD0F217141B9343C649C7EC1B7505EAA029FFB3AF31F74229C0FB995B837D4A057E115A711D9B1C961B270BD9EC0E5EF78FE77E21B977F88C3D19D4DF2D9C521B6E2D2075093722EFA62785C5959D3CC416874D3CE215A551B147254A483EF9DCD750F712124C56937EDE94DB0F2C08B7E51F9AAD49D159A53856205A3A8B6EC95DFB2417553DDA3FF15D53573EB6609986F8115E7B7D21
	3C266798675077CC21258DFEA3D9C3E2BBDA4EE2BB6FE7F34BA74BA36716D77274F3432672483985698A51FFDB9EB9375C99A6DFF5B8F2EEE172E4BC7CAB7A0BA86C5D0F6E775E2E77FF8DD2713E6A204FE61E584F9CFF7298BFF39C75986FEF3D6AD9F8AF728DCF6CEFCBEBBC7AEFCBC9AD78B789754DBA4805478132G8A272066CD31BA704F9E74ED86F9939FD983DA1DEE744C037EC2044A745FAD4676F50D987F6FCAFCC5C4FBC093B2AC3E3D28FD06DFAF227ED2E5BFC652F3FEF89FC1915D9E1530326FEB
	C51463126042E86267F36853EECA6F1FC45FBFDF63A78DF7AABBE0B958B3B1898FE2B65E2D65497CCB7A1ADD877131CCADBC77933D2F4AE8EC0F836FBC40AA0075G69G19G3B810A95D823835481D8811CG33GCBG12815682ECD4D8FF94CAA7E3598EC83A00451D2270A2BED7CBEAC2FBAFFAB6569FA257BD7B182E3576595ADB1AACE37DADFDBD96CBF3D35FD8D37FD8F7EB2B3955CC6A6B2A99FDDD2F5BF70761373E8836A8A4AEBB69B57B453BF2683ECF6F3985ECE86B69EB9F6C693699DB4176FD32FB
	D97DBA91B01A9BF6B5986148FC51BA45E97D69885B939A06367C01057B13447F47D26C7CFED60A1F5F3F147037DD101768B3A102FBCEC24F3F324B959034F114AD15E8387600D58E08E8DF002CD7BF0E674724G297B8437826312A90E34F1CE0EED6BA5B93E2DD7E46331E8112347E22FECBC9687E47DD8A438E199751F9BF2552E71719A6E3A06F361B691EEC0F42A9E8DAFE13B903ECDC08A99DE41F64DE01C1D62E6F4386B94B7AF23F8034B9EF4FE1D60BEA023753DCB14A4F2FFA76691FD90FEAD9E9F42B6
	0AFEDEE777C7B55CF5ED4C7FF28A793C9E7AE2F3000D5B8A7F2985462DD5117D306418A3D87C05B14D531CBEFD1EE6C1FB056A42F9CA7261E6B94B91B497935CFB65ACC7507CCE1E03793F684E3963B0937C51597DA1BC8BB3757939A924C71B03083914760729D2CEF7EE9EFF56588F0CBEB731212CAFBAF7DF6A5367AE7DBBE9790B601A7AF32EC065DE04D92E02FD76D40146CD40E4B285946E20F6623D9DF7547669B375F9BF39A7AF174BEF67761044ED6772D17635F385B8B95B39BCC85CD9748707727590
	18627E064BD998161E51FDCFDF7BF65F6908D97BECC17ACDDDB0720C6E1777A09FD783BEAD48F2A2E74F1B31F2826621FEAFAFC930123C5164F322B929F9CE95C2737CBF6EC16D87988FA552F9F18F32FBD2A773BFD0CB8788A4AE8D20AB99GG58CEGGD0CB818294G94G88G88GEFFBB0B6A4AE8D20AB99GG58CEGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGE599GGGG
**end of data**/
}
}