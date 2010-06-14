package cbit.vcell.model.gui;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.beans.PropertyChangeListener;

import javax.swing.table.TableColumn;

import cbit.gui.ScopedExpression;
import cbit.gui.TableCellEditorAutoCompletion;
import cbit.vcell.model.Kinetics;
import cbit.vcell.parser.ExpressionBindingException;
/**
 * This type was created in VisualAge.
 */
public class ParameterPanel extends javax.swing.JPanel implements PropertyChangeListener {
	private javax.swing.JScrollPane ivjJScrollPane1 = null;
	private ParameterTableModel ivjParameterTableModel = null;
	private javax.swing.JTable ivjScrollPaneTable = null;
	private Kinetics fieldKinetics = null;
	private boolean ivjConnPtoP2Aligning = false;
	private Kinetics ivjkinetics1 = null;

/**
 * Constructor
 */
public ParameterPanel() {
	super();
	initialize();
}

/**
 * Insert the method's description here.
 * Creation date: (8/9/2006 10:11:57 PM)
 */
public void cleanupOnClose() {
	getParameterTableModel().setKinetics(null);
}

/**
 * connEtoM1:  (kinetics1.this --> ParameterTableModel.kinetics)
 * @param value cbit.vcell.model.Kinetics
 */
private void connEtoM1(Kinetics value) {
	try {
		//if ((getkinetics1() != null)) {
			getParameterTableModel().setKinetics(getkinetics1());
		//}
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}


/**
 * connEtoM2:  (ParameterPanel.initialize() --> ScrollPaneTable.model)
 */
private void connEtoM2() {
	try {
		getScrollPaneTable().setModel(getParameterTableModel());
		getScrollPaneTable().createDefaultColumnsFromModel();

		//set column renderer, except for expression/value column.
		for(int i=0; i<getScrollPaneTable().getModel().getColumnCount(); i++) {
			if (i != ParameterTableModel.COLUMN_VALUE) {
				TableColumn column=getScrollPaneTable().getColumnModel().getColumn(i);
				column.setCellRenderer(new ParameterTableCellRenderer(getScrollPaneTable().getDefaultRenderer(Boolean.class)));
			}
		}
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}

/**
 * connPtoP2SetSource:  (ParameterPanel.kinetics <--> kinetics1.this)
 */
private void connPtoP2SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP2Aligning == false) {
			ivjConnPtoP2Aligning = true;
			if ((getkinetics1() != null)) {
				this.setKinetics(getkinetics1());
			}
			ivjConnPtoP2Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP2Aligning = false;
		handleException(ivjExc);
	}
}


/**
 * connPtoP2SetTarget:  (ParameterPanel.kinetics <--> kinetics1.this)
 */
private void connPtoP2SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP2Aligning == false) {
			ivjConnPtoP2Aligning = true;
			setkinetics1(this.getKinetics());
			ivjConnPtoP2Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP2Aligning = false;
		handleException(ivjExc);
	}
}


/**
 * Return the JScrollPane1 property value.
 * @return javax.swing.JScrollPane
 */
private javax.swing.JScrollPane getJScrollPane1() {
	if (ivjJScrollPane1 == null) {
		try {
			ivjJScrollPane1 = new javax.swing.JScrollPane();
			ivjJScrollPane1.setName("JScrollPane1");
			ivjJScrollPane1.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			ivjJScrollPane1.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			getJScrollPane1().setViewportView(getScrollPaneTable());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJScrollPane1;
}

/**
 * Gets the kinetics property (cbit.vcell.model.Kinetics) value.
 * @return The kinetics property value.
 * @see #setKinetics
 */
public Kinetics getKinetics() {
	return fieldKinetics;
}


/**
 * Return the kinetics1 property value.
 * @return cbit.vcell.model.Kinetics
 */
private Kinetics getkinetics1() {
	return ivjkinetics1;
}


/**
 * Return the ParameterTableModel property value.
 * @return cbit.vcell.model.gui.ParameterTableModel
 */
private ParameterTableModel getParameterTableModel() {
	if (ivjParameterTableModel == null) {
		try {
			ivjParameterTableModel = new ParameterTableModel(getScrollPaneTable());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjParameterTableModel;
}

/**
 * Return the ScrollPaneTable property value.
 * @return javax.swing.JTable
 */
private javax.swing.JTable getScrollPaneTable() {
	if (ivjScrollPaneTable == null) {
		try {
			ivjScrollPaneTable = new javax.swing.JTable();
			ivjScrollPaneTable.setName("ScrollPaneTable");
			getJScrollPane1().setColumnHeaderView(ivjScrollPaneTable.getTableHeader());
			ivjScrollPaneTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
			ivjScrollPaneTable.setBounds(0, 0, 200, 200);
			ivjScrollPaneTable.setAutoCreateColumnsFromModel(false);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjScrollPaneTable;
}


/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	System.out.println("--------- UNCAUGHT EXCEPTION --------- in ParameterPanel ");
	exception.printStackTrace(System.out);
	if (exception instanceof ExpressionBindingException){
		javax.swing.JOptionPane.showMessageDialog(this, exception.getMessage(), "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
	}
}


/**
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
private void initConnections() throws java.lang.Exception {
	this.addPropertyChangeListener(this);
	connPtoP2SetTarget();
	getJScrollPane1().addComponentListener(new ComponentAdapter() {
		public void componentResized(ComponentEvent e) {
			ScopedExpressionTableCellRenderer.formatTableCellSizes(getScrollPaneTable(),null,null);
		}
	});
}

/**
 * Initialize the class.
 */
private void initialize() {
	try {
		setName("ParameterPanel");
		setLayout(new java.awt.GridBagLayout());

		java.awt.GridBagConstraints constraintsJScrollPane1 = new java.awt.GridBagConstraints();
		constraintsJScrollPane1.gridx = 0; constraintsJScrollPane1.gridy = 0;
		constraintsJScrollPane1.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJScrollPane1.weightx = 1.0;
		constraintsJScrollPane1.weighty = 1.0;
		add(getJScrollPane1(), constraintsJScrollPane1);
		initConnections();
		parameterPanel_Initialize();
		connEtoM2();
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
		ParameterPanel aParameterPanel;
		aParameterPanel = new ParameterPanel();
		frame.setContentPane(aParameterPanel);
		frame.setSize(aParameterPanel.getSize());
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
 * Comment
 */
public void parameterPanel_Initialize() {

	getScrollPaneTable().setDefaultRenderer(ScopedExpression.class,new ScopedExpressionTableCellRenderer());
	getScrollPaneTable().setDefaultEditor(ScopedExpression.class,new TableCellEditorAutoCompletion(getScrollPaneTable(), false));
	
	getParameterTableModel().addTableModelListener(
		new javax.swing.event.TableModelListener(){
			public void tableChanged(javax.swing.event.TableModelEvent e){
				ScopedExpressionTableCellRenderer.formatTableCellSizes(getScrollPaneTable(),null,null);
			}
		}
	);
}


/**
 * Method to handle events for the PropertyChangeListener interface.
 * @param evt java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void propertyChange(java.beans.PropertyChangeEvent evt) {
	// user code begin {1}
	// user code end
	if (evt.getSource() == this && (evt.getPropertyName().equals("kinetics"))) { 
		connPtoP2SetTarget();
	}		
	// user code begin {2}
	// user code end
}

/**
 * Sets the kinetics property (cbit.vcell.model.Kinetics) value.
 * @param kinetics The new value for the property.
 * @see #getKinetics
 */
public void setKinetics(Kinetics kinetics) {
	Kinetics oldValue = fieldKinetics;
	fieldKinetics = kinetics;
	firePropertyChange("kinetics", oldValue, kinetics);
}


/**
 * Set the kinetics1 to a new value.
 * @param newValue cbit.vcell.model.Kinetics
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setkinetics1(Kinetics newValue) {
	if (ivjkinetics1 != newValue) {
		try {
			Kinetics oldValue = getkinetics1();
			ivjkinetics1 = newValue;
			connPtoP2SetSource();
			connEtoM1(ivjkinetics1);
			firePropertyChange("kinetics", oldValue, newValue);
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