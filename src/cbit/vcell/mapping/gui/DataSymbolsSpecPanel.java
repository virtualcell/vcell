package cbit.vcell.mapping.gui;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/

import java.awt.BorderLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import org.vcell.util.gui.sorttable.JSortTable;

import cbit.gui.ScopedExpression;
import cbit.gui.TableCellEditorAutoCompletion;
import cbit.vcell.mapping.SpeciesContextSpec;
import cbit.vcell.model.gui.ScopedExpressionTableCellRenderer;
import cbit.vcell.parser.ExpressionException;
/**
 * This type was created in VisualAge.
 */
public class DataSymbolsSpecPanel extends javax.swing.JPanel {
	private javax.swing.JScrollPane ivjScrollPanel = null;
	private JSortTable ivjScrollPaneTable = null;
	private SpeciesContextSpecParameterTableModel ivjSpeciesContextSpecParameterTableModel1 = null;

/**
 * Constructor
 */
public DataSymbolsSpecPanel() {
	super();
	initialize();
}

/**
 * Return the ScrollPaneTable property value.
 * @return cbit.vcell.messaging.admin.sorttable.JSortTable
 */
private JSortTable getScrollPaneTable() {
	if (ivjScrollPaneTable == null) {
		try {
			ivjScrollPaneTable = new JSortTable();
			ivjScrollPaneTable.setName("ScrollPaneTable");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjScrollPaneTable;
}

/**
 * Return the SpeciesContextSpecParameterTableModel1 property value.
 * @return cbit.vcell.mapping.gui.SpeciesContextSpecParameterTableModel
 */
private SpeciesContextSpecParameterTableModel getSpeciesContextSpecParameterTableModel1() {
	if (ivjSpeciesContextSpecParameterTableModel1 == null) {
		try {
			ivjSpeciesContextSpecParameterTableModel1 = new SpeciesContextSpecParameterTableModel(getScrollPaneTable());
		} catch (java.lang.Throwable ivjExc) {
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
	if (exception instanceof ExpressionException){
		javax.swing.JOptionPane.showMessageDialog(this, exception.getMessage(), "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
	}
	System.out.println("--------- UNCAUGHT EXCEPTION --------- in DataSymbolsSpecPanel");
	exception.printStackTrace(System.out);
}


/**
 * Initializes connections
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {	
}

/**
 * Initialize the class.
 */
private void initialize() {
	try {
		setName("DataSymbolsSpecPanel");
		setLayout(new java.awt.BorderLayout());
		setSize(572, 196);

		add(getScrollPaneTable().getEnclosingScrollPane(), BorderLayout.CENTER);
		initConnections();
		
		getScrollPaneTable().setModel(getSpeciesContextSpecParameterTableModel1());

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
		java.awt.Frame frame = new java.awt.Frame();
		DataSymbolsSpecPanel aDataSymbolsSpecPanel;
		aDataSymbolsSpecPanel = new DataSymbolsSpecPanel();
		frame.add("Center", aDataSymbolsSpecPanel);
		frame.setSize(aDataSymbolsSpecPanel.getSize());
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


//public void setDataSymbolSpec(DataSymbolSpec newValue) {
//	getDataSymbolSpecParameterTableModel1().setDataSymbolSpec(newValue);
//}

public void setDataSymbol(Object object) {
	// TODO Auto-generated method stub
	
}

}