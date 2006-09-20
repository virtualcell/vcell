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
import cbit.vcell.parser.gui.ScopedExpressionTableCellRenderer;
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
				connEtoM9(e);
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
 * connEtoM11:  (GeometryContextPanel.initialize() --> ScrollPaneTable1.setDefaultRenderer(Ljava.lang.Class;Ljavax.swing.table.TableCellRenderer;)V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM11() {
	try {
		// user code begin {1}
		// user code end
		getScrollPaneTable1().setDefaultRenderer(cbit.vcell.parser.ScopedExpression.class, new ScopedExpressionTableCellRenderer());
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
		connEtoM11();
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
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G6D0171B4GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E145BC8DF8D4551508817999A1E8140038C68C98EB04289129C609FE3426B53556CDAD6B5215D63662AED83E052EE9B7DF7B12CCC8C282C48D9AEB0AD851020D91108A8DD172CBB2E1C2E2B28E1184F1123C4C8CF9B3EF7266CD92A001BD776F4D1B1977B2B03B6E667BCE5EFB675CF34F4FBD775EF36F3BEF3852F7627232F3CAB9AEBB0F23FF0733B9AE2B1363CEAEFF594EF051EEEB91277AFB9760D56E
	150F0CF00D83DA72E35622926E73B78B60B9991E7742731BF07FBAD77F262383EEF46454GED565F8ACC934B59F61C4949474F4FB79643F5BB40F240212BD1C472DFE9AC237855944FA0BB07635662B22F9DAF2738E6F87E86A08EA0D9A4655361DA08A5CF29BAC24B754CC960B67FD04A59066AD155492042C3F85B69F5B7773B6CCC8F12F574F8B16519864F29G4878DCEE5CF9902E556D03764F7BFA1DF265F09FAF8815AE1B4763F45BAB6DBEE7D5D527AC797AE41F443792EC3B4D4D8B9CA78F21FA2FBDDE
	006B01BA601913906E57AEA4EBB9BCBF8D30CE41DFABA1FC835C77G4892336F333FD4D37D3BAEBE482D35FF2FF0070EFD86095815AC796D5B7DE1EF6EDB38ADDA1B104FB7822D548B6D8EE08690G8CGEF6398309C7F902E353DF23D68F2096EDA0F27CE6817DA45FEDE007B2AAA20D0DC875FA7CA7D9CC75A3976F80506BE938161EB3BEABE621304CFD81BA7F6AC61D65FFFF3CE3E06CFD6A7DC4C097792DF24F87DBEA13227694ADE6D8D0C49C05927B835EC6E250379F99A324B1FBD183F1156E3147D32AD
	B2EA6B19EE5456FB60BE9B7B1D63887ED60C0FD561C9FC7E86478CFACE83DA3F0F45C657F6260B593810ABB9B0D7538F310F9BF238E122C359F030AECFF8F15C8D93199FAB3A3C087109AABC51E5BA0ED374DCBAE2AD0A9E35961178321AD0FFAD3EG3185D0835085B08A908310F00145582D47BF3E4298EB37C9F21D4FA9747392C7F82DCDEF40159F15A51B496D15ED6EBE5EEA131C36DE0177F2344C59A664035EG3EFE9C7BFDA02309F77312CDF60AEE285D6662E5DE7212B22E26D7D1990947D40B534503
	G1707E3757A1A107F851BD77E5253EF13F9FF89C29FEFC2EDB2EC33C20DD0815C933FDC5B027C1586762F81582878A19357F70217E03C282AB20B62204FE3C116F0B597111FEF43E307916E4BAFB29FDFBE01F0D1703C60A22B67636657D475B4B87B107AB669BC6BE3F35CFB601A84651F86D88F400DC11DGE6G87C0B4C052980B29173BD8CCB5EC3B072B7869CD1AB1958D711DB6C6E2E94DD8F05F3A834BC4FA970CC3BCGB483B8G2681A4G24812C9BE77344FF3922D5F5F3E35A7DCF8D489EF63FDEF5
	EF263CCF2BF0C9BA75C5D33CA308DECE1F09EEFFF62BF5AB8C239702DC55FD3E6AFE9B3D6629F02F6B5471AA3D6E88429751AB09078DE694DB85172CC5FBG9AAE3118B8573C1B7431BA1B17E73D2ACD028E41777BA3045877A3EF8FDCA10D60D0927199008B608C0D7BBF21FAE8FFBFG708EC0A340D79C19735F83B88A308F602FG9F819C8678BB40B100AF95BFEE40F2FAC047A800C4008C00AC247BE508C5G99E08140CA0075G25132CC555G7DG8600E40055G46EB2CC595G9600B100F80074EBD8AC
	4D16BC59D7B727B0E0763E23320D515476B1DFB09A3377A8755FB16A336D54D7FA72BA29FF36D3FFEE27FE8FAEE7D75D3BA2306B7F82086FDF0E587728EF20D8FFD355860C065A61FD003FF0243F6C54E817F7298E9FF17E3E73568472F2E9BB32F6D777ADB5287B6A15747D7F8FA0FD7E9B93729FBF4C3B65865E433B7B6146CBE9F693521D4E1B8322426342F88F4C3375B031F9953C5704FCE813E549596B1379CEDE16A1357553AFE2D9DEDF2F47BFC5B3D913CD48E762884C33CA796BCCE80E6A935DEE9E8F
	BE8A8B25CF47F3A4AF70AED03ADE7439E5021F5D025693037CF9BAC75F583205C3F32C60C373BD495113EC285DBC125385C39ACA99380649E87EDCF63CCC65234DB0BE2035C69950AAA63339EE64BDB6576D39F09717FA561323154B45AE1A1DE318CC660438497E390E6894F5C2DB7799F82E26F8AF51353B8B96D6DC93AFC262A11D2F975DB23489DEB164E21EC3C7CA60EA8F249FA0348CDB010A1760FE3B9569F29CD2967F820444B664D9D5285F84AFCF6AC041FEF014653C07EFF05A8451FE00163F319637
	A9A80C1B88A7E40C76087436C6DDB8DB89A8E76EC66D5C408F58FC02DC8FEB2546FE27ACCA8718CE3BF6EE05ABA4FE9E518DDEB94066318FCEA29F2602EFB3G56CFE6735896B322335C06CEE07522C0F584DA7B35282DDFF6175236B242F37935242D37AB74FE1151E361B9A9047E1CC2DF834F19A174F68F23D7C441BC9B95CC7F3EC20F86DA020A1E95056803C30C0EE0F9887FDC055EB08556FFD302695BA4C6B7GADDEC5A7FE5AE4AE41EDB76463A1A5C64DF2C269B33FB33F96C433527EC368B92D287F88
	2A7C33E4AA12F9C8E66B6F34A9A4FFDBC365EE1CC2E413F26B15F2BD70AC8344CCA565B22612F2B55721F2C9BED6AEF3AA498BCB2EA379606E6B5475B906D9B943F524DC7CF501F257617AF60F30F249141E2EAA3FCBA937C1A9F7E89A6499G1EE9C42EE19AA9C7749B3F484AAD207464E944EED44EEF07F50C15632693F9083ED7D52EEBBAAAD7BE4E4A15CDA7394DFEDA3E812E15CAFDBFD54A452B6821755DF20915CB19CEF224607A083DED17D9390C69A457DEBF1D581DB5DDED6FAE0E15AB19C173749924
	7C1199C4EE528C3CDF2114334CA0F95AFEDA2FB0C32D5F1CECD6AEEB864959CAE792BF6E19293637AF0715EB18C9746A19C95683C8CF3F1FBF4EE565E495FD1D2A9C117B9B251CE1A6498F11FE28FCFC005C25C645FFB3C9BD99345EE203DA6ED09EABD7E1A06D50E0A0EB97CBC0391AAD2C1C43C06204151BE4D0477336FCA52E8CA4CE1369B5BD203EA714F2D9863266AB3D1E7827822ED15733BD29CD7B503C0677244E5906ED23155E91980B2B2ADA70BCC2761DF6B7213982150F3D1E6DAF19719C7BB4BC2F
	C338D9EC8E7C49B6363F94B318402DFE69C03E5E7ED959AC22EF75AC7F9C187C6B5AADE48C7734A119E3C0B34CE26377F82BFA0DDB6B750AFDCE3C3C26E3CF45C9759CC9F087CE22B9E240A9F9E593A4B123EC1CBAFC92690B565F94CD77A3F60A459C19DF62C06E2AD94CCF2D7DFBB95D3D3B2A2A20C91878226F93660B688B0B38EFEE1A1FD324630B125944877BE77BFDD1F1A9B30F68B4D3514981743859CC276F987F2C21931B6F7DCAB15B32BD2C1ED5D0C7C9B42B6747C3FF19483620040054B562E1FB1C
	4DD04FA4252E569347746B525E638CCDA2280C8D0A3E4B217E0DGFB67A07C07C3A80FC363AE1139751D43D7AB531FEC50FD3871636A38CD59B12E5BD6429C52C606B97E365A633264931D9F9DE2FEC9873A71862663DF6B5E536CE7A1598D357D10446A2906BAE4251E9747827AEB25EDC42E2AD2D2A042FBC6AE267DB59178568210FCE9250C7CA6BB1CB4FF6472D0DEAE7183926FF5B43A11CB7A776E33F2ECEFF2616BB5546674A58B396A4BDF67E86D95CF5B6148A93F11782261C63FCF1AEF684D6D81207D
	4A47F40A06B2E9GA41779C737BA1761E248D835B70669BC66C3FB66B61C20D512CBBBAF8D08128BF4E59E28446B144ADACCEEC4777BD8BE1FDB0956F7DE07B842420C049831136876CFBE96E755A0D7D6E4578C877A6B1ADE3C27A770B62913A536EAFF5D33CB6D2F825B94CDFF99DE3267A44690BFE544787D15843EDA2978EB4EB073D779CDB0D75C44F44AD4F4A265CCA3CCF78314D92914CB9AC13203F2FF2A67FF542A75DC1EEF505453F1E7ECCE56CDC43FBDB77B75BCF4FBEFEE736DCC4FB7C619FC994A
	A482107552ECC13DDEDA8B786239B01782B44CD57CBB04E801EB84165B5BAF865A3978A21353837CB1CA9D5B4610AD0253ABABCDD209D7F4758E1B5BCEE30476B69AA6E6F5E192ADC44665B1A6A39D6AB746C26E0E710D63CAFCGCE0EE5FD3174F3F44552B82677236385AA3F93FF0FFD457CBDA9BD19ABFB7DFACDFFA3C80C25F1916B7777D8DCEFEEF49C73776F95DD764C03DCE79E53F165A5060F81DC3A029F3D4C70467956226A794CEF4FDFE67EA54F79B856DC2489E83FE862D88C4A40172870AE6182F9
	1F6534E20D54D1164D642F857E72DB186F6EF29C46BCF2606C3257344F2CB1068796BACA63D8BE427C1A784C83DC5739D93AFE355CC27CB9F60B5F2F6BEE7E371CACG2267C30A1EE9D006DB40ECFDB78759EA136CF45DEC4AC1E3004B69D6A2A8B0FFB064B01F9D01BABCCABD1BF2D09E52679F66E9792EDCA6B7814A2ED9405AED1711614BEE3596355F4A6A19EBC436773181DF113BCAA9BF8D4A26AA650F6521F7F083E21F4FDB097F1BC52F9CBCF09EC70440F1F3EAB5DE67492207CDCD28F5761DE6726667
	B1BBB3C1567EFFE07232378446463EAD6A7EC0F81DDB982F837862945E4DDBD05B8C846DBF901ECF73994FAAA8DFF29B633995631DEE504FA6B8AF70FB77917D9F7E62BD2C3F466B5433A6D5E9AB2DFF23D2FFB354BDC9297FD401F67D874DA77058A553893C83ED11C93C34570CFDAB4060D309F7410E1A55A3100CA659CA1CFDA03FB22E23208749358D85AC57DEFE9B5B03FE3FA9785D62465B7C6F980CF1216FA348BB37D41C23174511F7EEF98A7EC90C2FD061098FBADA188FEA00969F475E937D3C830D65
	8537435A8B209A4082A083445C4EF27395A7BF61423C07B34138A039EDC2E740C69FFB9F56067CBD481F274F675B54E3AB71435BA272C3526D615F67B0A0FC7BB0DF5AD570913B6BACA8B749843E520530EE82E8G68DF48EC7EFDDBC5B81B597BC4EAACFD0796219D37DD359D04AF5F93193C1D1E48640DFBA2FBFFBFB3C23ECD915A17BDF4357611F9E75FE9759991632BADB9A8F635661D2805246E18057EF907445666AFD1ECE5GFEA3C0419D56225DGB5F73058EAEE8F5B1F9AC7B57B9331ED65D0E43EFC
	A6C23EAAA9B23E6688797A64E03EEF6E88EC473120E742B79442D799749C9744C75A2B65945ACFD98E348C0075G8571B0A6GB48388712C5D6636FF96365D50C4EB916B8157AC0A0375416FB194DD5F76C5E6633E903EE09B8D7101FCE4CE5B5109EC55588B2A7349B25E9FA7F354D6114DD171714A592C901941202D6B0630FC2B63355A6331AE549E1B01D63288560BG8DG8E00A80004C52CBD56359F3D4276A02B475056A0FA4E9D0E2CAD9605708537C55AA2AD3E3611487CF9A6AC5F1AC5DA7EBCF29A79
	B393E8250BA12E811A818400E9G090B19BF2B3E288B674FDA76228FFC4AEEB5021C68DBBE9A191D9F0665DB39D8AB46B7F97F4829770A706ED08BCE480EBBC779FE0E3E6F4BBD21DE3F2EDECC76A58A6E04ABG6987CF886CBC5E21BB593E68EEFC66CA0067A80085F7327E11F8B1B2BB9F8A4B17FC27965F143148645D9816AFDDD35E09714864FD9516EF6D1DDA713A2C9B45EB96504A92E07D81E08670G98G1292D83C4E8A9F2F4A512DAB09D76325486CFCADACDFDA02965F8FA31437AAAC5F9ACDF917AE
	C7A64F9016AFD3D3DEAB9759385A9D4237AB683964AEAD3EDD591159D79116EF6FDDDAF1FA688C0A539A2075G48G51G49GCF83EC3E0B4569AF3EB8F6B5F1BA61DC159A210DCF0565AB7D0E965F8DB91135E1DC88DFF09BD6E84A7BBAB7B27B0430FC8D1A727698A313F7B0ACDF0F263CFF09D05E7A30FC32263C383C485AEFC988DFF07B99B46509DBA213B79E4297ACAFDED3DEF5FEE472DAC278AA031ED3B4657DE704720C856164E5E84A1B037956875174003B9B561978CCF0B5FE2FDB8A4FFB955CDBA6
	04B343F37F5DACF779FE8B42CD0367B88577930CC3FB7A680C8641F535F46948C4FCD7225BF6AAC78BCF57EF3AB2722E30FE8439BAF8B2967F2AC37F6C5B068E6D33EF1BBA344F3E7D3663D50207BA54A75099BD3BF36233F1051DDAE7637E04EDF8966FB5CBBE376CF471D8AC49F179D1F4FE91E60C8E1F1BCCA7C832B7F8CFCA1151E25137778B0B363D27AC5A761E31CCECAFEF5133174C177257E87FF8795DECDE4C329C64C267C532074E8C3270B629EDE0C0C95EBABDFC9F131775253EED39D6ED5B8A2D5A
	366D30CEEC5B1BD6BD5BC8FB7D600D9D185F6B6549EEFB03CD36D15A0BB2FACF99F22671A44C789CFB37B4DFAE24E59AE5317EEF0ADC4BA9FD9B3BCFE95BE8BB054EE5F8C51F5447875B61B8D526507CB906C966DDC89DD2E688734BB6494E0778E19473931A9E7FB52774FCC5EC6D7029ED7DD4697FBBC42DB9AE030E996BB4468ED25F9B43DB7D75359E584FF4FD32CB5FF74FF4E97BEED5173E6F1E6F8A6F3BF5DD7A3E7BF5D7F85FE5F6CD6C3B2706553EBB2C60270EB0FC3BAC627DC5D2DF5DE9FD9F18CEEB
	7B206D343E8F2C27437B606CE9FD9F8C1C8E6F8371B4BA1F516B6CF7CA7474A63131F3D4ED63A10E2D7B6D0D281FE96CEDF576C922A02097C39C5B5FBA0A4F5A22F84B3A1B2D6B1F9BC4E36D21C4D8E78274A732B9AFDD18A8679ACB54FBB793037789229349BB18ECAE701B2D85AABCF9B773011B1D87C9GDA71BD6C7D548B97D5ED5DFAC0E96B14B1B57E20021FBCDE447CB3A0092E76035467295D7AB170C437F68C7C28DBBF86B23A4347404F3B75E3E0EDF7789858502D9D83C7AF29ED7FC431BD7BF2206D
	1FD07C831755B16377E194272E6710027FB2BB209E70BA516773B37ABEECBC236D4393E774FD58F1A63C8F3B4F687B3077CCF89F8E1E19F8ACF9A550D668D1847F039C35EF7CE34CCD39EA7C91855FED8C2C67B0456F4CD3177F3BD27EE781783FA97845DB5478CF953CB7DFBD66795B70230200F19F88A44FCD406FD9BD500FA65D43F25FD7B0AE9A1E63147E5559043E9190EC67C51F4CF17B1720F2671A585E60CAA8379A40887832A5E40F100CB7770EA0FB87E0458E6E4489C971D2443B5F44F8C7213C81A0
	91A09DE07D92A6734C855457D5A393BDFBF043574846CE5450F4BF1E4E37DFEFD337A14D9908CD9F603DFA8B486EDF4A6C1CA9329C5F33140DE3B5A758B8168A380C250163563A25FA6358C981691A35140CD7398A3E8B630B137CF8B20E2D99E467944A01B6A90969740240F4DA1B44F41A0FCC6458B7007E7B8A375E9A60FF83080C46EFD586E94F95856CEC4911FB7159ED7CDCF0AF0B036D0514FE10445D1EC296BBE3D0A686E08D4066FBC95B907F5E50446609627B2CC58D7731F8BC708D5A078ABAB758A4
	B97B6BECF6B30EEEB277BD07570408FF6CBEB67795B423BA1361B99DE06DFD6C2C6F68876C4CE46262055C537F2AFD56F70DE5BCA76BBE7A7E73FE55B9328FFBF309CC2B09496C81FA4C7D8897E5AA8D7257F73349FA18501785505198067A3C1F3EAE000E744DB32A69FFECD153110FBE237C4436A75FE736E5BD1944898FC4697E8E40EAEA9317CCEDFBEADEF6C9EC7B9652061F0978BC8C8C7CC71251EF9C7CB36A1ADCD2B252E12158405AB3B934FF31FE43A09A4AA0F80F5262137DB98269C327F05E128978
	7287D83F9AB0A35CFEF86ED2F00F19D9DF939EE0FD4DA2A0BDD314672F71B3BAAFCE1EFFB558C5695FDE1F7B83162137B67F1F8299FFFF271CCBDC85362C87A8DEE6AD2AGB0AFE37D6E5F8B5039EA55B1B4D872F9F92932F154A371DE74B9F6E59FD998525FD46055678176DFB46B1E87901651F3E874EA7843BC7A1B9166C2B6AEA682ED6DB2255F3AEAB87DB35E647595BB675D696B350A024F45071CAFA27DAC261075337287D95FFE9C4B354033G9075A0EB6F5810B3C7D756060BA364FB7E2A7948BC739C
	9E1B96BC981CC73F0071898F8667515B1B5978188C3432656C0CD3369D454598BC4782A48124812C83B0FE176DC5F0E7E159B8719E3DC57418A18194D45B8374FD0569EA6DA33175D8BE9B07129F7A8E37FCEA036E3974326F1218BAC22F4B5367E523AB31AF46016CF3GAD8AA081A095A083E03DE2DF73D9FA1E89ED2F544A322D4F013F24D0B60E1C7C089F0D129A22630F3B5571DFB2DF4849565111FB0868D67C10FF9EA0317977A1F6262086E86F90FFF3851C7F0F0C1F0F1E63945F7C221879267C6EBB38
	061FB5643C27A3F7144ACBD3490D6AB9D4C87A3B7B847ACEF8983A09334FA610342B4EA6350B02336FBCD773B07E1D8ABC36226F03390759B8B10E3FA7F4081273026816C3B9B94ABB8F0F37E8FE0902E7B2762FB23331BF6DE16D31DFBD7E2F06B29B81F652E7E30ABF2E09AE9F1CC75F6C79D3C6BC849C205F6F3818EFCBD2587C5A658A1EDF5F4DD54F0FB3DC4173AB1265272738026757A9817CAF3B0267D746CF62713EC1B43FB42730585BB50046DC2B6DDC688E9FA051D919CEC8EF51F9D0521FBA8A433E
	DBC9515AAB7FA6ACDF70BB8542B7AFAC1F76787469D6B45E0C26840FCF47B0BEAAA5F8FCCAABE463D3B450B6273071F8CDB3276872126A3E34795B4F05BF7AD6E510765FEA57DFBB3FEA57DEBB3FE9575E277D13FD62BD75776D7A5F1BD758555F1B13D87D1E00E2396B91EB118C908B1088107A880B5D076CA8F649491760B133315F0E566E6857E5E4A67FD6073E2D710E703FC9F30F832DE9FBC5D8843AD07D8CFF3F833517ACFAB0126016BB100FA4275DA11322947F28830D8982BF0051444EDF221F032074
	13CE749B891A7D912CEA01A5C4DB6FB93ECF36586CCA1CFC9EF0F6F0B578E763A393670BC46EB61C879B9F35966D86E8G688170GCC83D8G10843092E0B540E60002951063826C87288130GB8D63076489D5C2727BBA0692287B70AE4F362B374E48C351A519E0F7A3836EA8DDEE9ED6C30186AEB4DE7BB6BBB5A4C663A5A0E33351DE7CFB6B6B6B4B61079A8EEC4BD8F9E59BAD9737BFF84E3AB08DF22D77873C07A5D0E795389F4E8EE6BB01DEEBBE95156026DDF96363271BEED0556DC3A7118960E4C2F7B
	07B825BD5FF7295B13CB35961526CE5C1E447F4B86511E09170E57647DC66A6BE87D64F48BCEB7DFCFFEB9C911D33D55AF33D1F57FD46137BF363D782D4A207B2402FEFF4F904277771F633D919E7AA97AC41260FEA9A09F0B8383DEDE99D7DE94CA941C495BAEFA1DE83F0F2C793E4479555ED4B25F905CE90C2B21B8D2473B223E2EFB45703A9E9035472643E26858D4ADEA0FCDF522FAECA238A69145339D7AEE17D24E2C603ED2F0DF60BA916E1433DFF6A878D3D8AF04EF6611C065FDA556EB84633A583E29
	8A77957B4D9391799BCCF620EFF788CE40EB7B8127A010E78FAE63F4037B95FF78300ECE0FCD257785850774265FD8603668013698CDE563D811877D960D5EB856AE099EC8D94F93AC7E95869A535BDB557DB6966ACB8C53EF69F9G970B91144B91DC2D0B45884A8F491A5476016A1B986CE602AF9F5D6647B3B7D31BFF093F75D366E422AE253D04FF2FC2794686FF52CD32BD22437B2D21317BE12BBAF649EFCE5EDCB1892E099B38BC195B89337EEE764967EE54EF3C151515DE095B21FC7BE4428D35E67813
	2A5C976E484D610CA6EE8B89DC93E7C451E76272F0F01A38DC885CD17463EDC6B5F80738CBF8F88F4052EF84AE2B67024CA9FD3AF3C166E3F4CD74F870B782AFD7A29B1B815F7FB80B094FABBF520B891817BBDDB6C1001548E02D3B9F4D51F567E55E1F77EC2AC475FBD999CA243F6BD10974BE276279EFD0CB8788BBAB58245E9AGGA4D4GGD0CB818294G94G88G88G6D0171B4BBAB58245E9AGGA4D4GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GG
	GG81G81GBAGGG989BGGGG
**end of data**/
}
}
