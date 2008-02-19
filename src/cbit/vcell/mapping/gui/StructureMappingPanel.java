package cbit.vcell.mapping.gui;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

import cbit.vcell.mapping.*;
import cbit.vcell.parser.Expression;
/**
 * This type was created in VisualAge.
 */
public class StructureMappingPanel extends javax.swing.JPanel implements PropertyChangeListener {
	private cbit.vcell.geometry.Geometry ivjGeometry = null;
	private GeometryContext ivjgeometryContext1 = null;  
	private cbit.vcell.mapping.GeometryContext fieldGeometryContext = null;  
	private javax.swing.JScrollPane ivjJScrollPane1 = null;
	private Component ivjComponent1 = null;
	private javax.swing.DefaultCellEditor ivjDefaultCellEditor1 = null;  //  @jve:decl-index=0:
	private cbit.gui.JTableFixed ivjScrollPaneTable1 = null;
	private StructureMappingTableModel ivjStructureMappingTableModel1 = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private boolean ivjConnPtoP3Aligning = false;

class IvjEventHandler implements java.awt.event.FocusListener, java.beans.PropertyChangeListener {		
		public void focusGained(java.awt.event.FocusEvent e) {};
		public void focusLost(java.awt.event.FocusEvent e) {
			if (e.getSource() == StructureMappingPanel.this.getComponent1()) 
				connEtoC2(e);
		};
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == StructureMappingPanel.this && (evt.getPropertyName().equals("geometryContext"))) 
				connPtoP3SetTarget();
			if (evt.getSource() == StructureMappingPanel.this.getgeometryContext1() && (evt.getPropertyName().equals("geometry"))) 
				connEtoM3(evt);
			if (evt.getSource() == StructureMappingPanel.this.getScrollPaneTable1() && (evt.getPropertyName().equals("cellEditor"))) 
				connEtoM7(evt);
			if (evt.getSource() == StructureMappingPanel.this && (evt.getPropertyName().equals("geometryContext"))) 
				connEtoC3(evt);
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
		DefaultTableCellRenderer cellRenderer = new StructureMappingTableRenderer();
		for(int i=0; i<getScrollPaneTable1().getModel().getColumnCount(); i++)
		{
			TableColumn column=getScrollPaneTable1().getColumnModel().getColumn(i);
			column.setCellRenderer(cellRenderer);			
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
