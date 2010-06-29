package cbit.vcell.model.gui;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.beans.PropertyChangeListener;

import javax.swing.table.TableColumn;

import org.vcell.util.gui.ScrollTable;

import cbit.gui.ScopedExpression;
import cbit.gui.TableCellEditorAutoCompletion;
import cbit.vcell.model.Kinetics;
import cbit.vcell.parser.ExpressionBindingException;
/**
 * This type was created in VisualAge.
 */
public class ParameterPanel extends javax.swing.JPanel implements PropertyChangeListener {
	private ParameterTableModel ivjParameterTableModel = null;
	private ScrollTable ivjScrollPaneTable = null;
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
private ScrollTable getScrollPaneTable() {
	if (ivjScrollPaneTable == null) {
		try {
			ivjScrollPaneTable = new ScrollTable();
			ivjScrollPaneTable.setValidateExpressionBinding(false);
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
		add(getScrollPaneTable().getEnclosingScrollPane(), constraintsJScrollPane1);
		initConnections();
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