package cbit.vcell.mapping.gui;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/

import org.vcell.expression.IExpression;

import cbit.vcell.model.*;
import cbit.vcell.modelapp.GeometryContext;
import cbit.vcell.modelapp.SimulationContext;
import cbit.vcell.modelapp.SpeciesContextSpec;
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
	private org.vcell.util.gui.sorttable.JSortTable ivjScrollPaneTable = null;
	private SpeciesContextSpecParameterTableModel ivjSpeciesContextSpecParameterTableModel1 = null;
	private GeometryContext ivjgeometryContext1 = null;
	private java.awt.Component ivjComponent1 = null;
	private javax.swing.DefaultCellEditor ivjDefaultCellEditor1 = null;
	private long lastTimeRequestCellEditorFocus = 0l;
	private cbit.vcell.modelapp.SimulationContext fieldSimulationContext = null;
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
private void connEtoC9(cbit.vcell.modelapp.SpeciesContextSpec value) {
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
private void connEtoM4(cbit.vcell.modelapp.SimulationContext value) {
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
private void connEtoM5(cbit.vcell.modelapp.SimulationContext value) {
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
private void connEtoM7(cbit.vcell.modelapp.SpeciesContextSpec value) {
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
	IExpression exp = getSpeciesContextSpec().getBoundaryXmParameter().getExpression();
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
	IExpression exp = getSpeciesContextSpec().getBoundaryXpParameter().getExpression();
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
	IExpression exp = getSpeciesContextSpec().getBoundaryYmParameter().getExpression();
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
	IExpression exp = getSpeciesContextSpec().getBoundaryYpParameter().getExpression();
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
	IExpression exp = getSpeciesContextSpec().getBoundaryZmParameter().getExpression();
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
	IExpression exp = getSpeciesContextSpec().getBoundaryZpParameter().getExpression();
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
private cbit.vcell.modelapp.GeometryContext getgeometryContext1() {
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
private org.vcell.util.gui.sorttable.JSortTable getScrollPaneTable() {
	if (ivjScrollPaneTable == null) {
		try {
			ivjScrollPaneTable = new org.vcell.util.gui.sorttable.JSortTable();
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
 * Return the SpeciesContextSpec property value.
 * @return cbit.vcell.mapping.SpeciesContextSpec
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public cbit.vcell.modelapp.SpeciesContextSpec getSpeciesContextSpec() {
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
	if (exception instanceof org.vcell.expression.ExpressionException){
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
private void setgeometryContext1(cbit.vcell.modelapp.GeometryContext newValue) {
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
public void setSpeciesContextSpec(cbit.vcell.modelapp.SpeciesContextSpec newValue) {
	if (ivjSpeciesContextSpec != newValue) {
		try {
			cbit.vcell.modelapp.SpeciesContextSpec oldValue = getSpeciesContextSpec();
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
	D0CB838494G88G88G6B0171B4GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135DC8DF814551566A7A0EA1408C1422F818483849AEA56E6AD3B4DD6F69F3A46AD2D54627344152E38C69F6C26DDF617366C531B4904A4CCC202C49B95B52E00C1E38A12AE18461ADF48C0E226100D01261A4964CBB2B81999E73E497F4F1EF3FF3E79B273FDB390AB4F737A4DBD77671CFB6E39671E7B93C9511B31D99906A2C2B232887FF7B6031021BAC2E2DEBC3EDF50221CEDA6227A779660B0F95B
	E704EFAC641D5A5CE6AAA477F465C0BA91525B935ACC3F015FC748522E42DA782143A72D06102793DCFF8E4D677C086013CD53FBF27260FB881084B47C9ADD485F18DB4C69291CAE10E1A0E4B7AD73E66ED1CEEB04F4A7A0961068E265D3601BCBB94F29BC474BF55C95C7767E2347100F6D285A94A8F3333A967EADA13F4C5867C6DE0D39F93C4EBCC8A78330731924AB2B823ED516AFFA7FE06B324B6583B64961A86F373A5DF6E7EFF92F4FDED9F945AD596C12374E651425A199D3962BD3F248DFE23BD139B9
	349DE8835244033499C348AB89525B81E98ABDFF8C6975703B83A0FBC47FBEFA270A4B5FBE7AAD323B776FF38BF47A9769E17DCA74787BD772EED766ABF4AC660E224E53A12F485BE6AA85B4819C00C840EB54866E98F9973EB5DDF21D2B3F5F652CF13BEB9D5D1EE6D73764005F15151043E917A51B4B53CD889B67CFF24BB4648985D62F63266BB11D441D91E39EFFF993C9FD609EC33606CED263C68D77F919AE36F87DBAE13C976A72CE75CE3549293CBFA5EA5E64398F3233B4F89FFD7C036CF45E0ED17673
	EE12297D8F63D89F035FD88F4713510BA8BDCAC5E776B9175A8C2613A02F42A7EC237D10102549380A941EDE242907186FC69A92FCCC06ED3EC0D99E25F6375747F83E224872AF14FEEA40CFE7329C21F4CCBBA06F5CE01B0959577CF11C2F4621B6D389609C4082F083A28131C342469E9F796D8D5A18456A11EBFDF6C73764A12C6E7C5117618B734CE3B5BB3D3255E9135A2C9E3B354BA1F989AF93B30ABA681AD2EFB935FD9B7038A0B9A50FD5363B1C503A35DF12A50F1715D9BBFA984BF8A41A5BEA6F1700
	C13F1B08F6B70D9E41BEDB3D727F393B2D3264AF41729F9C45B1993036C10B50G5751E132E58275158C7D5F89C8D374F0FA8C5B3BA4F940DBD4D6B639DCDF785C2D5893F2EE98759CCFFD47FEA8DFB4ACF41CB40934B1C8C78F0BF67A46DED435D3EF372178D64F3018E38707F1DC6220FC8AE08F20E4244DD48568G48A34216FE5AAEEC29BEFFA3A97B51C2CDDB0A823B0E9EE1B694BF92B827DE253CD05E9D103797D0B48A768AE8854823E2DD089CC23DD6F17E340D917F1C6B98511EFF825897ADFA8A276F
	5149E73CB7D05EA6BE1F443C96E572C370C5E46A5033D4BC347259781D48C0DB0886BDA4G360F0AB1DCBDD64266C42D55AB09D950628183163A7DA34A64FFEC6C9416851C85FC84B88E7890708E60A460BD02DE0A90888DC883600C5277B4DAB78D78960E419C85F482668192GBB81E49C56860085B084089D97E3764AD8F2985D5C8C0E87482B4EBB433FE7F93F8E71FE9E627D967D8A4763D3DD9BE87A0B76A5175BCD81EDBBF85C05FD1882EC84ED69A540EFEE0047918D9A331D456348D79A101CF23D6416
	1C5D70434B73E247D1B66EAFFBDCCA1D35146E867FDA878E4D2B506347D1FE2BACFB6CDDBED93AA249B284BA7E7CC44A4B6B6BF27BDD3360756DF16C23EB907C2BD2BEF99CFD134D65F4CA540815AABC7F3B54B7CA8E299F042EF3791CB223EF1B40B8728BE9187B6614098384FD2B4307FE1E45E689D654275BE36F072901CB85A91EC03F793092DB52758E6C97E34CBD70A59342578D3EADFC5D3111F5A479F337C1EB8D0FD9B35F50B941FC09BC6177F5CC263F1954167DE16A23B95D4BE47D66A40E31CE28C8
	6D608849AFF8D1338C61794FDFBE215386DF476862C1575153224E61978B615BAB392000E718D7BA4D72163E067C6A259E2B4FA157C1DC5B50ED17DD9E256E8273C11CEB2EFE374B89BDD1683F9D40FADEFB3F4FC1D7CA512A70F11F9B280F83FD458112A7040F7B18660BF50ABB37BA174341CC8372CAA7F17CEC43C5DC7FBB21EE7A849BBFCCA7537CA714FC04FB521F4F6AE70D087C1C9930564D884C9FD572DBA14F91147FB3A5FF9964A58665770EAA72C1DE3AAA1F7533AC83F56E153E74C9901E6018DCD2
	6C78CC460B81F9ED5C26D97E6FB350269D2AD820F1A672BCB6AE76C2E4A6DBDB0BE7B23E25B3996F7D34DC2AD2AEFAA60B9B62613B9510B6255C4689E5EFB50B3557B90B2D576ED9BCDE1E0565A6A7C53928D9EC4D08636530FC39525E99A24A654CE67CCAE633F53FF88A5FDFAB654E2972DBD565985F5B0D221CE5B6DB1B10AF166F1B4D78B23DB4E409F2B3E633F6E2E6CF55CBF184164BBEA04AED6779E933D9BC128379EFA8727DC0A9579D614FAF62DF3FFC0B32C539318836D622FCD8BEBAC22D174BB922
	DC022ABD545FB65E9E738F7FFE92FD965DE7DE378ED8074A3D0360F7ABABAF2A76126F9F4C616553A2441E71EABE4E3512B9301683ACF304FF7BC13E58B3C6FF91C7D21FBB1D2D37A76E1B4364081C6377EF097FD6F3004D65F51948B38572D26708F97CE006BAEE2D71FADDB6BBF5845C06A5035A7FB15AF5837A129E3B47AB1BE1019A92766EB6203C98D3F3B25FE35CC677C168EF5201EF69DC319FEFF86BBD223B9F575A0B73BD1B6D0C50C754489A5239F0094124230F4E39CC8FB3667A75D1B631AF0B7137
	3EDA2B4FDF7FBCA048C10B3D6478F975B8EDA9985715AB094B33D3A557317E56EC262F3291212F62DB408EEE917AEA6EAE8D252F8BD397865656AA252DF950CE325256FB35EFEB5AE8508A424730F8D41C296C03B6CE4D936DBCBBB645564B2D03F2E525324C303AF70E8A997A20DEACE067BC24BFB20AF1814EDDD62EF9BC0F4F092337427C8730B5AAF19C4B49FDF63E7E4EED26FED7F6390518A0FA6949B6B18E2B0ED4F3BD27ECDAC52AA6BFB3E81D314CAB68B3C45E4A4FD8EE750FC363022E4C8EG13A9EB
	C248CEEE03F9F91B1049C169F6A77036BA6CA3D269C9962F6C1255710A9B4AA782F68276A8F56F1940FE74B35361B2EFFCC3ADF30EF50E264C114F759A0C37B3D94BEE774B1CG72EED5E4FEFCC268D106B271378B3E9FCD8ABD3234B4C97528912330F662A6C55FF7C39BC7A34518977D813FB4FED3649F3BAA641F1112C80A0F5C21A9BFDD27A3195CE311FE7947E23BB223E2057C9DC4481F82E50CF788799FA668F7ECFE5364F216E688B92B202CFC073027C423204783ED37524E7BD9A833CD982962271CD9
	0A7D5D8976F727A87F23ACBCC76BF159FC5EF27A5FA617D7AE9566DECE03757273185100BFCF0A76CE9D907CA3212D2DCAFBBF21E793C1B19791B159378F08F1E23A7DF90D5AB61232A3B5F55BF7FF0CE17F1DCC2747667BF5DB3132AB33F125506DAF9519E4A893BFDF68AAB9DB99EB209D0D9232FE18033C82E2CE5E3F519C212FD6A89F2954B9102B57BF36DF7B60E060D95D56A87F9EFFE7D470FE1F1FED5175F1CF94BB434AD468BF26747DAABA136F73FC51273C3BA0963ACB1C3B7C228667688EC82783F2
	96C0FC832886F4AE906B62BE836EDBC31EEB1921D79E2755F1E54A86CA1CAFE5A2DF58737074B7B355F30669A1EA9875B036A07479089A2C5E8ADAAF7AA66A31FEFFD607316ED628378B308FD0F8B778723BC51F4F9A4B4275D91C4F714E32F63756E33B96E8478108GAC83ECD15A1DE98C2B4B06A1CDDDF2FB99191E1E2AC7EED6CF2CDE5CB479BDB8CDFEEF0CCE6F3C3DEC1A7522C727573FD523B75BBF663FCEDED35F69988FDFB4605C50DA9BD26F66E7FEF7871EC5DE998F6CEBD974D4B99A83522C1EB528
	DEF9C05A91D00F59F44FF90C75A3A0AF96108458815887A8DA88F140C2E15BD955F816955A36F1D1E8F555812D917602F50167A70A2C2609F063295D47D74256EBDFB8359EDB473F5570BB2291CFCADE2F3597A3E0EBF73F5DD96EF5F9E49967BCECE02E40EF3613A57CFC66894F14F88778AC83A483F6G58DE2758A76E4D76AC94FB1DC787691D49BDED26AAC06BBD42B7BFF981756E0074BCC0ACE08BE097E0BF20E41150FBDB756FEFD06FB5B63E8685E81D69AEB5C8F7B7E6D74F8755FBA3A0DD3DC82B5E26
	49690D711630755A97E959711F4D28CFB764C5826E83A483F683C8CC1B69D80C50E7DCF5EDB8FD7AF7BE2E9AF12628E14CCC5EAEB2BD3D3A036A856A35AAC62BDEF9467474DA9536DE2BA63F070C536337ADECBD07A6BFB9EBFA7A1C95D42FBCA09D21496FFF8FCC2FFF75E16BAD0B5132539FDEC4BBCD043C6D00B400F1F11B2994D08F68DBAC6CF4FFF8BBD50C73C66C14E4CF2F1F0742561B31D82B5EBFCC135F0E3075E2B4794D4C191E5D4C8F2A97B88F93827831FBCEB3BD575A86F929GC6BB1107B43224
	4DD43CC45CBFBD160FFB1CAAA57DA4CD635A4152D2FEFB083EE23DD03A888746E3A2A48F3D3B419B8533D7C32D7EFDCEEE2D76FDCEC12D76FD4ECB3507999D5AD05F260B7C57EBC35F779C2F553A6FF91D7675F1FA4E6F71B9E5FB3FC4593298C19A4237C0B02BAE7B1CEC4AA1E7EF60FEC06151D3275F5F7EBA6D7EFA6B347BBBDC973A3FB36BC277B725DEDF16476A35E5F922DEDB16276AC34B72EC3D1EAC4C3F95B621FDDB1688BFB65170818976E36CDCC3A837D532FADAFAFA94A7464EE658F8FD6F6582AA
	0F57AB31D307FA2BEC65F94F4AF88F99F4CF77892C5EC41CB15CB61E4B4BB4482E3A8F15FE384F6B6BEC703C364E464F63F9345765735824C03DC41CAFD6727CCE58ACCB7DA88EFF27C46B4BD6CF2F942457E8DA1F656955DFFEDECF772C2F35932A3EB61FA6A27E6DEDC0FDE91C53DF31F9DC8E871E569391679EBA94F88F0977B18E984F99CBC41CFB581B43FDCB4C9291673EA7633C5A8E69B424AD957E6DF5DFA8FFDE3CD46F8C24063E9DAADB4A4EBAB2947AC5CABF272233B31047864539EDBD6445AD9576
	776C857DF17E69856DF15EFBC1FF1CFFF5A17CB89B9B7547595498FE1C0B9AC30F7311C975B81FD5684FCC1AB85D5263F175DBCEF31E3DE6FD9DB84D5ABA104D7ABA98B50757C104D9DF87371B436BA05A0CFD6932F75BBD7C261575F1B1D1777D83256FD2061AFEC6217F4CB8D5A7E7B8FDC3165AAF1CD34A779F90F40B6C321CD6E4CE3D282F4327AFEA6B70390B7ABACC3F98DE073F38282FC3F2A93C8EF3AFE96B70EC36DAD795CA5F7FBBFB2A2EAAB8BDA9C7DD7EF7CA794E03783E41E19DF679E4C2061661
	BC9CBCA87CC79E240F811AG1D4B189FE17EE873A0726901DD9618B5F5762D4B316EC7F922EEB2145F81580B75A02FE239B8F3FDF2827D8A3BE19476DD54142F1EAF5C7FB3DFF57E1038FFDC36DC782AE39E7495BBA03D87E0DCA1FC554BDE612B0E826D540A293E29FE051E2F3A177A3E76950167356BA83DEFC560F96DE9D91C578EC1DE6A8A91BFD678C47CD8FD2F106982DEAF72B84E227A7D44E028186E2B02717879574A0369625E0342EFF7C31F175DABEC64F4A76E13826E5BAEF86C5D35565EA6EAF1EC
	FC1F25F1B8565FF12F985F17CC58E6DEAC589F20AAD65CAF8F1D90771671EBD7116D4FE85FAF6FEA9DB7344632319E0AD55DAF3C5B1549F85A72844FC4485F930B343B72546FBBF0ECEF4DE0FBD0167F7014FC746F63D3727FE3CABE4ABBECD4673F132F4EC79D210FABA7626C71116322EF7B9FC9A00E3F0A50FDCFDE311275291DFFCB7EE9F1C6E10C65G9B73CB0782576B2815FE3B3FEF255E9C0821F3AAE9E5609CD8C5695BD6864E01F7BDE28E6C043C2AD5E28E3CCFE30BD8C8EFD5E8F3A9EDA72449EAC1
	5B6595F325E83518ABEBE40CBD2B1574B7E89A5FF53074D9D9ED4FD86EAFBFE7ACD3F8FC553DD5A0181F3C6493FE32837A6086C48192G5B7D3A883AFB3AB1799F18E63DBDB7DD0F594733F42E262F8E0C7352A9FD7F6A40B86FB5137097B977C1DCFA1FB8CF7DDEB36AE4BB2477G0CEB5ACCC781D50076B5A296DC56C45F2006BAD3E9F5391BE011F72836BE2CFFFDF953FB737FF759E2DEA6BE341EA44D2D57FDAB61D8435F3470EFD24A628C7C327EF534E07F36C05ECE40FEC0515AB6D3A920FA2D685F6EA6
	FE2F055BAB58CDD9EDFD74B50B320935CB03FEB2AE3CECAEF551B532955A69DBAB665722C2B13FA2578A1BDA614179142A2463E89A6F7DD97A14672B2C9758EEF8BBB3FFA59E6166A30BB16A3D786EF1C07248F61B5541B6C935D60F4565305B06C9457D744E157A1F7781FB570A58E43997472A4F65310F381CF2F0CD426B7EB52D0BEF0207A01DC2E97F65AD89A1D7527DCCB70C4F77C7B09675EF5E58CD85DB1B3E74093529A8CE2CCDD90301EB53DDD3561E330301EB935677675B878357262975178E862ECD
	22BE9B3397685FDC315F7F297A7D16696B0FE1AA3FD69EEC5E7D30D9FFEF70C433765E6029E66D7317E71AC31F753C502C7F16F9EF337AADB3739B2BACB8B6CDE08BFD00C8C0ACA0B1CE780CD91F7C8F21BEC343DFB4F477625E847FE2C5967C25967D3EBADA427F1D0B2F855F36F439E459550F6DABFB4A969CAF596526C4C61B5102BA7258FB7BE4D614536736200DBB249ECAE6F3E278D070DEA4CAB94B588E7DCD83942E0318F09D3B3B0BD807E5B6955673B2D56B027D0F78DB12CC8E8B1469863C4453C36B
	7C6B9313631F3330FF573D7E8BC4C69F7062DED2F57CD22EBAFDA9EFEB385CEA2E2BE97A7CCA5D651626265A1A4B1F57B4FDDAF3758A916BC86C207AEE795C41D91AEF3F91A93C9F3B78B772570BB3188C5B3DE7C2485058F259FC2D6513D6ADA9D87DA299F5DCA761FB90A15720A45637239BD61323EFD69B8E6B48D53C1E49D33F5E9FD37B67E96A404D2E2F4C9656DB746DF133A53CBDBEE4216FBE410EDA075DFC5F7C9D8B765355536315943B5FEAA9D4E8E62F45653563061A3F27AA90B1499074ADE13D58
	437CEBB9568D5843543A708DB95B42F01F7DE0017059E96B459EE6B7ED33E4832C47G4B86F17676D421584F76816DCDAA9B44C2C47CCB7FD80B963B817F6E3593DDA19F371F6429472D7E47750637F546ED7786B6DE95717EF10B682848E57E2575AA1D0B10B7AFDE7814CF7F086FDD5B2C5703CFFB01E871385C30348EB3AAFDC94EFD55AD57CA609B876DEC85248148C608F3GE500EAC07BC641E3799FCBC370E0CA263E4BE335BBE5FE37D8B03D58F656B46BFD7B266B3158373D9047DE5E989893DBA9FD46
	46401878F9457EA2A1AFFD230847F6972A7937EE02B5E0D368980B5946777F2476B505CB9C8641AF307C98EFEF5EA63FED30317C38837D6A96206F846C8794FD8362D5C0B5209550879883C483628149009D00B440BEC0DEC21B29A4C10C771A7613A1461B9F8C78871C08F9FB75AA4A252C274C5CF8DE67D5DC9B15372D748D5F95A8686CE5798B2E697B164557427B16D55734573A38EB41EB5D46EB5AEB5D835754EB9D23A571F6BD4AE587237F6DB55C7333B530596A34762A5E5CFC066E0B7F6ED57DCEAF78
	7AE3305C422F13875B2F7C0A7A450A84B6B7980D9CC6DAD3027FEF01E1DFCC7FDFGCCDF16BF9D5733A908FEAE024BEE693AAE59E0A16FD56ADC69503709EB9D61ED425621ED93760EE01BF0F6E85B045C91EC93439D38A67442BCE8D74ACDA8342B8AEDB6ED93E91F5A3B65BE05FEAB158B690D92B2947479D42EC1CAEB97F7D5AA5AD5B19FBAF04D04AE7761FBE5C6DB51017B439E3B43416783ADE3F742922A6863FEAA235DEDD5499D2F50D0EE7EB78CA7D46F5029680CFEF4A85FCF972293767F06F854FB99
	3E71AF10AC2F0794A86F184DD400DD83E7AAB31FDE1DE9A0C6B3B9408644CC0C28D5B34922CAB713CC98907A07374629606F7DA655FEF25F9C1BEECCA6EF66FEF2F3201FFCF5G47F2AB50F7ED963E6E3A6F7D90F6F9255F6AF0406E720B9AE7B75AE86D30AC797787EF8EE07BDED106E7323C9383A877F5D51D7F87D0CB8788E2FAF796B296GG20C3GGD0CB818294G94G88G88G6B0171B4E2FAF796B296GG20C3GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB
	8586GGGG81G81GBAGGGEC96GGGG
**end of data**/
}
}