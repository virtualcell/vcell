package cbit.vcell.mapping.gui;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.table.TableColumn;

import cbit.vcell.model.Model;
import cbit.vcell.model.Structure;
import cbit.vcell.model.Feature;
import cbit.vcell.mapping.*;
import cbit.vcell.parser.Expression;
/**
 * This type was created in VisualAge.
 */
public class StructureMappingPanel extends javax.swing.JPanel implements PropertyChangeListener {
	private cbit.vcell.geometry.Geometry ivjGeometry = null;
	private FeatureMapping ivjFeatureMapping = null;
	private GeometryContext ivjgeometryContext1 = null;  
	private cbit.vcell.mapping.GeometryContext fieldGeometryContext = null;  
	private boolean ivjConnPtoP4Aligning = false;
	private javax.swing.JScrollPane ivjJScrollPane1 = null;
	private javax.swing.ListSelectionModel ivjselectionModel1 = null;
	private Component ivjComponent1 = null;
	private javax.swing.DefaultCellEditor ivjDefaultCellEditor1 = null;
	private cbit.gui.JTableFixed ivjScrollPaneTable1 = null;
	private StructureMappingTableModel ivjStructureMappingTableModel1 = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private boolean ivjConnPtoP3Aligning = false;
	private StructureMappingBoundaryTypeDialog ivjStructureMappingBoundaryTypeDialog1 = null;

class IvjEventHandler implements java.awt.event.ActionListener, java.awt.event.FocusListener, java.beans.PropertyChangeListener, javax.swing.event.ListSelectionListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
//			if (e.getSource() == StructureMappingPanel.this.getSetButton()) 
//				connEtoC6(e);
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
			if (evt.getSource() == StructureMappingPanel.this && (evt.getPropertyName().equals("geometryContext"))) 
				connEtoC3(evt);
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
 * connEtoC3:  (StructureMappingPanel.geometryContext --> StructureMappingPanel.structureMappingPanel_GeometryContext(Lcbit.vcell.mapping.GeometryContext;)V)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.structureMappingPanel_GeometryContext(this.getGeometryContext());
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
//		this.refreshEnabled();
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
//		this.refreshEnabled();
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

 		initConnections();
		connEtoC1();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
//	refreshEnabled();
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
//private void refreshEnabled() {
//	
//	boolean bResolved = (getFeatureMapping()!=null)?(getFeatureMapping().getResolved()):false;
//	getSetButton().setEnabled(bResolved);
//}


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
	if (oldValue != null){
		oldValue.removePropertyChangeListener(this);
		StructureMapping oldStructureMappings[] = oldValue.getStructureMappings();
		for (int i=0;i<oldStructureMappings.length;i++){
			oldStructureMappings[i].removePropertyChangeListener(this);
		}
	}
	fieldGeometryContext = geometryContext;
	if (getGeometryContext()!=null){
		getGeometryContext().addPropertyChangeListener(this);
		StructureMapping newStructureMappings[] = geometryContext.getStructureMappings();
		for (int i=0;i<newStructureMappings.length;i++){
			newStructureMappings[i].addPropertyChangeListener(this);
		}
	}
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
public void structureMappingPanel_GeometryContext(cbit.vcell.mapping.GeometryContext arg1) {
	if(arg1 != null)
	{
		//refresh table
		getScrollPaneTable1().createDefaultColumnsFromModel();
		//set column editor
		JComboBox combo=new JComboBox(new String[]{"Flux","Value"});
		for(int i=StructureMappingTableModel.COLUMN_X_MINUS; i<=StructureMappingTableModel.COLUMN_Z_PLUS; i++)
		{
			TableColumn column=getScrollPaneTable1().getColumnModel().getColumn(i);
			column.setCellEditor(new DefaultCellEditor(combo));
		}
		//set column renderer
		for(int i=0; i<getScrollPaneTable1().getModel().getColumnCount(); i++)
		{
			TableColumn column=getScrollPaneTable1().getColumnModel().getColumn(i);
			column.setCellRenderer(new StructureMappingTableRenderer());
			
		}
		if(arg1.getGeometry().getDimension() == 0) //non-spatial
		{
			javax.swing.table.TableColumnModel tcm = getScrollPaneTable1().getColumnModel();
			//Subdomain and resolved are not needed for compartmental models.
			for(int i=StructureMappingTableModel.COLUMN_SUBDOMAIN; i<=StructureMappingTableModel.COLUMN_RESOLVED; i++)
			{
				javax.swing.table.TableColumn col = tcm.getColumn(i);
				col.setMinWidth(0);
				col.setMaxWidth(0);
				col.setPreferredWidth(0);
			}
			if(arg1.getSimulationContext().isStoch())//stoch
			{
				for(int i=StructureMappingTableModel.COLUMN_SURFVOL; i<=StructureMappingTableModel.COLUMN_VOLFRACT; i++)
				{
					javax.swing.table.TableColumn col = tcm.getColumn(i);
					col.setMinWidth(0);
					col.setMaxWidth(0);
					col.setPreferredWidth(0);
				}
			}
			else //ode
			{
				//need to consider different situation for ODE applications.
				//1. brand new ode applications.
				//for a newly created application (or somehow the sizes are input in half way),
				//then the volFrac and surf/vol ratio are specified null. we show the size columns only. 
				//2. ode applications with all the sizes specified.
				// Whatever old or new applications, if sizes are all specified, we don't show volFrac and Surf/vol.
				if((arg1.isAllVolFracAndSurfVolSpecifiedNull())||arg1.isAllSizeSpecifiedPositive())
				{
					for(int i=StructureMappingTableModel.COLUMN_SURFVOL; i<=StructureMappingTableModel.COLUMN_VOLFRACT; i++)
					{
						javax.swing.table.TableColumn col = tcm.getColumn(i);
						col.setMinWidth(0);
						col.setMaxWidth(0);
						col.setPreferredWidth(0);
					}
				}
				//3. old ode applications.
				//volFrac and surf/vol ratio are specified but sizes are not all specified.
				//we show volFrac and suf/vol ratio and sizes.
				
			}
			//Boundary conditions are not needed for compartmental models.
			for(int i=StructureMappingTableModel.COLUMN_X_MINUS; i<=StructureMappingTableModel.COLUMN_Z_PLUS; i++)
			{
				javax.swing.table.TableColumn col = tcm.getColumn(i);
				col.setMinWidth(0);
				col.setMaxWidth(0);
				col.setPreferredWidth(0);
			}
		}
		else //spatial
		{
			javax.swing.table.TableColumnModel tcm = getScrollPaneTable1().getColumnModel();
			javax.swing.table.TableColumn col = tcm.getColumn(StructureMappingTableModel.COLUMN_RESOLVED);
			col.setMinWidth(0);
			col.setMaxWidth(0);
			col.setPreferredWidth(0);
			if(getGeometryContext().isAllFeatureResolved()) //if all resolved, we don't need surf/vol and volFrac
			{
				for(int i=StructureMappingTableModel.COLUMN_SURFVOL; i<=StructureMappingTableModel.COLUMN_VOLFRACT; i++)
				{
					col = tcm.getColumn(i);
					col.setMinWidth(0);
					col.setMaxWidth(0);
					col.setPreferredWidth(0);
				}
			}
			for(int i=StructureMappingTableModel.COLUMN_VOLUME; i<=StructureMappingTableModel.COLUMN_SURFACE; i++)//volume and membrane sizes are not needed for spatial models
			{
				col = tcm.getColumn(i);
				col.setMinWidth(0);
				col.setMaxWidth(0);
				col.setPreferredWidth(0);
			}
			if(arg1.getGeometry().getDimension() == 1) ////1D,we don't need y-,y+
			{
				for(int i=StructureMappingTableModel.COLUMN_Y_MINUS; i<=StructureMappingTableModel.COLUMN_Y_PLUS; i++)
				{
					col = tcm.getColumn(i);
					col.setMinWidth(0);
					col.setMaxWidth(0);
					col.setPreferredWidth(0);
				}
			}
			if(arg1.getGeometry().getDimension() == 1 || arg1.getGeometry().getDimension() == 2) //1D & 2D,we don't need z-,z+
			{
				for(int i=StructureMappingTableModel.COLUMN_Z_MINUS; i<=StructureMappingTableModel.COLUMN_Z_PLUS; i++)
				{
					col = tcm.getColumn(i);
					col.setMinWidth(0);
					col.setMaxWidth(0);
					col.setPreferredWidth(0);
				}
			}
		}
	}
	return;
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
	D0CB838494G88G88GCF01B8B6GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E145BC8DD4D457193647ECE8CAF7CDC2DA5A102ECDCCC39A6D218D595A0DFB0EED692E361EAD5BC39BB63141346E467418547A136AA9C99EB04848706F28A42220C2G4111BF858165EFD090B48854E0CB2B83BCE042B0B319F9A8D8237B5D7B5D7B664D4CFB0CCEB73B1E737966FEBF777B395F7DFD77A1988B9727A6A59B85A1A9D5E07F6A13844158A788898F6FBA4BF1333FFD59A0A87E9D8248953EF6
	CA874F9020C537A698F204DF9E51C3B9824AE5D05E873FF78B2F9ED9F091FEE8689982DA4F1F670C736BA97D046BC9236509728CF8E681AC869CF9EE04E70EF027BC1F61E3981EC3D232A02C59CCF83ED2DE40F0D5D06E82888108580C7C51705CC5B55F57584078069F0C94367EE8BAB9135423281303F18B4AB630E71130ADE95D96226B8754CEA26304F2A4GF1BEC57844DA834F663E2909B323A3D649F4FDD43459CC53962753EA1FB0CD4CD89B9B87A4574C28B46392AFA0364FE297EDE04BD6D22F61281E
	56A38845D05EC0F1972A092E4570FBB9C02C0CFF658441D7EEFB59E086985E467DBBDD5A4C6C9F7A6BF704626CFF5C152D611F348D7D0A586671AF6759481D6FE2DB5CA6B10F87DAA2C04E1BAF9B6AG8681A4007DB4876C1F9C05E75B085461181EF6585B1C4EF65B186B02E3CC34416F46C620B0DC3FB86AF00D898236736A72CA95FB668314FB611E65B0A611353C4D1776FFD3D87B6C0349E9AAB1D99BF9BDB974CD0C4512B7BDB1C15DB9C735F4473C691D135E3ABF94143A5789914929AA3A93DFFFB4B91E
	5533D1213B79F7242DDF23B910813F13E85CA147A87E77943FC001477C9C20F8D20E82DA6FF61E9BC3D95C16DE5D63C2CE75C32AF1887F7E7D499B36238D428EDFDBCA6AE95E6DC01D47E5DBBAA9BECC01C7DBDE25F9CA4A09C01B7D7D4B864CAF27C07A6B56C460F98B469500D600EBG37G963D45F36CBB1F9C3F4B9C6B33382476992BEDCCF489A86B3C3D971E622C6432F45B5D1245BEAADE36382C16911B6896984F5CED920391AF395BB477C7C147B951AE3AAC1255E1075AAD5322A43A5C48F37F6DDC42
	6392A9F550BAAD0202E927406B7D075BA47EB60BDB7A03F34CA2099E8E24FF69B6E9136B164BD0A3D4G3FB1AE0FA511F8C5017F2BG6265B8645DA1757508AE98AF9A9BFB9D0E299967A071C4A8FB1B4439140E9D9B01DF77B60F711713894E866505EF73FA3A6F64A96AB1DBC7097996579C6FE33F29AEA6B9G7C4B8162GB6G685E017E8F5087B08B907E8E4F29B706F8CE19B3178915BFDB241AD34EB79ECD0EFA87F3E955BB3EFDAB290668A4F6EFFA077812E028826883F082AC8208820885C07EAB550B
	94F5937B820D83618A1EB8456FBAE64BF285AE7C9D757AE6993E4F072EE3E534ED0117ED3B8258C5A0C571BBCD71BB13BDD395385D9AF56432E7368F5E401E188FE9BA12DB1BA00E99C98906CAG4C0905F70A300F35DB5CA26FD59F3820C308E31E8CC17FAA6A0761B9GB081F09AE09A604FAC6F8F10FAD8FF2F86A884688438A2601CFF842089209C2082209E6094C08B409FG0E49F16C21FA4CE0238DA08CE08540FA000C6484C39D40B040C20025GF1G09G45A98906DEG89A082A086E0A300D117E0E8
	8698838885085671DCFA7D04B3695E66948E5C5FC205EF1C467DBB26088527F11F1BD87CDAD84C32D82C34748D3098E531F8E631387B72CDA8FECF8761577F85E06CFBCF869BFB52B7C82E6FD334812711F6A8E1F1A77DE50F02D64A5A66903321C270741DF7675117425A12373B32EFA9C159D76F266F7FFFG76791BB4FE62F551AE19C527E89F039FEEC67B8C351D4D1B638ED966FE0AF742BC5B81931BDB46FF014650A2C9AE6B480CA48E081284CBDB8F7D912A4BBDB36274CC51DCD7840D1963864C33B27F
	5784B2C70DBA6CF6118EBE328823BFC167C851A6CE03519D0E993B0478271348FEE2CA1CE3F37432241D8219E3EDB3E43E47B5FA1405341B53E51D06A10DAC99044AD43247B9F3B4DF912335B0BE103D06GB4A3G4EF5B70E70392E786653424A2BD32AEB3970A79FC816C0067C8EE1CFB25721CDDFCDD637FD891D2B995E0D367EE908EC204E098ED8F83866BA9CF6895A046E98D228CCDDC38EBCA73C6955C80B33689532085B11CEECB98BCB964F8684F3FB3770D8A369B73E5B13F6B0F08C82B5B867944DD6
	0B4DB1D14D789FEE23ED8A8653A6228BB2CE7B9757FE153A68EA450B2F77CF241D4D6238E546A6F540DE29F34CAAB9DC555C2603FB5261898BBF27438ED1296673D8EBAA09E1A444F685C0DCAA1F470AD2881D2FED5884566130B19B0156301334F5EF0D113555C610556D44364E12695F17698B209C6EC7EF286574D5D0DE67C77F26CCB72641BC1B66CBAF2E6374D920ADD2505727917AEA19CEE0311F7CB619DE29CFB018753E7429931CAE81ADD4C147B8E56AF2E85BFDBCA3421218B4CB0F5CE70C3ABC9F5A
	E556FF10DE20A37D4726D8FFEE57931D8F55737DF7149E57EF2B185EF8BD768164BB5F40794C3BE08E8738358B79D66CC23E32F44217F50A732D83BCD997EE5F056BC1FD3A323EFF157924F4648BCD77569BCB6B3B5D4879A298BDDA41FFD0666BE962FC8586D8E7G8C99D02FE4C0BE346F25B31CEFA123C79850EF426771A33A0573EDB020BEC24FD17035E610724DD64EA7E4605AA6BF8379AB61E9126B2BED63FC218A3AFFFDBBE43EA59938C6722D8F7DDD564E79D6E460DABBAE837DDE1F217477D687675B
	4E6A4949C47E22CC549B1EC978AE18B9DFF3A62E5372D93D0319CA7BFEA3732D4F44B5DBE2A646B1A3CB696F77BAB9DFE5965AE54E427DG31539367AF1C65FC438AFA2C028F758E1C1373A58B570744BE429F6A25B733CB0EDF9646F7C5966EC956E4A9752E157956E5E17BF33E6DD94ABCFD3A1B739933B17FAA59531C2D649BBDAF5B170D7607B23E98AF3E129E4E274BC1FA81FBB664A09F4EA1DB685999BD137A48F25DB2EBF25F0031383171BC1DC7705CA9126E9D897FA21045732538E3E40CDD8565F5G
	5BF3789C789F197CFCA9ECAAD2D81174286679D9CEAE5AD1176B1983EDDB46D3F08CFFE0A751A981EDE1AE9F3F3F24D36EF15B5CEE472815EE2F5958F3B6D5B9C7A22EA7154C9163D617DB6A06C54CAC9F27AE2592FB497E1B2159F9043BA6C34079A59C744682E01CAE0C15891AE7F70D0DBE13B046E2341647E2514DA705EB0FBC12EC500805BE0F65CC1EA796914FCC2420CD2F49B6DD83FAF89E37699B3A43AAB671795EE394776DEAAD2FA7966A506F66757C64635279FC73D990E0DDBF2B65E71C2DD04F2C
	DC57051E966D3A544FB87D9791CCC7CF9D37B78A6A5FGD0E4A4399DD2C756F1E45CC51D690575772A533358E067F01F1C754A5B6CD94D369AB3E29BADB4FA5AAA6E5B11BB51660293BCAEB1C0CFB4F29BAB5A0F2876B33F558D737DA1391E3ABD30475D436BF97516D7FFB5D9EEC80D0D729288E51F27E7ED04FF89482D85407552618692B7E9524A560FC2AAD9173B44F117681E6C3413100C1515DF96785964633BDB194FEB3E793850FC67CA325AD9F1C876E832F1AF4622F92FA7A68BEDDF55078330FE5540
	ED8A811E15G3816D9BC20DC4BEC87BCB967A95A47EDFE31011C19DB68824D040FBE51B56EF0CD032DBC82A63ACFB135D1F2A779DD4E47379493595F39A79DB7F81AE10A211FE8DB7DA91EE7F52057A66B7E6AA96FF8FDE6041E6959C40BEB00AFEC1571DAF6D099AF3DE501EA3CC2DF9BC9DE328F6334E61FA7DE4E47CB748B8150265F1C6271B266437C174FED3A5C48EDC23E279A396D8B01E7054C1755C4F47B2C7D191D1B5B14F6AECF3BDF554E49279ECE5E1A0F76953C6B31B3BA7A9749F1C3ABF621FE29
	096B37814F92G5CAF8DDF725AAF81DE77DE02218C2079BDB93EF504663DC760EB7B9F1E7176D35742758C02FC28DC47D48B7145E6F5CBF21318680E2EE352E21FE0B942FA9BCB13DEA5B3E68B6A78052CA3866ACF84C07C932DF2FE6C077C584F7B22718CF9D2ED82373B20CD2F08BB467B56959E6F85EB22057C5DA12A7126EB097DACAF76FB629D82710EGC0DB065B38AD858530E6AA60B69EEE6778D0404548781F4B78C440559D60F1BB5D41630BE5310378B20DCD407A45139DBC87EDA037C4164DB0FB4B
	561955F28D6B38EE667A57023C71A00F5D531375D4C6721EDD4A3A4BFBD546F0EF26A61647D2830F6B521FBEA78CFD74B94D38769E44F8CA87BDF12D023EE6167BDBFEA737F3A570ECBF48FD3D53C9FC3538A6583E7929CEB286CCDB6DF286F92F9FFE5D49E3D6D518E018AC6475DCBACB56A1231EE11E717F53D92EF7B170469772F63BF50E63F30A928CDDC53C1EED67086F23FC4017751E6C62FA97G6FF2197F31AE6F367AE117B2BFD1B6351B4BEE823932C3DCF6D1B70955384FF9G4A7CD41639867C6132
	4C297384EF35DBA598E62DB74532F2EC6F67AF11794F2D76FA73EA37027BB22BBF74BC2FBF966A569F6675EFB9CF5EA90EBBC6E75CA67AFF2F43AD79CE84E78941FB9EF8263FC699376A2BE7E561DECD69A6B23823BD97FB38BD2DE04B2CEC4FF3BDEA390FBAAF9F6F557459E741D8261675BEBCE08D1E0BAE73FB5798F5B8C1FE679023784D815F41F519088F71C0CFE23EA0FE0062B7AA7068730B47384F4211844398G2E4DFF370B0C6D2B203C9EA09140D88CE38B40E0B1DF2B7FE7EA2D90603DDCB7F86532
	DBEC83DE87FF7C7D58CE925FA9F10E159F5E299CEBB18EF3B5A48E4EE2FFFF35G656E27F2A177A007FE8F9B48DAE5B9484581EC845015A4980ACB384F79E91501FC666F9719336C1DDA10FE3CF54FFE201CD89B1CBEF7EDF07ADE2A8D6EFD7EEBC14ADD2A8B4E3F2BF5776A9F4EC365FFD45E995165F6A6135CD51B074CA5D877E009E79E425C4A4AA439958678A800D5G6BG0477F9EE0D24856CCF1D332A7D89FDBBFCA238D8D685A9979E245C5703147BDE3D2FDC517B5E6D586CD3C6391F784919FC4A57FC
	64303D463248794AAC5042G16822C82588820AF05B5F0A9EF37EF251D8E58EEE42A99F4F4G2E4B61186A70FD2FA15BBA57901C0F777B49797A58DB6AAD0773D776G71D565EC28FDC61268F9B91E8F3DB0404F0746CA79FB5353278235233A2DBD8165A2CA555A6325EC529E51C00B8558G20AB03358C0099E0320C37C7323E69AE5B83F7137E2D01F6EE7333736E5AA225B1D0DB04142949AD73133B3BF8BE9FD0EEF119DABC7BF3C9BC1783AD8EE0A3003E9C729A208B40D64E63B928EF8F944FB67E628FE2
	4AFF2AA4B95AFB33A9B8BFC3824AADACD74B71AD6E7784655991BDADBACF0F074EDAE745B1013D7F7B3CC9391F0DA847F30AD570DC8B007DA06C923F1F37351C1F133E5ECF6FC39D05BC8498BE4A7BC7FACBF0FE668714130E2A497DAAC8FD3F8DA8972A2A6F69566074FDA7205C1223EA799A1BC772F585506281B681E4D4A4982AGFA811C95BCDFA383672BFC156BEE72F524ADB8BF1D8165C2AA54644A5A0353F7B2205CE2D5FDEB0354776B00F24BD575BD53915C387ADDBF3903BE65D8D5FD374C417977C0
	C0398D95EAF9FA51C872D428CCB094G54817481C8G0BG22ABF91E7EDE5FF2AFF9BA6FDCF530B3B89FAB834A45D52A49ED72133B3BB6FC5BCF4E378DB72A6ADBF1B6B87FFE94D0CEF7CCCD6E3367025337A820DC012A3E662E6062F95ECF4EB71EF52A7A8C5D4169FB57CF4ED7DF2F2A3E78A0757D371F1C4927BC262A6F21734169DB6CA7676B5FADD5FD03BD41699B711373559766238F6F7A46507735CB013692GF1DF2BA43875D0CEBC4657A097CD84D7E4CAB0B418643B3394C74E6A495D8B442DBBB664
	231783111BAF1F3DFC5660C59DBBB4F5245EE57D84D2B470B8268A695AF75A5252556F34E5262B5FE95B1D1E0BF828C3F9331C5377274FFF676DC83A5A1D378354071F53BBE02E993BE41D9629DADC2B0A3364DEA20C7C7DB3F61C9608E6376F59122CE34C206D6F14C15D5F0F8D6A7E5EB04C6F6F9D03163FB86F7D7D9EF28E5DE762735B5E0C9A41FFFE4333F16E5020E8F1FDB0BEAEAF42861C62A85737B7D35B3783196A3E9D49D4772DBCF3FE5F2AB335FC4376DA3DB71B4A3B5DA21E9B1BAD120551DE1548
	7BC73F3B0A97E1669678BB236D753B98CF276468B8A16B35E7E97BB813256E63DF3348FD8B37E346B5AA7A7A71592CFC1966D9ABF4CB62B4B187F9BECF65A50BEBC2740B43C3D49EE9DA720FE6E9458AFD0DEAD07AFAD26E7FEB875466C69B9BB396280C9DD81FF4AA5DD35F056AAAEE6BFFE5EB47EEE336FA6CB6E7EB47EEC7F66058A567E847EED7CE605865654C9F3B4AC6E56C6AE57CFA995FA7B97AEAE51D43395AB1984DD50F01B5D7BB864E5C40B1184B550E41025C40B178DCAE39F7B1E29D333A582DCC
	7471DBCDCA9F6B843EFF1F68A47DCC650CEAE054653059484B96011FD37D004EEDA45F42CCFCFF7ED49F99EB57C0F9BDC022BC8FFE7952FCEBB563F12DF7AC1681D2DF59F1FC1712A463BF2278BA859E5F31EC69176F51GED49F17EDE292DC55156972A6536FE2FD5092F11716BDA8DBCBE63AE47F4DF8D0B790BF95AB970CA1EFA8E3C1A271D83EF64854E015F65E967405BF901F3A0ED37FA8EFC39CD69FB2D6C7B55F6EF5FEB99FEF707B2E7BCB17CE507321EBA997F2C592B9E08BA5AB3E4540E61D523FA8C
	C523F68CED4640B11CB1EA477016B1F08C6FB34EBF167C2153BBE6F58CDFFAD6999B4F9833458B5FA0633FFB4E3B1EFA06F7F7A979CF487C6D5DCA7CC9991FFDDEB936F95AEA35975E136F1F6FD162BD397052D1F2275FE619F34CC89023AAF2F637D93E3B2F07F291C09740F0951E6161B8722D9B440FF158D1C31868C2232F1A48AE2F6432AB00BF96E0932993E8D5553C2F7E79A669430A91075D95F868AF44468152006C3C1C4D23FBB215ED43568238CE5FC24F5243216E48EA3EF69F9A64EB7768EABEBE2D
	B27171C9D713E0482F719E0FAAEB3446277D17082D2DB5B88E254878920A6FD360F1FCDA56A75FBD84DACC8D37A96A9237292C165BB4C1BEC16267DB0A5FBB7A3556767F9B00BA7286BECD9D980B77AA78D85D8BBEAF2C65F7D92A2F11B39D1FBBF967DC5631F64BC4AF4DC41CFFC6AA74CCFEC5AD1FFF9651BA37C2B923AE41D0DC476F514E3E4F6FA3AE7D7A63C254DBDFD13DC73BEEF0B639398E5B7ADA1D671DE2713311BBD1674B15DCE78450636AC8CEE79CD35E39A3ED7B5DA45CBBA37D0C971D0CA324BF
	FA68CEAFBA31B7D52724BF6EC5A7B1BA4D6451379F17F05F92FF9CA5581E3B23F9C73860843B1B4D1EAB3F9E1634C437EFA7762F74C1FA3785865FD9208B42B6FA343669843141B1E8E6F52CB8615F97F80EF308859E82C798ED7D894FBC0D791EC0578E8DA7E15CB8497B409B47894E8965F09957DD457B4552133CDF9C3AC46CCC144B9534CC6E2AE379593EA1C67F747A479A2AC3EBFF7C37820E157FA65F794B2FCFB0D482748258GC26B392FC595413D5FB585A9B7F14FF2B89E0ED412FE39345EF79DB7C5
	714B6BFD57F1DF144706982015B5703BB27187C8CCC2219C893092A09EE0BB0071945F8B3F379736AD731F758EBA1C3D30G31A936276C3DCE653D7A07FD70FBE93C8FC67F73B70465FF5722F95F396C9466E597FB8EBD9916C41E685FD4A171EF96E8E1G4B81E2815683A44A7E0D6CE577E248769EF6731651C9FAC3DFBE38300AB7BCE8B279220DBF791372DEC54ED7A61213B4EC4CE9E477D49BBDE3A00EFFBBAF73F753FDC0AB247640BAC0707C43316391F23442E273CA860FCD41B3CF886697DA120FE868
	8DE17A6295FAE36B5F546378742609FCFFFADDF4C956D10B8D17876D96D71F43E69D1D937A1AC81F4925E3887966B427092FA7D22BC8ECA79DAE6BCD07DD721794186CA1BA561031B58C4AB86EDD2B6263DEFC137A38279C7BB6820F501CE058434A46E6CFDE23AD6F4F11EF41BCCB9B7AE6349AE37B7AA59E5B22E6BE3714747B4EAD07D214F343077D3EF38B516521FF2E5FF7EE394FCB7E79FE5F39054BE3FF18A42946FEFF2C789DD171692FC11E7BD4F530BD5B816D3D4806836AFB11B78E281FFBEDBDB07F
	99E562816D6FF2D38EAA3F4B4531E069E152B6C3108B92C0B840D200154DFC9CF870A079DE85EF84780E811DE393E4AFC47E8A074475CB055A3E7E35B0705F6ED8D8C84E06C69C30781EA675F37C8305243DA40713A259FD43C292A317F5E2D2C2D67EEDF2A17D9B9962B8C5231F2B49F04068A5C564DB724B160FD4CFB04F43F27003110FE003B6E819107344F0D9B9E62F0578ECEA1EFF6EC73DDB0BC95F561D06BD8C0099E098408990829081908590833096E0AB007ECC0221G208A2095E090E0728CEF0F97
	8F15EB598EC83600250D6232D869DDE39C93DEBECE764C4A0B750A3D0F69F2E77FE0F7C7DB6F55010E7E8FFAFB5B5B7A2F368DDC3D5859E96EB463783A7806F2DCEFC87FC475BBE982374EE0DC9631E7E5FCD8925A50D0F5F29E9B3ABE686F7E63879787552D6067C18FD67171EB55993539E1D30B9A8E670BFD6413A9561E1B3D4E0A0496185BDB66EFCF0CFF74E16D7CFE7EF0607C0EB9CC3FF7033CA41FCEA1EE75E1327FF50C0F3BC5399F7D7B619C99576D6EF3382D744B953ADEBDCC674732969C77595F0A
	2138D60643BA74A55A36E6178436B53FC43DAF9EAC716F0BC7CA547BE2F90932AFA24ED4C25AEF82F2F5C8662B15F1D7E4DC832D1360BE340EC913B27E8C350B603BC42210634D542E9B94B744F05D8A5C95063BD8C2628DAECF12BB7D089BA8A16B7BF12B4D06658FA90F558E611763B1CCED34BAAD8A3BADB26E0A5C67E3CA094F43509633AD3C5FEEAE2918275F76399CCED8F24CA116FE1D4D72740BD54ABC8D077A168648D34C07DD7DBCC748DC0C382CFE1EA3E4FE47FD3065FD455DFF9AE644974CE6FA70
	BC4C4C675525C40FBC8721390C96DBEA94945F9250CFBD870051BE01B6AC2B724F5DEFD7A9F3977F96DDEB4382F8AEFDDDC81504BDB04B9571CF410AC83FF11BCCA637CB48163F8268268D356EFAEDE34A2F1EC8C996F45D42CECC5CEEC1C7322FDBC8254959AD24C0624E12BF6A24D3027BE361B69D4E3C306C2E709D6558377D3E3FE80EFD5BDB31EDB25AFC6F8A07B7929F3BGBF5646F3E24329E3DAB9817350403445E6031564D41BFD0C4CC96DF3126819679FEBA4753BB98FA3323FBBC15FADFC2410799F
	D0CB8788C5419BA31E99GGBCD0GGD0CB818294G94G88G88GCF01B8B6C5419BA31E99GGBCD0GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG5899GGGG
**end of data**/
}


public void propertyChange(PropertyChangeEvent arg0) {
	if(arg0.getSource() instanceof GeometryContext)
	{
		//this for spatial model
		//when it is just created, all features are not resolved. we need to set volFrac and surf/vol ratio when they are null
		if(((GeometryContext)arg0.getSource()).getGeometry().getDimension() >0 )
			updateMembraneMappings(((GeometryContext)arg0.getSource()));
		structureMappingPanel_GeometryContext(((GeometryContext)arg0.getSource()));
	}
	else if((arg0.getSource() instanceof StructureMapping)&& getGeometryContext() != null)
	    structureMappingPanel_GeometryContext(getGeometryContext());
}

//to give default volFrac and surf/vol values for spatial models. otherwise there are null point exceptions in XmlProducer for these two paras.
//we need to decently fix this later.
private void updateMembraneMappings(GeometryContext gc)
{
	StructureMapping[] sms=gc.getStructureMappings();
	for(int i=0;i<sms.length;i++)
	{
		if(sms[i] instanceof MembraneMapping)
		{
			try{
				if(((MembraneMapping)sms[i]).getSurfaceToVolumeParameter().getExpression() == null)
					((MembraneMapping)sms[i]).getSurfaceToVolumeParameter().setExpression(new Expression(1.0));
				if(((MembraneMapping)sms[i]).getVolumeFractionParameter().getExpression() == null)
					((MembraneMapping)sms[i]).getVolumeFractionParameter().setExpression(new Expression(0.2));
			}catch(Exception e)
			{
				e.printStackTrace(System.err);
			}
		}
	}
}

}
