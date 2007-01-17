package cbit.vcell.mapping.gui;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.vcell.model.*;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.mapping.*;
/**
 * This type was created in VisualAge.
 */
public class SpeciesContextSpecPanel extends javax.swing.JPanel implements java.awt.event.FocusListener, java.beans.PropertyChangeListener {
	private SpeciesContextSpec ivjSpeciesContextSpec = null;
	protected transient java.beans.PropertyChangeSupport propertyChange;
	private javax.swing.JPanel ivjJPanel1 = null;
	private javax.swing.JScrollPane ivjjsortTable = null;
	private cbit.vcell.messaging.admin.sorttable.JSortTable ivjScrollPaneTable = null;
	private SpeciesContextSpecParameterTableModel ivjSpeciesContextSpecParameterTableModel1 = null;
	private GeometryContext ivjgeometryContext1 = null;
	private java.awt.Component ivjComponent1 = null;
	private javax.swing.DefaultCellEditor ivjDefaultCellEditor1 = null;
	private long lastTimeRequestCellEditorFocus = 0l;
	private cbit.vcell.mapping.SimulationContext fieldSimulationContext = null;
	private boolean ivjConnPtoP1Aligning = false;
	private SimulationContext ivjsimulationContext1 = null;

/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public SpeciesContextSpecPanel() {
	super();
	initialize();
}


/**
 * The addPropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void addPropertyChangeListener(java.beans.PropertyChangeListener listener) {
	getPropertyChange().addPropertyChangeListener(listener);
}


/**
 * connEtoC8:  (Component1.focus.focusLost(java.awt.event.FocusEvent) --> SpeciesContextSpecPanel.component1_FocusLost()V)
 * @param arg1 java.awt.event.FocusEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC8(java.awt.event.FocusEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.stopCellEditing();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoC9:  (SpeciesContextSpec.this --> SpeciesContextSpecPanel.stopCellEditing()V)
 * @param value cbit.vcell.mapping.SpeciesContextSpec
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC9(cbit.vcell.mapping.SpeciesContextSpec value) {
	try {
		// user code begin {1}
		// user code end
		this.stopCellEditing();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM1:  (SpeciesContextSpecPanel.initialize() --> ScrollPaneTable.model)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM1() {
	try {
		// user code begin {1}
		// user code end
		getScrollPaneTable().setModel(getSpeciesContextSpecParameterTableModel1());
		getScrollPaneTable().createDefaultColumnsFromModel();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM2:  (ScrollPaneTable.cellEditor --> DefaultCellEditor1.this)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM2(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		setDefaultCellEditor1((javax.swing.DefaultCellEditor)getScrollPaneTable().getCellEditor());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM3:  (DefaultCellEditor1.this --> Component1.this)
 * @param value javax.swing.DefaultCellEditor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM3(javax.swing.DefaultCellEditor value) {
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
 * connEtoM4:  (simulationContext1.this --> geometryContext1.this)
 * @param value cbit.vcell.mapping.SimulationContext
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM4(cbit.vcell.mapping.SimulationContext value) {
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
 * connEtoM5:  (simulationContext1.this --> SpeciesContextSpecParameterTableModel1.simulationContext)
 * @param value cbit.vcell.mapping.SimulationContext
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM5(cbit.vcell.mapping.SimulationContext value) {
	try {
		// user code begin {1}
		// user code end
		getSpeciesContextSpecParameterTableModel1().setSimulationContext(getsimulationContext1());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM7:  (SpeciesContextSpec.this --> SpeciesContextSpecParameterTableModel1.speciesContextSpec)
 * @param value cbit.vcell.mapping.SpeciesContextSpec
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM7(cbit.vcell.mapping.SpeciesContextSpec value) {
	try {
		// user code begin {1}
		// user code end
		getSpeciesContextSpecParameterTableModel1().setSpeciesContextSpec(getSpeciesContextSpec());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connPtoP1SetSource:  (SpeciesContextSpecPanel.simulationContext <--> simulationContext1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP1SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP1Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP1Aligning = true;
			if ((getsimulationContext1() != null)) {
				this.setSimulationContext(getsimulationContext1());
			}
			// user code begin {2}
			// user code end
			ivjConnPtoP1Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP1Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP1SetTarget:  (SpeciesContextSpecPanel.simulationContext <--> simulationContext1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP1SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP1Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP1Aligning = true;
			setsimulationContext1(this.getSimulationContext());
			// user code begin {2}
			// user code end
			ivjConnPtoP1Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP1Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * The firePropertyChange method was generated to support the propertyChange field.
 */
public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
	getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
}


/**
 * Method to handle events for the FocusListener interface.
 * @param e java.awt.event.FocusEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void focusGained(java.awt.event.FocusEvent e) {
	// user code begin {1}
	// user code end
	// user code begin {2}
	// user code end
}


/**
 * Method to handle events for the FocusListener interface.
 * @param e java.awt.event.FocusEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void focusLost(java.awt.event.FocusEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getComponent1()) 
		connEtoC8(e);
	// user code begin {2}
	// user code end
}


/**
 * Comment
 */
private java.lang.String getBoundaryXmString() {
	Parameter parm = getSpeciesContextSpec().getParameterFromRole(SpeciesContextSpec.ROLE_BoundaryValueXm);
	cbit.vcell.parser.Expression exp = getSpeciesContextSpec().getBoundaryXmParameter().getExpression();
	if (exp != null){
		return exp.infix(getSpeciesContextSpec().getSpeciesContext().getNameScope());
	}else{
		return "";
	}
}


/**
 * Comment
 */
private java.lang.String getBoundaryXpString() {
	cbit.vcell.parser.Expression exp = getSpeciesContextSpec().getBoundaryXpParameter().getExpression();
	if (exp != null){
		return exp.infix(getSpeciesContextSpec().getSpeciesContext().getNameScope());
	}else{
		return "";
	}
}


/**
 * Comment
 */
private java.lang.String getBoundaryYmString() {
	cbit.vcell.parser.Expression exp = getSpeciesContextSpec().getBoundaryYmParameter().getExpression();
	if (exp != null){
		return exp.infix(getSpeciesContextSpec().getSpeciesContext().getNameScope());
	}else{
		return "";
	}
}


/**
 * Comment
 */
private java.lang.String getBoundaryYpString() {
	cbit.vcell.parser.Expression exp = getSpeciesContextSpec().getBoundaryYpParameter().getExpression();
	if (exp != null){
		return exp.infix(getSpeciesContextSpec().getSpeciesContext().getNameScope());
	}else{
		return "";
	}
}


/**
 * Comment
 */
private java.lang.String getBoundaryZmString() {
	cbit.vcell.parser.Expression exp = getSpeciesContextSpec().getBoundaryZmParameter().getExpression();
	if (exp != null){
		return exp.infix(getSpeciesContextSpec().getSpeciesContext().getNameScope());
	}else{
		return "";
	}
}


/**
 * Comment
 */
private java.lang.String getBoundaryZpString() {
	cbit.vcell.parser.Expression exp = getSpeciesContextSpec().getBoundaryZpParameter().getExpression();
	if (exp != null){
		return exp.infix(getSpeciesContextSpec().getSpeciesContext().getNameScope());
	}else{
		return "";
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
 * Comment
 */
private java.lang.String getDiffusionString() {
	return getSpeciesContextSpec().getDiffusionParameter().getExpression().infix(getSpeciesContextSpec().getSpeciesContext().getNameScope());
}


/**
 * Return the geometryContext1 property value.
 * @return cbit.vcell.mapping.GeometryContext
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.mapping.GeometryContext getgeometryContext1() {
	// user code begin {1}
	// user code end
	return ivjgeometryContext1;
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
			ivjJPanel1.setLayout(new java.awt.BorderLayout());
			getJPanel1().add(getjsortTable(), "Center");
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
 * Return the jsortTable property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getjsortTable() {
	if (ivjjsortTable == null) {
		try {
			ivjjsortTable = new javax.swing.JScrollPane();
			ivjjsortTable.setName("jsortTable");
			ivjjsortTable.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			ivjjsortTable.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
			getjsortTable().setViewportView(getScrollPaneTable());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjjsortTable;
}

/**
 * Accessor for the propertyChange field.
 */
protected java.beans.PropertyChangeSupport getPropertyChange() {
	if (propertyChange == null) {
		propertyChange = new java.beans.PropertyChangeSupport(this);
	};
	return propertyChange;
}


/**
 * Return the ScrollPaneTable property value.
 * @return cbit.vcell.messaging.admin.sorttable.JSortTable
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.messaging.admin.sorttable.JSortTable getScrollPaneTable() {
	if (ivjScrollPaneTable == null) {
		try {
			ivjScrollPaneTable = new cbit.vcell.messaging.admin.sorttable.JSortTable();
			ivjScrollPaneTable.setName("ScrollPaneTable");
			getjsortTable().setColumnHeaderView(ivjScrollPaneTable.getTableHeader());
			getjsortTable().getViewport().setBackingStoreEnabled(true);
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
 * Return the SpeciesContextSpec property value.
 * @return cbit.vcell.mapping.SpeciesContextSpec
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public cbit.vcell.mapping.SpeciesContextSpec getSpeciesContextSpec() {
	// user code begin {1}
	// user code end
	return ivjSpeciesContextSpec;
}

/**
 * Return the SpeciesContextSpecParameterTableModel1 property value.
 * @return cbit.vcell.mapping.gui.SpeciesContextSpecParameterTableModel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private SpeciesContextSpecParameterTableModel getSpeciesContextSpecParameterTableModel1() {
	if (ivjSpeciesContextSpecParameterTableModel1 == null) {
		try {
			ivjSpeciesContextSpecParameterTableModel1 = new cbit.vcell.mapping.gui.SpeciesContextSpecParameterTableModel();
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSpeciesContextSpecParameterTableModel1;
}


/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	if (exception instanceof cbit.vcell.parser.ExpressionException){
		javax.swing.JOptionPane.showMessageDialog(this, exception.getMessage(), "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
	}
	System.out.println("--------- UNCAUGHT EXCEPTION --------- in SpeciesContextSpecPanel");
	exception.printStackTrace(System.out);
}


/**
 * Initializes connections
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	getScrollPaneTable().addPropertyChangeListener(this);
	this.addPropertyChangeListener(this);
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
		setName("SpeciesContextSpecPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(572, 196);

		java.awt.GridBagConstraints constraintsJPanel1 = new java.awt.GridBagConstraints();
		constraintsJPanel1.gridx = 3; constraintsJPanel1.gridy = 0;
		constraintsJPanel1.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanel1.weightx = 1.0;
		constraintsJPanel1.weighty = 1.0;
		constraintsJPanel1.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJPanel1(), constraintsJPanel1);
		initConnections();
		connEtoM1();
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
		java.awt.Frame frame = new java.awt.Frame();
		SpeciesContextSpecPanel aSpeciesContextSpecPanel;
		aSpeciesContextSpecPanel = new SpeciesContextSpecPanel();
		frame.add("Center", aSpeciesContextSpecPanel);
		frame.setSize(aSpeciesContextSpecPanel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of java.awt.Panel");
		exception.printStackTrace(System.out);
	}
}


/**
 * Method to handle events for the PropertyChangeListener interface.
 * @param evt java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void propertyChange(java.beans.PropertyChangeEvent evt) {
	// user code begin {1}
	// user code end
	if (evt.getSource() == getScrollPaneTable() && (evt.getPropertyName().equals("cellEditor"))) 
		connEtoM2(evt);
	if (evt.getSource() == this && (evt.getPropertyName().equals("simulationContext"))) 
		connPtoP1SetTarget();
	// user code begin {2}
	// user code end
}

/**
 * The removePropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void removePropertyChangeListener(java.beans.PropertyChangeListener listener) {
	getPropertyChange().removePropertyChangeListener(listener);
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
				ivjComponent1.removeFocusListener(this);
			}
			ivjComponent1 = newValue;

			/* Listen for events from the new object */
			if (ivjComponent1 != null) {
				ivjComponent1.addFocusListener(this);
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
			connEtoM3(ivjDefaultCellEditor1);
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
 * Set the geometryContext1 to a new value.
 * @param newValue cbit.vcell.mapping.GeometryContext
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setgeometryContext1(cbit.vcell.mapping.GeometryContext newValue) {
	if (ivjgeometryContext1 != newValue) {
		try {
			ivjgeometryContext1 = newValue;
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
			connPtoP1SetSource();
			connEtoM4(ivjsimulationContext1);
			connEtoM5(ivjsimulationContext1);
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
 * Set the SpeciesContextSpec to a new value.
 * @param newValue cbit.vcell.mapping.SpeciesContextSpec
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void setSpeciesContextSpec(cbit.vcell.mapping.SpeciesContextSpec newValue) {
	if (ivjSpeciesContextSpec != newValue) {
		try {
			cbit.vcell.mapping.SpeciesContextSpec oldValue = getSpeciesContextSpec();
			ivjSpeciesContextSpec = newValue;
			connEtoC9(ivjSpeciesContextSpec);
			connEtoM7(ivjSpeciesContextSpec);
			firePropertyChange("speciesContextSpec", oldValue, newValue);
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
private void stopCellEditing() {
	if(getDefaultCellEditor1() != null){
		if (!getDefaultCellEditor1().stopCellEditing()){
			if (System.currentTimeMillis() - lastTimeRequestCellEditorFocus > 250){
				if (getDefaultCellEditor1().getComponent() instanceof javax.swing.JTextField){
					javax.swing.JTextField textField = (javax.swing.JTextField)getDefaultCellEditor1().getComponent();
					String message = "invalid input: \""+textField.getText()+"\"";
					cbit.vcell.client.PopupGenerator.showErrorDialog(this,message);
					lastTimeRequestCellEditorFocus = System.currentTimeMillis();
					textField.requestFocus();
				}
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
	D0CB838494G88G88GEDFBB0B6GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135DC8FD8D45715E733A46BFEEBDB12B5A9B569FEB61A1636A4B1297D6257F577A3DB5312EADBE5B5B53B18329B32B1294D26291B5AAD59BCE686E5A2FF86440436B4A1719FA88E7F959195E6068886158291D492F94CBCE0F41899E79EB0A0A6FB4E3B773EF9B3735E0C12466F7B654DBD675EFB4EBD775CF37F922E641DC5B9593A920E4B4E61683FA3599C9730F25C1E97FF7B8E23B56DCFB3F20AFF6F
	820A38A75A8C706DG5E86E71A51441D8C64C2FA905281C83F853FF7F236004982BFB464ECEA63384D4FCD3C94DD4EB1BD1333DDCABBF7644137GB084B47CA66FC779FF3523145263A91DA1DB47F173ABB04FFDBB4AA86DE548138B348E40E08549AF4217A8F0FBF3934DB7F8E7923765C7BEDDBE56232813E19DAD3B15FE4B397F4DDED409321E107444B2B5109E83E063755CF0CE83FCDB3A2F0E1E308FBBC57324DDF0394C633C576BF40F1AC7A71C4D4D3DDE416E947CD60FDB9482A2263AF93760AABB0075
	666765CA75F0DCA65419A4518293A8EB88FE87G898798BDE79A691B613761E01A316CA0EB5F717DADD47F016B0FF16B6A468D059A6DEBB8C85AB5F8B0583E55D7B66AFEAF7545F5835ABC91F8ABGEBGDBGA5D5E94686409FA59F38CDFFG3EED432255B3BE6EF137F93D961743F7466390DC703B3999B81456A358BDBE874711FEDE3523CAC51FE8A0651E3E65F244A6C9752C4F13FB9E62523FF30FEE3B0ACD52132E6B3A2A08AD9CD5C11B905945F9DA32632BC2FDB2D4F6BF27141DFAED3DBEC7C5F66647
	6B7509341EE4056CADD55857D9128F2C065FD88E7B135073A6115EF4A8C8A77E7909149F53C340CBBF44FCE320006952E5381FB355FED9558E097FB2DFB7E0A6BAF84D613ABCA1795DFDE6A2737732AE7FA651B7A868C4179D929D53A555E9468CG712F78EDB8DE13EB520C29008C40D6C0E9AD78972023167958AAFD75CD7AD8B76F93AD93CE17C370F124EC3C61CD7842B873711DEE3F483B6D42F95E676407DD021F23F93EE0C09B8C07143BCB72FDBB48B8A538859FAFBABDEE281D9F97C44167A7F96EB594
	E19E1FA0F17B1C6382889877F22C5E45061D70F571FE71C32F039705E08E427F36817BE412BF8FB5C28554C6C55CA35B51DEE3507EB8C002EC07FDB9D85F87028F22C5F3F31747F3F5425B07AD61B2EA514E15D26CD8823FD754B29BBF180B34BDF5E94616BAD64FC7B9450AFAECCEBB2A4F7B26599853CDE23FF4C3FE9130G108A4884986AA14E54B3DFFAE9007912AD7FC12EEA7D5D2A3EB4F0F93D3E251E78D0DFFD441812E4213E33403B8F3082108E48C2D98DECDE081FC03B228EE5348E540949B0542B0F
	BF066EBAF53EC8698BB478C476E2C9768EBA1E5838E6F9722348C568B568B98A99EAFC52FF2BF623AF34009D86G0186560709B965E4CCD8F83F40C641B99FB830608876A80D3553B51897F084F09C3097F094309FD089B848E11462B8D89AF06D00FA396D99D25984103B9C108148EDCCB356G86GF100A4409A40D6C079E1983707D91F95CD2744304D2DE0EF183ECADEBD7D9E216DAA206DAC206DE66D0AA523DF5387ECFF513698295F94CAF5C776BB730F9DE1BE023E348B7056CD485829C2A33E7345EDA8
	D718945C22CD708AEE877C70D35E5D5BD0B79AAFC7BCF219FBA53A97622B95821ADF26FFED9B6A4F0B224FB9BCA18A3D02A842C2A7487F26A44BBFB16C8D06E6A6EB69B6EC23E78A622B1C7F3B5BB0B659BDEE37A0B931DC04729F13E223609246C1E92BE742AD927ABFED47F564D5E11A46664737EF63B0363AA6B04E133559D29E6D6975B947E1E860D441259C4638591AD72A301197E83846DCG5F2443AC56CD6DE631EE4F4CB739144BDE1D5A9C1E784017F439C7C8ACA9BB920CF5C427C7F355F5DFAE45E8
	CA7793DD9F7D947BD8E329A87941CEAE3F30D8B58FC779AF766C5328034EE35264A14D23352CCCD131893E230287B27826E921DA423B770FA84FA60C7093AE518A6B5AF607D374786432F7F53E01E34DB36E753821A5B23DFA924B791D6393AEE926E4353298E758AB45B830D7B7E06C880BF1196FA11F4DD3B438D9BDAE97F18D602DB9027DF7E932045ABF8E5209C7C87FE1FA2C8979EBE5BE2234A948A765DF1379CB01179A4177CFB1FE9670CA0E0673DF1079B6608DC670AF87E47D0017286013F62E5D0DB6
	778B57A684D81EE01FFCA07B71065D45E13C73542789BFE3B77A34CB3196F879A84AAC1BE1FB212423E4EECD21F2579CA53217B4E33EF5F23E16E632EE68032F8B10502C4C7740F5F9EF55CC6A4BBDC6666B52E3F43DFC8C73CD4D32FCCD47481C51CD73E1FE33DC5F419BAC5F52E3C4DE6AB1B26F270468779BB9DF06021F2548C7647EF5B64B377598191B3AE9FED38B11CB6CF2DC4772D53410FADADBC26D12520279DE177385A8BF21052CC7164277EDD93FA775ACDF5E71A09F57B0788D6A77A5834B3767B8
	19ABD1BF4C5FF2DCE91753B9AC5F0022BE341F77B8290F4407DFD7E24C127619D778C9BEE076CFC15CEDEEBE2D58CBFECBDA8FE27E0463EC4F78D4A10E35D4C8E7209E47D9FCDB1B4F760C8B2EA6F1CBFC6B74DAFBE2538922C74309E0FC2B9A1A521331FCF69F4A940197FF020D63CBFB146B56B63F5FE3F7CA01007A709BFB15710F508A77E2AC99F17A7CE2A7CCD081666F3B76223E382626E43A4738CD5A87E13CC98439EBCE307DF87B3B87B94D7D385ADE1C6E596C754C9E89B38FF0C3F7AF546D5030C76E
	C9E2070A13C1FB181EE95093797C9FAC5A72354F83A282B45BCB5EF8DF59CFAB8AEFE86AB5C47509D36AF576D5BD31571EA9E62F9460EFBD496CF546D1914DDE27C2A786D257C2392E1AD6085F2D2C2E03165D2ABE9AB10350BEB48558194AA228E303DC4F7333A13EEE66274466E6F91AA1E56F88B09DCCED3086854435A17D7B81DC976058A5795AE7726818D8897CACG192312E7B01FB86624736F9DE72438ABFA3CCCCDD03D2272BC6B077BF736D2BB2FFE68FE2E65530FF5EAE7AC738A47F48D96FA46E289
	76C34A5A0D3AF5G2253EBB3CC77A44813E1E1BAB92EA35D6986593C4BB9A3D4D412754A06EB4A75CA2995669CC09CE00115157D62F5EC47B8F19D2A7303EFABF54E65EFD755F97E8B233AE4AB51F51DB528F3C55AC6DDCB9A5379A757199D4BECB0E758185C46D9E6C7127EE8D632234A9A055433E81635FDBE543152467A3C6484FE25751B2C7F6C8526FF5C6AE5DC694EAF286AAF4D53B622771EF64559454FB76AF2B3187EFDB7187EA264C9EEE77A7F638D0CBB7620EBD2BD4BBFE1FAE6C25E327719BFA5E7
	B3FA9F50663F4F6A592BC31D6D4CC965B8B522137D8F72E64979FF2C43F334910FFD42EF167E5B65710B954C5D4D52E25D7CBEB25A71E7A52B2FC24F64B71C02F974942B6F69G2E4BA356DC9CDB13FDC74F7A09587615B625EFAC5FBEDF5536E35FC854ADB9C5EC3A7AD4503649E0579459362F48BA15F500EFF4B0DBADB748FD8D34159DCC575A9C14953666246D7367B0FBE5C17E06CED6667FF2345AC776EB8FC71C5539BA03FB7C384E487DBEB943BAEDC2F98BBA499916DE2677C974A58ABA51AF3D1035E9
	9970CACF33F39727B4BF4FC2BA913094108A5884487D004D0BDF5D0B7B562867DA1D50AA1F1BF77506EC2068DC583B8F65421E07267B77A9478C31433CC934431E8F221F0FA8C14A5DA315EB3905F2245DBFB900EBDD97141B87D884D88E58A037B9FDDFD52CB63373B95AD8D2EFC395563B956AA969CAB3D681ECGC717FCB636BF26AD5B832A36A46DBDB0B5B7BBB5CD5D2A1DC839C5F31477509C653D99185BF97B1EB9161B9F18DB7B9686EE35FDA4FED5DED45E69980ACE6BF0EC284D8D71DD744C2FAB7CAC
	72031970362E6B8A55636530B4A9F7A1221CB9ACDDF2A6349C7169278E61DA3F8AF89D00A140ACE091E085A0638C736D097DF816955D37F1D26873D8015681FBC1EB7879092C2B7EFA2C7ED4EFE3D14CF25C5950F2E49EFF2C3D11D3D9CF8AFEBFBF0AABE05EB16EF41B7D9E1FA86218078DCCAF7CA6BBD90E1E4F2C75042CF7C00E8DB0861885103D0E49476E4D961CE5FB1DA7C4694E845219002C33ACB61F29C63B171CCBB356GBAG8E40BC4092C06AB9E6779F6CBFF613F6EF3353B9A84C6A44F66BA2ECF7
	F3FE751F91655E8ECBEFBA27D66E6B33F36B63E5B14BF15DEAFE7C639A34E7A9709AGDD00B140FCC092E0F5B733E7E905A516BD03BB9FCF9BBBD3D4F1E6226F459BF3336BD8C439F03BE6F62B155B77495C6C5A9033DC162A3CC73367A6AFA5E63912BF2B15F36966E64F1908F26630F4152ABC33FEEE6DEB09D94E76E7B5BF3554211F8E82AFGC8GA48356GB683CCBD4CCFDD317DD4F64E1B71532DF3EC67B6C32CF295BDEA657EB9E6B9F5F92BE316EBD515B71DB3B73F791B08F26163F0A0CC9E39677C01
	F42E6585DEBC00509697A0AD8952A9BD6C7E295B04FB1CCCB9FDD1CA635CC163EB61C01436E6765C7C9AC48DC4466F224A503A9B3CD990FF75D5EA5F67CCD72A5F67FCDA29FE1FF3FBE59121C39D4A5BF4792E3CB27AFD4F5D15EA77BDFF125A7A14347775CD38C56738A009A5EB84A100EF01E0D475CC384910C3497E707D00AC6329835A6D5DF8C03D3D4F9ED0EF6F8B87223777D78722373725CADB97EB153AAE1DD56A3A1C2B0A2ECBFF1516AEA43E455523FFEF156358CBD5F5DCE49CA3679A4C38FD826FBB
	B7B2A287B1F2B6C37A6B49B78BA5FD7CFE011CBA58F811273C67C53C070C3827BB8B33B7474E98EE1BB152BC6D2247FAD4EEC746A1ED1BBDFFC85DE63FBC046751FE4F044FAE045BE54B21D219978C421D22B00E6A10BC2FC965C55EB7AAC45855E0AE15F9DA65734DDA36A7EDED3E2EE86B19DA0E2DFFC75B51DEAA67743DF61F47654253FA0E2DF37F4E94FE8F0977B1A53DE9460ADE364EB5FAF3E9ECE96DE56B5C5D57F0DC85A01D0034DE965FF67922457314DE2DB31023525B21F53D642CA3DB26DBA4FA06
	02CE4EC0D6096C5CF6B3703A7B187F75D7EB777347556A7D6C28566E672B55317BD92C566E676B55317BB93EBAFABF6F18D5767391193EF1F68725F70F78BC635D35D4664FEA34ED70CC0D3A8DB2EB34ED700B1A58B658D223ED035F5544360121965BB26CF4B8FD74261534714E9B4A365749ED7F6893A53DDE263F109DEA13FACADF2CD34605A6B93F20E774EE5153DDAB6B5CD527ED435EBAF59BDE28533661D0DDEC9BBA6B34ED682D0BED4369BAF59BD69B1436EA105B7EBF06D0DBB5D07A529CE57E46203F
	65617B8697BF6D1990B92E7CC39C07DB64F78C4BE08C2E86EC8164FEC8628809C70FCC211C9158E501DBCB41BECBAA7B3D82D6F68C5233007B309CA07DC3F6663A71930CAB64061179F7FCFD3EF23C5078CFE25577CD6C7E51F61E452AB28F460AD9C8AFGA41FE7312A404BE255CA20EDB89F9A1BB61F570AD5DF16E29F579FFEDE7BB509EE6A8FBF2F2D3C464EEB4B01975F4F560FD5BE36FE5C544FF4BAB901D5135F9D0A5FEB45E8EB3A4F8AA263454FD58631C5D9BE0B5BF983E9C65BG73115AA15CA7055D
	371D72B99D96FE34CB72B8523F0E7CDCDAFEF600756FDDD21D4BA03D9A10B9406E17837B583DE572576F67925D6A774B99FDB7F4D98374DC6EC270FCAE634AC69D1119D140E48E82FF4185249594AA5FF7E05F7EEDB6590392FEEB889F637B0D903EAF040F7ACE9B147CA5A1FC349146B8B3474E9ED76DE5ED4BDA3514F3FDB7DE73BDF9BAED93F711FC93B23F123D696199BD6973D4D378FC5DF4B16877DD973546C002B42606AE060F0105925DFBB1FC8C3C67E1E3A08EF81997599858AB2DADBAAE2599DD1798
	6D9309968769A41936414B464A0ACBEC2CFC759A2EBDB74969EFC8E9FC57C1525557147E0C797E72E326A3C446E75DDB050344C92B0F45C943E01A3194508498G8486D9DB5F4B1F5B19F94D9C4BB9EF399C718FC7817AC162E078BAEFDC22AF998CDF67A54A71E2A970CABEE26729C79B51A681C8AFGA483D682B2815C47ECAD1857A03DC10DF626526771F641A46FD2ECFDC87B1EAB185B1B7F473733F1396CFB5F62165FE153FCABD172B1FD53C23F954FFEA59B3F24FD2B8FE37B9C400B83AC812CG2C81EC
	125B5757C06F35F0FB853BA95EBEA63DE611B731CEE1AAC84609170C251FCBF3E49654E33A4C46D749CEB63E9AAEB31F3A470363A9DECEAF1252F86FCF52951E4FB2DFE03D317D2C6DB34908B59E499A23450B6F9EA7851F683473AE32C932703EEE0F4BE91F6652AF23FD7A2578F388F05FE536B6395B0BFDB56671B9E7BCEEB132A4C74BBEA21545B74165C3E9C6F188E97F6DAD0F22575090318D11735399DC0B86B7EF6426024CCDCE9F1B1BD68C313969B5FF785CF4E7485CD3AD064FCDD8BE4867457039A9
	347C5F0B61F393ABCF7A6C8A1269EF3F6277BF64FF7EEB1847BED799E45CD99A3577861D0D6AFB03F30D6A67AF9FB6C6BF6B7928D17BAD3323D1791619440D62A66C1BAD68E3BC0CE5C087E010E7B163154677B8A9E628440BF647A86ECD70AFD6C4A6FF7DE16D36269F0E7DF7AE4F9D4637AD439ED1740CE37D721E72B076176871CAC4C2FB79B05A48679C9D93C9D6CAFF75B07A38CB981148E4CC3CEA0A5C0B649731F9B2G6DCDGAC872C66495DDD55307486F4270D664964A3638F7BDB92BD45D648138765
	F651F479F0F0FEA2FABC13036D3B628FDEA0923A5B0BFBC945710BD9F17AE2BE5F5E5357E9ED6B3A5CEB6DB95755E5E96B395C56555FF62117E3734822A9655DF2539B37293E7DC60843249D7368B7EB5742EC22C32A37BE0A8E9D67FABAAF1EBB5B272685A91FF78DEDEC9570BD8853EBCAE073DB593797F3E56F346A0AB474CA217AEC9E8E2E290363F41D7FD667D7628BFFE8527647F71BE27B6301A6695DA778D15F34176E1B2B1B301D1E11913FA07BFDC313C926F57A3BBDFEA7EE28491A6497C5ECCDD2EE
	07F5181D6DE17E4B0CE54376B0968F3EA1A7DB989A336B0BD94CCE3033BD8CDFAC3D1105F486E02B1D1D3D8D6EE47BD913A34D780E249B4765F26C5F4BA755E89D8E7C3B57B5A7B9591F7EA3CF59EF361F59F43BB57AED3E0374D73AC37177196365742D6D4AD614D3A22499EB8496D3FE58046FDD4F73D7A2CFFB01586D73F8E1EA1DA6D469A5B90DD53FE9AB07EFB7546382A4G12GABG6BG1BG5C881311FF34A20A8CE2E4A9F679F827DBA4BE762B22392DEDDF1FE3392662392D0957CBFDDFB6923EA6FE
	C622D70C042F09C70B197FB5GAFF1042D47781DCA79D9C0B70DC6DFE3915F7869A5E52CB1ADF469183C707CFBE8FDB523C15FA0FD793095632A8368F100A5009500B500CD00AC00E9AC4D3887508268830C81E6818900C500E50054B1565F3BDAAB2374B7BD9888F6B847466D132D2817BC1F92F7233CB52DB8B74AEFDB25B7FC3D1051BDCA78BA0BF6ECB1DAE24716E20B7ADCD7EA091C6B7EE4D11F6BF6DB14F39D21D550FAFD72E587211BAD3867A7F360995E4D0FAA5E5C7CBBBE01253FB3953F470BBF7FB5
	589D1F2B8C32DF89C8F1B1FD0C0C8DC23BA151360C857F9638085B05FF02C76D15E2592B65D3307AB98DA17B5C7095418E937928DC667BD6ED1F7811B536CF7C2BD55DA71E36C67A44B3D6F51F4834C67A448BD61C93C6E19C8C4879DE12E997E45A2F25BA11566FF408E3B27D3712DEC86F90D0A0233FAE69B5A5518658DD15ADC83B4046038D67CCE872983ED7A634829B6E8FC71CAE979D8FD29E279B26D0599EEF59D0C72717D768DDA653D0EF7AB78C7B946F50A555893DAC109F24B355B9727F0618B47640
	B77917DC0E5F47954A6F18BBA585B2A66B1B754FAE566BB8C3A7370DF4C8A7E7C02BF6F2B91251BBB9BDF4C8G7F7056908A7A5E6FD3E51C5CFA3BDDF3CDD6F6054649AB61F1F285995FC01FF7C5BE27309C0A62173D633C4B853B4B2BEDEE877A28E5DA94027B0347ADD83F1F6521CC42CB15E24C95C5197F87D0CB8788D1CFB26BB096GG20C3GGD0CB818294G94G88G88GEDFBB0B6D1CFB26BB096GG20C3GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586
	GGGG81G81GBAGGGEA96GGGG
**end of data**/
}
}