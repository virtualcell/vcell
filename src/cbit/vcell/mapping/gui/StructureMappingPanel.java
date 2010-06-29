package cbit.vcell.mapping.gui;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.awt.Component;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.UIManager;

import org.vcell.util.gui.ScrollTable;

import cbit.vcell.mapping.GeometryContext;
import cbit.vcell.math.BoundaryConditionType;

/**
 * This type was created in VisualAge.
 */
public class StructureMappingPanel extends javax.swing.JPanel {
	private static final String PROPERTY_GEOMETRY_CONTEXT = "geometryContext";
	private GeometryContext ivjgeometryContext1 = null;  
	private GeometryContext fieldGeometryContext = null;
	private Component ivjComponent1 = null;
	private javax.swing.DefaultCellEditor ivjDefaultCellEditor1 = null;  //  @jve:decl-index=0:
	private ScrollTable ivjScrollPaneTable1 = null;
	private StructureMappingTableModel ivjStructureMappingTableModel1 = null;
	private IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private boolean ivjConnPtoP3Aligning = false;
	private VolumeSurfaceCalculatorPanel volumeSurfaceCalculatorPanel = null;
	private JComboBox boundaryConditionComboBox = new JComboBox(new String[]{BoundaryConditionType.NEUMANN_STRING,BoundaryConditionType.DIRICHLET_STRING});

class IvjEventHandler implements java.awt.event.FocusListener, java.beans.PropertyChangeListener {		
		public void focusGained(java.awt.event.FocusEvent e) {};
		public void focusLost(java.awt.event.FocusEvent e) {
			if (e.getSource() == StructureMappingPanel.this.getComponent1()) 
				connEtoC2(e);
		};
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == StructureMappingPanel.this && (evt.getPropertyName().equals(PROPERTY_GEOMETRY_CONTEXT))) 
				connPtoP3SetTarget();
			if (evt.getSource() == StructureMappingPanel.this.getgeometryContext1() && (evt.getPropertyName().equals(GeometryContext.PROPERTY_GEOMETRY))) {
				getVolumeSurfaceCalculatorPanel().setVisible(getGeometryContext().getGeometry().getDimension() == 0);
			}
			if (evt.getSource() == StructureMappingPanel.this.getScrollPaneTable1() && (evt.getPropertyName().equals("cellEditor"))) 
				connEtoM7(evt);
		}
	}

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
 * Gets the geometryContext property (cbit.vcell.mapping.GeometryContext) value.
 * @return The geometryContext property value.
 * @see #setGeometryContext
 */
public GeometryContext getGeometryContext() {
	return fieldGeometryContext;
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
 * Return the ScrollPaneTable1 property value.
 * @return cbit.gui.JTableFixed
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
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



private VolumeSurfaceCalculatorPanel getVolumeSurfaceCalculatorPanel() {
	if (volumeSurfaceCalculatorPanel == null) {
		try {
			volumeSurfaceCalculatorPanel = new VolumeSurfaceCalculatorPanel();
			volumeSurfaceCalculatorPanel.setVisible(false);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return volumeSurfaceCalculatorPanel;
}

/**
 * Return the StructureMappingTableModel1 property value.
 * @return cbit.vcell.mapping.gui.StructureMappingTableModel
 */
private StructureMappingTableModel getStructureMappingTableModel1() {
	if (ivjStructureMappingTableModel1 == null) {
		try {
			ivjStructureMappingTableModel1 = new StructureMappingTableModel(getScrollPaneTable1());
		} catch (java.lang.Throwable ivjExc) {
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
private void initConnections() throws java.lang.Exception {
	this.addPropertyChangeListener(ivjEventHandler);
	getScrollPaneTable1().addPropertyChangeListener(ivjEventHandler);
	connPtoP3SetTarget();
	connPtoP1SetTarget();
}

/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		setLayout(new java.awt.GridBagLayout());
		setSize(514, 220);

		java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 0; gbc.gridy = 0;
		gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0;
		gbc.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getVolumeSurfaceCalculatorPanel(), gbc);
		
		java.awt.GridBagConstraints constraintsJScrollPane1 = new java.awt.GridBagConstraints();
		constraintsJScrollPane1.gridx = 0; constraintsJScrollPane1.gridy = 1;
		constraintsJScrollPane1.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJScrollPane1.weightx = 1.0;
		constraintsJScrollPane1.weighty = 1.0;
		constraintsJScrollPane1.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getScrollPaneTable1().getEnclosingScrollPane(), constraintsJScrollPane1);
	
		//set column renderer
		getScrollPaneTable1().setDefaultEditor(BoundaryConditionType.class, new DefaultCellEditor(boundaryConditionComboBox));
		getScrollPaneTable1().setDefaultRenderer(BoundaryConditionType.class, new StructureMappingTableRenderer());
		getScrollPaneTable1().setDefaultRenderer(Double.class, new StructureMappingTableRenderer());
		getScrollPaneTable1().setDefaultRenderer(String.class, new StructureMappingTableRenderer());
		
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
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		
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
 * Sets the geometryContext property (cbit.vcell.mapping.GeometryContext) value.
 * @param geometryContext The new value for the property.
 * @see #getGeometryContext
 */
public void setGeometryContext(GeometryContext geometryContext) {
	GeometryContext oldValue = fieldGeometryContext;
	fieldGeometryContext = geometryContext;
	firePropertyChange(PROPERTY_GEOMETRY_CONTEXT, oldValue, geometryContext);
}


/**
 * Set the GeometryContext to a new value.
 * @param newValue cbit.vcell.mapping.GeometryContext
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setgeometryContext1(GeometryContext newValue) {
	if (ivjgeometryContext1 != newValue) {
		try {
			GeometryContext oldValue = getgeometryContext1();
			/* Stop listening for events from the current object */
			if (ivjgeometryContext1 != null) {
				ivjgeometryContext1.removePropertyChangeListener(ivjEventHandler);
			}
			ivjgeometryContext1 = newValue;

			/* Listen for events from the new object */
			if (ivjgeometryContext1 != null) {
				ivjgeometryContext1.addPropertyChangeListener(ivjEventHandler);
			}
			connPtoP3SetSource();
			getVolumeSurfaceCalculatorPanel().setVisible(ivjgeometryContext1.getGeometry().getDimension() == 0);
			getStructureMappingTableModel1().setGeometryContext(ivjgeometryContext1);
			firePropertyChange(PROPERTY_GEOMETRY_CONTEXT, oldValue, newValue);
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

}
